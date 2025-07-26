pipeline {
    agent any

    tools {
        // Assurez-vous que ces outils sont configurés dans Jenkins > Manage Jenkins > Global Tool Configuration
        maven 'M3' // Remplacez par le nom exact de votre installation Maven
        jdk 'JDK 17'       // Remplacez par le nom exact de votre installation JDK
    }

    environment {
        // Définissez vos variables d'environnement
        DOCKER_IMAGE_NAME = "votre_nom_utilisateur_dockerhub/devsecops-app" // Remplacer
        DOCKER_TAG = "latest"
        // Utilisez des credentials stockés dans Jenkins pour la sécurité
        // Credentials ID: docker-hub-creds (doit être de type Username and Password)
        // Credentials ID: nexus-creds (doit être de type Username and Password - si déploiement direct)
    }

    stages {
        stage('Checkout') {
            steps {
                echo '📥 Cloning source code...'
                git branch: 'main', url: 'https://github.com/ghofrane-dridi/devSecOps.git' // Adaptez si nécessaire
            }
        }

        stage('Build & Test') {
            steps {
                echo '🔧 Building with Maven and running tests...'
                // Option 1: Utiliser la configuration par défaut de Maven (contournant Nexus si pb)
                // sh 'mvn clean verify jacoco:report'

                // Option 2: Utiliser le settings.xml de Jenkins (si Nexus est configuré)
                 sh 'mvn -s /var/lib/jenkins/.m2/settings.xml clean verify jacoco:report'
            }
            post {
                always {
                    // Publier les rapports de test et de couverture même en cas d'échec
                    junit '**/target/surefire-reports/*.xml'
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/site/jacoco',
                        reportFiles: 'index.html',
                        reportName: 'JaCoCo Report'
                    ])
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo '🔍 Analyzing code with SonarQube...'
                // Assurez-vous que SonarQube est configuré dans Jenkins (Manage Jenkins > Configure System > SonarQube servers)
                // et que le scanner est installé (Manage Jenkins > Global Tool Configuration > SonarQube Scanner)
                withSonarQubeEnv('SonarQube') { // 'SonarQube' est le nom configuré dans Jenkins
                     sh "mvn -s /var/lib/jenkins/.m2/settings.xml sonar:sonar"
                     // OU sh "mvn sonar:sonar" (si vous utilisez la config par défaut)
                }
            }
        }

        // Optionnel: Attendre la qualité gate de SonarQube
        // stage("SonarQube Quality Gate") {
        //     steps {
        //         timeout(time: 1, unit: 'HOURS') {
        //             waitForQualityGate abortPipeline: true
        //         }
        //     }
        // }

        stage('Deploy to Nexus') {
            steps {
                echo '📦 Deploying artifacts to Nexus...'
                // Utilisez le plugin Nexus Artifact Uploader ou Maven
                // Si vous utilisez mvn deploy, assurez-vous que votre pom.xml pointe vers le bon dépôt Nexus
                // et que les credentials sont dans le settings.xml
                script {
                    // Option 1: Maven Deploy (nécessite configuration dans pom.xml et settings.xml)
                    sh 'mvn -s /var/lib/jenkins/.m2/settings.xml deploy -DskipTests'

                    // Option 2: Plugin Nexus Artifact Uploader (à configurer)
                    // nexusArtifactUploader(
                    //     nexusVersion: 'nexus3',
                    //     protocol: 'http',
                    //     nexusUrl: 'localhost:8181', // Ou l'IP/URL de votre Nexus
                    //     groupId: 'com.example',
                    //     version: '0.0.1-SNAPSHOT',
                    //     repository: 'maven-releases', // Ou maven-snapshots
                    //     credentialsId: 'nexus-creds', // Credentials ID dans Jenkins
                    //     artifacts: [
                    //         [artifactId: 'demo', file: 'target/demo-0.0.1-SNAPSHOT.jar', type: 'jar']
                    //     ]
                    // )
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                echo '🐳 Building Docker image...'
                script {
                    // Récupère le numéro de build Jenkins pour le tagger
                    def buildNumber = env.BUILD_NUMBER
                    // Vous pouvez aussi utiliser GIT_COMMIT si vous préférez
                    // def gitCommit = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()

                    // Tag avec le numéro de build ET latest
                    sh "docker build -t ${DOCKER_IMAGE_NAME}:${buildNumber} ."
                    sh "docker tag ${DOCKER_IMAGE_NAME}:${buildNumber} ${DOCKER_IMAGE_NAME}:latest"
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                echo '🚀 Pushing Docker image to Docker Hub...'
                script {
                    withCredentials([usernamePassword(credentialsId: 'docker-hub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        sh "docker login -u ${DOCKER_USER} -p ${DOCKER_PASS}"
                        def buildNumber = env.BUILD_NUMBER
                        sh "docker push ${DOCKER_IMAGE_NAME}:${buildNumber}"
                        sh "docker push ${DOCKER_IMAGE_NAME}:latest"
                        sh "docker logout"
                    }
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                echo '🚢 Deploying application with Docker Compose...'
                script {
                     // Option 1: Si Jenkins tourne sur la VM jenkins-vm et a accès à Docker
                     // Assurez-vous que le docker-compose.yml est dans le workspace
                     sh 'docker compose down || true' // Arrête les éventuels services précédents
                     sh 'docker compose up -d --build' // Reconstruit et démarre

                     // Option 2: Si vous déployez sur une autre machine, utilisez SSH
                     // withCredentials([sshUserPrivateKey(credentialsId: 'deploy-ssh-key', keyFileVariable: 'SSH_KEY', usernameVariable: 'SSH_USER')]) {
                     //    sh '''
                     //        scp -i $SSH_KEY docker-compose.yml $SSH_USER@remote_host:/path/to/deploy/
                     //        ssh -i $SSH_KEY $SSH_USER@remote_host "cd /path/to/deploy && docker compose down && docker compose pull && docker compose up -d"
                     //    '''
                     // }
                }
            }
        }

        // stage('Setup Monitoring (Grafana/Prometheus)') {
        //     steps {
        //         echo '📊 Setting up monitoring...'
        //         // Cette étape dépend fortement de votre configuration.
        //         // Si Prometheus/Grafana sont gérés par le même docker-compose.yml, ils démarreront avec l'étape précédente.
        //         // Sinon, vous pouvez avoir un docker-compose séparé ou utiliser d'autres outils.
        //         // sh 'docker compose -f monitoring-docker-compose.yml up -d'
        //     }
        // }
    }

    post {
        always {
            echo '🧹 Cleaning up...'
            // Ajoutez ici des étapes de nettoyage si nécessaire
            // sh 'docker system prune -f' // Exemple: nettoyer les images non utilisées (attention!)
        }
        success {
            echo '✅ Pipeline completed successfully!'
            emailext (
                subject: "SUCCESS: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",
                body: """<p>${env.JOB_NAME} - Build #${env.BUILD_NUMBER} - SUCCESS</p>
                         <p>Check console output at <a href="${env.BUILD_URL}">Build URL</a></p>""",
                to: 'ghofrane.dridi@esprit.tn' // Remplacer par l'email souhaité
            )
        }
        failure {
            echo '❌ Pipeline failed!'
            emailext (
                subject: "FAILURE: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",
                body: """<p>${env.JOB_NAME} - Build #${env.BUILD_NUMBER} - FAILURE</p>
                         <p>Check console output at <a href="${env.BUILD_URL}">Build URL</a></p>
                         <p><a href="${env.BUILD_URL}console">Console Output</a></p>""",
                to: 'ghofrane.dridi@esprit.tn' // Remplacer par l'email souhaité
            )
        }
    }
}
