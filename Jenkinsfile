pipeline {
    agent any

    tools {
        jdk 'JDK 17'       // Doit exister dans Jenkins > Global Tool Configuration
        maven 'M3'         // Doit exister dans Jenkins > Global Tool Configuration
    }

    environment {
        GITHUB_TOKEN = credentials('github-token')            // Ton token GitHub (type Secret text)
        SONARQUBE_TOKEN = credentials('sonarqube-token')      // Ton token SonarQube (type Secret text)
        SONAR_HOST_URL = 'http://localhost:9000'              // L'URL SonarQube (vérifie le port)
    }

    stages {
        stage('Cloner le dépôt') {
            steps {
                git branch: 'main', url: "https://${GITHUB_TOKEN}@github.com/ghofrane-dridi/devSecOps.git"
            }
        }

        stage('Build Maven') {
            steps {
                sh 'mvn clean install'
            }
        }

        stage('Tests & Couverture') {
            steps {
                sh 'mvn test'
                sh 'ls -l target/surefire-reports/'
            }
        }

        stage('Analyse SonarQube') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh """
                    mvn sonar:sonar \
                    -Dsonar.projectKey=devsecops \
                    -Dsonar.host.url=$SONAR_HOST_URL \
                    -Dsonar.token=$SONARQUBE_TOKEN \
                    -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                    """
                }
            }
        }

        stage('Vérifier JAR') {
            steps {
                sh 'ls -lh target/*.jar'
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
            junit '**/target/surefire-reports/*.xml'
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
