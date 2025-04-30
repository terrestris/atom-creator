package de.terrestris.inspire.atom.writers;

import de.terrestris.inspire.atom.config.Config;
import de.terrestris.inspire.atom.config.Entry;
import org.geotools.api.referencing.FactoryException;
import org.geotools.referencing.CRS;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.time.Instant;

import static de.terrestris.utils.xml.XmlUtils.writeSimpleElement;

public class ServiceFeedWriter {
  public static void write(XMLStreamWriter writer, Config config) throws XMLStreamException, FactoryException {
    AtomFeedWriter.writePrefix(writer);
    AtomFeedWriter.writeStartElement(writer);

    writeSimpleElement(writer, AtomFeedWriter.ATOM, "title", config.getTitle());

    var metadataLink = config.getMetadata();
    var selfLink = config.getLocation() + config.getId() + ".xml";
    AtomFeedWriter.writeMetadata(writer, config, metadataLink, selfLink);

    writer.writeEmptyElement(AtomFeedWriter.ATOM, "category");
    writer.writeAttribute("term", "http://inspire.ec.europa.eu/metadata-codelist/SpatialDataServiceCategory/infoFeatureAccessService");
    writer.writeAttribute("scheme", "http://inspire.ec.europa.eu/metadata-codelist/SpatialDataServiceCategory");

    for (var entry : config.getEntries()) {
      writeEntry(writer, entry, config);
    }

    AtomFeedWriter.writeEndElement(writer);
  }

  private static void writeEntry(XMLStreamWriter writer, Entry entry, Config config) throws XMLStreamException, FactoryException {
    writer.writeStartElement(AtomFeedWriter.ATOM, "entry");
    writeSimpleElement(writer, AtomFeedWriter.ATOM, "title", entry.getTitle());
    writeSimpleElement(writer, AtomFeedWriter.DLS, "spatial_dataset_identifier_code", entry.getId());
    writeSimpleElement(writer, AtomFeedWriter.DLS, "spatial_dataset_identifier_namespace", config.getIdentifierNamespace());
    LinkWriter.writeLink(writer, entry.getMetadata(), "describedby", "application/xml", "Metadaten");
    LinkWriter.writeLink(writer, config.getLocation() + entry.getId() + ".xml", "alternate", "application/atom+xml", "Selbstreferenz");
    writeSimpleElement(writer, AtomFeedWriter.ATOM, "id", config.getLocation() + entry.getId() + ".xml");
    writeSimpleElement(writer, AtomFeedWriter.ATOM, "rights", config.getLicense());
    writeSimpleElement(writer, AtomFeedWriter.ATOM, "updated", Instant.now().toString());
    writeSimpleElement(writer, AtomFeedWriter.ATOM, "summary", entry.getSummary());
    var bbox = config.getBbox();
    var polygon = String.format(
      "%s %s %s %s %s %s %s %s %s %s",
      bbox.getMiny(),
      bbox.getMinx(),
      bbox.getMaxy(),
      bbox.getMinx(),
      bbox.getMaxy(),
      bbox.getMaxx(),
      bbox.getMiny(),
      bbox.getMaxx(),
      bbox.getMiny(),
      bbox.getMinx()
    );
    writeSimpleElement(writer, AtomFeedWriter.GEORSS, "polygon", polygon);
    if (entry.getFiles() != null) {
      for (var file : entry.getFiles()) {
        var current = CRS.decode(file.getCrs());
        var code = current.getIdentifiers().iterator().next().getCode();
        writer.writeEmptyElement(AtomFeedWriter.ATOM, "category");
        writer.writeAttribute("term", "http://www.opengis.net/def/crs/EPSG/" + code);
        writer.writeAttribute("label", current.getName().getCode());
      }
    }
    // TODO WFS
    writer.writeEndElement(); // entry
  }
}
