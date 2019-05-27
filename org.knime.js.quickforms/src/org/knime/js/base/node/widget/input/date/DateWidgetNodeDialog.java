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
 *   23 May 2019 (albrecht): created
 */
package org.knime.js.base.node.widget.input.date;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemListener;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Optional;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.js.base.node.base.input.date.DateNodeConfig;
import org.knime.js.base.node.base.input.date.DateNodeUtil;
import org.knime.js.base.node.base.input.date.DateNodeValue;
import org.knime.js.base.node.base.input.date.GranularityTime;
import org.knime.js.base.node.configuration.input.date.DateDialogNodeValue;
import org.knime.js.base.node.widget.FlowVariableWidgetNodeDialog;
import org.knime.time.util.DateTimeType;
import org.knime.time.util.DialogComponentDateTimeSelection;
import org.knime.time.util.DialogComponentDateTimeSelection.DisplayOption;
import org.knime.time.util.SettingsModelDateTime;

/**
 * Node dialog for the date widget node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class DateWidgetNodeDialog extends FlowVariableWidgetNodeDialog<DateNodeValue> {

    private final JCheckBox m_showNowButton;
    private final JCheckBox m_useMin;
    private final JCheckBox m_useMax;
    private final JCheckBox m_useMinExecTime;
    private final JCheckBox m_useMaxExecTime;
    private final DialogComponentDateTimeSelection m_min;
    private final DialogComponentDateTimeSelection m_max;
    private final DialogComponentDateTimeSelection m_defaultField;
    private final JCheckBox m_useDefaultExecTime;
    private final JComboBox<DateTimeType> m_type;
    private final JComboBox<GranularityTime> m_granularity;

    private final DateInputWidgetConfig m_config;

    private DateTimeFormatter m_formatter = DateNodeUtil.LOCAL_DATE_TIME_FORMATTER;

    private NodeSettingsRO m_settings;

    /**
     * Constructor, inits fields calls layout routines
     */
    public DateWidgetNodeDialog() {
        m_config = new DateInputWidgetConfig();
        m_type = new JComboBox<DateTimeType>(DateTimeType.values());
        m_type.addActionListener(e -> updateDateTimeComponents());

        m_showNowButton = new JCheckBox();

        m_granularity = new JComboBox<GranularityTime>(GranularityTime.values());

        m_useMin = new JCheckBox();
        m_useMax = new JCheckBox();

        m_useMinExecTime = new JCheckBox("Use execution time");
        m_useMaxExecTime = new JCheckBox("Use execution time");
        m_useDefaultExecTime = new JCheckBox("Use execution time");

        SettingsModelDateTime minModel = new SettingsModelDateTime("min_date_time", LocalDateTime.now().withNano(0));
        m_min = new DialogComponentDateTimeSelection(minModel, null, DisplayOption.SHOW_DATE_AND_TIME_AND_TIMEZONE);
        SettingsModelDateTime maxModel = new SettingsModelDateTime("min_date_time", LocalDateTime.now().withNano(0));
        m_max = new DialogComponentDateTimeSelection(maxModel, null, DisplayOption.SHOW_DATE_AND_TIME_AND_TIMEZONE);
        SettingsModelDateTime defaultModel =
            new SettingsModelDateTime("min_date_time", LocalDateTime.now().withNano(0));
        m_defaultField =
            new DialogComponentDateTimeSelection(defaultModel, null, DisplayOption.SHOW_DATE_AND_TIME_AND_TIMEZONE);
        m_useDefaultExecTime.addItemListener(l -> defaultModel.setEnabled(!m_useDefaultExecTime.isSelected()));

        final ItemListener minListener = e -> {
            minModel.setEnabled(m_useMin.isSelected() && !m_useMinExecTime.isSelected());
            m_useMinExecTime.setEnabled(m_useMin.isSelected());

        };
        final ItemListener maxListener = e -> {
            maxModel.setEnabled(m_useMax.isSelected() && !m_useMaxExecTime.isSelected());
            m_useMaxExecTime.setEnabled(m_useMax.isSelected());

        };
        m_useMin.addItemListener(minListener);
        m_useMinExecTime.addItemListener(minListener);

        m_useMax.addItemListener(maxListener);
        m_useMaxExecTime.addItemListener(maxListener);

        minModel.setEnabled(m_useMin.isSelected() && !m_useMinExecTime.isSelected());
        maxModel.setEnabled(m_useMax.isSelected() && !m_useMaxExecTime.isSelected());
        defaultModel.setEnabled(!m_useDefaultExecTime.isSelected());
        m_useMinExecTime.setEnabled(m_useMin.isSelected());
        m_useMaxExecTime.setEnabled(m_useMax.isSelected());
        createAndAddTab();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getValueString(final NodeSettingsRO settings) throws InvalidSettingsException {
        DateDialogNodeValue value = new DateDialogNodeValue();
        value.loadFromNodeSettings(settings);
        final ZonedDateTime zdt = m_useDefaultExecTime.isSelected() ? ZonedDateTime.now() : value.getDate();
        final DateTimeType type = (DateTimeType)m_type.getSelectedItem();
        final Temporal temporal;
        if (type == DateTimeType.LOCAL_DATE) {
            m_formatter = DateNodeUtil.LOCAL_DATE_FORMATTER;
            temporal = zdt.toLocalDate();
        } else if (type == DateTimeType.LOCAL_TIME) {
            m_formatter = DateNodeUtil.LOCAL_TIME_FORMATTER;
            temporal = zdt.toLocalTime();
        } else if (type == DateTimeType.LOCAL_DATE_TIME) {
            m_formatter = DateNodeUtil.LOCAL_DATE_TIME_FORMATTER;
            temporal = zdt.toLocalDateTime();
        } else {
            m_formatter = DateNodeUtil.ZONED_DATE_TIME_FORMATTER;
            temporal = zdt;
        }
        return m_formatter.format(temporal);
    }

    /**
     * Updates the date&time dialog components depending on the selected type.
     */
    private void updateDateTimeComponents() {
        final DateTimeType type = (DateTimeType)m_type.getSelectedItem();
        if (type == DateTimeType.LOCAL_DATE) {
            m_formatter = DateNodeUtil.LOCAL_DATE_FORMATTER;
        } else if (type == DateTimeType.LOCAL_TIME) {
            m_formatter = DateNodeUtil.LOCAL_TIME_FORMATTER;
        } else if (type == DateTimeType.LOCAL_DATE_TIME) {
            m_formatter = DateNodeUtil.LOCAL_DATE_TIME_FORMATTER;
        } else {
            m_formatter = DateNodeUtil.ZONED_DATE_TIME_FORMATTER;
        }

        final boolean useDate = !(type == DateTimeType.LOCAL_TIME);
        final boolean useTime = !(type == DateTimeType.LOCAL_DATE);
        final boolean useZone = type == DateTimeType.ZONED_DATE_TIME;
        ((SettingsModelDateTime)m_min.getModel()).setUseDate(useDate);
        ((SettingsModelDateTime)m_min.getModel()).setUseTime(useTime);
        ((SettingsModelDateTime)m_min.getModel()).setUseZone(useZone);

        ((SettingsModelDateTime)m_max.getModel()).setUseDate(useDate);
        ((SettingsModelDateTime)m_max.getModel()).setUseTime(useTime);
        ((SettingsModelDateTime)m_max.getModel()).setUseZone(useZone);

        ((SettingsModelDateTime)m_defaultField.getModel()).setUseDate(useDate);
        ((SettingsModelDateTime)m_defaultField.getModel()).setUseTime(useTime);
        ((SettingsModelDateTime)m_defaultField.getModel()).setUseZone(useZone);

        // load current value and update warning label
        if (m_settings != null) {
            try {
                loadCurrentValue(m_settings);
            } catch (InvalidSettingsException e) {
                // nothing to do
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fillPanel(final JPanel panelWithGBLayout, final GridBagConstraints gbc) {
     // typePanel
        JPanel typePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc4 = new GridBagConstraints();
        gbc4.insets = new Insets(0, 0, 0, 0);
        gbc4.anchor = GridBagConstraints.NORTHWEST;
        gbc4.fill = GridBagConstraints.VERTICAL;
        gbc4.weightx = 1;
        gbc4.weighty = 0;
        gbc4.gridx = 0;
        gbc4.gridy = 0;
        gbc4.insets = new Insets(3, 5, 10, 5);
        typePanel.add(m_type, gbc4);

        // minPanel
        JPanel minPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(0, 0, 0, 0);
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.fill = GridBagConstraints.VERTICAL;
        gbc2.weightx = 0;
        gbc2.weighty = 0;
        gbc2.gridx = 0;
        gbc2.gridy = 0;
        gbc2.insets = new Insets(9, 1, 0, 0);
        minPanel.add(m_useMin, gbc2);
        gbc2.weightx = 1;
        gbc2.gridheight = 2;
        gbc2.gridx++;
        gbc2.insets = new Insets(0, 0, 0, 0);
        minPanel.add(m_min.getComponentPanel(), gbc2);
        gbc2.insets = new Insets(0, 7, 10, 0);
        gbc2.gridy += 2;
        minPanel.add(m_useMinExecTime, gbc2);

        // maxPanel
        JPanel maxPanel = new JPanel(new GridBagLayout());
        gbc2.gridheight = 1;
        gbc2.weightx = 0;
        gbc2.gridx = 0;
        gbc2.insets = new Insets(9, 1, 0, 0);
        maxPanel.add(m_useMax, gbc2);
        gbc2.weightx = 1;
        gbc2.gridheight = 2;
        gbc2.gridx++;
        gbc2.insets = new Insets(0, 0, 0, 0);
        maxPanel.add(m_max.getComponentPanel(), gbc2);
        gbc2.insets = new Insets(0, 7, 10, 0);
        gbc2.gridy += 2;
        maxPanel.add(m_useMaxExecTime, gbc2);

        // defaultPanel
        JPanel defaultPanel = new JPanel(new GridBagLayout());
        gbc2.gridheight = 1;
        gbc2.weightx = 0;
        gbc2.gridx = 0;
        gbc2.insets = new Insets(0, 0, 0, 0);
        defaultPanel.add(m_defaultField.getComponentPanel(), gbc2);
        gbc2.gridy++;
        gbc2.insets = new Insets(0, 7, 5, 0);
        defaultPanel.add(m_useDefaultExecTime, gbc2);
        GridBagConstraints gbc3 = (GridBagConstraints)gbc.clone();
        gbc3.anchor = GridBagConstraints.NORTHWEST;
        gbc3.fill = GridBagConstraints.VERTICAL;
        gbc3.insets = new Insets(0, 1, 5, 0);
        addPairToPanel("\"Now\" Button in Wizard: ", m_showNowButton, panelWithGBLayout, gbc3);
        gbc3.insets = new Insets(0, 5, 5, 0);
        gbc3.ipadx = 19;
        addPairToPanel("Granularity in Wizard: ", m_granularity, panelWithGBLayout, gbc3);
        gbc3.ipadx = 0;
        gbc3.insets = new Insets(0, 0, 0, 0);
        addPairToPanel("Type: ", typePanel, panelWithGBLayout, gbc3);
        gbc3.insets = new Insets(-8, 0, 0, 0);
        addPairToPanel("Earliest: ", minPanel, panelWithGBLayout, gbc3);
        addPairToPanel("Latest: ", maxPanel, panelWithGBLayout, gbc3);
        gbc3.insets = new Insets(-8, 22, 0, 0);
        addPairToPanel("Default Value: ", defaultPanel, panelWithGBLayout, gbc3);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs) throws NotConfigurableException {
        m_config.loadSettingsInDialog(settings);
        loadSettingsFrom(m_config);
        ((SettingsModelDateTime)m_defaultField.getModel()).setZonedDateTime(m_config.getDefaultValue().getDate());
        DateNodeConfig dateConfig = m_config.getDateNodeConfig();
        m_showNowButton.setSelected(dateConfig.isShowNowButton());
        m_granularity.setSelectedItem(dateConfig.getGranularity());
        m_useMin.setSelected(dateConfig.isUseMin());
        m_useMax.setSelected(dateConfig.isUseMax());
        m_useMinExecTime.setSelected(dateConfig.isUseMinExecTime());
        m_useMaxExecTime.setSelected(dateConfig.isUseMaxExecTime());
        m_useDefaultExecTime.setSelected(dateConfig.isUseDefaultExecTime());
        ((SettingsModelDateTime)m_min.getModel()).setZonedDateTime(dateConfig.getMin());
        ((SettingsModelDateTime)m_max.getModel()).setZonedDateTime(dateConfig.getMax());
        m_defaultField.getModel().setEnabled(!m_useDefaultExecTime.isSelected());
        m_min.getModel().setEnabled(m_useMin.isSelected() && !m_useMinExecTime.isSelected());
        m_max.getModel().setEnabled(m_useMax.isSelected() && !m_useMaxExecTime.isSelected());
        m_type.setSelectedItem(dateConfig.getType());
        updateDateTimeComponents();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
     // check if min and max are valid dates
        if (m_useMin.isSelected() && !m_useMinExecTime.isSelected()) {
            m_min.validateSettingsBeforeSave();
        }
        if (m_useMax.isSelected() && !m_useMaxExecTime.isSelected()) {
            m_max.validateSettingsBeforeSave();
        }

        // check if min is before max
        final Optional<String> validationResult =
            DateNodeUtil.validateMinMax(((SettingsModelDateTime)m_max.getModel()).getZonedDateTime(),
                true, false, ((SettingsModelDateTime)m_min.getModel()).getZonedDateTime(),
                ((SettingsModelDateTime)m_max.getModel()).getZonedDateTime(), (DateTimeType)m_type.getSelectedItem());
        if (validationResult.isPresent()) {
            throw new InvalidSettingsException("The latest date must not be before the earliest date!");
        }
        // check if default is inside min/max
        if (!m_useDefaultExecTime.isSelected()) {
            m_defaultField.validateSettingsBeforeSave();

            final Optional<String> validationResult2 = DateNodeUtil.validateMinMax(
                ((SettingsModelDateTime)m_defaultField.getModel()).getZonedDateTime(),
                m_useMin.isSelected() && !m_useMinExecTime.isSelected(),
                m_useMax.isSelected() && !m_useMaxExecTime.isSelected(),
                ((SettingsModelDateTime)m_min.getModel()).getZonedDateTime(),
                ((SettingsModelDateTime)m_max.getModel()).getZonedDateTime(), (DateTimeType)m_type.getSelectedItem());
            if (validationResult2.isPresent()) {
                throw new InvalidSettingsException(validationResult2.get());
            }
        }
        saveSettingsTo(m_config);
        m_config.getDefaultValue().setDate(((SettingsModelDateTime)m_defaultField.getModel()).getZonedDateTime());
        DateNodeConfig dateConfig = m_config.getDateNodeConfig();
        dateConfig.setShowNowButton(m_showNowButton.isSelected());
        dateConfig.setGranularity((GranularityTime)m_granularity.getSelectedItem());
        dateConfig.setUseMin(m_useMin.isSelected());
        dateConfig.setUseMax(m_useMax.isSelected());
        dateConfig.setUseMinExecTime(m_useMinExecTime.isSelected());
        dateConfig.setUseMaxExecTime(m_useMaxExecTime.isSelected());
        dateConfig.setUseDefaultExecTime(m_useDefaultExecTime.isSelected());
        dateConfig.setMin(((SettingsModelDateTime)m_min.getModel()).getZonedDateTime());
        dateConfig.setMax(((SettingsModelDateTime)m_max.getModel()).getZonedDateTime());
        dateConfig.setType((DateTimeType)m_type.getSelectedItem());
        m_config.saveSettings(settings);
    }

}
