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
package org.xwiki.test.ui.scheduler;

import junit.framework.Assert;

import org.junit.Test;
import org.xwiki.test.ui.framework.AbstractAdminAuthenticatedTest;
import org.xwiki.test.ui.framework.elements.ViewPage;
import org.xwiki.test.ui.scheduler.elements.SchedulerJobInlinePage;

/**
 * Tests Scheduler application features.
 * 
 * @since 2.3.1
 * @since 2.4M1
 * @version $Id$
 */
public class SchedulerTest extends AbstractAdminAuthenticatedTest
{
    /**
     * Tests that a scheduler job page default edit mode is "inline"
     */
    @Test
    public void testSchedulerJobDefaultEditMode()
    {
        getUtil().gotoPage("Scheduler", "WatchListDailyNotifier");

        ViewPage page = new ViewPage();
        Assert.assertTrue(page.exists());
        page.edit();

        SchedulerJobInlinePage inlineJob = new SchedulerJobInlinePage();
        // The edit sheet of scheduler jobs points to Quartz documentation.
        // Make sure this documentation is referenced to prove we are indeed in inline edit mode.
        Assert.assertTrue(inlineJob.isQuartzDocumentationReferenced());
    }
}
