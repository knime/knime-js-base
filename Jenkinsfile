#!groovy
def BN = BRANCH_NAME == "master" || BRANCH_NAME.startsWith("releases/") ? BRANCH_NAME : "master"

library "knime-pipeline@$BN"

properties([
	pipelineTriggers([
		upstream('knime-svg/' + env.BRANCH_NAME.replaceAll('/', '%2F')),
		upstream('knime-base/' + env.BRANCH_NAME.replaceAll('/', '%2F')),
		upstream('knime-js-core/' + env.BRANCH_NAME.replaceAll('/', '%2F')),
		upstream('knime-textprocessing/' + env.BRANCH_NAME.replaceAll('/', '%2F')),
		upstream('knime-expressions/' + env.BRANCH_NAME.replaceAll('/', '%2F'))
	]),
	buildDiscarder(logRotator(numToKeepStr: '5')),
	disableConcurrentBuilds()
])

try {
	knimetools.defaultTychoBuild('org.knime.update.js.base')

    workflowTests.runTests(
        dependencies: [
            repositories: ['knime-js-base', 'knime-timeseries', 'knime-distance', 'knime-jep',
			'knime-weka', 'knime-network', 'knime-xml', 'knime-datageneration',
			'knime-chemistry', 'knime-chromium', 'knime-textprocessing'], // add knime-parquet after conversion
			// ius: ['jp.co.infocom.cheminfo.marvin.feature']
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