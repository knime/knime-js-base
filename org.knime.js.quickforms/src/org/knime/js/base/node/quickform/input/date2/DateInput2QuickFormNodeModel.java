/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME GmbH, Konstanz, Germany
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
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
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
 * ------------------------------------------------------------------------
 */
package org.knime.js.base.node.quickform.input.date2;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Optional;

import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.web.ValidationError;
import org.knime.js.base.node.quickform.QuickFormFlowVariableNodeModel;
import org.knime.js.base.node.quickform.ValueOverwriteMode;
import org.knime.js.core.layout.LayoutTemplateProvider;
import org.knime.js.core.layout.bs.JSONLayoutViewContent;
import org.knime.js.core.layout.bs.JSONLayoutViewContent.ResizeMethod;
import org.knime.time.util.DateTimeType;

/**
 * The model for the date input quick form node.
 *
 * @author Patrick Winter, KNIME.com AG, Zurich, Switzerland
 * @author Simon Schmid, KNIME.com, Konstanz, Germany
 */
public class DateInput2QuickFormNodeModel extends
    QuickFormFlowVariableNodeModel<DateInput2QuickFormRepresentation, DateInput2QuickFormValue, DateTimeInputQuickFormConfig>
    implements LayoutTemplateProvider {

    /**
     * @param viewName
     */
    protected DateInput2QuickFormNodeModel(final String viewName) {
        super(viewName);
    }

    /**
     * Formatter for the date to string and string to date operations.
     */
    static final DateTimeFormatter LOCAL_DATE_FORMATTER = DateTimeFormatter.ISO_DATE;

    /**
     * Formatter for the date to string and string to date operations.
     */
    static final DateTimeFormatter LOCAL_TIME_FORMATTER = DateTimeFormatter.ISO_TIME;

    /**
     * Formatter for the date to string and string to date operations.
     */
    static final DateTimeFormatter LOCAL_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    /**
     * Formatter for the date to string and string to date operations.
     */
    static final DateTimeFormatter ZONED_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_ZONED_DATE_TIME;

    /**
     * {@inheritDoc}
     */
    @Override
    public DateInput2QuickFormValue createEmptyViewValue() {
        return new DateInput2QuickFormValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org_knime_js_base_node_quickform_input_date2";
    }

    /** {@inheritDoc} */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        final ZonedDateTime value;
        final ZonedDateTime now = ZonedDateTime.now();
        if (getOverwriteMode() == ValueOverwriteMode.NONE) {
            value = getConfig().getUseDefaultExecTime() ? now : getRelevantValue().getDate();
        } else {
            value = getRelevantValue().getDate();
        }
        final Optional<String> validationResult = validateMinMaxByConfig(value, now);
        if (validationResult.isPresent()) {
            if (getConfig().getUseDefaultExecTime()) {
                setWarningMessage("The current time is either before the earliest or latest allowed time!");
            } else if (getConfig().getUseMinExecTime()) {
                setWarningMessage("The selected time is before the current time!");
            } else if (getConfig().getUseMaxExecTime()) {
                setWarningMessage("The selected time is after the current time!");
            } else {
                throw new InvalidSettingsException(validationResult.get());
            }
        }
        return super.configure(inSpecs);
    }

    /** {@inheritDoc} */
    @Override
    protected PortObject[] execute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        final ZonedDateTime value;
        final ZonedDateTime now = ZonedDateTime.now();
        if (getOverwriteMode() == ValueOverwriteMode.NONE) {
            value = getConfig().getUseDefaultExecTime() ? now : getRelevantValue().getDate();
        } else {
            value = getRelevantValue().getDate();
        }
        final Optional<String> validationResult = validateMinMaxByConfig(value, now);
        if (validationResult.isPresent()) {
            throw new InvalidSettingsException(validationResult.get());
        }
        return super.execute(inObjects, exec);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createAndPushFlowVariable() throws InvalidSettingsException {
        final ZonedDateTime value;
        if (getOverwriteMode() == ValueOverwriteMode.NONE) {
            value = getConfig().getUseDefaultExecTime() ? ZonedDateTime.now() : getRelevantValue().getDate();
        } else {
            value = getRelevantValue().getDate();
        }
        final DateTimeType type = getConfig().getType();
        final DateTimeFormatter formatter;
        final Temporal temporal;
        if (type == DateTimeType.LOCAL_DATE) {
            formatter = LOCAL_DATE_FORMATTER;
            temporal = value.toLocalDate();
        } else if (type == DateTimeType.LOCAL_TIME) {
            formatter = LOCAL_TIME_FORMATTER;
            temporal = value.toLocalTime();
        } else if (type == DateTimeType.LOCAL_DATE_TIME) {
            formatter = LOCAL_DATE_TIME_FORMATTER;
            temporal = value.toLocalDateTime();
        } else {
            formatter = ZONED_DATE_TIME_FORMATTER;
            temporal = value;
        }
        pushFlowVariableString(getConfig().getFlowVariableName(), formatter.format(temporal));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void copyValueToConfig() {
        getConfig().getDefaultValue().setDate(getViewValue().getDate());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DateTimeInputQuickFormConfig createEmptyConfig() {
        return new DateTimeInputQuickFormConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DateInput2QuickFormRepresentation getRepresentation() {
        return new DateInput2QuickFormRepresentation(getRelevantValue(), getConfig());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(final DateInput2QuickFormValue viewContent) {
        final Optional<String> validationResult = validateMinMaxByConfig(viewContent.getDate(), ZonedDateTime.now());
        if (validationResult.isPresent()) {
            return new ValidationError(validationResult.get());
        }
        return super.validateViewValue(viewContent);
    }

    private Optional<String> validateMinMaxByConfig(final ZonedDateTime value, final ZonedDateTime now) {
        final ZonedDateTime min = getConfig().getUseMinExecTime() ? now : getConfig().getMin();
        final ZonedDateTime max = getConfig().getUseMaxExecTime() ? now : getConfig().getMax();
        final DateTimeType type = getConfig().getType();

        return validateMinMax(value, getConfig().getUseMin(), getConfig().getUseMax(), min, max, type);
    }

    static Optional<String> validateMinMax(final ZonedDateTime value, final boolean useMin, final boolean useMax,
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

    /**
     * {@inheritDoc}
     */
    @Override
    public JSONLayoutViewContent getLayoutTemplate() {
        JSONLayoutViewContent template = new JSONLayoutViewContent();
        template.setResizeMethod(ResizeMethod.VIEW_TAGGED_ELEMENT);
        return template;
    }

}
