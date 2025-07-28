pipeline {
    agent any

    environment {
        SONARQUBE_SERVER = 'SonarQube'
        SONAR_TOKEN = credentials('sonar-token')
        DOCKER_IMAGE = 'devsecops-springapp'
    }

    tools {
        maven 'Maven'
        jdk 'jdk-17'
    }

    stages {
        stage('Git Checkout') {
            steps {
                git credentialsId: 'github-creds', url: 'https://github.com/ghofrane-dridi/devSecOps.git'
            }
        }

        stage('Build with Maven') {
            steps {
                sh 'mvn clean install'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv("${SONARQUBE_SERVER}") {
                    sh "mvn sonar:sonar -Dsonar.projectKey=devsecops-springapp -Dsonar.login=$SONAR_TOKEN"
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh "docker build -t ${DOCKER_IMAGE} ."
            }
        }

        stage('Docker Run') {
            steps {
                sh "docker run -d --name springapp -p 8080:8080 ${DOCKER_IMAGE}"
            }
        }
    }

    post {
        always {
            echo 'Pipeline terminé.'
        }
        success {
            echo 'Déploiement réussi !'
        }
        failure {
            echo 'Échec du pipeline.'
        }
    }
}
