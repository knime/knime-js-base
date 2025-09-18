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
 *   11 Sept 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.configuration.filter.column;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.function.Supplier;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.webui.node.dialog.defaultdialog.util.updates.StateComputationFailureException;
import org.knime.js.base.node.base.filter.column.ColumnFilterNodeConfig;
import org.knime.js.base.node.base.filter.column.ColumnFilterNodeValue;
import org.knime.js.base.node.base.validation.AbstractValidatorConfig;
import org.knime.js.base.node.base.validation.InputSpecFilter;
import org.knime.js.base.node.base.validation.min.column.MinNumColumnsValidatorConfig;
import org.knime.js.base.node.configuration.ConfigurationNodeParametersUtility.IsMin2Validation;
import org.knime.js.base.node.configuration.ConfigurationNodeSettings;
import org.knime.js.base.node.configuration.OverwrittenByValueMessage;
import org.knime.js.base.node.configuration.column.InputFilterUtil.AllowAllTypesValueReference;
import org.knime.js.base.node.configuration.column.InputFilterUtil.HideColumnsWithoutDomainValueReference;
import org.knime.js.base.node.configuration.column.InputFilterUtil.InputFilter;
import org.knime.js.base.node.configuration.column.InputFilterUtil.TypeFilterSection;
import org.knime.js.base.node.configuration.column.InputFilterUtil.TypeFilterValueReference;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.NodeParametersInput;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.After;
import org.knime.node.parameters.layout.Before;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.layout.Section;
import org.knime.node.parameters.migration.LoadDefaultsForAbsentFields;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.persistence.Persistor;
import org.knime.node.parameters.persistence.legacy.LegacyColumnFilterPersistor;
import org.knime.node.parameters.updates.Effect;
import org.knime.node.parameters.updates.Effect.EffectType;
import org.knime.node.parameters.updates.EffectPredicate;
import org.knime.node.parameters.updates.EffectPredicateProvider;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.updates.StateProvider;
import org.knime.node.parameters.updates.ValueProvider;
import org.knime.node.parameters.updates.ValueReference;
import org.knime.node.parameters.widget.choices.ChoicesProvider;
import org.knime.node.parameters.widget.choices.ColumnChoicesProvider;
import org.knime.node.parameters.widget.choices.TypedStringChoice;
import org.knime.node.parameters.widget.choices.filter.ColumnFilter;
import org.knime.node.parameters.widget.message.TextMessage;
import org.knime.node.parameters.widget.number.NumberInputWidget;
import org.knime.node.parameters.widget.number.NumberInputWidgetValidation.MinValidation.IsNonNegativeValidation;

/**
 * WebUI Node Parameters for the Column Filter Configuration.
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz
 */
@SuppressWarnings("restriction")
@LoadDefaultsForAbsentFields
public class ColumnFilterDialogNodeParameters extends ConfigurationNodeSettings {

    @Section(title = "Validation")
    @After(FormFieldSection.class)
    @Before(TypeFilterSection.class)
    public interface ValidationSection {
    }

    /**
     * Default constructor
     */
    protected ColumnFilterDialogNodeParameters() {
        super(ColumnFilterDialogNodeConfig.class);
    }

    @TextMessage(ColumnFilterOverwrittenByValueMessage.class)
    @Layout(OutputSection.Top.class)
    Void m_overwrittenByValueMessage;

    @LoadDefaultsForAbsentFields
    private static final class DefaultValue implements NodeParameters {

        /**
         * Reason for the nested ColumnFilterLevel1 is the settings structure of default value:
         * <ul>
         * <li>defaultValue:</li>
         * <ul>
         * <li>columns: string[]</li>
         * <li>columnFilter:</li>
         * <ul>
         * <li>columnFilter: LegacyColumnFilter</li>
         * <ul>
         * <li>filter-type: string</li>
         * <li>...</li>
         * <li>datatype: ...</li>
         * </ul>
         * </ul>
         * </ul>
         * </ul>
         */
        @LoadDefaultsForAbsentFields
        static final class ColumnFilterLevel1 implements NodeParameters {
            @Widget(title = "Default values", description = "The columns that are selected by default.")
            @Persistor(ColumnFilterPersistor.class)
            @ChoicesProvider(ColumnFilterChoicesProvider.class)
            ColumnFilter m_columnFilter = new ColumnFilter().withIncludeUnknownColumns();
        }

        @Layout(OutputSection.Top.class)
        @Persist(configKey = ColumnFilterNodeConfig.CFG_COLUMN_FILTER)
        ColumnFilterLevel1 m_columnFilterLevel1 = new ColumnFilterLevel1();

        @Persist(configKey = ColumnFilterNodeValue.CFG_COLUMNS)
        @ChoicesProvider(ColumnFilterChoicesProvider.class)
        @ValueProvider(SelectableColumnsValueProvider.class)
        String[] m_columns = new String[0];
    }

    DefaultValue m_defaultValue = new DefaultValue();

