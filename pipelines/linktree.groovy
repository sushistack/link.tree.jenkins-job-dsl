pipeline {
    agent any

    parameters {
        choice('ORDER_TYPE', ['STANDARD', 'DELUXE', 'PREMIUM'], 'type of order')
        stringParam('TARGET_URL', '', 'target url')
        stringParam('CUSTOMER_NAME', '', 'customer name')
        stringParam('ANCHOR_TEXTS', '', 'anchor texts for link')
        stringParam('KEYWORDS', '', 'crawled keywords')
    }

    environment {
        SLACK_CHANNEL = '#link-tree-workflow'
        SLACK_CREDENTIALS_ID = 'slack-bot'
    }

    stages {
        stage('Build Link Tree Batch Application') {
            steps {
                script {
                    slackSend channel: env.SLACK_CHANNEL, 
                              message: "[Jenkins] Started to build Link Tree Application",
                              tokenCredentialId: env.SLACK_CREDENTIALS_ID
                }
                build job: '01-Build-Link-Tree',
                    parameters: [],
                    wait: true,
                    propagate: false
            }
            post {
                success {
                    slackSend channel: env.SLACK_CHANNEL, message: "[Jenkins] Succeeded in build Link Tree Application", tokenCredentialId: env.SLACK_CREDENTIALS_ID
                }
                failure {
                    slackSend channel: env.SLACK_CHANNEL, message: "[Jenkins] Failed to build Link Tree Application", tokenCredentialId: env.SLACK_CREDENTIALS_ID
                    error("Link Tree Application Build failed")
                }
            }
        }
    }
    stage('Crawl') {
        steps {
            build job: '02-Crawl',
                parameters: [
                    string(name: 'KEYWORDS', value: "${params.KEYWORDS}")
                ], 
                wait: true, 
                propagate: false
        }
        post {
            success {
                slackSend channel: env.SLACK_CHANNEL, message: "[Jenkins] Succeeded in crawl", tokenCredentialId: env.SLACK_CREDENTIALS_ID
            }
            failure {
                slackSend channel: env.SLACK_CHANNEL, message: "[Jenkins] Failed to crawl", tokenCredentialId: env.SLACK_CREDENTIALS_ID
                error("Crawl Job failed")
            }
        }
    }
    stage('Generate Links') {
        steps {
            build job: '03-Generate-Links',
                parameters: [
                    string(name: 'ORDER_TYPE', value: "${params.ORDER_TYPE}")
                    string(name: 'TARGET_URL', value: "${params.TARGET_URL}")
                    string(name: 'CUSTOMER_NAME', value: "${params.CUSTOMER_NAME}")
                    string(name: 'ANCHOR_TEXTS', value: "${params.ANCHOR_TEXTS}")
                    string(name: 'KEYWORDS', value: "${params.KEYWORDS}")
                ], 
                wait: true, 
                propagate: false
        }
        post {
            success {
                slackSend channel: env.SLACK_CHANNEL, message: "[Jenkins] Succeeded in generating links", tokenCredentialId: env.SLACK_CREDENTIALS_ID
            }
            failure {
                slackSend channel: env.SLACK_CHANNEL, message: "[Jenkins] Failed to generate links", tokenCredentialId: env.SLACK_CREDENTIALS_ID
                error("Link generating failed")
            }
        }
    }
    stage('Deploy Posts') {
        steps {
            build job: '04-Deploy-Posts',
                parameters: [], 
                wait: true, 
                propagate: false
        }
        post {
            success {
                slackSend channel: env.SLACK_CHANNEL, message: "[Jenkins] Succeeded in deploying posts", tokenCredentialId: env.SLACK_CREDENTIALS_ID
            }
            failure {
                slackSend channel: env.SLACK_CHANNEL, message: "[Jenkins] Failed to deploy posts", tokenCredentialId: env.SLACK_CREDENTIALS_ID
                error("Deploying posts failed")
            }
        }
    }
    stage('Validate Links') {
        steps {
            build job: '05-Validate-Links',
                parameters: [], 
                wait: true, 
                propagate: false
        }
        post {
            success {
                slackSend channel: env.SLACK_CHANNEL, message: "[Jenkins] Succeeded in validating links", tokenCredentialId: env.SLACK_CREDENTIALS_ID
            }
            failure {
                slackSend channel: env.SLACK_CHANNEL, message: "[Jenkins] Failed to validate links", tokenCredentialId: env.SLACK_CREDENTIALS_ID
                error("Validating Link failed")
            }
        }
    }
}