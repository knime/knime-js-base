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
package org.knime.js.base.node.viz.decisiontree.classification;

import java.awt.Component;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DialogComponentColorChooser;
import org.knime.core.node.defaultnodesettings.SettingsModelColor;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.js.core.settings.numberFormat.NumberFormatNodeDialogUI;

/**
 *
 * @author Adrian Nembach, KNIME.com
 */
public class DecisionTreeViewNodeDialogPane extends NodeDialogPane {

    private static final int TEXT_FIELD_SIZE = 20;

    private final JCheckBox m_hideInWizardCheckBox;

    private final JCheckBox m_generateImageCheckBox;

    //    private final JCheckBox m_showLegendCheckBox;
    private final JCheckBox m_displayFullscreenButtonCheckBox;
    //    private final JCheckBox m_resizeViewToWindow;
    private final JCheckBox m_enableViewConfigCheckBox;

    private final JCheckBox m_enableTitleChangeCheckBox;

    private final JCheckBox m_enableSubtitleChangeCheckBox;
    //    private final JCheckBox m_allowMouseWheelZoomingCheckBox;
    //    private final JCheckBox m_allowDragZoomingCheckBox;
    //    private final JCheckBox m_allowPanningCheckBox;
    //    private final JCheckBox m_showZoomResetCheckBox;
    private final JCheckBox m_enableSelectionCheckBox;
    private final JCheckBox m_publishSelectionCheckBox;
    private final JCheckBox m_subscribeSelectionCheckBox;
    //    private final JCheckBox m_subscribeFilterCheckBox;

    private final JSpinner m_maxRowsSpinner;
    private final JTextField m_appendedColumnName;
    private final JTextField m_chartTitleTextField;

    private final JTextField m_chartSubtitleTextField;

    //    private final JSpinner m_imageWidthSpinner;
    //    private final JSpinner m_imageHeightSpinner;
    private final DialogComponentColorChooser m_dataAreaColorChooser;

    private final DialogComponentColorChooser m_backgroundColorChooser;

    private final NumberFormatNodeDialogUI m_numberFormatUI;

    private final JSpinner m_expandedLevelSpinner;
    private final JCheckBox m_resetNodeStatus;
    private int[] m_nodeStatus;

    private final JCheckBox m_enableZoomingCheckBox;

