/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME GmbH, Konstanz, Germany
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
 */
package org.knime.js.base.node.quickform.input.slider;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.web.ValidationError;
import org.knime.js.base.node.quickform.QuickFormFlowVariableNodeModel;

/**
 * The model for the slider input quick form node.
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
public class SliderInputQuickFormNodeModel
        extends QuickFormFlowVariableNodeModel
        <SliderInputQuickFormRepresentation,
        SliderInputQuickFormValue,
        SliderInputQuickFormConfig> {

    /**
     * @param viewName
     */
    protected SliderInputQuickFormNodeModel(final String viewName) {
        super(new PortType[]{BufferedDataTable.TYPE_OPTIONAL}, new PortType[]{FlowVariablePortObject.TYPE}, viewName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SliderInputQuickFormValue createEmptyViewValue() {
        return new SliderInputQuickFormValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org_knime_js_base_node_quickform_input_slider";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        DataTableSpec spec = (DataTableSpec)inSpecs[0];
        if (spec != null) {
            setDomainRange(spec);
        }
        return super.configure(inSpecs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] execute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        if (getConfig().getSliderSettings() == null) {
            throw new InvalidSettingsException("No settings defined. Please configure the node.");
        }
        BufferedDataTable table = (BufferedDataTable)inObjects[0];
        if (table != null) {
            setDomainRange(table.getDataTableSpec());
        }
        return super.execute(inObjects, exec);
    }

    private void setDomainRange(final DataTableSpec spec) {
        SliderInputQuickFormConfig config = getConfig();
        if (config.getSliderSettings() == null) {
            return;
        }
        String colName = config.getDomainColumn().getStringValue();
        if (colName != null && !(config.getCustomMin() && config.getCustomMax())) {
            DataColumnSpec colSpec = spec.getColumnSpec(colName);
            if (colSpec == null) {
                setWarningMessage("Configured range column " + colName + " is not available anymore. Using previously configured range values.");
                return;
            }
            boolean minFailed = false;
            boolean maxFailed = false;
            if (!config.getCustomMin()) {
                DataCell min = colSpec.getDomain().getLowerBound();
                if (min == null || !min.getType().isCompatible(DoubleValue.class)) {
                    minFailed = true;
                } else {
                    config.getSliderSettings().setRangeMinValue(((DoubleValue)min).getDoubleValue());
                }
            }
            if (!config.getCustomMax()) {
                DataCell max = colSpec.getDomain().getUpperBound();
                if (max == null || !max.getType().isCompatible(DoubleValue.class)) {
                    maxFailed = true;
                } else {
                    config.getSliderSettings().setRangeMaxValue(((DoubleValue)max).getDoubleValue());
                }
            }
            if (minFailed || maxFailed) {
                StringBuilder builder = new StringBuilder();
                builder.append("Selected range column ");
                builder.append(colName);
                builder.append(" has no ");
                if (minFailed) {
                    builder.append("minimum ");
                    if (maxFailed) {
                        builder.append("or ");
                    }
                }
                if (maxFailed) {
                    builder.append("maximum ");
                }
                builder.append("domain values set. Using previously configured ranage values.");
                setWarningMessage(builder.toString());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createAndPushFlowVariable() throws InvalidSettingsException {
        double value = getRelevantValue().getDouble();
        ValidationError error = validateViewValue(getRelevantValue());
        if (error != null) {
            throw new InvalidSettingsException(error.getError());
        }
        pushFlowVariableDouble(getConfig().getFlowVariableName(), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void copyValueToConfig() {
        getConfig().getDefaultValue().setDouble(getViewValue().getDouble());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SliderInputQuickFormConfig createEmptyConfig() {
        return new SliderInputQuickFormConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected SliderInputQuickFormRepresentation getRepresentation() {
        return new SliderInputQuickFormRepresentation(getRelevantValue(), getConfig());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(final SliderInputQuickFormValue viewContent) {
        // TODO: validate specifics
        return super.validateViewValue(viewContent);
    }

}
