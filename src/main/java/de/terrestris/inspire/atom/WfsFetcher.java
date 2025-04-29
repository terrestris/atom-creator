package de.terrestris.inspire.atom;

import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLOutputFactory2;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.XMLStreamWriter2;
import org.springframework.web.util.DefaultUriBuilderFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class WfsFetcher {

  private static final XMLOutputFactory OUTFACTORY = XMLOutputFactory2.newDefaultFactory();

  private static final XMLInputFactory INFACTORY = XMLInputFactory2.newDefaultFactory();

  private static final String WFS = "http://www.opengis.net/wfs/2.0";

  private static final String GML = "http://www.opengis.net/gml/3.2";

  private final String url;

  private final String featureType;

  public WfsFetcher(String url, String featureType) {
    this.url = url;
    this.featureType = featureType;
  }

  public void createFile(String crs, String format, String file) throws IOException, XMLStreamException {
    var u = new URL(url);
    var builder = new DefaultUriBuilderFactory().builder()
      .scheme(u.getProtocol())
      .host(u.getHost())
      .path(u.getPath())
      .port(u.getPort())
      .queryParam("service", "WFS")
      .queryParam("request", "GetFeature")
      .queryParam("version", "2.0.0")
      .queryParam("typenames", featureType)
      .queryParam("maxFeatures", "50")
      .queryParam("outputformat", format)
      .queryParam("srsname", crs)
      .queryParam("startindex", "1");
    var in = builder.build().toURL().openStream();
    var reader = (XMLStreamReader2) INFACTORY.createXMLStreamReader(in);
    var writer = (XMLStreamWriter2) OUTFACTORY.createXMLStreamWriter(Files.newOutputStream(Path.of(file)));
    while (!(reader.isStartElement() && reader.getLocalName().equals("member"))) {
      writer.copyEventFromReader(reader, false);
      reader.next();
    }
    writer.flush();
  }

}
