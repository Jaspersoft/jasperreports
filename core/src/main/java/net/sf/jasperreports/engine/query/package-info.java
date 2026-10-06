/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2026 Actian Corporation, a division of HCL Software. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Actian,
 * the following license terms apply:
 *
 * This program is part of JasperReports.
 *
 * JasperReports is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JasperReports is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JasperReports. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * Provides support for report queries and query executers. 
 * <br/>
 * <h3>Report SQL Queries</h3>
 * To fill a report, provide the reporting engine with the report data, or at least instruct it 
 * how to get this data. 
 * <p>
 * JasperReports normally expects to receive a 
 * {@link net.sf.jasperreports.engine.JRDataSource} object as the report data source, but 
 * it has also been enhanced to work with JDBC so that it can retrieve data from relational 
 * databases if required. 
 * </p><p>
 * The library allows the report template to specify the SQL query for report data if this 
 * data is located in relational databases. 
 * The SQL query specified in the report template is taken into account and executed only if 
 * a <code>java.sql.Connection</code> object is supplied instead of the normal 
 * {@link net.sf.jasperreports.engine.JRDataSource} object when filling the report. 
 * </p><p>
 * This query can be introduced in the JRXML report template using the <code>&lt;queryString&gt;</code> 
 * element. If present, this element comes after the report parameter declarations and before 
 * the report fields. 
 * </p><p>
 * Report parameters in the query string are important to further refine the data retrieved 
 * from the database. These parameters can act as dynamic filters in the query that supplies 
 * data for the report. Parameters are introduced using a special syntax, similar to the one 
 * used in report expressions. 
 * </p><p>
 * There are three possible ways to use parameters in the query, described below:
 * <ul>
 * <li><code>$P{paramName}</code> Syntax - the parameters are used like normal <code>java.sql.PreparedStatement</code> parameters</li>
 * <li><code>$P!{paramName}</code> Syntax - the parameters are used to dynamically modify portions of the SQL 
 * query or to pass the entire SQL query as a parameter to the report-filling routines. The 
 * value supplied for those parameters replace the parameter references in the query, before 
 * it is sent to the database server using a <code>java.sql.PreparedStatement</code> object.</li>
 * <li><code>$X{functionName, param1, param2,...}</code> Syntax - There are also cases when a part of the query 
 * needs to be dynamically built starting from a report parameter value, with the query part containing both 
 * query text and bind parameters. This is the case, for instance, with <code>IN</code> and <code>NOT IN</code> 
 * query clauses that need to use a collection report parameter as a list of values. Such complex query clauses are 
 * introduced into the query using the $X{} syntax.</li>
 * </ul>
 * In the majority of cases, the SQL query text placed inside a report template is a <code>SELECT</code> 
 * statement. JasperReports uses a <code>java.sql.PreparedStatement</code> behind the scenes to 
 * execute that SQL query through JDBC and retrieve a <code>java.sql.ResultSet</code> object to 
 * use for report filling. However, the SQL query string might also contain stored procedure 
 * calls. 
 * </p><p>
 * Certain conditions must be met to put stored procedure calls in the SQL query string of a 
 * report template: 
 * <ul>
 * <li>The stored procedure must return a <code>java.sql.ResultSet</code> when called through JDBC.</li>
 * <li>The stored procedure cannot have OUT parameters.</li>
 * </ul>
 * <h3>Query Executers</h3>
 * Report data can be produced by specifying queries in languages other than SQL. Each query language 
 * is associated a query executer factory implementation. JasperReports has built-in query executer 
 * implementations for SQL, Hibernate 3, XPath, EJB-QL, CSV and Excel queries. 
 * <p>
 * The query language is specified in JRXML using the <code>language</code> attribute of the 
 * <code>&lt;queryString&gt;</code> tag. Using the API, the query language is set by 
 * {@link net.sf.jasperreports.engine.design.JRDesignQuery#setLanguage(String) setLanguage(String)}. The default language is SQL, thus ensuring 
 * backward compatibility for report queries that do not specify a query language. 
 * </p><p>
 * To register a query executer factory for a query language, one has to define a 
 * JasperReports property named 
 * {@link net.sf.jasperreports.engine.query.QueryExecuterFactory#QUERY_EXECUTER_FACTORY_PREFIX net.sf.jasperreports.query.executer.factory.&lt;language&gt;}. The same mechanism can be used to 
 * override the built-in query executers for a query language, for instance to use a custom 
 * query executer for SQL queries. 
 * </p><p>
 * The API for query executers involves an executer factory interface, a query executer 
 * interface, implementations of these interfaces, and {@link net.sf.jasperreports.engine.JRDataSource} implementations. 
 * </p><p>
 * {@link net.sf.jasperreports.engine.query.QueryExecuterFactory} is a factory interface used to query executers for a specific 
 * language and to provide information regarding the connection parameters required by the 
 * query executer to run the query. It has the following methods: 
 * <ul>
 * <li><code>public JRQueryExecuter createQueryExecuter(JasperReportsContext jasperReportsContext, JRDataset dataset, Map&lt;String,? extends JRValueParameter&gt; parameters)</code> - 
 * This method creates a query executer. The dataset includes the query string and the fields that 
 * will be requested from the data source created by the query executer. The parameters map contains 
 * parameter types and runtime values to be used for query parameters. This method usually sends the 
 * dataset and parameters map to the created query executer.</li>
 * <li><code>public Object[] getBuiltinParameters()</code> - This method returns parameters that will 
 * be automatically registered with a report/dataset based on the query language. These parameters will 
 * be used by query executers as the context/connection on which to execute the query. For instance, 
 * the Hibernate query executer factory specifies a <code>HIBERNATE_SESSION</code> parameter of type 
 * <code>org.hibernate.Session</code> whose value will be used by the query executer to run the query.</li>
 * <li><code>public boolean supportsQueryParameterType(String className)</code> - This method is used on 
 * report validation to determine whether a query parameter type (for a parameter specified in the query 
 * using <code>$P{..}</code>) is supported by the query executer implementation.</li>
 * </ul>
 * A {@link net.sf.jasperreports.engine.query.JRQueryExecuter} is responsible for running a query, creating a 
 * data source out of the result, and closing the result. It includes these methods:
 * <ul>
 * <li><code>public JRDataSource createDatasource()</code> - This method processes and runs the query 
 * and creates a data source out of the query result. Usually, the required data (query string and parameter 
 * values) is made available to the query executer by the factory on creation.</li>
 * <li><code>public void close()</code> - This method closes the query execution result and any other 
 * resource associated with it. It is called after all data produced by the query executer has been fetched.</li>
 * <li><code>public boolean cancelQuery()</code> - This method is called when the user decides to cancel 
 * a report fill process. The implementation should check whether the query is 
 * currently being executed and ask the underlying mechanism to abort the execution. 
 * The method should return true if the query was being executed and the execution 
 * was canceled. If execution abortion is not supported, the method will always return false.</li>
 * </ul>
 * Query executer implementation can benefit from using 
 * {@link net.sf.jasperreports.engine.query.JRAbstractQueryExecuter} as 
 * a base. The abstract base provides query parameter processing functionality and other 
 * utility methods. 
 * </p><p>
 * In most cases, a query executer needs a new {@link net.sf.jasperreports.engine.JRDataSource} 
 * implementation to wrap its specific query results. Still, in some of the cases, query executers 
 * can use existing {@link net.sf.jasperreports.engine.JRDataSource} implementations.  
 * </p><p>
 * Note that registering new query executer implementations by adding properties in the 
 * <code>jasperreports.properties</code> file, as mentioned above, is only one way of registering 
 * the executers. They can be registered in a more transparent way by using the 
 * JasperReports extension support. One ore more query executer implementations can be 
 * packaged in a query executer bundle that can be deployed as a single JAR file. This 
 * approach obviates the need to modify existing application files. The query executer 
 * extension point in JasperReports is represented by the 
 * {@link net.sf.jasperreports.engine.query.DefaultQueryExecuterFactoryBundle} final class.
 * </p>
 * <h3>SQL Query Executer</h3>
 * The SQL query executer is a JDBC-based executer for SQL queries. 
 * <p>
 * The SQL query executer factory does not register any parameter as the 
 * {@link net.sf.jasperreports.engine.JRParameter#REPORT_CONNECTION REPORT_CONNECTION} parameter 
 * is kept in all reports for backward compatibility. The 
 * SQL query executer uses this parameter to retrieve a <code>java.sql.Connection</code> object. 
 * The query executer creates a {@link net.sf.jasperreports.engine.JRResultSetDataSource} data source to wrap the JDBC 
 * result set. 
 * </p><p>
 * Aborting the currently running query is supported using 
 * <code>java.sql.PreparedStatement.cancel()</code> when running a report asynchronously by using {@link net.sf.jasperreports.engine.fill.FillHandle#cancellFill()}.
 * Alternatively, the query timeout of the JDBC statement used by the query executer can be set to cancel the query after a certain amount of time by using the
 * {@link net.sf.jasperreports.engine.query.JRJdbcQueryExecuterFactory#PROPERTY_JDBC_QUERY_TIMEOUT net.sf.jasperreports.jdbc.query.timeout} configuration property
 * at the report level or globally.
 * </p><p>The fetch size of the JDBC statement used
 * by the query executer behind the scenes can be set using the
 * {@link net.sf.jasperreports.engine.query.JRJdbcQueryExecuterFactory#PROPERTY_JDBC_FETCH_SIZE net.sf.jasperreports.jdbc.fetch.size} configuration property at report level or 
 * globally.
 * </p>
 * <h3>XPath Query Executer</h3>
 * The XPath query executer permits reports using XML data sources to specify the XPath 
 * that produces the list of nodes/records as the report query. 
 * <p>
 * The query executer factory registers a parameter named 
 * {@link net.sf.jasperreports.engine.query.JRXPathQueryExecuterFactory#PARAMETER_XML_DATA_DOCUMENT XML_DATA_DOCUMENT} 
 * of type <code>org.w3c.dom.Document</code>. The query executer will run the XPath query against this 
 * document and produce a {@link net.sf.jasperreports.engine.data.JRXmlDataSource} data source. 
 * </p><p>
 * Parameters are supported in the XPath query. All parameters will be replaced in the 
 * query string by their <code>java.lang.String</code> values. 
 * This query executer recognizes four additional parameters that serve for localization 
 * purposes when creating the {@link net.sf.jasperreports.engine.data.JRXmlDataSource} instance: 
 * <ul>
 * <li>{@link net.sf.jasperreports.engine.query.JRXPathQueryExecuterFactory#XML_LOCALE XML_LOCALE}</li>
 * <li>{@link net.sf.jasperreports.engine.query.JRXPathQueryExecuterFactory#XML_NUMBER_PATTERN XML_NUMBER_PATTERN}</li>
 * <li>{@link net.sf.jasperreports.engine.query.JRXPathQueryExecuterFactory#XML_DATE_PATTERN XML_DATE_PATTERN}</li>
 * </ul>
 * </p>
 * <h3>Hibernate Query Executer</h3>
 * JasperReports includes support for Hibernate 3 in the form of a query executer. This 
 * allows users to specify in a report an HQL query that should be used to retrieve report 
 * data. 
 * <p>
 * For reports having an HQL query, the executor factory will automatically define a 
 * parameter named {@link net.sf.jasperreports.engine.query.HibernateConstants#PARAMETER_HIBERNATE_SESSION HIBERNATE_SESSION}
 * of type <code>org.hibernate.Session</code>. Its value will be used by the query executor to create the query. 
 * </p><p>
 * Like SQL queries, HQL queries can embed two types of parameters:
 * <ul>
 * <li>using <code>$P{..}</code> syntax - These parameters are used as named parameters of the Hibernate query.</li>
 * <li>using <code>$P!{..}</code> syntax - The <code>java.lang.String</code> value of the parameter is substituted 
 * as-is in the query string before creating the Hibernate query. This type of parameter can be used to dynamically 
 * specify query clauses/parts.</li>
 * </ul>
 * The result of a Hibernate query can be obtained in several ways. The Hibernate query 
 * executer chooses the way the query result will be produced based on a property named 
 * {@link net.sf.jasperreports.engine.query.HibernateConstants#PROPERTY_HIBERNATE_QUERY_RUN_TYPE net.sf.jasperreports.hql.query.run.type}. The run type can be one of the following:
 * <ul>
 * <li><code>list</code> - The result is fetched using <code>org.hibernate.Query.list()</code>. The result 
 * rows can be fetched all at once or in fixed-sized chunks. To enable paginated result row retrieval, 
 * the {@link net.sf.jasperreports.engine.query.HibernateConstants#PROPERTY_HIBERNATE_QUERY_LIST_PAGE_SIZE net.sf.jasperreports.hql.query.list.page.size}
 * configuration property should have a positive value.</li>
 * <li><code>scroll</code> - The result is fetched using <code>org.hibernate.Query.scroll()</code>.</li>
 * <li><code>iterate</code> - The result is fetched using <code>org.hibernate.Query.iterate()</code>.</li>
 * </ul>
 * The fetch size of the query can be set using the 
 * {@link net.sf.jasperreports.engine.query.JRJdbcQueryExecuterFactory#PROPERTY_JDBC_FETCH_SIZE net.sf.jasperreports.jdbc.fetch.size} configuration property at report level or 
 * globally. 
 * </p><p>
 * However, when dealing with large amounts of data, using pagination is the most 
 * common way to present the document content. In this case, it is necessary to clear 
 * Hibernate's first-level cache after each page fetching, otherwise Hibernate will 
 * eventually cause an OutOfMemory error. If the Hibernate's session cache is regularly 
 * cleared, the memory trap can be avoided. Because flushing data and clearing the cache is 
 * a time-consuming process, you should use it only if really huge datasets are involved. 
 * This is why the {@link net.sf.jasperreports.engine.query.HibernateConstants#PROPERTY_HIBERNATE_CLEAR_CACHE net.sf.jasperreports.hql.clear.cache} property was introduced.
 * Normally, it defaults to false. If set to true, the periodic Hibernate session cache 
 * cleanup is performed after each page fetching. 
 * </p><p>
 * A report/dataset field is mapped to a value from the Hibernate query result either by its 
 * description or its name. By default, the program uses the report field name, but the report 
 * field description property can be used instead if the 
 * {@link net.sf.jasperreports.engine.query.HibernateConstants#PROPERTY_HIBERNATE_FIELD_MAPPING_DESCRIPTIONS net.sf.jasperreports.hql.field.mapping.descriptions} configuration
 * property is set to true either in the report template or globally. 
 * </p><p>
 * The mappings are similar to the ones used by JavaBeans data sources, except that select aliases are 
 * used when queries return tuples instead of single objects. 
 * </p>
 * <h3>MDX Query Executer</h3>
 * Reporting on OLAP data is supported in JasperReports via the MDX query executer and 
 * a data source that use the Mondrian API's (this is why often we refer to this query 
 * executer also as the Mondrian query executer). Users can create reports with MDX 
 * queries and map report fields onto the OLAP result; the engine will execute the query via 
 * Mondrian and pass the result to a data source implementation, which will be used to fill 
 * the report. 
 * <p>
 * The Mondrian query executer is registered by default for queries having <code>MDX</code> or <code>mdx</code> as 
 * the language specified in the report template. One can use JasperReports configuration 
 * properties to register additional or alternative query language to query executer mappings 
 * </p><p>
 * The Mondrian query executer requires a single connection parameter named 
 * {@link net.sf.jasperreports.olap.JRMondrianQueryExecuterFactory#PARAMETER_MONDRIAN_CONNECTION MONDRIAN_CONNECTION}
 * of type <code>mondrian.olap.Connection</code>.
 * </p><p>
 * MDX queries can contain placeholders for parameters of any type. When the query gets 
 * executed, each parameter placeholder will be replaced in the query string by its 
 * <code>toString()</code> value. Therefore, for MDX queries, <code>$P{...}</code> parameters are equivalent to 
 * <code>$P!{...}</code> query fragments.
 * </p><p>
 * The Mondrian query executer passes the query result to a {@link net.sf.jasperreports.olap.JRMondrianDataSource}, which 
 * will be used to iterate the result and map values from the result to the report fields. 
 * The field mapping deals with mapping values from the OLAP result to the report fields. 
 * As an OLAP result has a multidimensional and hierarchical structure while a 
 * JasperReports data source has a tabular structure, mapping values to fields is not a trivial 
 * task. 
 * </p><p>
 * A special syntax is used to specify what value should be mapped to a field. The field 
 * description is used to hold the mapping specification. 
 * </p><p>
 * Using the mapping syntax, one can map two types of values from the OLAP result: 
 * <ul>
 * <li>Member values are names or properties of members of the result axes.</li>
 * <li>Data/measure values are cell values from the result.</li>
 * </ul>
 * The Mondrian data source performs a traversal of the OLAP result by iterating the 
 * members of the result axes. On every step, each field is checked for whether its mapping 
 * matches the current position in the OLAP result. If so, the value is extracted from the 
 * result and set to the field. 
 * </p>
 * <h3>XML/A Query Executer</h3>
 * MDX queries can also be executed on remote OLAP data sources via the XML for 
 * Analysis interface. This functionality is implemented in JasperReports as a query 
 * executer. 
 * <p>
 * Just like the Mondrian query executer presented in the previous section, the XML/A 
 * query executer is also mapped by default to the <code>MDX</code> and <code>mdx</code> query languages, but the 
 * Mondrian query executer takes precedence. 
 * </p><p>
 * The dispatch between the two query executers that are mapped on the same query 
 * language is done by a special query executer implementation. It is actually the 
 * {@link net.sf.jasperreports.olap.JRMdxQueryExecuterFactory} class that is registered by default 
 * with the <code>MDX</code> and <code>mdx</code> 
 * query languages, and it delegates the creation of the query instances at runtime to either 
 * the {@link net.sf.jasperreports.olap.JRMondrianQueryExecuterFactory} or the 
 * {@link net.sf.jasperreports.olap.xmla.JRXmlaQueryExecuterFactory}, 
 * depending on the specific parameter values that are passed in at report-filling time. 
 * It first checks for the 
 * {@link net.sf.jasperreports.olap.JRMondrianQueryExecuterFactory#PARAMETER_MONDRIAN_CONNECTION MONDRIAN_CONNECTION} 
 * parameter, and if found, the Mondrian query executer takes over. If this parameter is not found, it 
 * then checks for the {@link net.sf.jasperreports.olap.xmla.JRXmlaQueryExecuterFactory#PARAMETER_XMLA_URL PARAMETER_XMLA_URL} 
 * to see if the XMLA query executer can be used. In fact, there are 3 possible connection parameters for the 
 * XML/A query executer:
 * <ul>
 * <li><code>XMLA_URL</code> - a <code>java.lang.String</code> value representing the XMLA/SOAP service URL</li>
 * <li><code>XMLA_DATASOURCE</code> - a <code>java.lang.String</code> value representing the information 
 * required to connect to the OLAP data source</li>
 * <li><code>XMLA_CATALOG</code> - a <code>java.lang.String</code> value representing name of the OLAP catalog to use</li>
 * </ul>
 * The XMLA query executer creates a data source equivalent to the one created by the 
 * Mondrian query executer, with a few minor exceptions. 
 * </p><p>
 * This means that the result cube traversal and field mapping logic available for the 
 * MDX query executer applies for the XMLA query executer as well. 
 * </p><p>
 * The XMLA query executer lacks some of the functionality of the Mondrian query 
 * executer, due to inherent limitations of the XML for Analysis standard. The missing 
 * features are the following: 
 * <ul>
 * <li>Mapping report fields to custom member properties does not work with XML/A</li>
 * <li>For XMLA, it is not possible to produce a complete <code>mondrian.olap.Member</code> 
 * object, hence this feature is not supported.</li>
 * <li>Parent member matching using the <code>mondrian.olap.Member.getParent()</code> method 
 * does nor work via XML/A, since the parent member information is not present in the response.</li>
 * </ul></p>
 * <h3>EJB-QL/JPA Query Executer</h3>
 * The EJB-QL report query executer adds support for reporting on EJB 3.0 persistent 
 * entities data. For an EJB-QL query in a report, the query executer will use the EJB 3.0 
 * Java Persistence API to execute the query against an entity manager provided at runtime, 
 * and use the query result as a data source for the report.
 * <p> 
 * The built-in EJB-QL query executer is registered by default for queries having <code>EJBQL</code> or 
 * <code>ejbql</code> as their language. This mapping can be changed by using JasperReports 
 * properties. 
 * </p><p>
 * The EJB-QL query executer contributes built-in parameters to the report: </p>
 * <ul>
 * <li>The entity manager to be used for executing the query</li> 
 * <li>An optional query hints map</li> 
 * </ul>
 * When the report template contains an EJB-QL query, one must provide a JPA entity 
 * manager at runtime; the query executer will run the query using the supplied entity 
 * manager. The entity manager is of type <code>javax.persistence.EntityManager</code> and 
 * should be provided via the 
 * {@link net.sf.jasperreports.engine.query.EjbqlConstants#PARAMETER_JPA_ENTITY_MANAGER JPA_ENTITY_MANAGER}
 * built-in parameter: 
 * <pre>
 *   Map parameters = new HashMap();
 *   javax.persistence.EntityManager entityManager = createEntityManager();
 *   parameters.put( EjbqlConstants.PARAMETER_JPA_ENTITY_MANAGER, entityManager );
 *   JasperFillManager.fillReport(jasperReport, parameters);
 * </pre>
 * The means of getting hold of an entity manager depends on the particular EJB/JPA 
 * environment and implementation. 
 * <p>
 * An additional parameter named 
 * {@link net.sf.jasperreports.engine.query.EjbqlConstants#PARAMETER_JPA_QUERY_HINTS_MAP JPA_QUERY_HINTS_MAP}
 * allows you to specify query hints for running the query. The parameter value should be a map containing hint values 
 * mapped to hint names. The hints are set using the 
 * </p><p>
 * <code>javax.persistence.Query.setHint(String hintName, Object value)</code> method. 
 * </p><p>
 * Hints can also be specified statically by using report properties. The query executer treats 
 * any report property starting with 
 * net.sf.jasperreports.ejbql.query.hint.&lt;hintName&gt; as a hint by interpreting 
 * the property suffix as the hint name and the property value as the hint value. Thus, if the 
 * following property is present in the report: 
 * </p><p>
 * <code>&lt;property name="net.sf.jasperreports.ejbql.query.hint.cacheType" value="Shared"/&gt;</code> 
 * </p><p>
 * then the <code>cacheType</code> hint having <code>Shared</code> as value will be set when running the query. 
 * </p><p>
 * Note that only hints that accept String values can be set using this mechanism. 
 * </p><p>
 * A separate report property can be used to paginate the query result. This property can be 
 * used for controlling the amount of Java heap space used by the query executer while 
 * filling the report. The property can be set in the following manner: 
 * </p><p>
 * <code>&lt;property name="net.sf.jasperreports.ejbql.query.page.size" value="500"/&gt;</code> 
 * </p><p>
 * The results of the query will be fetched in chunks containing 500 rows. 
 * </p><p>
 * The pagination is achieved via the <code>javax.persistence.Query.setMaxResults()</code> 
 * and <code>setFirstResult()</code> methods. Obviously, using pagination could result in 
 * performance loss. Therefore enabling it is primarily recommended when the query 
 * results are very large. 
 * </p><p>
 * EJB-QL report queries can contain parameters of any type. At runtime, the value of the 
 * parameter is directly set by using 
 * <code>javax.persistence.Query.setParameter(String name, Object value)</code>, with 
 * no other processing. 
 * </p><p>
 * The result of the query execution is sent to a 
 * {@link net.sf.jasperreports.jakarta.ejbql.JRJpaDataSource}
 * data source implementation, which iterates 
 * over it and extracts report field values. Fields are mapped to specific values in the query 
 * result by specifying the mapping as field description or field name. 
 * The JPA data source can handle two types of query results: </p>
 * <ul>
 * <li>Queries returning a single entity/bean per row</li>
 * <li>Queries returning object tuples as rows</li>
 * </ul>
 * When the query returns a single entity/bean per row, as in 
 * <p>
 * <code>SELECT m FROM Movie m</code>
 * </p><p>
 * or 
 * </p><p>
 * <code>SELECT NEW MovieDescription(m.title, m.gender) FROM Movie m</code> 
 * </p><p>
 * then the field mappings are interpreted as bean property names. The same conventions as for 
 * JavaBeans data sources are used. 
 * </p><p>
 * When the query returns multiple objects per row, as in 
 * </p><p>
 * <code>SELECT m.title, m.gender FROM Movie m</code> 
 * </p><p>
 * then the fields are mapped using one of the following forms: </p>
 * <ul>
 * <li><code>COLUMN_&lt;index&gt;</code> - maps the field to a value specified by its position 
 * in the resulting tuple. The positions start from 1.</li>
 * <li><code>COLUMN_&lt;index&gt;.&lt;property&gt;</code> - maps the field to a property of a 
 * value specified by its position in the resulting tuple.</li>
 * </ul>
 * For instance, the following mappings could be used for a query returning multiple
 * objects per row: <code>COLUMN_1</code>, <code>COLUMN_2</code>, <code>COLUMN_1.title</code>, and
 * <code>COLUMN_2.movie.title</code>.
 * 
 * <h3>Related Documentation</h3>
 * <a href="http://community.jaspersoft.com/wiki/jasperreports-library-tutorial">JasperReports Tutorial</a>
 * @see net.sf.jasperreports.engine.JRDataSource
 * @see net.sf.jasperreports.engine.JRResultSetDataSource
 * @see net.sf.jasperreports.jakarta.ejbql.JRJpaDataSource
 * @see net.sf.jasperreports.engine.data.JRXmlDataSource
 * @see net.sf.jasperreports.olap.JRMdxQueryExecuterFactory
 * @see net.sf.jasperreports.olap.JRMondrianDataSource
 * @see net.sf.jasperreports.olap.JRMondrianQueryExecuterFactory
 * @see net.sf.jasperreports.olap.xmla.JRXmlaQueryExecuterFactory
 */
package net.sf.jasperreports.engine.query;

