package de.terrestris.inspire.atom;

import de.terrestris.inspire.atom.config.Config;
import de.terrestris.inspire.atom.config.Entry;
import de.terrestris.inspire.atom.config.WfsConfig;
import de.terrestris.inspire.atom.config.WfsFormat;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.file.Files;

public class WfsFetcher {
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
      .queryParam("typeName", wfsConfig.getFeatureType())
      .queryParam("outputFormat", format.getFormat())
      .queryParam("srsname", crs);
    return builder.build();
  }

  public static void cacheFiles(Config config, DataPathsAndUrls paths) throws IOException {
    for (var entry : config.getEntries()) {
      var wfsConfig = entry.getWfsConfig();
      if (wfsConfig != null) {
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
    var inputChannel = Channels.newChannel(u.toURL().openStream());
    var outputStream = new FileOutputStream(paths.getCacheFilePath(entry, format, crs).toString());
    outputStream.getChannel().transferFrom(inputChannel, 0, Long.MAX_VALUE);
    outputStream.close();
    inputChannel.close();
    System.out.println("File cached.");
  }
}
