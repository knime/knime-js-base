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
 * History
 *   Sep 28, 2016 (Christian Albrecht, KNIME.com GmbH, Konstanz, Germany): created
 */
package org.knime.js.base.node.quickform.input.slider;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.knime.core.node.InvalidSettingsException;
import org.knime.js.base.node.quickform.QuickFormDialogPanel;
import org.knime.js.base.node.quickform.QuickFormNodeDialog;
import org.knime.js.core.settings.slider.SliderSettings;

/**
 * The sub node dialog panel for the double input quick form node.
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
@SuppressWarnings("serial")
public class SliderInputQuickFormDialogPanel extends QuickFormDialogPanel<SliderInputQuickFormValue> {

    //private JSlider m_slider = new JSlider(SwingConstants.HORIZONTAL, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
    private JSpinner m_spinner = new JSpinner(new SpinnerNumberModel(0.0, Double.NEGATIVE_INFINITY,
            Double.POSITIVE_INFINITY, 0.01));
    private boolean m_settingsAvailable = false;
    private boolean m_sliderSettingsInteger = false;

    /**
     * @param representation The dialog representation
     */
    public SliderInputQuickFormDialogPanel(final SliderInputQuickFormRepresentation representation) {
        super(representation.getDefaultValue());
        SliderSettings settings = representation.getSliderSettings();
        if (settings != null) {
            Double minValue = settings.getRangeMinValue();
            Double maxValue = settings.getRangeMaxValue();
            Double stepSize = settings.getStep();
            if (minValue != null && maxValue != null) {
                m_settingsAvailable = true;
                //if (settings.outputsIntegerOnly()) {
                    /*m_slider.setPreferredSize(new JTextField(QuickFormNodeDialog.DEF_TEXTFIELD_WIDTH).getPreferredSize());
                    m_slider.setMinimum(minValue.intValue());
                    m_slider.setMaximum(maxValue.intValue());
                    if (stepSize != null) {
                        m_slider.setPaintTicks(true);
                        m_slider.setMinorTickSpacing(stepSize.intValue());
                        m_slider.setSnapToTicks(true);
                    }
                    m_slider.setValue(new Double(getDefaultValue().getDouble()).intValue());
                    //TODO: pips?
                    setComponent(m_slider);
                    m_sliderSettingsInteger = true;*/
                //} else {
                    m_spinner.setPreferredSize(new JTextField(QuickFormNodeDialog.DEF_TEXTFIELD_WIDTH).getPreferredSize());
                    m_spinner.setModel(new SpinnerNumberModel(getDefaultValue().getDouble(), (double)minValue, (double)maxValue, stepSize == null ? 0.01 : (double)stepSize));
                    setComponent(m_spinner);
                //}
                return;
            }
        }
        JLabel warningLabel = new JLabel("Configuration for slider input control not available. Please check the node's settings.");
        setComponent(warningLabel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SliderInputQuickFormValue createNodeValue() throws InvalidSettingsException {
        if (!m_settingsAvailable) {
            return getDefaultValue();
        }
        SliderInputQuickFormValue value = new SliderInputQuickFormValue();
        /*if (m_sliderSettingsInteger) {
            value.setDouble(m_slider.getValue());
        } else {*/
            value.setDouble((Double)m_spinner.getValue());
        //}
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadNodeValue(final SliderInputQuickFormValue value) {
        super.loadNodeValue(value);
        if (value != null) {
            if (m_settingsAvailable) {
                /*if (m_sliderSettingsInteger) {
                    m_slider.setValue(new Double(value.getDouble()).intValue());
                } else {*/
                    m_spinner.setValue(value.getDouble());
                //}
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        //m_slider.setEnabled(enabled);
        m_spinner.setEnabled(enabled);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void resetToDefault() {
        //double value = getDefaultValue().getDouble();
        //m_slider.setValue(new Double(value).intValue());
        m_spinner.setValue(getDefaultValue().getDouble());
    }

}
