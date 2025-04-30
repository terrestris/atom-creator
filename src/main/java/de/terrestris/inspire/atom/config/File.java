package de.terrestris.inspire.atom.config;

import lombok.Data;

@Data
public class File {

  private String filename;

  private String crs;

  private String type;

  private String mimeType;
}
