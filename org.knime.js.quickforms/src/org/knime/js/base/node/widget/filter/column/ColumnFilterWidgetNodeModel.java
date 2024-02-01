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
package org.knime.js.base.node.widget.filter.column;

import java.util.Arrays;

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
import org.knime.core.node.workflow.VariableType;
import org.knime.js.base.node.base.filter.column.ColumnFilterNodeUtil;
import org.knime.js.base.node.base.filter.column.ColumnFilterNodeValue;
import org.knime.js.base.node.widget.WidgetNodeModel;

/**
 * The node model for the column filter widget node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class ColumnFilterWidgetNodeModel extends
    WidgetNodeModel<ReExecutableColumnFilterNodeRepresentation<ColumnFilterNodeValue>, ColumnFilterNodeValue, ColumnFilterWidgetConfig>
    implements BufferedDataTableHolder {

    /**
     * The version of the Column Filter Widget node. The versions correspond to KNIME Analytics Platform versions in
     * which changes were made to the node.
     *
     * @author Adrian Nembach, KNIME GmbH, Konstanz, Germany
     */
    public enum Version {
            /**
             * The first version of the Column Filter Widget node.
             */
            PRE_4_1,
            /**
             * The Column Filter Widget node in KNIME Analytics Platform 4.1.0. Following changes were made: - If the
             * node is dragged onto the workbench and the dialog isn't opened before execution, the node now includes
             * all rows by default (similar to the Column Filter node). - The node now outputs a string array flow
             * variable instead of a comma separated string
             */
            V_4_1;
    }

    private DataTableSpec m_spec = new DataTableSpec();

    private BufferedDataTable m_inTable = null;

    private final Version m_version;

    /**
     * Creates a new column filter widget node model
     *
     * @param viewName the interactive view name
     * @deprecated as of KNIME AP 4.1.0 use
     *             {@link ColumnFilterWidgetNodeModel#ColumnFilterWidgetNodeModel(String, Version)} instead
     */
    @Deprecated
    protected ColumnFilterWidgetNodeModel(final String viewName) {
        this(viewName, Version.PRE_4_1);
    }

    /**
     * Creates a new column filter widget node model
     *
     * @param viewName the interactive view name
     * @param version the version of the Column Filter Widget
     */
    protected ColumnFilterWidgetNodeModel(final String viewName, final Version version) {
        super(new PortType[]{BufferedDataTable.TYPE}, new PortType[]{BufferedDataTable.TYPE}, viewName);
        m_version = version;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        m_spec = (DataTableSpec)inSpecs[0];
        updateValuesFromSpec((DataTableSpec)inSpecs[0]);
        updateColumns((DataTableSpec)inSpecs[0]);
        createAndPushFlowVariable();
        return new DataTableSpec[]{
            ColumnFilterNodeUtil.createSpec((DataTableSpec)inSpecs[0], getRelevantValue().getColumns())};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] performExecute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        m_inTable = (BufferedDataTable)inObjects[0];
        DataTableSpec inSpec = (DataTableSpec)inObjects[0].getSpec();
        updateColumns(inSpec);
        createAndPushFlowVariable();
        DataTableSpec outSpec =
            ColumnFilterNodeUtil.createSpec((DataTableSpec)inObjects[0].getSpec(), getRelevantValue().getColumns());
        ColumnRearranger rearranger = new ColumnRearranger(inSpec);
        rearranger.keepOnly(outSpec.getColumnNames());
        BufferedDataTable outTable = exec.createColumnRearrangeTable((BufferedDataTable)inObjects[0], rearranger, exec);
        return new BufferedDataTable[]{outTable};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ColumnFilterNodeValue copyConfigToViewValue(final ColumnFilterNodeValue currentViewValue,
        final ColumnFilterWidgetConfig config, final ColumnFilterWidgetConfig previousConfig) {
        var defaultVal = config.getDefaultValue();
        var previousDefaultVal = previousConfig.getDefaultValue();
        if (!Arrays.equals(defaultVal.getColumns(), previousDefaultVal.getColumns())) {
            currentViewValue.setColumns(defaultVal.getColumns());
        }
        if (!defaultVal.getSettings().equals(previousDefaultVal.getSettings())) {
            currentViewValue.setSettings(defaultVal.getSettings());
        }
        return currentViewValue;
    }

    /**
     * Pushes the current value as flow variable.
     */
    private void createAndPushFlowVariable() {
        final String[] values = getRelevantValue().getColumns();
        if (m_version == Version.PRE_4_1) {
            pushFlowVariableString(getConfig().getFlowVariableName(), StringUtils.join(values, ","));
        } else {
            pushFlowVariable(getConfig().getFlowVariableName(), VariableType.StringArrayType.INSTANCE, values);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnFilterNodeValue createEmptyViewValue() {
        return new ColumnFilterNodeValue(m_version == Version.V_4_1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org.knime.js.base.node.widget.filter.column";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnFilterWidgetConfig createEmptyConfig() {
        return new ColumnFilterWidgetConfig(m_version);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ReExecutableColumnFilterNodeRepresentation<ColumnFilterNodeValue> getRepresentation() {
        ColumnFilterWidgetConfig config = getConfig();
        return new ReExecutableColumnFilterNodeRepresentation<ColumnFilterNodeValue>(getRelevantValue(),
            config.getDefaultValue(), config.getColumnFilterConfig(), config.getLabelConfig(), config.isEnableSearch(),
            config.getTriggerReExecution());
    }

    private void updateValuesFromSpec(final DataTableSpec spec) {
        getConfig().getDefaultValue().updateFromSpec(spec);
        if (getViewValue() != null) {
            getViewValue().updateFromSpec(spec);
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
        return m_inTable != null ? m_inTable.getDataTableSpec() : m_spec;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performReset() {
        m_inTable = null;
        super.performReset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void useCurrentValueAsDefault() {
        getConfig().getDefaultValue().setColumns(getViewValue().getColumns());
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
