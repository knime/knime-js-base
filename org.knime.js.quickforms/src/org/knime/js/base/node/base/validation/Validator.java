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
package org.knime.js.base.node.base.validation;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;

/**
 * Combines {@link PortObjectSpecValidator} and {@link PortObjectValidator} into a common interface.
 *
 * @author Adrian Nembach, KNIME GmbH, Konstanz, Germany
 * @param <S> the type of validated {@link PortObjectSpec}
 * @param <T> the type of validated {@link PortObject}
 */
public interface Validator<S extends PortObjectSpec, T extends PortObject>
    extends PortObjectSpecValidator<S>, PortObjectValidator<T> {

    /**
     * Creates a {@link Validator} that only validates a {@link PortObjectSpec}.
     *
     * @param specValidator the {@link PortObjectSpecValidator}
     * @param portObjectClass the class of the corresponding {@link PortObject}
     * @return a {@link Validator} that delegates to {@link PortObjectSpecValidator specValidator} to validate a
     *         {@link PortObjectSpec}
     */
    static <S extends PortObjectSpec, T extends PortObject> Validator<S, T>
        createSpecValidator(final PortObjectSpecValidator<S> specValidator) {
        return new Validator<S, T>() {

            @Override
            public void validateSpec(final S portObjectSpec) throws InvalidSettingsException {
                specValidator.validateSpec(portObjectSpec);
            }

            @Override
            public void validateObject(final T portObject) throws InvalidSettingsException {
                // nothing to validate
            }

        };
    }

    /**
     * Creates a {@link Validator} that only validates a {@link PortObject}.
     *
     * @param objectValidator the {@link PortObjectValidator} to delegate to
     * @return a {@link Validator} that delegates to {@link PortObjectValidator objectValidator} to validate a
     *         {@link PortObject}
     */
    static <S extends PortObjectSpec, T extends PortObject> Validator<S, T>
        createTableValidator(final PortObjectValidator<T> objectValidator) {
        return new Validator<S, T>() {

            @Override
            public void validateSpec(final S tableSpec) throws InvalidSettingsException {
                // nothing to validate
            }

            @Override
            public void validateObject(final T table) throws InvalidSettingsException {
                objectValidator.validateObject(table);
            }
        };
    }

    /**
     * Creates a {@link Validator} that delegates to {@link PortObjectSpecValidator specValidator} for spec validation and
     * to {@link PortObjectValidator objectValidator} for object validation.
     *
     * @param specValidator the {@link PortObjectSpecValidator}
     * @param objectValidator the {@link PortObjectValidator}
     * @return the combined {@link Validator}
     */
    static <S extends PortObjectSpec, T extends PortObject> Validator<S, T>
        createValidator(final PortObjectSpecValidator<S> specValidator, final PortObjectValidator<T> objectValidator) {
        return new Validator<S, T>() {

            @Override
            public void validateSpec(final S portObjectSpec) throws InvalidSettingsException {
                specValidator.validateSpec(portObjectSpec);
            }

            @Override
            public void validateObject(final T portObjectSpec) throws InvalidSettingsException {
                objectValidator.validateObject(portObjectSpec);
            }

        };
    }
}
