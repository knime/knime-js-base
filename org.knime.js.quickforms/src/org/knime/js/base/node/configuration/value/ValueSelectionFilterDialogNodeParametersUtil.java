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
 *   24 Sept 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.configuration.value;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.webui.node.dialog.defaultdialog.util.updates.StateComputationFailureException;
import org.knime.js.base.node.configuration.filter.value.ValueFilterDialogNodeParameters;
import org.knime.js.base.node.configuration.selection.value.ValueSelectionDialogNodeParameters;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.FormFieldSection;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.NodeParametersInput;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.persistence.NodeParametersPersistor;
import org.knime.node.parameters.persistence.Persistor;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.updates.StateProvider;
import org.knime.node.parameters.updates.ValueReference;
import org.knime.node.parameters.widget.choices.ColumnChoicesProvider;
import org.knime.node.parameters.widget.choices.StringChoicesProvider;
import org.knime.node.parameters.widget.choices.TypedStringChoice;
import org.knime.node.parameters.widget.message.TextMessage;
import org.knime.node.parameters.widget.message.TextMessage.Message;

/**
 * Utility class to deduplicate titles, descriptions, effects, references, and providers of node parameters including
 * value selection/filtering (i.e. {@link ValueSelectionDialogNodeParameters}, and
 * {@link ValueFilterDialogNodeParameters}).
 *
 * @author Robin Gerling
 */
public class ValueSelectionFilterDialogNodeParametersUtil {

    /**
     * Node Parameter for the Enable Column Field (previously Lock Column)
     *
     * With the migration of the Configuration & Widget nodes to ModernUI the setting title was adjusted resulting in
     * the default value being inverted.
     */
    public static final class EnableColumnFieldParameter implements NodeParameters {

        /**
         * Config key for the lock column field setting (now called: enable column field).
         */
        public static final String CFG_LOCK_COLUMN = "lockColumn";

        /**
         * The default value for the lock column field setting. (Inverted for the enable column field setting)
         */
        public static final boolean DEFAULT_LOCK_COLUMN = false;

        @Widget(title = "Enable column field",
            description = "When checked, the column field is shown and a column can be selected, else the field is not"
                + " shown and the <i>Default column</i> will be used.")
        @Persistor(EnableColumnFieldPersistor.class)
        @Layout(FormFieldSection.class)
        @ValueReference(EnableColumnFieldValueReference.class)
        boolean m_enableColumnField = !DEFAULT_LOCK_COLUMN;

        static final class EnableColumnFieldPersistor extends AbstractInvertBooleanPersistor {
            public EnableColumnFieldPersistor() {
                super(CFG_LOCK_COLUMN);
            }
        }

        /**
         * Parameter reference which should be attached to a boolean field.
         */
        public static final class EnableColumnFieldValueReference implements ParameterReference<Boolean> {
        }
    }


    /**
     * Persistor mapping from a boolean to the inverted boolean.
     */
    public abstract static class AbstractInvertBooleanPersistor implements NodeParametersPersistor<Boolean> {

        private final String m_configKey;

        /**
         * @param configKey the config key of the field
         */
        protected AbstractInvertBooleanPersistor(final String configKey) {
            m_configKey = configKey;
        }

        @Override
        public Boolean load(final NodeSettingsRO settings) throws InvalidSettingsException {
            return !settings.getBoolean(m_configKey);
        }

        @Override
        public final void save(final Boolean param, final NodeSettingsWO settings) {
            settings.addBoolean(m_configKey, !param);
        }

        @Override
        public final String[][] getConfigPaths() {
            return new String[][]{{m_configKey}};
        }
    }

    /**
     * Choices Provider returning all columns with a domain.
     */
    public abstract static class AbstractDefaultColumnChoicesProvider implements ColumnChoicesProvider {

        private Supplier<Map<String, List<String>>> m_possibleColumnValuesSupplier;

        private final Class<? extends StateProvider<Map<String, List<String>>>> m_possibleColumnValuesMapChoicesProvider; //NOSONAR

        /**
         * @param possibleColumnValuesMapChoicesProvider a state provider providing all columns and their values
         */
        protected AbstractDefaultColumnChoicesProvider(
            final Class<? extends StateProvider<Map<String, List<String>>>> possibleColumnValuesMapChoicesProvider) {
            m_possibleColumnValuesMapChoicesProvider = possibleColumnValuesMapChoicesProvider;
        }

