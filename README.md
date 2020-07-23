![CI](https://github.com/antlr/Antlr4Formatter/workflows/CI/badge.svg)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/81751584621541f39dd49aec48e79a7f)](https://www.codacy.com/app/teverett/Antlr4Formatter?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=teverett/Antlr4Formatter&amp;utm_campaign=Badge_Grade)

# Antlr4Formatter

## Building

The project uses [maven wrapper](https://github.com/takari/maven-wrapper), so it's easy to build the project without worrying about having the right Maven version installed locally.

To build `antlr4-formatter` and the standalone utility `antlr4-formatter-standalone` run:

```sh
./mvnw clean package
```

## Using the standalone formatter

To format a grammar use the script `formatFile.sh` passing the grammar file as an argument to the script.  You can also use it with the Spotless plugin in [gradle](https://github.com/diffplug/spotless/tree/main/plugin-gradle#antlr4formatter) or [maven](https://github.com/diffplug/spotless/tree/main/plugin-maven#antlr4formatter).

## Maven Coordinates

```xml
<groupId>com.khubla.antlr4formatter</groupId>
<artifactId>antlr4-formatter</artifactId>
<version>1.2.1</version>
<packaging>jar</packaging>
```

## Contributing

Contributions are welcome. To shorten the process of reviewing the pull request be sure that the code conforms to the projects code style. Before committing fix the code format by running [spotless](https://github.com/diffplug/spotless):

```sh
./mvnw spotless:apply
``` 
