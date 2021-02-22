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
                        // deleteDir()
                        // dir(env.BUILD_DIR) {
                            script {
                                checkout scm
                            }
                        // }
                    }
                }

                stage ('Stash') {
                    steps {
                        stash includes: '**', name: 'source', useDefaultExcludes: false
                    }
                }
            }
            // post {
            //     cleanup { deleteDir() }
            // }
        }

        stage ('Build') {
            steps {
                // deleteDir()
                // unstash 'source'
                // dir(env.BUILD_DIR) {
                    script {
                        bat 'mvn clean install'
                    }
                // }
            }
        }

        stage ('Unit Testing') {
            steps {
                // deleteDir()
                // unstash 'source'
                // dir(env.BUILD_DIR) {
                    script {
                        bat 'mvn test'
                    }
                // }
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
                    id: 'deployer',
                    serverId: 'artifactory 6.20',
                    releaseRepo: 'nagp-devops',
                    snapshotRepo: 'nagp-devops'
                )

                rtMavenRun(
                    pom: 'pom.xml',
                    goals: 'clean install',
                    deployerId: 'deployer'
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
                        bat 'docker build -t nimit07/nagpdevops:%BUILD_NUMBER% --no-cache -f Dockerfile .'
                    }
                }
            }
        }

        stage ('Push To DTR') {
            steps {
                bat 'docker push nimit07/nagpdevops:%BUILD_NUMBER%'
            }
        }

        stage ('Stopping running container') {
            steps {
                bat '''
                for /f %%i in ('docker ps -aqf "name=^nagpdevops"') do set containerId=%%i
                echo %containerId%
                If "%containerId%" == "" (
                echo "No Container running"
                ) ELSE (
                docker stop %ContainerId%
                docker rm -f %ContainerId%
                )'''
            }
        }

        stage ('Docker deployment') {
            steps {
                bat 'docker run --name nagpdevops -d -p 7000:3000 -p 8080:8080 nimit07/nagpdevops:%BUILD_NUMBER%'
            }
        }
    }

    post {
        always {
            junit 'target/surefire-reports/*.xml'
        }
    }
}