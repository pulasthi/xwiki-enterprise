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
package org.xwiki.test.cluster;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.junit.runner.RunWith;
import org.xwiki.test.integration.XWikiExecutor;
import org.xwiki.test.integration.XWikiExecutorSuite;

/**
 * Runs all functional tests found in the classpath and start/stop XWiki before/after the tests (only once).
 *
 * @version $Id$
 */
@RunWith(XWikiExecutorSuite.class)
@XWikiExecutorSuite.Executors(2)
public class AllTests
{
    private static final String WEBINF_PATH = "/observation/remote/jgroups";

    @XWikiExecutorSuite.Initialized
    public void initialize(List<XWikiExecutor> executors)
    {
        try {
            initChannel(executors.get(0), "tcp1");
            initChannel(executors.get(1), "tcp2");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Cluster Channels", e);
        }
    }

    private void initChannel(XWikiExecutor executor, String channelName) throws Exception
    {
        Properties properties = executor.loadXWikiProperties();
        properties.setProperty("observation.remote.enabled", "true");
        properties.setProperty("observation.remote.channels", channelName);
        executor.saveXWikiProperties(properties);

        Properties log4JProperties = executor.loadLog4JProperties();
        log4JProperties.setProperty("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
        log4JProperties.setProperty("log4j.appender.stdout.Target", "System.out");
        log4JProperties.setProperty("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
        log4JProperties.setProperty("log4j.appender.stdout.layout.ConversionPattern",
            "%d [%X{url}] [%t] %-5p %-30.30c{2} %x - %m %n");
        log4JProperties.setProperty("log4j.rootLogger", "warn, stdout");
        log4JProperties.setProperty("log4j.logger.org.xwiki.observation.remote", "debug");
        log4JProperties.setProperty("log4j.logger.com.xpn.xwiki.internal", "debug");
        executor.saveLog4JProperties(log4JProperties);

        String filename = channelName + ".xml";

        InputStream is = getClass().getResourceAsStream("/" + filename);
        try {
            FileOutputStream fos = new FileOutputStream(executor.getWebInfDirectory() + WEBINF_PATH + "/" + filename);

            byte[] buffer = new byte[1024];

            for (int nb; (nb = is.read(buffer)) > 0;) {
                fos.write(buffer, 0, nb);
            }
        } finally {
            is.close();
        }
    }
}