    @Widget(title = "Limit number of visible options", description = """
            By default the filter component adjusts its height to display all possible choices without a scroll bar.
            If the setting is enabled, you will be able to limit the number of visible options in case you have too
            many of them.
            """)
    @Persist(configKey = ColumnFilterNodeConfig.CFG_LIMIT_NUMBER_VIS_OPTIONS)
    @ValueReference(LimitNumberOfVisibleOptionsValueReference.class)
    @Layout(FormFieldSection.class)
    boolean m_limitNumberOfVisibleOptions = ColumnFilterNodeConfig.DEFAULT_LIMIT_NUMBER_VIS_OPTIONS;

    @Widget(title = "Number of visible options", description = """
            A number of options visible in the filter component without a vertical scroll bar. Changing this value
            will also affect the component's height. Notice that the height cannot be less than the overall height of
            the control buttons in the middle.
            """)
    @NumberInputWidget(minValidation = IsMin2Validation.class)
    @Persist(configKey = ColumnFilterNodeConfig.CFG_NUMBER_VIS_OPTIONS)
    @Effect(predicate = ShowNumberOfVisibleOptions.class, type = EffectType.SHOW)
    @Layout(FormFieldSection.class)
    int m_numberOfVisibleOptions = ColumnFilterNodeConfig.DEFAULT_NUMBER_VIS_OPTIONS;

    @Layout(TypeFilterSection.class)
    @Persist(configKey = ColumnFilterDialogNodeConfig.CFG_INPUT_FILTER)
    InputFilter m_inputFilter = new InputFilter();

    @Layout(ValidationSection.class)
    @Persist(configKey = ColumnFilterDialogNodeConfig.CFG_VALIDATION)
    Validation m_validation = new Validation();

    /**
     * Reason for the nested MinNumValidation is the settings structure of validation:
     * <ul>
     * <li>validation:</li>
     * <ul>
     * <li>min_num_columns:</li>
     * <ul>
     * <li>error_message: string</li>
     * <li>min_num_columns: integer</li>
     * </ul>
     * </ul>
     * </ul>
     */
    @LoadDefaultsForAbsentFields
    static final class Validation implements NodeParameters {

        @Persist(configKey = MinNumColumnsValidatorConfig.CFG_MIN_NUM_COLUMNS)
        MinNumValidation m_validation = new MinNumValidation();

        @LoadDefaultsForAbsentFields
        static final class MinNumValidation implements NodeParameters {
            @Widget(title = "Minimum number of columns",
                description = "Minimum number of columns the user has to select.")
            @NumberInputWidget(minValidation = IsNonNegativeValidation.class)
            @Persist(configKey = MinNumColumnsValidatorConfig.CFG_MIN_NUM_COLUMNS)
            int m_minNumColumns = MinNumColumnsValidatorConfig.DEFAULT_MIN_NUM_COLUMNS;

            @Widget(title = "Error message",
                description = "Error message to display when the minimum number of columns is not selected. If it is"
                    + " empty, a default message will be displayed: <i>Select at least &lt;number&gt; columns.</i>")
            @Persist(configKey = AbstractValidatorConfig.CFG_ERROR_MESSAGE)
            String m_errorMessage = AbstractValidatorConfig.DEFAULT_ERROR_MESSAGE;

        }

    }

    @ChoicesProvider(ColumnFilterChoicesProvider.class)
    @ValueProvider(SelectableColumnsValueProvider.class)
    @Persist(configKey = ColumnFilterNodeConfig.CFG_POSSIBLE_COLUMNS)
    String[] m_possibleColumns = new String[0];

    @Persist(configKey = ColumnFilterNodeConfig.CFG_TYPE)
    String m_type = ColumnFilterNodeConfig.DEFAULT_TYPE;

    private static final class ColumnFilterPersistor extends LegacyColumnFilterPersistor {

        protected ColumnFilterPersistor() {
            super(ColumnFilterNodeConfig.CFG_COLUMN_FILTER);
        }

    }

    private static final class LimitNumberOfVisibleOptionsValueReference implements ParameterReference<Boolean> {
    }

    static final class ColumnFilterChoicesProvider implements ColumnChoicesProvider {

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

    static final class SelectableColumnsValueProvider implements StateProvider<String[]> {

        private Supplier<List<TypedStringChoice>> m_columnChoicesSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            initializer.computeBeforeOpenDialog();
            m_columnChoicesSupplier = initializer.computeFromProvidedState(ColumnFilterChoicesProvider.class);
        }

        @Override
        public String[] computeState(final NodeParametersInput parametersInput)
            throws StateComputationFailureException {
            return m_columnChoicesSupplier.get().stream().map(tsc -> tsc.id()).toArray(String[]::new);
        }
    }

    private static final class ColumnFilterOverwrittenByValueMessage
        extends OverwrittenByValueMessage<ColumnFilterDialogNodeValue> {

        @Override
        protected String valueToString(final ColumnFilterDialogNodeValue value) {
            return Arrays.toString(value.getColumns());
        }

    }

    private static final class ShowNumberOfVisibleOptions implements EffectPredicateProvider {
        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            return i.getBoolean(LimitNumberOfVisibleOptionsValueReference.class).isTrue();
        }
    }

}
