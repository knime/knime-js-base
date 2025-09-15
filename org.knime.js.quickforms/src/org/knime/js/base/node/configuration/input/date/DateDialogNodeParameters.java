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
 *   11 Sept 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.configuration.input.date;

import java.util.function.Supplier;

import org.knime.core.webui.node.dialog.defaultdialog.widget.Modification;
import org.knime.js.base.node.base.input.date.DateNodeConfig;
import org.knime.js.base.node.base.input.date.GranularityTime;
import org.knime.js.base.node.configuration.ConfigurationNodeSettings;
import org.knime.js.base.node.configuration.OverwrittenByValueMessage;
import org.knime.js.base.node.configuration.input.date.DateDialogNodeParametersDefaultValueUtil.DefaultDateTimeTypeInputValueProvider;
import org.knime.js.base.node.configuration.input.date.DateDialogNodeParametersDefaultValueUtil.DefaultModification;
import org.knime.js.base.node.configuration.input.date.DateDialogNodeParametersDefaultValueUtil.DefaultPersistorDateTimeTypeInput;
import org.knime.js.base.node.configuration.input.date.DateDialogNodeParametersDefaultValueUtil.DefaultPersistorTimeSelection;
import org.knime.js.base.node.configuration.input.date.DateDialogNodeParametersDefaultValueUtil.DefaultReferenceDateTimeTypeInput;
import org.knime.js.base.node.configuration.input.date.DateDialogNodeParametersDefaultValueUtil.DefaultReferenceTimeSelection;
import org.knime.js.base.node.configuration.input.date.DateDialogNodeParametersDefaultValueUtil.TimeSelectionDefaultValue;
import org.knime.js.base.node.configuration.input.date.DateDialogNodeParametersValidationUtil.MaxDateTimeTypeInputValueProvider;
import org.knime.js.base.node.configuration.input.date.DateDialogNodeParametersValidationUtil.MaxModification;
import org.knime.js.base.node.configuration.input.date.DateDialogNodeParametersValidationUtil.MaxPersistorDateTimeTypeInput;
import org.knime.js.base.node.configuration.input.date.DateDialogNodeParametersValidationUtil.MaxPersistorTimeSelection;
import org.knime.js.base.node.configuration.input.date.DateDialogNodeParametersValidationUtil.MaxReferenceDateTimeTypeInput;
import org.knime.js.base.node.configuration.input.date.DateDialogNodeParametersValidationUtil.MaxReferenceTimeSelection;
import org.knime.js.base.node.configuration.input.date.DateDialogNodeParametersValidationUtil.MinDateTimeTypeInputValueProvider;
import org.knime.js.base.node.configuration.input.date.DateDialogNodeParametersValidationUtil.MinModification;
import org.knime.js.base.node.configuration.input.date.DateDialogNodeParametersValidationUtil.MinPersistorDateTimeTypeInput;
import org.knime.js.base.node.configuration.input.date.DateDialogNodeParametersValidationUtil.MinPersistorTimeSelection;
import org.knime.js.base.node.configuration.input.date.DateDialogNodeParametersValidationUtil.MinReferenceDateTimeTypeInput;
import org.knime.js.base.node.configuration.input.date.DateDialogNodeParametersValidationUtil.MinReferenceTimeSelection;
import org.knime.js.base.node.configuration.input.date.DateDialogNodeParametersValidationUtil.TimeSelectionMinMax;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.After;
import org.knime.node.parameters.layout.Before;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.layout.Section;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.persistence.Persistor;
import org.knime.node.parameters.updates.Effect;
import org.knime.node.parameters.updates.Effect.EffectType;
import org.knime.node.parameters.updates.ValueProvider;
import org.knime.node.parameters.updates.ValueReference;
import org.knime.node.parameters.widget.choices.ValueSwitchWidget;
import org.knime.node.parameters.widget.message.TextMessage;
import org.knime.time.util.DateTimeType;
import org.knime.time.util.DateTimeType.IsLocalDate;

/**
 * WebUI Node Parameters for the Date&Time Configuration.
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz
 */
@SuppressWarnings("restriction")
public class DateDialogNodeParameters extends ConfigurationNodeSettings {

    static final String LABEL_VALUE_CUSTOM = "Custom";

    static final String LABEL_DESCRIPTION_CUSTOM = "Use a custom date&amp;time.";

    static final String LABEL_VALUE_EXECUTION_TIME = "Execution time";

    static final String LABEL_DESCRIPTION_EXECUTION_TIME = "Use the execution time.";

