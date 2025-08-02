pipeline {
    agent any

    tools {
        maven 'M3'
        jdk 'JDK 17'
    }

    environment {
        GITHUB_REPO = 'https://github.com/ghofrane-dridi/devSecOps.git'
        SONARQUBE = 'SonarQube'
        SONAR_TOKEN = credentials('sonar-tokenn')
        NEXUS_URL = 'http://localhost:8181/repository/maven-releases/'
        DOCKER_IMAGE = 'ghofrane/devsecops-app:latest'
    }

    stages {
        stage('🚀 Start') {
            steps {
                echo 'Pipeline started for Ghofrane Dridi'
            }
        }

        stage('📦 Checkout') {
            steps {
                git branch: 'main', url: "${GITHUB_REPO}"
            }
        }

        stage('🔧 Maven Clean & Compile') {
            steps {
                sh 'mvn clean compile -B'
            }
        }

        stage('🧪 Run Tests') {
            steps {
                sh 'mvn verify'
            }
        }

        stage('📊 Code Coverage (JaCoCo)') {
            steps {
                jacoco()
            }
        }

        stage('🔍 SonarQube Analysis') {
            steps {
                withSonarQubeEnv(SONARQUBE) {
                    sh '''
                        mvn sonar:sonar \
                        -Dsonar.projectKey=devsecops \
                        -Dsonar.projectName=devSecOps \
                        -Dsonar.login=$SONAR_TOKEN \
                        -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                    '''
                }
            }
        }

        stage('✅ Quality Gate') {
            steps {
                timeout(time: 15, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('📦 Deploy to Nexus') {
            steps {
                sh '''
                    mvn deploy -DskipTests \
                    -DaltDeploymentRepository=releases::default::${NEXUS_URL}
                '''
            }
        }

        stage('🐳 Build Docker Image') {
            steps {
                sh "docker build -t ${DOCKER_IMAGE} ."
            }
        }

        stage('🚢 Push to DockerHub') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        sh """
                            docker login -u $DOCKER_USER -p $DOCKER_PASS
                            docker push ${DOCKER_IMAGE}
                        """
                    }
                }
            }
        }

        stage('⚙️ Docker Compose Up') {
            steps {
                sh 'docker compose up -d'
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
                to: 'ghofranedridi90@gmail.com'
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
