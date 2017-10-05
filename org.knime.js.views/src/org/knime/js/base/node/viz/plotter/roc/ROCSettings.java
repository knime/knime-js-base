/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.com; Email: contact@knime.com
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ---------------------------------------------------------------------
 *
 * History
 *   11.02.2008 (thor): created
 */
package org.knime.js.base.node.viz.plotter.roc;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.util.filter.NameFilterConfiguration.EnforceOption;
import org.knime.core.node.util.filter.column.DataColumnSpecFilterConfiguration;
import org.knime.core.node.util.filter.column.DataTypeColumnFilter;

/**
 * This class holds the settings for the ROC curve view.
 *
 * @author Thorsten Meinl, University of Konstanz
 */
final class ROCSettings {

    /**
     *
     */
    private static final String CURVES2_CFG = "curves2";

    /**
     * The config key for the settings storing the numeric columns.
     * @since 3.1
     */
    public static final String NUM_COLUMNS = "numColumns";

    private final DataColumnSpecFilterConfiguration m_numericCols = new DataColumnSpecFilterConfiguration(NUM_COLUMNS,
        new DataTypeColumnFilter(DoubleValue.class));

    private String m_classColumn;

    private DataCell m_positiveClass;

    private int m_maxPoints = 2000;

    /**
     * @return the numericCols
     * @since 3.1
     */
    public DataColumnSpecFilterConfiguration getNumericCols() {
        return m_numericCols;
    }

    /**
     * Returns the name of the class column.
     *
     * @return the class column's name
     */
    public String getClassColumn() {
        return m_classColumn;
    }

    /**
     * Sets the value from the class column that represents the "positive"
     * class.
     *
     * @param value any value
     */
    public void setPositiveClass(final DataCell value) {
        m_positiveClass = value;
    }

    /**
     * Returns the value from the class column that represents the "positive"
     * class.
     *
     * @return any value
     */
    public DataCell getPositiveClass() {
        return m_positiveClass;
    }

    /**
     * Sets the name of the class column.
     *
     * @param colName the class column's name
     */
    public void setClassColumn(final String colName) {
        m_classColumn = colName;
    }


    /**
     * Sets the maximum number of points for each curve that are shown in the view.
     *
     * @param maxPoints the maximum number of points or -1 if the number should not be limited
     * @since 2.10
     */
    public void setMaxPoints(final int maxPoints) {
        m_maxPoints = maxPoints;
    }


    /**
     * Returns the maximum number of points for each curve that are shown in the view.
     *
     * @return the maximum number of points or -1 if the number should not be limited
     * @since 2.10
     */
    public int getMaxPoints() {
        return m_maxPoints;
    }

    /**
     * Saves this object's settings to the given node settings.
     *
     * @param settings the node settings
     */
    public void saveSettings(final NodeSettingsWO settings) {
        settings.addString("classColumn", m_classColumn);
        settings.addDataCell("positiveClass", m_positiveClass);

        settings.addInt("maxPoints", m_maxPoints);
        m_numericCols.saveConfiguration(settings);
    }

    /**
     * Loads the settings from the given node settings object.
     *
     * @param settings the node settings
     * @throws InvalidSettingsException if the settings are invalid
     */
    public void loadSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_classColumn = settings.getString("classColumn");
        m_positiveClass = settings.getDataCell("positiveClass");

        if (settings.containsKey(NUM_COLUMNS)) {
            m_numericCols.loadConfigurationInModel(settings);
        } else {
            String[] curves = settings.getStringArray("curves", new String[0]);
            m_numericCols.loadDefaults(curves, null, EnforceOption.EnforceInclusion);
        }
        m_maxPoints = settings.getInt("maxPoints", -1); // since 2.10
    }

    /**
     * Loads the settings from the given node settings object.
     *
     * @param settings the node settings
     * @param spec the table spec
     */
    public void loadSettingsForDialog(final NodeSettingsRO settings, final DataTableSpec spec) {
        m_classColumn = settings.getString("classColumn", null);
        m_positiveClass = settings.getDataCell("positiveClass", null);

        // Fix for AP-5696: Inclusion, exclusion are not saved in settings
        if (settings.containsKey(NUM_COLUMNS)) {
            m_numericCols.loadConfigurationInDialog(settings, spec);
        } else {
            String[] curves = settings.getStringArray("curves", new String[0]);
            m_numericCols.loadDefaults(curves, null, EnforceOption.EnforceInclusion);
        }

        m_maxPoints = settings.getInt("maxPoints", 2000); // since 2.10
    }
}
