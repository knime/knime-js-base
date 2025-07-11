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
package org.knime.js.base.node.configuration.input.credentials;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import org.junit.jupiter.api.Test;
import org.knime.js.base.node.configuration.IntegratedComponentDialogTestBase;

import com.fasterxml.jackson.core.JsonProcessingException;

class CredentialsConfigurationComponentDialogTest extends IntegratedComponentDialogTestBase {

    @Test
    void testCredentialsConfigurationComponentDialog() throws JsonProcessingException {
        final var dialogData = getComponentDialog(getTopLevelNodeId(2));
        final var paramName = "credentials-3";
        final var data = dialogData.getDataFor(paramName);
        assertThatJson(data).inPath("$.credentials.isHiddenPassword").isBoolean().isTrue();
        assertThatJson(data).inPath("$.credentials.username").isString().isEqualTo("Hello");
        final var schema = dialogData.getSchemaFor(paramName);
        assertThatJson(schema).inPath("$.properties.credentials.type").isString().isEqualTo("object");
        assertThatJson(schema).inPath("$.properties.credentials.title").isString().isEqualTo("With labels");
        assertThatJson(schema).inPath("$.properties.credentials.description").isString()
            .isEqualTo("Default credentials");
        final var uiSchema = dialogData.getUiSchema();
        assertThatJson(uiSchema).inPath("$.elements[0].type").isString().isEqualTo("Control");
        assertThatJson(uiSchema).inPath("$.elements[0].scope").isString()
            .isEqualTo(String.format("#/properties/model/properties/%s/properties/credentials", paramName));
        assertThatJson(uiSchema).inPath("$.elements[0].options.usernameLabel").isString().isEqualTo("User");
        assertThatJson(uiSchema).inPath("$.elements[0].options.passwordLabel").isString().isEqualTo("Password");
        final var persistSchema = dialogData.getPersistSchema();
        assertThatJson(persistSchema)
            .inPath(String.format("$.properties.model.properties.%s.properties.credentials.type", paramName)).isString()
            .isEqualTo("leaf");
        assertThatJson(persistSchema)
            .inPath(String.format("$.properties.model.properties.%s.properties.credentials.configKey", paramName))
            .isString().isEqualTo("credentialsValue");

    }

    @Test
    void testCredentialsConfigurationComponentDialogWithEmptyLabels() throws JsonProcessingException {
        final var dialogData = getComponentDialog(getTopLevelNodeId(2));
        final var paramName = "credentials-with-empty-labels-4";
        final var uiSchema = dialogData.getUiSchema();
        assertThatJson(uiSchema).inPath("$.elements[1].scope").isString()
            .isEqualTo(String.format("#/properties/model/properties/%s/properties/credentials", paramName));
        assertThatJson(uiSchema).inPath("$.elements[1].options.usernameLabel").isString().isEqualTo("User");
        assertThatJson(uiSchema).inPath("$.elements[1].options.passwordLabel").isString().isEqualTo("Password");
    }


    @Test
    void testCredentialsConfigurationComponentDialogWithHiddenUsername() throws JsonProcessingException {
        final var dialogData = getComponentDialog(getTopLevelNodeId(2));
        final var paramName = "credentials-with-hidden-username-5";
        final var uiSchema = dialogData.getUiSchema();
        assertThatJson(uiSchema).inPath("$.elements[2].scope").isString()
            .isEqualTo(String.format("#/properties/model/properties/%s/properties/credentials", paramName));
        assertThatJson(uiSchema).inPath("$.elements[2].options.hasUsername").isBoolean().isFalse();
    }

}
