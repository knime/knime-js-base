package org.knime.dynamic.js.base.boxplot;

import java.util.LinkedHashMap;

import org.knime.base.node.viz.plotter.box.BoxplotCalculator;
import org.knime.base.node.viz.plotter.box.BoxplotStatistics;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnFilter2;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.dynamic.js.v30.DynamicJSConfig;
import org.knime.dynamic.js.v30.DynamicJSProcessor;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * @author Alexander Fillbrunn, University of Konstanz, Germany
 *
 */
public class ConditionalBoxplotProcessor_v2 implements DynamicJSProcessor {

    @Override
    public Object[] processInputObjects(PortObject[] inObjects, ExecutionContext exec, DynamicJSConfig config)
        throws Exception {
        BufferedDataTable dt = (BufferedDataTable)inObjects[0];
        BoxplotCalculator bc = new BoxplotCalculator();

        String catCol = ((SettingsModelString)config.getModel("catCol")).getStringValue();
        if (catCol == null) {
            throw new InvalidSettingsException("No category column given");
        }
        if (dt.getSpec().getColumnSpec(catCol) == null) {
            throw new InvalidSettingsException("Configured category column '" + catCol + "' is not available anymore.");
        }
        String[] numColumns =
            ((SettingsModelColumnFilter2)config.getModel("columns")).applyTo(dt.getDataTableSpec()).getIncludes();
        if (numColumns.length == 0) {
            throw new InvalidSettingsException("No numeric columns given");
        }
        boolean failOnSpecialDoubles =
            ((SettingsModelBoolean)config.getModel("failOnSpecialDoubles")).getBooleanValue();

        LinkedHashMap<String, LinkedHashMap<String, BoxplotStatistics>> stats =
            bc.calculateMultipleConditional(dt, catCol, numColumns, failOnSpecialDoubles, exec);
        CondBoxPlotResult res = new CondBoxPlotResult(stats, catCol, bc.getExcludedClasses(), bc.getIgnoredMissVals());
        return new Object[]{res, inObjects[1]};
    }

    @SuppressWarnings("javadoc")
    @JsonAutoDetect
    public static class CondBoxPlotResult {
        private LinkedHashMap<String, LinkedHashMap<String, BoxplotStatistics>> m_stats;

        private String m_catCol;

        private LinkedHashMap<String, String[]> m_excludedClasses;

        private LinkedHashMap<String, LinkedHashMap<String, Long>> m_ignoredMissVals;

        /**
         * @return the stats
         */
        public LinkedHashMap<String, LinkedHashMap<String, BoxplotStatistics>> getStats() {
            return m_stats;
        }

        /**
         * @param stats the stats to set
         */
        public void setStats(LinkedHashMap<String, LinkedHashMap<String, BoxplotStatistics>> stats) {
            m_stats = stats;
        }

        /**
         * @return the catCol
         */
        public String getCatCol() {
            return m_catCol;
        }

        /**
         * @param catCol the catCol to set
         */
        public void setCatCol(String catCol) {
            m_catCol = catCol;
        }

        /**
         * @return the excludedClasses
         */
        public LinkedHashMap<String, String[]> getExcludedClasses() {
            return m_excludedClasses;
        }

        /**
         * @param excludedClasses the excludedClasses to set
         */
        public void setExcludedClasses(LinkedHashMap<String, String[]> excludedClasses) {
            m_excludedClasses = excludedClasses;
        }

        /**
         * @return the ignoredMissVals
         */
        public LinkedHashMap<String, LinkedHashMap<String, Long>> getIgnoredMissVals() {
            return m_ignoredMissVals;
        }

        /**
         * @param ignoredMissVals the ignoredMissVals to set
         */
        public void setIgnoredMissVals(LinkedHashMap<String, LinkedHashMap<String, Long>> ignoredMissVals) {
            m_ignoredMissVals = ignoredMissVals;
        }

        /**
         * @param stats
         * @param catCol
         * @param excludedClasses
         * @param ignoredMissVals
         */
        public CondBoxPlotResult(LinkedHashMap<String, LinkedHashMap<String, BoxplotStatistics>> stats, String catCol,
            LinkedHashMap<String, String[]> excludedClasses,
            LinkedHashMap<String, LinkedHashMap<String, Long>> ignoredMissVals) {
            m_stats = stats;
            m_catCol = catCol;
            m_excludedClasses = excludedClasses;
            m_ignoredMissVals = ignoredMissVals;
        }

        public CondBoxPlotResult() {

        }
    }
}
