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
 *   Jun 10, 2025 (Martin Sillye, TNG Technology Consulting GmbH): created
 */
package org.knime.js.base.node.configuration.input.integer;

import java.util.Optional;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.webui.node.dialog.defaultdialog.DefaultNodeSettings;
import org.knime.core.webui.node.dialog.defaultdialog.layout.After;
import org.knime.core.webui.node.dialog.defaultdialog.layout.Before;
import org.knime.core.webui.node.dialog.defaultdialog.layout.Layout;
import org.knime.core.webui.node.dialog.defaultdialog.layout.Section;
import org.knime.core.webui.node.dialog.defaultdialog.persistence.api.NodeSettingsPersistor;
import org.knime.core.webui.node.dialog.defaultdialog.persistence.api.Persistor;
import org.knime.core.webui.node.dialog.defaultdialog.util.updates.StateComputationFailureException;
import org.knime.core.webui.node.dialog.defaultdialog.widget.DefaultValueProvider;
import org.knime.core.webui.node.dialog.defaultdialog.widget.OptionalWidget;
import org.knime.core.webui.node.dialog.defaultdialog.widget.Widget;
import org.knime.js.base.node.base.input.integer.IntegerNodeConfig;
import org.knime.js.base.node.configuration.ConfigurationNodeSettings;

/**
 * WebUI Node Settings for the Integer Configuration.
 *
 * @author Martin Sillye, TNG Technology Consulting GmbH
 */
@SuppressWarnings("restriction")
public class IntegerDialogNodeSettings extends ConfigurationNodeSettings {

    /**
     * Default constructor
     */
    public IntegerDialogNodeSettings() {
        super(IntegerInputDialogNodeConfig.class);
    }

    @Section(title = "Validation")
    @After(FormFieldSection.class)
    @Before(OutputSection.class)
    interface ValidationSection {
    }

    // the default value whose type is specific to the node

    static final class DefaultValue implements DefaultNodeSettings {
        @Widget(title = "Default value",
            description = "Default value for the field. If empty, no default value will be set.")
        @Layout(OutputSection.class)
        int m_integer;
    }

    DefaultValue m_defaultValue = new DefaultValue();

    // settings specific to the IntegerDialogNode

    @Widget(title = "Minimum value", description = "An optional minimum value.")
    @Layout(ValidationSection.class)
    @Persistor(MinValuePersistor.class)
    Optional<Integer> m_minimumValue = Optional.empty();

    @Widget(title = "Maximum value", description = "An optional maximum value.")
    @Layout(ValidationSection.class)
    @Persistor(MaxValuePersistor.class)
    @OptionalWidget(defaultProvider = MaxValueDefaultProvider.class)
    Optional<Integer> m_maximumValue = Optional.empty();

    static final class MaxValueDefaultProvider implements DefaultValueProvider<Integer> {

        @Override
        public void init(final StateProviderInitializer initializer) {
            initializer.computeBeforeOpenDialog();
        }

        @Override
        public Integer computeState(final DefaultNodeSettingsContext context) throws StateComputationFailureException {
            return IntegerNodeConfig.DEFAULT_MAX;
        }

    }

    static final class MinValuePersistor extends ValidationValuePersistor {

        MinValuePersistor() {
            super(IntegerNodeConfig.CFG_USE_MIN, IntegerNodeConfig.CFG_MIN);
        }

    }

    static final class MaxValuePersistor extends ValidationValuePersistor {

        MaxValuePersistor() {
            super(IntegerNodeConfig.CFG_USE_MAX, IntegerNodeConfig.CFG_MAX);
        }

    }

    abstract static class ValidationValuePersistor implements NodeSettingsPersistor<Optional<Integer>> {

        private final String m_useKey;

        private final String m_valueKey;

        ValidationValuePersistor(final String useKey, final String valueKey) {
            this.m_useKey = useKey;
            this.m_valueKey = valueKey;
        }

        @Override
        public Optional<Integer> load(final NodeSettingsRO settings) throws InvalidSettingsException {
            final var use = settings.getBoolean(m_useKey);
            if (!use) {
                return Optional.empty();
            }
            final var value = settings.getInt(m_valueKey);
            return Optional.of(value);
        }

        @Override
        public void save(final Optional<Integer> obj, final NodeSettingsWO settings) {
            settings.addBoolean(m_useKey, obj.isPresent());
            settings.addInt(m_valueKey, obj.isPresent() ? obj.get() : 0);
        }

        @Override
        public String[][] getConfigPaths() {
            return new String[][]{{m_useKey}, {m_valueKey}};
        }

    }
}
