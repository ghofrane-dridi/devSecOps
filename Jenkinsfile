pipeline {
    agent any

    tools {
        maven 'M3'            // Nom du Maven installé dans Jenkins
        jdk 'JDK 17'          // Nom du JDK 17 installé dans Jenkins
    }

    environment {
        // Ton dépôt GitHub
        GITHUB_REPO = 'https://github.com/ghofrane-dridi/devSecOps.git'

        // Configuration SonarQube
        SONARQUBE = 'SonarQube'           // Nom de l'environnement SonarQube dans Jenkins
        SONAR_TOKEN = credentials('sonar-token')  // Ton token stocké dans Jenkins Credentials

        // Configuration Nexus
        NEXUS_URL = 'http://192.168.56.11:8181/repository/maven-releases/'
        NEXUS_SNAPSHOT_URL = 'http://192.168.56.11:8181/repository/maven-snapshots/'

        // Docker
        DOCKER_IMAGE_NAME = 'ghofrane/devsecops-app:latest'
    }

    stages {
        stage('🚀 Start') {
            steps {
                echo 'Pipeline started for Ghofrane Dridi - DevSecOps Project'
            }
        }

        stage('📦 Checkout') {
            steps {
                echo '📥 Cloning repository from GitHub...'
                git branch: 'main', url: "${GITHUB_REPO}"
            }
        }

        stage('🔧 Maven Clean & Compile') {
            steps {
                echo '🛠️ Compiling project...'
                sh 'mvn clean compile -B'
            }
        }

        stage('🧪 Run Unit Tests (JUnit & Mockito)') {
            steps {
                echo '✅ Running unit tests...'
                sh 'mvn test'
            }
        }

        stage('📊 Code Coverage with JaCoCo') {
            steps {
                echo '📈 Generating JaCoCo coverage report...'
                jacoco()
            }
        }

        stage('🔍 SonarQube Analysis') {
            steps {
                echo '🔎 Running SonarQube analysis...'
                withSonarQubeEnv(SONARQUBE) {
                    sh """
                        mvn -B sonar:sonar \
                        -Dsonar.projectKey=devsecops-app \
                        -Dsonar.login=$SONAR_TOKEN
                    """
                }
            }
        }

        stage('✅ Quality Gate') {
            steps {
                echo '🛡️ Waiting for SonarQube Quality Gate...'
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('📦 Build & Deploy to Nexus') {
            steps {
                echo '📤 Deploying artifact to Nexus...'
                sh """
                    mvn deploy -DskipTests \
                    -DaltDeploymentRepository=releases::default::${NEXUS_URL}
                """
            }
        }

        stage('🐳 Build Docker Image') {
            steps {
                echo '🏗️ Building Docker image...'
                sh "docker build -t ${DOCKER_IMAGE_NAME} ."
            }
        }

        stage('🚢 Push to DockerHub') {
            steps {
                script {
                    echo '📤 Pushing Docker image to DockerHub...'
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        sh """
                            docker login -u $DOCKER_USER -p $DOCKER_PASS
                            docker push ${DOCKER_IMAGE_NAME}
                        """
                    }
                }
            }
        }

        stage('compose') {
            steps {
                echo '🔄 Starting application with Docker Compose...'
                sh 'docker compose up -d'
            }
        }

        stage('📊 Start Monitoring') {
            steps {
                echo '📈 Starting Prometheus and Grafana...'
                sh '''
                    docker start prometheus || docker run -d --name prometheus -p 9090:9090 prom/prometheus
                    docker start grafana || docker run -d --name grafana -p 3000:3000 grafana/grafana
                '''
            }
        }
    }

    post {
        always {
            emailext (
                subject: "Pipeline Status: ${currentBuild.currentResult}",
                body: """Bonjour Ghofrane,

Votre pipeline '${env.JOB_NAME}' (Build #${env.BUILD_NUMBER}) est terminé avec le statut : **${currentBuild.currentResult}**.

➡️ Consulter les logs : ${env.BUILD_URL}

Cordialement,  
Jenkins CI/CD""",
                to: 'ghofrane.dridi@esprit.tn'
            )
        }
        success {
            echo '✅ Pipeline succeeded! Application deployed successfully.'
        }
        failure {
            echo '❌ Pipeline failed! Check SonarQube, Nexus, or Docker configuration.'
        }
    }
}
