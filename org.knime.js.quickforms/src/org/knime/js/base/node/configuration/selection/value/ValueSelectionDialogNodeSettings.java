/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime    @Widget(title = "Limit number of visible options", description = "By default the List component adjusts its height to display all possible choices without a scroll bar. If the setting is enabled, you will be able to limit the number of visible options in case you have too many of them. The setting is available only for List selection type.")
    @Layout(DisplaySection.class)
    @Effect(predicate = ListSelectionPredicate.class, type = EffectType.SHOW)
    @Persist(configKey = "limit_number_visible_options")
    boolean m_limitNumberVisibleOptions = false;*  This program is free software; you can redistribute it and/or modify
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
package org.knime.js.base.node.configuration.selection.value;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.IntValue;
import org.knime.core.data.StringValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.base.dialog.selection.single.SingleSelectionComponentFactory;
import org.knime.js.base.node.base.selection.value.ColumnType;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.After;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.layout.Section;
import org.knime.node.parameters.persistence.NodeParametersPersistor;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.persistence.Persistor;
import org.knime.node.parameters.updates.Effect;
import org.knime.node.parameters.updates.Effect.EffectType;
import org.knime.node.parameters.updates.EffectPredicate;
import org.knime.node.parameters.updates.EffectPredicateProvider;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.updates.ValueReference;
import org.knime.node.parameters.widget.choices.ChoicesProvider;
import org.knime.node.parameters.widget.choices.Label;
import org.knime.node.parameters.widget.choices.RadioButtonsWidget;
import org.knime.node.parameters.widget.choices.util.FilteredInputTableColumnsProvider;
import org.knime.node.parameters.widget.number.NumberInputWidget;
import org.knime.node.parameters.widget.text.TextInputWidget;

/**
 * Settings for the Value Selection Configuration node.
 *
 * @author AI Migration
 */
@SuppressWarnings("restriction")
public final class ValueSelectionDialogNodeSettings implements NodeParameters {

    @Section(title = "Label and Description")
    interface LabelSection {
    }

    @Section(title = "Parameter")
    @After(LabelSection.class)
    interface ParameterSection {
    }

    @Section(title = "Selection Configuration")
    @After(ParameterSection.class)
    interface SelectionSection {
    }

    @Section(title = "Display Options")
    @After(SelectionSection.class)
    interface DisplaySection {
    }

    @Widget(title = "Label", description = "A descriptive label that will be shown in the dialog")
    @Layout(LabelSection.class)
    @TextInputWidget
    @Persist(configKey = "label")
    String m_label = "Value Selection";

    @Widget(title = "Description", description = "Some lines of description that will be shown for instance in the node description of the component exposing a dialog")
    @Layout(LabelSection.class)
    @TextInputWidget
    @Persist(configKey = "description")
    String m_description = "";

    @Widget(title = "Parameter Name", description = "Parameter identifier for external parameterization (e.g. batch execution). This will also be the name of the exported flow variable.")
    @Layout(ParameterSection.class)
    @TextInputWidget
    @Persist(configKey = "flowVariableName")
    String m_parameter = "value-selection";

    enum SelectionType {
        @Label("Dropdown")
        DROPDOWN("Dropdown"),
        @Label("List")
        LIST("List"),
        @Label("Radio buttons (vertical)")
        RADIO_VERTICAL("Radio buttons (vertical)"),
        @Label("Radio buttons (horizontal)")
        RADIO_HORIZONTAL("Radio buttons (horizontal)");

        private final String m_text;

        SelectionType(final String text) {
            m_text = text;
        }

        String getText() {
            return m_text;
        }
    }

    @Widget(title = "Selection Type", description = "Type of the selection panel")
    @Layout(SelectionSection.class)
    @RadioButtonsWidget(horizontal = true)
    @Persistor(SelectionTypePersistor.class)
    @ValueReference(SelectionTypeRef.class)
    SelectionType m_selectionType = SelectionType.DROPDOWN;