        @Override
        public void init(final StateProviderInitializer initializer) {
            ColumnChoicesProvider.super.init(initializer);
            m_possibleColumnValuesSupplier =
                initializer.computeFromProvidedState(m_possibleColumnValuesMapChoicesProvider);
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

    /**
     * Parameter reference which should be attached to a string field which uses the
     * {@link AbstractDefaultColumnChoicesProvider}.
     */
    public static final class DefaultColumnValueReference implements ParameterReference<String> {
    }

    /**
     * Value provider for a field annotated with {@link DefaultColumnValueReference}.
     */
    public abstract static class AbstractDefaultColumnValueProvider implements StateProvider<String> {
        private final Class<? extends StateProvider<List<TypedStringChoice>>> m_possibleColumnChoicesProvider;

        private Supplier<String> m_defaultColumnSupplier;

        private Supplier<List<TypedStringChoice>> m_possibleColumnChoicesSupplier;

        /**
         * @param possibleColumnChoicesProvider choices provider providing the possible column choices
         */
        protected AbstractDefaultColumnValueProvider(
            final Class<? extends StateProvider<List<TypedStringChoice>>> possibleColumnChoicesProvider) {
            m_possibleColumnChoicesProvider = possibleColumnChoicesProvider;
        }

        @Override
        public void init(final StateProviderInitializer initializer) {
            initializer.computeAfterOpenDialog();
            m_defaultColumnSupplier = initializer.getValueSupplier(DefaultColumnValueReference.class);
            m_possibleColumnChoicesSupplier = initializer.computeFromProvidedState(m_possibleColumnChoicesProvider);
        }

        @Override
        public String computeState(final NodeParametersInput parametersInput) throws StateComputationFailureException {
            final var possibleColumnChoices = m_possibleColumnChoicesSupplier.get();
            if (possibleColumnChoices.isEmpty()) {
                return "";
            }
            final var defaultColumn = m_defaultColumnSupplier.get();
            final var possibleColumnChoicesIds = possibleColumnChoices.stream().map(TypedStringChoice::id).toList();

            if (defaultColumn != null && possibleColumnChoicesIds.contains(defaultColumn)) {
                return defaultColumn;
            }
            return possibleColumnChoicesIds.get(0);
        }
    }

    /**
     * Persistor used to save/load the map of possible columns and values.
     */
    public abstract static class AbstractPossibleValuesPersistor
        implements NodeParametersPersistor<Map<String, List<String>>> {

        private final String m_cfgPossibleColumns;

        private final String m_cfgColumn;

        /**
         * @param cfgPossibleColumns the config key for the possible columns
         * @param cfgColumn the config key for the possible columns and their domain values
         */
        protected AbstractPossibleValuesPersistor(final String cfgPossibleColumns, final String cfgColumn) {
            m_cfgPossibleColumns = cfgPossibleColumns;
            m_cfgColumn = cfgColumn;

        }

        @Override
        public Map<String, List<String>> load(final NodeSettingsRO settings) throws InvalidSettingsException {
            return ValueSelectionFilterUtil.loadPossibleColumnsAndValuesInDialog(settings, m_cfgPossibleColumns,
                m_cfgColumn);
        }

        @Override
        public void save(final Map<String, List<String>> param, final NodeSettingsWO settings) {
            ValueSelectionFilterUtil.savePossibleColumnsAndValues(settings, param, m_cfgPossibleColumns, m_cfgColumn);
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[0][];
        }
    }

    /**
     * Choices provider providing the values for a certain column whose field should be annotated with
     * {@link DefaultColumnValueReference}.
     */
    public abstract static class AbstractDefaultValueChoicesProvider implements StringChoicesProvider {

        private Supplier<String> m_defaultColumnSupplier;

        private Supplier<Map<String, List<String>>> m_possibleColumnValuesSupplier;

        private final Class<? extends StateProvider<Map<String, List<String>>>> m_possibleColumnValuesChoicesProvider;

        /**
         * @param possibleColumnChoicesProvider the choices provider providing the possible columns and their domain
         *            values
         */
        protected AbstractDefaultValueChoicesProvider(
            final Class<? extends StateProvider<Map<String, List<String>>>> possibleColumnChoicesProvider) {
            m_possibleColumnValuesChoicesProvider = possibleColumnChoicesProvider;
        }

        @Override
        public void init(final StateProviderInitializer initializer) {
            initializer.computeBeforeOpenDialog();
            m_defaultColumnSupplier = initializer.computeFromValueSupplier(DefaultColumnValueReference.class);
            m_possibleColumnValuesSupplier =
                initializer.computeFromProvidedState(m_possibleColumnValuesChoicesProvider);
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

    /**
     * Message displayed if the node is not connected/contains no column/contains no column with a domain.
     */
    public static final class NoColumnsAvailableMessage implements StateProvider<Optional<TextMessage.Message>> {

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
