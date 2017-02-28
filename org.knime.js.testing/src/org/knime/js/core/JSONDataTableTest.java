/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
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
 * ---------------------------------------------------------------------
 *
 * History
 *   11.11.2016 (thor): created
 */
package org.knime.js.core;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashMap;

import org.junit.Test;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTable;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.container.ContainerTable;
import org.knime.core.data.container.DataContainer;
import org.knime.core.data.date.DateAndTimeCell;
import org.knime.core.data.date.DateAndTimeCellFactory;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.time.duration.DurationCellFactory;
import org.knime.core.data.time.localdate.LocalDateCellFactory;
import org.knime.core.data.time.localdatetime.LocalDateTimeCellFactory;
import org.knime.core.data.time.localtime.LocalTimeCellFactory;
import org.knime.core.data.time.period.PeriodCellFactory;
import org.knime.core.data.time.zoneddatetime.ZonedDateTimeCellFactory;
import org.knime.core.node.DefaultNodeProgressMonitor;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.Node;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeModel;
import org.knime.core.node.port.PortType;
import org.knime.core.node.workflow.SingleNodeContainer;
import org.knime.core.node.workflow.virtual.parchunk.VirtualParallelizedChunkPortObjectInNodeFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Testcases for {@link JSONDataTable}.
 *
 * @author Thorsten Meinl, KNIME.com, Zurich, Switzerland
 */
