Utility for OUI
===============

Overview
--------

This library contains a utility class for **[OUI][1]**. OUI is short for
_Organizationally Unique Identifier_. It is a 24-bit number assigned to
organizations by _IEEE Standards Association, [Registration Authority][2]_.


License
-------

  Apache License, Version 2.0


Maven
-----

```xml
<dependency>
    <groupId>com.neovisionaries</groupId>
    <artifactId>nv-oui</artifactId>
    <version>1.1</version>
</dependency>
```


Gradle
------

```Gradle
dependencies {
    compile 'com.neovisionaries:nv-oui:1.1'
}
```


OSGi
----

    Bundle-SymbolicName: com.neovisionaries.oui
    Export-Package: com.neovisionaries.oui;version="1.1.0"


Source Code
-----------

  <code>https://github.com/TakahikoKawasaki/nv-oui</code>


JavaDoc
-------

  <code>http://TakahikoKawasaki.github.io/nv-oui/</code>


Usage
-----

```java
// Load an 'oui.csv' file.
Oui oui = new Oui(new OuiCsvParser().parse(new URL("file:data/oui.csv")));

// (1) Look up by a 6-hexadecimal OUI. ("Apple, Inc." is returned.)
String name = oui.getName("00CDFE");

// (2) Case-insensitive. ("Google, Inc.")
oui.getName("3c5ab4");

// (3) Semi-colons can be interleaved. ("Microsoft Corporation")
oui.getName("48:50:73");

// (4) A 48-bit device address is okay. ("Facebook")
oui.getName("48:57:dd:01:02:03");

// (5) Hyphens are okay, too. ("Amazon Technologies Inc.")
oui.getName("F0-D2-F1");

// (6) Only the first 6 hexadecimal letters have a meeting. ("Oracle Corporation")
oui.getName("0010e0#XYZ")

// (7) This is parsed successfully, too. ("Intel Corporation")
oui.getName("00:03-47@XYZ");

// (8) Look up by a byte array. ("IBM Corp")
byte[] data = { 0x00, 0x04, (byte)0xAC };
oui.getName(data);
```


Links
-----

- [IEEE Standards Association, Registration Authority][2]
- [oui.csv][3]


Author
------

[Authlete, Inc.][4] & Neo Visionaries Inc.<br/>
Takahiko Kawasaki &lt;taka@authlete.com&gt;


[1]: https://en.wikipedia.org/wiki/Organizationally_unique_identifier
[2]: http://standards.ieee.org/develop/regauth/index.html
[3]: https://standards.ieee.org/develop/regauth/oui/oui.csv
[4]: https://www.authlete.com/
