package de.terrestris.inspire.atom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import de.terrestris.inspire.atom.config.Config;
import de.terrestris.inspire.atom.writers.DataFeedWriter;
import de.terrestris.inspire.atom.writers.ServiceFeedWriter;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.codehaus.stax2.XMLOutputFactory2;
import picocli.CommandLine;

import javax.xml.stream.XMLOutputFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "ATOM creator", version = "0.0.1", mixinStandardHelpOptions = true)
@Log4j2
@Data
public class AtomCreator implements Callable<Boolean> {

  private static final XMLOutputFactory FACTORY = XMLOutputFactory2.newDefaultFactory();

  private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());

  @CommandLine.Parameters(
    paramLabel = "OUTDIR",
    description = "Directory to write the files to"
  )
  private String outDir;

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

  public static void main(String[] args) {
    int code = new CommandLine(new AtomCreator()).execute(args);
    System.exit(code);
  }

  @Override
  public Boolean call() throws Exception {
    System.out.println("Starting atom creation...");

    if (generateSchema) {
      System.out.println("Generating schema...");
      generateSchema();
      System.out.println("Finished generating schema.");
      return true;
    }

    System.out.println("Reading config file...");
    var config = MAPPER.readValue(new File(file), Config.class);
    if (!config.getLocation().endsWith("/")) {
      config.setLocation(config.getLocation() + "/");
    }
    System.out.println("Finished reading config file.");

    System.out.println("Generating service feed...");
    try (var out = Files.newOutputStream(Path.of(outDir + "/" + config.getId() + ".xml"))) {
      var writer = FACTORY.createXMLStreamWriter(out);
      ServiceFeedWriter.write(writer, config);
    }
    System.out.println("Finished generating service feed.");

    System.out.println("Generating data feeds...");
    for (var entry : config.getEntries()) {
      System.out.println("Generating data feed " + entry.getTitle() + " ...");
      try (var out = Files.newOutputStream(Path.of(outDir + "/" + entry.getId() + ".xml"))) {
        var writer = FACTORY.createXMLStreamWriter(out);
        DataFeedWriter.write(writer, config, entry);
      }
    }
    System.out.println("Finished generating data feeds.");

    System.out.println("Finished atom creation.");
    return true;
  }
}
