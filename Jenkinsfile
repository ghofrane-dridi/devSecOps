pipeline {
    agent any

    tools {
        maven 'M3'
        jdk 'JDK 17'
    }

    environment {
        GITHUB_TOKEN = credentials('github-token')
    }

    stages {
        stage('Cloner le dépôt GitHub') {
            steps {
                git branch: 'main', url: "https://${GITHUB_TOKEN}@github.com/ghofrane-dridi/devSecOps.git"
            }
        }

        stage('Compiler avec Maven') {
            steps {
                sh 'mvn clean package'
                sh 'ls -l target' // pour vérifier le JAR généré
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t ghofranedridi/devsecops:latest .'
            }
        }
    }

    post {
        always {
            echo '✅ Build terminé'
        }
    }
}
