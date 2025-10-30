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
 *   30 Oct 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.parameters.nominal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;

import org.knime.core.webui.node.dialog.defaultdialog.internal.widget.PersistWithin;
import org.knime.core.webui.node.dialog.defaultdialog.util.updates.StateComputationFailureException;
import org.knime.core.webui.node.dialog.defaultdialog.widget.Modification;
import org.knime.core.webui.node.dialog.defaultdialog.widget.Modification.WidgetGroupModifier;
import org.knime.js.base.node.base.filter.value.ValueFilterNodeConfig;
import org.knime.js.base.node.base.filter.value.ValueFilterNodeValue;
import org.knime.js.base.node.configuration.value.ValueSelectionFilterDialogNodeParametersUtil.AbstractDefaultColumnChoicesProvider;
import org.knime.js.base.node.configuration.value.ValueSelectionFilterDialogNodeParametersUtil.AbstractDefaultColumnValueProvider;
import org.knime.js.base.node.configuration.value.ValueSelectionFilterDialogNodeParametersUtil.AbstractDefaultValueChoicesProvider;
import org.knime.js.base.node.configuration.value.ValueSelectionFilterDialogNodeParametersUtil.AbstractPossibleValuesPersistor;
import org.knime.js.base.node.configuration.value.ValueSelectionFilterDialogNodeParametersUtil.DefaultColumnValueReference;
import org.knime.js.base.node.configuration.value.ValueSelectionFilterDialogNodeParametersUtil.EnableColumnFieldParameter;
import org.knime.js.base.node.configuration.value.ValueSelectionFilterDialogNodeParametersUtil.EnableColumnFieldParameter.EnableColumnFieldValueReference;
import org.knime.js.base.node.configuration.value.ValueSelectionFilterDialogNodeParametersUtil.NoColumnsAvailableMessage;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.FormFieldSection;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.OutputSection;
import org.knime.js.base.node.parameters.OverwrittenByValueMessage;
import org.knime.js.base.node.parameters.filterandselection.MultipleSelectionComponentParameters;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.NodeParametersInput;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.migration.LoadDefaultsForAbsentFields;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.persistence.Persistor;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.updates.StateProvider;
import org.knime.node.parameters.updates.ValueProvider;
import org.knime.node.parameters.updates.ValueReference;
import org.knime.node.parameters.widget.choices.ChoicesProvider;
import org.knime.node.parameters.widget.choices.StringChoice;
import org.knime.node.parameters.widget.choices.filter.TwinlistWidget;
import org.knime.node.parameters.widget.message.TextMessage;

/**
 * Shared WebUI Node Parameters for Value Filter Configuration and Widget.
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz
 */
@SuppressWarnings("restriction")
@LoadDefaultsForAbsentFields
public final class ValueFilterNodeParameters implements NodeParameters {

    @TextMessage(ValueFilterOverwrittenByValueMessage.class)
    @Layout(OutputSection.Top.class)
    Void m_overwrittenByValueMessage;

    /**
     * The default parameters of the value filter configuration/widget.
     */
    public static class DefaultValue implements NodeParameters {
        @TextMessage(NoColumnsAvailableMessage.class)
        @Layout(OutputSection.Top.class)
        Void m_noColumnsAvailableMessage;

        @Widget(title = "Default column", description = "The column containing the values to filter.")
        @Layout(OutputSection.Top.class)
        @Persist(configKey = ValueFilterNodeValue.CFG_COLUMN)
        @ChoicesProvider(DefaultColumnChoicesProvider.class)
        @ValueProvider(DefaultColumnValueProvider.class)
        @ValueReference(DefaultColumnValueReference.class)
        String m_column = "";

        @Widget(title = "Default values", description = "The values that are selected by default.")
        @Layout(OutputSection.Top.class)
        @Persist(configKey = ValueFilterNodeValue.CFG_VALUES)
        @ChoicesProvider(DefaultValuesChoicesProvider.class)
        @ValueProvider(DefaultValuesValueProvider.class)
        @ValueReference(DefaultValuesValueReference.class)
        @Modification.WidgetReference(DefaultValuesModificationReference.class)
        @TwinlistWidget
        String[] m_values = new String[0];

        static final class DefaultValuesModificationReference implements Modification.Reference {
        }

        /**
         * Modification to change the Value Provider of the default values.
         */
        public abstract static class AbstractModifyDefaultValuesValueProvider implements Modification.Modifier {

