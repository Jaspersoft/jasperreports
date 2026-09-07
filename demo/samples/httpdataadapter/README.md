
# JasperReports - HTTP Data Adapter Sample <img src="../../resources/jasperreports.svg" alt="JasperReports logo" align="right"/>

Shows how the HTTP data adapters can be used to fill reports, including basic authentication.

### Main Features in This Sample

[HTTP Data Adapter](#httpdataadapter)

### Secondary Features

[JSONQL Data Source](../jsonqldatasource/README.md#jsonqldatasource)\
[XML Data Source](../xmldatasource/README.md#xmldatasource)

## <a name='httpdataadapter'>HTTP</a> Data Adapter
<div align="right">Documented by <a href='mailto:shertage@users.sourceforge.net'>Sanda Zaharia</a></div>

**Description / Goal**\
How to fill a report using data from an HTTP request returning XML or JSON file.

**Since:**

**Other Samples**\
[/demo/samples/jsonqldatasource](../jsonqldatasource/README.md)\
[/demo/samples/xmldatasource](../xmldatasource/README.md)

### Accessing Data over HTTP

An HTTP data adapter is a data file-based data adapter that uses an [HttpDataLocation](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/dataadapters/http/HttpDataLocation.html) object, in order to gain access to remote data over HTTP and retrieve content that can be mapped to a custom (usually JSONQL or XML) data source.

[DataFile](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/dataadapters/DataFile.html)-based adapters (such as [JsonDataAdapter](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/data/json/JsonDataAdapter.html) or [XmlDataAdapter](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/data/xml/XmlDataAdapter.html)) can be converted into HTTP data adapters by declaring their [DataFile](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/dataadapters/DataFile.html) element of type [HttpDataLocation](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/dataadapters/http/HttpDataLocation.html):

```
<jsonDataAdapter class="net.sf.jasperreports.data.json.JsonDataAdapterImpl">
  <name>JSON Http Data Adapter</name>
  <dataFile xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="httpDataLocation">
    ...
  </dataFile>
  ...
</jsonDataAdapter>
```

or

```
<xmlDataAdapter class="net.sf.jasperreports.data.xml.XmlDataAdapterImpl">
  <name>XML Http Data Adapter</name>
  <dataFile xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="httpDataLocation">
    ...
  </dataFile>
  ...
</xmlDataAdapter>
```

The [HttpDataLocation](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/dataadapters/http/HttpDataLocation.html) object encapsulates the following information, related to a given HTTP request:

- `method` - the request method name, which may be one of the following (see [RequestMethod](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/dataadapters/http/RequestMethod.html)):
    - `GET`
    - `POST`
    - `PUT`
- `url` - the data resource location URL
- `username` and `password` - user authentication info (supports both basic and digest HTTP authentication)
- a list of `urlParameter` elements - representing request URL parameters, each one being defined by a name and a value
- `body` - a String representing the request body, in case of a `POST` (or `PUT`) method; it will be ignored by a `GET` method
- a list of `postParameter` elements - representing request `POST` parameters, each characterized by a name and a value; they will be always ignored by a `GET` method. They will also be ignored if a body element was already specified
- a list of `header` elements - representing request headers, also each one characterized by a name and a value

### HTTP Parameter Properties

None of the elements described above (`method, url, username, password, urlParameter, body, postParameter` or `header`) are required in an HTTP data adapter.\
The only requirement is to have its type declared as `httpDataLocation` (`xsi:type="httpDataLocation"`).

In case no `method` element is specified, the default method name depends on the presence/absence of the body element or the `postParameter` list:

- if `body` and `postParameter` list are both missing, the method is `GET` by default
- if the `body` element is present, the method is `POST` by default
- if the `body` element is missing, but the `postParameter` list contains at least one element, the method is `POST` by default

Regarding the other elements (`url, username, urlParameter`, etc), we can send all this information via the usual report parameters, since data adapters are in fact parameter contributor objects.

The following predefined parameter properties let us configure the HTTP request, and always override the similar information declared in data adapter:

1. [net.sf.jasperreports.http.data.method](https://jasperreports.sourceforge.net/config.reference.html#net.sf.jasperreports.http.data.method)
    - is specified per dataset or at report parameter level
    - if declared per dataset, its value is mandatory and represents the HTTP request method used by the data adapter
    - if declared at report parameter level, it needs no value and is used to mark the report parameter as HTTP request method provider
    - if this property is specified in various report parameters, only the last one of them will be considered as HTTP request method provider
2. [net.sf.jasperreports.http.data.url](https://jasperreports.sourceforge.net/config.reference.html#net.sf.jasperreports.http.data.url)
    - is specified per dataset or at report parameter level
    - if declared per dataset, its value is mandatory and represents the base URL used by the HTTP data adapter
    - if declared at report parameter level, it needs no value and is used to mark the report parameter as URL provider for the HTTP request
    - overrides the deprecated parameter [PARAMETER_URL](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/dataadapters/http/HttpDataService.html#PARAMETER_URL)
    - if this property is specified in various report parameters, only the last one of them will be considered as the URL provider
3. [net.sf.jasperreports.http.data.username](https://jasperreports.sourceforge.net/config.reference.html#net.sf.jasperreports.http.data.username)
    - is specified per dataset or at report parameter level
    - if declared per dataset, its value is mandatory and represents the user name to be used in HTTP data adapters with basic authentication.
    - if declared at report parameter level, it needs no value and is used to mark the report parameter as user name provider for the HTTP request
    - overrides the deprecated parameter [PARAMETER_USERNAME](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/dataadapters/http/HttpDataService.html#PARAMETER_USERNAME)
    - if this property is specified in various report parameters, only the last one of them will be considered as the user name provider
4. [net.sf.jasperreports.http.data.password](https://jasperreports.sourceforge.net/config.reference.html#net.sf.jasperreports.http.data.password)
    - is specified per dataset or at report parameter level
    - if declared per dataset, its value is mandatory and represents the user password to be used in HTTP data adapters with basic authentication.
    - if declared at report parameter level, it needs no value and is used to mark the report parameter as user password provider for the HTTP request
    - overrides the deprecated parameter [PARAMETER__PASSWORD](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/dataadapters/http/HttpDataService.html#PARAMETER_PASSWORD)
    - if this property is specified in various report parameters, only the last one of them will be considered as the user password provider
5. [net.sf.jasperreports.http.data.body](https://jasperreports.sourceforge.net/config.reference.html#net.sf.jasperreports.http.data.body)
    - is specified per dataset or at report parameter level
    - if declared per dataset, its value is mandatory and represents the HTTP request body
    - if declared at report parameter level, it needs no value and is used to mark the report parameter as HTTP request body provider
    - if this property is specified in various report parameters, only the last one of them will be considered as the request body provider
    - the HTTP request body will be ignored by the `GET` method
6. [net.sf.jasperreports.http.data.url.parameter](https://jasperreports.sourceforge.net/config.reference.html#net.sf.jasperreports.http.data.url.parameter)
    - is specified at report parameter level
    - if the property has no value, it will be used to mark the report parameter as URL parameter to be appended to the base URL of the HTTP request; the URL parameter will have the same name and value as the report parameter
    - if the property has a given value, its value will be used as name for the related URL parameter; the value of the URL parameter will be the same as the report parameter value
    - overrides the deprecated parameter prefix [PARAMETER_PREFIX_URL_PARAMETER](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/dataadapters/http/HttpDataService.html#PARAMETER_PREFIX_URL_PARAMETER)
    - if this property is specified in various report parameters, the related URL parameter will be considered as parameter of type array, being sent multiple times
7. [net.sf.jasperreports.http.data.post.parameter](https://jasperreports.sourceforge.net/config.reference.html#net.sf.jasperreports.http.data.post.parameter)
    - is specified at report parameter level
    - if the property has no value, it will be used to mark the report parameter as HTTP request `POST` parameter; the `POST` parameter will have the same name and value as the report parameter
    - if the property has a given value, its value will be used as name for the related request `POST` parameter; the value of the POST parameter will be the same as the report parameter value
    - overrides the deprecated parameter prefix [PARAMETER_PREFIX_POST_PARAMETER](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/dataadapters/http/HttpDataService.html#PARAMETER_PREFIX_POST_PARAMETER)
    - if this property is specified in various report parameters, the related `POST` parameter will be considered as parameter of type array, being sent multiple times
    - `POST` parameters will be ignored by the `GET` method
    - `POST` parameters will be ignored if a request `body` is provided
8. [net.sf.jasperreports.http.data.header](https://jasperreports.sourceforge.net/config.reference.html#net.sf.jasperreports.http.data.header)
    - is specified at report parameter level
    - if the property has no value, it will be used to mark the report parameter as HTTP request header; the header will have the same name and value as the report parameter
    - if the property has a given value, its value will be used as name for the related request header; the value of the header will be the same as the report parameter value
    - if this property is specified in various report parameters, the related request header will be added multiple times to the request

### HTTP Data Service

The information stored in the [HttpDataLocation](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/dataadapters/http/HttpDataLocation.html) object is collected and processed by the [HttpDataService](url)https://jasperreports.sourceforge.net/api/net/sf/jasperreports/dataadapters/http/HttpDataService.html API, which builds a client object to send a specific request over HTTP.

As a result, a data file connection is obtained in the form of an [HttpDataConnection](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/dataadapters/http/HttpDataConnection.html) object.

This object comes with an [InputStream](https://docs.oracle.com/javase/8/docs/api/java/io/InputStream.html) that can be extracted and interpreted as custom data source (usually in JSON or XML format), using the available public methods of the class:

- `public InputStream getInputStream()` - retrieves the InputStream from this object
- `public void dispose()` - closes the HTTP response and HTTP client streams and release resources associated with these objects

### The JSON and XML HTTP Data Adapters

This sample provides 2 examples based on the HTTP data adapter: one of them will process the input in order to get a JSONQL data source, the other will prepare an XML data source by querying the same data.

Following are the 2 data adapter definitions:

- Content of the JSON Data Adapter (see `data/JsonHttpDataAdapter.jrdax` file provided in this sample directory):

```
<jsonDataAdapter class="net.sf.jasperreports.data.json.JsonDataAdapterImpl">
  <name>JSON Http Data Adapter</name>
  <dataFile xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:type="httpDataLocation">
    <method>GET</method>
    <url><![CDATA[https://www.omdbapi.com/?r=json]] ></url>
    <username>jasper</username>
    <password>r3Ports</password>
  </dataFile>
  <useConnection>true</useConnection>
  <timeZone xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xmlns:java="http://java.sun.com" xsi:type="java:java.lang.String">Europe/Bucharest</timeZone>
  <locale xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xmlns:java="http://java.sun.com" xsi:type="java:java.lang.String">ro_RO</locale>
  <selectExpression></selectExpression>
</jsonDataAdapter>
```

- Content of the XML Data Adapter (see `data/XmlHttpDataAdapter.jrdax` file provided in this sample directory):

```
<xmlDataAdapter class="net.sf.jasperreports.data.xml.XmlDataAdapterImpl">
  <name>XML Http Data Adapter</name>
  <dataFile xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:type="httpDataLocation">
    <method>GET</method>
    <url><![CDATA[https://www.omdbapi.com/?r=xml]] ></url>
    <username>jasper</username>
    <password>r3Ports</password>
  </dataFile>
  <useConnection>true</useConnection>
  <timeZone xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xmlns:java="http://java.sun.com" xsi:type="java:java.lang.String">Europe/Bucharest</timeZone>
  <locale xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xmlns:java="http://java.sun.com" xsi:type="java:java.lang.String">ro_RO</locale>
  <selectExpression></selectExpression>
</xmlDataAdapter>
```

We can observe that the above data adapters are quite similar:

- both of them provide a `<dataFile/>` element of type `httpDataLocation`, therefore they also became HTTP data adapters
- both of them are based on a HTTP request `GET` method
- both of them provide the same base URL: https://www.omdbapi.com - a web service URL to retrieve various movies information in terms of title, year, IMDb ID, type, poster image
- both of them use HTTP authentication via `<username>` and `<password>` elements; the embedded Jetty server is configured with a `HashLoginService` realm (`etc/realm.properties`) and `web.xml` security constraints to require credentials for data access. The sample runs the reports twice: first against a server configured with basic authentication (`web.xml`), then against one configured with digest authentication (`web-digest.xml`). The same data adapter credentials work transparently with both authentication methods, since the underlying Apache HttpClient automatically negotiates the appropriate scheme
- also the same settings are present for `<useConnection/>`, `<timeZone/>` and `<locale/>`
- a `selectExpression` is not provided, so that it has to be set in the related report

But:

- the JSON data adapter is an instance of [JsonDataAdapterImpl](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/data/json/JsonDataAdapterImpl.html), while the XML data adapter is an instance of [XmlDataAdapterImpl](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/data/xml/XmlDataAdapterImpl.html)
- the request URL parameter `r` has different values in these 2 adapters: `r=json` in JSON data adapter and `r=xml` in XML data adapter.\
This means that the response content will be delivered in JSON format for the first data adapter, and in XML format for the second one.

Now let's take a look into JRXML files provided in this sample, in order to see how the remaining request input is provided via report parameters and properties.

### The JRXML files

The JRXML files are found in the `reports` folder of this JasperReports sample.

Like the above data adapters, they look almost similar, the only differences being related to data adapter type, query language and dataset field properties (because these are also related to the query language).

Each JRXML report:

- contains 2 subdatasets (`FetchDataset` and `MoviesDataset`)
- sends similar requests over HTTP
- perform similar queries on their data source
- provides the same output layout in the form of a paginated list of movies information.

Following are presented the settings in the `JsonHttpDataAdapterReport.jrxml` file:

```
<jasperReport name="JsonHttpDataAdapterReport" language="java" columnCount="3" pageWidth="595" pageHeight="842"
  columnWidth="171" leftMargin="40" rightMargin="40" topMargin="50" bottomMargin="50">
  <property name="net.sf.jasperreports.data.adapter" value="data/JsonHttpDataAdapter.jrdax"/>
  <dataset name="FetchDataset"/>
  <dataset name="MoviesDataset">
    <property name="net.sf.jasperreports.data.adapter" value="data/JsonHttpDataAdapter.jrdax"/>
    <parameter name="page" class="java.lang.Integer">
      <property name="net.sf.jasperreports.http.data.url.parameter"/>
    </parameter>
    <query language="jsonql"><![CDATA[animals]] ></query>
    <field name="name" class="java.lang.String">
      <property name="net.sf.jasperreports.jsonql.field.expression" value="name"/>
    </field>
    <field name="size" class="java.lang.Integer">
      <property name="net.sf.jasperreports.jsonql.field.expression" value="size"/>
    </field>
    <field name="type" class="java.lang.String">
      <property name="net.sf.jasperreports.jsonql.field.expression" value="type"/>
    </field>
    <field name="image" class="java.lang.String">
      <property name="net.sf.jasperreports.jsonql.field.expression" value="image"/>
    </field>
  </dataset>
  <query language="jsonql"/>
  <field name="totalResults" class="java.lang.Integer">
    <property name="net.sf.jasperreports.jsonql.field.expression" value="totalResults"/>
  </field>
  ...
</jasperReport>
```

Both the main dataset and `MoviesDataset` require information from `JsonHttpDataAdapter.jrdax` file via `net.sf.jasperreports.data.adapter` property.

With these settings, the web service URL will look like:

http://www.omdbapi.com/?r=json

In response the web service will return a JSON object in the following format:

```
{
  "Search":[
    {"Title":"Aliens","Year":"1986","imdbID":"tt0090605","Type":"movie","Poster":"https://.../...jpg"},
    {"Title":"Cowboys & Aliens","Year":"2011","imdbID":"tt0409847","Type":"movie","Poster":"https://.../...jpg"},
    ...
   ],
  "totalResults":"213",
  "Response":"True"
}
```

Here we have:

- a `"Search"` property which is an array of the first 10 movies information objects
- a `"totalResults"` number that represents the number of records returned by this query
- a boolean `"Response"` which is `"True"` in our case, and lets us know that there were no errors/failures during this web service call

In the main dataset we are interested only in retrieving the `"totalResults"` number, in order to use it in the page header.\
To get it, we declared the report query language as `jsonql`, and set a related report field named `totalResults` as follows:

```
<query language="jsonql">
  <![CDATA[]] >
</query>
<field name="totalResults" class="java.lang.Integer">
  <property name="net.sf.jasperreports.jsonql.field.expression" value="totalResults"/>
</field>
```

One can observe the associated `net.sf.jasperreports.jsonql.field.expression` property that links the `totalResults` field name to the `"totalResults"` number provided by the JSON object.

`MoviesDataset` comes with one dataset parameter, related to request URL parameters via properties:

```
 <parameter name="page" class="java.lang.Integer">
   <property name="net.sf.jasperreports.http.data.url.parameter"/>
 </parameter>
```

The `page` parameter adds pagination (each page contains by default 10 results) to the request URL.\
The `net.sf.jasperreports.http.data.url.parameter` property has no value in this case, meaning that the name of the appended URL parameter is also `page`.\
Since there is no default value for this parameter, its value will be provided during report execution at runtime.

With these settings, for the second page of results, the web service URL will look like:

http://www.omdbapi.com/?r=json&page=2

Note: The http://www.omdbapi.com/?r=json& request URL has the same effect as http://www.omdbapi.com/?r=json&page=1

The query language in `MovieDataset` is also `jsonql`, but now we perform a `JSONQL` query on the `"Search"` array member of the JSON object, in order to retrieve the movie information regarding title, year, movie type and poster:

```
 <query language="jsonql">
   <![CDATA[Search]] >
 </query>
 <field name="title" class="java.lang.String">
   <property name="net.sf.jasperreports.jsonql.field.expression" value="Title"/>
 </field>
 <field name="year" class="java.lang.Integer">
   <property name="net.sf.jasperreports.jsonql.field.expression" value="Year"/>
 </field>
 <field name="type" class="java.lang.String">
   <property name="net.sf.jasperreports.jsonql.field.expression" value="Type"/>
 </field>
 <field name="poster" class="java.lang.String">
   <property name="net.sf.jasperreports.jsonql.field.expression" value="Poster"/>
 </field>
```

Here we have parameter properties with case sensitive values.

For instance, the title report field is linked to the `"Title"` member of the JSON object representing the movie information in the `"Search"` array.

All these fields are used in a list component that uses the `MovieDataset` as dataset, based on title and page parameter values:

```
<jr:list printOrder="Vertical">
  <datasetRun subDataset="MoviesDataset">
    <datasetParameter name="title">
      <datasetParameterExpression><![CDATA[$P{title}]] ></datasetParameterExpression>
    </datasetParameter>
    <datasetParameter name="page">
      <datasetParameterExpression><![CDATA[$V{REPORT_COUNT}]] ></datasetParameterExpression>
    </datasetParameter>
  </datasetRun>
  <jr:listContents height="65" width="170">
    ...
  </jr:listContents>
</jr:list>
```

The other subdataset in the report (i.e `FetchDataset`) is used in conjunction with an empty data source, in order to allow paginated results to be rendered page by page during the report execution.

```
<detail>
  <band height="65" splitType="Stretch">
    <element kind="component" width="170" height="65">
      <component kind="list" printOrder="Vertical">
        <datasetRun subDataset="FetchDataset">
          <dataSourceExpression><![CDATA[new JREmptyDataSource((int)(Math.ceil($F{totalResults} / 10d)))]] ></dataSourceExpression>
        </datasetRun>
        <contents height="65" width="170">
        ...
        </contents>
      </component>
    </element>
  </band>
</detail>
```

As already stated, the `XmlHttpDataAdapterReport.jrxml` file looks similar to `JsonHttpDataAdapterReport.jrxml` file.

Report parameters definitions are the same in both JRXML files. Query languages and dataset field definitions are slightly different.

Following are presented the main differences exposed by `XmlHttpDataAdapterReport.jrxml`:

```
<jasperReport name="XmlHttpDataAdapterReport" ...>
<property name="net.sf.jasperreports.data.adapter" value="data/XmlHttpDataAdapter.jrdax"/>
  <subDataset name="FetchDataset">
    <parameter name="title" class="java.lang.String"/>
  </subDataset>
  <subDataset name="MoviesDataset">
    <property name="net.sf.jasperreports.data.adapter" value="data/XmlHttpDataAdapter.jrdax"/>
    <parameter name="title" class="java.lang.String">
      <property name="net.sf.jasperreports.http.data.url.parameter" value="s"/>
    </parameter>
    <parameter name="page" class="java.lang.Integer">
      <property name="net.sf.jasperreports.http.data.url.parameter"/>
    </parameter>
    <query language="xPath">
      <![CDATA[/root/result]] >
    </query>
    <field name="title" class="java.lang.String">
      <property name="net.sf.jasperreports.xpath.field.expression" value="@title"/>
    </field>
    <field name="year" class="java.lang.Integer">
      <property name="net.sf.jasperreports.xpath.field.expression" value="@year"/>
    </field>
    <field name="type" class="java.lang.String">
      <property name="net.sf.jasperreports.xpath.field.expression" value="@type"/>
    </field>
    <field name="poster" class="java.lang.String">
      <property name="net.sf.jasperreports.xpath.field.expression" value="@poster"/>
    </field>
  </subDataset>
  <parameter name="title" class="java.lang.String" evaluationTime="Early">
    <property name="net.sf.jasperreports.http.data.url.parameter" value="s"/>
    <defaultValueExpression><![CDATA["aliens"]] ></defaultValueExpression>
  </parameter>
  <query language="xPath">
    <![CDATA[/root]] >
  </query>
  <field name="totalResults" class="java.lang.Integer">
    <property name="net.sf.jasperreports.xpath.field.expression" value="@totalResults"/>
  </field>
  ...
</jasperReport>
```

this JRXML file requires information from `data/XmlHttpDataAdapter.jrdax` data adapter for both main dataset and `MoviesDataset`.

The response content will be delivered in the following XML format:

```
<root totalResults="213" response="True">
  <result title="Aliens" year="1986" imdbID="tt0090605" type="movie" poster="https://.../...jpg"/>
  <result title="Cowboys & Aliens" year="2011" imdbID="tt0409847" type="movie" poster="https://.../...jpg"/>
  ...
</root>
```

- the query language is set to `xPath` (instead of `jsonql`) in both main dataset and `MoviesDataset`; in `MoviesDataset` the xPath query is performed over the `/root/result` nodes; 
- the web service URL will look like: http://www.omdbapi.com/?r=xml&s=aliens in main dataset and http://www.omdbapi.com/?r=xml&s=aliens&page=2 in `MoviesDataset` field properties have different names and values in main dataset:

```
<field name="totalResults" class="java.lang.Integer">
  <property name="net.sf.jasperreports.xpath.field.expression" value="@totalResults"/>
</field>
```

and in `MoviesDataset`:

```
<field name="title" class="java.lang.String">
  <property name="net.sf.jasperreports.xpath.field.expression" value="@title"/>
</field>
<field name="year" class="java.lang.Integer">
  <property name="net.sf.jasperreports.xpath.field.expression" value="@year"/>
</field>
<field name="type" class="java.lang.String">
  <property name="net.sf.jasperreports.xpath.field.expression" value="@type"/>
</field>
<field name="poster" class="java.lang.String">
  <property name="net.sf.jasperreports.xpath.field.expression" value="@poster"/>
</field>
```

### Running the Sample

Running the sample requires the Apache Maven library. Make sure that maven is already installed on your system (version 3.6 or later).
In a command prompt/terminal window set the current folder to `demo/samples/httpdataadapter` within the JasperReports source project and run the following command:

```
> mvn clean compile exec:exec@all
```

It will generate all supported document types containing the sample report in the `demo/samples/httpdataadapter/target/reports` directory.
