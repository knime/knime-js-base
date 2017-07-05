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
package org.knime.dynamic.js.v30;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import org.apache.xmlbeans.XmlObject;
import org.knime.core.data.DataValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModel;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelColor;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnFilter2;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnName;
import org.knime.core.node.defaultnodesettings.SettingsModelDate;
import org.knime.core.node.defaultnodesettings.SettingsModelDoubleBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.defaultnodesettings.SettingsModelStringArray;
import org.knime.dynamic.js.SettingsModelSVGOptions;
import org.knime.dynamicjsnode.v30.DynamicJSKnimeNode;
import org.knime.dynamicnode.v30.CheckBoxOption;
import org.knime.dynamicnode.v30.ColorOption;
import org.knime.dynamicnode.v30.ColumnFilterOption;
import org.knime.dynamicnode.v30.ColumnSelectorOption;
import org.knime.dynamicnode.v30.DateFormatOption;
import org.knime.dynamicnode.v30.DateOption;
import org.knime.dynamicnode.v30.DoubleOption;
import org.knime.dynamicnode.v30.DynamicInPort;
import org.knime.dynamicnode.v30.DynamicOption;
import org.knime.dynamicnode.v30.DynamicOptions;
import org.knime.dynamicnode.v30.DynamicOutPort;
import org.knime.dynamicnode.v30.DynamicTab;
import org.knime.dynamicnode.v30.FileOption;
import org.knime.dynamicnode.v30.FlowVariableSelectorOption;
import org.knime.dynamicnode.v30.IntegerOption;
import org.knime.dynamicnode.v30.PortType;
import org.knime.dynamicnode.v30.RadioButtonOption;
import org.knime.dynamicnode.v30.StringListOption;
import org.knime.dynamicnode.v30.StringOption;
import org.knime.dynamicnode.v30.SvgOption;
import org.knime.js.core.components.datetime.SettingsModelDateTimeOptions;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * @since 3.0
 */
public class DynamicJSConfig {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(DynamicJSConfig.class);

    static final String DEFAULT_APPENDED_COLUMN_NAME = "Appended (%s)_%d";

    static final boolean DEFAULT_HIDE_IN_WIZARD = false;
    static final String HIDE_IN_WIZARD_CONF = "hideInWizard";
    private boolean m_hideInWizard = DEFAULT_HIDE_IN_WIZARD;

    static final int DEFAULT_MAX_ROWS = 2500;
    static final String MAX_ROWS_CONF = "maxRows";
    private int m_maxRows = DEFAULT_MAX_ROWS;

    static final boolean DEFAULT_GENERATE_IMAGE = true;
    static final String GENERATE_IMAGE_CONF = "generateImage";
    private boolean m_generateImage = DEFAULT_GENERATE_IMAGE;

    private boolean m_hasSVGImageOutport = false;
    private int m_numberDataInports = 0;

    private final DynamicJSKnimeNode m_nodeConfig;

    private Map<String, SettingsModel> m_models = new HashMap<String, SettingsModel>();
    private Set<String> m_defaultModelIds = new HashSet<String>();

    private List<Vector<String>> m_enableDependencies = new ArrayList<Vector<String>>();

    private int m_additionalWait = 0;

    /**
     * Creates a new config object, populating SettingsModels with the default values taken from the given node config
     * parameter.
     * @param nodeConfig The node config, read from XML.
     */
    public DynamicJSConfig(final DynamicJSKnimeNode nodeConfig) {
        m_nodeConfig = nodeConfig;
        List<SvgOption> svgOptionList = new ArrayList<SvgOption>();
        m_generateImage = false;
        m_hasSVGImageOutport = false;
        if (m_nodeConfig.getFullDescription().getOptions() != null) {
            fillOptions(m_nodeConfig.getFullDescription().getOptions());
            svgOptionList = m_nodeConfig.getFullDescription().getOptions().getSvgOptionList();
        }
        for (DynamicTab tab : m_nodeConfig.getFullDescription().getTabList()) {
            fillOptions(tab.getOptions());
            svgOptionList.addAll(tab.getOptions().getSvgOptionList());
        }
        int svgMatches = 0;
        if (!svgOptionList.isEmpty()) {
            for (SvgOption option : svgOptionList) {
                try {
                    DynamicOutPort port = m_nodeConfig.getPorts().getOutPortList().get(option.getPortIndex());
                    if (port.getPortType().equals(PortType.IMAGE)) {
                        svgMatches++;
                    } else {
                        LOGGER.error("SVG option defined but out port is not of type IMAGE.");
                    }
                } catch (IndexOutOfBoundsException e) {
                    LOGGER.error("SVG option defined, but port with index " + option.getPortIndex()
                        + " does not exist.");
                }
            }
        }
        if (svgMatches > 0) {
            m_generateImage = DEFAULT_GENERATE_IMAGE;
            m_hasSVGImageOutport = true;
        }
        for (DynamicInPort port : m_nodeConfig.getPorts().getInPortList()) {
            if (port.getPortType().equals(PortType.DATA)) {
                m_numberDataInports++;
            }
        }
    }

