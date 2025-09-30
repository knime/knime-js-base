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
 *   15 Sept 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.configuration.selection.value;

import static org.knime.js.base.node.configuration.selection.SelectionNodeParametersUtil.LIMIT_VIS_OPT_DESCRIPTION;
import static org.knime.js.base.node.configuration.selection.SelectionNodeParametersUtil.LIMIT_VIS_OPT_TITLE;
import static org.knime.js.base.node.configuration.selection.SelectionNodeParametersUtil.NUM_VIS_OPT_DESCRIPTION;
import static org.knime.js.base.node.configuration.selection.SelectionNodeParametersUtil.NUM_VIS_OPT_TITLE;
import static org.knime.js.base.node.configuration.selection.SelectionNodeParametersUtil.SELECTION_TYPE_DESCRIPTION;
import static org.knime.js.base.node.configuration.selection.SelectionNodeParametersUtil.SELECTION_TYPE_TITLE;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Supplier;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.webui.node.dialog.defaultdialog.util.updates.StateComputationFailureException;
import org.knime.js.base.node.base.selection.value.ColumnType;
import org.knime.js.base.node.base.selection.value.ValueSelectionNodeConfig;
import org.knime.js.base.node.base.selection.value.ValueSelectionNodeValue;
import org.knime.js.base.node.configuration.ConfigurationNodeParametersUtility.IsMin2Validation;
import org.knime.js.base.node.configuration.ConfigurationNodeSettings;
import org.knime.js.base.node.configuration.OverwrittenByValueMessage;
import org.knime.js.base.node.configuration.selection.SelectionNodeParametersUtil.IsListSelectionType;
import org.knime.js.base.node.configuration.selection.SelectionNodeParametersUtil.LimitNumberOfVisibleOptionsValueReference;
import org.knime.js.base.node.configuration.selection.SelectionNodeParametersUtil.SelectionTypeChoicesProvider;
import org.knime.js.base.node.configuration.selection.SelectionNodeParametersUtil.SelectionTypeValueReference;
import org.knime.js.base.node.configuration.selection.SelectionNodeParametersUtil.ShowNumberOfVisibleOptions;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.NodeParametersInput;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.persistence.NodeParametersPersistor;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.persistence.Persistor;
import org.knime.node.parameters.persistence.legacy.EnumBooleanPersistor;
import org.knime.node.parameters.updates.Effect;
import org.knime.node.parameters.updates.Effect.EffectType;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.updates.StateProvider;
import org.knime.node.parameters.updates.ValueProvider;
import org.knime.node.parameters.updates.ValueReference;
import org.knime.node.parameters.widget.choices.ChoicesProvider;
import org.knime.node.parameters.widget.choices.ColumnChoicesProvider;
import org.knime.node.parameters.widget.choices.Label;
import org.knime.node.parameters.widget.choices.StringChoice;
import org.knime.node.parameters.widget.choices.StringChoicesProvider;
import org.knime.node.parameters.widget.choices.TypedStringChoice;
import org.knime.node.parameters.widget.choices.ValueSwitchWidget;
import org.knime.node.parameters.widget.message.TextMessage;
import org.knime.node.parameters.widget.message.TextMessage.Message;
import org.knime.node.parameters.widget.number.NumberInputWidget;

/**
 * WebUI Node Parameters for the Value Selection Configuration.
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz
 */
public class ValueSelectionDialogNodeParameters extends ConfigurationNodeSettings {

    enum AskForColumn {
            @Label(value = "Yes", description = "Enable a column to be selected.")
            YES, //
            @Label(value = "No, use default",
                description = "No column can be selected. The <i>Default column</i> will be used.")
            NO_USE_DEFAULT
    }

    /**
     * Default constructor
     */
    protected ValueSelectionDialogNodeParameters() {
        super(ValueSelectionDialogNodeConfig.class);
    }

    @TextMessage(ValueSelectionOverwrittenByValueMessage.class)
    @Layout(OutputSection.Top.class)
    Void m_overwrittenByValueMessage;

    private static final class DefaultValue implements NodeParameters {
        @TextMessage(NoColumnsAvailableMessage.class)
        @Layout(OutputSection.Top.class)
        Void m_noColumnsAvailableMessage;

        @Widget(title = "Default column", description = "The column containing the values.")
        @Layout(OutputSection.Top.class)
        @Persist(configKey = ValueSelectionNodeValue.CFG_COLUMN)
        @ChoicesProvider(PossibleColumnChoicesProvider.class)
        @ValueProvider(DefaultColumnValueProvider.class)
        @ValueReference(DefaultColumnValueReference.class)
        String m_defaultColumn;

        @Widget(title = "Default value", description = "The value that is selected by default.")
        @Layout(OutputSection.Top.class)
        @Persist(configKey = ValueSelectionNodeValue.CFG_VALUE)
        @ChoicesProvider(PossibleValuesChoicesProvider.class)
        @ValueProvider(DefaultValueValueProvider.class)
        @ValueReference(DefaultValueValueReference.class)
        String m_defaultValue;
    }

    DefaultValue m_defaultValue = new DefaultValue();

