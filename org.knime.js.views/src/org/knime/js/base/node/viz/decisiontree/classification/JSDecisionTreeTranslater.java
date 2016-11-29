/**
 *
 */
package org.knime.js.base.node.viz.decisiontree.classification;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.knime.base.node.mine.decisiontree2.PMMLPredicate;
import org.knime.base.node.mine.decisiontree2.model.DecisionTree;
import org.knime.base.node.mine.decisiontree2.model.DecisionTreeNode;
import org.knime.base.node.mine.decisiontree2.model.DecisionTreeNodeLeaf;
import org.knime.base.node.mine.decisiontree2.model.DecisionTreeNodeSplitPMML;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.node.BufferedDataTable;

/**
 * Translates a {@link DecisionTree} to a {@link JSDecisionTree}.
 *
 * @author Adrian Nembach, KNIME.com
 *
 */
public class JSDecisionTreeTranslater {

    /**
     * Returns a {@link JSDecisionTree} that stores for each row in <b>table</b> the {@link RowKey} in the
     * leaf the row belongs to in <b>decisionTree</b>
     *
     * @param decisionTree the KNIME {@link DecisionTree} to view in the JavaScript view
     * @param table {@link BufferedDataTable} containing rows that can be classified by <b>decisionTree</b>.
     * @param numberOfRows only the first <b>numberOfRows</b> are stored
     * @return JSDecisionTree with {@link RowKey}s stored in the leafs.
     * @throws Exception thrown if something went wrong (e.g. the table is not compatible with the decision tree).
     */
    public JSDecisionTree translate(final DecisionTree decisionTree, final BufferedDataTable table, final int numberOfRows) throws Exception {
        DataTableSpec tableSpec = table.getDataTableSpec();
        int counter = 0;
        for (DataRow row : table) {
            decisionTree.addCoveredPattern(row, tableSpec);
            if (++counter >= numberOfRows) {
                break;
            }
        }
        return translate(decisionTree);

    }

	public JSDecisionTree translate(final DecisionTree decisionTree) {

		final DecisionTreeNode rootNode = decisionTree.getRootNode();
		LinkedHashMap<DataCell, Double> rootClassCounts = rootNode.getClassCounts();
		String[] classNames = new String[rootClassCounts.size()];
		final Map<DataCell, Integer> classMapper = new HashMap<>();
		int i = 0;
		for (final DataCell c : rootClassCounts.keySet()) {
			classNames[i] = c.toString();
			classMapper.put(c, i);
			i++;
		}
		final JSDecisionTreeMetaData meta = new JSDecisionTreeMetaData(classNames);

		final JSDecisionTreeNode root = translateNode(rootNode, null, null, classMapper);
		return new JSDecisionTree(root, meta);
	}

	private JSDecisionTreeNode translateNode(final DecisionTreeNode node,
			final Integer parentName, final PMMLPredicate condition,
			final Map<DataCell, Integer> classMapper) {
		int name = node.getOwnIndex();
		final JSNodeContent content = translateNodeContent(node, classMapper);
		if (node instanceof DecisionTreeNodeLeaf) {
		    DecisionTreeNodeLeaf leaf = (DecisionTreeNodeLeaf)node;
		    Set<RowKey> rowKeys = leaf.coveredPattern();
		    String[] rowKeyStrings = new String[rowKeys.size()];
		    Iterator<RowKey> iterator = rowKeys.iterator();
		    for (int i = 0; i < rowKeys.size(); i++) {
		        rowKeyStrings[i] = iterator.next().getString();
		    }
			// stop recursion at leafs
			return new JSDecisionTreeNode(name, parentName, null, content, condition, rowKeyStrings);
		}

		// deal with children
		assert node instanceof DecisionTreeNodeSplitPMML : "Currently only PMML split nodes are supported.";
		DecisionTreeNodeSplitPMML pmmlSplitNode = (DecisionTreeNodeSplitPMML) node;
		PMMLPredicate[] childConditions = pmmlSplitNode.getSplitPred();
		JSDecisionTreeNode[] children = new JSDecisionTreeNode[childConditions.length];
		for (int i = 0; i < childConditions.length; i++) {
			children[i] = translateNode(node.getChildAt(i), name, childConditions[i], classMapper);
		}
		return new JSDecisionTreeNode(name, parentName, children, content, condition, null);
	}

	private JSNodeContent translateNodeContent(final DecisionTreeNode node, final Map<DataCell, Integer> classMapper) {
		int majorityClassIdx = classMapper.get(node.getMajorityClass());
		Map<DataCell, Double> nodeClassCounts = node.getClassCounts();
		final double[] classCounts = new double[nodeClassCounts.size()];
		for (Entry<DataCell, Double> entry : nodeClassCounts.entrySet()) {
			classCounts[classMapper.get(entry.getKey())] = entry.getValue();
		}
		return new JSNodeContent(majorityClassIdx, classCounts);
	}

}
