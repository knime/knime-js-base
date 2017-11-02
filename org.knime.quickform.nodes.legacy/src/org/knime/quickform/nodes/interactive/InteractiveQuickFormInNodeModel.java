/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 * History:
 * 24-Febr-2011: created
 */
package org.knime.quickform.nodes.interactive;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.knime.core.data.BooleanValue;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.RowKey;
import org.knime.core.data.StringValue;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.property.hilite.HiLiteHandler;
import org.knime.quickform.nodes.interactive.JSONDataTableSpec.JSTypes;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Node for boolean input.
 *
 * @author Christian Albrecht, KNIME AG, Zurich, Switzerland
 * @since 2.7
 */
public class InteractiveQuickFormInNodeModel extends NodeModel {

    private RowKey[] m_rowKeys;

    /**
     * @param nrInDataPorts
     * @param nrOutDataPorts
     */
    protected InteractiveQuickFormInNodeModel() {
        super(1, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
        // TODO Auto-generated method stub
        return inSpecs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
            throws Exception {

        BufferedDataTable table = inData[0];
        DataTableSpec spec = table.getDataTableSpec();
        m_rowKeys = new RowKey[table.getRowCount()];
        JSONDataTableSpec jsonTableSpec = new JSONDataTableSpec(spec, table.getRowCount());
        jsonTableSpec.addExtension("hilite", JSTypes.BOOLEAN);

        int currentRow = 0;
        int rowCount = table.getRowCount();
        HiLiteHandler hilite = getInHiLiteHandler(0);
        Object[][] dataArray = new Object[rowCount][spec.getNumColumns()];
        Object[][] extensionArray = new Object[rowCount][1];

        for (DataRow row : table) {
            // check if the user cancelled the execution
            exec.checkCanceled();
            // report progress
            exec.setProgress((double)currentRow / rowCount, " processing row " + currentRow);

            m_rowKeys[currentRow] = row.getKey();

            for (int i = 0; i < row.getNumCells(); i++) {
                DataCell cell = row.getCell(i);
                if (!cell.isMissing()) {
                    JSTypes jsType = JSONDataTableSpec.getJSONType(cell.getType());

                    switch (jsType) {
                        case BOOLEAN:
                            dataArray[currentRow][i] = ((BooleanValue)cell).getBooleanValue();
                            break;
                        case NUMBER:
                            dataArray[currentRow][i] = ((DoubleValue)cell).getDoubleValue();
                            break;
                        case STRING:
                            dataArray[currentRow][i] = ((StringValue)cell).getStringValue();
                            break;

                        default:
                            dataArray[currentRow][i] = null;
                            break;
                    }

                } else {
                    dataArray[currentRow][i] = null;
                }
            }

            extensionArray[currentRow][0] = hilite.isHiLit(row.getKey());
            currentRow++;
        }
        JSONDataTable jsonTable = new JSONDataTable();
        jsonTable.setSpec(jsonTableSpec);
        jsonTable.setData(dataArray);
        jsonTable.setExtensions(extensionArray);

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(jsonTable);

        showInBrowser(jsonString);

        //back
/*
        JSONDataTable parsedTable = mapper.readValue(jsonString, JSONDataTable.class);
        DataContainer newData = new DataContainer(spec);
        int i = 0;
        for (Object[] rowObjects : parsedTable.getData()) {
            i++;

            DataCell[] cells = new DataCell[rowObjects.length];

            for (int j = 0; j < rowObjects.length; j++) {
                new DoubleCell()
                spec.getColumnSpec(0).
            }

            DataRow row = new DefaultRow(String.valueOf(i), cells);
            newData.addRowToTable(row);
        }*/

        return inData;
    }

    /**
     * @param jsonTable
     * @throws IOException
     */
    private void showInBrowser(final String jsonTable) throws IOException {

        Display display = new Display();
        final Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());
        final Browser browser = new Browser(shell, SWT.NONE);

        browser.addProgressListener(new ProgressListener() {

            @Override
            public void completed(final ProgressEvent event) {
                browser.evaluate("try {"
                        + "kc.setDataTable('" + jsonTable + "');"
                        /*+ "var tableView = kc.tableView();"
                        + "kc.registerView(tableView);"
                        + "tableView.init();"*/
                        + "var lineChart = kc.lineChart();"
                        + "kc.registerView(lineChart);"
                        + "lineChart.init(\"view1\");"
                        + "var flotLineChart = kc.flotLineChart();"
                        + "kc.registerView(flotLineChart);"
                        + "flotLineChart.init(\"view2\");"
                        + " } catch(err) {"
                        + "console.error(err)}");
            }

            @Override
            public void changed(final ProgressEvent event) {
                // TODO Auto-generated method stub

            }
        });

        String setIEVersion = "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=10\">";
        /*String jsonParser = "<script type=\"text/javascript\">"
                + getFileContent("/src/org/knime/quickform/nodes/interactive/json_parser.js")
                + "</script>";*/
        boolean isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().
                getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
        String debugScript = "<script type=\"text/javascript\" "
                + "src=\"https://getfirebug.com/firebug-lite.js#startOpened=true\"></script>";
        String jqueryScript = "<script src=\"js/jquery-1.9.1.js\"></script>";
        String jTableScript = "<script src=\"js/jquery.dataTables.js\"></script>";
        String d3Script = "<script type=\"text/javascript\" src=\"js/d3.v3.min.js\"></script>";
        String kChartScript = "<script type=\"text/javascript\" src=\"js/kc-0.0.1.js\"></script>";
        String tableViewScript = "<script type=\"text/javascript\" src=\"js/kc_tableView-0.0.1.js\"></script>";
        String lineChartScript = "<script type=\"text/javascript\" src=\"js/kc_lineChart-0.0.1.js\"></script>";
        String flotScript = "<script type=\"text/javascript\" src=\"js/jquery.flot.js\"></script>";
        String flotSelScript = "<script type=\"text/javascript\" src=\"js/jquery.flot.selection.js\"></script>";
        String flotLineChart = "<script type=\"text/javascript\" src=\"js/kc_flotLineChart-0.0.1.js\"></script>";
        String jqueryUIStyle = "<link rel=\"stylesheet\" type=\"text/css\" href=\"css/jquery-ui-1.10.2.custom.css\">";
        String tableStyles = "<link rel=\"stylesheet\" type=\"text/css\" href=\"css/demo_table.css\">";
        String d3Styles = "<link rel=\"stylesheet\" type=\"text/css\" href=\"css/d3.css\">";

        StringBuilder pageBuilder = new StringBuilder();
        pageBuilder.append("<!doctype html><html><head>");
        pageBuilder.append(setIEVersion);
        if (isDebug) {
            pageBuilder.append(debugScript);
        } else {
            pageBuilder.append(jqueryUIStyle);
            pageBuilder.append(tableStyles);
            pageBuilder.append(d3Styles);
        }
        pageBuilder.append(jqueryScript);
        //pageBuilder.append(jTableScript);
        pageBuilder.append(d3Script);
        pageBuilder.append(kChartScript);
        //pageBuilder.append(tableViewScript);
        pageBuilder.append(lineChartScript);
        pageBuilder.append(flotScript);
        pageBuilder.append(flotSelScript);
        pageBuilder.append(flotLineChart);
        pageBuilder.append("</head><body>");
        pageBuilder.append("<div id=\"view1\" class=\"container\"></div>");
        pageBuilder.append("<div id=\"view2\" class=\"container\"></div>");
        //pageBuilder.append("<div id=\"view3\" class=\"container\"></div>");
     //   pageBuilder.append("<input type=\"button\" value=\"Submit\" onclick=\"getData();\">");
     //   pageBuilder.append("<input type=\"button\" value=\"Clear hilite\" onclick=\"kc.getExtension('hilite').fireClearHilite();\">");
        pageBuilder.append("</body></html>");

        //browser.setUrl("http://leda03.inf.uni-konstanz.de:8080/svg-demo");
        File tempFile = createTempHTMLFile(pageBuilder.toString());
        browser.setUrl(tempFile.getAbsolutePath());

        new PullData(browser, "pushData");

        shell.open();



        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
        tempFile.delete();
    }

    static String getPathToPlugin() {
      URL url = null;
      File dir = null;

      try {
       url = new URL("platform:/plugin/org.knime.quickform.nodes");
      } catch (MalformedURLException e) {
          //do nothing
      }

      if (url != null) {
       try {
        dir = new File(FileLocator.resolve(url).getFile());
       } catch (IOException e) {
           //do nothing
       }
      }

      if (dir == null) {
       //something went wrong return null
       return null;
      } else {
       return dir.getAbsolutePath();
      }
     }

    private File createTempHTMLFile(final String content) throws IOException {
        File tempFile = new File(getPathToPlugin() + "/src/org/knime/quickform/nodes/interactive/html/json_parse.html");
        if (tempFile.exists()) {
            tempFile.delete();
        }
        tempFile.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
        writer.write(content);
        writer.flush();
        writer.close();
        return tempFile;
    }

    private static String getFileContent(final String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File(getPathToPlugin() + fileName)));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line + "\n");
        }
        return builder.toString();
    }

    /**
     * @param jsonString
     */
    private JSONDataTable parseJSONString(final String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonString, JSONDataTable.class);
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * @param jsonString
     */
    /*public void handleHilite(final JSONDataTable table) {
        int hCol = -1;
        for (int i = 0; i < table.getSpec().getNumColumns(); i++) {
            if (table.getSpec().getExtensionNames()[i].equals("hilite")) {
                hCol = i;
                break;
            }
        }

        if (hCol > -1) {
            HiLiteHandler hlh = this.getInHiLiteHandler(0);
            hlh.fireClearHiLiteEvent();
            ArrayList<RowKey> highlighted = new ArrayList<RowKey>();
            for (int i = 0; i < table.getSpec().getNumRows(); i++) {
                if ((Boolean)table.getExtensions()[i][hCol]) {
                    highlighted.add(m_rowKeys[i]);
                }
            }
            hlh.fireHiLiteEvent(highlighted.toArray(new RowKey[0]));
        }
    }*/

    public void handleHilite(final Boolean[] hiliteIDs) {

            HiLiteHandler hlh = this.getInHiLiteHandler(0);
            hlh.fireClearHiLiteEvent();
            ArrayList<RowKey> highlighted = new ArrayList<RowKey>();
            for (int i = 0; i < hiliteIDs.length; i++) {
                if (hiliteIDs[i]) {
                    highlighted.add(m_rowKeys[i]);
                }
            }
            hlh.fireHiLiteEvent(highlighted.toArray(new RowKey[0]));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File nodeInternDir, final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File nodeInternDir, final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        // TODO Auto-generated method stub

    }

    /**
     *
     * @author Christian Albrecht, KNIME AG, Zurich, Switzerland
     */
    public class PullData extends BrowserFunction {

        PullData(final Browser browser, final String name) {
            super(browser, name);
        }

        @Override
        public Object function(final Object[] arguments) {
            String jsonString = (String)arguments[0];
            //JSONDataTable table = parseJSONString(jsonString);
            //handleHilite(table);
            ObjectMapper mapper = new ObjectMapper();
            try {
                Boolean[] hiliteIDs = mapper.readValue(jsonString, Boolean[].class);
                handleHilite(hiliteIDs);
            } catch (Exception e) {
                // TODO Auto-generated catch block
            }
            return null;
        }
     }

}
