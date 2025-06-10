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
 *   8 May 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.configuration.filter.column;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.knime.js.base.node.configuration.IntegratedComponentDialogTestBase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import net.javacrumbs.jsonunit.assertj.JsonAssert;

class ColumnFilterConfigurationComponentDialogTest extends IntegratedComponentDialogTestBase {

    private static JsonAssert assertSchemaProps(final JsonNode schema, final String prop) {
        return assertThatJson(schema).inPath(String.format("$.properties.columnFilter.%s", prop));
    }

    private static JsonAssert assertUiSchemaElements(final JsonNode uiSchema, final int index, final String element) {
        return assertThatJson(uiSchema).inPath(String.format("$.elements[%d].%s", index, element));
    }

    private static JsonAssert assertUiSchemaOptions(final JsonNode uiSchema, final int index, final String option) {
        return assertThatJson(uiSchema).inPath(String.format("$.elements[%d].options.%s", index, option));
    }

    @Test
    void testColumnFilterConfigurationComponentDialogTest() throws JsonProcessingException {
        final var dialogData = getComponentDialog(getTopLevelNodeId(7));
        final var paramName = "column-filter-5";
        assertThatJson(dialogData.getDataFor(paramName)).inPath("$.columnFilter").isObject();
        final var schema = dialogData.getSchemaFor(paramName);
        assertSchemaProps(schema, "type").isString().isEqualTo("object");
        assertSchemaProps(schema, "title").isString().isEqualTo("Column filter");
        assertSchemaProps(schema, "description").isString().isEqualTo("Column filter description");
        final var uiSchema = dialogData.getUiSchema();
        final var ind = 0;
        assertUiSchemaElements(uiSchema, ind, "type").isString().isEqualTo("Control");
        assertUiSchemaElements(uiSchema, ind, "scope").isString()
            .isEqualTo(String.format("#/properties/model/properties/%s/properties/columnFilter", paramName));
        assertUiSchemaOptions(uiSchema, ind, "unknownValuesText").isString().isEqualTo("Any unknown column");
        assertUiSchemaOptions(uiSchema, ind, "emptyStateLabel").isString().isEqualTo("No columns in this list.");
        assertUiSchemaOptions(uiSchema, ind, "format").isString().isEqualTo("typedStringFilter");
        assertUiSchemaOptions(uiSchema, ind, "possibleValues").isArray().hasSize(7);
    }

    @Test
    void testColumnFilterConfigurationComponentDialogTestWithTypeFilter() throws JsonProcessingException {
        final var uiSchema = getComponentDialog(getTopLevelNodeId(7)).getUiSchema();
        final var paramName = "column-filter-with-type-filter-6";
        final var ind = 1;
        assertUiSchemaElements(uiSchema, ind, "scope").isString()
            .isEqualTo(String.format("#/properties/model/properties/%s/properties/columnFilter", paramName));
        assertUiSchemaOptions(uiSchema, ind, "possibleValues").isArray().hasSize(2);
    }

    @Test
    void testColumnFilterConfigurationComponentDialogTestWithLimit() throws JsonProcessingException {
        final var uiSchema = getComponentDialog(getTopLevelNodeId(7)).getUiSchema();
        final var paramName = "column-filter-with-limit-7";
        final var ind = 2;
        assertUiSchemaElements(uiSchema, ind, "scope").isString()
            .isEqualTo(String.format("#/properties/model/properties/%s/properties/columnFilter", paramName));
        assertUiSchemaOptions(uiSchema, ind, "twinlistSize").isNumber().isEqualTo(BigDecimal.valueOf(5));
    }

}
