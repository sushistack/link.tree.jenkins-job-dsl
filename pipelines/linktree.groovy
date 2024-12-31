pipeline {
    agent any

    parameters {
        choice(name: 'ORDER_TYPE', choices: ['STANDARD', 'DELUXE', 'PREMIUM'], description: 'type of order')
        string(name: 'TARGET_URL', defaultValue: 'https://test.com', description: 'target url')
        string(name: 'CUSTOMER_NAME', defaultValue: '고객명', description: 'customer name')
        string(name: 'ANCHOR_TEXTS', defaultValue: '딸기', description: 'anchor texts for link')
        string(name: 'KEYWORDS', defaultValue: '딸기', description: 'crawled keywords')
    }

    environment {
        SLACK_CHANNEL = '#link-tree-workflow'
        SLACK_CREDENTIALS_ID = 'slack-bot'
    }

    stages {
        stage('Start Notification') {
            steps {
                slackSend channel: env.SLACK_CHANNEL, message: "Started to build Link Tree :rocket:", tokenCredentialId: env.SLACK_CREDENTIALS_ID
            }
        }
        stage('Build Link Tree Batch Application') {
            steps {
                slackSend channel: env.SLACK_CHANNEL, message: "1. Started to build Link Tree Application :gear:", tokenCredentialId: env.SLACK_CREDENTIALS_ID
                build job: '01-Build-Link-Tree',
                    parameters: [],
                    wait: true,
                    propagate: true
            }
            post {
                success {
                    slackSend color: "good", channel: env.SLACK_CHANNEL, message: "Succeeded in Build", tokenCredentialId: env.SLACK_CREDENTIALS_ID
                }
                failure {
                    slackSend color: "danger", channel: env.SLACK_CHANNEL, message: "Failed to Build", tokenCredentialId: env.SLACK_CREDENTIALS_ID
                    error("Link Tree Application Build failed")
                }
            }
        }
        stage('Crawl') {
            steps {
                slackSend channel: env.SLACK_CHANNEL, message: "2. Started to Crawl :spider:", tokenCredentialId: env.SLACK_CREDENTIALS_ID
                build job: '02-Crawl',
                    parameters: [
                        string(name: 'KEYWORDS', value: "${params.KEYWORDS}")
                    ], 
                    wait: true, 
                    propagate: true
            }
            post {
                success {
                    slackSend color: "good", channel: env.SLACK_CHANNEL, message: "Succeeded in Crawl", tokenCredentialId: env.SLACK_CREDENTIALS_ID
                }
                failure {
                    slackSend color: "danger", channel: env.SLACK_CHANNEL, message: "Failed to Crawl", tokenCredentialId: env.SLACK_CREDENTIALS_ID
                    error("Crawl Job failed")
                }
            }
        }
        stage('Generate Links') {
            steps {
                slackSend channel: env.SLACK_CHANNEL, message: "3. Started to Generate Links :link:", tokenCredentialId: env.SLACK_CREDENTIALS_ID
                build job: '03-Generate-Links',
                    parameters: [
                        string(name: 'ORDER_TYPE', value: "${params.ORDER_TYPE}"),
                        string(name: 'TARGET_URL', value: "${params.TARGET_URL}"),
                        string(name: 'CUSTOMER_NAME', value: "${params.CUSTOMER_NAME}"),
                        string(name: 'ANCHOR_TEXTS', value: "${params.ANCHOR_TEXTS}"),
                        string(name: 'KEYWORDS', value: "${params.KEYWORDS}")
                    ], 
                    wait: true, 
                    propagate: true
            }
            post {
                success {
                    slackSend color: "good", channel: env.SLACK_CHANNEL, message: "Succeeded in Generating Links", tokenCredentialId: env.SLACK_CREDENTIALS_ID
                }
                failure {
                    slackSend color: "danger", channel: env.SLACK_CHANNEL, message: "Failed to Generate Links", tokenCredentialId: env.SLACK_CREDENTIALS_ID
                    error("Link generating failed")
                }
            }
        }
        stage('Deploy Posts') {
            steps {
                slackSend channel: env.SLACK_CHANNEL, message: "4. Started to Deploy Posts :rocket:", tokenCredentialId: env.SLACK_CREDENTIALS_ID
                build job: '04-Deploy-Posts',
                    parameters: [], 
                    wait: true, 
                    propagate: true
            }
            post {
                success {
                    slackSend color: "good", channel: env.SLACK_CHANNEL, message: "Succeeded in Deploying Posts", tokenCredentialId: env.SLACK_CREDENTIALS_ID
                }
                failure {
                    slackSend color: "danger", channel: env.SLACK_CHANNEL, message: "Failed to Deploy Posts", tokenCredentialId: env.SLACK_CREDENTIALS_ID
                    error("Deploying posts failed")
                }
            }
        }
        stage('Validate Links') {
            steps {
                slackSend channel: env.SLACK_CHANNEL, message: "5. Started to Validate Links :link:", tokenCredentialId: env.SLACK_CREDENTIALS_ID
                build job: '05-Validate-Links',
                    parameters: [], 
                    wait: true, 
                    propagate: true
            }
            post {
                success {
                    slackSend color: "good", channel: env.SLACK_CHANNEL, message: "Succeeded in Validating Links", tokenCredentialId: env.SLACK_CREDENTIALS_ID
                }
                failure {
                    slackSend color: "danger", channel: env.SLACK_CHANNEL, message: "Failed to Validate Links", tokenCredentialId: env.SLACK_CREDENTIALS_ID
                    error("Validating Link failed")
                }
            }
        }
        stage('End Notification') {
            steps {
                slackSend channel: env.SLACK_CHANNEL, message: "Finished to build Link Tree :check_mark_button:", tokenCredentialId: env.SLACK_CREDENTIALS_ID
            }
        }
    }
}