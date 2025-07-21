pipeline {
    agent any

    tools {
        maven 'M3'         // Nom Maven configuré dans Jenkins
        jdk 'JDK 17'       // Nom JDK configuré dans Jenkins
    }

    environment {
        SONAR_TOKEN    = credentials('sonar-token')       // ID du token SonarQube
        SONAR_HOST_URL = 'http://localhost:9000'          // URL SonarQube
    }

    stages {
        stage('Checkout') {
            steps {
                echo '📥 Cloning source code...'
                checkout([$class: 'GitSCM',
                    branches: [[name: '*/main']],
                    userRemoteConfigs: [[
                        url: 'https://github.com/ghofrane-dridi/devSecOps.git',
                        credentialsId: 'github-creds'  // Ton ID credentials Jenkins pour GitHub
                    ]]
                ])
            }
        }

        stage('Build') {
            steps {
                echo '🔧 Building with Maven...'
                sh 'mvn clean install'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo '🔍 Running SonarQube analysis...'
                withSonarQubeEnv('SonarQube') {
                    sh """
                        mvn sonar:sonar \
                        -Dsonar.projectKey=devSecOps \
                        -Dsonar.host.url=$SONAR_HOST_URL \
                        -Dsonar.login=$SONAR_TOKEN
                    """
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                echo '🚀 Deploying to Nexus...'
                sh 'mvn deploy'
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline completed successfully!'
        }
        failure {
            echo '❌ Pipeline failed. Check logs for details.'
        }
    }
}
