# Growing Object Oriented Software Guided by Tests in Kotlin

This is a follow-along Kotlin implementation of the Auction Sniper described in the book 
[Growing Object Oriented Software Guided by Tests](http://growing-object-oriented-software.com).



## Technologies

The book was originally published in 2009. Some technologies are not actual anymore.
The replacements are listed below

| Original    | Replacement    | Comment    |
|:---:|:---:|:---:|
| Java    | Kotlin    | Runs on JVM 14    |
| Swing    | [TornadoFx](https://github.com/edvin/tornadofx)    | GUI framework    |
| [WindowLicker](https://wiki.c2.com/?WindowLicker)    | [TestFX](https://github.com/TestFX/TestFX)    | GUI test framework     |
| JUnit 4     | JUnit 5    | Wellknown unit test framework    |
| [Smack](https://www.igniterealtime.org/projects/smack/)    |     | XMPP Client   |
| [Openfire](https://www.igniterealtime.org/projects/openfire/)    |     | XMPP Server   |

The book assumes that XMPP server is already installed. 
For the beginning I'll use a Docker container from [beatrichartz](https://github.com/beatrichartz/growing-object-oriented-software-guided-by-tests-kotlin/blob/master/docker-compose.yml).
Also, all users should be created in the server.

## See also
During implementation, I was looking at GOOS implemenations from
- [beatrichartz](https://github.com/beatrichartz/growing-object-oriented-software-guided-by-tests-kotlin)
- [rhaendel]([]()https://github.com/rhaendel/goos)
