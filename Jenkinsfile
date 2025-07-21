pipeline {
    agent any

    tools {
        maven 'M3'       // Maven tool installed in Jenkins
        jdk 'JDK 17'     // JDK 17 installed in Jenkins
    }

    environment {
        SONAR_TOKEN = credentials('sonar-token')  // Secret text credential with SonarQube token
        SONAR_HOST_URL = 'http://localhost:9000'  // Your SonarQube instance
        JAVA_HOME = tool 'JDK 17'
        PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/ghofrane-dridi/devSecOps.git '
            }
        }

        stage('Build with Tests') {
            steps {
                echo '🏗️ Building project and running tests...'
                sh 'mvn clean verify'
            }
        }

        stage('Generate JaCoCo Report') {
            steps {
                echo '📊 Generating JaCoCo coverage report...'
                sh 'mvn jacoco:report'
            }
        }

        stage('Publish JaCoCo Report') {
            steps {
                echo '📈 Publishing JaCoCo coverage report...'
                jacoco() // Requires Jenkins JaCoCo plugin
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo '🔍 Running SonarQube analysis...'
                withSonarQubeEnv('SonarQube') { // Ensure this name matches your SonarQube server name in Jenkins
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('Quality Gate') {
            steps {
                echo '🛡️ Waiting for SonarQube Quality Gate...'
                timeout(time: 2, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline terminé avec succès.'
        }
        failure {
            echo '❌ Le pipeline a échoué. Vérifiez les logs pour plus de détails.'
        }
        always {
            echo '🏁 Fin du pipeline.'
        }
    }
}
