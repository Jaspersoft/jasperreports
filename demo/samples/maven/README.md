
# JasperReports - Maven Plugin Sample <img src="../../resources/jasperreports.svg" alt="JasperReports logo" align="right"/>

Shows how multiple report design files can be compiled, decompiled or updated in batch mode using the JasperReports Maven Plugin.

### Main Features in This Sample

[Compiling Multiple Report Template Files Using the JasperReports Maven Plugin](#compile)\
[Generating the JRXML Source Files for Multiple Compiled Report Template Files Using the JasperReports Maven Plugin (Decompiling)](#decompile)\
[Updating Multiple Report Template Files Using the JasperReports Maven Plugin](#update)\
[JasperReports Maven Plugin Usage](#usage)

### Secondary Features

[Report Compilers](../groovy/README.md#reportcompilers)
				

## <a name='compile'>Compiling</a> Multiple Report Template Files Using the JasperReports Maven Plugin
<div align="right">Documented by <a href='mailto:teodord@users.sourceforge.net'>Teodor Danciu</a></div>


**Description / Goal**	\
How to compile all your JRXML report source files at application build time using the JasperReports Maven Plugin.

**Since:** 7.0.6

**Other Samples** \
[/demo/samples/groovy](../groovy/README.md)\
[/demo/samples/java1.5](../java1.5/README.md)\
[/demo/samples/javascript](../javascript/README.md)


The JRXML files represent the source files for static report templates. These report templates need to be prepared for use at runtime by compiling them into `*.jasper` files, which are basically serialized [JasperReport](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/engine/JasperReport.html) objects, ready for filling with data.

Report template source files having the `*.jrxml` file extensions are compiled into serialized object files having the `*.jasper` file extension, just like Java source files having the `*.java` file extension are transformed into Java bytecode binary files with the `*.class` file extension.
The transformation of `*.jrxml` files into `*.jasper` files should be part of the application build process, just as the compilation of `*.java` files into `*.class` files is. In the majority of cases, when the report templates are static and do not change at runtime (only data feed into them changes), there is no point in deploying source JRXML files with the application.
After all, when deploying a Java application, you deploy `*.class` files, packaged up in JARs, not source `*.java` files. The same technique is applicable to JR report template files, where compiled `*.jasper` files should be created at application built time and then deployed as part of the application classpath as resources.

The JasperReports library provides an official Maven Plugin for compiling source `*.jrxml` report template files into `*.jasper` files.

This plugin can be declared inside a `pom.xml` file like this:

```
<plugin>
  <groupId>net.sf.jasperreports</groupId>
  <artifactId>jasperreports-maven-plugin</artifactId>
  <version>${jasperreports.version}</version>
</plugin>
```

This plugin scans resources folders and looks up for source report template `*.jrxml files` and compiles them into `*.jasper` files which are placed into the output folder hierarchy.
The output/target folder tree is similar to the source folder tree, meaning that the relative location of source files from the root source folder is preserved for the resulting `*.jasper ` files.

By default, the plugin uses the same JasperReports Library version as the plugin version. In order to use a different version of the JasperReport Library than the one represented by the plugin's own version, the `<dependencies>` section of the plugin needs to be configured as follows:

- for target versions 7.0.6 and later, the following dependency needs to be placed in the plugin configuration, which completely overrides the plugins's own JasperReports Library BOM:

```
<plugin>
  <groupId>net.sf.jasperreports</groupId>
  <artifactId>jasperreports-maven-plugin</artifactId>
  <version>${jasperreports.version}</version>
  <dependencies>
    <dependency>
      <groupId>net.sf.jasperreports</groupId>
      <artifactId>jasperreports-maven-plugin-bom</artifactId>
      <version>${desired.jasperreports.version}</version>
      <type>pom</type>
    </dependency>
  </dependencies>
</plugin>
```

- for versions older than 7.0.6, the plugin's own JasperReports Library BOM should first be inhibited, and in addition to that, the JasperReports Library artifacts of the desired version need to be added, depending on which extensions and custom component artifacts the source report templates make use of:

```
<plugin>
  <groupId>net.sf.jasperreports</groupId>
  <artifactId>jasperreports-maven-plugin</artifactId>
  <version>${jasperreports.version}</version>
  <dependencies>
    <dependency>
      <groupId>net.sf.jasperreports</groupId>
      <artifactId>jasperreports-maven-plugin-bom</artifactId>
      <version>${desired.jasperreports.version}</version>
      <type>pom</type>
      <exclusions>
        <exclusion>
          <groupId>*</groupId>
          <artifactId>*</artifactId>
        </exclusion>
      </exclusions>
    </dependency>
    <dependency>
      <groupId>net.sf.jasperreports</groupId>
      <artifactId>jasperreports</artifactId>
      <version>6.21.0</version>
      <scope>runtime</scope>
      <optional>false</optional>
    </dependency>
    ...
  </dependencies>
</plugin>
```

<div align="right"><a href='#top'>top</a></div>

---

## <a name='decompile'>Generating</a> the JRXML Source Files for Multiple Compiled Report Template Files Using the JasperReports Maven Plugin (Decompiling)
<div align="right">Documented by <a href='mailto:teodord@users.sourceforge.net'>Teodor Danciu</a></div>

**Description / Goal**\
How to re-create the JRXML source files for multiple compiled report templates using the JasperReports Maven Plugin. This is useful in cases where only the compiled `*.jasper` files of older reports are available, the initial `*.jrxml `source files being lost.

**Since:** 7.0.6


In case the older reports JRXML templates are lost, but we still have access to their `*.jasper` compiled version, there is a possibility to retrieve the related JRXML, based on a specific Maven plugin goal. This goal is used for decompiling [JasperReport](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/engine/JasperReport.html) objects serialized as `*.jasper` files and works as opposite to the report compilation goal described above.
This goal scans source folders and looks up for `*.jasper` files, load them into in-memory [JasperReport](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/engine/JasperReport.html) objects, then write their report template source into corresponding `*.jrxml` files, placed into a destination folder hierarchy.

The destination folder tree is similar to the source folder tree, meaning that the relative location of `*.jasper` files from the root folder is preserved for the resulting `*.jrxml` files.

Here is an example of how to configure the `decompile` Maven plugin goal:

```
<plugin>
  <groupId>net.sf.jasperreports</groupId>
  <artifactId>jasperreports-maven-plugin</artifactId>
  <version>${jasperreports.version}</version>
  <executions>
    <execution>
      <id>default-cli</id>
      <configuration>
        <sourceDirectoryDecompile>${project.build.directory}/reports</sourceDirectoryDecompile>
        <outputDirectoryDecompile>${project.build.directory}/reports-decompile</outputDirectoryDecompile>
      </configuration>
    </execution>
  </executions>
</plugin>
```

<div align="right"><a href='#top'>top</a></div>

---

## <a name='update'>Updating</a> Multiple Report Template Files Using the JasperReports Maven Plugin
<div align="right">Documented by <a href='mailto:teodord@users.sourceforge.net'>Teodor Danciu</a></div>

**Description / Goal**

How to update all your JRXML report source files using the JasperReports Maven Plugin, in order to perform the same modification on them or to bring them up-to-date with respect to the JasperReports Schema Reference.

**Since:** 7.0.6


A problem that may occur in applications with several report templates stored in the same resource repository is the amount of time required to modify each report template individually when the same change has to be applied for all reports. Manual modification is not the best practice in this case, the entire process should be done automatically.
Based on the fact that JRXML templates can be read into [JasperDesign](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/engine/design/JasperDesign.html) objects that can be easily modified and then saved again as JRXML templates, we only need to know where the initial JRXML templates are located, and which changes are to be applied, in order to process a bulk update on all these templates.

### Report Updaters

Changes can be very easily applied on report designs using specific [ReportUpdater](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/engine/util/ReportUpdater.html) implementations. Classes that inherit from the [ReportUpdater](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/engine/util/ReportUpdater.html) interface should implement the following method:

```
public JasperDesign update(JasperDesign jasperDesign)
```

in order to perform specific modifications on the [JasperDesign](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/engine/design/JasperDesign.html) object.


The JasperReports Maven Plugin has an `update` goal which allows performing such report template files update operation by accepting any number of report updaters to be added to its updaters list. Update operations will be applied sequential in the same order as the report updaters were added to the list. 

Here is an example of how to configure the `update` Maven plugin goal:

```
<plugin>
  <groupId>net.sf.jasperreports</groupId>
  <artifactId>jasperreports-maven-plugin</artifactId>
  <version>${jasperreports.version}</version>
  <executions>
    <execution>
      <id>default-cli</id>
      <configuration>
        <outputDirectoryUpdate>${project.build.directory}/reports-update</outputDirectoryUpdate>
        <updaters>
          <updater>com.update.RenewUuidsUpdater</updater>
          <updater>com.update.StyleUpdater</updater>
        </updaters>
      </configuration>
    </execution>
  </executions>
</plugin>
```

<div align="right"><a href='#top'>top</a></div>

---

## JasperReports Maven Plugin <a name='usage'>Usage</a>
<div align="right">Documented by <a href='mailto:teodord@users.sourceforge.net'>Teodor Danciu</a></div>

**Since:** 7.0.6


```
Name: JasperReports Maven Plugin
Description: Compile, decompile and update JasperReports report design files.
Group Id: net.sf.jasperreports
Artifact Id: jasperreports-maven-plugin
Goal Prefix: jasperreports

This plugin has 4 goals:

jasperreports:compile
  Description: Compiles JasperReports source report design *.jrxml files to
    compiled report design *.jasper files.
  Implementation: net.sf.jasperreports.maven.JasperReportsCompileMojo
  Language: java

  Available parameters:

    outputDirectory (Default: ${project.build.directory}/reports)
      The directory where the compiled report design *.jasper files will be
      generated.

    skip (Default: false)
      User property: jasperreports.compile.skip
      Flag to skip the reports compilation goal.

    sourceDirectory (Default:
    ${project.basedir}/src/main/reports)
      The directory where the source report design *.jrxml files are found.

    threads (Default: 1.5C)
      Sets the number of threads to use for executing the goal on multiple
      files in parallel. The value should be a positive integer representing
      the number of threads, or a float number representing a multiplier of the
      number of CPU cores, when followed by the letter C. For example, 2C means
      twice the number of CPU cores, while 0.5C means half the number of CPU
      cores. The default is 1.5C.

jasperreports:decompile
  Description: Decompiles JasperReports compiled report design *.jasper files
    to source report design *.jrxml files.
  Implementation: net.sf.jasperreports.maven.JasperReportsDecompileMojo
  Language: java

  Available parameters:

    outputDirectory
      Alias: outputDirectoryDecompile
      The directory where the source report design *.jrxml files will be
      generated.

    skip (Default: false)
      User property: jasperreports.decompile.skip
      Flag to skip the report decompilation goal.

    sourceDirectory
      Alias: sourceDirectoryDecompile
      The directory where the compiled report design *.jasper files are found.

    threads (Default: 1.5C)
      Sets the number of threads to use for executing the goal on multiple
      files in parallel. The value should be a positive integer representing
      the number of threads, or a float number representing a multiplier of the
      number of CPU cores, when followed by the letter C. For example, 2C means
      twice the number of CPU cores, while 0.5C means half the number of CPU
      cores. The default is 1.5C.

jasperreports:testCompile
  Description: Compiles JasperReports source test report design *.jrxml files
    to compiled report design *.jasper files.
  Implementation: net.sf.jasperreports.maven.JasperReportsTestCompileMojo
  Language: java

  Available parameters:

    outputDirectory (Default:
    ${project.build.directory}/test-reports)
      The directory where the compiled test report design *.jasper files will
      be generated.

    skip (Default: false)
      User property: jasperreports.testCompile.skip
      Flag to skip the test reports compilation goal.

    sourceDirectory (Default:
    ${project.basedir}/src/test/reports)
      The directory where the source test report design *.jrxml files are
      found.

    threads (Default: 1.5C)
      Sets the number of threads to use for executing the goal on multiple
      files in parallel. The value should be a positive integer representing
      the number of threads, or a float number representing a multiplier of the
      number of CPU cores, when followed by the letter C. For example, 2C means
      twice the number of CPU cores, while 0.5C means half the number of CPU
      cores. The default is 1.5C.

jasperreports:update
  Description: Updates JasperReports source report design *.jrxml files.
  Implementation: net.sf.jasperreports.maven.JasperReportsUpdateMojo
  Language: java

  Available parameters:

    outputDirectory (Default: ${project.build.directory}/reports)
      Alias: outputDirectoryUpdate
      The directory where the updated report design *.jrxml files will be
      generated.

    skip (Default: false)
      User property: jasperreports.update.skip
      Flag to skip the report update goal.

    sourceDirectory (Default:
    ${project.basedir}/src/main/reports)
      Alias: sourceDirectoryUpdate
      The directory where the source report design *.jrxml files are found.

    threads (Default: 1.5C)
      Sets the number of threads to use for executing the goal on multiple
      files in parallel. The value should be a positive integer representing
      the number of threads, or a float number representing a multiplier of the
      number of CPU cores, when followed by the letter C. For example, 2C means
      twice the number of CPU cores, while 0.5C means half the number of CPU
      cores. The default is 1.5C.

    updaters
      List of classes implementing the
      net.sf.jasperreports.engine.util.ReportUpdater interface to be called to
      update each report design file.
```
