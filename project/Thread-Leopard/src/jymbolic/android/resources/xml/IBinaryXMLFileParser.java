package jymbolic.android.resources.xml;

import java.io.IOException;
import java.util.List;


public interface IBinaryXMLFileParser {

	/**
	 * Parses the binary XML file with the AXMLPrinter2 library
	 * @param	buffer					The buffer to be parsed
	 * @throws	IOException				if an I/O error occurs.
	 */
	public void parseFile(byte[] buffer) throws IOException;

	/**
	 * Returns the Android XML document.
	 * 
	 * @return	the Android XML document
	 */
	public AXmlDocument getDocument();

	/**
	 * Returns a list containing all nodes of the xml document which have the given tag.
	 * 
	 * @param	tag		the tag being search for
	 * @return	list pointing on all nodes which have the given tag.
	 */
	public List<AXmlNode> getNodesWithTag(String tag);

}
