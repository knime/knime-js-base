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
package org.knime.js.base.node.viz.plotter.roc;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.NominalValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DialogComponentColorChooser;
import org.knime.core.node.defaultnodesettings.SettingsModelColor;
import org.knime.core.node.util.ColumnSelectionComboxBox;
import org.knime.core.node.util.filter.column.DataColumnSpecFilterConfiguration;
import org.knime.core.node.util.filter.column.DataColumnSpecFilterPanel;

/**
 *
 * @author Christian Albrecht, KNIME AG, Zurich, Switzerland, University of Konstanz
 */
public class ROCCurveNodeDialogPane extends NodeDialogPane {

    private static final int TEXT_FIELD_SIZE = 20;

    private final ROCCurveViewConfig m_config;

    private final JCheckBox m_generateImageCheckBox;
    private final JCheckBox m_showArea;

    private final JCheckBox m_showGridCheckBox;
    private final JCheckBox m_resizeViewToWindow;
    private final JCheckBox m_displayFullscreenButtonCheckBox;

    private final JSpinner m_imageWidthSpinner;
    private final JSpinner m_lineWidthSpinner;
    private final JSpinner m_imageHeightSpinner;
    private final DialogComponentColorChooser m_gridColorChooser;
    private final DialogComponentColorChooser m_dataAreaColorChooser;
    private final DialogComponentColorChooser m_backgroundColorChooser;

    private final JCheckBox m_enableViewConfigCheckBox;
    private final JCheckBox m_enableXAxisLabelEditCheckBox;
    private final JCheckBox m_enableYAxisLabelEditCheckBox;
    private final JCheckBox m_enableTitleChangeCheckBox;
    private final JCheckBox m_enableSubtitleChangeCheckBox;

    private final JCheckBox m_showWarningInViewCheckBox;

    private final JCheckBox m_ignoreMissingValuesCheckBox;

    private DataTableSpec m_spec;

    @SuppressWarnings("unchecked")
    private final ColumnSelectionComboxBox m_classColumn =
            new ColumnSelectionComboxBox((Border)null, NominalValue.class);

    private final JComboBox<DataCell> m_positiveClass =
            new JComboBox<>(new DefaultComboBoxModel<DataCell>());

    private final JSpinner m_maxPoints = new JSpinner(new SpinnerNumberModel(2000, -1, Integer.MAX_VALUE, 10));

    @SuppressWarnings("unchecked")
    private final DataColumnSpecFilterPanel m_sortColumns =
            new DataColumnSpecFilterPanel();

    private final JLabel m_warningLabel = new JLabel();

    private JTextField m_xAxisLabelField;

    private JTextField m_yAxisLabelField;

    private JCheckBox m_showLegendCheckBox;

    private JTextField m_chartTitleTextField;

    private JTextField m_chartSubtitleTextField;

