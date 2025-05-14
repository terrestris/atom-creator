package de.terrestris.inspire.atom;

import de.terrestris.inspire.atom.config.Config;
import de.terrestris.inspire.atom.config.Entry;
import de.terrestris.inspire.atom.config.WfsConfig;
import de.terrestris.inspire.atom.config.WfsFormat;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.file.Files;

public class WfsFetcher {
  private static void getFeatureCount(WfsConfig wfsConfig) throws IOException, ParserConfigurationException, SAXException {
    var u = new URL(wfsConfig.getUrl());
    var builder = new DefaultUriBuilderFactory().builder()
      .scheme(u.getProtocol())
      .host(u.getHost())
      .path(u.getPath())
      .port(u.getPort())
      .queryParam("service", "WFS")
      .queryParam("request", "GetFeature")
      .queryParam("version", "2.0.0")
      .queryParam("typeNames", wfsConfig.getFeatureType())
      .queryParam("resultType", "hits");
    var countUrl = builder.build(u).toURL();
    var conn = (HttpURLConnection) countUrl.openConnection();
    conn.setRequestMethod("GET");

    try (var is = conn.getInputStream()) {
      Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
      String count = doc.getDocumentElement().getAttribute("numberMatched");
      System.out.println(count);
    }
  }

  private static URI createWfsUrl(WfsConfig wfsConfig, WfsFormat format, String crs) throws MalformedURLException {
    var u = new URL(wfsConfig.getUrl());
    var builder = new DefaultUriBuilderFactory().builder()
      .scheme(u.getProtocol())
      .host(u.getHost())
      .path(u.getPath())
      .port(u.getPort())
      .queryParam("service", "WFS")
      .queryParam("request", "GetFeature")
      .queryParam("version", "2.0.0")
      .queryParam("typeNames", wfsConfig.getFeatureType())
      .queryParam("outputFormat", format.getFormat())
      .queryParam("srsname", crs);
    return builder.build();
  }

  public static void cacheFiles(Config config, DataPathsAndUrls paths) throws IOException {
    for (var entry : config.getEntries()) {
      var wfsConfig = entry.getWfsConfig();
      if (wfsConfig != null) {
//        try {
//          getFeatureCount(wfsConfig);
//        } catch (ParserConfigurationException | SAXException e) {
//          throw new RuntimeException(e);
//        }
        var entryCacheDirPath = paths.getCacheDirPath(entry);

        Files.createDirectories(entryCacheDirPath);
        System.out.println("Directory created successfully.");

        for (var crs : wfsConfig.getCrs()) {
          for (var format : wfsConfig.getFormats()) {
            cacheFile(entry, wfsConfig, paths, format, crs);
          }
        }
      }
    }
  }

  public static void cacheFile(Entry entry, WfsConfig wfsConfig, DataPathsAndUrls paths, WfsFormat format, String crs) throws IOException {
    var u = createWfsUrl(wfsConfig, format, crs);
    var filePath = paths.getCacheFilePath(entry, format, crs);
    var inputChannel = Channels.newChannel(u.toURL().openStream());
    var outputStream = new FileOutputStream(filePath.toFile());
    outputStream.getChannel().transferFrom(inputChannel, 0, Long.MAX_VALUE);
    outputStream.close();
    inputChannel.close();

//    // Set permissions to rw-rw-rw- (666)
//    var perms = PosixFilePermissions.fromString("rw-rw-rw-");
//    Files.setPosixFilePermissions(filePath, perms);

    System.out.println("File cached.");
  }
}
