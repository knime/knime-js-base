package org.knime.dynamic.js.base.scorer;

import org.knime.base.node.mine.scorer.accuracy.AccuracyScorerCalculator;
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
        
//        String[] columnNames = ((SettingsModelColumnFilter2)config.getModel("columns")).applyTo(table.getDataTableSpec()).getIncludes();
//        if (columnNames.length != 2) {
//            throw new InvalidSettingsException("Please choose two distincts columns.");
//        }
        
        SettingsModelString firstColumnModel  = (SettingsModelString) config.getModel("first_column");
        SettingsModelString secondColumnModel  = (SettingsModelString) config.getModel("second_column");
        
        String firstColumnName = firstColumnModel.getStringValue();
        String secondColumnName = secondColumnModel.getStringValue();
        
        
        AccuracyScorerCalculator scorerCalc = new AccuracyScorerCalculator(table, firstColumnName, secondColumnName, exec);
        int[][] confusionMatrix = scorerCalc.getConfusionMatrix();
        
        //TODO: make this into a proper JSONDataTable, see DataExplorer for example
        

        return new Object[]{table, confusionMatrix};
	}

}
