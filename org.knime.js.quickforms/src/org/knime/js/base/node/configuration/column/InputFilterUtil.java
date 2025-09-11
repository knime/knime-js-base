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
package org.knime.js.base.node.configuration.column;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import org.knime.core.data.DataType;
import org.knime.core.data.DataValue.UtilityFactory;
import org.knime.core.data.ExtensibleUtilityFactory;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.base.node.base.validation.InputSpecFilter;
import org.knime.js.base.node.base.validation.TypeFilterDialog;
import org.knime.js.base.node.configuration.ConfigurationNodeSettings.FormFieldSection;
import org.knime.js.base.node.configuration.ConfigurationNodeSettings.OutputSection;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.NodeParametersInput;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.After;
import org.knime.node.parameters.layout.Before;
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
import org.knime.node.parameters.widget.choices.StringChoice;
import org.knime.node.parameters.widget.choices.StringChoicesProvider;
import org.knime.node.parameters.widget.choices.filter.TwinlistWidget;

/**
 * Utility class for handling input filter configurations in configuration nodes.
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz
 */
@SuppressWarnings("javadoc")
public class InputFilterUtil {

    private static final List<StringChoice> POSSIBLE_TYPE_CHOICES =
        TypeFilterDialog.getDefaultTypes().stream().map(valueClass -> {
            final String id = valueClass.getName();
            final UtilityFactory utilityFor = DataType.getUtilityFor(valueClass);
            if (utilityFor instanceof ExtensibleUtilityFactory) {
                final ExtensibleUtilityFactory eu = (ExtensibleUtilityFactory)utilityFor;
                final String text = eu.getName();
                return new StringChoice(id, text);
            }
            throw new IllegalStateException("All value classes need to implement the ExtensibleUtilityFactory.");
        }).toList();

    public static final class InputFilter implements NodeParameters {

        @Widget(title = "Allow all types",
            description = "If checked, all types can be selected,"
                + " else, the selection can be restricted to a set of types.")
        @Persist(configKey = InputSpecFilter.Config.CFG_ALLOW_ALL_TYPES)
        @ValueReference(AllowAllTypesValueReference.class)
        boolean m_allowAllTypes = InputSpecFilter.Config.DEFAULT_ALLOW_ALL_TYPES;

        @Widget(title = "Hide columns without domain",
            description = "If checked, columns without a domain cannot be selected")
        @Persist(configKey = InputSpecFilter.Config.CFG_FILTER_COLUMNS_WITHOUT_DOMAIN)
        @ValueReference(HideColumnsWithoutDomainValueReference.class)
        boolean m_filterColumnsWithoutDomains = InputSpecFilter.Config.DEFAULT_FILTER_COLUMNS_WITHOUT_DOMAIN;

        @Widget(title = "Allowed types", description = "Restrict the selectable columns to certain types.")
        @Persistor(TypeFilterPersistor.class)
        @ChoicesProvider(TypeFilterChoicesProvider.class)
        @ValueReference(TypeFilterValueReference.class)
        @TwinlistWidget
        @Effect(predicate = AllowAllTypes.class, type = EffectType.HIDE)
        String[] m_typeFilter = new String[0];

        static final class AllowAllTypes implements EffectPredicateProvider {

            @Override
            public EffectPredicate init(final PredicateInitializer i) {
                return i.getBoolean(AllowAllTypesValueReference.class).isTrue();
            }
        }

        static final class TypeFilterChoicesProvider implements StringChoicesProvider {

            @Override
            public List<StringChoice> computeState(final NodeParametersInput parametersInput) {
                return POSSIBLE_TYPE_CHOICES;
            }

        }

    }

    private static final class TypeFilterPersistor implements NodeParametersPersistor<String[]> {

        @Override
        public String[] load(final NodeSettingsRO settings) throws InvalidSettingsException {
            final var typeFilterSettings = settings.getNodeSettings(InputSpecFilter.Config.CFG_TYPE_FILTER);
            return POSSIBLE_TYPE_CHOICES.stream()
                .map(idText -> typeFilterSettings.getBoolean(idText.id(), false) ? idText.id() : null)
                .filter(Objects::nonNull) //
                .toArray(String[]::new);
        }

        @Override
        public void save(final String[] param, final NodeSettingsWO settings) {
            final var typeFilterSettings = settings.addNodeSettings(InputSpecFilter.Config.CFG_TYPE_FILTER);
            final var selected = new HashSet<>(Arrays.asList(param));
            POSSIBLE_TYPE_CHOICES.stream().forEach(idText -> {
                final var id = idText.id();
                typeFilterSettings.addBoolean(id, selected.contains(id));
            });
        }

        @Override
        public String[][] getConfigPaths() {
            // TODO enable flow variables for config keys with dots
            // return CONFIG_KEYS.stream() //
            //     .map(idText -> new String[]{InputSpecFilter.Config.CFG_TYPE_FILTER, idText.id()}) //
            //     .toArray(String[][]::new);
            return new String[0][0];
        }
    }

    @Section(title = "Type Filter")
    @After(FormFieldSection.class)
    @Before(OutputSection.class)
    public interface TypeFilterSection {
    }

    public static final class TypeFilterValueReference implements ParameterReference<String[]> {
    }

    public static final class AllowAllTypesValueReference implements ParameterReference<Boolean> {
    }

    public static final class HideColumnsWithoutDomainValueReference implements ParameterReference<Boolean> {
    }

}
