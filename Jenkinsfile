pipeline {
    agent any

    tools {
        maven 'M3'       // Nom Maven configuré dans Jenkins (ex : M3)
        jdk 'JDK 17'     // Nom JDK configuré dans Jenkins (ex : JDK 17)
    }

    environment {
        SONAR_TOKEN = credentials('sonar-token')  // Token SonarQube (Secret Text)
        JAVA_HOME = tool name: 'JDK 17', type: 'jdk'  // Définit JAVA_HOME correctement
        PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/ghofrane-dridi/devSecOps.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh """
                        mvn sonar:sonar \
                            -Dsonar.projectKey=devsecops \
                            -Dsonar.host.url=http://localhost:9000 \
                            -Dsonar.login=${SONAR_TOKEN}
                    """
                }
            }
        }
    }

    post {
        always {
            echo '✅ Pipeline terminé.'
        }
        failure {
            echo '❌ Le pipeline a échoué. Vérifie les logs.'
        }
    }
}
