/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.xpn.xwiki.it.selenium;

import java.awt.event.KeyEvent;

import junit.framework.Test;

import com.xpn.xwiki.it.selenium.framework.AbstractWysiwygTestCase;
import com.xpn.xwiki.it.selenium.framework.AlbatrossSkinExecutor;
import com.xpn.xwiki.it.selenium.framework.XWikiTestSuite;

/**
 * Tests for the custom list support, to handle and generate valid XHTML lists in the wysiwyg. At the moment, this class
 * tests processing the rendered lists rather than creating new lists from the wysiwyg, to ensure that valid rendered
 * lists are managed correctly.
 * 
 * @version $Id$
 */
public class ListSupportTest extends AbstractWysiwygTestCase
{
    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Tests valid XHTML list support in the wysiwyg editor.");
        suite.addTestSuite(ListSupportTest.class, AlbatrossSkinExecutor.class);
        return suite;
    }

    /**
     * @see XWIKI-2734: Cannot edit the outer list item. The test is not deeply relevant as we are positioning the range
     *      programatically. The correct test would prove that the caret can be positioned there with the keys.
     */
    public void testEmptyListItemsEditable()
    {
        setWikiContent("** rox");
        // Check that a br is added in the parent list item so that it becomes editable
        assertXHTML("<ul><li><br class=\"spacer\"><ul><li>rox</li></ul></li></ul>");
        // Place the caret in the first list item
        moveCaret("XWE.body.firstChild.firstChild", 0);
        typeText("x");
        assertXHTML("<ul><li>x<br class=\"spacer\"><ul><li>rox</li></ul></li></ul>");
    }

    /**
     * Test the case when hitting enter in a list item before a sublist, that it creates an editable list item under.
     * The test is not deeply relevant since we are positioning the range in the item under programatically. The correct
     * test would prove that the caret can be positioned there with the keys.
     */
    public void testEnterBeforeSublist()
    {
        setWikiContent("* x\n** rox");
        moveCaret("XWE.body.firstChild.firstChild.firstChild", 1);
        typeEnter();
        // Check the created item is editable
        assertXHTML("<ul><li>x</li><li><br class=\"spacer\"><ul><li>rox</li></ul></li></ul>");
        moveCaret("XWE.body.firstChild.childNodes[1]", 0);
        typeText("w");
        assertXHTML("<ul><li>x</li><li>w<br class=\"spacer\"><ul><li>rox</li></ul></li></ul>");
    }

    /**
     * Test the midas bug which causes the list items in a list to be replaced with an empty list and the caret to be
     * left inside the ul, not editable.
     */
    public void testEnterOnEntireList()
    {
        setWikiContent("* foo\n* bar");
        // Set the selection around the list
        select("XWE.body.firstChild.firstChild.firstChild", 0, "XWE.body.firstChild.lastChild.firstChild", 3);
        typeEnter();
        typeText("foobar");
        assertXHTML("<p><br class=\"spacer\"></p>foobar");
    }

    /**
     * Test delete works fine inside a list item, and before another element (such as bold).
     */
    public void testDeleteInsideItem()
    {
        setWikiContent("* foo**bar**\n** far");
        // Set the selection inside the foo text
        moveCaret("XWE.body.firstChild.firstChild.firstChild", 1);
        typeDelete();
        assertXHTML("<ul><li>fo<strong>bar</strong><ul><li>far</li></ul></li></ul>");

        // set the selection just before the bold text but inside the text before
        moveCaret("XWE.body.firstChild.firstChild.firstChild", 2);
        typeDelete();
        assertXHTML("<ul><li>fo<strong>ar</strong><ul><li>far</li></ul></li></ul>");
    }

    /**
     * Test backspace works fine inside a list item, and after another element (such as italic).
     */
    public void testBackspaceInsideItem()
    {
        setWikiContent("* foo\n** b//arf//ar");
        // Set the selection in the "ar" text in the second list item
        moveCaret("XWE.body.firstChild.firstChild.lastChild.firstChild.lastChild", 1);
        typeBackspace();
        assertXHTML("<ul><li>foo<ul><li>b<em>arf</em>r</li></ul></li></ul>");
        // delete again, now it should delete inside the em
        typeBackspace();
        assertXHTML("<ul><li>foo<ul><li>b<em>ar</em>r</li></ul></li></ul>");
    }

    /**
     * Test that the delete at the end of the list works fine
     */
    public void testDeleteInSameList()
    {
        setWikiContent("* foo\n* bar");
        // Set the selection at the end of the first item
        moveCaret("XWE.body.firstChild.firstChild.firstChild", 3);
        typeDelete();
        assertXHTML("<ul><li>foobar</li></ul>");

    }

    /**
     * Test that the backspace at the beginning of the second item in a list moves the items together in the first list
     * item.
     */
    public void testBackspaceInSameList()
    {
        setWikiContent("* foo\n* bar");
        // Set the selection at the end of the first item
        moveCaret("XWE.body.firstChild.lastChild.firstChild", 0);
        typeBackspace();
        assertXHTML("<ul><li>foobar</li></ul>");

    }

    /**
     * Test that delete at the end of a list preserves browser default behaviour: for firefox is to join the two lists.
     */
    public void testDeleteInDifferentLists()
    {
        setWikiContent("* foo\n\n* bar");
        // Set the selection at the end of the first item
        moveCaret("XWE.body.firstChild.firstChild.firstChild", 3);
        typeDelete();
        assertXHTML("<ul><li>foo</li><li>bar</li></ul>");
    }

    /**
     * Test that backspace at the beginning of a list preserves browser default behaviour: for firefox is to join the
     * two lists.
     */
    public void testBackspaceInDifferentLists()
    {
        setWikiContent("* foo\n\n* bar");
        // Set the selection at the end of the first item
        moveCaret("XWE.body.lastChild.firstChild.firstChild", 0);
        typeBackspace();
        assertXHTML("<ul><li>foo</li><li>bar</li></ul>");
    }

    /**
     * Test that backspace at the beginning of a list after another list in an embedded document (two lists on the
     * second level) preserves default behaviour: for firefox is to join the two lists.
     */
    public void testBackspaceInEmbeddedDocumentDifferentLists()
    {
        setWikiContent("* foo\n* bar (((\n* foo 2\n1. bar 2)))");
        moveCaret("XWE.body.firstChild.childNodes[1].childNodes[1].childNodes[1].firstChild.firstChild", 0);
        typeBackspace();
        assertXHTML("<ul><li>foo</li><li>bar<div><ul><li>foo 2</li><li>bar 2</li></ul></div></li></ul>");
    }

    /**
     * Test that delete at the end of a list before another list in an embedded document (two lists on the second level)
     * preserves default behaviour: for firefox is to join the two lists.
     */
    public void testDeleteInEmbeddedDocumentDifferentLists()
    {
        setWikiContent("* foo\n* bar (((\n1. foo 2\n* bar 2)))");
        moveCaret("XWE.body.firstChild.childNodes[1].childNodes[1].firstChild.firstChild.firstChild", 5);
        typeDelete();
        assertXHTML("<ul><li>foo</li><li>bar<div><ol><li>foo 2</li><li>bar 2</li></ol></div></li></ul>");
    }

    public void testBackspaceInEmbeddedDocumentList()
    {
        setWikiContent("* foo(((bar\n* foar)))");
        moveCaret("XWE.body.firstChild.firstChild.childNodes[1].childNodes[1].firstChild.firstChild", 0);
        typeBackspace();
        assertXHTML("<ul><li>foo<div><p>barfoar</p></div></li></ul>");
    }

    public void testDeleteInEmbeddedDocumentList()
    {
        setWikiContent("* foo(((* bar\n\nfoar)))");
        moveCaret("XWE.body.firstChild.firstChild.childNodes[1].firstChild.firstChild.firstChild", 3);
        typeDelete();
        assertXHTML("<ul><li>foo<div><ul><li>barfoar</li></ul></div></li></ul>");
    }

    /**
     * Tests that the delete moves the first item on another level in the item in which is executed.
     */
    public void testDeleteBeforeSublist()
    {
        // 1/ with only one item -> the sublist should be removed
        setWikiContent("* foo\n** bar\n");
        // Set the selection at the end of the first item
        moveCaret("XWE.body.firstChild.firstChild.firstChild", 3);
        typeDelete();
        assertXHTML("<ul><li>foobar</li></ul>");

        // 2/ with more than one item -> only the first item should be moved to the list above
        setWikiContent("* foo\n** bar\n** far");
        // Set the selection at the end of the first item
        moveCaret("XWE.body.firstChild.firstChild.firstChild", 3);
        typeDelete();
        assertXHTML("<ul><li>foobar<ul><li>far</li></ul></li></ul>");
    }

    /**
     * Test that backspace at the beginning of an item in a sublist moves the item in the list item before, on a lower
     * list level.
     */
    public void testBackspaceBeginSublist()
    {
        // 1/ with only one item -> the sublist should be deleted
        setWikiContent("* foo\n** bar\n");
        // Set the selection at beginning of the first item in sublist
        moveCaret("XWE.body.firstChild.firstChild.lastChild.firstChild.firstChild", 0);
        typeBackspace();
        assertXHTML("<ul><li>foobar</li></ul>");

        // 2/ with more than one item -> only the first item should be moved to the list above
        setWikiContent("* foo\n** bar\n** far");
        // Set the selection at beginning of the first item in sublist
        moveCaret("XWE.body.firstChild.firstChild.lastChild.firstChild.firstChild", 0);
        typeBackspace();
        assertXHTML("<ul><li>foobar<ul><li>far</li></ul></li></ul>");
    }

    /**
     * Test that deleting at the end of a list item with a sublist with another sublist inside, moves the first sublist
     * and the elements on level 3 are moved to level 2.
     */
    public void testDeleteDecreasesLevelWithEmptyItem()
    {
        setWikiContent("* foo\n*** bar\n");
        // Set the selection at beginning of the first item in sublist
        moveCaret("XWE.body.firstChild.firstChild.firstChild", 3);
        typeDelete();
        assertXHTML("<ul><li>foo<br class=\"spacer\"><ul><li>bar</li></ul></li></ul>");
    }

    /**
     * Test that hitting backspace at the beginning of a list item with a sublist moves this element in its parent list
     * item and decreases the level of the subitems.
     */
    public void testBackspaceDecreasesLevelWithEmptyItem()
    {
        setWikiContent("* foo\n*** bar\n");
        // Set the selection at beginning of the first item in sublist
        moveCaret("XWE.body.firstChild.firstChild.lastChild.firstChild", 0);
        typeBackspace();
        assertXHTML("<ul><li>foo<br class=\"spacer\"><ul><li>bar</li></ul></li></ul>");
    }

    /**
     * Test delete at the end of a sublist item on a higher level moves the list item on the lower level inside it.
     * 
     * @see XWIKI-3114: Backspace is ignored at the beginning of a list item if the previous list item is on a lower
     *      level.
     */
    public void testDeleteBeforePreviousLevelItem()
    {
        setWikiContent("* foo\n** bar\n* bar minus one");
        // Set the selection at the end of "bar"
        moveCaret("XWE.body.firstChild.firstChild.lastChild.firstChild.firstChild", 3);
        typeDelete();
        assertXHTML("<ul><li>foo<ul><li>barbar minus one</li></ul></li></ul>");
    }

    /**
     * Test backspace at the beginning of a sublist item before a sublist moves the item on the lower level to the item
     * on the higher level.
     * 
     * @see XWIKI-3114: Backspace is ignored at the beginning of a list item if the previous list item is on a lower
     *      level.
     */
    public void testBackspaceAfterPreviousLevelItem()
    {
        setWikiContent("* foo\n** bar\n* bar minus one");
        // Set the selection at the end of "bar"
        moveCaret("XWE.body.firstChild.lastChild.firstChild", 0);
        typeBackspace();
        assertXHTML("<ul><li>foo<ul><li>barbar minus one</li></ul></li></ul>");
    }

    /**
     * Test deleting the last piece of text inside a list item with sublists, keeps the remaining list item empty but
     * editable. The test is weak, since we position the range programatically. The true test should try to navigate to
     * the list item.
     */
    public void testDeleteAllTextInListItem()
    {
        setWikiContent("* foo\n* b\n** ar");

        // Set the selection at the beginning of the text in the second list item
        moveCaret("XWE.body.firstChild.lastChild.firstChild", 0);
        typeDelete();
        // test that the list structure is correct: two items one with a sublist
        assertXHTML("<ul><li>foo</li><li><br class=\"spacer\"><ul><li>ar</li></ul></li></ul>");
        // type in the empty list item
        moveCaret("XWE.body.firstChild.lastChild", 0);
        typeText("bar");
        assertXHTML("<ul><li>foo</li><li>bar<br class=\"spacer\"><ul><li>ar</li></ul></li></ul>");
        // now delete, to test that it jumps the <br>
        typeDelete();
        assertXHTML("<ul><li>foo</li><li>barar</li></ul>");
    }

    /**
     * Test backspacing the last piece of text inside a list item with sublists, keeps the remaining list item empty but
     * editable. The test is weak, since we position the range programatically. The true test should try to navigate to
     * the list item.
     */
    public void testBackspaceAllTextInListItem()
    {
        setWikiContent("* foo\n* b\n** ar");

        // Set the selection at the end of the text in the second list item
        moveCaret("XWE.body.firstChild.lastChild.firstChild", 1);
        typeBackspace();
        // test that the list structure is correct: two items one with a sublist
        assertXHTML("<ul><li>foo</li><li><br class=\"spacer\"><ul><li>ar</li></ul></li></ul>");
        // type in the empty list item
        moveCaret("XWE.body.firstChild.lastChild", 0);
        typeText("bar");
        assertXHTML("<ul><li>foo</li><li>bar<br class=\"spacer\"><ul><li>ar</li></ul></li></ul>");
        // Put the caret at the beginning of the sublist
        moveCaret("XWE.body.firstChild.lastChild.lastChild.firstChild.firstChild", 0);
        // now backspace, to test that it jumps the <br>
        typeBackspace();
        assertXHTML("<ul><li>foo</li><li>barar</li></ul>");
    }

    /**
     * Test delete before text outside lists.
     */
    public void testDeleteBeforeParagraph()
    {
        setWikiContent("* one\n* two\n\nFoobar");

        // Set the selection at the end of the "two" list item
        moveCaret("XWE.body.firstChild.lastChild.firstChild", 3);
        typeDelete();
        assertXHTML("<ul><li>one</li><li>twoFoobar</li></ul>");

        // now run the case with delete in a sublist
        setWikiContent("* one\n** two\n\nFoobar");

        // Set the selection at the end of the "two" list item
        moveCaret("XWE.body.firstChild.firstChild.lastChild.firstChild.firstChild", 3);
        typeDelete();
        assertXHTML("<ul><li>one<ul><li>twoFoobar</li></ul></li></ul>");
    }

    /**
     * Test backspace at the beginning of list item, after text outside lists.
     */
    public void testBackspaceAfterParagraph()
    {
        setWikiContent("Foobar\n\n* one\n* two");

        // Set the selection at the beginning of the "one" list item
        moveCaret("XWE.body.lastChild.firstChild.firstChild", 0);
        typeBackspace();
        assertXHTML("<p>Foobarone</p><ul><li>two</li></ul>");

        // Now test the case when the list has a sublist, in which case FF keeps the sublist parent, as empty and
        // editable
        // Note that this behaves differently on Internet Explorer, unwrapping the sublist
        setWikiContent("Foobar\n\n* one\n** two");

        // Set the selection at the beginning of the "one" list item
        moveCaret("XWE.body.lastChild.firstChild.firstChild", 0);
        typeBackspace();
        assertXHTML("<p>Foobarone</p><ul><li><br class=\"spacer\"><ul><li>two</li></ul></li></ul>");
    }

    /**
     * Test deleting the whole selection on a list, on multiple list levels keeps the list valid. Test that the parents
     * of the indented list items that stay are editable.
     */
    public void testDeleteSelectionPreserveSublists()
    {
        setWikiContent("* one\n** two\n** three\n*** four\n*** five");

        // Set the selection starting in the one element and ending in the four element
        select("XWE.body.firstChild.firstChild.firstChild", 2,
            "XWE.body.firstChild.firstChild.lastChild.lastChild.lastChild.firstChild.firstChild", 2);
        typeDelete();
        assertXHTML("<ul><li>on<ul><li><br class=\"spacer\"><ul><li>ur</li><li>five</li></ul></li></ul></li></ul>");
    }

    /**
     * Test deleting the whole selection on a list, on multiple list levels deletes all the fully enclosed list items
     * and lists, and keeps the result in a single list item if the selection ends are on the same list level.
     */
    public void testDeleteSelectionDeletesEnclosedSublists()
    {
        setWikiContent("* one\n** two\n** three\n*** four\n** five\n* six");

        // Set the selection starting in the one element and ending in the six element
        select("XWE.body.firstChild.firstChild.firstChild", 2, "XWE.body.firstChild.lastChild.firstChild", 1);
        typeDelete();
        assertXHTML("<ul><li>onix</li></ul>");
    }

    /**
     * Test creating a list with two items and indenting the second item. The indented item should be a sublist of the
     * first item and the resulted HTML should be valid.
     */
    public void testIndentNoSublist()
    {
        clickUnorderedListButton();
        typeText("foo");
        typeEnter();
        typeText("bar");
        assertXHTML("<ul><li>foo</li><li>bar<br class=\"spacer\"></li></ul>");
        clickIndentButton();
        assertXHTML("<ul><li>foo<ul><li>bar<br class=\"spacer\"></li></ul></li></ul>");
        // test that the indented item cannot be indented once more
        assertFalse(isIndentButtonEnabled());
        moveCaret("XWE.body.firstChild.firstChild.childNodes[1].firstChild.firstChild", 0);
        typeTab();
        // check that nothing happened
        assertXHTML("<ul><li>foo<ul><li>bar<br class=\"spacer\"></li></ul></li></ul>");

        assertWiki("* foo\n** bar");
    }

    /**
     * Test that indenting an item to the second level under a list item with a list already on the second level unifies
     * the two lists.
     */
    public void testIndentUnderSublist()
    {
        clickUnorderedListButton();
        typeTextThenEnter("one");
        typeTextThenEnter("two");
        typeTab();
        typeTextThenEnter("two plus one");
        typeShiftTab();
        typeText("three");
        clickIndentButton();
        assertXHTML("<ul><li>one</li><li>two<ul><li>two plus one</li>"
            + "<li>three<br class=\"spacer\"></li></ul></li></ul>");
        assertWiki("* one\n* two\n** two plus one\n** three");
    }

    /**
     * Test indenting (using the tab key) an item with a sublist, that the child sublist is indented with its parent.
     * 
     * @see XWIKI-3118: Indenting a list item with a sublist works incorrectly.
     * @see XWIKI-3117: Shift + Tab does works incorrect on an item that contains a sublist.
     */
    public void testIndentOutdentWithSublist()
    {
        clickUnorderedListButton();
        typeText("foo");
        typeEnter();
        typeText("bar");
        clickIndentButton();
        // move to the end of the foo element, hit enter, tab and type. Should create a new list item, parent of the bar
        // sublist, tab should indent and type should add content
        moveCaret("XWE.body.firstChild.firstChild.firstChild", 3);
        typeText("one");
        moveCaret("XWE.body.firstChild.firstChild.firstChild", 3);
        typeEnter();
        assertXHTML("<ul><li>foo</li><li>one<ul><li>bar<br class=\"spacer\"></li></ul></li></ul>");
        // check that the list item is indentable, the list plugin is correctly recognizing lists (XWIKI-3061)
        assertTrue(isIndentButtonEnabled());
        clickIndentButton();
        assertXHTML("<ul><li>foo<ul><li>one<ul><li>bar<br class=\"spacer\"></li></ul></li></ul></li></ul>");
        assertWiki("* foo\n** one\n*** bar");
        // select second element "one"
        select("XWE.body.firstChild.firstChild.childNodes[1].firstChild.firstChild", 0,
            "XWE.body.firstChild.firstChild.childNodes[1].firstChild.firstChild", 3);
        // check that the list item is outdentable, the list plugin is correctly recognizing lists (XWIKI-3061)
        assertTrue(isOutdentButtonEnabled());
        clickOutdentButton();
        assertXHTML("<ul><li>foo</li><li>one<ul><li>bar</li></ul></li></ul>");
        moveCaret("XWE.body.firstChild.childNodes[1].childNodes[1].firstChild.firstChild", 0);
        clickOutdentButton();
        assertXHTML("<ul><li>foo</li><li>one</li><li>bar</li></ul>");
        assertWiki("* foo\n* one\n* bar");
    }

    /**
     * Test outdenting an item on the first level in a list: it should split the list in two and put the content of the
     * unindented item in between.
     */
    public void testOutdentOnFirstLevel()
    {
        clickUnorderedListButton();
        typeTextThenEnter("one");
        typeTextThenEnter("two");
        typeTab();
        typeTextThenEnter("two plus one");
        typeShiftTab();
        typeText("three");
        // move caret at the beginning of the "two" item
        moveCaret("XWE.body.firstChild.childNodes[1].firstChild", 0);
        typeShiftTab();
        assertXHTML("<ul><li>one</li></ul><p>two</p><ul><li>two plus one</li><li>three"
            + "<br class=\"spacer\"></li></ul>");
        assertWiki("* one\n\ntwo\n\n* two plus one\n* three");
    }

    /**
     * Test outdenting an item on the second level inside a list item which also contains content after the sublist
     * correctly moves the content in the outdented list item.
     */
    public void testOutdentWithContentAfter()
    {
        setContent("<ul><li>one<br />before<ul><li>two</li><li>three</li><li>four</li></ul>after</li></ul>");
        moveCaret("XWE.body.firstChild.firstChild.childNodes[3].childNodes[1].firstChild", 0);
        assertTrue(isOutdentButtonEnabled());
        clickOutdentButton();
        assertXHTML("<ul><li>one<br>before<ul><li>two</li></ul></li><li>three<ul><li>four</li></ul>"
            + "after</li></ul>");
    }

    /**
     * Test that hitting the . key at the end of a list item does not act as delete.
     * 
     * @see http://jira.xwiki.org/jira/browse/XWIKI-3304
     */
    public void testDotAtEndDoesNotDelete()
    {
        setWikiContent("* foo\n* bar");
        // Set the selection at the end of the first item
        moveCaret("XWE.body.firstChild.firstChild.firstChild", 3);
        // type the dot native, to make sure it goes through the browser's key handling code
        getSelenium().keyPressNative(Integer.toString(KeyEvent.VK_PERIOD));
        assertXHTML("<ul><li>foo.</li><li>bar</li></ul>");
    }

    /**
     * @see XWIKI-3447: List detection is reversed
     */
    public void testListDetection()
    {
        setWikiContent("before\n\n" + "* unordered list item\n*1. ordered sub-list item\n\n"
            + "1. ordered list item\n1*. unordered sub-list item");

        // Outside lists
        moveCaret("XWE.body.firstChild.firstChild", 3);
        assertFalse(isOrderedListDetected());
        assertFalse(isUnorderedListDetected());

        // Inside unordered list item
        moveCaret("XWE.body.childNodes[1].firstChild.firstChild", 4);
        assertFalse(isOrderedListDetected());
        assertTrue(isUnorderedListDetected());
        // Inside ordered sub-list item
        moveCaret("XWE.body.childNodes[1].firstChild.lastChild.firstChild.firstChild", 7);
        assertTrue(isOrderedListDetected());
        assertTrue(isUnorderedListDetected());

        // Inside ordered list item
        moveCaret("XWE.body.childNodes[2].firstChild.firstChild", 10);
        assertTrue(isOrderedListDetected());
        assertFalse(isUnorderedListDetected());
        // Inside unordered sub-list item
        moveCaret("XWE.body.childNodes[2].firstChild.lastChild.firstChild.firstChild", 9);
        assertTrue(isOrderedListDetected());
        assertTrue(isUnorderedListDetected());
    }

    /**
     * @see XWIKI-3773: Adding and editing lists in table cells.
     */
    public void testCreateListInTableCell()
    {
        insertTable();
        typeText("a");
        clickUnorderedListButton();
        typeEnter();
        typeText("b");
        assertWiki("|=(((*  a\n* b)))|= \n| | ");
    }

    /**
     * Test indenting a list fragment by selecting all the items and hitting the indent button.
     */
    public void testIndentListFragment()
    {
        setWikiContent("* one\n* two\n* three\n* three point one\n* three point two\n* three point three\n* four");
        select("XWE.body.firstChild.childNodes[3].firstChild", 0, "XWE.body.firstChild.childNodes[5].firstChild", 17);
        assertTrue(isIndentButtonEnabled());
        clickIndentButton();
        assertWiki("* one\n* two\n* three\n** three point one\n** three point two\n** three point three\n* four");
    }

    /**
     * Test the usual use case about only indenting the parent one level further, without its sublist. This cannot be
     * done in one because it is not a correct indent, from a semantic pov, but most users expect it to happen. The
     * correct steps are to indent the parent and then outindent the sublist, which is the case tested by this function.
     */
    public void testIndentParentWithNoSublist()
    {
        setWikiContent("* one\n* two\n* three\n** three point one\n** three point two\n** three point three\n* four");
        select("XWE.body.firstChild.childNodes[2].firstChild", 0, "XWE.body.firstChild.childNodes[2].firstChild", 5);
        assertTrue(isIndentButtonEnabled());
        clickIndentButton();
        select("XWE.body.firstChild.childNodes[1].childNodes[1].firstChild.childNodes[1].firstChild.firstChild", 0,
            "XWE.body.firstChild.childNodes[1].childNodes[1].firstChild.childNodes[1].childNodes[2].firstChild", 17);
        assertTrue(isOutdentButtonEnabled());
        clickOutdentButton();
        assertWiki("* one\n* two\n** three\n** three point one\n** three point two\n** three point three\n* four");
    }

    /**
     * Tests indenting two items, amongst which one with a sublist and then outending the item with the sublist.
     */
    public void testIndentItemWithSublistAndOutdent()
    {
        setWikiContent("* one\n* two\n* three\n** foo\n** bar\n* four\n* four\n* five");
        select("XWE.body.firstChild.childNodes[2].firstChild", 0, "XWE.body.firstChild.childNodes[3].firstChild", 4);
        assertTrue(isIndentButtonEnabled());
        clickIndentButton();
        assertWiki("* one\n* two\n** three\n*** foo\n*** bar\n** four\n* four\n* five");
        select("XWE.body.firstChild.childNodes[1].childNodes[1].firstChild.firstChild", 0,
            "XWE.body.firstChild.childNodes[1].childNodes[1].firstChild.childNodes[1].childNodes[1].firstChild", 3);
        assertTrue(isOutdentButtonEnabled());
        clickOutdentButton();
        assertWiki("* one\n* two\n* three\n** foo\n** bar\n** four\n* four\n* five");
    }

    /**
     * Tests a few indent and outdent operations on a list inside an embedded document (in this case, a table cell),
     * preceded by another list in the previous table cell.
     */
    public void testIndentOutdentInTableCell()
    {
        setWikiContent("|(((* item 1\n* item 2)))|(((* one\n** one plus one\n** one plus two\n* two\n* three)))\n| | ");
        select(
            "XWE.body.firstChild.firstChild.firstChild.childNodes[1].firstChild.firstChild.firstChild.childNodes[1]."
                + "childNodes[1].firstChild", 0,
            "XWE.body.firstChild.firstChild.firstChild.childNodes[1].firstChild.firstChild.childNodes[1]."
                + "firstChild", 3);
        assertTrue(isIndentButtonEnabled());
        clickIndentButton();
        assertWiki("|(((* item 1\n* item 2)))|(((* one\n** one plus one\n*** one plus two\n** two\n* three)))\n| | ");
        select(
            "XWE.body.firstChild.firstChild.firstChild.childNodes[1].firstChild.firstChild.firstChild.childNodes[1]."
                + "childNodes[1].firstChild", 0,
            "XWE.body.firstChild.firstChild.firstChild.childNodes[1].firstChild.firstChild.childNodes[1]."
                + "firstChild", 5);
        assertTrue(isOutdentButtonEnabled());
        clickOutdentButton();
        assertWiki("|(((* item 1\n* item 2)))|(((* one\n** one plus one\n*** one plus two\n* two\n\nthree)))\n| | ");
    }

    /**
     * Test that outdenting multiple list items on the first level of a list preserves distinct lines for the content of
     * the list items.
     * 
     * @see http://jira.xwiki.org/jira/browse/XWIKI-3921
     */
    public void testOutdentFirstLevelPreservesLines()
    {
        setWikiContent("* one\n* two\n* three\n** three plus one\n"
            + "* four\n* (((before\n* inner five\n* inner five + 1\n\nafter)))\n* six");
        select("XWE.body.firstChild.firstChild.firstChild", 0, "XWE.body.firstChild.childNodes[5].firstChild", 3);
        assertTrue(isOutdentButtonEnabled());
        clickOutdentButton();
        assertWiki("one\n\ntwo\n\nthree\n\n* three plus one\n\nfour\n\n"
            + "(((before\n\n* inner five\n* inner five + 1\n\nafter)))\n\nsix");
    }

    /**
     * @return {@code true} if the current selection is inside an ordered list, {@code false} otherwise
     */
    public boolean isOrderedListDetected()
    {
        return isToggleButtonDown("Ordered list");
    }

    /**
     * @return {@code true} if the current selection is inside an unordered list, {@code false} otherwise
     */
    public boolean isUnorderedListDetected()
    {
        return isToggleButtonDown("Unordered list");
    }
}
