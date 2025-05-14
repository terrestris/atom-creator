package de.terrestris.inspire.atom.writers;

import de.terrestris.inspire.atom.config.Service;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.time.Instant;

import static de.terrestris.utils.xml.XmlUtils.writeSimpleElement;

public class AtomFeedWriter {
  public static final String ATOM = "http://www.w3.org/2005/Atom";

  public static final String DLS = "http://inspire.ec.europa.eu/schemas/inspire_dls/1.0";

  public static final String GEORSS = "http://www.georss.org/georss";

  public static void writePrefix(XMLStreamWriter writer) throws XMLStreamException {
    writer.setDefaultNamespace(ATOM);
    writer.setPrefix("inspire_dls", DLS);
    writer.setPrefix("georss", GEORSS);
    writer.writeStartDocument();
  }

  public static void writeStartElement(XMLStreamWriter writer) throws XMLStreamException {
    writer.writeStartElement(ATOM, "feed");
    writer.writeDefaultNamespace(ATOM);
    writer.writeNamespace("inspire_dls", DLS);
    writer.writeNamespace("georss", GEORSS);
  }

  public static void writeMetadata(XMLStreamWriter writer, Service config, String selfLink, String describedbyLink) throws XMLStreamException {
    writeMetadata(writer, config, selfLink, describedbyLink, null);
  }

  public static void writeMetadata(XMLStreamWriter writer, Service config, String selfLink, String describedbyLink, String upLink) throws XMLStreamException {
    LinkWriter.writeLink(writer, selfLink, "self", "application/atom+xml", "Selbstreferenz");
    if (upLink != null) {
      LinkWriter.writeLink(writer, upLink, "up", "application/atom+xml", "Elternreferenz");
    }
    LinkWriter.writeLink(writer, describedbyLink, "describedby", "application/xml", "Metadaten");
    writeSimpleElement(writer, ATOM, "id", config.getId());
    writeSimpleElement(writer, ATOM, "rights", config.getLicense());
    writeSimpleElement(writer, ATOM, "updated", Instant.now().toString());
    writer.writeStartElement(ATOM, "author");
    writeSimpleElement(writer, ATOM, "name", config.getAuthor());
    writeSimpleElement(writer, ATOM, "email", config.getEmail());
    writer.writeEndElement(); // author
  }

  public static void writeEndElement(XMLStreamWriter writer) throws XMLStreamException {
    writer.writeEndElement(); // feed
  }
}
