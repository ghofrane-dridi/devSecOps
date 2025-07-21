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
                git branch: 'main', url: 'https://github.com/ghofrane-dridi/devSecOps.git'
            }
        }

        stage('Build with Tests') {
            steps {
                // Lancer tout en une fois pour que Maven gère bien les phases (évite doublons)
                sh 'mvn clean verify'
            }
        }

        stage('Generate JaCoCo Report') {
            steps {
                sh 'mvn jacoco:report'
            }
        }

        stage('Publish JaCoCo Report (Optional)') {
            steps {
                jacoco() // requires Jenkins JaCoCo plugin installed
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh """
                        mvn sonar:sonar \
                            -Dsonar.projectKey=demo \
                            -Dsonar.host.url=${SONAR_HOST_URL} \
                            -Dsonar.login=${SONAR_TOKEN}
                    """
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 1, unit: 'MINUTES') {
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
