<a name="webapp"/>

# JasperReports - Webapp Sample <img src="../../resources/jasperreports.svg" alt="JasperReports logo" align="right"/>

This sample demonstrates running JasperReports Library in a traditional Java web application (WAR),
served using the Jetty Maven plugin. It shows how to compile, fill and export report templates
at runtime using Java Servlets and Java Server Pages.

## Prerequisites

- JDK 17 or later
- Maven

## Building and Running

First, build the JasperReports Library from the project root:

```
mvn clean install -Dmaven.buildNumber.doCheck=false
```

Then start the web application with the Jetty plugin:

```
cd demo/samples/webapp
mvn clean jetty:run
```

The `jetty:run` goal automatically compiles the sources and deploys the exploded web application,
so a separate `package` step is not required.

Open http://localhost:8080/ to see the sample's home page.

### Jetty plugin configuration

The `org.eclipse.jetty.ee10:jetty-ee10-maven-plugin` is configured in `pom.xml` with:

- `webAppSourceDirectory` set to `src/main/webapp`, where the JSPs, servlet mappings (`WEB-INF/web.xml`)
  and static resources live.
- `deployMode` set to `FORK`, which runs the web application in a separate forked JVM rather than
  inside the Maven process itself.
- `stopPort` and `stopKey`, used to shut down the forked JVM cleanly from another terminal:

```
mvn jetty:stop
```

Since the server runs in a forked process, pressing `Ctrl+C` in the terminal running `jetty:run`
may not stop it; use `mvn jetty:stop` (with the same `stopPort`/`stopKey`, already set in `pom.xml`)
instead.

## Sample Walkthrough

The home page links to three pages that should be visited in order, since each step depends on
state produced by the previous one:

1. **compile JRXML** - compiles `reports/WebappReport.jrxml` into a `.jasper` template at runtime,
   via `servlets/CompileServlet` or `jsp/compile.jsp`. In most real applications report templates
   are compiled once at build time rather than on every request; this page exists to show how
   runtime compilation can be done when it is actually needed.
2. **fill report** - fills the compiled template with data (from `datasource/WebappDataSource` and
   `scriptlets/WebappScriptlet`), via `servlets/FillServlet` or `jsp/fill.jsp`. The resulting
   `net.sf.jasperreports.engine.JasperPrint` is stored in the HTTP session for the next step.
3. **export report** - exports the filled report from the session to PDF, HTML, XLS, RTF, ODT, ODS,
   DOCX, PPTX or XLSX, using the servlets registered in `WEB-INF/web.xml`
   (`net.sf.jasperreports.jakarta.servlets.*`, from the `jasperreports-servlets` extension) and the
   `jsp/html.jsp` / `jsp/viewer.jsp` pages for HTML output.

## Configuration

The `jasperreports.properties` file under `src/main/resources` provides the JasperReports
configuration properties used by the sample at runtime.
