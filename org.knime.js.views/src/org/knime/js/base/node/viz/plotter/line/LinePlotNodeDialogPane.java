/*
 * ------------------------------------------------------------------------
 *
 *  Copyright (C) 2003 - 2013
 *  University of Konstanz, Germany and
 *  KNIME GmbH, Konstanz, Germany
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
 *  propagated with or for interoperation with KNIME. The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 *
 * History
 *   13.05.2014 (Christian Albrecht, KNIME.com AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.viz.plotter.line;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.StringValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DialogComponentColorChooser;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelColor;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.util.ColumnSelectionPanel;
import org.knime.core.node.util.DataValueColumnFilter;
import org.knime.core.node.util.StringHistory;
import org.knime.core.node.util.filter.column.DataColumnSpecFilterPanel;
import org.knime.js.base.node.viz.pagedTable.PagedTableViewNodeDialogPane;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableBiMap.Builder;

/**
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland, University of Konstanz
 */
public class LinePlotNodeDialogPane extends NodeDialogPane {

    /** BiMap of locale keys and locale values as supported by moment.js */
    public static final BiMap<String, String> PREDEFINED_DATE_TIME_LOCALES = loadDateTimeLocales();

    /**
     * Keys for the string history to re-use user entered date formats.
     */
    public static final String DATE_TIME_FORMAT_HISTORY_KEY = "momentjs-date-formats";
    public static final String DATE_FORMAT_HISTORY_KEY = "momentjs-date-new-formats";
    public static final String TIME_FORMAT_HISTORY_KEY = "momentjs-time-formats";
    public static final String ZONED_DATE_TIME_FORMAT_HISTORY_KEY = "momentjs-zoned-date-time-formats";

    /** Sets of predefined date and time formats for JavaScript processing with moment.js. */
    public static final LinkedHashSet<String> PREDEFINED_DATE_TIME_FORMATS = createPredefinedDateTimeFormats();
    public static final LinkedHashSet<String> PREDEFINED_LOCAL_DATE_FORMATS = createPredefinedLocalDateFormats();
    public static final LinkedHashSet<String> PREDEFINED_LOCAL_DATE_TIME_FORMATS = createPredefinedLocalDateTimeFormats();
    public static final LinkedHashSet<String> PREDEFINED_LOCAL_TIME_FORMATS = createPredefinedLocalTimeFormats();
    public static final LinkedHashSet<String> PREDEFINED_ZONED_DATE_TIME_FORMATS = createPredefinedZonedDateTimeFormats();

    private static final int TEXT_FIELD_SIZE = 20;
    private static final int FORMAT_CHOOSER_WIDTH = 235;
    private static final int FORMAT_CHOOSER_HEIGHT = 17;

    private final JCheckBox m_hideInWizardCheckBox;
    private final JCheckBox m_generateImageCheckBox;
    private final JCheckBox m_showLegendCheckBox;
    private final JCheckBox m_autoRangeAxisCheckBox;
    private final JCheckBox m_useDomainInformationCheckBox;
    private final JCheckBox m_showGridCheckBox;
    private final JCheckBox m_showCrosshairCheckBox;
    private final JCheckBox m_snapToPointsCheckBox;
    private final JCheckBox m_resizeViewToWindow;
    private final JCheckBox m_displayFullscreenButtonCheckBox;
    private final JCheckBox m_enableViewConfigCheckBox;
    private final JCheckBox m_enableTitleChangeCheckBox;
    private final JCheckBox m_enableSubtitleChangeCheckBox;
    private final JCheckBox m_enableXColumnChangeCheckBox;
    private final JCheckBox m_enableYColumnChangeCheckBox;
    private final JCheckBox m_enableXAxisLabelEditCheckBox;
    private final JCheckBox m_enableYAxisLabelEditCheckBox;
    private final JCheckBox m_allowMouseWheelZoomingCheckBox;
    private final JCheckBox m_allowDragZoomingCheckBox;
    private final JCheckBox m_allowPanningCheckBox;
    private final JCheckBox m_showZoomResetCheckBox;
    private final JCheckBox m_enableDotSizeChangeCheckBox;
    private final JCheckBox m_enableSelectionCheckBox;
    private final JCheckBox m_allowRectangleSelectionCheckBox;
    private final JCheckBox m_allowLassoSelectionCheckBox;
    private final JComboBox<String> m_missingValueMethodComboBox;
    private final JCheckBox m_showWarningInViewCheckBox;

    private final JSpinner m_maxRowsSpinner;
    private final JTextField m_appendedColumnName;
    private final JTextField m_chartTitleTextField;
    private final JTextField m_chartSubtitleTextField;
    private final ColumnSelectionPanel m_xColComboBox;
    private final DataColumnSpecFilterPanel m_yColFilter;
    private final JTextField m_xAxisLabelField;
    private final JTextField m_yAxisLabelField;
    private final JSpinner m_dotSize;
    private final JSpinner m_imageWidthSpinner;
    private final JSpinner m_imageHeightSpinner;
    private final DialogComponentColorChooser m_gridColorChooser;
    private final DialogComponentColorChooser m_dataAreaColorChooser;
    private final DialogComponentColorChooser m_backgroundColorChooser;
    private final DialogComponentStringSelection m_globalDateTimeLocaleChooser;
    private final DialogComponentStringSelection m_globalDateTimeFormatChooser;
    private final DialogComponentStringSelection m_globalLocalDateFormatChooser;
    private final DialogComponentStringSelection m_globalLocalDateTimeFormatChooser;
    private final DialogComponentStringSelection m_globalLocalTimeFormatChooser;
    private final DialogComponentStringSelection m_globalZonedDateTimeFormatChooser;
    private final DialogComponentStringSelection m_timezoneChooser;

