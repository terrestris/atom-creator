package de.terrestris.inspire.atom.writers;

import de.terrestris.inspire.atom.config.*;
import org.geotools.api.referencing.FactoryException;
import org.geotools.referencing.CRS;
import org.springframework.web.util.DefaultUriBuilderFactory;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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

    var wfsConfig = entry.getWfsConfig();
    if (wfsConfig != null) {
//      var wfsFetcher = new WfsFetcher(cfg.getUrl(), cfg.getFeatureType());
      for (var crs : wfsConfig.getCrs()) {
        for (var format : wfsConfig.getFormats()) {
          writeWfsEntry(writer, config, entry, crs, format, wfsConfig);
        }
      }
    }

    AtomFeedWriter.writeEndElement(writer);
  }

  private static void writeWfsEntry(XMLStreamWriter writer, Config config, Entry entry, String crs, WfsFormat format, WfsConfig wfsConfig) throws IOException, XMLStreamException, FactoryException {
//    wfsFetcher.createFile(crs, format.getFormat(), cfg.getFeatureType() + "_" + format.getTypeTitle() + "_" + crs);
    var url = createWfsUrl(wfsConfig, wfsConfig.getFeatureType(), format.getFormat(), crs);
    var current = CRS.decode(crs);
    var code = current.getIdentifiers().iterator().next().getCode();
    writer.writeStartElement(AtomFeedWriter.ATOM, "entry");
    writeSimpleElement(writer, AtomFeedWriter.ATOM, "title", entry.getTitle() + " as " + format.getTypeTitle() + " in CRS " + crs);
    LinkWriter.writeLink(writer, url, "alternate", format.getMimeType(), entry.getTitle());
    writeSimpleElement(writer, AtomFeedWriter.ATOM, "id", config.getLocation() + wfsConfig.getFeatureType() + "_" + format.getTypeTitle() + "_" + crs);
    writeSimpleElement(writer, AtomFeedWriter.ATOM, "updated", Instant.now().toString());
    writer.writeEmptyElement(AtomFeedWriter.ATOM, "category");
    writer.writeAttribute("term", "http://www.opengis.net/def/crs/EPSG/" + code);
    writer.writeAttribute("label", current.getName().getCode());
    writer.writeEndElement(); // entry
  }

  private static String createWfsUrl(WfsConfig wfsConfig, String featureType, String outputFormat, String crs) throws MalformedURLException {
    var u = new URL(wfsConfig.getUrl());
    var builder = new DefaultUriBuilderFactory().builder()
      .scheme(u.getProtocol())
      .host(u.getHost())
      .path(u.getPath())
      .port(u.getPort())
      .queryParam("service", "WFS")
      .queryParam("request", "GetFeature")
      .queryParam("version", "2.0.0")
      .queryParam("typeName", featureType)
      .queryParam("outputFormat", outputFormat)
      .queryParam("srsname", crs);
    return builder.build().toString();
  }

  private static void writeFileEntry(XMLStreamWriter writer, Entry entry, File file) throws FactoryException, XMLStreamException {
    var current = CRS.decode(file.getCrs());
    var code = current.getIdentifiers().iterator().next().getCode();
    writer.writeStartElement(AtomFeedWriter.ATOM, "entry");
    writeSimpleElement(writer, AtomFeedWriter.ATOM, "title", entry.getTitle() + " as " + file.getTypeTitle() + " in CRS " + file.getCrs());
    LinkWriter.writeLink(writer, file.getFilename(), "alternate", file.getMimeType(), entry.getTitle());
    writeSimpleElement(writer, AtomFeedWriter.ATOM, "id", file.getFilename());
    writeSimpleElement(writer, AtomFeedWriter.ATOM, "updated", Instant.now().toString());
    writer.writeEmptyElement(AtomFeedWriter.ATOM, "category");
    writer.writeAttribute("term", "http://www.opengis.net/def/crs/EPSG/" + code);
    writer.writeAttribute("label", current.getName().getCode());
    writer.writeEndElement(); // entry
  }
}
