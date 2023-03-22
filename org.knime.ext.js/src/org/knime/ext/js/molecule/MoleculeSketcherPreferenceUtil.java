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
 */
package org.knime.ext.js.molecule;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.FrameworkUtil;

/**
 * Utility class for Molecule Sketcher preferences initialized via the {@link MoleculeSketcherPreferenceInitializer} and
 * configurable via the {@link MoleculeSketcherPreferenceInitializer}.
 * 
 * @author @author Marc Bux, KNIME GmbH, Berlin, Germany
 *
 * @since 4.7.2
 */
public final class MoleculeSketcherPreferenceUtil {

    static final String NAME_SELECTION_KEY = "knime.ext.js.molecule.sketcher.name";

    static final String NAME_SELECTION_DEF = Ketcher_2_7_2.class.getName();

    static final String SERVER_URL_KEY = "knime.ext.js.molecule.server.url";

    static final String SERVER_URL_DEF = "";

    private static final MoleculeSketcherPreferenceUtil INSTANCE = new MoleculeSketcherPreferenceUtil();

    /** @return the instance to use. */
    public static MoleculeSketcherPreferenceUtil getInstance() {
        return INSTANCE;
    }

    private final String m_bundleSymbolicName;

    private final ScopedPreferenceStore m_store;

    private MoleculeSketcherPreferenceUtil() {
        m_bundleSymbolicName = FrameworkUtil.getBundle(MoleculeSketcher.class).getSymbolicName();
        m_store = new ScopedPreferenceStore(InstanceScope.INSTANCE, m_bundleSymbolicName);
    }

    ScopedPreferenceStore getStore() {
        return m_store;
    }

    /**
     * @return the currently selection {@link MoleculeSketcher}
     */
    public MoleculeSketcher getSelectedSketcher() {
        return MoleculeSketcherRegistry.getInstance().getMoleculeSketcher(m_store.getString(NAME_SELECTION_KEY));
    }

    /**
     * @return the URL of the sketcher server, or an empty String if a local sketcher installation should be used
     */
    public String getServerURL() {
        return m_store.getString(SERVER_URL_KEY);
    }

}
