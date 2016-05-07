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
 *   Apr 20, 2016 (albrecht): created
 */
package org.knime.js.base.node.viz.pagedTable;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.util.filter.column.DataColumnSpecFilterConfiguration;

/**
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
public class PagedTableViewConfig {

    final static String CFG_HIDE_IN_WIZARD = "hideInWizard";
    private final static boolean DEFAULT_HIDE_IN_WIZARD = false;
    private boolean m_hideInWizard = DEFAULT_HIDE_IN_WIZARD;

    final static String CFG_MAX_ROWS = "maxRows";
    private final static int DEFAULT_MAX_ROWS = 100000;
    private int m_maxRows = DEFAULT_MAX_ROWS;

    final static String CFG_ENABLE_PAGING = "enablePaging";
    private final static boolean DEFAULT_ENABLE_PAGING = true;
    private boolean m_enablePaging = DEFAULT_ENABLE_PAGING;

    final static String CFG_INITIAL_PAGE_SIZE = "initialPageSize";
    private final static int DEFAULT_INITIAL_PAGE_SIZE = 10;
    private int m_initialPageSize = DEFAULT_INITIAL_PAGE_SIZE;

    final static String CFG_PAGE_SIZES = "allowedPageSizes";
    private final static int[] DEFAULT_PAGE_SIZES = new int[]{10, 25, 50, 100};
    private int[] m_allowedPageSizes = DEFAULT_PAGE_SIZES;

    final static String CFG_PAGE_SIZE_SHOW_ALL = "enableShowAll";
    private final static boolean DEFAULT_PAGE_SIZE_SHOW_ALL = false;
    private boolean m_pageSizeShowAll = DEFAULT_PAGE_SIZE_SHOW_ALL;

    final static String CFG_ENABLE_JUMP_TO_PAGE = "enableJumpToPage";
    private final static boolean DEFAULT_ENABLE_JUMP_TO_PAGE = false;
    private boolean m_enableJumpToPage = DEFAULT_ENABLE_JUMP_TO_PAGE;

    final static String CFG_DISPLAY_ROW_COLORS = "displayRowColors";
    private final static boolean DEFAULT_DISPLAY_ROW_COLORS = true;
    private boolean m_displayRowColors = DEFAULT_DISPLAY_ROW_COLORS;

    final static String CFG_DISPLAY_ROW_IDS = "displayRowIDs";
    private final static boolean DEFAULT_DISPLAY_ROW_IDS = true;
    private boolean m_displayRowIds = DEFAULT_DISPLAY_ROW_IDS;

    final static String CFG_DISPLAY_COLUMN_HEADERS = "displayColumnHeaders";
    private final static boolean DEFAULT_DISPLAY_COLUMN_HEADERS = true;
    private boolean m_displayColumnHeaders = DEFAULT_DISPLAY_COLUMN_HEADERS;

    final static String CFG_DISPLAY_ROW_INDEX = "displayRowIndex";
    private final static boolean DEFAULT_DISPLAY_ROW_INDEX = false;
    private boolean m_displayRowIndex = DEFAULT_DISPLAY_ROW_INDEX;

    final static String CFG_TITLE = "title";
    private final static String DEFAULT_TITLE = "";
    private String m_title = DEFAULT_TITLE;

    final static String CFG_SUBTITLE = "subtitle";
    private final static String DEFAULT_SUBTITLE = "";
    private String m_subtitle = DEFAULT_SUBTITLE;

    final static String CFG_COLUMN_FILTER = "columnFilter";
    private DataColumnSpecFilterConfiguration m_columnFilterConfig =
        new DataColumnSpecFilterConfiguration(CFG_COLUMN_FILTER);

    final static String CFG_ENABLE_SELECTION = "enableSelection";
    private final static boolean DEFAULT_ENABLE_SELECTION = false;
    private boolean m_enableSelection = DEFAULT_ENABLE_SELECTION;

    final static String CFG_SELECTION_COLUMN_NAME = "selectionColumnName";
    final static String DEFAULT_SELECTION_COLUMN_NAME = "Selected (Paged Table View)";
    private String m_selectionColumnName = DEFAULT_SELECTION_COLUMN_NAME;

    final static String CFG_ENABLE_SEARCHING = "enableSearching";
    private final static boolean DEFAULT_ENABLE_SEARCHING = true;
    private boolean m_enableSearching = DEFAULT_ENABLE_SEARCHING;

    final static String CFG_ENABLE_COLUMN_SEARCHING = "enableColumnSearching";
    private final static boolean DEFAULT_ENABLE_COLUMN_SEARCHING = false;
    private boolean m_enableColumnSearching = DEFAULT_ENABLE_COLUMN_SEARCHING;

    final static String CFG_ENABLE_SORTING = "enableSorting";
    private final static boolean DEFAULT_ENABLE_SORTING = true;
    private boolean m_enableSorting = DEFAULT_ENABLE_SORTING;

    final static String CFG_GLOBAL_DATE_FORMAT = "globalDateFormat";
    final static String DEFAULT_GLOBAL_DATE_FORMAT = PagedTableViewNodeDialogPane.PREDEFINED_FORMATS.iterator().next();
    private String m_globalDateFormat = DEFAULT_GLOBAL_DATE_FORMAT;

    final static String CFG_ENABLE_GLOBAL_NUMBER_FORMAT = "enableGlobalNumberFormat";
    private final static boolean DEFAULT_ENABLE_GLOBAL_NUMBER_FORMAT = false;
    private boolean m_enableGlobalNumberFormat = DEFAULT_ENABLE_GLOBAL_NUMBER_FORMAT;

    final static String CFG_GLOBAL_NUMBER_FORMAT_DECIMALS = "globalNumberFormatDecimals";
    private final static int DEFAULT_GLOBAL_NUMBER_FORMAT_DECIMALS = 2;
    private int m_globalNumberFormatDecimals = DEFAULT_GLOBAL_NUMBER_FORMAT_DECIMALS;


    /**
     * @return the hideInWizard
     */
    public boolean getHideInWizard() {
        return m_hideInWizard;
    }

    /**
     * @param hideInWizard the hideInWizard to set
     */
    public void setHideInWizard(final boolean hideInWizard) {
        m_hideInWizard = hideInWizard;
    }

    /**
     * @return the maxRows
     */
    public int getMaxRows() {
        return m_maxRows;
    }

    /**
     * @param maxRows the maxRows to set
     */
    public void setMaxRows(final int maxRows) {
        m_maxRows = maxRows;
    }

    /**
     * @return the enablePaging
     */
    public boolean getEnablePaging() {
        return m_enablePaging;
    }

    /**
     * @param enablePaging the enablePaging to set
     */
    public void setEnablePaging(final boolean enablePaging) {
        m_enablePaging = enablePaging;
    }

    /**
     * @return the intialPageSize
     */
    public int getIntialPageSize() {
        return m_initialPageSize;
    }

    /**
     * @param intialPageSize the intialPageSize to set
     */
    public void setIntialPageSize(final int intialPageSize) {
        m_initialPageSize = intialPageSize;
    }

    /**
     * @return the allowedPageSizes
     */
    public int[] getAllowedPageSizes() {
        return m_allowedPageSizes;
    }

    /**
     * @param allowedPageSizes the allowedPageSizes to set
     */
    public void setAllowedPageSizes(final int[] allowedPageSizes) {
        m_allowedPageSizes = allowedPageSizes;
    }

    /**
     * @return the pageSizeShowAll
     */
    public boolean getPageSizeShowAll() {
        return m_pageSizeShowAll;
    }

    /**
     * @param pageSizeShowAll the pageSizeShowAll to set
     */
    public void setPageSizeShowAll(final boolean pageSizeShowAll) {
        m_pageSizeShowAll = pageSizeShowAll;
    }

    /**
     * @return the enableJumpToPage
     */
    public boolean getEnableJumpToPage() {
        return m_enableJumpToPage;
    }

    /**
     * @param enableJumpToPage the enableJumpToPage to set
     */
    public void setEnableJumpToPage(final boolean enableJumpToPage) {
        m_enableJumpToPage = enableJumpToPage;
    }

    /**
     * @return the displayRowColors
     */
    public boolean getDisplayRowColors() {
        return m_displayRowColors;
    }

    /**
     * @param displayRowColors the displayRowColors to set
     */
    public void setDisplayRowColors(final boolean displayRowColors) {
        m_displayRowColors = displayRowColors;
    }

    /**
     * @return the displayRowIds
     */
    public boolean getDisplayRowIds() {
        return m_displayRowIds;
    }

    /**
     * @param displayRowIds the displayRowIds to set
     */
    public void setDisplayRowIds(final boolean displayRowIds) {
        m_displayRowIds = displayRowIds;
    }

    /**
     * @return the displayColumnHeaders
     */
    public boolean getDisplayColumnHeaders() {
        return m_displayColumnHeaders;
    }

    /**
     * @param displayColumnHeaders the displayColumnHeaders to set
     */
    public void setDisplayColumnHeaders(final boolean displayColumnHeaders) {
        m_displayColumnHeaders = displayColumnHeaders;
    }

    /**
     * @return the displayRowIndex
     */
    public boolean getDisplayRowIndex() {
        return m_displayRowIndex;
    }

    /**
     * @param displayRowIndex the displayRowIndex to set
     */
    public void setDisplayRowIndex(final boolean displayRowIndex) {
        m_displayRowIndex = displayRowIndex;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return m_title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(final String title) {
        m_title = title;
    }

    /**
     * @return the subtitle
     */
    public String getSubtitle() {
        return m_subtitle;
    }

    /**
     * @param subtitle the subtitle to set
     */
    public void setSubtitle(final String subtitle) {
        m_subtitle = subtitle;
    }

    /**
     * @return the columnFilterConfig
     */
    public DataColumnSpecFilterConfiguration getColumnFilterConfig() {
        return m_columnFilterConfig;
    }

    /**
     * @param columnFilterConfig the columnFilterConfig to set
     */
    public void setColumnFilterConfig(final DataColumnSpecFilterConfiguration columnFilterConfig) {
        m_columnFilterConfig = columnFilterConfig;
    }

    /**
     * @return the enableSelection
     */
    public boolean getEnableSelection() {
        return m_enableSelection;
    }

    /**
     * @param enableSelection the enableSelection to set
     */
    public void setEnableSelection(final boolean enableSelection) {
        m_enableSelection = enableSelection;
    }

    /**
     * @return the selectionColumnName
     */
    public String getSelectionColumnName() {
        return m_selectionColumnName;
    }

    /**
     * @param selectionColumnName the selectionColumnName to set
     */
    public void setSelectionColumnName(final String selectionColumnName) {
        m_selectionColumnName = selectionColumnName;
    }

    /**
     * @return the enableSearching
     */
    public boolean getEnableSearching() {
        return m_enableSearching;
    }

    /**
     * @param enableSearching the enableSearching to set
     */
    public void setEnableSearching(final boolean enableSearching) {
        m_enableSearching = enableSearching;
    }

    /**
     * @return the enableColumnSearching
     */
    public boolean getEnableColumnSearching() {
        return m_enableColumnSearching;
    }

    /**
     * @param enableColumnSearching the enableColumnSearching to set
     */
    public void setEnableColumnSearching(final boolean enableColumnSearching) {
        m_enableColumnSearching = enableColumnSearching;
    }

    /**
     * @return the enableSorting
     */
    public boolean getEnableSorting() {
        return m_enableSorting;
    }

    /**
     * @param enableSorting the enableSorting to set
     */
    public void setEnableSorting(final boolean enableSorting) {
        m_enableSorting = enableSorting;
    }

    /**
     * @return the globalDateFormat
     */
    public String getGlobalDateFormat() {
        return m_globalDateFormat;
    }

    /**
     * @param globalDateFormat the globalDateFormat to set
     */
    public void setGlobalDateFormat(final String globalDateFormat) {
        m_globalDateFormat = globalDateFormat;
    }

    /**
     * @return the enableGlobalNumberFormat
     */
    public boolean getEnableGlobalNumberFormat() {
        return m_enableGlobalNumberFormat;
    }

    /**
     * @param enableGlobalNumberFormat the enableGlobalNumberFormat to set
     */
    public void setEnableGlobalNumberFormat(final boolean enableGlobalNumberFormat) {
        m_enableGlobalNumberFormat = enableGlobalNumberFormat;
    }

    /**
     * @return the globalNumberFormatDecimals
     */
    public int getGlobalNumberFormatDecimals() {
        return m_globalNumberFormatDecimals;
    }

    /**
     * @param globalNumberFormatDecimals the globalNumberFormatDecimals to set
     */
    public void setGlobalNumberFormatDecimals(final int globalNumberFormatDecimals) {
        m_globalNumberFormatDecimals = globalNumberFormatDecimals;
    }

    /** Saves current parameters to settings object.
     * @param settings To save to.
     */
    public void saveSettings(final NodeSettingsWO settings) {
        settings.addBoolean(CFG_HIDE_IN_WIZARD, m_hideInWizard);
        settings.addInt(CFG_MAX_ROWS, m_maxRows);
        settings.addBoolean(CFG_ENABLE_PAGING, m_enablePaging);
        settings.addInt(CFG_INITIAL_PAGE_SIZE, m_initialPageSize);
        settings.addIntArray(CFG_PAGE_SIZES, m_allowedPageSizes);
        settings.addBoolean(CFG_PAGE_SIZE_SHOW_ALL, m_pageSizeShowAll);
        settings.addBoolean(CFG_ENABLE_JUMP_TO_PAGE, m_enableJumpToPage);
        settings.addBoolean(CFG_DISPLAY_ROW_COLORS, m_displayRowColors);
        settings.addBoolean(CFG_DISPLAY_ROW_IDS, m_displayRowIds);
        settings.addBoolean(CFG_DISPLAY_COLUMN_HEADERS, m_displayColumnHeaders);
        settings.addBoolean(CFG_DISPLAY_ROW_INDEX, m_displayRowIndex);
        settings.addString(CFG_TITLE, m_title);
        settings.addString(CFG_SUBTITLE, m_subtitle);
        m_columnFilterConfig.saveConfiguration(settings);
        settings.addBoolean(CFG_ENABLE_SELECTION, m_enableSelection);
        settings.addString(CFG_SELECTION_COLUMN_NAME, m_selectionColumnName);
        settings.addBoolean(CFG_ENABLE_SEARCHING, m_enableSearching);
        settings.addBoolean(CFG_ENABLE_COLUMN_SEARCHING, m_enableColumnSearching);
        settings.addBoolean(CFG_ENABLE_SORTING, m_enableSorting);
        settings.addString(CFG_GLOBAL_DATE_FORMAT, m_globalDateFormat);
        settings.addBoolean(CFG_ENABLE_GLOBAL_NUMBER_FORMAT, m_enableGlobalNumberFormat);
        settings.addInt(CFG_GLOBAL_NUMBER_FORMAT_DECIMALS, m_globalNumberFormatDecimals);
    }

    /** Loads parameters in NodeModel.
     * @param settings To load from.
     * @throws InvalidSettingsException If incomplete or wrong.
     */
    public void loadSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_hideInWizard = settings.getBoolean(CFG_HIDE_IN_WIZARD);
        m_maxRows = settings.getInt(CFG_MAX_ROWS);
        m_enablePaging = settings.getBoolean(CFG_ENABLE_PAGING);
        m_initialPageSize = settings.getInt(CFG_INITIAL_PAGE_SIZE);
        m_allowedPageSizes = settings.getIntArray(CFG_PAGE_SIZES);
        m_pageSizeShowAll = settings.getBoolean(CFG_PAGE_SIZE_SHOW_ALL);
        m_enableJumpToPage = settings.getBoolean(CFG_ENABLE_JUMP_TO_PAGE);
        m_displayRowColors = settings.getBoolean(CFG_DISPLAY_ROW_COLORS);
        m_displayRowIds = settings.getBoolean(CFG_DISPLAY_ROW_IDS);
        m_displayColumnHeaders = settings.getBoolean(CFG_DISPLAY_COLUMN_HEADERS);
        m_displayRowIndex = settings.getBoolean(CFG_DISPLAY_ROW_INDEX);
        m_title = settings.getString(CFG_TITLE);
        m_subtitle = settings.getString(CFG_SUBTITLE);
        m_columnFilterConfig.loadConfigurationInModel(settings);
        m_enableSelection = settings.getBoolean(CFG_ENABLE_SELECTION);
        m_selectionColumnName = settings.getString(CFG_SELECTION_COLUMN_NAME);
        m_enableSearching = settings.getBoolean(CFG_ENABLE_SEARCHING);
        m_enableColumnSearching = settings.getBoolean(CFG_ENABLE_COLUMN_SEARCHING);
        m_enableSorting = settings.getBoolean(CFG_ENABLE_SORTING);
        m_globalDateFormat = settings.getString(CFG_GLOBAL_DATE_FORMAT);
        m_enableGlobalNumberFormat = settings.getBoolean(CFG_ENABLE_GLOBAL_NUMBER_FORMAT);
        m_globalNumberFormatDecimals = settings.getInt(CFG_GLOBAL_NUMBER_FORMAT_DECIMALS);
    }

    /** Loads parameters in Dialog.
     * @param settings To load from.
     * @param spec The spec from the incoming data table
     */
    public void loadSettingsForDialog(final NodeSettingsRO settings, final DataTableSpec spec) {
        m_hideInWizard = settings.getBoolean(CFG_HIDE_IN_WIZARD, DEFAULT_HIDE_IN_WIZARD);
        m_maxRows = settings.getInt(CFG_MAX_ROWS, DEFAULT_MAX_ROWS);
        m_enablePaging = settings.getBoolean(CFG_ENABLE_PAGING, DEFAULT_ENABLE_PAGING);
        m_initialPageSize = settings.getInt(CFG_INITIAL_PAGE_SIZE, DEFAULT_INITIAL_PAGE_SIZE);
        m_allowedPageSizes = settings.getIntArray(CFG_PAGE_SIZES, DEFAULT_PAGE_SIZES);
        m_pageSizeShowAll = settings.getBoolean(CFG_PAGE_SIZE_SHOW_ALL, DEFAULT_PAGE_SIZE_SHOW_ALL);
        m_enableJumpToPage = settings.getBoolean(CFG_ENABLE_JUMP_TO_PAGE, DEFAULT_ENABLE_JUMP_TO_PAGE);
        m_displayRowColors = settings.getBoolean(CFG_DISPLAY_ROW_COLORS, DEFAULT_DISPLAY_ROW_COLORS);
        m_displayRowIds = settings.getBoolean(CFG_DISPLAY_ROW_IDS, DEFAULT_DISPLAY_ROW_IDS);
        m_displayColumnHeaders = settings.getBoolean(CFG_DISPLAY_COLUMN_HEADERS, DEFAULT_DISPLAY_COLUMN_HEADERS);
        m_displayRowIndex = settings.getBoolean(CFG_DISPLAY_ROW_INDEX, DEFAULT_DISPLAY_ROW_INDEX);
        m_title = settings.getString(CFG_TITLE, DEFAULT_TITLE);
        m_subtitle = settings.getString(CFG_SUBTITLE, DEFAULT_SUBTITLE);
        m_columnFilterConfig.loadConfigurationInDialog(settings, spec);
        m_enableSelection = settings.getBoolean(CFG_ENABLE_SELECTION, DEFAULT_ENABLE_SELECTION);
        m_selectionColumnName = settings.getString(CFG_SELECTION_COLUMN_NAME, DEFAULT_SELECTION_COLUMN_NAME);
        m_enableSearching = settings.getBoolean(CFG_ENABLE_SEARCHING, DEFAULT_ENABLE_SEARCHING);
        m_enableColumnSearching = settings.getBoolean(CFG_ENABLE_COLUMN_SEARCHING, DEFAULT_ENABLE_COLUMN_SEARCHING);
        m_enableSorting = settings.getBoolean(CFG_ENABLE_SORTING, DEFAULT_ENABLE_SORTING);
        m_globalDateFormat = settings.getString(CFG_GLOBAL_DATE_FORMAT, DEFAULT_GLOBAL_DATE_FORMAT);
        m_enableGlobalNumberFormat = settings.getBoolean(CFG_ENABLE_GLOBAL_NUMBER_FORMAT, DEFAULT_ENABLE_GLOBAL_NUMBER_FORMAT);
        m_globalNumberFormatDecimals = settings.getInt(CFG_GLOBAL_NUMBER_FORMAT_DECIMALS, DEFAULT_GLOBAL_NUMBER_FORMAT_DECIMALS);
    }

}
