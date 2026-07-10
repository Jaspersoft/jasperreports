<a name="osgi"/>

# OSGi Sample

This sample demonstrates running JasperReports Library inside an OSGi container (Apache Felix).
It starts an embedded Felix framework, installs JasperReports and its dependencies as OSGi bundles,
then fills and exports several reports to PDF and XML.

## Prerequisites

- JDK 1.8 or later
- Maven

## Building and Running

First, build the JasperReports Library from the project root:

```
mvn clean install -Dmaven.buildNumber.doCheck=false
```

Then run the sample:

```
cd demo/samples/osgi
mvn clean compile exec:java
```

The output files (PDF and XML) are written to the `target/reports/` directory.

## How It Works

The `OsgiApp` main class performs the following steps:

1. Starts an embedded Apache Felix OSGi framework
2. Installs all dependency JARs as OSGi bundles, fixing their manifests as needed
   (correcting version strings, adding Export-Package and DynamicImport-Package headers)
3. Fills compiled report templates (`.jasper`) via the bundle-loaded JasperReports API using reflection
4. Exports the filled reports to PDF and XML formats

A custom `CompositeClassLoader` aggregates class loading across multiple OSGi bundle classloaders,
since each bundle has an isolated classloader context.

## Reports

- **ImagesReport** - embedding images in various formats
- **ExcelXlsDataAdapterReport** - reading data from an Excel file via data adapter
- **ExcelXlsQeDataAdapterReport** - reading data from an Excel file via query executer data adapter
- **GroovyReport** - report expressions written in Groovy
- **JavaScriptReport** - report expressions written in JavaScript
