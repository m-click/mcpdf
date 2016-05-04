# Mcpdf

Mcpdf fixes PDFtk's unicode issues, so you can write Łódź into your forms. Mcpdf aims to become a drop-in replacement for PDFtk.

It is essentially a command line interface to the [iText PDF library](http://itextpdf.com/product/itext) with a PDFtk compatible syntax.

## Getting Started

Make sure you have installed a Java Runtime Environment such as [OpenJDK](http://openjdk.java.net/).

[Download](https://oss.sonatype.org/content/repositories/releases/aero/m-click/mcpdf/0.2.3/mcpdf-0.2.3-jar-with-dependencies.jar) the latest standalone version of Mcpdf (jar-with-dependencies).

For the sake of brevity, we will refer to this file as `mcpdf.jar`. So you should either rename the file or create a symlink:

    ln -s mcpdf-0.2.3-jar-with-dependencies.jar mcpdf.jar

Test it via:

    java -jar mcpdf.jar

## Usage

The goal of Mcpdf is to become a drop-in replacement for PDFtk, so you could take any working [PDFtk command](http://www.pdflabs.com/docs/pdftk-man-page/) and replace `pdftk` with `java -jar mcpdf.jar`. That's all there is to it.

Note that not all PDFtk operations are implemented at the moment. Just the following subset is supported.

[Please create an issue tracker entry](https://github.com/m-click/mcpdf/issues) if you see something missing that you need. Don't forget to provide the exact PDFtk command that you would like to see in Mcpdf.

### Fill in form data

Fill in form data from `DATA.xfdf` into `FORM.pdf` and write the result to `RESULT.pdf`:

    java -jar mcpdf.jar FORM.pdf fill_form - output - < DATA.xfdf > RESULT.pdf

Fill in form data and flatten the document to prevent further editing:

    java -jar mcpdf.jar FORM.pdf fill_form - output - flatten < DATA.xfdf > RESULT.pdf

### Add a stamp or background to a PDF file.

Use `BACKGROUND.pdf` as a background for `INPUT.pdf` and output to `RESULT.pdf`:

    java -jar mcpdf.jar BACKGROUND.pdf background INPUT.pdf output - > RESULT.pdf

Similarly, you can use the stamp functionality to add `STAMP.pdf` as a stamp, or foreground, for `INPUT.pdf` and output into `RESULT.pdf`. This works just like background, except that it places `DRAFT.pdf` in front of `INPUT.pdf` instead of behind it:

    java -jar mcpdf.jar STAMP.pdf stamp INPUT.pdf output - > RESULT.pdf

## Advanced Topics

### Combine with Another Library Version

[Download](https://oss.sonatype.org/content/repositories/releases/aero/m-click/mcpdf/0.2.3/mcpdf-0.2.3.jar) the plain JAR file (`mcpdf-0.2.3.jar`) instead of the jar-with-dependencies.

[Download](https://search.maven.org/#search|gav|1|g%3A%22com.itextpdf%22%20AND%20a%3A%22itextpdf%22) the version of the iText PDF library you want to use (`itextpdf-X.Y.Z.jar`).

Run both in combination:

    java -cp itextpdf-X.Y.Z.jar:mcpdf-0.2.3.jar aero.m_click.mcpdf.Main

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
    sed -i "s,mcpdf\([-/]\)[0-9.]*[0-9],mcpdf\1$VERSION,g" README.org
    git commit -m "Prepare documentation for release mcpdf-$VERSION" README.org

Create a new release and upload it to the OSSRH staging area:

    mvn release:clean release:prepare release:perform

[Follow the OSSRH release instructions](http://central.sonatype.org/pages/releasing-the-deployment.html][Follow the OSSRH release instructions), that is:

1. Open https://oss.sonatype.org/
2. Login
3. Select `Staging Repositories`
4. Select topmost item
5. Click `Close` and `Confirm`
6. Click `Refresh` a few times
7. Click `Release` and `Confirm`
8. [Watch it appear at the Central Repository](https://search.maven.org/#search|gav|1|g%3A%22aero.m-click%22%20AND%20a%3A%22mcpdf%22)

Push to GitHub:

    git push
