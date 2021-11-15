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
package org.knime.js.base.node.widget.input.date;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Optional;

import org.knime.core.node.ExecutionContext;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.web.ValidationError;
import org.knime.js.base.node.base.input.date.DateNodeConfig;
import org.knime.js.base.node.base.input.date.DateNodeRepresentation;
import org.knime.js.base.node.base.input.date.DateNodeUtil;
import org.knime.js.base.node.base.input.date.DateNodeValue;
import org.knime.js.base.node.quickform.ValueOverwriteMode;
import org.knime.js.base.node.widget.WidgetFlowVariableNodeModel;
import org.knime.time.util.DateTimeType;
import org.knime.time.util.DateTimeUtils;

/**
 * The node model for the double widget node
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class DateWidgetNodeModel
    extends WidgetFlowVariableNodeModel<DateNodeRepresentation<DateNodeValue>, DateNodeValue, DateInputWidgetConfig> {

    private ZonedDateTime m_previousTime = DateNodeConfig.DEFAULT_ZDT;

    /**
     * @param viewName the interactive view name
     */
    public DateWidgetNodeModel(final String viewName) {
        super(viewName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
        final ZonedDateTime value;
        final ZonedDateTime now = DateTimeUtils.nowZonedDateTimeMillis();
        DateNodeConfig dateConfig = getConfig().getDateNodeConfig();
        if (getOverwriteMode() == ValueOverwriteMode.NONE && dateConfig.isUseDefaultExecTime()) {
            value = now;
        } else {
            value = getRelevantValue().getDate();
        }
        final Optional<String> validationResult = DateNodeUtil.validateMinMaxByConfig(dateConfig, value, now);
        if (validationResult.isPresent()) {
            if (dateConfig.isUseDefaultExecTime()) {
                setWarningMessage("The current time is either before the earliest or latest allowed time!");
            } else if (dateConfig.isUseMinExecTime()) {
                setWarningMessage("The selected time is before the current time!");
            } else if (dateConfig.isUseMaxExecTime()) {
                setWarningMessage("The selected time is after the current time!");
            } else {
                throw new InvalidSettingsException(validationResult.get());
            }
        }
        return super.configure(inSpecs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] performExecute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
        final ZonedDateTime value;
        final ZonedDateTime now = DateTimeUtils.nowZonedDateTimeMillis();
        if (getOverwriteMode() == ValueOverwriteMode.NONE && getConfig().getDateNodeConfig().isUseDefaultExecTime()) {
            value = now;
        } else {
            value = getRelevantValue().getDate();
        }
        final Optional<String> validationResult =
            DateNodeUtil.validateMinMaxByConfig(getConfig().getDateNodeConfig(), value, now);
        if (validationResult.isPresent()) {
            throw new InvalidSettingsException(validationResult.get());
        }
        m_previousTime = getConfig().getDefaultValue().getDate();
        return super.performExecute(inObjects, exec);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DateNodeValue createEmptyViewValue() {
        return new DateNodeValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJavascriptObjectID() {
        return "org.knime.js.base.node.widget.input.date";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createAndPushFlowVariable() throws InvalidSettingsException {
        final ZonedDateTime value;
        DateNodeConfig dateConfig = getConfig().getDateNodeConfig();
        final ZonedDateTime now = DateTimeUtils.nowZonedDateTimeMillis();
        if (getOverwriteMode() == ValueOverwriteMode.NONE && dateConfig.isUseDefaultExecTime()) {
            value = now;
        } else {
            value = getRelevantValue().getDate();
        }
        final DateTimeType type = dateConfig.getType();
        final DateTimeFormatter formatter;
        final Temporal temporal;
        if (type == DateTimeType.LOCAL_DATE) {
            formatter = DateNodeUtil.LOCAL_DATE_FORMATTER;
            temporal = value.toLocalDate();
        } else if (type == DateTimeType.LOCAL_TIME) {
            formatter = DateNodeUtil.LOCAL_TIME_FORMATTER;
            temporal = value.toLocalTime();
        } else if (type == DateTimeType.LOCAL_DATE_TIME) {
            formatter = DateNodeUtil.LOCAL_DATE_TIME_FORMATTER;
            temporal = value.toLocalDateTime();
        } else {
            formatter = DateNodeUtil.ZONED_DATE_TIME_FORMATTER;
            temporal = value;
        }
        pushFlowVariableString(getConfig().getFlowVariableName(), formatter.format(temporal));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DateInputWidgetConfig createEmptyConfig() {
        return new DateInputWidgetConfig();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DateNodeRepresentation<DateNodeValue> getRepresentation() {
        DateInputWidgetConfig config = getConfig();
        return new DateWidgetNodeRepresentation(getRelevantValue(), config.getDefaultValue(),
            config.getDateNodeConfig(), config.getLabelConfig());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void useCurrentValueAsDefault() {
        getConfig().getDefaultValue().setDate(getViewValue().getDate());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationError validateViewValue(final DateNodeValue value) {
        final Optional<String> validationResult =
            DateNodeUtil.validateMinMaxByConfig(getConfig().getDateNodeConfig(), value.getDate(), ZonedDateTime.now());
            if (validationResult.isPresent()) {
                return new ValidationError(validationResult.get());
            }
        return super.validateViewValue(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DateNodeValue copyConfigToViewValue(final DateNodeValue currentViewValue,
        final DateInputWidgetConfig config, final DateInputWidgetConfig previousConfig) {
        // We need to check against the last expected execution time which should match against any config
        // timestamps. If not, we can assume there is an override by a flow-variable. This allows us to avoid
        // ignoring client-side updates because of timestamp differences but allow upstream variable overrides.
        ZonedDateTime configDate = config.getDefaultValue().getDate();
        if (!(configDate.equals(m_previousTime) || configDate.equals(previousConfig.getDefaultValue().getDate()))) {
            return getConfig().getDefaultValue();
        } else {
            return currentViewValue;
        }
    }

}
