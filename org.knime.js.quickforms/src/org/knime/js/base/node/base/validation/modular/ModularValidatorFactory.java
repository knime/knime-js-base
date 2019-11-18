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
import java.util.stream.Collectors;

import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.CheckUtils;
import org.knime.js.base.node.base.validation.Validator;
import org.knime.js.base.node.base.validation.ValidatorConfig;
import org.knime.js.base.node.base.validation.ValidatorDialog;
import org.knime.js.base.node.base.validation.ValidatorFactory;

/**
 * A modular {@link ValidatorFactory} that allows to combine multiple {@link ValidatorFactory ValidatorFactories} that
 * share a common type of {@link PortObjectSpec} and {@link PortObject}.
 *
 * @author Adrian Nembach, KNIME GmbH, Konstanz, Germany
 * @param <S> The type of {@link PortObjectSpec} this factory creates {@link Validator validators} for
 * @param <T> The type of {@link PortObject} this factory creates {@link Validator validators} for
 */
public final class ModularValidatorFactory<S extends PortObjectSpec, T extends PortObject>
    implements ValidatorFactory<S, T, ModularValidatorConfig> {

    private List<ValidatorFactory<S, T, ?>> m_factories;

    /**
     * Creates a {@link ModularValidatorFactory} that contains all factories in {@link ValidatorFactory validatorFactories}.
     *
     * @param validatorFactories the factories to that make up the combined factory
     */
    public ModularValidatorFactory(final List<ValidatorFactory<S, T, ?>> validatorFactories) {
        m_factories = new ArrayList<>(validatorFactories);
    }

    /**
     * Creates a {@link ModularValidatorFactory} that contains all factories in {@link ValidatorFactory validatorFactories}.
     *
     * @param factories the factories to that make up the combined factory
     */
    @SafeVarargs // the array is only used locally to create an independent list
    public ModularValidatorFactory(final ValidatorFactory<S, T, ?>... factories) {
        CheckUtils.checkArgument(factories.length > 0, "At least one factory must be provided");
        m_factories = new ArrayList<>(factories.length);
        for (ValidatorFactory<S, T, ?> factory : factories) {
            m_factories.add(factory);
        }
    }

    @Override
    public ModularValidatorConfig createConfig() {
        return new ModularValidatorConfig(
            m_factories.stream().map(ValidatorFactory::createConfig).collect(Collectors.toList()),
            m_factories.stream().map(ValidatorFactory::getSettingsKey).collect(Collectors.toList()));
    }

    @Override
    public Validator<S, T> createValidator(final ModularValidatorConfig config) {
        final Iterator<ValidatorFactory<S, T, ?>> factories = m_factories.iterator();
        final List<Validator<S, T>> validators = new ArrayList<>(m_factories.size());
        for (int i = 0; factories.hasNext(); i++) {
            validators.add(createValidator(factories.next(), config.getConfig(i)));
        }
        return new ModularValidator<>(validators);
    }

    @SuppressWarnings("unchecked") // it is explicitly checked that factory and config are compatible
    private static <S extends PortObjectSpec, T extends PortObject> Validator<S, T>
        createValidator(final ValidatorFactory<S, T, ?> factory, final ValidatorConfig config) {
        CheckUtils.checkState(factory.getConfigClass().isInstance(config),
            "Incompatible factory of type '%s' and config of type '%s' encountered.", factory.getClass().getName(),
            config.getClass().getName());
        // safe if config was created by factory
        assert factory.getConfigClass().isInstance(config);
        @SuppressWarnings("rawtypes") // necessary to make the call in the next line
        final ValidatorFactory unsafeFactory = factory;
        return unsafeFactory.createValidator(config);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidatorDialog<ModularValidatorConfig> createDialog() {
        return new ModularValidatorDialog(
            m_factories.stream().map(ValidatorFactory::createDialog).collect(Collectors.toList()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSettingsKey() {
        return "validation";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<ModularValidatorConfig> getConfigClass() {
        return ModularValidatorConfig.class;
    }
}
