pipeline {
    agent any
    tools {
        maven 'Maven3'   // Nom de l'installation Maven dans Jenkins
        git 'DefaultGit' // Nom de l'installation Git dans Jenkins (si configuré)
    }
    environment {
        GITHUB_TOKEN = credentials('github-token')
    }
    stages {
        stage('Cloner le dépôt') {
            steps {
                git branch: 'main', url: "https://${GITHUB_TOKEN}@github.com/ghofrane-dridi/devSecOps.git"
            }
        }
        stage('Vérifier Git') {
            steps {
                sh 'git --version'
                sh '''
                   git config --global user.name "Ghofrane Dridi"
                   git config --global user.email "ghofranedridi90@gmail.com"
                   git config --list
                '''
            }
        }
        stage('Compiler Maven') {
            steps {
                sh 'mvn clean package'
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
