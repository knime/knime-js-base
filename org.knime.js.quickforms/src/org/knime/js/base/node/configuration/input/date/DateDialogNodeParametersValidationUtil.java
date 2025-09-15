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
 *   18 Sept 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.configuration.input.date;

import java.util.List;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.base.node.base.input.date.DateNodeConfig;
import org.knime.js.base.node.configuration.input.date.DateTimeTypeInputParameters.AbstractDateTimeTypeInputValueProvider;
import org.knime.js.base.node.configuration.input.date.DateTimeTypeInputParameters.DateTimeTypeInputParametersModification;
import org.knime.node.parameters.persistence.NodeParametersPersistor;
import org.knime.node.parameters.updates.EffectPredicate;
import org.knime.node.parameters.updates.EffectPredicateProvider;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.widget.choices.Label;
import org.knime.time.util.DateTimeType;
import org.knime.time.util.DateTimeType.IsDateTimeType;

/**
 * Utility class to handle everything validation related of the {@link DateDialogNodeParameters}, e.g. Persistors,
 * Modifications, Effects.
 *
 * @author Robin Gerling
 */
final class DateDialogNodeParametersValidationUtil {

    private DateDialogNodeParametersValidationUtil() {
        // Utility
    }

    enum TimeSelectionMinMax {
            @Label(value = "None", description = "Do not use a date&amp;time.")
            NONE, //
            @Label(value = DateDialogNodeParameters.LABEL_VALUE_CUSTOM,
                description = DateDialogNodeParameters.LABEL_DESCRIPTION_CUSTOM)
            CUSTOM, //
            @Label(value = DateDialogNodeParameters.LABEL_VALUE_EXECUTION_TIME,
                description = DateDialogNodeParameters.LABEL_DESCRIPTION_EXECUTION_TIME)
            EXECUTION_TIME
    }

    abstract static class ShowDateTimeType extends IsDateTimeType {

        private Class<? extends ParameterReference<TimeSelectionMinMax>> m_timeSelectionRef;

