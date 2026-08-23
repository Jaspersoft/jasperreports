/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2025 Cloud Software Group, Inc. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
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
package net.sf.jasperreports.olap.xmla;

import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.query.JRAbstractQueryExecuter;
import net.sf.jasperreports.olap.JRMdxQueryExecuterFactory;
import net.sf.jasperreports.olap.JROlapDataSource;
import net.sf.jasperreports.olap.result.JROlapResult;


/**
 * @author Michael Gunther (m.guenther at users.sourceforge.net)
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @author swood
 */
public class JRXmlaQueryExecuter extends JRAbstractQueryExecuter
{

	private static final Log log = LogFactory.getLog(JRXmlaQueryExecuter.class);
	
	public static final String EXCEPTION_MESSAGE_KEY_XMLA_CANNOT_RETRIEVE_ELEMENT = "data.olap.xmla.cannot.retrieve.element";
	public static final String EXCEPTION_MESSAGE_KEY_MESSAGE_CALL_FAILED = "data.olap.xmla.message.call.failed";
	public static final String EXCEPTION_MESSAGE_KEY_XMLA_NO_LEVEL_NAME = "data.olap.xmla.no.level.name";
	public static final String EXCEPTION_MESSAGE_KEY_XMLA_NULL_ELEMENT = "data.olap.xmla.null.element";
	
	private static final String SLICER_AXIS_NAME = "SlicerAxis";
	private static final String MDD_URI = "urn:schemas-microsoft-com:xml-analysis:mddataset";
	private static final String XMLA_URI = "urn:schemas-microsoft-com:xml-analysis";
	
	private static final String LEVEL_UNIQUE_NAME_PATTERN_DEFINITION = "\\[[^\\]]+\\]\\.\\[([^\\]]+)\\]";
	private static final Pattern LEVEL_UNIQUE_NAME_PATTERN = Pattern.compile(LEVEL_UNIQUE_NAME_PATTERN_DEFINITION);
	private static final int LEVEL_UNIQUE_NAME_PATTERN_NAME_GROUP = 1;
	
	private static final String HIERARCHY_LEVEL_UNIQUE_NAME_PATTERN_DEFINITION = LEVEL_UNIQUE_NAME_PATTERN_DEFINITION + "\\.\\[([^\\]]+)\\]";
	private static final Pattern HIERARCHY_LEVEL_UNIQUE_NAME_PATTERN = Pattern.compile(HIERARCHY_LEVEL_UNIQUE_NAME_PATTERN_DEFINITION);
	private static final int HIERARCHY_LEVEL_UNIQUE_NAME_PATTERN_NAME_GROUP = 2;

	private SOAPFactory sf;
	private SOAPConnection connection;
	private JRXmlaResult xmlaResult;


	/**
	 * 
	 */
	public JRXmlaQueryExecuter(
		JasperReportsContext jasperReportsContext,
		JRDataset dataset, 
		Map<String, ? extends JRValueParameter> parametersMap
		)
	{
		super(jasperReportsContext, dataset, parametersMap);

		parseQuery();
	}

	@Override
	protected String getCanonicalQueryLanguage()
	{
		return JRMdxQueryExecuterFactory.CANONICAL_LANGUAGE;
	}

	@Override
	protected String getParameterReplacement(String parameterName)
	{
		return String.valueOf(getParameterValue(parameterName));
	}

	public JROlapResult getResult()
	{
		try
		{
			this.sf = SOAPFactory.newInstance();
			this.connection = createSOAPConnection();
			SOAPMessage queryMessage = createQueryMessage();

			URL soapURL = new URL(getSoapUrl());
			SOAPMessage resultMessage = executeQuery(queryMessage, soapURL);
			
			xmlaResult = new JRXmlaResult();
			parseResult(resultMessage);
		}
		catch (MalformedURLException | SOAPException e)
		{
			throw new JRRuntimeException(e);
		}

		return xmlaResult;
	}
	
	@Override
	public JRDataSource createDatasource() throws JRException
	{
		getResult();
		
		return new JROlapDataSource(dataset, xmlaResult);
	}

