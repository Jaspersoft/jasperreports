<a name="springboot"/>

# Spring Boot Sample

This sample demonstrates running JasperReports Library in a Spring Boot 4 application.
A REST controller compiles and serves several report templates as PDF, accessible through a browser.

## Prerequisites

- JDK 17 or later
- Maven

## Building and Running

First, build the JasperReports Library from the project root:

```
mvn clean install -Dmaven.buildNumber.doCheck=false
```

Then start the Spring Boot application:

```
cd demo/samples/springboot
mvn clean compile spring-boot:run
```

Open http://localhost:8080/ to see the list of available reports.
Each report link returns the rendered PDF at `/rest/pdf/{reportName}`.

## Reports

The sample includes reports copied from other demo samples:

- **StretchReport** - text field stretching with different border techniques (from `stretch` sample)
- **ImagesReport** - embedding images in various formats: JPEG, SVG, TIFF, WebP, GIF (from `osgi` sample)
- **ExcelXlsDataAdapterReport** - reading data from an Excel file via data adapter (from `osgi` sample)
- **ExcelXlsQeDataAdapterReport** - reading data from an Excel file via query executer data adapter (from `osgi` sample)
- **AllChartsReport** - bar, pie, line, area, scatter, time series, bubble, meter, and thermometer charts (from `chartthemes` sample)

## Configuration

The `jasperreports.properties` file sets image DPI to 150 and enables the `eye.candy.sixties` chart theme
(provided by the `jasperreports-chart-themes` extension).
