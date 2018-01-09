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
 *   08.11.2016 (Adrian): created
 */
package org.knime.js.base.node.viz.decisiontree.classification;

import java.util.Arrays;
import java.util.List;

import org.knime.base.data.xml.SvgCell;
import org.knime.base.node.mine.decisiontree2.PMMLDecisionTreeTranslator;
import org.knime.base.node.mine.decisiontree2.model.DecisionTree;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.container.CellFactory;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.data.container.SingleCellFactory;
import org.knime.core.data.def.BooleanCell;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectHolder;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.image.ImagePortObject;
import org.knime.core.node.port.image.ImagePortObjectSpec;
import org.knime.core.node.port.inactive.InactiveBranchPortObject;
import org.knime.core.node.port.inactive.InactiveBranchPortObjectSpec;
import org.knime.core.node.port.pmml.PMMLPortObject;
import org.knime.core.node.port.pmml.PMMLPortObjectSpec;
import org.knime.core.node.web.ValidationError;
import org.knime.core.pmml.PMMLModelType;
import org.knime.js.core.layout.LayoutTemplateProvider;
import org.knime.js.core.layout.bs.JSONLayoutViewContent;
import org.knime.js.core.layout.bs.JSONLayoutViewContent.ResizeMethod;
import org.knime.js.core.node.AbstractSVGWizardNodeModel;
import org.w3c.dom.Node;

/**
 *
 * @author Adrian Nembach, KNIME.com
 */
public class DecisionTreeViewNodeModel extends AbstractSVGWizardNodeModel<DecisionTreeViewRepresentation, DecisionTreeViewValue> implements PortObjectHolder, LayoutTemplateProvider {

    private static final String[] EMPTY_SELECTION = new String[0];

    private DecisionTreeViewConfig m_config;

    private PMMLPortObject m_pmmlTree;

    private BufferedDataTable m_table;

