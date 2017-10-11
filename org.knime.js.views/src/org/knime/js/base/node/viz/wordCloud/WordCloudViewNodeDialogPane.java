/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME GmbH, Konstanz, Germany
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
 * ---------------------------------------------------------------------
 *
 * History
 *   2 Oct 2017 (albrecht): created
 */
package org.knime.js.base.node.viz.wordCloud;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
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
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.ColumnSelectionPanel;
import org.knime.core.node.util.DataValueColumnFilter;
import org.knime.js.core.settings.DialogUtil;

/**
 * Dialog pane for the word cloud view
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class WordCloudViewNodeDialogPane extends NodeDialogPane {

    private final JCheckBox m_generateImageCheckBox;
    private final JSpinner m_maxWordsSpinner;
    private final JCheckBox m_displayFullscreenButtonCheckBox;
    private final JTextField m_titleTextField;
    private final JTextField m_subtitleTextField;
    private final ColumnSelectionPanel m_wordColumnSelection;
    private final ColumnSelectionPanel m_sizeColumnSelection;
    private final JCheckBox m_useSizePropertyCheckBox;
    private final JTextField m_fontTextField;
    private final JSpinner m_minFontSizeSpinner;
    private final JSpinner m_maxFontSizeSpinner;
    private final JComboBox<WordCloudFontScaleType> m_fontScaleTypeComboBox;
    private final JComboBox<WordCloudSpiralType> m_spiralTypeComboBox;
    private final JSpinner m_numOrientationsSpinner;
    private final JSpinner m_startAngleSpinner;
    private final JSpinner m_endAngleSpinner;

    private final JCheckBox m_enableViewConfigCheckBox;
    private final JCheckBox m_enableTitleChangeCheckBox;
    private final JCheckBox m_enableSubtitleChangeCheckBox;
    private final JCheckBox m_enableFontSizeChangeCheckBox;
    private final JCheckBox m_enableScaleTypeChangeCheckBox;
    private final JCheckBox m_enableSpiralTypeChangeCheckBox;
    private final JCheckBox m_enableNumOrientationsChangeCheckBox;
    private final JCheckBox m_enableAnglesChangeCheckBox;

    private String m_previousSizeColumn;

    WordCloudViewNodeDialogPane() {
        m_generateImageCheckBox = new JCheckBox("Create image at outport");
        m_maxWordsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, null, 1));
        m_displayFullscreenButtonCheckBox = new JCheckBox("Display fullscreen button");
        m_titleTextField = new JTextField(DialogUtil.DEF_TEXTFIELD_WIDTH);
        m_subtitleTextField = new JTextField(DialogUtil.DEF_TEXTFIELD_WIDTH);
        @SuppressWarnings("unchecked")
        DataValueColumnFilter wordColFilter = new DataValueColumnFilter(StringValue.class);
        Border wordColBorder = BorderFactory.createTitledBorder("Choose word column");
        m_wordColumnSelection = new ColumnSelectionPanel(wordColBorder, wordColFilter, false, true);
        @SuppressWarnings("unchecked")
        DataValueColumnFilter sizeColFilter = new DataValueColumnFilter(DoubleValue.class);
        Border sizeColBorder = BorderFactory.createTitledBorder("Choose size column");
        m_sizeColumnSelection = new ColumnSelectionPanel(sizeColBorder, sizeColFilter, true, false);
        m_sizeColumnSelection.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                String col = m_sizeColumnSelection.getSelectedColumn();
                if (col != null) {
                    m_previousSizeColumn = col;
                }
                m_useSizePropertyCheckBox.setSelected(col == null);
            }
        });
        m_useSizePropertyCheckBox = new JCheckBox("Use size property");
        m_useSizePropertyCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                boolean c = m_useSizePropertyCheckBox.isSelected();
                String colSelect = m_previousSizeColumn;
                if (m_previousSizeColumn == null && m_sizeColumnSelection.getNrItemsInList() > 1) {
                    colSelect = m_sizeColumnSelection.getAvailableColumns().get(0).getName();
                }
                m_sizeColumnSelection.setSelectedColumn(c ? null : colSelect);
            }
        });
        m_fontTextField = new JTextField(DialogUtil.DEF_TEXTFIELD_WIDTH);
        m_minFontSizeSpinner = new JSpinner(new SpinnerNumberModel(1f, 1f, null, 0.5f));
        m_maxFontSizeSpinner = new JSpinner(new SpinnerNumberModel(1f, 1f, null, 0.5f));
        m_fontScaleTypeComboBox = new JComboBox<WordCloudFontScaleType>();
        for (WordCloudFontScaleType type : WordCloudFontScaleType.values()) {
            m_fontScaleTypeComboBox.addItem(type);
        }
        m_spiralTypeComboBox = new JComboBox<WordCloudSpiralType>();
        for (WordCloudSpiralType type : WordCloudSpiralType.values()) {
            m_spiralTypeComboBox.addItem(type);
        }
        m_numOrientationsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, null, 1));
        m_startAngleSpinner = new JSpinner(new SpinnerNumberModel(-90, -90, 90, 1));
        m_endAngleSpinner = new JSpinner(new SpinnerNumberModel(-90, -90, 90, 1));

        m_enableViewConfigCheckBox = new JCheckBox("Enable view edit controls");
        m_enableViewConfigCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                enableViewConfigFields();
            }
        });
        m_enableTitleChangeCheckBox = new JCheckBox("Enable title edit controls");
        m_enableSubtitleChangeCheckBox = new JCheckBox("Enable subtitle edit controls");
        m_enableFontSizeChangeCheckBox = new JCheckBox("Enable font size controls");
        m_enableScaleTypeChangeCheckBox = new JCheckBox("Enable font scale controls");
        m_enableSpiralTypeChangeCheckBox = new JCheckBox("Enable spiral type controls");
        m_enableNumOrientationsChangeCheckBox = new JCheckBox("Enable orientation count controls");
        m_enableAnglesChangeCheckBox = new JCheckBox("Enable angle controls");

        addTab("Options", initOptions());
        addTab("Display", initDisplay());
        addTab("View Controls", initControls());
    }

    private JPanel initOptions() {
        JPanel generalPanel = new JPanel(new GridBagLayout());
        generalPanel.setBorder(new TitledBorder("General Options"));
        GridBagConstraints gbcG = createConfiguredGridBagConstraints();
        gbcG.fill = GridBagConstraints.HORIZONTAL;
        gbcG.gridwidth = 1;
        generalPanel.add(new JLabel("No. of rows to display: "), gbcG);
        gbcG.gridx++;
        m_maxWordsSpinner.setPreferredSize(new Dimension(100, DialogUtil.DEF_TEXTFIELD_WIDTH));
        generalPanel.add(m_maxWordsSpinner, gbcG);
        gbcG.gridwidth = 2;
        gbcG.gridx = 0;
        gbcG.gridy++;
        generalPanel.add(m_generateImageCheckBox, gbcG);

        JPanel titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setBorder(new TitledBorder("Titles"));
        GridBagConstraints gbcT = createConfiguredGridBagConstraints();
        titlePanel.add(new JLabel("Title: "), gbcT);
        gbcT.gridx++;
        titlePanel.add(m_titleTextField, gbcT);
        gbcT.gridx = 0;
        gbcT.gridy++;
        titlePanel.add(new JLabel("Subtitle: "), gbcT);
        gbcT.gridx++;
        titlePanel.add(m_subtitleTextField, gbcT);

        JPanel columnsPanel = new JPanel(new GridBagLayout());
        columnsPanel.setBorder(new TitledBorder("Columns"));
        GridBagConstraints gbcC = createConfiguredGridBagConstraints();
        m_wordColumnSelection.setPreferredSize(new Dimension(300, 50));
        columnsPanel.add(m_wordColumnSelection, gbcC);
        gbcC.gridy++;
        m_sizeColumnSelection.setPreferredSize(new Dimension(300, 50));
        columnsPanel.add(m_sizeColumnSelection, gbcC);
        gbcC.gridy++;
        columnsPanel.add(m_useSizePropertyCheckBox, gbcC);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createConfiguredGridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(generalPanel, gbc);
        gbc.gridy++;
        panel.add(titlePanel, gbc);
        gbc.gridy++;
        panel.add(columnsPanel, gbc);
        return panel;
    }

    private JPanel initDisplay() {
        JPanel fontPanel = new JPanel(new GridBagLayout());
        fontPanel.setBorder(new TitledBorder("Font"));
        GridBagConstraints gbcF = createConfiguredGridBagConstraints();
        fontPanel.add(new JLabel("Font family: "), gbcF);
        gbcF.gridx++;
        fontPanel.add(m_fontTextField, gbcF);
        gbcF.gridx = 0;
        gbcF.gridy++;
        fontPanel.add(new JLabel("Font scale: "), gbcF);
        gbcF.gridx++;
        fontPanel.add(m_fontScaleTypeComboBox, gbcF);
        gbcF.gridx = 0;
        gbcF.gridy++;
        fontPanel.add(new JLabel("Minimum font size: "), gbcF);
        gbcF.gridx++;
        m_minFontSizeSpinner.setPreferredSize(new Dimension(100, DialogUtil.DEF_TEXTFIELD_WIDTH));
        fontPanel.add(m_minFontSizeSpinner, gbcF);
        gbcF.gridx = 0;
        gbcF.gridy++;
        fontPanel.add(new JLabel("Maximum font size: "), gbcF);
        gbcF.gridx++;
        m_maxFontSizeSpinner.setPreferredSize(new Dimension(100, DialogUtil.DEF_TEXTFIELD_WIDTH));
        fontPanel.add(m_maxFontSizeSpinner, gbcF);

        JPanel orientPanel = new JPanel(new GridBagLayout());
        orientPanel.setBorder(new TitledBorder("Orientation"));
        GridBagConstraints gbcO = createConfiguredGridBagConstraints();
        orientPanel.add(new JLabel("Spiral type: "), gbcO);
        gbcO.gridx++;
        orientPanel.add(m_spiralTypeComboBox, gbcO);
        gbcO.gridx = 0;
        gbcO.gridy++;
        orientPanel.add(new JLabel("Number of orientations: "), gbcO);
        gbcO.gridx++;
        m_numOrientationsSpinner.setPreferredSize(new Dimension(100, DialogUtil.DEF_TEXTFIELD_WIDTH));
        orientPanel.add(m_numOrientationsSpinner, gbcO);
        gbcO.gridx = 0;
        gbcO.gridy++;
        orientPanel.add(new JLabel("Start angle: "), gbcO);
        gbcO.gridx++;
        m_startAngleSpinner.setPreferredSize(new Dimension(100, DialogUtil.DEF_TEXTFIELD_WIDTH));
        orientPanel.add(m_startAngleSpinner, gbcO);
        gbcO.gridx = 0;
        gbcO.gridy++;
        orientPanel.add(new JLabel("End angle: "), gbcO);
        gbcO.gridx++;
        m_endAngleSpinner.setPreferredSize(new Dimension(100, DialogUtil.DEF_TEXTFIELD_WIDTH));
        orientPanel.add(m_endAngleSpinner, gbcO);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createConfiguredGridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(fontPanel, gbc);
        gbc.gridy++;
        panel.add(orientPanel, gbc);
        return panel;
    }

    private JPanel initControls() {
        JPanel viewControlsPanel = new JPanel(new GridBagLayout());
        viewControlsPanel.setBorder(BorderFactory.createTitledBorder("View edit controls"));
        GridBagConstraints gbcV = createConfiguredGridBagConstraints();
        viewControlsPanel.add(m_enableViewConfigCheckBox, gbcV);
        gbcV.gridy++;
        viewControlsPanel.add(m_enableTitleChangeCheckBox, gbcV);
        gbcV.gridx++;
        viewControlsPanel.add(m_enableSubtitleChangeCheckBox, gbcV);
        gbcV.gridx = 0;
        gbcV.gridy++;
        viewControlsPanel.add(m_enableFontSizeChangeCheckBox, gbcV);
        gbcV.gridx++;
        viewControlsPanel.add(m_enableScaleTypeChangeCheckBox, gbcV);
        gbcV.gridx = 0;
        gbcV.gridy++;
        viewControlsPanel.add(m_enableNumOrientationsChangeCheckBox, gbcV);
        gbcV.gridx++;
        viewControlsPanel.add(m_enableAnglesChangeCheckBox, gbcV);
        gbcV.gridx = 0;
        gbcV.gridy++;
        viewControlsPanel.add(m_enableSpiralTypeChangeCheckBox, gbcV);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createConfiguredGridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(viewControlsPanel, gbc);
        gbc.gridy++;
        panel.add(m_displayFullscreenButtonCheckBox, gbc);
        return panel;
    }

    private GridBagConstraints createConfiguredGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        return gbc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        if ((int)m_startAngleSpinner.getValue() > (int)m_endAngleSpinner.getValue()) {
            throw new InvalidSettingsException("Start angle can not be larger than end angle.");
        }

        WordCloudViewConfig config = new WordCloudViewConfig();
        config.setGenerateImage(m_generateImageCheckBox.isSelected());
        config.setMaxWords((int)m_maxWordsSpinner.getValue());
        config.setDisplayFullscreenButton(m_displayFullscreenButtonCheckBox.isSelected());
        config.setTitle(m_titleTextField.getText());
        config.setSubtitle(m_subtitleTextField.getText());
        config.setWordColumn(m_wordColumnSelection.getSelectedColumn());
        config.setSizeColumn(m_sizeColumnSelection.getSelectedColumn());
        config.setUseSizeProp(m_useSizePropertyCheckBox.isSelected());
        config.setFont(m_fontTextField.getText());
        config.setMinFontSize((float)m_minFontSizeSpinner.getValue());
        config.setMaxFontSize((float)m_maxFontSizeSpinner.getValue());
        config.setFontScaleType((WordCloudFontScaleType)m_fontScaleTypeComboBox.getSelectedItem());
        config.setSpiralType((WordCloudSpiralType)m_spiralTypeComboBox.getSelectedItem());
        config.setNumOrientations((int)m_numOrientationsSpinner.getValue());
        config.setStartAngle((int)m_startAngleSpinner.getValue());
        config.setEndAngle((int)m_endAngleSpinner.getValue());
        config.setEnableViewConfig(m_enableViewConfigCheckBox.isSelected());
        config.setEnableTitleChange(m_enableTitleChangeCheckBox.isSelected());
        config.setEnableSubtitleChange(m_enableSubtitleChangeCheckBox.isSelected());
        config.setEnableFontSizeChange(m_enableFontSizeChangeCheckBox.isSelected());
        config.setEnableScaleTypeChange(m_enableScaleTypeChangeCheckBox.isSelected());
        config.setEnableSpiralTypeChange(m_enableSpiralTypeChangeCheckBox.isSelected());
        config.setEnableNumOrientationsChange(m_enableNumOrientationsChangeCheckBox.isSelected());
        config.setEnableAnglesChange(m_enableAnglesChangeCheckBox.isSelected());
        config.saveSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs) throws NotConfigurableException {
        WordCloudViewConfig config = new WordCloudViewConfig();
        config.loadSettingsForDialog(settings, (DataTableSpec)specs[0]);
        m_generateImageCheckBox.setSelected(config.getGenerateImage());
        m_maxWordsSpinner.setValue(config.getMaxWords());
        m_displayFullscreenButtonCheckBox.setSelected(config.getDisplayFullscreenButton());
        m_titleTextField.setText(config.getTitle());
        m_subtitleTextField.setText(config.getSubtitle());
        m_wordColumnSelection.update((DataTableSpec)specs[0], config.getWordColumn(), config.getWordColumn() == null);
        m_sizeColumnSelection.update((DataTableSpec)specs[0], config.getSizeColumn());
        m_fontTextField.setText(config.getFont());
        m_minFontSizeSpinner.setValue(config.getMinFontSize());
        m_maxFontSizeSpinner.setValue(config.getMaxFontSize());
        m_fontScaleTypeComboBox.setSelectedItem(config.getFontScaleType());
        m_spiralTypeComboBox.setSelectedItem(config.getSpiralType());
        m_numOrientationsSpinner.setValue(config.getNumOrientations());
        m_startAngleSpinner.setValue(config.getStartAngle());
        m_endAngleSpinner.setValue(config.getEndAngle());
        m_enableViewConfigCheckBox.setSelected(config.getEnableViewConfig());
        m_enableTitleChangeCheckBox.setSelected(config.getEnableTitleChange());
        m_enableSubtitleChangeCheckBox.setSelected(config.getEnableSubtitleChange());
        m_enableFontSizeChangeCheckBox.setSelected(config.getEnableFontSizeChange());
        m_enableScaleTypeChangeCheckBox.setSelected(config.getEnableScaleTypeChange());
        m_enableSpiralTypeChangeCheckBox.setSelected(config.getEnableSpiralTypeChange());
        m_enableNumOrientationsChangeCheckBox.setSelected(config.getEnableNumOrientationsChange());
        m_enableAnglesChangeCheckBox.setSelected(config.getEnableAnglesChange());

        enableViewConfigFields();
        boolean onlyNoneColAvailable = m_sizeColumnSelection.getAvailableColumns().size() < 2;
        m_sizeColumnSelection.setEnabled(!onlyNoneColAvailable);
        m_useSizePropertyCheckBox.setEnabled(!onlyNoneColAvailable);
        m_useSizePropertyCheckBox.setSelected(onlyNoneColAvailable || config.getUseSizeProp());
        if (config.getSizeColumn() != null) {
            m_previousSizeColumn = config.getSizeColumn();
        }
    }

    private void enableViewConfigFields() {
        boolean enable = m_enableViewConfigCheckBox.isSelected();
        m_enableTitleChangeCheckBox.setEnabled(enable);
        m_enableSubtitleChangeCheckBox.setEnabled(enable);
        m_enableFontSizeChangeCheckBox.setEnabled(enable);
        m_enableScaleTypeChangeCheckBox.setEnabled(enable);
        m_enableSpiralTypeChangeCheckBox.setEnabled(enable);
        m_enableNumOrientationsChangeCheckBox.setEnabled(enable);
        m_enableAnglesChangeCheckBox.setEnabled(enable);
    }

}
