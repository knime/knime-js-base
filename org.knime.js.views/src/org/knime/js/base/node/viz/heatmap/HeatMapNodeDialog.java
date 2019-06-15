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
 *   Aug 3, 2018 (awalter): created
 */
package org.knime.js.base.node.viz.heatmap;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.knime.base.data.xml.SvgValue;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.NominalValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DialogComponentColorChooser;
import org.knime.core.node.defaultnodesettings.SettingsModelColor;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.ColumnSelectionPanel;
import org.knime.core.node.util.DataValueColumnFilter;
import org.knime.core.node.util.filter.column.DataColumnSpecFilterConfiguration;
import org.knime.core.node.util.filter.column.DataColumnSpecFilterPanel;
import org.knime.js.core.CSSUtils;
import org.knime.js.core.settings.DialogUtil;

/**
 * Dialog for the Heatmap node.
 *
 * @author Alison Walter, KNIME GmbH, Konstanz, Germany
 */
public class HeatMapNodeDialog extends NodeDialogPane {

    private static final int TEXT_FIELD_SIZE = 20;

    private final HeatMapViewConfig m_config;

    private DataColumnSpecFilterConfiguration m_columnSpecFilter = null;
    private DataTableSpec m_spec;

    private final DataColumnSpecFilterPanel m_columnsFilterPanel;
    private final ColumnSelectionPanel m_labelColumnColumnSelectionPanel;
    private final ColumnSelectionPanel m_svgLabelColumnColumnSelectionPanel;
    private final JCheckBox m_customMinValueCheckBox;
    private final JCheckBox m_customMaxValueCheckBox;
    private final JSpinner m_minValueSpinner;
    private final JSpinner m_maxValueSpinner;

    private final DialogComponentColorChooser m_firstColorColorChooser;
    private final DialogComponentColorChooser m_secondColorColorChooser;
    private final DialogComponentColorChooser m_thirdColorColorChooser;
    private final JCheckBox m_useBinsCheckBox;
    private final JSpinner m_numberOfBinsSpinner;
    private final ThreeColorGradientComponent m_gradientColors;
    private final DialogComponentColorChooser m_missingValueColorColorChooser;
    private final DialogComponentColorChooser m_upperOutOfRangeColorColorChooser;
    private final DialogComponentColorChooser m_lowerOutOfRangeColorColorChooser;

    private final JCheckBox m_showWarningInViewCheckBox;
    private final JCheckBox m_generateImageCheckBox;
    private final JSpinner m_imageWidthSpinner;
    private final JSpinner m_imageHeightSpinner;
    private final JCheckBox m_resizeToWindowCheckBox;
    private final JCheckBox m_displayFullscreenButtonCheckBox;
    private final JTextField m_chartTitleTextField;
    private final JTextField m_chartSubtitleTextField;

    private final JCheckBox m_enableViewConfigurationCheckBox;
    private final JCheckBox m_enableTitleChangeCheckBox;
    private final JCheckBox m_enableColorModeEditCheckBox;
    private final JCheckBox m_enableShowToolTipsCheckBox;
    private final JCheckBox m_showToolTipsCheckBox;
    private final JCheckBox m_subscribeFilterCheckBox;
    private final JCheckBox m_enableSelectionCheckBox;
    private final JCheckBox m_publishSelectionCheckBox;
    private final JCheckBox m_subscribeSelectionCheckBox;
    private final JTextField m_selectionColumnNameTextField;
    private final JCheckBox m_showResetSelectionButtonCheckBox;
    private final JCheckBox m_showSelectedRowOnlyCheckBox;
    private final JCheckBox m_enableShowSelectedRowOnlyCheckBox;
    private final JCheckBox m_enablePagingCheckBox;
    private final JSpinner m_initialPageSizeSpinner;
    private final JCheckBox m_enablePageSizeChangeCheckBox;
    private final JTextField m_allowedPageSizesTextField;
    private final JCheckBox m_pageSizeShowAllCheckBox;
    private final JCheckBox m_enableZoomCheckBox;
    private final JCheckBox m_enablePanningCheckBox;
    private final JCheckBox m_showZoomResetButtonCheckBox;