    /**
     * Creates a new dialog pane.
     */
    public DecisionTreeViewNodeDialogPane() {
        m_hideInWizardCheckBox = new JCheckBox("Hide in wizard");
        m_generateImageCheckBox = new JCheckBox("Create image at outport");
        //        m_showLegendCheckBox = new JCheckBox("Show color legend");
        m_displayFullscreenButtonCheckBox = new JCheckBox("Display fullscreen button");
        //        m_resizeViewToWindow = new JCheckBox("Resize view to fill window");
        m_enableViewConfigCheckBox = new JCheckBox("Enable view edit controls");
        m_enableTitleChangeCheckBox = new JCheckBox("Enable title edit controls");
        m_enableSubtitleChangeCheckBox = new JCheckBox("Enable subtitle edit controls");
        //        m_allowMouseWheelZoomingCheckBox = new JCheckBox("Enable mouse wheel zooming");
        //        m_allowDragZoomingCheckBox = new JCheckBox("Enable drag zooming");
        //        m_allowPanningCheckBox = new JCheckBox("Enable panning");
        //        m_showZoomResetCheckBox = new JCheckBox("Show zoom reset button");
        m_enableSelectionCheckBox = new JCheckBox("Enable selection");
        m_publishSelectionCheckBox = new JCheckBox("Publish selection events");
        m_subscribeSelectionCheckBox = new JCheckBox("Subscribe to selection events");
        //        m_subscribeFilterCheckBox = new JCheckBox("Subscribe to filter events");

        m_maxRowsSpinner = new JSpinner();
        m_appendedColumnName = new JTextField(TEXT_FIELD_SIZE);
        m_chartTitleTextField = new JTextField(TEXT_FIELD_SIZE);
        m_chartSubtitleTextField = new JTextField(TEXT_FIELD_SIZE);

        m_expandedLevelSpinner = new JSpinner(
            new SpinnerNumberModel(DecisionTreeViewConfig.DEFAULT_EXPANDED_LEVEL, 0, Integer.MAX_VALUE, 1));
        m_resetNodeStatus = new JCheckBox("Reset node status");
        m_resetNodeStatus.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                if (m_resetNodeStatus.isSelected()) {
                    m_expandedLevelSpinner.setEnabled(true);
                }
            }

        });

        m_numberFormatUI = new NumberFormatNodeDialogUI();

        m_enableZoomingCheckBox = new JCheckBox("Enable zooming");

        //        m_imageWidthSpinner = new JSpinner(new SpinnerNumberModel(100, 100, Integer.MAX_VALUE, 1));
        //        m_imageHeightSpinner = new JSpinner(new SpinnerNumberModel(100, 100, Integer.MAX_VALUE, 1));
        m_dataAreaColorChooser =
            new DialogComponentColorChooser(new SettingsModelColor("dataAreaColor", null), "Data area color: ", true);
        m_backgroundColorChooser = new DialogComponentColorChooser(new SettingsModelColor("backgroundColor", null),
            "Background color: ", true);

        m_enableViewConfigCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                enableViewControls();
            }
        });
        m_enableSelectionCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                enableSelectionControls();
            }
        });

        addTab("Options", initOptionsPanel());
        addTab("General Plot Options", initGeneralPanel());
        addTab("View Controls", initControlsPanel());
    }

    /**
     * @return
     */
    private Component initOptionsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 5, 10, 5);
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        panel.add(m_hideInWizardCheckBox, c);
        c.gridy++;
        panel.add(m_generateImageCheckBox, c);
        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 1;
        panel.add(new JLabel("Expanded Levels"), c);
        c.gridx++;
        panel.add(m_expandedLevelSpinner, c);
        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 2;
        panel.add(m_resetNodeStatus, c);
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy++;
        panel.add(new JLabel("Maximum number of rows: "), c);
        c.gridx += 1;
        m_maxRowsSpinner.setPreferredSize(new Dimension(100, TEXT_FIELD_SIZE));
        panel.add(m_maxRowsSpinner, c);
        c.gridx = 0;
        c.gridy++;
        panel.add(new JLabel("Selection column name: "), c);
        c.gridx++;
        panel.add(m_appendedColumnName, c);
        //        c.gridx = 0;
        //        c.gridy++;

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
        //        sizesPanel.add(new JLabel("Width of image (in px): "), cc);
        //        cc.gridx++;
        //        m_imageWidthSpinner.setPreferredSize(new Dimension(100, TEXT_FIELD_SIZE));
        //        sizesPanel.add(m_imageWidthSpinner, cc);
        //        cc.gridx = 0;
        //        cc.gridy++;
        //        sizesPanel.add(new JLabel("Height of image (in px): "), cc);
        //        cc.gridx++;
        //        m_imageHeightSpinner.setPreferredSize(new Dimension(100, TEXT_FIELD_SIZE));
        //        sizesPanel.add(m_imageHeightSpinner, cc);
        //        cc.gridx = 0;
        //        cc.gridy++;
        //        cc.anchor = GridBagConstraints.CENTER;
        //        sizesPanel.add(m_resizeViewToWindow, cc);
        //        cc.gridx++;
        sizesPanel.add(m_displayFullscreenButtonCheckBox, cc);
        //
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
        backgroundPanel.add(m_numberFormatUI.createPanel(), cc);

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

        c.gridx = 0;
        c.gridy++;
        JPanel selectionControlPanel = new JPanel(new GridBagLayout());
        selectionControlPanel.setBorder(BorderFactory.createTitledBorder("Selection"));
        panel.add(selectionControlPanel, c);
        cc.gridx = 0;
        cc.gridy = 0;
        selectionControlPanel.add(m_enableSelectionCheckBox, cc);
        cc.gridx = 0;
        cc.gridy++;
        selectionControlPanel.add(m_publishSelectionCheckBox, cc);
        cc.gridx++;
        selectionControlPanel.add(m_subscribeSelectionCheckBox, cc);
        //        cc.gridx++;
        //        selectionControlPanel.add(m_subscribeFilterCheckBox, cc);

        //        c.gridx = 0;
        //        c.gridy++;
        //        JPanel panControlPanel = new JPanel(new GridBagLayout());
        //        panControlPanel.setBorder(BorderFactory.createTitledBorder("Panning"));
        //        panel.add(panControlPanel, c);
        //        cc.gridx = 0;
        //        cc.gridy = 0;
        //        panControlPanel.add(m_allowPanningCheckBox, cc);
        //
        c.gridy++;
        JPanel zoomControlPanel = new JPanel(new GridBagLayout());
        zoomControlPanel.setBorder(BorderFactory.createTitledBorder("Zoom"));
        panel.add(zoomControlPanel, c);
        cc.gridx = 0;
        cc.gridy = 0;
        zoomControlPanel.add(m_enableZoomingCheckBox, cc);
        //        zoomControlPanel.add(m_allowMouseWheelZoomingCheckBox, cc);
        //        cc.gridx++;
        //        zoomControlPanel.add(m_allowDragZoomingCheckBox, cc);
        //        cc.gridx++;
        //        zoomControlPanel.add(m_showZoomResetCheckBox, cc);

        return panel;
    }

    private void enableViewControls() {
        boolean enable = m_enableViewConfigCheckBox.isSelected();
        m_enableTitleChangeCheckBox.setEnabled(enable);
        m_enableSubtitleChangeCheckBox.setEnabled(enable);
    }

    private void enableSelectionControls() {
        boolean enable = m_enableSelectionCheckBox.isSelected();
        //        m_allowRectangleSelectionCheckBox.setEnabled(enable);
        //        m_allowLassoSelectionCheckBox.setEnabled(enable);
        m_publishSelectionCheckBox.setEnabled(enable);
        m_subscribeSelectionCheckBox.setEnabled(enable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
        throws NotConfigurableException {
        DecisionTreeViewConfig config = new DecisionTreeViewConfig();
        config.loadInDialog(settings);
        m_hideInWizardCheckBox.setSelected(config.isHideInWizard());
        m_generateImageCheckBox.setSelected(config.isGenerateImage());

        //        m_showLegendCheckBox.setSelected(config.getShowLegend());
        m_displayFullscreenButtonCheckBox.setSelected(config.getDisplayFullScreenButton());
        //        m_autoRangeAxisCheckBox.setSelected(config.getAutoRangeAxes());
        //        m_useDomainInformationCheckBox.setSelected(config.getUseDomainInfo());
        //        m_showGridCheckBox.setSelected(config.getShowGrid());
        //        m_showCrosshairCheckBox.setSelected(config.getShowCrosshair());
        //        m_snapToPointsCheckBox.setSelected(config.getSnapToPoints());
        //        m_resizeViewToWindow.setSelected(config.getResizeToWindow());

        m_enableViewConfigCheckBox.setSelected(config.isEnableViewConfiguration());
        m_enableTitleChangeCheckBox.setSelected(config.isEnableTitleChange());
        m_enableSubtitleChangeCheckBox.setSelected(config.isEnableSubtitleChange());
        //        m_allowMouseWheelZoomingCheckBox.setSelected(config.getEnableZooming());
        //        m_allowDragZoomingCheckBox.setSelected(config.getEnableDragZooming());
        //        m_allowPanningCheckBox.setSelected(config.getEnablePanning());
        //        m_showZoomResetCheckBox.setSelected(config.getShowZoomResetButton());
        m_appendedColumnName.setText(config.getSelectionColumnName());
        m_enableSelectionCheckBox.setSelected(config.getEnableSelection());
        if (specs[1] != null) {
            m_enableSelectionCheckBox.setEnabled(true);
            m_appendedColumnName.setEnabled(true);
        } else {
            m_enableSelectionCheckBox.setEnabled(false);
            m_appendedColumnName.setEnabled(false);
        }
        m_publishSelectionCheckBox.setSelected(config.getPublishSelection());
        m_subscribeSelectionCheckBox.setSelected(config.getSubscribeSelection());
        //        m_subscribeFilterCheckBox.setSelected(config.getSubscribeFilter());

        m_chartTitleTextField.setText(config.getTitle());
        m_chartSubtitleTextField.setText(config.getSubtitle());

        m_maxRowsSpinner.setValue(config.getMaxRows());

        //        m_imageWidthSpinner.setValue(config.getImageWidth());
        //        m_imageHeightSpinner.setValue(config.getImageHeight());
        m_backgroundColorChooser.setColor(config.getBackgroundColor());
        m_dataAreaColorChooser.setColor(config.getDataAreaColor());

        m_numberFormatUI.loadSettingsFrom(config.getNumberFormat());

        m_expandedLevelSpinner.setValue(config.getExpandedLevel());
        m_expandedLevelSpinner.setEnabled(!config.isNodeStatusFromView());
        m_resetNodeStatus.setEnabled(config.isNodeStatusFromView());
        m_resetNodeStatus.setSelected(false);
        m_nodeStatus = config.getNodeStatus();

        m_enableZoomingCheckBox.setSelected(config.getEnableZooming());

        enableViewControls();
        enableSelectionControls();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        DecisionTreeViewConfig config = new DecisionTreeViewConfig();
        config.setHideInWizard(m_hideInWizardCheckBox.isSelected());
        config.setGenerateImage(m_generateImageCheckBox.isSelected());

        //        config.setShowLegend(m_showLegendCheckBox.isSelected());
        config.setDisplayFullScreenButton(m_displayFullscreenButtonCheckBox.isSelected());
        //        config.setResizeToWindow(m_resizeViewToWindow.isSelected());

        config.setSelectionColumnName(m_appendedColumnName.getText());
        config.setEnableViewConfiguration(m_enableViewConfigCheckBox.isSelected());
        config.setEnableTitleChange(m_enableTitleChangeCheckBox.isSelected());
        config.setEnableSubtitleChange(m_enableSubtitleChangeCheckBox.isSelected());
        //        config.setEnableZooming(m_allowMouseWheelZoomingCheckBox.isSelected());
        //        config.setEnableDragZooming(m_allowDragZoomingCheckBox.isSelected());
        //        config.setEnablePanning(m_allowPanningCheckBox.isSelected());
        //        config.setShowZoomResetButton(m_showZoomResetCheckBox.isSelected());
        config.setEnableSelection(m_enableSelectionCheckBox.isSelected());
        config.setPublishSelection(m_publishSelectionCheckBox.isSelected());
        config.setSubscribeSelection(m_subscribeSelectionCheckBox.isSelected());
        //        config.setSubscribeFilter(m_subscribeFilterCheckBox.isSelected());
        config.setExpandedLevel((int)m_expandedLevelSpinner.getValue());
        boolean resetNodeStatus = m_resetNodeStatus.isSelected();
        if (resetNodeStatus) {
            m_nodeStatus = null;
            config.setNodeStatusFromView(false);
        } else {
            config.setNodeStatusFromView(m_resetNodeStatus.isEnabled());
        }
        config.setNodeStatus(m_nodeStatus);

        config.setTitle(m_chartTitleTextField.getText());
        config.setSubtitle(m_chartSubtitleTextField.getText());
        config.setMaxRows((Integer)m_maxRowsSpinner.getValue());

        //        config.setImageWidth((Integer)m_imageWidthSpinner.getValue());
        //        config.setImageHeight((Integer)m_imageHeightSpinner.getValue());
        config.setBackgroundColor(m_backgroundColorChooser.getColor());
        config.setDataAreaColor(m_dataAreaColorChooser.getColor());
        config.setNumberFormat(m_numberFormatUI.saveSettingsTo());

        config.setEnableZooming(m_enableZoomingCheckBox.isSelected());

        config.saveSettings(settings);
    }

}
