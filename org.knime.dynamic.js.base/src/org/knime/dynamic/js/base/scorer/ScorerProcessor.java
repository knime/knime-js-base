package org.knime.dynamic.js.base.scorer;

import java.util.Arrays;

import org.knime.base.node.mine.scorer.accuracy.AccuracyScorerCalculator;
import org.knime.base.util.SortingStrategy;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.dynamic.js.v30.DynamicJSConfig;
import org.knime.dynamic.js.v30.DynamicStatefulJSProcessor;
import org.knime.js.core.JSONDataTable;

public class ScorerProcessor extends DynamicStatefulJSProcessor {

    private static final String INSERTION_ORDER = "Insertion\u00A0Order";
	private static final String LEXICAL = "Lexical";
	private static final String[] AVAILABLE_STRATEGIES = new String[]{INSERTION_ORDER, LEXICAL};


	@Override
	public Object[] processInputObjects(final PortObject[] inObjects, final ExecutionContext exec, final DynamicJSConfig config)
			throws Exception {
        BufferedDataTable table = (BufferedDataTable)inObjects[0];
        SettingsModelString firstColumnModel  = (SettingsModelString) config.getModel("first_column");
        SettingsModelString secondColumnModel  = (SettingsModelString) config.getModel("second_column");
        String firstColumnName = firstColumnModel.getStringValue();
        String secondColumnName = secondColumnModel.getStringValue();
		//Check sorting strategy
		String strategy = ((SettingsModelString)config.getModel("sorting_strategy")).getStringValue();
		if (!Arrays.asList(AVAILABLE_STRATEGIES).contains(strategy)) {
			throw new IllegalArgumentException("Sorting strategy not supported: " + strategy);
		}
		SortingStrategy sortingStrategy = SortingStrategy.valueOf(strategy.replaceAll("\u00A0", ""));
		boolean sortingReversed = ((SettingsModelBoolean)config.getModel("reverse_order")).getBooleanValue();
		boolean ignoreMissingValues = ((SettingsModelBoolean)config.getModel("ignore_missing_values")).getBooleanValue();
		AccuracyScorerCalculator.ScorerCalculatorConfiguration calculatorConfig = new AccuracyScorerCalculator.ScorerCalculatorConfiguration();
		calculatorConfig.setSortingStrategy(sortingStrategy);
		calculatorConfig.setSortingReversed(sortingReversed);
		calculatorConfig.setIgnoreMissingValues(ignoreMissingValues);

        AccuracyScorerCalculator scorerCalc = AccuracyScorerCalculator.createCalculator(table, firstColumnName, secondColumnName, calculatorConfig, exec.createSubExecutionContext(0.9));

        JSONDataTable confusionMatrixTable = createJSONTableFromBufferedDataTable(scorerCalc.getConfusionMatrixTable(exec), exec.createSubExecutionContext(0.03));

        AccuracyScorerCalculator.ClassStatisticsConfiguration classStatsConfig = new AccuracyScorerCalculator.ClassStatisticsConfiguration();
        boolean isTpCalculated = getBooleanFromConfig(config, "displayTruePositives");
        boolean isFpCalculated = getBooleanFromConfig(config, "displayFalsePositives");
        boolean isTnCalculated = getBooleanFromConfig(config, "displayTrueNegatives");
        boolean isFnCalculated = getBooleanFromConfig(config, "displayFalseNegatives");
        boolean isAccuracyCalculated = getBooleanFromConfig(config, "displayAccuracy");
        boolean isBalancedAccuracyCalculated = getBooleanFromConfig(config, "displayBalancedAccuracy");
        boolean isErrorRateCalculated = getBooleanFromConfig(config, "displayErrorRate");
        boolean isFalseNegativeRateCalculated = getBooleanFromConfig(config, "displayFalseNegativeRate");
        boolean isRecallCalculated = getBooleanFromConfig(config, "displayRecall");
        boolean isPrecisionCalculated = getBooleanFromConfig(config, "displayPrecision");
        boolean isSensitivityCalculated = getBooleanFromConfig(config, "displaySensitivity");
        boolean isSpecificityCalculated = getBooleanFromConfig(config, "displaySpecificity");
        boolean isFMeasureCalculated = getBooleanFromConfig(config, "displayFMeasure");
        classStatsConfig.withTpCalculated(isTpCalculated)
        .withFpCalculated(isFpCalculated)
        .withTnCalculated(isTnCalculated)
        .withFnCalculated(isFnCalculated)
        .withAccuracyCalculated(isAccuracyCalculated)
        .withBalancedAccuracyCalculated(isBalancedAccuracyCalculated)
        .withErrorRateCalculated(isErrorRateCalculated)
        .withFalseNegativeRateCalculated(isFalseNegativeRateCalculated)
        .withRecallCalculated(isRecallCalculated)
        .withPrecisionCalculated(isPrecisionCalculated)
        .withSensitivityCalculated(isSensitivityCalculated)
        .withSpecifityCalculated(isSpecificityCalculated)
        .withFmeasureCalculated(isFMeasureCalculated);
        JSONDataTable classStatsTable = createJSONTableFromBufferedDataTable(scorerCalc.getClassStatisticsTable(classStatsConfig, exec), exec.createSubExecutionContext(0.03));

        AccuracyScorerCalculator.OverallStatisticsConfiguration overallStatsConfig = new AccuracyScorerCalculator.OverallStatisticsConfiguration();
        boolean isOverallAccuracyCalculated = getBooleanFromConfig(config, "displayOverallAccuracy");
        boolean isOverallErrorCalculated = getBooleanFromConfig(config, "displayOverallError");
        boolean isCohensKappaCalculated = getBooleanFromConfig(config, "displayCohensKappa");
        boolean isCorrectClassifiedCalculated = getBooleanFromConfig(config, "displayCorrectClassified");
        boolean isWrongClassifiedCalculated = getBooleanFromConfig(config, "displayWrongClassified");
        overallStatsConfig.withOverallAccuracyCalculated(isOverallAccuracyCalculated)
        .withOverallErrorCalculated(isOverallErrorCalculated)
        .withCohensKappaCalculated(isCohensKappaCalculated)
        .withCorrectClassifiedCalculated(isCorrectClassifiedCalculated)
        .withWrongClassifiedCalculated(isWrongClassifiedCalculated);
        JSONDataTable overallStatsTable = createJSONTableFromBufferedDataTable(scorerCalc.getOverallStatisticsTable(overallStatsConfig, exec), exec.createSubExecutionContext(0.03));
//        List<String> warnings = scorerCalc.getWarnings();
//        StringBuffer buffer = new StringBuffer();
//        for (String warning : warnings) {
//            buffer.append(warning + "\n");
//        }
//        setWarningMessage(buffer.toString());
        ScorerResult result = new ScorerResult(confusionMatrixTable, classStatsTable, overallStatsTable);
        exec.setProgress(1);
        return new Object[] {result};
	}

