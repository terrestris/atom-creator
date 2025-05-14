package de.terrestris.inspire.atom.config;

import lombok.Data;

import java.util.List;

@Data
public class Service {
  private List<Entry> entries;

  private String id;

  private String title;

  private String author;

  private String email;

  private String license;

  private String identifierNamespace;

  private BoundingBox bbox;

  private String metadata;

  private String feedBasePath;
}
