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
 *   22 May 2019 (albrecht): created
 */
package org.knime.js.base.node.configuration.input.dbl;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.text.ParseException;

import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.NumberFormatter;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.js.base.node.base.input.dbl.DoubleNodeConfig;
import org.knime.js.base.node.configuration.FlowVariableDialogNodeNodeDialog;

/**
 * The dialog for the double configuration node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class DoubleDialogNodeNodeDialog extends FlowVariableDialogNodeNodeDialog<DoubleDialogNodeValue> {

    private final JCheckBox m_useMin;
    private final JCheckBox m_useMax;
    private final JSpinner m_min;
    private final JSpinner m_max;
    private final JSpinner m_defaultSpinner;

    private final DoubleInputDialogNodeConfig m_config;


    /**
     * Constructor, inits fields calls layout routines
     */
    public DoubleDialogNodeNodeDialog() {
        m_config = new DoubleInputDialogNodeConfig();
        m_useMin = new JCheckBox();
        m_useMax = new JCheckBox();
        m_min = createSpinner(0.0, 1.0);
        m_max = createSpinner(1.0, 1.0);
        m_defaultSpinner = createSpinner(0.0, 0.1);

        m_useMin.addItemListener(e -> m_min.setEnabled(m_useMin.isSelected()));
        m_useMax.addItemListener(e -> m_max.setEnabled(m_useMax.isSelected()));
        m_min.addChangeListener(e -> {
            double min = (Double)m_min.getValue();
            if (((Double)m_max.getValue()) < min) {
                m_max.setValue(min);
            }
        });
        m_max.addChangeListener(e -> {
            double max = (Double)m_max.getValue();
            if (((Double)m_min.getValue()) > max) {
                m_min.setValue(max);
            }
        });
        m_min.setEnabled(m_useMin.isSelected());
        m_max.setEnabled(m_useMax.isSelected());
        createAndAddTab();
    }

    /**
     * Create a spinner control element with an unbounded value.
     * @param value the value of the spinner element
     * @param stepSize the increment to add/subtract when using the arrow controls next to the text field
     * @return a spinner control element
     */
    static JSpinner createSpinner(final double value, final double stepSize) {
        return createSpinner(value, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, stepSize);
    }

    /**
     * Create a spinner control element with the given value and limitations.
     * @param value the value of the spinner element
     * @param min the minimum selectable value
     * @param max the maximum selectable value
     * @param stepSize the increment to add/subtract when using the arrow controls next to the text field
     * @return a spinner control element
     */
    static JSpinner createSpinner(final double value, final double min, final double max, final double stepSize) {

        // Create a spinner model with precise arithmetics by calculating the result of adding/subtract one step size
        // as BigDecimal. When calculating with doubles, we see numeric errors like 2.000001 - 2 = 1.000000000139778E-6
        SpinnerNumberModel model =
            new SpinnerNumberModel(value, min, max, stepSize) {
                private static final long serialVersionUID = 1L;

                private final BigDecimal m_increment = BigDecimal.valueOf(getStepSize().doubleValue());

                @Override
                public Object getNextValue() {
                    return BigDecimal.valueOf((Double)getValue()).add(m_increment).doubleValue();
                }

                @Override
                public Object getPreviousValue() {
                    return BigDecimal.valueOf((Double)getValue()).subtract(m_increment).doubleValue();
                }
            };
        JSpinner spinner = new JSpinner(model);

        JSpinner.NumberEditor e = (JSpinner.NumberEditor) spinner.getEditor();
        e.getTextField().setFormatterFactory(new AbstractFormatterFactory() {
            @Override
            @SuppressWarnings("serial")
            public AbstractFormatter getFormatter(final JFormattedTextField tf) {
                return new NumberFormatter() {
                    @Override
                    public Object stringToValue(final String text) throws ParseException {
                        try {
                            // We want to ignore the case of the scientific notation exponent separator ("e" and "E")
                            // NumberEditor relies on DecimalFormat, which supports only "E" as exponent separator
                            return Double.valueOf(text);
                        } catch (NumberFormatException er) {
                            // this is handled gracefully by the spinner, NumberFormatException is not
                            throw new ParseException(er.getMessage(), 0);
                        }
                    }
                    @Override
                    public String valueToString(final Object v) {
                        return v.toString();
                    }
                };
            }
        });

        return spinner;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getValueString(final NodeSettingsRO settings) throws InvalidSettingsException {
        DoubleDialogNodeValue value = new DoubleDialogNodeValue();
        value.loadFromNodeSettings(settings);
        return "" + value.getDouble();
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
        minPanel.add(m_useMin, gbc2);
        gbc2.weightx = 1;
        gbc2.gridx++;
        gbc2.insets = new Insets(0, 5, 0, 0);
        minPanel.add(m_min, gbc2);
        JPanel maxPanel = new JPanel(new GridBagLayout());
        gbc2.weightx = 0;
        gbc2.gridx = 0;
        gbc2.insets = new Insets(0, 0, 0, 0);
        maxPanel.add(m_useMax, gbc2);
        gbc2.weightx = 1;
        gbc2.gridx++;
        gbc2.insets = new Insets(0, 5, 0, 0);
        maxPanel.add(m_max, gbc2);
        addPairToPanel("Minimum: ", minPanel, panelWithGBLayout, gbc);
        addPairToPanel("Maximum: ", maxPanel, panelWithGBLayout, gbc);
        addPairToPanel("Default Value: ", m_defaultSpinner, panelWithGBLayout, gbc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs) throws NotConfigurableException {
        m_config.loadSettingsInDialog(settings);
        loadSettingsFrom(m_config);
        m_defaultSpinner.setValue(m_config.getDefaultValue().getDouble());
        DoubleNodeConfig doubleConfig = m_config.getDoubleConfig();
        m_useMin.setSelected(doubleConfig.isUseMin());
        m_useMax.setSelected(doubleConfig.isUseMax());
        m_min.setValue(doubleConfig.getMin());
        m_max.setValue(doubleConfig.getMax());
        m_min.setEnabled(m_useMin.isSelected());
        m_max.setEnabled(m_useMax.isSelected());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        saveSettingsTo(m_config);
        m_config.getDefaultValue().setDouble((Double)m_defaultSpinner.getValue());
        DoubleNodeConfig doubleConfig = m_config.getDoubleConfig();
        doubleConfig.setUseMin(m_useMin.isSelected());
        doubleConfig.setUseMax(m_useMax.isSelected());
        doubleConfig.setMin((Double)m_min.getValue());
        doubleConfig.setMax((Double)m_max.getValue());
        m_config.saveSettings(settings);
    }

}
