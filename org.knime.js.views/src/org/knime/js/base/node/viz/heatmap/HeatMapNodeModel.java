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
 *   Aug 6, 2018 (awalter): created
 */
package org.knime.js.base.node.viz.heatmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.knime.base.data.xml.SvgCell;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.DoubleValue;
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
import org.knime.core.node.util.filter.NameFilterConfiguration.FilterResult;
import org.knime.core.node.util.filter.column.DataColumnSpecFilterConfiguration;
import org.knime.core.node.web.ValidationError;
import org.knime.core.node.wizard.CSSModifiable;
import org.knime.js.core.JSONDataTable;
import org.knime.js.core.layout.LayoutTemplateProvider;
import org.knime.js.core.layout.bs.JSONLayoutViewContent;
import org.knime.js.core.layout.bs.JSONLayoutViewContent.ResizeMethod;
import org.knime.js.core.node.AbstractSVGWizardNodeModel;

/**
 * The model for the heatmap node.
 *
 * @author Alison Walter, KNIME GmbH, Konstanz, Germany
 */
public class HeatMapNodeModel extends AbstractSVGWizardNodeModel<HeatMapViewRepresentation, HeatMapViewValue>
implements CSSModifiable, BufferedDataTableHolder, LayoutTemplateProvider {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(HeatMapNodeModel.class);
    private final static String JAVASCRIPT_ID = "org.knime.js.base.node.viz.heatmap";

    private final HeatMapViewConfig m_config;
    private BufferedDataTable m_table;

    /**
     * @param viewName the name of the view
     */
    protected HeatMapNodeModel(final String viewName) {
        super(new PortType[]{BufferedDataTable.TYPE}, new PortType[]{ImagePortObject.TYPE, BufferedDataTable.TYPE},
            viewName);
        m_config = new HeatMapViewConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        DataTableSpec tableSpec = (DataTableSpec) inSpecs[0];
        final String[] includedCols = m_config.getColumns().applyTo(tableSpec).getIncludes();
        if (includedCols == null || includedCols.length < 1) {
            throw new InvalidSettingsException("Given data table contains no numeric columns");
        }
        if (m_config.getEnableSelection()) {
            final ColumnRearranger createColumnRearranger = createColumnAppender(tableSpec, null);
            tableSpec = createColumnRearranger.createSpec();
        }

        PortObjectSpec image;
        if (generateImage()) {
            image = new ImagePortObjectSpec(SvgCell.TYPE);
        } else {
            image = InactiveBranchPortObjectSpec.INSTANCE;
        }

        return new PortObjectSpec[]{image, tableSpec};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HeatMapViewRepresentation createEmptyViewRepresentation() {
        return new HeatMapViewRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HeatMapViewValue createEmptyViewValue() {
        return new HeatMapViewValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HeatMapViewRepresentation getViewRepresentation() {
        final HeatMapViewRepresentation rep = super.getViewRepresentation();
        synchronized (getLock()) {
            if (rep.getTable() == null && m_table != null) {
                // set internal table
                try {
                    final JSONDataTable jT = createJSONTableFromBufferedDataTable(null);
                    rep.setTable(jT);
                } catch (Exception e) {
                    LOGGER.error("Could not create JSON table: " + e.getMessage(), e);
                }
            }
        }
        return rep;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return JAVASCRIPT_ID;
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
    public ValidationError validateViewValue(final HeatMapViewValue viewContent) {
        // No validation to do
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveCurrentValue(final NodeSettingsWO content) {
        // Nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCssStyles() {
        return m_config.getCustomCSS();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCssStyles(final String styles) {
        m_config.setCustomCSS(styles);
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performExecuteCreateView(final PortObject[] inObjects, final ExecutionContext exec)
        throws Exception {
        synchronized (getLock()) {
            final HeatMapViewRepresentation representation = getViewRepresentation();
            m_table = (BufferedDataTable)inObjects[0];
            // Ensures that min/max value set, even if dialog has not been opened
            m_config.setMinValue(getMin(m_table.getDataTableSpec(), m_config.getColumns(), m_config.getUseCustomMin()));
            m_config.setMaxValue(getMax(m_table.getDataTableSpec(), m_config.getColumns(), m_config.getUseCustomMax()));

            representation.setShowWarningInView(m_config.getShowWarningInView());
            representation.setImageWidth(m_config.getImageWidth());
            representation.setImageHeight(m_config.getImageHeight());
            representation.setResizeToWindow(m_config.getResizeToWindow());
            representation.setDisplayFullscreenButton(m_config.getDisplayFullscreenButton());
            representation.setMinValue(m_config.getMinValue());
            representation.setMaxValue(m_config.getMaxValue());
            representation.setEnableViewConfiguration(m_config.getEnableViewConfiguration());
            representation.setEnableTitleChange(m_config.getEnableTitleChange());
            representation.setEnableColorModeEdit(m_config.getEnableColorModeEdit());
            representation.setEnableShowToolTips(m_config.getEnableShowToolTips());
            representation.setThreeColorGradient(m_config.getThreeColorGradient());
            representation.setDiscreteGradientColors(m_config.getDiscreteGradientColors());
            representation.setNumDiscreteColors(m_config.getNumDiscreteColors());
            representation.setMissingValueColor(m_config.getMissingValueColor());
            representation.setUpperOutOfRangeColor(m_config.getUpperOutOfRangeColor());
            representation.setLowerOutOfRangeColor(m_config.getLowerOutOfRangeColor());
            representation.setColumns(m_config.getColumns().applyTo(m_table.getDataTableSpec()).getIncludes());
            representation.setLabelColumn(m_config.getLabelColumn());
            representation.setSvgLabelColumn(m_config.getSvgLabelColumn());
            representation.setEnableSelection(m_config.getEnableSelection());
            representation.setSelectionColumnName(m_config.getSelectionColumnName());
            representation.setShowResetSelectionButton(m_config.getShowResetSelectionButton());
            representation.setEnableShowSelectedRowsOnly(m_config.getEnableShowSelectedRowsOnly());
            representation.setEnablePaging(m_config.getEnablePaging());
            representation.setEnablePageSizeChange(m_config.getEnablePageSizeChange());
            representation.setAllowedPageSizes(m_config.getAllowedPageSizes());
            representation.setEnableShowAll(m_config.getEnableShowAll());
            representation.setEnableZoom(m_config.getEnableZoom());
            representation.setEnablePanning(m_config.getEnablePanning());
            representation.setShowZoomResetButton(m_config.getShowZoomResetButton());
            representation.setDataTableId(getTableId(0));

            final JSONDataTable jsonTable = createJSONTableFromBufferedDataTable(exec);
            representation.setTable(jsonTable);
            representation.setFilterIds(jsonTable.getSpec().getFilterIds());
            representation.setRunningInView(false);

            final HeatMapViewValue value = getViewValue();
            if (isViewValueEmpty()) {
                value.setChartTitle(m_config.getChartTitle());
                value.setChartSubtitle(m_config.getChartSubtitle());
                value.setShowToolTips(m_config.getShowToolTips());
                value.setContinuousGradient(m_config.getContinuousGradient());
                value.setSelection(new String[0]);
                value.setPublishSelection(m_config.getPublishSelection());
                value.setSubscribeSelection(m_config.getSubscribeSelection());
                value.setSubscribeFilter(m_config.getSubscribeFilter());
                value.setShowSelectedRowsOnly(m_config.getShowSelectedRowsOnly());
                value.setInitialPageSize(m_config.getInitialPageSize());
                value.setCurrentPage(1);
                value.setZoomX(0);
                value.setZoomY(0);
                value.setZoomK(1);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] performExecuteCreatePortObjects(final PortObject svgImageFromView, final PortObject[] inObjects,
        final ExecutionContext exec) throws Exception {
        BufferedDataTable out = m_table;
        synchronized (getLock()) {
            if (m_config.getEnableSelection()) {
                final List<String> selection = getViewValue().getSelection() == null ? null : Arrays.asList(getViewValue().getSelection());
                final ColumnRearranger createColumnRearranger =
                    createColumnAppender(m_table.getDataTableSpec(), selection);
                out = exec.createColumnRearrangeTable(m_table, createColumnRearranger, exec);
            }
            getViewRepresentation().setRunningInView(true);
        }
        return new PortObject[]{svgImageFromView, out};
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
        synchronized (getLock()) {
            copyViewValueToConfig();
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
        (new HeatMapViewConfig()).loadSettings(settings);
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
        final JSONLayoutViewContent template = new JSONLayoutViewContent();
        if (m_config.getResizeToWindow()) {
            template.setResizeMethod(ResizeMethod.ASPECT_RATIO_4by3);
        } else {
            template.setResizeMethod(ResizeMethod.VIEW_TAGGED_ELEMENT);
        }
        return template;
    }

    // -- Helper methods --

    private ColumnRearranger createColumnAppender(final DataTableSpec spec, final List<String> selectionList) {
        String newColName = m_config.getSelectionColumnName();
        if (newColName == null || newColName.trim().isEmpty()) {
            newColName = HeatMapViewConfig.DEFAULT_SELECTION_COLUMN_NAME;
        }
        newColName = DataTableSpec.getUniqueColumnName(spec, newColName);
        DataColumnSpec outColumnSpec =
            new DataColumnSpecCreator(newColName, DataType.getType(BooleanCell.class)).createSpec();
        ColumnRearranger rearranger = new ColumnRearranger(spec);
        CellFactory fac = new SingleCellFactory(outColumnSpec) {
            @Override
            public DataCell getCell(final DataRow row) {
                if (selectionList != null && !selectionList.isEmpty()) {
                    if (selectionList.contains(row.getKey().toString())) {
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

    private void copyViewValueToConfig() {
        final HeatMapViewValue viewValue = getViewValue();
        m_config.setChartTitle(viewValue.getChartTitle());
        m_config.setChartSubtitle(viewValue.getChartSubtitle());
        m_config.setShowToolTips(viewValue.getShowToolTips());
        m_config.setContinuousGradient(viewValue.getContinuousGradient());
        m_config.setInitialPageSize(viewValue.getInitialPageSize());
        m_config.setPublishSelection(viewValue.getPublishSelection());
        m_config.setSubscribeSelection(viewValue.getSubscribeSelection());
        m_config.setSubscribeFilter(viewValue.getSubscribeFilter());
        m_config.setShowSelectedRowsOnly(viewValue.getShowSelectedRowsOnly());
    }

    private JSONDataTable createJSONTableFromBufferedDataTable(final ExecutionContext exec) throws CanceledExecutionException {
        final boolean keepFilterCols = m_config.getSubscribeFilter();
        final FilterResult filter = m_config.getColumns().applyTo(m_table.getDataTableSpec());
        final List<String> include = new ArrayList<>(Arrays.asList(filter.getIncludes()));
        if (m_config.getEnableShowToolTips() && m_config.getSvgLabelColumn() != null && !m_config.getSvgLabelColumn().isEmpty()) {
            include.add(m_config.getSvgLabelColumn());
        }
        if (m_config.getLabelColumn() != null && !m_config.getLabelColumn().isEmpty() && !include.contains(m_config.getLabelColumn())) {
            include.add(m_config.getLabelColumn());
        }
        final String[] includeCols = include.toArray(new String[include.size()]);

        JSONDataTable jsonTable = JSONDataTable.newBuilder()
                .setDataTable(m_table)
                .setId(getTableId(0))
                .setFirstRow(1)
                .keepFilterColumns(keepFilterCols)
                .setIncludeColumns(includeCols)
                .calculateDataHash(true)
                .build(exec);
        return jsonTable;
    }

    private double getMax(final DataTableSpec spec, final DataColumnSpecFilterConfiguration filter,
        final boolean useCustom) {
        if (useCustom) {
            return m_config.getMaxValue();
        }
        final String[] included = filter.applyTo(spec).getIncludes();
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < included.length; i++) {
            final DataCell upperBound = spec.getColumnSpec(included[i]).getDomain().getUpperBound();
            if (upperBound != null && ((DoubleValue)upperBound).getDoubleValue() > max) {
                max = ((DoubleValue)upperBound).getDoubleValue();
            }
        }
        if (max == Double.NEGATIVE_INFINITY) {
            max = m_config.getMaxValue();
        }
        return max;
    }

    private double getMin(final DataTableSpec spec, final DataColumnSpecFilterConfiguration filter,
        final boolean useCustom) {
        if (useCustom) {
            return m_config.getMinValue();
        }
        final String[] included = filter.applyTo(spec).getIncludes();
        double min = Double.POSITIVE_INFINITY;
        for (int i = 0; i < included.length; i++) {
            final DataCell lowerBound = spec.getColumnSpec(included[i]).getDomain().getLowerBound();
            if (lowerBound != null && ((DoubleValue)lowerBound).getDoubleValue() < min) {
                min = ((DoubleValue)lowerBound).getDoubleValue();
            }
        }
        if (min == Double.POSITIVE_INFINITY) {
            min = m_config.getMinValue();
        }
        return min;
    }

}
