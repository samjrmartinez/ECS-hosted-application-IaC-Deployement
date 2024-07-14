pipeline {
    agent {
        node {
            label 'us-east-1'
        }
    }

    parameters {
        choice(name: 'ENVIRONMENT', choices: ['non-prod', 'prod', 'custom'], description: 'Environment to build.')
        string(name: 'CUSTOM_BRANCH', defaultValue: '', description: 'Custom branch.')
    }

    // S: PIPELINE ENVIRONMENT
    environment {
        TOKEN = credentials('github-rnjenkins-token')
        IS_NON_PROD = "${(params.ENVIRONMENT == 'non-prod')}"
        BRANCH_NAME = getBranchName(params.ENVIRONMENT, params.CUSTOM_BRANCH)
        property_file = getEnvironmentFile(params.ENVIRONMENT)
        println "Using script file: ${property_file}"
        REBUILD_SCRIPT = "${'./rebuild-' + property_file}"
    }
    // E: PIPELINE ENVIRONMENT

    // S: PIPELINE OPTIONS
    options {
        timeout(time: 90, unit: 'MINUTES')
        disableConcurrentBuilds()
        timestamps()
    }
    // E: PIPELINE OPTIONS

    // S: PIPELINE TOOLS
    tools {
        maven 'Maven 3.8.6'
        jdk 'Java 17.0.6'
    }
    // E: PIPELINE TOOLS

    // S: PIPELINE STAGES
    stages {

        // S: VALIDATE
        stage('Validate') {
            steps {
                script {
                    if (!BRANCH_NAME?.trim()) {
                        currentBuild.result = 'ABORTED'
                        error('Invalid parameters.')
                    }

                    // Get branch name to build
                    env.BRANCH_NAME = BRANCH_NAME
                }
            }
        }
        // E: VALIDATE

        // S: MERGE
        stage('Merge') {
            steps {
                cleanWs(deleteDirs: true,
                        disableDeferredWipeout: true)
                checkout changelog: true, poll: true, scm:
                        [$class                           : 'GitSCM', branches: [[name: BRANCH_NAME]],
                         doGenerateSubmoduleConfigurations: false,
                         extensions                       : [[$class : 'PreBuildMerge',
                                                              options: [fastForwardMode: 'NO_FF', mergeRemote: 'origin', mergeStrategy: 'DEFAULT',
                                                                        mergeTarget    : BRANCH_NAME]]],
                         submoduleCfg                     : [],
                         userRemoteConfigs                : [[url: "https://${TOKEN}@github.com/samjrmartinez/ECS-hosted-application-IaC-Deployment"]]]
            }
        }
        // E: MERGE

        // S: BUILD
        stage('Build') {
            steps {
                script {
                    def mvnCmd = "mvn -U clean package "
                    def mvnRet = sh(returnStatus: true, script: "${mvnCmd}")
                    echo "Maven returned ${mvnRet}"
                    if (mvnRet != 0) {
                        error('Failure')
                    }
                }
            }
        }
        // E: BUILD

        // S: UPLOAD
        stage('Upload') {
            steps {
                rtUpload(
                        serverId: 'custom-jfrog-repo',
                        failNoOp: true,
                        // If there is a change version in the pom, update the target folder too in
                        // the following spec file
                        specPath: 'artifact-props.json'
                )
            }
        }
        // E:UPLOAD

        // S: DEPLOY
        stage('Deploy') {
            steps {
                script {
                    println "Pending to Implement"
                }
            }
        }
        // E: DEPLOY
    }
    // E: PIPELINE STAGES
    post {
        unstable {
            script {
                currentBuild.result = 'UNSTABLE'
            }
        }
        failure {
            script {
                currentBuild.result = 'FAILED'
            }
        }
        success {
            script {
                currentBuild.result = 'SUCCESS'
            }
        }
    }
}

static def getEnvironmentFile(envName) {
    if ("custom" == envName) {
        return 'non-prod.sh'
    } else {
        return envName + '.sh'
    }
}

static def getBranchName(envName, customBranchName) {
    if ("non-prod" == envName) {
        return "develop"
    } else if ("prod" == envName) {
        return "main"
    } else {
        return customBranchName
    }
}
