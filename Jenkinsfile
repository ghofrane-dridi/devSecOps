pipeline {
    agent any

    stages {
        stage('GIT Checkout') {
            steps {
                echo 'Clonage du projet depuis GitHub'
                git branch: 'main', url: 'https://github.com/ghofrane-dridi/devSecOps.git '
            }
        }

        stage('Build Maven') {
            steps {
                echo 'Compilation avec Maven'
                sh 'mvn clean package'
            }
        }

        stage('Tests Unitaires') {
            steps {
                echo 'Exécution des tests'
                sh 'mvn test'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo 'Analyse de qualité avec SonarQube'
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn sonar:sonar -Dsonar.login=TON_TOKEN_ICI'
                }
            }
        }
    }

    post {
        always {
            echo 'Build terminé'
        }
    }
}