    @SuppressWarnings("unchecked")
    HeatMapNodeDialog() {
        m_config = new HeatMapViewConfig();

        m_columnsFilterPanel = new DataColumnSpecFilterPanel();
        m_columnsFilterPanel.setBorder(BorderFactory.createTitledBorder("Columns to display"));
        m_labelColumnColumnSelectionPanel =
            new ColumnSelectionPanel(BorderFactory.createTitledBorder("Choose a label column: "),
                new DataValueColumnFilter(NominalValue.class), false, true);
        m_svgLabelColumnColumnSelectionPanel =
            new ColumnSelectionPanel(BorderFactory.createTitledBorder("Choose an image column: "),
                new DataValueColumnFilter(SvgValue.class), true, false);
        m_svgLabelColumnColumnSelectionPanel.setRequired(false);
        m_customMinValueCheckBox = new JCheckBox("Custom:", false);
        m_customMaxValueCheckBox = new JCheckBox("Custom:", false);
        m_minValueSpinner = new JSpinner(new SpinnerNumberModel(0.01, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0.01));
        m_minValueSpinner.setPreferredSize(new Dimension(260, 20));
        m_maxValueSpinner = new JSpinner(new SpinnerNumberModel(0.01, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0.01));
        m_maxValueSpinner.setPreferredSize(new Dimension(260, 20));
        m_columnsFilterPanel.addChangeListener(e -> updateMinMax());
        m_customMinValueCheckBox.addChangeListener(e -> enableCustomMin());
        m_customMaxValueCheckBox.addChangeListener(e -> enableCustomMax());
        m_minValueSpinner.addChangeListener(e -> minGreaterThanMax());
        m_maxValueSpinner.addChangeListener(e -> maxLessThanMin());

        m_gradientColors = new ThreeColorGradientComponent(Color.BLACK, Color.BLACK, Color.BLACK);
        m_firstColorColorChooser = new DialogComponentColorChooser(new SettingsModelColor("firstGradientColor", Color.BLACK), null, true) {
            @Override
            public void setColor(final Color newValue) {
                super.setColor(newValue);
                m_gradientColors.setFirstColor(getColor());
            }
        };
        m_secondColorColorChooser = new DialogComponentColorChooser(new SettingsModelColor("secondGradientColor", Color.BLACK), null, true) {
            @Override
            public void setColor(final Color newValue) {
                super.setColor(newValue);
                m_gradientColors.setMiddleColor(getColor());
            }
        };
        m_thirdColorColorChooser = new DialogComponentColorChooser(new SettingsModelColor("thirdGradientColor", Color.BLACK), null, true) {
            @Override
            public void setColor(final Color newValue) {
                super.setColor(newValue);
                m_gradientColors.setLastColor(getColor());
            }
        };
        m_useBinsCheckBox = new JCheckBox("Use discrete gradient");
        m_numberOfBinsSpinner = new JSpinner(new SpinnerNumberModel(3, 3, 21, 2));
        m_useBinsCheckBox.addChangeListener(e -> enableSpinner());
        m_numberOfBinsSpinner.addChangeListener(e -> updateNumberOfBins());
        m_missingValueColorColorChooser = new DialogComponentColorChooser(new SettingsModelColor("missingColor", Color.BLACK), "Select color for missing values:", true);
        m_upperOutOfRangeColorColorChooser = new DialogComponentColorChooser(new SettingsModelColor("upperOutOfRangeColor", Color.BLACK), "Select a color for values greater than the range maximum:", true);
        m_lowerOutOfRangeColorColorChooser = new DialogComponentColorChooser(new SettingsModelColor("lowerOutOfRangeColor", Color.BLACK), "Select a color for values less than the range minimum:", true);

        m_showWarningInViewCheckBox = new JCheckBox("Show warnings in view");
        m_generateImageCheckBox = new JCheckBox("Create image at outport");
        m_imageWidthSpinner = new JSpinner(new SpinnerNumberModel(100, 100, Integer.MAX_VALUE, 1));
        m_imageHeightSpinner = new JSpinner(new SpinnerNumberModel(100, 100, Integer.MAX_VALUE, 1));
        m_resizeToWindowCheckBox = new JCheckBox("Resize view to fill window");
        m_displayFullscreenButtonCheckBox = new JCheckBox("Display fullscreen button");
        m_chartTitleTextField = new JTextField(TEXT_FIELD_SIZE);
        m_chartSubtitleTextField = new JTextField(TEXT_FIELD_SIZE);

        m_enableViewConfigurationCheckBox = new JCheckBox("Enable view edit controls");
        m_enableViewConfigurationCheckBox.addChangeListener(e -> enableViewEdit());
        m_enableTitleChangeCheckBox = new JCheckBox("Enable title/subtitle edit controls");
        m_enableColorModeEditCheckBox = new JCheckBox("Enable color mode edit");
        m_enableShowToolTipsCheckBox = new JCheckBox("Enable 'show tool tips' option");
        m_showToolTipsCheckBox = new JCheckBox("Show tool tips");
        m_subscribeFilterCheckBox = new JCheckBox("Subscribe to filter events");
        m_enableSelectionCheckBox = new JCheckBox("Enable selection");
        m_enableSelectionCheckBox.addChangeListener(e -> enableSelection());
        m_publishSelectionCheckBox = new JCheckBox("Publish selection events");
        m_subscribeSelectionCheckBox = new JCheckBox("Subscribe to selection events");
        m_selectionColumnNameTextField = new JTextField(TEXT_FIELD_SIZE);
        m_showResetSelectionButtonCheckBox = new JCheckBox("Show reset selection button");
        m_showSelectedRowOnlyCheckBox = new JCheckBox("Show selected rows only");
        m_enableShowSelectedRowOnlyCheckBox = new JCheckBox("Enable 'Show selected rows only' option");
        m_enablePagingCheckBox = new JCheckBox("Enable pagination");
        m_enablePagingCheckBox.addChangeListener(e -> enablePaging());
        m_initialPageSizeSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        m_enablePageSizeChangeCheckBox = new JCheckBox("Enable page size change control");
        m_enablePageSizeChangeCheckBox.addChangeListener(e -> enablePaging());
        m_allowedPageSizesTextField = new JTextField(TEXT_FIELD_SIZE);
        m_pageSizeShowAllCheckBox = new JCheckBox("Add \"All\" option to page sizes");
        m_enableZoomCheckBox = new JCheckBox("Enable zooming");
        m_enablePanningCheckBox = new JCheckBox("Enable panning");
        m_showZoomResetButtonCheckBox = new JCheckBox("Show zoom reset button");

        addTab("Options", createOptionsTab());
        addTab("View Configuration", createViewConfigTab());
        addTab("Interactivity", createInteractivityTab());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        m_columnsFilterPanel.saveConfiguration(m_config.getColumns());
        final String[] includedCols = m_config.getColumns().applyTo(m_spec).getIncludes();
        if (includedCols == null || includedCols.length < 1) {
            throw new InvalidSettingsException("At least one numeric column must be included");
        }
        m_config.setLabelColumn(m_labelColumnColumnSelectionPanel.getSelectedColumn());
        m_config.setSvgLabelColumn(m_svgLabelColumnColumnSelectionPanel.isEnabled()
            ? m_svgLabelColumnColumnSelectionPanel.getSelectedColumn() : null);
        m_config.setMinValue((double) m_minValueSpinner.getValue());
        m_config.setMaxValue((double) m_maxValueSpinner.getValue());
        m_config.setUseCustomMin(m_customMinValueCheckBox.isSelected());
        m_config.setUseCustomMax(m_customMaxValueCheckBox.isSelected());

        m_config.setThreeColorGradient(m_gradientColors.getColorsAsHex());
        m_config.setDiscreteGradientColors(m_gradientColors.getAllColorsHex());
        m_config.setContinuousGradient(!m_useBinsCheckBox.isSelected());
        m_config.setNumDiscreteColors((int) m_numberOfBinsSpinner.getValue());
        m_config.setMissingValueColor(CSSUtils.cssHexStringFromColor(m_missingValueColorColorChooser.getColor()));
        m_config.setUpperOutOfRangeColor(CSSUtils.cssHexStringFromColor(m_upperOutOfRangeColorColorChooser.getColor()));
        m_config.setLowerOutOfRangeColor(CSSUtils.cssHexStringFromColor(m_lowerOutOfRangeColorColorChooser.getColor()));

        m_config.setShowWarningInView(m_showWarningInViewCheckBox.isSelected());
        m_config.setGenerateImage(m_generateImageCheckBox.isSelected());
        m_config.setImageWidth((int) m_imageWidthSpinner.getValue());
        m_config.setImageHeight((int) m_imageHeightSpinner.getValue());
        m_config.setResizeToWindow(m_resizeToWindowCheckBox.isSelected());
        m_config.setDisplayFullscreenButton(m_displayFullscreenButtonCheckBox.isSelected());
        m_config.setChartTitle(m_chartTitleTextField.getText());
        m_config.setChartSubtitle(m_chartSubtitleTextField.getText());

        m_config.setEnableViewConfiguration(m_enableViewConfigurationCheckBox.isSelected());
        m_config.setEnableTitleChange(m_enableTitleChangeCheckBox.isSelected());
        m_config.setEnableColorModeEdit(m_enableColorModeEditCheckBox.isSelected());
        m_config.setEnableShowToolTips(m_enableShowToolTipsCheckBox.isSelected());
        m_config.setShowToolTips(m_showToolTipsCheckBox.isSelected());
        m_config.setSubscribeFilter(m_subscribeFilterCheckBox.isSelected());
        m_config.setEnableSelection(m_enableSelectionCheckBox.isSelected());
        m_config.setPublishSelection(m_publishSelectionCheckBox.isSelected());
        m_config.setSubscribeSelection(m_subscribeSelectionCheckBox.isSelected());
        m_config.setSelectionColumnName(m_selectionColumnNameTextField.getText());
        m_config.setShowResetSelectionButton(m_showResetSelectionButtonCheckBox.isSelected());
        m_config.setShowSelectedRowsOnly(m_showSelectedRowOnlyCheckBox.isSelected());
        m_config.setEnableShowSelectedRowsOnly(m_enableShowSelectedRowOnlyCheckBox.isSelected());
        m_config.setEnablePaging(m_enablePagingCheckBox.isSelected());
        m_config.setInitialPageSize((int) m_initialPageSizeSpinner.getValue());
        m_config.setEnablePageSizeChange(m_enablePageSizeChangeCheckBox.isSelected());
        m_config.setEnablePageSizeChange(m_enablePageSizeChangeCheckBox.isSelected());
        m_config.setAllowedPageSizes(getAllowedPageSizes());
        m_config.setEnableShowAll(m_pageSizeShowAllCheckBox.isSelected());
        m_config.setEnableZoom(m_enableZoomCheckBox.isSelected());
        m_config.setEnablePanning(m_enablePanningCheckBox.isSelected());
        m_config.setShowZoomResetButton(m_showZoomResetButtonCheckBox.isSelected());

        m_config.saveSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
            throws NotConfigurableException {
        final DataTableSpec spec = (DataTableSpec) specs[0];
        m_config.loadSettingsForDialog(settings, spec);

        m_columnSpecFilter = m_config.getColumns();
        m_spec = (DataTableSpec) specs[0];
        m_columnsFilterPanel.loadConfiguration(m_config.getColumns(), spec);
        m_labelColumnColumnSelectionPanel.update((DataTableSpec) specs[0], m_config.getLabelColumn(), true);
        loadSvgColumn(spec, m_config);
        m_customMinValueCheckBox.setSelected(m_config.getUseCustomMin());
        m_customMaxValueCheckBox.setSelected(m_config.getUseCustomMax());
        m_minValueSpinner.setValue(m_config.getMinValue());
        m_maxValueSpinner.setValue(m_config.getMaxValue());

        final String[] colors = m_config.getThreeColorGradient();
        m_gradientColors.setColors(colors[0], colors[1], colors[2]);
        m_useBinsCheckBox.setSelected(!m_config.getContinuousGradient());
        m_numberOfBinsSpinner.setValue(m_config.getNumDiscreteColors());
        m_firstColorColorChooser.setColor(CSSUtils.colorFromCssHexString(colors[0]));
        m_secondColorColorChooser.setColor(CSSUtils.colorFromCssHexString(colors[1]));
        m_thirdColorColorChooser.setColor(CSSUtils.colorFromCssHexString(colors[2]));
        m_missingValueColorColorChooser.setColor(CSSUtils.colorFromCssHexString(m_config.getMissingValueColor()));
        m_upperOutOfRangeColorColorChooser.setColor(CSSUtils.colorFromCssHexString(m_config.getUpperOutOfRangeColor()));
        m_lowerOutOfRangeColorColorChooser.setColor(CSSUtils.colorFromCssHexString(m_config.getLowerOutOfRangeColor()));

        m_showWarningInViewCheckBox.setSelected(m_config.getShowWarningInView());
        m_generateImageCheckBox.setSelected(m_config.getGenerateImage());
        m_imageWidthSpinner.setValue(m_config.getImageWidth());
        m_imageHeightSpinner.setValue(m_config.getImageHeight());
        m_resizeToWindowCheckBox.setSelected(m_config.getResizeToWindow());
        m_displayFullscreenButtonCheckBox.setSelected(m_config.getDisplayFullscreenButton());
        m_chartTitleTextField.setText(m_config.getChartTitle());
        m_chartSubtitleTextField.setText(m_config.getChartSubtitle());

        m_enableViewConfigurationCheckBox.setSelected(m_config.getEnableViewConfiguration());
        m_enableTitleChangeCheckBox.setSelected(m_config.getEnableTitleChange());
        m_enableColorModeEditCheckBox.setSelected(m_config.getEnableColorModeEdit());
        m_enableShowToolTipsCheckBox.setSelected(m_config.getEnableShowToolTips());
        m_showToolTipsCheckBox.setSelected(m_config.getShowToolTips());
        m_subscribeFilterCheckBox.setSelected(m_config.getSubscribeFilter());
        m_enableSelectionCheckBox.setSelected(m_config.getEnableSelection());
        m_publishSelectionCheckBox.setSelected(m_config.getPublishSelection());
        m_subscribeSelectionCheckBox.setSelected(m_config.getSubscribeSelection());
        m_selectionColumnNameTextField.setText(m_config.getSelectionColumnName());
        m_showResetSelectionButtonCheckBox.setSelected(m_config.getShowResetSelectionButton());
        m_showSelectedRowOnlyCheckBox.setSelected(m_config.getShowSelectedRowsOnly());
        m_enableShowSelectedRowOnlyCheckBox.setSelected(m_config.getEnableShowSelectedRowsOnly());
        m_enablePagingCheckBox.setSelected(m_config.getEnablePaging());
        m_initialPageSizeSpinner.setValue(m_config.getInitialPageSize());
        m_enablePageSizeChangeCheckBox.setSelected(m_config.getEnablePageSizeChange());
        m_allowedPageSizesTextField.setText(getAllowedPageSizesString(m_config.getAllowedPageSizes()));
        m_pageSizeShowAllCheckBox.setSelected(m_config.getEnableShowAll());
        m_enableZoomCheckBox.setSelected(m_config.getEnableZoom());
        m_enablePanningCheckBox.setSelected(m_config.getEnablePanning());
        m_showZoomResetButtonCheckBox.setSelected(m_config.getShowZoomResetButton());

        setNumberOfFilters((DataTableSpec) specs[0]);
        enableSpinner();
        enableViewEdit();
        enablePaging();
        enableSelection();
        enableCustomMin();
        enableCustomMax();
        updateMinMax();
    }

    // -- Helper methods --

    private JPanel createOptionsTab() {
        final JPanel panel = new JPanel(new GridBagLayout());
        final GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.weightx = 1;
        m_labelColumnColumnSelectionPanel.setPreferredSize(new Dimension(200, 50));
        panel.add(m_labelColumnColumnSelectionPanel, c);
        c.gridx = 2;
        m_svgLabelColumnColumnSelectionPanel.setPreferredSize(new Dimension(200, 50));
        panel.add(m_svgLabelColumnColumnSelectionPanel, c);

        c.gridx = 0;
        c.gridy++;
        c.weightx = 0;
        c.gridwidth = 3;

        panel.add(m_columnsFilterPanel, c);

        c.gridx = 0;
        c.gridy++;

        JPanel panel2 = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel minPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(0, 0, 0, 0);
        gbc2.anchor = GridBagConstraints.NORTHWEST;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.weightx = 0;
        gbc2.weighty = 0;
        gbc2.gridx = 0;
        gbc2.gridy = 0;
        minPanel.add(m_customMinValueCheckBox, gbc2);
        gbc2.weightx = 1;
        gbc2.gridx++;
        gbc2.insets = new Insets(0, 5, 0, 0);
        minPanel.add(m_minValueSpinner, gbc2);
        DialogUtil.addPairToPanel("Minimum value:", minPanel, panel2, gbc);

        JPanel maxPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.insets = new Insets(0, 0, 0, 0);
        gbc3.anchor = GridBagConstraints.NORTHWEST;
        gbc3.fill = GridBagConstraints.BOTH;
        gbc3.weightx = 0;
        gbc3.weighty = 0;
        gbc3.gridx = 0;
        gbc3.gridy = 0;
        maxPanel.add(m_customMaxValueCheckBox, gbc3);
        gbc3.weightx = 1;
        gbc3.gridx++;
        gbc3.insets = new Insets(0, 5, 0, 0);
        maxPanel.add(m_maxValueSpinner, gbc3);
        DialogUtil.addPairToPanel("Maximum value:", maxPanel, panel2, gbc);

        panel.add(panel2, c);

        c.gridx = 0;
        c.gridy++;

        return panel;
    }

    private JPanel createViewConfigTab() {
        final JPanel panel = new JPanel(new GridBagLayout());
        final GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        final JPanel generalPanel = new JPanel(new GridBagLayout());
        generalPanel.setBorder(BorderFactory.createTitledBorder("General"));
        panel.add(generalPanel, c);
        final GridBagConstraints generalPanelConstraints = DialogUtil.defaultGridBagConstraints();
        generalPanelConstraints.weightx = 1;
        generalPanel.add(new JLabel("Width (px):"), generalPanelConstraints);
        generalPanelConstraints.gridx++;
        generalPanel.add(m_imageWidthSpinner, generalPanelConstraints);
        generalPanelConstraints.gridx = 0;
        generalPanelConstraints.gridy++;
        generalPanel.add(new JLabel("Height (px):"), generalPanelConstraints);
        generalPanelConstraints.gridx++;
        generalPanel.add(m_imageHeightSpinner, generalPanelConstraints);
        generalPanelConstraints.gridx = 0;
        generalPanelConstraints.gridy++;
        generalPanel.add(m_generateImageCheckBox, generalPanelConstraints);
        generalPanelConstraints.gridx++;
        generalPanel.add(m_showWarningInViewCheckBox, generalPanelConstraints);
        generalPanelConstraints.gridx = 0;
        generalPanelConstraints.gridy++;
        generalPanel.add(m_resizeToWindowCheckBox, generalPanelConstraints);
        generalPanelConstraints.gridx++;
        generalPanel.add(m_displayFullscreenButtonCheckBox, generalPanelConstraints);
        generalPanelConstraints.gridx = 0;
        generalPanelConstraints.gridy++;

        c.gridx = 0;
        c.gridy++;

        final JPanel displayPanel = new JPanel(new GridBagLayout());
        displayPanel.setBorder(BorderFactory.createTitledBorder("Display"));
        panel.add(displayPanel, c);
        final GridBagConstraints displayPanelConstraints = DialogUtil.defaultGridBagConstraints();
        displayPanelConstraints.weightx = 1;
        displayPanel.add(new JLabel("Chart title:"), displayPanelConstraints);
        displayPanelConstraints.gridx++;
        displayPanel.add(m_chartTitleTextField, displayPanelConstraints);
        displayPanelConstraints.gridx = 0;
        displayPanelConstraints.gridy++;
        displayPanel.add(new JLabel("Chart subtitle:"), displayPanelConstraints);
        displayPanelConstraints.gridx++;
        displayPanel.add(m_chartSubtitleTextField, displayPanelConstraints);
        displayPanelConstraints.gridx = 0;
        displayPanelConstraints.gridy++;
        displayPanel.add(m_showToolTipsCheckBox, displayPanelConstraints);
        displayPanelConstraints.gridx = 0;
        displayPanelConstraints.gridy++;

        c.gridx = 0;
        c.gridy++;

        final JPanel gradientPanel = new JPanel(new GridBagLayout());
        gradientPanel.setBorder(BorderFactory.createTitledBorder("Gradient colors"));
        panel.add(gradientPanel, c);
        final GridBagConstraints gradientConstraints = DialogUtil.defaultGridBagConstraints();
        gradientConstraints.weightx = 1;
        gradientPanel.add(m_useBinsCheckBox, gradientConstraints);
        gradientConstraints.gridx++;
        gradientPanel.add(new JLabel("Number of colors (odd): "), gradientConstraints);
        gradientConstraints.gridx++;
        gradientPanel.add(m_numberOfBinsSpinner, gradientConstraints);
        gradientConstraints.gridx = 0;
        gradientConstraints.gridy++;
        gradientPanel.add(new JLabel("Select gradient colors: "), gradientConstraints);
        gradientConstraints.gridx = 0;
        gradientConstraints.gridy++;
        gradientConstraints.anchor = GridBagConstraints.WEST;
        gradientPanel.add(m_firstColorColorChooser.getComponentPanel(), gradientConstraints);
        gradientConstraints.gridx++;
        gradientConstraints.anchor = GridBagConstraints.CENTER;
        gradientPanel.add(m_secondColorColorChooser.getComponentPanel(), gradientConstraints);
        gradientConstraints.gridx++;
        gradientConstraints.anchor = GridBagConstraints.EAST;
        gradientPanel.add(m_thirdColorColorChooser.getComponentPanel(), gradientConstraints);
        gradientConstraints.gridx = 0;
        gradientConstraints.gridy++;
        gradientConstraints.gridwidth = 3;
        gradientConstraints.weightx = 0;
        gradientPanel.add(m_gradientColors, gradientConstraints);
        gradientConstraints.gridx = 0;
        gradientConstraints.gridy++;

        c.gridx = 0;
        c.gridy++;

        final JPanel missingValuePanel = new JPanel(new GridBagLayout());
        missingValuePanel.setBorder(BorderFactory.createTitledBorder("Missing value color"));
        panel.add(missingValuePanel, c);
        final GridBagConstraints missingValueConstraints = DialogUtil.defaultGridBagConstraints();
        missingValueConstraints.weightx = 1;
        missingValuePanel.add(m_missingValueColorColorChooser.getComponentPanel(), missingValueConstraints);
        missingValueConstraints.gridx = 0;
        missingValueConstraints.gridy++;

        c.gridx = 0;
        c.gridy++;

        final JPanel outOfRangeColorPanel = new JPanel(new GridBagLayout());
        outOfRangeColorPanel.setBorder(BorderFactory.createTitledBorder("Out of range value colors"));
        panel.add(outOfRangeColorPanel, c);
        final GridBagConstraints outOfRangeColorConstraints = DialogUtil.defaultGridBagConstraints();
        outOfRangeColorConstraints.weightx = 1;
        outOfRangeColorPanel.add(m_upperOutOfRangeColorColorChooser.getComponentPanel(), outOfRangeColorConstraints);
        outOfRangeColorConstraints.gridx = 0;
        outOfRangeColorConstraints.gridy++;
        outOfRangeColorPanel.add(m_lowerOutOfRangeColorColorChooser.getComponentPanel(), outOfRangeColorConstraints);
        outOfRangeColorConstraints.gridx = 0;
        outOfRangeColorConstraints.gridy++;

        c.gridx = 0;
        c.gridy++;

        return panel;
    }

    private JPanel createInteractivityTab() {
        final JPanel panel = new JPanel(new GridBagLayout());
        final GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        final JPanel viewEditPanel = new JPanel(new GridBagLayout());
        viewEditPanel.setBorder(BorderFactory.createTitledBorder("View Edit Controls"));
        panel.add(viewEditPanel, c);
        final GridBagConstraints viewEditPanelConstraints = DialogUtil.defaultGridBagConstraints();
        viewEditPanelConstraints.weightx = 1;
        viewEditPanel.add(m_enableViewConfigurationCheckBox, viewEditPanelConstraints);
        viewEditPanelConstraints.gridx++;
        viewEditPanel.add(m_enableTitleChangeCheckBox, viewEditPanelConstraints);
        viewEditPanelConstraints.gridx = 0;
        viewEditPanelConstraints.gridy++;
        viewEditPanel.add(m_enableColorModeEditCheckBox, viewEditPanelConstraints);
        viewEditPanelConstraints.gridx++;
        viewEditPanel.add(m_enableShowToolTipsCheckBox, viewEditPanelConstraints);
        viewEditPanelConstraints.gridx = 0;
        viewEditPanelConstraints.gridy++;

        c.gridx = 0;
        c.gridy++;

        final JPanel selectionPanel = new JPanel(new GridBagLayout());
        selectionPanel.setBorder(BorderFactory.createTitledBorder("Filtering & Selection"));
        panel.add(selectionPanel, c);
        final GridBagConstraints selectionPanelConstraints = DialogUtil.defaultGridBagConstraints();
        selectionPanelConstraints.weightx = 1;
        selectionPanel.add(m_enableSelectionCheckBox, selectionPanelConstraints);
        selectionPanelConstraints.gridx++;
        selectionPanel.add(m_showResetSelectionButtonCheckBox, selectionPanelConstraints);
        selectionPanelConstraints.gridx = 0;
        selectionPanelConstraints.gridy++;
        selectionPanel.add(new JLabel("Selection column name:"), selectionPanelConstraints);
        selectionPanelConstraints.gridx++;
        selectionPanel.add(m_selectionColumnNameTextField, selectionPanelConstraints);
        selectionPanelConstraints.gridx = 0;
        selectionPanelConstraints.gridy++;
        selectionPanel.add(m_publishSelectionCheckBox, selectionPanelConstraints);
        selectionPanelConstraints.gridx++;
        selectionPanel.add(m_subscribeSelectionCheckBox, selectionPanelConstraints);
        selectionPanelConstraints.gridx = 0;
        selectionPanelConstraints.gridy++;
        selectionPanel.add(m_enableShowSelectedRowOnlyCheckBox, selectionPanelConstraints);
        selectionPanelConstraints.gridx++;
        selectionPanel.add(m_showSelectedRowOnlyCheckBox, selectionPanelConstraints);
        selectionPanelConstraints.gridx = 0;
        selectionPanelConstraints.gridy++;
        selectionPanel.add(m_subscribeFilterCheckBox, selectionPanelConstraints);

        c.gridx = 0;
        c.gridy++;

        final JPanel paginationPanel = new JPanel(new GridBagLayout());
        paginationPanel.setBorder(BorderFactory.createTitledBorder("Pagination"));
        panel.add(paginationPanel, c);
        final GridBagConstraints paginationPanelConstraints = DialogUtil.defaultGridBagConstraints();
        paginationPanelConstraints.weightx = 1;
        paginationPanel.add(m_enablePagingCheckBox, paginationPanelConstraints);
        paginationPanelConstraints.gridx = 0;
        paginationPanelConstraints.gridy++;
        paginationPanel.add(new JLabel("Initial page size:"), paginationPanelConstraints);
        paginationPanelConstraints.gridx++;
        paginationPanel.add(m_initialPageSizeSpinner, paginationPanelConstraints);
        paginationPanelConstraints.gridx = 0;
        paginationPanelConstraints.gridy++;
        paginationPanel.add(m_enablePageSizeChangeCheckBox, paginationPanelConstraints);
        paginationPanelConstraints.gridx = 0;
        paginationPanelConstraints.gridy++;
        paginationPanel.add(new JLabel("Selectable page sizes:"), paginationPanelConstraints);
        paginationPanelConstraints.gridx++;
        paginationPanel.add(m_allowedPageSizesTextField, paginationPanelConstraints);
        paginationPanelConstraints.gridx = 0;
        paginationPanelConstraints.gridy++;
        paginationPanel.add(m_pageSizeShowAllCheckBox, paginationPanelConstraints);
        paginationPanelConstraints.gridx = 0;
        paginationPanelConstraints.gridy++;

        c.gridx = 0;
        c.gridy++;

        final JPanel zoomPanel = new JPanel(new GridBagLayout());
        zoomPanel.setBorder(BorderFactory.createTitledBorder("Zooming & Panning"));
        panel.add(zoomPanel, c);
        final GridBagConstraints zoomPanelConstraints = DialogUtil.defaultGridBagConstraints();
        zoomPanelConstraints.weightx = 1;
        zoomPanel.add(m_enableZoomCheckBox, zoomPanelConstraints);
        zoomPanelConstraints.gridx++;
        zoomPanel.add(m_showZoomResetButtonCheckBox, zoomPanelConstraints);
        zoomPanelConstraints.gridx = 0;
        zoomPanelConstraints.gridy++;
        zoomPanel.add(m_enablePanningCheckBox, zoomPanelConstraints);
        zoomPanelConstraints.gridx = 0;
        zoomPanelConstraints.gridy++;

        c.gridx = 0;
        c.gridy++;

        return panel;
    }

    private void loadSvgColumn(final DataTableSpec spec, final HeatMapViewConfig config)
        throws NotConfigurableException {
        boolean hasSVGCol = false;
        final String svgColumn = config.getSvgLabelColumn();
        for (int i = 0; i < spec.getNumColumns(); i++) {
            if (spec.getColumnSpec(i).getType().isCompatible(SvgValue.class)) {
                hasSVGCol = true;
                break;
            }
        }
        // If there are no SVG Columns then just disable the option
        if (!hasSVGCol) {
            m_svgLabelColumnColumnSelectionPanel.setEnabled(false);
        } else {
            m_svgLabelColumnColumnSelectionPanel.setEnabled(true);
        }
        m_svgLabelColumnColumnSelectionPanel.update(spec, svgColumn, false);
    }

    private void enableSpinner() {
        final boolean enabled = m_useBinsCheckBox.isSelected();
        m_numberOfBinsSpinner.setEnabled(enabled);
        m_gradientColors.useBins(enabled);
        final int numBins = (int) m_numberOfBinsSpinner.getValue();
        if (enabled && numBins % 2 != 0) {
            m_gradientColors.setNumberOfBins(numBins);
        }
    }

    private void enableViewEdit() {
        final boolean enabled = m_enableViewConfigurationCheckBox.isSelected();
        m_enableTitleChangeCheckBox.setEnabled(enabled);
        m_enableColorModeEditCheckBox.setEnabled(enabled);
        m_enableShowToolTipsCheckBox.setEnabled(enabled);
    }

    private void enablePaging() {
        final boolean enabled = m_enablePagingCheckBox.isSelected();
        final boolean enableSize = m_enablePageSizeChangeCheckBox.isSelected();
        m_initialPageSizeSpinner.setEnabled(enabled);
        m_enablePageSizeChangeCheckBox.setEnabled(enabled);
        m_allowedPageSizesTextField.setEnabled(enabled && enableSize);
        m_pageSizeShowAllCheckBox.setEnabled(enabled && enableSize);
    }

    private void enableSelection() {
        final boolean enabled = m_enableSelectionCheckBox.isSelected();
        m_publishSelectionCheckBox.setEnabled(enabled);
        m_subscribeSelectionCheckBox.setEnabled(enabled);
        m_selectionColumnNameTextField.setEnabled(enabled);
        m_showResetSelectionButtonCheckBox.setEnabled(enabled);
        m_showSelectedRowOnlyCheckBox.setEnabled(enabled);
        m_enableShowSelectedRowOnlyCheckBox.setEnabled(enabled);
    }

    private void updateNumberOfBins() {
        final int numBins = (int)m_numberOfBinsSpinner.getValue();
        if (numBins % 2 != 0) {
            m_gradientColors.setNumberOfBins(numBins);
        } else if (numBins < 3) {
            m_gradientColors.setNumberOfBins(3);
            m_numberOfBinsSpinner.setValue(3);
        } else if (numBins > 21) {
            m_gradientColors.setNumberOfBins(21);
            m_numberOfBinsSpinner.setValue(21);
        } else {
            m_gradientColors.setNumberOfBins(numBins + 1);
            m_numberOfBinsSpinner.setValue(numBins + 1);
        }
    }

    private void setNumberOfFilters(final DataTableSpec spec) {
        int numFilters = 0;
        for (int i = 0; i < spec.getNumColumns(); i++) {
            if (spec.getColumnSpec(i).getFilterHandler().isPresent()) {
                numFilters++;
            }
        }
        final StringBuilder builder = new StringBuilder("Subscribe to filter events");
        builder.append(" (");
        builder.append(numFilters == 0 ? "no" : numFilters);
        builder.append(numFilters == 1 ? " filter" : " filters");
        builder.append(" available)");
        m_subscribeFilterCheckBox.setText(builder.toString());
    }

    private static String getAllowedPageSizesString(final int[] sizes) {
        if (sizes.length < 1) {
            return "";
        }
        final StringBuilder builder = new StringBuilder(String.valueOf(sizes[0]));
        for (int i = 1; i < sizes.length; i++) {
            builder.append(", ");
            builder.append(sizes[i]);
        }
        return builder.toString();
    }

    private int[] getAllowedPageSizes() throws InvalidSettingsException {
        final String[] sizesArray = m_allowedPageSizesTextField.getText().split(",");
        final int[] allowedPageSizes = new int[sizesArray.length];
        try {
            for (int i = 0; i < sizesArray.length; i++) {
                allowedPageSizes[i] = Integer.parseInt(sizesArray[i].trim());
            }
        } catch (final NumberFormatException e) {
            throw new InvalidSettingsException(e.getMessage(), e);
        }
        return allowedPageSizes;
    }

    private double[] getMinMax() {
        m_columnsFilterPanel.saveConfiguration(m_columnSpecFilter);
        final String[] included = m_columnSpecFilter.applyTo(m_spec).getIncludes();
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < included.length; i++) {
            final DataCell lowerBound = m_spec.getColumnSpec(included[i]).getDomain().getLowerBound();
            final DataCell upperBound = m_spec.getColumnSpec(included[i]).getDomain().getUpperBound();
            if (lowerBound != null && ((DoubleValue)lowerBound).getDoubleValue() < min) {
                min = ((DoubleValue)lowerBound).getDoubleValue();
            }
            if (upperBound != null && ((DoubleValue)upperBound).getDoubleValue() > max) {
                max = ((DoubleValue)upperBound).getDoubleValue();
            }
        }
        if (min == Double.POSITIVE_INFINITY) {
            min = (double)m_minValueSpinner.getValue();
        }
        if (max == Double.NEGATIVE_INFINITY) {
            max = (double)m_maxValueSpinner.getValue();
        }
        return new double[]{min, max};
    }

