/*
 * ------------------------------------------------------------------------
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
 * ------------------------------------------------------------------------
 *
 * History
 *   24.04.2015 (Christian Albrecht, KNIME.com AG, Zurich, Switzerland): created
 */
package org.knime.dynamic.js;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.apache.xmlbeans.XmlObject;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModel;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnFilter2;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnName;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.dynamicjsnode.v212.DynamicJSKnimeNode;
import org.knime.dynamicnode.v212.CheckBoxOption;
import org.knime.dynamicnode.v212.ColumnFilterOption;
import org.knime.dynamicnode.v212.ColumnSelectorOption;
import org.knime.dynamicnode.v212.DynamicOption;
import org.knime.dynamicnode.v212.DynamicOptions;
import org.knime.dynamicnode.v212.DynamicTab;
import org.knime.dynamicnode.v212.StringOption;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of Konstanz
 */
public class DynamicJSConfig {

    static final int DEFAULT_MAX_ROWS = 2500;

    static final String MAX_ROWS = "maxRows";

    private final DynamicJSKnimeNode m_nodeConfig;

    private int m_maxRows = DEFAULT_MAX_ROWS;

    private Map<String, SettingsModel> m_models = new HashMap<String, SettingsModel>();

    private List<Vector<String>> m_enableDependencies = new ArrayList<Vector<String>>();

    /**
     * Creates a new config object, populating SettingsModels with the default values taken from the given node config
     * parameter.
     * @param nodeConfig The node config, read from XML.
     */
    public DynamicJSConfig(final DynamicJSKnimeNode nodeConfig) {
        m_nodeConfig = nodeConfig;
        if (m_nodeConfig.getFullDescription().getOptions() != null) {
            fillOptions(m_nodeConfig.getFullDescription().getOptions());
        }
        for (DynamicTab tab : m_nodeConfig.getFullDescription().getTabList()) {
            fillOptions(tab.getOptions());
        }
    }

    private void fillOptions(final DynamicOptions options) {
        XmlObject[] oOptions = options.selectPath("$this/*");
        for (XmlObject option : oOptions) {
            if (option instanceof CheckBoxOption) {
                CheckBoxOption cO = (CheckBoxOption)option;
                SettingsModelBoolean bModel = new SettingsModelBoolean(cO.getId(), cO.getDefaultValue());
                m_models.put(cO.getId(), bModel);
            } else if (option instanceof StringOption) {
                StringOption sO = (StringOption)option;
                SettingsModelString sModel = new SettingsModelString(sO.getId(), sO.getDefaultValue());
                m_models.put(sO.getId(), sModel);
            } else if (option instanceof ColumnFilterOption) {
                ColumnFilterOption cO = (ColumnFilterOption)option;
                SettingsModelColumnFilter2 cModel = new SettingsModelColumnFilter2(cO.getId());
                m_models.put(cO.getId(), cModel);
            } else if (option instanceof ColumnSelectorOption) {
                ColumnSelectorOption cO = (ColumnSelectorOption)option;
                SettingsModelColumnName cModel = new SettingsModelColumnName(cO.getId(), cO.getDefaultColumn());
                m_models.put(cO.getId(), cModel);
            }
            DynamicOption gOption = (DynamicOption)option;
            if (gOption.isSetEnableDependency() && gOption.isSetEnableValue()) {
                Vector<String> dependency = new Vector<String>();
                dependency.add(gOption.getEnableDependency());
                dependency.add(gOption.getId());
                dependency.add(gOption.getEnableValue().getStringValue());
                m_enableDependencies.add(dependency);
            }
        }
    }

    /**
     * @return the maxRows
     */
    public int getMaxRows() {
        return m_maxRows;
    }

    /**
     * @param maxRows the maxRows to set
     */
    public void setMaxRows(final int maxRows) {
        m_maxRows = maxRows;
    }

    /**
     * @return the models
     */
    public Map<String, SettingsModel> getModels() {
        return m_models;
    }

    /**
     * @param models the models to set
     */
    public void setModels(final Map<String, SettingsModel> models) {
        m_models = models;
    }

    /**
     * @param name name of the model to get
     * @return the model
     */
    public SettingsModel getModel(final String name) {
        return m_models.get(name);
    }

    /**
     * @param name name of the model to set
     * @param model the model to set
     */
    public void setModel(final String name, final SettingsModel model) {
        m_models.put(name, model);
    }

    /**
     * @return the enableDependencies
     */
    public List<Vector<String>> getEnableDependencies() {
        return m_enableDependencies;
    }

    /**
     * @param enableDependencies the enableDependencies to set
     */
    public void setEnableDependencies(final List<Vector<String>> enableDependencies) {
        m_enableDependencies = enableDependencies;
    }

    /**
     * Adds the config settings to the given <code>NodeSettings</code> object.
     * @param settings The object to write settings into.
     */
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        for (SettingsModel model : m_models.values()) {
            model.saveSettingsTo(settings);
        }
        saveAdditionalSettings(settings);
    }

    /**
     * Saves additional settings, which is everything except the models.
     * @param settings The object to write settings into.
     */
    public void saveAdditionalSettings(final NodeSettingsWO settings) {
        settings.addInt(MAX_ROWS, m_maxRows);
    }

    /**
     * @param settings
     * @throws InvalidSettingsException
     */
    public void loadAdditionalNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_maxRows = settings.getInt(MAX_ROWS);
    }

    /**
     * @param settings
     */
    public void loadAdditionalNodeSettingsInDialog(final NodeSettingsRO settings) {
        m_maxRows = settings.getInt(MAX_ROWS, DEFAULT_MAX_ROWS);
    }

    /**
     * @param settings
     * @throws InvalidSettingsException
     */
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        for (Entry<String, SettingsModel> entry : m_models.entrySet()) {
            entry.getValue().loadSettingsFrom(settings);
        }
        loadAdditionalNodeSettings(settings);
    }
}
