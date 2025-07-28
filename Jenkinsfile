pipeline {
    agent any

    tools {
        maven 'M3'         // Maven configuré dans Jenkins
        jdk 'JDK 17'       // JDK configuré dans Jenkins
    }

    environment {
        JAVA_HOME        = tool('JDK 17')
        PATH             = "${JAVA_HOME}/bin:${env.PATH}"
        GITHUB_TOKEN     = credentials('github-token')      // GitHub token dans Jenkins Credentials
        SONAR_TOKEN      = credentials('sonar-token')       // SonarQube token dans Jenkins Credentials
    }

    stages {
        stage('Checkout') {
            steps {
                echo '🔄 Clonage du dépôt GitHub...'
                git branch: 'main',
                    url: "https://github.com/ghofrane-dridi/devSecOps.git",
                    credentialsId: 'github-creds'
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
                        -Dsonar.projectKey=devsecops \
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
                echo '🚀 Déploiement vers Nexus...'

                // Injection des credentials Nexus configurés dans Jenkins (user/pass)
                withCredentials([usernamePassword(credentialsId: 'nexus-creds', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
                    // Déploie avec les identifiants, en utilisant settings.xml spécifique si nécessaire
                    sh '''
                        mvn deploy -Dusername=$NEXUS_USER -Dpassword=$NEXUS_PASS
                    '''
                }
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline terminé avec succès.'
        }
        failure {
            echo '❌ Échec du pipeline. Consultez les logs.'
        }
        aborted {
            echo '⚠️ Pipeline interrompu manuellement.'
        }
        always {
            echo '🏁 Fin du pipeline.'
        }
    }
}
