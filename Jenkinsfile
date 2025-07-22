pipeline {
    agent any

    tools {
        maven 'M3'
        jdk 'JDK 17'
    }

    environment {
        SONAR_TOKEN    = credentials('sonar-token')
        SONAR_HOST_URL = 'http://localhost:9000'
        NEXUS_USER     = credentials('nexus-creds').username
        NEXUS_PASSWORD = credentials('nexus-creds').password
        NEXUS_URL      = 'http://localhost:8181/repository/maven-releases/'  // adapte selon ton repo Nexus
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
                    withEnv(["SONAR_PROJECT_KEY=devsecops"]) {
                        sh '''
                            mvn sonar:sonar \
                              -Dsonar.projectKey=$SONAR_PROJECT_KEY \
                              -Dsonar.host.url=$SONAR_HOST_URL \
                              -Dsonar.login=$SONAR_TOKEN
                        '''
                    }
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                echo '🚀 Deploying artifact to Nexus...'
                sh '''
                    mvn deploy \
                      -Dnexus.username=$NEXUS_USER \
                      -Dnexus.password=$NEXUS_PASSWORD \
                      -DaltDeploymentRepository=nexus::default::$NEXUS_URL
                '''
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline completed successfully!'
        }
        failure {
            echo '❌ Pipeline failed. Check logs for details.'
        }
    }
}