    /**
     * Creates a new dialog pane.
     */
    public LinePlotNodeDialogPane() {
        m_hideInWizardCheckBox = new JCheckBox("Hide in wizard");
        m_generateImageCheckBox = new JCheckBox("Create image at outport");
        m_showLegendCheckBox = new JCheckBox("Show color legend");
        m_autoRangeAxisCheckBox = new JCheckBox("Auto range axes");
        m_useDomainInformationCheckBox = new JCheckBox("Use domain information");
        m_showGridCheckBox = new JCheckBox("Show grid");
        m_showCrosshairCheckBox = new JCheckBox("Enable mouse crosshair");
        m_snapToPointsCheckBox = new JCheckBox("Snap to data pionts");
        m_resizeViewToWindow = new JCheckBox("Resize view to fill window");
        m_displayFullscreenButtonCheckBox = new JCheckBox("Display fullscreen button");
        m_enableViewConfigCheckBox = new JCheckBox("Enable view edit controls");
        m_enableTitleChangeCheckBox = new JCheckBox("Enable title edit controls");
        m_enableSubtitleChangeCheckBox = new JCheckBox("Enable subtitle edit controls");
        m_enableXColumnChangeCheckBox = new JCheckBox("Enable column chooser for x-axis");
        m_enableYColumnChangeCheckBox = new JCheckBox("Enable column chooser for y-axis");
        m_enableXAxisLabelEditCheckBox = new JCheckBox("Enable label edit for x-axis");
        m_enableYAxisLabelEditCheckBox = new JCheckBox("Enable label edit for y-axis");
        m_enableDotSizeChangeCheckBox = new JCheckBox("Enable dot size edit");
        m_allowMouseWheelZoomingCheckBox = new JCheckBox("Enable mouse wheel zooming");
        m_allowDragZoomingCheckBox = new JCheckBox("Enable drag zooming");
        m_allowPanningCheckBox = new JCheckBox("Enable panning");
        m_showZoomResetCheckBox = new JCheckBox("Show zoom reset button");
        m_enableSelectionCheckBox = new JCheckBox("Enable selection");
        m_allowRectangleSelectionCheckBox = new JCheckBox("Enable rectangular selection");
        m_allowLassoSelectionCheckBox = new JCheckBox("Enable lasso selection");
        m_missingValueMethodComboBox = new JComboBox<String>();
        m_missingValueMethodComboBox.addItem("Connect");
        m_missingValueMethodComboBox.addItem("Gap");
        m_missingValueMethodComboBox.addItem("Skip column");

        m_maxRowsSpinner = new JSpinner();
        m_appendedColumnName = new JTextField(TEXT_FIELD_SIZE);
        m_chartTitleTextField = new JTextField(TEXT_FIELD_SIZE);
        m_chartSubtitleTextField = new JTextField(TEXT_FIELD_SIZE);
        Border xColBoxBorder = BorderFactory.createTitledBorder("Choose column for x axis");
        @SuppressWarnings("unchecked")
        DataValueColumnFilter xColFilter = new DataValueColumnFilter(DoubleValue.class, StringValue.class);
        m_xColComboBox = new ColumnSelectionPanel(xColBoxBorder, xColFilter, false, true);
        m_yColFilter = new DataColumnSpecFilterPanel();
        m_xAxisLabelField = new JTextField(TEXT_FIELD_SIZE);
        m_yAxisLabelField = new JTextField(TEXT_FIELD_SIZE);
        m_dotSize = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        m_imageWidthSpinner = new JSpinner(new SpinnerNumberModel(100, 100, Integer.MAX_VALUE, 1));
        m_imageHeightSpinner = new JSpinner(new SpinnerNumberModel(100, 100, Integer.MAX_VALUE, 1));
        m_gridColorChooser = new DialogComponentColorChooser(
            new SettingsModelColor("gridColor", null), "Grid color: ", true);
        m_dataAreaColorChooser = new DialogComponentColorChooser(
            new SettingsModelColor("dataAreaColor", null), "Data area color: ", true);
        m_backgroundColorChooser = new DialogComponentColorChooser(
            new SettingsModelColor("backgroundColor", null), "Background color: ", true);

        m_showWarningInViewCheckBox = new JCheckBox("Show warnings in view");

        m_enableViewConfigCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
               enableViewControls();
            }
        });
        m_showCrosshairCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                enableCrosshairControls();
            }
        });
        m_enableSelectionCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                enableSelectionControls();
            }
        });
        m_showGridCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                m_gridColorChooser.getModel().setEnabled(m_showGridCheckBox.isSelected());

            }
        });

        m_globalDateTimeLocaleChooser =
                new DialogComponentStringSelection(
                    new SettingsModelString(
                        LinePlotViewConfig.GLOBAL_DATE_TIME_LOCALE,
                        PREDEFINED_DATE_TIME_LOCALES.get(LinePlotViewConfig.DEFAULT_GLOBAL_DATE_TIME_LOCALE)
                    ),
                    "", PREDEFINED_DATE_TIME_LOCALES.values(), true);

        m_globalDateTimeFormatChooser =
            new DialogComponentStringSelection(new SettingsModelString(LinePlotViewConfig.GLOBAL_DATE_TIME_FORMAT,
                LinePlotViewConfig.DEFAULT_GLOBAL_DATE_TIME_FORMAT), "", PREDEFINED_DATE_TIME_FORMATS, true);
        m_globalDateTimeFormatChooser.setSizeComponents(FORMAT_CHOOSER_WIDTH, FORMAT_CHOOSER_HEIGHT);

        m_globalLocalDateFormatChooser =
            new DialogComponentStringSelection(new SettingsModelString(LinePlotViewConfig.GLOBAL_LOCAL_DATE_FORMAT,
                LinePlotViewConfig.DEFAULT_GLOBAL_LOCAL_DATE_FORMAT), "", PREDEFINED_LOCAL_DATE_FORMATS, true);
        m_globalLocalDateFormatChooser.setSizeComponents(FORMAT_CHOOSER_WIDTH, FORMAT_CHOOSER_HEIGHT);

        m_globalLocalDateTimeFormatChooser =
            new DialogComponentStringSelection(new SettingsModelString(LinePlotViewConfig.GLOBAL_LOCAL_DATE_TIME_FORMAT,
                LinePlotViewConfig.DEFAULT_GLOBAL_LOCAL_DATE_TIME_FORMAT), "", PREDEFINED_LOCAL_DATE_TIME_FORMATS, true);
        m_globalLocalDateTimeFormatChooser.setSizeComponents(FORMAT_CHOOSER_WIDTH, FORMAT_CHOOSER_HEIGHT);

        m_globalLocalTimeFormatChooser =
            new DialogComponentStringSelection(new SettingsModelString(LinePlotViewConfig.GLOBAL_LOCAL_TIME_FORMAT,
                LinePlotViewConfig.DEFAULT_GLOBAL_LOCAL_TIME_FORMAT), "", PREDEFINED_LOCAL_TIME_FORMATS, true);
        m_globalLocalTimeFormatChooser.setSizeComponents(FORMAT_CHOOSER_WIDTH, FORMAT_CHOOSER_HEIGHT);

        m_globalZonedDateTimeFormatChooser =
            new DialogComponentStringSelection(new SettingsModelString(LinePlotViewConfig.GLOBAL_ZONED_DATE_TIME_FORMAT,
                LinePlotViewConfig.DEFAULT_GLOBAL_ZONED_DATE_TIME_FORMAT), "", PREDEFINED_ZONED_DATE_TIME_FORMATS, true);
        m_globalZonedDateTimeFormatChooser.setSizeComponents(FORMAT_CHOOSER_WIDTH, FORMAT_CHOOSER_HEIGHT);
        m_globalZonedDateTimeFormatChooser.getModel().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                setTimezoneChooserState();
            }
        });

        m_timezoneChooser = new DialogComponentStringSelection(new SettingsModelString(LinePlotViewConfig.TIMEZONE, LinePlotViewConfig.DEFAULT_TIMEZONE), "",
           new LinkedHashSet<String>(Arrays.asList(TimeZone.getAvailableIDs())), false);
        m_timezoneChooser.setSizeComponents(FORMAT_CHOOSER_WIDTH, FORMAT_CHOOSER_HEIGHT);

        addTab("Options", initOptionsPanel());
        addTab("Axis Configuration", initAxisPanel());
        addTab("General Plot Options", initGeneralPanel());
        addTab("View Controls", initControlsPanel());
    }

    /**
     * @return
     */
    private Component initOptionsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        panel.add(m_hideInWizardCheckBox, c);
        c.gridx += 2;
        panel.add(m_generateImageCheckBox, c);
        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 1;
        panel.add(new JLabel("Maximum number of rows: "), c);
        c.gridx += 1;
        m_maxRowsSpinner.setPreferredSize(new Dimension(100, TEXT_FIELD_SIZE));
        panel.add(m_maxRowsSpinner, c);
        c.gridx++;
        panel.add(new JLabel("Selection column name: "), c);
        c.gridx++;
        panel.add(m_appendedColumnName, c);
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy++;
        m_xColComboBox.setPreferredSize(new Dimension(260, 50));
        panel.add(m_xColComboBox, c);

        c.gridx += 2;
        JPanel missingValuePanel = new JPanel(new GridBagLayout());
        missingValuePanel.setBorder(BorderFactory.createTitledBorder("Missing value handling (y-axis)"));
        panel.add(missingValuePanel, c);
        GridBagConstraints cc = new GridBagConstraints();
        cc.insets = new Insets(1, 1, 1, 1);
        cc.anchor = GridBagConstraints.NORTHWEST;
        cc.gridx = 0;
        cc.gridy = 0;
        cc.gridwidth = 2;
        m_missingValueMethodComboBox.setPreferredSize(new Dimension(250, 25));
        missingValuePanel.add(m_missingValueMethodComboBox, cc);
        cc.gridwidth = 1;
        c.gridx = 0;
        c.gridy++;

        c.gridx = 0;
        panel.add(new JLabel("Choose column for y axis: "), c);
        c.gridy++;
        c.gridwidth = 4;
        panel.add(m_yColFilter, c);

        return panel;
    }

    private Component initAxisPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;

        JPanel labelsPanel = new JPanel(new GridBagLayout());
        labelsPanel.setBorder(BorderFactory.createTitledBorder("Labels"));
        panel.add(labelsPanel, c);
        GridBagConstraints cc = new GridBagConstraints();
        cc.insets = new Insets(5, 5, 5, 5);
        cc.anchor = GridBagConstraints.NORTHWEST;
        cc.gridx = 0;
        cc.gridy = 0;
        labelsPanel.add(new JLabel("Label for x axis: "), cc);
        cc.gridx++;
        labelsPanel.add(m_xAxisLabelField, cc);
        cc.gridx = 0;
        cc.gridy++;
        labelsPanel.add(new JLabel("Label for y axis: "), cc);
        cc.gridx++;
        labelsPanel.add(m_yAxisLabelField, cc);
        c.gridx = 0;
        c.gridy++;

        JPanel formatPanel = new JPanel(new GridBagLayout());
        formatPanel.setBorder(BorderFactory.createTitledBorder("Formatter"));
        panel.add(formatPanel, c);
        cc.gridx = 0;
        cc.gridy = 0;
        formatPanel.add(new JLabel("Locale: "), cc);
        cc.gridx++;
        formatPanel.add(m_globalDateTimeLocaleChooser.getComponentPanel(), cc);
        cc.gridx = 0;
        cc.gridy++;
        formatPanel.add(new JLabel("Local Date format: "), cc);
        cc.gridx++;
        formatPanel.add(m_globalLocalDateFormatChooser.getComponentPanel(), cc);
        cc.gridx = 0;
        cc.gridy++;
        formatPanel.add(new JLabel("Local Date&Time format: "), cc);
        cc.gridx++;
        formatPanel.add(m_globalLocalDateTimeFormatChooser.getComponentPanel(), cc);
        cc.gridx = 0;
        cc.gridy++;
        formatPanel.add(new JLabel("Local Time format: "), cc);
        cc.gridx++;
        formatPanel.add(m_globalLocalTimeFormatChooser.getComponentPanel(), cc);
        cc.gridx = 0;
        cc.gridy++;
        formatPanel.add(new JLabel("Zoned Date&Time format: "), cc);
        cc.gridx++;
        formatPanel.add(m_globalZonedDateTimeFormatChooser.getComponentPanel(), cc);
        cc.gridx = 0;
        cc.gridy++;
        formatPanel.add(new JLabel("Time zone (for zoned format): "), cc);
        cc.gridx++;
        formatPanel.add(m_timezoneChooser.getComponentPanel(), cc);
        cc.gridx = 0;
        cc.gridy++;
        formatPanel.add(new JLabel("Date&Time (legacy) format: "), cc);
        cc.gridx++;
        formatPanel.add(m_globalDateTimeFormatChooser.getComponentPanel(), cc);
        cc.gridx = 0;
        c.gridx = 0;
        c.gridy++;

        JPanel legendPanel = new JPanel(new GridBagLayout());
        legendPanel.setBorder(BorderFactory.createTitledBorder("Legends"));
        panel.add(legendPanel, c);
        cc.gridx = 0;
        cc.gridy = 0;
        legendPanel.add(m_showLegendCheckBox, cc);
        c.gridx = 0;
        c.gridy++;

        JPanel rangePanel = new JPanel(new GridBagLayout());
        rangePanel.setBorder(BorderFactory.createTitledBorder("Axes ranges"));
        panel.add(rangePanel, c);
        cc.gridx = 0;
        cc.gridy = 0;
        rangePanel.add(m_autoRangeAxisCheckBox, cc);
        cc.gridx++;
        rangePanel.add(m_useDomainInformationCheckBox, cc);

        return panel;
    }

    private Component initGeneralPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;

        JPanel titlesPanel = new JPanel(new GridBagLayout());
        titlesPanel.setBorder(BorderFactory.createTitledBorder("Titles"));
        panel.add(titlesPanel, c);
        GridBagConstraints cc = new GridBagConstraints();
        cc.insets = new Insets(5, 5, 5, 5);
        cc.anchor = GridBagConstraints.NORTHWEST;
        cc.gridx = 0;
        cc.gridy = 0;
        titlesPanel.add(new JLabel("Chart title: "), cc);
        cc.gridx++;
        titlesPanel.add(m_chartTitleTextField, cc);
        cc.gridx = 0;
        cc.gridy++;
        titlesPanel.add(new JLabel("Chart subtitle: "), cc);
        cc.gridx++;
        titlesPanel.add(m_chartSubtitleTextField, cc);
        c.gridx = 0;
        c.gridy++;

        JPanel sizesPanel = new JPanel(new GridBagLayout());
        sizesPanel.setBorder(BorderFactory.createTitledBorder("Sizes"));
        panel.add(sizesPanel, c);
        cc.gridx = 0;
        cc.gridy = 0;
        sizesPanel.add(new JLabel("Width of image (in px): "), cc);
        cc.gridx++;
        m_imageWidthSpinner.setPreferredSize(new Dimension(100, TEXT_FIELD_SIZE));
        sizesPanel.add(m_imageWidthSpinner, cc);
        cc.gridx = 0;
        cc.gridy++;
        sizesPanel.add(new JLabel("Height of image (in px): "), cc);
        cc.gridx++;
        m_imageHeightSpinner.setPreferredSize(new Dimension(100, TEXT_FIELD_SIZE));
        sizesPanel.add(m_imageHeightSpinner, cc);
        cc.gridx = 0;
        cc.gridy++;
        cc.anchor = GridBagConstraints.CENTER;
        sizesPanel.add(m_resizeViewToWindow, cc);
        cc.gridx++;
        sizesPanel.add(m_displayFullscreenButtonCheckBox, cc);

        c.gridx = 0;
        c.gridy++;
        JPanel backgroundPanel = new JPanel(new GridBagLayout());
        backgroundPanel.setBorder(BorderFactory.createTitledBorder("Background"));
        panel.add(backgroundPanel, c);
        cc.gridx = 0;
        cc.gridy = 0;
        backgroundPanel.add(m_backgroundColorChooser.getComponentPanel(), cc);
        cc.gridy++;
        backgroundPanel.add(m_dataAreaColorChooser.getComponentPanel(), cc);
        cc.gridy++;
        backgroundPanel.add(m_showGridCheckBox, cc);
        cc.gridy++;
        backgroundPanel.add(m_gridColorChooser.getComponentPanel(), cc);

        c.gridx = 0;
        c.gridy++;
        panel.add(m_showWarningInViewCheckBox, c);

        /*c.gridx = 0;
        c.gridy++;
        panel.add(new JLabel("Dot size: "), c);
        c.gridx++;
        m_dotSize.setPreferredSize(new Dimension(100, 20));
        panel.add(m_dotSize, c);
        c.gridx++;
        c.gridwidth = 2;
        panel.add(m_enableDotSizeChangeCheckBox, c);*/

        return panel;
    }

    private Component initControlsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.NORTHWEST;
        c.ipadx = 20;
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        JPanel viewControlsPanel = new JPanel(new GridBagLayout());
        viewControlsPanel.setBorder(BorderFactory.createTitledBorder("View edit controls"));
        panel.add(viewControlsPanel, c);
        GridBagConstraints cc = new GridBagConstraints();
        cc.insets = new Insets(5, 5, 5, 5);
        cc.anchor = GridBagConstraints.NORTHWEST;
        cc.gridx = 0;
        cc.gridy = 0;
        viewControlsPanel.add(m_enableViewConfigCheckBox, cc);
        cc.gridy++;
        viewControlsPanel.add(m_enableTitleChangeCheckBox, cc);
        cc.gridx += 2;
        viewControlsPanel.add(m_enableSubtitleChangeCheckBox, cc);
        cc.gridx = 0;
        cc.gridy++;
        viewControlsPanel.add(m_enableXColumnChangeCheckBox, cc);
        cc.gridx += 2;
        viewControlsPanel.add(m_enableYColumnChangeCheckBox, cc);
        cc.gridx = 0;
        cc.gridy++;
        viewControlsPanel.add(m_enableXAxisLabelEditCheckBox, cc);
        cc.gridx += 2;
        viewControlsPanel.add(m_enableYAxisLabelEditCheckBox, cc);

        c.gridx = 0;
        c.gridy++;
        JPanel crosshairControlPanel = new JPanel(new GridBagLayout());
        crosshairControlPanel.setBorder(BorderFactory.createTitledBorder("Crosshair"));
        panel.add(crosshairControlPanel, c);
        cc.gridx = 0;
        cc.gridy = 0;
        crosshairControlPanel.add(m_showCrosshairCheckBox, cc);
        cc.gridx += 2;
        crosshairControlPanel.add(m_snapToPointsCheckBox, cc);

        c.gridx = 0;
        c.gridy++;
        JPanel selectionControlPanel = new JPanel(new GridBagLayout());
        selectionControlPanel.setBorder(BorderFactory.createTitledBorder("Selection"));
        panel.add(selectionControlPanel, c);
        cc.gridx = 0;
        cc.gridy = 0;
        selectionControlPanel.add(m_enableSelectionCheckBox, cc);
        cc.gridx++;
        selectionControlPanel.add(m_allowRectangleSelectionCheckBox, cc);
        cc.gridx += 2;
        selectionControlPanel.add(m_allowLassoSelectionCheckBox, cc);

        c.gridx = 0;
        c.gridy++;
        JPanel panControlPanel = new JPanel(new GridBagLayout());
        panControlPanel.setBorder(BorderFactory.createTitledBorder("Panning"));
        panel.add(panControlPanel, c);
        cc.gridx = 0;
        cc.gridy = 0;
        panControlPanel.add(m_allowPanningCheckBox, cc);

        c.gridy++;
        JPanel zoomControlPanel = new JPanel(new GridBagLayout());
        zoomControlPanel.setBorder(BorderFactory.createTitledBorder("Zoom"));
        panel.add(zoomControlPanel, c);
        cc.gridx = 0;
        cc.gridy = 0;
        zoomControlPanel.add(m_allowMouseWheelZoomingCheckBox, cc);
        cc.gridx++;
        zoomControlPanel.add(m_allowDragZoomingCheckBox, cc);
        cc.gridx++;
        zoomControlPanel.add(m_showZoomResetCheckBox, cc);

        return panel;
    }

    private void enableViewControls() {
        boolean enable = m_enableViewConfigCheckBox.isSelected();
        m_enableTitleChangeCheckBox.setEnabled(enable);
        m_enableSubtitleChangeCheckBox.setEnabled(enable);
        m_enableXColumnChangeCheckBox.setEnabled(enable);
        m_enableYColumnChangeCheckBox.setEnabled(enable);
        m_enableXAxisLabelEditCheckBox.setEnabled(enable);
        m_enableYAxisLabelEditCheckBox.setEnabled(enable);
        m_enableDotSizeChangeCheckBox.setEnabled(enable);
    }

    private void enableSelectionControls() {
        boolean enable = m_enableSelectionCheckBox.isSelected();
        m_allowRectangleSelectionCheckBox.setEnabled(enable);
        m_allowLassoSelectionCheckBox.setEnabled(enable);
    }

    private void enableCrosshairControls() {
        boolean enable = m_showCrosshairCheckBox.isSelected();
        m_snapToPointsCheckBox.setEnabled(enable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final DataTableSpec[] specs)
            throws NotConfigurableException {
        LinePlotViewConfig config = new LinePlotViewConfig();
        config.loadSettingsForDialog(settings, specs[0]);
        m_hideInWizardCheckBox.setSelected(config.getHideInWizard());
        m_generateImageCheckBox.setSelected(config.getGenerateImage());

        m_showLegendCheckBox.setSelected(config.getShowLegend());
        m_autoRangeAxisCheckBox.setSelected(config.getAutoRangeAxes());
        m_useDomainInformationCheckBox.setSelected(config.getUseDomainInfo());
        m_showGridCheckBox.setSelected(config.getShowGrid());
        m_showCrosshairCheckBox.setSelected(config.getShowCrosshair());
        m_snapToPointsCheckBox.setSelected(config.getSnapToPoints());
        m_resizeViewToWindow.setSelected(config.getResizeToWindow());
        m_displayFullscreenButtonCheckBox.setSelected(config.getDisplayFullscreenButton());

        m_appendedColumnName.setText(config.getSelectionColumnName());
        m_enableViewConfigCheckBox.setSelected(config.getEnableViewConfiguration());
        m_enableTitleChangeCheckBox.setSelected(config.getEnableTitleChange());
        m_enableSubtitleChangeCheckBox.setSelected(config.getEnableSubtitleChange());
        m_enableXColumnChangeCheckBox.setSelected(config.getEnableXColumnChange());
        m_enableYColumnChangeCheckBox.setSelected(config.getEnableYColumnChange());
        m_enableXAxisLabelEditCheckBox.setSelected(config.getEnableXAxisLabelEdit());
        m_enableYAxisLabelEditCheckBox.setSelected(config.getEnableYAxisLabelEdit());
        m_enableDotSizeChangeCheckBox.setSelected(config.getEnableDotSizeChange());
        m_allowMouseWheelZoomingCheckBox.setSelected(config.getEnableZooming());
        m_allowDragZoomingCheckBox.setSelected(config.getEnableDragZooming());
        m_allowPanningCheckBox.setSelected(config.getEnablePanning());
        m_showZoomResetCheckBox.setSelected(config.getShowZoomResetButton());
        m_enableSelectionCheckBox.setSelected(config.getEnableSelection());
        m_allowRectangleSelectionCheckBox.setSelected(config.getEnableRectangleSelection());
        m_allowLassoSelectionCheckBox.setSelected(config.getEnableLassoSelection());
        setMissingValueMethod(config.getMissingValueMethod());

        m_chartTitleTextField.setText(config.getChartTitle());
        m_chartSubtitleTextField.setText(config.getChartSubtitle());
        String xCol = config.getxColumn();
        m_xColComboBox.update(specs[0], xCol, true);
        m_yColFilter.loadConfiguration(config.getyColumnsConfig(), specs[0]);
        m_xAxisLabelField.setText(config.getxAxisLabel());
        m_yAxisLabelField.setText(config.getyAxisLabel());
        m_dotSize.setValue(config.getDotSize());
        m_maxRowsSpinner.setValue(config.getMaxRows());

        m_globalDateTimeLocaleChooser.replaceListItems(loadDateTimeLocales().values(),
            PREDEFINED_DATE_TIME_LOCALES.get(config.getGlobalDateTimeLocale()));
        m_globalDateTimeFormatChooser.replaceListItems(createPredefinedDateTimeFormats(), config.getGlobalDateTimeFormat());
        m_globalLocalDateFormatChooser.replaceListItems(createPredefinedLocalDateFormats(), config.getGlobalLocalDateFormat());
        m_globalLocalDateTimeFormatChooser.replaceListItems(createPredefinedLocalDateTimeFormats(), config.getGlobalLocalDateTimeFormat());
        m_globalLocalTimeFormatChooser.replaceListItems(createPredefinedLocalTimeFormats(), config.getGlobalLocalTimeFormat());
        m_globalZonedDateTimeFormatChooser.replaceListItems(createPredefinedZonedDateTimeFormats(), config.getGlobalZonedDateTimeFormat());
        ((SettingsModelString)m_timezoneChooser.getModel()).setStringValue(config.getTimezone());

        m_imageWidthSpinner.setValue(config.getImageWidth());
        m_imageHeightSpinner.setValue(config.getImageHeight());
        m_backgroundColorChooser.setColor(config.getBackgroundColor());
        m_dataAreaColorChooser.setColor(config.getDataAreaColor());
        m_gridColorChooser.setColor(config.getGridColor());
        m_gridColorChooser.getModel().setEnabled(m_showGridCheckBox.isSelected());

        m_showWarningInViewCheckBox.setSelected(config.getShowWarningInView());

        enableViewControls();
        enableCrosshairControls();
        enableSelectionControls();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        validateSettings();

        LinePlotViewConfig config = new LinePlotViewConfig();
        config.setHideInWizard(m_hideInWizardCheckBox.isSelected());
        config.setGenerateImage(m_generateImageCheckBox.isSelected());

        config.setShowLegend(m_showLegendCheckBox.isSelected());
        config.setAutoRangeAxes(m_autoRangeAxisCheckBox.isSelected());
        config.setUseDomainInfo(m_useDomainInformationCheckBox.isSelected());
        config.setShowGrid(m_showGridCheckBox.isSelected());
        config.setShowCrosshair(m_showCrosshairCheckBox.isSelected());
        config.setSnapToPoints(m_snapToPointsCheckBox.isSelected());
        config.setResizeToWindow(m_resizeViewToWindow.isSelected());
        config.setDisplayFullscreenButton(m_displayFullscreenButtonCheckBox.isSelected());

        config.setSelectionColumnName(m_appendedColumnName.getText());
        config.setEnableViewConfiguration(m_enableViewConfigCheckBox.isSelected());
        config.setEnableTitleChange(m_enableTitleChangeCheckBox.isSelected());
        config.setEnableSubtitleChange(m_enableSubtitleChangeCheckBox.isSelected());
        config.setEnableXColumnChange(m_enableXColumnChangeCheckBox.isSelected());
        config.setEnableYColumnChange(m_enableYColumnChangeCheckBox.isSelected());
        config.setEnableXAxisLabelEdit(m_enableXAxisLabelEditCheckBox.isSelected());
        config.setEnableYAxisLabelEdit(m_enableYAxisLabelEditCheckBox.isSelected());
        config.setEnableDotSizeChange(m_enableDotSizeChangeCheckBox.isSelected());
        config.setEnableZooming(m_allowMouseWheelZoomingCheckBox.isSelected());
        config.setEnableDragZooming(m_allowDragZoomingCheckBox.isSelected());
        config.setEnablePanning(m_allowPanningCheckBox.isSelected());
        config.setShowZoomResetButton(m_showZoomResetCheckBox.isSelected());
        config.setEnableSelection(m_enableSelectionCheckBox.isSelected());
        config.setEnableRectangleSelection(m_allowRectangleSelectionCheckBox.isSelected());
        config.setEnableLassoSelection(m_allowLassoSelectionCheckBox.isSelected());
        config.setMissingValueMethod(getMissingValueMethod());

        config.setChartTitle(m_chartTitleTextField.getText());
        config.setChartSubtitle(m_chartSubtitleTextField.getText());
        config.setxColumn(m_xColComboBox.getSelectedColumn());
        m_yColFilter.saveConfiguration(config.getyColumnsConfig());
        config.setxAxisLabel(m_xAxisLabelField.getText());
        config.setyAxisLabel(m_yAxisLabelField.getText());
        config.setDotSize((Integer)m_dotSize.getValue());
        config.setMaxRows((Integer)m_maxRowsSpinner.getValue());

        config.setGlobalDateTimeLocale(PREDEFINED_DATE_TIME_LOCALES.inverse().get(
            ((SettingsModelString)m_globalDateTimeLocaleChooser.getModel()).getStringValue())
        );
        String globalDateTimeFormat = ((SettingsModelString)m_globalDateTimeFormatChooser.getModel()).getStringValue();
        config.setGlobalDateTimeFormat(globalDateTimeFormat);
        String globalLocalDateFormat = ((SettingsModelString)m_globalLocalDateFormatChooser.getModel()).getStringValue();
        config.setGlobalLocalDateFormat(globalLocalDateFormat);
        String globalLocalDateTimeFormat = ((SettingsModelString)m_globalLocalDateTimeFormatChooser.getModel()).getStringValue();
        config.setGlobalLocalDateTimeFormat(globalLocalDateTimeFormat);
        String globalLocalTimeFormat = ((SettingsModelString)m_globalLocalTimeFormatChooser.getModel()).getStringValue();
        config.setGlobalLocalTimeFormat(globalLocalTimeFormat);
        String globalZonedDateTimeFormat = ((SettingsModelString)m_globalZonedDateTimeFormatChooser.getModel()).getStringValue();
        config.setGlobalZonedDateTimeFormat(globalZonedDateTimeFormat);
        StringHistory.getInstance(DATE_TIME_FORMAT_HISTORY_KEY).add(globalDateTimeFormat);
        StringHistory.getInstance(DATE_FORMAT_HISTORY_KEY).add(globalLocalDateFormat);
        StringHistory.getInstance(DATE_TIME_FORMAT_HISTORY_KEY).add(globalLocalDateTimeFormat);
        StringHistory.getInstance(TIME_FORMAT_HISTORY_KEY).add(globalLocalTimeFormat);
        StringHistory.getInstance(ZONED_DATE_TIME_FORMAT_HISTORY_KEY).add(globalZonedDateTimeFormat);
        String timezone = ((SettingsModelString)m_timezoneChooser.getModel()).getStringValue();
        config.setTimezone(timezone);

        config.setImageWidth((Integer)m_imageWidthSpinner.getValue());
        config.setImageHeight((Integer)m_imageHeightSpinner.getValue());
        config.setBackgroundColor(m_backgroundColorChooser.getColor());
        config.setDataAreaColor(m_dataAreaColorChooser.getColor());
        config.setGridColor(m_gridColorChooser.getColor());

        config.setShowWarningInView(m_showWarningInViewCheckBox.isSelected());

        config.saveSettings(settings);
    }

    private void validateSettings() throws InvalidSettingsException {
        // validate only local time to prevent date related symbols (otherwise a dat will be displayed for the time only)
        // all other formats are free to have any other symbols
        String localTimeFormatString = ((SettingsModelString)m_globalLocalTimeFormatChooser.getModel()).getStringValue();
        String pattern = "(\\[.*\\])*((A|a|H|h|k|m|S|s|[^a-zA-Z]|\\[.*\\])+|(LT|LTS))(\\[.*\\])*";
        if (!Pattern.matches(pattern, localTimeFormatString)) {
            throw new InvalidSettingsException("Local Time format is not valid.");
        }
    }

    /**
     * @return a list of predefined formats for use in a date format with moment.js
     */
    public static LinkedHashSet<String> createPredefinedDateTimeFormats() {
        LinkedHashSet<String> formats = new LinkedHashSet<String>();

        // check the StringHistory first
        String[] userFormats = StringHistory.getInstance(DATE_TIME_FORMAT_HISTORY_KEY).getHistory();
        for (String userFormat : userFormats) {
            formats.add(userFormat);
        }

        formats.add("YYYY-MM-DD");
        formats.add("ddd MMM DD YYYY HH:mm:ss");
        formats.add("M/D/YY");
        formats.add("MMM D, YYYY");
        formats.add("MMMM D, YYYY");
        formats.add("dddd, MMM D, YYYY");
        formats.add("h:mm A");
        formats.add("h:mm:ss A");
        formats.add("HH:mm:ss");
        formats.add("YYYY-MM-DD;HH:mm:ss.SSS");

        return formats;
    }

    /**
     * @return a list of predefined formats for use in a date format with moment.js
     */
    private static LinkedHashSet<String> createPredefinedZonedDateTimeFormats() {
        LinkedHashSet<String> formats = new LinkedHashSet<String>();

        // check the StringHistory first
        formats.addAll(Arrays.asList(
            StringHistory.getInstance(ZONED_DATE_TIME_FORMAT_HISTORY_KEY).getHistory()
        ));
        formats.addAll(Arrays.asList(
            StringHistory.getInstance(DATE_TIME_FORMAT_HISTORY_KEY).getHistory()
        ));
        formats.addAll(Arrays.asList(
            StringHistory.getInstance(DATE_FORMAT_HISTORY_KEY).getHistory()
        ));
        formats.addAll(Arrays.asList(
            StringHistory.getInstance(TIME_FORMAT_HISTORY_KEY).getHistory()
        ));

        formats.add("YYYY-MM-DD z");
        formats.add("ddd MMM DD YYYY HH:mm:ss z");
        formats.add("M/D/YY z");
        formats.add("MMM D, YYYY z");
        formats.add("MMMM D, YYYY z");
        formats.add("dddd, MMM D, YYYY z");
        formats.add("h:mm A z");
        formats.add("h:mm:ss A z");
        formats.add("HH:mm:ss z");
        formats.add("YYYY-MM-DD;HH:mm:ss.SSS z");

        formats.add("YYYY-MM-DD");
        formats.add("ddd MMM DD YYYY HH:mm:ss");
        formats.add("M/D/YY");
        formats.add("MMM D, YYYY");
        formats.add("MMMM D, YYYY");
        formats.add("dddd, MMM D, YYYY");
        formats.add("h:mm A");
        formats.add("h:mm:ss A");
        formats.add("HH:mm:ss");
        formats.add("YYYY-MM-DD;HH:mm:ss.SSS");

        return formats;
    }

    /**
     * @return a list of predefined formats for use in a date format with moment.js
     */
    private static LinkedHashSet<String> createPredefinedLocalTimeFormats() {
        LinkedHashSet<String> formats = new LinkedHashSet<String>();

        // check also the StringHistory....
        formats.addAll(Arrays.asList(
            StringHistory.getInstance(TIME_FORMAT_HISTORY_KEY).getHistory()
        ));

        formats.add("HH:mm:ss");
        formats.add("h:mm A");
        formats.add("h:mm:ss A");
        formats.add("HH:mm:ss.SSS");

        return formats;
    }

    /**
     * @return a list of predefined formats for use in a date format with moment.js
     */
    private static LinkedHashSet<String> createPredefinedLocalDateTimeFormats() {
        LinkedHashSet<String> formats = new LinkedHashSet<String>();
        formats.add("YYYY-MM-DD");
        formats.add("ddd MMM DD YYYY HH:mm:ss");
        formats.add("M/D/YY");
        formats.add("MMM D, YYYY");
        formats.add("MMMM D, YYYY");
        formats.add("dddd, MMM D, YYYY");
        formats.add("h:mm A");
        formats.add("h:mm:ss A");
        formats.add("HH:mm:ss");
        formats.add("YYYY-MM-DD;HH:mm:ss.SSS");

        // check the StringHistory first
        formats.addAll(Arrays.asList(
            StringHistory.getInstance(DATE_TIME_FORMAT_HISTORY_KEY).getHistory()
        ));
        formats.addAll(Arrays.asList(
            StringHistory.getInstance(DATE_FORMAT_HISTORY_KEY).getHistory()
        ));
        formats.addAll(Arrays.asList(
            StringHistory.getInstance(TIME_FORMAT_HISTORY_KEY).getHistory()
        ));

        return formats;
    }

    /**
     * @return a list of predefined formats for use in a date format with moment.js
     */
    private static LinkedHashSet<String> createPredefinedLocalDateFormats() {
        LinkedHashSet<String> formats = new LinkedHashSet<String>();

        // check the StringHistory first
        formats.addAll(Arrays.asList(
            StringHistory.getInstance(DATE_FORMAT_HISTORY_KEY).getHistory()
        ));

        formats.add("YYYY-MM-DD");
        formats.add("M/D/YY");
        formats.add("MMM D, YYYY");
        formats.add("MMMM D, YYYY");
        formats.add("dddd, MMM D, YYYY");

        return formats;
    }

    /**
     * @return a BiMap of locale keys and locale values as supported by moment.js
     * @throws IOException
     */
    private static BiMap<String, String> loadDateTimeLocales() {
        Builder<String, String> biMapBuilder = ImmutableBiMap.builder();

        Properties props = new Properties();
        InputStream input = PagedTableViewNodeDialogPane.class.getResourceAsStream("locales.properties");

        try {
            props.load(input);
            props.entrySet().stream()
                .sorted(
                    (e1, e2) -> ((String)e1.getValue()).toLowerCase().compareTo(((String)e2.getValue()).toLowerCase())
                )
                .forEach(
                    (entry) -> biMapBuilder.put((String)entry.getKey(), (String)entry.getValue())
                );

        } catch (IOException e) {
            biMapBuilder.put("en", "English (United States)");
        }

        return biMapBuilder.build();
    }

    private void setMissingValueMethod(final String method) {
        switch (method) {
            case LinePlotViewConfig.MISSING_VALUE_METHOD_GAP:
                m_missingValueMethodComboBox.setSelectedIndex(1);
                break;
            case LinePlotViewConfig.MISSING_VALUE_METHOD_REMOVE_COLUMN:
                m_missingValueMethodComboBox.setSelectedIndex(2);
                break;
            case LinePlotViewConfig.MISSING_VALUE_METHOD_NO_GAP:
            default:
                m_missingValueMethodComboBox.setSelectedIndex(0);
                break;
        }
    }

    private String getMissingValueMethod() {
        switch (m_missingValueMethodComboBox.getSelectedIndex()) {
            case 0:
                return LinePlotViewConfig.MISSING_VALUE_METHOD_NO_GAP;
            case 1:
                return LinePlotViewConfig.MISSING_VALUE_METHOD_GAP;
            case 2:
                return LinePlotViewConfig.MISSING_VALUE_METHOD_REMOVE_COLUMN;
        }
        return null;
    }

    private void setTimezoneChooserState() {
        String zonedValue = ((SettingsModelString)m_globalZonedDateTimeFormatChooser.getModel()).getStringValue();
        boolean enabled = zonedValue.indexOf('z') != -1 || zonedValue.indexOf('Z') != -1;
        m_timezoneChooser.getModel().setEnabled(enabled);
        String tooltip = enabled ? "" : "Zone date&time format must contain a zone symbol ('z' or 'Z') to enable the time zone selector";
        m_timezoneChooser.setToolTipText(tooltip);
    }
}
