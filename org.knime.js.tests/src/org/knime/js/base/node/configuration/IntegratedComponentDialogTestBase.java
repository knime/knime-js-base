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
 *   Apr 24, 2025 (Paul Bärnreuther): created
 */
package org.knime.js.base.node.configuration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.knime.core.node.util.CheckUtils;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.webui.node.NodeWrapper;
import org.knime.core.webui.node.dialog.NodeDialogManager;
import org.knime.testing.workflow.WorkflowTestBase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Extend from this class to test how configurations are presented in component dialogs.
 *
 * @author Paul Bärnreuther
 */
@SuppressWarnings("restriction")
public class IntegratedComponentDialogTestBase extends WorkflowTestBase {

    static final ObjectMapper MAPPER = new ObjectMapper();

    private NodeID m_baseId;

    private static String previousComponentUiMode;

    static final String UI_MODE_PROP = "org.knime.component.ui.mode";

    static void setComponentUiMode(final String mode) {
        System.setProperty(UI_MODE_PROP, mode);
    }

    @BeforeAll
    static void setUpComponentUiModeJs() {
        previousComponentUiMode = System.setProperty(UI_MODE_PROP, "js");
    }

    @BeforeEach
    void setUpEach() throws Exception {
        m_baseId = loadAndSetWorkflow();
    }

    @AfterAll
    static void tearDownComponentUiModeJs() {
        if (previousComponentUiMode != null) {
            System.setProperty(UI_MODE_PROP, previousComponentUiMode);
        } else {
            System.clearProperty(UI_MODE_PROP);
        }
    }

    /**
     * Get the node id of a top level node
     *
     * @param index the index of the node in the workflow
     * @return the node id of the top level node
     */
    protected NodeID getTopLevelNodeId(final int index) {
        return new NodeID(m_baseId, index);
    }

    /**
     * Extract the dialog data of a component node.
     *
     * @param nodeId of a top level component node
     * @return the dialog data of the component node
     * @throws JsonProcessingException
     * @throws JsonMappingException
     */
    protected NodeDialogData getComponentDialog(final NodeID nodeId) throws JsonProcessingException {
        final var nc = getManager().getNodeContainer(nodeId);
        CheckUtils.checkState(nc instanceof SubNodeContainer, "Expected a sub node container for node id: " + nodeId);
        final var initialData =
            NodeDialogManager.getInstance().getDataServiceManager().callInitialDataService(NodeWrapper.of(nc));
        final var initialDataJson = MAPPER.readTree(initialData).get("result");
        return new NodeDialogData() {

            @Override
            public JsonNode getUiSchema() {
                return initialDataJson.get("ui_schema");
            }

            @Override
            public JsonNode getInitialUpdates() {
                return initialDataJson.get("initialUpdates");
            }

            @Override
            public JsonNode getSchemaFor(final String paramName) {
                return initialDataJson.get("schema").get("properties").get("model").get("properties").get(paramName);
            }

            @Override
            public JsonNode getDataFor(final String paramName) {
                return initialDataJson.get("data").get("model").get(paramName);
            }

            @Override
            public JsonNode getPersistSchema() {
                return initialDataJson.get("persist");
            }

        };
    }

    /**
     * Unified accessors for the dialog data of a component node.
     *
     * @author Paul Bärnreuther
     */
    public interface NodeDialogData {

        /**
         *
         * @return the complete ui schema of the component dialog
         */
        JsonNode getUiSchema();

        /**
         * Returns the persist schema of the component dialog.
         * @return the persist schema of the component dialog
         */
        JsonNode getPersistSchema();

        /**
         * @return the initial updates of provided states
         */
        JsonNode getInitialUpdates();

        /**
         *
         * @param paramName the name of the parameter
         * @return the schema for the parameter with the given name
         */
        JsonNode getSchemaFor(String paramName);

        /**
         * @param paramName the name of the parameter
         * @return the data for the parameter with the given name
         */
        JsonNode getDataFor(String paramName);

    }

}
