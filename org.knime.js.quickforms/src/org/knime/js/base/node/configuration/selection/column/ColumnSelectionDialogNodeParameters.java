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
 *   10 Sept 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.configuration.selection.column;

import static org.knime.js.base.node.configuration.selection.SelectionNodeParametersUtil.LIMIT_VIS_OPT_DESCRIPTION;
import static org.knime.js.base.node.configuration.selection.SelectionNodeParametersUtil.LIMIT_VIS_OPT_TITLE;
import static org.knime.js.base.node.configuration.selection.SelectionNodeParametersUtil.NUM_VIS_OPT_DESCRIPTION;
import static org.knime.js.base.node.configuration.selection.SelectionNodeParametersUtil.NUM_VIS_OPT_TITLE;
import static org.knime.js.base.node.configuration.selection.SelectionNodeParametersUtil.SELECTION_TYPE_DESCRIPTION;
import static org.knime.js.base.node.configuration.selection.SelectionNodeParametersUtil.SELECTION_TYPE_TITLE;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.function.Supplier;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.webui.node.dialog.defaultdialog.util.updates.StateComputationFailureException;
import org.knime.js.base.node.base.selection.column.ColumnSelectionNodeConfig;
import org.knime.js.base.node.base.selection.column.ColumnSelectionNodeValue;
import org.knime.js.base.node.base.validation.InputSpecFilter;
import org.knime.js.base.node.configuration.ConfigurationNodeParametersUtility.IsMin2Validation;
import org.knime.js.base.node.configuration.ConfigurationNodeSettings;
import org.knime.js.base.node.configuration.OverwrittenByValueMessage;
import org.knime.js.base.node.configuration.column.InputFilterUtil.AllowAllTypesValueReference;
import org.knime.js.base.node.configuration.column.InputFilterUtil.HideColumnsWithoutDomainValueReference;
import org.knime.js.base.node.configuration.column.InputFilterUtil.InputFilter;
import org.knime.js.base.node.configuration.column.InputFilterUtil.TypeFilterSection;
import org.knime.js.base.node.configuration.column.InputFilterUtil.TypeFilterValueReference;
import org.knime.js.base.node.configuration.selection.SelectionNodeParametersUtil.IsListSelectionType;
import org.knime.js.base.node.configuration.selection.SelectionNodeParametersUtil.LimitNumberOfVisibleOptionsValueReference;
import org.knime.js.base.node.configuration.selection.SelectionNodeParametersUtil.SelectionTypeChoicesProvider;
import org.knime.js.base.node.configuration.selection.SelectionNodeParametersUtil.SelectionTypeValueReference;
import org.knime.js.base.node.configuration.selection.SelectionNodeParametersUtil.ShowNumberOfVisibleOptions;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.NodeParametersInput;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.migration.LoadDefaultsForAbsentFields;
import org.knime.node.parameters.persistence.NodeParametersPersistor;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.persistence.Persistor;
import org.knime.node.parameters.updates.Effect;
import org.knime.node.parameters.updates.Effect.EffectType;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.updates.StateProvider;
import org.knime.node.parameters.updates.ValueProvider;
import org.knime.node.parameters.updates.ValueReference;
import org.knime.node.parameters.widget.choices.ChoicesProvider;
import org.knime.node.parameters.widget.choices.ColumnChoicesProvider;
import org.knime.node.parameters.widget.choices.TypedStringChoice;
import org.knime.node.parameters.widget.message.TextMessage;
import org.knime.node.parameters.widget.number.NumberInputWidget;

/**
 * WebUI Node Parameters for the Column Selection Configuration.
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz
 */
@LoadDefaultsForAbsentFields
public class ColumnSelectionDialogNodeParameters extends ConfigurationNodeSettings {

    /**
     * Default constructor
     */
    protected ColumnSelectionDialogNodeParameters() {
        super(ColumnSelectionDialogNodeConfig.class);
    }

    @TextMessage(ColumnSelectionOverwrittenByValueMessage.class)
    @Layout(OutputSection.Top.class)
    Void m_overwrittenByValueMessage;

    private static final class DefaultValue implements NodeParameters {
        @Widget(title = "Default value",
            description = "Default value for the field. If you want to use a value that is not among the possible"
                + " choices as the default, override the field using a flow variable.")
        @Layout(OutputSection.Top.class)
        @Persistor(DefaultColumnValuePersistor.class)
        @ChoicesProvider(PossibleColumnChoicesProvider.class)
        @ValueProvider(ColumnValueValueProvider.class)
        @ValueReference(ColumnValueValueReference.class)
        String m_columnValue;
    }

    DefaultValue m_defaultValue = new DefaultValue();

    @Widget(title = SELECTION_TYPE_TITLE, description = SELECTION_TYPE_DESCRIPTION)
    @ChoicesProvider(SelectionTypeChoicesProvider.class)
    @Persist(configKey = ColumnSelectionNodeConfig.CFG_TYPE)
    @ValueReference(SelectionTypeValueReference.class)
    @Layout(FormFieldSection.class)
    String m_selectionType = ColumnSelectionNodeConfig.DEFAULT_TYPE;

