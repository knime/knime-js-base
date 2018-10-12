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
 *   06.09.2018 (Daniel Bogenrieder, KNIME AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.css.editor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

/**
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
final class CSSEditorConfig {

    /** File containing default script. */
    private static final String DEFAULT_SCRIPT_CSS = "default_style.css";
    private static final NodeLogger LOGGER = NodeLogger.getLogger(CSSEditorConfig.class);

    private static final String CSS_CODE = "cssCode";
    private static final String VARIABLE_NAME = "variableName";
    private static final String APPEND_CHECKBOX = "appendCheckbox";
    private static final String SELECTED_BUTTON = "selectedButton";
    private static final String PREPEND_VARIABLE = "prependVariable";
    private static final String REPLACE_VARIABLE = "replaceVariable";
    private static final String WAS_COLLAPSED = "wasCollapsed";

    private String m_cssCode;
    private String m_newFlowVariableName = "css-stylesheet";
    private boolean m_appendCheckbox = false;
    private int m_selectedButton;
    private String m_prependVariable;
    private String m_replaceVariable;
    private boolean m_wasCollapsed;

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
     * @return the output flow variable name
     */
    public String getFlowVariableName() {
        return m_newFlowVariableName;
    }

    /**
     * @param flowVariableName the name of the output flow variable to set
     */
    public void setFlowVariableName(final String flowVariableName) {
        m_newFlowVariableName = flowVariableName;
    }

    public void setAppendCheckbox(final boolean appendCheckbox) {
        m_appendCheckbox = appendCheckbox;
    }

    public boolean getAppendCheckbox() {
        return m_appendCheckbox;
    }

    public void setSelectedButton(final int selectedButton) {
        m_selectedButton = selectedButton;
    }

    public int getSelectedButton() {
        return m_selectedButton;
    }

    public void setPrependVariable (final String variableName) {
        m_prependVariable = variableName;
    }

    public String getPrependVariable() {
        return m_prependVariable;
    }

    public void setReplaceVariable (final String variableName) {
        m_replaceVariable = variableName;
    }

    public String getReplaceVariable() {
        return m_replaceVariable;
    }

    public void setWasCollapsed (final boolean wasCollapsed) {
        m_wasCollapsed = wasCollapsed;
    }

    public boolean getWasCollapsed() {
        return m_wasCollapsed;
    }

    public boolean isReplace() {
        return getSelectedButton() == 1;
    }

    /** Saves current parameters to settings object.
     * @param settings To save to.
     */
    public void saveSettings(final NodeSettingsWO settings) {
        settings.addString(CSS_CODE, m_cssCode);
        settings.addString(VARIABLE_NAME, m_newFlowVariableName);
        settings.addBoolean(APPEND_CHECKBOX, m_appendCheckbox);
        settings.addInt(SELECTED_BUTTON, m_selectedButton);
        settings.addString(PREPEND_VARIABLE, m_prependVariable);
        settings.addString(REPLACE_VARIABLE, m_replaceVariable);
        settings.addBoolean(WAS_COLLAPSED, m_wasCollapsed);
    }

    /** Loads parameters in NodeModel.
     * @param settings To load from.
     * @throws InvalidSettingsException If incomplete or wrong.
     */
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_cssCode = settings.getString(CSS_CODE);
        m_newFlowVariableName = settings.getString(VARIABLE_NAME);
        m_appendCheckbox = settings.getBoolean(APPEND_CHECKBOX);
        m_selectedButton = settings.getInt(SELECTED_BUTTON);
        m_prependVariable = settings.getString(PREPEND_VARIABLE);
        m_replaceVariable = settings.getString(REPLACE_VARIABLE);
        m_wasCollapsed = settings.getBoolean(WAS_COLLAPSED);
    }

    /** Loads parameters in Dialog.
     * @param settings To load from.
     */
    public void loadSettingsForDialog(final NodeSettingsRO settings) {
        m_cssCode = settings.getString(CSS_CODE, null);
        if (m_cssCode == null) {
            try {
                m_cssCode = IOUtils.toString(CSSEditorConfig.class.getResource(DEFAULT_SCRIPT_CSS),
                    StandardCharsets.UTF_8);
            } catch (IOException e) {
                LOGGER.error(String.format("Could not read default css from file \"%s\"", DEFAULT_SCRIPT_CSS), e);
                m_cssCode = "";
            }
        }
        m_appendCheckbox = settings.getBoolean(APPEND_CHECKBOX, false);
        m_newFlowVariableName = settings.getString(VARIABLE_NAME, "");
        m_selectedButton = settings.getInt(SELECTED_BUTTON, 0);
        m_prependVariable = settings.getString(PREPEND_VARIABLE, "");
        m_replaceVariable = settings.getString(REPLACE_VARIABLE, "");
        m_wasCollapsed = settings.getBoolean(WAS_COLLAPSED, true);
    }
}
