pipeline {
    agent any
    tools
    {
        maven 'M3'
    }
    options {
        timestamps()
        timeout(time: 1, unit: 'HOURS')

        skipDefaultCheckout()
        buildDiscarder(logRotator(daysToKeepStr: '10', numToKeepStr: '10'))

        disableConcurrentBuilds()
    }

    environment {
        BUILD_DIR = 'nagp-devops-exercise-pipeline'
        ARTIFACTORY_CREDENTIALS_ID = 'artifactory'
        DOCKER_CREDENTIALS_ID = "dockerhub"
    }

    stages {
        stage('Sequential Setup Steps') {
            stages {
                stage ('Checkout') {
                    steps {
                        script {
                            checkout scm
                        }
                    }
                }
            }
        }

        stage('check branch name') {
            steps {
                script {
                    bat 'echo "Branch name" + %env.BRANCH_NAME%'
                }
            }
        }

        stage ('Build') {
            steps {
                script {
                    bat 'mvn clean install'
                }
            }
        }

        stage ('Unit Testing') {
            steps {
                script {
                    bat 'mvn test'
                }
            }
        }

        stage ("Sonar Analysis") {
            steps {
                withSonarQubeEnv("SoarQube8.4") {
                    bat 'mvn sonar:sonar'
                }
            }
        }
        stage ('Upload to Artifactory') {
            steps {
                rtMavenDeployer(
                    id: 'dev-deployer',
                    serverId: 'artifactory 6.20',
                    releaseRepo: 'nagp-devops-exam-2-dev',
                    snapshotRepo: 'nagp-devops-exam-2-dev'
                )

                rtMavenRun(
                    pom: 'pom.xml',
                    goals: 'clean install',
                    deployerId: 'dev-deployer'
                )

                rtPublishBuildInfo(
                    serverId: 'artifactory 6.20'
                )
            }
        }

        stage ('Docker Image') {
            steps {
                withDockerServer([uri:'tcp://localhost:2375', credentialsId: env.DOCKER_CREDENTIALS_ID]) {
                    withDockerRegistry([credentialsId: env.DOCKER_CREDENTIALS_ID, url: "https://docker.io/"]) {
                        bat 'docker login -u nimit07 -p Human@123'
                        bat 'docker build -t nimit07/nagp-devops-dev:%BUILD_NUMBER% --no-cache -f Dockerfile .'
                    }
                }
            }
        }

        stage ('Push To DTR') {
            steps {
                bat 'docker push nimit07/nagp-devops-dev:%BUILD_NUMBER%'
            }
        }

        stage ('Stopping running container') {
            steps {
                bat '''
                for /f %%i in ('docker ps -aqf "name=^nagp-devops-dev"') do set containerId=%%i
                echo %containerId%
                If "%containerId%" == "" (
                echo "No Container running"
                ) ELSE (
                docker stop %containerId%
                docker rm -f %containerId%
                )'''
            }
        }

        stage ('Docker deployment') {
            steps {
                bat 'docker run --name nagp-devops-dev -d -p 6200:8080 nimit07/nagp-devops-dev:%BUILD_NUMBER%'
            }
        }
    }

    post {
        always {
            junit 'target/surefire-reports/*.xml'
        }
    }
}