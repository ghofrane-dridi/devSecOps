pipeline {
    agent any

    tools {
        maven 'M3'         // Nom de l'installation Maven (dans Jenkins > Global Tool Configuration)
        jdk 'JDK 17'       // Nom de l'installation JDK (également configurée dans Jenkins)
    }

    environment {
        JAVA_HOME        = tool('JDK 17')
        PATH             = "${JAVA_HOME}/bin:${env.PATH}"
        GITHUB_TOKEN     = credentials('github-token')      // Secret text dans Jenkins (GitHub Token)
        SONAR_TOKEN      = credentials('sonar-token')       // Secret text dans Jenkins (SonarQube Token)
        SONAR_HOST_URL   = 'http://localhost:9000'          // URL du serveur SonarQube
    }

    stages {
        stage('📥 Checkout') {
            steps {
                echo '🔄 Clonage du dépôt GitHub...'
                git branch: 'main',
                    url: 'https://github.com/ghofrane-dridi/devSecOps.git',
                    credentialsId: 'github-creds'
            }
        }

        stage('🏗️ Build & Test') {
            steps {
                echo '🔧 Compilation et exécution des tests...'
                sh 'mvn clean verify'
            }
        }

        stage('📊 JaCoCo Report') {
            steps {
                echo '📈 Génération du rapport de couverture de code...'
                sh 'mvn jacoco:report'
            }
        }

        stage('📤 Publish JaCoCo Report') {
            steps {
                echo '📎 Publication du rapport dans Jenkins...'
                jacoco()  // Nécessite le plugin Jenkins JaCoCo
            }
        }

        stage('🔍 SonarQube Analysis') {
            steps {
                echo '📡 Lancement de l’analyse SonarQube...'
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

        stage('✅ Quality Gate') {
            steps {
                echo '🛡️ Vérification de la Quality Gate SonarQube...'
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('🚀 Deploy to Nexus') {
            steps {
                echo '📦 Déploiement de l’artefact sur Nexus...'
                sh 'mvn deploy'
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline terminé avec succès.'
        }
        failure {
            echo '❌ Échec du pipeline. Veuillez consulter les logs pour les détails.'
        }
        aborted {
            echo '⚠️ Pipeline interrompu manuellement.'
        }
        always {
            echo '🏁 Fin de l’exécution du pipeline.'
        }
    }
}
