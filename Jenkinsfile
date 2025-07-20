pipeline {
    agent any

    tools {
        maven 'M3'       // Nom de l'installation Maven dans Jenkins (ex: M3)
        jdk 'JDK 17'     // Nom de l'installation JDK dans Jenkins (ex: JDK 17)
    }

    environment {
        // Utilisation d'une credential de type "Secret text" avec l'ID 'sonar-token'
        SONAR_TOKEN = credentials('sonar-token')

        // Configuration SonarQube (optionnel mais utile)
        SONAR_HOST_URL = 'http://localhost:9000'
        JAVA_HOME = tool 'JDK 17'
        PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                // Checkout depuis GitHub
                git branch: 'main', url: 'https://github.com/ghofrane-dridi/devSecOps.git '
            }
        }

        stage('Build') {
            steps {
                // Build Maven sans tests
                sh 'mvn clean install -DskipTests'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                // Assurez-vous que 'SonarQube' est bien configuré dans Jenkins
                withSonarQubeEnv('SonarQube') {
                    sh """
                        mvn sonar:sonar \
                            -Dsonar.projectKey=devsecops \
                            -Dsonar.host.url=\${SONAR_HOST_URL} \
                            -Dsonar.login=\${SONAR_TOKEN}
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
