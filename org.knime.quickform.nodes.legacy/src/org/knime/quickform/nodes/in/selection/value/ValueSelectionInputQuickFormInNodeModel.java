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
 * History:
 * 24-Febr-2011: created
 */
package org.knime.quickform.nodes.in.selection.value;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.util.node.quickform.AbstractQuickFormElement;
import org.knime.core.util.node.quickform.in.AbstractQuickFormInElement;
import org.knime.core.util.node.quickform.in.ValueSelectionInputQuickFormInElement;
import org.knime.quickform.nodes.in.selection.QuickFormDataInNodeModel;

/**
 * Node for value selection input quickform.
 *
 * @author Thomas Gabriel, KNIME.com AG, Zurich
 * @since 2.6
 */
public class ValueSelectionInputQuickFormInNodeModel extends
        QuickFormDataInNodeModel<ValueSelectionInputQuickFormInConfiguration> {

    /** Creates a new value selection node model. */
    public ValueSelectionInputQuickFormInNodeModel() {
        super(new PortType[]{BufferedDataTable.TYPE},
                new PortType[]{FlowVariablePortObject.TYPE});
    }

    /** {@inheritDoc} */
    @Override
    protected void createAndPushFlowVariable() throws InvalidSettingsException {
        ValueSelectionInputQuickFormInConfiguration cfg =
            (ValueSelectionInputQuickFormInConfiguration) getConfiguration();
        if (cfg == null) {
            throw new InvalidSettingsException("No settings available");
        }
        String variableName = cfg.getVariableName();
        String column = cfg.getValueConfiguration().getColumn();
        String value = cfg.getValueConfiguration().getValue();
        if (value != null) {
            if (column != null) {
                pushFlowVariableString(variableName + "_column", column);
            }
            pushFlowVariableString(variableName, value);
        } else {
            pushFlowVariableString(variableName, "null");
        }
    }

    /** {@inheritDoc} */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs)
            throws InvalidSettingsException {
        ValueSelectionInputQuickFormInConfiguration cfg =
            (ValueSelectionInputQuickFormInConfiguration) getConfiguration();
        if (cfg != null) {
            final DataTableSpec inspec = (DataTableSpec) inSpecs[0];
            cfg.setChoiceValues(createMap(inspec));
        }
        return super.configure(inSpecs);
    }

    /**
     * Creates a map which map column name to a set of possible values.
     * @param spec the data table spec to get column names and domain
     * @return a map with column names and corresponding poss. values
     */
    static Map<String, Set<String>> createMap(final DataTableSpec spec) {
        Map<String, Set<String>> map = new LinkedHashMap<String, Set<String>>();
        for (int i = 0; i < spec.getNumColumns(); i++) {
            DataColumnSpec column = spec.getColumnSpec(i);
            if (column.getDomain().hasValues()) {
                Set<DataCell> values = column.getDomain().getValues();
                Set<String> strValues = new LinkedHashSet<String>();
                for (DataCell v : values) {
                    strValues.add(v.toString());
                }
                map.put(column.getName(), strValues);
            }
        }
        return map;
    }

    /** {@inheritDoc} */
    @Override
    public ValueSelectionInputQuickFormInConfiguration createConfiguration() {
        return new ValueSelectionInputQuickFormInConfiguration();
    }

    /** {@inheritDoc} */
    @Override
    public AbstractQuickFormInElement getQuickFormElement() {
        ValueSelectionInputQuickFormInConfiguration cfg =
            (ValueSelectionInputQuickFormInConfiguration) getConfiguration();
        ValueSelectionInputQuickFormInElement e =
                new ValueSelectionInputQuickFormInElement(cfg.getLabel(),
                        cfg.getDescription(), cfg.getWeight());
        final DataTableSpec spec = (DataTableSpec) getInPortObjectSpecs()[0];
        e.setChoiceValues(createMap(spec));
        ValueSelectionInputQuickFormValueInConfiguration valCfg =
                cfg.getValueConfiguration();
        e.setColumn(valCfg.getColumn());
        e.setValue(valCfg.getValue());
        e.setLockColumn(valCfg.getLockColumn());
        return e;
    }

    /** {@inheritDoc} */
    @Override
    public void loadFromQuickFormElement(final AbstractQuickFormInElement e)
            throws InvalidSettingsException {
        ValueSelectionInputQuickFormValueInConfiguration valCfg
                = ((ValueSelectionInputQuickFormInConfiguration)
                        getConfiguration()).getValueConfiguration();
        ValueSelectionInputQuickFormInElement sol =
                AbstractQuickFormElement.cast(
                        ValueSelectionInputQuickFormInElement.class, e);
        valCfg.setColumn(sol.getColumn());
        valCfg.setValue(sol.getValue());
    }

}
