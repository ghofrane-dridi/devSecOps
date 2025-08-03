pipeline {
    agent any

    tools {
        jdk 'JDK 17'       // Nom exact configuré dans Jenkins (Global Tool Configuration)
        maven 'M3'         // Nom exact Maven configuré
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
                sh 'mvn clean install'
            }
        }

        stage('Tests & Couverture') {
            steps {
                // Lance les tests unitaires + génération du rapport XML JaCoCo (requis par SonarQube)
                sh 'mvn test jacoco:report'
                sh 'ls -l target/site/jacoco/'
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
                        -Dsonar.login=$SONARQUBE_TOKEN \
                        -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                    """
                }
            }
        }

        stage('Vérifier JAR') {
            steps {
                sh 'ls -lh target/*.jar || echo "⚠️ Aucun fichier .jar trouvé !"'
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
            echo '📦 Build terminé.'
        }
        success {
            echo '✅ Pipeline terminé avec succès.'
        }
        failure {
            echo '❌ Échec du pipeline.'
        }
    }
}
