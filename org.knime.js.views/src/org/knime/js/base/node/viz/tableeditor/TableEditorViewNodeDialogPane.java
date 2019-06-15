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
 *   Apr 28, 2016 (albrecht): created
 */
package org.knime.js.base.node.viz.tableeditor;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.filter.column.DataColumnSpecFilterConfiguration;
import org.knime.core.node.util.filter.column.DataColumnSpecFilterPanel;
import org.knime.js.core.components.datetime.DialogComponentDateTimeOptions;
import org.knime.js.core.components.datetime.SettingsModelDateTimeOptions;

/**
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
public class TableEditorViewNodeDialogPane extends NodeDialogPane {
    private static final int TEXT_FIELD_SIZE = 20;

    private final TableEditorViewConfig m_config;

    private final JSpinner m_maxRowsSpinner;
    private final JCheckBox m_enablePagingCheckBox;
    private final JSpinner m_initialPageSizeSpinner;
    private final JCheckBox m_enablePageSizeChangeCheckBox;
    private final JTextField m_allowedPageSizesField;
    private final JCheckBox m_enableShowAllCheckBox;
    private final JCheckBox m_enableJumpToPageCheckBox;
    private final JCheckBox m_displayRowColorsCheckBox;
    private final JCheckBox m_displayRowIdsCheckBox;
    private final JCheckBox m_displayColumnHeadersCheckBox;
    private final JCheckBox m_displayRowIndexCheckBox;
    private final JCheckBox m_displayFullscreenButtonCheckBox;
    private final JTextField m_titleField;
    private final JTextField m_subtitleField;
    private final DataColumnSpecFilterPanel m_columnFilterPanel;
    private final JCheckBox m_enableSelectionCheckbox;
    private final JCheckBox m_enableClearSelectionButtonCheckbox;
    private final JRadioButton m_singleSelectionRadioButton;
    private final JRadioButton m_multipleSelectionRadioButton;
    private final JTextField m_selectionColumnNameField;
    private final JCheckBox m_publishSelectionCheckBox;
    private final JCheckBox m_subscribeSelectionCheckBox;
    private final JCheckBox m_enableHideUnselectedCheckbox;
    private final JCheckBox m_hideUnselectedCheckbox;
    private final JCheckBox m_enableSearchCheckbox;
    private final JCheckBox m_enableColumnSearchCheckbox;
    private final JCheckBox m_publishFilterCheckBox;
    private final JCheckBox m_subscribeFilterCheckBox;
    private final JCheckBox m_enableSortingCheckBox;
    private final JCheckBox m_enableClearSortButtonCheckBox;
    private final DialogComponentDateTimeOptions m_dateTimeFormats;
    private final JCheckBox m_enableGlobalNumberFormatCheckbox;
    private final JSpinner m_globalNumberFormatDecimalSpinner;
    private final JCheckBox m_displayMissingValueAsQuestionMark;

    // editor
    private final DataColumnSpecFilterPanel m_editableColumnsFilterPanel;
    private TableEditorChangesSettingsModel m_editorChanges;
    private final JButton m_resetEditorChangesButton;
    private String m_tableHash;

    TableEditorViewNodeDialogPane() {
        m_config = new TableEditorViewConfig();

        m_maxRowsSpinner = new JSpinner(new SpinnerNumberModel(0, 0, null, 1));
        m_enablePagingCheckBox = new JCheckBox("Enable pagination");
        m_enablePagingCheckBox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                enablePagingFields();
            }
        });
        m_initialPageSizeSpinner = new JSpinner(new SpinnerNumberModel(1, 1, null, 1));
        m_enablePageSizeChangeCheckBox = new JCheckBox("Enable page size change control");
        m_enablePageSizeChangeCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                enablePagingFields();
            }
        });
        m_allowedPageSizesField = new JTextField(20);
        m_enableShowAllCheckBox = new JCheckBox("Add \"All\" option to page sizes");
        m_enableJumpToPageCheckBox = new JCheckBox("Display field to jump to a page directly");
        m_displayRowColorsCheckBox = new JCheckBox("Display row colors");
        m_displayRowIdsCheckBox = new JCheckBox("Display row keys");
        m_displayColumnHeadersCheckBox = new JCheckBox("Display column headers");
        m_displayColumnHeadersCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                enableSortingFields();
            }
        });
        m_displayRowIndexCheckBox = new JCheckBox("Display row indices");
        m_displayFullscreenButtonCheckBox = new JCheckBox("Display fullscreen button");
        m_titleField = new JTextField(TEXT_FIELD_SIZE);
        m_subtitleField = new JTextField(TEXT_FIELD_SIZE);
        m_columnFilterPanel = new DataColumnSpecFilterPanel();
        m_enableSelectionCheckbox = new JCheckBox("Enable selection");
        m_enableSelectionCheckbox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                enableSelectionFields();
            }
        });
        m_enableClearSelectionButtonCheckbox = new JCheckBox("Enable 'Clear Selection' button");
        m_singleSelectionRadioButton = new JRadioButton("Single Selection");
        m_multipleSelectionRadioButton = new JRadioButton("Multiple Selection");
        ButtonGroup selectionGroup = new ButtonGroup();
        selectionGroup.add(m_singleSelectionRadioButton);
        selectionGroup.add(m_multipleSelectionRadioButton);
        m_multipleSelectionRadioButton.setSelected(true);
        m_singleSelectionRadioButton.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                enableSelectionFields();
            }
        });
        m_selectionColumnNameField = new JTextField(TEXT_FIELD_SIZE);
        m_publishSelectionCheckBox = new JCheckBox("Publish selection events");
        m_subscribeSelectionCheckBox = new JCheckBox("Subscribe to selection events");
        m_hideUnselectedCheckbox = new JCheckBox("Show selected rows only");
        m_enableHideUnselectedCheckbox = new JCheckBox("Enable 'Show selected rows only' option");
        m_enableSearchCheckbox = new JCheckBox("Enable searching");
        m_enableSearchCheckbox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                enableSearchFields();
            }
        });
        m_enableColumnSearchCheckbox = new JCheckBox("Enable search for individual columns");
        m_enableColumnSearchCheckbox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                enableSearchFields();
            }
        });
        m_publishFilterCheckBox = new JCheckBox("Publish filter events");
        m_subscribeFilterCheckBox = new JCheckBox("Subscribe to filter events");
        m_enableSortingCheckBox = new JCheckBox("Enable sorting on columns");
        m_enableSortingCheckBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                enableSortingFields();
            }
        });
        m_enableClearSortButtonCheckBox = new JCheckBox("Enable 'Clear Sorting' button");

        DialogComponentDateTimeOptions.Config dateTimeFormatsConfig = new DialogComponentDateTimeOptions.Config();
        dateTimeFormatsConfig.setShowTimezoneChooser(false);
        m_dateTimeFormats = new DialogComponentDateTimeOptions(
            new SettingsModelDateTimeOptions(TableEditorViewConfig.CFG_DATE_TIME_FORMATS), "Global Date Formatters", dateTimeFormatsConfig);

        m_enableGlobalNumberFormatCheckbox = new JCheckBox("Enable global number format (double cells)");
        m_enableGlobalNumberFormatCheckbox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                enableFormatterFields();
            }
        });
        m_globalNumberFormatDecimalSpinner = new JSpinner(new SpinnerNumberModel(2, 0, null, 1));

        m_displayMissingValueAsQuestionMark = new JCheckBox("Display missing value as red question mark");

        // editor
        m_editableColumnsFilterPanel = new DataColumnSpecFilterPanel();
        m_resetEditorChangesButton = new JButton("Reset editor changes");
        m_resetEditorChangesButton.setEnabled(false);
        m_resetEditorChangesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                m_editorChanges.reset();
                m_resetEditorChangesButton.setEnabled(false);
            }
        });

        addTab("Options", initOptions());
        addTab("Editor", initEditor());
        addTab("Interactivity", initInteractivity());
        addTab("Formatters", initFormatters());
    }

    private JPanel initOptions() {
        JPanel generalPanel = new JPanel(new GridBagLayout());
        generalPanel.setBorder(new TitledBorder("General Options"));
        GridBagConstraints gbcG = createConfiguredGridBagConstraints();
        gbcG.fill = GridBagConstraints.HORIZONTAL;
        gbcG.gridwidth = 1;
        generalPanel.add(new JLabel("No. of rows to display: "), gbcG);
        gbcG.gridx++;
        m_maxRowsSpinner.setPreferredSize(new Dimension(100, TEXT_FIELD_SIZE));
        generalPanel.add(m_maxRowsSpinner, gbcG);

        JPanel titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setBorder(new TitledBorder("Titles"));
        GridBagConstraints gbcT = createConfiguredGridBagConstraints();
        titlePanel.add(new JLabel("Title: "), gbcT);
        gbcT.gridx++;
        titlePanel.add(m_titleField, gbcT);
        gbcT.gridx = 0;
        gbcT.gridy++;
        titlePanel.add(new JLabel("Subtitle: "), gbcT);
        gbcT.gridx++;
        titlePanel.add(m_subtitleField, gbcT);

        JPanel displayPanel = new JPanel(new GridBagLayout());
        displayPanel.setBorder(new TitledBorder("Display Options"));
        GridBagConstraints gbcD = createConfiguredGridBagConstraints();
        gbcD.gridwidth = 1;
        gbcD.gridx = 0;
        displayPanel.add(m_displayRowColorsCheckBox, gbcD);
        gbcD.gridx++;
        displayPanel.add(m_displayRowIdsCheckBox, gbcD);
        gbcD.gridx++;
        displayPanel.add(m_displayFullscreenButtonCheckBox, gbcD);
        gbcD.gridx = 0;
        gbcD.gridy++;
        displayPanel.add(m_displayRowIndexCheckBox, gbcD);
        gbcD.gridx++;
        displayPanel.add(m_displayColumnHeadersCheckBox, gbcD);
        gbcD.gridx = 0;
        gbcD.gridy++;
        displayPanel.add(new JLabel("Columns to display: "), gbcD);
        gbcD.gridy++;
        gbcD.gridwidth = 3;
        displayPanel.add(m_columnFilterPanel, gbcD);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createConfiguredGridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(generalPanel, gbc);
        gbc.gridy++;
        panel.add(titlePanel, gbc);
        gbc.gridy++;
        panel.add(displayPanel, gbc);
        return panel;
    }

    private JPanel initInteractivity() {
        JPanel pagingPanel = new JPanel(new GridBagLayout());
        pagingPanel.setBorder(new TitledBorder("Paging"));
        GridBagConstraints gbcP = createConfiguredGridBagConstraints();
        gbcP.gridwidth = 2;
        pagingPanel.add(m_enablePagingCheckBox, gbcP);
        gbcP.gridy++;
        gbcP.gridwidth = 1;
        pagingPanel.add(new JLabel("Initial page size: "), gbcP);
        gbcP.gridx++;
        m_initialPageSizeSpinner.setPreferredSize(new Dimension(100, TEXT_FIELD_SIZE));
        pagingPanel.add(m_initialPageSizeSpinner, gbcP);
        gbcP.gridx = 0;
        gbcP.gridy++;
        pagingPanel.add(m_enablePageSizeChangeCheckBox, gbcP);
        gbcP.gridx = 0;
        gbcP.gridy++;
        pagingPanel.add(new JLabel("Selectable page sizes: "), gbcP);
        gbcP.gridx++;
        pagingPanel.add(m_allowedPageSizesField, gbcP);
        gbcP.gridx = 0;
        gbcP.gridy++;
        gbcP.gridwidth = 2;
        pagingPanel.add(m_enableShowAllCheckBox, gbcP);
        //gbcP.gridy++;
        //pagingPanel.add(m_enableJumpToPageCheckBox, gbcP);

        JPanel selectionPanel = new JPanel(new GridBagLayout());
        selectionPanel.setBorder(new TitledBorder("Selection"));
        GridBagConstraints gbcS = createConfiguredGridBagConstraints();
        gbcS.gridwidth = 1;
        selectionPanel.add(m_enableSelectionCheckbox, gbcS);
        gbcS.gridx++;
        selectionPanel.add(m_enableClearSelectionButtonCheckbox, gbcS);
        gbcS.gridx = 0;
        gbcS.gridy++;
        selectionPanel.add(m_multipleSelectionRadioButton, gbcS);
        gbcS.gridx++;
        selectionPanel.add(m_singleSelectionRadioButton, gbcS);
        gbcS.gridx = 0;
        gbcS.gridy++;
        selectionPanel.add(m_hideUnselectedCheckbox, gbcS);
        gbcS.gridx++;
        selectionPanel.add(m_enableHideUnselectedCheckbox, gbcS);
        gbcS.gridx = 0;
        gbcS.gridy++;
        selectionPanel.add(m_publishSelectionCheckBox, gbcS);
        gbcS.gridx++;
        selectionPanel.add(m_subscribeSelectionCheckBox, gbcS);
        gbcS.gridx = 0;
        gbcS.gridy++;
        selectionPanel.add(new JLabel("Selection column name: "), gbcS);
        gbcS.gridx++;
        selectionPanel.add(m_selectionColumnNameField, gbcS);

        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBorder(new TitledBorder("Searching / Filtering"));
        GridBagConstraints gbcSe = createConfiguredGridBagConstraints();
        searchPanel.add(m_enableSearchCheckbox, gbcSe);
        gbcSe.gridx++;
        searchPanel.add(m_enableColumnSearchCheckbox, gbcSe);
        gbcSe.gridx = 0;
        gbcSe.gridy++;
        /*searchPanel.add(m_publishFilterCheckBox, gbcSe);
        gbcSe.gridx++;*/
        searchPanel.add(m_subscribeFilterCheckBox, gbcSe);

        JPanel sortingPanel = new JPanel(new GridBagLayout());
        sortingPanel.setBorder(new TitledBorder("Sorting"));
        GridBagConstraints gbcSo = createConfiguredGridBagConstraints();
        sortingPanel.add(m_enableSortingCheckBox, gbcSo);
        gbcSo.gridx++;
        sortingPanel.add(m_enableClearSortButtonCheckBox, gbcSo);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createConfiguredGridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(pagingPanel, gbc);
        gbc.gridy++;
        panel.add(selectionPanel, gbc);
        gbc.gridy++;
        panel.add(searchPanel, gbc);
        gbc.gridy++;
        panel.add(sortingPanel, gbc);
        return panel;
    }

    private JPanel initFormatters() {
        JPanel numberPanel = new JPanel(new GridBagLayout());
        numberPanel.setBorder(new TitledBorder("Number Formatter"));
        GridBagConstraints gbcN = createConfiguredGridBagConstraints();
        gbcN.gridwidth = 2;
        numberPanel.add(m_enableGlobalNumberFormatCheckbox, gbcN);
        gbcN.gridy++;
        gbcN.gridwidth = 1;
        numberPanel.add(new JLabel("Decimal places: "), gbcN);
        gbcN.gridx++;
        m_globalNumberFormatDecimalSpinner.setPreferredSize(new Dimension(100, TEXT_FIELD_SIZE));
        numberPanel.add(m_globalNumberFormatDecimalSpinner, gbcN);

        JPanel missingValuePanel = new JPanel(new GridBagLayout());
        missingValuePanel.setBorder(new TitledBorder("Missing value formatter"));
        missingValuePanel.add(m_displayMissingValueAsQuestionMark);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createConfiguredGridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(m_dateTimeFormats.getPanel(), gbc);
        gbc.gridy++;
        panel.add(numberPanel, gbc);
        gbc.gridy++;
        panel.add(missingValuePanel, gbc);
        return panel;
    }

    private JPanel initEditor() {
        JPanel editorPanel = new JPanel(new GridBagLayout());
        editorPanel.setBorder(new TitledBorder("Editor Options"));
        GridBagConstraints gbcD = createConfiguredGridBagConstraints();
        gbcD.gridwidth = 1;
        gbcD.gridx = 0;
        editorPanel.add(new JLabel("Editable columns: "), gbcD);
        gbcD.gridy++;
        gbcD.gridwidth = 3;
        editorPanel.add(m_editableColumnsFilterPanel, gbcD);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createConfiguredGridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(editorPanel, gbc);
        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(m_resetEditorChangesButton, gbc);

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
    protected void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs) throws NotConfigurableException {
        DataTableSpec inSpec = (DataTableSpec)specs[0];
        m_config.loadSettingsForDialog(settings, inSpec);
        m_maxRowsSpinner.setValue(m_config.getMaxRows());
        m_enablePagingCheckBox.setSelected(m_config.getEnablePaging());
        m_initialPageSizeSpinner.setValue(m_config.getIntialPageSize());
        m_enablePageSizeChangeCheckBox.setSelected(m_config.getEnablePageSizeChange());
        m_allowedPageSizesField.setText(getAllowedPageSizesString(m_config.getAllowedPageSizes()));
        m_enableShowAllCheckBox.setSelected(m_config.getPageSizeShowAll());
        m_enableJumpToPageCheckBox.setSelected(m_config.getEnableJumpToPage());
        m_displayRowColorsCheckBox.setSelected(m_config.getDisplayRowColors());
        m_displayRowIdsCheckBox.setSelected(m_config.getDisplayRowIds());
        m_displayColumnHeadersCheckBox.setSelected(m_config.getDisplayColumnHeaders());
        m_displayRowIndexCheckBox.setSelected(m_config.getDisplayRowIndex());
        m_displayFullscreenButtonCheckBox.setSelected(m_config.getDisplayFullscreenButton());
        m_titleField.setText(m_config.getTitle());
        m_subtitleField.setText(m_config.getSubtitle());
        m_columnFilterPanel.loadConfiguration(m_config.getColumnFilterConfig(), inSpec);
        m_enableSelectionCheckbox.setSelected(m_config.getEnableSelection());
        m_enableClearSelectionButtonCheckbox.setSelected(m_config.getEnableClearSelectionButton());
        boolean single = m_config.getSingleSelection();
        m_singleSelectionRadioButton.setSelected(single);
        m_multipleSelectionRadioButton.setSelected(!single);
        m_selectionColumnNameField.setText(m_config.getSelectionColumnName());
        m_hideUnselectedCheckbox.setSelected(m_config.getHideUnselected());
        m_enableHideUnselectedCheckbox.setSelected(m_config.getEnableHideUnselected());
        m_publishSelectionCheckBox.setSelected(m_config.getPublishSelection());
        m_subscribeSelectionCheckBox.setSelected(m_config.getSubscribeSelection());
        m_enableSearchCheckbox.setSelected(m_config.getEnableSearching());
        m_enableColumnSearchCheckbox.setSelected(m_config.getEnableColumnSearching());
        m_publishFilterCheckBox.setSelected(m_config.getPublishFilter());
        m_subscribeFilterCheckBox.setSelected(m_config.getSubscribeFilter());
        m_enableSortingCheckBox.setSelected(m_config.getEnableSorting());
        m_enableClearSortButtonCheckBox.setSelected(m_config.getEnableClearSortButton());
        m_dateTimeFormats.loadSettingsFromModel(m_config.getDateTimeFormats());
        m_enableGlobalNumberFormatCheckbox.setSelected(m_config.getEnableGlobalNumberFormat());
        m_globalNumberFormatDecimalSpinner.setValue(m_config.getGlobalNumberFormatDecimals());
        m_displayMissingValueAsQuestionMark.setSelected(m_config.getDisplayMissingValueAsQuestionMark());
        enablePagingFields();
        enableSelectionFields();
        enableSearchFields();
        enableFormatterFields();
        enableSortingFields();
        setNumberOfFilters(inSpec);
        // editor
        m_editableColumnsFilterPanel.loadConfiguration(m_config.getEditableColumnFilterConfig(), inSpec);
        m_editorChanges = m_config.getEditorChanges();
        m_tableHash = m_config.getTableHash();
        m_resetEditorChangesButton.setEnabled(m_editorChanges.getChanges().size() > 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        m_dateTimeFormats.validateSettings();

        m_config.setMaxRows((Integer)m_maxRowsSpinner.getValue());
        m_config.setEnablePaging(m_enablePagingCheckBox.isSelected());
        m_config.setIntialPageSize((Integer)m_initialPageSizeSpinner.getValue());
        m_config.setEnablePageSizeChange(m_enablePageSizeChangeCheckBox.isSelected());
        m_config.setAllowedPageSizes(getAllowedPageSizes());
        m_config.setPageSizeShowAll(m_enableShowAllCheckBox.isSelected());
        m_config.setEnableJumpToPage(m_enableJumpToPageCheckBox.isSelected());
        m_config.setDisplayRowColors(m_displayRowColorsCheckBox.isSelected());
        m_config.setDisplayRowIds(m_displayRowIdsCheckBox.isSelected());
        m_config.setDisplayColumnHeaders(m_displayColumnHeadersCheckBox.isSelected());
        m_config.setDisplayRowIndex(m_displayRowIndexCheckBox.isSelected());
        m_config.setDisplayFullscreenButton(m_displayFullscreenButtonCheckBox.isSelected());
        m_config.setTitle(m_titleField.getText());
        m_config.setSubtitle(m_subtitleField.getText());
        DataColumnSpecFilterConfiguration filterConfig = new DataColumnSpecFilterConfiguration(TableEditorViewConfig.CFG_COLUMN_FILTER);
        m_columnFilterPanel.saveConfiguration(filterConfig);
        m_config.setColumnFilterConfig(filterConfig);
        m_config.setEnableSelection(m_enableSelectionCheckbox.isSelected());
        m_config.setEnableClearSelectionButton(m_enableClearSelectionButtonCheckbox.isSelected());
        m_config.setSingleSelection(m_singleSelectionRadioButton.isSelected());
        m_config.setSelectionColumnName(m_selectionColumnNameField.getText());
        m_config.setHideUnselected(m_hideUnselectedCheckbox.isSelected());
        m_config.setEnableHideUnselected(m_enableHideUnselectedCheckbox.isSelected());
        m_config.setPublishSelection(m_publishSelectionCheckBox.isSelected());
        m_config.setSubscribeSelection(m_subscribeSelectionCheckBox.isSelected());
        m_config.setEnableSorting(m_enableSortingCheckBox.isSelected());
        m_config.setEnableClearSortButton(m_enableClearSortButtonCheckBox.isSelected());
        m_config.setEnableSearching(m_enableSearchCheckbox.isSelected());
        m_config.setEnableColumnSearching(m_enableColumnSearchCheckbox.isSelected());
        m_config.setPublishFilter(m_publishFilterCheckBox.isSelected());
        m_config.setSubscribeFilter(m_subscribeFilterCheckBox.isSelected());
        m_config.setDateTimeFormats((SettingsModelDateTimeOptions)m_dateTimeFormats.getModel());
        m_config.setEnableGlobalNumberFormat(m_enableGlobalNumberFormatCheckbox.isSelected());
        m_config.setGlobalNumberFormatDecimals((Integer)m_globalNumberFormatDecimalSpinner.getValue());
        m_config.setDisplayMissingValueAsQuestionMark(m_displayMissingValueAsQuestionMark.isSelected());

        // editor
        DataColumnSpecFilterConfiguration editableColumnsFilterConfig = new DataColumnSpecFilterConfiguration(TableEditorViewConfig.CFG_EDITABLE_COLUMNS_FILTER);
        m_editableColumnsFilterPanel.saveConfiguration(editableColumnsFilterConfig);
        m_config.setEditableColumnFilterConfig(editableColumnsFilterConfig);
        m_config.setEditorChanges(m_editorChanges);
        m_config.setTableHash(m_tableHash);

        m_config.saveSettings(settings);
    }

    private String getAllowedPageSizesString(final int[] sizes) {
        if (sizes.length < 1) {
            return "";
        }
        StringBuilder builder = new StringBuilder(String.valueOf(sizes[0]));
        for (int i = 1; i < sizes.length; i++) {
            builder.append(", ");
            builder.append(sizes[i]);
        }
        return builder.toString();
    }

    private int[] getAllowedPageSizes() throws InvalidSettingsException {
        String[] sizesArray = m_allowedPageSizesField.getText().split(",");
        int[] allowedPageSizes = new int[sizesArray.length];
        try {
            for (int i = 0; i < sizesArray.length; i++) {
                allowedPageSizes[i] = Integer.parseInt(sizesArray[i].trim());
            }
        } catch (NumberFormatException e) {
            throw new InvalidSettingsException(e.getMessage(), e);
        }
        return allowedPageSizes;
    }

    private void enablePagingFields() {
        boolean enableGlobal = m_enablePagingCheckBox.isSelected();
        boolean enableSizeChange = m_enablePageSizeChangeCheckBox.isSelected();
        m_initialPageSizeSpinner.setEnabled(enableGlobal);
        m_enablePageSizeChangeCheckBox.setEnabled(enableGlobal);
        m_allowedPageSizesField.setEnabled(enableGlobal && enableSizeChange);
        m_enableShowAllCheckBox.setEnabled(enableGlobal && enableSizeChange);
        m_enableJumpToPageCheckBox.setEnabled(enableGlobal);
    }

    private void enableSelectionFields() {
        boolean enable = m_enableSelectionCheckbox.isSelected();
        boolean single = m_singleSelectionRadioButton.isSelected();

        m_enableClearSelectionButtonCheckbox.setEnabled(enable);
        m_singleSelectionRadioButton.setEnabled(enable);
        m_multipleSelectionRadioButton.setEnabled(enable);
        m_hideUnselectedCheckbox.setEnabled(enable && !single);
        m_enableHideUnselectedCheckbox.setEnabled(enable && !single);
        m_publishSelectionCheckBox.setEnabled(enable);
        m_subscribeSelectionCheckBox.setEnabled(enable && !single);
        m_selectionColumnNameField.setEnabled(enable);
    }

    private void enableSearchFields() {
        boolean enable = m_enableSearchCheckbox.isSelected() || m_enableColumnSearchCheckbox.isSelected();
        m_publishFilterCheckBox.setEnabled(enable);
    }

    private void enableFormatterFields() {
        boolean enableNumberFormat = m_enableGlobalNumberFormatCheckbox.isSelected();
        m_globalNumberFormatDecimalSpinner.setEnabled(enableNumberFormat);
    }

    private void enableSortingFields() {
        boolean enableFields = m_displayColumnHeadersCheckBox.isSelected();
        m_enableSortingCheckBox.setEnabled(enableFields);
        m_enableClearSortButtonCheckBox.setEnabled(enableFields && m_enableSortingCheckBox.isSelected());
    }
}
