pipeline {
    agent any

    tools {
        maven 'M3'       // Nom Maven configuré dans Jenkins
        jdk 'JDK 17'     // Nom JDK configuré dans Jenkins
    }

    environment {
        SONAR_TOKEN = credentials('sonar-token')       // token Sonar (si utilisé)
    }

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/votre-utilisateur/votre-repo.git', branch: 'main'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean install'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn sonar:sonar -Dsonar.login=$SONAR_TOKEN'
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                sh 'mvn deploy'
            }
        }
    }
}
