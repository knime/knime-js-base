package org.knime.js.base.node.viz.decisiontree.classification;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * A decision tree representation that is translated into JSON by Jackson.
 * It serves as an interface to JSON for the different decision tree
 * implementations existent in the KNIME AP.
 *
 * @author Adrian Nembach, KNIME.com
 *
 */
@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class JSDecisionTree {

	private final JSDecisionTreeNode m_root;

	private final JSDecisionTreeMetaData m_metaData;

	public JSDecisionTree(final JSDecisionTreeNode root, final JSDecisionTreeMetaData metaData) {
		m_root = root;
		m_metaData = metaData;
	}

	/**
	 * Creates an int array that represents the NodeStatus corresponding to a tree of which the
	 * first <b>expandedLevels</b> are expanded.
	 * I.e. passing a 0 means that only the root will be displayed.
	 * Passing 1 results in an expanded root node and so on.
	 *
	 * @param expandedLevels
	 * @return an array corresponding to the nodeStatus for a tree with the first <b>expandedLevels</b> expanded.
	 */
	public int[] createNodeStatusFor(final int expandedLevels) {
	    if (expandedLevels == 0) {
	        return null;
	    }
	    ArrayList<Integer> nodeId = new ArrayList<Integer>();
	    int currentLevel = 0;
	    traverseTreeAndRecordNodeIds(m_root, expandedLevels, currentLevel, nodeId);
	    int[] nodeStatus = new int[nodeId.stream().mapToInt(i -> i).max().getAsInt() + 1];
	    nodeId.forEach(i -> nodeStatus[i] = 1);
        return nodeStatus;
	}

	private void traverseTreeAndRecordNodeIds(final JSDecisionTreeNode node, final int maxLevel, final int currentLevel, final List<Integer> nodeId) {
	    if (currentLevel >= maxLevel) {
	        return;
	    }
	    nodeId.add(node.getName());
	    int nextLevel = currentLevel + 1;
	    final JSDecisionTreeNode[] children = node.getChildren();
	    if (children == null) {
	        return;
	    }
	    for (JSDecisionTreeNode child : children) {
	        traverseTreeAndRecordNodeIds(child, maxLevel, nextLevel, nodeId);
	    }
	}

	public JSDecisionTreeNode getRoot() {
		return m_root;
	}

	public JSDecisionTreeMetaData getMetaData() {
		return m_metaData;
	}

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
	    JSDecisionTree other = (JSDecisionTree)obj;
        return new EqualsBuilder()
                .append(m_root, other.m_root)
                .append(m_metaData, other.m_metaData)
                .isEquals();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
	    return new HashCodeBuilder()
	            .append(m_root)
	            .append(m_metaData)
	            .toHashCode();
	}

}
