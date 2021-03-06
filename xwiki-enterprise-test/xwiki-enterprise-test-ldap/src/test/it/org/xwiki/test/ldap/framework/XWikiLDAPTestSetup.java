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
package org.xwiki.test.ldap.framework;

import java.util.Properties;

import org.xwiki.test.integration.XWikiTestSetup;

import junit.framework.Test;

/**
 * JUnit TestSetup extension that modify xwiki.cfg file to use LDAP as authentication system. This class is meant to
 * wrap a JUnit TestSuite. For example:
 * 
 * <pre>
 * &lt;code&gt;
 * public static Test suite()
 * {
 *     // Create some TestSuite object here
 *     return new XWikiLDAPTestSetup(suite);
 * }
 * &lt;/code&gt;
 * </pre>
 * 
 * @version $Id$
 */
public class XWikiLDAPTestSetup extends XWikiTestSetup
{
    /**
     * The xwiki.cfg properties modified for the test.
     */
    public Properties currentXWikiConf;

    // ///

    /**
     * The default xwiki.cfg properties.
     */
    private Properties initialXWikiConf;

    /**
     * The log4j.properties properties.
     */
    private Properties logProperties;

    public XWikiLDAPTestSetup(Test test) throws Exception
    {
        super(test);

        // Prepare xwiki.cfg properties

        this.initialXWikiConf = getXWikiExecutor().loadXWikiCfg();
        this.currentXWikiConf = getXWikiExecutor().loadXWikiCfg();

        this.currentXWikiConf.setProperty("xwiki.authentication.ldap", "1");
        /*
         * CURRENTXWIKICONF.setProperty("xwiki.authentication.authclass",
         * "com.xpn.xwiki.user.impl.LDAP.XWikiLDAPAuthServiceImpl");
         */
        this.currentXWikiConf.setProperty("xwiki.authentication.ldap.server", LDAPTestSetup.LDAP_SERVER);
        this.currentXWikiConf.setProperty("xwiki.authentication.ldap.base_DN", LDAPTestSetup.LDAP_BASEDN);
        this.currentXWikiConf.setProperty("xwiki.authentication.ldap.bind_DN", LDAPTestSetup.LDAP_BINDDN_CN);
        this.currentXWikiConf.setProperty("xwiki.authentication.ldap.bind_pass", LDAPTestSetup.LDAP_BINDPASS_CN);
        this.currentXWikiConf.setProperty("xwiki.authentication.ldap.UID_attr", LDAPTestSetup.LDAP_USERUID_FIELD);
        this.currentXWikiConf.setProperty("xwiki.authentication.ldap.fields_mapping", "name="
            + LDAPTestSetup.LDAP_USERUID_FIELD + ",last_name=sn,first_name=givenname,fullname=description,email=mail");
        /*
         * CURRENTXWIKICONF.setProperty("xwiki.authentication.ldap.group_mapping", "XWiki.XWikiAdminGroup=cn=HMS
         * Lydia,ou=crews,ou=groups,o=sevenSeas");
         */
        this.currentXWikiConf.setProperty("xwiki.authentication.ldap.groupcache_expiration", "1");
        this.currentXWikiConf.setProperty("xwiki.authentication.ldap.user_group", LDAPTestSetup.HMSLYDIA_DN);
        this.currentXWikiConf.setProperty("xwiki.authentication.ldap.exclude_group", LDAPTestSetup.EXCLUSIONGROUP_DN);
        this.currentXWikiConf.setProperty("xwiki.authentication.ldap.validate_password", "0");
        this.currentXWikiConf.setProperty("xwiki.authentication.ldap.update_user", "1");
        this.currentXWikiConf.setProperty("xwiki.authentication.ldap.trylocal", "1");
        this.currentXWikiConf.setProperty("xwiki.authentication.ldap.mode_group_sync", "always");
        this.currentXWikiConf.setProperty("xwiki.authentication.ldap.ssl", "0");
        this.currentXWikiConf.setProperty("xwiki.authentication.ldap.ssl.keystore", "");

        // Prepare log4j.properties properties
        this.logProperties = new Properties();
        this.logProperties.setProperty("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
        this.logProperties.setProperty("log4j.appender.stdout.Target", "System.out");
        this.logProperties.setProperty("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
        this.logProperties.setProperty("log4j.appender.stdout.layout.ConversionPattern",
            "%d [%X{url}] [%t] %-5p %-30.30c{2} %x - %m %n");
        this.logProperties.setProperty("log4j.rootLogger", "warn, stdout");
        this.logProperties.setProperty("log4j.logger.com.xpn.xwiki.plugin.ldap", "trace");
        this.logProperties.setProperty("log4j.logger.com.xpn.xwiki.user.impl.LDAP", "trace");
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.test.XWikiTestSetup#setUp()
     */
    @Override
    protected void setUp() throws Exception
    {
        this.currentXWikiConf.setProperty("xwiki.authentication.ldap.port", "" + LDAPTestSetup.getLDAPPort());
        getXWikiExecutor().saveXWikiCfg(this.currentXWikiConf);

        getXWikiExecutor().saveLog4JProperties(this.logProperties);

        super.setUp();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.test.XWikiTestSetup#tearDown()
     */
    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();

        getXWikiExecutor().saveXWikiCfg(this.initialXWikiConf);
    }
}
