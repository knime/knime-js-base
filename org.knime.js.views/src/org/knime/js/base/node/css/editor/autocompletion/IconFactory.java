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
 *   Sep 12, 2018 (daniel): created
 */
package org.knime.js.base.node.css.editor.autocompletion;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 *  Classes needed to show the knime icon next to knime completions
 *  @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
class IconFactory {

    private static IconFactory INSTANCE;

    private Map<String, Icon> iconMap;


    /**
     * Private constructor to prevent instantiation.
     */
    private IconFactory() {
        iconMap = new HashMap<String, Icon>();
    }


    /**
     * Returns the singleton instance of this class.
     *
     * @return The singleton instance.
     */
    public static IconFactory get() {
        if (INSTANCE==null) {
            INSTANCE = new IconFactory();
        }
        return INSTANCE;
    }


    /**
     * Returns the icon requested.
     *
     * @param key The key for the icon.
     * @return The icon.
     */
    public Icon getIcon(final String key) {
        Icon icon = iconMap.get(key);
        if (icon==null) {
            icon = loadIcon("data/images/" + key + ".png");
            iconMap.put(key, icon);
        }
        return icon;
    }


    /**
     * Loads an icon by file name.
     *
     * @param name The icon file name.
     * @return The icon.
     */
    private static Icon loadIcon(final String name) {
        Bundle bundle = FrameworkUtil.getBundle(IconFactory.class);
        IPath path = new Path("src/org/knime/js/base/node/css/editor/autocompletion/" + name);
        URL bundleURL = FileLocator.find(bundle, path, null);
        if (bundleURL == null) {
            throw new IllegalArgumentException("icon not found: " + name);
        }
        URL res;
        try {
            res = FileLocator.toFileURL(bundleURL);
        } catch (IOException e) {
            throw new IllegalArgumentException("icon not found: " + name);
        }
        return new ImageIcon(res);
    }


}