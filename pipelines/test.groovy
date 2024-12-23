pipeline {
    agent any

    parameters {
        choice(name: 'ORDER_TYPE', choices: ['STANDARD', 'DELUXE', 'PREMIUM'], description: 'type of order')
        string(name: 'TARGET_URL', defaultValue: '', description: 'target url')
        string(name: 'CUSTOMER_NAME', defaultValue: '', description: 'customer name')
        string(name: 'ANCHOR_TEXTS', defaultValue: '', description: 'anchor texts for link')
        string(name: 'KEYWORDS', defaultValue: '', description: 'crawled keywords')
    }

    environment {
        SLACK_CHANNEL = '#link-tree-workflow'
        SLACK_CREDENTIALS_ID = 'slack-bot'
    }

    stages {
        stage('Build Link Tree Batch Application') {
            steps {
                build job: 'Test-01',
                    parameters: [
                        string(name: 'ORDER_TYPE', value: "${params.ORDER_TYPE}"),
                        string(name: 'TARGET_URL', value: "${params.TARGET_URL}"),
                        string(name: 'CUSTOMER_NAME', value: "${params.CUSTOMER_NAME}"),
                        string(name: 'ANCHOR_TEXTS', value: "${params.ANCHOR_TEXTS}"),
                        string(name: 'KEYWORDS', value: "${params.KEYWORDS}")
                    ],
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
    
        stage('Crawl') {
            steps {
                build job: 'Test-02',
                    parameters: [], 
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
                build job: 'Test-03',
                    parameters: [], 
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
    }
}