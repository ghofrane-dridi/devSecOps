pipeline {
    agent any

    tools {
        maven 'M3'         // Nom Maven configuré dans Jenkins (Global Tool Configuration)
        jdk 'JDK 17'       // Nom JDK configuré dans Jenkins
    }

    environment {
        GITHUB_TOKEN   = credentials('github-token')      // ID du token GitHub (Credentials)
        SONAR_TOKEN    = credentials('sonar-token')       // ID du token SonarQube (Credentials)
        SONAR_HOST_URL = 'http://localhost:9000'          // Adresse du serveur SonarQube
    }

    stages {

        stage('Clone Repository') {
            steps {
                echo '📥 Cloning source code...'
                git branch: 'main', url: 'https://github.com/<ton-utilisateur>/<ton-repo>.git'
            }
        }

        stage('Start Docker Compose') {
            steps {
                echo '🐳 Lancement des services via Docker Compose...'
                sh 'docker-compose down -v || true'
                sh 'docker-compose up -d --build'
            }
        }

        stage('Wait for Nexus (port 8081)') {
            steps {
                echo '⏳ Attente du démarrage de Nexus...'
                script {
                    // Attendre que Nexus réponde (timeout 90s)
                    def maxTries = 30
                    def count = 0
                    def ready = false
                    while (!ready && count < maxTries) {
                        def response = sh(script: "curl -s -o /dev/null -w '%{http_code}' http://localhost:8181", returnStdout: true).trim()
                        if (response == '200') {
                            ready = true
                            echo '✅ Nexus est prêt.'
                        } else {
                            sleep(3)
                            count++
                        }
                    }
                    if (!ready) {
                        error '❌ Nexus ne répond pas sur le port 8181 après 90s.'
                    }
                }
            }
        }

        stage('Build Maven') {
            steps {
                echo '🔧 Compilation du projet...'
                sh 'mvn clean install'
            }
        }

        stage('Analyse SonarQube') {
            steps {
                echo '🔍 Analyse SonarQube...'
                withSonarQubeEnv('SonarQube') {
                    sh """
                        mvn sonar:sonar \
                        -Dsonar.projectKey=devSecOps \
                        -Dsonar.host.url=${SONAR_HOST_URL} \
                        -Dsonar.login=${SONAR_TOKEN}
                    """
                }
            }
        }

        stage('Déploiement Nexus') {
            steps {
                echo '🚀 Déploiement de l’artefact dans Nexus...'
                sh 'mvn deploy'
            }
        }
    }

    post {
        always {
            echo '🧹 Nettoyage des conteneurs Docker...'
            sh 'docker-compose down -v || true'
        }

        success {
            echo '✅ Pipeline terminé avec succès !'
        }

        failure {
            echo '❌ Échec du pipeline. Voir les logs pour plus de détails.'
        }
    }
}