    private void updateMinMax() {
        if (m_customMinValueCheckBox.isSelected() && m_customMaxValueCheckBox.isSelected()) {
            return;
        }
        final double[] minMax = getMinMax();
        if (!m_customMinValueCheckBox.isSelected()) {
            m_minValueSpinner.setValue(minMax[0]);
        }
        if (!m_customMaxValueCheckBox.isSelected()) {
            m_maxValueSpinner.setValue(minMax[1]);
        }
    }

    private void enableCustomMin() {
        m_minValueSpinner.setEnabled(m_customMinValueCheckBox.isSelected());
        updateMinMax();
    }

    private void enableCustomMax() {
        m_maxValueSpinner.setEnabled(m_customMaxValueCheckBox.isSelected());
        updateMinMax();
    }

    private void minGreaterThanMax() {
        final double minValue = (double) m_minValueSpinner.getValue();
        final double maxValue = (double) m_maxValueSpinner.getValue();
        if (minValue > maxValue) {
            m_minValueSpinner.setValue(maxValue);
        }
    }

    private void maxLessThanMin() {
        final double minValue = (double) m_minValueSpinner.getValue();
        final double maxValue = (double) m_maxValueSpinner.getValue();
        if (minValue > maxValue) {
            m_maxValueSpinner.setValue(minValue);
        }
    }
}