    /**
     * @param config
     * @return
     */
    private boolean getBooleanFromConfig(final DynamicJSConfig config, final String option) {
        return ((SettingsModelBoolean)config.getModel(option)).getBooleanValue();
    }

    /**
     * Converts a buffered data table into {@link JSONDataTable} format
     * @param table
     * @param exec
     * @return corresponding {@link JSONDataTable} object
     * @throws CanceledExecutionException
     */
    private JSONDataTable createJSONTableFromBufferedDataTable(final BufferedDataTable table, final ExecutionContext exec) throws CanceledExecutionException {
        JSONDataTable jsonTable = getJsonDataTableBuilder(table).build(exec);
        return jsonTable;
    }

    /**
     * Gets a builder for the concrete view
     * @param table
     * @return corresponding builder object
     */
    private JSONDataTable.Builder getJsonDataTableBuilder(final BufferedDataTable table) {
        return JSONDataTable.newBuilder()
                .setDataTable(table);
    }

	/**
	 * Class for wrapping all the results of the ScorerCalculator into one unique structure
	 * @author Pascal Lee
	 */
	//Class for wrapping all the results of the ScorerCalculator into one unique structure
	public class ScorerResult {
		private JSONDataTable confusionMatrix;
		private JSONDataTable classStatistics;
		private JSONDataTable overallStatistics;

		/**
		 * @return the confusion matrix
		 */
		public JSONDataTable getConfusionMatrix() {
			return confusionMatrix;
		}

		/**
         * @return the classStatistics data table
         */
        public JSONDataTable getClassStatistics() {
            return classStatistics;
        }

        /**
         * @return the overallStatistics data table
         */
        public JSONDataTable getOverallStatistics() {
            return overallStatistics;
        }

        public ScorerResult() {
		}

		/**
		 * @param confusionMatrix
		 * @param classStatistics
		 */
		public ScorerResult(final JSONDataTable confusionMatrix, final JSONDataTable classStatistics, final JSONDataTable overallStatistics) {
			super();
			this.confusionMatrix = confusionMatrix;
			this.classStatistics = classStatistics;
			this.overallStatistics = overallStatistics;
		}
	}
}
