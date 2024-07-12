#!groovy
def BN = (BRANCH_NAME == 'master' || BRANCH_NAME.startsWith('releases/')) ? BRANCH_NAME : 'releases/2024-12'

library "knime-pipeline@$BN"

properties([
    pipelineTriggers([
        upstream('knime-js-core/' + env.BRANCH_NAME.replaceAll('/', '%2F'))
    ]),
    parameters(workflowTests.getConfigurationsAsParameters() +
        booleanParam(defaultValue: true, description: 'Test image generation with CEF', name: 'CEF') +
        booleanParam(defaultValue: false, description: 'Test image generation with Chromium', name: 'CHROMIUM')),
    buildDiscarder(logRotator(numToKeepStr: '5')),
    disableConcurrentBuilds()
])

try {
    knimetools.defaultTychoBuild('org.knime.update.js.base')

    testConfigs = [:]
    baseIus = [ 'org.knime.features.quickform.legacy.feature.group', 'org.knime.features.ext.r.feature.group',
                'org.knime.features.xml.feature.group', 'org.knime.features.chem.types.feature.group',
                'org.knime.features.datageneration.feature.group', 'org.knime.features.ext.jfreechart.feature.group',
                'org.knime.features.network.feature.group', 'org.knime.features.ext.textprocessing.feature.group',
                'org.knime.features.ext.weka.feature.group' ]
    repositories = [ 'knime-js-base', 'knime-js-core', 'knime-timeseries', 'knime-distance', 'knime-jep', 'knime-weka',
                     'knime-network', 'knime-xml', 'knime-datageneration', 'knime-chemistry', 'knime-textprocessing',
                     'knime-svm', 'knime-jfreechart', 'knime-js-labs', 'knime-stats', 'knime-r', 'knime-python', 'knime-database',
                     'knime-filehandling', 'knime-kerberos', 'knime-excel' ]
    if (params.CEF) {
        testConfigs['cef'] = {
            stage('Workflow tests with CEF') {
                withEnv(["KNIME_JS_IMAGE_GENARATION_MODE=cef"]) {
                    workflowTests.runTests(
                        dependencies: [
                            repositories: repositories + 'knime-cef',
                            ius: baseIus + 'org.knime.features.browser.cef.feature.group'
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
                            repositories: repositories + 'knime-chromium',
                            ius: baseIus + 'org.knime.features.browser.chromium.feature.group'
                        ],
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
