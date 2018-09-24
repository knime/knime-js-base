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
 *   Sep 11, 2018 (Daniel Bogenrieder): created
 */
package org.knime.js.base.node.css.editor.autocompletion;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.JTextComponent;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.fife.rsta.ac.css.PropertyValueCompletionProvider;
import org.fife.ui.autocomplete.AbstractCompletionProvider;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.Util;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenTypes;
import org.xml.sax.SAXException;

/**
 * Extends the existing PropertyValueCompletionProvider to enable adding of knime-classes to the auto completion
 *
 * @author Daniel Bogenrieder, KNIME GmbH, Konstanz, Germany
 */
public class CssPropertyValueCompletionProvider extends PropertyValueCompletionProvider {

    private boolean isLess;

    private AbstractCompletionProvider.CaseInsensitiveComparator comparator;

    /**
     * The most common vendor prefixes. We ignore these.
     */
    private static final Pattern VENDOR_PREFIXES = Pattern.compile("^\\-(?:ms|moz|o|xv|webkit|khtml|apple)\\-");

    /**
     * @param isLess
     */
    public CssPropertyValueCompletionProvider(final boolean isLess) {
        super(isLess);
        comparator = new AbstractCompletionProvider.CaseInsensitiveComparator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValidChar(final char ch) {
        switch (ch) {
            case '-':
            case '_':
            case '#':
            case '@':
            case '.':
                return true;
        }
        return Character.isLetterOrDigit(ch);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Completion> getCompletionsImpl(final JTextComponent comp) {
        List<Completion> completionList = super.getCompletionsImpl(comp);
        RSyntaxTextArea textArea = (RSyntaxTextArea)comp;
        LexerState lex = getLexerCssState(textArea, textArea.getCaretLineNumber());

        if (lex == LexerState.SELECTOR) {
            if (getAlreadyEnteredText(comp).endsWith(".")) {
                try {
                    List<Completion> tempList = loadKnimeClassCompletions(comp, true);
                    completionList.addAll(tempList);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    List<Completion> tempList = loadKnimeClassCompletions(comp, false);
                    completionList.addAll(tempList);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return completionList;
    }


    /**
     * Append knime-classes to the other already found completions
     *
     * @param textComp textComponent which should be checked for completions
     * @param showAll true if not only knime-classes should be shown
     * @return returns either the knime-classes or the full list of completions
     * @throws IOException
     */
    private List<Completion> loadKnimeClassCompletions( final JTextComponent textComp,
        final boolean showAll) throws IOException {

        List<Completion> completions;
        URL url = getClass().getResource("../data/knime.xml");
        if (showAll) {
            completions = loadFromXML(url.getPath(), getAlreadyEnteredText(textComp));
        } else {
            completions = loadFromXML(url.getPath(), ".");
        }
        List<Completion> retVal = new ArrayList<>();

        Collections.sort(completions);
        String text = getAlreadyEnteredText(textComp);

        if (!showAll) {
            int index = Collections.binarySearch(completions, text, comparator);
            if (index < 0) { // No exact match
                index = -index - 1;
            } else {
                // If there are several overloads for the function being
                // completed, Collections.binarySearch() will return the index
                // of one of those overloads, but we must return all of them,
                // so search backward until we find the first one.
                int pos = index - 1;
                while (pos > 0 && comparator.compare(completions.get(pos), text) == 0) {
                    retVal.add(completions.get(pos));
                    pos--;
                }
            }

            while (index < completions.size()) {
                Completion c = completions.get(index);
                if (Util.startsWithIgnoreCase(c.getInputText(), text)) {
                    retVal.add(c);
                    index++;
                } else {
                    break;
                }
            }
        } else {
            return completions;
        }
        return retVal;
    }

    /**
     * Loads completions from an XML file. The XML should validate against <code>CompletionXml.dtd</code>.
     *
     * @param resource A resource the current ClassLoader can get to.
     * @throws IOException If an IO error occurs.
     */
    protected List<Completion> loadFromXML(final String resource, final String prependString) throws IOException {
        ClassLoader cl = getClass().getClassLoader();
        InputStream in = cl.getResourceAsStream(resource);
        if (in == null) {
            File file = new File(resource);
            if (file.isFile()) {
                in = new FileInputStream(file);
            } else {
                throw new IOException("No such resource: " + resource);
            }
        }
        BufferedInputStream bin = new BufferedInputStream(in);
        try {
            return loadFromXML(bin, null, prependString);
        } finally {
            bin.close();
        }
    }

    /**
     * Loads completions from an XML input stream. The XML should validate against <code>CompletionXml.dtd</code>.
     *
     * @param in The input stream to read from.
     * @param cl The class loader to use when loading any extra classes defined in the XML, such as custom
     *            {@link FunctionCompletion}s. This may be <code>null</code> if the default is to be used, or if no
     *            custom completions are defined in the XML.
     * @throws IOException If an IO error occurs.
     */
    private List<Completion> loadFromXML(final InputStream in, final ClassLoader cl, final String prependString)
        throws IOException {

        List<Completion> completions = null;

        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        KnimeCssCompletionXMLParser handler = new KnimeCssCompletionXMLParser(this, cl, prependString);
        BufferedInputStream bin = new BufferedInputStream(in);
        try {
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(bin, handler);
            completions = handler.getCompletions();
            // Ignore parameterized completion params
        } catch (SAXException se) {
            throw new IOException(se.toString());
        } catch (ParserConfigurationException pce) {
            throw new IOException(pce.toString());
        } finally {
            bin.close();
        }

        return completions;
    }

    private LexerState getLexerCssState(final RSyntaxTextArea textArea, int line) {

        int dot = textArea.getCaretPosition();
        LexerState state = LexerState.SELECTOR;
        boolean somethingFound = false;
        while (line >= 0 && !somethingFound) {
            Token t = textArea.getTokenListForLine(line--);
            while (t != null && t.isPaintable() && !t.containsPosition(dot)) {
                if (t.getType() == TokenTypes.RESERVED_WORD) {
                    state = LexerState.PROPERTY;
                    removeVendorPrefix(t.getLexeme());
                    somethingFound = true;
                } else if (!isLess && t.getType() == TokenTypes.VARIABLE) {
                    // TokenTypes.VARIABLE == IDs in CSS, variables in Less
                    state = LexerState.SELECTOR;
                    somethingFound = true;
                } else if (t.getType() == TokenTypes.PREPROCESSOR || t.getType() == TokenTypes.FUNCTION
                    || t.getType() == TokenTypes.LITERAL_NUMBER_DECIMAL_INT) {
                    state = LexerState.VALUE;
                    somethingFound = true;
                } else if (t.isLeftCurly()) {
                    state = LexerState.PROPERTY;
                    somethingFound = true;
                } else if (t.isRightCurly()) {
                    state = LexerState.SELECTOR;
                    somethingFound = true;
                } else if (t.isSingleChar(TokenTypes.OPERATOR, ':')) {
                    state = LexerState.VALUE;
                    somethingFound = true;
                } else if (t.isSingleChar(TokenTypes.OPERATOR, ';')) {
                    state = LexerState.PROPERTY;
                    somethingFound = true;
                }
                t = t.getNextToken();
            }
        }

        return state;
    }

    private static final String removeVendorPrefix(String text) {
        if (text.length() > 0 && text.charAt(0) == '-') {
            Matcher m = VENDOR_PREFIXES.matcher(text);
            if (m.find()) {
                text = text.substring(m.group().length());
            }
        }
        return text;
    }

}