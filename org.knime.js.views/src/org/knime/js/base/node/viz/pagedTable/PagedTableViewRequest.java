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
 *   31 Jul 2018 (albrecht): created
 */
package org.knime.js.base.node.viz.pagedTable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.js.core.JSONViewRequest;
import org.knime.js.core.selections.json.JSONTableSelection;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class PagedTableViewRequest extends JSONViewRequest {

    private long m_start;
    private int m_length;
    private Search m_search;
    private Order[] m_order;
    private Column[] m_columns;
    private JSONTableSelection m_filter;
    private String[] m_selection;
    private boolean m_countRows;

    /**
     * @return the start
     */
    public long getStart() {
        return m_start;
    }

    /**
     * @param start the start to set
     */
    public void setStart(final long start) {
        m_start = start;
    }

    /**
     * @return the length
     */
    public int getLength() {
        return m_length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(final int length) {
        m_length = length;
    }

    /**
     * @return the search
     */
    public Search getSearch() {
        return m_search;
    }

    /**
     * @param search the search to set
     */
    public void setSearch(final Search search) {
        m_search = search;
    }

    /**
     * @return the order
     */
    public Order[] getOrder() {
        return m_order;
    }

    /**
     * @param order the order to set
     */
    public void setOrder(final Order[] order) {
        m_order = order;
    }

    /**
     * @return the columns
     */
    public Column[] getColumns() {
        return m_columns;
    }

    /**
     * @param columns the columns to set
     */
    public void setColumns(final Column[] columns) {
        m_columns = columns;
    }

    /**
     * @return the filter
     */
    public JSONTableSelection getFilter() {
        return m_filter;
    }

    /**
     * @param filter the filter to set
     */
    public void setFilter(final JSONTableSelection filter) {
        m_filter = filter;
    }

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
     * @return the countRows
     */
    public boolean isCountRows() {
        return m_countRows;
    }

    /**
     * @param countRows the countRows to set
     */
    public void setCountRows(final boolean countRows) {
        m_countRows = countRows;
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
        PagedTableViewRequest other = (PagedTableViewRequest)obj;
        return new EqualsBuilder()
                .append(m_start, other.m_start)
                .append(m_length, other.m_length)
                .append(m_search, other.m_search)
                .append(m_order, other.m_order)
                .append(m_columns, other.m_columns)
                .append(m_filter, other.m_filter)
                .append(m_selection, other.m_selection)
                .append(m_countRows, other.m_countRows)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(m_start)
                .append(m_length)
                .append(m_search)
                .append(m_order)
                .append(m_columns)
                .append(m_filter)
                .append(m_selection)
                .append(m_countRows)
                .toHashCode();
    }

    static class Search {
        private String m_value;
        private boolean m_regex;

        /**
         * @return the value
         */
        public String getValue() {
            return m_value;
        }

        /**
         * @param value the value to set
         */
        public void setValue(final String value) {
            m_value = value;
        }

        /**
         * @return the regex
         */
        public boolean isRegex() {
            return m_regex;
        }

        /**
         * @param regex the regex to set
         */
        public void setRegex(final boolean regex) {
            m_regex = regex;
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
            Search other = (Search)obj;
            return new EqualsBuilder()
                    .append(m_value, other.m_value)
                    .append(m_regex, other.m_regex)
                    .isEquals();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                    .append(m_value)
                    .append(m_regex)
                    .toHashCode();
        }
    }

    static class Order {
        private String m_column;
        private String m_dir;

        /**
         * @return the column
         */
        public String getColumn() {
            return m_column;
        }

        /**
         * @param column the column to set
         */
        public void setColumn(final String column) {
            m_column = column;
        }

        /**
         * @return the dir
         */
        public String getDir() {
            return m_dir;
        }

        /**
         * @param dir the dir to set
         */
        public void setDir(final String dir) {
            m_dir = dir;
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
            Order other = (Order)obj;
            return new EqualsBuilder()
                    .append(m_column, other.m_column)
                    .append(m_dir, other.m_dir)
                    .isEquals();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                    .append(m_column)
                    .append(m_dir)
                    .toHashCode();
        }
    }

    static class Column {
        private String m_data;
        private String m_name;
        private boolean m_searchable;
        private boolean m_orderable;
        private Search m_search;

        /**
         * @return the data
         */
        public String getData() {
            return m_data;
        }

        /**
         * @param data the data to set
         */
        public void setData(final String data) {
            m_data = data;
        }

        /**
         * @return the name
         */
        public String getName() {
            return m_name;
        }

        /**
         * @param name the name to set
         */
        public void setName(final String name) {
            m_name = name;
        }

        /**
         * @return the searchable
         */
        public boolean isSearchable() {
            return m_searchable;
        }

        /**
         * @param searchable the searchable to set
         */
        public void setSearchable(final boolean searchable) {
            m_searchable = searchable;
        }

        /**
         * @return the orderable
         */
        public boolean isOrderable() {
            return m_orderable;
        }

        /**
         * @param orderable the orderable to set
         */
        public void setOrderable(final boolean orderable) {
            m_orderable = orderable;
        }

        /**
         * @return the search
         */
        public Search getSearch() {
            return m_search;
        }

        /**
         * @param search the search to set
         */
        public void setSearch(final Search search) {
            m_search = search;
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
            Column other = (Column)obj;
            return new EqualsBuilder()
                    .append(m_data, other.m_data)
                    .append(m_name, other.m_name)
                    .append(m_searchable, other.m_searchable)
                    .append(m_orderable, other.m_orderable)
                    .append(m_search, other.m_search)
                    .isEquals();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                    .append(m_data)
                    .append(m_name)
                    .append(m_searchable)
                    .append(m_orderable)
                    .append(m_search)
                    .toHashCode();
        }
    }

}
