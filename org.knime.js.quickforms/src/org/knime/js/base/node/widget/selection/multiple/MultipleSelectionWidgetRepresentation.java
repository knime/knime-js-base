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
 *   7 Feb 2020 (albrecht): created
 */
package org.knime.js.base.node.widget.selection.multiple;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.js.base.node.base.LabeledConfig;
import org.knime.js.base.node.base.selection.singleMultiple.SingleMultipleSelectionNodeConfig;
import org.knime.js.base.node.base.selection.singleMultiple.SingleMultipleSelectionNodeRepresentation;
import org.knime.js.base.node.base.selection.singleMultiple.SingleMultipleSelectionNodeValue;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The view representation for the multiple selection widget node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 * @param <VAL> the value implementation of the node
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class MultipleSelectionWidgetRepresentation<VAL extends SingleMultipleSelectionNodeValue>
    extends SingleMultipleSelectionNodeRepresentation<VAL> {

    private final boolean m_enableSearch;

    private final boolean m_ignoreInvalidValues;

    @JsonCreator
    private MultipleSelectionWidgetRepresentation(@JsonProperty("label") final String label,
        @JsonProperty("description") final String description, @JsonProperty("required") final boolean required,
        @JsonProperty("defaultValue") final VAL defaultValue, @JsonProperty("currentValue") final VAL currentValue,
        @JsonProperty("possibleChoices") final String[] possibleChoices, @JsonProperty("type") final String type,
        @JsonProperty("limitNumberVisOptions") final boolean limitNumberVisOptions,
        @JsonProperty("numberVisOptions") final Integer numberVisOptions,
        @JsonProperty("enableSearch") final boolean enableSearch,
        @JsonProperty("ignoreInvalidValues")final boolean ignoreInvalidValues,
        @JsonProperty("triggerReExecution") final boolean triggerReExecution) {

        super(label, description, required, defaultValue, currentValue, possibleChoices, type, limitNumberVisOptions,
            numberVisOptions, triggerReExecution);

        m_enableSearch = enableSearch;
        m_ignoreInvalidValues = ignoreInvalidValues;

    }

    /**
     * @param currentValue The value currently used by the node
     * @param defaultValue The default value of the node
     * @param selectionConfig The config of the node
     * @param enableSearch True, if the twinlist is supposed to render a search for column names, false otherwise
     * @param labelConfig The label config of the node
     * @param ignoreInvalidValues
     * @param triggerReExecution
     */
    public MultipleSelectionWidgetRepresentation(final VAL currentValue, final VAL defaultValue,
        final SingleMultipleSelectionNodeConfig selectionConfig, final LabeledConfig labelConfig,
        final boolean enableSearch, final boolean ignoreInvalidValues, final boolean triggerReExecution) {

        super(currentValue, defaultValue, selectionConfig, labelConfig, triggerReExecution);
        m_enableSearch = enableSearch;
        m_ignoreInvalidValues = ignoreInvalidValues;
    }

    /**
     * @return the enableSearch
     */
    @JsonProperty("enableSearch")
    public boolean isEnableSearch() {
        return m_enableSearch;
    }

    /**
     * @return the ignoreInvalidValues
     */
    @JsonProperty("ignoreInvalidValues")
    public boolean isIgnoreInvalidValues() {
        return m_ignoreInvalidValues;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("enableSearch=");
        sb.append(m_enableSearch);
        sb.append(", ");
        sb.append("ignoreInvalidValues=");
        sb.append(m_ignoreInvalidValues);
        sb.append(", ");

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(m_enableSearch).append(m_ignoreInvalidValues).toHashCode();
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
        @SuppressWarnings("unchecked")
        MultipleSelectionWidgetRepresentation<VAL> other = (MultipleSelectionWidgetRepresentation<VAL>)obj;
        return new EqualsBuilder().append(m_enableSearch, other.m_enableSearch)
            .append(m_ignoreInvalidValues, other.m_ignoreInvalidValues).isEquals();
    }
}
