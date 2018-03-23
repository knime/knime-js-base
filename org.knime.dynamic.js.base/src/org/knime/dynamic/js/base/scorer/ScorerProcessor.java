package org.knime.dynamic.js.base.scorer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.knime.base.node.mine.scorer.accuracy.AccuracyScorerCalculator;
import org.knime.base.node.mine.scorer.accuracy.AccuracyScorerCalculator.ValueStats;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.dynamic.js.v30.DynamicJSConfig;
import org.knime.dynamic.js.v30.DynamicStatefulJSProcessor;

public class ScorerProcessor extends DynamicStatefulJSProcessor {

	@Override
	public Object[] processInputObjects(PortObject[] inObjects, ExecutionContext exec, DynamicJSConfig config)
			throws Exception {
        BufferedDataTable table = (BufferedDataTable)inObjects[0];
        
        SettingsModelString firstColumnModel  = (SettingsModelString) config.getModel("first_column");
        SettingsModelString secondColumnModel  = (SettingsModelString) config.getModel("second_column");
        String firstColumnName = firstColumnModel.getStringValue();
        String secondColumnName = secondColumnModel.getStringValue();
                
        AccuracyScorerCalculator scorerCalc = new AccuracyScorerCalculator(table, firstColumnName, secondColumnName, exec);
        String[] classes = scorerCalc.getTargetValues();
        List<String>[][] keyStore = scorerCalc.getKeyStore();
        int[][] confusionMatrix = scorerCalc.getConfusionMatrix();
        List<ValueStats> valueStatsList = new ArrayList<ValueStats>();
        Iterator<ValueStats> valueStatsIterator = scorerCalc.getIterator();
        while(valueStatsIterator.hasNext()) {
        	valueStatsList.add(valueStatsIterator.next());
        }
        double accuracy = scorerCalc.getAccuracy();
        double cohensKappa = scorerCalc.getCohensKappa();
        
        //TODO: make this into a proper JSONDataTable, see DataExplorer for example
//        return new Object[]{table, confusionMatrix, valueStatsList};
        ScorerResult result = new ScorerResult(classes, confusionMatrix, keyStore, valueStatsList, accuracy, cohensKappa);
        
        return new Object[] {table, result};
	}

	//Class for wrapping all the results of the ScorerCalculator into one unique structure
	public class ScorerResult {
		private String[] classes;
		private int[][] confusionMatrix;
		private List<String>[][] keyStore;
		private List<ValueStats> valueStatsList;
	    private double accuracy;
	    private double cohensKappa;
	    
		public String[] getClasses() {
			return classes;
		}

		public void setClasses(String[] classes) {
			this.classes = classes;
		}

		public int[][] getConfusionMatrix() {
			return confusionMatrix;
		}
		
		public void setConfusionMatrix(int[][] confusionMatrix) {
			this.confusionMatrix = confusionMatrix;
		}
		
		public List<String>[][] getKeyStore() {
			return keyStore;
		}

		public void setKeyStore(List<String>[][] keyStore) {
			this.keyStore = keyStore;
		}

		public List<ValueStats> getValueStatsList() {
			return valueStatsList;
		}
		
		public void setValueStatsList(List<ValueStats> valueStatsList) {
			this.valueStatsList = valueStatsList;
		}

		public double getAccuracy() {
			return accuracy;
		}

		public void setAccuracy(double accuracy) {
			this.accuracy = accuracy;
		}

		public double getCohensKappa() {
			return cohensKappa;
		}

		public void setCohensKappa(double cohensKappa) {
			this.cohensKappa = cohensKappa;
		}
	    
		public ScorerResult() {
		}

		/**
		 * @param confusionMatrix
		 * @param valueStatsList
		 * @param m_accuracy
		 * @param m_cohensKappa
		 */
		public ScorerResult(String[] classes, int[][] confusionMatrix, List<String>[][] keyStore, List<ValueStats> valueStatsList, double accuracy,
				double cohensKappa) {
			super();
			this.classes = classes;
			this.confusionMatrix = confusionMatrix;
			this.keyStore = keyStore;
			this.valueStatsList = valueStatsList;
			this.accuracy = accuracy;
			this.cohensKappa = cohensKappa;
		}
	}	
}
