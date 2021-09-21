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
package org.knime.dynamic.js.v30;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.config.base.JSONConfig;
import org.knime.core.node.extension.InvalidNodeFactoryExtensionException;
import org.knime.core.node.workflow.FileNativeNodeContainerPersistor;
import org.knime.js.views.AbstractUpdateViewValuesTest;

/**
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class DynamicJSNodeViewUpdateValuesTest extends AbstractUpdateViewValuesTest {

    private static final String[] RANDOM_COL_NAMES = new String[]{randomString(), randomString(), randomString()};

    @SuppressWarnings("javadoc")
    public DynamicJSNodeViewUpdateValuesTest() throws InstantiationException, IllegalAccessException,
        InvalidSettingsException, InvalidNodeFactoryExtensionException, IOException {
        super(createNodeFactory(), createPropertyMap());
    }

    private static Map<String, Class<?>> createPropertyMap() {
        Map<String, Class<?>> res = new HashMap<>();
        res.put("includeMissValCat", Boolean.class);
        res.put("freq", String.class);
        res.put("title", String.class);
        res.put("subtitle", String.class);
        res.put("togglePie", Boolean.class);
        res.put("holeSize", Double.class);
        res.put("insideTitle", String.class);
        res.put("showLabels", Boolean.class);
        res.put("labelType", String.class);
        res.put("subscribeToSelection", Boolean.class);
        res.put("publishSelection", Boolean.class);
        return res;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Map<String, String> getConfigKeyToValueKeyMap() {
        Map<String, String> res = new HashMap<>();
        res.put("includeMissValCat", "options/includeMissValCat");
        res.put("freq", "options/freq");
        res.put("title", "options/title");
        res.put("subtitle", "options/subtitle");
        res.put("togglePie", "options/togglePie");
        res.put("holeSize", "options/holeSize");
        res.put("insideTitle", "options/insideTitle");
        res.put("showLabels", "options/showLabels");
        res.put("labelType", "options/labelType");
        res.put("subscribeToSelection", "options/subscribeToSelection");
        res.put("publishSelection", "options/publishSelection");
        return res;
    }

    private static NodeFactory<NodeModel> createNodeFactory() throws InvalidSettingsException, InstantiationException,
        IllegalAccessException, InvalidNodeFactoryExtensionException, IOException {
        NodeFactory<NodeModel> nodeFactory =
            FileNativeNodeContainerPersistor.loadNodeFactory(DynamicJSNodeFactory.class.getName());
        NodeSettings settings = JSONConfig.readJSON(new NodeSettings("settings"), new StringReader(
            "{\"name\":\"root\",\"value\":{\"nodeDir\":{\"type\":\"string\",\"value\":\"org.knime.dynamic.js.base:nodes/:donutChart\"}}}"));
        nodeFactory.loadAdditionalFactorySettings(settings);
        return nodeFactory;
    }

    @Override
    protected DataTableSpec getSpec() {
        var types = new DataType[3];
        Arrays.fill(types, StringCell.TYPE);
        return new DataTableSpec("dynamicjsnode_input", RANDOM_COL_NAMES, types);
    }

    @Override
    protected Map<String, Class<?>> getIndependentConfigValues() {
        return Map.of("cat", String.class);
    }

    private static String randomString() {
        return UUID.randomUUID().toString();
    }

    @Override
    protected String randomString(final String key) {
        if (key.equals("cat")) {
            return RANDOM_COL_NAMES[RANDOM.nextInt(3)];
        } else {
            return super.randomString(key);
        }
    }

}
