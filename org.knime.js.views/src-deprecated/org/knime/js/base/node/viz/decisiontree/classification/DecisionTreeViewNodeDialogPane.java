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
package org.knime.js.base.node.viz.decisiontree.classification;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
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
 * @deprecated
 */
@Deprecated
public class DecisionTreeViewNodeDialogPane extends NodeDialogPane {

    private static final int TEXT_FIELD_SIZE = 20;

    private final DecisionTreeViewConfig m_config;

    private final JCheckBox m_generateImageCheckBox;

    private final JCheckBox m_displayFullscreenButtonCheckBox;
    private final JCheckBox m_enableViewConfigCheckBox;

    private final JCheckBox m_enableTitleChangeCheckBox;

    private final JCheckBox m_enableSubtitleChangeCheckBox;
    //    private final JCheckBox m_allowMouseWheelZoomingCheckBox;
    //    private final JCheckBox m_allowPanningCheckBox;
    //    private final JCheckBox m_showZoomResetCheckBox;
    private final JCheckBox m_enableSelectionCheckBox;
    private final JCheckBox m_publishSelectionCheckBox;
    private final JCheckBox m_subscribeSelectionCheckBox;
    private final JCheckBox m_displaySelectionResetButtonCheckBox;

    private final JSpinner m_maxRowsSpinner;
    private final JTextField m_appendedColumnName;
    private final JTextField m_chartTitleTextField;


    private final JTextField m_chartSubtitleTextField;

    private final DialogComponentColorChooser m_dataAreaColorChooser;

    private final DialogComponentColorChooser m_backgroundColorChooser;

    private final DialogComponentColorChooser m_nodeBackgroundColorChooser;

    private final NumberFormatNodeDialogUI m_numberFormatUI;
    private final JSpinner m_truncationLimitSpinner;

    private final JSpinner m_expandedLevelSpinner;
    private final JButton m_resetNodeStatusButton;
    private final JLabel m_nodeStatusFromViewAlert;
    private int[] m_nodeStatus;

    private final JCheckBox m_enableZoomingCheckBox;
    private final JCheckBox m_showZoomResetButton;
    private final JSpinner m_zoomLevelSpinner;

