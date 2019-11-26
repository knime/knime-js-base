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
 *   27 May 2019 (albrecht): created
 */
package org.knime.js.base.node.configuration.filter.column;

import org.apache.commons.lang.StringUtils;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.BufferedDataTableHolder;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.js.base.node.base.filter.column.ColumnFilterNodeUtil;
import org.knime.js.base.node.base.validation.InputSpecFilter;
import org.knime.js.base.node.base.validation.Validator;
import org.knime.js.base.node.base.validation.min.column.MinNumColumnsValidatorFactory;
import org.knime.js.base.node.base.validation.modular.ModularValidatorConfig;
import org.knime.js.base.node.base.validation.modular.ModularValidatorFactory;
import org.knime.js.base.node.configuration.DialogNodeModel;

/**
 * Node model for the column filter configuration node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class ColumnFilterDialogNodeModel extends
    DialogNodeModel<ColumnFilterDialogNodeRepresentation, ColumnFilterDialogNodeValue, ColumnFilterDialogNodeConfig>
    implements BufferedDataTableHolder {

    static final ModularValidatorFactory<DataTableSpec, BufferedDataTable> VALIDATOR_FACTORY =
        new ModularValidatorFactory<>(MinNumColumnsValidatorFactory.INSTANCE);

    private DataTableSpec m_spec = new DataTableSpec();

    private BufferedDataTable m_inTable = null;

    /**
     * Creates a new column filter configuration node model
     */
    public ColumnFilterDialogNodeModel() {
        super(new PortType[]{BufferedDataTable.TYPE}, new PortType[]{BufferedDataTable.TYPE});
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        final DataTableSpec spec = (DataTableSpec)inSpecs[0];
        m_spec = spec;
        final DataTableSpec filteredSpec = createSpecFilter().filter(spec);
        updateValuesFromSpec(filteredSpec);
        updateColumns(filteredSpec);
        createAndPushFlowVariable();
        final DataTableSpec outSpec = ColumnFilterNodeUtil.createSpec(filteredSpec, getRelevantValue().getColumns());
        createSpecValidator().validateSpec(outSpec);
        return new DataTableSpec[]{outSpec};
    }

    private InputSpecFilter createSpecFilter() {
        return getConfig().getInputSpecFilterConfig().createFilter();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] execute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        m_inTable = (BufferedDataTable)inObjects[0];
        DataTableSpec unfilteredSpec = (DataTableSpec)inObjects[0].getSpec();
        final InputSpecFilter specFilter = createSpecFilter();
        DataTableSpec inSpec = specFilter.filter(unfilteredSpec);
        updateColumns(inSpec);
        createAndPushFlowVariable();
        DataTableSpec outSpec = ColumnFilterNodeUtil.createSpec(inSpec, getRelevantValue().getColumns());
        final ColumnRearranger cr = new ColumnRearranger(unfilteredSpec);
        cr.keepOnly(outSpec.getColumnNames());
        final BufferedDataTable outTable = exec.createColumnRearrangeTable(m_inTable, cr, exec);
        createSpecValidator().validateObject(outTable);
        return new BufferedDataTable[]{outTable};
    }

    private Validator<DataTableSpec, BufferedDataTable> createSpecValidator() {
        return createSpecValidator(getConfig().getValidatorConfig());
    }

    private static Validator<DataTableSpec, BufferedDataTable>
        createSpecValidator(final ModularValidatorConfig config) {
        return VALIDATOR_FACTORY.createValidator(config);
    }

    /**
     * Validates the settings provided by the user in either the node dialog or the component dialog.
     *
     * @param spec the (filtered) {@link DataTableSpec} of the node input
     * @param included the names of the columns included by the user settings
     * @param validatorConfig the {@link ModularValidatorConfig} configured by the user in the node dialog
     */
    static void validateUserSettings(final DataTableSpec spec, final String[] included,
        final ModularValidatorConfig validatorConfig) throws InvalidSettingsException {
        final ColumnRearranger cr = new ColumnRearranger(spec);
        cr.keepOnly(included);
        createSpecValidator(validatorConfig).validateSpec(cr.createSpec());
    }

    /**
     * Pushes the current value as flow variable.
     */
    private void createAndPushFlowVariable() {
        final String[] values = getRelevantValue().getColumns();
        pushFlowVariableString(getConfig().getFlowVariableName(), StringUtils.join(values, ","));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnFilterDialogNodeValue createEmptyDialogValue() {
        return new ColumnFilterDialogNodeValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnFilterDialogNodeConfig createEmptyConfig() {
        return new ColumnFilterDialogNodeConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ColumnFilterDialogNodeRepresentation getRepresentation() {
        return new ColumnFilterDialogNodeRepresentation(getRelevantValue(), getConfig(), getSpec());
    }

    private void updateValuesFromSpec(final DataTableSpec spec) {
        getConfig().getDefaultValue().updateFromSpec(spec);
        if (getDialogValue() != null) {
            getDialogValue().updateFromSpec(spec);
        }
    }

    /**
     * Update the possible columns in the config
     *
     * @param spec The input spec
     */
    private void updateColumns(final DataTableSpec spec) {
        getConfig().getColumnFilterConfig().setPossibleColumns(spec.getColumnNames());
    }

    /**
     * @return The spec of the input table
     */
    private DataTableSpec getSpec() {
        final DataTableSpec unfilteredSpec = m_inTable != null ? m_inTable.getDataTableSpec() : m_spec;
        return createSpecFilter().filter(unfilteredSpec);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        m_inTable = null;
        super.reset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BufferedDataTable[] getInternalTables() {
        return m_inTable != null ? new BufferedDataTable[]{m_inTable} : new BufferedDataTable[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInternalTables(final BufferedDataTable[] tables) {
        if (tables.length > 0) {
            m_inTable = tables[0];
            DataTableSpec spec = getSpec();
            if (spec != null) {
                updateColumns(spec);
                updateValuesFromSpec(spec);
            }
        }
    }

}
