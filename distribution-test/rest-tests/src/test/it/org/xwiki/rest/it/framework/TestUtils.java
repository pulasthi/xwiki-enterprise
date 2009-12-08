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
package org.xwiki.rest.it.framework;

import org.apache.commons.httpclient.HttpMethod;

/**
 * @version $Id$
 */
public class TestUtils
{
    public static void printHttpMethodInfo(HttpMethod method) throws Exception
    {
        System.out.format("%s %s. Status: %d %s\n", method.getName(), method.getURI(), method.getStatusCode(), method
            .getStatusText());
    }

    public static void banner(String message)
    {
        banner(message, 80);
    }

    public static void banner(String message, int size)
    {
        System.out.println();

        for (int i = 0; i < size; i++) {
            System.out.print("*");
        }

        System.out.format("\n* %s ", message);

        for (int i = 0; i < (size - message.length() - 3); i++) {
            System.out.print("*");
        }

        System.out.println();

        for (int i = 0; i < size; i++) {
            System.out.print("*");
        }

        System.out.println();
    }
}