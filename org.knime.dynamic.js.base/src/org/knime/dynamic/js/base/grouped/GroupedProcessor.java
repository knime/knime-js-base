package org.knime.dynamic.js.base.grouped;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.knime.base.data.aggregation.AggregationOperator;
import org.knime.base.data.aggregation.ColumnAggregator;
import org.knime.base.data.aggregation.GlobalSettings;
import org.knime.base.data.aggregation.OperatorColumnSettings;
import org.knime.base.data.aggregation.general.CountOperator;
import org.knime.base.data.aggregation.numerical.MeanOperator;
import org.knime.base.data.aggregation.numerical.SumOperator;
import org.knime.base.node.preproc.groupby.BigGroupByTable;
import org.knime.base.node.preproc.groupby.ColumnNamePolicy;
import org.knime.base.node.preproc.groupby.GroupByTable;
import org.knime.base.node.preproc.groupby.MemoryGroupByTable;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.defaultnodesettings.SettingsModel;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnFilter2;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.property.hilite.DefaultHiLiteMapper;
import org.knime.core.node.property.hilite.HiLiteHandler;
import org.knime.core.node.property.hilite.HiLiteTranslator;
import org.knime.core.node.util.filter.NameFilterConfiguration.FilterResult;
import org.knime.dynamic.js.v30.DynamicJSConfig;
import org.knime.dynamic.js.v30.DynamicJSViewRepresentation;
import org.knime.dynamic.js.v30.DynamicStatefulJSProcessor;
import org.knime.js.core.JSONDataTable;
import org.knime.js.core.selections.json.JSONSelectionTranslator;

public class GroupedProcessor extends DynamicStatefulJSProcessor {
	
    private static final String COUNT = "Occurence\u00A0Count";
	private static final String SUM = "Sum";
	private static final String AVG = "Average";
	private static final String[] AVAILABLE_METHODS = new String[]{COUNT, SUM, AVG};

	@Override
	public Object[] processInputObjects(PortObject[] inObjects,
			ExecutionContext exec, DynamicJSConfig config) throws Exception {
		BufferedDataTable table = (BufferedDataTable)inObjects[0];
		//Check aggregation method
		String method = ((SettingsModelString)config.getModel("aggr")).getStringValue();
		if (!Arrays.asList(AVAILABLE_METHODS).contains(method)) {
			throw new IllegalArgumentException("Aggregation method not supported: " + method);
		}
		
		//Check category column settings
		String catColName = ((SettingsModelString)config.getModel("cat")).getStringValue();
		if (catColName == null) {
			throw new IllegalArgumentException("No column selected for category values.");
		}
		int columnIndex = table.getDataTableSpec().findColumnIndex(catColName);
		if (columnIndex < 0) {
			throw new IllegalArgumentException("Index for category column with name " + catColName + " not found.");
		}
		
		//Check frequency column(s) settings
		ColumnAggregator[] colAggregators = null;
		if (!method.equals(COUNT)) {
			String[] freqColumns = new String[0];
			final SettingsModel freqModel = config.getModel("freq");
			if (freqModel instanceof SettingsModelString) {
				String freqColName = ((SettingsModelString) freqModel).getStringValue();
				if (freqColName == null) {
					throw new IllegalArgumentException("No column selected for frequency values.");
				}
				columnIndex = table.getDataTableSpec().findColumnIndex(freqColName);
				if (columnIndex < 0) {
					throw new IllegalArgumentException(
							"Index for frequency column with name " + freqColName + " not found.");
				}
				freqColumns = new String[]{freqColName};
			} else if (freqModel instanceof SettingsModelColumnFilter2) {
				DataTableSpec inSpec = ((BufferedDataTable) inObjects[0]).getDataTableSpec();
				FilterResult filterResult = ((SettingsModelColumnFilter2) freqModel).applyTo(inSpec);
				freqColumns = filterResult.getIncludes();
				if (freqColumns.length < 1) {
					throw new IllegalArgumentException(
							"Frequency column filter include list empty. Select at least one frequency column.");
				}
			}
			colAggregators = new ColumnAggregator[freqColumns.length];
			for (int i = 0; i < freqColumns.length; i++) {
				AggregationOperator operator = null;
				if (method.equals(SUM)) {
					operator = new SumOperator(GlobalSettings.DEFAULT, OperatorColumnSettings.DEFAULT_EXCL_MISSING);
				} else if (method.equals(AVG)) {
					operator = new MeanOperator(GlobalSettings.DEFAULT, OperatorColumnSettings.DEFAULT_EXCL_MISSING);
				}
				if (operator == null) {
					throw new IllegalArgumentException("Could not initialize aggregation operator for method " + method);
				}
				colAggregators[i] = new ColumnAggregator(table.getDataTableSpec().getColumnSpec(freqColumns[i]), operator);
			}
		} else {
            AggregationOperator operator =
                new CountOperator(GlobalSettings.DEFAULT, OperatorColumnSettings.DEFAULT_INCL_MISSING);
			colAggregators = new ColumnAggregator[]{new ColumnAggregator(table.getDataTableSpec().getColumnSpec(catColName), operator)};
		}
		
		Boolean inMemory = ((SettingsModelBoolean)config.getModel("processInMemory")).getBooleanValue();
		GroupByTable groupTable;
		if(inMemory) {
		    groupTable = new MemoryGroupByTable(exec, table, Arrays.asList(new String[]{catColName}),
            colAggregators, GlobalSettings.DEFAULT, true, ColumnNamePolicy.KEEP_ORIGINAL_NAME, false);
		} else {
	        groupTable = new BigGroupByTable(exec, table, Arrays.asList(new String[]{catColName}),
            colAggregators, GlobalSettings.DEFAULT, true, ColumnNamePolicy.KEEP_ORIGINAL_NAME, false);
		}

        // Missing values processing        
        if (((SettingsModelBoolean)config.getModel("reportOnMissingValues")).getBooleanValue()) {
            Map<String, Long> missingValuesMap = groupTable.getMissingValuesMap();
            if (missingValuesMap.size() > 0) {
                String warning =
                    "The following data columns have missing values, which were ignored during the aggregation:\n"
                        + missingValuesMap.entrySet().stream()
                            .map(x -> "    " + x.getKey() + " - " + x.getValue().toString() + " missing value(s)")
                            .collect(Collectors.joining(",\n"));
                setWarningMessage(warning);
            }
        }

		Object[] processedObjects = new Object[inObjects.length];
		HiLiteTranslator translator = new HiLiteTranslator();
		translator.setMapper(new DefaultHiLiteMapper(groupTable.getHiliteMapping()));
		Object[] outputObject = new Object[2];
		JSONDataTable jsonTable = createJSONTableFromBufferedDataTable(
            exec,
            groupTable.getBufferedTable(), UUID.randomUUID().toString());
		outputObject[0] = jsonTable;
		outputObject[1] = new JSONSelectionTranslator(translator);
		processedObjects[0] = outputObject;
		System.arraycopy(inObjects, 1, processedObjects, 1, inObjects.length-1);
		return processedObjects;
	}
	
	   private static JSONDataTable createJSONTableFromBufferedDataTable(final ExecutionContext exec, final BufferedDataTable inTable, final String tableId) throws CanceledExecutionException {
	        JSONDataTable table = JSONDataTable.newBuilder()
	                .setDataTable(inTable)
	                .setId(tableId)
	                .setFirstRow(1)
	                .setMaxRows((int)inTable.size())
	                .build(exec);
	        return table;
	    }
}
