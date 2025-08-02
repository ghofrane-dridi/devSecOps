pipeline {
    agent any

    stages {
        stage('Cloner le dépôt GitHub') {
            steps {
                echo '📥 Clonage du dépôt...'
                git url: 'https://github.com/ghofrane-dridi/devSecOps.git', branch: 'main', credentialsId: 'github-token'
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
