pipeline {

    agent { label 'Jenkins-Agent' }



    tools {

        jdk 'Java17'

        maven 'Maven3'

    }



    environment {

        SONAR_PROJECT_KEY = 'myapp'

        SONAR_TOKEN       = credentials('jenkins-sonarqube-token')



        APP_NAME    = "register-app-pipeline"

        RELEASE     = "1.0.0"

        DOCKER_USER = "mohancc1" // 🔁 Replace with your actual DockerHub username if different

        DOCKER_PASS = credentials('dockerhub') // 🔐 Credentials ID from Jenkins

        IMAGE_NAME  = "${DOCKER_USER}/${APP_NAME}"

        IMAGE_TAG   = "${RELEASE}-${BUILD_NUMBER}"

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

                        -Dsonar.host.url=http://54.92.199.33:9000 \

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

                script {

                    def docker_image

                    docker.withRegistry('', DOCKER_PASS) {

                        docker_image = docker.build("${IMAGE_NAME}")

                    }



                    docker.withRegistry('', DOCKER_PASS) {

                        docker_image.push("${IMAGE_TAG}")

                        docker_image.push("latest")

                    }

                }

            }

        }

    }

} 
