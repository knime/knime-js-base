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
package org.knime.js.base.node.widget.selection.multiple;

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
import org.knime.node.parameters.widget.choices.Label;
import org.knime.node.parameters.widget.choices.RadioButtonsWidget;
import org.knime.node.parameters.widget.choices.filter.TwinlistWidget;
import org.knime.node.parameters.widget.number.NumberInputWidget;
import org.knime.node.parameters.widget.text.TextAreaWidget;
import org.knime.node.parameters.widget.text.TextInputWidget;

/**
 * Settings for the Multiple Selection Widget node.
 *
 * @author AI Migration
 */
@SuppressWarnings("restriction")
public final class MultipleSelectionWidgetNodeSettings implements NodeParameters {

    @Section(title = "Multiple Selection Configuration")
    @After(WidgetNodeSettingsBase.CommonSections.ValidationSection.class)
    interface SelectionSection {
    }

    @Widget(title = "Label", description = "A descriptive label that will be shown in the dialog")
    @Layout(WidgetNodeSettingsBase.CommonSections.LabelSection.class)
    @TextInputWidget
    @Persist(configKey = "label")
    String m_label = "Multiple Selection";

    @Widget(title = "Description", description = "Some lines of description that will be shown for instance in the node description of the component exposing a dialog")
    @Layout(WidgetNodeSettingsBase.CommonSections.LabelSection.class)
    @TextAreaWidget
    @Persist(configKey = "description")
    String m_description = "Enter Description";

    @Widget(title = "Parameter Name", description = "Parameter identifier for external parameterization (e.g. batch execution).")
    @Layout(WidgetNodeSettingsBase.CommonSections.ParameterSection.class)
    @TextInputWidget
    @Persist(configKey = "flowVariableName")
    String m_parameter = "multiple-selection";

    @Widget(title = "Hide in Dialog", description = "If the widget is hidden, it cannot be shown in the dialog, and becomes unreachable.")
    @Layout(WidgetNodeSettingsBase.CommonSections.ParameterSection.class)
    @Persist(configKey = "hideInWizard")
    boolean m_hideInWizard = false;

    @Widget(title = "Custom CSS", description = "Enter custom CSS styling for this widget")
    @Layout(WidgetNodeSettingsBase.CommonSections.InputSection.class)
    @TextAreaWidget
    @Persist(configKey = "customCSS")
    String m_customCSS = "";

    // Multiple selection specific settings
    enum SelectionType {
        @Label("List")
        LIST("List"),
        @Label("Twinlist")
        TWINLIST("Twinlist"),
        @Label("Combobox")
        COMBOBOX("Combobox"),
        @Label("Check boxes (horizontal)")
        CHECK_BOXES_HORIZONTAL("Check boxes (horizontal)"),
        @Label("Check boxes (vertical)")
        CHECK_BOXES_VERTICAL("Check boxes (vertical)");

        private final String m_text;

        SelectionType(final String text) {
            m_text = text;
        }

        String getText() {
            return m_text;
        }
    }

    @Widget(title = "Selection Type", description = "Type of UI component for multiple selection")
    @Layout(SelectionSection.class)
    @RadioButtonsWidget
    @Persistor(SelectionTypePersistor.class)
    SelectionType m_selectionType = SelectionType.TWINLIST;

    @Widget(title = "Possible Choices", description = "Enter possible choices, one per line")
    @Layout(SelectionSection.class)
    @TextAreaWidget
    @Persistor(PossibleChoicesPersistor.class)
    String m_possibleChoices = "";

    @Widget(title = "Default Values", description = "Default selected values")
    @Layout(SelectionSection.class)
    @TwinlistWidget
    @Persistor(DefaultValuesPersistor.class)
    String[] m_defaultValues = new String[0];

    @Widget(title = "Enable Search", description = "Enable search functionality for the selection component")
    @Layout(SelectionSection.class)
    @Persist(configKey = "enableSearch")
    boolean m_enableSearch = false;

    @Widget(title = "Ignore Missing Selected Values", description = "If selected values are missing from possible choices, ignore them instead of showing an error")
    @Layout(SelectionSection.class)
    @Persist(configKey = "omitInvalid")
    boolean m_omitInvalid = false;

    @Widget(title = "Limit number of visible options", description = "If enabled, limit the number of visible options")
    @Layout(SelectionSection.class)
    @Persist(configKey = "limitNumberVisOptions")
    boolean m_limitNumberVisOptions = false;

    @Widget(title = "Number of visible options", description = "Number of visible options")
    @Layout(SelectionSection.class)
    @NumberInputWidget
    @Persist(configKey = "numberVisOptions")
    int m_numberVisOptions = 5;

    static class SelectionTypePersistor implements NodeParametersPersistor<SelectionType> {
        @Override
        public SelectionType load(final NodeSettingsRO settings) throws InvalidSettingsException {
            String type = settings.getString("type", "Twinlist");
            switch (type) {
                case "List":
                    return SelectionType.LIST;
                case "Combobox":
                    return SelectionType.COMBOBOX;
                case "Check boxes (horizontal)":
                    return SelectionType.CHECK_BOXES_HORIZONTAL;
                case "Check boxes (vertical)":
                    return SelectionType.CHECK_BOXES_VERTICAL;
                default:
                    return SelectionType.TWINLIST;
            }
        }

        @Override
        public void save(final SelectionType value, final NodeSettingsWO settings) {
            settings.addString("type", value.getText());
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][]{{"type"}};
        }
    }

    static class PossibleChoicesPersistor implements NodeParametersPersistor<String> {
        @Override
        public String load(final NodeSettingsRO settings) throws InvalidSettingsException {
            String[] choices = settings.getStringArray("possibleChoices", new String[0]);
            return String.join("\n", choices);
        }

        @Override
        public void save(final String value, final NodeSettingsWO settings) {
            String[] choices = value != null ? value.split("\n") : new String[0];
            settings.addStringArray("possibleChoices", choices);
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][]{{"possibleChoices"}};
        }
    }

    static class DefaultValuesPersistor implements NodeParametersPersistor<String[]> {
        @Override
        public String[] load(final NodeSettingsRO settings) throws InvalidSettingsException {
            try {
                NodeSettingsRO defaultSettings = settings.getNodeSettings("defaultValue");
                return defaultSettings.getStringArray("variableValue", new String[0]);
            } catch (InvalidSettingsException e) {
                return new String[0];
            }
        }

        @Override
        public void save(final String[] value, final NodeSettingsWO settings) {
            NodeSettingsWO defaultSettings = settings.addNodeSettings("defaultValue");
            defaultSettings.addStringArray("variableValue", value != null ? value : new String[0]);
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][] {{"defaultValue"}};
        }
    }
}
