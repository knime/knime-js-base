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
 *   25 Nov 2025 (AI Migration): created
 */
package org.knime.dynamic.js.base.autocompletetextfield;

import java.util.List;
import java.util.function.Supplier;

import org.knime.core.webui.node.dialog.defaultdialog.util.updates.StateComputationFailureException;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.FormFieldSection;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.OutputSection;
import org.knime.js.base.node.widget.WidgetNodeParametersLabeled;
import org.knime.node.parameters.NodeParametersInput;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.After;
import org.knime.node.parameters.layout.Before;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.layout.Section;
import org.knime.node.parameters.migration.LoadDefaultsForAbsentFields;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.updates.StateProvider;
import org.knime.node.parameters.updates.ValueProvider;
import org.knime.node.parameters.updates.ValueReference;
import org.knime.node.parameters.widget.choices.ChoicesProvider;
import org.knime.node.parameters.widget.choices.TypedStringChoice;
import org.knime.node.parameters.widget.choices.util.CompatibleColumnsProvider.StringColumnsProvider;

/**
 * Node parameters for the Autocomplete Text Widget node.
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz, Germany
 */
@LoadDefaultsForAbsentFields
@SuppressWarnings("restriction")
public final class AutocompleteTextFieldNodeParameters extends WidgetNodeParametersLabeled {

    @After(FormFieldSection.class)
    @Before(OutputSection.class)
    @Section(title = "Data", description = "Settings regarding the data used to generate the autocomplete options.")
    interface DataSection {
    }

    @Widget(title = "Max rows", description = "The number of rows to calculate the autocomplete options from.")
    @ValueReference(MaxRowsReference.class)
    @Layout(DataSection.class)
    int m_maxRows = 2500;

    @Widget(title = "Autocomplete options column",
        description = "Select the column that contains the autocomplete options.")
    @ValueReference(AutoSelectReference.class)
    @ChoicesProvider(StringColumnsProvider.class)
    @ValueProvider(AutoSelectValueProvider.class)
    @Layout(DataSection.class)
    String m_autoSelect;

    @Widget(title = "Restrict to options",
        description = "Check if value has to be one of the given options. If not checked any string is accepted.")
    @Layout(DataSection.class)
    boolean m_restrict;

    @Widget(title = "Default value", description = "An optional default value.")
    @Layout(OutputSection.class)
    @Persist(configKey = "string_input")
    String m_stringInput = "";

    /** Not used in the autocomplete Text Field, but needed for backwards compatibility. */
    boolean m_generateImage;

    private static final class MaxRowsReference implements ParameterReference<Integer> {
    }

    private static final class AutoSelectReference implements ParameterReference<String> {
    }

    private static final class AutoSelectValueProvider implements StateProvider<String> {

        private Supplier<List<TypedStringChoice>> m_possibleColumns;

        private Supplier<String> m_currentColumn;

        @Override
        public void init(StateProviderInitializer initializer) {
            m_possibleColumns = initializer.computeFromProvidedState(StringColumnsProvider.class);
            m_currentColumn = initializer.getValueSupplier(AutoSelectReference.class);
        }

        @Override
        public String computeState(NodeParametersInput parametersInput) throws StateComputationFailureException {
            final var currentColumn = m_currentColumn.get();
            if (currentColumn != null) {
                return currentColumn;
            }
            final var possibleColumns = m_possibleColumns.get();
            return possibleColumns.isEmpty() ? null : possibleColumns.get(possibleColumns.size() - 1).id();
        }

    }

}
