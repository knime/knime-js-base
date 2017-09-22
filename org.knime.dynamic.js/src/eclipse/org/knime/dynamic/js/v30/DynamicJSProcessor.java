/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
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
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
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
 *   Jun 2, 2015 (albrecht): created
 */
package org.knime.dynamic.js.v30;

import java.util.Map;
import java.util.WeakHashMap;

import org.knime.core.node.ExecutionContext;
import org.knime.core.node.port.PortObject;

/**
 * Interface to provide the possibility to pre-process incoming data during execute of a dynamically created JavaScript
 * node.
 *
 * @author Christian Albrecht, KNIME.com AG, Zurich, Switzerland
 * @since 3.0
 */
public interface DynamicJSProcessor {

    /**
     * Container class to store warning messages.
     * @since 3.4
     */
    class WarningMessage {
        private static final Map<DynamicJSProcessor, String> messageMap = new WeakHashMap<DynamicJSProcessor, String>();
    }

    /**
     * Called during execute. Possibility to process {@link PortObject}s to perform heavy calculation or data
     * transformation that is not intended to be run in JavaScript.
     *
     * @param inObjects The input objects.
     * @param exec The original execution context used during execute. Implementations can
     * @param config The configuration object containing the current node settings.
     * @return An array of processed input objects. If an object is not modified the original PortObject is expected,
     *         otherwise new PortObjects can be created or arbitrary Java objects, that can be serialized directly to
     *         JSON. The indices of the array are expected to be the same as in the inObjects array.
     * @throws Exception If processing the inputs fails for any reason.
     */
    public Object[] processInputObjects(final PortObject[] inObjects, final ExecutionContext exec,
        final DynamicJSConfig config) throws Exception;

    /**
     * Sets an optional warning message by the implementing processor.
     * @param message the warning message to set
     * @since 3.4
     */
    public default void setWarningMessage(final String message) {
        WarningMessage.messageMap.put(this, message);
    }

    /**
     * Returns an optional warning message. Also erases the warning message from this instance, so it can
     * only be queried once after every {@link #processInputObjects(PortObject[], ExecutionContext, DynamicJSConfig)} call.
     * @return a warning message to display, or null if not set
     * @since 3.4
     */
    public default String getWarningMessage() {
        return WarningMessage.messageMap.get(this);
    }
}