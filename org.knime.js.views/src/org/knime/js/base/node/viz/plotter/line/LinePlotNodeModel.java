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
 *  propagated with or for interoperation with KNIME. The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 *
 * History
 *   13.05.2014 (Christian Albrecht, KNIME AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.viz.plotter.line;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

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
import org.knime.core.node.util.filter.NameFilterConfiguration.EnforceOption;
import org.knime.core.node.util.filter.NameFilterConfiguration.FilterResult;
import org.knime.core.node.web.ValidationError;
import org.knime.js.core.JSONDataTable;
import org.knime.js.core.JSONDataTable.JSONDataTableRow;
import org.knime.js.core.JSONDataTableSpec;
import org.knime.js.core.JSONDataTableSpec.JSTypes;
import org.knime.js.core.datasets.JSONKeyedValues2DDataset;
import org.knime.js.core.datasets.JSONKeyedValuesRow;
import org.knime.js.core.layout.LayoutTemplateProvider;
import org.knime.js.core.layout.bs.JSONLayoutViewContent;
import org.knime.js.core.layout.bs.JSONLayoutViewContent.ResizeMethod;
import org.knime.js.core.node.AbstractSVGWizardNodeModel;
import org.knime.js.core.settings.ValueStore;

/**
 *
 * @author Christian Albrecht, KNIME AG, Zurich, Switzerland, University of Konstanz
 */
final class LinePlotNodeModel extends AbstractSVGWizardNodeModel<LinePlotViewRepresentation,
        LinePlotViewValue> implements LayoutTemplateProvider/*, CSSModifiable*/ {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(LinePlotNodeModel.class);

    private final LinePlotViewConfig m_config;

    private BufferedDataTable m_table;

    private ValueStore m_valueStore;

    static final String ROWS_LIMITATION_WARNING_ID = "rowsLimitation";
    static final String SKIP_Y_COLUMNS_WARNING_ID = "skipYColumns";

    /**
     * Creates a new model instance.
     */
    LinePlotNodeModel(final String viewName) {
        super(new PortType[]{BufferedDataTable.TYPE, BufferedDataTable.TYPE_OPTIONAL}, new PortType[]{
            ImagePortObject.TYPE, BufferedDataTable.TYPE}, viewName);
        m_config = new LinePlotViewConfig();
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

        m_config.getyColumnsConfig(true, tableSpec);

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
            newColName = LinePlotViewConfig.DEFAULT_SELECTION_COLUMN_NAME;
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
    public LinePlotViewRepresentation createEmptyViewRepresentation() {
        return new LinePlotViewRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LinePlotViewValue createEmptyViewValue() {
        return new LinePlotViewValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LinePlotViewRepresentation getViewRepresentation() {
        LinePlotViewRepresentation rep = super.getViewRepresentation();
        synchronized (getLock()) {
            if (rep.getKeyedDataset() != null) {
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
        return "org.knime.js.base.node.viz.plotter.line";
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
     * Re-enable once styling makes sense
    @Override
    public String getCssStyles() {
        return m_config.getCustomCSS();
    }

    @Override
    public void setCssStyles(final String styles) {
        m_config.setCustomCSS(styles);
    }
    */

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(final LinePlotViewValue viewContent) {
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
            BufferedDataTable colorTable = (BufferedDataTable)inData[1];
            LinePlotViewRepresentation representation = getViewRepresentation();
            // don't use staggered rendering and resizing for image creation
            representation.setEnableStaggeredRendering(false);
            representation.setResizeToWindow(false);
            if (representation.getKeyedDataset() == null) {
                // create dataset for view
                copyConfigToView(m_table.getDataTableSpec());
                representation.setKeyedDataset(createKeyedDataset(colorTable, exec));
            }
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
            LinePlotViewRepresentation representation = getViewRepresentation();
            // enable staggered rendering and resizing for interactive view
            representation.setEnableStaggeredRendering(true);
            representation.setResizeToWindow(m_config.getResizeToWindow());

            LinePlotViewValue viewValue = getViewValue();
            if (m_config.getEnableSelection()) {
                List<String> selectionList = null;
                if (viewValue != null && viewValue.getSelection() != null) {
                    selectionList = Arrays.asList(viewValue.getSelection());
                }
                ColumnRearranger rearranger = createColumnAppender(m_table.getDataTableSpec(), selectionList);
                out = exec.createColumnRearrangeTable(m_table, rearranger, exec);
            }
        }
        exec.setProgress(1);
        return new PortObject[]{svgImageFromView, out};
    }

    private JSONKeyedValues2DDataset
        createKeyedDataset(final BufferedDataTable colorTable, final ExecutionContext exec)
            throws CanceledExecutionException {

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

        JSONDataTable.Builder builder = JSONDataTable.newBuilder()
                .setDataTable(filteredTable)
                .setId(getTableId(0))
                .setFirstRow(1)
                .setMaxRows(m_config.getMaxRows());
        final JSONDataTable table = builder.build(exec.createSubProgress(0.79));

        JSONDataTable jsonColorTable = null;
        if (colorTable != null) {
            jsonColorTable = JSONDataTable.newBuilder()
                    .setDataTable(colorTable)
                    .setId(getTableId(1))
                    .setFirstRow(1)
                    .build(exec.createSilentSubProgress(0.01));
        }
        exec.setProgress(0.9);
        ExecutionMonitor datasetExecutionMonitor = exec.createSubProgress(0.1);
        final JSONDataTableSpec tableSpec = table.getSpec();
        int numColumns = tableSpec.getNumColumns();
        String[] rowKeys = new String[tableSpec.getNumRows()];
        JSONKeyedValuesRow[] rowValues = new JSONKeyedValuesRow[tableSpec.getNumRows()];
        JSONDataTableRow[] tableRows = table.getRows();
        for (int rowID = 0; rowID < rowValues.length; rowID++) {
            JSONDataTableRow currentRow = tableRows[rowID];
            rowKeys[rowID] = currentRow.getRowKey();
            Double[] rowData = new Double[numColumns];
            Object[] tableData = currentRow.getData();
            for (int colID = 0; colID < numColumns; colID++) {
                if (tableData[colID] instanceof Double) {
                    rowData[colID] = (Double)tableData[colID];
                } else if (tableData[colID] instanceof Long) {
                    rowData[colID] = ((Long)tableData[colID]).doubleValue();
                } else if (tableData[colID] instanceof String) {
                    String data = (String) tableData[colID];
                    if (tableSpec.getKnimeTypes()[colID].equals("Local Date Time")) {
                        rowData[colID] = ((Long)LocalDateTime.parse(data).toInstant(ZoneOffset.UTC).toEpochMilli()).doubleValue();
                    } else if (tableSpec.getKnimeTypes()[colID].equals("Local Date")) {
                        rowData[colID] = ((Long)LocalDate.parse(data).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()).doubleValue();
                    } else if (tableSpec.getKnimeTypes()[colID].equals("Local Time")) {
                        rowData[colID] = ((Long)LocalTime.parse(data).atDate(LocalDate.now()).toInstant(ZoneOffset.UTC).toEpochMilli()).doubleValue();
                    } else if (tableSpec.getKnimeTypes()[colID].equals("Zoned Date Time")) {
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

        JSONKeyedValues2DDataset dataset = new JSONKeyedValues2DDataset(getTableId(0), tableSpec.getColNames(), rowValues);

        // Write which columns have missing values
        boolean[] flags = table.getSpec().getContainsMissingValues();
        String[] missingValueColumns = IntStream.range(0, flags.length)
            .filter(i -> flags[i])
            .mapToObj(i -> table.getSpec().getColNames()[i])
            .toArray(String[]::new);
        dataset.setMissingValueColumns(missingValueColumns);
        if (Arrays.asList(missingValueColumns).contains(m_config.getxColumn()) && getViewRepresentation().getReportOnMissingValues()) {
            setWarningMessage("Missing values of the X axis column will be removed in the view");
        }
        if (m_config.getMissingValueMethod().equals(LinePlotViewConfig.MISSING_VALUE_METHOD_REMOVE_COLUMN) && missingValueColumns.length > 0 && getViewRepresentation().getReportOnMissingValues()) {
            setWarningMessage("Y axis columns with missing values will not be available in the view according to the chosen handling method.");
            if (m_config.getShowWarningInView() && getViewRepresentation().getReportOnMissingValues()) {
                getViewRepresentation().getWarnings().setWarningMessage(
                    "Column(s) '" + String.join("', '", missingValueColumns) + "' have missing values and are not available", SKIP_Y_COLUMNS_WARNING_ID);
            }
        }

        int colorExceptionCount = 0;
        for (int col = 0; col < tableSpec.getNumColumns(); col++) {
            String colColor = null;
            try {
                colColor = getColorForColumn(tableSpec.getColNames()[col], jsonColorTable);
            } catch (NullPointerException e) {
                colorExceptionCount++;
            }
            if (colColor != null) {
                dataset.setColumnColor(colColor, col);
            }
            if (tableSpec.getColTypes()[col].equals(JSTypes.STRING)
                && tableSpec.getPossibleValues().get(col) != null) {
                dataset.setSymbol(getSymbolMap(tableSpec.getPossibleValues().get(col)), col);
            }
            if (tableSpec.getColTypes()[col].equals(JSTypes.DATE_TIME)) {
                dataset.setDateTimeFormat(tableSpec.getKnimeTypes()[col], col);
            }
        }
        if (colorExceptionCount > 0) {
            setWarningMessage("There was a problem processing the colors "
                    + "from the table provided. The displayed colors may differ from those intended."
                    + " Total warnings: " + String.valueOf(colorExceptionCount));
        }

        LinePlotViewValue viewValue = getViewValue();

        final String[] yColumns = viewValue.getyColumns();
        if (yColumns == null || !Arrays.asList(tableSpec.getColNames()).containsAll(Arrays.asList(yColumns))) {
            viewValue.setyColumns(Arrays.stream(tableSpec.getColNames())
                .filter(x -> Arrays.stream(yColumns)
                    .anyMatch(y -> y == x)
                 )
                .toArray(size -> new String[size]));
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

    private String getColorForColumn(final String colKey, final JSONDataTable colorTable) {
        if (colKey != null && colorTable != null) {
            for (int row = 0; row < colorTable.getRows().length; row++) {
                    if (colKey.equals(colorTable.getRows()[row].getData()[0].toString())) {
                        String currentColor = colorTable.getSpec().getRowColorValues()[row];
                        if (currentColor != null) {
                            return currentColor;
                        }
                    }
            }
        }
        return null;
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
        new LinePlotViewConfig().loadSettings(settings);
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean generateImage() {
        return m_config.getGenerateImage();
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

    private static void copyConfigToViewRepresentation(final LinePlotViewConfig config,
        final LinePlotViewRepresentation representation) {
        representation.setShowLegend(config.getShowLegend());
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

        // added with 3.4
        representation.setMissingValueMethod(config.getMissingValueMethod());
        representation.setShowWarningInView(config.getShowWarningInView());
        representation.setDateTimeFormats(config.getDateTimeFormats().getJSONSerializableObject());
        representation.setReportOnMissingValues(config.getReportOnMissingValues());

        // added with 4.1
        representation.setEnforceOrigin(config.isEnforceOrigin());

        // added with 4.4
        representation.setEnableLineSizeChange(config.getEnableLineSizeChange());
    }

    private static void copyConfigToViewValue(final ValueStore valStore, final DataTableSpec spec,
        final LinePlotViewConfig config, final LinePlotViewValue viewValue) {

        valStore.storeAndTransfer(LinePlotViewConfig.CHART_TITLE, config.getChartTitle(), viewValue::setChartTitle);
        valStore.storeAndTransfer(LinePlotViewConfig.CHART_SUBTITLE, config.getChartSubtitle(),
            viewValue::setChartSubtitle);
        valStore.storeAndTransfer(LinePlotViewConfig.X_COL, config.getxColumn(), viewValue::setxColumn);
        valStore.storeAndTransfer(LinePlotViewConfig.X_AXIS_LABEL, config.getxAxisLabel(), viewValue::setxAxisLabel);
        valStore.storeAndTransfer(LinePlotViewConfig.Y_AXIS_LABEL, config.getyAxisLabel(), viewValue::setyAxisLabel);
        valStore.storeAndTransfer(LinePlotViewConfig.DOT_SIZE, config.getDotSize(), viewValue::setDotSize);
        valStore.storeAndTransfer(LinePlotViewConfig.LINE_SIZE, config.getLineSize(), viewValue::setLineSize);
        valStore.storeAndTransfer(LinePlotViewConfig.X_AXIS_MIN, config.getxAxisMin(), viewValue::setxAxisMin);
        valStore.storeAndTransfer(LinePlotViewConfig.X_AXIS_MAX, config.getxAxisMax(), viewValue::setxAxisMax);
        valStore.storeAndTransfer(LinePlotViewConfig.Y_AXIS_MIN, config.getyAxisMin(), viewValue::setyAxisMin);
        valStore.storeAndTransfer(LinePlotViewConfig.Y_AXIS_MAX, config.getyAxisMax(), viewValue::setyAxisMax);

        // we store (i.e. memorize) the table spec such that we can overwrite the view values
        // if the table spec changed on re-execution (a re-execution triggered by the node view)
        valStore.storeAndTransfer("tablespec", spec, s -> {
            FilterResult filter = config.getyColumnsConfig().applyTo(spec);
            viewValue.setyColumns(filter.getIncludes());

            if ((config.getxAxisMin() == null) && config.getUseDomainInfo() && (config.getxColumn() != null)) {
                viewValue.setxAxisMin(getMinimumFromColumns(spec, config.getxColumn()));
            }
            if ((config.getxAxisMax() == null) && config.getUseDomainInfo() && (config.getxColumn() != null)) {
                viewValue.setxAxisMax(getMaximumFromColumns(spec, config.getxColumn()));
            }
            if (config.getyAxisMin() == null && config.getUseDomainInfo()) {
                viewValue.setyAxisMin(getMinimumFromColumns(spec, filter.getIncludes()));
            }
            if (config.getyAxisMax() == null && config.getUseDomainInfo()) {
                viewValue.setyAxisMax(getMaximumFromColumns(spec, filter.getIncludes()));
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

    private void copyValueToConfig() {
        LinePlotViewValue viewValue = getViewValue();
        m_config.setChartTitle(viewValue.getChartTitle());
        m_config.setChartSubtitle(viewValue.getChartSubtitle());
        m_config.setxColumn(viewValue.getxColumn());
        m_config.getyColumnsConfig().loadDefaults(viewValue.getyColumns(), null, EnforceOption.EnforceInclusion);
        m_config.setxAxisLabel(viewValue.getxAxisLabel());
        m_config.setyAxisLabel(viewValue.getyAxisLabel());
        m_config.setxAxisMin(viewValue.getxAxisMin());
        m_config.setxAxisMax(viewValue.getxAxisMax());
        m_config.setyAxisMin(viewValue.getyAxisMin());
        m_config.setyAxisMax(viewValue.getyAxisMax());
        m_config.setDotSize(viewValue.getDotSize());
        m_config.setLineSize(viewValue.getLineSize());
    }

    private static Double getMinimumFromColumns(final DataTableSpec spec, final String... columnNames) {
        double minimum = Double.MAX_VALUE;
        for (String column : columnNames) {
            DataColumnSpec colSpec = spec.getColumnSpec(column);
            if (colSpec != null) {
                DataCell lowerCell = colSpec.getDomain().getLowerBound();
                if ((lowerCell != null) && lowerCell.getType().isCompatible(DoubleValue.class)) {
                    minimum = Math.min(minimum, ((DoubleValue)lowerCell).getDoubleValue());
                }
            }
        }
        if (minimum < Double.MAX_VALUE) {
            return minimum;
        }
        return null;
    }

    private static Double getMaximumFromColumns(final DataTableSpec spec, final String... columnNames) {
        double maximum = Double.MIN_VALUE;
        for (String column : columnNames) {
            DataColumnSpec colSpec = spec.getColumnSpec(column);
            if (colSpec != null) {
                DataCell upperCell = colSpec.getDomain().getUpperBound();
                if ((upperCell != null) && upperCell.getType().isCompatible(DoubleValue.class)) {
                    maximum = Math.max(maximum, ((DoubleValue)upperCell).getDoubleValue());
                }
            }
        }
        if (maximum > Double.MIN_VALUE) {
            return maximum;
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
