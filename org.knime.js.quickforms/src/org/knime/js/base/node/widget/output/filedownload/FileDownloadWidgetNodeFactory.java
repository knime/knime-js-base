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
 *   Jun 4, 2019 (Daniel Bogenrieder): created
 */
package org.knime.js.base.node.widget.output.filedownload;

import java.util.Map;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.webui.node.dialog.SettingsType;
import org.knime.core.webui.node.dialog.defaultdialog.DefaultKaiNodeInterface;
import org.knime.core.webui.node.dialog.kai.KaiNodeInterface;
import org.knime.core.webui.node.impl.WebUINodeConfiguration;
import org.knime.js.base.node.widget.WidgetNodeFactory;

/**
 * Factory for the file download widget node
 *
 * @author Daniel Bogenrieder, Christian Albrecht, KNIME GmbH, Konstanz, Germany
 */
public class FileDownloadWidgetNodeFactory
    extends WidgetNodeFactory<FileDownloadWidgetNodeModel, FileDownloadWidgetRepresentation, FileDownloadWidgetValue> {

    private static final String NAME = "File Download Widget";

    private static final String DESCRIPTION =
        "Provides a link with a downloadable file. The user needs to select a string or path flow variable "
            + "pointing to an existing file. This node is typically connected to a file writer (e.g. "
            + "CSV writer), whereby the writer exposes its destination file as variable that is selected in this "
            + "node's configuration dialog.";

    @SuppressWarnings({"deprecation", "restriction"})
    private static final WebUINodeConfiguration CONFIG = WebUINodeConfiguration.builder()//
        .name(NAME) //
        .icon("./widget_download_file.png") //
        .shortDescription(DESCRIPTION) //
        .fullDescription(DESCRIPTION) //
        .modelSettingsClass(FileDownloadWidgetNodeParameters.class) //
        .addInputPort("Flow Variable Input", FlowVariablePortObject.TYPE,
            "Variable input with the given path variable defined.") //
        .nodeType(NodeType.Widget) //
        .build();

    @SuppressWarnings("javadoc")
    public FileDownloadWidgetNodeFactory() {
        super(CONFIG, FileDownloadWidgetNodeParameters.class);
    }

    @Override
    public FileDownloadWidgetNodeModel createNodeModel() {
        return new FileDownloadWidgetNodeModel(getInteractiveViewName());
    }

    @Override
    public String getInteractiveViewName() {
        return NAME;
    }

    @Override
    protected NodeDialogPane createNodeDialogPane() {
        return new FileDownloadWidgetDialog();
    }

    @SuppressWarnings("restriction")
    @Override
    public KaiNodeInterface createKaiNodeInterface() {
        return new DefaultKaiNodeInterface(Map.of(SettingsType.MODEL, FileDownloadWidgetNodeParameters.class));
    }

}
