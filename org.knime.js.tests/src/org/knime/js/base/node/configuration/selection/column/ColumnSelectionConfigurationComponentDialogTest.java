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
package org.knime.js.base.node.configuration.selection.column;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.knime.js.base.node.configuration.IntegratedComponentDialogTestBase;

import com.fasterxml.jackson.core.JsonProcessingException;

class ColumnSelectionConfigurationComponentDialogTest extends IntegratedComponentDialogTestBase {

    @Test
    void testColumnSelectionConfigurationComponentDialogTestTypeRadioHorizontal() throws JsonProcessingException {
        final var dialogData = getComponentDialog(getTopLevelNodeId(7));
        final var paramName = "radio-horizontal-3";
        assertThatJson(dialogData.getDataFor(paramName)).inPath("$.column").isString().isEqualTo("Column 1");
        final var schema = dialogData.getSchemaFor(paramName);
        assertThatJson(schema).inPath("$.properties.column.type").isString().isEqualTo("string");
        assertThatJson(schema).inPath("$.properties.column.title").isString().isEqualTo("Label radio horizontal");
        assertThatJson(schema).inPath("$.properties.column.description").isString()
            .isEqualTo("Description radio horizontal");
        final var uiSchema = dialogData.getUiSchema();
        assertThatJson(uiSchema).inPath("$.elements[0].type").isString().isEqualTo("Control");
        assertThatJson(uiSchema).inPath("$.elements[0].scope").isString()
            .isEqualTo(String.format("#/properties/model/properties/%s/properties/column", paramName));
        assertThatJson(uiSchema).inPath("$.elements[0].options.format").isString().isEqualTo("radio");
        assertThatJson(uiSchema).inPath("$.elements[0].options.radioLayout").isString().isEqualTo("horizontal");
        assertThatJson(uiSchema).inPath("$.elements[0].options.possibleValues").isArray().hasSize(10);
    }

    @Test
    void testColumnSelectionConfigurationComponentDialogTestTypeRadioVertical() throws JsonProcessingException {
        final var dialogData = getComponentDialog(getTopLevelNodeId(7));
        final var paramName = "radio-vertical-5";
        assertThatJson(dialogData.getDataFor(paramName)).inPath("$.column").isString().isEqualTo("Column 2");
        final var schema = dialogData.getSchemaFor(paramName);
        assertThatJson(schema).inPath("$.properties.column.type").isString().isEqualTo("string");
        final var uiSchema = dialogData.getUiSchema();
        assertThatJson(uiSchema).inPath("$.elements[1].type").isString().isEqualTo("Control");
        assertThatJson(uiSchema).inPath("$.elements[1].scope").isString()
            .isEqualTo(String.format("#/properties/model/properties/%s/properties/column", paramName));
        assertThatJson(uiSchema).inPath("$.elements[1].options.format").isString().isEqualTo("radio");
        assertThatJson(uiSchema).inPath("$.elements[1].options.radioLayout").isString().isEqualTo("vertical");
        assertThatJson(uiSchema).inPath("$.elements[0].options.possibleValues").isArray().hasSize(10);
    }

    @Test
    void testColumnSelectionConfigurationComponentDialogTestTypeList() throws JsonProcessingException {
        final var dialogData = getComponentDialog(getTopLevelNodeId(7));
        final var paramName = "list-6";
        assertThatJson(dialogData.getDataFor(paramName)).inPath("$.column").isString().isEqualTo("Column 3");
        assertThatJson(dialogData.getSchemaFor(paramName)).inPath("$.properties.column.type").isString()
            .isEqualTo("string");
        final var uiSchema = dialogData.getUiSchema();
        assertThatJson(uiSchema).inPath("$.elements[2].type").isString().isEqualTo("Control");
        assertThatJson(uiSchema).inPath("$.elements[2].scope").isString()
            .isEqualTo(String.format("#/properties/model/properties/%s/properties/column", paramName));
        assertThatJson(uiSchema).inPath("$.elements[2].options.format").isString().isEqualTo("singleSelectListBox");
        assertThatJson(uiSchema).inPath("$.elements[2].options.possibleValues").isArray().hasSize(10);
    }

    @Test
    void testColumnSelectionConfigurationComponentDialogTestTypeListWithLimit() throws JsonProcessingException {
        final var dialogData = getComponentDialog(getTopLevelNodeId(7));
        final var paramName = "list-with-size-limit-7";
        assertThatJson(dialogData.getDataFor(paramName)).inPath("$.column").isString().isEqualTo("Column 4");
        assertThatJson(dialogData.getSchemaFor(paramName)).inPath("$.properties.column.type").isString()
            .isEqualTo("string");
        final var uiSchema = dialogData.getUiSchema();
        assertThatJson(uiSchema).inPath("$.elements[3].type").isString().isEqualTo("Control");
        assertThatJson(uiSchema).inPath("$.elements[3].scope").isString()
            .isEqualTo(String.format("#/properties/model/properties/%s/properties/column", paramName));
        assertThatJson(uiSchema).inPath("$.elements[3].options.format").isString().isEqualTo("singleSelectListBox");
        assertThatJson(uiSchema).inPath("$.elements[3].options.possibleValues").isArray().hasSize(10);
        assertThatJson(uiSchema).inPath("$.elements[3].options.size").isNumber().isEqualTo(BigDecimal.valueOf(3));
    }

    @Test
    void testColumnSelectionConfigurationComponentDialogTestTypeDropdown() throws JsonProcessingException {
        final var dialogData = getComponentDialog(getTopLevelNodeId(7));
        final var paramName = "dropdown-8";
        assertThatJson(dialogData.getDataFor(paramName)).inPath("$.column").isString().isEqualTo("Column 5");
        assertThatJson(dialogData.getSchemaFor(paramName)).inPath("$.properties.column.type").isString()
            .isEqualTo("string");
        final var uiSchema = dialogData.getUiSchema();
        assertThatJson(uiSchema).inPath("$.elements[4].type").isString().isEqualTo("Control");
        assertThatJson(uiSchema).inPath("$.elements[4].scope").isString()
            .isEqualTo(String.format("#/properties/model/properties/%s/properties/column", paramName));
        assertThatJson(uiSchema).inPath("$.elements[4].options.format").isString().isEqualTo("dropDown");
        assertThatJson(uiSchema).inPath("$.elements[4].options.possibleValues").isArray().hasSize(10);
    }

}