	protected String getSoapUrl() throws MalformedURLException
	{
		String soapUrl;
		String xmlaUrl = (String) getParameterValue(JRXmlaQueryExecuterFactory.PARAMETER_XMLA_URL);
		String user = (String) getParameterValue(JRXmlaQueryExecuterFactory.PARAMETER_XMLA_USER, true);
		if (user == null || user.length() == 0)
		{
			soapUrl = xmlaUrl;
		}
		else
		{
			URL url = new URL(xmlaUrl);
			soapUrl = url.getProtocol() + "://" + user;
			
			String password = (String) getParameterValue(JRXmlaQueryExecuterFactory.PARAMETER_XMLA_PASSWORD, true);
			if (password != null && password.length() > 0)
			{
				soapUrl += ":" + password;
			}

			soapUrl += "@" + url.getHost();
			if (url.getPort() != -1)
			{
				soapUrl += ":" + url.getPort();
			}
			soapUrl += url.getPath();
		}
		return soapUrl;
	}

	@Override
	public boolean cancelQuery() throws JRException
	{
		return false;
	}

	@Override
	public void close()
	{
		if (connection != null)
		{
			try
			{
				connection.close();
			}
			catch (SOAPException e)
			{
				throw new JRRuntimeException(e);
			}
			connection = null;
		}
	}

	protected SOAPConnection createSOAPConnection()
	{
		try
		{
			SOAPConnectionFactory scf = SOAPConnectionFactory.newInstance();
			return scf.createConnection();
		}
		catch (UnsupportedOperationException | SOAPException e)
		{
			throw new JRRuntimeException(e);
		}
	}

	protected SOAPMessage createQueryMessage()
	{
		String queryStr = getQueryString();

		if (log.isDebugEnabled())
		{
			log.debug("MDX query: " + queryStr);
		}
		
		try
		{
			MessageFactory mf = MessageFactory.newInstance();
			SOAPMessage message = mf.createMessage();

			MimeHeaders mh = message.getMimeHeaders();
			mh.setHeader("SOAPAction", "\"urn:schemas-microsoft-com:xml-analysis:Execute\"");

			SOAPPart soapPart = message.getSOAPPart();
			SOAPEnvelope envelope = soapPart.getEnvelope();
			SOAPBody body = envelope.getBody();
			Name nEx = envelope.createName("Execute", "", XMLA_URI);

			SOAPElement eEx = body.addChildElement(nEx);

			// add the parameters

			// COMMAND parameter
			// <Command>
			// <Statement>queryStr</Statement>
			// </Command>
			Name nCom = envelope.createName("Command", "", XMLA_URI);
			SOAPElement eCommand = eEx.addChildElement(nCom);
			Name nSta = envelope.createName("Statement", "", XMLA_URI);
			SOAPElement eStatement = eCommand.addChildElement(nSta);
			eStatement.addTextNode(queryStr);

			// <Properties>
			// <PropertyList>
			// <DataSourceInfo>dataSource</DataSourceInfo>
			// <Catalog>catalog</Catalog>
			// <Format>Multidimensional</Format>
			// <AxisFormat>TupleFormat</AxisFormat>
			// </PropertyList>
			// </Properties>
			Map<String, String> paraList = new HashMap<>();
			String datasource = (String) getParameterValue(JRXmlaQueryExecuterFactory.PARAMETER_XMLA_DATASOURCE);
			paraList.put("DataSourceInfo", datasource);
			String catalog = (String) getParameterValue(JRXmlaQueryExecuterFactory.PARAMETER_XMLA_CATALOG);
			paraList.put("Catalog", catalog);
			paraList.put("Format", "Multidimensional");
			paraList.put("AxisFormat", "TupleFormat");
			addParameterList(envelope, eEx, "Properties", "PropertyList", paraList);
			message.saveChanges();

			if (log.isDebugEnabled())
			{
				log.debug("XML/A query message: \n" + prettyPrintSOAP(message.getSOAPPart().getEnvelope()));
			}

			return message;
		}
		catch (SOAPException e)
		{
			throw new JRRuntimeException(e);
		}
	}

	protected void addParameterList(SOAPEnvelope envelope, SOAPElement eParent, String typeName, String listName, Map<String, String> params) throws SOAPException
	{
		Name nPara = envelope.createName(typeName, "", XMLA_URI);
		SOAPElement eType = eParent.addChildElement(nPara);
		nPara = envelope.createName(listName, "", XMLA_URI);
		SOAPElement eList = eType.addChildElement(nPara);
		if (params == null)
		{
			return;
		}
		for (Map.Entry<String, String> entry : params.entrySet())
		{
			String tag = entry.getKey();
			String value = entry.getValue();
			nPara = envelope.createName(tag, "", XMLA_URI);
			SOAPElement eTag = eList.addChildElement(nPara);
			eTag.addTextNode(value);
		}
	}

