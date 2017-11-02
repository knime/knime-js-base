/*
 * ------------------------------------------------------------------------
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
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
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
 * ------------------------------------------------------------------------
 *
 */
package org.knime.quickform.nodes.in.selection.value;

import java.util.Arrays;

import org.knime.base.node.io.filereader.DataCellFactory;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.util.node.quickform.AbstractQuickFormElement;
import org.knime.core.util.node.quickform.in.AbstractQuickFormInElement;
import org.knime.core.util.node.quickform.in.ValueFilterInputQuickFormInElement;
import org.knime.quickform.nodes.in.selection.QuickFormDataInNodeModel;

/**
 * Node for value filter input quickform.
 *
 * @author Thomas Gabriel, KNIME.com AG, Zurich
 * @since 2.6
 */
public class ValueFilterInputQuickFormInNodeModel extends
        QuickFormDataInNodeModel<ValueFilterInputQuickFormInConfiguration> {

    /** Create a new value filter quickform node model. */
    protected ValueFilterInputQuickFormInNodeModel() {
        super(1, 1);
    }

    /** {@inheritDoc} */
    @Override
    protected void createAndPushFlowVariable() throws InvalidSettingsException {
        ValueFilterInputQuickFormInConfiguration cfg =
            (ValueFilterInputQuickFormInConfiguration) getConfiguration();
        if (cfg == null) {
            throw new InvalidSettingsException("No settings available");
        }
        String variableName = cfg.getVariableName();
        ValueFilterInputQuickFormValueInConfiguration valCfg =
                cfg.getValueConfiguration();
        String column = valCfg.getColumn();
        pushFlowVariableString(variableName + "_column", column);
        String[] values = valCfg.getValues();
        String value = Arrays.toString(values);
        value = value.substring(1, value.length() - 1);
        pushFlowVariableString(variableName, value);
    }

    /** {@inheritDoc} */
    @Override
    protected ValueFilterInputQuickFormInConfiguration
            createConfiguration() {
        return new ValueFilterInputQuickFormInConfiguration();
    }

    /** {@inheritDoc} */
    @Override
    public AbstractQuickFormInElement getQuickFormElement() {
        ValueFilterInputQuickFormInConfiguration cfg =
            (ValueFilterInputQuickFormInConfiguration) getConfiguration();
        ValueFilterInputQuickFormInElement e =
                new ValueFilterInputQuickFormInElement(cfg.getLabel(),
                        cfg.getDescription(), cfg.getWeight());
        final DataTableSpec spec = (DataTableSpec) getInPortObjectSpecs()[0];
        if (spec != null) {
            ValueFilterInputQuickFormValueInConfiguration valCfg =
                    cfg.getValueConfiguration();
            e.setChoiceValues(
                    ValueSelectionInputQuickFormInNodeModel.createMap(spec),
                    valCfg.getColumn(), valCfg.getValues());
            e.setLockColumn(valCfg.getLockColumn());
        }
        return e;
    }

    /** {@inheritDoc} */
    @Override
    public void loadFromQuickFormElement(final AbstractQuickFormInElement e)
            throws InvalidSettingsException {
        ValueFilterInputQuickFormInConfiguration cfg =
            (ValueFilterInputQuickFormInConfiguration) getConfiguration();
        ValueFilterInputQuickFormInElement vf =
                AbstractQuickFormElement.cast(
                        ValueFilterInputQuickFormInElement.class, e);
        ValueFilterInputQuickFormValueInConfiguration valCfg =
                cfg.getValueConfiguration();
        valCfg.setColumn(vf.getColumn());
        valCfg.setValues(vf.getValues());
    }

    /** {@inheritDoc} */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs)
            throws InvalidSettingsException {
        super.configure(inSpecs);
        ValueFilterInputQuickFormInConfiguration cfg =
            (ValueFilterInputQuickFormInConfiguration) getConfiguration();
        final DataTableSpec inspec = (DataTableSpec) inSpecs[0];
        if (cfg != null) {
            cfg.setChoiceValues(ValueSelectionInputQuickFormInNodeModel
                    .createMap(inspec));
        }
        DataTableSpec spec = createSpec(inspec);
        return new DataTableSpec[]{spec};
    }

    /** {@inheritDoc} */
    @Override
    protected PortObject[] execute(final PortObject[] inObjects,
            final ExecutionContext exec) throws Exception {
        createAndPushFlowVariable();
        final DataTableSpec inspec = (DataTableSpec) inObjects[0].getSpec();
        final DataTableSpec outSpec = createSpec(inspec);
        BufferedDataContainer cont = exec.createDataContainer(outSpec, false);
        ValueFilterInputQuickFormInConfiguration cfg =
            (ValueFilterInputQuickFormInConfiguration) getConfiguration();
        String[] values;
        if (cfg != null) {
            values = cfg.getValueConfiguration().getValues();
        } else {
            values = new String[0];
        }

        DataCellFactory cellFactory = new DataCellFactory();
        DataType type = outSpec.getColumnSpec(0).getType();
        for (int i = 0; i < values.length; i++) {
            DataCell result = cellFactory.createDataCellOfType(
                    type, values[i]);
            cont.addRowToTable(new DefaultRow(RowKey.createRowKey(i), result));
        }
        cont.close();
        return new PortObject[]{cont.getTable()};
    }

    private DataTableSpec createSpec(final DataTableSpec inspec)
            throws InvalidSettingsException {
        ValueFilterInputQuickFormInConfiguration cfg =
            (ValueFilterInputQuickFormInConfiguration) getConfiguration();
        String column = cfg.getValueConfiguration().getColumn();
        if (column != null && inspec.containsName(column)) {
            return new DataTableSpec(inspec.getColumnSpec(column));
        } else {
            throw new InvalidSettingsException("Unknown column "
                    + cfg.getValueConfiguration().getColumn() + " selected.");
        }
    }
}
