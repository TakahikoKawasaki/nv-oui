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


import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Utility class for OUI (Organizationally Unique Identifier).
 *
 * @see <a href="http://standards.ieee.org/develop/regauth/index.html"
 *      >IEEE Standards Association, Registration Authority</a>
 *
 * @see <a href="https://standards.ieee.org/develop/regauth/oui/oui.csv"
 *      >MAC Address Block Large (MA-L) [csv]</a>
 *
 * @see <a href="https://standards.ieee.org/develop/regauth/oui/oui.txt"
 *      >MAC Address Block Large (MA-L) [txt]</a>
 *
 * @author Takahiko Kawasaki
 */
public class Oui
{
    /**
     * The pattern of OUIs given to {@link #getName(String)} method.
     */
    private static final Pattern OUI_PATTERN =
            Pattern.compile("^([0-9a-fA-F]{2})[:-]?([0-9a-fA-F]{2})[:-]?([0-9a-fA-F]{2}).*");


    private final Map<String, String> mData;


    /**
     * A constructor with OUI data in {@link Map} format.
     *
     * <p>
     * Keys in the data must be 6 upper-case hexadecimal letters that
     * represent OUIs. Values in the data must be organization names.
     * <p>
     *
     * <p>
     * A map returned from {@code parse} method of {@link OuiCsvParser}
     * class can be used as the parameter of this constructor.
     * </p>
     *
     * @param data
     *         OUI data.
     *
     * @since 1.1
     */
    public Oui(Map<String, String> data)
    {
        mData = data;
    }


    /**
     * Get the name of the organization which the OUI belongs to.
     *
     * <p>
     * All of the following are valid as input.
     * </p>
     *
     * <blockquote>
     *   <table cellpadding="5" border="1" style="border-collapse: collapse;">
     *     <tr>
     *       <td>{@code "00CDFE"}</td>
     *       <td>6 hexadecimal letters compose a 24-bit OUI.
     *     </tr>
     *     <tr>
     *       <td>{@code "3c5ab4"}</td>
     *       <td>Case-insensitive.</td>
     *     </tr>
     *     <tr>
     *       <td>{@code "48:50:73"}</td>
     *       <td>Semi-colons can be interleaved.</td>
     *     </tr>
     *     <tr>
     *       <td>{@code "48:57:dd:01:02:03"}</td>
     *       <td>A 48-bit device address is okay.</td>
     *     </tr>
     *     <tr>
     *       <td>{@code "F0-D2-F1"}</td>
     *       <td>Hyphens are okay, too.</td>
     *     </tr>
     *     <tr>
     *       <td>{@code "0010e0#XYZ"}</td>
     *       <td>Only the first 6 hexadecimal letters have a meaning.</td>
     *     </tr>
     *     <tr>
     *       <td>{@code "00:03-47@XYZ"}</td>
     *       <td>This is parsed successfully, too.</td>
     *     </tr>
     *   </table>
     * </blockquote>
     *
     * <p>
     * This method was a static method in version 1.0, but it is an instance
     * method since version 1.1.
     * </p>
     *
     * @param oui
     *         An OUI. If {@code null} is given, {@code null} is returned.
     *
     * @return
     *         An organization name. If the OUI not belong to any organization,
     *         {@code null} is returned.
     */
    public String getName(String oui)
    {
        if (oui == null || mData == null)
        {
            // Not found.
            return null;
        }

        Matcher matcher = OUI_PATTERN.matcher(oui);

        // If the given argument does not match the address pattern.
        if (matcher.matches() == false)
        {
            // Not found (invalid format).
            return null;
        }

        // Construct a 6-letter OUI.
        String number = String.format("%s%s%s",
            matcher.group(1).toUpperCase(),
            matcher.group(2).toUpperCase(),
            matcher.group(3).toUpperCase());

        // Look up.
        return mData.get(number);
    }


    /**
     * Get the name of the organization which the OUI belongs to.
     *
     * <p>
     * This method was a static method in version 1.0, but it is an instance
     * method since version 1.1.
     * </p>
     *
     * @param oui
     *         An OUI. The first 3 bytes (= 24-bit) in the byte array
     *         is regarded as an OUI. If {@code null} or a byte array
     *         whose length is less than 3 is given, {@code null} is
     *         returned.
     *
     * @return
     *         An organization name. If the OUI does not belong to any
     *         organization, {@code null} is returned.
     */
    public String getName(byte[] oui)
    {
        if (oui == null || oui.length < 3 || mData == null)
        {
            // Not found.
            return null;
        }

        // Construct a 6-letter OUI.
        String number = String.format("%02X%02X%02X", oui[0], oui[1], oui[2]);

        // Look up.
        return mData.get(number);
    }
}
