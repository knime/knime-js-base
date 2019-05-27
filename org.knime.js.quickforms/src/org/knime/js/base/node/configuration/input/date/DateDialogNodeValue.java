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
package org.knime.js.base.node.configuration.input.date;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;
import javax.json.JsonValue;

import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.dialog.DialogNodeValue;
import org.knime.js.base.node.base.date.DateNodeConfig;
import org.knime.js.base.node.base.date.DateNodeValue;
import org.knime.time.util.DateTimeUtils;

/**
 * The value for the date configuration node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class DateDialogNodeValue extends DateNodeValue implements DialogNodeValue {

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromNodeSettingsInDialog(final NodeSettingsRO settings) {
        String value = settings.getString(CFG_DATE, DateNodeConfig.DEFAULT_ZDT.toString());
        if (value == null) {
            setDate(null);
        } else {
            try {
                setDate(ZonedDateTime.parse(value));
            } catch (Exception e) {
                setDate(DateNodeConfig.DEFAULT_ZDT);
            }
        }
    }

    private void updateDateByStringInput(final String string) throws IllegalArgumentException {
        ZonedDateTime date = getDate();
        if (DateTimeUtils.asZonedDateTime(string).isPresent()) {
            setDate(ZonedDateTime.parse(string));
        } else if (DateTimeUtils.asLocalDateTime(string).isPresent()) {
            setDate(ZonedDateTime.of(LocalDateTime.parse(string), date.getZone()));
        } else if (DateTimeUtils.asLocalDate(string).isPresent()) {
            setDate(ZonedDateTime.of(LocalDate.parse(string), date.toLocalTime(), date.getZone()));
        } else if (DateTimeUtils.asLocalTime(string).isPresent()) {
            setDate(ZonedDateTime.of(date.toLocalDate(), LocalTime.parse(string), date.getZone()));
        } else if (DateTimeUtils.asTimezone(string).isPresent()) {
            setDate(ZonedDateTime.of(date.toLocalDateTime(), ZoneId.of(string)));
        } else {
            throw new IllegalArgumentException(string + " cannot be parsed as any date&time type or time zone!");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromString(final String fromCmdLine) throws UnsupportedOperationException {
        try {
            if (fromCmdLine == null) {
                throw new UnsupportedOperationException("Input must not be null!");
            } else {
                updateDateByStringInput(fromCmdLine);
            }
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromJson(final JsonValue json) throws JsonException {
        if (json instanceof JsonString) {
            loadFromString(((JsonString)json).getString());
        } else if (json instanceof JsonObject) {
            try {
                JsonValue val = ((JsonObject)json).get(CFG_DATE);
                if (JsonValue.NULL.equals(val)) {
                    throw new IllegalArgumentException("Input must not be null!");
                }
                String dateVal = ((JsonObject)json).getString(CFG_DATE);
                if (dateVal == null) {
                    throw new IllegalArgumentException("Input must not be null!");
                } else {
                    updateDateByStringInput(dateVal);
                }
            } catch (Exception e) {
                throw new JsonException("Expected string value for key '" + CFG_DATE
                    + "' to be a date, time, date&time, zoned date&time or a time zone in ISO format.", e);
            }
        } else {
            throw new JsonException("Expected JSON object or JSON string, but got " + json.getValueType());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonValue toJson() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        if (getDate() == null) {
            builder.addNull(CFG_DATE);
        } else {
            builder.add(CFG_DATE, getDate().toString());
        }
        return builder.build().get(CFG_DATE);
    }
}
