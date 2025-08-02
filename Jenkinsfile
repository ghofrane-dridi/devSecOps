pipeline {
    agent any

    tools {
        jdk 'JDK 17'
        maven 'M3'
    }

    environment {
        GITHUB_TOKEN = credentials('github-token')
        SONARQUBE_TOKEN = credentials('sonarqube-token')
        SONAR_HOST_URL = 'http://localhost:9000'
    }

    stages {
        stage('Cloner le dépôt') {
            steps {
                git branch: 'main', url: "https://${GITHUB_TOKEN}@github.com/ghofrane-dridi/devSecOps.git"
            }
        }

        stage('Build Maven') {
            steps {
                sh 'mvn clean install -DskipTests'  // Build sans tests
            }
        }

        stage('Tests & Couverture') {
            steps {
                sh 'mvn test'  // Lance les tests (JUnit, Mockito) et génère rapports Surefire + JaCoCo
            }
        }

        stage('Analyse SonarQube') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh """
                    mvn sonar:sonar \
                        -Dsonar.projectKey=devsecops \
                        -Dsonar.host.url=$SONAR_HOST_URL \
                        -Dsonar.login=$SONARQUBE_TOKEN
                    """
                }
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
            junit '**/target/surefire-reports/*.xml'  // Publication rapports tests JUnit
            echo 'Build terminé.'
        }

        success {
            echo '✅ Pipeline terminé avec succès.'
        }

        failure {
            echo '❌ Échec du pipeline.'
        }
    }
}