    @Persist(configKey = ValueSelectionNodeConfig.CFG_COLUMN_TYPE)
    @Layout(FormFieldSection.class)
    @ValueReference(ColumnTypeParameterReference.class)
    ColumnType m_columnType = ValueSelectionNodeConfig.DEFAULT_COLUMN_TYPE;

    @Widget(title = "Enable column selection", description = """
            Enable column selection:
            """)
    @Persistor(AskForColumnPersistor.class)
    @Layout(FormFieldSection.class)
    @ValueReference(AskForColumnValueReference.class)
    @ValueSwitchWidget
    AskForColumn m_askForColumn = AskForColumn.YES;

    @Widget(title = SELECTION_TYPE_TITLE, description = SELECTION_TYPE_DESCRIPTION)
    @ChoicesProvider(SelectionTypeChoicesProvider.class)
    @Persist(configKey = ValueSelectionNodeConfig.CFG_TYPE)
    @ValueReference(SelectionTypeValueReference.class)
    @Layout(FormFieldSection.class)
    String m_selectionType = ValueSelectionNodeConfig.DEFAULT_TYPE;

    @Widget(title = LIMIT_VIS_OPT_TITLE, description = LIMIT_VIS_OPT_DESCRIPTION)
    @Persist(configKey = ValueSelectionNodeConfig.CFG_LIMIT_NUMBER_VIS_OPTIONS)
    @ValueReference(LimitNumberOfVisibleOptionsValueReference.class)
    @Effect(predicate = IsListSelectionType.class, type = EffectType.SHOW)
    @Layout(FormFieldSection.class)
    boolean m_limitNumberOfVisibleOptions = ValueSelectionNodeConfig.DEFAULT_LIMIT_NUMBER_VIS_OPTIONS;

    @Widget(title = NUM_VIS_OPT_TITLE, description = NUM_VIS_OPT_DESCRIPTION)
    @NumberInputWidget(minValidation = IsMin2Validation.class)
    @Persist(configKey = ValueSelectionNodeConfig.CFG_NUMBER_VIS_OPTIONS)
    @Effect(predicate = ShowNumberOfVisibleOptions.class, type = EffectType.SHOW)
    @Layout(FormFieldSection.class)
    int m_numberOfVisibleOptions = ValueSelectionNodeConfig.DEFAULT_NUMBER_VIS_OPTIONS;

    @Persistor(PossibleValuesPersistor.class)
    @ValueProvider(PossibleColumnValuesMapChoicesProvider.class)
    Map<String, List<String>> m_possibleValues = new TreeMap<>();

    private static final class PossibleValuesPersistor implements NodeParametersPersistor<Map<String, List<String>>> {

        @Override
        public Map<String, List<String>> load(final NodeSettingsRO settings) throws InvalidSettingsException {
            return ValueSelectionNodeConfig.loadPossibleColumnsAndValuesInDialog(settings);
        }

        @Override
        public void save(final Map<String, List<String>> param, final NodeSettingsWO settings) {
            ValueSelectionNodeConfig.savePossibleColumnsAndValues(settings, param);
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[0][];
        }
    }

    static final class AskForColumnPersistor extends EnumBooleanPersistor<AskForColumn> {
        public AskForColumnPersistor() {
            super(ValueSelectionNodeConfig.CFG_LOCK_COLUMN, AskForColumn.class, AskForColumn.NO_USE_DEFAULT);
        }
    }

    private static final class DefaultColumnValueReference implements ParameterReference<String> {
    }

    private static final class AskForColumnValueReference implements ParameterReference<AskForColumn> {
    }

    private static final class DefaultValueValueReference implements ParameterReference<String> {
    }

    private static final class ColumnTypeParameterReference implements ParameterReference<ColumnType> {
    }

    private static final class PossibleColumnChoicesProvider implements ColumnChoicesProvider {

        private Supplier<Map<String, List<String>>> m_possibleColumnValuesSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            ColumnChoicesProvider.super.init(initializer);
            m_possibleColumnValuesSupplier =
                initializer.computeFromProvidedState(PossibleColumnValuesMapChoicesProvider.class);
        }

