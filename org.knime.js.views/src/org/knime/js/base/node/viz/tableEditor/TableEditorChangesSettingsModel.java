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
 *   10.11.2017 (Oleg Yasnev): created
 */
package org.knime.js.base.node.viz.tableEditor;

import java.util.HashMap;
import java.util.Map;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.SettingsModel;
import org.knime.core.node.port.PortObjectSpec;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author Oleg Yasnev, KNIME GmbH, Konstanz, Germany
 */
@JsonAutoDetect
@JsonIgnoreProperties({"enabled", "configName"})
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class TableEditorChangesSettingsModel extends SettingsModel {

    // map from RowId to a map from Column Index to Data Value
    private Map<Integer, Map<Integer, Object>> m_changes;

    private String m_configName;

    /**
     * @param configName the identifier the value is stored with in the {@link org.knime.core.node.NodeSettings} object
     */
    public TableEditorChangesSettingsModel(final String configName) {
        if ((configName == null) || "".equals(configName)) {
            throw new IllegalArgumentException("The configName must be a " + "non-empty string");
        }
        m_configName = configName;

        m_changes = new HashMap<Integer, Map<Integer,Object>>();
    }

    /**
     * Serialization constructor. Do not use!
     */
    public TableEditorChangesSettingsModel() {
        m_configName = null;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected TableEditorChangesSettingsModel createClone() {
        TableEditorChangesSettingsModel copy = new TableEditorChangesSettingsModel(getConfigName());
        return copy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getModelTypeID() {
        return "SMID_tableEditorChanges";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getConfigName() {
        return m_configName;
    }

    /**
     * @param configName the configName to set
     */
    public void setConfigName(final String configName) {
        m_configName = configName;
    }

    /**
     * @return the changes
     */
    public Map<Integer, Map<Integer, Object>> getChanges() {
        return m_changes;
    }

    /**
     * @param changes the changes to set
     */
    public void setChanges(final Map<Integer, Map<Integer, Object>> changes) {
        m_changes = changes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsForDialog(final NodeSettingsRO settings, final PortObjectSpec[] specs)
        throws NotConfigurableException {
        try {
            loadSettings(settings);
        } catch (InvalidSettingsException e) {
            // if settings not found: keep the old value.
            return;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsForDialog(final NodeSettingsWO settings) throws InvalidSettingsException {
        saveSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettingsForModel(final NodeSettingsRO settings) throws InvalidSettingsException {
        Map<Integer, Map<Integer, Object>> curChanges = m_changes;
        loadSettings(settings);
        m_changes = curChanges;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsForModel(final NodeSettingsRO settings) throws InvalidSettingsException {
        loadSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsForModel(final NodeSettingsWO settings) {
        saveSettings(settings);
    }

    private void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        NodeSettingsRO editChangesSettings = settings.getNodeSettings(m_configName);
        int numRows = editChangesSettings.getInt("numRows");
        m_changes = new HashMap<Integer, Map<Integer,Object>>(numRows);
        for (int i = 0; i < numRows; i++) {
            NodeSettingsRO rowSettings = editChangesSettings.getNodeSettings("rowEntry" + i);
            int rowId = rowSettings.getInt("rowId");
            int numCells = rowSettings.getInt("numCells");
            Map<Integer, Object> rowMap = new HashMap<Integer, Object>(numCells);
            for (int j = 0; j < numCells; j++) {
                NodeSettingsRO cellSettings = rowSettings.getNodeSettings("cellEntry" + j);
                int colIndex = cellSettings.getInt("colIndex");
                String type = cellSettings.getString("type");
                Object value = null;
                switch (type) {
                    case "mv":
                        // null value has been already assigned
                        break;
                    case "int":
                        value = cellSettings.getInt("value");
                        break;
                    case "double":
                        value = cellSettings.getDouble("value");
                        break;
                    case "string":
                        value = cellSettings.getString("value");
                        break;
                }
                rowMap.put(colIndex, value);
            }
            m_changes.put(rowId, rowMap);
        }
    }

    private void saveSettings(final NodeSettingsWO settings) {
        NodeSettingsWO editChangesSettings = settings.addNodeSettings(m_configName);
        editChangesSettings.addInt("numRows", m_changes.size());
        int rowCnt = 0;
        for (Map.Entry<Integer, Map<Integer, Object>> rowEntry : m_changes.entrySet()) {
            NodeSettingsWO rowSettings = editChangesSettings.addNodeSettings("rowEntry" + rowCnt);
            rowSettings.addInt("rowId", rowEntry.getKey());
            rowSettings.addInt("numCells", rowEntry.getValue().size());
            int cellCnt = 0;
            for (Map.Entry<Integer, Object> cellEntry : rowEntry.getValue().entrySet()) {
                NodeSettingsWO cellSettings = rowSettings.addNodeSettings("cellEntry" + cellCnt);
                cellSettings.addInt("colIndex", cellEntry.getKey());
                Object value = cellEntry.getValue();
                if (value == null) {
                    cellSettings.addString("type", "mv");  // missing value
                } else if (value instanceof Integer) {
                    cellSettings.addString("type", "int");
                    cellSettings.addInt("value", (Integer)value);
                } else if (value instanceof Double) {
                    cellSettings.addString("type", "double");
                    cellSettings.addDouble("value", (Double)value);
                } else if (value instanceof String) {
                    cellSettings.addString("type", "string");
                    cellSettings.addString("value", value.toString());
                }
                cellCnt++;
            }
            rowCnt++;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + " ('" + m_configName + "')";
    }

}
