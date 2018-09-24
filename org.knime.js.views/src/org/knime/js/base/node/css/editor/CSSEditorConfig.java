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

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.util.dialog.field.FieldCollection;
import org.knime.core.node.util.dialog.field.FieldList.InColumnList;
import org.knime.core.node.util.dialog.field.FieldList.InFlowVariableList;
import org.knime.core.node.util.dialog.field.FieldList.OutColumnList;
import org.knime.core.node.util.dialog.field.FieldList.OutFlowVariableList;

/**
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
final class CSSEditorConfig {

    private static final String HIDE_IN_WIZARD = "hideInWizard";
    private static final String CSS_CODE = "cssCode";
    private static final String OUT_VARS = "outputVariables";
    private static final String VARIABLE_NAME = "variableName";
    private static final String GUARDED_DOCUMENT = "guardedDocument";
    private static final String APPEND_CHECKBOX = "appendCheckbox";
    private static final String SELECTED_BUTTON = "selectedButton";
    private static final String PREPEND_VARIABLE = "prependVariable";
    private static final String REPLACE_VARIABLE = "replaceVariable";
    private static final String WAS_COLLAPSED = "wasCollapsed";

    private boolean m_hideInWizard = false;
    private String m_cssCode;
    private OutFlowVariableList m_outVarList;
    private String m_newFlowVariableName = "css-stylesheet";
    private String m_guardedDocument;
    private boolean m_appendCheckbox = false;
    private int m_selectedButton;
    private String m_prependVariable;
    private String m_replaceVariable;
    private boolean m_wasCollapsed;


    /**
    *
    */
   public CSSEditorConfig() {
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

    public void setGuardedDocument(final String guardedDocument) {
        m_guardedDocument = guardedDocument;
    }

    public String getGuardedDocument() {
        return m_guardedDocument;
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

    public Boolean getWasCollapsed() {
        return m_wasCollapsed;
    }

    /** Saves current parameters to settings object.
     * @param settings To save to.
     */
    public void saveSettings(final NodeSettingsWO settings) {
        settings.addBoolean(HIDE_IN_WIZARD, getHideInWizard());
        m_outVarList.saveSettings(settings.addConfig(OUT_VARS));

      //added with 3.7
        settings.addString(CSS_CODE, m_cssCode);
        settings.addString(VARIABLE_NAME, m_newFlowVariableName);
        settings.addString(GUARDED_DOCUMENT, m_guardedDocument);
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
        setHideInWizard(settings.getBoolean(HIDE_IN_WIZARD, false));
        m_outVarList.loadSettings(settings.getConfig(OUT_VARS));

        //added with 3.7
        m_cssCode = settings.getString(CSS_CODE);
        m_newFlowVariableName = settings.getString(VARIABLE_NAME, "");
        m_guardedDocument = settings.getString(GUARDED_DOCUMENT, "");
        m_appendCheckbox = settings.getBoolean(APPEND_CHECKBOX, false);
        m_selectedButton = settings.getInt(SELECTED_BUTTON, 0);
        m_prependVariable = settings.getString(PREPEND_VARIABLE, "");
        m_replaceVariable = settings.getString(REPLACE_VARIABLE, "");
        m_wasCollapsed = settings.getBoolean(WAS_COLLAPSED, true);
    }

    /** Loads parameters in Dialog.
     * @param settings To load from.
     */
    public void loadSettingsForDialog(final NodeSettingsRO settings) {
        setHideInWizard(settings.getBoolean(HIDE_IN_WIZARD, false));

        try {
            m_outVarList.loadSettingsForDialog(settings.getConfig(OUT_VARS));
        } catch (InvalidSettingsException e) {
           m_outVarList = new OutFlowVariableList(true);
        }

        //added with 3.7
        m_cssCode = settings.getString(CSS_CODE, "");
        m_guardedDocument = settings.getString(GUARDED_DOCUMENT, null);
        m_appendCheckbox = settings.getBoolean(APPEND_CHECKBOX, false);
        m_newFlowVariableName = settings.getString(VARIABLE_NAME, "");
        m_selectedButton = settings.getInt(SELECTED_BUTTON, 0);
        m_prependVariable = settings.getString(PREPEND_VARIABLE, "");
        m_replaceVariable = settings.getString(REPLACE_VARIABLE, "");
        m_wasCollapsed = settings.getBoolean(WAS_COLLAPSED, true);
    }
}
