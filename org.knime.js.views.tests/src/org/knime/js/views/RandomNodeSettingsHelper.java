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
 *   Aug 2, 2021 (hornm): created
 */
package org.knime.js.views;

import java.util.Random;
import java.util.UUID;

import org.knime.core.data.DataCell;
import org.knime.core.data.MissingCell;
import org.knime.core.node.NodeSettings;

/**
 * Helper to add random values to a {@link NodeSettings}-object.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
abstract class RandomNodeSettingsHelper {

    protected static final Random RANDOM = new Random();

    protected void addRandomValue(final String key, final Class<?> clazz, final NodeSettings ns) {
        if (clazz.equals(Integer.class)) {
            int val = RANDOM.nextInt();
            ns.addInt(key, val);
        } else if (clazz.equals(Double.class)) {
            double val = randomDouble(key);
            ns.addDouble(key, val);
        } else if (clazz.equals(Float.class)) {
            Float val = RANDOM.nextFloat();
            ns.addFloat(key, val);
        } else if (clazz.equals(Boolean.class)) {
            // alternate an already existing boolean for that key to make sure it changes
            boolean val = ns.getBoolean(key, RANDOM.nextBoolean());
            ns.addBoolean(key, !val);
        } else if (clazz.equals(String.class)) {
            String val = randomString(key);
            ns.addString(key, val);
        } else if (clazz.equals(IntegerStoredAsString.class)) {
            int val = RANDOM.nextInt();
            ns.addString(key, String.valueOf(val));
        } else if (clazz.equals(DoubleStoredAsString.class)) {
            double val = randomDouble(key);
            ns.addString(key, String.valueOf(val));
        } else if (clazz.equals(DataCell.class)) {
            ns.addDataCell(key, new MissingCell(""));
        } else if (clazz.equals(Object.class)) {
            //
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Allows one to control the generation of random values for string entries.
     *
     * @param key
     * @return a random string
     */
    protected String randomString(final String key) {
        return UUID.randomUUID().toString();
    }

    /**
     * Allows one to control the generation of random values for double entries.
     *
     * @param key
     * @return a random double
     */
    protected double randomDouble(final String key) {
        return RANDOM.nextDouble();
    }

    /**
     * If a random integer is supposed to be created which is, however, stored as string in a
     * {@link NodeSettings}-instance.
     */
    public final static class IntegerStoredAsString {
        private IntegerStoredAsString() {
        }
    }

    /**
     * If a random double is supposed to be created which is, however, stored as string in a
     * {@link NodeSettings}-instance.
     */
    public final static class DoubleStoredAsString {
        private DoubleStoredAsString() {
        }
    }

}
