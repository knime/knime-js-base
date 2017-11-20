/*
 * ------------------------------------------------------------------------
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
 * ------------------------------------------------------------------------
 *
 * History
 *   14.04.2014 (Christian Albrecht, KNIME AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.viz.tableeditor;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.core.JSONDataTable;
import org.knime.js.core.JSONViewContent;
import org.knime.js.core.components.datetime.SettingsModelDateTimeOptions.JSONDateTimeOptions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class TableEditorViewRepresentation extends JSONViewContent {

    private JSONDataTable m_table;
    private boolean m_enablePaging;
    private int m_initialPageSize;
    private boolean m_enablePageSizeChange;
    private int[] m_allowedPageSizes;
    private boolean m_pageSizeShowAll;
    private boolean m_enableJumpToPage;
    private boolean m_displayRowColors;
    private boolean m_displayRowIds;
    private boolean m_displayColumnHeaders;
    private boolean m_displayRowIndex;
    private boolean m_displayFullscreenButton;
    private boolean m_fixedHeaders;
    private String m_title;
    private String m_subtitle;
    private boolean m_enableSelection;
    private boolean m_singleSelection;
    private boolean m_enableSearching;
    private boolean m_enableColumnSearching;
    private boolean m_enableHideUnselected;
    private boolean m_enableClearSelectionButton;
    private boolean m_enableSorting;
    private boolean m_enableClearSortButton;
    private JSONDateTimeOptions m_dateTimeFormats;
    private boolean m_enableGlobalNumberFormat;
    private int m_globalNumberFormatDecimals;
    private boolean m_displayMissingValueAsQuestionMark;

    private static final String CFG_PUBLISH_FILTER_ID = "publishFilterId";
    private String m_publishFilterId;
    private static final String CFG_SUBSCRIPTION_FILTER_IDS = "subscriptionFilterIds";
    private String[] m_subscriptionFilterIds;

    // editor settings
    private static final String CFG_EDITABLE_COLUMNS = "editableColumns";
    private String[] m_editableColumns;

    /** Serialization constructor. Don't use. */
    public TableEditorViewRepresentation() { }

    /**
     * @param table
     */
    public TableEditorViewRepresentation(final JSONDataTable table) {
        setTable(table);
    }

    /**
     * @return The JSON data table.
     */
    @JsonProperty("table")
    public JSONDataTable getTable() {
        return m_table;
    }

    /**
     * @param table The table to set.
     */
    @JsonProperty("table")
    public void setTable(final JSONDataTable table) {
        m_table = table;
    }

    /**
     * @return true if pagination is enabled
     */
    public boolean getEnablePaging() {
        return m_enablePaging;
    }

    /**
     * @param enablePaging true if pagination is enabled
     */
    public void setEnablePaging(final boolean enablePaging) {
        m_enablePaging = enablePaging;
    }

    /**
     * @return the initialPageSize
     */
    public int getInitialPageSize() {
        return m_initialPageSize;
    }

    /**
     * @param initialPageSize the initialPageSize to set
     */
    public void setInitialPageSize(final int initialPageSize) {
        m_initialPageSize = initialPageSize;
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
    public JSONDateTimeOptions getDateTimeFormats() {
        return m_dateTimeFormats;
    }

    /**
     * @param dateTimeFormats the dateTimeFormats to set
     */
    public void setDateTimeFormats(final JSONDateTimeOptions dateTimeFormats) {
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
     * @return the publishFilterId
     */
    public String getPublishFilterId() {
        return m_publishFilterId;
    }

    /**
     * @param publishFilterId the publishFilterId to set
     */
    public void setPublishFilterId(final String publishFilterId) {
        m_publishFilterId = publishFilterId;
    }

    /**
     * @return the subscriptionFilterIds
     */
    public String[] getSubscriptionFilterIds() {
        return m_subscriptionFilterIds;
    }

    /**
     * @param subscriptionFilterIds the subscriptionFilterIds to set
     */
    public void setSubscriptionFilterIds(final String[] subscriptionFilterIds) {
        m_subscriptionFilterIds = subscriptionFilterIds;
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
     * @return the editableColumns
     */
    public String[] getEditableColumns() {
        return m_editableColumns;
    }

    /**
     * @param editableColumns the editableColumns to set
     */
    public void setEditableColumns(final String[] editableColumns) {
        m_editableColumns = editableColumns;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        // save everything but table
        settings.addBoolean(TableEditorViewConfig.CFG_ENABLE_PAGING, m_enablePaging);
        settings.addInt(TableEditorViewConfig.CFG_INITIAL_PAGE_SIZE, m_initialPageSize);
        settings.addBoolean(TableEditorViewConfig.CFG_ENABLE_PAGE_SIZE_CHANGE, m_enablePageSizeChange);
        settings.addIntArray(TableEditorViewConfig.CFG_PAGE_SIZES, m_allowedPageSizes);
        settings.addBoolean(TableEditorViewConfig.CFG_PAGE_SIZE_SHOW_ALL, m_pageSizeShowAll);
        settings.addBoolean(TableEditorViewConfig.CFG_ENABLE_JUMP_TO_PAGE, m_enableJumpToPage);
        settings.addBoolean(TableEditorViewConfig.CFG_DISPLAY_ROW_COLORS, m_displayRowColors);
        settings.addBoolean(TableEditorViewConfig.CFG_DISPLAY_ROW_IDS, m_displayRowIds);
        settings.addBoolean(TableEditorViewConfig.CFG_DISPLAY_COLUMN_HEADERS, m_displayColumnHeaders);
        settings.addBoolean(TableEditorViewConfig.CFG_DISPLAY_ROW_INDEX, m_displayRowIndex);
        settings.addBoolean(TableEditorViewConfig.CFG_FIXED_HEADERS, m_fixedHeaders);
        settings.addString(TableEditorViewConfig.CFG_TITLE, m_title);
        settings.addString(TableEditorViewConfig.CFG_SUBTITLE, m_subtitle);
        settings.addBoolean(TableEditorViewConfig.CFG_ENABLE_SELECTION, m_enableSelection);
        settings.addBoolean(TableEditorViewConfig.CFG_ENABLE_SEARCHING, m_enableSearching);
        settings.addBoolean(TableEditorViewConfig.CFG_ENABLE_COLUMN_SEARCHING, m_enableColumnSearching);
        settings.addBoolean(TableEditorViewConfig.CFG_ENABLE_SORTING, m_enableSorting);
        settings.addBoolean(TableEditorViewConfig.CFG_ENABLE_CLEAR_SORT_BUTTON, m_enableClearSortButton);
        settings.addBoolean(TableEditorViewConfig.CFG_ENABLE_GLOBAL_NUMBER_FORMAT, m_enableGlobalNumberFormat);
        settings.addInt(TableEditorViewConfig.CFG_GLOBAL_NUMBER_FORMAT_DECIMALS, m_globalNumberFormatDecimals);

        //added with 3.3
        settings.addBoolean(TableEditorViewConfig.CFG_ENABLE_HIDE_UNSELECTED, m_enableHideUnselected);
        settings.addBoolean(TableEditorViewConfig.CFG_DISPLAY_FULLSCREEN_BUTTON, m_displayFullscreenButton);
        settings.addString(CFG_PUBLISH_FILTER_ID, m_publishFilterId);
        settings.addStringArray(CFG_SUBSCRIPTION_FILTER_IDS, m_subscriptionFilterIds);

        //added with 3.4
        settings.addBoolean(TableEditorViewConfig.CFG_DISPLAY_MISSING_VALUE_AS_QUESTION_MARK, m_displayMissingValueAsQuestionMark);
        m_dateTimeFormats.saveToNodeSettings(settings.addNodeSettings(TableEditorViewConfig.CFG_DATE_TIME_FORMATS));

        //added with 3.5
        settings.addBoolean(TableEditorViewConfig.CFG_SINGLE_SELECTION, m_singleSelection);
        settings.addBoolean(TableEditorViewConfig.CFG_ENABLE_CLEAR_SELECTION_BUTTON, m_enableClearSelectionButton);

        //editor settings
        settings.addStringArray(CFG_EDITABLE_COLUMNS, m_editableColumns);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        // load everything but table
        m_enablePaging = settings.getBoolean(TableEditorViewConfig.CFG_ENABLE_PAGING);
        m_initialPageSize = settings.getInt(TableEditorViewConfig.CFG_INITIAL_PAGE_SIZE);
        m_enablePageSizeChange = settings.getBoolean(TableEditorViewConfig.CFG_ENABLE_PAGE_SIZE_CHANGE);
        m_allowedPageSizes = settings.getIntArray(TableEditorViewConfig.CFG_PAGE_SIZES);
        m_pageSizeShowAll = settings.getBoolean(TableEditorViewConfig.CFG_PAGE_SIZE_SHOW_ALL);
        m_enableJumpToPage = settings.getBoolean(TableEditorViewConfig.CFG_ENABLE_JUMP_TO_PAGE);
        m_displayRowColors = settings.getBoolean(TableEditorViewConfig.CFG_DISPLAY_ROW_COLORS);
        m_displayRowIds = settings.getBoolean(TableEditorViewConfig.CFG_DISPLAY_ROW_IDS);
        m_displayColumnHeaders = settings.getBoolean(TableEditorViewConfig.CFG_DISPLAY_COLUMN_HEADERS);
        m_displayRowIndex = settings.getBoolean(TableEditorViewConfig.CFG_DISPLAY_ROW_INDEX);
        m_fixedHeaders = settings.getBoolean(TableEditorViewConfig.CFG_FIXED_HEADERS);
        m_title = settings.getString(TableEditorViewConfig.CFG_TITLE);
        m_subtitle = settings.getString(TableEditorViewConfig.CFG_SUBTITLE);
        m_enableSelection = settings.getBoolean(TableEditorViewConfig.CFG_ENABLE_SELECTION);
        m_enableSearching = settings.getBoolean(TableEditorViewConfig.CFG_ENABLE_SEARCHING);
        m_enableColumnSearching = settings.getBoolean(TableEditorViewConfig.CFG_ENABLE_COLUMN_SEARCHING);
        m_enableSorting = settings.getBoolean(TableEditorViewConfig.CFG_ENABLE_SORTING);
        m_enableClearSortButton = settings.getBoolean(TableEditorViewConfig.CFG_ENABLE_CLEAR_SORT_BUTTON);
        m_enableGlobalNumberFormat = settings.getBoolean(TableEditorViewConfig.CFG_ENABLE_GLOBAL_NUMBER_FORMAT);
        m_globalNumberFormatDecimals = settings.getInt(TableEditorViewConfig.CFG_GLOBAL_NUMBER_FORMAT_DECIMALS);

        //added with 3.3
        m_enableHideUnselected = settings.getBoolean(TableEditorViewConfig.CFG_ENABLE_HIDE_UNSELECTED, TableEditorViewConfig.DEFAULT_ENABLE_HIDE_UNSELECTED);
        m_displayFullscreenButton = settings.getBoolean(TableEditorViewConfig.CFG_DISPLAY_FULLSCREEN_BUTTON, TableEditorViewConfig.DEFAULT_DISPLAY_FULLSCREEN_BUTTON);
        m_publishFilterId = settings.getString(CFG_PUBLISH_FILTER_ID, null);
        m_subscriptionFilterIds = settings.getStringArray(CFG_SUBSCRIPTION_FILTER_IDS, (String[])null);

        //added with 3.4
        m_displayMissingValueAsQuestionMark = settings.getBoolean(TableEditorViewConfig.CFG_DISPLAY_MISSING_VALUE_AS_QUESTION_MARK, TableEditorViewConfig.DEFAULT_DISPLAY_MISSING_VALUE_AS_QUESTION_MARK);
        if (settings.containsKey(TableEditorViewConfig.CFG_DATE_TIME_FORMATS)) {
            m_dateTimeFormats = JSONDateTimeOptions.loadFromNodeSettings(settings.getNodeSettings(TableEditorViewConfig.CFG_DATE_TIME_FORMATS));
        }

        //added with 3.5
        m_singleSelection = settings.getBoolean(TableEditorViewConfig.CFG_SINGLE_SELECTION, TableEditorViewConfig.DEFAULT_SINGLE_SELECTION);
        m_enableClearSelectionButton = settings.getBoolean(TableEditorViewConfig.CFG_ENABLE_CLEAR_SELECTION_BUTTON, TableEditorViewConfig.DEFAULT_ENABLE_CLEAR_SELECTION_BUTTON);

        //editor settings
        m_editableColumns = settings.getStringArray(CFG_EDITABLE_COLUMNS, (String[])null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        TableEditorViewRepresentation other = (TableEditorViewRepresentation)obj;
        return new EqualsBuilder()
                .append(m_table, other.m_table)
                .append(m_enablePaging, other.m_enablePaging)
                .append(m_initialPageSize, other.m_initialPageSize)
                .append(m_enablePageSizeChange, other.m_enablePageSizeChange)
                .append(m_allowedPageSizes, other.m_allowedPageSizes)
                .append(m_pageSizeShowAll, other.m_pageSizeShowAll)
                .append(m_enableJumpToPage, other.m_enableJumpToPage)
                .append(m_displayRowColors, other.m_displayRowColors)
                .append(m_displayRowIds, other.m_displayRowIds)
                .append(m_displayColumnHeaders, other.m_displayColumnHeaders)
                .append(m_displayRowIndex, other.m_displayRowIndex)
                .append(m_displayFullscreenButton, other.m_displayFullscreenButton)
                .append(m_fixedHeaders, other.m_fixedHeaders)
                .append(m_title, other.m_title)
                .append(m_subtitle, other.m_subtitle)
                .append(m_enableSelection, other.m_enableSelection)
                .append(m_singleSelection, other.m_singleSelection)
                .append(m_enableClearSelectionButton, other.m_enableClearSelectionButton)
                .append(m_enableSearching, other.m_enableSearching)
                .append(m_enableColumnSearching, other.m_enableColumnSearching)
                .append(m_enableHideUnselected, other.m_enableHideUnselected)
                .append(m_enableSorting, other.m_enableSorting)
                .append(m_enableClearSortButton, other.m_enableClearSortButton)
                .append(m_dateTimeFormats, other.m_dateTimeFormats)
                .append(m_enableGlobalNumberFormat, other.m_enableGlobalNumberFormat)
                .append(m_globalNumberFormatDecimals, other.m_globalNumberFormatDecimals)
                .append(m_displayMissingValueAsQuestionMark, other.m_displayMissingValueAsQuestionMark)
                .append(m_publishFilterId, other.m_publishFilterId)
                .append(m_subscriptionFilterIds, other.m_subscriptionFilterIds)
                .append(m_editableColumns, other.m_editableColumns)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_table)
                .append(m_enablePaging)
                .append(m_initialPageSize)
                .append(m_enablePageSizeChange)
                .append(m_allowedPageSizes)
                .append(m_pageSizeShowAll)
                .append(m_enableJumpToPage)
                .append(m_displayRowColors)
                .append(m_displayRowIds)
                .append(m_displayColumnHeaders)
                .append(m_displayRowIndex)
                .append(m_displayFullscreenButton)
                .append(m_fixedHeaders)
                .append(m_title)
                .append(m_subtitle)
                .append(m_enableSelection)
                .append(m_singleSelection)
                .append(m_enableClearSelectionButton)
                .append(m_enableSearching)
                .append(m_enableColumnSearching)
                .append(m_enableHideUnselected)
                .append(m_enableSorting)
                .append(m_enableClearSortButton)
                .append(m_dateTimeFormats)
                .append(m_enableGlobalNumberFormat)
                .append(m_globalNumberFormatDecimals)
                .append(m_displayMissingValueAsQuestionMark)
                .append(m_publishFilterId)
                .append(m_subscriptionFilterIds)
                .append(m_editableColumns)
                .toHashCode();
    }

}
