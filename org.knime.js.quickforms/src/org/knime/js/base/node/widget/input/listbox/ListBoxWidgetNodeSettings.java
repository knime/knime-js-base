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
package org.knime.js.base.node.widget.input.listbox;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.base.node.widget.util.WidgetNodeSettingsBase;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.After;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.layout.Section;
import org.knime.node.parameters.persistence.NodeParametersPersistor;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.persistence.Persistor;
import org.knime.node.parameters.widget.number.NumberInputWidget;
import org.knime.node.parameters.widget.text.TextAreaWidget;
import org.knime.node.parameters.widget.text.TextInputWidget;

/**
 * Settings for the List Box Widget node.
 *
 * @author AI Migration
 */
@SuppressWarnings("restriction")
public final class ListBoxWidgetNodeSettings implements NodeParameters {

    @Section(title = "List Box Configuration")
    @After(WidgetNodeSettingsBase.CommonSections.ValidationSection.class)
    interface ListBoxSection {
    }

    @Widget(title = "Label", description = "A descriptive label that will be shown in the dialog")
    @Layout(WidgetNodeSettingsBase.CommonSections.LabelSection.class)
    @TextInputWidget
    @Persist(configKey = "label")
    String m_label = "List Box";

    @Widget(title = "Description", description = "Some lines of description that will be shown for instance in the node description of the component exposing a dialog")
    @Layout(WidgetNodeSettingsBase.CommonSections.LabelSection.class)
    @TextAreaWidget
    @Persist(configKey = "description")
    String m_description = "Enter Description";

    @Widget(title = "Parameter Name", description = "Parameter identifier for external parameterization (e.g. batch execution).")
    @Layout(WidgetNodeSettingsBase.CommonSections.ParameterSection.class)
    @TextInputWidget
    @Persist(configKey = "flowVariableName")
    String m_parameter = "list-box";

    @Widget(title = "Hide in Dialog", description = "If the widget is hidden, it cannot be shown in the dialog, and becomes unreachable.")
    @Layout(WidgetNodeSettingsBase.CommonSections.ParameterSection.class)
    @Persist(configKey = "hideInWizard")
    boolean m_hideInWizard = false;

    @Widget(title = "Custom CSS", description = "Enter custom CSS styling for this widget")
    @Layout(WidgetNodeSettingsBase.CommonSections.InputSection.class)
    @TextAreaWidget
    @Persist(configKey = "customCSS")
    String m_customCSS = "";

    // List box specific settings
    @Widget(title = "Separator", description = "Character or string used to separate values")
    @Layout(ListBoxSection.class)
    @TextInputWidget
    @Persist(configKey = "separator")
    String m_separator = ",";

    @Widget(title = "Separate at each character", description = "If enabled, separate the input at each character instead of using a separator")
    @Layout(ListBoxSection.class)
    @Persist(configKey = "separateEachCharacter")
    boolean m_separateEachCharacter = false;

    @Widget(title = "Omit Empty Values", description = "If enabled, omit empty values from the list")
    @Layout(ListBoxSection.class)
    @Persist(configKey = "omitEmpty")
    boolean m_omitEmpty = false;

    @Widget(title = "Regular Expression", description = "Regular expression pattern for validation")
    @Layout(ListBoxSection.class)
    @TextInputWidget
    @Persist(configKey = "regex")
    String m_regex = "";

    @Widget(title = "Validation Error Message", description = "Error message to display when validation fails")
    @Layout(ListBoxSection.class)
    @TextInputWidget
    @Persist(configKey = "errorMessage")
    String m_errorMessage = "";

    @Widget(title = "Default Value", description = "Default text value for the list box")
    @Layout(ListBoxSection.class)
    @TextAreaWidget
    @Persistor(DefaultValuePersistor.class)
    String m_defaultValue = "";

    @Widget(title = "Number of visible options", description = "Number of visible options in the list box")
    @Layout(ListBoxSection.class)
    @NumberInputWidget
    @Persist(configKey = "numberVisOptions")
    int m_numberVisOptions = 5;


    static class DefaultValuePersistor implements NodeParametersPersistor<String> {
        @Override
        public String load(final NodeSettingsRO settings) throws InvalidSettingsException {
            try {
                NodeSettingsRO defaultSettings = settings.getNodeSettings("defaultValue");
                return defaultSettings.getString("string", "");
            } catch (InvalidSettingsException e) {
                return "";
            }
        }

        @Override
        public void save(final String value, final NodeSettingsWO settings) {
            NodeSettingsWO defaultSettings = settings.addNodeSettings("defaultValue");
            defaultSettings.addString("string", value != null ? value : "");
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][] {{"defaultValue"}};
        }
    }
}
