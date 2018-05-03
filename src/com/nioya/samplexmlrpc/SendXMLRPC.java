package com.nioya.samplexmlrpc;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcClientException;
import org.apache.xmlrpc.client.XmlRpcTransport;
import org.apache.xmlrpc.client.XmlRpcTransportFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class SendXMLRPC {
	private String connectionURL = "";

	public SendXMLRPC(String connectionURL) {
		this.connectionURL = connectionURL;
	}

	@SuppressWarnings("unchecked")
	public void send() {

		Map<String, Object> responseMap = new HashMap<String, Object>();
		generalInfoLogger("version: 1.0.0");

		Map<String, Object> param = new HashMap<String, Object>();
		Map<String, Object> headers = new HashMap<String, Object>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> sampleParameter = new HashMap<String, Object>();

		sampleParameter.put("type", "key");
		sampleParameter.put("id", "12345678");
		parameters.put("subscriber", sampleParameter);

		/*
		 * If spesific date is needed SimpleDateFormat dateFormat = new
		 * SimpleDateFormat("yyyyMMdd'T'hh:mm:ss");
		 * 
		 * Date formattedDate = new Date(); try{ formattedDate =
		 * dateFormat.parse(keyValuePairs.get("TIMESTAMP")); }catch
		 * (ParseException e){ Date date = new Date(); String tempFormattedDate
		 * = dateFormat.format(date); try { formattedDate =
		 * dateFormat.parse(tempFormattedDate); } catch (ParseException e1) {
		 * logger.error(e1.getMessage() + "Time: " + tempFormattedDate); } }
		 */

		headers.put("serviceName", "testServiceName");
		headers.put("systemType", "testSystemType");
		headers.put("timestamp", new Date());
		headers.put("transactionId", "12345678");

		headers.put("version", "1.0");

		Map<String, Object> addListId = new HashMap<String, Object>();
		addListId.put("operation", "add");
		addListId.put("listId", "list1");
		addListId.put("number", "87654321");
		Vector<Map<String, Object>> addOperation = new Vector<Map<String, Object>>();
		addOperation.addElement(addListId);
		parameters.put("operations", addOperation);

		param.put("header", headers);
		param.put("parameters", parameters);

		Vector<Object> params = new Vector<Object>();
		params.add(param);

		List<Object> check_params = new ArrayList<>();
		check_params.add(param);
		generalInfoLogger("params:" + check_params.toString());

		generalInfoLogger("URL IS:" + this.connectionURL);
		try {

			XmlRpcClient client = new XmlRpcClient();
			XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
			URL targetURL = new URL(this.connectionURL);
			config.setServerURL(targetURL);
			config.setEnabledForExtensions(true);
			client.setConfig(config);
			generalInfoLogger("Config Settings Done");
			MessageLoggingTransport loggingTransport = new MessageLoggingTransport(client);
			final XmlRpcTransportFactory transportFactory = new XmlRpcTransportFactory() {
				public XmlRpcTransport getTransport() {

					return loggingTransport;
				}
			};
			responseMap = (Map<String, Object>) client.execute("method:name", check_params);

			if (responseMap == null) {
				generalInfoLogger("NULL HEADER");
			} else {
				generalInfoLogger("SUCCESS RESPONSE");
				Map<String, Object> responseHeader = (Map<String, Object>) responseMap.get("header");
				int respCode = (int) responseHeader.get("responseCode");
				String respMessage = (String) responseHeader.get("responseMessage");
				if (respCode == 0) {
					generalInfoLogger("SUCCESS OPERATION");
				} else {
					generalInfoLogger("ERROR OPERATION. ErrorCode:" + respCode + " Error Details:" + respMessage);
				}
				// DO SMT

			}

			String request = loggingTransport.getRequest();
			String response = loggingTransport.getResponse();

			generalInfoLogger("request" + prettyXML(request));
			generalInfoLogger("response" + prettyXML(response));

			generalInfoLogger("responseMap:" + responseMap.toString());

		} catch (MalformedURLException e) {
			generalInfoLogger(e.getMessage());
		} catch (XmlRpcClientException e) {
			generalInfoLogger(e.getMessage());
		} catch (XmlRpcException e) {
			generalInfoLogger(e.getMessage());
		}
	}

	private void generalInfoLogger(String log) {
		System.out.println("LOG:" + log);
	}

	private String prettyXML(String xml) {
		return prettyXML(xml, 1);
	}

	private String prettyXML(String xml, int indent) {
		try {
			// Turn xml string into a document
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));

			// Remove whitespaces outside tags
			document.normalize();
			XPath xPath = XPathFactory.newInstance().newXPath();
			NodeList nodeList = (NodeList) xPath.evaluate("//text()[normalize-space()='']", document,
					XPathConstants.NODESET);

			for (int i = 0; i < nodeList.getLength(); ++i) {
				Node node = nodeList.item(i);
				node.getParentNode().removeChild(node);
			}

			// Setup pretty print options
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(indent));

			// Return pretty print xml string
			StringWriter stringWriter = new StringWriter();
			transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
			return stringWriter.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