    /**
     * Default constructor
     */
    protected DateDialogNodeParameters() {
        super(DateInputDialogNodeConfig.class);
    }

    @TextMessage(DateOverwrittenByValueMessage.class)
    @Layout(OutputSection.Top.class)
    Void m_overwrittenByValueMessage;

    static final class DefaultValue implements NodeParameters {
        @Modification(DefaultModification.class)
        @Layout(OutputSection.Top.class)
        @Persistor(DefaultPersistorDateTimeTypeInput.class)
        @ValueReference(DefaultReferenceDateTimeTypeInput.class)
        @ValueProvider(DefaultDateTimeTypeInputValueProvider.class)
        DateTimeTypeInputParameters m_default = new DateTimeTypeInputParameters();
    }

    @Widget(title = "Default value", description = "The default values used as date&amp;time.")
    @ValueReference(DefaultReferenceTimeSelection.class)
    @Layout(OutputSection.Top.class)
    @Persistor(DefaultPersistorTimeSelection.class)
    @ValueSwitchWidget
    TimeSelectionDefaultValue m_timeSelectionDefault = TimeSelectionDefaultValue.CUSTOM;

    DefaultValue m_defaultValue = new DefaultValue();

    @Widget(title = "Type", description = "Which date&amp;time type should be selectable.")
    @Layout(FormFieldSection.class)
    @Persist(configKey = DateNodeConfig.CFG_TYPE)
    @ValueReference(DateTimeType.Ref.class)
    @ValueSwitchWidget
    /**
     * Default is Date & Time which is not the first enum value since we do not want to change the model until the
     * Date&Time Widget is migrated to WebUI
     */
    DateTimeType m_type = DateNodeConfig.DEFAULT_TYPE;

    @Widget(title = "Granularity", description = "In which granularity the time should be displayed in the wizard.")
    @Layout(FormFieldSection.class)
    @Persist(configKey = DateNodeConfig.CFG_GRANULARITY)
    @Effect(type = EffectType.HIDE, predicate = IsLocalDate.class)
    GranularityTime m_granularity = DateNodeConfig.DEFAULT_GRANULARITY;

    @Widget(title = "Earliest value", description = "The earliest allowed value used as date&amp;time.")
    @ValueReference(MinReferenceTimeSelection.class)
    @Layout(ValidationSection.class)
    @Persistor(MinPersistorTimeSelection.class)
    @ValueSwitchWidget
    TimeSelectionMinMax m_timeSelectionMin = TimeSelectionMinMax.NONE;

    @Modification(MinModification.class)
    @Layout(ValidationSection.class)
    @Persistor(MinPersistorDateTimeTypeInput.class)
    @ValueProvider(MinDateTimeTypeInputValueProvider.class)
    @ValueReference(MinReferenceDateTimeTypeInput.class)
    DateTimeTypeInputParameters m_min = new DateTimeTypeInputParameters();

    @Widget(title = "Latest value", description = "The latest allowed value used as date&amp;time.")
    @ValueReference(MaxReferenceTimeSelection.class)
    @Layout(ValidationSection.class)
    @Persistor(MaxPersistorTimeSelection.class)
    @ValueSwitchWidget
    TimeSelectionMinMax m_timeSelectionMax = TimeSelectionMinMax.NONE;

    @Modification(MaxModification.class)
    @Layout(ValidationSection.class)
    @Persistor(MaxPersistorDateTimeTypeInput.class)
    @ValueProvider(MaxDateTimeTypeInputValueProvider.class)
    @ValueReference(MaxReferenceDateTimeTypeInput.class)
    DateTimeTypeInputParameters m_max = new DateTimeTypeInputParameters();

    static final class DateOverwrittenByValueMessage extends OverwrittenByValueMessage<DateDialogNodeValue> {

        private Supplier<DateTimeType> m_dateTimeTypeSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            super.init(initializer);
            m_dateTimeTypeSupplier = initializer.computeFromValueSupplier(DateTimeType.Ref.class);
        }

        @Override
        protected String valueToString(final DateDialogNodeValue value) {
            final var currDate = value.getDate();
            return switch (m_dateTimeTypeSupplier.get()) {
                case LOCAL_DATE -> currDate.toLocalDate().toString();
                case LOCAL_TIME -> currDate.toLocalTime().toString();
                case LOCAL_DATE_TIME -> currDate.toLocalDateTime().toString();
                case ZONED_DATE_TIME -> currDate.toString();
            };
        }

    }

    @Section(title = "Validation")
    @After(FormFieldSection.class)
    @Before(OutputSection.class)
    interface ValidationSection {
    }

}
