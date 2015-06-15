package org.knime.dynamic.js.base.boxplot;
import java.util.LinkedHashMap;

import org.knime.base.node.viz.plotter.box.BoxplotCalculator;
import org.knime.base.node.viz.plotter.box.BoxplotStatistics;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnFilter2;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.dynamic.js.DynamicJSConfig;
import org.knime.dynamic.js.DynamicJSProcessor;

public class BoxplotProcessor implements DynamicJSProcessor {
             
    @Override
    public Object[] processInputObjects(PortObject[] inObjects,
            ExecutionContext exec, DynamicJSConfig config) throws Exception {
        BufferedDataTable dt = (BufferedDataTable)inObjects[0];
        BoxplotCalculator bc = new BoxplotCalculator();

        String[] numColumns = ((SettingsModelColumnFilter2)config.getModel("columns")).applyTo(dt.getDataTableSpec()).getIncludes();
        if (numColumns.length == 0) {
            throw new InvalidSettingsException("No numeric columns given");
        }

        Object stats = bc.calculateMultiple(dt, numColumns, exec);
        return new Object[] {stats};

    }

}
