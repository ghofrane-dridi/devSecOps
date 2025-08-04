pipeline {
    agent any

    tools {
        jdk 'JDK 17'         // Nom défini dans Jenkins > Global Tool Configuration
        maven 'M3'           // Idem pour Maven
    }

    environment {
        SONAR_HOST_URL = 'http://localhost:9000'
        DOCKER_IMAGE = 'devsecops:1.0.1-SNAPSHOT'
        DOCKER_TAG = '1.0.1-SNAPSHOT'
        NEXUS_URL = 'http://localhost:8081'
        NEXUS_REPO = 'maven-releases'
        NEXUS_CREDENTIALS_ID = 'nexus-credentials' // Crée ce secret dans Jenkins si pas encore fait
    }

    stages {

        stage('Checkout') {
            steps {
                git credentialsId: 'github-token', url: 'https://github.com/ghofrane-dridi/devSecOps.git'
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean verify'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    withCredentials([string(credentialsId: 'sonarqube-token', variable: 'SONAR_TOKEN')]) {
                        sh '''
                            mvn sonar:sonar \
                                -Dsonar.projectKey=devsecops \
                                -Dsonar.host.url=$SONAR_HOST_URL \
                                -Dsonar.login=$SONAR_TOKEN
                        '''
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 2, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh '''
                    docker build -t $DOCKER_IMAGE .
                '''
            }
        }

        stage('Push to Nexus') {
            steps {
                withCredentials([usernamePassword(credentialsId: "${NEXUS_CREDENTIALS_ID}", passwordVariable: 'NEXUS_PASS', usernameVariable: 'NEXUS_USER')]) {
                    sh '''
                        mvn deploy -DaltDeploymentRepository=${NEXUS_REPO}::default::${NEXUS_URL}/repository/${NEXUS_REPO} \
                                   -Dnexus.username=$NEXUS_USER \
                                   -Dnexus.password=$NEXUS_PASS
                    '''
                }
            }
        }
    }
}
