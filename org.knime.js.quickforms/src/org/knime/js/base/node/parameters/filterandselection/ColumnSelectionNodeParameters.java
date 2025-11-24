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
 *   29 Oct 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.parameters.filterandselection;

import java.util.List;
import java.util.function.Supplier;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.webui.node.dialog.defaultdialog.internal.widget.PersistWithin;
import org.knime.core.webui.node.dialog.defaultdialog.util.updates.StateComputationFailureException;
import org.knime.core.webui.node.dialog.defaultdialog.widget.Modification;
import org.knime.core.webui.node.dialog.defaultdialog.widget.Modification.WidgetGroupModifier;
import org.knime.js.base.node.base.selection.column.ColumnSelectionNodeConfig;
import org.knime.js.base.node.base.selection.column.ColumnSelectionNodeValue;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.FormFieldSection;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.OutputSection;
import org.knime.js.base.node.parameters.OverwrittenByValueMessage;
import org.knime.node.parameters.NodeParameters;
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
import org.knime.node.parameters.widget.choices.ChoicesProvider;
import org.knime.node.parameters.widget.choices.ColumnChoicesProvider;
import org.knime.node.parameters.widget.choices.TypedStringChoice;
import org.knime.node.parameters.widget.choices.util.AllColumnsProvider;
import org.knime.node.parameters.widget.message.TextMessage;

/**
 * Shared WebUI Node Parameters for Column Selection Configuration and Widget.
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz
 */
@SuppressWarnings("restriction")
@LoadDefaultsForAbsentFields
public final class ColumnSelectionNodeParameters implements NodeParameters {

    @TextMessage(ColumnSelectionOverwrittenByValueMessage.class)
    @Layout(OutputSection.Top.class)
    Void m_overwrittenByValueMessage;

    private static final class DefaultValue implements NodeParameters {
        @Widget(title = "Default value", description = "The column that is selected by default.")
        @Layout(OutputSection.Top.class)
        @Persistor(DefaultColumnValuePersistor.class)
        @ChoicesProvider(AllColumnsProvider.class)
        @ValueProvider(ColumnValueValueProvider.class)
        @ValueReference(ColumnValueReference.class)
        @Modification.WidgetReference(ColumnValueReference.class)
        String m_columnValue;
    }

    DefaultValue m_defaultValue = new DefaultValue();

    @PersistWithin.PersistEmbedded
    @Layout(FormFieldSection.class)
    SingleSelectionComponentParameters m_singleSelectionComponentParameters = new SingleSelectionComponentParameters();

    @ChoicesProvider(AllColumnsProvider.class)
    @Modification.WidgetReference(PossibleColumnsModificationReference.class)
    @Persist(configKey = ColumnSelectionNodeConfig.CFG_POSSIBLE_COLUMNS)
    String[] m_possibleColumns = new String[0];

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

    private static final class ColumnValueReference implements ParameterReference<String>, Modification.Reference {
    }

    private static final class PossibleColumnsModificationReference implements Modification.Reference {
    }

    /**
     * A value provider to set a default column. To be used with the {@link ChangeColumnProvider} modification
     */
    public static class ColumnValueValueProvider implements StateProvider<String> {

        private Supplier<String> m_columnValueSupplier;

        private Supplier<List<TypedStringChoice>> m_possibleColumnChoicesSupplier;

        private Class<? extends ColumnChoicesProvider> m_columnChoicesProviderClass = AllColumnsProvider.class;

        ColumnValueValueProvider() {
            // for serialization
        }

        /**
         * @param columnChoicesProviderClass the column choices provider from which to extract a selected column
         */
        public ColumnValueValueProvider(final Class<? extends ColumnChoicesProvider> columnChoicesProviderClass) {
            m_columnChoicesProviderClass = columnChoicesProviderClass;
        }

        @Override
        public final void init(final StateProviderInitializer initializer) {
            initializer.computeAfterOpenDialog();
            m_columnValueSupplier = initializer.getValueSupplier(ColumnValueReference.class);
            m_possibleColumnChoicesSupplier = initializer.computeFromProvidedState(m_columnChoicesProviderClass);
        }

        @Override
        public final String computeState(final NodeParametersInput parametersInput)
            throws StateComputationFailureException {
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
        extends OverwrittenByValueMessage<ColumnSelectionNodeValue> {

        @Override
        protected String valueToString(final ColumnSelectionNodeValue value) {
            return value.getColumn();
        }

    }

    /**
     * Modification to change the choices provider providing the selectable columns.
     */
    public abstract static class ChangeColumnProvider implements Modification.Modifier {
        @Override
        public final void modify(final WidgetGroupModifier group) {
            group.find(ColumnValueReference.class) //
                .modifyAnnotation(ChoicesProvider.class).withValue(getColumnChoicesProvider()).modify();
            group.find(ColumnValueReference.class) //
                .modifyAnnotation(ValueProvider.class).withValue(getColumnValueValueProvider()).modify();
            group.find(PossibleColumnsModificationReference.class) //
                .modifyAnnotation(ChoicesProvider.class).withValue(getColumnChoicesProvider()).modify();
        }

        /**
         * @return the column choices provider to use
         */
        public abstract Class<? extends ColumnChoicesProvider> getColumnChoicesProvider();

        /**
         * @return the value provider to use,
         */
        public abstract Class<? extends ColumnValueValueProvider> getColumnValueValueProvider();
    }

}
