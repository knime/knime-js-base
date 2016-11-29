package org.knime.js.base.node.viz.decisiontree.classification;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Represents the content of a decision tree node for the translation to JSON via Jackson.
 * In case of a classification tree this could be the majority class and the class counts.
 *
 * @author Adrian Nembach, KNIME.com
 *
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class JSNodeContent {

	private int m_majorityClassIdx;

	private double[] m_classCounts;

	public JSNodeContent(final int majorityClassIdx, final double[] classCounts) {
		m_majorityClassIdx = majorityClassIdx;
		m_classCounts = classCounts;
	}

	public int getMajorityClassIdx() {
		return m_majorityClassIdx;
	}

	public void setMajorityClassIdx(final int majorityClassIdx) {
		m_majorityClassIdx = majorityClassIdx;
	}

	public double[] getClassCounts() {
		return m_classCounts;
	}

	public void setClassCounts(final double[] classCounts) {
		m_classCounts = classCounts;
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
        JSNodeContent other = (JSNodeContent)obj;
	    return new EqualsBuilder()
	            .append(m_majorityClassIdx, other.m_majorityClassIdx)
	            .append(m_classCounts, other.m_classCounts)
	            .isEquals();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
	    return new HashCodeBuilder()
	            .append(m_majorityClassIdx)
	            .append(m_classCounts)
	            .toHashCode();
	}
}
