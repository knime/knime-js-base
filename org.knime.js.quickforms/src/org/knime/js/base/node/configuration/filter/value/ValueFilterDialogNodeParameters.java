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
 *   23 Sept 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.configuration.filter.value;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.util.filter.NameFilterConfiguration.EnforceOption;
import org.knime.core.webui.node.dialog.defaultdialog.internal.widget.PersistWithin;
import org.knime.core.webui.node.dialog.defaultdialog.util.updates.StateComputationFailureException;
import org.knime.js.base.node.configuration.ConfigurationNodeSettings;
import org.knime.js.base.node.configuration.value.ValueSelectionFilterDialogNodeParametersUtil.DefaultColumnValueReference;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.OutputSection;
import org.knime.js.base.node.parameters.nominal.ValueFilterNodeParameters;
import org.knime.js.base.node.parameters.nominal.ValueFilterNodeParameters.DefaultValue.AbstractModifyDefaultValuesValueProvider;
import org.knime.js.base.node.parameters.nominal.ValueFilterNodeParameters.DefaultValuesChoicesProvider;
import org.knime.js.base.node.parameters.nominal.ValueFilterNodeParameters.DefaultValuesValueReference;
import org.knime.js.base.node.parameters.nominal.ValueFilterNodeParameters.PossibleColumnValuesValueReference;
import org.knime.node.parameters.NodeParametersInput;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.migration.LoadDefaultsForAbsentFields;
import org.knime.node.parameters.persistence.NodeParametersPersistor;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.persistence.Persistor;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.updates.StateProvider;
import org.knime.node.parameters.updates.ValueProvider;
import org.knime.node.parameters.updates.ValueReference;
import org.knime.node.parameters.widget.choices.Label;
import org.knime.node.parameters.widget.choices.StringChoice;
import org.knime.node.parameters.widget.choices.ValueSwitchWidget;

/**
 * WebUI Node Parameters for the Value Filter Configuration.
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz
 */
@SuppressWarnings("restriction")
public final class ValueFilterDialogNodeParameters extends ConfigurationNodeSettings {

    ValueFilterDialogNodeParameters() {
        super(ValueFilterDialogNodeConfig.class);
    }

    enum AnyUnknownValueHandling {
            @Label(value = "Exclude", description = "New values are added to the exclude list.")
            EXCLUDE, // NOSONAR
            @Label(value = "Include", description = "New values are added to the include list.")
            INCLUDE; // NOSONAR
    }

    @LoadDefaultsForAbsentFields
    private static final class DefaultValue extends ValueFilterNodeParameters.DefaultValue {
        @ValueProvider(DefaultExcludesValueProvider.class)
        @ValueReference(DefaultExcludesValueReference.class)
        @Persist(configKey = ValueFilterDialogNodeValue.CFG_EXCLUDES)
        String[] m_excludes = new String[0];

        @Widget(title = "Any unknown value handling", description = "Specifies the handling of new values:")
        @Layout(OutputSection.Top.class)
        @Persistor(AnyUnknownColumnHandlingPersistor.class)
        @ValueReference(DefaultAnyUnkownValueHandlingValueReference.class)
        @ValueSwitchWidget
        AnyUnknownValueHandling m_anyUnknownValueHandling = AnyUnknownValueHandling.EXCLUDE;
    }

    @PersistWithin.PersistEmbedded
    ValueFilterNodeParameters m_valueFilterNodeParameters = new ValueFilterNodeParameters();

    DefaultValue m_defaultValue = new DefaultValue();

    private static final class DefaultExcludesValueReference implements ParameterReference<String[]> {
    }

    private static final class DefaultAnyUnkownValueHandlingValueReference
        implements ParameterReference<AnyUnknownValueHandling> {
    }

    static final class ModifyDefaultValuesValueProvider extends AbstractModifyDefaultValuesValueProvider {
        ModifyDefaultValuesValueProvider() {
            super(DefaultValuesValueProvider.class);
        }
    }

    private static final class DefaultValuesValueProvider implements StateProvider<String[]> {

        private Supplier<String[]> m_defaultValuesSupplier;

