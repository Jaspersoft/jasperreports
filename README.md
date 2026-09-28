> [!IMPORTANT]
> _**JasperReports Library 7 released!**_
> 
> The version 7 of the JasperReports Library introduces
> major refactoring of the project, which is needed for the [Jakarta Migration](https://blogs.oracle.com/javamagazine/post/transition-from-java-ee-to-jakarta-ee).
> The changes help improving the dependency management by splitting the library into multiple optional artifacts (`*.jar` files) depending on
> the functionality they provide. Deprecated code has been removed and the backward compatibility of serialized/compiled `*.jasper` report template
> files has been deliberately broken.
> 
> More details about the changes can be found [here](https://github.com/Jaspersoft/jasperreports/tree/7.0.0?tab=readme-ov-file#jasperreports-library-700-change-log).

## JasperReports Library 7.0.0 Change Log
- removal of the Ant build system and replacing it with a Maven build system;
- deprecated code removed;
- breaking backward compatibility of serialized/compiled `*.jasper` report template files, mostly because of historical 
deprecated serialization code removal/cleanup mentioned above (source `*.jrxml` report templates need to be recompiled to `*.jasper` using the new version of the library);
- breaking backward compatibility of source `*.jrxml` report template files and `*.jrtx` style template files by replacing the [Apache Commons Digester](https://commons.apache.org/proper/commons-digester/) based parsers with [Jackson XML](https://github.com/FasterXML/jackson-dataformat-xml) object serialization. `*.jrxml` and `*.jrtx` files created with version 6 or older can no longer be loaded with version 7 or newer of the library alone. The conversion from the old file formats to the new file formats and back can be made using [Jaspersoft Studio 7](https://www.jaspersoft.com/products/jaspersoft-community) and later versions of it;
- extracting various optional extension JAR artifacts from the the core library JAR artifact to allow the [Jakarta Migration](https://blogs.oracle.com/javamagazine/post/transition-from-java-ee-to-jakarta-ee) of certain of these optional features while also
introducing better third party Maven dependency management of these artifacts;
- some Java package names have changed as a consequence of separating functionality into optional JAR artifacts;
- upgraded [JFreeChart](https://jfree.org/jfreechart/) to version 1.5.4 which no longer has support for 3D charts. Reports having Pie 3D, Bar 3D and Stacked Bar 3D charts would continue to work, but will be rendered as 2D, all their 3D effects being ignored; 

# JasperReports® - Free Java Reporting Library

The [**JasperReports Library**](https://community.jaspersoft.com/downloads/community-edition/) is the world's most popular open source reporting engine. 
It is entirely written in Java and it is able to use data coming from any kind of data source and 
produce pixel-perfect documents that can be viewed, printed or exported in a variety of document 
formats including HTML, PDF, Excel, OpenOffice, MS Word and other.

_**Documentation:**_

- [Samples Reference](https://jasperreports.sourceforge.net/sample.reference/README.html)
- [Configuration Reference](https://jasperreports.sourceforge.net/config.reference.html)
- [Functions Reference](https://jasperreports.sourceforge.net/function.reference.html)
- [FAQ](http://community.jaspersoft.com/wiki/jasperreports-library-faqs)
- [API (Javadoc)](https://jasperreports.sourceforge.net/api/index.html)
- [Discussion Forums](https://community.jaspersoft.com/forums/)

_**Older resources:**_

- [JasperReports Ultimate Guide](https://jasperreports.sourceforge.net/JasperReports-Ultimate-Guide-3.pdf) (version 3.0)
<br/>
<br/>

**Jaspersoft® Studio** - *report designer for the JasperReports Library*

The report templates for the *JasperReports Library* are XML files which can be edited using a powerful,
free to use, Eclipse-based report designer called [Jaspersoft Studio](https://community.jaspersoft.com/downloads/community-edition/).
Using *Jaspersoft Studio*, reports can be built out of any data source and can have their look and feel 
formatted for printing or on-screen reading, or can be deployed to a *JasperReports Server* instance, 
*JasperReports IO* repository or to a custom application using the *JasperReports Library* implementation
and exported to a wide range of output document formats.
<br/>
<br/>

**JasperReports Server** - *reporting and analytics server*

[JasperReports Server](https://www.jaspersoft.com/products/jaspersoft-commercial) is a stand-alone and embeddable 
reporting server. It provides reporting and analytics that can be embedded into a web or mobile application as well as 
operate as a central information hub for the enterprise by delivering mission critical information on a real-time or 
scheduled basis to the browser, mobile device, or email inbox in a variety of file formats. *JasperReports Server* is 
optimized to share, secure, and centrally manage your Jaspersoft reports and analytic views.
<br/>
<br/>

**JasperReports Web Studio** - *web-based version of the desktop Jaspersoft® Studio*

[JasperReports Web Studio](https://www.jaspersoft.com/products/jaspersoft-commercial) is a new web visual designer that 
creates and edits report templates for the *JasperReports® Library* reporting engine and the whole *Jaspersoft®* family of 
products that use the open-source library to produce dynamic content and rich data visualizations.
<br/>
<br/>

**JasperReports IO** - *reporting and data visualization in a world of cloud, microservices, and DevOps*

[JasperReports IO](https://www.jaspersoft.com/products/jaspersoft-commercial) is a RESTful reporting and data 
visualization service built on *JasperReports Library*, designed for generating reports and data visualizations in 
modern software architectures. Just as the *JasperReports Library* offers a `Java API` to leverage a powerful and high 
quality reporting engine inside Java applications, *JasperReports IO* offers a `REST API` to leverage the same reporting 
engine from any other software development platform.
<br/>
<br/>

## Building the JasperReports Library Project

The JasperReports Library project consists of one core JAR artifact and a number for optional extension JAR artifacts.

The build system relies entirely on the [Maven](https://maven.apache.org/) build tool.

Building the core JAR artifact and the optional extension JAR artifacts can be performed from root folder of the project
using the following command:

    mvn clean install source:jar javadoc:jar

The project has a separate artifact for tests under the `/tests`, which can be run using the following command:

    mvn clean test -f tests

The project documentation consists of general overview, configuration reference, samples reference, functions reference and the aggregated Javadoc.
It can be all generated using the following command:

    mvn clean compile -f docs

The generated documentation is to be found under the `/docs/target/docs` folder of the project.

## Running the samples

The project shows and documents many of its features using a set of samples which are themselves Maven projects that can be
run from command line. Some of these samples make use of a demo HSQLDB data base that needs to be started before the respective
reports are run.

Starting the HSQLDB demo database containing demo data is done using the following command in the `/demo/hsqldb` folder of the project:

    mvn exec:java

The samples are each in a separate subfolder in the /demo/samples folder of the project and can be run either individually or all
at once. To run an individual sample, the following command should be run in the respective sample folder:

    mvn clean compile exec:java
    
This `exec:java` Maven goal is calling the `test()` method of the sample application main class. Calling individual methods from the sample application main class can be performed
using a command like the following:

    mvn exec:java -Dexec.args=pdf

which calls the `pdf()` method of the sample application main class to export the reports to PDF. Multiple methods can be called in sequence like this:

    mvn exec:java -Dexec.args="pdf xls"
    
Viewing the source JRXML report design is done using the following command:

    mvn exec:java@viewDesign
    
The compiled `*.jasper` report template file can also be viewed using something like:

    mvn exec:java@viewDesign -Dexec.args=target/reports/I18nReport.jasper 
 
The generated `*.jrprint` report file can be viewed using either:

    mvn exec:java@view
    
or a more explicit command like:

    mvn exec:java@view -Dexec.args=target/reports/I18nReport.jrprint

which would also work for XML exported report files:

    mvn exec:java@view -Dexec.args=target/reports/I18nReport.jrpxml

Some samples use additional services that need to be started before the actual sample reports are run.
To make sure all required steps are performed in right order, an alternate way to run the sample is as follows:

    mvn clean compile exec:exec@all

This command is also the one that can be used to run all the samples at once, but it needs to be launched from the `/demo/samples` folder:

    mvn clean compile exec:exec@all

