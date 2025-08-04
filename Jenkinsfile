pipeline {
    agent any

    tools {
        jdk 'JDK 17'         // Global Tool Config dans Jenkins
        maven 'M3'           // Global Tool Config dans Jenkins
    }

    environment {
        GITHUB_TOKEN = credentials('github-token')              // GitHub Personal Token
        SONARQUBE_TOKEN = credentials('sonarqube-token')        // SonarQube Token
        SONAR_HOST_URL = 'http://localhost:9000'                // URL de SonarQube
    }

    stages {
        stage('📥 Cloner le dépôt') {
            steps {
                git branch: 'main',
                    url: "https://${GITHUB_TOKEN}@github.com/ghofrane-dridi/devSecOps.git"
            }
        }

        stage('🔧 Build Maven') {
            steps {
                sh 'mvn clean install -DskipTests' // Compilation sans tests ici
            }
        }

        stage('🧪 Tests & Couverture') {
            steps {
                sh 'mvn test'
                sh 'ls -lh target/surefire-reports/' // Vérifie les fichiers de test
            }
        }

        stage('📊 Analyse SonarQube') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh """
                        mvn sonar:sonar \
                        -Dsonar.projectKey=devsecops \
                        -Dsonar.host.url=${SONAR_HOST_URL} \
                        -Dsonar.login=${SONARQUBE_TOKEN}
                    """
                }
            }
        }

        stage('✅ Quality Gate') {
            steps {
                waitForQualityGate abortPipeline: true
            }
        }

        stage('📦 Vérifier le JAR') {
            steps {
                sh 'ls -lh target/*.jar'
            }
        }

        stage('🐳 Docker Compose (UP)') {
            steps {
                echo '⏬ Arrêt + démarrage Docker Compose...'
                sh 'docker compose down -v || true'
                sh 'docker compose up -d'
            }
        }

        stage('🐳 Build Image Docker') {
            steps {
                sh 'docker build -t ghofranedridi/devsecops:latest .'
            }
        }

        stage('🚀 Déployer vers Nexus') {
            steps {
                sh 'mvn deploy'
            }
        }
    }

    post {
        always {
            junit 'target/surefire-reports/*.xml'
            echo '🧹 Nettoyage terminé.'
        }
        success {
            echo '✅ Succès : pipeline complet.'
        }
        failure {
            echo '❌ Échec : pipeline interrompu.'
        }
    }
}
