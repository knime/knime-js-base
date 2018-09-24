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

import java.util.ArrayList;
import java.util.List;

import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.CompletionXMLParser;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *  Extends CompletionXMLParser in order to also parse a given icon path
 *  @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
public class KnimeCssCompletionXMLParser extends CompletionXMLParser{


    /**
     * The completions found after parsing the XML.
     */
    private List<Completion> completions;

    /**
     * The provider we're getting completions for.
     */
    private CompletionProvider provider;

    private String icon;
    private String name;
    private boolean doingKeywords;
    private String type;
    private StringBuilder desc;
    private boolean gettingDesc;
    private boolean inKeyword;
    private String prependString;
    /**
     * @param provider
     */
    public KnimeCssCompletionXMLParser(final CompletionProvider provider) {
        super(provider);
    }

    /**
     * Constructor.
     *
     * @param provider The provider to get completions for.
     * @param cl The class loader to use, if necessary, when loading classes
     *        from the XML  (custom {@link FunctionCompletion}s, for example).
     *        This may be <code>null</code> if the default is to be used, or
     *        if the XML does not define specific classes for completion types.
     * @see #reset(CompletionProvider)
     */
    public KnimeCssCompletionXMLParser(final CompletionProvider provider, final ClassLoader cl , final String prependString) {
       super(provider,cl);
       this.provider = provider;
       completions = new ArrayList<>();
       desc = new StringBuilder();
       this.prependString = prependString;
    }

    /**
     * Called when character data inside an element is found.
     */
    @Override
    public void characters(final char[] ch, final int start, final int length) {
        super.characters(ch, start, length);
        if (gettingDesc) {
            desc.append(ch, start, length);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endElement(final String uri, final String localName, final String qName) {

        if ("keywords".equals(qName)) {
            doingKeywords = false;
            super.endElement(uri, localName, qName);
        } else if (doingKeywords) {
            if ("keyword".equals(qName)) {
               Completion c  = null;
               if("knime".equals(type)) {
                   c = createKnimeCompletion();
                   completions.add(c);
               } else {
                   super.endElement(uri, localName, qName);
                   completions.add(super.getCompletions().get(super.getCompletions().size()-1));
               }
            } else {
                super.endElement(uri, localName, qName);
            }
            inKeyword = false;
        } else if (inKeyword && "desc".equals(qName)) {
                gettingDesc = false;
        }
    }

    private KnimeBasicCssCompletion createKnimeCompletion() {
        KnimeBasicCssCompletion bcc = new KnimeBasicCssCompletion(provider, prependString + name, icon);
        if (desc.length()>0) {
            bcc.setSummary("<div><a><b>" + name + ":</b></a><hr></div>" + "<div><br>" + desc.toString() +"</div>");
            desc.setLength(0);
        }
        return bcc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset(final CompletionProvider provider) {
        super.reset(provider);
        this.provider = provider;
        completions.clear();
        doingKeywords = gettingDesc = inKeyword = false;
    }

    @Override
    public InputSource resolveEntity(final String publicID,
            final String systemID) throws SAXException {
        return new InputSource(getClass().
                getResourceAsStream("CompletionXmlKnime.dtd"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attrs) {
        super.startElement(uri, localName, qName, attrs);
        if ("keywords".equals(qName)) {
            doingKeywords = true;
        }
        else if (doingKeywords) {
            if ("keyword".equals(qName)) {
                name = attrs.getValue("name");
                type = attrs.getValue("type");
                icon = attrs.getValue("icon");
                inKeyword = true;
            } else if (inKeyword && "desc".equals(qName)) {
                    gettingDesc = true;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Completion> getCompletions() {
        return completions;
    }






}

