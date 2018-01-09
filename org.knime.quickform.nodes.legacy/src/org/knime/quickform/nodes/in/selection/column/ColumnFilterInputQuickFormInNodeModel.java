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
 * ------------------------------------------------------------------------
 *
 */
package org.knime.quickform.nodes.in.selection.column;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.util.node.quickform.in.AbstractQuickFormInElement;
import org.knime.core.util.node.quickform.in.TwinStringListInputQuickFormInElement;
import org.knime.quickform.nodes.in.selection.QuickFormDataInNodeModel;

/**
 * Node for column filter input quickform.
 *
 * @author Thomas Gabriel, KNIME.com AG, Zurich
 * @since 2.6
 */
public class ColumnFilterInputQuickFormInNodeModel extends
        QuickFormDataInNodeModel<ColumnFilterInputQuickFormInConfiguration> {

    /** Create a new column option quickform node model. */
    protected ColumnFilterInputQuickFormInNodeModel() {
        super(1, 1);
    }

    /** {@inheritDoc} */
    @Override
    protected void createAndPushFlowVariable() throws InvalidSettingsException {
        ColumnFilterInputQuickFormInConfiguration cfg =
            (ColumnFilterInputQuickFormInConfiguration) getConfiguration();
        if (cfg == null) {
            throw new InvalidSettingsException("No settings available");
        }
        String variableName = cfg.getVariableName();
        String[] values = cfg.getValueConfiguration().getValues();
        String value = Arrays.toString(values);
        value = value.substring(1, value.length() - 1);
        pushFlowVariableString(variableName, value);
    }

    /** {@inheritDoc} */
    @Override
    protected ColumnFilterInputQuickFormInConfiguration
            createConfiguration() {
        return new ColumnFilterInputQuickFormInConfiguration();
    }

    /** {@inheritDoc} */
    @Override
    public AbstractQuickFormInElement getQuickFormElement() {
        ColumnFilterInputQuickFormInConfiguration cfg =
            (ColumnFilterInputQuickFormInConfiguration) getConfiguration();
        TwinStringListInputQuickFormInElement e =
                new TwinStringListInputQuickFormInElement(cfg.getLabel(),
                        cfg.getDescription(), cfg.getWeight());
        e.setValues(cfg.getValueConfiguration().getValues());
        PortObjectSpec[] portSpecs = getInPortObjectSpecs();
        if (portSpecs != null && portSpecs[0] != null) {
            final DataTableSpec spec = (DataTableSpec) portSpecs[0];
            e.setChoices(spec.getColumnNames());
        } else {
            e.setChoices(cfg.getAllValues());
        }
        return e;
    }

    /** {@inheritDoc} */
    @Override
    public void loadFromQuickFormElement(final AbstractQuickFormInElement e)
            throws InvalidSettingsException {
        ColumnFilterInputQuickFormInConfiguration cfg =
            (ColumnFilterInputQuickFormInConfiguration) getConfiguration();
        TwinStringListInputQuickFormInElement si =
                AbstractQuickFormInElement.cast(
                        TwinStringListInputQuickFormInElement.class, e);
        cfg.getValueConfiguration().setValues(si.getValues());
        cfg.setAllValues(si.getChoices());
    }

    /** {@inheritDoc} */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs)
            throws InvalidSettingsException {
        createAndPushFlowVariable();
        final DataTableSpec inspec = (DataTableSpec) inSpecs[0];
        ColumnFilterInputQuickFormInConfiguration cfg =
            (ColumnFilterInputQuickFormInConfiguration) getConfiguration();
        final String[] allValues = inspec.getColumnNames();
        cfg.setAllValues(allValues);
        String[] curValues = cfg.getValueConfiguration().getValues();
        final List<String> valueList;
        if (curValues == null) {
            valueList = new ArrayList<String>();
        } else { 
            valueList = new ArrayList<String>(Arrays.asList(curValues));
        }
        valueList.retainAll(Arrays.asList(allValues));
        cfg.getValueConfiguration().setValues(valueList.toArray(new String[0]));
        return new PortObjectSpec[]{createSpec(inspec)};
    }

    /** {@inheritDoc} */
    @Override
    protected PortObject[] execute(final PortObject[] inObjects,
            final ExecutionContext exec) throws Exception {
        createAndPushFlowVariable();
        final DataTableSpec inspec = (DataTableSpec) inObjects[0].getSpec();
        final DataTableSpec outSpec = createSpec(inspec);
        BufferedDataContainer cont = exec.createDataContainer(outSpec, false);
        cont.close();
        return new PortObject[]{cont.getTable()};
    }

    private DataTableSpec createSpec(final DataTableSpec inspec)
            throws InvalidSettingsException {
        ColumnFilterInputQuickFormInConfiguration cfg =
            (ColumnFilterInputQuickFormInConfiguration) getConfiguration();
        final String[] values = cfg.getValueConfiguration().getValues();
        final List<DataColumnSpec> cspecs = new ArrayList<DataColumnSpec>();
        List<String> unknownCols = new ArrayList<String>();
        for (int i = 0; i < values.length; i++) {
            String column = values[i];
            if (column != null && inspec.containsName(column)) {
                cspecs.add(inspec.getColumnSpec(column));
            } else {
                unknownCols.add(column);
            }
        }
        if (!unknownCols.isEmpty()) {
            throw new InvalidSettingsException("Unknown columns "
                    + unknownCols + " selected.");
        }
        return new DataTableSpec(cspecs.toArray(new DataColumnSpec[0]));
    }

}
