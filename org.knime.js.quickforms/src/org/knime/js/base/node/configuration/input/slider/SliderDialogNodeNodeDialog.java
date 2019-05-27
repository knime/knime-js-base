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
 *   May 25, 2019 (daniel): created
 */
package org.knime.js.base.node.configuration.input.slider;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.js.base.node.configuration.FlowVariableDialogNodeNodeDialog;
import org.knime.js.base.node.quickform.input.slider.SliderInputQuickFormValue;
import org.knime.js.core.settings.slider.SliderSettings;

/**
 * The dialog for the slider configuration node
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
public class SliderDialogNodeNodeDialog extends FlowVariableDialogNodeNodeDialog<SliderDialogNodeValue> {

    private final JCheckBox m_useCustomMin;
    private final JCheckBox m_useCustomMax;
    private final JSpinner m_min;
    private final JSpinner m_max;
    private final JSpinner m_defaultSpinner;

    private final SliderDialogNodeConfig m_config;

    /**
     *
     */
    public SliderDialogNodeNodeDialog() {
        m_config = new SliderDialogNodeConfig();
        m_useCustomMin = new JCheckBox();
        m_useCustomMax = new JCheckBox();
        m_min = new JSpinner(getSpinnerModel());
        m_max = new JSpinner(getSpinnerModel());
        m_defaultSpinner = new JSpinner(new SpinnerNumberModel(50, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1));
        m_useCustomMin.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent e) {
                m_min.setEnabled(m_useCustomMin.isSelected());
            }
        });
        m_useCustomMax.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent e) {
                m_max.setEnabled(m_useCustomMax.isSelected());
            }
        });
        m_min.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                double min = (Double)m_min.getValue();
                if (((Double)m_max.getValue()) < min) {
                    m_max.setValue(min);
                }
            }
        });
        m_max.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                double max = (Double)m_max.getValue();
                if (((Double)m_min.getValue()) > max) {
                    m_min.setValue(max);
                }
            }
        });
        m_min.setEnabled(m_useCustomMin.isSelected());
        m_max.setEnabled(m_useCustomMax.isSelected());
        /*Slider panel*/

        createAndAddTab();
    }

    /**
     * @return a default spinner model
     */
    private static SpinnerNumberModel getSpinnerModel() {
        return new SpinnerNumberModel(0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
        JPanel minPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(0, 0, 0, 0);
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.weightx = 0;
        gbc2.weighty = 0;
        gbc2.gridx = 0;
        gbc2.gridy = 0;
        minPanel.add(m_useCustomMin, gbc2);
        gbc2.weightx = 1;
        gbc2.gridx++;
        gbc2.insets = new Insets(0, 5, 0, 0);
        minPanel.add(m_min, gbc2);
        JPanel maxPanel = new JPanel(new GridBagLayout());
        gbc2.weightx = 0;
        gbc2.gridx = 0;
        gbc2.insets = new Insets(0, 0, 0, 0);
        maxPanel.add(m_useCustomMax, gbc2);
        gbc2.weightx = 1;
        gbc2.gridx++;
        gbc2.insets = new Insets(0, 5, 0, 0);
        maxPanel.add(m_max, gbc2);
        addPairToPanel("Minimum: ", minPanel, panelWithGBLayout, gbc);
        addPairToPanel("Maximum: ", maxPanel, panelWithGBLayout, gbc);
        addPairToPanel("Default Value: ", m_defaultSpinner, panelWithGBLayout, gbc);
    }

    //Format double as int if no decimals, otherwise as regular double
    private String formatDoubleAndInt(final double value) {
        if(value == (long)value) {
            return String.format("%d",(long)value);
        }
        else {
            return String.format("%s",value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
            throws NotConfigurableException {
        m_config.loadSettingsInDialog(settings);
        loadSettingsFrom(m_config);
        m_useCustomMin.setSelected(m_config.isUseCustomMin());
        m_useCustomMax.setSelected(m_config.isUseCustomMax());
        SliderSettings sSettings = m_config.getSliderSettings();
        if (sSettings != null) {
            Double min = sSettings.getRangeMinValue();
            min = min == null ? 0 : min;
            Double max = sSettings.getRangeMaxValue();
            max = max == null ? 100 : max;
            m_min.setValue(min);
            m_max.setValue(max);

        }
        double defaultValue = m_config.getDefaultValue().getDouble();
        m_defaultSpinner.setValue(defaultValue);
        m_min.setEnabled(m_useCustomMin.isSelected());
        m_max.setEnabled(m_useCustomMax.isSelected());
    }

    private void validateSettings() throws InvalidSettingsException{
        double min = Double.parseDouble(m_min.getValue().toString());
        double max = Double.parseDouble(m_max.getValue().toString());
        double value = Double.parseDouble(m_defaultSpinner.getValue().toString());
        if (max <= min) {
            throw new InvalidSettingsException("Maximum range has to be larger than minimum.");
        }
        if (value < min || value > max) {
            throw new InvalidSettingsException("Default value has to be in between selected minimum and maximum");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        validateSettings();
        super.saveSettingsTo(m_config);
        m_config.setUseCustomMin(m_useCustomMin.isSelected());
        m_config.setUseCustomMax(m_useCustomMax.isSelected());

        SliderSettings sSettings = new SliderSettings();
        sSettings.setRangeMinValue((Double.parseDouble(m_min.getValue().toString())));
        sSettings.setRangeMaxValue((Double.parseDouble(m_max.getValue().toString())));
        double defaultValue = (Double.parseDouble(m_defaultSpinner.getValue().toString()));
        m_config.getDefaultValue().setDouble(defaultValue);
        sSettings.setStart(new double[]{defaultValue});

        sSettings.validateSettings();
        m_config.setSliderSettings(sSettings);
        m_config.saveSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getValueString(final NodeSettingsRO settings) throws InvalidSettingsException {
        SliderInputQuickFormValue value = new SliderInputQuickFormValue();
        value.loadFromNodeSettings(settings);
        return "" + value.getDouble();
    }

}
