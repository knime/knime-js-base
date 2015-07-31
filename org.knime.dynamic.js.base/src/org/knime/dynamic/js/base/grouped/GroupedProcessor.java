package org.knime.dynamic.js.base.grouped;

import java.util.HashSet;
import java.util.Set;

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.defaultnodesettings.SettingsModel;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnFilter2;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.util.filter.NameFilterConfiguration.FilterResult;
import org.knime.dynamic.js.DynamicJSConfig;
import org.knime.dynamic.js.DynamicJSProcessor;

public class GroupedProcessor implements DynamicJSProcessor {

	@Override
	public Object[] processInputObjects(PortObject[] inObjects,
			ExecutionContext exec, DynamicJSConfig config) throws Exception {
		BufferedDataTable table = (BufferedDataTable)inObjects[0];
		//Check category column settings
		String colName = ((SettingsModelString)config.getModel("cat")).getStringValue();
		if (colName == null) {
			throw new IllegalArgumentException("No column selected for category values.");
		}
		int columnIndex = table.getDataTableSpec().findColumnIndex(colName);
		if (columnIndex < 0) {
			throw new IllegalArgumentException("Index for category column with name " + colName + " not found.");
		}
		//Check uniqueness of category values
		Set<String> possibleValues = new HashSet<String>();
		try (CloseableRowIterator iterator = table.iterator()) {
			while (iterator.hasNext()) {
				StringCell cell = (StringCell)iterator.next().getCell(columnIndex);
				String value = cell.getStringValue();
				if (possibleValues.contains(value)) {
					throw new IllegalArgumentException("Selected category column contains non unique values. Please use a \"GroupBy\" node, or similar, to aggregate data first.");
				} else {
					possibleValues.add(value);
				}
			}
		}
		
		//Check frequency column(s) settings
		final SettingsModel freqModel = config.getModel("freq");
		if (freqModel instanceof SettingsModelString) {
			colName = ((SettingsModelString)freqModel).getStringValue();
			if (colName == null) {
				throw new IllegalArgumentException("No column selected for frequency values.");
			}
			columnIndex = table.getDataTableSpec().findColumnIndex(colName);
			if (columnIndex < 0) {
				throw new IllegalArgumentException("Index for frequency column with name " + colName + " not found.");
			}
		} else if (freqModel instanceof SettingsModelColumnFilter2) {
			DataTableSpec inSpec = ((BufferedDataTable)inObjects[0]).getDataTableSpec();
			FilterResult filterResult = ((SettingsModelColumnFilter2)freqModel).applyTo(inSpec);
			if (filterResult.getIncludes().length < 1) {
				throw new IllegalArgumentException("Frequency column filter include list empty. Select at least one frequency column.");
			}
		}
		return inObjects;
	}
}
