pipeline {
    agent any

    tools {
        maven 'M3'         // Le nom de Maven configuré dans Jenkins (Manage Jenkins > Global Tool Configuration)
        jdk 'JDK 17'       // Le nom du JDK configuré dans Jenkins
    }

    environment {
        GITHUB_TOKEN = credentials('github-token')      // ID du token GitHub dans Jenkins
        SONAR_TOKEN  = credentials('sonar-token')       // ID du token SonarQube dans Jenkins
        SONAR_HOST_URL = 'http://localhost:9000'        // URL du serveur SonarQube
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
