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
 *   AI Migration
 */
package org.knime.js.base.node.widget.input.string;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.base.node.base.input.string.StringNodeConfig;
import org.knime.js.base.node.widget.util.CommonWidgetPersistors;
import org.knime.js.base.node.widget.util.WidgetNodeSettingsBase;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.persistence.NodeParametersPersistor;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.persistence.Persistor;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.updates.ValueReference;
import org.knime.node.parameters.widget.choices.Label;
import org.knime.node.parameters.widget.choices.RadioButtonsWidget;
import org.knime.node.parameters.widget.number.NumberInputWidget;
import org.knime.node.parameters.widget.number.NumberInputWidgetValidation.MinValidation.IsPositiveIntegerValidation;
import org.knime.node.parameters.widget.text.TextInputWidget;

/**
 * Settings for the String Widget node.
 *
 * @author AI Migration
 */
@SuppressWarnings("restriction")
public final class StringWidgetNodeSettings implements NodeParameters {

    @Widget(title = "Label", description = "A descriptive label that will be shown in the dialog")
    @Layout(WidgetNodeSettingsBase.CommonSections.LabelSection.class)
    @TextInputWidget
    @Persist(configKey = "label")
    String m_label = "Label";

    @Widget(title = "Description", description = "Some lines of description that will be shown for instance in the node description of the component exposing a dialog")
    @Layout(WidgetNodeSettingsBase.CommonSections.LabelSection.class)
    @TextInputWidget
    @Persist(configKey = "description")
    String m_description = "Enter Description";

    @Widget(title = "Parameter Name", description = "Parameter identifier for external parameterization (e.g. batch execution). This will also be the name of the exported flow variable.")
    @Layout(WidgetNodeSettingsBase.CommonSections.ParameterSection.class)
    @TextInputWidget
    @Persist(configKey = "flowVariableName")
    String m_parameter = "string-input";

    @Widget(title = "Required", description = "If checked, the widget must be configured before the workflow can be executed")
    @Layout(WidgetNodeSettingsBase.CommonSections.ParameterSection.class)
    @Persist(configKey = "required")
    boolean m_required = true;

    @Widget(title = "Default Value", description = "The value that is selected by default")
    @Layout(WidgetNodeSettingsBase.CommonSections.ParameterSection.class)
    @TextInputWidget
    @Persistor(CommonWidgetPersistors.StringDefaultValuePersistor.class)
    String m_defaultValue = "";

    @Widget(title = "Hide in Dialog", description = "If the widget is hidden, it cannot be shown in the dialog, and becomes unreachable.")
    @Layout(WidgetNodeSettingsBase.CommonSections.ParameterSection.class)
    @Persist(configKey = "hideInWizard")
    boolean m_hideInWizard = false;

    @Widget(title = "Custom CSS", description = "Enter custom CSS styling for this widget")
    @Layout(WidgetNodeSettingsBase.CommonSections.InputSection.class)
    @TextInputWidget
    @Persist(configKey = "customCSS")
    String m_customCSS = "";

    @Widget(title = "Regular Expression", description = "A regular expression to validate the input. If left empty, no validation is performed.")
    @Layout(WidgetNodeSettingsBase.CommonSections.ValidationSection.class)
    @TextInputWidget
    @Persist(configKey = "regex")
    String m_regex = "";

    @Widget(title = "Error Message", description = "Error message to display when the input doesn't match the regular expression. The character ? will be replaced by the current (invalid) value.")
    @Layout(WidgetNodeSettingsBase.CommonSections.ValidationSection.class)
    @TextInputWidget
    @Persist(configKey = "error_message")
    String m_errorMessage = "";

    enum EditorType {
        @Label("Single-line")
        SINGLE_LINE("Single-line"),
        @Label("Multi-line")
        MULTI_LINE("Multi-line");

        private final String m_text;

        EditorType(final String text) {
            m_text = text;
        }

        String getText() {
            return m_text;
        }
    }

    @Widget(title = "Editor Type", description = "Type of the string input editor")
    @Layout(WidgetNodeSettingsBase.CommonSections.EditorSection.class)
    @RadioButtonsWidget(horizontal = true)
    @Persistor(EditorTypePersistor.class)
    @ValueReference(EditorTypeRef.class)
    EditorType m_editorType = EditorType.SINGLE_LINE;

    interface EditorTypeRef extends ParameterReference<EditorType> {
    }

    @Widget(title = "Editor Width", description = "The width of the text editor in characters (only applies to multi-line editor)")
    @Layout(WidgetNodeSettingsBase.CommonSections.EditorSection.class)
    @NumberInputWidget(minValidation = IsPositiveIntegerValidation.class)
    @Persist(configKey = "multilineEditorWidth")
    int m_multilineEditorWidth = 60;

    @Widget(title = "Editor Height", description = "The height of the text editor in lines (only applies to multi-line editor)")
    @Layout(WidgetNodeSettingsBase.CommonSections.EditorSection.class)
    @NumberInputWidget(minValidation = IsPositiveIntegerValidation.class)
    @Persist(configKey = "multilineEditorHeight")
    int m_multilineEditorHeight = 5;

    /**
     * Custom persistor for editor type that maps to the legacy "editorType" config key.
     */
    static final class EditorTypePersistor implements NodeParametersPersistor<EditorType> {

        @Override
        public EditorType load(final NodeSettingsRO settings) throws InvalidSettingsException {
            String editorType = settings.getString("editorType", StringNodeConfig.DEFAULT_EDITOR_TYPE);
            return editorType.equals(StringNodeConfig.EDITOR_TYPE_MULTI_LINE_STRING)
                ? EditorType.MULTI_LINE : EditorType.SINGLE_LINE;
        }

        @Override
        public void save(final EditorType obj, final NodeSettingsWO settings) {
            String editorTypeString = obj == EditorType.MULTI_LINE
                ? StringNodeConfig.EDITOR_TYPE_MULTI_LINE_STRING
                : StringNodeConfig.EDITOR_TYPE_SINGLE_LINE_STRING;
            settings.addString("editorType", editorTypeString);
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][] {{"editorType"}};
        }
    }

    /**
     * Custom persistor for default value that maps to the legacy nested "defaultValue" config structure.
     * NOTE: This class is now deprecated in favor of CommonWidgetPersistors.StringDefaultValuePersistor
     * but is kept here for reference and compatibility.
     *
     * @deprecated Use {@link CommonWidgetPersistors.StringDefaultValuePersistor} instead
     */
    @Deprecated
    static final class DefaultValuePersistor implements NodeParametersPersistor<String> {

        @Override
        public String load(final NodeSettingsRO settings) throws InvalidSettingsException {
            if (settings.containsKey("defaultValue")) {
                NodeSettingsRO defaultValueSettings = settings.getNodeSettings("defaultValue");
                return defaultValueSettings.getString("string", "");
            }
            return "";
        }

        @Override
        public void save(final String obj, final NodeSettingsWO settings) {
            NodeSettingsWO defaultValueSettings = settings.addNodeSettings("defaultValue");
            defaultValueSettings.addString("string", obj != null ? obj : "");
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][] {{"defaultValue"}};
        }
    }

    /**
     * Predicate provider for showing multi-line editor specific options.
     */
}
