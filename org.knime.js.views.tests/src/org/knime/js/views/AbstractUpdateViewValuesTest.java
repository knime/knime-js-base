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
 *   Jul 28, 2021 (hornm): created
 */
package org.knime.js.views;

import java.util.Collections;
import java.util.Map;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.filestore.internal.NotInWorkflowDataRepository;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.DefaultNodeProgressMonitor;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.Node;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.extension.InvalidNodeFactoryExtensionException;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.wizard.WizardNode;
import org.knime.core.node.workflow.ExecutionEnvironment;
import org.knime.core.node.workflow.FileNativeNodeContainerPersistor;
import org.knime.core.node.workflow.FlowObjectStack;
import org.knime.core.node.workflow.NodeContext;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.SingleNodeContainer.MemoryPolicy;
import org.knime.js.core.JSONViewContent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Helper to test whether view value properties are properly updated if the associated config (i.e. node settings) has
 * been changed (usually because it's controlled by a flow variable).
 *
 * Background: for a lot of view-node implementations, the view value (the 'mutable data' that is rendered JS-side for a
 * node view) is (also) taken from the node's configuration and thus copied (i.e. copied from config to value) on the
 * node's execution. However, the copy-step should only happen if either it hasn't been done, yet (first node execution)
 * or the config-value changed (flow-variable controlled). Otherwise we would overwrite view values which actually have
 * been changed on the JS-side by the user (which is possible for many view value properties).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public abstract class AbstractUpdateViewValuesTest extends RandomNodeSettingsHelper {

    private static final ObjectMapper MAPPER = JSONViewContent.createObjectMapper();

    private final NodeFactory<NodeModel> m_nodeFactory;

    private final Map<String, Class<?>> m_viewValuePropertiesExpectedToChange;

    private final Map<String, String> m_configKeyToValueKeyMap;

    private final Map<String, Class<?>> m_independentConfigValues;

    private final DataTableSpec m_spec;

    /**
     * @param nodeFactoryClass the node factory class to run the test for (expected to be a {@link WizardNode})
     * @param viewValuePropertiesExpectedToChange the view value properties (key and type) expected to change if the
     *            associated config changes
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvalidSettingsException
     * @throws InvalidNodeFactoryExtensionException
     */
    protected AbstractUpdateViewValuesTest(final String nodeFactoryClass,
        final Map<String, Class<?>> viewValuePropertiesExpectedToChange) throws InstantiationException,
        IllegalAccessException, InvalidSettingsException, InvalidNodeFactoryExtensionException {
        this(FileNativeNodeContainerPersistor.loadNodeFactory(nodeFactoryClass), viewValuePropertiesExpectedToChange);
    }

    /**
     * @param nodeFactory the node factory class to run the test for
     * @param viewValuePropertiesExpectedToChange the view value properties (key and type) expected to change if the
     *            associated config changes
     */
    protected AbstractUpdateViewValuesTest(final NodeFactory<NodeModel> nodeFactory,
        final Map<String, Class<?>> viewValuePropertiesExpectedToChange) {
        m_nodeFactory = nodeFactory;
        m_viewValuePropertiesExpectedToChange = viewValuePropertiesExpectedToChange;
        m_configKeyToValueKeyMap = getConfigKeyToValueKeyMap();
        m_spec = getSpec();
        m_independentConfigValues = getIndependentConfigValues();
    }

    /**
     * The assumption is that the config-key (node settings) and the value-key (view value) are the same. However,
     * that's not always the case and the actual mapping can be provided by overwriting this method.
     *
     * @return a map from the config-key (dialog/node-settings) to the value-key (view) in case they differ
     */
    protected Map<String, String> getConfigKeyToValueKeyMap() {
        return Collections.emptyMap();
    }

    /**
     * @return the {@link DataTableSpec} used to create the (empty) table which is fed into the node to simulate its
     *         execution
     */
    protected DataTableSpec getSpec() {
        return new DataTableSpec();
    }

    /**
     * @return a map of config-values that should be initialized with a random value but not tested (i.e. those
     *         config-values are not associated with a view-value)
     */
    protected Map<String, Class<?>> getIndependentConfigValues() {
        return Collections.emptyMap();
    }

    @SuppressWarnings("javadoc")
    @Before
    public void pushNodeContext() {
        NodeContext.pushContext(new Object());
    }

    /**
     * The actual test.
     *
     * @throws Exception
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void testViewValueChangeOnConfigValueChange() throws Exception {
        Node n = new Node(m_nodeFactory);

        ExecutionContext exec = createExec(n);

        // 1. get node settings object
        NodeSettings ns = new NodeSettings("");
        n.saveModelSettingsTo(ns);

        // 2. pre-set (mostly random) config values and 'execute' the node for the first time
        n.reset();
        m_viewValuePropertiesExpectedToChange.entrySet().forEach(e -> {
            String[] key = e.getKey().split("/");
            addRandomValue(key[key.length - 1], e.getValue(), ns);
        });
        m_independentConfigValues.entrySet().forEach(e -> {
            addRandomValue(e.getKey(), e.getValue(), ns);
        });
        n.loadModelSettingsFrom(ns);
        performExecute(n, exec, m_spec);
        JSONViewContent viewVal1 = getViewValue(n);
        JsonNode viewValJson1 = toJsonNode(viewVal1);

        // 3. re-execute with same config -> no view value changes expected
        n.reset();
        ((WizardNode)n.getNodeModel()).loadViewValue(viewVal1, false);
        n.loadModelSettingsFrom(ns);
        performExecute(n, exec, m_spec);
        JSONViewContent viewVal2 = getViewValue(n);
        JsonNode viewValJson2 = toJsonNode(viewVal2);
        m_viewValuePropertiesExpectedToChange.keySet().forEach(k -> {
            String key = m_configKeyToValueKeyMap.get(k);
            if (key == null) {
                key = k;
            }
            Assert.assertFalse(get(viewValJson1, key).isNull());
        });
        Assert.assertEquals(viewValJson1, viewValJson2);

        // 4. re-execute with different config -> view value changes expected
        n.reset();
        ((WizardNode)n.getNodeModel()).loadViewValue(viewVal2, false);
        m_viewValuePropertiesExpectedToChange.entrySet().forEach(e -> {
            String[] key = e.getKey().split("/");
            addRandomValue(key[key.length - 1], e.getValue(), ns);
        });
        n.loadModelSettingsFrom(ns);
        performExecute(n, exec, m_spec);
        JsonNode viewValJson3 = toJsonNode(getViewValue(n));
        m_viewValuePropertiesExpectedToChange.keySet().forEach(k -> {
            String key = m_configKeyToValueKeyMap.get(k);
            if (key == null) {
                key = k;
            }
            Assert.assertFalse(get(viewValJson2, key).isNull());
            Assert.assertFalse(get(viewValJson3, key).isNull());
            Assert.assertNotEquals(get(viewValJson3, key), get(viewValJson2, key));
        });
    }

    @SuppressWarnings("javadoc")
    @After
    public void removeNodeContext() {
        NodeContext.removeLastContext();
    }

    private static JsonNode get(final JsonNode n, final String key) {
        String[] s = key.split("/");
        JsonNode subNode = n;
        for (int i = 0; i < s.length; i++) {
            subNode = subNode.get(s[i]);
        }
        return subNode;
    }

    static JSONViewContent getViewValue(final Node n) {
        return (JSONViewContent)((WizardNode)n.getNodeModel()).getViewValue();
    }

    static JsonNode toJsonNode(final JSONViewContent viewContent) {
        return MAPPER.convertValue(viewContent, JsonNode.class);
    }

    private static void performExecute(final Node n, final ExecutionContext exec, final PortObject... data) {
        n.setFlowObjectStack(createFOS(), createFOS());
        n.execute(data, new ExecutionEnvironment(), exec);
    }

    static void performExecute(final Node n, final ExecutionContext exec, final DataTableSpec spec) {
        var data = IntStream.range(0, n.getNrInPorts()).mapToObj(i -> {
            if (i == 0) {
                return FlowVariablePortObject.INSTANCE;
            } else {
                return createTable(spec, exec);
            }
        }).toArray(PortObject[]::new);
        performExecute(n, exec, data);
    }

    private static FlowObjectStack createFOS() {
        return FlowObjectStack.createFromFlowVariableList(Collections.emptyList(), new NodeID(0));
    }

    static BufferedDataTable createTable(final DataTableSpec spec, final ExecutionContext exec) {
        BufferedDataContainer container = exec.createDataContainer(spec);
        container.close();
        return container.getTable();
    }

    static ExecutionContext createExec(final Node n) {
        return new ExecutionContext(new DefaultNodeProgressMonitor(), n, MemoryPolicy.CacheInMemory,
            NotInWorkflowDataRepository.newInstance());
    }

}
