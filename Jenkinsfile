pipeline {
    agent any

    tools {
        maven 'M3'      // Nom exact de l'installation Maven dans Jenkins
        git 'Default'   // Nom exact de l'installation Git dans Jenkins
    }

    environment {
        GITHUB_TOKEN = credentials('github-token')
    }

    stages {
        stage('Cloner le dépôt GitHub') {
            steps {
                echo '📥 Clonage du dépôt...'
                git branch: 'main',
                    url: "https://${GITHUB_TOKEN}@github.com/ghofrane-dridi/DevSecOps.git"
            }
        }

        stage('Compiler avec Maven') {
            steps {
                echo '🔧 Compilation avec Maven...'
                sh 'mvn clean package'
            }
        }

        stage('Exécuter les tests') {
            steps {
                echo '🧪 Exécution des tests...'
                sh 'mvn test'
            }
        }

        stage('Construire l\'image Docker') {
            steps {
                echo '🐋 Construction de l\'image Docker...'
                sh 'docker build -t devsecops-app .'
            }
        }
    }

    post {
        always {
            echo '✅ Build terminé.'
        }
    }
}
