# Growing Object Oriented Software Guided by Tests in Kotlin

Kotlin implementation of the Auction Sniper application from 
[Growing Object Oriented Software Guided by Tests](http://growing-object-oriented-software.com).

Examples from `Part V: Adcanced Topics` are in a [separate](https://github.com/dehasi/goos-part5) repo. 

## Technologies

The book was originally published in 2009. New technologies have appeared.

The replacements are listed below:

| Original    | Replacement    | Comment    |
|:---:|:---:|:---:|
| Java    | Kotlin    | Runs on JVM 14    |
| Ant   | Gradle    | Build tool  |
| Swing    | [TornadoFX](https://github.com/edvin/tornadofx)    | GUI framework    |
| [WindowLicker](https://wiki.c2.com/?WindowLicker)    | [TestFX](https://github.com/TestFX/TestFX)    | GUI test framework     |
| [JUnit 4](https://junit.org/junit4/)     | [JUnit 5](https://junit.org/junit5/)    | Unit-testing framework    |
| Hamcrest     | AssertJ    | Assertions library    |
| [jMock](http://jmock.org/)     | [MockK](https://mockk.io/)    | Mocking library    |
| [Smack](https://www.igniterealtime.org/projects/smack/)    |     | XMPP Client   |
| [Openfire](https://www.igniterealtime.org/projects/openfire/)    |     | XMPP Server   |
| Local Openfire setup   | Docker compose    |   |

The book assumes that XMPP server is already installed
and all  users are created on the server.
For the beginning I'll use a Docker container from [beatrichartz](https://github.com/beatrichartz/growing-object-oriented-software-guided-by-tests-kotlin/blob/master/docker-compose.yml).

## See also
During implementation, I was looking at GOOS implementations from
- [beatrichartz](https://github.com/beatrichartz/growing-object-oriented-software-guided-by-tests-kotlin)
- [rhaendel](https://github.com/rhaendel/goos)

### Attention

I'm not a professional Kotlin developer. 
If you can rewrite it in more kotlin-idiomatic style pull requests are welcome!
