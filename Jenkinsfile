pipeline {
    agent any

    tools {
        maven 'M3'       // Nom de Maven configuré dans Jenkins
        jdk 'JDK 17'     // Nom du JDK configuré dans Jenkins
    }

    environment {
        SONAR_TOKEN = credentials('sonar-token')  // Credential Secret Text SonarQube
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/ghofrane-dridi/devSecOps.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh """
                        mvn sonar:sonar \
                            -Dsonar.projectKey=devsecops \
                            -Dsonar.host.url=http://localhost:9000 \
                            -Dsonar.login=${SONAR_TOKEN}
                    """
                }
            }
        }
    }

    post {
        always {
            echo '✅ Pipeline terminé.'
        }
        failure {
            echo '❌ Le pipeline a échoué. Vérifie les logs.'
        }
    }
}
