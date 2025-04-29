package de.terrestris.inspire.atom;

import lombok.Data;

import java.util.List;

@Data
public class WfsConfig {

  private String url;

  private List<String> crs;

  private List<String> formats;

  private String featureType;

}
