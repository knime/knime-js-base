package org.knime.dynamic.js.base.voronoi;
import org.knime.core.data.DataRow;
import org.knime.core.data.container.DataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.dynamic.js.v30.DynamicJSConfig;
import org.knime.dynamic.js.v30.DynamicJSProcessor;

public class VoronoiProcessor implements DynamicJSProcessor {

    @Override
    public Object[] processInputObjects(final PortObject[] inObjects, final ExecutionContext exec, final DynamicJSConfig config)
            throws Exception {
        BufferedDataTable dt = (BufferedDataTable)inObjects[0];

        SettingsModelString xCol = (SettingsModelString)config.getModel("xCol");
        SettingsModelString yCol = (SettingsModelString)config.getModel("yCol");

        int xIdx = dt.getSpec().findColumnIndex(xCol.getStringValue());
        int yIdx = dt.getSpec().findColumnIndex(yCol.getStringValue());

        if (xIdx == -1) {
            throw new InvalidSettingsException("No x-column given");
        }
        if (yIdx == -1) {
            throw new InvalidSettingsException("No y-column given");
        }

        //int filteredCount = 0;
        DataContainer dc = exec.createDataContainer(dt.getDataTableSpec());
        for (DataRow row : dt) {
            if (!row.getCell(xIdx).isMissing() && !row.getCell(yIdx).isMissing()) {
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

        return new Object[] {dc.getTable()};
    }

}
