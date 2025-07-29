pipeline {
    agent { label 'Jenkins-Agent' }

    tools {
        jdk 'java17'
        maven 'Maven3'
    }

    environment {
        SONAR_PROJECT_KEY = 'myapp' // ✅ Change this to match your SonarQube project key if needed
        SONAR_TOKEN = credentials('jenkins-sonarqube-token') // ✅ Must match Jenkins > Credentials ID for your Sonar token
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
                // ✅ 'github' must match your Jenkins credential ID for GitHub
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
script {
waitForQualityGate abortPipeline: false, credentialsId: 'jenkins-sonarqube-token'
}
            }
        }
    }
}
