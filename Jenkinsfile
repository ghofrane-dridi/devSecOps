pipeline {
    agent any

    tools {
        jdk 'JDK 17'       // Nom défini dans Jenkins > Global Tool Configuration
        maven 'M3'         // Nom défini pour Maven
    }

    environment {
        GITHUB_TOKEN     = credentials('github-token')
        SONARQUBE_TOKEN  = credentials('sonarqube-token')
        SONAR_HOST_URL   = 'http://localhost:9000'
    }

    stages {
        stage('Cloner le dépôt') {
            steps {
                git branch: 'main', url: "https://${GITHUB_TOKEN}@github.com/ghofrane-dridi/devSecOps.git"
            }
        }

        stage('Build Maven (sans tests)') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }

        stage('Tests unitaires avec JaCoCo') {
            steps {
                sh 'mvn test'
                junit '**/target/surefire-reports/*.xml'
            }
        }

        stage('Analyse SonarQube + JaCoCo') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh """
                        mvn verify sonar:sonar \
                        -Dsonar.projectKey=devsecops \
                        -Dsonar.host.url=$SONAR_HOST_URL \
                        -Dsonar.login=$SONARQUBE_TOKEN \
                        -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                    """
                }
            }
        }

        stage('Vérifier le JAR') {
            steps {
                sh 'ls -lh target/*.jar'
            }
        }

        stage('Construire l’image Docker') {
            steps {
                sh 'docker build -t ghofranedridi/devsecops:latest .'
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline réussi : build, test, analyse, docker OK.'
        }
        failure {
            echo '❌ Pipeline échoué.'
        }
        always {
            echo '📦 Pipeline terminé (succès ou non).'
        }
    }
}
