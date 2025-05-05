package de.terrestris.inspire.atom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import de.terrestris.inspire.atom.config.Config;
import de.terrestris.inspire.atom.writers.DataFeedWriter;
import de.terrestris.inspire.atom.writers.ServiceFeedWriter;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.codehaus.stax2.XMLOutputFactory2;
import picocli.CommandLine;

import javax.validation.constraints.NotNull;
import javax.xml.stream.XMLOutputFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "ATOM creator", version = "0.0.1", mixinStandardHelpOptions = true)
@Log4j2
@Data
public class AtomCreator implements Callable<Boolean> {

  private static final XMLOutputFactory FACTORY = XMLOutputFactory2.newDefaultFactory();

  private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());

  @CommandLine.Parameters(
    paramLabel = "DATA_DIR",
    description = "Data directory. Should contain folders `cache`, `files` and `feeds`. This directory has to be exposed by a webserver."
  )
  private String dataDir;

  @CommandLine.Parameters(
    paramLabel = "PUBLIC_URL",
    description = "The public URL of the data directory"
  )
  private String publicUrl;

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

    var paths = new DataPathsAndUrls(dataDir, publicUrl);

    if (clean) {
      System.out.println("Cleaning up...");
      clean(paths);
      System.out.println("Finished cleaning up.");
    }

    System.out.println("Starting atom creation...");
    System.out.println("Reading config file...");
    var config = MAPPER.readValue(new File(file), Config.class);
    System.out.println("Finished reading config file.");

    System.out.println("Generating service feed...");
    try (var out = Files.newOutputStream(paths.getFeedPath(config.getId()))) {
      var writer = FACTORY.createXMLStreamWriter(out);
      ServiceFeedWriter.write(writer, config, paths);
    }
    System.out.println("Finished generating service feed.");

    System.out.println("Caching WFS results...");
    WfsFetcher.cacheFiles(config, paths);
    System.out.println("Finished caching WFS results.");

    System.out.println("Generating data feeds...");
    for (var entry : config.getEntries()) {
      System.out.println("Generating data feed " + entry.getTitle() + " ...");
      try (var out = Files.newOutputStream(paths.getFeedPath(entry.getId()))) {
        var writer = FACTORY.createXMLStreamWriter(out);
        DataFeedWriter.write(writer, paths, config, entry);
      }
    }
    System.out.println("Finished generating data feeds.");

    System.out.println("Finished atom creation.");
    return true;
  }

  private void clean(DataPathsAndUrls paths) throws IOException {
    Path feedsDirPath = paths.getFeedDirPath();

    Files.walkFileTree(feedsDirPath, new SimpleFileVisitor<>() {
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (file.toString().endsWith(".xml")) {
          Files.delete(file);
          System.out.println("Deleted: " + file);
        }
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult visitFileFailed(Path file, IOException exc) {
        // Log the error or handle it as needed
        return FileVisitResult.CONTINUE;
      }
    });

    Path cacheDirPath = paths.getCacheDirPath();

    Files.walkFileTree(cacheDirPath, new SimpleFileVisitor<>() {
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        // Skip files; only process directories
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult postVisitDirectory(@NotNull Path dir, IOException exc) {
        // Attempt to delete the directory
        if (!dir.equals(cacheDirPath)) {
          try {
            FileUtils.deleteDirectory(dir.toFile());
            System.out.println("Deleted directory: " + dir);
          } catch (IOException e) {
            System.err.println("Failed to delete directory: " + dir);
          }
        }
        return FileVisitResult.CONTINUE;
      }
    });
  }
}
