pipeline {
    agent any

    tools {
        maven 'M3'       // Nom configuré dans Jenkins Global Tool Configuration
        jdk 'JDK 17'     // Idem
    }

    environment {
        SONARQUBE_SERVER = 'SonarQube'     // Nom configuré dans Jenkins > Manage Jenkins > Configure System > SonarQube servers
        SONAR_TOKEN = credentials('sonar-token')     // Créez un Secret Text dans Jenkins (Manage Jenkins > Credentials)
        DOCKER_REGISTRY = 'http://localhost:8081/repository/maven-releases/' // URL de Nexus repo (format Maven)
    }

    stages {

        stage('Checkout') {
            steps {
                git url: 'https://github.com/ghofrane-dridi/devSecOps.git', branch: 'main'
            }
        }

        stage('Build Maven') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv("${SONARQUBE_SERVER}") {
                    sh "mvn sonar:sonar -Dsonar.login=$SONAR_TOKEN"
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
                sh 'docker build -t devsecops-springapp .'
            }
        }

        stage('Push to Nexus') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'nexus-credentials', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
                    sh '''
                        mvn deploy -DaltDeploymentRepository=nexus::default::${DOCKER_REGISTRY} \
                                   -Dnexus.username=$NEXUS_USER -Dnexus.password=$NEXUS_PASS
                    '''
                }
            }
        }

        stage('Run Docker Container') {
            steps {
                sh '''
                    docker stop springapp || true
                    docker rm springapp || true
                    docker run -d --name springapp -p 8080:8080 devsecops-springapp
                '''
            }
        }
    }

    post {
        success {
            echo "Pipeline executed successfully!"
        }
        failure {
            echo "Pipeline failed. Please check logs."
        }
    }
}
