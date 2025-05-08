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
package org.knime.js.base.node.configuration.input.integer;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import org.junit.jupiter.api.Test;
import org.knime.js.base.node.configuration.IntegratedComponentDialogTestBase;

import com.fasterxml.jackson.core.JsonProcessingException;

class IntegerConfigurationComponentDialogTest extends IntegratedComponentDialogTestBase {

    @Test
    void testIntegerConfigurationComponentDialog() throws JsonProcessingException {
        final var dialogData = getComponentDialog(getTopLevelNodeId(2));
        final var paramName = "integer-input-3";
        assertThatJson(dialogData.getDataFor(paramName)).inPath("$.integer").isNumber().isZero();
        final var schema = dialogData.getSchemaFor(paramName);
        assertThatJson(schema).inPath("$.properties.integer.type").isString().isEqualTo("integer");
        assertThatJson(schema).inPath("$.properties.integer.title").isString().isEqualTo("Default");
        assertThatJson(schema).inPath("$.properties.integer.description").isString().isEqualTo("Default integer");
        final var uiSchema = dialogData.getUiSchema();
        assertThatJson(uiSchema).inPath("$.elements[0].type").isString().isEqualTo("Control");
        assertThatJson(uiSchema).inPath("$.elements[0].scope").isString()
            .isEqualTo(String.format("#/properties/model/properties/%s/properties/integer", paramName));
        assertThatJson(uiSchema).inPath("$.elements[0].options.validations[0]").isObject().containsEntry("id", "min")
            .containsEntry("errorMessage", "The value must be at least -2147483648.");
        assertThatJson(uiSchema).inPath("$.elements[0].options.validations[1]").isObject().containsEntry("id", "max")
            .containsEntry("errorMessage", "The value must not exceed 2147483647.");
    }

    @Test
    void testIntegerConfigurationComponentDialogWithMinAndMax() throws JsonProcessingException {
        final var dialogData = getComponentDialog(getTopLevelNodeId(2));
        final var paramName = "integer-input-with-min-and-max-4";
        final var uiSchema = dialogData.getUiSchema();
        assertThatJson(uiSchema).inPath("$.elements[1].scope").isString()
            .isEqualTo(String.format("#/properties/model/properties/%s/properties/integer", paramName));
        assertThatJson(uiSchema).inPath("$.elements[1].options.validations").isArray().hasSize(2);
        assertThatJson(uiSchema).inPath("$.elements[1].options.validations[0]").isObject().containsEntry("id", "min")
            .containsEntry("errorMessage", "The value must be at least 0.");
        assertThatJson(uiSchema).inPath("$.elements[1].options.validations[1]").isObject().containsEntry("id", "max")
            .containsEntry("errorMessage", "The value must not exceed 100.");
    }

}
