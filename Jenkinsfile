pipeline {
    agent any

    environment {
        GITHUB_TOKEN = credentials('github-token') // Token GitHub dans Jenkins
    }

    stages {
        stage('Cloner le dépôt GitHub') {
            steps {
                echo '📥 Clonage du dépôt...'
                git branch: 'main', url: "https://${GITHUB_TOKEN}@github.com/ghofrane-dridi/devSecOps.git"
            }
        }

        stage('Compiler avec Maven') {
            steps {
                echo '🔧 Compilation avec Maven...'
                sh 'mvn clean package'
            }
        }

        stage('Docker Build') {
            steps {
                echo '🐳 Construction de l\'image Docker...'
                sh 'docker build -t ghofranedridi/devsecops:latest .'
            }
        }
    }

    post {
        always {
            echo "✅ Build terminé pour DevSecOps"
        }
    }
}
