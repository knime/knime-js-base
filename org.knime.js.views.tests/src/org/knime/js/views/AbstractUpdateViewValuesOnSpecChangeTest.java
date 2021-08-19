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

import static org.knime.js.views.AbstractUpdateViewValuesTest.createExec;
import static org.knime.js.views.AbstractUpdateViewValuesTest.getViewValue;
import static org.knime.js.views.AbstractUpdateViewValuesTest.performExecute;
import static org.knime.js.views.AbstractUpdateViewValuesTest.toJsonNode;

import java.util.Collections;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.Node;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.wizard.WizardNode;
import org.knime.core.node.workflow.FileNativeNodeContainerPersistor;
import org.knime.core.node.workflow.NodeContext;
import org.knime.js.core.JSONViewContent;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Similar to {@link AbstractUpdateViewValuesTest} with the difference that it checks for view value changes if the
 * {@link DataTableSpec} of the input data has been changed.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public abstract class AbstractUpdateViewValuesOnSpecChangeTest extends RandomNodeSettingsHelper {

    private final String m_nodeFactoryClass;

    private final Map<String, Class<?>> m_viewValuePropertiesExpectedToChange;

    private final DataTableSpec m_spec1;

    private final DataTableSpec m_spec2;

    private final Map<String, String> m_configKeyToValueKeyMap;

    /**
     * @param nodeFactoryClass the node factory to run the test for (expected to be a {@link WizardNode})
     * @param viewValuePropertiesExpectedToChange the view value properties (key and type) expected to change if the
     *            input table spec changes
     * @param spec1 the initial table spec
     * @param spec2 the changed table spec
     */
    protected AbstractUpdateViewValuesOnSpecChangeTest(final String nodeFactoryClass,
        final Map<String, Class<?>> viewValuePropertiesExpectedToChange, final DataTableSpec spec1,
        final DataTableSpec spec2) {
        m_nodeFactoryClass = nodeFactoryClass;
        m_viewValuePropertiesExpectedToChange = viewValuePropertiesExpectedToChange;
        m_spec1 = spec1;
        m_spec2 = spec2;
        m_configKeyToValueKeyMap = getConfigKeyToValueKeyMap();
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
    public void testViewValueChangesOnSpecChange() throws Exception {
        NodeFactory<NodeModel> nodeFactory = FileNativeNodeContainerPersistor.loadNodeFactory(m_nodeFactoryClass);
        Node n = new Node(nodeFactory);

        ExecutionContext exec = createExec(n);

        // 1. get node settings object
        NodeSettings ns = new NodeSettings("");
        n.saveModelSettingsTo(ns);

        // 2. 'execute' the node for the first time (without any configs set)
        n.reset();
        initialNodeSettings(ns);
        n.loadModelSettingsFrom(ns);
        performExecute(n, exec, m_spec1);
        JSONViewContent viewVal1 = getViewValue(n);
        JsonNode viewValJson1 = toJsonNode(viewVal1);

        // 3. re-execute with same spec and same config -> no view value changes expected
        n.reset();
        ((WizardNode)n.getNodeModel()).loadViewValue(viewVal1, false);
        n.loadModelSettingsFrom(ns);
        performExecute(n, exec, m_spec1);
        JSONViewContent viewVal2 = getViewValue(n);
        JsonNode viewValJson2 = toJsonNode(viewVal2);
        Assert.assertEquals(viewValJson1, viewValJson2);

        // 4. re-execute different spec and different config -> view value changes expected
        n.reset();
        ((WizardNode)n.getNodeModel()).loadViewValue(viewVal2, false);
        changedNodeSettings(ns);
        n.loadModelSettingsFrom(ns);
        performExecute(n, exec, m_spec2);
        JsonNode viewValJson3 = toJsonNode(getViewValue(n));
        m_viewValuePropertiesExpectedToChange.keySet().forEach(k -> {
            String key = m_configKeyToValueKeyMap.get(k);
            if (key == null) {
                key = k;
            }
            Assert.assertFalse(viewValJson2.get(key).isNull());
            Assert.assertFalse(viewValJson3.get(key).isNull());
            Assert.assertNotEquals(viewValJson3.get(key), viewValJson2.get(key));
        });
    }

    /**
     * The initial node settings.
     *
     * @param ns
     */
    protected void initialNodeSettings(final NodeSettings ns) {
        //
    }

    /**
     * The changed node settings.
     *
     * @param ns
     */
    protected void changedNodeSettings(final NodeSettings ns) {
        //
    }

    @SuppressWarnings("javadoc")
    @After
    public void removeNodeContext() {
        NodeContext.removeLastContext();
    }

}
