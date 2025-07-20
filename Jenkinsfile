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
                sh 'mvn clean install -DskipTests' // Build first
                sh 'mvn test'                      // Run tests to generate jacoco.exec
            }
        }

        stage('Generate JaCoCo Report') {
            steps {
                sh 'mvn jacoco:report' // Generate HTML report
            }
        }

        stage('Publish JaCoCo Report (Optional)') {
            steps {
                jacoco() // Publish report in Jenkins UI (requires plugin)
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') { // Make sure this name matches Jenkins config
                    sh """
                        mvn sonar:sonar \
                            -Dsonar.projectKey=demo \
                            -Dsonar.host.url=\${SONAR_HOST_URL} \
                            -Dsonar.login=\${SONAR_TOKEN} \
                            -Djacoco.reportPath=target/site/jacoco/index.html
                    """
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
