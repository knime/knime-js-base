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
 *   AI Migration
 */
package org.knime.js.base.node.widget.input.slider;

import org.knime.js.base.node.widget.util.CommonWidgetPersistors;
import org.knime.js.base.node.widget.util.WidgetNodeSettingsBase;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.After;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.layout.Section;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.persistence.Persistor;
import org.knime.node.parameters.updates.Effect;
import org.knime.node.parameters.updates.Effect.EffectType;
import org.knime.node.parameters.updates.EffectPredicate;
import org.knime.node.parameters.updates.ValueReference;
import org.knime.node.parameters.updates.util.BooleanReference;
import org.knime.node.parameters.widget.choices.ChoicesProvider;
import org.knime.node.parameters.widget.choices.util.AllColumnsProvider;
import org.knime.node.parameters.widget.choices.util.CompatibleColumnsProvider.DoubleColumnsProvider;
import org.knime.node.parameters.widget.choices.Label;
import org.knime.node.parameters.widget.choices.RadioButtonsWidget;
import org.knime.node.parameters.widget.choices.StringChoicesProvider;
import org.knime.node.parameters.widget.choices.ValueSwitchWidget;
import org.knime.node.parameters.widget.number.NumberInputWidget;
import org.knime.node.parameters.widget.number.NumberInputWidgetValidation.MinValidation.IsNonNegativeValidation;
import org.knime.node.parameters.widget.text.TextAreaWidget;
import org.knime.node.parameters.widget.text.TextInputWidget;

/**
 * Settings for the Slider Widget node.
 *
 * @author AI Migration
 */
@SuppressWarnings("restriction")
public final class SliderWidgetNodeSettings implements NodeParameters {

    @Section(title = "Slider Configuration")
    @After(WidgetNodeSettingsBase.CommonSections.ValidationSection.class)
    interface SliderSection {
    }

    @Section(title = "Appearance")
    @After(SliderSection.class)
    interface AppearanceSection {
    }

    @Widget(title = "Label", description = "A descriptive label that will be shown in the dialog")
    @Layout(WidgetNodeSettingsBase.CommonSections.LabelSection.class)
    @TextInputWidget
    @Persist(configKey = "label")
    String m_label = "Label";

    @Widget(title = "Description", description = "Some lines of description that will be shown for instance in the node description of the component exposing a dialog")
    @Layout(WidgetNodeSettingsBase.CommonSections.LabelSection.class)
    @TextAreaWidget
    @Persist(configKey = "description")
    String m_description = "Enter Description";

    @Widget(title = "Parameter Name", description = "Parameter identifier for external parameterization (e.g. batch execution). This will also be the name of the exported flow variable.")
    @Layout(WidgetNodeSettingsBase.CommonSections.ParameterSection.class)
    @TextInputWidget
    @Persist(configKey = "flowVariableName")
    String m_parameter = "slider-input";

    @Widget(title = "Required", description = "If checked, the widget must be configured before the workflow can be executed")
    @Layout(WidgetNodeSettingsBase.CommonSections.ParameterSection.class)
    @Persist(configKey = "required")
    boolean m_required = true;

    @Widget(title = "Default Value", description = "The value that is selected by default")
    @Layout(WidgetNodeSettingsBase.CommonSections.ParameterSection.class)
    @NumberInputWidget
    @Persistor(CommonWidgetPersistors.DoubleDefaultValuePersistor.class)
    double m_defaultValue = 50.0;

    @Widget(title = "Hide in Dialog", description = "If the widget is hidden, it cannot be shown in the dialog, and becomes unreachable.")
    @Layout(WidgetNodeSettingsBase.CommonSections.ParameterSection.class)
    @Persist(configKey = "hideInWizard")
    boolean m_hideInWizard = false;

    @Widget(title = "Custom CSS", description = "Enter custom CSS styling for this widget")
    @Layout(WidgetNodeSettingsBase.CommonSections.InputSection.class)
    @TextAreaWidget
    @Persist(configKey = "customCSS")
    String m_customCSS = "";

