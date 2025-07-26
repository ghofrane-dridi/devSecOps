pipeline {
    agent any

    tools {
        maven 'M3'       // Maven configuré dans Jenkins (Global Tools)
        jdk 'JDK 17'     // JDK configuré dans Jenkins (Global Tools)
    }

    environment {
        GITHUB_TOKEN    = credentials('github-token')      // Secret text GitHub
        SONAR_TOKEN     = credentials('sonar-token')       // Secret text SonarQube
        SONAR_HOST_URL  = 'http://localhost:9000'          // URL de SonarQube
        JAVA_HOME       = tool('JDK 17')
        PATH            = "${JAVA_HOME}/bin:${env.PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                echo '🔄 Clonage du dépôt GitHub...'
                git credentialsId: 'github-token',
                    url: "https://ghofrane-dridi:${GITHUB_TOKEN}@github.com/ghofrane-dridi/devSecOps.git",
                    branch: 'main'
            }
        }

        stage('Build & Test') {
            steps {
                echo '🏗️ Compilation et tests Maven...'
                sh 'mvn clean verify'
            }
        }

        stage('JaCoCo Report') {
            steps {
                echo '📊 Génération du rapport JaCoCo...'
                sh 'mvn jacoco:report'
            }
        }

        stage('Publish JaCoCo Report') {
            steps {
                echo '📈 Publication du rapport JaCoCo...'
                jacoco()
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo '🔍 Analyse statique avec SonarQube...'
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

        stage('Quality Gate') {
            steps {
                echo '🛡️ Vérification de la Quality Gate...'
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline exécuté avec succès.'
        }
        failure {
            echo '❌ Le pipeline a échoué. Consultez les logs.'
        }
        aborted {
            echo '⚠️ Pipeline interrompu (aborted).'
        }
        always {
            echo '🏁 Fin du pipeline.'
        }
    }
}
