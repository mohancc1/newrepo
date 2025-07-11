pipeline {
    agent { label 'Jenkins-Agent' }

    tools {
        jdk 'Java17'
        maven 'Maven3'
    }

    environment {
        SONAR_PROJECT_KEY = 'myapp'
        SONAR_TOKEN       = credentials('jenkins-sonarqube-token')
        
        APP_NAME    = "register-app-pipeline"
        RELEASE     = "1.0.0"
        IMAGE_TAG   = "${RELEASE}-${BUILD_NUMBER}"
    }

    stages {
        stage("Cleanup Workspace") {
            steps {
                cleanWs()
            }
        }

        stage("Checkout from SCM") {
            steps {
                git branch: 'main', credentialsId: 'github', url: 'https://github.com/mohancc1/newrepo'
            }
        }

        stage("Build Application") {
            steps {
                sh 'mvn clean package'
            }
        }

        stage("Test Application") {
            steps {
                sh 'mvn test'
            }
        }

        stage("SonarQube Analysis") {
            steps {
                withSonarQubeEnv('sonarqube-server') {
                    sh '''
                        mvn sonar:sonar \
                        -Dsonar.projectKey=$SONAR_PROJECT_KEY \
                        -Dsonar.host.url=http://23.22.244.79:9000 \
                        -Dsonar.login=$SONAR_TOKEN
                    '''
                }
            }
        }

        stage("Quality Gate") {
            steps {
                script {
                    waitForQualityGate abortPipeline: false, credentialsId: 'jenkins-sonarqube-token'
                }
            }
        }

        stage("Build & Push Docker Image") {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )
                ]) {
                    script {
                        def imageName = "${DOCKER_USER}/${APP_NAME}"

                        docker.withRegistry('https://index.docker.io/v1/', 'dockerhub') {
                            def dockerImage = docker.build(imageName)
                            dockerImage.push("${IMAGE_TAG}")
                            dockerImage.push("latest")
                        }
                    }
                }
            }
        }

        stage("Trivy Scan") {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )
                ]) {
                    script {
                        sh """
                            docker run -v /var/run/docker.sock:/var/run/docker.sock \
                            aquasec/trivy image ${DOCKER_USER}/${APP_NAME}:latest \
                            --no-progress --scanners vuln \
                            --exit-code 0 --severity HIGH,CRITICAL --format table
                        """
                    }
                }
            }
        }

        stage("Cleanup Artifacts") {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )
                ]) {
                    script {
                        sh "docker rmi ${DOCKER_USER}/${APP_NAME}:${IMAGE_TAG} || true"
                        sh "docker rmi ${DOCKER_USER}/${APP_NAME}:latest || true"
                    }
                }
            }
        }
    }
}
