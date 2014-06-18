/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
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
 *   Jun 13, 2014 (winter): created
 */
package org.knime.js.base.node.quickform.selection.value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
import org.knime.js.base.node.quickform.QuickFormFlowVariableConfig;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author winter
 */
public class ValueSelectionQuickFormConfig extends QuickFormFlowVariableConfig {

    private static final String CFG_COLUMN_TYPE = "columnType";

    private static final ColumnType DEFAULT_COLUMN_TYPE = ColumnType.All;

    private ColumnType m_columnType = DEFAULT_COLUMN_TYPE;

    private static final String CFG_LOCK_COLUMN = "lockColumn";

    private static final boolean DEFAULT_LOCK_COLUMN = false;

    private boolean m_lockColumn = DEFAULT_LOCK_COLUMN;

    private static final String CFG_DEFAULT_COLUMN = "defaultColumn";

    private static final String DEFAULT_DEFAULT_COLUMN = "";

    private String m_defaultColumn = DEFAULT_DEFAULT_COLUMN;

    private static final String CFG_DEFAULT_VALUE = "default";

    private static final String DEFAULT_DEFAULT_VALUE = "";

    private String m_defaultValue = DEFAULT_DEFAULT_VALUE;

    private static final String CFG_POSSIBLE_COLUMNS = "possibleColumns";

    private Map<String, List<String>> m_possibleValues = new TreeMap<String, List<String>>();

    private static final String CFG_TYPE = "type";

    private static final String DEFAULT_TYPE = SingleSelectionComponentFactory.DROPDOWN;

    private String m_type = DEFAULT_TYPE;

    private static final String CFG_COLUMN = "column";

    private static final String DEFAULT_COLUMN = "";

    private String m_column = DEFAULT_COLUMN;

    private static final String CFG_VALUE = "value";

    private static final String DEFAULT_VALUE = "";

    private String m_value = DEFAULT_VALUE;

    ColumnType getColumnType() {
        return m_columnType;
    }

    void setColumnType(final ColumnType columnType) {
        m_columnType = columnType;
    }

    boolean getLockColumn() {
        return m_lockColumn;
    }

    void setLockColumn(final boolean lockColumn) {
        m_lockColumn = lockColumn;
    }

    String getDefaultColumn() {
        return m_defaultColumn;
    }

    void setDefaultColumn(final String defaultColumn) {
        m_defaultColumn = defaultColumn;
    }

    String getDefaultValue() {
        return m_defaultValue;
    }

    void setDefaultValue(final String defaultValue) {
        m_defaultValue = defaultValue;
    }

    Map<String, List<String>> getPossibleValues() {
        return m_possibleValues;
    }

    void setPossibleValues(final Map<String, List<String>> possibleValues) {
        m_possibleValues = possibleValues;
    }

    String getType() {
        return m_type;
    }

    void setType(final String type) {
        m_type = type;
    }

    String getColumn() {
        return m_column;
    }

    void setColumn(final String column) {
        m_column = column;
    }

    String getValue() {
        return m_value;
    }

    void setValue(final String value) {
        m_value = value;
    }

    @Override
    public void saveSettings(final NodeSettingsWO settings) {
        super.saveSettings(settings);
        settings.addString(CFG_COLUMN_TYPE, m_columnType.name());
        settings.addBoolean(CFG_LOCK_COLUMN, m_lockColumn);
        settings.addString(CFG_DEFAULT_COLUMN, m_defaultColumn);
        settings.addString(CFG_DEFAULT_VALUE, m_defaultValue);
        settings.addStringArray(CFG_POSSIBLE_COLUMNS,
            m_possibleValues.keySet().toArray(new String[m_possibleValues.keySet().size()]));
        settings.addString(CFG_DEFAULT_COLUMN, m_defaultColumn);
        for (String key : m_possibleValues.keySet()) {
            List<String> values = m_possibleValues.get(key);
            settings.addStringArray(key, values.toArray(new String[values.size()]));
        }
        settings.addString(CFG_TYPE, m_type);
        settings.addString(CFG_COLUMN, m_column);
        settings.addString(CFG_VALUE, m_value);
    }

    @Override
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        super.loadSettings(settings);
        m_columnType = ColumnType.valueOf(settings.getString(CFG_COLUMN_TYPE));
        m_lockColumn = settings.getBoolean(CFG_LOCK_COLUMN);
        m_defaultColumn = settings.getString(CFG_DEFAULT_COLUMN);
        m_defaultValue = settings.getString(CFG_DEFAULT_VALUE);
        m_possibleValues = new TreeMap<String, List<String>>();
        String[] columns = settings.getStringArray(CFG_POSSIBLE_COLUMNS);
        for (String column : columns) {
            m_possibleValues.put(column, Arrays.asList(settings.getStringArray(column)));
        }
        m_type = settings.getString(CFG_TYPE);
        m_column = settings.getString(CFG_COLUMN);
        m_value = settings.getString(CFG_VALUE);
    }

    @Override
    public void loadSettingsInDialog(final NodeSettingsRO settings) {
        super.loadSettingsInDialog(settings);
        m_defaultValue = settings.getString(CFG_DEFAULT_VALUE, DEFAULT_DEFAULT_VALUE);
        m_columnType = ColumnType.valueOf(settings.getString(CFG_COLUMN_TYPE, DEFAULT_COLUMN_TYPE.name()));
        m_lockColumn = settings.getBoolean(CFG_LOCK_COLUMN, DEFAULT_LOCK_COLUMN);
        m_defaultColumn = settings.getString(CFG_DEFAULT_COLUMN, DEFAULT_DEFAULT_COLUMN);
        m_possibleValues = new TreeMap<String, List<String>>();
        String[] columns = settings.getStringArray(CFG_POSSIBLE_COLUMNS, new String[0]);
        for (String column : columns) {
            m_possibleValues.put(column, Arrays.asList(settings.getStringArray(column, new String[0])));
        }
        m_type = settings.getString(CFG_TYPE, DEFAULT_TYPE);
        m_column = settings.getString(CFG_COLUMN, DEFAULT_COLUMN);
        m_value = settings.getString(CFG_VALUE, DEFAULT_VALUE);
    }

    /**
     * @param spec the spec to set
     */
    @JsonIgnore
    public void setFromSpec(final DataTableSpec spec) {
        // Only add column specs for columns that have values and are of the selected type
        List<DataColumnSpec> specs = new ArrayList<DataColumnSpec>();
        for (DataColumnSpec cspec : spec) {
            if (cspec.getDomain().hasValues()) {
                switch (m_columnType) {
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
                List<String> v = new ArrayList<String>();
                for (final DataCell cell : vals) {
                    v.add(cell.toString());
                }
                values.put(colSpec.getName(), v);
            }
        }
        m_possibleValues = values;
    }

}