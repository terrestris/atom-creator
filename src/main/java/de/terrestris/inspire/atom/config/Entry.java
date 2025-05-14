package de.terrestris.inspire.atom.config;

import lombok.Data;

@Data
public class Entry {

  private String title;

  private String id;

  private String summary;

  private String metadata;

  private String basePath;
}
