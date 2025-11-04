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
 *   12 Sept 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.parameters.date;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

import org.apache.commons.lang.StringEscapeUtils;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.webui.node.dialog.defaultdialog.util.updates.StateComputationFailureException;
import org.knime.core.webui.node.dialog.defaultdialog.widget.Modification;
import org.knime.js.base.node.parameters.date.DateTimeTypeInputParameters.DateTimeTypeInputParametersModification.LocalDateModificationReference;
import org.knime.js.base.node.parameters.date.DateTimeTypeInputParameters.DateTimeTypeInputParametersModification.LocalDateTimeModificationReference;
import org.knime.js.base.node.parameters.date.DateTimeTypeInputParameters.DateTimeTypeInputParametersModification.LocalTimeModificationReference;
import org.knime.js.base.node.parameters.date.DateTimeTypeInputParameters.DateTimeTypeInputParametersModification.ZonedDateTimeModificationReference;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.NodeParametersInput;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.persistence.NodeParametersPersistor;
import org.knime.node.parameters.updates.Effect;
import org.knime.node.parameters.updates.Effect.EffectType;
import org.knime.node.parameters.updates.EffectPredicateProvider;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.updates.StateProvider;
import org.knime.time.util.DateTimeType;

/**
 * WebUI Node Parameters for the DateTimeInput of the Date&Time Configuration consisting of a {@link LocalDate},
 * {@link LocalTime}, {@link LocalDateTime}, and {@link ZonedDateTime} input.<br>
 * This class is responsible that all the input fields contain the same values (w.r.t the component of a DateTime they
 * represent). I.e., when changing the time in the LocalTime input, the LocalDateTime input, and the ZonedDateTime input
 * will be updated as soon as the value of the {@link DateTimeType} input changes.<br>
 * That means, that only one of the four inputs can be different than the others, why the minority function can be used
 * to determine the different components of a ZonedDateTime ({@link #minority}, {@link #getZonedDateTimeFromDateTimes}).
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz
 */
@SuppressWarnings("restriction")
final class DateTimeTypeInputParameters implements NodeParameters {

    DateTimeTypeInputParameters() {
    }

    DateTimeTypeInputParameters(final LocalDate localDate, final LocalTime localTime, final LocalDateTime localDateTime,
        final ZonedDateTime zonedDateTime) {
        m_date = localDate;
        m_time = localTime;
        m_localDateTime = localDateTime;
        m_zonedDateTime = zonedDateTime;
    }

    @Modification.WidgetReference(LocalDateModificationReference.class)
    LocalDate m_date = LocalDate.now();

