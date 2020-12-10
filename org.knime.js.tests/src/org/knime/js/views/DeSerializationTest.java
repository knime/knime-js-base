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
 *   Jun 27, 2018 (hornm): created
 */
package org.knime.js.views;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.knime.core.node.web.WebViewContent;
import org.knime.core.wizard.SubnodeViewValue;
import org.knime.js.base.node.viz.decisiontree.classification.DecisionTreeViewRepresentation;
import org.knime.js.base.node.viz.decisiontree.classification.JSDecisionTree;
import org.knime.js.base.node.viz.decisiontree.classification.JSDecisionTreeMetaData;
import org.knime.js.base.node.viz.decisiontree.classification.JSDecisionTreeNode;
import org.knime.js.base.node.viz.decisiontree.classification.JSNodeContent;
import org.knime.js.base.node.viz.plotter.roc.ROCCurveViewRepresentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Tests for de-/serialization of view-objects, such as {@link WebViewContent}-implementations.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class DeSerializationTest {

    /**
     * Helper method to test the de-/serialization of {@link WebViewContent}-implementations.
     *
     * @param webViewContent
     * @return the newly deserialized web view content object
     * @throws Exception if an error occurs
     */
    @SuppressWarnings({"resource"})
    private static <C extends WebViewContent> C testDeSerializeWebViewContent(final C webViewContent) throws Exception {
        String s = ((ByteArrayOutputStream)webViewContent.saveToStream()).toString("UTF-8");
        return deserialize(s, webViewContent.getClass());
    }

    @SuppressWarnings({"unchecked", "resource"})
    private static <C extends WebViewContent> C deserialize(final String content, final Class<?> webViewContentClass)
        throws IOException, InstantiationException, IllegalAccessException {
        C newWebViewContent = (C)webViewContentClass.newInstance();
        newWebViewContent.loadFromStream(IOUtils.toInputStream(content, StandardCharsets.UTF_8));
        return newWebViewContent;
    }

    /**
     * Tests de-/serialization of {@link ROCCurveViewRepresentation}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void testDeSerializationROCCurveViewRepresentation() throws Exception {
        //tests for SRV-1521 only
        ROCCurveViewRepresentation rep = new ROCCurveViewRepresentation();
        rep.setBackgroundColor(Color.red);
        rep.setDataAreaColor(Color.green);
        rep.setGridColor(Color.blue);

        ROCCurveViewRepresentation newRep = testDeSerializeWebViewContent(rep);
        assertThat(newRep, is(rep));
    }

    /**
     * Tests de-/serialization of {@link DecisionTreeViewRepresentation}.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void testDeSerializationDecisionTreeViewRepresentation() throws Exception {
        //tests for SRV-1521 only
        DecisionTreeViewRepresentation rep = new DecisionTreeViewRepresentation();
        JSDecisionTreeNode root = new JSDecisionTreeNode(0, 4, new JSDecisionTreeNode[0],
            new JSNodeContent(5, new double[]{8}), null, new String[]{"foobar"});
        JSDecisionTreeMetaData metaData = new JSDecisionTreeMetaData(new String[]{"class1", "class2"});
        JSDecisionTree tree = new JSDecisionTree(root, metaData);
        rep.setTree(tree);

        DecisionTreeViewRepresentation newRep = testDeSerializeWebViewContent(rep);
        assertThat(newRep, is(rep));
    }

    /**
     * Tests de-/serialization of {@link SubnodeViewValue}.
     *
     * @throws Exception
     */
    @Test
    public void testDeSerializationSubnodeViewValue() throws Exception {
        SubnodeViewValue subnodeViewVal = new SubnodeViewValue();
        Map<String, String> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode val = mapper.createObjectNode();
        val.put("prop1", "propval1");
        val.put("prop2", "propval2");
        map.put("key", mapper.writeValueAsString(val));
        map.put("key2", mapper.writeValueAsString(val));
        subnodeViewVal.setViewValues(map);
        SubnodeViewValue subnodeViewVal2 = testDeSerializeWebViewContent(subnodeViewVal);
        assertThat("differing view values after deserialization", subnodeViewVal2.getViewValues(),
            is(subnodeViewVal.getViewValues()));

        // try to deserialize a value as it would arrive from JS
        ObjectNode jsonObj = mapper.createObjectNode();
        jsonObj.set("key", val);
        jsonObj.set("key2", val);
        subnodeViewVal2 = deserialize(mapper.writeValueAsString(jsonObj), SubnodeViewValue.class);
        assertThat("differing view values after deserialization", subnodeViewVal2.getViewValues(),
            is(subnodeViewVal.getViewValues()));
    }
}
