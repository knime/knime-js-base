package org.knime.dynamic.js.base.boxplot;
import org.knime.base.node.viz.plotter.box.BoxplotCalculator;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnFilter2;
import org.knime.core.node.port.PortObject;
import org.knime.dynamic.js.v30.DynamicJSConfig;
import org.knime.dynamic.js.v30.DynamicJSProcessor;

public class BoxplotProcessor_v2 implements DynamicJSProcessor {
             
    @Override
    public Object[] processInputObjects(PortObject[] inObjects,
            ExecutionContext exec, DynamicJSConfig config) throws Exception {
        BufferedDataTable dt = (BufferedDataTable)inObjects[0];
        BoxplotCalculator bc = new BoxplotCalculator();

        String[] numColumns = ((SettingsModelColumnFilter2)config.getModel("columns")).applyTo(dt.getDataTableSpec()).getIncludes();
        if (numColumns.length == 0) {
            throw new InvalidSettingsException("No numeric columns given");
        }

        BoxPlotResult res = new BoxPlotResult(bc.calculateMultiple(dt, numColumns, exec), bc.getExcludedDataCols(),
            bc.getNumMissValPerCol());
        return new Object[]{res};

    }

    private class BoxPlotResult {
        public Object stats;

        public Object excludedDataCols;

        public Object numMissValPerCol;

        /**
         * @param stats
         * @param excludedDataCols
         * @param numMissValPerCol
         */
        public BoxPlotResult(Object stats, Object excludedDataCols, Object numMissValPerCol) {
            super();
            this.stats = stats;
            this.excludedDataCols = excludedDataCols;
            this.numMissValPerCol = numMissValPerCol;
        }

    }

}
