/*
 * ------------------------------------------------------------------------
 *
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
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
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
 *   Jul 29, 2021 (hornm): created
 */
package org.knime.js.base.node.viz.plotter.line;

import java.util.Map;

import org.knime.core.data.DataColumnDomainCreator;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.node.NodeSettings;
import org.knime.js.views.AbstractUpdateViewValuesOnSpecChangeTest;

/**
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class LinePlotUpdateValuesOnSpecChangeTest extends AbstractUpdateViewValuesOnSpecChangeTest {

    @SuppressWarnings("javadoc")
    public LinePlotUpdateValuesOnSpecChangeTest() {
        super(LinePlotNodeFactory.class.getName(), Map.of(//
            LinePlotViewConfig.Y_COLS, Object.class, //
            LinePlotViewConfig.X_AXIS_MIN, DoubleStoredAsString.class, //
            LinePlotViewConfig.X_AXIS_MAX, DoubleStoredAsString.class, //
            LinePlotViewConfig.Y_AXIS_MIN, DoubleStoredAsString.class, //
            LinePlotViewConfig.Y_AXIS_MAX, DoubleStoredAsString.class), spec1(), spec2());
    }

    private static DataTableSpec spec1() {
        DataColumnDomainCreator colDom = new DataColumnDomainCreator();
        colDom.setLowerBound(new DoubleCell(1));
        colDom.setUpperBound(new DoubleCell(2));
        DataColumnSpecCreator colSpec = new DataColumnSpecCreator("col1", DoubleCell.TYPE);
        colSpec.setDomain(colDom.createDomain());
        return new DataTableSpec(colSpec.createSpec());
    }

    private static DataTableSpec spec2() {
        DataColumnDomainCreator colDom = new DataColumnDomainCreator();
        colDom.setLowerBound(new DoubleCell(10));
        colDom.setUpperBound(new DoubleCell(20));
        DataColumnSpecCreator colSpec = new DataColumnSpecCreator("col2", DoubleCell.TYPE);
        colSpec.setDomain(colDom.createDomain());
        return new DataTableSpec(colSpec.createSpec());
    }

    @Override
    protected Map<String, String> getConfigKeyToValueKeyMap() {
        return Map.of(LinePlotViewConfig.Y_COLS, "yColumns");
    }

    @Override
    protected double randomDouble(final String key) {
        // make sure that max > min
        if (key.endsWith("Max")) {
            return super.randomDouble(key) * 10;
        } else {
            return super.randomDouble(key);
        }
    }

    @Override
    protected void initialNodeSettings(final NodeSettings ns) {
        ns.addBoolean(LinePlotViewConfig.USE_DOMAIN_INFO, true);
        ns.addString(LinePlotViewConfig.X_COL, "col1");
    }

    @Override
    protected void changedNodeSettings(final NodeSettings ns) {
        ns.addBoolean(LinePlotViewConfig.USE_DOMAIN_INFO, true);
        ns.addString(LinePlotViewConfig.X_COL, "col2");
    }

}
