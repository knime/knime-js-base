package org.knime.dynamic.js.base.sunburst;
import java.util.Arrays;
import java.util.stream.Stream;

import org.knime.core.data.DataRow;
import org.knime.core.data.container.DataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnFilter2;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.dynamic.js.v30.DynamicJSConfig;
import org.knime.dynamic.js.v30.DynamicJSProcessor;
import org.knime.base.data.filter.column.FilterColumnTable;

public class SunburstProcessor implements DynamicJSProcessor {
             
    @Override
    public Object[] processInputObjects(PortObject[] inObjects,
            ExecutionContext exec, DynamicJSConfig config) throws Exception {
    	
        BufferedDataTable dt = (BufferedDataTable)inObjects[0];

        // Get columns selected for path.
        String[] pathColumns = ((SettingsModelColumnFilter2)config.getModel("pathColumns")).applyTo(dt.getDataTableSpec()).getIncludes();
        
        // Get column selected for frequency.
        String freqColumn = ((SettingsModelString)config.getModel("freqColumn")).getStringValue();
               
        // Concatenate y-axis and x-axis columns, but only include columns that exist.
        String[] includeColumns = Stream
        		.concat(Arrays.stream(pathColumns), Stream.of(freqColumn))
        		.filter(p -> p != null)
        		.distinct()
                .toArray(String[]::new);

        
//		JSONDataTable table = JSONDataTable.newBuilder()
//			.setDataTable(dt)
//			.setFirstRow(1)
//			.setMaxRows(config.getMaxRows())
//			.setIncludeColumns(includeColumns)
//			.excludeRowsWithMissingValues(false)
//			// TODO: keep Columns with...
//			// TODO: Can we exclude here rows where all values missing?
//			.build(exec);
//        
//
//		int removed = table.numberRemovedRowsWithMissingValues();
//		if (removed > 0) {
//			setWarningMessage("Table contained " + removed + " rows with missing values. These rows are ignored in the view.");
//		}
//
//		return new Object[] {table}; //, inObjects[1]};
        
        // Deprecated
        FilterColumnTable ft = new FilterColumnTable(dt, includeColumns);
        DataContainer dc = exec.createDataContainer(ft.getDataTableSpec());
        for (DataRow row : ft) {
      		dc.addRowToTable(row);
        }
        dc.close();
        return new Object[] {dc.getTable(), inObjects[1]}; //, inObjects[1]};
    }

}