        /**
         * @param type the chosen DateTimeType
         * @param timeSelectionRef reference to the time selection field
         */
        protected ShowDateTimeType(final DateTimeType type,
            final Class<? extends ParameterReference<TimeSelectionMinMax>> timeSelectionRef) {
            super(type);
            m_timeSelectionRef = timeSelectionRef;
        }

        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            return super.init(i) //
                .and(i.getEnum(m_timeSelectionRef).isOneOf(TimeSelectionMinMax.CUSTOM));
        }
    }

    abstract static class AbstractTimeSelectionMinMaxPersistor implements NodeParametersPersistor<TimeSelectionMinMax> {

        private final String m_cfgKeyUseMinOrMax;

        private final String m_cfgKeyUseExecutionTime;

        AbstractTimeSelectionMinMaxPersistor(final String cfgKeyUseMinOrMax, final String cfgKeyUseExecutionTime) {
            m_cfgKeyUseMinOrMax = cfgKeyUseMinOrMax;
            m_cfgKeyUseExecutionTime = cfgKeyUseExecutionTime;
        }

        @Override
        public TimeSelectionMinMax load(final NodeSettingsRO settings) throws InvalidSettingsException {
            if (!settings.getBoolean(m_cfgKeyUseMinOrMax)) {
                return TimeSelectionMinMax.NONE;
            }
            final var useExecutionTime = settings.getBoolean(m_cfgKeyUseExecutionTime);
            return useExecutionTime ? TimeSelectionMinMax.EXECUTION_TIME : TimeSelectionMinMax.CUSTOM;
        }

        @Override
        public void save(final TimeSelectionMinMax param, final NodeSettingsWO settings) {
            boolean useMinOrMax = false;
            boolean useExecutionTime = false;
            if (param == TimeSelectionMinMax.CUSTOM) {
                useMinOrMax = true;
            } else if (param == TimeSelectionMinMax.EXECUTION_TIME) {
                useExecutionTime = true;
            }
            settings.addBoolean(m_cfgKeyUseMinOrMax, useMinOrMax);
            settings.addBoolean(m_cfgKeyUseExecutionTime, useExecutionTime);
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][]{{m_cfgKeyUseMinOrMax}, {m_cfgKeyUseExecutionTime}};
        }
    }

    static final class MinReferenceTimeSelection implements ParameterReference<TimeSelectionMinMax> {
    }

    static final class MinReferenceDateTimeTypeInput implements ParameterReference<DateTimeTypeInputParameters> {
    }

    static final class MinPersistorTimeSelection extends AbstractTimeSelectionMinMaxPersistor {
        public MinPersistorTimeSelection() {
            super(DateNodeConfig.CFG_USE_MIN, DateNodeConfig.CFG_USE_MIN_EXEC_TIME);
        }
    }

    static final class MinPersistorDateTimeTypeInput extends DateTimeTypeInputParameters.DateTimeTypeInputPersistor {
        MinPersistorDateTimeTypeInput() {
            super(DateNodeConfig.CFG_MIN);
        }
    }

    static final class MinShowLocalTime extends ShowDateTimeType {
        MinShowLocalTime() {
            super(DateTimeType.LOCAL_TIME, MinReferenceTimeSelection.class);
        }
    }

    static final class MinShowLocalDate extends ShowDateTimeType {
        MinShowLocalDate() {
            super(DateTimeType.LOCAL_DATE, MinReferenceTimeSelection.class);
        }
    }

    static final class MinShowLocalDateTime extends ShowDateTimeType {
        MinShowLocalDateTime() {
            super(DateTimeType.LOCAL_DATE_TIME, MinReferenceTimeSelection.class);
        }
    }

    static final class MinShowZonedDateTime extends ShowDateTimeType {
        MinShowZonedDateTime() {
            super(DateTimeType.ZONED_DATE_TIME, MinReferenceTimeSelection.class);
        }
    }

    static final class MinDateTimeTypeInputValueProvider extends AbstractDateTimeTypeInputValueProvider {
        MinDateTimeTypeInputValueProvider() {
            super(DateTimeType.Ref.class, MinReferenceDateTimeTypeInput.class);
        }
    }

    static final class MinModification extends DateTimeTypeInputParametersModification {
        @Override
        protected String getTitle() {
            return "Custom earliest %s";
        }

        @Override
        protected String getDescription() {
            return "The earliest allowed %s.";
        }

        @Override
        protected List<Class<? extends EffectPredicateProvider>> getShowSpecificDateTimeEffectPredicateProviders() {
            return List.of(MinShowLocalDate.class, MinShowLocalTime.class, MinShowLocalDateTime.class,
                MinShowZonedDateTime.class);
        }
    }

    static final class MaxReferenceTimeSelection implements ParameterReference<TimeSelectionMinMax> {
    }

    static final class MaxReferenceDateTimeTypeInput implements ParameterReference<DateTimeTypeInputParameters> {
    }

    static final class MaxPersistorTimeSelection extends AbstractTimeSelectionMinMaxPersistor {
        public MaxPersistorTimeSelection() {
            super(DateNodeConfig.CFG_USE_MAX, DateNodeConfig.CFG_USE_MAX_EXEC_TIME);
        }
    }

    static final class MaxPersistorDateTimeTypeInput extends DateTimeTypeInputParameters.DateTimeTypeInputPersistor {
        MaxPersistorDateTimeTypeInput() {
            super(DateNodeConfig.CFG_MAX);
        }
    }

    static final class MaxShowLocalTime extends ShowDateTimeType {
        MaxShowLocalTime() {
            super(DateTimeType.LOCAL_TIME, MaxReferenceTimeSelection.class);
        }
    }

    static final class MaxShowLocalDate extends ShowDateTimeType {
        MaxShowLocalDate() {
            super(DateTimeType.LOCAL_DATE, MaxReferenceTimeSelection.class);
        }
    }

    static final class MaxShowLocalDateTime extends ShowDateTimeType {
        MaxShowLocalDateTime() {
            super(DateTimeType.LOCAL_DATE_TIME, MaxReferenceTimeSelection.class);
        }
    }

    static final class MaxShowZonedDateTime extends ShowDateTimeType {
        MaxShowZonedDateTime() {
            super(DateTimeType.ZONED_DATE_TIME, MaxReferenceTimeSelection.class);
        }
    }

    static final class MaxDateTimeTypeInputValueProvider extends AbstractDateTimeTypeInputValueProvider {
        MaxDateTimeTypeInputValueProvider() {
            super(DateTimeType.Ref.class, MaxReferenceDateTimeTypeInput.class);
        }
    }

    static final class MaxModification extends DateTimeTypeInputParametersModification {
        @Override
        protected String getTitle() {
            return "Custom latest %s";
        }

        @Override
        protected String getDescription() {
            return "The latest allowed %s.";
        }

        @Override
        protected List<Class<? extends EffectPredicateProvider>> getShowSpecificDateTimeEffectPredicateProviders() {
            return List.of(MaxShowLocalDate.class, MaxShowLocalTime.class, MaxShowLocalDateTime.class,
                MaxShowZonedDateTime.class);
        }
    }
}
