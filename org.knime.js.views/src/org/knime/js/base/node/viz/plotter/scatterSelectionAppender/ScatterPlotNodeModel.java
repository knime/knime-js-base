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
 *   11.11.2014 (Christian Albrecht, KNIME AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.viz.plotter.scatterSelectionAppender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.knime.base.data.xml.SvgCell;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.StringValue;
import org.knime.core.data.container.CellFactory;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.data.container.SingleCellFactory;
import org.knime.core.data.def.BooleanCell;
import org.knime.core.data.property.filter.FilterHandler;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
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
import org.knime.js.core.JSONDataTable.JSONDataTableRow;
import org.knime.js.core.JSONDataTableSpec;
import org.knime.js.core.JSONDataTableSpec.JSTypes;
import org.knime.js.core.color.JSONColorModel;
import org.knime.js.core.datasets.JSONKeyedValues2DDataset;
import org.knime.js.core.datasets.JSONKeyedValuesRow;
import org.knime.js.core.layout.LayoutTemplateProvider;
import org.knime.js.core.layout.bs.JSONLayoutViewContent;
import org.knime.js.core.layout.bs.JSONLayoutViewContent.ResizeMethod;
import org.knime.js.core.node.AbstractSVGWizardNodeModel;
import org.knime.js.core.settings.ValueStore;

/**
 *
 * @author Christian Albrecht, KNIME AG, Zurich, Switzerland
 */
public class ScatterPlotNodeModel extends AbstractSVGWizardNodeModel<ScatterPlotViewRepresentation,
        ScatterPlotViewValue> implements LayoutTemplateProvider/*,CSSModifiable*/ {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(ScatterPlotNodeModel.class);

    private final ScatterPlotViewConfig m_config;

    private BufferedDataTable m_table;

    private ValueStore m_valueStore;

    static final String ROWS_LIMITATION_WARNING_ID = "rowsLimitation";

    /**
     * Creates a new model instance.
     * @param viewName the view name
     */
    protected ScatterPlotNodeModel(final String viewName) {
        super(new PortType[]{BufferedDataTable.TYPE}, new PortType[]{ImagePortObject.TYPE, BufferedDataTable.TYPE}, viewName);
        m_config = new ScatterPlotViewConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        List<String> allAllowedCols = new LinkedList<String>();

        DataTableSpec tableSpec = (DataTableSpec)inSpecs[0];

        for (DataColumnSpec colspec : tableSpec) {
            if (colspec.getType().isCompatible(DoubleValue.class) || colspec.getType().isCompatible(StringValue.class)) {
                allAllowedCols.add(colspec.getName());
            }
        }

        if (tableSpec.getNumColumns() < 1 || allAllowedCols.size() < 1) {
            throw new InvalidSettingsException("Data table must have"
                + " at least one numerical or categorical column.");
        }

        DataTableSpec out = tableSpec;
        if (m_config.getEnableSelection()) {
            ColumnRearranger rearranger = createColumnAppender(tableSpec, null);
            out = rearranger.createSpec();
        }

        PortObjectSpec imageSpec;
        if (generateImage()) {
            imageSpec = new ImagePortObjectSpec(SvgCell.TYPE);
        } else {
            imageSpec = InactiveBranchPortObjectSpec.INSTANCE;
        }
        return new PortObjectSpec[]{imageSpec, out};
    }

    private ColumnRearranger createColumnAppender(final DataTableSpec spec, final List<String> selectionList) {
        String newColName = m_config.getSelectionColumnName();
        if (newColName == null || newColName.trim().isEmpty()) {
            newColName = ScatterPlotViewConfig.DEFAULT_SELECTION_COLUMN_NAME;
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
    public ScatterPlotViewRepresentation createEmptyViewRepresentation() {
        return new ScatterPlotViewRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScatterPlotViewValue createEmptyViewValue() {
        return new ScatterPlotViewValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScatterPlotViewRepresentation getViewRepresentation() {
        ScatterPlotViewRepresentation rep = super.getViewRepresentation();
        synchronized (getLock()) {
            if (rep.getKeyedDataset() != null) {
                //make sure current table ids are used at all times
                rep.getKeyedDataset().setId(getTableId(0));
            }
            if (rep.getDateTimeFormats() == null) {
                rep.setDateTimeFormats(m_config.getDateTimeFormats().getJSONSerializableObject());
            }
        }
        return rep;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org.knime.js.base.node.viz.plotter.scatterSelectionAppender";
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

    /*
    Re-enable when styling makes sense
    @Override
    public String getCssStyles() {
        return m_config.getCustomCSS();
    }

    @Override
    public void setCssStyles(final String styles) {
        m_config.setCustomCSS(styles);
    }*/

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(final ScatterPlotViewValue viewContent) {
        synchronized (getLock()) {
            // validate value, nothing to do atm
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveCurrentValue(final NodeSettingsWO content) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performExecuteCreateView(final PortObject[] inData, final ExecutionContext exec) throws Exception {
        synchronized (getLock()) {
            m_table = (BufferedDataTable)inData[0];
            ScatterPlotViewRepresentation representation = getViewRepresentation();
            // don't use staggered rendering and resizing for image creation
            representation.setEnableStaggeredRendering(false);
            representation.setResizeToWindow(false);
            // Test if re-execute, dataset generation not necessary
            if (representation.getKeyedDataset() == null) {
                // create dataset for view
                copyConfigToView(m_table.getDataTableSpec());
                representation.setKeyedDataset(createKeyedDataset(exec));
            }
            setSubscriptionFilterIds(m_table.getDataTableSpec());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] performExecuteCreatePortObjects(final PortObject svgImageFromView,
        final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        BufferedDataTable out = m_table;
        synchronized (getLock()) {
            ScatterPlotViewRepresentation representation = getViewRepresentation();
            // enable staggered rendering and resizing for interactive view
            representation.setEnableStaggeredRendering(true);
            representation.setResizeToWindow(m_config.getResizeToWindow());

            ScatterPlotViewValue viewValue = getViewValue();
            if (m_config.getEnableSelection()) {
                List<String> selectionList = null;
                if (viewValue != null && viewValue.getSelection() != null) {
                    selectionList = Arrays.asList(viewValue.getSelection());
                }
                ColumnRearranger rearranger = createColumnAppender(m_table.getDataTableSpec(), selectionList);
                out = exec.createColumnRearrangeTable(m_table, rearranger, exec);
            }
            setSubscriptionFilterIds(m_table.getDataTableSpec());
        }
        exec.setProgress(1);
        return new PortObject[]{svgImageFromView, out};
    }

    private void setSubscriptionFilterIds(final DataTableSpec spec) {
        ScatterPlotViewRepresentation viewRepresentation = getViewRepresentation();
        if (viewRepresentation != null) {
            List<String> idList = new ArrayList<String>();
            for (int i = 0; i < spec.getNumColumns(); i++) {
                Optional<FilterHandler> filterHandler = spec.getColumnSpec(i).getFilterHandler();
                if (filterHandler.isPresent()) {
                    idList.add(filterHandler.get().getModel().getFilterUUID().toString());
                }
            }
            viewRepresentation.setSubscriptionFilterIds(idList.toArray(new String[0]));
        }
    }

    private JSONKeyedValues2DDataset createKeyedDataset(final ExecutionContext exec) throws CanceledExecutionException {
        ColumnRearranger c = createNumericColumnRearranger(m_table.getDataTableSpec());
        BufferedDataTable filteredTable = exec.createColumnRearrangeTable(m_table, c, exec.createSubProgress(0.1));
        exec.setProgress(0.1);
        //construct dataset
        if (m_config.getMaxRows() < filteredTable.size()) {
            String msg = "Only the first " + m_config.getMaxRows() + " rows are displayed.";
            setWarningMessage(msg);
            if (m_config.getShowWarningInView()) {
                getViewRepresentation().getWarnings().setWarningMessage(msg, ROWS_LIMITATION_WARNING_ID);
            }
        }
        final JSONDataTable table = JSONDataTable.newBuilder()
                .setDataTable(filteredTable)
                .setId(getTableId(0))
                .setFirstRow(1)
                .setMaxRows(m_config.getMaxRows())
                .build(exec.createSubProgress(0.8));
        exec.setProgress(0.9);
        ExecutionMonitor datasetExecutionMonitor = exec.createSubProgress(0.1);
        final JSONDataTableSpec tableSpec = table.getSpec();
        int numColumns = tableSpec.getNumColumns();
        String[] rowKeys = new String[tableSpec.getNumRows()];
        JSONKeyedValuesRow[] rowValues = new JSONKeyedValuesRow[tableSpec.getNumRows()];
        JSONDataTableRow[] tableRows = table.getRows();
        boolean hasUnsupportedValues = false;  // NaN or Inf
        for (int rowID = 0; rowID < rowValues.length; rowID++) {
            JSONDataTableRow currentRow = tableRows[rowID];
            rowKeys[rowID] = currentRow.getRowKey();
            Double[] rowData = new Double[numColumns];
            Object[] tableData = currentRow.getData();
            for (int colID = 0; colID < numColumns; colID++) {
                if (tableData[colID] == null) {
                    hasUnsupportedValues = true;
                    rowData[colID] = null;
                } else if (tableData[colID] instanceof Double) {
                    Double value = (Double)tableData[colID];
                    if (Double.isNaN(value) || Double.isInfinite(value)) {
                        hasUnsupportedValues = true;
                        value = null;
                    }
                    rowData[colID] = value;
                } else if (tableData[colID] instanceof Long) {
                    rowData[colID] = ((Long)tableData[colID]).doubleValue();
                } else if (tableData[colID] instanceof String) {
                    String data = (String)tableData[colID];
                    if (tableSpec.getKnimeTypes()[colID].equals("Date&time (Local)")) {
                        rowData[colID] = ((Long)LocalDateTime.parse(data).toInstant(ZoneOffset.UTC).toEpochMilli()).doubleValue();
                    } else if (tableSpec.getKnimeTypes()[colID].equals("Date")) {
                        rowData[colID] = ((Long)LocalDate.parse(data).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()).doubleValue();
                    } else if (tableSpec.getKnimeTypes()[colID].equals("Time")) {
                        rowData[colID] = ((Long)LocalTime.parse(data).atDate(LocalDate.now()).toInstant(ZoneOffset.UTC).toEpochMilli()).doubleValue();
                    } else if (tableSpec.getKnimeTypes()[colID].equals("Date&time (Zoned)")) {
                        rowData[colID] = ((Long)ZonedDateTime.parse(data).toInstant().toEpochMilli()).doubleValue();
                    } else {
                        // String
                        rowData[colID] = ((Integer)getOrdinalFromStringValue(data, table, colID)).doubleValue();
                    }
                }
            }
            rowValues[rowID] = new JSONKeyedValuesRow(currentRow.getRowKey(), rowData);
            rowValues[rowID].setColor(tableSpec.getRowColorValues()[rowID]);
            datasetExecutionMonitor.setProgress(((double)rowID) / rowValues.length, "Creating dataset, processing row "
                + rowID + " of " + rowValues.length + ".");
        }

        if (hasUnsupportedValues && getViewRepresentation().getReportOnMissingValues()) {
            setWarningMessage("Table contains missing or unsupported values - these values will be omitted.");
        }

        JSONKeyedValues2DDataset dataset = new JSONKeyedValues2DDataset(getTableId(0), tableSpec.getColNames(), rowValues);
        for (int col = 0; col < tableSpec.getNumColumns(); col++) {
            if (tableSpec.getColTypes()[col].equals(JSTypes.STRING)
                && tableSpec.getPossibleValues().get(col) != null) {
                dataset.setSymbol(getSymbolMap(tableSpec.getPossibleValues().get(col)), col);
            }
            if (tableSpec.getColTypes()[col].equals(JSTypes.DATE_TIME)) {
                dataset.setDateTimeFormat(tableSpec.getKnimeTypes()[col], col);
            }
        }

        JSONColorModel[] colorModels = tableSpec.getColorModels();
        if (colorModels != null && colorModels.length > 0) {
            dataset.setColorModels(colorModels);
        }

        ScatterPlotViewValue viewValue = getViewValue();
        final String xColumn = viewValue.getxColumn();
        if (StringUtils.isEmpty(xColumn) || !Arrays.asList(tableSpec.getColNames()).contains(xColumn)) {
            viewValue.setxColumn(tableSpec.getColNames()[0]);
        }
        final String yColumn = viewValue.getyColumn();
        if (StringUtils.isEmpty(yColumn) || !Arrays.asList(tableSpec.getColNames()).contains(yColumn)) {
            viewValue.setyColumn(tableSpec.getColNames()[tableSpec.getNumColumns() > 1 ? 1 : 0]);
        }

        return dataset;
    }

    private ColumnRearranger createNumericColumnRearranger(final DataTableSpec in) {
        ColumnRearranger c = new ColumnRearranger(in);
        for (DataColumnSpec colSpec : in) {
            DataType type = colSpec.getType();
            if (!type.isCompatible(DoubleValue.class) && !type.isCompatible(StringValue.class)) {
                c.remove(colSpec.getName());
            }
        }
        return c;
    }

    private int getOrdinalFromStringValue(final String stringValue, final JSONDataTable table, final int colID) {
        LinkedHashSet<Object> possibleValues = table.getSpec().getPossibleValues().get(colID);
        if (possibleValues != null) {
            int ordinal = 0;
            for (Object value : possibleValues) {
                if (value != null && value.equals(stringValue)) {
                    return ordinal;
                }
                ordinal++;
            }
        }
        return -1;
    }

    private Map<String, String> getSymbolMap(final LinkedHashSet<Object> linkedHashSet) {
        Map<String, String> symbolMap = new HashMap<String, String>();
        Integer ordinal = 0;
        for (Object value : linkedHashSet) {
            symbolMap.put(ordinal.toString(), value.toString());
            ordinal++;
        }
        return symbolMap;
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
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        m_config.saveSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        new ScatterPlotViewConfig().loadSettings(settings);
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
    protected void useCurrentValueAsDefault() {
        synchronized (getLock()) {
            copyValueToConfig();
        }
    }

    private void copyConfigToView(final DataTableSpec spec) {
        copyConfigToViewRepresentation(m_config, getViewRepresentation());

        if (m_valueStore == null) {
            m_valueStore = new ValueStore();
        } else if (isViewValueEmpty()) {
            // if the node is reset and executed again, the empty view value indicates that we also need to clear
            // the value store to make sure all values are transferred from the configuration
            m_valueStore.clear();
        }
        copyConfigToViewValue(m_valueStore, spec, m_config, getViewValue());
    }

    private static void copyConfigToViewValue(final ValueStore valStore, final DataTableSpec spec,
        final ScatterPlotViewConfig config, final ScatterPlotViewValue viewValue) {
        valStore.storeAndTransfer(ScatterPlotViewConfig.CHART_TITLE, config.getChartTitle(), viewValue::setChartTitle);
        valStore.storeAndTransfer(ScatterPlotViewConfig.CHART_SUBTITLE, config.getChartSubtitle(),
            viewValue::setChartSubtitle);
        valStore.storeAndTransfer(ScatterPlotViewConfig.X_COL, config.getxColumn(), viewValue::setxColumn);
        valStore.storeAndTransfer(ScatterPlotViewConfig.Y_COL, config.getyColumn(), viewValue::setyColumn);
        valStore.storeAndTransfer(ScatterPlotViewConfig.X_AXIS_LABEL, config.getxAxisLabel(), viewValue::setxAxisLabel);
        valStore.storeAndTransfer(ScatterPlotViewConfig.Y_AXIS_LABEL, config.getyAxisLabel(), viewValue::setyAxisLabel);
        valStore.storeAndTransfer(ScatterPlotViewConfig.DOT_SIZE, config.getDotSize(), viewValue::setDotSize);
        valStore.storeAndTransfer(ScatterPlotViewConfig.X_AXIS_MIN, config.getxAxisMin(), viewValue::setxAxisMin);
        valStore.storeAndTransfer(ScatterPlotViewConfig.X_AXIS_MAX, config.getxAxisMax(), viewValue::setxAxisMax);
        valStore.storeAndTransfer(ScatterPlotViewConfig.Y_AXIS_MIN, config.getyAxisMin(), viewValue::setyAxisMin);
        valStore.storeAndTransfer(ScatterPlotViewConfig.Y_AXIS_MAX, config.getyAxisMax(), viewValue::setyAxisMax);


        // added with 3.3
        valStore.storeAndTransfer(ScatterPlotViewConfig.CFG_PUBLISH_SELECTION, config.getPublishSelection(),
            viewValue::setPublishSelection);
        valStore.storeAndTransfer(ScatterPlotViewConfig.CFG_SUBSCRIBE_SELECTION, config.getSubscribeSelection(),
            viewValue::setSubscribeSelection);
        valStore.storeAndTransfer(ScatterPlotViewConfig.CFG_SUBSCRIBE_FILTER, config.getSubscribeFilter(),
            viewValue::setSubscribeFilter);

        // added with 3.4
        valStore.storeAndTransfer(ScatterPlotViewConfig.SHOW_LEGEND, config.getShowLegend(), viewValue::setShowLegend);

        // added with 4.4
        valStore.storeAndTransfer(ScatterPlotViewConfig.CFG_SHOW_SELECTED_ROWS_ONLY, config.getShowSelectedRowsOnly(),
            viewValue::setShowSelectedOnly);

        // we store (i.e. memorize) the table spec such that we can overwrite the view values
        // if the table spec changed on re-execution (a re-execution triggered by the node view)
        valStore.storeAndTransfer("tablespec", spec, s -> {
            if (config.getxAxisMin() == null && config.getUseDomainInfo() && (config.getxColumn() != null)) {
                viewValue.setxAxisMin(getMinimumFromColumn(spec, config.getxColumn()));
            }
            if (config.getxAxisMax() == null && config.getUseDomainInfo() && (config.getxColumn() != null)) {
                viewValue.setxAxisMax(getMaximumFromColumn(spec, config.getxColumn()));
            }
            if (config.getyAxisMin() == null && config.getUseDomainInfo() && (config.getyColumn() != null)) {
                viewValue.setyAxisMin(getMinimumFromColumn(spec, config.getyColumn()));
            }
            if (config.getyAxisMax() == null && config.getUseDomainInfo() && (config.getyColumn() != null)) {
                viewValue.setyAxisMax(getMaximumFromColumn(spec, config.getyColumn()));
            }

            // Check axes ranges
            Double xMin = viewValue.getxAxisMin();
            Double xMax = viewValue.getxAxisMax();
            if (xMin != null && xMax != null && xMin >= xMax) {
                LOGGER.info(
                    "Unsetting x-axis ranges. Minimum (" + xMin + ") has to be smaller than maximum (" + xMax + ").");
                viewValue.setxAxisMin(null);
                viewValue.setxAxisMax(null);
            }
            Double yMin = viewValue.getyAxisMin();
            Double yMax = viewValue.getyAxisMax();
            if (yMin != null && yMax != null && yMin >= yMax) {
                LOGGER.info(
                    "Unsetting y-axis ranges. Minimum (" + yMin + ") has to be smaller than maximum (" + yMax + ").");
                viewValue.setyAxisMin(null);
                viewValue.setyAxisMax(null);
            }
        }, (s1, s2) -> s1.equalStructure(s2));
    }

    private static void copyConfigToViewRepresentation(final ScatterPlotViewConfig config,
        final ScatterPlotViewRepresentation representation) {
        representation.setAutoRangeAxes(config.getAutoRangeAxes());
        representation.setUseDomainInformation(config.getUseDomainInfo());
        representation.setShowGrid(config.getShowGrid());
        representation.setShowCrosshair(config.getShowCrosshair());
        representation.setSnapToPoints(config.getSnapToPoints());

        representation.setEnableViewConfiguration(config.getEnableViewConfiguration());
        representation.setEnableTitleChange(config.getEnableTitleChange());
        representation.setEnableSubtitleChange(config.getEnableSubtitleChange());
        representation.setEnableXColumnChange(config.getEnableXColumnChange());
        representation.setEnableYColumnChange(config.getEnableYColumnChange());
        representation.setEnableXAxisLabelEdit(config.getEnableXAxisLabelEdit());
        representation.setEnableYAxisLabelEdit(config.getEnableYAxisLabelEdit());
        representation.setEnableDotSizeChange(config.getEnableDotSizeChange());

        representation.setEnablePanning(config.getEnablePanning());
        representation.setEnableZooming(config.getEnableZooming());
        representation.setEnableDragZooming(config.getEnableDragZooming());
        representation.setShowZoomResetButton(config.getShowZoomResetButton());
        representation.setEnableSelection(config.getEnableSelection());
        representation.setEnableRectangleSelection(config.getEnableRectangleSelection());
        representation.setEnableLassoSelection(config.getEnableLassoSelection());

        representation.setImageWidth(config.getImageWidth());
        representation.setImageHeight(config.getImageHeight());
        representation.setBackgroundColor(config.getBackgroundColorString());
        representation.setDataAreaColor(config.getDataAreaColorString());
        representation.setGridColor(config.getGridColorString());

        // added with 3.3
        representation.setDisplayFullscreenButton(config.getDisplayFullscreenButton());
        representation.setEnableShowSelectedOnly(config.getEnableShowSelectedOnly());

        // added with 3.4
        representation.setEnableSwitchLegend(config.getEnableSwitchLegend());
        representation.setShowWarningInView(config.getShowWarningInView());
        representation.setReportOnMissingValues(config.getReportOnMissingValues());

        // added with 3.5
        representation.setDateTimeFormats(config.getDateTimeFormats().getJSONSerializableObject());

        // added with 4.1
        representation.setEnforceOrigin(config.isEnforceOrigin());
    }

    private void copyValueToConfig() {
        ScatterPlotViewValue viewValue = getViewValue();
        m_config.setChartTitle(viewValue.getChartTitle());
        m_config.setChartSubtitle(viewValue.getChartSubtitle());
        m_config.setxColumn(viewValue.getxColumn());
        m_config.setyColumn(viewValue.getyColumn());
        m_config.setxAxisLabel(viewValue.getxAxisLabel());
        m_config.setyAxisLabel(viewValue.getyAxisLabel());
        m_config.setxAxisMin(viewValue.getxAxisMin());
        m_config.setxAxisMax(viewValue.getxAxisMax());
        m_config.setyAxisMin(viewValue.getyAxisMin());
        m_config.setyAxisMax(viewValue.getyAxisMax());
        m_config.setDotSize(viewValue.getDotSize());

        // added with 3.3
        m_config.setPublishSelection(viewValue.getPublishSelection());
        m_config.setSubscribeSelection(viewValue.getSubscribeSelection());
        m_config.setSubscribeFilter(viewValue.getSubscribeFilter());

        // added with 3.4
        m_config.setShowLegend(viewValue.getShowLegend());

        // added with 4.4
        m_config.setShowSelectedRowsOnly(viewValue.getShowSelectedOnly());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean generateImage() {
        return m_config.getGenerateImage();
    }

    private static Double getMinimumFromColumn(final DataTableSpec spec, final String columnName) {
        DataColumnSpec colSpec = spec.getColumnSpec(columnName);
        if (colSpec != null) {
            DataCell lowerCell = colSpec.getDomain().getLowerBound();
            if ((lowerCell != null) && lowerCell.getType().isCompatible(DoubleValue.class)) {
                return ((DoubleValue)lowerCell).getDoubleValue();
            }
        }
        return null;
    }

    private static Double getMaximumFromColumn(final DataTableSpec spec, final String columnName) {
        DataColumnSpec colSpec = spec.getColumnSpec(columnName);
        if (colSpec != null) {
            DataCell upperCell = colSpec.getDomain().getUpperBound();
            if ((upperCell != null) && upperCell.getType().isCompatible(DoubleValue.class)) {
                return ((DoubleValue)upperCell).getDoubleValue();
            }
        }
        return null;
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
}
