pipeline {
    agent { label "Jenkins-Agent" }

    environment {
        APP_NAME = "register-app-pipeline"
        IMAGE_TAG = "${BUILD_NUMBER}"  // Ensures IMAGE_TAG is always available
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

        stage("Update the Deployment Tags") {
            steps {
                sh """
                    echo "Before Update:"
                    cat deployment.yaml

                    sed -i 's|image: ${APP_NAME}:.*|image: ${APP_NAME}:${IMAGE_TAG}|' deployment.yaml

                    echo "After Update:"
                    cat deployment.yaml
                """
            }
        }

        stage("Push the changed deployment file to Git") {
            steps {
                sh """
                    git config user.name "mohancc1"
                    git config user.email "mohan.cc1@gmail.com"
                    git add deployment.yaml || echo "No changes to add"
                    git commit -m "Updated image tag to ${IMAGE_TAG}" || echo "No changes to commit"
                    git push https://mohancc1:${GIT_TOKEN}@github.com/mohancc1/newrepo.git HEAD:main
                """
            }
        }
    }

    // Make sure to define GIT_TOKEN as a secret text credential in Jenkins with ID 'github-token'
    // This avoids exposing your credentials directly in the URL
    environment {
        GIT_TOKEN = credentials('github-token')  // secure push
    }
}
