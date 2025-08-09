pipeline {
    agent any

    tools {
        jdk 'JDK 17'        // Configuré dans Jenkins > Global Tool Configuration
        maven 'M3'          // Configuré dans Jenkins > Global Tool Configuration
    }

    environment {
        GITHUB_TOKEN = credentials('github-token')             // Jenkins Credentials (GitHub)
        SONARQUBE_TOKEN = credentials('sonarqube-token')       // Jenkins Credentials (SonarQube)
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
                sh 'mvn clean install'  // Compile + tests + package
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
                        -Dsonar.host.url=${SONAR_HOST_URL} \
                        -Dsonar.login=${SONARQUBE_TOKEN}
                    """
                }
            }
        }

        stage('Vérifier JAR') {
            steps {
                sh 'ls -l target/'
            }
        }

        stage('Docker Compose Up') {
            steps {
                echo '🚀 Arrêt et lancement avec Docker Compose...'
                sh 'docker compose down -v --remove-orphans || true'
                sh 'docker compose up -d'
            }
        }

        stage('Construire Docker') {
            steps {
                sh 'docker build -t ghofrane028/devsecops:latest .'
            }
        }

        stage('Docker Push') {
            steps {
                echo '📤 Pousser l\'image Docker vers Docker Hub...'
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh '''
                        docker login -u $DOCKER_USER -p $DOCKER_PASS
                        docker push ghofrane028/devsecops:latest
                    '''
                }
            }
        }

        stage('Déployer sur Nexus') {
            steps {
                echo '📦 Déploiement sur Nexus...'
                sh 'mvn deploy'
            }
        }

        stage('📊 Start Monitoring') {
            steps {
                echo '📈 Lancement de Prometheus et Grafana pour le monitoring...'
                sh '''
                    # Lancer Prometheus (avec fichier prometheus.yml dans le repo)
                    if [ ! "$(docker ps -aq -f name=prometheus)" ]; then
                        docker run -d --name prometheus -p 9090:9090 \
                            -v $(pwd)/prometheus.yml:/etc/prometheus/prometheus.yml \
                            prom/prometheus
                    else
                        docker start prometheus
                    fi

                    # Lancer Grafana
                    if [ ! "$(docker ps -aq -f name=grafana)" ]; then
                        docker run -d --name grafana -p 3000:3000 \
                            -e GF_SECURITY_ADMIN_USER=admin \
                            -e GF_SECURITY_ADMIN_PASSWORD=admin \
                            grafana/grafana
                    else
                        docker start grafana
                    fi
                '''
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
