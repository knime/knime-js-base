/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME GmbH, Konstanz, Germany
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
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ---------------------------------------------------------------------
 *
 * History
 *   Apr 28, 2016 (albrecht): created
 */
package org.knime.js.base.node.viz.pagedTable;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.StringHistory;
import org.knime.core.node.util.filter.column.DataColumnSpecFilterConfiguration;
import org.knime.core.node.util.filter.column.DataColumnSpecFilterPanel;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableBiMap.Builder;

/**
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
public class PagedTableViewNodeDialogPane extends NodeDialogPane {

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
    private final JTextField m_selectionColumnNameField;
    private final JCheckBox m_publishSelectionCheckBox;
    private final JCheckBox m_subscribeSelectionCheckBox;
    private final JCheckBox m_enableHideUnselectedCheckbox;
    private final JCheckBox m_enableSearchCheckbox;
    private final JCheckBox m_enableColumnSearchCheckbox;
    private final JCheckBox m_publishFilterCheckBox;
    private final JCheckBox m_subscribeFilterCheckBox;
    private final JCheckBox m_enableSortingCheckBox;
    private final JCheckBox m_enableClearSortButtonCheckBox;
    private final DialogComponentStringSelection m_globalDateTimeLocaleChooser;
    private final DialogComponentStringSelection m_globalDateTimeFormatChooser;
    private final DialogComponentStringSelection m_globalLocalDateFormatChooser;
    private final DialogComponentStringSelection m_globalLocalDateTimeFormatChooser;
    private final DialogComponentStringSelection m_globalLocalTimeFormatChooser;
    private final DialogComponentStringSelection m_globalZonedDateTimeFormatChooser;
    private final JCheckBox m_enableGlobalNumberFormatCheckbox;
    private final JSpinner m_globalNumberFormatDecimalSpinner;

    PagedTableViewNodeDialogPane() {
        m_hideInWizardCheckBox = new JCheckBox("Hide in wizard");
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
        m_selectionColumnNameField = new JTextField(TEXT_FIELD_SIZE);
        m_publishSelectionCheckBox = new JCheckBox("Publish selection events");
        m_subscribeSelectionCheckBox = new JCheckBox("Subscribe to selection events");
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

        m_globalDateTimeLocaleChooser =
                new DialogComponentStringSelection(
                    new SettingsModelString(
                        PagedTableViewConfig.CFG_GLOBAL_DATE_TIME_LOCALE,
                        PREDEFINED_DATE_TIME_LOCALES.get(PagedTableViewConfig.DEFAULT_GLOBAL_DATE_TIME_LOCALE)
                    ),
                    "", PREDEFINED_DATE_TIME_LOCALES.values(), true);

        m_globalDateTimeFormatChooser =
            new DialogComponentStringSelection(new SettingsModelString(PagedTableViewConfig.CFG_GLOBAL_DATE_TIME_FORMAT,
                PagedTableViewConfig.DEFAULT_GLOBAL_DATE_TIME_FORMAT), "", PREDEFINED_DATE_TIME_FORMATS, true);
        m_globalDateTimeFormatChooser.setSizeComponents(FORMAT_CHOOSER_WIDTH, FORMAT_CHOOSER_HEIGHT);

        m_globalLocalDateFormatChooser =
            new DialogComponentStringSelection(new SettingsModelString(PagedTableViewConfig.CFG_GLOBAL_LOCAL_DATE_FORMAT,
                PagedTableViewConfig.DEFAULT_GLOBAL_LOCAL_DATE_FORMAT), "", PREDEFINED_LOCAL_DATE_FORMATS, true);
        m_globalLocalDateFormatChooser.setSizeComponents(FORMAT_CHOOSER_WIDTH, FORMAT_CHOOSER_HEIGHT);

        m_globalLocalDateTimeFormatChooser =
            new DialogComponentStringSelection(new SettingsModelString(PagedTableViewConfig.CFG_GLOBAL_LOCAL_DATE_TIME_FORMAT,
                PagedTableViewConfig.DEFAULT_GLOBAL_LOCAL_DATE_TIME_FORMAT), "", PREDEFINED_LOCAL_DATE_TIME_FORMATS, true);
        m_globalLocalDateTimeFormatChooser.setSizeComponents(FORMAT_CHOOSER_WIDTH, FORMAT_CHOOSER_HEIGHT);

        m_globalLocalTimeFormatChooser =
            new DialogComponentStringSelection(new SettingsModelString(PagedTableViewConfig.CFG_GLOBAL_LOCAL_TIME_FORMAT,
                PagedTableViewConfig.DEFAULT_GLOBAL_LOCAL_TIME_FORMAT), "", PREDEFINED_LOCAL_TIME_FORMATS, true);
        m_globalLocalTimeFormatChooser.setSizeComponents(FORMAT_CHOOSER_WIDTH, FORMAT_CHOOSER_HEIGHT);

        m_globalZonedDateTimeFormatChooser =
            new DialogComponentStringSelection(new SettingsModelString(PagedTableViewConfig.CFG_GLOBAL_ZONED_DATE_TIME_FORMAT,
                PagedTableViewConfig.DEFAULT_GLOBAL_ZONED_DATE_TIME_FORMAT), "", PREDEFINED_ZONED_DATE_TIME_FORMATS, true);
        m_globalZonedDateTimeFormatChooser.setSizeComponents(FORMAT_CHOOSER_WIDTH, FORMAT_CHOOSER_HEIGHT);

        m_enableGlobalNumberFormatCheckbox = new JCheckBox("Enable global number format (double cells)");
        m_enableGlobalNumberFormatCheckbox.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                enableFormatterFields();
            }
        });
        m_globalNumberFormatDecimalSpinner = new JSpinner(new SpinnerNumberModel(2, 0, null, 1));
        addTab("Options", initOptions());
        addTab("Interactivity", initInteractivity());
        addTab("Formatters", initFormatters());
    }

    /**
     * @return
     */
    private JPanel initOptions() {
        JPanel generalPanel = new JPanel(new GridBagLayout());
        generalPanel.setBorder(new TitledBorder("General Options"));
        GridBagConstraints gbcG = createConfiguredGridBagConstraints();
        gbcG.gridwidth = 2;
        gbcG.fill = GridBagConstraints.HORIZONTAL;
        generalPanel.add(m_hideInWizardCheckBox, gbcG);
        gbcG.gridy++;
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
        selectionPanel.add(m_enableSelectionCheckbox, gbcS);
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
        JPanel datePanel = new JPanel(new GridBagLayout());
        datePanel.setBorder(new TitledBorder("Global Date Formatters"));
        GridBagConstraints gbcD = createConfiguredGridBagConstraints();
        datePanel.add(new JLabel("Locale: "), gbcD);
        gbcD.gridx++;
        datePanel.add(m_globalDateTimeLocaleChooser.getComponentPanel(), gbcD);
        gbcD.gridx = 0;
        gbcD.gridy++;
        datePanel.add(new JLabel("Date&Time (legacy) format: "), gbcD);
        gbcD.gridx++;
        datePanel.add(m_globalDateTimeFormatChooser.getComponentPanel(), gbcD);
        gbcD.gridx = 0;
        gbcD.gridy++;
        datePanel.add(new JLabel("Local Date format: "), gbcD);
        gbcD.gridx++;
        datePanel.add(m_globalLocalDateFormatChooser.getComponentPanel(), gbcD);
        gbcD.gridx = 0;
        gbcD.gridy++;
        datePanel.add(new JLabel("Local Date&Time format: "), gbcD);
        gbcD.gridx++;
        datePanel.add(m_globalLocalDateTimeFormatChooser.getComponentPanel(), gbcD);
        gbcD.gridx = 0;
        gbcD.gridy++;
        datePanel.add(new JLabel("Local Time format: "), gbcD);
        gbcD.gridx++;
        datePanel.add(m_globalLocalTimeFormatChooser.getComponentPanel(), gbcD);
        gbcD.gridx = 0;
        gbcD.gridy++;
        datePanel.add(new JLabel("Zoned Date&Time format: "), gbcD);
        gbcD.gridx++;
        datePanel.add(m_globalZonedDateTimeFormatChooser.getComponentPanel(), gbcD);
        gbcD.gridx = 0;

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

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = createConfiguredGridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(datePanel, gbc);
        gbc.gridy++;
        panel.add(numberPanel, gbc);
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
        PagedTableViewConfig config = new PagedTableViewConfig();
        DataTableSpec inSpec = (DataTableSpec)specs[0];
        config.loadSettingsForDialog(settings, inSpec);
        m_hideInWizardCheckBox.setSelected(config.getHideInWizard());
        m_maxRowsSpinner.setValue(config.getMaxRows());
        m_enablePagingCheckBox.setSelected(config.getEnablePaging());
        m_initialPageSizeSpinner.setValue(config.getIntialPageSize());
        m_enablePageSizeChangeCheckBox.setSelected(config.getEnablePageSizeChange());
        m_allowedPageSizesField.setText(getAllowedPageSizesString(config.getAllowedPageSizes()));
        m_enableShowAllCheckBox.setSelected(config.getPageSizeShowAll());
        m_enableJumpToPageCheckBox.setSelected(config.getEnableJumpToPage());
        m_displayRowColorsCheckBox.setSelected(config.getDisplayRowColors());
        m_displayRowIdsCheckBox.setSelected(config.getDisplayRowIds());
        m_displayColumnHeadersCheckBox.setSelected(config.getDisplayColumnHeaders());
        m_displayRowIndexCheckBox.setSelected(config.getDisplayRowIndex());
        m_displayFullscreenButtonCheckBox.setSelected(config.getDisplayFullscreenButton());
        m_titleField.setText(config.getTitle());
        m_subtitleField.setText(config.getSubtitle());
        m_columnFilterPanel.loadConfiguration(config.getColumnFilterConfig(), inSpec);
        m_enableSelectionCheckbox.setSelected(config.getEnableSelection());
        m_selectionColumnNameField.setText(config.getSelectionColumnName());
        m_enableHideUnselectedCheckbox.setSelected(config.getEnableHideUnselected());
        m_publishSelectionCheckBox.setSelected(config.getPublishSelection());
        m_subscribeSelectionCheckBox.setSelected(config.getSubscribeSelection());
        m_enableSearchCheckbox.setSelected(config.getEnableSearching());
        m_enableColumnSearchCheckbox.setSelected(config.getEnableColumnSearching());
        m_publishFilterCheckBox.setSelected(config.getPublishFilter());
        m_subscribeFilterCheckBox.setSelected(config.getSubscribeFilter());
        m_enableSortingCheckBox.setSelected(config.getEnableSorting());
        m_enableClearSortButtonCheckBox.setSelected(config.getEnableClearSortButton());
        m_globalDateTimeLocaleChooser.replaceListItems(loadDateTimeLocales().values(),
                                                       PREDEFINED_DATE_TIME_LOCALES.get(config.getGlobalDateTimeLocale()));
        m_globalDateTimeFormatChooser.replaceListItems(createPredefinedDateTimeFormats(), config.getGlobalDateTimeFormat());
        m_globalLocalDateFormatChooser.replaceListItems(createPredefinedLocalDateFormats(), config.getGlobalLocalDateFormat());
        m_globalLocalDateTimeFormatChooser.replaceListItems(createPredefinedLocalDateTimeFormats(), config.getGlobalLocalDateTimeFormat());
        m_globalLocalTimeFormatChooser.replaceListItems(createPredefinedLocalTimeFormats(), config.getGlobalLocalTimeFormat());
        m_globalZonedDateTimeFormatChooser.replaceListItems(createPredefinedZonedDateTimeFormats(), config.getGlobalZonedDateTimeFormat());
        m_enableGlobalNumberFormatCheckbox.setSelected(config.getEnableGlobalNumberFormat());
        m_globalNumberFormatDecimalSpinner.setValue(config.getGlobalNumberFormatDecimals());
        enablePagingFields();
        enableSelectionFields();
        enableSearchFields();
        enableFormatterFields();
        enableSortingFields();
        setNumberOfFilters(inSpec);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        PagedTableViewConfig config = new PagedTableViewConfig();
        config.setHideInWizard(m_hideInWizardCheckBox.isSelected());
        config.setMaxRows((Integer)m_maxRowsSpinner.getValue());
        config.setEnablePaging(m_enablePagingCheckBox.isSelected());
        config.setIntialPageSize((Integer)m_initialPageSizeSpinner.getValue());
        config.setEnablePageSizeChange(m_enablePageSizeChangeCheckBox.isSelected());
        config.setAllowedPageSizes(getAllowedPageSizes());
        config.setPageSizeShowAll(m_enableShowAllCheckBox.isSelected());
        config.setEnableJumpToPage(m_enableJumpToPageCheckBox.isSelected());
        config.setDisplayRowColors(m_displayRowColorsCheckBox.isSelected());
        config.setDisplayRowIds(m_displayRowIdsCheckBox.isSelected());
        config.setDisplayColumnHeaders(m_displayColumnHeadersCheckBox.isSelected());
        config.setDisplayRowIndex(m_displayRowIndexCheckBox.isSelected());
        config.setDisplayFullscreenButton(m_displayFullscreenButtonCheckBox.isSelected());
        config.setTitle(m_titleField.getText());
        config.setSubtitle(m_subtitleField.getText());
        DataColumnSpecFilterConfiguration filterConfig = new DataColumnSpecFilterConfiguration(PagedTableViewConfig.CFG_COLUMN_FILTER);
        m_columnFilterPanel.saveConfiguration(filterConfig);
        config.setColumnFilterConfig(filterConfig);
        config.setEnableSelection(m_enableSelectionCheckbox.isSelected());
        config.setSelectionColumnName(m_selectionColumnNameField.getText());
        config.setEnableHideUnselected(m_enableHideUnselectedCheckbox.isSelected());
        config.setPublishSelection(m_publishSelectionCheckBox.isSelected());
        config.setSubscribeSelection(m_subscribeSelectionCheckBox.isSelected());
        config.setEnableSorting(m_enableSortingCheckBox.isSelected());
        config.setEnableClearSortButton(m_enableClearSortButtonCheckBox.isSelected());
        config.setEnableSearching(m_enableSearchCheckbox.isSelected());
        config.setEnableColumnSearching(m_enableColumnSearchCheckbox.isSelected());
        config.setPublishFilter(m_publishFilterCheckBox.isSelected());
        config.setSubscribeFilter(m_subscribeFilterCheckBox.isSelected());
        config.setGlobalDateTimeLocale(PREDEFINED_DATE_TIME_LOCALES.inverse().get(
            ((SettingsModelString)m_globalDateTimeLocaleChooser.getModel()).getStringValue())
        );
        config.setGlobalDateTimeFormat(((SettingsModelString)m_globalDateTimeFormatChooser.getModel()).getStringValue());
        config.setGlobalLocalDateFormat(((SettingsModelString)m_globalLocalDateFormatChooser.getModel()).getStringValue());
        config.setGlobalLocalDateTimeFormat(((SettingsModelString)m_globalLocalDateTimeFormatChooser.getModel()).getStringValue());
        config.setGlobalLocalTimeFormat(((SettingsModelString)m_globalLocalTimeFormatChooser.getModel()).getStringValue());
        config.setGlobalZonedDateTimeFormat(((SettingsModelString)m_globalZonedDateTimeFormatChooser.getModel()).getStringValue());
        config.setEnableGlobalNumberFormat(m_enableGlobalNumberFormatCheckbox.isSelected());
        config.setGlobalNumberFormatDecimals((Integer)m_globalNumberFormatDecimalSpinner.getValue());
        config.saveSettings(settings);
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

    /**
     * @return
     */
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
        m_selectionColumnNameField.setEnabled(enable);
        m_enableHideUnselectedCheckbox.setEnabled(enable);
        m_publishSelectionCheckBox.setEnabled(enable);
        m_subscribeSelectionCheckBox.setEnabled(enable);
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

    /**
     * @return a list of predefined formats for use in a date format with moment.js
     */
    public static LinkedHashSet<String> createPredefinedDateTimeFormats() {
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
        // check also the StringHistory....
        String[] userFormats = StringHistory.getInstance(DATE_TIME_FORMAT_HISTORY_KEY).getHistory();
        for (String userFormat : userFormats) {
            formats.add(userFormat);
        }
        return formats;
    }

    /**
     * @return a list of predefined formats for use in a date format with moment.js
     */
    private static LinkedHashSet<String> createPredefinedZonedDateTimeFormats() {
        LinkedHashSet<String> formats = new LinkedHashSet<String>();
        // TODO: time-zone aware formats
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

        // check also the StringHistory....
        String[] userFormats = StringHistory.getInstance(ZONED_DATE_TIME_FORMAT_HISTORY_KEY).getHistory();
        for (String userFormat : userFormats) {
            formats.add(userFormat);
        }
        return formats;    }

    /**
     * @return a list of predefined formats for use in a date format with moment.js
     */
    private static LinkedHashSet<String> createPredefinedLocalTimeFormats() {
        LinkedHashSet<String> formats = new LinkedHashSet<String>();
        formats.add("HH:mm:ss");
        formats.add("h:mm A");
        formats.add("h:mm:ss A");
        formats.add("HH:mm:ss.SSS");
        // check also the StringHistory....
        String[] userFormats = StringHistory.getInstance(TIME_FORMAT_HISTORY_KEY).getHistory();
        for (String userFormat : userFormats) {
            formats.add(userFormat);
        }
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
        // check also the StringHistory....
        String[] userFormats = StringHistory.getInstance(DATE_TIME_FORMAT_HISTORY_KEY).getHistory();
        for (String userFormat : userFormats) {
            formats.add(userFormat);
        }
        return formats;
    }

    /**
     * @return a list of predefined formats for use in a date format with moment.js
     */
    private static LinkedHashSet<String> createPredefinedLocalDateFormats() {
        LinkedHashSet<String> formats = new LinkedHashSet<String>();
        formats.add("YYYY-MM-DD");
        formats.add("M/D/YY");
        formats.add("MMM D, YYYY");
        formats.add("MMMM D, YYYY");
        formats.add("dddd, MMM D, YYYY");
        // check also the StringHistory....
        String[] userFormats = StringHistory.getInstance(DATE_FORMAT_HISTORY_KEY).getHistory();
        for (String userFormat : userFormats) {
            formats.add(userFormat);
        }
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
}
