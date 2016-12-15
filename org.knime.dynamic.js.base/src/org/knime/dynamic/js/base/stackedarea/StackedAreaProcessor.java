package org.knime.dynamic.js.base.stackedarea;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import org.knime.core.data.DataRow;
import org.knime.core.data.container.DataContainer;
import org.knime.core.data.sort.BufferedDataTableSorter;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnFilter2;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.dynamic.js.v30.DynamicJSConfig;
import org.knime.dynamic.js.v30.DynamicJSProcessor;
import org.knime.base.data.filter.column.FilterColumnTable;

public class StackedAreaProcessor implements DynamicJSProcessor {
             
    @Override
    public Object[] processInputObjects(PortObject[] inObjects,
            ExecutionContext exec, DynamicJSConfig config) throws Exception {
    	
        BufferedDataTable dt = (BufferedDataTable)inObjects[0];

        // Get columns selected for y-axis.
        String[] yAxisColumns = ((SettingsModelColumnFilter2)config.getModel("columns")).applyTo(dt.getDataTableSpec()).getIncludes();
        if (yAxisColumns.length == 0) {
            throw new InvalidSettingsException("No y-axis columns given");
        }
        
        // Get column selected for x-axis.
        String xAxisColumn = ((SettingsModelString)config.getModel("xAxisColumn")).getStringValue();
        // Is the column unequal RowID?
        if (xAxisColumn != null) {
        	
        	// Sort for selected column.         	
        	boolean sort = ((SettingsModelBoolean)config.getModel("xAxisSort")).getBooleanValue();
        	if (sort) {
	            BufferedDataTableSorter sorter = new BufferedDataTableSorter(dt, Collections.singletonList(xAxisColumn), new boolean[] {true});
	           	dt = sorter.sort(exec);
        	}
        }
        
        // Filter out missing values.
        
        // But only include selected columns.
        String[] includeColumns = Stream
        		.concat(Arrays.stream(yAxisColumns), Stream.of(xAxisColumn))
        		.filter(p -> p != null)
                .toArray(String[]::new);

        FilterColumnTable ft = new FilterColumnTable(dt, includeColumns);
        //int filteredCount = 0;
        DataContainer dc = exec.createDataContainer(ft.getDataTableSpec());
        for (DataRow row : ft) {
        	if (row.stream().allMatch(cell -> !cell.isMissing())) {
        		dc.addRowToTable(row);
        		//filteredCount++;
        	}
        }
        dc.close();

        /*
         * TODO: Show warning message
        if (filteredCount > 0) {
            setWarningMessage(filteredCount + " rows contain missing values and are ignored.");
        }
        */

        return new Object[] {dc.getTable(), inObjects[1]};
    }

}
