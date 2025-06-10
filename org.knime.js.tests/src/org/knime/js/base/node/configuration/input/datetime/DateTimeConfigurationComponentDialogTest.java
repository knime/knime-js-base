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
 *   2 May 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.configuration.input.datetime;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import org.junit.jupiter.api.Test;
import org.knime.js.base.node.configuration.IntegratedComponentDialogTestBase;

import com.fasterxml.jackson.core.JsonProcessingException;

class DateTimeConfigurationComponentDialogTest extends IntegratedComponentDialogTestBase {

    @Test
    void testDateTimeConfigurationComponentDialogTypeDate() throws JsonProcessingException {
        final var dialogData = getComponentDialog(getTopLevelNodeId(11));
        final var paramName = "date-input-7";
        assertThatJson(dialogData.getDataFor(paramName)).inPath("$.date&time").isString().isEqualTo("2025-01-01");
        final var schema = dialogData.getSchemaFor(paramName);
        assertThatJson(schema).inPath("$.properties.date&time.type").isString().isEqualTo("string");
        assertThatJson(schema).inPath("$.properties.date&time.title").isString().isEqualTo("Date");
        assertThatJson(schema).inPath("$.properties.date&time.description").isString().isEqualTo("Date");
        final var uiSchema = dialogData.getUiSchema();
        assertThatJson(uiSchema).inPath("$.elements[0].type").isString().isEqualTo("Control");
        assertThatJson(uiSchema).inPath("$.elements[0].scope").isString()
            .isEqualTo(String.format("#/properties/model/properties/%s/properties/date&time", paramName));
        assertThatJson(uiSchema).inPath("$.elements[0].options.format").isString().isEqualTo("localDate");
    }

    @Test
    void testDateTimeConfigurationComponentDialogTypeTime() throws JsonProcessingException {
        final var dialogData = getComponentDialog(getTopLevelNodeId(11));
        final var paramName = "time-input-8";
        assertThatJson(dialogData.getDataFor(paramName)).inPath("$.date&time").isString().isEqualTo("12:12:00");
        assertThatJson(dialogData.getSchemaFor(paramName)).inPath("$.properties.date&time.type").isString().isEqualTo("string");
        final var uiSchema = dialogData.getUiSchema();
        assertThatJson(uiSchema).inPath("$.elements[1].type").isString().isEqualTo("Control");
        assertThatJson(uiSchema).inPath("$.elements[1].scope").isString()
            .isEqualTo(String.format("#/properties/model/properties/%s/properties/date&time", paramName));
        assertThatJson(uiSchema).inPath("$.elements[1].options.format").isString().isEqualTo("localTime");
        assertThatJson(uiSchema).inPath("$.elements[1].options.showSeconds").isBoolean().isFalse();
        assertThatJson(uiSchema).inPath("$.elements[1].options.showMilliseconds").isBoolean().isFalse();
    }

    @Test
    void testDateTimeConfigurationComponentDialogTypeLocalDateTime() throws JsonProcessingException {
        final var dialogData = getComponentDialog(getTopLevelNodeId(11));
        final var paramName = "local-date-time-input-9";
        assertThatJson(dialogData.getDataFor(paramName)).inPath("$.date&time").isString().isEqualTo("2025-12-31T23:59:59");
        assertThatJson(dialogData.getSchemaFor(paramName)).inPath("$.properties.date&time.type").isString().isEqualTo("string");
        final var uiSchema = dialogData.getUiSchema();
        assertThatJson(uiSchema).inPath("$.elements[2].type").isString().isEqualTo("Control");
        assertThatJson(uiSchema).inPath("$.elements[2].scope").isString()
            .isEqualTo(String.format("#/properties/model/properties/%s/properties/date&time", paramName));
        assertThatJson(uiSchema).inPath("$.elements[2].options.format").isString().isEqualTo("dateTime");
        assertThatJson(uiSchema).inPath("$.elements[2].options.showSeconds").isBoolean().isTrue();
        assertThatJson(uiSchema).inPath("$.elements[2].options.showMilliseconds").isBoolean().isFalse();
    }

    @Test
    void testDateTimeConfigurationComponentDialogTypeZonedDateTime() throws JsonProcessingException {
        final var dialogData = getComponentDialog(getTopLevelNodeId(11));
        final var paramName = "zoned-date-time-input-10";
        final var data = dialogData.getDataFor(paramName);
        assertThatJson(data).inPath("$.date&time.dateTime").isString().isEqualTo("2025-01-01T00:00:01");
        assertThatJson(data).inPath("$.date&time.timeZone").isString().isEqualTo("Europe/Zurich");
        assertThatJson(dialogData.getSchemaFor(paramName)).inPath("$.properties.date&time.type").isString().isEqualTo("string");
        final var uiSchema = dialogData.getUiSchema();
        assertThatJson(uiSchema).inPath("$.elements[3].type").isString().isEqualTo("Control");
        assertThatJson(uiSchema).inPath("$.elements[3].scope").isString()
            .isEqualTo(String.format("#/properties/model/properties/%s/properties/date&time", paramName));
        assertThatJson(uiSchema).inPath("$.elements[3].options.format").isString().isEqualTo("zonedDateTime");
        assertThatJson(uiSchema).inPath("$.elements[3].options.showSeconds").isBoolean().isTrue();
        assertThatJson(uiSchema).inPath("$.elements[3].options.showMilliseconds").isBoolean().isTrue();
        assertThatJson(uiSchema).inPath("$.elements[3].options.possibleValues").isArray();
    }

}
