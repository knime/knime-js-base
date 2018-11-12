package org.knime.dynamic.js.base.grouped;

import java.util.Arrays;
import java.util.Map;
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
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.defaultnodesettings.SettingsModel;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnFilter2;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.property.hilite.DefaultHiLiteMapper;
import org.knime.core.node.property.hilite.HiLiteTranslator;
import org.knime.core.node.util.filter.NameFilterConfiguration.FilterResult;
import org.knime.dynamic.js.v30.DynamicJSConfig;
import org.knime.dynamic.js.v30.DynamicStatefulJSProcessor;
import org.knime.js.core.JSONDataTable;
import org.knime.js.core.JSONDataTable.Builder;
import org.knime.js.core.selections.json.JSONSelectionTranslator;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 *
 */
public class GroupedProcessor extends DynamicStatefulJSProcessor {
	
    private static final String COUNT = "Occurence\u00A0Count";
	private static final String SUM = "Sum";
	private static final String AVG = "Average";
	private static final String[] AVAILABLE_METHODS = new String[]{COUNT, SUM, AVG};
	
	/**
	 * {@inheritDoc}
	 */
	@Override
    public Object[] processInputObjects(PortObject[] inObjects, ExecutionContext exec, DynamicJSConfig config)
        throws Exception {
	    GroupingResult result = processInputObjects(inObjects, exec, config, true); 
        return new Object[] {result};
	}
	
    /**
     * @param inObjects The input objects.
     * @param exec An execution context used during execute to set progress and check for cancellation.
     * @param config The configuration object containing the current node settings.
     * @param serializeTable true if the result should contain the serialized {@link JSONDataTable}, false if the 
     * generated {@link BufferedDataTable} should be contained 
     * @return a {@link GroupingResult} containing the grouped table and a row map for interactive selections support.
     * @throws Exception If processing the inputs fails for any reason.
     */
    protected GroupingResult processInputObjects(final PortObject[] inObjects, final ExecutionContext exec,
        final DynamicJSConfig config, final boolean serializeTable) throws Exception {
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
            colAggregators, GlobalSettings.DEFAULT, /*FIXME: I need to be configurable! */true, ColumnNamePolicy.KEEP_ORIGINAL_NAME, false);
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

        GroupingResult result = new GroupingResult();
		if (serializeTable) {
		    Builder builder = JSONDataTable.newBuilder()
		            .setDataTable(groupTable.getBufferedTable())
		            .setId(UUID.randomUUID().toString())
		            .setFirstRow(1)
		            .setMaxRows((int)groupTable.getBufferedTable().size());
		    result.setTable(builder.build(exec));
		} else {
		    result.setDataTable(groupTable.getBufferedTable());
		}
		result.setUUID(UUID.randomUUID().toString());
		HiLiteTranslator translator = new HiLiteTranslator();
		translator.setMapper(new DefaultHiLiteMapper(groupTable.getHiliteMapping()));
		result.setTranslator(new JSONSelectionTranslator(translator));
		return result;
	}
	   
    @JsonAutoDetect
    public static final class GroupingResult {
        
        private JSONDataTable m_table;
        private BufferedDataTable m_dataTable;
        private String m_UUID;
        private JSONSelectionTranslator m_translator;
        
        public JSONDataTable getTable() {
            return m_table;
        }
        
        public void setTable(JSONDataTable table) {
            m_table = table;
        }
        
        @JsonIgnore
        protected BufferedDataTable getDataTable() {
            return m_dataTable;
        }
        
        @JsonIgnore
        protected void setDataTable(BufferedDataTable dataTable) {
            m_dataTable = dataTable;
        }
        
        public String getUUID() {
            return m_UUID;
        }
        
        public void setUUID(String uuid) {
            m_UUID = uuid;
        }
        
        public JSONSelectionTranslator getTranslator() {
            return m_translator;
        }
        
        public void setTranslator(JSONSelectionTranslator translator) {
            m_translator = translator;
        }
    }
}
