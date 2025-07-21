pipeline {
    agent any

    tools {
        maven 'M3'       // Maven configured in Jenkins
        jdk 'JDK 17'     // JDK configured in Jenkins
    }

    environment {
        GITHUB_TOKEN = credentials('github-token')  // GitHub token credential ID
        SONAR_TOKEN = credentials('sonar-token')    // SonarQube token credential ID
        SONAR_HOST_URL = 'http://localhost:9000'    // SonarQube URL
        JAVA_HOME = tool 'JDK 17'
        PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                echo '🔄 Checking out from GitHub repository...'
                git url: "https://${GITHUB_TOKEN}@github.com/ghofrane-dridi/devSecOps.git", branch: 'main'
            }
        }

        stage('Build & Test') {
            steps {
                echo '🏗️ Building and running tests...'
                sh 'mvn clean verify'
            }
        }

        stage('Generate JaCoCo Report') {
            steps {
                echo '📊 Generating JaCoCo code coverage report...'
                sh 'mvn jacoco:report'
            }
        }

        stage('Publish JaCoCo Report') {
            steps {
                echo '📈 Publishing JaCoCo report in Jenkins...'
                jacoco()
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo '🔍 Running SonarQube analysis...'
                withSonarQubeEnv('SonarQube') {
                    sh "mvn sonar:sonar -Dsonar.host.url=${SONAR_HOST_URL} -Dsonar.login=${SONAR_TOKEN}"
                }
            }
        }

        stage('Quality Gate') {
            steps {
                echo '🛡️ Waiting for SonarQube Quality Gate status...'
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline completed successfully!'
        }
        failure {
            echo '❌ Pipeline failed. Check logs for details.'
        }
        always {
            echo '🏁 Pipeline finished.'
        }
    }
}
