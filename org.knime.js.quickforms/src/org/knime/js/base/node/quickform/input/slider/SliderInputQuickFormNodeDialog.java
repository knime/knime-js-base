/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
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

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.js.base.node.quickform.QuickFormNodeDialog;
import org.knime.js.core.settings.numberFormat.NumberFormatNodeDialogUI;
import org.knime.js.core.settings.numberFormat.NumberFormatSettings;
import org.knime.js.core.settings.slider.SliderPipsSettings;
import org.knime.js.core.settings.slider.SliderPipsSettings.PipMode;
import org.knime.js.core.settings.slider.SliderSettings;
import org.knime.js.core.settings.slider.SliderSettings.Direction;
import org.knime.js.core.settings.slider.SliderSettings.Orientation;

/**
 * The dialog for the slider input quick form node.
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
public class SliderInputQuickFormNodeDialog extends QuickFormNodeDialog {

    private final DialogComponentColumnNameSelection m_domainColumnSelection;
    private final JCheckBox m_useMin;
    private final JCheckBox m_useMax;
    private final JSpinner m_min;
    private final JSpinner m_max;
    private final JSpinner m_defaultSpinner;
    private DataTableSpec m_currentSpec;
    private SliderInputQuickFormConfig m_config;

    /*Slider panel*/
    private final JCheckBox m_useStepCheckbox;
    private final JSpinner m_stepSpinner;
    private final JCheckBox m_lowerConnectCheckbox;
    private final JCheckBox m_upperConnectCheckbox;
    private final JRadioButton m_orientationHorizontalButton;
    private final JRadioButton m_orientationVerticalButton;
    private final JRadioButton m_directionLTRButton;
    private final JRadioButton m_directionRTLButton;
    private final JCheckBox m_tooltipsCheckbox;
    private final JCheckBox m_tooltipsFormatCheckbox;
    private final NumberFormatNodeDialogUI m_tooltipsFormat;
    private final JPanel m_tooltipsFormatPanel;

    /*Pip panel*/
    private final JCheckBox m_pipsEnableCheckbox;
    private final JComboBox<SliderPipsSettings.PipMode> m_pipsModeComboBox;
    private final JSpinner m_pipsDensitySpinner;
    private final JTextField m_pipsValuesTextField;
    private final JCheckBox m_pipsSteppedCheckbox;
    private final NumberFormatNodeDialogUI m_pipsFormat;
    private final JPanel m_pipsFormatPanel;

    /** Constructors, inits fields calls layout routines. */
    @SuppressWarnings("unchecked")
    SliderInputQuickFormNodeDialog() {
        m_config = new SliderInputQuickFormConfig();
        m_domainColumnSelection = new DialogComponentColumnNameSelection(m_config.getDomainColumn(), "", 0, false, true, DoubleValue.class);
        m_config.getDomainColumn().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                setDomainValues(true);
            }
        });
        m_useMin = new JCheckBox();
        m_useMax = new JCheckBox();
        m_min = new JSpinner(new SpinnerNumberModel(0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1));
        m_max = new JSpinner(new SpinnerNumberModel(100, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1));
        m_defaultSpinner = new JSpinner(new SpinnerNumberModel(50, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1));
        m_useMin.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent e) {
                setDomainValues(false);
            }
        });
        m_useMax.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent e) {
                setDomainValues(false);
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
        m_min.setEnabled(m_useMin.isSelected());
        m_max.setEnabled(m_useMax.isSelected());

        /*Slider panel*/
        m_useStepCheckbox = new JCheckBox("Use Stepping");
        m_useStepCheckbox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                m_stepSpinner.setEnabled(m_useStepCheckbox.isSelected());
            }
        });
        m_stepSpinner = new JSpinner(new SpinnerNumberModel(1, 0, Double.POSITIVE_INFINITY, 1));
        m_lowerConnectCheckbox = new JCheckBox("Lower");
        m_upperConnectCheckbox = new JCheckBox("Upper");
        m_orientationHorizontalButton = new JRadioButton("Horizontal");
        m_orientationHorizontalButton.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                setDirectionLabels();
            }
        });
        m_orientationVerticalButton = new JRadioButton("Vertical");
        m_orientationVerticalButton.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                setDirectionLabels();
            }
        });
        m_directionLTRButton = new JRadioButton("LTR");
        m_directionRTLButton = new JRadioButton("RTL");
        m_tooltipsCheckbox = new JCheckBox("Show Tooltip");
        m_tooltipsCheckbox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                enableTooltipFields();
            }
        });
        m_tooltipsFormatCheckbox = new JCheckBox("Use Formatter For Tooltips");
        m_tooltipsFormatCheckbox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                enableTooltipFields();
            }
        });
        m_tooltipsFormat = new NumberFormatNodeDialogUI();
        m_tooltipsFormatPanel = m_tooltipsFormat.createPanel();

        /*Pips panel*/
        m_pipsEnableCheckbox = new JCheckBox("Enable Labels/Pips");
        m_pipsEnableCheckbox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                enablePipFields(false);
            }
        });
        m_pipsModeComboBox = new JComboBox<>(PipMode.values());
        m_pipsModeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                enablePipFields(true);
            }
        });
        m_pipsDensitySpinner = new JSpinner(new SpinnerNumberModel(3, 1, Integer.MAX_VALUE, 1));
        m_pipsValuesTextField = new JTextField();
        m_pipsSteppedCheckbox = new JCheckBox("Stepped");
        m_pipsFormat = new NumberFormatNodeDialogUI();
        m_pipsFormatPanel = m_pipsFormat.createPanel();

        createAndAddTab();
        createSliderTab();
        createPipTab();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
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
        addPairToPanel("Range Column: ", m_domainColumnSelection.getComponentPanel(), panelWithGBLayout, gbc);
        addPairToPanel("Minimum: ", minPanel, panelWithGBLayout, gbc);
        addPairToPanel("Maximum: ", maxPanel, panelWithGBLayout, gbc);
        addPairToPanel("Default Value: ", m_defaultSpinner, panelWithGBLayout, gbc);
    }

    private final void createSliderTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addPairToPanel("", m_useStepCheckbox, panel, gbc);
        addPairToPanel("Step Size", m_stepSpinner, panel, gbc);

        JPanel connectPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.fill = GridBagConstraints.HORIZONTAL;
        gbc2.gridx = gbc2.gridy = 0;
        gbc2.weightx = 0;
        connectPanel.add(m_lowerConnectCheckbox, gbc2);
        gbc2.gridx++;
        gbc2.weightx = 1;
        connectPanel.add(m_upperConnectCheckbox, gbc2);

        addPairToPanel("Connect", connectPanel, panel, gbc);

        ButtonGroup orientationGroup = new ButtonGroup();
        orientationGroup.add(m_orientationHorizontalButton);
        orientationGroup.add(m_orientationVerticalButton);
        JPanel orientationPanel = new JPanel(new GridBagLayout());
        gbc2.gridx = gbc2.gridy = 0;
        gbc2.weightx = 0;
        orientationPanel.add(m_orientationHorizontalButton, gbc2);
        gbc2.gridx++;
        gbc2.weightx = 1;
        orientationPanel.add(m_orientationVerticalButton, gbc2);
        addPairToPanel("Orientation", orientationPanel, panel, gbc);

        ButtonGroup directionGroup = new ButtonGroup();
        directionGroup.add(m_directionLTRButton);
        directionGroup.add(m_directionRTLButton);
        JPanel directionPanel = new JPanel(new GridBagLayout());
        gbc2.gridx = gbc2.gridy = 0;
        gbc2.weightx = 0;
        directionPanel.add(m_directionLTRButton, gbc2);
        gbc2.gridx++;
        gbc2.weightx = 1;
        directionPanel.add(m_directionRTLButton, gbc2);
        addPairToPanel("Direction", directionPanel, panel, gbc);

        addTripelToPanel("", m_tooltipsCheckbox, m_tooltipsFormatCheckbox, panel, gbc);
        m_tooltipsFormatPanel.setBorder(BorderFactory.createTitledBorder("Tooltip Format Options"));
        panel.add(m_tooltipsFormatPanel, gbc);

        JPanel borderPanel = new JPanel(new BorderLayout());
        borderPanel.add(panel, BorderLayout.CENTER);

        addTab("Slider", borderPanel);
    }

    private void createPipTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addPairToPanel("", m_pipsEnableCheckbox, panel, gbc);
        addPairToPanel("Pips Mode", m_pipsModeComboBox, panel, gbc);
        addPairToPanel("Density", m_pipsDensitySpinner, panel, gbc);
        addPairToPanel("Values", m_pipsValuesTextField, panel, gbc);
        addPairToPanel("", m_pipsSteppedCheckbox, panel, gbc);

        m_pipsFormatPanel.setBorder(BorderFactory.createTitledBorder("Label format options"));
        panel.add(m_pipsFormatPanel, gbc);

        JPanel borderPanel = new JPanel(new BorderLayout());
        borderPanel.add(panel, BorderLayout.CENTER);

        addTab("Labels", borderPanel);
    }

    private void setDirectionLabels() {
        if (m_orientationHorizontalButton.isSelected()) {
            m_directionLTRButton.setText("Left To Right");
            m_directionRTLButton.setText("Right To Left");
        } else {
            m_directionLTRButton.setText("Top To Bottom");
            m_directionRTLButton.setText("Bottom To Top");
        }
    }

    private void enableTooltipFields() {
        boolean enableAll = m_tooltipsCheckbox.isSelected();
        boolean enableFormat = m_tooltipsFormatCheckbox.isSelected();
        m_tooltipsFormatCheckbox.setEnabled(enableAll);
        m_tooltipsFormatPanel.setEnabled(enableAll && enableFormat);
        m_tooltipsFormat.setEnabled(enableAll && enableFormat);
    }

    private void enablePipFields(final boolean fillDefaultValues) {
        boolean enableAll = m_pipsEnableCheckbox.isSelected();
        PipMode mode = (PipMode)m_pipsModeComboBox.getSelectedItem();
        m_pipsModeComboBox.setEnabled(enableAll);
        m_pipsDensitySpinner.setEnabled(enableAll);
        boolean enableValues = (mode == PipMode.POSITIONS || mode == PipMode.VALUES || mode == PipMode.COUNT);
        m_pipsValuesTextField.setEnabled(enableAll && enableValues);
        m_pipsSteppedCheckbox.setEnabled(enableAll && enableValues);
        m_pipsFormatPanel.setEnabled(enableAll);
        m_pipsFormat.setEnabled(enableAll);

        if (fillDefaultValues) {
            String defaultValues = "";
            if (mode == PipMode.COUNT) {
                defaultValues = "6";
            } else if (mode == PipMode.POSITIONS) {
                defaultValues = "0,25,50,75,100";
            }
            m_pipsValuesTextField.setText(defaultValues);
        }
    }

    private void setDomainValues(final boolean forceDomain) {
        String domainColumn = m_domainColumnSelection.getSelected();
        m_useMin.setEnabled(domainColumn != null);
        m_useMax.setEnabled(domainColumn != null);
        if (domainColumn == null) {
            m_useMin.setSelected(true);
            m_useMax.setSelected(true);
        } else if (forceDomain) {
            m_useMin.setSelected(false);
            m_useMax.setSelected(false);
        }
        m_min.setEnabled(m_useMin.isSelected());
        m_max.setEnabled(m_useMax.isSelected());
        if (domainColumn != null && !m_useMin.isSelected()) {
            DataColumnSpec colSpec = m_currentSpec.getColumnSpec(domainColumn);
            if (colSpec != null) {
                DataCell lowerBound = colSpec.getDomain().getLowerBound();
                if (lowerBound != null && lowerBound.getType().isCompatible(DoubleValue.class)) {
                    m_min.setValue(((DoubleValue)lowerBound).getDoubleValue());
                }
            }
        }
        if (domainColumn != null && !m_useMax.isSelected()) {
            DataColumnSpec colSpec = m_currentSpec.getColumnSpec(domainColumn);
            if (colSpec != null) {
                DataCell upperBound = colSpec.getDomain().getUpperBound();
                if (upperBound != null && upperBound.getType().isCompatible(DoubleValue.class)) {
                    m_max.setValue(((DoubleValue)upperBound).getDoubleValue());
                }
            }
        }
        if (domainColumn != null && forceDomain) {
            double newDefault = ((Double)m_max.getValue() - (Double)m_min.getValue()) / 2 + (Double)m_min.getValue();
            m_defaultSpinner.setValue(newDefault);
        }
    }

    private double[] getPipValues() throws InvalidSettingsException{
        String vString = m_pipsValuesTextField.getText();
        if (!m_pipsValuesTextField.isEnabled() || vString == null || "".equals(vString)) {
            return null;
        }
        String[] splitted = vString.split(",");
        double[] values = new double[splitted.length];
        for (int i = 0; i < splitted.length; i++) {
            try {
                values[i] = Double.parseDouble(splitted[i].trim());
            } catch (NumberFormatException e) {
                throw new InvalidSettingsException("The entered values are not valid. " + splitted[i].trim() + " could not be parsed as double value.", e);
            }
        }
        return values;
    }

    private String formatPipValues(final double[] values) {
        if (values == null || values.length < 1) {
            return null;
        }
        StringBuilder builder = new StringBuilder(formatDoubleAndInt(values[0]));
        for (int i = 1; i < values.length; i++) {
            builder.append(",");
            builder.append(formatDoubleAndInt(values[i]));
        }
        return builder.toString();
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
        super.loadSettingsFrom(m_config);
        m_currentSpec = (DataTableSpec)specs[0];
        m_domainColumnSelection.loadSettingsFrom(settings, specs);
        m_config.getDomainColumn().setEnabled(m_currentSpec != null);
        m_useMin.setSelected(m_config.getCustomMin());
        m_useMax.setSelected(m_config.getCustomMax());
        SliderSettings sSettings = m_config.getSliderSettings();
        if (sSettings != null) {
            Double min = sSettings.getRangeMinValue();
            min = min == null ? 0 : min;
            Double max = sSettings.getRangeMaxValue();
            max = max == null ? 100 : max;
            m_min.setValue(min);
            m_max.setValue(max);

            Double step = sSettings.getStep();
            if (step != null) {
                m_useStepCheckbox.setSelected(true);
                m_stepSpinner.setValue(step);
            } else {
                m_useStepCheckbox.setSelected(false);
            }
            boolean[] connect = sSettings.getConnect();
            if (connect != null && connect.length >= 2) {
                m_lowerConnectCheckbox.setSelected(connect[0]);
                m_upperConnectCheckbox.setSelected(connect[1]);
            }
            boolean vertical = sSettings.getOrientation() == Orientation.VERTICAL;
            m_orientationHorizontalButton.setSelected(!vertical);
            m_orientationVerticalButton.setSelected(vertical);

            boolean rtl = sSettings.getDirection() == Direction.RTL;
            m_directionLTRButton.setSelected(!rtl);
            m_directionRTLButton.setSelected(rtl);
            Object[] tooltips = sSettings.getTooltips();
            Object tO = null;
            if (tooltips != null && tooltips.length > 0) {
                tO = tooltips[0];
                boolean tIsBoolean = tO instanceof Boolean;
                boolean tIsFormat = tO instanceof NumberFormatSettings;
                m_tooltipsCheckbox.setSelected(tIsFormat || (tIsBoolean && (Boolean)tO));
                m_tooltipsFormatCheckbox.setSelected(tIsFormat);
                m_tooltipsFormat.loadSettingsFrom(tIsFormat ? (NumberFormatSettings)tO : null);
            }
            m_tooltipsFormat.loadSettingsFrom(tO instanceof NumberFormatSettings ? (NumberFormatSettings)tO : null);
            SliderPipsSettings pips = sSettings.getPips();
            if (pips != null) {
                m_pipsEnableCheckbox.setSelected(true);
                m_pipsModeComboBox.setSelectedItem(pips.getMode());
                m_pipsDensitySpinner.setValue(pips.getDensity());
                m_pipsValuesTextField.setText(formatPipValues(pips.getValues()));
                m_pipsSteppedCheckbox.setSelected(pips.getStepped());
                m_pipsFormat.loadSettingsFrom(pips.getFormat());
            } else {
                m_pipsEnableCheckbox.setSelected(false);
                m_pipsFormat.loadSettingsFrom(null);
            }
        }
        double defaultValue = m_config.getDefaultValue().getDouble();
        m_defaultSpinner.setValue(defaultValue);
        m_min.setEnabled(m_useMin.isSelected());
        m_max.setEnabled(m_useMax.isSelected());

        enableTooltipFields();
        enablePipFields(false);
        setDomainValues(false);
    }

    private void validateSettings() throws InvalidSettingsException{
        double min = (Double)m_min.getValue();
        double max = (Double)m_max.getValue();
        double value = (Double)m_defaultSpinner.getValue();
        if (max <= min) {
            throw new InvalidSettingsException("Maximum range has to be larger than minimum.");
        }
        if (value < min || value > max) {
            throw new InvalidSettingsException("Default value has to be in between selected minimum and maximum");
        }
        if (m_useStepCheckbox.isSelected()) {
            double stepSize = (Double)m_stepSpinner.getValue();
            if (stepSize >= (max-min)) {
                throw new InvalidSettingsException("Step size needs to be smaller than slider range.");
            }
        }
        if (m_pipsEnableCheckbox.isSelected()) {
            getPipValues();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        validateSettings();
        super.saveSettingsTo(m_config);
        m_config.getDomainColumn().saveSettingsTo(settings);
        m_config.setCustomMin(m_useMin.isSelected());
        m_config.setCustomMax(m_useMax.isSelected());

        SliderSettings sSettings = new SliderSettings();
        sSettings.setRangeMinValue((Double)m_min.getValue());
        sSettings.setRangeMaxValue((Double)m_max.getValue());
        double defaultValue = (Double)m_defaultSpinner.getValue();
        m_config.getDefaultValue().setDouble(defaultValue);
        sSettings.setStart(new double[]{defaultValue});
        if (m_useStepCheckbox.isSelected()) {
            sSettings.setStep((Double)m_stepSpinner.getValue());
        }
        sSettings.setConnect(new boolean[]{m_lowerConnectCheckbox.isSelected(), m_upperConnectCheckbox.isSelected()});
        sSettings.setOrientation(m_orientationVerticalButton.isSelected() ? Orientation.VERTICAL : Orientation.HORIZONTAL);
        sSettings.setDirection(m_directionRTLButton.isSelected() ? Direction.RTL : Direction.LTR);
        if (m_tooltipsCheckbox.isSelected()) {
            Object tooltip = m_tooltipsCheckbox.isSelected();
            if (m_tooltipsFormatCheckbox.isSelected()) {
                tooltip = m_tooltipsFormat.saveSettingsTo();
            }
            sSettings.setTooltips(new Object[]{tooltip});
        } else {
            sSettings.setTooltips(null);
        }
        if (m_pipsEnableCheckbox.isSelected()) {
            SliderPipsSettings pipsSettings = new SliderPipsSettings();
            pipsSettings.setMode((PipMode)m_pipsModeComboBox.getSelectedItem());
            pipsSettings.setDensity((Integer)m_pipsDensitySpinner.getValue());
            if (m_pipsValuesTextField.isEnabled()) {
                pipsSettings.setValues(getPipValues());
            }
            if (m_pipsSteppedCheckbox.isEnabled()) {
                pipsSettings.setStepped(m_pipsSteppedCheckbox.isSelected());
            }
            NumberFormatSettings pipsFormat = m_pipsFormat.saveSettingsTo();
            pipsSettings.setFormat(pipsFormat);
            sSettings.setPips(pipsSettings);
        }

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
