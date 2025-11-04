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
 *   4 Nov 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.parameters.fileupload;

import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.OutputSection;
import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.persistence.Persist;

/**
 * The common store in workflow directory node parameter of the single/multiple file upload widgets.
 *
 * @author Robin Gerling
 */
public class StoreInWFDirParameter implements NodeParameters {

    /**
     * The config key for the store in workflow directory setting.
     */
    public static final String CFG_STORE_IN_WF_DIR = "store_in_wf_dir";

    /**
     * The default value for the store in workflow directory setting.
     */
    public static final boolean DEFAULT_STORE_IN_WF_DIR = true;

    @Widget(title = "Store uploaded file in workflow directory", description = """
            Check this box to store the file in a temp directory in the workflow directory (e.g.
            /path/to/workflow/tmp/file_name). Otherwise, it will be created in the temp directory of the system, which
            can lead to unwanted behaviour, i.e. reader nodes do not allow direct access to the file system on
            KNIME Hub or due to swapping a job between systems in a server/executor environment. Note that the
            uploaded file will be deleted from the workflow when the workflow is discarded or reset.
            """)
    @Layout(OutputSection.Bottom.class)
    @Persist(configKey = CFG_STORE_IN_WF_DIR)
    boolean m_storeInWfDir = DEFAULT_STORE_IN_WF_DIR;

}
