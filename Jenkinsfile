pipeline {
    agent any

    tools {
        maven 'M3'          // Nom exact de ton Maven configuré dans Jenkins
        jdk 'jdk'           // Nom exact de ton JDK (vérifie dans Global Tools)
    }

    environment {
        GITHUB_TOKEN = credentials('github-token')
        SONARQUBE_TOKEN = credentials('sonarqube-token')
    }

    stages {
        stage('📥 Cloner le dépôt') {
            steps {
                git branch: 'main',
                    url: "https://${GITHUB_TOKEN}@github.com/ghofrane-dridi/DevSecOps.git"
            }
        }

        stage('🔧 Compilation') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('🧪 Tests') {
            steps {
                sh 'mvn test'
            }
        }

        stage('📊 Analyse SonarQube') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh "mvn sonar:sonar -Dsonar.projectKey=devsecops -Dsonar.token=${SONARQUBE_TOKEN}"
                }
            }
        }

        stage('🐋 Build Docker') {
            steps {
                sh 'docker build -t devsecops-app .'
            }
        }
    }

    post {
        always {
            echo '✅ Pipeline terminé.'
        }
    }
}
