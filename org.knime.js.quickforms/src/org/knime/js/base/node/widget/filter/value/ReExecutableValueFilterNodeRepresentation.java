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
 *   Nov 16, 2021 (ben.laney): created
 */
package org.knime.js.base.node.widget.filter.value;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.js.base.node.base.LabeledConfig;
import org.knime.js.base.node.base.filter.value.ValueFilterNodeConfig;
import org.knime.js.base.node.base.filter.value.ValueFilterNodeValue;
import org.knime.js.base.node.widget.ReExecutableNodeRepresentation;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The re-executable representation for the value filter widget node.
 *
 * @author ben.laney, KNIME GmbH, Konstanz, Germany
 * @param <VAL> the value implementation of the node
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class ReExecutableValueFilterNodeRepresentation<VAL extends ValueFilterNodeValue>
    extends ReExecutableNodeRepresentation<VAL> {

    private final boolean m_lockColumn;

    private final Map<String, List<String>> m_possibleValues;

    private final boolean m_enableSearch;

    private final String m_type;

    private final boolean m_limitNumberVisOptions;

    private final Integer m_numberVisOptions;

    /**
     * For deserialization via Jackson. Subclasses must call this constructor in their deserialization constructor.
     *
     * @param label the widget label
     * @param description the description
     * @param required <code>true</code> if a value is required, <code>false</code> otherwise
     * @param defaultValue the node's default value
     * @param currentValue the node's current value
     * @param lockColumn * @param enableSearch
     * @param possibleValues
     * @param type
     * @param limitNumberVisOptions
     * @param numberVisOptions
     * @param triggerReExecution
     */
    @JsonCreator
    protected ReExecutableValueFilterNodeRepresentation(final String label, final String description,
        final boolean required, final VAL defaultValue, final VAL currentValue, final boolean lockColumn,
        final Map<String, List<String>> possibleValues, final String type, final boolean enableSearch,
        final boolean limitNumberVisOptions, final Integer numberVisOptions, final boolean triggerReExecution) {
        super(label, description, required, defaultValue, currentValue, triggerReExecution);
        m_lockColumn = lockColumn;
        m_possibleValues = possibleValues;
        m_type = type;
        m_enableSearch = enableSearch;
        m_limitNumberVisOptions = limitNumberVisOptions;
        m_numberVisOptions = numberVisOptions;
    }

    /**
     * @param currentValue The value currently used by the node
     * @param defaultValue The default value of the node
     * @param filterConfig The config of the node
     * @param enableSearch True, if the twinlist is supposed to render a search for column names, false otherwise
     * @param labelConfig The label config of the node
     * @param triggerReExecution
     */
    public ReExecutableValueFilterNodeRepresentation(final VAL currentValue, final VAL defaultValue,
        final ValueFilterNodeConfig filterConfig, final LabeledConfig labelConfig, final boolean enableSearch,
        final boolean triggerReExecution) {
        super(currentValue, defaultValue, labelConfig, triggerReExecution);
        m_lockColumn = filterConfig.isLockColumn();
        m_type = filterConfig.getType();
        m_possibleValues = filterConfig.getPossibleValues();
        m_enableSearch = enableSearch;
        m_limitNumberVisOptions = filterConfig.isLimitNumberVisOptions();
        m_numberVisOptions = filterConfig.getNumberVisOptions();
    }

    /**
     * @return the lockColumn
     */
    @JsonProperty("lockColumn")
    public boolean isLockColumn() {
        return m_lockColumn;
    }

    /**
     * @return the possibleColumns
     */
    @JsonProperty("possibleColumns")
    public String[] getPossibleColumns() {
        return m_possibleValues.keySet().toArray(new String[m_possibleValues.keySet().size()]);
    }

    /**
     * @return the possibleValues
     */
    @JsonProperty("possibleValues")
    public Map<String, List<String>> getPossibleValues() {
        return m_possibleValues;
    }

    /**
     * @return the type
     */
    @JsonProperty("type")
    public String getType() {
        return m_type;
    }

    /**
     * @return the enableSearch
     */
    @JsonProperty("enableSearch")
    public boolean isEnableSearch() {
        return m_enableSearch;
    }

    /**
     * @return the limitNumberVisOptions
     */
    @JsonProperty("limitNumberVisOptions")
    public boolean isLimitNumberVisOptions() {
        return m_limitNumberVisOptions;
    }

    /**
     * @return the numberVisOptions
     */
    @JsonProperty("numberVisOptions")
    public Integer getNumberVisOptions() {
        return m_numberVisOptions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(", ");
        sb.append("lockColumn=");
        sb.append(m_lockColumn);
        sb.append(", ");
        sb.append("possibleValues=");
        sb.append("{");
        sb.append(m_possibleValues);
        sb.append("}");
        sb.append(", ");
        sb.append("type=");
        sb.append(m_type);
        sb.append(", ");
        sb.append("enableSearch=");
        sb.append(m_enableSearch);
        sb.append(", ");
        sb.append("limitNumberVisOptions=");
        sb.append(m_limitNumberVisOptions);
        sb.append(", ");
        sb.append("numberVisOptions=");
        sb.append(m_numberVisOptions);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(m_lockColumn).append(m_possibleValues)
            .append(m_type).append(m_enableSearch).append(m_limitNumberVisOptions).append(m_numberVisOptions)
            .toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
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
        @SuppressWarnings("unchecked")
        ReExecutableValueFilterNodeRepresentation<VAL> other = (ReExecutableValueFilterNodeRepresentation<VAL>)obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(m_lockColumn, other.m_lockColumn)
            .append(m_possibleValues, other.m_possibleValues).append(m_type, other.m_type)
            .append(m_enableSearch, other.m_enableSearch).append(m_limitNumberVisOptions, other.m_limitNumberVisOptions)
            .append(m_numberVisOptions, other.m_numberVisOptions).isEquals();
    }
}
