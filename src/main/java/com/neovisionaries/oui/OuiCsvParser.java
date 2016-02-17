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


import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A parser for a {@code oui.csv} file.
 *
 * @see <a href="https://standards.ieee.org/develop/regauth/oui/oui.csv"
 *      >MAC Address Block Large (MA-L) [csv]</a>
 *
 * @since 1.1
 *
 * @author Takahiko Kawasaki
 */
public class OuiCsvParser
{
    private static final char DOUBLE_QUOTE = '"';
    private static final char COMMA = ',';
    private static final Pattern DATA_LINE_PATTERN = Pattern.compile("^MA-L,([0-9A-F]{6}),(.+)$");


    public SortedMap<String, String> parse(URL source) throws IOException
    {
        checkNull("source", source);

        InputStream in = null;

        try
        {
            return parse(in = source.openStream());
        }
        finally
        {
            closeSilently(in);
        }
    }


    public SortedMap<String, String> parse(InputStream source) throws IOException
    {
        checkNull("source", source);

        return parse(new BufferedReader(new InputStreamReader(source, "UTF-8")));
    }


    public SortedMap<String, String> parse(Reader source) throws IOException
    {
        checkNull("source", source);

        if (source instanceof BufferedReader)
        {
            return parse((BufferedReader)source);
        }
        else
        {
            // Wrap the reader with BufferedReader.
            return parse(new BufferedReader(source));
        }
    }


    public SortedMap<String, String> parse(BufferedReader source) throws IOException
    {
        checkNull("source", source);

        SortedMap<String, String> data = new TreeMap<String, String>();
        String[] lineData = new String[2];

        while (true)
        {
            // Read one line from the input stream.
            String line = source.readLine();

            // If the end of the stream has been reached.
            if (line == null)
            {
                break;
            }

            // Parse the line. Then, if it was parsed successfully.
            if (parseLine(line, lineData))
            {
                data.put(lineData[0], lineData[1]);
            }
        }

        return data;
    }


    private static void checkNull(String name, Object object)
    {
        if (object == null)
        {
            throw new IllegalArgumentException(String.format("'%s' is null.", name));
        }
    }


    private static void closeSilently(Closeable closeable)
    {
        if (closeable == null)
        {
            return;
        }

        try
        {
            closeable.close();
        }
        catch (IOException e)
        {
        }
    }


    private static boolean parseLine(String line, String[] lineData)
    {
        Matcher matcher = DATA_LINE_PATTERN.matcher(line);

        if (matcher.matches() == false)
        {
            return false;
        }

        lineData[0] = matcher.group(1);
        lineData[1] = extractField(matcher.group(2)).trim();

        return true;
    }


    private static String extractField(String input)
    {
        boolean quoted = (countLeadingChar(input, DOUBLE_QUOTE) % 2) == 1;
        int index = quoted ? 1 : 0;
        int len = input.length();

        StringBuilder output = new StringBuilder();

        for ( ; index < len; ++index)
        {
            char ch = input.charAt(index);

            if (ch == DOUBLE_QUOTE)
            {
                if ((index + 1 < len) && input.charAt(index + 1) == DOUBLE_QUOTE)
                {
                    output.append(DOUBLE_QUOTE);
                    ++index;
                    continue;
                }

                break;
            }

            if ((ch == COMMA) && (quoted == false))
            {
                break;
            }

            output.append(ch);
        }

        return output.toString();
    }


    private static int countLeadingChar(String line, char ch)
    {
        int len = line.length();

        for (int i = 0; i < len; ++i)
        {
            if (line.charAt(i) != ch)
            {
                return i;
            }
        }

        return len;
    }
}
