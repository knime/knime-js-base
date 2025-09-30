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
 *   30 Sept 2025 (robin): created
 */
package org.knime.js.base.node.configuration.input.slider;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.knime.js.base.node.configuration.IntegratedComponentDialogTestBase;

import com.fasterxml.jackson.core.JsonProcessingException;

class SliderConfigurationComponentDialogTest extends IntegratedComponentDialogTestBase {
    @Test
    void testSliderConfigurationComponentDialog() throws JsonProcessingException {
        final var dialogData = getComponentDialog(getTopLevelNodeId(4));
        final var paramName = "integer-slider-3";
        assertThatJson(dialogData.getDataFor(paramName)).inPath("$.double").isNumber()
            .isEqualTo(BigDecimal.valueOf(50));
        final var schema = dialogData.getSchemaFor(paramName);
        assertThatJson(schema).inPath("$.properties.double.type").isString().isEqualTo("integer");
        assertThatJson(schema).inPath("$.properties.double.title").isString().isEqualTo("Default");
        assertThatJson(schema).inPath("$.properties.double.description").isString().isEqualTo("Default slider");
        final var uiSchema = dialogData.getUiSchema();
        assertThatJson(uiSchema).inPath("$.elements[0].type").isString().isEqualTo("Control");
        assertThatJson(uiSchema).inPath("$.elements[0].scope").isString()
            .isEqualTo(String.format("#/properties/model/properties/%s/properties/double", paramName));
        assertThatJson(uiSchema).inPath("$.elements[0].options.validation.min.parameters.min").isNumber()
            .isEqualTo(BigDecimal.valueOf(5.0));
        assertThatJson(uiSchema).inPath("$.elements[0].options.validation.max.parameters.max").isNumber()
            .isEqualTo(BigDecimal.valueOf(96.0));
    }

    @Test
    void testWithRangeColumn() throws JsonProcessingException {
        final var dialogData = getComponentDialog(getTopLevelNodeId(4));
        final var paramName = "integer-slider-4";
        assertThatJson(dialogData.getDataFor(paramName)).inPath("$.double").isNumber()
            .isEqualTo(BigDecimal.valueOf(4));
        final var schema = dialogData.getSchemaFor(paramName);
        assertThatJson(schema).inPath("$.properties.double.type").isString().isEqualTo("integer");
        assertThatJson(schema).inPath("$.properties.double.title").isString().isEqualTo("With range column");
        assertThatJson(schema).inPath("$.properties.double.description").isString().isEqualTo("Slider with range column");
        final var uiSchema = dialogData.getUiSchema();
        assertThatJson(uiSchema).inPath("$.elements[1].type").isString().isEqualTo("Control");
        assertThatJson(uiSchema).inPath("$.elements[1].scope").isString()
            .isEqualTo(String.format("#/properties/model/properties/%s/properties/double", paramName));
        assertThatJson(uiSchema).inPath("$.elements[1].options.validation.min.parameters.min").isNumber()
            .isEqualTo(BigDecimal.valueOf(4.0));
        assertThatJson(uiSchema).inPath("$.elements[1].options.validation.max.parameters.max").isNumber()
            .isEqualTo(BigDecimal.valueOf(100.0));
    }
}
