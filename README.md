Utility for OUI
===============

Overview
--------

This library contains a utility class for *[OUI][1]*. OUI is short for
_Organizationally Unique Identifier_. It is a 24-bit number assigned to
organizations by IEEE Standards Association, [Registration Authority][2].


License
-------

  Apache License, Version 2.0


Maven
-----

```xml
<dependency>
    <groupId>com.neovisionaries</groupId>
    <artifactId>nv-oui</artifactId>
    <version>1.0</version>
</dependency>
```


Gradle
------

```Gradle
dependencies {
    compile 'com.neovisionaries:nv-oui:1.0'
}
```


OSGi
----

    Bundle-SymbolicName: com.neovisionaries.oui
    Export-Package: com.neovisionaries.oui;version="1.0.0"


Source Code
-----------

  <code>https://github.com/TakahikoKawasaki/nv-oui</code>


JavaDoc
-------

  <code>http://TakahikoKawasaki.github.io/nv-oui/</code>


Usage
-----

The current version of this library provides `Oui.getName` method only.
It is a method to look up an organization name by an OUI.

```java
// (1) "Apple, Inc." is returned.
String name = Oui.getName("00CDFE");

// (2) "Google, Inc." (case-insensitive)
Oui.getName("3c5ab4");

// (3) "Microsoft Corporation" (Semi-colons can be interleaved.)
Oui.getName("48:50:73");

// (4) "Facebook" (A 48-bit device address is okay.)
Oui.getName("48:57:dd:01:02:03");

// (5) "Amazon Technologies Inc." (Hyphens are okay, too.)
Oui.getName("F0-D2-F1");

// (6) "Oracle Corporation" (Only the first 6 hexadecimal letters have a meeting.)
Oui.getName("0010e0#XYZ")

// (7) "IBM Corp" (This is parsed successfully, too.)
Oui.getName("00:03-47@XYZ");
```


Data
----

The data `Oui` class refers to are written in `oui.properties` under
`src/main/resources/com/neovisionaries/oui` folder. The properties file
is generated by `bin/oui-csv-to-properties.rb` script. The script uses
`data/oui.csv` as input data. The CSV file can be downloaded from [here][3].

`oui.properties` should be periodically updated.


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
