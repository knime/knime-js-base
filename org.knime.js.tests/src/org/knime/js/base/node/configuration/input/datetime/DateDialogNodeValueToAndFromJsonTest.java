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
 *   6 May 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.configuration.input.datetime;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.knime.core.webui.node.dialog.defaultdialog.jsonforms.JsonFormsDataUtil;
import org.knime.js.base.node.base.input.date.GranularityTime;
import org.knime.js.base.node.configuration.input.date.DateDialogNodeFactory;
import org.knime.js.base.node.configuration.input.date.DateDialogNodeRepresentation;
import org.knime.js.base.node.configuration.input.date.DateDialogNodeValue;
import org.knime.js.base.node.configuration.input.date.DateInputDialogNodeConfig;
import org.knime.time.util.DateTimeType;

class DateDialogNodeValueToAndFromJsonTest {

    private static final LocalTime TIME_DFLT = LocalTime.of(0, 0);

    private static final LocalDate DATE_DFLT = LocalDate.of(1, 1, 1);

    private static final ZoneId ZONE_DFLT = ZoneId.of("Europe/Berlin");

    private static DateDialogNodeValue createDateDialogValue(final ZonedDateTime zonedDateTime) {
        final var dateDialogValue = new DateDialogNodeFactory().createNodeModel().createEmptyDialogValue();
        dateDialogValue.setDate(zonedDateTime);
        return dateDialogValue;
    }

    private static DateDialogNodeRepresentation createDateDialogNodeRepresentation(
        final DateDialogNodeValue dateDialogNodeValue, final DateTimeType dateTimeType,
        final GranularityTime granularityTime) {
        final var dateInputDialogNodeConfig = new DateInputDialogNodeConfig();
        dateInputDialogNodeConfig.getDateConfig().setGranularity(granularityTime);
        dateInputDialogNodeConfig.getDateConfig().setType(dateTimeType);
        return new DateDialogNodeRepresentation(dateDialogNodeValue, dateInputDialogNodeConfig);
    }

    static Stream<Arguments> getFromAndToDialogJSONTestCases() {
        final var toDate = LocalDate.of(1234, 1, 24);
        final var fromDate = LocalDate.of(4321, 4, 21);
        final var toTime = LocalTime.of(12, 12, 12);
        final var fromTime = LocalTime.of(21, 21, 21);
        final var toLocalDateTime = LocalDateTime.of(toDate, toTime);
        final var fromLocalDateTime = LocalDateTime.of(fromDate, fromTime);
        final var toZonedDateTime = ZonedDateTime.of(toLocalDateTime, ZoneId.of("Europe/Zurich"));
        final var fromZonedDateTime = ZonedDateTime.of(fromLocalDateTime, ZoneId.of("Europe/Paris"));

        return Stream.of( //
            Arguments.of(DateTimeType.LOCAL_DATE, //
                ZonedDateTime.of(toDate, TIME_DFLT, ZONE_DFLT), toDate, //
                fromDate, ZonedDateTime.of(fromDate, TIME_DFLT, ZONE_DFLT)), //
            Arguments.of(DateTimeType.LOCAL_TIME, //
                ZonedDateTime.of(DATE_DFLT, toTime, ZONE_DFLT), toTime, //
                fromTime, ZonedDateTime.of(DATE_DFLT, fromTime, ZONE_DFLT)), //
            Arguments.of(DateTimeType.LOCAL_DATE_TIME, //
                ZonedDateTime.of(toLocalDateTime, ZONE_DFLT), toLocalDateTime, //
                fromLocalDateTime, ZonedDateTime.of(fromLocalDateTime, ZONE_DFLT)), //
            Arguments.of(DateTimeType.ZONED_DATE_TIME, //
                toZonedDateTime, toZonedDateTime, //
                fromZonedDateTime, fromZonedDateTime) //
        );
    }

    @ParameterizedTest(name = "DateTimeType: ''{0}''")
    @MethodSource("getFromAndToDialogJSONTestCases")
    void testFromAndToDialogJSON(final DateTimeType dateTimeType, final ZonedDateTime toDialogDateTime,
        final Temporal expectedToDialogDateTime, final Temporal fromDialogDateTime,
        final ZonedDateTime expectedFromDialogDateTime) throws IOException {
        final var dateDialogNodeValue = createDateDialogValue(toDialogDateTime);
        final var dateDialogNodeRep =
            createDateDialogNodeRepresentation(dateDialogNodeValue, dateTimeType, GranularityTime.SHOW_SECONDS);

        final var dialogJSON = dateDialogNodeRep.transformValueToDialogJson(dateDialogNodeValue);
        assertEquals(dialogJSON, JsonFormsDataUtil.getMapper().valueToTree(expectedToDialogDateTime),
            "Unexpected value during serialization");

        dateDialogNodeRep.setValueFromDialogJson(JsonFormsDataUtil.getMapper().valueToTree(fromDialogDateTime),
            dateDialogNodeValue);
        assertEquals(expectedFromDialogDateTime, dateDialogNodeValue.getDate(),
            "Unexpected value during deserialization");
    }

    static Stream<Arguments> getToDialogJSONTruncateTimeTestCases() {
        return Stream.of( //
            Arguments.of(GranularityTime.SHOW_MINUTES, LocalTime.of(12, 12)),
            Arguments.of(GranularityTime.SHOW_SECONDS, LocalTime.of(12, 12, 12)),
            Arguments.of(GranularityTime.SHOW_MILLIS, LocalTime.of(12, 12, 12, 123000000)));
    }

    @ParameterizedTest(name = "Truncate to: ''{0}''")
    @MethodSource("getToDialogJSONTruncateTimeTestCases")
    void testToDialogJSONTruncateTime(final GranularityTime granularity, final LocalTime expectedTime)
        throws IOException {
        final var testTime = LocalTime.of(12, 12, 12, 123456789);
        final var testDateTime = ZonedDateTime.of(LocalDateTime.of(DATE_DFLT, testTime), ZONE_DFLT);
        final var dateDialogNodeValue = createDateDialogValue(testDateTime);
        final var dateDialogNodeRep =
            createDateDialogNodeRepresentation(dateDialogNodeValue, DateTimeType.LOCAL_TIME, granularity);

        final var dialogJSON = dateDialogNodeRep.transformValueToDialogJson(dateDialogNodeValue);
        assertEquals(dialogJSON, JsonFormsDataUtil.getMapper().valueToTree(expectedTime),
            "Unexpected value during serialization");
    }

}
