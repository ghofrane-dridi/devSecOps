pipeline {
    agent any

    tools {
        maven 'M3'
        jdk 'JDK 17'
    }

    environment {
        SONARQUBE = 'SonarQube'
        SONAR_TOKEN = credentials('sonar-token')
        NEXUS_URL = 'http://localhost:8081/repository/maven-releases/'
    }

    stages {
        stage('Start') {
            steps {
                echo '🚀 Starting Pipeline...'
            }
        }

        stage('Checkout') {
            steps {
                echo '📦 Cloning repository...'
                git branch: 'main', url: 'https://github.com/ghofrane-dridi/devSecOps.git'
            }
        }

        stage('Build Maven') {
            steps {
                echo '🔧 Building project with Maven...'
                sh 'mvn -B -e -U clean install -DskipTests'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo '🔍 Analyzing code with SonarQube...'
                withSonarQubeEnv('SonarQube') {
                    sh """
                        mvn -B -e sonar:sonar -Dsonar.login=$SONAR_TOKEN
                    """
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 20, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                echo '🐳 Building Docker image...'
                sh 'docker build -t ghofrane/devsecops-app:latest .'
            }
        }

        stage('Push to Nexus') {
            steps {
                echo '📤 Deploying artifact to Nexus...'
                sh """
                    mvn -B deploy -DskipTests -DaltDeploymentRepository=releases::default::${NEXUS_URL}
                """
            }
        }
    }

    post {
        always {
            echo '✅ Pipeline finished'
        }
        failure {
            echo '❌ Build failed!'
        }
    }
}
