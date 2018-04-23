package org.knime.dynamic.js.base.scorer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.knime.base.node.mine.scorer.accuracy.AccuracyScorerCalculator;
import org.knime.base.node.mine.scorer.accuracy.AccuracyScorerCalculator.ValueStats;
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
	public Object[] processInputObjects(PortObject[] inObjects, ExecutionContext exec, DynamicJSConfig config)
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
        AccuracyScorerCalculator scorerCalc = AccuracyScorerCalculator.createCalculator(table, firstColumnName, secondColumnName, calculatorConfig, exec);
        
        JSONDataTable confusionMatrixJSTable = createJSONTableFromBufferedDataTable(scorerCalc.getConfusionMatrixTable(exec), exec);
//        List<String> warnings = scorerCalc.getWarnings();
//        StringBuffer buffer = new StringBuffer();
//        for (String warning : warnings) {
//            buffer.append(warning + "\n");
//        }
//        setWarningMessage(buffer.toString());
        
//        List<ValueStats> valueStatsList = new ArrayList<ValueStats>();
//        Iterator<ValueStats> valueStatsIterator = scorerCalc.getIterator();
//        while(valueStatsIterator.hasNext()) {
//        	valueStatsList.add(valueStatsIterator.next());
//        }
        
        //TODO: make this into a proper JSONDataTable, see DataExplorer for example
//        return new Object[]{table, confusionMatrix, valueStatsList};
        ScorerResult result = new ScorerResult(confusionMatrixJSTable);
        
        return new Object[] {result};
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
    protected JSONDataTable.Builder getJsonDataTableBuilder(final BufferedDataTable table) {
        return JSONDataTable.newBuilder()
                .setDataTable(table);
    }	

	//Class for wrapping all the results of the ScorerCalculator into one unique structure
	public class ScorerResult {
		private JSONDataTable confusionMatrix;

		public JSONDataTable getConfusionMatrix() {
			return confusionMatrix;
		}
		
		public ScorerResult() {
		}

		/**
		 * @param confusionMatrix
		 * @param valueStatsList
		 * @param m_accuracy
		 * @param m_cohensKappa
		 */
		public ScorerResult(JSONDataTable confusionMatrix) {
			super();
			this.confusionMatrix = confusionMatrix;
		}
	}	
}
