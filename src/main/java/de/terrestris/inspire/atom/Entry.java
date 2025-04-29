package de.terrestris.inspire.atom;

import lombok.Data;

import java.util.List;

@Data
public class Entry {

  private List<File> files;

  private WfsConfig wfsConfig;

  private String title;

  private String id;

  private String summary;

  private String format;

  private String metadata;

}
