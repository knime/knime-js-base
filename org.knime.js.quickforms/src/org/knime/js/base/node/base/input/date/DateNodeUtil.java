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
 *   23 May 2019 (albrecht): created
 */
package org.knime.js.base.node.base.input.date;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Optional;

import org.knime.time.util.DateTimeType;

/**
 * Utility methods for the Date Configuration and Widget nodes
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class DateNodeUtil {

    /**
     * Formatter for the date to string and string to date operations.
     */
    public static final DateTimeFormatter LOCAL_DATE_FORMATTER = DateTimeFormatter.ISO_DATE;

    /**
     * Formatter for the date to string and string to date operations.
     */
    public static final DateTimeFormatter LOCAL_TIME_FORMATTER = DateTimeFormatter.ISO_TIME;

    /**
     * Formatter for the date to string and string to date operations.
     */
    public static final DateTimeFormatter LOCAL_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    /**
     * Formatter for the date to string and string to date operations.
     */
    public static final DateTimeFormatter ZONED_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_ZONED_DATE_TIME;

    /**
     * Validate if a given date/time value honors a given config
     *
     * @param config the config with settings to validate
     * @param value the value to validate
     * @param now the current time
     * @return an optional string with an error message, not present if validation succeeds
     */
    public static Optional<String> validateMinMaxByConfig(final DateNodeConfig config, final ZonedDateTime value,
        final ZonedDateTime now) {
        final ZonedDateTime min = config.isUseMinExecTime() ? now : config.getMin();
        final ZonedDateTime max = config.isUseMaxExecTime() ? now : config.getMax();
        final DateTimeType type = config.getType();

        return validateMinMax(value, config.isUseMin(), config.isUseMax(), min, max, type);
    }

    /**
     * Validate if a given date/time value honors given conditions
     *
     * @param value the value to validate
     * @param useMin if minimum date/time is to be validated
     * @param useMax if maximum date/time is to be validated
     * @param min the minimum date/time
     * @param max the maximum date/time
     * @param type the {@link DateTimeType} to be used for the given value
     * @return an optional string with an error message, not present if validation succeeds
     */
    public static Optional<String> validateMinMax(final ZonedDateTime value, final boolean useMin, final boolean useMax,
        final ZonedDateTime min, final ZonedDateTime max, final DateTimeType type) {
        final boolean checkMin;
        final boolean checkMax;
        final Temporal valueTemporal;
        final Temporal minTemporal;
        final Temporal maxTemporal;
        if (type == DateTimeType.LOCAL_DATE) {
            checkMin = value.toLocalDate().isBefore(min.toLocalDate());
            checkMax = value.toLocalDate().isAfter(max.toLocalDate());
            valueTemporal = value.toLocalDate();
            minTemporal = min.toLocalDate();
            maxTemporal = max.toLocalDate();
        } else if (type == DateTimeType.LOCAL_TIME) {
            checkMin = value.toLocalTime().isBefore(min.toLocalTime());
            checkMax = value.toLocalTime().isAfter(max.toLocalTime());
            valueTemporal = value.toLocalTime();
            minTemporal = min.toLocalTime();
            maxTemporal = max.toLocalTime();
        } else if (type == DateTimeType.LOCAL_DATE_TIME) {
            checkMin = value.toLocalDateTime().isBefore(min.toLocalDateTime());
            checkMax = value.toLocalDateTime().isAfter(max.toLocalDateTime());
            valueTemporal = value.toLocalDateTime();
            minTemporal = min.toLocalDateTime();
            maxTemporal = max.toLocalDateTime();
        } else {
            checkMin = value.isBefore(min);
            checkMax = value.isAfter(max);
            valueTemporal = value;
            minTemporal = min;
            maxTemporal = max;
        }
        if (useMin && checkMin) {
            return Optional.of("The set date&time '" + valueTemporal + "' must not be before '" + minTemporal + "'.");
        }
        if (useMax && checkMax) {
            return Optional.of("The set date&time '" + valueTemporal + "' must not be after '" + maxTemporal + "'.");
        }
        return Optional.empty();
    }

}