    interface SelectionTypeRef extends ParameterReference<SelectionType> {
    }

    @Widget(title = "Column Type", description = "The type of columns that can be selected")
    @Layout(SelectionSection.class)
    @Persist(configKey = "columnType")
    ColumnType m_columnType = ColumnType.All;

    @Widget(title = "Lock Column", description = "If selected the column is locked and can not be selected from the component dialog")
    @Layout(SelectionSection.class)
    @Persist(configKey = "lockColumn")
    boolean m_lockColumn = false;

    @Widget(title = "Limit number of visible options", description = "By default the List component adjusts its height to display all possible choices without a scroll bar. If the setting is enabled, you will be able to limit the number of visible options in case you have too many of them. The setting is available only for List selection type.")
    @Layout(DisplaySection.class)
    @Effect(predicate = ListSelectionPredicate.class, type = EffectType.SHOW)
    @Persist(configKey = "limit_number_visible_options")
    boolean m_limitNumberVisibleOptions = false;

    @Widget(title = "Number of visible options", description = "A number of options visible in the List component without a vertical scroll bar. Changing this value will also affect the component's height. The setting is available only for List selection type.")
    @Layout(DisplaySection.class)
    @NumberInputWidget
    @Effect(predicate = ListSelectionPredicate.class, type = EffectType.SHOW)
    @Persist(configKey = "number_visible_options")
    int m_numberVisibleOptions = 5;

    /**
     * Custom persistor for selection type that maps to the legacy "type" config key.
     */
    static final class SelectionTypePersistor implements NodeParametersPersistor<SelectionType> {

        @Override
        public SelectionType load(final NodeSettingsRO settings) throws InvalidSettingsException {
            String type = settings.getString("type", SingleSelectionComponentFactory.DROPDOWN);

            switch (type) {
                case SingleSelectionComponentFactory.LIST:
                    return SelectionType.LIST;
                case SingleSelectionComponentFactory.RADIO_BUTTONS_VERTICAL:
                    return SelectionType.RADIO_VERTICAL;
                case SingleSelectionComponentFactory.RADIO_BUTTONS_HORIZONTAL:
                    return SelectionType.RADIO_HORIZONTAL;
                case SingleSelectionComponentFactory.DROPDOWN:
                default:
                    return SelectionType.DROPDOWN;
            }
        }

        @Override
        public void save(final SelectionType obj, final NodeSettingsWO settings) {
            String legacyType;
            switch (obj) {
                case LIST:
                    legacyType = SingleSelectionComponentFactory.LIST;
                    break;
                case RADIO_VERTICAL:
                    legacyType = SingleSelectionComponentFactory.RADIO_BUTTONS_VERTICAL;
                    break;
                case RADIO_HORIZONTAL:
                    legacyType = SingleSelectionComponentFactory.RADIO_BUTTONS_HORIZONTAL;
                    break;
                case DROPDOWN:
                default:
                    legacyType = SingleSelectionComponentFactory.DROPDOWN;
                    break;
            }
            settings.addString("type", legacyType);
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][]{{"type"}};
        }
    }

    /**
     * Choices provider for columns compatible with the selected column type.
     */
    static final class ColumnChoicesProvider implements FilteredInputTableColumnsProvider {
        @Override
        public boolean isIncluded(final DataColumnSpec col) {
            // Show all columns that are compatible with string, int, or double values
            return col.getType().isCompatible(StringValue.class)
                || col.getType().isCompatible(IntValue.class)
                || col.getType().isCompatible(DoubleValue.class);
        }

        @Override
        public int getInputTableIndex() {
            return 0;
        }
    }

    /**
     * Predicate provider for showing List-specific options.
     */
    public static class ListSelectionPredicate implements EffectPredicateProvider {
        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            return i.getEnum(SelectionTypeRef.class).isOneOf(SelectionType.LIST);
        }
    }
}
