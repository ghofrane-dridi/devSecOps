pipeline {
    agent any

    tools {
        maven 'M3'
        jdk 'JDK 17'
    }

    environment {
        SONAR_TOKEN    = credentials('sonar-token')
        SONAR_HOST_URL = 'http://localhost:9000'
    }

    stages {
        stage('Checkout') {
            steps {
                echo '📥 Cloning source code...'
                checkout([$class: 'GitSCM',
                    branches: [[name: '*/main']],
                    userRemoteConfigs: [[
                        url: 'https://github.com/ghofrane-dridi/devSecOps.git',
                        credentialsId: 'github-creds'
                    ]]
                ])
            }
        }

        stage('Build') {
            steps {
                echo '🔧 Building with Maven...'
                sh 'mvn clean install'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo '🔍 Running SonarQube analysis...'
                withSonarQubeEnv('SonarQube') {
                    // Passer les variables en env pour éviter interpolation Groovy directe
                    withEnv(["SONAR_PROJECT_KEY=devsecops"]) {
                        sh '''
                            mvn sonar:sonar \
                              -Dsonar.projectKey=$SONAR_PROJECT_KEY \
                              -Dsonar.host.url=$SONAR_HOST_URL \
                              -Dsonar.token=$SONAR_TOKEN
                        '''
                    }
                }
            }
        }
    }  // <-- Fermeture du bloc stages ici

    post {
        success {
            echo '✅ Pipeline completed successfully!'
        }
        failure {
            echo '❌ Pipeline failed. Check logs for details.'
        }
    }
}
