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
package org.knime.js.base.node.widget.selection.single;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.base.node.widget.util.CommonWidgetPersistors;
import org.knime.js.base.node.widget.util.WidgetNodeSettingsBase;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.After;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.layout.Section;
import org.knime.node.parameters.persistence.NodeParametersPersistor;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.persistence.Persistor;
import org.knime.node.parameters.updates.ValueReference;
import org.knime.node.parameters.updates.util.BooleanReference;
import org.knime.node.parameters.widget.choices.ChoicesProvider;
import org.knime.node.parameters.widget.choices.Label;
import org.knime.node.parameters.widget.choices.RadioButtonsWidget;
import org.knime.node.parameters.widget.choices.util.AllColumnsProvider;
import org.knime.node.parameters.widget.number.NumberInputWidget;
import org.knime.node.parameters.widget.number.NumberInputWidgetValidation.MinValidation.IsNonNegativeValidation;
import org.knime.node.parameters.widget.text.TextAreaWidget;
import org.knime.node.parameters.widget.text.TextInputWidget;

/**
 * Settings for the Single Selection Widget node.
 *
 * @author AI Migration
 */
@SuppressWarnings("restriction")
public final class SingleSelectionWidgetNodeSettings implements NodeParameters {

    @Section(title = "Selection Configuration")
    @After(WidgetNodeSettingsBase.CommonSections.ValidationSection.class)
    interface SelectionSection {
    }

    @Widget(title = "Label", description = "A descriptive label that will be shown in the dialog")
    @Layout(WidgetNodeSettingsBase.CommonSections.LabelSection.class)
    @TextInputWidget
    @Persist(configKey = "label")
    String m_label = "Label";

    @Widget(title = "Description", description = "Some lines of description that will be shown for instance in the node description of the component exposing a dialog")
    @Layout(WidgetNodeSettingsBase.CommonSections.LabelSection.class)
    @TextAreaWidget
    @Persist(configKey = "description")
    String m_description = "Enter Description";

    @Widget(title = "Parameter Name", description = "Parameter identifier for external parameterization (e.g. batch execution). This will also be the name of the exported flow variable.")
    @Layout(WidgetNodeSettingsBase.CommonSections.ParameterSection.class)
    @TextInputWidget
    @Persist(configKey = "flowVariableName")
    String m_parameter = "single-selection";

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
    @TextAreaWidget
    @Persist(configKey = "customCSS")
    String m_customCSS = "";

    // Selection type and source
    enum SelectionType {
        @Label("List")
        LIST("List"),
        @Label("Dropdown")
        DROPDOWN("Dropdown"),
        @Label("Radio buttons (vertical)")
        RADIO_VERTICAL("Radio (Vertical)"),
        @Label("Radio buttons (horizontal)")
        RADIO_HORIZONTAL("Radio (Horizontal)");

        private final String m_text;

        SelectionType(final String text) {
            m_text = text;
        }

        String getText() {
            return m_text;
        }
    }

    @Widget(title = "Selection Type", description = "The type of selection widget to display")
    @Layout(SelectionSection.class)
    @RadioButtonsWidget
    @Persist(configKey = "type")
    SelectionType m_selectionType = SelectionType.DROPDOWN;

    interface UseColumnRef extends BooleanReference {
    }

    @Widget(title = "Use values from table column", description = "Use unique values from a table column as selection options")
    @Layout(SelectionSection.class)
    @Persist(configKey = "useColumn")
    @ValueReference(UseColumnRef.class)
    boolean m_useColumn = false;

    @Widget(title = "Column", description = "The column to use for selection values")
    @Layout(SelectionSection.class)
    @ChoicesProvider(AllColumnsProvider.class)
    @Persist(configKey = "column")
    String m_column = "";

    @Widget(title = "Possible Values", description = "The list of possible values for selection (one per line)")
    @Layout(SelectionSection.class)
    @TextAreaWidget
    @Persistor(PossibleValuesPersistor.class)
    String m_possibleValues = "Value 1\nValue 2\nValue 3";

    @Widget(title = "Number of visible options", description = "The number of options visible in list mode (0 for all)")
    @Layout(SelectionSection.class)
    @NumberInputWidget(minValidation = IsNonNegativeValidation.class)
    @Persist(configKey = "limitNumberVisOptions")
    int m_numberVisibleOptions = 10;

    /**
     * Custom persistor for possible values that handles the legacy array structure.
     */
    static final class PossibleValuesPersistor implements NodeParametersPersistor<String> {

        @Override
        public String load(final NodeSettingsRO settings) throws InvalidSettingsException {
            if (settings.containsKey("possibleValues")) {
                String[] values = settings.getStringArray("possibleValues");
                return String.join("\n", values);
            }
            return "Value 1\nValue 2\nValue 3";
        }

        @Override
        public void save(final String obj, final NodeSettingsWO settings) {
            if (obj != null && !obj.trim().isEmpty()) {
                String[] values = obj.split("\n");
                settings.addStringArray("possibleValues", values);
            } else {
                settings.addStringArray("possibleValues", new String[]{"Value 1", "Value 2", "Value 3"});
            }
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][] {{"possibleValues"}};
        }
    }

    /**
     * Predicate for showing column selection when "Use values from table column" is checked.
     */

    /**
     * Predicate for showing manual values when "Use values from table column" is NOT checked.
     */
}
