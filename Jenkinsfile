#!groovy
def BN = BRANCH_NAME == "master" || BRANCH_NAME.startsWith("releases/") ? BRANCH_NAME : "master"

library "knime-pipeline@$BN"

properties([
	pipelineTriggers([
		upstream('knime-js-core/' + env.BRANCH_NAME.replaceAll('/', '%2F'))
	]),
	buildDiscarder(logRotator(numToKeepStr: '5')),
	disableConcurrentBuilds()
])

try {
	knimetools.defaultTychoBuild('org.knime.update.js.base')

    workflowTests.runTests(
        dependencies: [
            repositories: ['knime-js-base', 'knime-js-core', 'knime-timeseries', 'knime-distance', 'knime-jep',
			'knime-weka', 'knime-network', 'knime-xml', 'knime-datageneration',
			'knime-chemistry', 'knime-chromium', 'knime-textprocessing', 'knime-svm', 'knime-dl4j', 'knime-jfreechart',
			'knime-js-labs', 'knime-stats']
        ]
    )

    stage('Sonarqube analysis') {
        env.lastStage = env.STAGE_NAME
        workflowTests.runSonar()
    }
 } catch (ex) {
	 currentBuild.result = 'FAILED'
	 throw ex
 } finally {
	 notifications.notifyBuild(currentBuild.result);
 }
/* vim: set ts=4: */