    @Widget(title = LIMIT_VIS_OPT_TITLE, description = LIMIT_VIS_OPT_DESCRIPTION)
    @Persist(configKey = ColumnSelectionNodeConfig.CFG_LIMIT_NUMBER_VIS_OPTIONS)
    @ValueReference(LimitNumberOfVisibleOptionsValueReference.class)
    @Effect(predicate = IsListSelectionType.class, type = EffectType.SHOW)
    @Layout(FormFieldSection.class)
    boolean m_limitNumberOfVisibleOptions = ColumnSelectionNodeConfig.DEFAULT_LIMIT_NUMBER_VIS_OPTIONS;

    @Widget(title = NUM_VIS_OPT_TITLE, description = NUM_VIS_OPT_DESCRIPTION)
    @NumberInputWidget(minValidation = IsMin2Validation.class)
    @Persist(configKey = ColumnSelectionNodeConfig.CFG_NUMBER_VIS_OPTIONS)
    @Effect(predicate = ShowNumberOfVisibleOptions.class, type = EffectType.SHOW)
    @Layout(FormFieldSection.class)
    int m_numberOfVisibleOptions = ColumnSelectionNodeConfig.DEFAULT_NUMBER_VIS_OPTIONS;

    @ChoicesProvider(PossibleColumnChoicesProvider.class)
    @Persist(configKey = ColumnSelectionNodeConfig.CFG_POSSIBLE_COLUMNS)
    String[] m_possibleColumns = new String[0];

    @Layout(TypeFilterSection.class)
    @Persist(configKey = ColumnSelectionDialogNodeConfig.CFG_INPUT_FILTER)
    InputFilter m_inputFilter = new InputFilter();

    private static final class DefaultColumnValuePersistor implements NodeParametersPersistor<String> {

        @Override
        public String load(final NodeSettingsRO settings) throws InvalidSettingsException {
            return settings.getString(ColumnSelectionNodeValue.CFG_COLUMN, "");
        }

        @Override
        public void save(final String param, final NodeSettingsWO settings) {
            final var valueToSave = param == null ? "" : param;
            settings.addString(ColumnSelectionNodeValue.CFG_COLUMN, valueToSave);
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][]{{ColumnSelectionNodeValue.CFG_COLUMN}};
        }

    }

    private static final class ColumnValueValueReference implements ParameterReference<String> {
    }

    static final class PossibleColumnChoicesProvider implements ColumnChoicesProvider {

        private Supplier<Boolean> m_hideColumnsWithoutDomainSupplier;

        private Supplier<Boolean> m_allowAllTypesSupplier;

        private Supplier<String[]> m_typeFilterSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            ColumnChoicesProvider.super.init(initializer);
            m_hideColumnsWithoutDomainSupplier =
                initializer.computeFromValueSupplier(HideColumnsWithoutDomainValueReference.class);
            m_allowAllTypesSupplier = initializer.computeFromValueSupplier(AllowAllTypesValueReference.class);
            m_typeFilterSupplier = initializer.computeFromValueSupplier(TypeFilterValueReference.class);
        }

        @Override
        public List<DataColumnSpec> columnChoices(final NodeParametersInput context) {
            final var tableSpec = context.getInTableSpec(0);
            if (tableSpec.isEmpty()) {
                return List.of();
            }

            final var selectedTypes = new HashSet<>(Arrays.asList(m_typeFilterSupplier.get()));
            return tableSpec.get().stream()
                .filter(colSpec -> m_allowAllTypesSupplier.get()
                    || selectedTypes.contains(colSpec.getType().getPreferredValueClass().getName()))
                .filter(colSpec -> !m_hideColumnsWithoutDomainSupplier.get() || !InputSpecFilter.hasNoDomain(colSpec))
                .toList();
        }
    }

    private static final class ColumnValueValueProvider implements StateProvider<String> {

        private Supplier<String> m_columnValueSupplier;

        private Supplier<List<TypedStringChoice>> m_possibleColumnChoicesSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            initializer.computeAfterOpenDialog();
            m_columnValueSupplier = initializer.getValueSupplier(ColumnValueValueReference.class);
            m_possibleColumnChoicesSupplier = initializer.computeFromProvidedState(PossibleColumnChoicesProvider.class);
        }

        @Override
        public String computeState(final NodeParametersInput parametersInput) throws StateComputationFailureException {
            final var possibleColumnChoices = m_possibleColumnChoicesSupplier.get();
            if (possibleColumnChoices.isEmpty()) {
                return null;
            }
            final var columnValue = m_columnValueSupplier.get();
            final var possibleColumnChoicesIds = possibleColumnChoices.stream().map(TypedStringChoice::id).toList();

            if (columnValue != null && possibleColumnChoicesIds.contains(columnValue)) {
                return columnValue;
            }
            return possibleColumnChoicesIds.get(0);
        }

    }

    private static final class ColumnSelectionOverwrittenByValueMessage
        extends OverwrittenByValueMessage<ColumnSelectionDialogNodeValue> {

        @Override
        protected String valueToString(final ColumnSelectionDialogNodeValue value) {
            return value.getColumn();
        }

    }

}
