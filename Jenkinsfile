pipeline {
    agent { label 'Jenkins-Agent' }

    tools {
        jdk 'Java17'
        maven 'Maven3'
    }

    environment {
        SONAR_PROJECT_KEY = 'myapp'
        SONAR_TOKEN       = credentials('jenkins-sonarqube-token') // Still useful to define if needed elsewhere or for explicit pass
        APP_NAME          = "register-app-pipeline"
        RELEASE           = "1.0.0"
        DOCKER_USER       = "mohancc1" // 🔁 Replace with your actual DockerHub username if different
        DOCKER_PASS       = credentials('dockerhub') // 🔐 Credentials ID from Jenkins
        IMAGE_NAME        = "${DOCKER_USER}/${APP_NAME}"
        IMAGE_TAG         = "${RELEASE}-${BUILD_NUMBER}"
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
                        -Dsonar.projectKey=$SONAR_PROJECT_KEY
                        # -Dsonar.host.url and -Dsonar.login are handled by withSonarQubeEnv
                    '''
                }
            }
        }

        stage("Quality Gate") {
            steps {
                script {
                    // Ensure the 'sonarqube-server' configuration in Jenkins uses 'jenkins-sonarqube-token'
                    waitForQualityGate abortPipeline: false
                }
            }
        }

        stage("Build & Push Docker Image") {
            steps {
                script {
                    def docker_image
                    docker.withRegistry('', DOCKER_PASS) {
                        docker_image = docker.build("${IMAGE_NAME}", ".") // Added '.' for Dockerfile context
                        docker_image.push("${IMAGE_TAG}")
                        docker_image.push("latest")
                    }
                }
            }
        }
    }
}
