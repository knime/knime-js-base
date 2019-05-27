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
 *   27 May 2019 (albrecht): created
 */
package org.knime.js.base.node.base.listbox;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.js.base.node.base.LabeledConfig;
import org.knime.js.base.node.base.LabeledNodeRepresentation;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The base representation for the list box configuration and widget node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 * @param <VAL> the value implementation of the node
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class ListBoxNodeRepresentation<VAL extends ListBoxNodeValue> extends LabeledNodeRepresentation<VAL> {

    private final String m_regex;
    private final String m_errorMessage;
    private final String m_separator;
    private final boolean m_separateEachCharacter;
    private final boolean m_omitEmpty;
    private final String m_separatorRegex;
    private final Integer m_numberVisOptions;

    @JsonCreator
    private ListBoxNodeRepresentation(@JsonProperty("label") final String label,
        @JsonProperty("description") final String description,
        @JsonProperty("required") final boolean required,
        @JsonProperty("defaultValue") final VAL defaultValue,
        @JsonProperty("currentValue") final VAL currentValue,
        @JsonProperty("regex") final String regex,
        @JsonProperty("errormessage") final String errorMessage,
        @JsonProperty("separator") final String separator,
        @JsonProperty("separateeachcharacter") final boolean separateEachCharacter,
        @JsonProperty("omitempty") final boolean omitEmpty,
        @JsonProperty("separatorregex") final String separatorRegex,
        @JsonProperty("numberVisOptions") final Integer numberVisOptions) {
        super(label, description, required, defaultValue, currentValue);
        m_regex = regex;
        m_errorMessage = errorMessage;
        m_separator = separator;
        m_separateEachCharacter = separateEachCharacter;
        m_omitEmpty = omitEmpty;
        m_separatorRegex = separatorRegex;
        m_numberVisOptions = numberVisOptions;
    }

    /**
     * @param currentValue The value currently used by the node
     * @param defaultValue The default value of the node
     * @param listBoxConfig The config of the node
     * @param labelConfig The label config of the node
     */
    public ListBoxNodeRepresentation(final VAL currentValue, final VAL defaultValue,
        final ListBoxNodeConfig listBoxConfig, final LabeledConfig labelConfig) {
        super(currentValue, defaultValue, labelConfig);
        m_regex = listBoxConfig.getRegex();
        m_errorMessage = listBoxConfig.getErrorMessage();
        if (listBoxConfig.getSeparator() == null) {
            m_separator = ListBoxNodeConfig.DEFAULT_SEPARATOR;
        } else {
            m_separator = listBoxConfig.getSeparator();
        }
        m_separateEachCharacter = listBoxConfig.getSeparateEachCharacter();
        m_separatorRegex = listBoxConfig.getSeparatorRegex();
        m_omitEmpty = listBoxConfig.getOmitEmpty();
        m_numberVisOptions = listBoxConfig.getNumberVisOptions();
    }

    /**
     * @return the regex
     */
    @JsonProperty("regex")
    public String getRegex() {
        return m_regex;
    }

    /**
     * @return the errorMessage
     */
    @JsonProperty("errormessage")
    public String getErrorMessage() {
        return m_errorMessage;
    }

    /**
     * @return the separator
     */
    @JsonProperty("separator")
    public String getSeparator() {
        return m_separator;
    }

    /**
     * @return separateEachCharacter
     */
    @JsonProperty("separateeachcharacter")
    public boolean getSeparateEachCharacter() {
        return m_separateEachCharacter;
    }

    /**
     * @return separatorRegex
     */
    @JsonProperty("separatorregex")
    public String getSeparatorRegex() {
        return m_separatorRegex;
    }


    /**
     * @return the omitEmpty
     */
    @JsonProperty("omitempty")
    public boolean getOmitEmpty() {
        return m_omitEmpty;
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
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(", ");
        sb.append("regex=");
        sb.append(m_regex);
        sb.append(", ");
        sb.append("errorMessage=");
        sb.append(m_errorMessage);
        sb.append(", ");
        sb.append("separator=");
        sb.append(m_separator);
        sb.append(", ");
        sb.append("separateEachCharacter=");
        sb.append(m_separateEachCharacter);
        sb.append(", ");
        sb.append("separatorRegex=");
        sb.append(m_separatorRegex);
        sb.append(", ");
        sb.append("omitEmpty=");
        sb.append(m_omitEmpty);
        sb.append(", ");
        sb.append("numberVisOptions=");
        sb.append(m_numberVisOptions);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode())
                .append(m_regex)
                .append(m_errorMessage)
                .append(m_separator)
                .append(m_separateEachCharacter)
                .append(m_separatorRegex)
                .append(m_omitEmpty)
                .append(m_numberVisOptions)
                .toHashCode();
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
        ListBoxNodeRepresentation<VAL> other = (ListBoxNodeRepresentation<VAL>)obj;
        return new EqualsBuilder().appendSuper(super.equals(obj))
                .append(m_regex, other.m_regex)
                .append(m_errorMessage, other.m_errorMessage)
                .append(m_separator, other.m_separator)
                .append(m_separateEachCharacter, other.m_separateEachCharacter)
                .append(m_separatorRegex, m_separatorRegex)
                .append(m_omitEmpty, other.m_omitEmpty)
                .append(m_numberVisOptions, other.m_numberVisOptions)
                .isEquals();
    }

}
