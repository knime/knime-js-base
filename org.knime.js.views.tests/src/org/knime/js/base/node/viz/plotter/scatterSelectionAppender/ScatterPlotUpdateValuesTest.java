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
package org.knime.js.base.node.viz.plotter.scatterSelectionAppender;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.extension.InvalidNodeFactoryExtensionException;
import org.knime.js.views.AbstractUpdateViewValuesTest;

/**
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class ScatterPlotUpdateValuesTest extends AbstractUpdateViewValuesTest {

    private static final String[] RANDOM_COL_NAMES = new String[]{randomString(), randomString(), randomString()};

    @SuppressWarnings("javadoc")
    public ScatterPlotUpdateValuesTest() throws InstantiationException, IllegalAccessException,
        InvalidSettingsException, InvalidNodeFactoryExtensionException {
        super(ScatterPlotNodeFactory.class.getName(), createPropertyMap());
    }

    private static Map<String, Class<?>> createPropertyMap() {
        Map<String, Class<?>> res = new HashMap<>();
        res.put(ScatterPlotViewConfig.CHART_TITLE, String.class);
        res.put(ScatterPlotViewConfig.CHART_SUBTITLE, String.class);
        res.put(ScatterPlotViewConfig.X_COL, String.class);
        res.put(ScatterPlotViewConfig.X_AXIS_LABEL, String.class);
        res.put(ScatterPlotViewConfig.Y_AXIS_LABEL, String.class);
        res.put(ScatterPlotViewConfig.DOT_SIZE, IntegerStoredAsString.class);
        res.put(ScatterPlotViewConfig.CFG_PUBLISH_SELECTION, Boolean.class);
        res.put(ScatterPlotViewConfig.CFG_SUBSCRIBE_SELECTION, Boolean.class);
        res.put(ScatterPlotViewConfig.CFG_SUBSCRIBE_FILTER, Boolean.class);
        res.put(ScatterPlotViewConfig.SHOW_LEGEND, Boolean.class);
        res.put(ScatterPlotViewConfig.CFG_SHOW_SELECTED_ROWS_ONLY, Boolean.class);
        res.put(ScatterPlotViewConfig.X_AXIS_MIN, DoubleStoredAsString.class);
        res.put(ScatterPlotViewConfig.X_AXIS_MAX, DoubleStoredAsString.class);
        res.put(ScatterPlotViewConfig.Y_AXIS_MIN, DoubleStoredAsString.class);
        res.put(ScatterPlotViewConfig.Y_AXIS_MAX, DoubleStoredAsString.class);
        return res;
    }

    @Override
    protected Map<String, String> getConfigKeyToValueKeyMap() {
        return Map.of(ScatterPlotViewConfig.X_COL, "xColumn", //
            ScatterPlotViewConfig.DOT_SIZE, "dotSize");
    }

    @Override
    protected DataTableSpec getSpec() {
        var types = new DataType[3];
        Arrays.fill(types, StringCell.TYPE);
        return new DataTableSpec("scatterplotview_input", RANDOM_COL_NAMES, types);
    }

    private static String randomString() {
        return UUID.randomUUID().toString();
    }

    private int numCalls = 0;

    @Override
    protected String randomString(final String key) {
        if (key.equals("xCol")) {
            return RANDOM_COL_NAMES[(numCalls++) % 3];
        } else {
            return super.randomString(key);
        }
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

}
