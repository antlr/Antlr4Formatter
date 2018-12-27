[![Travis](https://api.travis-ci.org/antlr/Antlr4Formatter.png)](https://travis-ci.org/antlr/Antlr4Formatter)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/81751584621541f39dd49aec48e79a7f)](https://www.codacy.com/app/teverett/Antlr4Formatter?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=teverett/Antlr4Formatter&amp;utm_campaign=Badge_Grade)

# Antlr4Formatter

Building
====
The project uses [maven wrapper](https://github.com/takari/maven-wrapper), so it's easy to build the project without worring about having the right Maven version installed locally.

To build `antlr4-formatter` and the standalone utility `antlr4-formatter-standalone` run:

```bash
./mvnw clean package
```

Using the standalone formatter
====

To format a grammar use the script `formatFile.sh` passing the grammar file as an argument to the script.

Maven Coordinates
====

```
<groupId>com.khubla.antlr4formatter</groupId>
<artifactId>antlr4-formatter</artifactId>
<version>1.1.0</version>
<packaging>jar</packaging>
```


