package de.terrestris.inspire.atom.config;

import lombok.Data;

@Data
public class WfsFormat {

  private String format;

  private String type;

  private String mimeType;

}
