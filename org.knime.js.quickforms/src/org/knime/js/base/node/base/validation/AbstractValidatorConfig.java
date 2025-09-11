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
 *   Nov 15, 2019 (Adrian Nembach, KNIME GmbH, Konstanz, Germany): created
 */
package org.knime.js.base.node.base.validation;

import java.util.Optional;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

/**
 * An abstract implementation of {@link ValidatorConfig} that stores an error message.
 *
 * @author Adrian Nembach, KNIME GmbH, Konstanz, Germany
 */
public abstract class AbstractValidatorConfig implements ValidatorConfig {

    public static final String CFG_ERROR_MESSAGE = "error_message";

    public static final String DEFAULT_ERROR_MESSAGE = "";

    private String m_errorMessage = DEFAULT_ERROR_MESSAGE;


    /**
     * @return the errorMessage
     */
    String getErrorMessage() {
        return m_errorMessage;
    }

    /**
     * @return an optional wrapping the error message if an error message was provided or an empty optional
     */
    public Optional<String> getErrorMessageForValidator() {
        if (m_errorMessage.length() == 0) {
            return Optional.empty();
        } else {
            return Optional.of(m_errorMessage);
        }
    }

    /**
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(final String errorMessage) {
        m_errorMessage = errorMessage;
    }

    @Override
    public void saveTo(final NodeSettingsWO settings) {
        settings.addString(CFG_ERROR_MESSAGE, m_errorMessage);
    }

    @Override
    public void loadInModel(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_errorMessage = settings.getString(CFG_ERROR_MESSAGE, DEFAULT_ERROR_MESSAGE);
    }

    @Override
    public void loadInDialog(final NodeSettingsRO settings) {
        m_errorMessage = settings.getString(CFG_ERROR_MESSAGE, DEFAULT_ERROR_MESSAGE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj.getClass().equals(getClass())) {
            final AbstractValidatorConfig other = (AbstractValidatorConfig)obj;
            return m_errorMessage.equals(other.m_errorMessage);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return new HashCodeBuilder().append(m_errorMessage).append(delegateHashCode()).toHashCode();
    }

    /**
     * Subclasses only need to check if {@link AbstractValidatorConfig} has the same members.
     *
     * @param config the AbstractValidatorConfig to check for equality
     * @return true if {@link AbstractValidatorConfig config} is equal, false otherwise
     */
    protected abstract boolean delegateEquals(final AbstractValidatorConfig config);

    /**
     * @return the hashcode based on the members of the implementing subclass
     */
    protected abstract int delegateHashCode();

}
