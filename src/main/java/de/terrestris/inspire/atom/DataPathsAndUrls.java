package de.terrestris.inspire.atom;

import de.terrestris.inspire.atom.config.Entry;
import de.terrestris.inspire.atom.config.WfsFormat;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

public class DataPathsAndUrls {
  private final String publicUrl;
  public Path dataDir;

  public DataPathsAndUrls(String dataDir, String publicUrl) {
    this.dataDir = Path.of(dataDir);
    this.publicUrl = publicUrl;
  }

  public URL getFileUrl(String filename) throws MalformedURLException {
    return new URL(publicUrl + "/files/" + filename);
  }

  public URL getFeedUrl(String feedId) throws MalformedURLException {
    return new URL(publicUrl + "/feeds/" + feedId + ".xml");
  }

  public Path getFeedPath(String feedId) {
    return Path.of(dataDir + "/feeds/" + feedId + ".xml");
  }

  public Path getCacheDirPath(Entry entry) {
    return Path.of(dataDir + "/cache/" + entry.getId());
  }

  public Path getCacheFilePath(Entry entry, WfsFormat format, String crs) {
    return Path.of(getCacheDirPath(entry) + "/" + normalize(format.getTypeTitle()) + "_" + normalize(crs) + "." + format.getFileExtension());
  }

  public URL getCacheFileUrl(Entry entry, WfsFormat format, String crs) throws MalformedURLException {
    return new URL(publicUrl + "/cache/" + entry.getId() + "/" + normalize(format.getTypeTitle()) + "_" + normalize(crs) + "." + format.getFileExtension());
  }

  private static String normalize(String pathSegment) {
    return pathSegment.replaceAll("[^A-Za-z0-9_-]", "_");
  }

  public Path getFeedDirPath() {
    return Path.of(dataDir + "/feeds");
  }

  public Path getCacheDirPath() {
    return Path.of(dataDir + "/cache");
  }
}
