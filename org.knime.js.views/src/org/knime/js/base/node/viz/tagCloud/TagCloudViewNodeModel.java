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
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
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
 *   2 Oct 2017 (albrecht): created
 */
package org.knime.js.base.node.viz.tagCloud;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.knime.base.data.xml.SvgCell;
import org.knime.base.node.util.DataArray;
import org.knime.base.node.util.DefaultDataArray;
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
import org.knime.core.node.BufferedDataTableHolder;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.image.ImagePortObject;
import org.knime.core.node.port.image.ImagePortObjectSpec;
import org.knime.core.node.port.inactive.InactiveBranchPortObjectSpec;
import org.knime.core.node.web.ValidationError;
import org.knime.js.core.JSONDataTable;
import org.knime.js.core.layout.LayoutTemplateProvider;
import org.knime.js.core.layout.bs.JSONLayoutViewContent;
import org.knime.js.core.layout.bs.JSONLayoutViewContent.ResizeMethod;
import org.knime.js.core.node.AbstractSVGWizardNodeModel;

/**
 * Node model for the tag cloud view
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class TagCloudViewNodeModel
    extends AbstractSVGWizardNodeModel<TagCloudViewRepresentation, TagCloudViewValue> implements LayoutTemplateProvider, BufferedDataTableHolder {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(TagCloudViewNodeModel.class);

    private final TagCloudViewConfig m_config;
    private BufferedDataTable m_table;

    /**
     * @param viewName The name of the interactive view
     */
    protected TagCloudViewNodeModel(final String viewName) {
        super(new PortType[]{BufferedDataTable.TYPE}, new PortType[]{ImagePortObject.TYPE, BufferedDataTable.TYPE}, viewName);
        m_config = new TagCloudViewConfig();
        setOptionalViewWaitTime(1000l);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        DataTableSpec inSpec = (DataTableSpec)inSpecs[0];
        String wordCol = m_config.getWordColumn();
        if (wordCol != null && !inSpec.containsName(wordCol)) {
            throw new InvalidSettingsException("Selected tag column '" + wordCol + "' does not exist!");
        }
        String sizeCol = m_config.getSizeColumn();
        if (sizeCol != null && !inSpec.containsName(sizeCol)) {
            throw new InvalidSettingsException("Selected size column '" + sizeCol + "' does not exist!");
        }
        PortObjectSpec imageSpec;
        if (generateImage()) {
            imageSpec = new ImagePortObjectSpec(SvgCell.TYPE);
        } else {
            imageSpec = InactiveBranchPortObjectSpec.INSTANCE;
        }
        DataTableSpec tableSpec = inSpec;
        if (m_config.getEnableSelection()) {
            ColumnRearranger rearranger = createColumnAppender(inSpec, null);
            tableSpec = inSpec = rearranger.createSpec();
        }
        return new PortObjectSpec[]{imageSpec, tableSpec};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TagCloudViewRepresentation createEmptyViewRepresentation() {
        return new TagCloudViewRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TagCloudViewValue createEmptyViewValue() {
        return new TagCloudViewValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org.knime.js.base.node.viz.tagCloud";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHideInWizard() {
        return m_config.getHideInWizard();
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
    public ValidationError validateViewValue(final TagCloudViewValue viewContent) {
        /* nothing to validate atm */
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveCurrentValue(final NodeSettingsWO content) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performExecuteCreateView(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        m_table = (BufferedDataTable)inObjects[0];
        synchronized (getLock()) {
            TagCloudViewRepresentation representation = getViewRepresentation();
            if (representation.getData() == null) {
                copyConfigToView();
                representation.setData(extractWordCloudData(m_table, exec.createSubExecutionContext(0.8)));
                representation.setFilterTable(getJSONTable(m_table, exec.createSubExecutionContext(0.2)));
            }
            representation.setTableID(getTableId(0));
            representation.setSubscriptionFilterIds(getSubscriptionFilterIds(m_table.getDataTableSpec()));
            representation.setImageGeneration(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] performExecuteCreatePortObjects(final PortObject svgImageFromView, final PortObject[] inObjects,
        final ExecutionContext exec) throws Exception {
        m_table = (BufferedDataTable)inObjects[0];
        BufferedDataTable returnTable = m_table;
        synchronized (getLock()) {
            TagCloudViewRepresentation representation = getViewRepresentation();
            representation.setImageGeneration(false);
            if (m_config.getEnableSelection()) {
                TagCloudViewValue viewValue = getViewValue();
                List<String> selectionList = null;
                if (viewValue != null) {
                    if (viewValue.getSelection() != null) {
                        selectionList = Arrays.asList(viewValue.getSelection());
                    }
                }
                ColumnRearranger rearranger = createColumnAppender(m_table.getDataTableSpec(), selectionList);
                returnTable = exec.createColumnRearrangeTable(m_table, rearranger, exec);
            }
        }
        exec.setProgress(1);
        return new PortObject[]{svgImageFromView, returnTable};
    }

    private ColumnRearranger createColumnAppender(final DataTableSpec spec, final List<String> selectionList) {
        final List<String> usedRowIds = getAllRowIdsFromData();
        String newColName = m_config.getSelectionColumnName();
        if (newColName == null || newColName.trim().isEmpty()) {
            newColName = TagCloudViewConfig.DEFAULT_SELECTION_COLUMN_NAME;
        }
        newColName = DataTableSpec.getUniqueColumnName(spec, newColName);
        DataColumnSpec outColumnSpec =
                new DataColumnSpecCreator(newColName, DataType.getType(BooleanCell.class)).createSpec();
        ColumnRearranger rearranger = new ColumnRearranger(spec);
        CellFactory fac = new SingleCellFactory(outColumnSpec) {

            @Override
            public DataCell getCell(final DataRow row) {
                String rowID = row.getKey().toString();
                if (!usedRowIds.contains(rowID)) {
                    return DataType.getMissingCell();
                }
                if (selectionList != null) {
                    if (selectionList.contains(rowID)) {
                        return BooleanCell.TRUE;
                    } else {
                        return BooleanCell.FALSE;
                    }
                }
                return BooleanCell.FALSE;
            }
        };
        rearranger.append(fac);
        return rearranger;
    }

    private List<TagCloudData> extractWordCloudData(final BufferedDataTable table, final ExecutionContext exec) throws Exception {
        TagCloudTermCellResolver termResolver = new TagCloudTermCellResolver()
                .useRowIds(m_config.getWordColumn() == null)
                .setWordColumn(m_config.getWordColumn())
                .useSizeProperty(m_config.getUseSizeProp())
                .setSizeColumn(m_config.getSizeColumn())
                .setMaxWords(m_config.getMaxWords())
                .aggregateStrings(m_config.getAggregateWords())
                .ignoreTermTags(m_config.getIgnoreTermTags())
                .extractRowColors(m_config.getUseColorProp());
        DataArray da = new DefaultDataArray(table, 1, Integer.MAX_VALUE, exec.createSubExecutionContext(0.2));
        List<TagCloudData> data = termResolver.extractWordCloudData(da, exec.createSubExecutionContext(0.8));

        Map<String, String> warnMessages = new HashMap<String, String>();
        if (termResolver.isClippingOccured()) {
            String warnMessage = "Only the first " + m_config.getMaxWords() + " tags are displayed.";
            setWarningMessage(warnMessage);
            warnMessages.put("knime_clipped_rows", warnMessage);
        }
        int missingValueRowsRemoved = termResolver.getMissingValueCount();
        if (m_config.getReportMissingValues() && missingValueRowsRemoved > 0) {
            String warnMessage = missingValueRowsRemoved + " rows were omitted due to missing values.";
            setWarningMessage(warnMessage);
            warnMessages.put("knime_missing_values", warnMessage);
        }
        if (warnMessages.size() > 0) {
            TagCloudViewRepresentation representation = getViewRepresentation();
            representation.setWarningMessages(warnMessages);
        }

        setOptionalViewWaitTime(1000l /* animation time + buffer */ + (data.size() * 2) /* layout time */);
        return data;
    }

    private JSONDataTable getJSONTable(final BufferedDataTable table, final ExecutionContext exec) throws IllegalArgumentException, CanceledExecutionException {
        if (!m_config.getEnableViewConfig() && !m_config.getSubscribeFilter()) {
            return null;
        }
        List<String> includeColumns = new ArrayList<String>();
        for (DataColumnSpec spec : table.getDataTableSpec()) {
            if (spec.getFilterHandler().isPresent()) {
                includeColumns.add(spec.getName());
            }
        }
        if (includeColumns.size() < 1) {
            return null;
        }
        return JSONDataTable.newBuilder()
                .setDataTable(table)
                .setIncludeColumns(includeColumns.toArray(new String[0]))
                .setId(getTableId(0))
                .extractRowColors(false)
                .extractRowSizes(false)
                .excludeRowsWithMissingValues(true) /* missing values are by default excluded from filter */
                .build(exec);
    }

    private List<String> getAllRowIdsFromData() {
        TagCloudViewRepresentation representation = getViewRepresentation();
        List<String> rowIDs = new ArrayList<String>();
        if (representation != null && representation.getData() != null) {
            for (TagCloudData tcd : representation.getData()) {
                rowIDs.addAll(Arrays.asList(tcd.getRowIDs()));
            }
        }
        return rowIDs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TagCloudViewRepresentation getViewRepresentation() {
        TagCloudViewRepresentation representation = super.getViewRepresentation();
        synchronized (getLock()) {
            if (representation != null && representation.getFilterTable() == null && m_table != null) {
                // set internal table
                try {
                    representation.setFilterTable(getJSONTable(m_table, null));
                } catch (Exception e) {
                    LOGGER.error("Could not create JSON table: " + e.getMessage(), e);
                }
            }
        }
        return representation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean generateImage() {
        return m_config.getGenerateImage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performReset() {
        m_table = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void useCurrentValueAsDefault() {
        copyValueToConfig();
    }

    private void copyConfigToView() {
        TagCloudViewRepresentation representation = getViewRepresentation();
        representation.setShowWarningsInView(m_config.getShowWarningsInView());
        representation.setResizeToWindow(m_config.getResizeToWindow());
        representation.setImageWidth(m_config.getImageWidth());
        representation.setImageHeight(m_config.getImageHeight());
        representation.setDisplayFullscreenButton(m_config.getDisplayFullscreenButton());
        representation.setDisplayRefreshButton(m_config.getDisplayRefreshButton());
        representation.setDisableAnimations(m_config.getDisableAnimations());
        representation.setUseColorProperty(m_config.getUseColorProp());
        representation.setFont(m_config.getFont());
        representation.setFontBold(m_config.getFontBold());
        representation.setEnableViewConfig(m_config.getEnableViewConfig());
        representation.setEnableTitleChange(m_config.getEnableTitleChange());
        representation.setEnableSubtitleChange(m_config.getEnableSubtitleChange());
        representation.setEnableFontSizeChange(m_config.getEnableFontSizeChange());
        representation.setEnableScaleTypeChange(m_config.getEnableScaleTypeChange());
        representation.setEnableSpiralTypeChange(m_config.getEnableSpiralTypeChange());
        representation.setEnableNumOrientationsChange(m_config.getEnableNumOrientationsChange());
        representation.setEnableAnglesChange(m_config.getEnableAnglesChange());
        representation.setEnableSelection(m_config.getEnableSelection());
        representation.setSelectionColor(m_config.getSelectionColorString());
        representation.setEnableShowSelectedOnly(m_config.getEnableShowSelectedOnly());
        representation.setDisplayClearSelectionButton(m_config.getDisplayClearSelectionButton());

        TagCloudViewValue value = getViewValue();
        value.setTitle(m_config.getTitle());
        value.setSubtitle(m_config.getSubtitle());
        value.setMinFontSize(m_config.getMinFontSize());
        value.setMaxFontSize(m_config.getMaxFontSize());
        value.setFontScaleType(m_config.getFontScaleType());
        value.setSpiralType(m_config.getSpiralType());
        value.setNumOrientations(m_config.getNumOrientations());
        value.setStartAngle(m_config.getStartAngle());
        value.setEndAngle(m_config.getEndAngle());
        value.setPublishSelection(m_config.getPublishSelection());
        value.setSubscribeSelection(m_config.getSubscribeSelection());
        value.setShowSelectedOnly(m_config.getDefaultShowSelectedOnly());
        value.setSubscribeFilter(m_config.getSubscribeFilter());
    }

    private void copyValueToConfig() {
        TagCloudViewValue value = getViewValue();
        m_config.setTitle(value.getTitle());
        m_config.setSubtitle(value.getSubtitle());
        m_config.setMinFontSize(value.getMinFontSize());
        m_config.setMaxFontSize(value.getMaxFontSize());
        m_config.setFontScaleType(value.getFontScaleType());
        m_config.setSpiralType(value.getSpiralType());
        m_config.setNumOrientations(value.getNumOrientations());
        m_config.setStartAngle(value.getStartAngle());
        m_config.setEndAngle(value.getEndAngle());
        m_config.setPublishSelection(value.getPublishSelection());
        m_config.setSubscribeSelection(value.getSubscribeSelection());
        m_config.setDefaultShowSelectedOnly(value.getShowSelectedOnly());
        m_config.setSubscribeFilter(value.getSubscribeFilter());
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
        (new TagCloudViewConfig()).loadSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_config.loadSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JSONLayoutViewContent getLayoutTemplate() {
        JSONLayoutViewContent template = new JSONLayoutViewContent();
        if (m_config.getResizeToWindow()) {
            template.setResizeMethod(ResizeMethod.ASPECT_RATIO_16by9);
        } else {
            template.setResizeMethod(ResizeMethod.VIEW_LOWEST_ELEMENT);
        }
        return template;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BufferedDataTable[] getInternalTables() {
        return new BufferedDataTable[]{m_table};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInternalTables(final BufferedDataTable[] tables) {
        m_table = tables[0];
    }

}
