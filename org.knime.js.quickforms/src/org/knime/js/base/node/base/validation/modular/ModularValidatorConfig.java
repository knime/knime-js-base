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
 *   Nov 14, 2019 (Adrian Nembach, KNIME GmbH, Konstanz, Germany): created
 */
package org.knime.js.base.node.base.validation.modular;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.js.base.node.base.validation.ValidatorConfig;
import org.knime.js.base.node.base.validation.ValidatorFactory;
import org.knime.js.base.node.base.validation.ValidatorRegistry;

/**
 * The {@link ValidatorConfig} for a {@link ModularValidatorFactory}. It combines the configs of multiple
 * {@link ValidatorFactory factories} that are managed by the same {@link ModularValidatorFactory}.
 *
 * @author Adrian Nembach, KNIME GmbH, Konstanz, Germany
 */
public final class ModularValidatorConfig implements ValidatorConfig {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(ModularValidatorConfig.class);

    private final List<ValidatorConfig> m_configs;

    private final List<String> m_keys;

    ModularValidatorConfig(final List<ValidatorConfig> configs, final List<String> keys) {
        assert configs.size() == keys.size();
        m_configs = configs;
        m_keys = keys;
    }

    ValidatorConfig getConfig(final int idx) {
        return m_configs.get(idx);
    }

    @Override
    public void saveTo(final NodeSettingsWO settings) {
        final Iterator<String> keys = m_keys.iterator();
        final Iterator<ValidatorConfig> configs = m_configs.iterator();
        while (keys.hasNext()) {
            assert configs.hasNext();
            configs.next().saveTo(settings.addNodeSettings(keys.next()));
        }
    }

    /**
     * Loads a fresh config from the provided {@link NodeSettingsRO settings}. Uses the {@link ValidatorRegistry} to
     * create the appropriate config objects.
     *
     * @param settings {@link NodeSettingsRO} to load from
     * @return a fresh config corresponding to the configuration stored in {@link NodeSettingsRO settings}
     * @throws InvalidSettingsException if any settings can't be loaded
     */
    public static ModularValidatorConfig load(final NodeSettingsRO settings) throws InvalidSettingsException {
        final List<String> keys = new ArrayList<>();
        final List<ValidatorConfig> configs = new ArrayList<>();
        for (final String key : settings.keySet()) {
            final ValidatorConfig config = ValidatorRegistry.INSTANCE.createConfigForKey(key);
            config.loadInModel(settings.getNodeSettings(key));
            keys.add(key);
            configs.add(config);
        }
        return new ModularValidatorConfig(configs, keys);
    }

    @Override
    public void loadInModel(final NodeSettingsRO settings) throws InvalidSettingsException {
        final Iterator<String> keys = m_keys.iterator();
        final Iterator<ValidatorConfig> configs = m_configs.iterator();
        while (keys.hasNext()) {
            assert configs.hasNext();
            try {
                configs.next().loadInModel(settings.getNodeSettings(keys.next()));
            } catch (InvalidSettingsException e) {
                LOGGER.error("No settings available for validator.", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadInDialog(final NodeSettingsRO settings) {
        final Iterator<String> keys = m_keys.iterator();
        final Iterator<ValidatorConfig> configs = m_configs.iterator();
        while (keys.hasNext()) {
            assert configs.hasNext();
            try {
                configs.next().loadInDialog(settings.getNodeSettings(keys.next()));
            } catch (InvalidSettingsException e) {
                LOGGER.error("No settings available for validator.", e);
            }
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof ModularValidatorConfig) {
            final ModularValidatorConfig other = (ModularValidatorConfig)obj;
            return new EqualsBuilder().append(m_keys, other.m_keys).append(m_configs, other.m_configs).isEquals();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(m_keys).append(m_configs).toHashCode();
    }

}