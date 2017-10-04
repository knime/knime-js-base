package org.knime.js.base.node.viz.decisiontree.classification;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Holds meta data for the {@link JSDecisionTree}.
 * For example a mapping from class names to indices.
 *
 * @author Adrian Nembach, KNIME.com
 *
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class JSDecisionTreeMetaData {

	private final String[] m_classNames;

	public JSDecisionTreeMetaData(final String[] classNames) {
		m_classNames = classNames;
	}

	public String[] getClassNames() {
		return m_classNames;
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
        JSDecisionTreeMetaData other = (JSDecisionTreeMetaData)obj;
	    return new EqualsBuilder()
	            .append(m_classNames, other.m_classNames)
	            .isEquals();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
	    return new HashCodeBuilder()
	            .append(m_classNames)
	            .toHashCode();
	}
}
