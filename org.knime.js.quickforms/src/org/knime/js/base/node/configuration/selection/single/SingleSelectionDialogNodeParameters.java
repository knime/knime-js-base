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
 *   9 Sept 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.configuration.selection.single;

import static org.knime.js.base.node.configuration.selection.SelectionNodeParametersUtil.LIMIT_VIS_OPT_DESCRIPTION;
import static org.knime.js.base.node.configuration.selection.SelectionNodeParametersUtil.LIMIT_VIS_OPT_TITLE;
import static org.knime.js.base.node.configuration.selection.SelectionNodeParametersUtil.NUM_VIS_OPT_DESCRIPTION;
import static org.knime.js.base.node.configuration.selection.SelectionNodeParametersUtil.NUM_VIS_OPT_TITLE;
import static org.knime.js.base.node.configuration.selection.SelectionNodeParametersUtil.SELECTION_TYPE_DESCRIPTION;
import static org.knime.js.base.node.configuration.selection.SelectionNodeParametersUtil.SELECTION_TYPE_TITLE;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.webui.node.dialog.defaultdialog.util.updates.StateComputationFailureException;
import org.knime.js.base.node.base.selection.singleMultiple.SingleMultipleSelectionNodeConfig;
import org.knime.js.base.node.base.selection.singleMultiple.SingleMultipleSelectionNodeValue;
import org.knime.js.base.node.base.selection.singleMultiple.SingleSelectionNodeConfig;
import org.knime.js.base.node.configuration.ConfigurationNodeParametersUtility.IsMin2Validation;
import org.knime.js.base.node.configuration.ConfigurationNodeSettings;
import org.knime.js.base.node.configuration.OverwrittenByValueMessage;
import org.knime.js.base.node.configuration.selection.SelectionNodeParametersUtil.IsListSelectionType;
import org.knime.js.base.node.configuration.selection.SelectionNodeParametersUtil.LimitNumberOfVisibleOptionsValueReference;
import org.knime.js.base.node.configuration.selection.SelectionNodeParametersUtil.SelectionTypeChoicesProvider;
import org.knime.js.base.node.configuration.selection.SelectionNodeParametersUtil.SelectionTypeValueReference;
import org.knime.js.base.node.configuration.selection.SelectionNodeParametersUtil.ShowNumberOfVisibleOptions;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.NodeParametersInput;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.persistence.NodeParametersPersistor;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.persistence.Persistor;
import org.knime.node.parameters.updates.Effect;
import org.knime.node.parameters.updates.Effect.EffectType;
import org.knime.node.parameters.updates.ParameterReference;
import org.knime.node.parameters.updates.StateProvider;
import org.knime.node.parameters.updates.ValueProvider;
import org.knime.node.parameters.updates.ValueReference;
import org.knime.node.parameters.widget.choices.ChoicesProvider;
import org.knime.node.parameters.widget.choices.StringChoicesProvider;
import org.knime.node.parameters.widget.message.TextMessage;
import org.knime.node.parameters.widget.number.NumberInputWidget;
import org.knime.node.parameters.widget.text.TextAreaWidget;

/**
 * WebUI Node Settings for the Single Selection Configuration.
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz
 */
public class SingleSelectionDialogNodeParameters extends ConfigurationNodeSettings {

    /**
     * Default constructor
     */
    protected SingleSelectionDialogNodeParameters() {
        super(SingleSelectionDialogNodeConfig.class);
    }

    @TextMessage(SingleSelectionOverwrittenByValueMessage.class)
    @Layout(OutputSection.Top.class)
    Void m_overwrittenByValueMessage;

    private static final class DefaultValue implements NodeParameters {
        @Widget(title = "Default value",
            description = "Default value for the field. If you want to use a value that is not among the possible"
                + " choices as the default, override the field using a flow variable.")
        @Layout(OutputSection.Top.class)
        @Persistor(DefaultVariableValuePersistor.class)
        @ChoicesProvider(VariableValueChoicesProvider.class)
        @ValueProvider(VariableValueValueProvider.class)
        @ValueReference(VariableValueValueReference.class)
        String m_variableValue;
    }

    DefaultValue m_defaultValue = new DefaultValue();

    @Widget(title = SELECTION_TYPE_TITLE, description = SELECTION_TYPE_DESCRIPTION)
    @ChoicesProvider(SelectionTypeChoicesProvider.class)
    @Persist(configKey = SingleMultipleSelectionNodeConfig.CFG_TYPE)
    @ValueReference(SelectionTypeValueReference.class)
    @Layout(FormFieldSection.class)
    String m_selectionType = SingleSelectionNodeConfig.DEFAULT_TYPE;