    /**
     * Creates a new dialog pane.
     */
    public ROCCurveNodeDialogPane() {
        m_config = new ROCCurveViewConfig();

        m_generateImageCheckBox = new JCheckBox("Create image at outport");
        m_showArea = new JCheckBox("Show area under curve");
        m_showGridCheckBox = new JCheckBox("Show grid");
        m_resizeViewToWindow = new JCheckBox("Resize view to fill window");
        m_displayFullscreenButtonCheckBox = new JCheckBox("Display fullscreen button");

        m_imageWidthSpinner = new JSpinner(new SpinnerNumberModel(100, 100, Integer.MAX_VALUE, 1));
        m_imageHeightSpinner = new JSpinner(new SpinnerNumberModel(100, 100, Integer.MAX_VALUE, 1));
        m_lineWidthSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));

        m_enableViewConfigCheckBox = new JCheckBox("Enable view edit controls");
        m_enableTitleChangeCheckBox = new JCheckBox("Enable title edit controls");
        m_enableSubtitleChangeCheckBox = new JCheckBox("Enable subtitle edit controls");
        m_enableXAxisLabelEditCheckBox = new JCheckBox("Enable label edit for x-axis");
        m_enableYAxisLabelEditCheckBox = new JCheckBox("Enable label edit for y-axis");

        m_chartTitleTextField = new JTextField(TEXT_FIELD_SIZE);
        m_chartSubtitleTextField = new JTextField(TEXT_FIELD_SIZE);
        m_xAxisLabelField = new JTextField(TEXT_FIELD_SIZE);
        m_yAxisLabelField = new JTextField(TEXT_FIELD_SIZE);
        m_showLegendCheckBox = new JCheckBox("Show color legend");

        m_gridColorChooser = new DialogComponentColorChooser(
            new SettingsModelColor("gridColor", null), "Grid color: ", true);
        m_dataAreaColorChooser = new DialogComponentColorChooser(
            new SettingsModelColor("dataAreaColor", null), "Data area color: ", true);
        m_backgroundColorChooser = new DialogComponentColorChooser(
            new SettingsModelColor("backgroundColor", null), "Background color: ", true);

        m_showWarningInViewCheckBox = new JCheckBox("Show warnings in view");

        m_ignoreMissingValuesCheckBox = new JCheckBox("Ignore missing values");

        m_enableViewConfigCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
               enableViewControls();
            }
        });

        m_showGridCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                m_gridColorChooser.getModel().setEnabled(m_showGridCheckBox.isSelected());

            }
        });

        final JPanel p = new JPanel(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(2, 2, 2, 2);
        c.anchor = GridBagConstraints.NORTHWEST;

        p.add(new JLabel("Class column   "), c);
        c.gridx++;
        p.add(m_classColumn, c);
        m_classColumn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                changeClassColumn(p);
            }
        });

        c.gridx = 0;
        c.gridy++;
        p.add(new JLabel("Positive class value   "), c);
        c.gridx++;
