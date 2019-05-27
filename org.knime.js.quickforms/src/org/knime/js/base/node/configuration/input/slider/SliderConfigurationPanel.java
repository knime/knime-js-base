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
 *   May 24, 2019 (daniel): created
 */
package org.knime.js.base.node.configuration.input.slider;

import javax.swing.BoundedRangeModel;
import javax.swing.JSlider;

import org.knime.core.node.InvalidSettingsException;
import org.knime.js.base.node.configuration.AbstractDialogNodeConfigurationPanel;

/**
 * The component dialog panel for the slider configuration node
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("serial")
public class SliderConfigurationPanel extends AbstractDialogNodeConfigurationPanel<SliderDialogNodeValue> {

    private final JSlider m_component;

    /**
     * @param representation the dialog node settings
     */
    public SliderConfigurationPanel(final SliderDialogNodeRepresentation representation) {
        super(representation.getDefaultValue());
        double min = 0;
        double max = 100;
        if(representation.isUseCustomMin()) {
            min = representation.getSliderSettings().getRangeMinValue();
        }
        if(representation.isUseCustomMax()) {
            max = representation.getSliderSettings().getRangeMaxValue();
        }
        m_component = new JSlider();
        BoundedRangeModel sliderModel = m_component.getModel();
        //new SpinnerNumberModel(0, min, max, 1)
        sliderModel.setMaximum((int)max);
        sliderModel.setMinimum((int)min);
        m_component.setPaintTicks(true);
        m_component.setMajorTickSpacing(5);

        m_component.setPaintLabels(true);
        m_component.setLabelTable(m_component.createStandardLabels(5));
        //m_component.setPreferredSize(new JTextField(DialogUtil.DEF_TEXTFIELD_WIDTH).getPreferredSize());
        m_component.setValue((int)representation.getDefaultValue().getDouble());
        setComponent(m_component);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void resetToDefault() {
        m_component.setValue((int)getDefaultValue().getDouble());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected SliderDialogNodeValue createNodeValue() throws InvalidSettingsException {
        SliderDialogNodeValue value = new SliderDialogNodeValue();
        value.setDouble(m_component.getValue());
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadNodeValue(final SliderDialogNodeValue value) {
        super.loadNodeValue(value);
        if (value != null) {
            m_component.setValue((int)value.getDouble());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        m_component.setEnabled(enabled);
    }

}
