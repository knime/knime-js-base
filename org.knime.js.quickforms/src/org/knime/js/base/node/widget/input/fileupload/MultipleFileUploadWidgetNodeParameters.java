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
 *   5 Nov 2025 (Robin Gerling): created
 */
package org.knime.js.base.node.widget.input.fileupload;

import org.knime.core.webui.node.dialog.defaultdialog.internal.widget.PersistWithin;
import org.knime.js.base.node.base.LabeledConfig;
import org.knime.js.base.node.base.input.fileupload.MultipleFileUploadNodeConfig;
import org.knime.js.base.node.parameters.ConfigurationAndWidgetNodeParametersUtil.FormFieldSection;
import org.knime.js.base.node.parameters.fileupload.SingleMultipleFileUploadNodeParameters;
import org.knime.js.base.node.parameters.fileupload.StoreInWFDirParameter;
import org.knime.js.base.node.widget.WidgetNodeParametersLabeled;
import org.knime.js.base.node.widget.input.fileupload.MultipleFileUploadDefaultValueParameters.MultipleFileUploadDefaultValueParametersPersistor;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.layout.Layout;
import org.knime.node.parameters.persistence.Persist;
import org.knime.node.parameters.persistence.Persistor;

/**
 * Settings for the multiple file upload widget node.
 *
 * @author Robin Gerling, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("restriction")
public final class MultipleFileUploadWidgetNodeParameters extends WidgetNodeParametersLabeled {

    @Persistor(MultipleFileUploadDefaultValueParametersPersistor.class)
    MultipleFileUploadDefaultValueParameters m_defaultValue = new MultipleFileUploadDefaultValueParameters();

    @PersistWithin.PersistEmbedded
    SingleMultipleFileUploadNodeParameters m_multipleFileUploadNodeParameters =
        new SingleMultipleFileUploadNodeParameters();

    @PersistWithin.PersistEmbedded
    StoreInWFDirParameter m_storeInWFDirParameter = new StoreInWFDirParameter();

    @Widget(title = "Allow multiple file uploads", description = """
            Check this box to allow the upload of multiple files at once. \
            Each file will be represented as an individual row in the output table. \
            It is only possible to set one default file.
            """)
    @Layout(FormFieldSection.class)
    @Persist(configKey = MultipleFileUploadNodeConfig.CFG_ALLOW_MULTIPLE_FILES)
    boolean m_allowMultipleFiles = MultipleFileUploadNodeConfig.DEFAULT_ALLOW_MULTIPLE_FILES;

    @Widget(title = "Require file upload",
        description = "Check this box to require the user to upload at least one file. If this box is checked and no"
            + " file is provided the node will not execute.")
    @Layout(FormFieldSection.Bottom.class)
    boolean m_required = LabeledConfig.DEFAULT_REQUIRED;

}
