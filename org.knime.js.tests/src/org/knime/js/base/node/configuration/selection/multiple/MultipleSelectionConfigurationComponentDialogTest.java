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
package org.knime.js.base.node.configuration.selection.multiple;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import java.math.BigDecimal;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.knime.js.base.node.configuration.IntegratedComponentDialogTestBase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import net.javacrumbs.jsonunit.assertj.JsonAssert;

class MultipleSelectionConfigurationComponentDialogTest extends IntegratedComponentDialogTestBase {

    private static JsonAssert assertSchemaProps(final JsonNode schema, final String prop) {
        return assertThatJson(schema).inPath(String.format("$.properties.value.%s", prop));
    }

    private static JsonAssert assertUiSchemaElements(final JsonNode uiSchema, final int index, final String element) {
        return assertThatJson(uiSchema).inPath(String.format("$.elements[%d].%s", index, element));
    }

    private static JsonAssert assertUiSchemaOptions(final JsonNode uiSchema, final int index, final String option) {
        return assertThatJson(uiSchema).inPath(String.format("$.elements[%d].options.%s", index, option));
    }

    static Stream<Arguments> getCheckboxMultipleSelectionTestCases() {
        return Stream.of( //
            Arguments.of("horizontal", "checkboxes-horizontal-3", 0, "Multiple selection",
                "Multiple selection description"), //
            Arguments.of("vertical", "checkboxes-vertical-5", 1, "Label", "Enter Description"));
    }

    @ParameterizedTest(name = "testMultipleSelectionConfigurationComponentDialogTestType ''checkboxes {0}''")
    @MethodSource("getCheckboxMultipleSelectionTestCases")
    void testMultipleSelectionConfigurationComponentDialogTestTypeCheckboxesHorizontal(final String layout,
        final String paramName, final int elementIndex, final String title, final String description)
        throws JsonProcessingException {
        final var dialogData = getComponentDialog(getTopLevelNodeId(6));
        assertThatJson(dialogData.getDataFor(paramName)).inPath("$.value").isArray()
            .isEqualTo(new String[]{"Chameleon", "Giraffe"});
        final var schema = dialogData.getSchemaFor(paramName);
        assertSchemaProps(schema, "type").isString().isEqualTo("array");
        assertSchemaProps(schema, "title").isString().isEqualTo(title);
        assertSchemaProps(schema, "description").isString().isEqualTo(description);
        final var uiSchema = dialogData.getUiSchema();
        assertUiSchemaElements(uiSchema, elementIndex, "type").isString().isEqualTo("Control");
        assertUiSchemaElements(uiSchema, elementIndex, "scope").isString()
            .isEqualTo(String.format("#/properties/model/properties/%s/properties/value", paramName));
        assertUiSchemaOptions(uiSchema, elementIndex, "checkboxLayout").isString().isEqualTo(layout);
        assertUiSchemaOptions(uiSchema, elementIndex, "format").isString().isEqualTo("checkboxes");
        assertUiSchemaOptions(uiSchema, elementIndex, "possibleValues").isArray().hasSize(5);
    }

    static Stream<Arguments> getNonCheckboxMultipleSelectionTestCases() {
        return Stream.of( //
            Arguments.of("multiSelectListBox", "list-8", 2), //
            Arguments.of("twinList", "twinlist-10", 4), //
            Arguments.of("comboBox", "combobox-12", 6) //
        );
    }

    @ParameterizedTest(name = "testMultipleSelectionConfigurationComponentDialogType ''{0}''")
    @MethodSource("getNonCheckboxMultipleSelectionTestCases")
    void testMultipleSelectionConfigurationComponentDialogType(final String format, final String paramName,
        final int elementIndex) throws JsonProcessingException {
        final var dialogData = getComponentDialog(getTopLevelNodeId(6));
        assertThatJson(dialogData.getDataFor(paramName)).inPath("$.value").isArray()
            .isEqualTo(new String[]{"Chameleon", "Giraffe"});
        final var schema = dialogData.getSchemaFor(paramName);
        assertSchemaProps(schema, "type").isString().isEqualTo("array");
        final var uiSchema = dialogData.getUiSchema();
        assertUiSchemaElements(uiSchema, elementIndex, "type").isString().isEqualTo("Control");
        assertUiSchemaElements(uiSchema, elementIndex, "scope").isString()
            .isEqualTo(String.format("#/properties/model/properties/%s/properties/value", paramName));
        assertUiSchemaOptions(uiSchema, elementIndex, "format").isString().isEqualTo(format);
        assertUiSchemaOptions(uiSchema, elementIndex, "possibleValues").isArray().hasSize(5);
    }

    @Test
    void testMultipleSelectionConfigurationComponentDialogTypeListWithLimit() throws JsonProcessingException {
        final var uiSchema = getComponentDialog(getTopLevelNodeId(6)).getUiSchema();
        final var paramName = "list-with-limit-9";
        final var ind = 3;
        assertUiSchemaElements(uiSchema, ind, "scope").isString()
            .isEqualTo(String.format("#/properties/model/properties/%s/properties/value", paramName));
        assertUiSchemaOptions(uiSchema, ind, "format").isString().isEqualTo("multiSelectListBox");
        assertUiSchemaOptions(uiSchema, ind, "size").isNumber().isEqualTo(BigDecimal.valueOf(5));
    }

    @Test
    void testMultipleSelectionConfigurationComponentDialogTypeTwinListWithLimit() throws JsonProcessingException {
        final var uiSchema = getComponentDialog(getTopLevelNodeId(6)).getUiSchema();
        final var paramName = "twinlist-with-limit-11";
        final var ind = 5;
        assertUiSchemaElements(uiSchema, ind, "scope").isString()
            .isEqualTo(String.format("#/properties/model/properties/%s/properties/value", paramName));
        assertUiSchemaOptions(uiSchema, ind, "format").isString().isEqualTo("twinList");
        assertUiSchemaOptions(uiSchema, ind, "twinlistSize").isNumber().isEqualTo(BigDecimal.valueOf(6));
    }

}
