/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
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
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
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
 *   29 May 2019 (albrecht): created
 */
package org.knime.js.base.node.base.filter.value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.base.dialog.selection.multiple.MultipleSelectionsComponentFactory;
import org.knime.js.base.node.configuration.value.ValueSelectionFilterUtil;

/**
 * Base config file for the value filter configuration and widget nodes
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class ValueFilterNodeConfig {

    public static final String CFG_LOCK_COLUMN = "lockColumn";
    private static final boolean DEFAULT_LOCK_COLUMN = false;
    private boolean m_lockColumn = DEFAULT_LOCK_COLUMN;

    public static final String CFG_POSSIBLE_COLUMNS = "possibleColumns";

    /**
     * Maps the names of the columns with <b>discrete</b> domains to the string representations of the values in their
     * domains. <br/>
     *
     * For instance, if the column "A" has the domain {"a", "b", "c"} and the column "B" has the domain {true, false},
     * then this map will contain the entries "A" -> {"a", "b", "c"} and "B" -> {"true", "false"}. <br/>
     *
     * Updated in {@link ValueFilterNodeConfig#setFromSpec(DataTableSpec)}.
     */
    private Map<String, List<String>> m_possibleValues = new TreeMap<>();

    public static final String CFG_TYPE = "type";
    public static final String DEFAULT_TYPE = MultipleSelectionsComponentFactory.TWINLIST;
    private String m_type = DEFAULT_TYPE;

    public static final String CFG_COL = "colValues";

    public static final String CFG_LIMIT_NUMBER_VIS_OPTIONS = "limit_number_visible_options";
    public static final boolean DEFAULT_LIMIT_NUMBER_VIS_OPTIONS = false;
    private boolean m_limitNumberVisOptions = DEFAULT_LIMIT_NUMBER_VIS_OPTIONS;

    public static final String CFG_NUMBER_VIS_OPTIONS = "number_visible_options";
    public static final Integer DEFAULT_NUMBER_VIS_OPTIONS = 5;
    private Integer m_numberVisOptions = DEFAULT_NUMBER_VIS_OPTIONS;

    /**
     * @return the lockColumn
     */
    public boolean isLockColumn() {
        return m_lockColumn;
    }

    /**
     * @param lockColumn the lockColumn to set
     */
    public void setLockColumn(final boolean lockColumn) {
        m_lockColumn = lockColumn;
    }

    /**
     * @return the possibleValues
     */
    public Map<String, List<String>> getPossibleValues() {
        return m_possibleValues;
    }

    /**
     * @param possibleValues the possibleValues to set
     */
    public void setPossibleValues(final Map<String, List<String>> possibleValues) {
        m_possibleValues = possibleValues;
    }

    /**
     * @return the type
     */
    public String getType() {
        return m_type;
    }

    /**
     * @param type the type to set
     */
    public void setType(final String type) {
        m_type = type;
    }

    /**
     * @return the limitNumberVisOptions
     */
    public boolean isLimitNumberVisOptions() {
        return m_limitNumberVisOptions;
    }

    /**
     * @param limitNumberVisOptions the limitNumberVisOptions to set
     */
    public void setLimitNumberVisOptions(final boolean limitNumberVisOptions) {
        m_limitNumberVisOptions = limitNumberVisOptions;
    }

    /**
     * @return the numberVisOptions
     */
    public Integer getNumberVisOptions() {
        return m_numberVisOptions;
    }

    /**
     * @param numberVisOptions the numberVisOptions to set
     */
    public void setNumberVisOptions(final Integer numberVisOptions) {
        m_numberVisOptions = numberVisOptions;
    }

    /**
     * Saves the current settings
     *
     * @param settings the settings to save to
     */
    public void saveSettings(final NodeSettingsWO settings) {
        settings.addBoolean(CFG_LOCK_COLUMN, m_lockColumn);
        settings.addStringArray(CFG_POSSIBLE_COLUMNS,
                m_possibleValues.keySet().toArray(new String[m_possibleValues.keySet().size()]));
        NodeSettingsWO colSettings = settings.addNodeSettings(CFG_COL);
        for (String key : m_possibleValues.keySet()) {
            List<String> values = m_possibleValues.get(key);
            colSettings.addStringArray(key, values.toArray(new String[values.size()]));
        }
        settings.addString(CFG_TYPE, m_type);
        settings.addBoolean(CFG_LIMIT_NUMBER_VIS_OPTIONS, m_limitNumberVisOptions);
        settings.addInt(CFG_NUMBER_VIS_OPTIONS, m_numberVisOptions);
    }

    /**
     * Loads the config from saved settings
     *
     * @param settings the settings to load from
     * @throws InvalidSettingsException
     */
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_lockColumn = settings.getBoolean(CFG_LOCK_COLUMN);
        m_possibleValues = new TreeMap<String, List<String>>();
        String[] columns = settings.getStringArray(CFG_POSSIBLE_COLUMNS);
        NodeSettingsRO colSettings = settings.getNodeSettings(CFG_COL);
        for (String column : columns) {
            m_possibleValues.put(column, Arrays.asList(colSettings.getStringArray(column)));
        }
        m_type = settings.getString(CFG_TYPE);
        m_limitNumberVisOptions = settings.getBoolean(CFG_LIMIT_NUMBER_VIS_OPTIONS);
        m_numberVisOptions = settings.getInt(CFG_NUMBER_VIS_OPTIONS);
    }

    /**
     * Loads the config from saved settings for dialog display
     *
     * @param settings the settings to load from
     */
    public void loadSettingsInDialog(final NodeSettingsRO settings) {
        m_lockColumn = settings.getBoolean(CFG_LOCK_COLUMN, DEFAULT_LOCK_COLUMN);
        m_possibleValues =
            ValueSelectionFilterUtil.loadPossibleColumnsAndValuesInDialog(settings, CFG_POSSIBLE_COLUMNS, CFG_COL);
        m_type = settings.getString(CFG_TYPE, DEFAULT_TYPE);
        m_limitNumberVisOptions = settings.getBoolean(CFG_LIMIT_NUMBER_VIS_OPTIONS, DEFAULT_LIMIT_NUMBER_VIS_OPTIONS);
        m_numberVisOptions = settings.getInt(CFG_NUMBER_VIS_OPTIONS, DEFAULT_NUMBER_VIS_OPTIONS);
    }

    /**
     * Memorizes all the domains of the columns in the input table that have non-null domains.
     *
     * @param spec the spec to set
     */
    public void setFromSpec(final DataTableSpec spec) {
        m_possibleValues = getPossibleValues(spec);
    }

    /**
     * Determines the possible values with the current settings from a given table spec
     *
     * @param dataTableSpec the spec to determine the possible values from
     * @return a map of columns and their corresponding domain values
     */
    public static Map<String, List<String>> getPossibleValues(final DataTableSpec dataTableSpec) {
     // Only add column specs for columns that have non-null domains
        List<DataColumnSpec> specs = new ArrayList<DataColumnSpec>();
        for (DataColumnSpec cspec : dataTableSpec) {
            if (cspec.getDomain().hasValues()) {
                specs.add(cspec);
            }
        }
        DataTableSpec filteredSpec = new DataTableSpec(specs.toArray(new DataColumnSpec[specs.size()]));
        Map<String, List<String>> values = new TreeMap<String, List<String>>();
        for (DataColumnSpec colSpec : filteredSpec) {
            final Set<DataCell> vals = colSpec.getDomain().getValues();
            if (vals != null) {
                List<String> v = new ArrayList<String>();
                for (final DataCell cell : vals) {
                    v.add(cell.toString());
                }
                values.put(colSpec.getName(), v);
            }
        }
        return values;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("lockColumn=");
        sb.append(m_lockColumn);
        sb.append(", ");
        sb.append("possibleValues=");
        sb.append("{");
        sb.append(m_possibleValues);
        sb.append("}");
        sb.append(", ");
        sb.append("type=");
        sb.append(m_type);
        sb.append(", ");
        sb.append("m_limitNumberVisOptions=");
        sb.append(m_limitNumberVisOptions);
        sb.append(", ");
        sb.append("m_numberVisOptions=");
        sb.append(m_numberVisOptions);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_lockColumn)
                .append(m_possibleValues)
                .append(m_type)
                .append(m_limitNumberVisOptions)
                .append(m_numberVisOptions)
                .toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        ValueFilterNodeConfig other = (ValueFilterNodeConfig)obj;
        return new EqualsBuilder()
                .append(m_lockColumn, other.m_lockColumn)
                .append(m_possibleValues, other.m_possibleValues)
                .append(m_type, other.m_type)
                .append(m_limitNumberVisOptions, other.m_limitNumberVisOptions)
                .append(m_numberVisOptions, other.m_numberVisOptions)
                .isEquals();
    }


}
