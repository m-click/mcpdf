# Mcpdf

Mcpdf is an alternative to PDFtk with fixed unicode issues, so you can write Łódź into your forms.

It implements a small subset of PDFtk for which it implements compatible command line interface, so it can be used as a drop-in replacement for this subset of commands.

Internally it uses the [iText PDF library](http://itextpdf.com/product/itext).

## Getting Started

Make sure you have installed a Java Runtime Environment such as [OpenJDK](http://openjdk.java.net/).

[Download](https://oss.sonatype.org/content/repositories/releases/aero/m-click/mcpdf/0.2.10/mcpdf-0.2.10-jar-with-dependencies.jar) the latest standalone version of Mcpdf (jar-with-dependencies).

For the sake of brevity, we will refer to this file as `mcpdf.jar`. So you should either rename the file or create a symlink:

    ln -s mcpdf-0.2.10-jar-with-dependencies.jar mcpdf.jar

Test it via:

    java -jar mcpdf.jar

## Usage

For the implemented subset of commands (see below), Mcpdf can be used as a drop-in replacement for PDFtk, taking a [PDFtk command](http://www.pdflabs.com/docs/pdftk-man-page/) and replace `pdftk` with `java -jar mcpdf.jar`.

[Please create an issue tracker entry](https://github.com/m-click/mcpdf/issues) if you would like to add more commands.

### Fill in form data

Fill in form data from `DATA.xfdf` into `FORM.pdf` and write the result to `OUTPUT.pdf`:

    java -jar mcpdf.jar FORM.pdf fill_form - output - < DATA.xfdf > OUTPUT.pdf

Fill in form data and flatten the document to prevent further editing:

    java -jar mcpdf.jar FORM.pdf fill_form - output - flatten < DATA.xfdf > OUTPUT.pdf

### Add stamp and/or background

Use `BACKGROUND.pdf` as a background for `INPUT.pdf` and output to `OUTPUT.pdf`:

    java -jar mcpdf.jar INPUT.pdf background BACKGROUND.pdf output - > OUTPUT.pdf

Similarly, you can use the stamp functionality to add `STAMP.pdf` as a stamp (i.e. foreground) for `INPUT.pdf` and output into `OUTPUT.pdf`. This works just like background, except that it places `STAMP.pdf` in front of `INPUT.pdf` instead of behind it:

    java -jar mcpdf.jar INPUT.pdf stamp STAMP.pdf output - > OUTPUT.pdf

## Advanced Topics

### Combine with Another Library Version

[Download](https://oss.sonatype.org/content/repositories/releases/aero/m-click/mcpdf/0.2.10/mcpdf-0.2.10.jar) the plain JAR file (`mcpdf-0.2.10.jar`) instead of the jar-with-dependencies.

[Download](https://search.maven.org/#search|gav|1|g%3A%22com.itextpdf%22%20AND%20a%3A%22itextpdf%22) the version of the iText PDF library you want to use (`itextpdf-X.Y.Z.jar`).

Run both in combination:

    java -cp itextpdf-X.Y.Z.jar:mcpdf-0.2.10.jar aero.m_click.mcpdf.Main

### Build from Source

Make sure you have installed [Maven](https://maven.apache.org/) and [Git](http://git-scm.com/).

Download the latest Mcpdf source and change into that folder:

    git clone https://github.com/m-click/mcpdf.git
    cd mcpdf

Build the plain JAR file as well as the jar-with-dependencies:

    mvn package

This will download the correct version of the iText PDF library and all required Maven modules automatically.

You will find the JAR files in the `target` subfolder.

### Deploy Releases

Mcpdf uses the [Sonatype OSSRH](https://docs.sonatype.org/display/Repository/Sonatype%2BOSS%2BMaven%2BRepository%2BUsage%2BGuide) (OSS Repository Hosting Service). The project creation ticket was [OSSRH-8759](https://issues.sonatype.org/browse/OSSRH-8759).

Create `~/.m2/settings.xml`:

    <settings>
      <servers>
        <server>
          <id>sonatype-nexus-snapshots</id>
          <username>...</username>
          <password>...</password>
        </server>
        <server>
          <id>sonatype-nexus-staging</id>
          <username>...</username>
          <password>...</password>
        </server>
      </servers>
    </settings>

Prepare documentation:

    VERSION=`sed -n 's/^  <version>\([^-]*\).*/\1/p' pom.xml`
    sed -i "s,mcpdf\([-/]\)[0-9.]*[0-9],mcpdf\1$VERSION,g" README.md
    git commit -m "Prepare documentation for release mcpdf-$VERSION" README.md

Create a new release and upload it to the OSSRH staging area:

    mvn release:clean release:prepare release:perform && git push

In case this fails with an error message stating that `javadoc` cannot be found, try setting `JAVA_HOME`:

    export JAVA_HOME=/usr && mvn release:clean release:prepare release:perform && git push

If the automatic deployment failed:

  * Open https://oss.sonatype.org/
    * Select `Staging Repositories`
    * Click `Refresh` a few times as needed
    * If it failed to be closed automatically, click `Close`, then `Confirm`
    * Click `Refresh` a few times as needed
    * If it failed to be released automatically, click `Release`, then `Confirm`

Watch it appear at the Central Repository:

  * https://repo1.maven.org/maven2/aero/m-click/mcpdf/
  * https://search.maven.org/artifact/aero.m-click/mcpdf
  * https://search.maven.org/#search|gav|1|g%3A%22aero.m-click%22%20AND%20a%3A%22mcpdf%22

## License
Affero GPL v3

Copyright (C) 2014  Volker Grabsch <grabsch@m-click.aero>
