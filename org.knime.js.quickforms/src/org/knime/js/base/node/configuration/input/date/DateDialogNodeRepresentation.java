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

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import org.knime.core.node.dialog.DialogNodePanel;
import org.knime.core.node.dialog.SubNodeDescriptionProvider;
import org.knime.core.webui.node.dialog.WebDialogNodeRepresentation;
import org.knime.core.webui.node.dialog.defaultdialog.jsonforms.JsonFormsDataUtil;
import org.knime.core.webui.node.dialog.defaultdialog.jsonforms.renderers.DialogElementRendererSpec;
import org.knime.js.base.node.base.input.date.DateNodeRepresentation;
import org.knime.js.base.node.configuration.renderers.DateRenderer;
import org.knime.js.base.node.configuration.renderers.LocalDateTimeRenderer;
import org.knime.js.base.node.configuration.renderers.TimeRenderer;
import org.knime.js.base.node.configuration.renderers.ZonedDateTimeRenderer;
import org.knime.time.util.DateTimeType;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * The dialog representation of the date configuration node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class DateDialogNodeRepresentation extends DateNodeRepresentation<DateDialogNodeValue>
    implements SubNodeDescriptionProvider<DateDialogNodeValue>, WebDialogNodeRepresentation<DateDialogNodeValue> {

    @JsonCreator
    private DateDialogNodeRepresentation(@JsonProperty("label") final String label, //
        @JsonProperty("description") final String description, //
        @JsonProperty("required") final boolean required, //
        @JsonProperty("defaultValue") final DateDialogNodeValue defaultValue, //
        @JsonProperty("currentValue") final DateDialogNodeValue currentValue, //
        @JsonProperty("shownowbutton") final boolean showNowButton, //
        @JsonProperty("granularity") final String granularity, //
        @JsonProperty("usemin") final boolean useMin, //
        @JsonProperty("usemax") final boolean useMax, //
        @JsonProperty("useminexectime") final boolean useMinExecTime, //
        @JsonProperty("usemaxexectime") final boolean useMaxExecTime, //
        @JsonProperty("usedefaultexectime") final boolean useDefaultExecTime, //
        @JsonProperty("min") final String min, //
        @JsonProperty("max") final String max, //
        @JsonProperty("type") final String type) {
        super(label, description, required, defaultValue, currentValue, showNowButton, granularity, useMin, useMax,
            useMinExecTime, useMaxExecTime, useDefaultExecTime, min, max, type);
    }

    /**
     * @param currentValue The value currently used by the node
     * @param config The config of the node
     */
    public DateDialogNodeRepresentation(final DateDialogNodeValue currentValue,
        final DateInputDialogNodeConfig config) {
        super(currentValue, config.getDefaultValue(), config.getDateConfig(), config.getLabelConfig());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DialogNodePanel<DateDialogNodeValue> createDialogPanel() {
        return new DateConfigurationPanel(this);
    }

    @Override
    public DialogElementRendererSpec getWebUIDialogElementRendererSpec() {
        return switch (getType()) {
            case LOCAL_DATE -> new DateRenderer(this);
            case LOCAL_TIME -> new TimeRenderer(this);
            case LOCAL_DATE_TIME -> new LocalDateTimeRenderer(this);
            case ZONED_DATE_TIME -> new ZonedDateTimeRenderer(this);
        };
    }

    @Override
    public JsonNode transformValueToDialogJson(final DateDialogNodeValue dialogValue) throws IOException {
        final var mapper = JsonFormsDataUtil.getMapper();

        var date = dialogValue.equals(getDefaultValue()) && isUseDefaultExecTime() ? ZonedDateTime.now()
            : dialogValue.getDate();

        if (isShowMilliseconds()) {
            date = date.truncatedTo(ChronoUnit.MILLIS);
        } else if (isShowSeconds()) {
            date = date.truncatedTo(ChronoUnit.SECONDS);
        } else {
            date = date.truncatedTo(ChronoUnit.MINUTES);
        }

        final var value = switch (getType()) {
            case LOCAL_DATE -> date.toLocalDate();
            case LOCAL_TIME -> date.toLocalTime();
            case LOCAL_DATE_TIME -> date.toLocalDateTime();
            case ZONED_DATE_TIME -> date;
        };

        return mapper.valueToTree(value);
    }

    @Override
    public void setValueFromDialogJson(final JsonNode json, final DateDialogNodeValue value) throws IOException {
        final var mapper = JsonFormsDataUtil.getMapper();
        final var type = getType();

        final var currDate = value.getDate();
        final ZonedDateTime newDate;
        if (type == DateTimeType.LOCAL_DATE) {
            final var localDate = mapper.convertValue(json, LocalDate.class);
            newDate = ZonedDateTime.of(LocalDateTime.of(localDate, currDate.toLocalTime()), currDate.getZone());
        } else if (type == DateTimeType.LOCAL_TIME) {
            final var localTime = mapper.convertValue(json, LocalTime.class);
            newDate = ZonedDateTime.of(LocalDateTime.of(currDate.toLocalDate(), localTime), currDate.getZone());
        } else if (type == DateTimeType.LOCAL_DATE_TIME) {
            final var localDateTime = mapper.convertValue(json, LocalDateTime.class);
            newDate = ZonedDateTime.of(localDateTime, currDate.getZone());
        } else if (type == DateTimeType.ZONED_DATE_TIME) {
            newDate = mapper.convertValue(json, ZonedDateTime.class);
        } else {
            throw new IllegalArgumentException(
                String.format("Invalid value '%s'. Possible values: %s", type, DateTimeType.values()));
        }

        value.setDate(newDate);
    }
}