    // Domain and range settings
    @Widget(title = "Domain Column", description = "Select a column to use its domain for the slider range. If no column is selected, custom min/max values are used.")
    @Layout(WidgetNodeSettingsBase.CommonSections.ValidationSection.class)
    @ChoicesProvider(DoubleColumnsProvider.class)
    @Persist(configKey = "domainColumn")
    String m_domainColumn = "";

    interface UseMinRef extends BooleanReference {
    }

    interface UseMaxRef extends BooleanReference {
    }

    @Widget(title = "Use custom minimum", description = "Check to enforce a custom minimum value")
    @Layout(WidgetNodeSettingsBase.CommonSections.ValidationSection.class)
    @Persist(configKey = "useCustomMin")
    @ValueReference(UseMinRef.class)
    boolean m_useCustomMin = false;

    @Widget(title = "Custom minimum", description = "The custom minimum value for the slider")
    @Layout(WidgetNodeSettingsBase.CommonSections.ValidationSection.class)
    @NumberInputWidget
    @Persist(configKey = "customMinValue")
    double m_customMin = 0.0;

    @Widget(title = "Use custom maximum", description = "Check to enforce a custom maximum value")
    @Layout(WidgetNodeSettingsBase.CommonSections.ValidationSection.class)
    @Persist(configKey = "useCustomMax")
    @ValueReference(UseMaxRef.class)
    boolean m_useCustomMax = false;

    @Widget(title = "Custom maximum", description = "The custom maximum value for the slider")
    @Layout(WidgetNodeSettingsBase.CommonSections.ValidationSection.class)
    @NumberInputWidget
    @Persist(configKey = "customMaxValue")
    double m_customMax = 100.0;

    // Slider-specific settings
    interface UseStepRef extends BooleanReference {
    }

    @Widget(title = "Use step size", description = "Check to define a step size for the slider")
    @Layout(SliderSection.class)
    @Persist(configKey = "useStep")
    @ValueReference(UseStepRef.class)
    boolean m_useStep = false;

    @Widget(title = "Step size", description = "The step size for slider increments")
    @Layout(SliderSection.class)
    @NumberInputWidget(minValidation = IsNonNegativeValidation.class)
    @Persist(configKey = "step")
    double m_step = 1.0;

    @Widget(title = "Connect lower handle", description = "Connect the lower part of the slider")
    @Layout(SliderSection.class)
    @Persist(configKey = "connect_lower")
    boolean m_connectLower = false;

    @Widget(title = "Connect upper handle", description = "Connect the upper part of the slider")
    @Layout(SliderSection.class)
    @Persist(configKey = "connect_upper")
    boolean m_connectUpper = false;

    enum Orientation {
        @Label("Horizontal")
        HORIZONTAL("horizontal"),
        @Label("Vertical")
        VERTICAL("vertical");

        private final String m_text;

        Orientation(final String text) {
            m_text = text;
        }

        String getText() {
            return m_text;
        }
    }

    @Widget(title = "Orientation", description = "The orientation of the slider")
    @Layout(AppearanceSection.class)
    @RadioButtonsWidget(horizontal = true)
    @Persist(configKey = "orientation")
    Orientation m_orientation = Orientation.HORIZONTAL;

    enum Direction {
        @Label("Left to Right")
        LTR("ltr"),
        @Label("Right to Left")
        RTL("rtl");

        private final String m_text;

        Direction(final String text) {
            m_text = text;
        }

        String getText() {
            return m_text;
        }
    }

    @Widget(title = "Direction", description = "The direction of value increase")
    @Layout(AppearanceSection.class)
    @RadioButtonsWidget(horizontal = true)
    @Persist(configKey = "direction")
    Direction m_direction = Direction.LTR;

    @Widget(title = "Show tooltips", description = "Show tooltips with values when interacting with the slider")
    @Layout(AppearanceSection.class)
    @Persist(configKey = "tooltips")
    boolean m_tooltips = true;

    /**
     * Predicate for showing custom minimum value field when "Use custom minimum" is checked.
     */

    /**
     * Predicate for showing custom maximum value field when "Use custom maximum" is checked.
     */

    /**
     * Predicate for showing step size field when "Use step size" is checked.
     */
}
