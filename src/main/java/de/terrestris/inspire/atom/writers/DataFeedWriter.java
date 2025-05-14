package de.terrestris.inspire.atom.writers;

import de.terrestris.inspire.atom.PathBuilder;
import de.terrestris.inspire.atom.config.Dataset;
import de.terrestris.inspire.atom.config.Entry;
import de.terrestris.inspire.atom.config.File;
import de.terrestris.inspire.atom.config.Service;
import org.geotools.api.referencing.FactoryException;
import org.geotools.referencing.CRS;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.time.Instant;
import java.util.List;

import static de.terrestris.utils.xml.XmlUtils.writeSimpleElement;

public class DataFeedWriter {
  public static void write(XMLStreamWriter writer, Service serviceConfig, Entry entry, List<Dataset> datasets) throws XMLStreamException, FactoryException {
    AtomFeedWriter.writePrefix(writer);
    AtomFeedWriter.writeStartElement(writer);

    writeSimpleElement(writer, AtomFeedWriter.ATOM, "title", entry.getTitle());

    var metadataLink = entry.getMetadata();
    var selfLink = PathBuilder.build(serviceConfig.getFeedBasePath(), entry.getId() + ".xml");
    var upLink = PathBuilder.build(serviceConfig.getFeedBasePath(), serviceConfig.getId() + ".xml");
    AtomFeedWriter.writeMetadata(writer, serviceConfig, metadataLink, selfLink, upLink);

    for (var dataset : datasets) {
      for (var file : dataset.getFiles()) {
        writeFileEntry(writer, entry, dataset, file);
      }
    }

    AtomFeedWriter.writeEndElement(writer);
  }

  private static void writeFileEntry(XMLStreamWriter writer, Entry entry, Dataset dataset, File file) throws FactoryException, XMLStreamException {
    var current = CRS.decode(file.getCrs());
    var code = current.getIdentifiers().iterator().next().getCode();
    var url = PathBuilder.build(entry.getBasePath(), file.getFilename());

    writer.writeStartElement(AtomFeedWriter.ATOM, "entry");
    writeSimpleElement(writer, AtomFeedWriter.ATOM, "title", entry.getTitle() + " from " + dataset.getDate() + " as " + file.getTypeTitle() + " in CRS " + file.getCrs());
    LinkWriter.writeLink(writer, url, "alternate", file.getMimeType(), entry.getTitle());
    writeSimpleElement(writer, AtomFeedWriter.ATOM, "id", file.getFilename());
    writeSimpleElement(writer, AtomFeedWriter.ATOM, "updated", Instant.now().toString());
    writer.writeEmptyElement(AtomFeedWriter.ATOM, "category");
    writer.writeAttribute("term", "http://www.opengis.net/def/crs/EPSG/" + code);
    writer.writeAttribute("label", current.getName().getCode());
    writer.writeEndElement(); // entry
  }
}