        @Override
        public List<DataColumnSpec> columnChoices(final NodeParametersInput parametersInput) {
            final var possibleColumnValues = m_possibleColumnValuesSupplier.get();
            final var tableSpec = parametersInput.getInTableSpec(0);
            if (possibleColumnValues.isEmpty() || tableSpec.isEmpty()) {
                return List.of();
            }
            return tableSpec.get().stream().filter(colSpec -> possibleColumnValues.containsKey(colSpec.getName()))
                .toList();
        }
    }

    private static final class PossibleValuesChoicesProvider implements StringChoicesProvider {

        private Supplier<String> m_defaultColumnSupplier;

        private Supplier<Map<String, List<String>>> m_possibleColumnValuesSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            StringChoicesProvider.super.init(initializer);
            m_defaultColumnSupplier = initializer.computeFromValueSupplier(DefaultColumnValueReference.class);
            m_possibleColumnValuesSupplier =
                initializer.computeFromProvidedState(PossibleColumnValuesMapChoicesProvider.class);
        }

        @Override
        public List<String> choices(final NodeParametersInput parametersInput) {
            final var defaultColumn = m_defaultColumnSupplier.get();
            final var possibleColumnValues = m_possibleColumnValuesSupplier.get();
            if (defaultColumn == null || possibleColumnValues.isEmpty()
                || !possibleColumnValues.containsKey(defaultColumn)) {
                return List.of();
            }
            return possibleColumnValues.get(defaultColumn);
        }
    }

    private static final class DefaultColumnValueProvider implements StateProvider<String> {

        private Supplier<String> m_defaultColumnSupplier;

        private Supplier<List<TypedStringChoice>> m_possibleColumnChoicesSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            initializer.computeAfterOpenDialog();
            m_defaultColumnSupplier = initializer.getValueSupplier(DefaultColumnValueReference.class);
            m_possibleColumnChoicesSupplier = initializer.computeFromProvidedState(PossibleColumnChoicesProvider.class);
        }

        @Override
        public String computeState(final NodeParametersInput parametersInput) throws StateComputationFailureException {
            final var possibleColumnChoices = m_possibleColumnChoicesSupplier.get();
            if (possibleColumnChoices.isEmpty()) {
                return null;
            }
            final var defaultColumn = m_defaultColumnSupplier.get();
            final var possibleColumnChoicesIds = possibleColumnChoices.stream().map(TypedStringChoice::id).toList();

            if (defaultColumn != null && possibleColumnChoicesIds.contains(defaultColumn)) {
                return defaultColumn;
            }
            return possibleColumnChoicesIds.get(0);
        }
    }

    private static final class DefaultValueValueProvider implements StateProvider<String> {

        private Supplier<String> m_defaultValueSupplier;

        private Supplier<List<StringChoice>> m_possibleValuesChoicesSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            initializer.computeAfterOpenDialog();
            m_defaultValueSupplier = initializer.getValueSupplier(DefaultValueValueReference.class);
            m_possibleValuesChoicesSupplier = initializer.computeFromProvidedState(PossibleValuesChoicesProvider.class);
        }

        @Override
        public String computeState(final NodeParametersInput parametersInput) throws StateComputationFailureException {
            final var possibleValuesChoices = m_possibleValuesChoicesSupplier.get();
            if (possibleValuesChoices.isEmpty()) {
                return null;
            }
            final var defaultValue = m_defaultValueSupplier.get();
            final var possibleValueChoicesIds = possibleValuesChoices.stream().map(StringChoice::id).toList();

            if (defaultValue != null && possibleValueChoicesIds.contains(defaultValue)) {
                return defaultValue;
            }
            return possibleValueChoicesIds.get(0);
        }
    }

    static final class PossibleColumnValuesMapChoicesProvider implements StateProvider<Map<String, List<String>>> {

        private Supplier<ColumnType> m_columnTypeSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            initializer.computeBeforeOpenDialog();
            m_columnTypeSupplier = initializer.getValueSupplier(ColumnTypeParameterReference.class);
        }

        @Override
        public Map<String, List<String>> computeState(final NodeParametersInput parametersInput)
            throws StateComputationFailureException {
            final var tableSpecOpt = parametersInput.getInTableSpec(0);

            return tableSpecOpt.isEmpty() ? Map.of()
                : ValueSelectionNodeConfig.getPossibleValues(tableSpecOpt.get(), m_columnTypeSupplier.get());
        }
    }

    static final class ValueSelectionOverwrittenByValueMessage
        extends OverwrittenByValueMessage<ValueSelectionDialogNodeValue> {

        private Supplier<AskForColumn> m_askForColumnSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            super.init(initializer);
            m_askForColumnSupplier = initializer.computeFromValueSupplier(AskForColumnValueReference.class);
        }

        @Override
        protected String valueToString(final ValueSelectionDialogNodeValue value) {
            return m_askForColumnSupplier.get() == AskForColumn.NO_USE_DEFAULT ? value.getValue()
                : String.format("Column: %s; Value: %s", value.getColumn(), value.getValue());
        }

    }

    static final class NoColumnsAvailableMessage implements StateProvider<Optional<TextMessage.Message>> {

        @Override
        public void init(final StateProviderInitializer initializer) {
            initializer.computeBeforeOpenDialog();
        }

        @Override
        public Optional<Message> computeState(final NodeParametersInput parametersInput)
            throws StateComputationFailureException {
            final var tableSpec = parametersInput.getInTableSpec(0);
            if (tableSpec.isEmpty()) {
                return Optional.of(new TextMessage.Message("No column information available.",
                    "Connect a node to the input port.", TextMessage.MessageType.INFO));
            }
            if (tableSpec.get().stream().noneMatch(Objects::nonNull)) {
                return Optional.of(new TextMessage.Message("No column information available.",
                    "Execute upstream nodes first.", TextMessage.MessageType.INFO));
            }
            if (tableSpec.get().stream().noneMatch(colSpec -> colSpec.getDomain().hasValues())) {
                return Optional.of(new Message("No domain information available.",
                    "Add nominal domain information to any column.", TextMessage.MessageType.WARNING));
            }
            return Optional.empty();
        }

    }
}
