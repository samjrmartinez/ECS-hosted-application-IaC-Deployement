pipeline {
    agent any

    // S: PIPELINE TOOLS
    tools {
        maven 'Maven 3.8.6'
        jdk 'Java 17.0.6'
    }
    // E: PIPELINE TOOLS

    environment {
        SONAR_GITHUB_ACCESS_TOKEN = credentials('SonarGithubAccessToken')
        SONAR_ACCESS_TOKEN = credentials('SonarAccessToken')
        MVN_REVISION = get_MvnRevision()
        MVN_CMD_REV = "mvn -Drevision=${MVN_REVISION} -U "
    }

    // S: PIPELINE STAGES
    stages {
        // S: COMPILE
        stage ('COMPILE') {
            steps {
                script {
                    def startTime = get_CurrentTime()
                    setBuildStatus('In progress...', 'PENDING', '1. COMPILE', startTime);
                    def mvnCmd = "${MVN_CMD_REV} clean compile "
                    def mvnret = sh(returnStatus: true, script: "${mvnCmd}")
                    echo "Maven returned ${mvnret}"
                    if (mvnret != 0) {
                      setBuildStatus('MVN build error', 'FAILURE', '1. COMPILE', startTime)
                      error('Failure BUILD')
                    }
                    setBuildStatus('Success', 'SUCCESS', '1. COMPILE', startTime)
                }
            }
        }
        // E: COMPILE

        // S: TEST
        stage ('TEST') {
          steps {
            script {
              def startTime = get_CurrentTime()
              setBuildStatus('In progress...', 'PENDING', '2. TEST', startTime);
              def mvnCmd = "${MVN_CMD_REV} test -D skipTests=false "
              def mvnret = sh(returnStatus: true, script: "${mvnCmd}")
              echo "Maven returned ${mvnret}"
              if (mvnret != 0) {
                setBuildStatus('Integration/Unit testing error', 'FAILURE', '2. TEST', startTime)
                error('Failure TESTING')
              }
              setBuildStatus('Success', 'SUCCESS', '2. TEST', startTime)
            }
          }
        }
        // E: TEST

        // S: PACKAGE
        stage ('PACKAGE') {
          steps {
            script {
              def startTime = get_CurrentTime()
              setBuildStatus('In progress...', 'PENDING', '3. PACKAGE', startTime);
              def mvnCmd = "${MVN_CMD_REV} package "
              def mvnret = sh(returnStatus: true, script: "${mvnCmd}")
              echo "Maven returned ${mvnret}"
              if (mvnret != 0) {
                setBuildStatus('Packaging error', 'FAILURE', '3. PACKAGE', startTime)
                error('Failure TESTING')
              }
              setBuildStatus('Success', 'SUCCESS', '3. PACKAGE', startTime)
            }
          }
        }
        // E: PACKAGE

        // S: SONAR
        stage ('SONAR') {
          when {
            expression { env.CHANGE_ID != null }
          }
          steps {
            withSonarQubeEnv('sonarqube') {
              script {
                def startTime = get_CurrentTime()
                setBuildStatus('In progress...', 'PENDING', '4. SONAR', startTime);
                def sonarParams = " -Dsonar.github.pullRequest=${CHANGE_ID} -Dsonar.github.repository=samjrmartinez/ECS-hosted-application-IaC-Deployment -Dsonar.github.oauth=${SONAR_GITHUB_ACCESS_TOKEN} -Dsonar.host.url=https://sonarqube.custom.com -Dsonar.login=${SONAR_ACCESS_TOKEN}"
                def mvnCmd = '${MVN_CMD_REV} sonar:sonar ' + sonarParams
                def mvnret = sh(returnStatus: true, script: "${mvnCmd}")
                echo "Maven returned ${mvnret}"
                if (mvnret != 0) {
                  setBuildStatus('sonarqube error', 'FAILURE', '4. SONAR', startTime)
                  error('Failure SONAR')
                }
                setBuildStatus('Success', 'SUCCESS', '4. SONAR', startTime)
              }
            }
          }
        }
        // E: SONAR
    }
}

def get_MvnRevision() {
  def mvnRevision
    switch(CHANGE_TARGET) {
      case 'main':
        mvnRevision = 'MAIN-SNAPSHOT'
        break
      default:
        mvnRevision = 'STAGE-SNAPSHOT'
        break
    }
    return mvnRevision
}

def get_CurrentTime() {
  script {
    return new Date()
  }
}

def get_Duration(Date startTime, Date endTime) {
  script {
    groovy.time.TimeDuration duration = groovy.time.TimeCategory.minus(endTime, startTime)
    return duration
  }
}

void setBuildStatus(String message, String state, String context, Date startTime) {
  if (state != 'PENDING') {
    def endTime = get_CurrentTime()
    def duration = get_Duration(startTime, endTime)
    message = message + ": " + duration
  }
  step([
      $class: "GitHubCommitStatusSetter",
      reposSource: [$class: "ManuallyEnteredRepositorySource", url: "https://github.com/samjrmartinez/ECS-hosted-application-IaC-Deployment"],
      contextSource: [$class: "ManuallyEnteredCommitContextSource", context: context],
      errorHandlers: [[$class: "ChangingBuildStatusErrorHandler", result: "UNSTABLE"]],
      statusResultSource: [ $class: "ConditionalStatusResultSource", results: [[$class: "AnyBuildResult", message: message, state: state]] ]
  ]);
}