            private final Class<? extends StateProvider<String[]>> m_defaultValuesValueProviderClass;

            /**
             * Default constructor
             *
             * @param defaultValuesValueProviderClass the value provider to use for the default values
             */
            protected AbstractModifyDefaultValuesValueProvider(
                final Class<? extends StateProvider<String[]>> defaultValuesValueProviderClass) {
                m_defaultValuesValueProviderClass = defaultValuesValueProviderClass;
            }

            @Override
            public final void modify(final WidgetGroupModifier group) {
                group.find(DefaultValuesModificationReference.class).modifyAnnotation(ValueProvider.class)
                    .withValue(m_defaultValuesValueProviderClass).modify();
            }

        }
    }

    @PersistWithin.PersistEmbedded
    @Layout(FormFieldSection.class)
    MultipleSelectionComponentParameters m_multipleSelectionComponentParameters =
        new MultipleSelectionComponentParameters();

    @PersistWithin.PersistEmbedded
    EnableColumnFieldParameter m_enableColumnFieldParameter = new EnableColumnFieldParameter();

    @Persistor(PossibleColumnValuesMapPersistor.class)
    @ValueProvider(PossibleColumnValuesMapChoicesProvider.class)
    @ValueReference(PossibleColumnValuesValueReference.class)
    Map<String, List<String>> m_possibleValues = new TreeMap<>();

    static final class PossibleColumnValuesMapChoicesProvider implements StateProvider<Map<String, List<String>>> {

        @Override
        public void init(final StateProviderInitializer initializer) {
            initializer.computeBeforeOpenDialog();
        }

        @Override
        public Map<String, List<String>> computeState(final NodeParametersInput parametersInput)
            throws StateComputationFailureException {
            final var tableSpec = parametersInput.getInTableSpec(0);
            return tableSpec.isEmpty() ? Map.of() : ValueFilterNodeConfig.getPossibleValues(tableSpec.get());
        }
    }

    private static final class DefaultValuesValueProvider implements StateProvider<String[]> {

        private Supplier<String[]> m_defaultValuesSupplier;

        private Supplier<List<StringChoice>> m_defaultValuesChoicesSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            initializer.computeAfterOpenDialog();
            m_defaultValuesSupplier = initializer.getValueSupplier(DefaultValuesValueReference.class);
            m_defaultValuesChoicesSupplier = initializer.computeFromProvidedState(DefaultValuesChoicesProvider.class);
        }

        @Override
        public String[] computeState(final NodeParametersInput parametersInput)
            throws StateComputationFailureException {
            final var defaultValuesChoices = m_defaultValuesChoicesSupplier.get();
            if (defaultValuesChoices.isEmpty()) {
                return new String[0];
            }
            final var defaultValues = Set.of(m_defaultValuesSupplier.get());

            return defaultValuesChoices.stream() //
                .map(StringChoice::id) //
                .filter(defaultValues::contains) //
                .toArray(String[]::new);
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

    /**
     * The choices provider for the default values.
     */
    public static final class DefaultValuesChoicesProvider extends AbstractDefaultValueChoicesProvider {
        DefaultValuesChoicesProvider() {
            super(PossibleColumnValuesMapChoicesProvider.class);
        }
    }

    static final class PossibleColumnValuesMapPersistor extends AbstractPossibleValuesPersistor {
        PossibleColumnValuesMapPersistor() {
            super(ValueFilterNodeConfig.CFG_POSSIBLE_COLUMNS, ValueFilterNodeConfig.CFG_COL);
        }
    }

    /**
     * The reference for the map of possible columns and their values.
     */
    public static final class PossibleColumnValuesValueReference
        implements ParameterReference<Map<String, List<String>>> {
    }

    /**
     * The reference for the default values.
     */
    public static final class DefaultValuesValueReference implements ParameterReference<String[]> {
    }

    private static final class ValueFilterOverwrittenByValueMessage
        extends OverwrittenByValueMessage<ValueFilterNodeValue> {

        private Supplier<Boolean> m_enableColumnFieldSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            super.init(initializer);
            m_enableColumnFieldSupplier = initializer.computeFromValueSupplier(EnableColumnFieldValueReference.class);
        }

        @Override
        protected String valueToString(final ValueFilterNodeValue value) {
            final var values = Arrays.toString(value.getValues());
            return m_enableColumnFieldSupplier.get() != null && m_enableColumnFieldSupplier.get()
                ? String.format("Column: %s; Value: %s", value.getColumn(), values) : values;
        }

    }

}
