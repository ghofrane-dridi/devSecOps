pipeline {
    agent any

    tools {
        maven 'M3'       // Nom donné dans Jenkins > Global Tool Configuration
        jdk 'JDK 17'     // JDK installé et configuré dans Jenkins
    }

    environment {
        SONARQUBE = 'SonarQube'           // Nom du serveur SonarQube dans Jenkins
        SONAR_LOGIN = credentials('sonar-token')  // Token sécurisé (Stocké dans Jenkins Credentials)
        NEXUS_URL = 'http://localhost:8081/repository/maven-releases/'  // URL Nexus
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
                sh 'mvn clean install -DskipTests'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo '🔍 Analyzing code with SonarQube...'
                withSonarQubeEnv('SonarQube') {
                    sh "mvn sonar:sonar -Dsonar.login=${SONAR_LOGIN}"
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
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
                sh "mvn deploy -DskipTests -Dnexus.url=${NEXUS_URL} -DaltDeploymentRepository=releases::default::${NEXUS_URL}"
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
