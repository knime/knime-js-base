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
 *   May 27, 2019 (Daniel Bogenrieder): created
 */
package org.knime.js.base.node.widget.input.slider;

import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.web.ValidationError;
import org.knime.js.base.node.base.input.slider.SliderNodeRepresentation;
import org.knime.js.base.node.base.input.slider.SliderNodeValue;
import org.knime.js.base.node.widget.WidgetFlowVariableNodeModel;
import org.knime.js.core.settings.slider.SliderSettings;

/**
 * The node model for the slider widget node
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
public class SliderWidgetNodeModel
    extends WidgetFlowVariableNodeModel<SliderNodeRepresentation<SliderNodeValue>, SliderNodeValue, SliderInputWidgetConfig> {

    /**
     * @param viewName
     */
    protected SliderWidgetNodeModel(final String viewName) {
        super(new PortType[]{BufferedDataTable.TYPE_OPTIONAL}, new PortType[]{FlowVariablePortObject.TYPE}, viewName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SliderNodeValue createEmptyViewValue() {
        return new SliderNodeValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
       return "org.knime.js.base.node.widget.input.slider";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] performExecute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        if (getConfig().getSliderSettings() == null) {
            throw new InvalidSettingsException("No settings defined. Please configure the node.");
        }
        SliderInputWidgetConfig config = getConfig();
        SliderSettings sliderSettings = getRepresentation().getSliderSettings();

        // Apply the new value only if the value is set.
        // This is needed, as otherwise the range variable might be overwritten.
        if (config.isUseCustomMin() && !Double.isNaN(config.getCustomMinValue())) {
            sliderSettings.setRangeMinValue(config.getCustomMinValue());
        }
        if (config.isUseCustomMax() && !Double.isNaN(config.getCustomMaxValue())) {
            sliderSettings.setRangeMaxValue(config.getCustomMaxValue());
        }
        return super.performExecute(inObjects, exec);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createAndPushFlowVariable() throws InvalidSettingsException {
        ValidationError error = validateViewValue(getRelevantValue());
        if (error != null) {
            throw new InvalidSettingsException(error.getError());
        }
        Double sliderValue = getRelevantValue().getDouble();
        if (sliderValue == null) {
            sliderValue = 0.0d;
        }
        pushFlowVariableDouble(getConfig().getFlowVariableName(), sliderValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SliderInputWidgetConfig createEmptyConfig() {
        return new SliderInputWidgetConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected SliderWidgetNodeRepresentation getRepresentation() {
        SliderInputWidgetConfig config = getConfig();
        return new SliderWidgetNodeRepresentation(getRelevantValue(), config.getDefaultValue(),
            config.getSliderConfig(), config.getLabelConfig(), config.getSliderSettings(),
            config.getCustomMinValue(), config.getCustomMaxValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void useCurrentValueAsDefault() {
        getConfig().getDefaultValue().setDouble(getViewValue().getDouble());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(final SliderNodeValue value) {
        if (getConfig().getSliderSettings() != null) {
            double dialogValue = value.getDouble();
            // Take the customMinValue if it exists, which means that the flow variable is set or the range min otherwise.
            double minimum = !Double.isNaN(getConfig().getCustomMinValue()) ? getConfig().getCustomMinValue()
                                                                            : getConfig().getSliderSettings().getRangeMinValue();
            // Take the customMaxValue if it exists, which means that the flow variable is set or the range max otherwise.
            double maximum = !Double.isNaN(getConfig().getCustomMaxValue()) ? getConfig().getCustomMaxValue()
                                                                            : getConfig().getSliderSettings().getRangeMaxValue();
            if (getConfig().isUseCustomMin()
                    && dialogValue < minimum) {
                return new ValidationError("The set value " + dialogValue
                    + " is smaller than the allowed minimum of " + minimum);
            }
            if (getConfig().isUseCustomMax()
                    && dialogValue > maximum) {
                return new ValidationError("The set value " + dialogValue
                    + " is bigger than the allowed maximum of " + maximum);
            }
        }
        return super.validateViewValue(value);
    }

}
