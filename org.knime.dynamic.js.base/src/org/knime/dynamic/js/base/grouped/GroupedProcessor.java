package org.knime.dynamic.js.base.grouped;

import java.util.HashSet;
import java.util.Set;

import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.dynamic.js.DynamicJSConfig;
import org.knime.dynamic.js.DynamicJSProcessor;

public class GroupedProcessor implements DynamicJSProcessor {

	@Override
	public Object[] processInputObjects(PortObject[] inObjects,
			ExecutionContext exec, DynamicJSConfig config) throws Exception {
		BufferedDataTable table = (BufferedDataTable)inObjects[0];
		String colName = ((SettingsModelString)config.getModel("cat")).getStringValue();
		if (colName == null) {
			throw new IllegalArgumentException("No column selected for category values.");
		}
		int columnIndex = table.getDataTableSpec().findColumnIndex(colName);
		if (columnIndex < 0) {
			throw new IllegalArgumentException("Index for column with name " + colName + " not found.");
		}
		Set<String> possibleValues = new HashSet<String>();
		try (CloseableRowIterator iterator = table.iterator()) {
			while (iterator.hasNext()) {
				StringCell cell = (StringCell)iterator.next().getCell(columnIndex);
				String value = cell.getStringValue();
				if (possibleValues.contains(value)) {
					throw new IllegalArgumentException("Selected category column contains non unique values.");
				} else {
					possibleValues.add(value);
				}
			}
		}
		return inObjects;
	}

}
