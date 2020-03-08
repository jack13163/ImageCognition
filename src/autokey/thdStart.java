package autokey;//package autokey;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class thdStart implements Runnable {
	Robot robot;
	static boolean state;
	Document doc = null;
	String nodename = null;
	String attributename = null;
	String attributevalue = null;
	long loop;

	public void init(String xmlFile) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		doc = db.parse(new File(xmlFile));
	}

	thdStart() throws Exception {
		init("info.xml");

		try {
			robot = new Robot();
		} catch (AWTException e1) {
			e1.printStackTrace();
		}
		state = false;
		new Thread(this).start();
	}

	public void run() {

		NodeList nodeList = doc.getElementsByTagName("loop");
		Node fatherNode = nodeList.item(0);
		NamedNodeMap attributes = fatherNode.getAttributes();

		try {
			loop = Long.parseLong(attributes.item(0).getNodeValue());
		} catch (Exception e) {
			loop = -1;
		}
		if (loop == -1) {
			while (true) {
				synchronized (this) {
					if (state) {
						state = false;
						break;
					}
				}
				doLoop(fatherNode);
			}
		}else if(loop>0){
			for(int i=0;i<loop;i++){
				synchronized (this) {
					if (state) {
						state = false;
						break;
					}
				}
				doLoop(fatherNode);
			}
			GamePanel.btn3.setEnabled(true);
			GamePanel.btn4.setEnabled(false);
		}

	}

	private void doLoop(Node fatherNode) {
		NodeList childNodes = fatherNode.getChildNodes();
		// System.out.println(childNodes.getLength());
		for (int j = 0; j < childNodes.getLength(); j++) {
			Node childNode = childNodes.item(j);

			if (childNode instanceof Element) {
				nodename = childNode.getNodeName();
				NamedNodeMap loopattributes = childNodes.item(j)
						.getAttributes();
				Node loopattribute = loopattributes.item(0);
				attributename = loopattribute.getNodeName();
				attributevalue = loopattribute.getNodeValue();

				if ("delay".equals(nodename)) {
					robot.delay(Integer.parseInt(attributevalue));
				}
				if (("mouseMove".equals(nodename) || "move".equals(nodename))
						&& ("x".equals(attributename))) {
					robot.mouseMove(Integer.parseInt(attributevalue), Integer
							.parseInt(loopattributes.item(1).getNodeValue()));
				}
				if ("mousePress".equals(nodename) || "press".equals(nodename)) {
					if ("left".equals(attributevalue)) {
						robot.mousePress(16);
					}
					if ("right".equals(attributevalue)) {
						robot.mousePress(8);
					}
					if ("center".equals(attributevalue)) {
						robot.mousePress(8);
					}
				}
				if ("mouseRelease".equals(nodename)
						|| "release".equals(nodename)) {
					if ("left".equals(attributevalue)) {
						robot.mouseRelease(16);
					}
					if ("right".equals(attributevalue)) {
						robot.mouseRelease(8);
					}
					if ("center".equals(attributevalue)) {
						robot.mouseRelease(4);
					}
				}
				if ("mouseWheel".equals(nodename) || "wheel".equals(nodename)) {
					robot.mouseWheel(Integer.parseInt(attributevalue));
				}
				if ("keyPress".equals(nodename)) {
					robot.keyPress(Integer.parseInt(attributevalue));
				}
				if ("keyRelease".equals(nodename)) {
					robot.keyRelease(Integer.parseInt(attributevalue));
				}

			}
		}
	}

}
