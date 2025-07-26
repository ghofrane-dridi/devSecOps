pipeline {
    agent any

    tools {
        maven 'M3'         // Maven installé via Manage Jenkins > Global Tool Configuration
        jdk 'JDK 17'       // JDK installé dans Jenkins
    }

    environment {
        GITHUB_TOKEN = credentials('github-token')      // ✅ Assure-toi que ce token existe dans Credentials
        SONAR_TOKEN  = credentials('sonar-token')       // ✅ Assure-toi aussi que celui-ci existe
        SONAR_HOST_URL = 'http://localhost:9000'        // URL SonarQube
    }

    stages {
        stage('Checkout') {
            steps {
                echo '📥 Cloning source code...'
                git branch: 'main', url: "https://ghp:${GITHUB_TOKEN}@github.com/<TON-UTILISATEUR>/<TON-REPO>.git"
            }
        }

        stage('Build') {
            steps {
                echo '🔧 Building with Maven...'
                sh 'mvn clean install'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo '🔍 Running SonarQube analysis...'
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

        stage('Deploy to Nexus') {
            steps {
                echo '🚀 Deploying to Nexus...'
                sh 'mvn deploy'
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline completed successfully!'
        }
        failure {
            echo '❌ Le pipeline a échoué.'
            script {
                echo '🧹 Nettoyage Docker...'
                sh 'docker-compose down -v || true'
            }
        }
    }
}
