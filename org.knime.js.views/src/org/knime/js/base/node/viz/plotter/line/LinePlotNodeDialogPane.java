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
 *  propagated with or for interoperation with KNIME. The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 *
 * History
 *   13.05.2014 (Christian Albrecht, KNIME AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.viz.plotter.line;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

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
import org.knime.core.node.defaultnodesettings.SettingsModelColor;
import org.knime.core.node.util.ColumnSelectionPanel;
import org.knime.core.node.util.DataValueColumnFilter;
import org.knime.core.node.util.filter.column.DataColumnSpecFilterPanel;
import org.knime.js.core.components.datetime.DialogComponentDateTimeOptions;
import org.knime.js.core.components.datetime.SettingsModelDateTimeOptions;

/**
 *
 * @author Christian Albrecht, KNIME AG, Zurich, Switzerland, University of Konstanz
 */
public class LinePlotNodeDialogPane extends NodeDialogPane {
    private static final int TEXT_FIELD_SIZE = 20;

    private final LinePlotViewConfig m_config;

    private final JCheckBox m_generateImageCheckBox;
    private final JCheckBox m_showLegendCheckBox;
    private final JCheckBox m_autoRangeAxisCheckBox;
    private final JCheckBox m_enforceOriginBox;
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
    /*private final JCheckBox m_enableSelectionCheckBox;
    private final JCheckBox m_allowRectangleSelectionCheckBox;
    private final JCheckBox m_allowLassoSelectionCheckBox;*/
    private final JComboBox<String> m_missingValueMethodComboBox;
    private final JCheckBox m_showWarningInViewCheckBox;
    private final JCheckBox m_reportOnMissingValuesCheckBox;

    private final JSpinner m_maxRowsSpinner;
    //private final JTextField m_appendedColumnName;
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
    private final DialogComponentDateTimeOptions m_dateTimeFormats;

    /**
     * Creates a new dialog pane.
     */
    public LinePlotNodeDialogPane() {
        m_config = new LinePlotViewConfig();

        m_generateImageCheckBox = new JCheckBox("Create image at outport");
        m_showLegendCheckBox = new JCheckBox("Show color legend");
        m_autoRangeAxisCheckBox = new JCheckBox("Auto range axes");
        m_enforceOriginBox = new JCheckBox("Enforce Origin in view");
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
        /*m_enableSelectionCheckBox = new JCheckBox("Enable selection");
        m_allowRectangleSelectionCheckBox = new JCheckBox("Enable rectangular selection");
        m_allowLassoSelectionCheckBox = new JCheckBox("Enable lasso selection");*/
        m_missingValueMethodComboBox = new JComboBox<String>();
        m_missingValueMethodComboBox.addItem("Connect");
        m_missingValueMethodComboBox.addItem("Gap");
        m_missingValueMethodComboBox.addItem("Skip column");

        m_maxRowsSpinner = new JSpinner();
        //m_appendedColumnName = new JTextField(TEXT_FIELD_SIZE);
        m_chartTitleTextField = new JTextField(TEXT_FIELD_SIZE);
        m_chartSubtitleTextField = new JTextField(TEXT_FIELD_SIZE);
        Border xColBoxBorder = BorderFactory.createTitledBorder("Choose column for x-axis");
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
        m_reportOnMissingValuesCheckBox = new JCheckBox("Report on missing values");

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
        /*m_enableSelectionCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                enableSelectionControls();
            }
        });*/
        m_showGridCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                m_gridColorChooser.getModel().setEnabled(m_showGridCheckBox.isSelected());

            }
        });

        m_dateTimeFormats = new DialogComponentDateTimeOptions(
            new SettingsModelDateTimeOptions(LinePlotViewConfig.DATE_TIME_FORMATS), "Formatter");

        m_reportOnMissingValuesCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                boolean isReportOnMissingValues = m_reportOnMissingValuesCheckBox.isSelected();
                m_missingValueMethodComboBox.setEnabled(isReportOnMissingValues);
                if (!isReportOnMissingValues) {
                    m_missingValueMethodComboBox.setSelectedItem("Connect");
                }
            }
        });
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
        panel.add(m_generateImageCheckBox, c);
        c.gridx += 2;
        c.gridwidth = 1;
        panel.add(new JLabel("Maximum number of rows: "), c);
        c.gridx++;
        m_maxRowsSpinner.setPreferredSize(new Dimension(100, TEXT_FIELD_SIZE));
        panel.add(m_maxRowsSpinner, c);
        /*c.gridx++;
        panel.add(new JLabel("Selection column name: "), c);
        c.gridx++;
        panel.add(m_appendedColumnName, c);*/
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy++;
        m_xColComboBox.setPreferredSize(new Dimension(260, 50));
        panel.add(m_xColComboBox, c);

        c.gridwidth = 4;
        c.gridx = 0;
        c.gridy++;
        m_yColFilter.setBorder(BorderFactory.createTitledBorder("Choose columns for y-axis"));
        panel.add(m_yColFilter, c);
        c.gridx = 0;
        c.gridy++;

        panel.add(m_reportOnMissingValuesCheckBox, c);
        c.gridy++;
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

        panel.add(m_dateTimeFormats.getPanel(), c);
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
        cc.gridy++;
        rangePanel.add(m_enforceOriginBox, cc);

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
        /*JPanel selectionControlPanel = new JPanel(new GridBagLayout());
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
        c.gridy++;*/
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

    /*private void enableSelectionControls() {
        boolean enable = m_enableSelectionCheckBox.isSelected();
        m_allowRectangleSelectionCheckBox.setEnabled(enable);
        m_allowLassoSelectionCheckBox.setEnabled(enable);
    }*/

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
        m_config.loadSettingsForDialog(settings, specs[0]);
        m_generateImageCheckBox.setSelected(m_config.getGenerateImage());

        m_showLegendCheckBox.setSelected(m_config.getShowLegend());
        m_autoRangeAxisCheckBox.setSelected(m_config.getAutoRangeAxes());
        m_enforceOriginBox.setSelected(m_config.isEnforceOrigin());
        m_useDomainInformationCheckBox.setSelected(m_config.getUseDomainInfo());
        m_showGridCheckBox.setSelected(m_config.getShowGrid());
        m_showCrosshairCheckBox.setSelected(m_config.getShowCrosshair());
        m_snapToPointsCheckBox.setSelected(m_config.getSnapToPoints());
        m_resizeViewToWindow.setSelected(m_config.getResizeToWindow());
        m_displayFullscreenButtonCheckBox.setSelected(m_config.getDisplayFullscreenButton());

        //m_appendedColumnName.setText(m_config.getSelectionColumnName());
        m_enableViewConfigCheckBox.setSelected(m_config.getEnableViewConfiguration());
        m_enableTitleChangeCheckBox.setSelected(m_config.getEnableTitleChange());
        m_enableSubtitleChangeCheckBox.setSelected(m_config.getEnableSubtitleChange());
        m_enableXColumnChangeCheckBox.setSelected(m_config.getEnableXColumnChange());
        m_enableYColumnChangeCheckBox.setSelected(m_config.getEnableYColumnChange());
        m_enableXAxisLabelEditCheckBox.setSelected(m_config.getEnableXAxisLabelEdit());
        m_enableYAxisLabelEditCheckBox.setSelected(m_config.getEnableYAxisLabelEdit());
        m_enableDotSizeChangeCheckBox.setSelected(m_config.getEnableDotSizeChange());
        m_allowMouseWheelZoomingCheckBox.setSelected(m_config.getEnableZooming());
        m_allowDragZoomingCheckBox.setSelected(m_config.getEnableDragZooming());
        m_allowPanningCheckBox.setSelected(m_config.getEnablePanning());
        m_showZoomResetCheckBox.setSelected(m_config.getShowZoomResetButton());
        /*m_enableSelectionCheckBox.setSelected(m_config.getEnableSelection());
        m_allowRectangleSelectionCheckBox.setSelected(m_config.getEnableRectangleSelection());
        m_allowLassoSelectionCheckBox.setSelected(m_config.getEnableLassoSelection());*/
        setMissingValueMethod(m_config.getMissingValueMethod());

        m_chartTitleTextField.setText(m_config.getChartTitle());
        m_chartSubtitleTextField.setText(m_config.getChartSubtitle());
        String xCol = m_config.getxColumn();
        m_xColComboBox.update(specs[0], xCol, true);
        m_yColFilter.loadConfiguration(m_config.getyColumnsConfig(), specs[0]);
        m_xAxisLabelField.setText(m_config.getxAxisLabel());
        m_yAxisLabelField.setText(m_config.getyAxisLabel());
        m_dotSize.setValue(m_config.getDotSize());
        m_maxRowsSpinner.setValue(m_config.getMaxRows());

        m_dateTimeFormats.loadSettingsFromModel(m_config.getDateTimeFormats());

        m_imageWidthSpinner.setValue(m_config.getImageWidth());
        m_imageHeightSpinner.setValue(m_config.getImageHeight());
        m_backgroundColorChooser.setColor(m_config.getBackgroundColor());
        m_dataAreaColorChooser.setColor(m_config.getDataAreaColor());
        m_gridColorChooser.setColor(m_config.getGridColor());
        m_gridColorChooser.getModel().setEnabled(m_showGridCheckBox.isSelected());

        m_showWarningInViewCheckBox.setSelected(m_config.getShowWarningInView());
        m_reportOnMissingValuesCheckBox.setSelected(m_config.getReportOnMissingValues());

        enableViewControls();
        enableCrosshairControls();
        //enableSelectionControls();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        m_dateTimeFormats.validateSettings();

        m_config.setGenerateImage(m_generateImageCheckBox.isSelected());

        m_config.setShowLegend(m_showLegendCheckBox.isSelected());
        m_config.setAutoRangeAxes(m_autoRangeAxisCheckBox.isSelected());
        m_config.setEnforceOrigin(m_enforceOriginBox.isSelected());
        m_config.setUseDomainInfo(m_useDomainInformationCheckBox.isSelected());
        m_config.setShowGrid(m_showGridCheckBox.isSelected());
        m_config.setShowCrosshair(m_showCrosshairCheckBox.isSelected());
        m_config.setSnapToPoints(m_snapToPointsCheckBox.isSelected());
        m_config.setResizeToWindow(m_resizeViewToWindow.isSelected());
        m_config.setDisplayFullscreenButton(m_displayFullscreenButtonCheckBox.isSelected());

        //m_config.setSelectionColumnName(m_appendedColumnName.getText());
        m_config.setEnableViewConfiguration(m_enableViewConfigCheckBox.isSelected());
        m_config.setEnableTitleChange(m_enableTitleChangeCheckBox.isSelected());
        m_config.setEnableSubtitleChange(m_enableSubtitleChangeCheckBox.isSelected());
        m_config.setEnableXColumnChange(m_enableXColumnChangeCheckBox.isSelected());
        m_config.setEnableYColumnChange(m_enableYColumnChangeCheckBox.isSelected());
        m_config.setEnableXAxisLabelEdit(m_enableXAxisLabelEditCheckBox.isSelected());
        m_config.setEnableYAxisLabelEdit(m_enableYAxisLabelEditCheckBox.isSelected());
        m_config.setEnableDotSizeChange(m_enableDotSizeChangeCheckBox.isSelected());
        m_config.setEnableZooming(m_allowMouseWheelZoomingCheckBox.isSelected());
        m_config.setEnableDragZooming(m_allowDragZoomingCheckBox.isSelected());
        m_config.setEnablePanning(m_allowPanningCheckBox.isSelected());
        m_config.setShowZoomResetButton(m_showZoomResetCheckBox.isSelected());
        /*m_config.setEnableSelection(m_enableSelectionCheckBox.isSelected());
        m_config.setEnableRectangleSelection(m_allowRectangleSelectionCheckBox.isSelected());
        m_config.setEnableLassoSelection(m_allowLassoSelectionCheckBox.isSelected());*/
        m_config.setMissingValueMethod(getMissingValueMethod());

        m_config.setChartTitle(m_chartTitleTextField.getText());
        m_config.setChartSubtitle(m_chartSubtitleTextField.getText());
        m_config.setxColumn(m_xColComboBox.getSelectedColumn());
        m_yColFilter.saveConfiguration(m_config.getyColumnsConfig());
        m_config.setxAxisLabel(m_xAxisLabelField.getText());
        m_config.setyAxisLabel(m_yAxisLabelField.getText());
        m_config.setDotSize((Integer)m_dotSize.getValue());
        m_config.setMaxRows((Integer)m_maxRowsSpinner.getValue());

        m_config.setDateTimeFormats((SettingsModelDateTimeOptions)m_dateTimeFormats.getModel());

        m_config.setImageWidth((Integer)m_imageWidthSpinner.getValue());
        m_config.setImageHeight((Integer)m_imageHeightSpinner.getValue());
        m_config.setBackgroundColor(m_backgroundColorChooser.getColor());
        m_config.setDataAreaColor(m_dataAreaColorChooser.getColor());
        m_config.setGridColor(m_gridColorChooser.getColor());

        m_config.setShowWarningInView(m_showWarningInViewCheckBox.isSelected());
        m_config.setReportOnMissingValues(m_reportOnMissingValuesCheckBox.isSelected());

        m_config.saveSettings(settings);
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
}