	/**
	 * Sends the SOAP Message over the connection and returns the
	 * Result-SOAP-Message
	 * 
	 * @return Reply-Message
	 */
	protected SOAPMessage executeQuery(SOAPMessage message, URL url)
	{
		try
		{
			
			return connection.call(message, url);
		}
		catch (SOAPException e)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_MESSAGE_CALL_FAILED,
					(Object[])null,
					e);
		}
	}

	/**
	 * Parses the result-Message into this class's structure
	 * 
	 * @param reply
	 *            The reply-Message from the Server
	 */
	protected void parseResult(SOAPMessage reply) throws SOAPException
	{
		SOAPPart soapPart = reply.getSOAPPart();
		SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
		SOAPBody soapBody = soapEnvelope.getBody();
		SOAPElement eElement = null;

		if (log.isDebugEnabled())
		{
			log.debug("XML/A result envelope: " + prettyPrintSOAP(soapEnvelope));
		}
		
		SOAPFault fault = soapBody.getFault();
		if (fault != null)
		{
			handleResultFault(fault);
		}
		
		Name eName = soapEnvelope.createName("ExecuteResponse", "", XMLA_URI);

		// Get the ExecuteResponse-Node
		Iterator<?> responseElements = soapBody.getChildElements(eName);
		if (responseElements.hasNext())
		{
			Object eObj = responseElements.next();
			if (eObj == null)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_XMLA_NULL_ELEMENT,
						new Object[]{"ExecuteResponse"});
			}
			eElement = (SOAPElement) eObj;
		}
		else
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_XMLA_CANNOT_RETRIEVE_ELEMENT,
					new Object[]{"ExecuteResponse"});
		}

		// Get the return-Node
		Name rName = soapEnvelope.createName("return", "", XMLA_URI);
		Iterator<?> returnElements = eElement.getChildElements(rName);
		SOAPElement returnElement = null;
		if (returnElements.hasNext())
		{
			Object eObj = returnElements.next();
			if (eObj == null)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_XMLA_NULL_ELEMENT,
						new Object[]{"return"});
			}
			returnElement = (SOAPElement) eObj;
		}
		else
		{
			// Should be old-Microsoft XMLA-SDK. Try without m-prefix
			Name rName2 = soapEnvelope.createName("return", "", "");
			returnElements = eElement.getChildElements(rName2);
			if (returnElements.hasNext())
			{
				Object eObj = returnElements.next();
				if (eObj == null)
				{
					throw 
						new JRRuntimeException(
							EXCEPTION_MESSAGE_KEY_XMLA_NULL_ELEMENT,
							new Object[]{"return"});
				}
				returnElement = (SOAPElement) eObj;
			}
			else
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_XMLA_CANNOT_RETRIEVE_ELEMENT,
						new Object[]{"return"});
			}
		}

		// Get the root-Node
		Name rootName = soapEnvelope.createName("root", "", MDD_URI);
		SOAPElement rootElement = null;
		Iterator<?> rootElements = returnElement.getChildElements(rootName);
		if (rootElements.hasNext())
		{
			Object eObj = rootElements.next();
			if (eObj == null)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_XMLA_NULL_ELEMENT,
						new Object[]{"root"});
			}
			rootElement = (SOAPElement) eObj;
		}
		else
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_XMLA_CANNOT_RETRIEVE_ELEMENT,
					new Object[]{"root"});
		}
		// Get the OlapInfo-Node
		Name olapInfoName = soapEnvelope.createName("OlapInfo", "", MDD_URI);
		SOAPElement olapInfoElement = null;
		Iterator<?> olapInfoElements = rootElement.getChildElements(olapInfoName);
		if (olapInfoElements.hasNext())
		{
			Object eObj = olapInfoElements.next();
			if (eObj == null)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_XMLA_NULL_ELEMENT,
						new Object[]{"OlapInfo"});
			}
			olapInfoElement = (SOAPElement) eObj;
		}
		else
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_XMLA_CANNOT_RETRIEVE_ELEMENT,
					new Object[]{"OlapInfo"});
		}

		parseOLAPInfoElement(olapInfoElement);

		// Get the Axes Element
		Name axesName = soapEnvelope.createName("Axes", "", MDD_URI);
		SOAPElement axesElement = null;
		Iterator<?> axesElements = rootElement.getChildElements(axesName);
		if (axesElements.hasNext())
		{
			Object eObj = axesElements.next();
			if (eObj == null)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_XMLA_NULL_ELEMENT,
						new Object[]{"Axes"});
			}
			axesElement = (SOAPElement) eObj;
		}
		else
		{
			throw 
			new JRRuntimeException(
				EXCEPTION_MESSAGE_KEY_XMLA_CANNOT_RETRIEVE_ELEMENT,
				new Object[]{"Axes"});
		}

		parseAxesElement(axesElement);

		// Get the CellData Element
		Name cellDataName = soapEnvelope.createName("CellData", "", MDD_URI);
		SOAPElement cellDataElement = null;
		Iterator<?> cellDataElements = rootElement.getChildElements(cellDataName);
		if (cellDataElements.hasNext())
		{
			Object eObj = cellDataElements.next();
			if (eObj == null)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_XMLA_NULL_ELEMENT,
						new Object[]{"CellData"});
			}
			cellDataElement = (SOAPElement) eObj;
		}
		else
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_XMLA_CANNOT_RETRIEVE_ELEMENT,
					new Object[]{"CellData"});
		}
		parseCellDataElement(cellDataElement);
	}

	protected void handleResultFault(SOAPFault fault)
	{
		StringBuilder errorMsg = new StringBuilder();
		errorMsg.append("XML/A fault: ");
		
		String faultString = fault.getFaultString();
		if (faultString != null)
		{
			errorMsg.append(faultString);
			errorMsg.append("; ");
		}

		String faultActor = fault.getFaultActor();
		if (faultActor != null)
		{
			errorMsg.append("Actor: ");
			errorMsg.append(faultActor);
			errorMsg.append("; ");
		}

		String faultCode = fault.getFaultCode();
		if (faultCode != null)
		{
			errorMsg.append("Code: ");
			errorMsg.append(faultCode);
			errorMsg.append("; ");
		}
		
		throw new JRRuntimeException(errorMsg.toString());
	}

	protected void parseOLAPInfoElement(SOAPElement olapInfoElement) throws SOAPException
	{
		// CubeInfo-Element is not needed

		// Get the AxesInfo-Node
		Name axesInfoName = sf.createName("AxesInfo", "", MDD_URI);
		SOAPElement axesElement = null;
		Iterator<?> axesInfoElements = olapInfoElement.getChildElements(axesInfoName);
		if (axesInfoElements.hasNext())
		{
			Object axesObj = axesInfoElements.next();
			if (axesObj == null)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_XMLA_NULL_ELEMENT,
						new Object[]{"AxesInfo"});
			}
			axesElement = (SOAPElement) axesObj;
		}
		else
		{
			throw 
			new JRRuntimeException(
				EXCEPTION_MESSAGE_KEY_XMLA_CANNOT_RETRIEVE_ELEMENT,
				new Object[]{"AxesInfo"});
		}
		
		parseAxesInfoElement(axesElement);

		// CellInfo is not needed
	}

	protected void parseAxesInfoElement(SOAPElement axesInfoElement) throws SOAPException
	{
		// Cycle over AxisInfo-Elements
		Name axisInfoName = sf.createName("AxisInfo", "", MDD_URI);
		Iterator<?> itAxis = axesInfoElement.getChildElements(axisInfoName);
		while (itAxis.hasNext())
		{
			SOAPElement axisElement = (SOAPElement) itAxis.next();
			Name name = sf.createName("name");
			String axisName = axisElement.getAttributeValue(name);
			if (axisName.equals(SLICER_AXIS_NAME))
			{
				continue;
			}

			JRXmlaResultAxis axis = new JRXmlaResultAxis(axisName);
			xmlaResult.addAxis(axis);
			
			if (log.isDebugEnabled())
			{
				log.debug("adding axis: " + axis.getAxisName());
			}
			
			// retrieve the hierarchies by <HierarchyInfo>
			name = sf.createName("HierarchyInfo", "", MDD_URI);
			Iterator<?> itHierInfo = axisElement.getChildElements(name);
			while (itHierInfo.hasNext())
			{
				SOAPElement eHierInfo = (SOAPElement) itHierInfo.next();
				handleHierInfo(axis, eHierInfo);
			}
		}
	}

	protected void parseAxesElement(SOAPElement axesElement) throws SOAPException
	{
		// Cycle over Axis-Elements
		Name aName = sf.createName("Axis", "", MDD_URI);
		Iterator<?> itAxis = axesElement.getChildElements(aName);
		while (itAxis.hasNext())
		{
			SOAPElement axisElement = (SOAPElement) itAxis.next();
			Name name = sf.createName("name");
			String axisName = axisElement.getAttributeValue(name);

			if (axisName.equals(SLICER_AXIS_NAME))
			{
				continue;
			}

			// LookUp for the Axis
			JRXmlaResultAxis axis = xmlaResult.getAxisByName(axisName);

			// retrieve the tuples by <Tuples>
			name = sf.createName("Tuples", "", MDD_URI);
			Iterator<?> itTuples = axisElement.getChildElements(name);
			if (itTuples.hasNext())
			{
				SOAPElement eTuples = (SOAPElement) itTuples.next();
				handleTuplesElement(axis, eTuples);
			}
		}
	}

	protected void parseCellDataElement(SOAPElement cellDataElement) throws SOAPException
	{
		Name name = sf.createName("Cell", "", MDD_URI);
		Iterator<?> itCells = cellDataElement.getChildElements(name);
		while (itCells.hasNext())
		{
			SOAPElement cellElement = (SOAPElement) itCells.next();
			
			Name errorName = sf.createName("Error", "", MDD_URI);
			Iterator<?> errorElems = cellElement.getChildElements(errorName);
			if (errorElems.hasNext())
			{
				handleCellErrors(errorElems);
			}
			
			Name ordinalName = sf.createName("CellOrdinal");
			String cellOrdinal = cellElement.getAttributeValue(ordinalName);

			Object value = null;
			Iterator<?> valueElements = cellElement.getChildElements(sf.createName("Value", "", MDD_URI));
			if (valueElements.hasNext())
			{
				SOAPElement valueElement = (SOAPElement) valueElements.next();
				String valueType = valueElement.getAttribute("xsi:type");
				if (valueType.equals("xsd:int"))
				{
					value = Long.valueOf(valueElement.getValue());
				}
				else if (
					valueType.equals("xsd:double")
					|| valueType.equals("xsd:decimal")
					)
				{
					value = Double.valueOf(valueElement.getValue());
				}
				else
				{
					value = valueElement.getValue();
				}
			}

			String fmtValue = "";
			Iterator<?> fmtValueElements = cellElement.getChildElements(sf.createName("FmtValue", "", MDD_URI));
			if (fmtValueElements.hasNext())
			{
				SOAPElement fmtValueElement = ((SOAPElement) fmtValueElements.next());
				fmtValue = fmtValueElement.getValue();
			}

			int pos = Integer.parseInt(cellOrdinal);
			JRXmlaCell cell = new JRXmlaCell(value, fmtValue);
			xmlaResult.setCell(cell, pos);
		}
	}

	protected void handleCellErrors(Iterator<?> errorElems) throws SOAPException
	{
		SOAPElement errorElem = (SOAPElement) errorElems.next();
		
		StringBuilder errorMsg = new StringBuilder();
		errorMsg.append("Cell error: ");
		
		Iterator<?> descriptionElems = errorElem.getChildElements(sf.createName("Description", "", MDD_URI));
		if (descriptionElems.hasNext())
		{
			SOAPElement descrElem = (SOAPElement) descriptionElems.next();
			errorMsg.append(descrElem.getValue());
			errorMsg.append("; ");
		}
		
		Iterator<?> sourceElems = errorElem.getChildElements(sf.createName("Source", "", MDD_URI));
		if (sourceElems.hasNext())
		{
			SOAPElement sourceElem = (SOAPElement) sourceElems.next();
			errorMsg.append("Source: ");
			errorMsg.append(sourceElem.getValue());
			errorMsg.append("; ");
		}
		
		Iterator<?> codeElems = errorElem.getChildElements(sf.createName("ErrorCode", "", MDD_URI));
		if (codeElems.hasNext())
		{
			SOAPElement codeElem = (SOAPElement) codeElems.next();
			errorMsg.append("Code: ");
			errorMsg.append(codeElem.getValue());
			errorMsg.append("; ");
		}
		
		throw new JRRuntimeException(errorMsg.toString());
	}

	protected void handleHierInfo(JRXmlaResultAxis axis, SOAPElement hierInfoElement) throws SOAPException
	{
		Name name = sf.createName("name");
		String dimName = hierInfoElement.getAttributeValue(name); // Get the Dimension Name

		if (log.isDebugEnabled())
		{
			log.debug("Adding hierarchy: " + dimName);
		}

		JRXmlaHierarchy hier = new JRXmlaHierarchy(dimName);
		axis.addHierarchy(hier);
	}

	protected void handleTuplesElement(JRXmlaResultAxis axis, SOAPElement tuplesElement) throws SOAPException
	{
		Name tName = sf.createName("Tuple", "", MDD_URI);
		for (Iterator<?> itTuple = tuplesElement.getChildElements(tName); itTuple.hasNext();)
		{
			SOAPElement eTuple = (SOAPElement) itTuple.next();
			handleTupleElement(axis, eTuple);
		}
	}

	protected void handleTupleElement(JRXmlaResultAxis axis, SOAPElement tupleElement) throws SOAPException
	{
		JRXmlaMemberTuple tuple = new JRXmlaMemberTuple(axis.getHierarchiesOnAxis().length);
		
		Name memName = sf.createName("Member", "", MDD_URI);
		Iterator<?> itMember = tupleElement.getChildElements(memName);
		int memNum = 0;
		while (itMember.hasNext())
		{
			SOAPElement memElement = (SOAPElement) itMember.next();
			
			Name name = sf.createName("Hierarchy", "", "");
			String hierName = memElement.getAttributeValue(name);
			
			String uName = "";
			Iterator<?> uNameElements = memElement.getChildElements(sf.createName("UName", "", MDD_URI));
			if (uNameElements.hasNext())
			{
				uName = ((SOAPElement) uNameElements.next()).getValue();
			}
			String caption = "";
			Iterator<?> captionElements = memElement.getChildElements(sf.createName("Caption", "", MDD_URI));
			if (captionElements.hasNext())
			{
				caption = ((SOAPElement) captionElements.next()).getValue();
			}
			String lName = "";
			Iterator<?> lNameElements = memElement.getChildElements(sf.createName("LName", "", MDD_URI));
			if (lNameElements.hasNext())
			{
				String levelUniqueName = ((SOAPElement) lNameElements.next()).getValue();
				Matcher matcher = LEVEL_UNIQUE_NAME_PATTERN.matcher(levelUniqueName);
				if (matcher.matches())
				{
					lName = matcher.group(LEVEL_UNIQUE_NAME_PATTERN_NAME_GROUP);
				}
				else
				{
					matcher = HIERARCHY_LEVEL_UNIQUE_NAME_PATTERN.matcher(levelUniqueName);
					if (matcher.matches())
					{
						lName = matcher.group(HIERARCHY_LEVEL_UNIQUE_NAME_PATTERN_NAME_GROUP);
					}
					else
					{
						throw 
							new JRRuntimeException(
								EXCEPTION_MESSAGE_KEY_XMLA_NO_LEVEL_NAME,
								new Object[]{levelUniqueName});
					}
				}
			}
			
			int lNum = 0;
			Iterator<?> lNumElements = memElement.getChildElements(sf.createName("LNum", "", MDD_URI));
			if (lNumElements.hasNext())
			{
				lNum = Integer.parseInt(((SOAPElement) lNumElements.next()).getValue());
			}
			JRXmlaMember member = new JRXmlaMember(caption, uName, hierName, lName, lNum);
			
			if (log.isDebugEnabled())
			{
				log.debug("Adding member: axis - " + axis.getAxisName() + " hierName - " + hierName  + " lName - " + lName + " uName - " + uName);
			}
			tuple.setMember(memNum++, member);
		}

		axis.addTuple(tuple);
	}
	
	protected String prettyPrintSOAP(SOAPElement element)
	{
		final StringWriter sw = new StringWriter();

		try
		{
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(
				new DOMSource(element),
				new StreamResult(sw));
		}
		catch (TransformerException e)
		{
			throw new JRRuntimeException(e);
		}


		return sw.toString();
	}
}
