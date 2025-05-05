package de.terrestris.inspire.atom.writers;

import de.terrestris.inspire.atom.DataPathsAndUrls;
import de.terrestris.inspire.atom.config.*;
import org.geotools.api.referencing.FactoryException;
import org.geotools.referencing.CRS;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.Instant;

import static de.terrestris.utils.xml.XmlUtils.writeSimpleElement;

public class DataFeedWriter {
  public static void write(XMLStreamWriter writer, DataPathsAndUrls paths, Config config, Entry entry) throws XMLStreamException, FactoryException, IOException {
    AtomFeedWriter.writePrefix(writer);
    AtomFeedWriter.writeStartElement(writer);

    writeSimpleElement(writer, AtomFeedWriter.ATOM, "title", entry.getTitle());

    var metadataLink = entry.getMetadata();
    var selfLink = paths.getFeedUrl(entry.getId()).toString();
    var upLink = paths.getFeedUrl(config.getId()).toString();
    AtomFeedWriter.writeMetadata(writer, config, metadataLink, selfLink, upLink);

    if (entry.getFiles() != null) {
      for (var file : entry.getFiles()) {
        writeFileEntry(writer, entry, file, paths);
      }
    }

    var wfsConfig = entry.getWfsConfig();
    if (wfsConfig != null) {
      for (var crs : wfsConfig.getCrs()) {
        for (var format : wfsConfig.getFormats()) {
          writeWfsEntry(writer, paths, entry, crs, format);
        }
      }
    }

    AtomFeedWriter.writeEndElement(writer);
  }

  private static void writeWfsEntry(XMLStreamWriter writer, DataPathsAndUrls paths, Entry entry, String crs, WfsFormat format) throws IOException, XMLStreamException, FactoryException {
    var url = paths.getCacheFileUrl(entry, format, crs).toString();
    var current = CRS.decode(crs);
    var code = current.getIdentifiers().iterator().next().getCode();

    writer.writeStartElement(AtomFeedWriter.ATOM, "entry");
    writeSimpleElement(writer, AtomFeedWriter.ATOM, "title", entry.getTitle() + " as " + format.getTypeTitle() + " in CRS " + crs);
    LinkWriter.writeLink(writer, url, "alternate", format.getMimeType(), entry.getTitle());
    writeSimpleElement(writer, AtomFeedWriter.ATOM, "id", entry.getId() + "_" + format.getTypeTitle() + "_" + code);
    writeSimpleElement(writer, AtomFeedWriter.ATOM, "updated", Instant.now().toString());
    writer.writeEmptyElement(AtomFeedWriter.ATOM, "category");
    writer.writeAttribute("term", "http://www.opengis.net/def/crs/EPSG/" + code);
    writer.writeAttribute("label", current.getName().getCode());
    writer.writeEndElement(); // entry
  }

  private static void writeFileEntry(XMLStreamWriter writer, Entry entry, File file, DataPathsAndUrls paths) throws FactoryException, XMLStreamException, MalformedURLException {
    var current = CRS.decode(file.getCrs());
    var code = current.getIdentifiers().iterator().next().getCode();
    var url = paths.getFileUrl(file.getFilename()).toString();

    writer.writeStartElement(AtomFeedWriter.ATOM, "entry");
    writeSimpleElement(writer, AtomFeedWriter.ATOM, "title", entry.getTitle() + " as " + file.getTypeTitle() + " in CRS " + file.getCrs());
    LinkWriter.writeLink(writer, url, "alternate", file.getMimeType(), entry.getTitle());
    writeSimpleElement(writer, AtomFeedWriter.ATOM, "id", file.getFilename());
    writeSimpleElement(writer, AtomFeedWriter.ATOM, "updated", Instant.now().toString());
    writer.writeEmptyElement(AtomFeedWriter.ATOM, "category");
    writer.writeAttribute("term", "http://www.opengis.net/def/crs/EPSG/" + code);
    writer.writeAttribute("label", current.getName().getCode());
    writer.writeEndElement(); // entry
  }
}
