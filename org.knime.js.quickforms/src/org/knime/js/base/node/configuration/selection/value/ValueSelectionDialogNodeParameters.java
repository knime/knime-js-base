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
import static org.knime.js.base.node.configuration.value.ValueSelectionFilterDialogNodeParametersUtil.ENABLE_COLUMN_FIELD_DESCRIPTION;
import static org.knime.js.base.node.configuration.value.ValueSelectionFilterDialogNodeParametersUtil.ENABLE_COLUMN_FIELD_TITLE;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

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
import org.knime.js.base.node.configuration.value.ValueSelectionFilterDialogNodeParametersUtil.AbstractDefaultColumnChoicesProvider;
import org.knime.js.base.node.configuration.value.ValueSelectionFilterDialogNodeParametersUtil.AbstractDefaultColumnValueProvider;
import org.knime.js.base.node.configuration.value.ValueSelectionFilterDialogNodeParametersUtil.AbstractDefaultValueChoicesProvider;
import org.knime.js.base.node.configuration.value.ValueSelectionFilterDialogNodeParametersUtil.AbstractInvertBooleanPersistor;
import org.knime.js.base.node.configuration.value.ValueSelectionFilterDialogNodeParametersUtil.AbstractPossibleValuesPersistor;
import org.knime.js.base.node.configuration.value.ValueSelectionFilterDialogNodeParametersUtil.DefaultColumnValueReference;
import org.knime.js.base.node.configuration.value.ValueSelectionFilterDialogNodeParametersUtil.EnableColumnFieldValueReference;
import org.knime.js.base.node.configuration.value.ValueSelectionFilterDialogNodeParametersUtil.NoColumnsAvailableMessage;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.NodeParametersInput;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.persistence.Persistor;
import org.knime.node.parameters.updates.Effect;
import org.knime.node.parameters.updates.Effect.EffectType;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.updates.StateProvider;
import org.knime.node.parameters.updates.ValueProvider;
import org.knime.node.parameters.updates.ValueReference;
import org.knime.node.parameters.widget.choices.ChoicesProvider;
import org.knime.node.parameters.widget.choices.StringChoice;
import org.knime.node.parameters.widget.message.TextMessage;
import org.knime.node.parameters.widget.number.NumberInputWidget;

/**
 * WebUI Node Parameters for the Value Selection Configuration.
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz
 */
public class ValueSelectionDialogNodeParameters extends ConfigurationNodeSettings {

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
        @ChoicesProvider(DefaultColumnChoicesProvider.class)
        @ValueProvider(DefaultColumnValueProvider.class)
        @ValueReference(DefaultColumnValueReference.class)
        String m_defaultColumn;

        @Widget(title = "Default value", description = "The value that is selected by default.")
        @Layout(OutputSection.Top.class)
        @Persist(configKey = ValueSelectionNodeValue.CFG_VALUE)
        @ChoicesProvider(DefaultValueChoicesProvider.class)
        @ValueProvider(DefaultValueValueProvider.class)
        @ValueReference(DefaultValueValueReference.class)
        String m_defaultValue;
    }

    DefaultValue m_defaultValue = new DefaultValue();

    @Persist(configKey = ValueSelectionNodeConfig.CFG_COLUMN_TYPE)
    @Layout(FormFieldSection.class)
    @ValueReference(ColumnTypeParameterReference.class)
    ColumnType m_columnType = ValueSelectionNodeConfig.DEFAULT_COLUMN_TYPE;

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

    @Widget(title = ENABLE_COLUMN_FIELD_TITLE, description = ENABLE_COLUMN_FIELD_DESCRIPTION)
    @Persistor(EnableColumnFieldPersistor.class)
    @Layout(FormFieldSection.class)
    @ValueReference(EnableColumnFieldValueReference.class)
    boolean m_enableColumnField = true;

    @Persistor(PossibleValuesPersistor.class)
    @ValueProvider(PossibleColumnValuesMapChoicesProvider.class)
    Map<String, List<String>> m_possibleValues = new TreeMap<>();

    static final class PossibleValuesPersistor extends AbstractPossibleValuesPersistor {
        PossibleValuesPersistor() {
            super(ValueSelectionNodeConfig.CFG_POSSIBLE_COLUMNS, ValueSelectionNodeConfig.CFG_COL);
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

    private static final class DefaultColumnChoicesProvider extends AbstractDefaultColumnChoicesProvider {
        DefaultColumnChoicesProvider() {
            super(PossibleColumnValuesMapChoicesProvider.class);
        }
    }

    private static final class DefaultColumnValueProvider extends AbstractDefaultColumnValueProvider {
        DefaultColumnValueProvider() {
            super(DefaultColumnChoicesProvider.class);
        }
    }

    static final class EnableColumnFieldPersistor extends AbstractInvertBooleanPersistor {
        public EnableColumnFieldPersistor() {
            super(ValueSelectionNodeConfig.CFG_LOCK_COLUMN);
        }
    }

    private static final class DefaultValueValueReference implements ParameterReference<String> {
    }

    private static final class ColumnTypeParameterReference implements ParameterReference<ColumnType> {
    }

    private static final class DefaultValueChoicesProvider extends AbstractDefaultValueChoicesProvider {

        public DefaultValueChoicesProvider() {
            super(PossibleColumnValuesMapChoicesProvider.class);
        }
    }

    private static final class DefaultValueValueProvider implements StateProvider<String> {

        private Supplier<String> m_defaultValueSupplier;

        private Supplier<List<StringChoice>> m_possibleValuesChoicesSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            initializer.computeAfterOpenDialog();
            m_defaultValueSupplier = initializer.getValueSupplier(DefaultValueValueReference.class);
            m_possibleValuesChoicesSupplier = initializer.computeFromProvidedState(DefaultValueChoicesProvider.class);
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
            return possibleValuesChoices.get(0).id();
        }
    }

    static final class ValueSelectionOverwrittenByValueMessage
        extends OverwrittenByValueMessage<ValueSelectionDialogNodeValue> {

        private Supplier<Boolean> m_enableColumnFieldSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            super.init(initializer);
            m_enableColumnFieldSupplier = initializer.computeFromValueSupplier(EnableColumnFieldValueReference.class);
        }

        @Override
        protected String valueToString(final ValueSelectionDialogNodeValue value) {
            return m_enableColumnFieldSupplier.get() != null && m_enableColumnFieldSupplier.get()
                ? String.format("Column: %s; Value: %s", value.getColumn(), value.getValue()) : value.getValue();
        }

    }

}