    @Modification.WidgetReference(LocalTimeModificationReference.class)
    LocalTime m_time = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);

    @Modification.WidgetReference(LocalDateTimeModificationReference.class)
    LocalDateTime m_localDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

    @Modification.WidgetReference(ZonedDateTimeModificationReference.class)
    ZonedDateTime m_zonedDateTime = ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS);

    abstract static class AbstractDateTimeTypeInputValueProvider implements StateProvider<DateTimeTypeInputParameters> {

        private Supplier<DateTimeTypeInputParameters> m_dateTimeTypeInput;

        private final Class<? extends ParameterReference<DateTimeType>> m_dateTimeTypeReference;

        private final Class<? extends ParameterReference<DateTimeTypeInputParameters>> m_dateTimeTypeInputReference;

        AbstractDateTimeTypeInputValueProvider(
            final Class<? extends ParameterReference<DateTimeType>> dateTimeTypeReference,
            final Class<? extends ParameterReference<DateTimeTypeInputParameters>> dateTimeTypeInputReference) {
            m_dateTimeTypeReference = dateTimeTypeReference;
            m_dateTimeTypeInputReference = dateTimeTypeInputReference;
        }

        @Override
        public void init(final StateProviderInitializer initializer) {
            initializer.computeFromValueSupplier(m_dateTimeTypeReference);
            m_dateTimeTypeInput = initializer.getValueSupplier(m_dateTimeTypeInputReference);
        }

        @Override
        public DateTimeTypeInputParameters computeState(final NodeParametersInput parametersInput)
            throws StateComputationFailureException {
            final var zonedDateTime = getZonedDateTimeFromDateTimes(m_dateTimeTypeInput.get());
            return new DateTimeTypeInputParameters(zonedDateTime.toLocalDate(), zonedDateTime.toLocalTime(),
                zonedDateTime.toLocalDateTime(), zonedDateTime);
        }
    }

    abstract static class DateTimeTypeInputParametersModification implements Modification.Modifier {
        static final class LocalDateModificationReference implements Modification.Reference {
        }

        static final class LocalTimeModificationReference implements Modification.Reference {
        }

        static final class LocalDateTimeModificationReference implements Modification.Reference {
        }

        static final class ZonedDateTimeModificationReference implements Modification.Reference {
        }

        private static final List<String> TYPE = List.of("date", "time", "local date&time", "zoned date&time");

        private static final List<Class<? extends Modification.Reference>> MODIFICATION_REFS =
            List.of(LocalDateModificationReference.class, //
                LocalTimeModificationReference.class, //
                LocalDateTimeModificationReference.class, //
                ZonedDateTimeModificationReference.class);

        @Override
        public void modify(final Modification.WidgetGroupModifier group) {
            for (var i = 0; i < MODIFICATION_REFS.size(); i++) {
                final var dateTimeType = TYPE.get(i);
                group.find(MODIFICATION_REFS.get(i)) //
                    .addAnnotation(Widget.class) //
                    .withProperty("title", String.format(getTitle(), dateTimeType)) //
                    .withProperty("description",
                        String.format(getDescription(),
                            StringEscapeUtils.escapeXml(dateTimeType.toLowerCase(Locale.ENGLISH)))) //
                    .modify();
                group.find(MODIFICATION_REFS.get(i)) //
                    .addAnnotation(Effect.class) //
                    .withProperty("type", EffectType.SHOW) //
                    .withProperty("predicate", getShowSpecificDateTimeEffectPredicateProviders().get(i)) //
                    .modify();
            }
        }

        /**
         * The string will be formatted such that the title contains the exact date time type. Therefore, it should
         * contain a '%s'.
         *
         * @return the title of a date&time input field
         */
        protected abstract String getTitle();

        /**
         * The string will be formatted such that the description contains the exact date time type. Therefore, it
         * should contain a '%s'.
         *
         * @return the description of a date&time input field
         */
        protected abstract String getDescription();

        /**
         * Provide a list of effect predicate providers that determine which input field to show. The list should be in
         * order: LocalDate, LocalTime, LocalDateTime, and ZonedDateTime
         *
         * @return a list of EffectPredicateProviders which are responsible for showing the correct date&time input
         */
        protected abstract List<Class<? extends EffectPredicateProvider>>
            getShowSpecificDateTimeEffectPredicateProviders();
    }

    abstract static class DateTimeTypeInputPersistor implements NodeParametersPersistor<DateTimeTypeInputParameters> {

        private final String m_configKey;

        DateTimeTypeInputPersistor(final String configKey) {
            m_configKey = configKey;
        }

        @Override
        public DateTimeTypeInputParameters load(final NodeSettingsRO settings) throws InvalidSettingsException {
            String dateTimeString = "";
            try {
                dateTimeString = settings.getString(m_configKey);
                final var dateTime = ZonedDateTime.parse(dateTimeString);
                return new DateTimeTypeInputParameters(dateTime.toLocalDate(), dateTime.toLocalTime(),
                    dateTime.toLocalDateTime(), dateTime);
            } catch (InvalidSettingsException e) {
                throw new InvalidSettingsException(
                    String.format("Date&time for key '%s' could not be loaded.", m_configKey), e);
            } catch (DateTimeParseException e) {
                throw new InvalidSettingsException(
                    String.format("Date&time string '%s' could not be parsed to a ZonedDateTime.", dateTimeString), e);
            }
        }

        @Override
        public void save(final DateTimeTypeInputParameters param, final NodeSettingsWO settings) {
            final var zonedDateTimeToSave = getZonedDateTimeFromDateTimes(param);
            settings.addString(m_configKey, zonedDateTimeToSave.toString());
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][]{{m_configKey}};
        }
    }

    /**
     * This method is either called on save {@link DateTimeTypeInputPersistor#save}, or when the user switches the
     * date&time type {@link AbstractDateTimeTypeInputValueProvider#computeState}.<br>
     * When we load the DateTimeTypeInput, we got a single ZonedDateTime which is split to the four different types.
     * Therefore, we know, that all types have the same date/time component. When the user changes the type or applies
     * the settings, we know that only one of the types could have changed, i.e. we can use the date/time component
     * which only appears once ({@link #minority}.
     *
     * @param dateTimeTypeInputParameters the current date time type input values to compute the zoned date time from
     * @return a zoned date time of the given input dates & times
     */
    private static ZonedDateTime
        getZonedDateTimeFromDateTimes(final DateTimeTypeInputParameters dateTimeTypeInputParameters) {
        final var localDate = dateTimeTypeInputParameters.m_date;
        final var localTime = dateTimeTypeInputParameters.m_time;
        final var localDateTime = dateTimeTypeInputParameters.m_localDateTime;
        final var zonedDateTime = dateTimeTypeInputParameters.m_zonedDateTime;
        final var localDateTimeDate = localDateTime.toLocalDate();
        final var localDateTimeTime = localDateTime.toLocalTime();
        final var zonedDateTimeDate = zonedDateTime.toLocalDate();
        final var zonedDateTimeTime = zonedDateTime.toLocalTime();

        LocalDate minorityDate = minority(localDate, localDateTimeDate, zonedDateTimeDate);
        LocalTime minorityTime = minority(localTime, localDateTimeTime, zonedDateTimeTime);

        LocalDateTime minorityLocalDateTime = LocalDateTime.of(minorityDate, minorityTime);
        return ZonedDateTime.of(minorityLocalDateTime, zonedDateTime.getZone());
    }

    private static <T> T minority(final T a, final T b, final T c) {
        if (a.equals(b) && !a.equals(c)) {
            return c;
        }
        if (!a.equals(b) && a.equals(c)) {
            return b;
        }
        // either a is different than b and c, but b and c are the same, or a, b, and c are the same
        return a;
    }

}
