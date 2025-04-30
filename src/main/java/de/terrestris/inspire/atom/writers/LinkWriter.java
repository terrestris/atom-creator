package de.terrestris.inspire.atom.writers;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class LinkWriter {
  public static void writeLink(XMLStreamWriter writer, String link, String rel, String type, String title) throws XMLStreamException {
    writer.writeEmptyElement(AtomFeedWriter.ATOM, "link");
    writer.writeAttribute("href", link);
    writer.writeAttribute("rel", rel);
    writer.writeAttribute("type", type);
    if (title != null) {
      writer.writeAttribute("title", title);
    }
  }
}
