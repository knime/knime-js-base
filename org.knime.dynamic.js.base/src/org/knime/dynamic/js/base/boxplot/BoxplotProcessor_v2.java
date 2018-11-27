package org.knime.dynamic.js.base.boxplot;

import java.util.LinkedHashMap;

import org.knime.base.node.viz.plotter.box.BoxplotCalculator;
import org.knime.base.node.viz.plotter.box.BoxplotStatistics;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnFilter2;
import org.knime.core.node.port.PortObject;
import org.knime.dynamic.js.v30.DynamicJSConfig;
import org.knime.dynamic.js.v30.DynamicJSProcessor;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * @author Alexander Fillbrunn, University of Konstanz, Germany
 *
 */
public class BoxplotProcessor_v2 implements DynamicJSProcessor {

    @Override
    public Object[] processInputObjects(PortObject[] inObjects, ExecutionContext exec, DynamicJSConfig config)
        throws Exception {
        BufferedDataTable dt = (BufferedDataTable)inObjects[0];
        BoxplotCalculator bc = new BoxplotCalculator();

        String[] numColumns =
            ((SettingsModelColumnFilter2)config.getModel("columns")).applyTo(dt.getDataTableSpec()).getIncludes();
        if (numColumns.length == 0) {
            throw new InvalidSettingsException("No numeric columns given");
        }
        boolean failOnSpecialDoubles =
            ((SettingsModelBoolean)config.getModel("failOnSpecialDoubles")).getBooleanValue();

        BoxPlotResult res = new BoxPlotResult(bc.calculateMultiple(dt, numColumns, failOnSpecialDoubles, exec),
            bc.getExcludedDataCols(), bc.getNumMissValPerCol());
        return new Object[]{res};

    }

    @SuppressWarnings("javadoc")
    @JsonAutoDetect
    public static class BoxPlotResult {
        public LinkedHashMap<String, BoxplotStatistics> m_stats;

        public String[] m_excludedDataCols;

        public LinkedHashMap<String, Long> m_numMissValPerCol;

        /**
         * @return the stats
         */
        public LinkedHashMap<String, BoxplotStatistics> getStats() {
            return m_stats;
        }

        /**
         * @param stats the stats to set
         */
        public void setStats(LinkedHashMap<String, BoxplotStatistics> stats) {
            this.m_stats = stats;
        }

        /**
         * @return the excludedDataCols
         */
        public String[] getExcludedDataCols() {
            return m_excludedDataCols;
        }

        /**
         * @param excludedDataCols the excludedDataCols to set
         */
        public void setExcludedDataCols(String[] excludedDataCols) {
            m_excludedDataCols = excludedDataCols;
        }

        /**
         * @return the numMissValPerCol
         */
        public LinkedHashMap<String, Long> getNumMissValPerCol() {
            return m_numMissValPerCol;
        }

        /**
         * @param numMissValPerCol the numMissValPerCol to set
         */
        public void setNumMissValPerCol(LinkedHashMap<String, Long> numMissValPerCol) {
            m_numMissValPerCol = numMissValPerCol;
        }

        /**
         * @param stats
         * @param excludedDataCols
         * @param numMissValPerCol
         */
        public BoxPlotResult(LinkedHashMap<String, BoxplotStatistics> stats, String[] excludedDataCols,
            LinkedHashMap<String, Long> numMissValPerCol) {
            super();
            m_stats = stats;
            m_excludedDataCols = excludedDataCols;
            m_numMissValPerCol = numMissValPerCol;
        }

        public BoxPlotResult() {
        }
    }

}
