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
package org.knime.js.base.node.quickform.filter.rangeslider;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.js.core.settings.slider.SliderNodeDialogUI;
import org.knime.js.core.settings.slider.SliderPipsSettings;
import org.knime.js.core.settings.slider.SliderPipsSettings.PipMode;
import org.knime.js.core.settings.slider.SliderSettings;

/**
 * Dialog for the range slider filter node.
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
public class RangeSliderFilterNodeDialog extends NodeDialogPane {

    private RangeSliderFilterConfig m_config;
    private SliderNodeDialogUI m_sliderUI;

    private final JCheckBox m_hideInWizardCheckbox;
    private final JCheckBox m_deleteOtherFiltersCheckbox;
    private final JCheckBox m_useLabelCheckbox;
    private final JCheckBox m_customLabelCheckbox;
    private final JTextField m_labelTextfield;

    /** Constructors, inits fields calls layout routines. */
    RangeSliderFilterNodeDialog() {
        m_config = new RangeSliderFilterConfig();
        m_sliderUI = new SliderNodeDialogUI(2, false, true);
        m_hideInWizardCheckbox = new JCheckBox("Hide In Wizard");
        m_deleteOtherFiltersCheckbox = new JCheckBox("Delete Existing Filter Definitions");
        m_useLabelCheckbox = new JCheckBox("Show label");
        m_customLabelCheckbox = new JCheckBox("Custom");
        m_labelTextfield = new JTextField(20);

        m_useLabelCheckbox.addChangeListener(getLabelChangeListener());
        m_customLabelCheckbox.addChangeListener(getLabelChangeListener());
        m_sliderUI.getDomainColumnSelection().getModel().addChangeListener(getLabelChangeListener());

        addTab("Options", createOptions());
        addTab("Slider", m_sliderUI.createSliderPanel());
        addTab("Labels", m_sliderUI.createTicksPanel());
    }

    private JPanel createOptions() {

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = gbc.gridy = 0;
        gbc.weightx = gbc.weighty = 0;

        panel.add(m_hideInWizardCheckbox, gbc);
        gbc.gridx++;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        panel.add(m_deleteOtherFiltersCheckbox, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(m_useLabelCheckbox, gbc);
        gbc.gridx++;
        panel.add(m_customLabelCheckbox, gbc);
        gbc.gridx++;
        gbc.weightx = 1;
        panel.add(m_labelTextfield, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        gbc.gridwidth = 3;
        panel.add(m_sliderUI.createRangePanel(), gbc);
        gbc.gridy++;
        panel.add(m_sliderUI.createStartValuePanel(), gbc);

        return panel;
    }

    private ChangeListener getLabelChangeListener() {
        return new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                updateLabel();
            }
        };
    }

    private void updateLabel() {
        boolean enableAll = m_useLabelCheckbox.isSelected();
        boolean enableText = m_customLabelCheckbox.isSelected();
        m_customLabelCheckbox.setEnabled(enableAll);
        m_labelTextfield.setEnabled(enableAll && enableText);
        if (!enableAll) {
            m_labelTextfield.setText(null);
        }
        if (enableAll && !enableText) {
            String columnName = m_sliderUI.getDomainColumnSelection().getSelected();
            m_labelTextfield.setText(columnName);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
            throws NotConfigurableException {
        m_config.loadSettingsInDialog(settings);
        m_hideInWizardCheckbox.setSelected(m_config.getHideInWizard());
        m_deleteOtherFiltersCheckbox.setSelected(m_config.getDeleteOtherFilters());
        m_useLabelCheckbox.setSelected(m_config.getUseLabel());
        m_customLabelCheckbox.setSelected(m_config.getCustomLabel());
        m_labelTextfield.setText(m_config.getLabel());
        m_sliderUI.getDomainColumnSelection().loadSettingsFrom(settings, specs);
        m_sliderUI.getCustomMinCheckbox().setSelected(m_config.getCustomMin());
        m_sliderUI.getCustomMaxCheckbox().setSelected(m_config.getCustomMax());
        JCheckBox[] domainExtendCheckboxes = m_sliderUI.getStartDomainExtendsCheckboxes();
        if (domainExtendCheckboxes.length != m_config.getUseDomainExtends().length) {
            throw new NotConfigurableException("Length of use domain extends differs in config and slider UI settings.");
        }
        for (int i = 0; i < domainExtendCheckboxes.length; i++) {
            domainExtendCheckboxes[i].setSelected(m_config.getUseDomainExtends()[i]);
        }
        SliderSettings sSettings = m_config.getSliderSettings();
        if (m_config.getDomainColumn().getStringValue() == null) {
            sSettings.setTooltips(new Object[]{true, true});
            SliderPipsSettings defaultPips = new SliderPipsSettings();
            defaultPips.setMode(PipMode.RANGE);
            defaultPips.setDensity(3);
            sSettings.setPips(defaultPips);
        }
        m_sliderUI.loadSettingsFrom(sSettings, (DataTableSpec)specs[0]);

        updateLabel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        m_config.setHideInWizard(m_hideInWizardCheckbox.isSelected());
        m_config.setDeleteOtherFilters(m_deleteOtherFiltersCheckbox.isSelected());
        m_config.setDomainColumn((SettingsModelString)m_sliderUI.getDomainColumnSelection().getModel());
        m_config.setUseLabel(m_useLabelCheckbox.isSelected());
        m_config.setCustomLabel(m_customLabelCheckbox.isSelected());
        m_config.setLabel(m_labelTextfield.getText());
        m_config.setCustomMin(m_sliderUI.getCustomMinCheckbox().isSelected());
        m_config.setCustomMax(m_sliderUI.getCustomMaxCheckbox().isSelected());
        JCheckBox[] domainExtendCheckboxes = m_sliderUI.getStartDomainExtendsCheckboxes();
        boolean[] useDomainExtends = new boolean[domainExtendCheckboxes.length];
        for (int i = 0; i < domainExtendCheckboxes.length; i++) {
            useDomainExtends[i] = domainExtendCheckboxes[i].isSelected();
        }
        m_config.setUseDomainExtends(useDomainExtends);
        SliderSettings sSettings = new SliderSettings();
        m_sliderUI.saveSettings(sSettings);
        sSettings.setBehaviour("drag-tap");
        sSettings.validateSettings();
        m_config.setSliderSettings(sSettings);
        m_config.saveSettings(settings);
    }

}