//        m_positiveClass.setMinimumSize(new Dimension(100, m_positiveClass
//                .getHeight()));
        p.add(m_positiveClass, c);

        c.gridx++;
        c.anchor = GridBagConstraints.WEST;
        p.add(m_warningLabel, c);
        c.anchor = GridBagConstraints.NORTHWEST;


        c.gridx = 0;
        c.gridy++;
        p.add(new JLabel("Limit data points for each curve to   "), c);
        c.gridx++;
        p.add(m_maxPoints, c);

        c.gridy++;
        c.gridx = 0;
        c.gridwidth = 3;
        p.add(
                new JLabel(
                        "Columns containing the positive class probabilities"),
                c);

        c.gridy++;
        c.gridx = 0;
        c.gridwidth = 3;
        p.add(m_sortColumns, c);

        c.gridx = 0;
        c.gridy++;
        p.add(m_ignoreMissingValuesCheckBox, c);

        addTab("ROC Curve Settings", p);
        addTab("General Plot Options", initGeneralPanel());
        addTab("Axis Configuration", initAxisPanel());
        addTab("View Controls", initControlsPanel());
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
        viewControlsPanel.add(m_enableXAxisLabelEditCheckBox, cc);
        cc.gridx += 2;
        viewControlsPanel.add(m_enableYAxisLabelEditCheckBox, cc);
        return panel;
    }

    private void enableViewControls() {
        boolean enable = m_enableViewConfigCheckBox.isSelected();
        m_enableTitleChangeCheckBox.setEnabled(enable);
        m_enableSubtitleChangeCheckBox.setEnabled(enable);
        m_enableXAxisLabelEditCheckBox.setEnabled(enable);
        m_enableYAxisLabelEditCheckBox.setEnabled(enable);
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

        JPanel legendPanel = new JPanel(new GridBagLayout());
        legendPanel.setBorder(BorderFactory.createTitledBorder("Legends"));
        panel.add(legendPanel, c);
        cc.gridx = 0;
        cc.gridy = 0;
        legendPanel.add(m_showLegendCheckBox, cc);
        c.gridx = 0;
        c.gridy++;

        return panel;
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
        panel.add(m_generateImageCheckBox, c);
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

        GridBagConstraints cc = new GridBagConstraints();
        cc.insets = new Insets(5, 5, 5, 5);
        cc.anchor = GridBagConstraints.NORTHWEST;
        cc.gridx = 0;
        cc.gridy = 0;

        JPanel genPanel = new JPanel(new GridBagLayout());
        genPanel.setBorder(BorderFactory.createTitledBorder("General"));
        panel.add(genPanel, c);
        genPanel.add(m_generateImageCheckBox, cc);
        cc.gridwidth = 1;
        cc.gridy++;
        cc.gridx = 0;
        genPanel.add(new JLabel("Chart title:"), cc);
        cc.gridx = 1;
        genPanel.add(m_chartTitleTextField, cc);
        cc.gridx = 0;
        cc.gridy++;
        genPanel.add(new JLabel("Chart subtitle:"), cc);
        cc.gridx = 1;
        genPanel.add(m_chartSubtitleTextField, cc);

        c.gridy++;

        cc.gridx = 0;
        cc.gridy = 0;

        JPanel sizesPanel = new JPanel(new GridBagLayout());
        sizesPanel.setBorder(BorderFactory.createTitledBorder("Display"));
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
        sizesPanel.add(new JLabel("Line width (in px): "), cc);
        cc.gridx++;
        m_lineWidthSpinner.setPreferredSize(new Dimension(100, TEXT_FIELD_SIZE));
        sizesPanel.add(m_lineWidthSpinner, cc);
        cc.gridx = 0;
        cc.gridy++;
        sizesPanel.add(m_showArea, cc);
        cc.gridy++;
        cc.anchor = GridBagConstraints.CENTER;
        sizesPanel.add(m_resizeViewToWindow, cc);
        c.gridy++;
        cc.gridx++;
        sizesPanel.add(m_displayFullscreenButtonCheckBox, cc);

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

        return panel;
    }


    /**
     * Called if the user changed the class column.
     *
     * @param parent the panel which is the parent for message boxes
     */
    private void changeClassColumn(final JComponent parent) {
        String selCol = m_classColumn.getSelectedColumn();
        ((DefaultComboBoxModel<DataCell>)m_positiveClass.getModel()).removeAllElements();
        if ((selCol != null) && (m_spec != null)) {
            DataColumnSpec cs = m_spec.getColumnSpec(selCol);
            Set<DataCell> values = cs.getDomain().getValues();
            if (values == null) {
                m_warningLabel.setForeground(Color.RED);
                m_warningLabel.setText(" Column '" + selCol
                        + "' contains no possible values");
                return;
            }

            if (values.size() > 2) {
                m_warningLabel.setText(" Column '" + selCol
                        + "' contains more than two possible values");
            } else {
                m_warningLabel.setText("");
            }
            for (DataCell cell : values) {
                m_positiveClass.addItem(cell);
            }
            parent.revalidate();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final DataTableSpec[] specs)
            throws NotConfigurableException {
        m_config.loadSettingsForDialog(settings, specs[0]);
        m_generateImageCheckBox.setSelected(m_config.getGenerateImage());

        m_showArea.setSelected(m_config.getShowArea());
        m_showGridCheckBox.setSelected(m_config.getShowGrid());
        m_resizeViewToWindow.setSelected(m_config.getResizeToWindow());
        m_displayFullscreenButtonCheckBox.setSelected(m_config.getDisplayFullscreenButton());

        m_lineWidthSpinner.setValue(m_config.getLineWidth());
        m_imageWidthSpinner.setValue(m_config.getImageWidth());
        m_imageHeightSpinner.setValue(m_config.getImageHeight());
        m_backgroundColorChooser.setColor(m_config.getBackgroundColor());
        m_dataAreaColorChooser.setColor(m_config.getDataAreaColor());
        m_gridColorChooser.setColor(m_config.getGridColor());
        m_gridColorChooser.getModel().setEnabled(m_showGridCheckBox.isSelected());

        m_showLegendCheckBox.setSelected(m_config.getShowLegend());
        m_xAxisLabelField.setText(m_config.getxAxisTitle());
        m_yAxisLabelField.setText(m_config.getyAxisTitle());
        m_chartTitleTextField.setText(m_config.getTitle());
        m_chartSubtitleTextField.setText(m_config.getSubtitle());

        m_spec = specs[0];
        m_classColumn.update(specs[0], m_config.getRocSettings().getClassColumn());
        m_positiveClass.setSelectedItem(m_config.getRocSettings().getPositiveClass());

        m_enableViewConfigCheckBox.setSelected(m_config.getEnableControls());
        m_enableTitleChangeCheckBox.setSelected(m_config.getEnableEditTitle());
        m_enableSubtitleChangeCheckBox.setSelected(m_config.getEnableEditSubtitle());
        m_enableXAxisLabelEditCheckBox.setSelected(m_config.getEnableEditXAxisLabel());
        m_enableYAxisLabelEditCheckBox.setSelected(m_config.getEnableEditYAxisLabel());
        m_maxPoints.setValue(m_config.getRocSettings().getMaxPoints());

        m_showWarningInViewCheckBox.setSelected(m_config.getShowWarningInView());

        m_ignoreMissingValuesCheckBox.setSelected(m_config.getIgnoreMissingValues());

        DataColumnSpecFilterConfiguration cfg = m_config.getRocSettings().getNumericCols();
        cfg.loadConfigurationInDialog(settings, specs[0]);
        m_sortColumns.loadConfiguration(cfg, specs[0]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        m_config.setGenerateImage(m_generateImageCheckBox.isSelected());

        m_config.setShowArea(m_showArea.isSelected());
        m_config.setShowGrid(m_showGridCheckBox.isSelected());
        m_config.setResizeToWindow(m_resizeViewToWindow.isSelected());
        m_config.setDisplayFullscreenButton(m_displayFullscreenButtonCheckBox.isSelected());

        m_config.setLineWidth((Integer)m_lineWidthSpinner.getValue());
        m_config.setImageWidth((Integer)m_imageWidthSpinner.getValue());
        m_config.setImageHeight((Integer)m_imageHeightSpinner.getValue());
        m_config.setBackgroundColor(m_backgroundColorChooser.getColor());
        m_config.setDataAreaColor(m_dataAreaColorChooser.getColor());
        m_config.setGridColor(m_gridColorChooser.getColor());

        m_config.setTitle(m_chartTitleTextField.getText());
        m_config.setSubtitle(m_chartSubtitleTextField.getText());
        m_config.setxAxisTitle(m_xAxisLabelField.getText());
        m_config.setyAxisTitle(m_yAxisLabelField.getText());
        m_config.setShowLegend(m_showLegendCheckBox.isSelected());

        m_config.setEnableControls(m_enableViewConfigCheckBox.isSelected());
        m_config.setEnableEditTitle(m_enableTitleChangeCheckBox.isSelected());
        m_config.setEnableEditSubtitle(m_enableSubtitleChangeCheckBox.isSelected());
        m_config.setEnableEditXAxisLabel(m_enableXAxisLabelEditCheckBox.isSelected());
        m_config.setEnableEditYAxisLabel(m_enableYAxisLabelEditCheckBox.isSelected());

        m_config.getRocSettings().setClassColumn(m_classColumn.getSelectedColumn());
        m_config.getRocSettings()
                .setPositiveClass((DataCell)m_positiveClass.getSelectedItem());
        m_config.getRocSettings().setMaxPoints((Integer) m_maxPoints.getValue());

        m_config.setShowWarningInView(m_showWarningInViewCheckBox.isSelected());

        m_config.setIgnoreMissingValues(m_ignoreMissingValuesCheckBox.isSelected());

        m_sortColumns.saveConfiguration(m_config.getRocSettings().getNumericCols());

        m_config.saveSettings(settings);
    }
}
