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
package org.knime.js.base.node.viz.tagCloud;

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

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DoubleValue;
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
import org.knime.js.core.settings.DialogUtil;

/**
 * Dialog pane for the tag cloud view
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class TagCloudViewNodeDialogPane extends NodeDialogPane {
    private final JCheckBox m_generateImageCheckBox;
    private final JCheckBox m_showWarningsCheckBox;
    private final JCheckBox m_reportMissingValuesCheckBox;
    private final JSpinner m_maxWordsSpinner;
    private final JCheckBox m_displayFullscreenButtonCheckBox;
    private final JCheckBox m_displayRefreshButtonCheckBox;
    private final JCheckBox m_disableAnimationsCheckBox;
    private final JTextField m_titleTextField;
    private final JTextField m_subtitleTextField;
    private final ColumnSelectionPanel m_wordColumnSelection;
    private final JCheckBox m_aggregateWordsCheckBox;
    private final JCheckBox m_ignoreTermTagsCheckBox;
    private final ColumnSelectionPanel m_sizeColumnSelection;
    private final JCheckBox m_useSizePropertyCheckBox;
    private final JCheckBox m_resizeToWindowCheckBox;
    private final JSpinner m_imageWidthSpinner;
    private final JSpinner m_imageHeightSpinner;
    private final JCheckBox m_useColorPropertyCheckBox;
    private final JTextField m_fontTextField;
    private final JSpinner m_minFontSizeSpinner;
    private final JSpinner m_maxFontSizeSpinner;
    private final JComboBox<TagCloudFontScaleType> m_fontScaleTypeComboBox;
    private final JCheckBox m_fontBoldCheckBox;
    private final JComboBox<TagCloudSpiralType> m_spiralTypeComboBox;
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
    private final JCheckBox m_enableSelectionCheckBox;
    private final JTextField m_selectionColumnNameField;
    private final DialogComponentColorChooser m_selectionColorChooser;
    private final JCheckBox m_publishSelectionCheckBox;
    private final JCheckBox m_subscribeSelectionCheckBox;
    private final JCheckBox m_enableShowSelectedOnlyCheckBox;
    private final JCheckBox m_showSelectedOnlyCheckBox;
    private final JCheckBox m_displayClearSelectionButtonCheckBox;
    private final JCheckBox m_subscribeFilterCheckBox;

    private String m_previousSizeColumn;

    TagCloudViewNodeDialogPane() {
        m_generateImageCheckBox = new JCheckBox("Create image at outport");
        m_showWarningsCheckBox = new JCheckBox("Display warnings in view");
        m_reportMissingValuesCheckBox = new JCheckBox("Report on missing values");
        m_maxWordsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, null, 1));
        m_displayFullscreenButtonCheckBox = new JCheckBox("Display full screen button");
        m_displayRefreshButtonCheckBox = new JCheckBox("Display refresh button");
        m_disableAnimationsCheckBox = new JCheckBox("Disable animations");
        m_resizeToWindowCheckBox = new JCheckBox("Resize view to fill window");
        m_resizeToWindowCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                enableSizeFields();
            }
        });
        int minWidth = TagCloudViewRepresentation.MIN_WIDTH;
        int minHeight = TagCloudViewRepresentation.MIN_HEIGHT;
        m_imageWidthSpinner = new JSpinner(new SpinnerNumberModel(minWidth, minWidth, Integer.MAX_VALUE, 1));
        m_imageHeightSpinner = new JSpinner(new SpinnerNumberModel(minHeight, minHeight, Integer.MAX_VALUE, 1));
        m_titleTextField = new JTextField(DialogUtil.DEF_TEXTFIELD_WIDTH);
        m_subtitleTextField = new JTextField(DialogUtil.DEF_TEXTFIELD_WIDTH);
        DataValueColumnFilter wordColFilter = TagCloudTermCellResolver.getWordColumnFilter();
        Border wordColBorder = BorderFactory.createTitledBorder("Tag column");
        m_wordColumnSelection = new ColumnSelectionPanel(wordColBorder, wordColFilter, false, true);
        m_wordColumnSelection.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                enableIgnoreTagsField();
            }
        });
        m_aggregateWordsCheckBox = new JCheckBox("Aggregate tags");
        m_aggregateWordsCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                enableIgnoreTagsField();
            }
        });
        m_ignoreTermTagsCheckBox = new JCheckBox("Ignore term tags");
        @SuppressWarnings("unchecked")
        DataValueColumnFilter sizeColFilter = new DataValueColumnFilter(DoubleValue.class);
        Border sizeColBorder = BorderFactory.createTitledBorder("Size column");
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
        m_useSizePropertyCheckBox = new JCheckBox("Use row size property");
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
        m_useColorPropertyCheckBox = new JCheckBox("Use row color property");
        m_fontTextField = new JTextField(DialogUtil.DEF_TEXTFIELD_WIDTH);
        m_minFontSizeSpinner = new JSpinner(new SpinnerNumberModel(1f, 1f, null, 0.5f));
        m_maxFontSizeSpinner = new JSpinner(new SpinnerNumberModel(1f, 1f, null, 0.5f));
        m_fontScaleTypeComboBox = new JComboBox<TagCloudFontScaleType>();
        for (TagCloudFontScaleType type : TagCloudFontScaleType.values()) {
            m_fontScaleTypeComboBox.addItem(type);
        }
        m_fontBoldCheckBox = new JCheckBox("Use bold font face");
        m_spiralTypeComboBox = new JComboBox<TagCloudSpiralType>();
        for (TagCloudSpiralType type : TagCloudSpiralType.values()) {
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
                enableSelectionFields();
            }
        });
        m_enableTitleChangeCheckBox = new JCheckBox("Enable title edit controls");
        m_enableSubtitleChangeCheckBox = new JCheckBox("Enable subtitle edit controls");
        m_enableFontSizeChangeCheckBox = new JCheckBox("Enable font size controls");
        m_enableScaleTypeChangeCheckBox = new JCheckBox("Enable font scale controls");
        m_enableSpiralTypeChangeCheckBox = new JCheckBox("Enable spiral type controls");
        m_enableNumOrientationsChangeCheckBox = new JCheckBox("Enable orientation count controls");
        m_enableAnglesChangeCheckBox = new JCheckBox("Enable angle controls");
        m_enableSelectionCheckBox = new JCheckBox("Enable selection");
        m_enableSelectionCheckBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                enableSelectionFields();
            }
        });
        m_selectionColumnNameField = new JTextField(DialogUtil.DEF_TEXTFIELD_WIDTH);
        m_selectionColorChooser = new DialogComponentColorChooser(
            new SettingsModelColor("selectionColor", null), "Selection outline color: ", true);
        m_publishSelectionCheckBox = new JCheckBox("Publish selection events");
        m_subscribeSelectionCheckBox = new JCheckBox("Subscribe to selection events");
        m_subscribeSelectionCheckBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                enableSelectionFields();
            }
        });
        m_enableShowSelectedOnlyCheckBox = new JCheckBox("Enable 'Show selected tags only' option");
        m_showSelectedOnlyCheckBox = new JCheckBox("Show selected tags only");
        m_displayClearSelectionButtonCheckBox = new JCheckBox("Display 'Clear selection' button");
        m_subscribeFilterCheckBox = new JCheckBox("Subscribe to filter events");

        addTab("Options", initOptions());
        addTab("Display", initDisplay());
        addTab("Interactivity", initControls());
    }

    private JPanel initOptions() {
        JPanel generalPanel = new JPanel(new GridBagLayout());
        generalPanel.setBorder(new TitledBorder("General Options"));
        GridBagConstraints gbcG = createConfiguredGridBagConstraints();
        gbcG.fill = GridBagConstraints.HORIZONTAL;
        gbcG.gridwidth = 1;
        generalPanel.add(new JLabel("No. of words to display: "), gbcG);
        gbcG.gridx++;
        m_maxWordsSpinner.setPreferredSize(new Dimension(100, DialogUtil.DEF_TEXTFIELD_WIDTH));
        generalPanel.add(m_maxWordsSpinner, gbcG);
        //gbcG.gridwidth = 2;
        gbcG.gridx = 0;
        gbcG.gridy++;
        generalPanel.add(m_showWarningsCheckBox, gbcG);
        gbcG.gridx++;
        generalPanel.add(m_reportMissingValuesCheckBox, gbcG);
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
        gbcC.gridwidth = 2;
        m_wordColumnSelection.setPreferredSize(new Dimension(300, 50));
        columnsPanel.add(m_wordColumnSelection, gbcC);
        gbcC.gridwidth = 1;
        gbcC.gridy++;
        columnsPanel.add(m_aggregateWordsCheckBox, gbcC);
        gbcC.gridx++;
        columnsPanel.add(m_ignoreTermTagsCheckBox, gbcC);
        gbcC.gridwidth = 2;
        gbcC.gridx = 0;
        gbcC.gridy++;
        m_sizeColumnSelection.setPreferredSize(new Dimension(300, 50));
        columnsPanel.add(m_sizeColumnSelection, gbcC);
        gbcC.gridy++;
        columnsPanel.add(m_useSizePropertyCheckBox, gbcC);

        JPanel sizesPanel = new JPanel(new GridBagLayout());
        sizesPanel.setBorder(new TitledBorder("View and image sizes"));
        GridBagConstraints gbcS = createConfiguredGridBagConstraints();
        sizesPanel.add(new JLabel("Width of view/image (in px): "), gbcS);
        gbcS.gridx++;
        sizesPanel.add(m_imageWidthSpinner, gbcS);
        gbcS.gridx = 0;
        gbcS.gridy++;
        sizesPanel.add(new JLabel("Height of view/image (in px): "), gbcS);
        gbcS.gridx++;
        sizesPanel.add(m_imageHeightSpinner, gbcS);
        gbcS.gridx = 0;
        gbcS.gridy++;
        sizesPanel.add(m_resizeToWindowCheckBox, gbcS);
        gbcS.gridx++;
        sizesPanel.add(m_displayFullscreenButtonCheckBox, gbcS);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createConfiguredGridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(generalPanel, gbc);
        gbc.gridy++;
        panel.add(titlePanel, gbc);
        gbc.gridy++;
        panel.add(columnsPanel, gbc);
        gbc.gridy++;
        panel.add(sizesPanel, gbc);
        return panel;
    }

    private JPanel initDisplay() {
        JPanel fontPanel = new JPanel(new GridBagLayout());
        fontPanel.setBorder(new TitledBorder("Font"));
        GridBagConstraints gbcF = createConfiguredGridBagConstraints();
        fontPanel.add(new JLabel("Font family: "), gbcF);
        gbcF.gridwidth = 3;
        gbcF.gridx++;
        fontPanel.add(m_fontTextField, gbcF);
        gbcF.gridwidth = 1;
        gbcF.gridx = 0;
        gbcF.gridy++;
        fontPanel.add(new JLabel("Font scale: "), gbcF);
        gbcF.gridwidth = 3;
        gbcF.gridx++;
        fontPanel.add(m_fontScaleTypeComboBox, gbcF);
        gbcF.gridwidth = 1;
        gbcF.gridx = 0;
        gbcF.gridy++;
        fontPanel.add(new JLabel("Font size minimum: "), gbcF);
        gbcF.gridx++;
        m_minFontSizeSpinner.setPreferredSize(new Dimension(100, DialogUtil.DEF_TEXTFIELD_WIDTH));
        fontPanel.add(m_minFontSizeSpinner, gbcF);
        gbcF.gridx++;
        fontPanel.add(new JLabel("Maximum: "), gbcF);
        gbcF.gridx++;
        m_maxFontSizeSpinner.setPreferredSize(new Dimension(100, DialogUtil.DEF_TEXTFIELD_WIDTH));
        fontPanel.add(m_maxFontSizeSpinner, gbcF);
        gbcF.gridwidth = 4;
        gbcF.gridx = 0;
        gbcF.gridy++;
        fontPanel.add(m_fontBoldCheckBox, gbcF);

        JPanel colorPanel = new JPanel(new GridBagLayout());
        colorPanel.setBorder(new TitledBorder("Colors"));
        GridBagConstraints gbcC = createConfiguredGridBagConstraints();
        colorPanel.add(m_useColorPropertyCheckBox, gbcC);

        JPanel orientPanel = new JPanel(new GridBagLayout());
        orientPanel.setBorder(new TitledBorder("Orientation"));
        GridBagConstraints gbcO = createConfiguredGridBagConstraints();
        orientPanel.add(new JLabel("Spiral type: "), gbcO);
        gbcO.gridwidth = 3;
        gbcO.gridx++;
        orientPanel.add(m_spiralTypeComboBox, gbcO);
        gbcO.gridwidth = 1;
        gbcO.gridx = 0;
        gbcO.gridy++;
        orientPanel.add(new JLabel("Number of orientations: "), gbcO);
        gbcO.gridwidth = 3;
        gbcO.gridx++;
        m_numOrientationsSpinner.setPreferredSize(new Dimension(100, DialogUtil.DEF_TEXTFIELD_WIDTH));
        orientPanel.add(m_numOrientationsSpinner, gbcO);
        gbcO.gridwidth = 1;
        gbcO.gridx = 0;
        gbcO.gridy++;
        orientPanel.add(new JLabel("Start angle: "), gbcO);
        gbcO.gridx++;
        m_startAngleSpinner.setPreferredSize(new Dimension(100, DialogUtil.DEF_TEXTFIELD_WIDTH));
        orientPanel.add(m_startAngleSpinner, gbcO);
        gbcO.gridx++;
        orientPanel.add(new JLabel("End angle: "), gbcO);
        gbcO.gridx++;
        m_endAngleSpinner.setPreferredSize(new Dimension(100, DialogUtil.DEF_TEXTFIELD_WIDTH));
        orientPanel.add(m_endAngleSpinner, gbcO);

        JPanel behavPanel = new JPanel(new GridBagLayout());
        behavPanel.setBorder(new TitledBorder("Behaviour"));
        GridBagConstraints gbcB = createConfiguredGridBagConstraints();
        behavPanel.add(m_displayRefreshButtonCheckBox, gbcB);
        gbcB.gridx++;
        behavPanel.add(m_disableAnimationsCheckBox, gbcB);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createConfiguredGridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(fontPanel, gbc);
        gbc.gridy++;
        panel.add(colorPanel, gbc);
        gbc.gridy++;
        panel.add(orientPanel, gbc);
        gbc.gridy++;
        panel.add(behavPanel, gbc);
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
        viewControlsPanel.add(m_enableScaleTypeChangeCheckBox, gbcV);
        gbcV.gridx++;
        viewControlsPanel.add(m_enableFontSizeChangeCheckBox, gbcV);
        gbcV.gridx = 0;
        gbcV.gridy++;
        viewControlsPanel.add(m_enableSpiralTypeChangeCheckBox, gbcV);
        gbcV.gridx++;
        viewControlsPanel.add(m_enableNumOrientationsChangeCheckBox, gbcV);
        gbcV.gridx = 0;
        gbcV.gridy++;
        viewControlsPanel.add(m_enableAnglesChangeCheckBox, gbcV);

        JPanel selectionPanel = new JPanel(new GridBagLayout());
        selectionPanel.setBorder(BorderFactory.createTitledBorder("Selection"));
        GridBagConstraints gbcS = createConfiguredGridBagConstraints();
        selectionPanel.add(m_enableSelectionCheckBox, gbcS);
        gbcS.gridx++;
        selectionPanel.add(m_selectionColorChooser.getComponentPanel(), gbcS);
        gbcS.gridx = 0;
        gbcS.gridy++;
        selectionPanel.add(m_publishSelectionCheckBox, gbcS);
        gbcS.gridx++;
        selectionPanel.add(m_subscribeSelectionCheckBox, gbcS);
        gbcS.gridx = 0;
        gbcS.gridy++;
        selectionPanel.add(m_showSelectedOnlyCheckBox, gbcS);
        gbcS.gridx++;
        selectionPanel.add(m_enableShowSelectedOnlyCheckBox, gbcS);
        gbcS.gridx = 0;
        gbcS.gridy++;
        selectionPanel.add(m_displayClearSelectionButtonCheckBox, gbcS);
        gbcS.gridy++;
        selectionPanel.add(new JLabel("Selection column name: "), gbcS);
        gbcS.gridx++;
        selectionPanel.add(m_selectionColumnNameField, gbcS);

        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter"));
        GridBagConstraints gbcF = createConfiguredGridBagConstraints();
        filterPanel.add(m_subscribeFilterCheckBox, gbcF);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createConfiguredGridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(viewControlsPanel, gbc);
        gbc.gridy++;
        panel.add(selectionPanel, gbc);
        gbc.gridy++;
        panel.add(filterPanel, gbc);
        return panel;
    }

    private void setNumberOfFilters(final DataTableSpec spec) {
        int numFilters = 0;
        for (int i = 0; i < spec.getNumColumns(); i++) {
            if (spec.getColumnSpec(i).getFilterHandler().isPresent()) {
                numFilters++;
            }
        }
        StringBuilder builder = new StringBuilder("Subscribe to filter events");
        builder.append(" (");
        builder.append(numFilters == 0 ? "no" : numFilters);
        builder.append(numFilters == 1 ? " filter" : " filters");
        builder.append(" available)");
        m_subscribeFilterCheckBox.setText(builder.toString());
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

        TagCloudViewConfig config = new TagCloudViewConfig();
        config.setGenerateImage(m_generateImageCheckBox.isSelected());
        config.setShowWarningsInView(m_showWarningsCheckBox.isSelected());
        config.setReportMissingValues(m_reportMissingValuesCheckBox.isSelected());
        config.setResizeToWindow(m_resizeToWindowCheckBox.isSelected());
        config.setImageWidth((int)m_imageWidthSpinner.getValue());
        config.setImageHeight((int)m_imageHeightSpinner.getValue());
        config.setMaxWords((int)m_maxWordsSpinner.getValue());
        config.setDisplayFullscreenButton(m_displayFullscreenButtonCheckBox.isSelected());
        config.setDisplayRefreshButton(m_displayRefreshButtonCheckBox.isSelected());
        config.setDisableAnimations(m_disableAnimationsCheckBox.isSelected());
        config.setTitle(m_titleTextField.getText());
        config.setSubtitle(m_subtitleTextField.getText());
        config.setWordColumn(m_wordColumnSelection.getSelectedColumn());
        config.setAggregateWords(m_aggregateWordsCheckBox.isSelected());
        config.setIgnoreTermTags(m_ignoreTermTagsCheckBox.isSelected());
        config.setSizeColumn(m_sizeColumnSelection.getSelectedColumn());
        config.setUseSizeProp(m_useSizePropertyCheckBox.isSelected());
        config.setUseColorProp(m_useColorPropertyCheckBox.isSelected());
        config.setFont(m_fontTextField.getText());
        config.setMinFontSize((float)m_minFontSizeSpinner.getValue());
        config.setMaxFontSize((float)m_maxFontSizeSpinner.getValue());
        config.setFontScaleType((TagCloudFontScaleType)m_fontScaleTypeComboBox.getSelectedItem());
        config.setFontBold(m_fontBoldCheckBox.isSelected());
        config.setSpiralType((TagCloudSpiralType)m_spiralTypeComboBox.getSelectedItem());
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
        config.setEnableSelection(m_enableSelectionCheckBox.isSelected());
        config.setSelectionColumnName(m_selectionColumnNameField.getText());
        config.setSelectionColor(m_selectionColorChooser.getColor());
        config.setPublishSelection(m_publishSelectionCheckBox.isSelected());
        config.setSubscribeSelection(m_subscribeSelectionCheckBox.isSelected());
        config.setEnableShowSelectedOnly(m_enableShowSelectedOnlyCheckBox.isSelected());
        config.setDefaultShowSelectedOnly(m_showSelectedOnlyCheckBox.isSelected());
        config.setDisplayClearSelectionButton(m_displayClearSelectionButtonCheckBox.isSelected());
        config.setSubscribeFilter(m_subscribeFilterCheckBox.isSelected());
        config.saveSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs) throws NotConfigurableException {
        DataTableSpec spec = (DataTableSpec)specs[0];

        TagCloudViewConfig config = new TagCloudViewConfig();
        config.loadSettingsForDialog(settings, spec);
        m_generateImageCheckBox.setSelected(config.getGenerateImage());
        m_showWarningsCheckBox.setSelected(config.getShowWarningsInView());
        m_reportMissingValuesCheckBox.setSelected(config.getReportMissingValues());
        m_resizeToWindowCheckBox.setSelected(config.getResizeToWindow());
        m_imageWidthSpinner.setValue(config.getImageWidth());
        m_imageHeightSpinner.setValue(config.getImageHeight());
        m_maxWordsSpinner.setValue(config.getMaxWords());
        m_displayFullscreenButtonCheckBox.setSelected(config.getDisplayFullscreenButton());
        m_displayRefreshButtonCheckBox.setSelected(config.getDisplayRefreshButton());
        m_disableAnimationsCheckBox.setSelected(config.getDisableAnimations());
        m_titleTextField.setText(config.getTitle());
        m_subtitleTextField.setText(config.getSubtitle());
        m_wordColumnSelection.update(spec, config.getWordColumn(), config.getWordColumn() == null);
        m_aggregateWordsCheckBox.setSelected(config.getAggregateWords());
        m_ignoreTermTagsCheckBox.setSelected(config.getIgnoreTermTags());
        m_sizeColumnSelection.update(spec, config.getSizeColumn());
        m_useColorPropertyCheckBox.setSelected(config.getUseColorProp());
        m_fontTextField.setText(config.getFont());
        m_minFontSizeSpinner.setValue(config.getMinFontSize());
        m_maxFontSizeSpinner.setValue(config.getMaxFontSize());
        m_fontScaleTypeComboBox.setSelectedItem(config.getFontScaleType());
        m_fontBoldCheckBox.setSelected(config.getFontBold());
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
        m_enableSelectionCheckBox.setSelected(config.getEnableSelection());
        m_selectionColumnNameField.setText(config.getSelectionColumnName());
        m_selectionColorChooser.setColor(config.getSelectionColor());
        m_publishSelectionCheckBox.setSelected(config.getPublishSelection());
        m_subscribeSelectionCheckBox.setSelected(config.getSubscribeSelection());
        m_enableShowSelectedOnlyCheckBox.setSelected(config.getEnableShowSelectedOnly());
        m_showSelectedOnlyCheckBox.setSelected(config.getDefaultShowSelectedOnly());
        m_displayClearSelectionButtonCheckBox.setSelected(config.getDisplayClearSelectionButton());
        m_subscribeFilterCheckBox.setSelected(config.getSubscribeFilter());

        enableViewConfigFields();
        enableSelectionFields();
        boolean onlyNoneColAvailable = m_sizeColumnSelection.getAvailableColumns().size() < 2;
        m_sizeColumnSelection.setEnabled(!onlyNoneColAvailable);
        m_useSizePropertyCheckBox.setEnabled(!onlyNoneColAvailable);
        m_useSizePropertyCheckBox.setSelected(onlyNoneColAvailable || config.getUseSizeProp());
        if (config.getSizeColumn() != null) {
            m_previousSizeColumn = config.getSizeColumn();
        }
        enableSizeFields();
        enableIgnoreTagsField();
        setNumberOfFilters(spec);
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

    private void enableSelectionFields() {
        boolean enable = m_enableSelectionCheckBox.isSelected();
        boolean sub = m_subscribeSelectionCheckBox.isSelected();
        boolean conf = m_enableViewConfigCheckBox.isSelected();
        m_selectionColumnNameField.setEnabled(enable);
        m_publishSelectionCheckBox.setEnabled(enable);
        m_displayClearSelectionButtonCheckBox.setEnabled(enable);
        m_selectionColorChooser.getModel().setEnabled(enable || conf || sub);
        m_showSelectedOnlyCheckBox.setEnabled(enable || conf || sub);
        m_enableShowSelectedOnlyCheckBox.setEnabled(conf && (enable || sub));
    }

    private void enableSizeFields() {
        boolean enable = !m_resizeToWindowCheckBox.isSelected();
        m_imageWidthSpinner.setEnabled(enable);
        m_imageHeightSpinner.setEnabled(enable);
    }

    private void enableIgnoreTagsField() {
        boolean enable = m_aggregateWordsCheckBox.isSelected();
        DataColumnSpec spec = m_wordColumnSelection.getSelectedColumnAsSpec();
        if (spec != null) {
            enable &= TagCloudTermCellResolver.isTermValue(spec);
        }
        m_ignoreTermTagsCheckBox.setEnabled(enable);
    }

}