    @SuppressWarnings("unchecked")
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
                SettingsModelColumnFilter2 cModel = null;
                if (cO.isSetFilterClasses()) {
                    try {
                        List<String> filterClasses = cO.getFilterClasses();
                        cModel = new SettingsModelColumnFilter2(cO.getId(), getFilterClasses(filterClasses));
                    } catch (ClassNotFoundException e) {
                        //make unchecked
                        throw new ClassCastException(e.getMessage());
                    }
                } else {
                    cModel = new SettingsModelColumnFilter2(cO.getId());
                }
                m_models.put(cO.getId(), cModel);
            } else if (option instanceof ColumnSelectorOption) {
                ColumnSelectorOption cO = (ColumnSelectorOption)option;
                SettingsModel model;
                if (cO.getAllowRowID()) {
                    model = new SettingsModelColumnName(cO.getId(), cO.getDefaultColumn());
                } else {
                    model = new SettingsModelString(cO.getId(), cO.getDefaultColumn());
                }
                m_models.put(cO.getId(), model);
            } else if (option instanceof IntegerOption) {
                IntegerOption iO = (IntegerOption)option;
                int minValue = Integer.MIN_VALUE;
                if (iO.isSetMinValue()) {
                    minValue = iO.getMinValue().intValue();
                }
                int maxValue = Integer.MAX_VALUE;
                if (iO.isSetMaxValue()) {
                    maxValue = iO.getMaxValue().intValue();
                }
                int defaultValue = 0;
                if (iO.isSetDefaultValue()) {
                    defaultValue = iO.getDefaultValue().intValue();
                }
                SettingsModelIntegerBounded iModel = new SettingsModelIntegerBounded(iO.getId(), defaultValue, minValue, maxValue);
                m_models.put(iO.getId(), iModel);
            } else if (option instanceof DoubleOption) {
                DoubleOption dO = (DoubleOption)option;
                double minValue = Double.MIN_VALUE;
                if (dO.isSetMinValue()) {
                    minValue = dO.getMinValue();
                }
                double maxValue = Double.MAX_VALUE;
                if (dO.isSetMaxValue()) {
                    maxValue = dO.getMaxValue();
                }
                double defaultValue = 0d;
                if (dO.isSetDefaultValue()) {
                    defaultValue = dO.getDefaultValue();
                }
                SettingsModelDoubleBounded dModel = new SettingsModelDoubleBounded(dO.getId(), defaultValue, minValue, maxValue);
                m_models.put(dO.getId(), dModel);
            } else if (option instanceof RadioButtonOption) {
                RadioButtonOption rO = (RadioButtonOption)option;
                SettingsModelString sModel = new SettingsModelString(rO.getId(), rO.getDefaultValue());
                m_models.put(rO.getId(), sModel);
            } else if (option instanceof StringListOption) {
                StringListOption sO = (StringListOption)option;
                SettingsModel sModel;
                List<String> defaultValues = sO.getDefaultValues();
                if (sO.getAllowMultipleSelection()) {
                    String[] defaultStrings = null;
                    if (defaultValues != null) {
                        defaultStrings = defaultValues.toArray(new String[0]);
                    }
                    sModel = new SettingsModelStringArray(sO.getId(), defaultStrings);
                } else {
                    String defaultString = null;
                    if (defaultValues != null && defaultValues.size() > 0) {
                        defaultString = defaultValues.get(0);
                    }
                    sModel = new SettingsModelString(sO.getId(), defaultString);
                }
                m_models.put(sO.getId(), sModel);
            } else if (option instanceof DateOption) {
                DateOption dO = (DateOption)option;
                SettingsModelDate dModel = new SettingsModelDate(dO.getId());
                if (dO.isSetDefaultValue()) {
                    dModel.setTimeInMillis(dO.getDefaultValue().getTimeInMillis());
                }
                if (dO.isSetMode()) {
                    dModel.setSelectedFields(dO.getMode().intValue()-1);
                }
                m_models.put(dO.getId(), dModel);
            } else if (option instanceof DateFormatOption) {
                DateFormatOption dfO = (DateFormatOption)option;
                SettingsModelDateTimeOptions dfModel = new SettingsModelDateTimeOptions(dfO.getId());
                m_models.put(dfO.getId(), dfModel);
            } else if (option instanceof ColorOption) {
                ColorOption cO = (ColorOption)option;
                int r,g,b,a;
                r = g = b = a = 0;
                if (cO.isSetDefaultR()) {
                    r = cO.getDefaultR();
                }
                if (cO.isSetDefaultG()) {
                    g = cO.getDefaultG();
                }
                if (cO.isSetDefaultB()) {
                    b = cO.getDefaultB();
                }
                if (cO.isSetDefaultAlpha()) {
                    a = cO.getDefaultAlpha();
                }
                Color defaultColor = new Color(r, g, b, a);
                SettingsModelColor cModel = new SettingsModelColor(cO.getId(), defaultColor);
                m_models.put(cO.getId(), cModel);
            } else if (option instanceof FlowVariableSelectorOption) {
                FlowVariableSelectorOption fO = (FlowVariableSelectorOption)option;
                SettingsModelString sModel = new SettingsModelString(fO.getId(), fO.getDefaultValue());
                m_models.put(fO.getId(), sModel);
            } else if (option instanceof FileOption) {
                FileOption fO = (FileOption)option;
                SettingsModelString sModel = new SettingsModelString(fO.getId(), fO.getDefaultValue());
                m_models.put(fO.getId(), sModel);
            } else if (option instanceof SvgOption) {
                SvgOption sO = (SvgOption)option;
                SettingsModelSVGOptions sModel = new SettingsModelSVGOptions(sO.getId());
                if (sO.isSetDefaultWidth()) {
                    sModel.setWidth(sO.getDefaultWidth().intValue());
                }
                if (sO.isSetDefaultHeight()) {
                    sModel.setHeight(sO.getDefaultHeight().intValue());
                }
                if (sO.isSetDefaultFullscreen()) {
                    sModel.setAllowFullscreen(sO.getDefaultFullscreen());
                }
                if (sO.isSetAllowFullscreen()) {
                    sModel.setShowFullscreenOption(sO.getAllowFullscreen());
                }
                if (sO.isSetAdditionalWait()) {
                    m_additionalWait = Math.max(m_additionalWait, sO.getAdditionalWait().intValue());
                }
                m_models.put(sO.getId(), sModel);
            }

            if (option instanceof DynamicOption) {
                DynamicOption gOption = (DynamicOption)option;
                if (gOption.getLoadDefaults()) {
                    m_defaultModelIds.add(gOption.getId());
                }
                if (gOption.isSetEnableDependency() && gOption.isSetEnableValue()) {
                    Vector<String> dependency = new Vector<String>();
                    dependency.add(gOption.getEnableDependency());
                    dependency.add(gOption.getId());
                    dependency.addAll(gOption.getEnableValue());
                    m_enableDependencies.add(dependency);
                }
            }
        }
    }

    /**
     * @param filterClasses
     * @return
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    Class<? extends DataValue>[] getFilterClasses(final List<String> filterClasses) throws ClassNotFoundException {
        Class<? extends DataValue>[] filters =  new Class[filterClasses.size()];
        int i = 0;
        for (String clazz : filterClasses) {
            Class<? extends DataValue> c = (Class<? extends DataValue>)Class.forName(clazz);
            filters[i++] = c;
        }
        return filters;
    }

    /**
     * @return the hideInWizard
     */
    public boolean getHideInWizard() {
        return m_hideInWizard;
    }

    /**
     * @param hideInWizard the hideInWizard to set
     */
    public void setHideInWizard(final boolean hideInWizard) {
        m_hideInWizard = hideInWizard;
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
     * @return the generateImage
     */
    public boolean getGenerateImage() {
        return m_generateImage;
    }

    /**
     * @param generateImage the generateImage to set
     */
    public void setGenerateImage(final boolean generateImage) {
        m_generateImage = generateImage;
    }

    /**
     * @return true, if at least one correctly configured image outport is present
     */
    public boolean getHasSvgImageOutport() {
        return m_hasSVGImageOutport;
    }

    /**
     * @return the numberDataInports
     */
    public int getNumberDataInPorts() {
        return m_numberDataInports;
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
     * @return the additionalWait
     */
    public int getAdditionalWait() {
        return m_additionalWait;
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
        settings.addBoolean(HIDE_IN_WIZARD_CONF, m_hideInWizard);
        settings.addInt(MAX_ROWS_CONF, m_maxRows);
        settings.addBoolean(GENERATE_IMAGE_CONF, m_generateImage);
    }

    /**
     * @param settings
     * @throws InvalidSettingsException
     */
    public void loadAdditionalNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_hideInWizard = settings.getBoolean(HIDE_IN_WIZARD_CONF);
        m_maxRows = settings.getInt(MAX_ROWS_CONF);
        m_generateImage = settings.getBoolean(GENERATE_IMAGE_CONF);
    }

    /**
     * @param settings
     */
    public void loadAdditionalNodeSettingsInDialog(final NodeSettingsRO settings) {
        m_hideInWizard = settings.getBoolean(HIDE_IN_WIZARD_CONF, DEFAULT_HIDE_IN_WIZARD);
        m_maxRows = settings.getInt(MAX_ROWS_CONF, DEFAULT_MAX_ROWS);
        m_generateImage = settings.getBoolean(GENERATE_IMAGE_CONF, DEFAULT_GENERATE_IMAGE);
    }

    /**
     * @param settings
     * @throws InvalidSettingsException
     */
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        for (Entry<String, SettingsModel> entry : m_models.entrySet()) {
            try {
                entry.getValue().loadSettingsFrom(settings);
            } catch (InvalidSettingsException e) {
                if (!m_defaultModelIds.contains(entry.getKey())) {
                    throw e;
                }
            }
        }
        loadAdditionalNodeSettings(settings);
    }
}
