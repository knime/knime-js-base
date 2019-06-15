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
import org.knime.js.core.JSONViewContent;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class TableEditorViewValue extends JSONViewContent {


    private static final String CFG_SELECTION = "selection";
    private String[] m_selection;

    private static final String CFG_SELECT_ALL = "selectAll";
    private boolean m_selectAll;

    private static final String CFG_SELECT_ALL_INDETERMINATE = "selectAllIndeterminate";
    private static final boolean DEFAULT_SELECT_ALL_INDETERMINATE = false;
    private boolean m_selectAllIndeterminate;

    private static final String CFG_PAGE_SIZE = "pageSize";
    private int m_pageSize;

    private static final String CFG_CURRENT_PAGE = "currentPage";
    private int m_currentPage;

    private static final String CFG_FILTER_STRING = "filterString";
    private String m_filterString;

    private static final String CFG_COLUMN_FILTER_STRINGS = "columnFilterStrings";
    private String[] m_columnFilterStrings;

    private static final String CFG_CURRENT_ORDER = "currentOrder";
    private Object[][] m_currentOrder = new Object[0][];

    private static final String CFG_HIDE_UNSELECTED = "hideUnselected";
    private static final boolean DEFAULT_HIDE_UNSELECTED = false;
    private boolean m_hideUnselected;

    private boolean m_publishSelection;
    private boolean m_subscribeSelection;
    private boolean m_publishFilter;
    private boolean m_subscribeFilter;

    // editor
    private TableEditorChangesSettingsModel m_editorChanges = new TableEditorChangesSettingsModel(TableEditorViewConfig.CFG_EDITOR_CHANGES);

    private static final String CFG_TABLE_HASH = "tableHash";
    private String m_tableHash = null;

    /**
     * @return the selection
     */
    public String[] getSelection() {
        return m_selection;
    }

    /**
     * @param selection the selection to set
     */
    public void setSelection(final String[] selection) {
        m_selection = selection;
    }

    /**
     * @return the selectAll
     */
    public boolean getSelectAll() {
        return m_selectAll;
    }

    /**
     * @param selectAll the selectAll to set
     */
    public void setSelectAll(final boolean selectAll) {
        m_selectAll = selectAll;
    }

    /**
     * @return the selectAllIndeterminate
     */
    public boolean getSelectAllIndeterminate() {
        return m_selectAllIndeterminate;
    }

    /**
     * @param selectAllIndeterminate the selectAllIndeterminate to set
     */
    public void setSelectAllIndeterminate(final boolean selectAllIndeterminate) {
        m_selectAllIndeterminate = selectAllIndeterminate;
    }

    /**
     * @return the pageSize
     */
    public int getPageSize() {
        return m_pageSize;
    }

    /**
     * @param pageSize the pageSize to set
     */
    public void setPageSize(final int pageSize) {
        m_pageSize = pageSize;
    }

    /**
     * @return the currentPage
     */
    public int getCurrentPage() {
        return m_currentPage;
    }

    /**
     * @param currentPage the currentPage to set
     */
    public void setCurrentPage(final int currentPage) {
        m_currentPage = currentPage;
    }

    /**
     * @return the filterString
     */
    public String getFilterString() {
        return m_filterString;
    }

    /**
     * @param filterString the filterString to set
     */
    public void setFilterString(final String filterString) {
        m_filterString = filterString;
    }

    /**
     * @return the columnFilterStrings
     */
    public String[] getColumnFilterStrings() {
        return m_columnFilterStrings;
    }

    /**
     * @param columnFilterStrings the columnFilterStrings to set
     */
    public void setColumnFilterStrings(final String[] columnFilterStrings) {
        m_columnFilterStrings = columnFilterStrings;
    }

    /**
     * @return the currentOrder
     */
    public Object[][] getCurrentOrder() {
        return m_currentOrder;
    }

    /**
     * @param currentOrder the currentOrder to set
     */
    public void setCurrentOrder(final Object[][] currentOrder) {
        m_currentOrder = currentOrder;
    }

    /**
     * @return the hideUnselected
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

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void saveToNodeSettings(final NodeSettingsWO settings) {
        settings.addStringArray(CFG_SELECTION, m_selection);
        settings.addBoolean(CFG_SELECT_ALL, m_selectAll);
        settings.addInt(CFG_PAGE_SIZE, m_pageSize);
        settings.addInt(CFG_CURRENT_PAGE, m_currentPage);
        settings.addString(CFG_FILTER_STRING, m_filterString);
        settings.addStringArray(CFG_COLUMN_FILTER_STRINGS, m_columnFilterStrings);
        NodeSettingsWO orderSettings = settings.addNodeSettings(CFG_CURRENT_ORDER);
        orderSettings.addInt("numSettings", m_currentOrder.length);
        for (int i = 0; i < m_currentOrder.length; i++) {
            NodeSettingsWO sO = orderSettings.addNodeSettings("order_" + i);
            sO.addInt("col", (Integer)m_currentOrder[i][0]);
            sO.addString("dir", (String)m_currentOrder[i][1]);
        }

        //added with 3.3
        settings.addBoolean(CFG_SELECT_ALL_INDETERMINATE, m_selectAllIndeterminate);
        settings.addBoolean(CFG_HIDE_UNSELECTED, m_hideUnselected);
        settings.addBoolean(TableEditorViewConfig.CFG_PUBLISH_SELECTION, m_publishSelection);
        settings.addBoolean(TableEditorViewConfig.CFG_SUBSCRIBE_SELECTION, m_subscribeSelection);
        settings.addBoolean(TableEditorViewConfig.CFG_PUBLISH_FILTER, m_publishFilter);
        settings.addBoolean(TableEditorViewConfig.CFG_SUBSCRIBE_FILTER, m_subscribeFilter);

        // editor settings
        m_editorChanges.setConfigName(TableEditorViewConfig.CFG_EDITOR_CHANGES);  // assign the config name in case it has not been assigned by using serialization constructor
        m_editorChanges.saveSettingsTo(settings);
        settings.addString(CFG_TABLE_HASH, m_tableHash);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_selection = settings.getStringArray(CFG_SELECTION);
        m_selectAll = settings.getBoolean(CFG_SELECT_ALL);
        m_pageSize = settings.getInt(CFG_PAGE_SIZE);
        m_currentPage = settings.getInt(CFG_CURRENT_PAGE);
        m_filterString = settings.getString(CFG_FILTER_STRING);
        m_columnFilterStrings = settings.getStringArray(CFG_COLUMN_FILTER_STRINGS);
        NodeSettingsRO orderSettings = settings.getNodeSettings(CFG_CURRENT_ORDER);
        int numSettings = orderSettings.getInt("numSettings");
        m_currentOrder = new Object[numSettings][];
        for (int i = 0; i < numSettings; i++) {
            NodeSettingsRO sO = orderSettings.getNodeSettings("order_" + i);
            int col = sO.getInt("col");
            String dir = sO.getString("dir");
            m_currentOrder[i] = new Object[]{col, dir};
        }

        //added with 3.3
        m_selectAllIndeterminate = settings.getBoolean(CFG_SELECT_ALL_INDETERMINATE, DEFAULT_SELECT_ALL_INDETERMINATE);
        m_hideUnselected = settings.getBoolean(CFG_HIDE_UNSELECTED, DEFAULT_HIDE_UNSELECTED);
        m_publishSelection = settings.getBoolean(TableEditorViewConfig.CFG_PUBLISH_SELECTION, TableEditorViewConfig.DEFAULT_PUBLISH_SELECTION);
        m_subscribeSelection = settings.getBoolean(TableEditorViewConfig.CFG_SUBSCRIBE_SELECTION, TableEditorViewConfig.DEFAULT_SUBSCRIBE_SELECTION);
        m_publishFilter = settings.getBoolean(TableEditorViewConfig.CFG_PUBLISH_FILTER, TableEditorViewConfig.DEFAULT_PUBLISH_FILTER);
        m_subscribeFilter = settings.getBoolean(TableEditorViewConfig.CFG_SUBSCRIBE_FILTER, TableEditorViewConfig.DEFAULT_SUBSCRIBE_FILTER);

        // editor settings
        m_editorChanges.loadSettingsFrom(settings);
        m_tableHash = settings.getString(CFG_TABLE_HASH);
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
        TableEditorViewValue other = (TableEditorViewValue)obj;
        return new EqualsBuilder()
                .append(m_selection, other.m_selection)
                .append(m_selectAll, other.m_selectAll)
                .append(m_selectAllIndeterminate, other.m_selectAllIndeterminate)
                .append(m_pageSize, other.m_pageSize)
                .append(m_currentPage, other.m_currentPage)
                .append(m_filterString, other.m_filterString)
                .append(m_columnFilterStrings, other.m_columnFilterStrings)
                .append(m_currentOrder, other.m_currentOrder)
                .append(m_hideUnselected, other.m_hideUnselected)
                .append(m_publishSelection, other.m_publishSelection)
                .append(m_subscribeSelection, other.m_subscribeSelection)
                .append(m_publishFilter, other.m_publishFilter)
                .append(m_subscribeFilter, other.m_subscribeFilter)
                .append(m_editorChanges, other.m_editorChanges)
                .append(m_tableHash, m_tableHash)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_selection)
                .append(m_selectAll)
                .append(m_selectAllIndeterminate)
                .append(m_pageSize)
                .append(m_currentPage)
                .append(m_filterString)
                .append(m_columnFilterStrings)
                .append(m_currentOrder)
                .append(m_hideUnselected)
                .append(m_publishSelection)
                .append(m_subscribeSelection)
                .append(m_publishFilter)
                .append(m_subscribeFilter)
                .append(m_editorChanges)
                .append(m_tableHash)
                .toHashCode();
    }

}
