package com.nioya.samplexmlrpc;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcClientException;

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

}
