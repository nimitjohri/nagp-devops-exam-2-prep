private static final List<String> emailRecipients = ['nimitjohri5@gmail.com', 'heenam.66@gmail.com']

def scmVars
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
                            scmVars = checkout scm
                            echo scmVars.GIT_BRANCH
                        }
                    }
                }
            }
        }

        stage('check branch name') {
            steps {
                script {
                    bat 'echo "Branch name" + %env.GIT_BRANCH%'
                    echo scmVars.GIT_BRANCH
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
        failure {
         script {
               stage ('Send Email (Failure)') {
                     emailext(
                           recipientProviders: [[$class: 'CulpritsRecipientProvider']],
                           to: emailRecipients.join(', '),
                           subject: "Build failed in Jenkins: ${env.JOB_NAME} ${env.BUILD_DISPLAY_NAME}",
                           attachLog: true,
                           body: """
                              <html>
                              <body>
                              <p>Check console output <a href='${env.BUILD_URL}console'>here</a>.</p>
                              </body>
                              </html>
                           """
                     )
                  }
               }
            }
        success {
         script {
               stage ('Send Email (Success)') {
                    mail bcc: '', body: "<b>Example</b><br>\n<br>Project: ${env.JOB_NAME} <br>Build Number: ${env.BUILD_NUMBER} <br> URL de build: ${env.BUILD_URL}", cc: '', charset: 'UTF-8', from: '', mimeType: 'text/html', replyTo: '', subject: "ERROR CI: Project name -> ${env.JOB_NAME}", to: "nimit.johri@nagarro.com";
                    //  emailext(
                    //        recipientProviders: [[$class: 'CulpritsRecipientProvider']],
                    //        to: emailRecipients.join(', '),
                    //        subject: "Build failed in Jenkins: ${env.JOB_NAME} ${env.BUILD_DISPLAY_NAME}",
                    //        attachLog: true,
                    //        body: """
                    //           <html>
                    //           <body>
                    //           <p>Check console output <a href='${env.BUILD_URL}console'>here</a>.</p>
                    //           </body>
                    //           </html>
                    //        """
                    //  )
                  }
               }
            }

    }
}