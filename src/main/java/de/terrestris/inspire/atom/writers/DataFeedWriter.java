package de.terrestris.inspire.atom.writers;

import de.terrestris.inspire.atom.config.Config;
import de.terrestris.inspire.atom.config.WfsConfig;
import de.terrestris.inspire.atom.config.Entry;
import de.terrestris.inspire.atom.config.File;
import de.terrestris.inspire.atom.WfsFetcher;
import org.geotools.api.referencing.FactoryException;
import org.geotools.referencing.CRS;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.time.Instant;

import static de.terrestris.utils.xml.XmlUtils.writeSimpleElement;

public class DataFeedWriter {
  public static void write(XMLStreamWriter writer, Config config, Entry entry) throws XMLStreamException, FactoryException, IOException {
    AtomFeedWriter.writePrefix(writer);
    AtomFeedWriter.writeStartElement(writer);

    writeSimpleElement(writer, AtomFeedWriter.ATOM, "title", entry.getTitle());

    var metadataLink = entry.getMetadata();
    var selfLink = config.getLocation() + entry.getId() + ".xml";
    var upLink =config.getLocation() + config.getId() + ".xml";
    AtomFeedWriter.writeMetadata(writer, config, metadataLink, selfLink, upLink);

    if (entry.getFiles() != null) {
      for (var file : entry.getFiles()) {
        writeFileEntry(writer, entry, file);
      }
    }

    var cfg = entry.getWfsConfig();
    if (cfg != null) {
      var wfsFetcher = new WfsFetcher(cfg.getUrl(), cfg.getFeatureType());
      for (var crs : cfg.getCrs()) {
        for (var format : cfg.getFormats()) {
          writeWfsEntry(writer, config, entry, crs, format, wfsFetcher, cfg);
        }
      }
    }

    AtomFeedWriter.writeEndElement(writer);
  }

  private static void writeWfsEntry(XMLStreamWriter writer, Config config, Entry entry, String crs, String format, WfsFetcher wfsFetcher, WfsConfig cfg) throws IOException, XMLStreamException, FactoryException {
    wfsFetcher.createFile(crs, format, cfg.getFeatureType() + "_" + format + "_" + crs);
    var current = CRS.decode(crs);
    var code = current.getIdentifiers().iterator().next().getCode();
    writer.writeStartElement(AtomFeedWriter.ATOM, "entry");
    writeSimpleElement(writer, AtomFeedWriter.ATOM, "title", entry.getTitle() + " in CRS " + crs);
    LinkWriter.writeLink(writer, config.getLocation() + cfg.getFeatureType() + "_" + format + "_" + crs, "alternate", format, entry.getTitle());
    writeSimpleElement(writer, AtomFeedWriter.ATOM, "id", config.getLocation() + cfg.getFeatureType() + "_" + format + "_" + crs);
    writeSimpleElement(writer, AtomFeedWriter.ATOM, "updated", Instant.now().toString());
    writer.writeEmptyElement(AtomFeedWriter.ATOM, "category");
    writer.writeAttribute("term", "http://www.opengis.net/def/crs/EPSG/" + code);
    writer.writeAttribute("label", current.getName().getCode());
    writer.writeEndElement(); // entry
  }

  private static void writeFileEntry(XMLStreamWriter writer, Entry entry, File file) throws FactoryException, XMLStreamException {
    var current = CRS.decode(file.getCrs());
    var code = current.getIdentifiers().iterator().next().getCode();
    writer.writeStartElement(AtomFeedWriter.ATOM, "entry");
    writeSimpleElement(writer, AtomFeedWriter.ATOM, "title", entry.getTitle() + " as " + file.getType() + " in CRS " + file.getCrs());
    LinkWriter.writeLink(writer, file.getFilename(), "alternate", file.getMimeType(), entry.getTitle());
    writeSimpleElement(writer, AtomFeedWriter.ATOM, "id", file.getFilename());
    writeSimpleElement(writer, AtomFeedWriter.ATOM, "updated", Instant.now().toString());
    writer.writeEmptyElement(AtomFeedWriter.ATOM, "category");
    writer.writeAttribute("term", "http://www.opengis.net/def/crs/EPSG/" + code);
    writer.writeAttribute("label", current.getName().getCode());
    writer.writeEndElement(); // entry
  }
}
