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
 *   Apr 20, 2016 (albrecht): created
 */
package org.knime.js.base.node.viz.tableeditor;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.IntValue;
import org.knime.core.data.LongValue;
import org.knime.core.data.NominalValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.util.filter.InputFilter;
import org.knime.core.node.util.filter.column.DataColumnSpecFilterConfiguration;
import org.knime.js.core.components.datetime.SettingsModelDateTimeOptions;

/**
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
public class TableEditorViewConfig {

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

    final static String CFG_ENABLE_PAGE_SIZE_CHANGE = "enablePageSizeChange";
    private final static boolean DEFAULT_ENABLE_PAGE_SIZE_CHANGE = true;
    private boolean m_enablePageSizeChange = DEFAULT_ENABLE_PAGE_SIZE_CHANGE;

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

    final static String CFG_DISPLAY_FULLSCREEN_BUTTON = "displayFullscreenButton";
    final static boolean DEFAULT_DISPLAY_FULLSCREEN_BUTTON = true;
    private boolean m_displayFullscreenButton = DEFAULT_DISPLAY_FULLSCREEN_BUTTON;

    final static String CFG_FIXED_HEADERS = "fixedHeaders";
    private final static boolean DEFAULT_FIXED_HEADERS = false;
    private boolean m_fixedHeaders = DEFAULT_FIXED_HEADERS;

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
    final static boolean DEFAULT_ENABLE_SELECTION = false;
    private boolean m_enableSelection = DEFAULT_ENABLE_SELECTION;

    final static String CFG_SINGLE_SELECTION = "singleSelection";
    final static boolean DEFAULT_SINGLE_SELECTION = false;
    private boolean m_singleSelection = DEFAULT_SINGLE_SELECTION;

    final static String CFG_PUBLISH_SELECTION = "publishSelection";
    final static boolean DEFAULT_PUBLISH_SELECTION = true;
    private boolean m_publishSelection = DEFAULT_PUBLISH_SELECTION;

    final static String CFG_SUBSCRIBE_SELECTION = "subscribeSelection";
    final static boolean DEFAULT_SUBSCRIBE_SELECTION = true;
    private boolean m_subscribeSelection = DEFAULT_SUBSCRIBE_SELECTION;

    final static String CFG_ENABLE_CLEAR_SELECTION_BUTTON = "enableClearSelectionButton";
    final static boolean DEFAULT_ENABLE_CLEAR_SELECTION_BUTTON = true;
    private boolean m_enableClearSelectionButton = DEFAULT_ENABLE_CLEAR_SELECTION_BUTTON;

    final static String CFG_ENABLE_SEARCHING = "enableSearching";
    private final static boolean DEFAULT_ENABLE_SEARCHING = true;
    private boolean m_enableSearching = DEFAULT_ENABLE_SEARCHING;

    final static String CFG_ENABLE_COLUMN_SEARCHING = "enableColumnSearching";
    private final static boolean DEFAULT_ENABLE_COLUMN_SEARCHING = false;
    private boolean m_enableColumnSearching = DEFAULT_ENABLE_COLUMN_SEARCHING;

    final static String CFG_ENABLE_HIDE_UNSELECTED = "enableHideUnselected";
    final static boolean DEFAULT_ENABLE_HIDE_UNSELECTED = true;
    private boolean m_enableHideUnselected = DEFAULT_ENABLE_HIDE_UNSELECTED;

    final static String CFG_HIDE_UNSELECTED = "hideUnselected";
    final static boolean DEFAULT_HIDE_UNSELECTED = false;
    private boolean m_hideUnselected = DEFAULT_HIDE_UNSELECTED;

    final static String CFG_ENABLE_SORTING = "enableSorting";
    private final static boolean DEFAULT_ENABLE_SORTING = true;
    private boolean m_enableSorting = DEFAULT_ENABLE_SORTING;

    final static String CFG_PUBLISH_FILTER = "publishFilter";
    final static boolean DEFAULT_PUBLISH_FILTER = true;
    private boolean m_publishFilter = DEFAULT_PUBLISH_FILTER;

    final static String CFG_SUBSCRIBE_FILTER = "subscribeFilter";
    final static boolean DEFAULT_SUBSCRIBE_FILTER = true;
    private boolean m_subscribeFilter = DEFAULT_SUBSCRIBE_FILTER;

    final static String CFG_SELECTION_COLUMN_NAME = "selectionColumnName";
    final static String DEFAULT_SELECTION_COLUMN_NAME = "Selected (Paged Table View)";
    private String m_selectionColumnName = DEFAULT_SELECTION_COLUMN_NAME;

    final static String CFG_ENABLE_CLEAR_SORT_BUTTON = "enableClearSortButton";
    private final static boolean DEFAULT_ENABLE_CLEAR_SORT_BUTTON = false;
    private boolean m_enableClearSortButton = DEFAULT_ENABLE_CLEAR_SORT_BUTTON;

    final static String CFG_DATE_TIME_FORMATS = "dateTimeFormats";
    private SettingsModelDateTimeOptions m_dateTimeFormats = new SettingsModelDateTimeOptions(CFG_DATE_TIME_FORMATS);

    final static String CFG_GLOBAL_DATE_TIME_FORMAT = "globalDateFormat";

    final static String CFG_ENABLE_GLOBAL_NUMBER_FORMAT = "enableGlobalNumberFormat";
    private final static boolean DEFAULT_ENABLE_GLOBAL_NUMBER_FORMAT = false;
    private boolean m_enableGlobalNumberFormat = DEFAULT_ENABLE_GLOBAL_NUMBER_FORMAT;

    final static String CFG_GLOBAL_NUMBER_FORMAT_DECIMALS = "globalNumberFormatDecimals";
    private final static int DEFAULT_GLOBAL_NUMBER_FORMAT_DECIMALS = 2;
    private int m_globalNumberFormatDecimals = DEFAULT_GLOBAL_NUMBER_FORMAT_DECIMALS;

    final static String CFG_DISPLAY_MISSING_VALUE_AS_QUESTION_MARK = "displayMissingValueAsQuestionMark";
    final static boolean DEFAULT_DISPLAY_MISSING_VALUE_AS_QUESTION_MARK = true;
    private boolean m_displayMissingValueAsQuestionMark = DEFAULT_DISPLAY_MISSING_VALUE_AS_QUESTION_MARK;

    // Editor settings

    final static String CFG_EDITABLE_COLUMNS_FILTER = "editableColumnsFilter";
    final static InputFilter<DataColumnSpec> EDIT_FILTER = new InputFilter<DataColumnSpec>() {

        @Override
        public boolean include(final DataColumnSpec spec) {
            DataType type = spec.getType();
            return type.isCompatible(NominalValue.class) || type.isCompatible(IntValue.class) || type.isCompatible(LongValue.class) || type.isCompatible(DoubleValue.class);
        }
    };
    private DataColumnSpecFilterConfiguration m_editableColumnsFilterConfig =
        new DataColumnSpecFilterConfiguration(CFG_EDITABLE_COLUMNS_FILTER, EDIT_FILTER);

    static final String CFG_EDITOR_CHANGES = "editorChanges";
    private TableEditorChangesSettingsModel m_editorChanges = new TableEditorChangesSettingsModel(CFG_EDITOR_CHANGES);

    static final String CFG_TABLE_HASH = "tableHash";
    private String m_tableHash = null;


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
     * @return the enablePageSizeChange
     */
    public boolean getEnablePageSizeChange() {
        return m_enablePageSizeChange;
    }

    /**
     * @param enablePageSizeChange the enablePageSizeChange to set
     */
    public void setEnablePageSizeChange(final boolean enablePageSizeChange) {
        m_enablePageSizeChange = enablePageSizeChange;
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
     * @return the displayFullscreenButton
     */
    public boolean getDisplayFullscreenButton() {
        return m_displayFullscreenButton;
    }

    /**
     * @param displayFullscreenButton the displayFullscreenButton to set
     */
    public void setDisplayFullscreenButton(final boolean displayFullscreenButton) {
        m_displayFullscreenButton = displayFullscreenButton;
    }

    /**
     * @return the fixedHeaders
     */
    public boolean getFixedHeaders() {
        return m_fixedHeaders;
    }

    /**
     * @param fixedHeaders the fixedHeaders to set
     */
    public void setFixedHeaders(final boolean fixedHeaders) {
        m_fixedHeaders = fixedHeaders;
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
     * @return the singleSelection
     */
    public boolean getSingleSelection() {
        return m_singleSelection;
    }

    /**
     * @param singleSelection the singleSelection to set
     */
    public void setSingleSelection(final boolean singleSelection) {
        m_singleSelection = singleSelection;
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
     * @return the enableHideUnselected
     */
    public boolean getEnableHideUnselected() {
        return m_enableHideUnselected;
    }

    /**
     * @param enableHideUnselected the enableHideUnselected to set
     */
    public void setEnableHideUnselected(final boolean enableHideUnselected) {
        m_enableHideUnselected = enableHideUnselected;
    }

    /**
     * @return if hideUnselected
     */
    public boolean getHideUnselected() {
        return m_hideUnselected;
    }

    /**
     * @param hideUnselected the hideUnselected to set
     */
    public void setHideUnselected(final boolean hideUnselected) {
        m_hideUnselected = hideUnselected;
    }

    /**
     * @return the publishSelection
     */
    public boolean getPublishSelection() {
        return m_publishSelection;
    }

    /**
     * @param publishSelection the publishSelection to set
     */
    public void setPublishSelection(final boolean publishSelection) {
        m_publishSelection = publishSelection;
    }

    /**
     * @return the subscribeSelection
     */
    public boolean getSubscribeSelection() {
        return m_subscribeSelection;
    }

    /**
     * @param subscribeSelection the subscribeSelection to set
     */
    public void setSubscribeSelection(final boolean subscribeSelection) {
        m_subscribeSelection = subscribeSelection;
    }

    /**
     * @return the enableClearSelectionButton
     */
    public boolean getEnableClearSelectionButton() {
        return m_enableClearSelectionButton;
    }

    /**
     * @param enableClearSelectionButton the enableClearSelectionButton to set
     */
    public void setEnableClearSelectionButton(final boolean enableClearSelectionButton) {
        m_enableClearSelectionButton = enableClearSelectionButton;
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
     * @return the publishFilter
     */
    public boolean getPublishFilter() {
        return m_publishFilter;
    }

    /**
     * @param publishFilter the publishFilter to set
     */
    public void setPublishFilter(final boolean publishFilter) {
        m_publishFilter = publishFilter;
    }

    /**
     * @return the subscribeFilter
     */
    public boolean getSubscribeFilter() {
        return m_subscribeFilter;
    }

    /**
     * @param subscribeFilter the subscribeFilter to set
     */
    public void setSubscribeFilter(final boolean subscribeFilter) {
        m_subscribeFilter = subscribeFilter;
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
     * @return the enableClearSortButton
     */
    public boolean getEnableClearSortButton() {
        return m_enableClearSortButton;
    }

    /**
     * @param enableClearSortButton the enableClearSortButton to set
     */
    public void setEnableClearSortButton(final boolean enableClearSortButton) {
        m_enableClearSortButton = enableClearSortButton;
    }

    /**
     * @return the dateTimeFormats
     */
    public SettingsModelDateTimeOptions getDateTimeFormats() {
        return m_dateTimeFormats;
    }

    /**
     * @param dateTimeFormats the dateTimeFormats to set
     */
    public void setDateTimeFormats(final SettingsModelDateTimeOptions dateTimeFormats) {
        m_dateTimeFormats = dateTimeFormats;
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

    /**
     * @return the displayMissingValueAsQuestionMark
     */
    public boolean getDisplayMissingValueAsQuestionMark() {
        return m_displayMissingValueAsQuestionMark;
    }

    /**
     * @param displayMissingValueAsQuestionMark the displayMissingValueAsQuestionMark to set
     */
    public void setDisplayMissingValueAsQuestionMark(final boolean displayMissingValueAsQuestionMark) {
        m_displayMissingValueAsQuestionMark = displayMissingValueAsQuestionMark;
    }

    /**
     * @return the editableColumnFilterConfig
     */
    public DataColumnSpecFilterConfiguration getEditableColumnFilterConfig() {
        return m_editableColumnsFilterConfig;
    }

    /**
     * @param editableColumnFilterConfig the editableColumnFilterConfig to set
     */
    public void setEditableColumnFilterConfig(final DataColumnSpecFilterConfiguration editableColumnFilterConfig) {
        m_editableColumnsFilterConfig = editableColumnFilterConfig;
    }

    /**
     * @return the editorChanges
     */
    public TableEditorChangesSettingsModel getEditorChanges() {
        return m_editorChanges;
    }

    /**
     * @param editorChanges the editorChanges to set
     */
    public void setEditorChanges(final TableEditorChangesSettingsModel editorChanges) {
        m_editorChanges = editorChanges;
    }

    /**
     * @return the tableHash
     */
    public String getTableHash() {
        return m_tableHash;
    }

    /**
     * @param tableHash the tableHash to set
     */
    public void setTableHash(final String tableHash) {
        m_tableHash = tableHash;
    }


    /** Saves current parameters to settings object.
     * @param settings To save to.
     */
    public void saveSettings(final NodeSettingsWO settings) {
        settings.addBoolean(CFG_HIDE_IN_WIZARD, m_hideInWizard);
        settings.addInt(CFG_MAX_ROWS, m_maxRows);
        settings.addBoolean(CFG_ENABLE_PAGING, m_enablePaging);
        settings.addInt(CFG_INITIAL_PAGE_SIZE, m_initialPageSize);
        settings.addBoolean(CFG_ENABLE_PAGE_SIZE_CHANGE, m_enablePageSizeChange);
        settings.addIntArray(CFG_PAGE_SIZES, m_allowedPageSizes);
        settings.addBoolean(CFG_PAGE_SIZE_SHOW_ALL, m_pageSizeShowAll);
        settings.addBoolean(CFG_ENABLE_JUMP_TO_PAGE, m_enableJumpToPage);
        settings.addBoolean(CFG_DISPLAY_ROW_COLORS, m_displayRowColors);
        settings.addBoolean(CFG_DISPLAY_ROW_IDS, m_displayRowIds);
        settings.addBoolean(CFG_DISPLAY_COLUMN_HEADERS, m_displayColumnHeaders);
        settings.addBoolean(CFG_DISPLAY_ROW_INDEX, m_displayRowIndex);
        settings.addBoolean(CFG_FIXED_HEADERS, m_fixedHeaders);
        settings.addString(CFG_TITLE, m_title);
        settings.addString(CFG_SUBTITLE, m_subtitle);
        m_columnFilterConfig.saveConfiguration(settings);
        settings.addBoolean(CFG_ENABLE_SELECTION, m_enableSelection);
        settings.addString(CFG_SELECTION_COLUMN_NAME, m_selectionColumnName);
        settings.addBoolean(CFG_ENABLE_SEARCHING, m_enableSearching);
        settings.addBoolean(CFG_ENABLE_COLUMN_SEARCHING, m_enableColumnSearching);
        settings.addBoolean(CFG_ENABLE_SORTING, m_enableSorting);
        settings.addBoolean(CFG_ENABLE_CLEAR_SORT_BUTTON, m_enableClearSortButton);
        settings.addBoolean(CFG_ENABLE_GLOBAL_NUMBER_FORMAT, m_enableGlobalNumberFormat);
        settings.addInt(CFG_GLOBAL_NUMBER_FORMAT_DECIMALS, m_globalNumberFormatDecimals);

        //added with 3.3
        settings.addBoolean(CFG_DISPLAY_FULLSCREEN_BUTTON, m_displayFullscreenButton);
        settings.addBoolean(CFG_ENABLE_HIDE_UNSELECTED, m_enableHideUnselected);
        settings.addBoolean(CFG_PUBLISH_SELECTION, m_publishSelection);
        settings.addBoolean(CFG_SUBSCRIBE_SELECTION, m_subscribeSelection);
        settings.addBoolean(CFG_PUBLISH_FILTER, m_publishFilter);
        settings.addBoolean(CFG_SUBSCRIBE_FILTER, m_subscribeFilter);

        //added with 3.4
        settings.addBoolean(CFG_DISPLAY_MISSING_VALUE_AS_QUESTION_MARK, m_displayMissingValueAsQuestionMark);
        m_dateTimeFormats.saveSettingsTo(settings);
        settings.addBoolean(CFG_HIDE_UNSELECTED, m_hideUnselected);

        //added with 3.5
        settings.addBoolean(CFG_SINGLE_SELECTION, m_singleSelection);
        settings.addBoolean(CFG_ENABLE_CLEAR_SELECTION_BUTTON, m_enableClearSelectionButton);

        // editor settings
        m_editableColumnsFilterConfig.saveConfiguration(settings);
        m_editorChanges.setConfigName(TableEditorViewConfig.CFG_EDITOR_CHANGES);  // assign the config name in case it has not been assigned by using serialization constructor
        m_editorChanges.saveSettingsTo(settings);
        settings.addString(CFG_TABLE_HASH, m_tableHash);
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
        m_enablePageSizeChange = settings.getBoolean(CFG_ENABLE_PAGE_SIZE_CHANGE);
        m_allowedPageSizes = settings.getIntArray(CFG_PAGE_SIZES);
        m_pageSizeShowAll = settings.getBoolean(CFG_PAGE_SIZE_SHOW_ALL);
        m_enableJumpToPage = settings.getBoolean(CFG_ENABLE_JUMP_TO_PAGE);
        m_displayRowColors = settings.getBoolean(CFG_DISPLAY_ROW_COLORS);
        m_displayRowIds = settings.getBoolean(CFG_DISPLAY_ROW_IDS);
        m_displayColumnHeaders = settings.getBoolean(CFG_DISPLAY_COLUMN_HEADERS);
        m_displayRowIndex = settings.getBoolean(CFG_DISPLAY_ROW_INDEX);
        m_fixedHeaders = settings.getBoolean(CFG_FIXED_HEADERS);
        m_title = settings.getString(CFG_TITLE);
        m_subtitle = settings.getString(CFG_SUBTITLE);
        m_columnFilterConfig.loadConfigurationInModel(settings);
        m_enableSelection = settings.getBoolean(CFG_ENABLE_SELECTION);
        m_selectionColumnName = settings.getString(CFG_SELECTION_COLUMN_NAME);
        m_enableSearching = settings.getBoolean(CFG_ENABLE_SEARCHING);
        m_enableColumnSearching = settings.getBoolean(CFG_ENABLE_COLUMN_SEARCHING);
        m_enableSorting = settings.getBoolean(CFG_ENABLE_SORTING);
        m_enableClearSortButton = settings.getBoolean(CFG_ENABLE_CLEAR_SORT_BUTTON);
        m_enableGlobalNumberFormat = settings.getBoolean(CFG_ENABLE_GLOBAL_NUMBER_FORMAT);
        m_globalNumberFormatDecimals = settings.getInt(CFG_GLOBAL_NUMBER_FORMAT_DECIMALS);

        //added with 3.3
        m_displayFullscreenButton = settings.getBoolean(CFG_DISPLAY_FULLSCREEN_BUTTON, DEFAULT_DISPLAY_FULLSCREEN_BUTTON);
        m_enableHideUnselected = settings.getBoolean(CFG_ENABLE_HIDE_UNSELECTED, DEFAULT_ENABLE_HIDE_UNSELECTED);
        m_publishSelection = settings.getBoolean(CFG_PUBLISH_SELECTION, DEFAULT_PUBLISH_SELECTION);
        m_subscribeSelection = settings.getBoolean(CFG_SUBSCRIBE_SELECTION, DEFAULT_SUBSCRIBE_SELECTION);
        m_publishFilter = settings.getBoolean(CFG_PUBLISH_FILTER, DEFAULT_PUBLISH_FILTER);
        m_subscribeFilter = settings.getBoolean(CFG_SUBSCRIBE_FILTER, DEFAULT_SUBSCRIBE_FILTER);

        //added with 3.4
        m_displayMissingValueAsQuestionMark = settings.getBoolean(CFG_DISPLAY_MISSING_VALUE_AS_QUESTION_MARK, DEFAULT_DISPLAY_MISSING_VALUE_AS_QUESTION_MARK);

        if (settings.containsKey(CFG_DATE_TIME_FORMATS)) {
            m_dateTimeFormats.loadSettingsFrom(settings);
        } else {
            String legacyDateTimeFormat = settings.getString(CFG_GLOBAL_DATE_TIME_FORMAT);
            m_dateTimeFormats.getGlobalDateTimeFormatModel().setStringValue(legacyDateTimeFormat);
        }
        m_hideUnselected = settings.getBoolean(CFG_HIDE_UNSELECTED, DEFAULT_HIDE_UNSELECTED);

        //added with 3.5
        m_singleSelection = settings.getBoolean(CFG_SINGLE_SELECTION, DEFAULT_SINGLE_SELECTION);
        m_enableClearSelectionButton = settings.getBoolean(CFG_ENABLE_CLEAR_SELECTION_BUTTON, DEFAULT_ENABLE_CLEAR_SELECTION_BUTTON);

        // editor settings
        m_editableColumnsFilterConfig.loadConfigurationInModel(settings);
        m_editorChanges.loadSettingsFrom(settings);
        m_tableHash = settings.getString(CFG_TABLE_HASH);
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
        m_enablePageSizeChange = settings.getBoolean(CFG_ENABLE_PAGE_SIZE_CHANGE, DEFAULT_ENABLE_PAGE_SIZE_CHANGE);
        m_allowedPageSizes = settings.getIntArray(CFG_PAGE_SIZES, DEFAULT_PAGE_SIZES);
        m_pageSizeShowAll = settings.getBoolean(CFG_PAGE_SIZE_SHOW_ALL, DEFAULT_PAGE_SIZE_SHOW_ALL);
        m_enableJumpToPage = settings.getBoolean(CFG_ENABLE_JUMP_TO_PAGE, DEFAULT_ENABLE_JUMP_TO_PAGE);
        m_displayRowColors = settings.getBoolean(CFG_DISPLAY_ROW_COLORS, DEFAULT_DISPLAY_ROW_COLORS);
        m_displayRowIds = settings.getBoolean(CFG_DISPLAY_ROW_IDS, DEFAULT_DISPLAY_ROW_IDS);
        m_displayColumnHeaders = settings.getBoolean(CFG_DISPLAY_COLUMN_HEADERS, DEFAULT_DISPLAY_COLUMN_HEADERS);
        m_displayRowIndex = settings.getBoolean(CFG_DISPLAY_ROW_INDEX, DEFAULT_DISPLAY_ROW_INDEX);
        m_fixedHeaders = settings.getBoolean(CFG_FIXED_HEADERS, DEFAULT_FIXED_HEADERS);
        m_title = settings.getString(CFG_TITLE, DEFAULT_TITLE);
        m_subtitle = settings.getString(CFG_SUBTITLE, DEFAULT_SUBTITLE);
        m_columnFilterConfig.loadConfigurationInDialog(settings, spec);
        m_enableSelection = settings.getBoolean(CFG_ENABLE_SELECTION, DEFAULT_ENABLE_SELECTION);
        m_selectionColumnName = settings.getString(CFG_SELECTION_COLUMN_NAME, DEFAULT_SELECTION_COLUMN_NAME);
        m_enableSearching = settings.getBoolean(CFG_ENABLE_SEARCHING, DEFAULT_ENABLE_SEARCHING);
        m_enableColumnSearching = settings.getBoolean(CFG_ENABLE_COLUMN_SEARCHING, DEFAULT_ENABLE_COLUMN_SEARCHING);
        m_enableSorting = settings.getBoolean(CFG_ENABLE_SORTING, DEFAULT_ENABLE_SORTING);
        m_enableClearSortButton = settings.getBoolean(CFG_ENABLE_CLEAR_SORT_BUTTON, DEFAULT_ENABLE_CLEAR_SORT_BUTTON);
        m_enableGlobalNumberFormat = settings.getBoolean(CFG_ENABLE_GLOBAL_NUMBER_FORMAT, DEFAULT_ENABLE_GLOBAL_NUMBER_FORMAT);
        m_globalNumberFormatDecimals = settings.getInt(CFG_GLOBAL_NUMBER_FORMAT_DECIMALS, DEFAULT_GLOBAL_NUMBER_FORMAT_DECIMALS);

        //added with 3.3
        m_displayFullscreenButton = settings.getBoolean(CFG_DISPLAY_FULLSCREEN_BUTTON, DEFAULT_DISPLAY_FULLSCREEN_BUTTON);
        m_enableHideUnselected = settings.getBoolean(CFG_ENABLE_HIDE_UNSELECTED, DEFAULT_ENABLE_HIDE_UNSELECTED);
        m_publishSelection = settings.getBoolean(CFG_PUBLISH_SELECTION, DEFAULT_PUBLISH_SELECTION);
        m_subscribeSelection = settings.getBoolean(CFG_SUBSCRIBE_SELECTION, DEFAULT_SUBSCRIBE_SELECTION);
        m_publishFilter = settings.getBoolean(CFG_PUBLISH_FILTER, DEFAULT_PUBLISH_FILTER);
        m_subscribeFilter = settings.getBoolean(CFG_SUBSCRIBE_FILTER, DEFAULT_SUBSCRIBE_FILTER);

        //added with 3.4
        m_displayMissingValueAsQuestionMark = settings.getBoolean(CFG_DISPLAY_MISSING_VALUE_AS_QUESTION_MARK, DEFAULT_DISPLAY_MISSING_VALUE_AS_QUESTION_MARK);
        if (settings.containsKey(CFG_DATE_TIME_FORMATS)) {
            try {
                m_dateTimeFormats.loadSettingsFrom(settings);
            } catch (InvalidSettingsException e) {
                // return default
            }
        } else {
            String legacyDateTimeFormat = settings.getString(CFG_GLOBAL_DATE_TIME_FORMAT, null);
            if (legacyDateTimeFormat != null) {
                m_dateTimeFormats.getGlobalDateTimeFormatModel().setStringValue(legacyDateTimeFormat);
            }
        }
        m_hideUnselected = settings.getBoolean(CFG_HIDE_UNSELECTED, DEFAULT_HIDE_UNSELECTED);

        //added with 3.5
        m_singleSelection = settings.getBoolean(CFG_SINGLE_SELECTION, DEFAULT_SINGLE_SELECTION);
        m_enableClearSelectionButton = settings.getBoolean(CFG_ENABLE_CLEAR_SELECTION_BUTTON, DEFAULT_ENABLE_CLEAR_SELECTION_BUTTON);

        // editor settings
        m_editableColumnsFilterConfig.loadConfigurationInDialog(settings, spec);
        try {
            m_editorChanges.loadSettingsFrom(settings);
        } catch (InvalidSettingsException e) {
            // return default
        }
        m_tableHash = settings.getString(CFG_TABLE_HASH, null);
    }

}
