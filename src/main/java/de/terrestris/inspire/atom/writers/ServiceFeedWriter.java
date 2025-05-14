package de.terrestris.inspire.atom.writers;

import de.terrestris.inspire.atom.PathBuilder;
import de.terrestris.inspire.atom.config.Config;
import de.terrestris.inspire.atom.config.Entry;
import de.terrestris.inspire.atom.config.File;
import org.geotools.api.referencing.FactoryException;
import org.geotools.referencing.CRS;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.time.Instant;

import static de.terrestris.utils.xml.XmlUtils.writeSimpleElement;

public class ServiceFeedWriter {
  public static void write(XMLStreamWriter writer, Config config) throws XMLStreamException, FactoryException {
    var serviceConfig = config.getService();

    AtomFeedWriter.writePrefix(writer);
    AtomFeedWriter.writeStartElement(writer);

    writeSimpleElement(writer, AtomFeedWriter.ATOM, "title", serviceConfig.getTitle());

    var metadataLink = serviceConfig.getMetadata();
    var selfLink = PathBuilder.build(serviceConfig.getFeedBasePath(), serviceConfig.getId() + ".xml");
    AtomFeedWriter.writeMetadata(writer, serviceConfig, metadataLink, selfLink);

    writer.writeEmptyElement(AtomFeedWriter.ATOM, "category");
    writer.writeAttribute("term", "http://inspire.ec.europa.eu/metadata-codelist/SpatialDataServiceCategory/infoFeatureAccessService");
    writer.writeAttribute("scheme", "http://inspire.ec.europa.eu/metadata-codelist/SpatialDataServiceCategory");

    for (var entry : serviceConfig.getEntries()) {
      writeEntry(writer, entry, config);
    }

    AtomFeedWriter.writeEndElement(writer);
  }

  private static void writeEntry(XMLStreamWriter writer, Entry entry, Config config) throws XMLStreamException, FactoryException {
    var serviceConfig = config.getService();

    writer.writeStartElement(AtomFeedWriter.ATOM, "entry");
    writeSimpleElement(writer, AtomFeedWriter.ATOM, "title", entry.getTitle());
    writeSimpleElement(writer, AtomFeedWriter.DLS, "spatial_dataset_identifier_code", entry.getId());
    writeSimpleElement(writer, AtomFeedWriter.DLS, "spatial_dataset_identifier_namespace", serviceConfig.getIdentifierNamespace());
    LinkWriter.writeLink(writer, entry.getMetadata(), "describedby", "application/xml", "Metadaten");
    var entryLink = PathBuilder.build(serviceConfig.getFeedBasePath(), entry.getId() + ".xml");
    LinkWriter.writeLink(writer, entryLink, "alternate", "application/atom+xml", "Selbstreferenz");
    writeSimpleElement(writer, AtomFeedWriter.ATOM, "id", serviceConfig.getId());
    writeSimpleElement(writer, AtomFeedWriter.ATOM, "rights", serviceConfig.getLicense());
    writeSimpleElement(writer, AtomFeedWriter.ATOM, "updated", Instant.now().toString());
    writeSimpleElement(writer, AtomFeedWriter.ATOM, "summary", entry.getSummary());
    var bbox = serviceConfig.getBbox();
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

    var crsList = config.getDatasets().stream()
      .filter(dataset -> dataset.getId().equals(entry.getId()))
      .flatMap(dataset -> dataset.getFiles().stream().map(File::getCrs))
      .distinct()
      .toList();

    for (var crs : crsList) {
      var current = CRS.decode(crs);
      var code = current.getIdentifiers().iterator().next().getCode();
      writer.writeEmptyElement(AtomFeedWriter.ATOM, "category");
      writer.writeAttribute("term", "http://www.opengis.net/def/crs/EPSG/" + code);
      writer.writeAttribute("label", current.getName().getCode());
    }

    writer.writeEndElement(); // entry
  }
}
