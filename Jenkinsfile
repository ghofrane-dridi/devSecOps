pipeline {
    agent any

    environment {
        GITHUB_TOKEN = credentials('github-token')
    }

    stages {
        stage('Cloner le dépôt') {
            steps {
                git branch: 'main', url: "https://${GITHUB_TOKEN}@github.com/ghofrane-dridi/devSecOps.git"
            }
        }

        stage('Compiler Maven') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Installer et configurer Git') {
            steps {
                echo '📦 Installation Git...'
                sh '''
                    sudo apt update
                    sudo apt install -y git
                    git --version
                    git config --global user.name "Ghofrane Dridi"
                    git config --global user.email "ghofranedridi90@gmail.com"
                    git config --list
                '''
            }
        }

        stage('Vérifier JAR') {
            steps {
                sh 'ls -l target/'
            }
        }

        stage('Construire Docker') {
            steps {
                sh 'docker build -t ghofranedridi/devsecops:latest .'
            }
        }
    }

    post {
        always {
            echo 'Build terminé.'
        }
    }
}
