pipeline {
    agent any
    tools {
        maven 'M3'        // Ton installation Maven nommée 'M3' dans Jenkins
        jdk 'jdk-17'      // Ton JDK installé dans Jenkins (nom exact à vérifier dans ta config)
        // git 'DefaultGit' // Optionnel : si tu as configuré Git dans Jenkins et veux l’utiliser
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
        stage('Configurer Git') {
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
