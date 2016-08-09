package org.knime.dynamic.js.base.parallelcoords;
import org.knime.core.data.DataRow;
import org.knime.core.data.container.DataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnFilter2;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.util.filter.NameFilterConfiguration.FilterResult;
import org.knime.dynamic.js.v30.DynamicJSConfig;
import org.knime.dynamic.js.v30.DynamicJSProcessor;

public class ParallelCoordsProcessor implements DynamicJSProcessor {

    @Override
    public Object[] processInputObjects(final PortObject[] inObjects, final ExecutionContext exec, final DynamicJSConfig config)
            throws Exception {
        BufferedDataTable dt = (BufferedDataTable)inObjects[0];

        SettingsModelColumnFilter2 filter = (SettingsModelColumnFilter2)config.getModel("columns");
        FilterResult res = filter.applyTo(dt.getDataTableSpec());
        if (res.getIncludes().length == 0) {
            throw new InvalidSettingsException("No columns selected");
        }

        //int filteredCount = 0;
        DataContainer dc = exec.createDataContainer(dt.getDataTableSpec());
        for (DataRow row : dt) {
            boolean ignore = false;
            for (int i = 0; i < row.getNumCells() && !ignore; i++) {
                if (row.getCell(i).isMissing()) {
                    ignore = true;
                    //filteredCount++;
                }
            }
            if (!ignore) {
                dc.addRowToTable(row);
            }
        }
        dc.close();
        /*
         * TODO: Show warning message
        if (filteredCount > 0) {
            setWarningMessage(filteredCount + " rows contain missing values and are ignored.");
        }
         */

        return new Object[] {dc.getTable()};
    }

}
