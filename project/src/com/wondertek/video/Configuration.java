package com.wondertek.video;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;
//import org.xml.sax.SAXException;
/*
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
*/

public class Configuration {
	protected Document doc = null;
	protected String fname = null;
	
	public int load(String filename) {
		doc = null;
		fname = new String(filename);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;

		try {
			builder = factory.newDocumentBuilder();
			File f = new File(fname);
			if (f.exists()) {
				doc = builder.parse(f);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			if (null == doc)
			{
				Util.Trace("Configuration.load file not loaded"+fname);
				doc = builder.newDocument();
				Element root = doc.createElement("alter");
				doc.appendChild(root);
				Node comment = doc.createComment("This is a configxml for cnlive");
				root.appendChild(comment);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (null != doc) {
			Element de = doc.getDocumentElement();
			Util.Trace("Configuration.load root=" + de.getTagName());
			return 1;
		}
		else {
			Util.Trace("Configuration.load can't create any dom");
			return 0;
		}
	}
	
	public int save(String saveas) {
		int ret = 0;
		if (null == saveas)
			saveas = fname;
		if (null == saveas || null == doc)
			return ret;
		
		XmlSerializer serializer = Xml.newSerializer();
		
		try {
			BufferedOutputStream buf = new BufferedOutputStream(new FileOutputStream(saveas));
			serializer.setOutput(buf, "UTF-8");
	        serializer.startDocument("UTF-8", true);
	        
	        Node node = doc.getDocumentElement();
	        writeNode(node, serializer);
	        serializer.endDocument();
	        serializer.flush();
	        buf.flush();
	        buf.close();
	        ret = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	protected void writeNode(Node node, XmlSerializer serializer) throws Exception {
		if (null == node)
			return;

		short ntype = node.getNodeType();
		//String report = String.format("wn: ntype=%d name=%s value=%s", ntype, node.getNodeName(), node.getNodeValue());
		//Util.Trace(report);

		if (Node.COMMENT_NODE == ntype)
			serializer.comment(node.getNodeValue());
		else if (Node.TEXT_NODE == ntype)
			serializer.text(node.getNodeValue());
		else if (Node.CDATA_SECTION_NODE == ntype)
			serializer.cdsect(node.getNodeValue());
		else if (Node.ATTRIBUTE_NODE == ntype)
			serializer.attribute(null, node.getNodeName(), node.getNodeValue());
		else if (Node.ELEMENT_NODE == ntype) {
			serializer.startTag(null, node.getNodeName());
			
			NamedNodeMap attrs = node.getAttributes();
			if (null != attrs) {
				for (int index=0; index < attrs.getLength(); index++)
					writeNode(attrs.item(index), serializer);
			}
			
			NodeList ns = node.getChildNodes();
			if (null != ns) {
				for (int index=0; index < ns.getLength(); index++)
					writeNode(ns.item(index), serializer);
			}
			serializer.endTag(null, node.getNodeName());
		}
		else
			Util.Trace("Configuration.writeNode: unknow node"+node.getNodeName());
	}

	public void addAppointment(String appointmentID, String timeInterval, String program, String url, String warnSysTime)
	{
		if (null != doc && null != doc.getDocumentElement()) {
			Element root = doc.getDocumentElement();

			Element appoint = doc.createElement("appointment");
			root.appendChild(appoint);

			appoint.setAttribute("id", appointmentID);

			Element ti = doc.createElement("timeInterval");
			appoint.appendChild(ti);
			ti.appendChild(doc.createTextNode(timeInterval));

			Element pg = doc.createElement("program");
			appoint.appendChild(pg);
			pg.appendChild(doc.createTextNode(program));

			Element rl = doc.createElement("url");
			appoint.appendChild(rl);
			rl.appendChild(doc.createTextNode(url));

			Element wst = doc.createElement("warnSysTime");
			appoint.appendChild(wst);
			wst.appendChild(doc.createTextNode(warnSysTime));

			save(null);
		}

		return;
	}

	public int getAppointmentNum() {
		int num = 0;
		if (null != doc && null != doc.getDocumentElement()) {
			NodeList ns = doc.getDocumentElement().getElementsByTagName("appointment");
			if (null != ns)
				num = ns.getLength();
		}
		//Util.Trace("Configuration.GetAppointmentNum = " + num);
		return num;
	}

	public void deleteAppointment(int index) {
		Element appoint = getAppointment(index);
		if (null != appoint) {
			appoint.getParentNode().removeChild(appoint);
			save(null);
		}
	}
	
	public void deleteAppointment(String id) {
		Element appoint = getAppointment(id);
		if (null != appoint) {
			appoint.getParentNode().removeChild(appoint);
			save(null);
		}
	}

	protected Element getAppointment(int index) {
		if (null != doc && null != doc.getDocumentElement()) {
			NodeList ns = doc.getDocumentElement().getElementsByTagName("appointment");
			if (null != ns) {
				Node node = ns.item(index);
				if (null != node && Node.ELEMENT_NODE == node.getNodeType())
					return (Element)node;
			}
		}
		return null;
	}
	
	protected Element getAppointment(String id) {
		if (null != id && null != doc && null != doc.getDocumentElement()) {
			NodeList ns = doc.getDocumentElement().getElementsByTagName("appointment");
			if (null != ns) {
				int count = ns.getLength();
				while (--count >= 0) {
					Element appoint = (Element)ns.item(count);
					if (id.equals(appoint.getAttribute("id")))
						return appoint;
				}
			}
		}
		return null;
	}

	protected String getAppointmentValue(Element appoint, String key) {
		if (null != appoint) {
			NodeList ns = appoint.getElementsByTagName(key);
			if (null != ns && ns.getLength() > 0) {
				Node node = ns.item(0).getFirstChild();
				if (null != node)
					return node.getNodeValue();
			}
		}
		return null;
	}

	protected void setAppointmentValue(Element appoint, String key, String value) {
		if (null != appoint) {
			NodeList ns = appoint.getElementsByTagName(key);
			if (null != ns && ns.getLength() > 0) {
				Node node = ns.item(0).getFirstChild();
				if (null != node && node.getNodeValue() != value) {
					node.setNodeValue(value);
					save(null);
				}
			}
		}
	}

	public String getWarnSysTimeValue(int index) {
		Element appoint = getAppointment(index);
		return getAppointmentValue(appoint, "warnSysTime");
	}

	public String getTimeIntervalValue(int index) {
		Element appoint = getAppointment(index);
		return getAppointmentValue(appoint, "timeInterval");
	}

	public String getProgramValue(int index) {
		Element appoint = getAppointment(index);
		return getAppointmentValue(appoint, "program");
	}

	public String getUrlValue(int index) {
		Element appoint = getAppointment(index);
		return getAppointmentValue(appoint, "url");
	}

	public void modifyTimeInterval(int index, String time) {
		Element appoint = getAppointment(index);
		setAppointmentValue(appoint, "timeInterval", time);
	}

}
