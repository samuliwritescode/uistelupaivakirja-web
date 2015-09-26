package fi.capeismi.fish.uistelupaivakirja.web.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import fi.capeismi.fish.uistelupaivakirja.web.model.ContainsMap;
import fi.capeismi.fish.uistelupaivakirja.web.model.TableView;

public class Transformers {

	public static Element resultSetToXml(String elementName, ResultSet res, ResultSetMetaData meta)
			throws ParserConfigurationException, FactoryConfigurationError, SQLException {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element root = doc.createElement("root");

		while (res.next()) {
			Element record = doc.createElement(elementName);
			for (int i = 1; i < meta.getColumnCount() + 1; i++) {
				Element prop = doc.createElement(meta.getColumnName(i));
				String text = res.getString(meta.getColumnName(i));
				if (text != null) {
					prop.appendChild(doc.createTextNode(text));
				}

				record.appendChild(prop);
			}
			root.appendChild(record);
		}

		return root;
	}

	public static void resultSetToMapContainer(ResultSet res, ResultSetMetaData meta, ContainsMap target)
			throws SQLException {
		while (res.next()) {
			Map<String, String> row = new HashMap<>();
			for (int i = 1; i < meta.getColumnCount() + 1; i++) {
				String key = meta.getColumnName(i);
				String value = res.getString(meta.getColumnName(i));
				if (value != null) {
					row.put(key, value);
				}
			}
			target.add(row);
		}
	}

	public static ResultSet sqlToResultSet(String sql) throws SQLException {
		DriverManager.registerDriver(new com.mysql.jdbc.Driver());
		Connection conn = DriverManager.getConnection("jdbc:mysql://dev.capeismi.fi:3306/trolling", "root", "");
		ResultSet res = conn.createStatement().executeQuery(sql);
		return res;
	}

	public static DOMSource objectToDomSource(Object content)
			throws JAXBException, PropertyException, ParserConfigurationException, FactoryConfigurationError {
		JAXBContext ctx = JAXBContext.newInstance(content.getClass());
		Marshaller m = ctx.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		Node node = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		m.marshal(content, node);
		return new DOMSource(node);
	}

	public static TableView sqlToTableView(String sql, String name) throws SQLException {
		return resultSetToTableView(sqlToResultSet(sql), name);
	}

	private static TableView resultSetToTableView(ResultSet res, final String name) {
		return new TableView() {

			@Override
			public List<Map<String, String>> getTable() {
				List<Map<String, String>> retval = new ArrayList<>();
				try {
					ResultSetMetaData meta = res.getMetaData();
					while (res.next()) {
						Map<String, String> row = new HashMap<>();
						for (int i = 1; i < meta.getColumnCount() + 1; i++) {
							String key = meta.getColumnName(i);
							String value = res.getString(meta.getColumnName(i));
							if (value != null) {
								row.put(key, value);
							}
						}
						retval.add(row);
					}
				} catch (Exception e) {
					// Well snap.
				}

				return retval;
			}

			@Override
			public String getName() {
				return name;
			}
		};
	}

}
