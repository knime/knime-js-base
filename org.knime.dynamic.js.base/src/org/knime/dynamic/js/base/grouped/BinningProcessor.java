package org.knime.dynamic.js.base.grouped;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.knime.base.node.preproc.autobinner3.AutoBinner;
import org.knime.base.node.preproc.autobinner3.AutoBinnerLearnSettings;
import org.knime.base.node.preproc.autobinner3.AutoBinnerLearnSettings.BinNaming;
import org.knime.base.node.preproc.autobinner.apply.AutoBinnerApply;
import org.knime.base.node.preproc.autobinner.pmml.PMMLPreprocDiscretize;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataRow;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.sort.BufferedDataTableSorter;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.util.filter.InputFilter;
import org.knime.core.node.util.filter.column.DataColumnSpecFilterConfiguration;
import org.knime.dynamic.js.v30.DynamicJSConfig;

/**
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 *
 */
public class BinningProcessor extends GroupedProcessor {

    @Override
    public Object[] processInputObjects(PortObject[] inObjects, ExecutionContext exec, DynamicJSConfig config)
        throws Exception {
        final BufferedDataTable table = (BufferedDataTable)inObjects[0];
        SettingsModelString catCol = (SettingsModelString)config.getModel("cat");

        //Check bin column settings
        String catColName = catCol.getStringValue();
        if (catColName == null) {
            throw new IllegalArgumentException("No column selected for binning.");
        }
        int columnIndex = table.getDataTableSpec().findColumnIndex(catColName);
        if (columnIndex < 0) {
            throw new IllegalArgumentException("Index for binning column with name " + catColName + " not found.");
        }

        //Create bin column with user settings
        AutoBinnerLearnSettings binnerSettings = new AutoBinnerLearnSettings();
        binnerSettings.setFilterConfiguration(new DataColumnSpecFilterConfiguration("filter", new InputFilter<DataColumnSpec>() {
            
            @Override
            public boolean include(DataColumnSpec name) {
                return name.getName().equals(catColName);
            }
        }));
        binnerSettings.setBinNaming(BinNaming.edges);
        AutoBinner binner = new AutoBinner(binnerSettings, table.getDataTableSpec());
        BufferedDataTable inData = binner.calcDomainBoundsIfNeccessary(table, exec, Arrays.asList(catColName));
        PMMLPreprocDiscretize op = binner.execute(inData, exec.createSubExecutionContext(0.25));
        List<String> binnedNames = op.getConfiguration().getNames();
        assert binnedNames.size() == 1;
        String binnedColName = binnedNames.get(0);
        List<String> orderedBinNames = op.getConfiguration().getDiscretize(binnedColName).getBins().stream()
            .map(e -> e.getBinValue()).collect(Collectors.toList());
        AutoBinnerApply applier = new AutoBinnerApply();
        BufferedDataTable outData = applier.execute(op, table, exec.createSubExecutionContext(0.25));

        //Group table with bin column according to user settings on GroupedProcessor
        catCol.setStringValue(binnedColName);
        Object[] grouped =
            super.processInputObjects(new PortObject[]{outData}, exec.createSubExecutionContext(0.5), config);
        BufferedDataTable groupedTable = (BufferedDataTable)grouped[0];
        //catCol.setStringValue(catColName);
        
        //Make sure bins are sorted correctly in output table
        final int binIndex =
            Arrays.asList(groupedTable.getDataTableSpec().getColumnNames()).indexOf(binnedColName);
        Comparator<DataRow> comp = new Comparator<DataRow>() {

            @Override
            public int compare(DataRow o1, DataRow o2) {
                int bin1 = orderedBinNames.indexOf(((StringCell)o1.getCell(binIndex)).getStringValue());
                int bin2 = orderedBinNames.indexOf(((StringCell)o2.getCell(binIndex)).getStringValue());
                return Integer.compare(bin1, bin2);
            }
        };
        BufferedDataTableSorter sorter = new BufferedDataTableSorter(groupedTable, comp);
        sorter.setSortInMemory(true);
        return new Object[]{sorter.sort(exec.createSubExecutionContext(0.01))};
    }

}
