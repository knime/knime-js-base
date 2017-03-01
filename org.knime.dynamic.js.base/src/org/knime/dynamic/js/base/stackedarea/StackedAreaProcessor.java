package org.knime.dynamic.js.base.stackedarea;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import org.knime.core.data.sort.BufferedDataTableSorter;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnFilter2;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.dynamic.js.v30.DynamicJSConfig;
import org.knime.dynamic.js.v30.DynamicJSProcessor;
import org.knime.js.core.JSONDataTable;

public class StackedAreaProcessor implements DynamicJSProcessor {
             
    @Override
    public Object[] processInputObjects(PortObject[] inObjects,
            ExecutionContext exec, DynamicJSConfig config) throws Exception {
    	
        BufferedDataTable dt = (BufferedDataTable)inObjects[0];

        // Get columns selected for y-axis.
        String[] yAxisColumns = ((SettingsModelColumnFilter2)config.getModel("columns")).applyTo(dt.getDataTableSpec()).getIncludes();
        
        // Get column selected for x-axis.
        String xAxisColumn = ((SettingsModelString)config.getModel("xAxisColumn")).getStringValue();
        // Sort for selected column.         	
        boolean sort = ((SettingsModelBoolean)config.getModel("xAxisSort")).getBooleanValue();
        // Is the column unequal RowID?
        if (xAxisColumn != null) {
        	
        	if (sort) {
	            BufferedDataTableSorter sorter = new BufferedDataTableSorter(dt, Collections.singletonList(xAxisColumn), new boolean[] {true});
	           	dt = sorter.sort(exec.createSubExecutionContext(0.5));
        	}
        }
               
        // Concatenate y-axis and x-axis columns, but only include columns that exist.
        String[] includeColumns = Stream
        		.concat(Arrays.stream(yAxisColumns), Stream.of(xAxisColumn))
        		.filter(p -> p != null)
        		.distinct()
                .toArray(String[]::new);
        
        JSONDataTable table = JSONDataTable.newBuilder()
        		.setDataTable(dt)
        		.setFirstRow(1)
        		.setMaxRows(config.getMaxRows())
        		.setIncludeColumns(includeColumns)
        		.excludeRowsWithMissingValues(true)
        		.build(sort ? exec.createSubExecutionContext(0.5): exec);

        int removed = table.numberRemovedRowsWithMissingValues();
        if (removed > 0) {
        	setWarningMessage("Table contained " + removed + " rows with missing values. These rows are ignored in the view.");
        }

        return new Object[] {table, inObjects[1]};
    }

}
