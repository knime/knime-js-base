package org.knime.js.base.node.viz.decisiontree.classification;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.base.node.mine.decisiontree2.PMMLPredicate;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
/**
 * A representation of a single decision tree node.
 * This representation serves as interface to JSON for all the different
 * decision trees in the KNIME AP.
 *
 * @author Adrian Nembach, KNIME.com
 *
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class JSDecisionTreeNode {

	/**
	 * Name of the node.
	 * Can also be viewed as its id.
	 */
	private Integer m_name;

	/**
	 * Name of the parent node.
	 * Necessary for d3 tree layout.
	 */
	private Integer m_parent;

	private JSDecisionTreeNode[] m_children;

	private JSNodeContent m_content;

	private PMMLPredicate m_condition;

	private String[] m_rowKeys;

	public JSDecisionTreeNode(final Integer id, final Integer parentId,
			final JSDecisionTreeNode[] children, final JSNodeContent content,
			final PMMLPredicate condition, final String[] rowKeys) {
		m_name = id;
		m_parent = parentId;
		m_children = children;
		m_content = content;
		setRowKeys(rowKeys);
		setCondition(condition);
	}

	public Integer getParent() {
		return m_parent;
	}

	public void setParent(final Integer parent) {
		m_parent = parent;
	}

	public Integer getName() {
		return m_name;
	}

	public void setName(final Integer name) {
		m_name = name;
	}

	public JSDecisionTreeNode[] getChildren() {
		return m_children;
	}

	public void setChildren(final JSDecisionTreeNode[] children) {
		m_children = children;
	}

	/**
	 * @return the content
	 */
	public JSNodeContent getContent() {
		return m_content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(final JSNodeContent content) {
		m_content = content;
	}

	/**
	 * @return the condition
	 */
	public PMMLPredicate getCondition() {
		return m_condition;
	}

	/**
	 * @param condition the condition to set
	 */
	public void setCondition(final PMMLPredicate condition) {
		m_condition = condition;
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
        JSDecisionTreeNode other = (JSDecisionTreeNode)obj;
	    return new EqualsBuilder()
	            .append(m_children, other.m_children)
	            .append(m_condition, other.m_condition)
	            .append(m_content, other.m_content)
	            .append(m_name, other.m_name)
	            .append(m_parent, other.m_parent)
	            .isEquals();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
	    return new HashCodeBuilder()
	            .append(m_children)
	            .append(m_condition)
	            .append(m_content)
	            .append(m_name)
	            .append(m_parent)
	            .toHashCode();
	}

    /**
     * @return the rowKeys
     */
    public String[] getRowKeys() {
        return m_rowKeys;
    }

    /**
     * @param rowKeys the rowKeys to set
     */
    public void setRowKeys(final String[] rowKeys) {
        m_rowKeys = rowKeys;
    }

}