public class JSONDataTableTest {
    /**
     * Checks that serialization of a {@link DataTable} via a {@link JSONDataTable} works as expected (see
     * also AP-6661).
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void testSerialization() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        DataTableSpec tableSpec = new DataTableSpec(
            new DataColumnSpecCreator("col1", StringCell.TYPE).createSpec(),
            new DataColumnSpecCreator("col2", IntCell.TYPE).createSpec(),
            new DataColumnSpecCreator("col3", DoubleCell.TYPE).createSpec(),
            new DataColumnSpecCreator("col4", DateAndTimeCell.TYPE).createSpec()
        );
        DataRow expectedRow = new DefaultRow("r0", Arrays.asList(
            new StringCell("Some value"),
            new IntCell(1),
            new DoubleCell(1.2),
            DateAndTimeCellFactory.create("2007-12-03T10:30:31.124")
        ));

        DataContainer cont = new DataContainer(tableSpec);

        cont.addRowToTable(expectedRow);
        cont.close();
        DataTable expectedTable = cont.getTable();

        NodeFactory<NodeModel> dummyFactory =
            (NodeFactory)new VirtualParallelizedChunkPortObjectInNodeFactory(new PortType[0]);
        ExecutionContext execContext = new ExecutionContext(new DefaultNodeProgressMonitor(), new Node(dummyFactory),
            SingleNodeContainer.MemoryPolicy.CacheOnDisc, new HashMap<Integer, ContainerTable>());

        String json = mapper.writer().writeValueAsString(
            JSONDataTable.newBuilder().setDataTable(expectedTable).setFirstRow(1).setMaxRows(1).build(execContext));

        ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            JSONDataTable deserializedJsonTable = mapper.reader().forType(JSONDataTable.class).readValue(json);
            DataTable actualTable = deserializedJsonTable.createBufferedDataTable(execContext);
            assertThat("Unexpected deserialized spec", actualTable.getDataTableSpec(),
                is(expectedTable.getDataTableSpec()));

            DataRow actualRow = actualTable.iterator().next();
            assertThat("Unexpected row key", actualRow.getKey(), is(expectedRow.getKey()));
            assertThat("Unexpected cell (StringCell)", actualRow.getCell(0), is(expectedRow.getCell(0)));
            assertThat("Unexpected cell (IntCell)", actualRow.getCell(1), is(expectedRow.getCell(1)));
            assertThat("Unexpected cell (DoubleCell)", actualRow.getCell(2), is(expectedRow.getCell(2)));
            assertThat("Unexpected cell (DateAndTimeCell)", actualRow.getCell(3), is(expectedRow.getCell(3)));
        } finally {
            Thread.currentThread().setContextClassLoader(oldLoader);
        }
    }

    /**
     * Checks that serialization of the new date/time types works as expected. (see AP-6967)
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void testDateTimeSerialization() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        DataTableSpec tableSpec = new DataTableSpec(
            new DataColumnSpecCreator("col1", DateAndTimeCell.TYPE).createSpec(),
            new DataColumnSpecCreator("col2", LocalDateCellFactory.TYPE).createSpec(),
            new DataColumnSpecCreator("col3", LocalDateTimeCellFactory.TYPE).createSpec(),
            new DataColumnSpecCreator("col4", LocalTimeCellFactory.TYPE).createSpec(),
            new DataColumnSpecCreator("col5", ZonedDateTimeCellFactory.TYPE).createSpec(),
            new DataColumnSpecCreator("col6", PeriodCellFactory.TYPE).createSpec(),
            new DataColumnSpecCreator("col7", DurationCellFactory.TYPE).createSpec()
        );
        DataRow expectedRow = new DefaultRow("r0", Arrays.asList(
            DateAndTimeCellFactory.create("2007-12-03T10:30:31.124"),
            LocalDateCellFactory.create("2007-12-03"),
            LocalDateTimeCellFactory.create("2007-12-03T10:15:30"),
            LocalTimeCellFactory.create("10:15:30"),
            ZonedDateTimeCellFactory.create("2007-12-03T10:15:30+01:00[Europe/Paris]"),
            PeriodCellFactory.create("P1Y2M3W4D"),
            DurationCellFactory.create("PT20.345S")
        ));

        DataContainer cont = new DataContainer(tableSpec);

        cont.addRowToTable(expectedRow);
        cont.close();
        DataTable expectedTable = cont.getTable();

        NodeFactory<NodeModel> dummyFactory =
            (NodeFactory)new VirtualParallelizedChunkPortObjectInNodeFactory(new PortType[0]);
        ExecutionContext execContext = new ExecutionContext(new DefaultNodeProgressMonitor(), new Node(dummyFactory),
            SingleNodeContainer.MemoryPolicy.CacheOnDisc, new HashMap<Integer, ContainerTable>());

        String json = mapper.writer().writeValueAsString(
            JSONDataTable.newBuilder().setDataTable(expectedTable).setFirstRow(1).setMaxRows(1).build(execContext));

        ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            JSONDataTable deserializedJsonTable = mapper.reader().forType(JSONDataTable.class).readValue(json);
            DataTable actualTable = deserializedJsonTable.createBufferedDataTable(execContext);
            assertThat("Unexpected deserialized spec", actualTable.getDataTableSpec(),
                is(expectedTable.getDataTableSpec()));

            DataRow actualRow = actualTable.iterator().next();
            assertThat("Unexpected row key", actualRow.getKey(), is(expectedRow.getKey()));
            assertThat("Unexpected cell (DateAndTimeCell)", actualRow.getCell(0), is(expectedRow.getCell(0)));
            assertThat("Unexpected cell (LocalDateCell)", actualRow.getCell(1), is(expectedRow.getCell(1)));
            assertThat("Unexpected cell (LocalDateTimeCell)", actualRow.getCell(2), is(expectedRow.getCell(2)));
            assertThat("Unexpected cell (LocalTimeCell)", actualRow.getCell(3), is(expectedRow.getCell(3)));
            assertThat("Unexpected cell (ZonedDateTimeCell)", actualRow.getCell(4), is(expectedRow.getCell(4)));
            assertThat("Unexpected cell (PeriodCell)", actualRow.getCell(5), is(expectedRow.getCell(5)));
            assertThat("Unexpected cell (DurationCell)", actualRow.getCell(6), is(expectedRow.getCell(6)));
        } finally {
            Thread.currentThread().setContextClassLoader(oldLoader);
        }
    }
}
