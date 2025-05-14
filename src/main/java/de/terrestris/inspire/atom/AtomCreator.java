package de.terrestris.inspire.atom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import de.terrestris.inspire.atom.config.Config;
import de.terrestris.inspire.atom.config.Dataset;
import de.terrestris.inspire.atom.config.Service;
import de.terrestris.inspire.atom.writers.DataFeedWriter;
import de.terrestris.inspire.atom.writers.ServiceFeedWriter;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.codehaus.stax2.XMLOutputFactory2;
import picocli.CommandLine;

import javax.xml.stream.XMLOutputFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "ATOM creator", version = "0.0.1", mixinStandardHelpOptions = true)
@Log4j2
@Data
public class AtomCreator implements Callable<Boolean> {

  private static final XMLOutputFactory FACTORY = XMLOutputFactory2.newDefaultFactory();

  private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());

  @CommandLine.Parameters(
    paramLabel = "CONFIG_DIR",
    description = "Config directory. Needs to contain one `service.yaml`."
  )
  private String configDir;

  @CommandLine.Parameters(
    paramLabel = "OUTPUT_DIR",
    description = "Output directory.."
  )
  private String outputDir;

  @CommandLine.Option(
    names = {"-s", "--schema"},
    description = "Generate the schema file"
  )
  private boolean generateSchema;

  @CommandLine.Option(
    names = {"--clean"},
    description = "Remove any XML files in the `feeds` folder and all folders in the `cache` folder before creating new feeds"
  )
  private boolean clean;

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
    if (generateSchema) {
      System.out.println("Generating schema...");
      generateSchema();
      System.out.println("Finished generating schema.");
      return true;
    }

    if (clean) {
      System.out.println("Cleaning up...");
      clean();
      System.out.println("Finished cleaning up.");
    }

    System.out.println("Starting atom creation...");
    System.out.println("Reading config file...");
    var config = readConfig();
    System.out.println("Finished reading config file.");

    System.out.println("Generating service feed...");
    try (var out = Files.newOutputStream(PathBuilder.buildPath(outputDir, config.getService().getId() + ".xml"))) {
      var writer = FACTORY.createXMLStreamWriter(out);
      ServiceFeedWriter.write(writer, config);
    }
    System.out.println("Finished generating service feed.");

    System.out.println("Generating data feeds...");
    for (var entry : config.getService().getEntries()) {
      System.out.println("Generating data feed " + entry.getTitle() + " ...");
      var datasets = config.getDatasets().stream()
        .filter(dataset -> dataset.getId().equals(entry.getId()))
        .toList();
      try (var out = Files.newOutputStream(PathBuilder.buildPath(outputDir, entry.getId() + ".xml"))) {
        var writer = FACTORY.createXMLStreamWriter(out);
        DataFeedWriter.write(writer, config.getService(), entry, datasets);
      }
    }
    System.out.println("Finished generating data feeds.");

    System.out.println("Finished atom creation.");
    return true;
  }

  private Config readConfig() throws Exception {

    final Service[] service = new Service[1];
    List<Dataset> datasets = new ArrayList<>();

    Files.walkFileTree(Path.of(configDir), new SimpleFileVisitor<>() {
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (file.getFileName().toString().equals("service.yaml")) {
          service[0] = MAPPER.readValue(file.toFile(), Service.class);
        } else {
          datasets.add(MAPPER.readValue(file.toFile(), Dataset.class));
        }
        return FileVisitResult.CONTINUE;
      }
    });

    if (service[0] == null) {
      throw new Exception("service.yaml not found");
    }

    var config = new Config();
    config.setService(service[0]);
    config.setDatasets(datasets);

    return config;
  }

  private void clean() throws IOException {
    Files.walkFileTree(Path.of(outputDir), new SimpleFileVisitor<>() {
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (file.toString().endsWith(".xml")) {
          Files.delete(file);
          System.out.println("Deleted: " + file);
        }
        return FileVisitResult.CONTINUE;
      }
    });
  }
}
