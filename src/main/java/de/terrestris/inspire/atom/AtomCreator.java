package de.terrestris.inspire.atom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.codehaus.stax2.XMLOutputFactory2;
import org.geotools.api.referencing.FactoryException;
import org.geotools.referencing.CRS;
import picocli.CommandLine;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.concurrent.Callable;

import static de.terrestris.utils.xml.XmlUtils.writeSimpleElement;

@CommandLine.Command(name = "ATOM creator", version = "0.0.1", mixinStandardHelpOptions = true)
@Log4j2
@Data
public class AtomCreator implements Callable<Boolean> {

  private static final String ATOM = "http://www.w3.org/2005/Atom";

  private static final String DLS = "http://inspire.ec.europa.eu/schemas/inspire_dls/1.0";

  private static final String GEORSS = "http://www.georss.org/georss";

  private static final XMLOutputFactory FACTORY = XMLOutputFactory2.newDefaultFactory();

  private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());

  @CommandLine.Option(
    names = {"-f", "--file"},
    description = "Configuration files to generate the feeds for"
  )
  private String file;

  @CommandLine.Option(
    names = {"-s", "--schema"},
    description = "Generate the schema file"
  )
  private boolean generateSchema;

  private static void generateSchema() throws IOException {
    SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
    MAPPER.acceptJsonFormatVisitor(MAPPER.constructType(Config.class), visitor);
    var jsonSchema = visitor.finalSchema();

    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    mapper.writeValue(new File("schema.yaml"), jsonSchema);
  }

  public static void main(String[] args) throws IOException {
    int code = new CommandLine(new AtomCreator()).execute(args);
    System.exit(code);
  }

  private static void writeLink(XMLStreamWriter writer, String link, String rel, String type, String title) {
    try {
      writer.writeStartElement(ATOM, "link");
      writer.writeAttribute("href", link);
      writer.writeAttribute("rel", rel);
      writer.writeAttribute("type", type);
      if (title != null) {
        writer.writeAttribute("title", title);
      }
      writer.writeEndElement(); // link
    } catch (XMLStreamException e) {
      log.error("Error writing link: {}", e.getMessage());
      log.trace("Stack trace:", e);
    }
  }

  private static void writeDatasetFeed(Entry entry, Config config) throws XMLStreamException, IOException, FactoryException {
    try (var out = Files.newOutputStream(Path.of("/tmp/" + entry.getId() + ".xml"))) {
      var writer = FACTORY.createXMLStreamWriter(out);
      writer.setDefaultNamespace(ATOM);
      writer.setPrefix("dls", DLS);
      writer.setPrefix("georss", GEORSS);
      writer.writeStartDocument();
      writer.writeStartElement(ATOM, "feed");
      writer.writeDefaultNamespace(ATOM);
      writer.writeNamespace("dls", DLS);
      writer.writeNamespace("georss", GEORSS);
      writeSimpleElement(writer, ATOM, "title", entry.getTitle());
      writeLink(writer, config.getLocation() + entry.getId() + ".xml", "self", "application/atom+xml", null);
      writeLink(writer, config.getLocation() + config.getId() + ".xml", "up", "application/atom+xml", null);
      writeLink(writer, entry.getMetadata(), "describedby", "application/xml", "Metadaten");
      writeSimpleElement(writer, ATOM, "id", config.getLocation() + entry.getId() + ".xml");
      writeSimpleElement(writer, ATOM, "rights", config.getLicense());
      writeSimpleElement(writer, ATOM, "updated", Instant.now().toString());
      writer.writeStartElement(ATOM, "author");
      writeSimpleElement(writer, ATOM, "name", config.getAuthor());
      writeSimpleElement(writer, ATOM, "email", config.getEmail());
      writer.writeEndElement(); // author
      if (entry.getFiles() != null) {
        for (var file : entry.getFiles()) {
          var current = CRS.decode(file.getCrs());
          var code = current.getIdentifiers().iterator().next().getCode();
          writer.writeStartElement(ATOM, "entry");
          writeSimpleElement(writer, ATOM, "title", entry.getTitle() + " in CRS " + file.getCrs());
          writeLink(writer, config.getLocation() + file.getFilename(), "alternate", entry.getFormat(), entry.getTitle());
          writeSimpleElement(writer, ATOM, "id", config.getLocation() + file.getFilename());
          writeSimpleElement(writer, ATOM, "updated", Instant.now().toString());
          writer.writeStartElement(ATOM, "category");
          writer.writeAttribute("term", "http://www.opengis.net/def/crs/EPSG/" + code);
          writer.writeAttribute("label", current.getName().getCode());
          writer.writeEndElement(); // category
          writer.writeEndElement(); // entry
        }
      }
      var cfg = entry.getWfsConfig();
      if (cfg != null) {
        var wfsFetcher = new WfsFetcher(cfg.getUrl(), cfg.getFeatureType());
        for (var crs : cfg.getCrs()) {
          for (var format : cfg.getFormats()) {
            wfsFetcher.createFile(crs, format, cfg.getFeatureType() + "_" + format + "_" + crs);
            var current = CRS.decode(crs);
            var code = current.getIdentifiers().iterator().next().getCode();
            writer.writeStartElement(ATOM, "entry");
            writeSimpleElement(writer, ATOM, "title", entry.getTitle() + " in CRS " + crs);
            writeLink(writer, config.getLocation() + cfg.getFeatureType() + "_" + format + "_" + crs, "alternate", entry.getFormat(), entry.getTitle());
            writeSimpleElement(writer, ATOM, "id", config.getLocation() + cfg.getFeatureType() + "_" + format + "_" + crs);
            writeSimpleElement(writer, ATOM, "updated", Instant.now().toString());
            writer.writeStartElement(ATOM, "category");
            writer.writeAttribute("term", "http://www.opengis.net/def/crs/EPSG/" + code);
            writer.writeAttribute("label", current.getName().getCode());
            writer.writeEndElement(); // category
            writer.writeEndElement(); // entry

          }
        }
      }
      writer.writeEndElement(); // feed
    }
  }

  private static void writeEntry(XMLStreamWriter writer, Entry entry, Config config) {
    try {
      writer.writeStartElement(ATOM, "entry");
      writeSimpleElement(writer, ATOM, "title", entry.getTitle());
      writeSimpleElement(writer, DLS, "spatial_dataset_identifier_code", entry.getId());
      writeSimpleElement(writer, DLS, "spatial_dataset_identifier_namespace", config.getIdentifierNamespace());
      writeLink(writer, entry.getMetadata(), "describedby", "application/xml", "Metadaten");
      writeLink(writer, config.getLocation() + entry.getId() + ".xml", "self", "application/atom+xml", "Selbstreferenz");
      writeSimpleElement(writer, ATOM, "id", config.getLocation() + entry.getId() + ".xml");
      writeSimpleElement(writer, ATOM, "rights", config.getLicense());
      writeSimpleElement(writer, ATOM, "updated", Instant.now().toString());
      writeSimpleElement(writer, ATOM, "summary", entry.getSummary());
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
      writeSimpleElement(writer, GEORSS, "polygon", polygon);
      if (entry.getFiles() != null) {
        for (var file : entry.getFiles()) {
          var current = CRS.decode(file.getCrs());
          var code = current.getIdentifiers().iterator().next().getCode();
          writer.writeStartElement(ATOM, "category");
          writer.writeAttribute("term", "http://www.opengis.net/def/crs/EPSG/" + code);
          writer.writeAttribute("label", current.getName().getCode());
          writer.writeEndElement(); // category
        }
      }
      // TODO WFS
      writer.writeEndElement(); // entry
      writeDatasetFeed(entry, config);
    } catch (XMLStreamException | FactoryException | IOException e) {
      log.error("Error writing entry: {}", e.getMessage());
      log.trace("Stack trace:", e);
    }
  }

  @Override
  public Boolean call() throws Exception {
    if (generateSchema) {
      generateSchema();
      return true;
    }
    var config = MAPPER.readValue(new File(file), Config.class);
    if (!config.getLocation().endsWith("/")) {
      config.setLocation(config.getLocation() + "/");
    }
    try (var out = Files.newOutputStream(Path.of("/tmp/" + config.getId() + ".xml"))) {
      var writer = FACTORY.createXMLStreamWriter(out);
      writer.setDefaultNamespace(ATOM);
      writer.setPrefix("dls", DLS);
      writer.setPrefix("georss", GEORSS);
      writer.writeStartDocument();
      writer.writeStartElement(ATOM, "feed");
      writer.writeDefaultNamespace(ATOM);
      writer.writeNamespace("dls", DLS);
      writer.writeNamespace("georss", GEORSS);
      writeSimpleElement(writer, ATOM, "title", config.getTitle());
      writeLink(writer, config.getMetadata(), "describedby", "application/xml", "Metadaten");
      writeLink(writer, config.getLocation() + config.getId() + ".xml", "self", "application/atom+xml", "Selbstreferenz");
      writeSimpleElement(writer, ATOM, "id", config.getLocation() + config.getId() + ".xml");
      writeSimpleElement(writer, ATOM, "rights", config.getLicense());
      writeSimpleElement(writer, ATOM, "updated", Instant.now().toString());
      writer.writeStartElement(ATOM, "author");
      writeSimpleElement(writer, ATOM, "name", config.getAuthor());
      writeSimpleElement(writer, ATOM, "email", config.getEmail());
      writer.writeEndElement(); // author
      writer.writeStartElement(ATOM, "category");
      writer.writeAttribute("term", "http://inspire.ec.europa.eu/metadata-codelist/SpatialDataServiceCategory/infoFeatureAccessService");
      writer.writeAttribute("scheme", "http://inspire.ec.europa.eu/metadata-codelist/SpatialDataServiceCategory");
      writer.writeEndElement(); // category
      config.getEntries().forEach(e -> writeEntry(writer, e, config));
      writer.writeEndElement(); // feed
    }
    return true;
  }

}