    @Widget(title = LIMIT_VIS_OPT_TITLE, description = LIMIT_VIS_OPT_DESCRIPTION)
    @Persist(configKey = SingleMultipleSelectionNodeConfig.CFG_LIMIT_NUMBER_VIS_OPTIONS)
    @ValueReference(LimitNumberOfVisibleOptionsValueReference.class)
    @Effect(predicate = IsListSelectionType.class, type = EffectType.SHOW)
    @Layout(FormFieldSection.class)
    boolean m_limitNumberOfVisibleOptions = SingleMultipleSelectionNodeConfig.DEFAULT_LIMIT_NUMBER_VIS_OPTIONS;

    @Widget(title = NUM_VIS_OPT_TITLE, description = NUM_VIS_OPT_DESCRIPTION)
    @NumberInputWidget(minValidation = IsMin2Validation.class)
    @Persist(configKey = SingleMultipleSelectionNodeConfig.CFG_NUMBER_VIS_OPTIONS)
    @Effect(predicate = ShowNumberOfVisibleOptions.class, type = EffectType.SHOW)
    @Layout(FormFieldSection.class)
    int m_numberOfVisibleOptions = SingleMultipleSelectionNodeConfig.DEFAULT_NUMBER_VIS_OPTIONS;

    @Widget(title = "Possible choices", description = "The possible choices, each line is one possible value.")
    @TextAreaWidget
    @Persistor(PossibleChoicesPersistor.class)
    @ValueReference(PossibleChoicesReference.class)
    @Layout(FormFieldSection.class)
    String m_possibleChoices;

    private static final class DefaultVariableValuePersistor implements NodeParametersPersistor<String> {

        @Override
        public String load(final NodeSettingsRO settings) throws InvalidSettingsException {
            final var variableValue = settings.getStringArray(SingleMultipleSelectionNodeValue.CFG_VARIABLE_VALUE);
            return variableValue.length == 0 ? null : variableValue[0];
        }

        @Override
        public void save(final String param, final NodeSettingsWO settings) {
            final var valueToSave = param == null ? new String[0] : new String[]{param};
            settings.addStringArray(SingleMultipleSelectionNodeValue.CFG_VARIABLE_VALUE, valueToSave);
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][]{{SingleMultipleSelectionNodeValue.CFG_VARIABLE_VALUE}};
        }

    }

    private static final class PossibleChoicesPersistor implements NodeParametersPersistor<String> {

        @Override
        public String load(final NodeSettingsRO settings) throws InvalidSettingsException {
            final var possibleChoices = settings.getStringArray(SingleMultipleSelectionNodeConfig.CFG_POSSIBLE_CHOICES);
            return possibleChoices.length == 0 ? null : String.join("\n", possibleChoices);
        }

        @Override
        public void save(final String param, final NodeSettingsWO settings) {
            final var valueToSave = param == null || param.isEmpty() ? new String[0] : param.split("\n");
            settings.addStringArray(SingleMultipleSelectionNodeConfig.CFG_POSSIBLE_CHOICES, valueToSave);
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][]{{SingleMultipleSelectionNodeConfig.CFG_POSSIBLE_CHOICES}};
        }

    }

    private static final class VariableValueValueReference implements ParameterReference<String> {
    }

    private static final class PossibleChoicesReference implements ParameterReference<String> {
    }

    private static final class VariableValueChoicesProvider implements StringChoicesProvider {

        private Supplier<String> m_possibleChoicesSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            StringChoicesProvider.super.init(initializer);
            m_possibleChoicesSupplier = initializer.computeFromValueSupplier(PossibleChoicesReference.class);
        }

        @Override
        public List<String> choices(final NodeParametersInput context) {
            final var possibleChoices = m_possibleChoicesSupplier.get();
            return possibleChoices == null || possibleChoices.isEmpty() ? List.of()
                : Arrays.asList(possibleChoices.split("\n"));
        }
    }

    private static final class VariableValueValueProvider implements StateProvider<String> {

        private Supplier<String> m_possibleChoicesSupplier;

        private Supplier<String> m_variableValueSupplier;

        @Override
        public void init(final StateProviderInitializer initializer) {
            initializer.computeAfterOpenDialog();
            m_possibleChoicesSupplier = initializer.computeFromValueSupplier(PossibleChoicesReference.class);
            m_variableValueSupplier = initializer.getValueSupplier(VariableValueValueReference.class);
        }

        @Override
        public String computeState(final NodeParametersInput parametersInput) throws StateComputationFailureException {
            final var variableValue = m_variableValueSupplier.get();
            final var possibleChoicesString = m_possibleChoicesSupplier.get();
            if (possibleChoicesString != null && !possibleChoicesString.isEmpty()) {
                final var possibleChoices = possibleChoicesString.split("\n");
                if (variableValue != null
                    && Arrays.stream(possibleChoices).anyMatch(val -> val.equals(variableValue))) {
                    return variableValue;
                }
                return possibleChoices[0];
            }
            return null;
        }

    }

    private static final class SingleSelectionOverwrittenByValueMessage
        extends OverwrittenByValueMessage<SingleSelectionDialogNodeValue> {

        @Override
        protected String valueToString(final SingleSelectionDialogNodeValue value) {
            return value.getVariableValue()[0];
        }

    }

}
