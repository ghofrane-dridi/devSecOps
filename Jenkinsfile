pipeline {
    agent any

    tools {
        maven 'M3'       // Nom de Maven défini dans Jenkins
        jdk 'JDK 17'     // Nom du JDK défini dans Jenkins
    }

    environment {
        GITHUB_TOKEN = credentials('github-token') // Ton GitHub token
        SONAR_TOKEN  = credentials('sonar-token')  // Token défini dans Jenkins pour SonarQube
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/votre-utilisateur/votre-repo.git'
            }
        }

        stage('Build with Maven') {
            steps {
                sh 'mvn clean install'
            }
        }

        stage('Code Quality - SonarQube') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh "mvn sonar:sonar -Dsonar.login=$SONAR_TOKEN"
                }
            }
        }

        stage('SonarQube Quality Gate') {
            steps {
                waitForQualityGate abortPipeline: true
            }
        }

        stage('Docker Compose Up') {
            steps {
                sh 'docker-compose up -d'
            }
        }

        stage('Deploy to Nexus') {
            steps {
                sh 'mvn deploy'
            }
        }
    }

    post {
        always {
            script {
                node {
                    echo '🧹 Nettoyage Docker...'
                    sh 'docker-compose down -v || true'
                }
            }
        }

        success {
            echo '✅ Pipeline terminé avec succès.'
        }

        failure {
            echo '❌ Le pipeline a échoué.'
        }
    }
}
