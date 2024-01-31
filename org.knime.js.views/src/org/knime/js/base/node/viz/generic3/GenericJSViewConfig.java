/*
 * ------------------------------------------------------------------------
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
 * ------------------------------------------------------------------------
 *
 * History
 *   06.05.2014 (Christian Albrecht, KNIME AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.viz.generic3;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.util.dialog.field.FieldCollection;
import org.knime.core.node.util.dialog.field.FieldList.InColumnList;
import org.knime.core.node.util.dialog.field.FieldList.InFlowVariableList;
import org.knime.core.node.util.dialog.field.FieldList.OutColumnList;
import org.knime.core.node.util.dialog.field.FieldList.OutFlowVariableList;
import org.knime.js.base.template.JSTemplate;

/**
 *
 * @author Christian Albrecht, KNIME AG, Zurich, Switzerland, University of Konstanz
 */
public final class GenericJSViewConfig {

    /** Default row maximum. */
    static final int DEFAULT_MAX_ROWS = 2500;

    /** File containing default script. */
    private static final String DEFAULT_SCRIPT_CSS = "default_script.css";

    /** File containing default CSS. */
    private static final String DEFAULT_SCRIPT_JS = "default_script.js";

    private static final NodeLogger LOGGER = NodeLogger.getLogger(GenericJSViewConfig.class);

    private static final String HIDE_IN_WIZARD = "hideInWizard";
    private static final String GENERATE_VIEW = "generateView";
    private static final String MAX_ROWS = "maxRows";
    private static final String JS_CODE = "jsCode";
    private static final String JS_SVG_CODE = "jsSVGCode";
    private static final String CSS_CODE = "cssCode";
    private static final String DEPENDENCIES = "dependencies";
    private static final String WAIT_TIME = "waitTime";
    private static final String OUT_VARS = "outputVariables";
    private static final String CUSTOM_CSS = "customCSS";
    private static final String SANITIZE_INPUT = "sanitizeInput";
    //private static final String VIEW_NAME = "viewName";

    private boolean m_hideInWizard = false;
    private boolean m_generateView = false;
    private int m_maxRows = DEFAULT_MAX_ROWS;
    private String m_jsCode;
    private String m_jsSVGCode;
    private String m_cssCode;
    private String[] m_dependencies;
    private int m_waitTime;
    private OutFlowVariableList m_outVarList;
    private String m_customCSS;
    private boolean m_sanitizeInput = false;

    //private String m_viewName;

