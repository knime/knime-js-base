/*
 * ------------------------------------------------------------------------
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
 * ------------------------------------------------------------------------
 *
 * History
 *   23.04.2014 (Christian Albrecht, KNIME AG, Zurich, Switzerland): created
 */
package org.knime.js.base.node.viz.tableEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.knime.core.data.BooleanValue;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.IntValue;
import org.knime.core.data.LongValue;
import org.knime.core.data.StringValue;
import org.knime.core.data.container.CellFactory;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.data.container.SingleCellFactory;
import org.knime.core.data.def.BooleanCell;
import org.knime.core.data.def.BooleanCell.BooleanCellFactory;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.LongCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.property.filter.FilterHandler;
import org.knime.core.node.BufferedDataContainer;
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
import org.knime.core.node.util.filter.NameFilterConfiguration.FilterResult;
import org.knime.core.node.web.ValidationError;
import org.knime.js.core.JSONDataTable;
import org.knime.js.core.JSONDataTableSpec;
import org.knime.js.core.node.AbstractWizardNodeModel;

/**
 *
 * @author Christian Albrecht, KNIME.com GmbH, Konstanz, Germany
 */
public class TableEditorViewNodeModel extends AbstractWizardNodeModel<TableEditorViewRepresentation, TableEditorViewValue> implements BufferedDataTableHolder {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(TableEditorViewNodeModel.class);

    private final TableEditorViewConfig m_config;
    private BufferedDataTable m_table;

    /**
     * @param viewName The name of the interactive view
     */
    protected TableEditorViewNodeModel(final String viewName) {
        super(new PortType[]{BufferedDataTable.TYPE}, new PortType[]{BufferedDataTable.TYPE}, viewName);
        m_config = new TableEditorViewConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org.knime.js.base.node.viz.tableEditor";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        DataTableSpec tableSpec = (DataTableSpec)inSpecs[0];
        if (m_config.getEnableSelection()) {
            ColumnRearranger rearranger = createColumnAppender(tableSpec, null);
            tableSpec = rearranger.createSpec();
        }
        return new PortObjectSpec[]{tableSpec};
    }

