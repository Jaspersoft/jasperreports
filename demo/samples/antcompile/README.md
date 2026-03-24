
# JasperReports - Ant Compile Sample <img src="../../resources/jasperreports.svg" alt="JasperReports logo" align="right"/>

Shows how multiple JRXML files can be compiled in batch mode using the ANT build tool.

### Main Features in This Sample

[Compiling Multiple Report Template Files Using the Ant Build Tool](#antcompile)\
[Generating the JRXML Source Files for Multiple Compiled Report Template Files Using the Ant Build Tool (Decompiling)](#antdecompile)

### Secondary Features

[Report Compilers](../groovy/README.md#reportcompilers)
				

## <a name='antcompile'>Compiling</a> Multiple Report Template Files Using the Ant Build Tool
<div align="right">Documented by <a href='mailto:teodord@users.sourceforge.net'>Teodor Danciu</a></div>


**Description / Goal**	\
How to compile all your JRXML report source files at application build time using the Ant build tool.

**Since:** 0.4.6

**Other Samples** \
[/demo/samples/groovy](../groovy/README.md)\
[/demo/samples/java1.5](../java1.5/README.md)\
[/demo/samples/javascript](../javascript/README.md)


The JRXML files represent the source files for static report templates. These report templates need to be prepared for use at runtime by compiling them into `*.jasper` files, which are basically serialized [JasperReport](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/engine/JasperReport.html) objects, ready for filling with data.

Report template source files having the `*.jrxml` file extensions are compiled into serialized object files having the `*.jasper` file extension, just like Java source files having the `*.java` file extension are transformed into Java bytecode binary files with the `*.class` file extension.
The transformation of `*.jrxml` files into `*.jasper` files should be part of the application build process, just as the compilation of `*.java` files into `*.class` files is. In the majority of cases, when the report templates are static and do not change at runtime (only data feed into them changes), there is no point in deploying source JRXML files with the application.
After all, when deploying a Java application, you deploy `*.class` files, packaged up in JARs, not source `*.java` files. The same technique is applicable to JR report template files, where compiled `*.jasper` files should be created at application built time and then deployed as part of the application classpath as resources.

The JasperReports library provides a built-in Ant task for compiling source JRXML report template files into `*.jasper` files. This task is represented by the [JRAntCompileTask](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/ant/JRAntCompileTask.html) task and works very similar to the Ant built-in [Javac](http://ant.apache.org/manual/Tasks/javac.html) task.

This task scans source folders and looks up for report template files (usually having the JRXML file extension) and compiles them into `*.jasper` files which are placed into a destination folder hierarchy.
The destination folder tree is similar to the source folder tree, meaning that the relative location of source files from the root source folder is preserved for the resulting `*.jasper ` files.

Similar to the `Javac` task, the source folders can be specified using either the `srcdir` attribute of the task (when there is only one root source folder) or using a nested `<src>` tag (when source files are scattered across multiple paths).

Open the `build.xml` file in the current sample and notice how the custom Ant task called `jrc` is defined:

```
<taskdef name="jrc" classname="net.sf.jasperreports.ant.JRAntCompileTask"> 
  <classpath refid="classpath"/>
</taskdef>
```

This task definition uses a `<classpath>` element which contains the JasperReports JAR and all its required dependencies.

Then, the custom `jrc` task is used to compile report templates having the `*.jrxml` file extension found under the sample's `reports` folder. This source folder is specified using the `srcdir` attribute of the `jrc` task in the `compile1` target of the `build.xml` file:

```
<target name="compile1" description="Compiles report designs specified using the &quot;srcdir&quot; in the &lt;jrc&gt; tag." depends="prepare-sample-classpath"> 
  <mkdir dir="./build/reports"/> 
  <taskdef name="jrc" classname="net.sf.jasperreports.ant.JRAntCompileTask"> 
    <classpath refid="sample-classpath"/>
  </taskdef>
  <jrc 
    srcdir="./reports"
    destdir="./build/reports"
    tempdir="./build/reports"
    keepjava="true"
    xmlvalidation="true"
    threads="1.5C">
  <classpath refid="sample-classpath"/>
  <include name="**/*.jrxml"/>
  </jrc>
</target>
```

The `compile2` target in the same build.xml file performs the same report compilation process, but the source folder is specified using a nested `<src>` tag with filesets. The nested source tag allows compiling report templates that are scattered through many different locations and are not grouped under a single root report source folder.

```
<target name="compile2" description="Compiles report designs specified using a &lt;fileset&gt; in the &lt;src&gt; tag." depends="prepare-sample-classpath">
  <mkdir dir="./build/reports"/> 
  <taskdef name="jrc" classname="net.sf.jasperreports.ant.JRAntCompileTask"> 
    <classpath refid="sample-classpath"/>
  </taskdef>
  <jrc 
      destdir="./build/reports"
      tempdir="./build/reports"
      keepjava="true"
      xmlvalidation="true"
      threads="4">
    <src>
      <fileset dir="./reports">
        <include name="**/*.jrxml"/>
      </fileset>
    </src>
    <classpath refid="sample-classpath"/>
  </jrc> 
</target>
```

Notice that both report compilation targets have a `<classpath>` nested element, used to specify the classpath used by the report compiler. This so called run-classpath contains classes that are referenced inside the report templates themselves, such as scriptlet classes, chart customizers and so forth.
In this particular sample, both source JRXML files make use of such helper classes found in the `src` folder of the samples. These helper classes have to be compiled before the report templates are compiled, using the `javac` target of the `build.xml` file.

In addition to the `srcdir` and the `destdir` attributes, the `jrc` custom Ant task shipped with JasperReports supports the following attributes:

- `compiler` : Name of the class that implements the [JRCompiler](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/engine/design/JRCompiler.html) interface to be used for compiling the reports (optional).
- `xmlvalidation` : Flag to indicate whether the XML validation should be performed on the source report template files (true by default).
- `tempdir` : Location to store the temporarily generated files (the current working directory by default).
- `keepjava` : Flag to indicate if the temporary Java files generated on the fly should be kept and not deleted automatically (false by default).
- `threads` : Number of threads to use for executing the task on multiple files in parallel. The value should be a positive integer or a float number representing a multiplier of the number of CPU cores, when followed by the letter C. For example, 2C means twice the number of CPU cores, while 0.5C means half the number of CPU cores (default is 1).


In our sample, we use the default report compiler, which is the JDT-based compiler, because the JDT JAR is found in the classpath. This compiler works on the assumption that report expressions are Java expressions and thus it produces a Java class file dynamically containing all the report expressions and compiles it using the JDT Java compiler. Normally, this report compiler does all the Java class file generation and compilation in-memory and does not work with actual files on disk, which makes it very flexible and easy to deploy in all environments. However, if the `keepjava` flag is turned to true, it will save the report's temporary Java source file on disk, in the specified tempdir. This is useful for debugging report expressions in certain cases.

Depending on the report expression language they are mapped to, other report compiler implementations might produce script files instead of Java files, for report expression evaluation. The `keepjava` and the `tempdir` attributes will still work for them, except that the files that will be kept into the temporary location will not be Java source files but script files, also helpful for debugging.

<div align="right"><a href='#top'>top</a></div>

---

## <a name='antdecompile'>Generating</a> the JRXML Source Files for Multiple Compiled Report Template Files Using the Ant Build Tool (Decompiling)
<div align="right">Documented by <a href='mailto:shertage@users.sourceforge.net'>Sanda Zaharia</a></div>

**Description / Goal**\
How to re-create the JRXML source files for multiple compiled report templates using the Ant build tool. This is useful in cases where only the compiled `*.jasper` files of older reports are available, the initial `*.jrxml `source files being lost.

**Since:** 3.7.1


In case the older reports JRXML templates are lost, but we still have access to their `*.jasper` compiled version, there is a possibility to retrieve the related JRXML, based on a specific built-in Ant task. This task provided by the JasperReports library is used for decompiling [JasperReport](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/engine/JasperReport.html) objects serialized as `*.jasper` files. Its functionality is defined in the [JRAntDecompileTask](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/ant/JRAntDecompileTask.html) class and works as opposite to the Ant built-in [JRAntCompileTask](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/ant/JRAntCompileTask.html) task described above.
This task scans source folders and looks up for `*.jasper` files, load them into in-memory [JasperReport](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/engine/JasperReport.html) objects, then write their report template source into corresponding `*.jrxml` files, placed into a destination folder hierarchy.

The destination folder tree is similar to the source folder tree, meaning that the relative location of `*.jasper` files from the root folder is preserved for the resulting `*.jrxml` files.
Similar to the [JRAntCompileTask](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/ant/JRAntCompileTask.html) task, the source folders can be specified using either the `srcdir` attribute of the task (when there is only one root source folder) or using a nested `<src>` tag (when source `*.jasper` files are scattered across multiple paths).
Below is the `decompile` target definition in the `build.xml` file, which uses the custom `jrdc` Ant task definition:

```
<target name="decompile" description="Decompiles report designs specified using a &lt;fileset&gt; in the &lt;src&gt; tag." depends="prepare-sample-classpath">
  <taskdef name="jrdc" classname="net.sf.jasperreports.ant.JRAntDecompileTask"> 
    <classpath refid="sample-classpath"/>
  </taskdef>
  <jrdc destdir="./build/reports" threads="4">
    <src>
      <fileset dir="./build/reports">
        <include name="**/*.jasper"/>
      </fileset>
    </src>
    <classpath refid="sample-classpath"/>
  </jrdc> 
</target>
```

### The JRXML Writer Tool

Once loaded from their serialized form, the JasperReport objects are passed one by one to the JRXML writer tool represented by the [JRXmlWriter](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/engine/xml/JRXmlWriter.html) class. The `decompile()` method is called to read the report template structure from the JasperReport object and write it down in a related JRXML file:

```
...
JasperReport jasperReport = (JasperReport)JRLoader.loadObjectFromFile(srcFileName);
        
new JRXmlWriter(jasperReportsContext).write(jasperReport, destFileName, "UTF-8");
...
```

The [JRXmlWriter](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/engine/xml/JRXmlWriter.html) tool provides dedicated methods for writing the report prologue, report properties, styles, datasets, sections and elements.

To see the effective result of the decompile task, just run the following command in the root directory of the sample:

```
> ant clean javac compile1 decompile
```

 You will see in the `build/reports/com/bar` and `build/reports/com/foo` output directories both the compiled `*.jasper` and the decompiled `*.jrxml` files.