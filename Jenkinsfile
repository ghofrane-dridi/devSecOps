pipeline {
    agent any

    tools {
        jdk 'JDK 17'        // Configuré dans Jenkins > Global Tool Configuration
        maven 'M3'          // Configuré dans Jenkins > Global Tool Configuration
    }

    environment {
        GITHUB_TOKEN = credentials('github-token')             // Stocké dans Jenkins > Credentials (ID : github-token)
        SONARQUBE_TOKEN = credentials('sonarqube-token')       // Stocké dans Jenkins > Credentials (ID : sonarqube-token)
        SONAR_HOST_URL = 'http://localhost:9000'               // URL locale de SonarQube
    }

    stages {
        stage('Cloner le dépôt') {
            steps {
                git branch: 'main', url: "https://${GITHUB_TOKEN}@github.com/ghofrane-dridi/devSecOps.git"
            }
        }

        stage('Build Maven') {
            steps {
                sh 'mvn clean install'  // Compile + Test + Package
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
                withSonarQubeEnv('SonarQube') { // Ce nom doit correspondre à celui configuré dans Jenkins > Manage Jenkins > Configure System
                    sh """
                    mvn sonar:sonar \
                      -Dsonar.projectKey=devsecops \
                      -Dsonar.host.url=${SONAR_HOST_URL} \
                      -Dsonar.login=${SONARQUBE_TOKEN}
                    """
                }
            }
        }

        stage('Vérifier JAR') {
            steps {
                sh 'ls -lh target/*.jar || echo "Aucun fichier JAR trouvé !"'
            }
        }

        stage('Docker Compose Up') {
            steps {
                echo '🚀 Arrêt et lancement avec Docker Compose...'
                sh 'docker compose down -v || true'
                sh 'docker compose up -d'
            }
        }

        stage('Construire Docker') {
            steps {
                sh 'docker build -t ghofranedridi/devsecops:latest .'
            }
        }

        stage('Déployer sur Nexus') {
            steps {
                echo '📦 Déploiement sur Nexus...'
                sh 'mvn deploy'
            }
        }
    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml'
            echo '🛠️ Build terminé.'
        }
        success {
            echo '✅ Pipeline terminé avec succès.'
        }
        failure {
            echo '❌ Échec du pipeline.'
        }
    }
}
