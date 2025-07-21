pipeline {
    agent any

    tools {
        maven 'M3'         // Nom Maven configuré dans Jenkins
        jdk 'JDK 17'       // Nom JDK configuré dans Jenkins
    }

    environment {
        GITHUB_TOKEN   = credentials('github-token')      // ID du token GitHub
        SONAR_TOKEN    = credentials('sonar-token')       // ID du token SonarQube
        SONAR_HOST_URL = 'http://localhost:9000'          // URL SonarQube
    }

    stages {
        stage('Checkout') {
            steps {
                echo '📥 Cloning source code...'
                git branch: 'main', url: 'https://github.com/<ton-utilisateur>/<ton-repo>.git'
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

                // Alternative : exécution directe sans withSonarQubeEnv (optionnelle)
                sh "mvn sonar:sonar -Dsonar.host.url=http://localhost:9000/ -Dsonar.token=${SONAR_TOKEN}"
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
