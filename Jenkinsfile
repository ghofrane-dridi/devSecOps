pipeline {
    agent any

    tools {
        maven 'M3'         // Maven installé dans Jenkins (Manage Jenkins > Global Tool Configuration)
        jdk 'JDK 17'       // JDK installé dans Jenkins
    }

    environment {
        JAVA_HOME        = tool('JDK 17')
        PATH             = "${JAVA_HOME}/bin:${env.PATH}"
        GITHUB_TOKEN     = credentials('github-token')      // ID du secret GitHub (Secret Text dans Jenkins)
        SONAR_TOKEN      = credentials('sonar-token')       // ID du token SonarQube
        SONAR_HOST_URL   = 'http://localhost:9000'          // L'URL de ton serveur SonarQube
    }

    stages {
        stage('Checkout') {
            steps {
                echo '🔄 Clonage du dépôt GitHub...'
                git branch: 'main',
                    url: "https://ghofrane-dridi:${GITHUB_TOKEN}@github.com/ghofrane-dridi/devSecOps.git"
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
                echo '📈 Publication du rapport JaCoCo dans Jenkins...'
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
                echo '🛡️ Vérification de la Quality Gate SonarQube...'
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                echo '🚀 Déploiement de l’artefact vers Nexus...'
                sh 'mvn deploy'
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline terminé avec succès.'
        }
        failure {
            echo '❌ Échec du pipeline. Consultez les erreurs.'
        }
        aborted {
            echo '⚠️ Pipeline interrompu manuellement.'
        }
        always {
            echo '🏁 Fin du pipeline.'
        }
    }
}
