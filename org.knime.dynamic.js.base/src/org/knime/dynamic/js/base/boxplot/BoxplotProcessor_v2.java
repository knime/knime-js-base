package org.knime.dynamic.js.base.boxplot;

import java.util.LinkedHashMap;

import org.knime.base.node.viz.plotter.box.BoxplotCalculator;
import org.knime.base.node.viz.plotter.box.BoxplotStatistics;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnFilter2;
import org.knime.core.node.port.PortObject;
import org.knime.dynamic.js.v30.DynamicJSConfig;
import org.knime.dynamic.js.v30.DynamicJSProcessor;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

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

    @JsonAutoDetect
    public static class BoxPlotResult {
        public LinkedHashMap<String, BoxplotStatistics> stats;

        public String[] excludedDataCols;

        public LinkedHashMap<String, Long> numMissValPerCol;

        /**
         * @return the stats
         */
        public LinkedHashMap<String, BoxplotStatistics> getStats() {
            return stats;
        }

        /**
         * @param stats the stats to set
         */
        public void setStats(LinkedHashMap<String, BoxplotStatistics> stats) {
            this.stats = stats;
        }

        /**
         * @return the excludedDataCols
         */
        public String[] getExcludedDataCols() {
            return excludedDataCols;
        }

        /**
         * @param excludedDataCols the excludedDataCols to set
         */
        public void setExcludedDataCols(String[] excludedDataCols) {
            this.excludedDataCols = excludedDataCols;
        }

        /**
         * @return the numMissValPerCol
         */
        public LinkedHashMap<String, Long> getNumMissValPerCol() {
            return numMissValPerCol;
        }

        /**
         * @param numMissValPerCol the numMissValPerCol to set
         */
        public void setNumMissValPerCol(LinkedHashMap<String, Long> numMissValPerCol) {
            this.numMissValPerCol = numMissValPerCol;
        }

        /**
         * @param stats
         * @param excludedDataCols
         * @param numMissValPerCol
         */
        public BoxPlotResult(LinkedHashMap<String, BoxplotStatistics> stats, String[] excludedDataCols,
            LinkedHashMap<String, Long> numMissValPerCol) {
            super();
            this.stats = stats;
            this.excludedDataCols = excludedDataCols;
            this.numMissValPerCol = numMissValPerCol;
        }

        public BoxPlotResult() {
        }
    }

}
