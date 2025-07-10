pipeline {
    agent any

    tools {
        maven 'M3'           // Remplace par le nom exact défini dans Jenkins > Global Tool Configuration
        jdk 'JDK 17'         // Assure-toi que ce nom est identique à celui dans Jenkins
    }

    environment {
        SONAR_TOKEN = credentials('sonar-token') // Assure-toi d’avoir créé ce credential dans Jenkins
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

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Publish Test Results') {
            steps {
                junit '**/target/surefire-reports/*.xml'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh """
                        mvn verify sonar:sonar \
                          -Dsonar.projectKey=devsecops \
                          -Dsonar.host.url=http://localhost:9000 \
                          -Dsonar.login=${SONAR_TOKEN} \
                          -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
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
