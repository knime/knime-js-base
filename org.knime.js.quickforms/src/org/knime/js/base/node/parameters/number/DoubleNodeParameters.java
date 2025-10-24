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
 *   24 Oct 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.parameters.number;

import java.util.Optional;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.webui.node.dialog.defaultdialog.util.updates.StateComputationFailureException;
import org.knime.js.base.node.base.input.dbl.DoubleNodeConfig;
import org.knime.js.base.node.base.input.dbl.DoubleNodeValue;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.FormFieldSection;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.OutputSection;
import org.knime.js.base.node.parameters.OverwrittenByValueMessage;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.NodeParametersInput;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.After;
import org.knime.node.parameters.layout.Before;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.layout.Section;
import org.knime.node.parameters.persistence.NodeParametersPersistor;
import org.knime.node.parameters.persistence.Persistor;
import org.knime.node.parameters.widget.OptionalWidget;
import org.knime.node.parameters.widget.OptionalWidget.DefaultValueProvider;
import org.knime.node.parameters.widget.message.TextMessage;

/**
 * The node parameters for configuration and widget nodes which use the {@link DoubleNodeValue}.
 *
 * @author Robin Gerling
 */
public final class DoubleNodeParameters implements NodeParameters {

    @Section(title = "Validation")
    @After(FormFieldSection.class)
    @Before(OutputSection.class)
    interface ValidationSection {
    }

    @TextMessage(DoubleOverwrittenByValueMessage.class)
    @Layout(OutputSection.Top.class)
    Void m_overwrittenByValueMessage;

    static final class DoubleOverwrittenByValueMessage extends OverwrittenByValueMessage<DoubleNodeValue> {

        @Override
        protected String valueToString(final DoubleNodeValue value) {
            return String.valueOf(value.getDouble());
        }

    }

    static final class DefaultValue implements NodeParameters {
        @Widget(title = "Default value", description = "The value that is used by default in the input field.")
        @Layout(OutputSection.Top.class)
        double m_double;
    }

    DefaultValue m_defaultValue = new DefaultValue();

    @Widget(title = "Minimum value", description = "An optional minimum value.")
    @Layout(ValidationSection.class)
    @Persistor(MinValuePersistor.class)
    Optional<Double> m_minimumValue = Optional.empty();

    @Widget(title = "Maximum value", description = "An optional maximum value.")
    @Layout(ValidationSection.class)
    @Persistor(MaxValuePersistor.class)
    @OptionalWidget(defaultProvider = MaxValueDefaultProvider.class)
    Optional<Double> m_maximumValue = Optional.empty();

    static final class MaxValueDefaultProvider implements DefaultValueProvider<Double> {

        @Override
        public void init(final StateProviderInitializer initializer) {
            initializer.computeBeforeOpenDialog();
        }

        @Override
        public Double computeState(final NodeParametersInput context) throws StateComputationFailureException {
            return DoubleNodeConfig.DEFAULT_MAX;
        }

    }

    static final class MinValuePersistor extends ValidationValuePersistor {

        MinValuePersistor() {
            super(DoubleNodeConfig.CFG_USE_MIN, DoubleNodeConfig.CFG_MIN);
        }

    }

    static final class MaxValuePersistor extends ValidationValuePersistor {

        MaxValuePersistor() {
            super(DoubleNodeConfig.CFG_USE_MAX, DoubleNodeConfig.CFG_MAX);
        }

    }

    abstract static class ValidationValuePersistor implements NodeParametersPersistor<Optional<Double>> {

        private final String m_useKey;

        private final String m_valueKey;

        ValidationValuePersistor(final String useKey, final String valueKey) {
            this.m_useKey = useKey;
            this.m_valueKey = valueKey;
        }

        @Override
        public Optional<Double> load(final NodeSettingsRO settings) throws InvalidSettingsException {
            final var use = settings.getBoolean(m_useKey);
            if (!use) {
                return Optional.empty();
            }
            final var value = settings.getDouble(m_valueKey);
            return Optional.of(value);
        }

        @Override
        public void save(final Optional<Double> obj, final NodeSettingsWO settings) {
            settings.addBoolean(m_useKey, obj.isPresent());
            settings.addDouble(m_valueKey, obj.orElse(0.0));
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][]{{m_useKey}, {m_valueKey}};
        }

    }

}
