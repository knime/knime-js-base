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
package org.knime.js.base.node.base.selection.value;

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
import org.knime.core.data.DoubleValue;
import org.knime.core.data.IntValue;
import org.knime.core.data.StringValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.base.dialog.selection.single.SingleSelectionComponentFactory;
import org.knime.js.base.node.configuration.value.ValueSelectionFilterUtil;

/**
 * Base config file for the value selection configuration and widget nodes
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class ValueSelectionNodeConfig {

    public static final String CFG_COLUMN_TYPE = "columnType";

    public static final ColumnType DEFAULT_COLUMN_TYPE = ColumnType.All;

    private ColumnType m_columnType = DEFAULT_COLUMN_TYPE;

    public static final String CFG_LOCK_COLUMN = "lockColumn";

    private static final boolean DEFAULT_LOCK_COLUMN = false;

    private boolean m_lockColumn = DEFAULT_LOCK_COLUMN;

    public static final String CFG_POSSIBLE_COLUMNS = "possibleColumns";

    private Map<String, List<String>> m_possibleValues = new TreeMap<String, List<String>>();

    public static final String CFG_TYPE = "type";

    public static final String DEFAULT_TYPE = SingleSelectionComponentFactory.DROPDOWN;

    private String m_type = DEFAULT_TYPE;

    public static final String CFG_COL = "colValues";

    public static final String CFG_LIMIT_NUMBER_VIS_OPTIONS = "limit_number_visible_options";

    public static final boolean DEFAULT_LIMIT_NUMBER_VIS_OPTIONS = false;

    private boolean m_limitNumberVisOptions = DEFAULT_LIMIT_NUMBER_VIS_OPTIONS;

    public static final String CFG_NUMBER_VIS_OPTIONS = "number_visible_options";

    public static final Integer DEFAULT_NUMBER_VIS_OPTIONS = 5;

    private Integer m_numberVisOptions = DEFAULT_NUMBER_VIS_OPTIONS;

    /**
     * @return the columnType
     */
    public ColumnType getColumnType() {
        return m_columnType;
    }

    /**
     * @param columnType The columnType to set
     */
    public void setColumnType(final ColumnType columnType) {
        m_columnType = columnType;
    }

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
     * Sets the possible values with the current settings from a given table spec
     *
     * @param spec the spec to set
     */
    public void setFromSpec(final DataTableSpec spec) {
        m_possibleValues = getPossibleValues(spec, m_columnType);
    }

    /**
     * Determines the possible values with the current settings from a given table spec
     *
     * @param dataTableSpec the spec to determine the possible values from
     * @param columnType the allowed column types
     * @return a map of columns and their corresponding domain values
     */
    public static Map<String, List<String>> getPossibleValues(final DataTableSpec dataTableSpec,
        final ColumnType columnType) {
        // Only add column specs for columns that have values and are of the selected type
        List<DataColumnSpec> specs = new ArrayList<DataColumnSpec>();
        for (DataColumnSpec cspec : dataTableSpec) {
            if (cspec.getDomain().hasValues()) {
                switch (columnType) {
                    case String:
                        if (cspec.getType().isCompatible(StringValue.class)) {
                            specs.add(cspec);
                        }
                        break;
                    case Integer:
                        if (cspec.getType().isCompatible(IntValue.class)) {
                            specs.add(cspec);
                        }
                        break;
                    case Double:
                        if (cspec.getType().isCompatible(DoubleValue.class)) {
                            specs.add(cspec);
                        }
                        break;
                    case All:
                        specs.add(cspec);
                        break;
                }
            }
        }
        DataTableSpec filteredSpec = new DataTableSpec(specs.toArray(new DataColumnSpec[specs.size()]));
        Map<String, List<String>> values = new TreeMap<String, List<String>>();
        for (DataColumnSpec colSpec : filteredSpec) {
            final Set<DataCell> vals = colSpec.getDomain().getValues();
            if (vals != null) {
                if (colSpec.getType().isCompatible(StringValue.class) && vals.size() < 1) {
                    //skip if no possible values for string column
                    continue;
                }
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
     * Saves the current settings
     *
     * @param settings the settings to save to
     */
    public void saveSettings(final NodeSettingsWO settings) {
        settings.addString(CFG_COLUMN_TYPE, m_columnType.name());
        settings.addBoolean(CFG_LOCK_COLUMN, m_lockColumn);
        ValueSelectionFilterUtil.savePossibleColumnsAndValues(settings, m_possibleValues, CFG_POSSIBLE_COLUMNS,
            CFG_COL);
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
        m_columnType = ColumnType.valueOf(settings.getString(CFG_COLUMN_TYPE));
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
        m_columnType = ColumnType.valueOf(settings.getString(CFG_COLUMN_TYPE, DEFAULT_COLUMN_TYPE.name()));
        m_lockColumn = settings.getBoolean(CFG_LOCK_COLUMN, DEFAULT_LOCK_COLUMN);
        m_possibleValues =
            ValueSelectionFilterUtil.loadPossibleColumnsAndValuesInDialog(settings, CFG_POSSIBLE_COLUMNS, CFG_COL);
        m_type = settings.getString(CFG_TYPE, DEFAULT_TYPE);
        m_limitNumberVisOptions = settings.getBoolean(CFG_LIMIT_NUMBER_VIS_OPTIONS, DEFAULT_LIMIT_NUMBER_VIS_OPTIONS);
        m_numberVisOptions = settings.getInt(CFG_NUMBER_VIS_OPTIONS, DEFAULT_NUMBER_VIS_OPTIONS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("columnType=");
        sb.append(m_columnType);
        sb.append(", ");
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
        return new HashCodeBuilder().append(m_columnType).append(m_lockColumn).append(m_possibleValues).append(m_type)
            .append(m_limitNumberVisOptions).append(m_numberVisOptions).toHashCode();
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
        ValueSelectionNodeConfig other = (ValueSelectionNodeConfig)obj;
        return new EqualsBuilder().append(m_columnType, other.m_columnType).append(m_lockColumn, other.m_lockColumn)
            .append(m_possibleValues, other.m_possibleValues).append(m_type, other.m_type)
            .append(m_limitNumberVisOptions, other.m_limitNumberVisOptions)
            .append(m_numberVisOptions, other.m_numberVisOptions).isEquals();
    }

}
