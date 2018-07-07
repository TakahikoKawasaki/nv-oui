/*
 * Copyright (C) 2016 Neo Visionaries Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package com.neovisionaries.oui;


import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.net.URL;
import org.junit.Test;


public class OuiTest
{
    private static final Oui OUI = loadOui("file:data/oui.csv");


    private static Oui loadOui(String csv)
    {
        try
        {
            return new Oui(new OuiCsvParser().parse(new URL(csv)));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }


    private static void doTest(String address, String expectedName)
    {
        assertEquals(expectedName, OUI.getName(address));
    }


    private static void doTest(byte[] address, String expectedName)
    {
        assertEquals(expectedName, OUI.getName(address));
    }


    @Test
    public void test01()
    {
        doTest("00CDFE", "Apple, Inc.");
    }


    @Test
    public void test02()
    {
        doTest("3c5ab4", "Google, Inc.");
    }


    @Test
    public void test03()
    {
        doTest("48:50:73", "Microsoft Corporation");
    }


    @Test
    public void test04()
    {
        doTest("48:57:dd:01:02:03", "Facebook Inc");
    }


    @Test
    public void test05()
    {
        doTest("F0-D2-F1","Amazon Technologies Inc.");
    }


    @Test
    public void test06()
    {
        doTest("0010e0#XYZ", "Oracle Corporation");
    }


    @Test
    public void test07()
    {
        doTest("00:03-47@XYZ", "Intel Corporation");
    }


    @Test
    public void test08()
    {
        doTest("000B38", "Kn\u00FCrr GmbH");
    }


    @Test
    public void test09()
    {
        doTest("001EFC", "JSC \"MASSA-K\"");
    }


    @Test
    public void test10()
    {
        byte[] address = { 0x00, 0x04, (byte)0xAC };
        doTest(address, "IBM Corp");
    }
}
