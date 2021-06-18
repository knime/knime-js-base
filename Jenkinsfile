#!groovy
def BN = BRANCH_NAME == "master" || BRANCH_NAME.startsWith("releases/") ? BRANCH_NAME : "master"

library "knime-pipeline@$BN"

properties([
    pipelineTriggers([
        upstream('knime-js-core/' + env.BRANCH_NAME.replaceAll('/', '%2F'))
    ]),
    parameters(workflowTests.getConfigurationsAsParameters() +
        booleanParam(defaultValue: false, description: 'Test image generation with CEF', name: 'CEF') +
        booleanParam(defaultValue: true, description: 'Test image generation with Chromium', name: 'CHROMIUM')),
    buildDiscarder(logRotator(numToKeepStr: '5')),
    disableConcurrentBuilds()
])

try {
    knimetools.defaultTychoBuild('org.knime.update.js.base')

    testConfigs = [:]
    repositories = [ 'knime-js-base', 'knime-js-core', 'knime-timeseries', 'knime-distance', 'knime-jep', 'knime-weka',
                     'knime-network', 'knime-xml', 'knime-datageneration', 'knime-chemistry', 'knime-textprocessing',
                     'knime-svm', 'knime-jfreechart', 'knime-js-labs', 'knime-stats', 'knime-r', 'knime-database',
                     'knime-filehandling', 'knime-kerberos' ]
    if (params.CEF) {
        testConfigs['cef'] = {
            stage('Workflow tests with CEF') {
                withEnv(["KNIME_JS_IMAGE_GENARATION_MODE=cef"]) {
                    workflowTests.runTests(
                        dependencies: [
                            repositories: repositories,
                            ius: [ 'com.knime.features.workbench.cef.feature.group' ]
                        ]
                    )
                }
            }
        }
    }

    if (params.CHROMIUM) {
        testConfigs['chromium'] = {
            stage('Workflow tests with Chromium') {
                withEnv(["KNIME_JS_IMAGE_GENARATION_MODE=chromium"]) {
                    workflowTests.runTests(
                        dependencies: [
                            repositories: repositories + 'knime-chromium'
                        ]
                    )
                }
            }
        }
    }

    parallel testConfigs

    stage('Sonarqube analysis') {
         env.lastStage = env.STAGE_NAME
        workflowTests.runSonar()
    }
} catch (ex) {
    currentBuild.result = 'FAILURE'
    throw ex
} finally {
    notifications.notifyBuild(currentBuild.result);
}
/* vim: set shiftwidth=4 expandtab smarttab: */