        private Supplier<String[]> m_defaultExcludesSupplier;

        private Supplier<List<StringChoice>> m_defaultValuesChoicesSupplier;

        private Supplier<AnyUnknownValueHandling> m_anyUnknownValueHandlingSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            initializer.computeAfterOpenDialog();
            m_defaultValuesSupplier = initializer.getValueSupplier(DefaultValuesValueReference.class);
            m_defaultExcludesSupplier = initializer.getValueSupplier(DefaultExcludesValueReference.class);
            m_defaultValuesChoicesSupplier = initializer.computeFromProvidedState(DefaultValuesChoicesProvider.class);
            m_anyUnknownValueHandlingSupplier =
                initializer.getValueSupplier(DefaultAnyUnkownValueHandlingValueReference.class);
        }

        @Override
        public String[] computeState(final NodeParametersInput parametersInput)
            throws StateComputationFailureException {
            final var defaultValuesChoices = m_defaultValuesChoicesSupplier.get();
            if (defaultValuesChoices.isEmpty()) {
                return new String[0];
            }
            final var defaultValues = Set.of(m_defaultValuesSupplier.get());
            final var defaultExcludes = Set.of(m_defaultExcludesSupplier.get());
            final var anyUnkownValueHandling = m_anyUnknownValueHandlingSupplier.get();

            return defaultValuesChoices.stream() //
                .map(StringChoice::id) //
                .filter(choice -> defaultValues.contains(choice)
                    || anyUnkownValueHandling == AnyUnknownValueHandling.INCLUDE && !defaultExcludes.contains(choice))
                .toArray(String[]::new);
        }
    }

    static final class AnyUnknownColumnHandlingPersistor implements NodeParametersPersistor<AnyUnknownValueHandling> {

        @Override
        public AnyUnknownValueHandling load(final NodeSettingsRO settings) throws InvalidSettingsException {
            final var enforceOption = EnforceOption.parse(settings.getString(ValueFilterDialogNodeValue.CFG_ENFORCE_OPT,
                ValueFilterDialogNodeValue.DEFAULT_ENFORCE_OPT.toString()));
            return enforceOption == EnforceOption.EnforceInclusion ? AnyUnknownValueHandling.EXCLUDE
                : AnyUnknownValueHandling.INCLUDE;
        }

        @Override
        public void save(final AnyUnknownValueHandling param, final NodeSettingsWO settings) {
            settings.addString(ValueFilterDialogNodeValue.CFG_ENFORCE_OPT, (param == AnyUnknownValueHandling.EXCLUDE
                ? EnforceOption.EnforceInclusion : EnforceOption.EnforceExclusion).toString());

        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][]{{ValueFilterDialogNodeValue.CFG_ENFORCE_OPT}};
        }

    }

    private static final class DefaultExcludesValueProvider implements StateProvider<String[]> {

        private Supplier<String> m_defaultColumnSupplier;

        private Supplier<String[]> m_defaultValuesSupplier;

        private Supplier<Map<String, List<String>>> m_possibleColumnValuesSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            m_defaultColumnSupplier = initializer.getValueSupplier(DefaultColumnValueReference.class);
            m_defaultValuesSupplier = initializer.computeFromValueSupplier(DefaultValuesValueReference.class);
            m_possibleColumnValuesSupplier = initializer.getValueSupplier(PossibleColumnValuesValueReference.class);
        }

        @Override
        public String[] computeState(final NodeParametersInput parametersInput)
            throws StateComputationFailureException {
            final var defaultColumn = m_defaultColumnSupplier.get();
            final var possibleColumnValues = m_possibleColumnValuesSupplier.get();
            if (defaultColumn == null || !possibleColumnValues.containsKey(defaultColumn)
                || possibleColumnValues.get(defaultColumn).isEmpty()) {
                return new String[0];
            }
            final var defaultValues = Set.of(m_defaultValuesSupplier.get());
            final var possibleValues = possibleColumnValues.get(defaultColumn);
            return possibleValues.stream().filter(Predicate.not(defaultValues::contains)).toArray(String[]::new);
        }
    }

}