    /**
     *
     */
    public GenericJSViewConfig() {
        m_dependencies = new String[0];
        m_outVarList = new OutFlowVariableList(true);
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
     * @return the generateView
     */
    public boolean getGenerateView() {
        return m_generateView;
    }

    /**
     * @param generateView the generateView to set
     */
    public void setGenerateView(final boolean generateView) {
        m_generateView = generateView;
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
     * @return the jsCode
     */
    public String getJsCode() {
        return m_jsCode;
    }

    /**
     * @param jsCode the jsCode to set
     */
    public void setJsCode(final String jsCode) {
        m_jsCode = jsCode;
    }

    /**
     * @return the jsSVGCode
     */
    public String getJsSVGCode() {
        return m_jsSVGCode;
    }

    /**
     * @param jsSVGCode the jsSVGCode to set
     */
    public void setJsSVGCode(final String jsSVGCode) {
        m_jsSVGCode = jsSVGCode;
    }

    /**
     * @return the cssCode
     */
    public String getCssCode() {
        return m_cssCode;
    }

    /**
     * @param cssCode the cssCode to set
     */
    public void setCssCode(final String cssCode) {
        m_cssCode = cssCode;
    }

    /**
     * @return the dependencies
     */
    public String[] getDependencies() {
        return m_dependencies;
    }

    /**
     * @param dependencies the dependencies to set
     */
    public void setDependencies(final String[] dependencies) {
        m_dependencies = dependencies;
    }

    /**
     * @return the waitTime
     */
    public int getWaitTime() {
        return m_waitTime;
    }

    /**
     * @param waitTime the waitTime to set
     */
    public void setWaitTime(final int waitTime) {
        m_waitTime = waitTime;
    }

    /**
     * @return the outVarList
     */
    public OutFlowVariableList getOutVarList() {
        return m_outVarList;
    }

    /**
     * @param outVarList the outVarList to set
     */
    public void setOutVarList(final OutFlowVariableList outVarList) {
        m_outVarList = outVarList;
    }

    /**
     * @return a {@link FieldCollection} object. Only the output flow variable is filled, all other elements are empty lists
     */
    public FieldCollection getFieldCollection() {
        return new FieldCollection(new InColumnList(), new InFlowVariableList(), new OutColumnList(), m_outVarList);
    }

    /**
     * @param fields sets a {@link FieldCollection} object. Only the output flow variable list is imported.
     */
    public void setFieldCollection(final FieldCollection fields) {
        m_outVarList = fields.getOutFlowVariableList();
    }

    /**
     * @return the customCSS
     */
    public String getCustomCSS() {
        return m_customCSS;
    }

    /**
     * @param customCSS the customCSS to set
     */
    public void setCustomCSS(final String customCSS) {
        m_customCSS = customCSS;
    }

    /**
     * @return the sanitizeInput
     */
    public boolean isSanitizeInput() {
        return m_sanitizeInput;
    }

    /**
     * @param sanitizeInput the sanitizeInput to set
     */
    public void setSanitizeInput(final boolean sanitizeInput) {
        m_sanitizeInput = sanitizeInput;
    }

    /**
     * @return the viewName
     */
    /*public String getViewName() {
        return m_viewName;
    }*/

    /**
     * @param viewName the viewName to set
     */
    /*public void setViewName(final String viewName) {
        m_viewName = viewName;
    }*/

    /** Saves current parameters to settings object.
     * @param settings To save to.
     */
    public void saveSettings(final NodeSettingsWO settings) {
        settings.addBoolean(HIDE_IN_WIZARD, getHideInWizard());
        settings.addBoolean(GENERATE_VIEW, getGenerateView());
        settings.addInt(MAX_ROWS, getMaxRows());
        settings.addString(JS_CODE, m_jsCode);
        settings.addString(JS_SVG_CODE, m_jsSVGCode);
        settings.addString(CSS_CODE, m_cssCode);
        settings.addStringArray(DEPENDENCIES, m_dependencies);
        settings.addInt(WAIT_TIME, getWaitTime());
        m_outVarList.saveSettings(settings.addConfig(OUT_VARS));

        //added with 3.6
        settings.addString(CUSTOM_CSS, m_customCSS);

        //added with 5.2
        settings.addBoolean(SANITIZE_INPUT, m_sanitizeInput);
    }

    /** Loads parameters in NodeModel.
     * @param settings To load from.
     * @throws InvalidSettingsException If incomplete or wrong.
     */
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        setHideInWizard(settings.getBoolean(HIDE_IN_WIZARD));
        setGenerateView(settings.getBoolean(GENERATE_VIEW));
        setMaxRows(settings.getInt(MAX_ROWS, DEFAULT_MAX_ROWS));
        m_jsCode = settings.getString(JS_CODE);
        setJsSVGCode(settings.getString(JS_SVG_CODE));
        m_cssCode = settings.getString(CSS_CODE);
        m_dependencies = settings.getStringArray(DEPENDENCIES);
        setWaitTime(settings.getInt(WAIT_TIME));
        m_outVarList.loadSettings(settings.getConfig(OUT_VARS));

        //added with 3.6
        m_customCSS = settings.getString(CUSTOM_CSS, "");

        //added with 5.2
        m_sanitizeInput = settings.getBoolean(SANITIZE_INPUT, false);
    }

    /** Loads parameters in Dialog.
     * @param settings To load from.
     */
    public void loadSettingsForDialog(final NodeSettingsRO settings) {
        setHideInWizard(settings.getBoolean(HIDE_IN_WIZARD, false));
        setGenerateView(settings.getBoolean(GENERATE_VIEW, false));
        setMaxRows(settings.getInt(MAX_ROWS, DEFAULT_MAX_ROWS));
        m_jsCode = settings.getString(JS_CODE, null);
        if (m_jsCode == null) {
            try {
                m_jsCode = IOUtils.toString(GenericJSViewConfig.class.getResource(DEFAULT_SCRIPT_JS),
                    StandardCharsets.UTF_8);
            } catch (IOException e) {
                LOGGER.error(String.format("Could not read default javascript from file \"%s\"", DEFAULT_SCRIPT_JS), e);
                m_jsCode = "";
            }
        }
        m_jsSVGCode = settings.getString(JS_SVG_CODE, null);
        m_cssCode = settings.getString(CSS_CODE, null);
        if (m_cssCode == null) {
            try {
                m_cssCode = IOUtils.toString(GenericJSViewConfig.class.getResource(DEFAULT_SCRIPT_CSS),
                    StandardCharsets.UTF_8);
            } catch (IOException e) {
                LOGGER.error(String.format("Could not read default css from file \"%s\"", DEFAULT_SCRIPT_CSS), e);
                m_cssCode = "";
            }
        }
        m_dependencies = settings.getStringArray(DEPENDENCIES, new String[0]);
        setWaitTime(settings.getInt(WAIT_TIME, 0));
        try {
            m_outVarList.loadSettingsForDialog(settings.getConfig(OUT_VARS));
        } catch (InvalidSettingsException e) {
           m_outVarList = new OutFlowVariableList(true);
        }

        //added with 3.6
        m_customCSS = settings.getString(CUSTOM_CSS, "");

        //added with 5.2
        m_sanitizeInput = settings.getBoolean(SANITIZE_INPUT, false);
    }

    /**
     * Create a template from the current settings
     *
     * @param metaCategory the meta category of the template
     * @return the template with a new uuid.
     */
    public JSTemplate createTemplate(@SuppressWarnings("rawtypes") final Class metaCategory) {
        final JSTemplate template = new JSTemplate(metaCategory, this);
        return template;
    }
}
