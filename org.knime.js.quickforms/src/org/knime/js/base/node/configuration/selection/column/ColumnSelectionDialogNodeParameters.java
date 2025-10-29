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
 *   10 Sept 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.configuration.selection.column;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.function.Supplier;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.webui.node.dialog.defaultdialog.internal.widget.PersistWithin;
import org.knime.js.base.node.base.validation.InputSpecFilter;
import org.knime.js.base.node.configuration.ConfigurationNodeSettings;
import org.knime.js.base.node.configuration.column.InputFilterUtil.AllowAllTypesValueReference;
import org.knime.js.base.node.configuration.column.InputFilterUtil.HideColumnsWithoutDomainValueReference;
import org.knime.js.base.node.configuration.column.InputFilterUtil.InputFilter;
import org.knime.js.base.node.configuration.column.InputFilterUtil.TypeFilterSection;
import org.knime.js.base.node.configuration.column.InputFilterUtil.TypeFilterValueReference;
import org.knime.js.base.node.parameters.filterandselection.ColumnSelectionNodeParameters;
import org.knime.js.base.node.parameters.filterandselection.ColumnSelectionNodeParameters.ColumnValueValueProvider;
import org.knime.node.parameters.NodeParametersInput;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.migration.Migrate;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.widget.choices.ColumnChoicesProvider;

/**
 * WebUI Node Parameters for the Column Selection Configuration.
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz
 */
@SuppressWarnings("restriction")
public class ColumnSelectionDialogNodeParameters extends ConfigurationNodeSettings {

    /**
     * Default constructor
     */
    protected ColumnSelectionDialogNodeParameters() {
        super(ColumnSelectionDialogNodeConfig.class);
    }

    @PersistWithin.PersistEmbedded
    ColumnSelectionNodeParameters m_columnSelectionNodeParameters = new ColumnSelectionNodeParameters();

    @Layout(TypeFilterSection.class)
    @Persist(configKey = ColumnSelectionDialogNodeConfig.CFG_INPUT_FILTER)
    @Migrate(loadDefaultIfAbsent = true)
    InputFilter m_inputFilter = new InputFilter();

    static final class ChangeColumnChoicesProviderModification
        extends ColumnSelectionNodeParameters.ChangeColumnProvider {

        @Override
        public Class<? extends ColumnChoicesProvider> getColumnChoicesProvider() {
            return TypeFilteredColumnChoicesProvider.class;
        }

        @Override
        public Class<? extends ColumnValueValueProvider> getColumnValueValueProvider() {
            return null;
        }

    }

    static final class TypeFilteredColumnValueValueProvider extends ColumnValueValueProvider {

        TypeFilteredColumnValueValueProvider() {
            super(TypeFilteredColumnChoicesProvider.class);
        }

    }

    static final class TypeFilteredColumnChoicesProvider implements ColumnChoicesProvider {

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

}
