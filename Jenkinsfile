pipeline {
    agent any

    tools {
        maven 'M3'           // Nom de l'outil Maven configuré dans Jenkins
        jdk 'JDK 17'         // Nom du JDK 17 configuré dans Jenkins
    }

    environment {
        SONARQUBE = 'sonar-server'         // Nom de l'environnement SonarQube dans Jenkins
        SONAR_TOKEN = credentials('sonar-token')  // ID du secret text dans Credentials
        NEXUS_URL = 'http://192.168.56.11:8181/repository/maven-releases/'
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-creds')  // ID du credential DockerHub
    }

    stages {
        stage('🚀 Start') {
            steps {
                echo '🚀 Starting Pipeline...'
            }
        }

        stage('📦 Checkout') {
            steps {
                echo '📥 Cloning repository...'
                git branch: 'azyzkahloul5Nids1Spec', url: 'https://github.com/devops-G6/devops'
            }
        }

        stage('🔧 Maven Clean & Compile') {
            steps {
                echo '🛠️ Compiling project...'
                sh 'mvn clean compile -B'
            }
        }

        stage('🧪 Test (JUnit & Mockito)') {
            steps {
                echo '✅ Running unit tests...'
                sh 'mvn test'
            }
        }

        stage('📊 Code Coverage with JaCoCo') {
            steps {
                echo '📈 Generating JaCoCo report...'
                jacoco()
            }
        }

        stage('🔍 SonarQube Analysis') {
            steps {
                echo '🔎 Running SonarQube analysis...'
                withSonarQubeEnv(SONARQUBE) {
                    sh 'mvn verify sonar:sonar -Dsonar.login=$SONAR_TOKEN'
                }
            }
        }

        stage('✅ Quality Gate') {
            steps {
                echo '🛡️ Waiting for SonarQube Quality Gate...'
                timeout(time: 2, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('📦 Deploy to Nexus') {
            steps {
                echo '📤 Deploying artifact to Nexus...'
                sh 'mvn deploy -DskipTests'
            }
        }

        stage('🐳 Build Docker Image') {
            steps {
                echo '🏗️ Building Docker image...'
                sh 'docker build -t azyzkahloul/myimage:latest .'
            }
        }

        stage('🚢 Push to DockerHub') {
            steps {
                script {
                    echo '📤 Pushing image to DockerHub...'
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        sh """
                            docker login -u $DOCKER_USER -p $DOCKER_PASS
                            docker push azyzkahloul/myimage:latest
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

        stage('📊 Start Monitoring (Prometheus & Grafana)') {
            steps {
                echo '📈 Starting Prometheus and Grafana...'
                sh '''
                    docker start prometheus || true
                    docker start grafana || true
                '''
            }
        }
    }

    post {
        always {
            emailext (
                subject: "Pipeline Status: ${currentBuild.currentResult}",
                body: """Bonjour,

Votre pipeline '${env.JOB_NAME}' Build #${env.BUILD_NUMBER} est terminé avec le statut : **${currentBuild.currentResult}**.

➡️ Consulter les logs : ${env.BUILD_URL}

Cordialement,
Jenkins CI/CD""",
                recipientProviders: [[$class: 'DevelopersRecipientProvider']],
                to: 'aziz.kahloul@esprit.tn'
            )
        }
        success {
            echo '✅ Pipeline succeeded!'
        }
        failure {
            echo '❌ Pipeline failed!'
        }
    }
}
