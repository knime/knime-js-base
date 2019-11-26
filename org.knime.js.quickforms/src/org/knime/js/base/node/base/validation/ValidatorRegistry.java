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
 *   Nov 26, 2019 (Adrian Nembach, KNIME GmbH, Konstanz, Germany): created
 */
package org.knime.js.base.node.base.validation;

import java.util.HashMap;
import java.util.Map;

import org.knime.core.node.util.CheckUtils;
import org.knime.js.base.node.base.validation.domain.HasDomainValidatorFactory;
import org.knime.js.base.node.base.validation.min.column.MinNumColumnsValidatorFactory;
import org.knime.js.base.node.base.validation.min.row.MinNumRowsValidatorFactory;
import org.knime.js.base.node.base.validation.missing.MissingValueValidatorFactory;

/**
 * The central registry for {@link ValidatorFactory ValidatorFactories}.
 * It manages the different factories and makes sure that no key collisions occur.
 *
 * @author Adrian Nembach, KNIME GmbH, Konstanz, Germany
 */
public enum ValidatorRegistry {

    /**
     * The singleton instance.
     */
    INSTANCE;

    private final Map<String, ValidatorFactory<?, ?, ?>> m_keyToFactory = new HashMap<>();


    private ValidatorRegistry() {
        registerFactory(HasDomainValidatorFactory.INSTANCE);
        registerFactory(MinNumColumnsValidatorFactory.INSTANCE);
        registerFactory(MinNumRowsValidatorFactory.INSTANCE);
        registerFactory(MissingValueValidatorFactory.INSTANCE);
    }

    private void registerFactory(final ValidatorFactory<?, ?, ?> factory) {
        final String key = factory.getSettingsKey();
        CheckUtils.checkState(!m_keyToFactory.containsKey(key), "Duplicate key '%s' detected.");
        m_keyToFactory.put(key, factory);
    }

    /**
     * Creates a new {@link ValidatorConfig} by looking up the {@link ValidatorFactory} corresponding to <b>key</b>
     * and invoking {@link ValidatorFactory#createConfig()} on it.
     *
     * @param key the key returned by {@link ValidatorFactory#getSettingsKey()}
     * @return a fresh config for the provided <b>key</b>
     */
    public ValidatorConfig createConfigForKey(final String key) {
        CheckUtils.checkArgument(m_keyToFactory.containsKey(key), "Unknown key '%s' encountered.", key);
        return m_keyToFactory.get(key).createConfig();
    }

}
