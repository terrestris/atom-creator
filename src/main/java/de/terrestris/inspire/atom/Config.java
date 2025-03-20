package de.terrestris.inspire.atom;

import lombok.Data;

import java.util.List;

@Data
public class Config {

  private String location;

  private List<Entry> entries;

  private String id;

  private String title;

  private String author;

  private String email;

  private String license;

  private String identifierNamespace;

  private BoundingBox bbox;

  private String metadata;

}