    /**
     * Default constructor for this node model.
     * @param viewName the view name
     */
    public DecisionTreeViewNodeModel(final String viewName) {
        super(new PortType[]{PMMLPortObject.TYPE, BufferedDataTable.TYPE_OPTIONAL}, new PortType[]{ImagePortObject.TYPE, BufferedDataTable.TYPE}, viewName);
        m_config = new DecisionTreeViewConfig();
        setOptionalViewWaitTime(1000L);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DecisionTreeViewRepresentation createEmptyViewRepresentation() {
        return new DecisionTreeViewRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DecisionTreeViewValue createEmptyViewValue() {
        return new DecisionTreeViewValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org.knime.js.base.node.viz.decisiontree";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHideInWizard() {
        return m_config.isHideInWizard();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHideInWizard(final boolean hide) {
        m_config.setHideInWizard(hide);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(final DecisionTreeViewValue viewContent) {
        // nothing to do atm
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveCurrentValue(final NodeSettingsWO content) {
        // do nothing (for now)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        PMMLPortObjectSpec pmmlSpec = (PMMLPortObjectSpec)inSpecs[0];
        DataTableSpec tableSpec = (DataTableSpec)inSpecs[1];
        PortObjectSpec out;
        if (tableSpec != null) {
            // check if table is compatible with decision tree
            DataTableSpec trainingSpec = pmmlSpec.getDataTableSpec();
            for (final DataColumnSpec colSpec : trainingSpec) {
                DataColumnSpec other = tableSpec.getColumnSpec(colSpec.getName());
                if (other == null || !colSpec.equalStructure(other)) {
                    StringBuilder exMessage = new StringBuilder("The provided table is not compatible with the decision tree.");
                    if (other == null) {
                        exMessage.append(" (Column \"")
                            .append(colSpec)
                            .append("\" is not contained in table.)");
                    } else {
                        exMessage.append("Column \"")
                            .append(other)
                            .append("\" does not match column \"")
                            .append(colSpec)
                            .append("\".)");
                    }
                    throw new InvalidSettingsException(exMessage.toString());
                }
            }
            // set output tableSpec
            out = tableSpec;
            if (m_config.getEnableSelection()) {
                ColumnRearranger rearranger = createColumnAppender(tableSpec, null);
                out = rearranger.createSpec();
            }
        } else {
            out = InactiveBranchPortObjectSpec.INSTANCE;
        }

        PortObjectSpec imageSpec;
        if (generateImage()) {
            imageSpec = new ImagePortObjectSpec(SvgCell.TYPE);
        } else {
            imageSpec = InactiveBranchPortObjectSpec.INSTANCE;
        }
        return new PortObjectSpec[] {imageSpec, out};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performExecuteCreateView(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        m_pmmlTree = (PMMLPortObject) inObjects[0];
        List<Node> models = m_pmmlTree.getPMMLValue().getModels(
            PMMLModelType.TreeModel);
        if (models.isEmpty()) {
            String msg = "Decision Tree evaluation failed: "
                    + "No tree model found.";
            throw new RuntimeException(msg);
        }

        m_table = (BufferedDataTable)inObjects[1];

        synchronized (getLock()) {

            // test if re-execute (tree is already set in that case.
            if (getViewRepresentation().getTree() == null) {
                copyConfigToView();
                writeTreeToRepresentation();
            }
        }
    }

    private void writeTreeToRepresentation() throws Exception {
        PMMLDecisionTreeTranslator trans = new PMMLDecisionTreeTranslator();
        m_pmmlTree.initializeModelTranslator(trans);
        DecisionTree decTree = trans.getDecisionTree();

        JSDecisionTreeTranslater jsTrans = new JSDecisionTreeTranslater();
        JSDecisionTree jsDecTree;
        if (m_table != null) {
            int maxRows = m_config.getMaxRows();
            if (maxRows < m_table.size()) {
                setWarningMessage("Only the first " + maxRows + " rows are displayed in the view.");
            }
            jsDecTree = jsTrans.translate(decTree, m_table, maxRows);
        } else {
            jsDecTree = jsTrans.translate(decTree);
        }
        int[] nodeStatus = m_config.getNodeStatus();
        if (nodeStatus == null) {
            nodeStatus = jsDecTree.createNodeStatusFor(m_config.getExpandedLevel());
        }
        DecisionTreeViewRepresentation representation = getViewRepresentation();
        representation.setTree(jsDecTree);
        DecisionTreeViewValue value = getViewValue();
        value.setNodeStatus(nodeStatus);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] performExecuteCreatePortObjects(final PortObject svgImageFromView, final PortObject[] inObjects,
        final ExecutionContext exec) throws Exception {
        PortObject out = m_table;
        synchronized (getLock()) {
            DecisionTreeViewValue value = getViewValue();
            if (m_table != null && m_config.getEnableSelection()) {
                List<String> selectionList = null;
                if (value != null && value.getSelection() != null) {
                    selectionList = Arrays.asList(value.getSelection());
                }
                ColumnRearranger rearranger = createColumnAppender(m_table.getDataTableSpec(), selectionList);
                out = exec.createColumnRearrangeTable(m_table, rearranger, exec);
            } else {
                out = InactiveBranchPortObject.INSTANCE;
            }
        }
        return new PortObject[] {svgImageFromView, out};
    }

    private ColumnRearranger createColumnAppender(final DataTableSpec spec, final List<String> selectionList) {
        String newColName = m_config.getSelectionColumnName();
        if (newColName == null || newColName.trim().isEmpty()) {
            newColName = DecisionTreeViewConfig.DEFAULT_SELECTION_COLUMN_NAME;
        }
        newColName = DataTableSpec.getUniqueColumnName(spec, newColName);
        DataColumnSpec outColumnSpec =
            new DataColumnSpecCreator(newColName, DataType.getType(BooleanCell.class)).createSpec();
        ColumnRearranger rearranger = new ColumnRearranger(spec);
        CellFactory fac = new SingleCellFactory(outColumnSpec) {

            private int m_rowIndex = 0;

            @Override
            public DataCell getCell(final DataRow row) {
                if (++m_rowIndex > m_config.getMaxRows()) {
                    return DataType.getMissingCell();
                }
                if (selectionList != null) {
                    if (selectionList.contains(row.getKey().toString())) {
                        return BooleanCell.TRUE;
                    }
                }
                return BooleanCell.FALSE;
            }
        };
        rearranger.append(fac);
        return rearranger;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean generateImage() {
        return m_config.isGenerateImage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performReset() {
        m_pmmlTree = null;
        m_table = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void useCurrentValueAsDefault() {
        synchronized (getLock()) {
            copyValueToConfig();
        }
    }

    private void copyValueToConfig() {
        DecisionTreeViewValue value = getViewValue();
        m_config.setTitle(value.getTitle());
        m_config.setSubtitle(value.getSubtitle());
        m_config.setNodeStatus(value.getNodeStatus());
        DecisionTreeViewRepresentation representation = getViewRepresentation();
        int[] configuredNodeStatus = representation.getTree().createNodeStatusFor(m_config.getExpandedLevel());
        m_config.setNodeStatusFromView(!Arrays.equals(configuredNodeStatus, value.getNodeStatus()));
        m_config.setPublishSelection(value.getPublishSelection());
        m_config.setSubscribeSelection(value.getSubscribeSelection());
        m_config.setScale(value.getScale());
    }

    private void copyConfigToView() {
        // Copy to representation
        DecisionTreeViewRepresentation representation = getViewRepresentation();
        representation.setBackgroundColor(m_config.getBackgroundColorString());
        representation.setDataAreaColor(m_config.getDataAreaColorString());
        representation.setNodeBackgroundColor(m_config.getNodeBackgroundColorString());
        representation.setEnableSubtitleChange(m_config.isEnableSubtitleChange());
        representation.setEnableTitleChange(m_config.isEnableTitleChange());
        representation.setEnableViewConfiguration(m_config.isEnableViewConfiguration());
        representation.setNumberFormat(m_config.getNumberFormat());
        representation.setEnableZooming(m_config.getEnableZooming());
        representation.setShowZoomResetButton(m_config.getShowZoomResetButton());
        representation.setDisplayFullscreenButton(m_config.getDisplayFullScreenButton());
        representation.setDisplaySelectionResetButton(m_config.getDisplaySelectionResetButton());
        representation.setTruncationLimit(m_config.getTruncationLimit());
        if (m_table == null) {
            // can't select if there is no table.
            representation.setEnableSelection(false);
        } else {
            representation.setEnableSelection(m_config.getEnableSelection());
            representation.setTableId(getTableId(1));
        }

        // Copy to value
        DecisionTreeViewValue value = getViewValue();
        if (isViewValueEmpty()) {
            value.setSubtitle(m_config.getSubtitle());
            value.setTitle(m_config.getTitle());
            value.setPublishSelection(m_config.getPublishSelection());
            value.setSubscribeSelection(m_config.getSubscribeSelection());
            value.setSelection(EMPTY_SELECTION);
            value.setScale(m_config.getScale());
//          int[] nodeStatus = {5, 1, 0};
//          value.setNodeStatus(m_config.getNodeStatus());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        m_config.saveSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        new DecisionTreeViewConfig().loadInModel(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_config.loadInModel(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PortObject[] getInternalPortObjects() {
        if (m_table == null) {
            return new PortObject[] {m_pmmlTree};
        }
        return new PortObject[] {m_pmmlTree, m_table};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInternalPortObjects(final PortObject[] portObjects) {
        m_pmmlTree = (PMMLPortObject)portObjects[0];
        if (portObjects.length == 2) {
            m_table = (BufferedDataTable)portObjects[1];
        }
        DecisionTreeViewRepresentation representation = getViewRepresentation();
        if (m_pmmlTree != null && representation != null) {
            try {
                writeTreeToRepresentation();
            } catch (Exception e) {
                // TODO error handling
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSONLayoutViewContent getLayoutTemplate() {
        JSONLayoutViewContent view = new JSONLayoutViewContent();
        view.setResizeMethod(ResizeMethod.VIEW_TAGGED_ELEMENT);
        // ensures that scroll bars are shown when the tree is too wide
        // or if a different resize method is used
        // unfortunately, this introduces scrollbars during the initial animation as well..
        view.setScrolling(true);
        return view;
    }
}