    private ColumnRearranger createColumnAppender(final DataTableSpec spec, final List<String> selectionList) {
        String newColName = m_config.getSelectionColumnName();
        if (newColName == null || newColName.trim().isEmpty()) {
            newColName = TableEditorViewConfig.DEFAULT_SELECTION_COLUMN_NAME;
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
                            /*return selectAll ? BooleanCell.FALSE : BooleanCell.TRUE;*/
                        return BooleanCell.TRUE;
                    } else {
                        return BooleanCell.FALSE;
                    }
                }
                /*return selectAll ? BooleanCell.TRUE : BooleanCell.FALSE;*/
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
    public void saveCurrentValue(final NodeSettingsWO content) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TableEditorViewRepresentation createEmptyViewRepresentation() {
        return new TableEditorViewRepresentation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TableEditorViewValue createEmptyViewValue() {
        return new TableEditorViewValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TableEditorViewRepresentation getViewRepresentation() {
        TableEditorViewRepresentation rep = super.getViewRepresentation();
        synchronized (getLock()) {
            if (rep.getTable() == null && m_table != null) {
                // set internal table
                try {
                    JSONDataTable jT = createJSONTableFromBufferedDataTable(m_table, null);
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
    public ValidationError validateViewValue(final TableEditorViewValue value) {
        // no validation done here
        return null;
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
    protected PortObject[] performExecute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        BufferedDataTable out;
        synchronized (getLock()) {
            TableEditorViewRepresentation viewRepresentation = getViewRepresentation();
            if (viewRepresentation.getTable() == null) {
                if (m_table == null) {
                    m_table = (BufferedDataTable)inObjects[0];
                }
                JSONDataTable jsonTable = createJSONTableFromBufferedDataTable(m_table, exec.createSubExecutionContext(0.5));
                viewRepresentation.setTable(jsonTable);
                copyConfigToRepresentation();
            }

            out = m_table;
            TableEditorViewValue viewValue = getViewValue();

            // apply edit changes
            if (viewValue != null && viewValue.getEditChanges() != null && viewValue.getEditChanges().size() > 0) {
                DataTableSpec spec = m_table.getDataTableSpec();
                Map<Integer, Map<Integer, Object>> editChanges = viewValue.getEditChanges();
                BufferedDataContainer dc = exec.createDataContainer(spec);
                JSONDataTableSpec jsonSpec = viewRepresentation.getTable().getSpec();
                int rowId = 0;
                for (DataRow row : m_table) {
                    Map<Integer, Object> rowEditChanges = editChanges.get(rowId);
                    DataCell[] copy = new DataCell[row.getNumCells()];
                    for (int i = 0; i < row.getNumCells(); i++) {
                        // since some columns could have been filtered out from the view, we need to map knime-table 'i' to json-table 'i'
                        int jsonI = jsonSpec.getColumnIndex(spec.getColumnNames()[i]);
                        DataCell cell = row.getCell(i);
                        if (rowEditChanges != null && rowEditChanges.containsKey(jsonI)) {
                            Object value = rowEditChanges.get(jsonI);
                            DataType type = spec.getColumnSpec(i).getType();
                            if (value == null) {
                                copy[i] = DataType.getMissingCell();
                            } else if (type.isCompatible(IntValue.class) && value instanceof Integer) {
                                copy[i] = new IntCell((Integer) value);
                            } else if (type.isCompatible(LongValue.class) && value instanceof Integer) {
                                copy[i] = new LongCell(((Integer) value).longValue());
                            } else if (type.isCompatible(DoubleValue.class) && value instanceof Double) {
                                copy[i] = new DoubleCell((Double) value);
                            } else if (type.isCompatible(StringValue.class)) {
                                copy[i] = new StringCell(value.toString());
                            } else if (type.isCompatible(BooleanValue.class)) {
                                copy[i] = BooleanCellFactory.create((Boolean) value);
                            }
                            else {
                                // this part should never be reached, but in case it is, we provide a better error than a NPE
                                throw new ClassCastException("Casting to the type " + type.getName() + " is not supported.");
                            }
                        } else {
                            copy[i] = cell;
                        }
                    }
                    dc.addRowToTable(new DefaultRow(row.getKey(), copy));
                    rowId++;
                }
                dc.close();
                m_table = dc.getTable();
                out = m_table;
                viewRepresentation.setTable(null);
            }

            if (m_config.getEnableSelection()) {
                List<String> selectionList = null;
                if (viewValue != null) {
                    if (viewValue.getSelection() != null) {
                        selectionList = Arrays.asList(viewValue.getSelection());
                    }
                }
                ColumnRearranger rearranger = createColumnAppender(m_table.getDataTableSpec(), selectionList);
                out = exec.createColumnRearrangeTable(m_table, rearranger, exec.createSubExecutionContext(0.5));
            }
            setSubscriptionFilterIds(m_table.getDataTableSpec());
        }
        exec.setProgress(1);
        return new PortObject[]{out};
    }

    private JSONDataTable createJSONTableFromBufferedDataTable(final BufferedDataTable table, final ExecutionContext exec) throws CanceledExecutionException {
        FilterResult filter = m_config.getColumnFilterConfig().applyTo(table.getDataTableSpec());
        //ColumnRearranger rearranger = new ColumnRearranger(table.getDataTableSpec());
        //rearranger.keepOnly(filter.getIncludes());
        //BufferedDataTable filteredTable = exec.createColumnRearrangeTable(table, rearranger, exec.createSubExecutionContext(0.5));
        JSONDataTable jsonTable = JSONDataTable.newBuilder()
                .setDataTable(table)
                .setId(getTableId(0))
                .setFirstRow(1)
                .setMaxRows(m_config.getMaxRows())
                .setExcludeColumns(filter.getExcludes())
                .build(exec);
        if (m_config.getMaxRows() < table.size()) {
            setWarningMessage("Only the first "
                    + m_config.getMaxRows() + " rows are displayed.");
        }
        return jsonTable;
    }

    private void setSubscriptionFilterIds(final DataTableSpec spec) {
        TableEditorViewRepresentation viewRepresentation = getViewRepresentation();
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

    private void copyConfigToRepresentation() {
        synchronized(getLock()) {
            TableEditorViewRepresentation viewRepresentation = getViewRepresentation();
            TableEditorViewValue viewValue = getViewValue();
            viewRepresentation.setEnablePaging(m_config.getEnablePaging());
            viewRepresentation.setInitialPageSize(m_config.getIntialPageSize());
            viewRepresentation.setEnablePageSizeChange(m_config.getEnablePageSizeChange());
            viewRepresentation.setAllowedPageSizes(m_config.getAllowedPageSizes());
            viewRepresentation.setPageSizeShowAll(m_config.getPageSizeShowAll());
            viewRepresentation.setEnableJumpToPage(m_config.getEnableJumpToPage());
            viewRepresentation.setDisplayRowColors(m_config.getDisplayRowColors());
            viewRepresentation.setDisplayRowIds(m_config.getDisplayRowIds());
            viewRepresentation.setDisplayColumnHeaders(m_config.getDisplayColumnHeaders());
            viewRepresentation.setDisplayRowIndex(m_config.getDisplayRowIndex());
            viewRepresentation.setFixedHeaders(m_config.getFixedHeaders());
            viewRepresentation.setTitle(m_config.getTitle());
            viewRepresentation.setSubtitle(m_config.getSubtitle());
            viewRepresentation.setEnableSelection(m_config.getEnableSelection());
            viewRepresentation.setEnableSearching(m_config.getEnableSearching());
            viewRepresentation.setEnableColumnSearching(m_config.getEnableColumnSearching());
            viewRepresentation.setEnableSorting(m_config.getEnableSorting());
            viewRepresentation.setEnableClearSortButton(m_config.getEnableClearSortButton());
            viewRepresentation.setEnableGlobalNumberFormat(m_config.getEnableGlobalNumberFormat());
            viewRepresentation.setGlobalNumberFormatDecimals(m_config.getGlobalNumberFormatDecimals());

            //added with 3.3
            viewRepresentation.setDisplayFullscreenButton(m_config.getDisplayFullscreenButton());
            viewRepresentation.setEnableHideUnselected(m_config.getEnableHideUnselected());
            viewValue.setPublishSelection(m_config.getPublishSelection());
            viewValue.setSubscribeSelection(m_config.getSubscribeSelection());
            viewValue.setPublishFilter(m_config.getPublishFilter());
            viewValue.setSubscribeFilter(m_config.getSubscribeFilter());

            //added with 3.4
            viewRepresentation.setDisplayMissingValueAsQuestionMark(m_config.getDisplayMissingValueAsQuestionMark());
            viewRepresentation.setDateTimeFormats(m_config.getDateTimeFormats().getJSONSerializableObject());
            viewValue.setHideUnselected(m_config.getHideUnselected() && !m_config.getSingleSelection());

            //added with 3.5
            viewRepresentation.setSingleSelection(m_config.getSingleSelection());
            viewRepresentation.setEnableClearSelectionButton(m_config.getEnableClearSelectionButton());

            //editor settings
            viewRepresentation.setEditableColumns(m_config.getEditableColumnFilterConfig().applyTo(m_table.getDataTableSpec()).getIncludes());
        }
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
        TableEditorViewValue viewValue = getViewValue();
        m_config.setHideUnselected(viewValue.getHideUnselected());
        m_config.setPublishSelection(viewValue.getPublishSelection());
        m_config.setSubscribeSelection(viewValue.getSubscribeSelection());
        m_config.setPublishFilter(viewValue.getPublishFilter());
        m_config.setSubscribeFilter(viewValue.getSubscribeFilter());
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
        (new TableEditorViewConfig()).loadSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_config.loadSettings(settings);
    }

}
