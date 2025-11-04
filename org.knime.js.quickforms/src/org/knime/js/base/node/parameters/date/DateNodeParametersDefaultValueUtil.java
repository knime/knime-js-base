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
package org.knime.js.base.node.parameters.date;

import java.util.List;

import org.knime.js.base.node.base.input.date.DateNodeConfig;
import org.knime.js.base.node.base.input.date.DateNodeValue;
import org.knime.js.base.node.configuration.input.date.DateDialogNodeParameters;
import org.knime.js.base.node.parameters.date.DateTimeTypeInputParameters.AbstractDateTimeTypeInputValueProvider;
import org.knime.js.base.node.parameters.date.DateTimeTypeInputParameters.DateTimeTypeInputParametersModification;
import org.knime.node.parameters.persistence.legacy.EnumBooleanPersistor;
import org.knime.node.parameters.updates.EffectPredicate;
import org.knime.node.parameters.updates.EffectPredicateProvider;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.widget.choices.Label;
import org.knime.time.util.DateTimeType;
import org.knime.time.util.DateTimeType.IsDateTimeType;

/**
 * Utility class to handle everything default value related of the {@link DateDialogNodeParameters}, e.g. Persistors,
 * Modifications, Effects.
 *
 * @author Robin Gerling
 */
final class DateNodeParametersDefaultValueUtil {

    private DateNodeParametersDefaultValueUtil() {
        // Utility
    }

    enum TimeSelectionDefaultValue {
            @Label(value = DateNodeParameters.LABEL_VALUE_CUSTOM,
                description = DateNodeParameters.LABEL_DESCRIPTION_CUSTOM)
            CUSTOM, //
            @Label(value = DateNodeParameters.LABEL_VALUE_EXECUTION_TIME,
                description = DateNodeParameters.LABEL_DESCRIPTION_EXECUTION_TIME)
            EXECUTION_TIME
    }

    abstract static class ShowDateTimeType extends IsDateTimeType {

        protected ShowDateTimeType(final DateTimeType type) {
            super(type);
        }

        @Override
        public EffectPredicate init(final PredicateInitializer i) {
            return super.init(i)
                .and(i.getEnum(DefaultReferenceTimeSelection.class).isOneOf(TimeSelectionDefaultValue.CUSTOM));
        }
    }

    static final class DefaultReferenceTimeSelection implements ParameterReference<TimeSelectionDefaultValue> {
    }

    static final class DefaultReferenceDateTimeTypeInput implements ParameterReference<DateTimeTypeInputParameters> {
    }

    static final class DefaultShowLocalTime extends ShowDateTimeType {
        DefaultShowLocalTime() {
            super(DateTimeType.LOCAL_TIME);
        }
    }

    static final class DefaultShowLocalDate extends ShowDateTimeType {
        DefaultShowLocalDate() {
            super(DateTimeType.LOCAL_DATE);
        }
    }

    static final class DefaultShowLocalDateTime extends ShowDateTimeType {
        DefaultShowLocalDateTime() {
            super(DateTimeType.LOCAL_DATE_TIME);
        }
    }

    static final class DefaultShowZonedDateTime extends ShowDateTimeType {
        DefaultShowZonedDateTime() {
            super(DateTimeType.ZONED_DATE_TIME);
        }
    }

    static final class DefaultDateTimeTypeInputValueProvider extends AbstractDateTimeTypeInputValueProvider {
        DefaultDateTimeTypeInputValueProvider() {
            super(DateTimeType.Ref.class, DefaultReferenceDateTimeTypeInput.class);
        }
    }

    static final class DefaultModification extends DateTimeTypeInputParametersModification {
        @Override
        protected String getTitle() {
            return "Custom default %s";
        }

        @Override
        protected String getDescription() {
            return "The default %s.";
        }

        @Override
        protected List<Class<? extends EffectPredicateProvider>> getShowSpecificDateTimeEffectPredicateProviders() {
            return List.of(DefaultShowLocalDate.class, DefaultShowLocalTime.class, DefaultShowLocalDateTime.class,
                DefaultShowZonedDateTime.class);
        }
    }

    static final class DefaultPersistorTimeSelection extends EnumBooleanPersistor<TimeSelectionDefaultValue> {
        public DefaultPersistorTimeSelection() {
            super(DateNodeConfig.CFG_USE_DEFAULT_EXEC_TIME, TimeSelectionDefaultValue.class,
                TimeSelectionDefaultValue.EXECUTION_TIME);
        }
    }

    static final class DefaultPersistorDateTimeTypeInput
        extends DateTimeTypeInputParameters.DateTimeTypeInputPersistor {
        DefaultPersistorDateTimeTypeInput() {
            super(DateNodeValue.CFG_DATE);
        }
    }

}
