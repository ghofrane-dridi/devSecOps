pipeline {
    agent any

    tools {
        maven 'M3'       // Nom du Maven configuré dans Jenkins
        jdk 'JDK 17'     // Nom du JDK configuré dans Jenkins
    }

    environment {
        SONAR_TOKEN = credentials('sonar-token') // Jenkins Credentials ID
        SONAR_HOST_URL = 'http://localhost:9000' // URL de SonarQube
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/ton-compte/ton-projet.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }

        stage('Test et Couverture') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Analyse SonarQube') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh '''
                        mvn sonar:sonar \
                            -Dsonar.projectKey=ton-projet \
                            -Dsonar.host.url=${SONAR_HOST_URL} \
                            -Dsonar.login=${SONAR_TOKEN}
                    '''
                }
            }
        }

        stage('Qualité Code - Attente Sonar') {
            steps {
                timeout(time: 1, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Déploiement Nexus') {
            steps {
                sh 'mvn deploy'
            }
        }

    }

    post {
        always {
            echo 'Pipeline terminé.'
        }
        success {
            echo 'Build et analyse réussies.'
        }
        failure {
            echo 'Échec du pipeline.'
        }
    }
}