    /**
     * Creates a new dialog pane.
     */
    public DecisionTreeViewNodeDialogPane() {
        m_config = new DecisionTreeViewConfig();

        m_generateImageCheckBox = new JCheckBox("Create image at outport");
        m_displayFullscreenButtonCheckBox = new JCheckBox("Display fullscreen button");
        m_enableViewConfigCheckBox = new JCheckBox("Enable view edit controls");
        m_enableTitleChangeCheckBox = new JCheckBox("Enable title edit controls");
        m_enableSubtitleChangeCheckBox = new JCheckBox("Enable subtitle edit controls");
        //        m_allowMouseWheelZoomingCheckBox = new JCheckBox("Enable mouse wheel zooming");
        //        m_allowPanningCheckBox = new JCheckBox("Enable panning");
        //        m_showZoomResetCheckBox = new JCheckBox("Show zoom reset button");
        m_enableSelectionCheckBox = new JCheckBox("Enable selection");
        m_publishSelectionCheckBox = new JCheckBox("Publish selection events");
        m_subscribeSelectionCheckBox = new JCheckBox("Subscribe to selection events");
        m_displaySelectionResetButtonCheckBox = new JCheckBox("Display selection reset button");

        m_maxRowsSpinner = new JSpinner();
        m_appendedColumnName = new JTextField(TEXT_FIELD_SIZE);
        m_chartTitleTextField = new JTextField(TEXT_FIELD_SIZE);
        m_chartSubtitleTextField = new JTextField(TEXT_FIELD_SIZE);

        m_expandedLevelSpinner = new JSpinner(
            new SpinnerNumberModel(DecisionTreeViewConfig.DEFAULT_EXPANDED_LEVEL, 0, Integer.MAX_VALUE, 1));
        m_resetNodeStatusButton = new JButton("Reset node status");
        m_nodeStatusFromViewAlert = new JLabel("Nodes were collapsed/expanded in the view.");
        m_nodeStatusFromViewAlert.setForeground(Color.RED);
        m_resetNodeStatusButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                m_expandedLevelSpinner.setEnabled(true);
                m_resetNodeStatusButton.setEnabled(false);
                m_nodeStatusFromViewAlert.setVisible(false);
            }

        });

        m_numberFormatUI = new NumberFormatNodeDialogUI();

        m_enableZoomingCheckBox = new JCheckBox("Enable zooming");
        m_showZoomResetButton = new JCheckBox("Show zoom reset button");
        m_zoomLevelSpinner = new JSpinner(new SpinnerNumberModel(DecisionTreeViewConfig.DEFAULT_SCALE, 1e-6, 1000, 0.1));

        m_dataAreaColorChooser =
            new DialogComponentColorChooser(new SettingsModelColor("dataAreaColor", null), "Tree area color: ", true);
        m_backgroundColorChooser = new DialogComponentColorChooser(new SettingsModelColor("backgroundColor", null),
            "Background color: ", true);
        m_nodeBackgroundColorChooser = new DialogComponentColorChooser(new SettingsModelColor("nodeBackgroundColor", null),
            "Node background color: ", true);

        m_truncationLimitSpinner = new JSpinner(
            new SpinnerNumberModel(DecisionTreeViewConfig.DEFAULT_TRUNCATION_LIMIT, 3, Integer.MAX_VALUE, 1));

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

        addTab("Decision Tree Plot Options", initOptionsPanel());
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
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        panel.add(m_generateImageCheckBox, c);
        c.gridx = 0;
        c.gridy++;

        JPanel nodeStatusPanel = new JPanel(new GridBagLayout());
        nodeStatusPanel.setBorder(BorderFactory.createTitledBorder("Node status"));
        panel.add(nodeStatusPanel, c);
        GridBagConstraints cc = new GridBagConstraints();
        cc.gridx = 0;
        cc.gridy = 0;
        cc.insets = new Insets(5, 5, 5, 5);
        cc.anchor = GridBagConstraints.NORTHWEST;
        nodeStatusPanel.add(new JLabel("Expanded levels"), cc);
        cc.gridx++;
        nodeStatusPanel.add(m_expandedLevelSpinner, cc);
        cc.gridx = 0;
        cc.gridy++;
        cc.gridwidth = 2;
        nodeStatusPanel.add(m_resetNodeStatusButton, cc);
        cc.gridy++;
        nodeStatusPanel.add(m_nodeStatusFromViewAlert, cc);

        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy++;
        JPanel selectionPanel = new JPanel(new GridBagLayout());
        selectionPanel.setBorder(BorderFactory.createTitledBorder("Selection"));
        panel.add(selectionPanel, c);
        cc.gridx = 0;
        cc.gridy = 0;
        cc.gridwidth = 1;
        selectionPanel.add(new JLabel("Maximum number of rows: "), cc);
        cc.gridx += 1;
        m_maxRowsSpinner.setPreferredSize(new Dimension(100, TEXT_FIELD_SIZE));
        selectionPanel.add(m_maxRowsSpinner, cc);
        cc.gridx = 0;
        cc.gridy++;
        selectionPanel.add(new JLabel("Selection column name: "), cc);
        cc.gridx++;
        selectionPanel.add(m_appendedColumnName, cc);

        return panel;
    }

    private Component initGeneralPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
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
        sizesPanel.add(m_displayFullscreenButtonCheckBox, cc);
        cc.gridy++;
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
        backgroundPanel.add(m_nodeBackgroundColorChooser.getComponentPanel(), cc);

        c.gridx = 0;
        c.gridy++;
        JPanel numFormatPanel = new JPanel(new GridBagLayout());
        numFormatPanel.setBorder(BorderFactory.createTitledBorder("Number format"));
        panel.add(numFormatPanel, c);
        cc.gridy = 0;
        numFormatPanel.add(m_numberFormatUI.createPanel(), cc);

        c.gridy++;
        c.gridwidth = 1;
        panel.add(new JLabel("Truncation limit: "), c);
        c.gridx++;
        panel.add(m_truncationLimitSpinner, c);

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
        cc.gridx++;
        selectionControlPanel.add(m_displaySelectionResetButtonCheckBox, cc);
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
        cc.gridx++;
        zoomControlPanel.add(m_showZoomResetButton, cc);
        cc.gridx = 0;
        cc.gridy++;
        zoomControlPanel.add(new JLabel("Zoom level:"), cc);
        cc.gridx++;
        zoomControlPanel.add(m_zoomLevelSpinner, cc);
        //        zoomControlPanel.add(m_allowMouseWheelZoomingCheckBox, cc);
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
        m_displaySelectionResetButtonCheckBox.setEnabled(enable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
        throws NotConfigurableException {
        m_config.loadInDialog(settings);
        m_generateImageCheckBox.setSelected(m_config.isGenerateImage());

        m_displayFullscreenButtonCheckBox.setSelected(m_config.getDisplayFullScreenButton());


        m_enableViewConfigCheckBox.setSelected(m_config.isEnableViewConfiguration());
        m_enableTitleChangeCheckBox.setSelected(m_config.isEnableTitleChange());
        m_enableSubtitleChangeCheckBox.setSelected(m_config.isEnableSubtitleChange());
        //        m_allowPanningCheckBox.setSelected(m_config.getEnablePanning());
        //        m_showZoomResetCheckBox.setSelected(m_config.getShowZoomResetButton());
        m_appendedColumnName.setText(m_config.getSelectionColumnName());
        m_enableSelectionCheckBox.setSelected(m_config.getEnableSelection());
        if (specs[1] != null) {
            m_enableSelectionCheckBox.setEnabled(true);
            m_appendedColumnName.setEnabled(true);
        } else {
            m_enableSelectionCheckBox.setSelected(false);
            m_enableSelectionCheckBox.setEnabled(false);
            m_appendedColumnName.setEnabled(false);
        }
        m_publishSelectionCheckBox.setSelected(m_config.getPublishSelection());
        m_subscribeSelectionCheckBox.setSelected(m_config.getSubscribeSelection());
        m_displaySelectionResetButtonCheckBox.setSelected(m_config.getDisplaySelectionResetButton());

        m_chartTitleTextField.setText(m_config.getTitle());
        m_chartSubtitleTextField.setText(m_config.getSubtitle());

        m_maxRowsSpinner.setValue(m_config.getMaxRows());

        m_backgroundColorChooser.setColor(m_config.getBackgroundColor());
        m_dataAreaColorChooser.setColor(m_config.getDataAreaColor());
        m_nodeBackgroundColorChooser.setColor(m_config.getNodeBackgroundColor());

        m_numberFormatUI.loadSettingsFrom(m_config.getNumberFormat());

        m_expandedLevelSpinner.setValue(m_config.getExpandedLevel());
        m_expandedLevelSpinner.setEnabled(!m_config.isNodeStatusFromView());
        m_resetNodeStatusButton.setEnabled(m_config.isNodeStatusFromView());
        m_nodeStatusFromViewAlert.setVisible(m_config.isNodeStatusFromView());
        m_nodeStatus = m_config.getNodeStatus();

        m_enableZoomingCheckBox.setSelected(m_config.getEnableZooming());
        m_showZoomResetButton.setSelected(m_config.getShowZoomResetButton());
        m_zoomLevelSpinner.setValue(m_config.getScale());

        m_truncationLimitSpinner.setValue(m_config.getTruncationLimit());

        enableViewControls();
        enableSelectionControls();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        m_config.setGenerateImage(m_generateImageCheckBox.isSelected());

        m_config.setDisplayFullScreenButton(m_displayFullscreenButtonCheckBox.isSelected());

        m_config.setSelectionColumnName(m_appendedColumnName.getText());
        m_config.setEnableViewConfiguration(m_enableViewConfigCheckBox.isSelected());
        m_config.setEnableTitleChange(m_enableTitleChangeCheckBox.isSelected());
        m_config.setEnableSubtitleChange(m_enableSubtitleChangeCheckBox.isSelected());
        //        m_config.setShowZoomResetButton(m_showZoomResetCheckBox.isSelected());
        m_config.setEnableSelection(m_enableSelectionCheckBox.isSelected());
        m_config.setPublishSelection(m_publishSelectionCheckBox.isSelected());
        m_config.setSubscribeSelection(m_subscribeSelectionCheckBox.isSelected());
        m_config.setDisplaySelectionResetButton(m_displaySelectionResetButtonCheckBox.isSelected());
        m_config.setExpandedLevel((int)m_expandedLevelSpinner.getValue());
        boolean resetNodeStatus = !(m_resetNodeStatusButton.isEnabled());
        if (resetNodeStatus) {
            m_nodeStatus = null;
            m_config.setNodeStatusFromView(false);
        } else {
            m_config.setNodeStatusFromView(m_resetNodeStatusButton.isEnabled());
        }
        m_config.setNodeStatus(m_nodeStatus);

        m_config.setTitle(m_chartTitleTextField.getText());
        m_config.setSubtitle(m_chartSubtitleTextField.getText());
        m_config.setMaxRows((Integer)m_maxRowsSpinner.getValue());

        m_config.setBackgroundColor(m_backgroundColorChooser.getColor());
        m_config.setDataAreaColor(m_dataAreaColorChooser.getColor());
        m_config.setNodeBackgroundColor(m_nodeBackgroundColorChooser.getColor());
        m_config.setNumberFormat(m_numberFormatUI.saveSettingsTo());

        m_config.setEnableZooming(m_enableZoomingCheckBox.isSelected());
        m_config.setShowZoomResetButton(m_showZoomResetButton.isSelected());
        m_config.setScale((double)m_zoomLevelSpinner.getValue());

        m_config.setTruncationLimit((int)m_truncationLimitSpinner.getValue());

        m_config.saveSettings(settings);
    }

}
