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
 *   3 Nov 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.parameters.filterandselection;

import static org.knime.js.base.node.parameters.filterandselection.MultipleSelectionComponentParameters.CFG_TYPE;
import static org.knime.js.base.node.parameters.filterandselection.MultipleSelectionComponentParameters.DEFAULT_TYPE;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.knime.core.webui.node.dialog.defaultdialog.internal.widget.PersistWithin;
import org.knime.core.webui.node.dialog.defaultdialog.util.updates.StateComputationFailureException;
import org.knime.core.webui.node.dialog.defaultdialog.widget.Modification;
import org.knime.core.webui.node.dialog.defaultdialog.widget.Modification.WidgetGroupModifier;
import org.knime.js.base.node.base.filter.column.ColumnFilterNodeConfig;
import org.knime.js.base.node.base.filter.column.ColumnFilterNodeValue;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.FormFieldSection;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.OutputSection;
import org.knime.js.base.node.parameters.OverwrittenByValueMessage;
import org.knime.js.base.node.parameters.filterandselection.LimitVisibleOptionsParameters.LimitVisibleOptionsParametersModifier;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.NodeParametersInput;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.migration.LoadDefaultsForAbsentFields;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.persistence.Persistor;
import org.knime.node.parameters.persistence.legacy.LegacyColumnFilterPersistor;
import org.knime.node.parameters.updates.StateProvider;
import org.knime.node.parameters.updates.ValueProvider;
import org.knime.node.parameters.widget.choices.ChoicesProvider;
import org.knime.node.parameters.widget.choices.ColumnChoicesProvider;
import org.knime.node.parameters.widget.choices.TypedStringChoice;
import org.knime.node.parameters.widget.choices.filter.ColumnFilter;
import org.knime.node.parameters.widget.choices.util.AllColumnsProvider;
import org.knime.node.parameters.widget.message.TextMessage;
import org.knime.node.parameters.widget.number.NumberInputWidgetValidation.MinValidation;

/**
 * Shared WebUI Node Parameters for Column Filter Configuration and Widget.
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz
 */
@SuppressWarnings("restriction")
@LoadDefaultsForAbsentFields
public final class ColumnFilterNodeParameters implements NodeParameters {

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
        private static final class ColumnFilterLevel1 implements NodeParameters {
            @Widget(title = "Default values", description = "The columns that are selected by default.")
            @Persistor(ColumnFilterPersistor.class)
            @ChoicesProvider(AllColumnsProvider.class)
            @Modification.WidgetReference(ColumnFilterModificationReference.class)
            ColumnFilter m_columnFilter = new ColumnFilter().withIncludeUnknownColumns();
        }

        @Layout(OutputSection.Top.class)
        @Persist(configKey = ColumnFilterNodeConfig.CFG_COLUMN_FILTER)
        ColumnFilterLevel1 m_columnFilterLevel1 = new ColumnFilterLevel1();

        @Persist(configKey = ColumnFilterNodeValue.CFG_COLUMNS)
        @ValueProvider(SelectableColumnsValueProvider.class)
        @Modification.WidgetReference(ColumnsModificationReference.class)
        String[] m_columns = new String[0];
    }

    DefaultValue m_defaultValue = new DefaultValue();

    @PersistWithin.PersistEmbedded
    @Modification(LimitVisibleOptionsModification.class)
    @Layout(FormFieldSection.class)
    LimitVisibleOptionsParameters m_limitVisibleOptionsParameters = new LimitVisibleOptionsParameters(true);

    private static final class LimitVisibleOptionsModification extends LimitVisibleOptionsParametersModifier {

        @Override
        public String getLimitNumVisOptionsDescription() {
            return """
                    By default the filter component adjusts its height to display all possible choices without a \
                    scroll bar. If the setting is enabled, you will be able to limit the number of visible options in \
                    case you have too many of them.""";
        }

        @Override
        public String getNumVisOptionsDescription() {
            return """
                    A number of options visible in the filter component without a vertical scroll bar. Changing this \
                    value will also affect the component's height. Notice that the height cannot be less than the \
                    overall height of the control buttons in the middle.""";
        }

        @Override
        public Class<? extends MinValidation> getMinNumVisOptions() {
            return IsMin5Validation.class;
        }

    }

    private static final class IsMin5Validation extends MinValidation {

        @Override
        protected double getMin() {
            return 5;
        }

    }

    @ValueProvider(SelectableColumnsValueProvider.class)
    @Persist(configKey = ColumnFilterNodeConfig.CFG_POSSIBLE_COLUMNS)
    @Modification.WidgetReference(PossibleColumnsModificationReference.class)
    String[] m_possibleColumns = new String[0];

    @Persist(configKey = CFG_TYPE)
    String m_type = DEFAULT_TYPE;

    private static final class ColumnFilterModificationReference implements Modification.Reference {
    }

    private static final class PossibleColumnsModificationReference implements Modification.Reference {
    }

    private static final class ColumnsModificationReference implements Modification.Reference {
    }

    private static final class ColumnFilterPersistor extends LegacyColumnFilterPersistor {

        protected ColumnFilterPersistor() {
            super(ColumnFilterNodeConfig.CFG_COLUMN_FILTER);
        }

    }

    /**
     * A value provider to set possible columns. TO be used with the {@link AbstractChangeProvidersModification}
     */
    public static class SelectableColumnsValueProvider implements StateProvider<String[]> {

        private Class<? extends ColumnChoicesProvider> m_columnChoicesProviderClass = AllColumnsProvider.class;

        private Supplier<List<TypedStringChoice>> m_columnChoicesSupplier;

        SelectableColumnsValueProvider() {
        }

        /**
         * @param columnChoicesProviderClass the choices provider providing possible columns
         */
        protected SelectableColumnsValueProvider(
            final Class<? extends ColumnChoicesProvider> columnChoicesProviderClass) {
            m_columnChoicesProviderClass = columnChoicesProviderClass;
        }

        @Override
        public void init(final StateProviderInitializer initializer) {
            initializer.computeBeforeOpenDialog();
            m_columnChoicesSupplier = initializer.computeFromProvidedState(m_columnChoicesProviderClass);
        }

        @Override
        public String[] computeState(final NodeParametersInput parametersInput)
            throws StateComputationFailureException {
            return m_columnChoicesSupplier.get().stream().map(tsc -> tsc.id()).toArray(String[]::new);
        }
    }

    private static final class ColumnFilterOverwrittenByValueMessage
        extends OverwrittenByValueMessage<ColumnFilterNodeValue> {

        @Override
        protected String valueToString(final ColumnFilterNodeValue value) {
            return Arrays.toString(value.getColumns());
        }

    }

    /**
     * Modification to change the choices provider providing the selectable columns.
     */
    public abstract static class AbstractChangeProvidersModification implements Modification.Modifier {
        @Override
        public final void modify(final WidgetGroupModifier group) {
            group.find(ColumnFilterModificationReference.class) //
                .modifyAnnotation(ChoicesProvider.class).withValue(getColumnChoicesProvider()).modify();
            group.find(ColumnsModificationReference.class) //
                .modifyAnnotation(ValueProvider.class).withValue(getColumnValueProvider()).modify();
            group.find(PossibleColumnsModificationReference.class) //
                .modifyAnnotation(ValueProvider.class).withValue(getColumnValueProvider()).modify();
        }

        /**
         * @return the column choices provider to use
         */
        public abstract Class<? extends ColumnChoicesProvider> getColumnChoicesProvider();

        /**
         * @return the value provider to use
         */
        public abstract Class<? extends SelectableColumnsValueProvider> getColumnValueProvider();
    }

}
