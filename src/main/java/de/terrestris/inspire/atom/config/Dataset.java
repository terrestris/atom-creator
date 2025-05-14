package de.terrestris.inspire.atom.config;

import lombok.Data;

import java.util.List;

@Data
public class Dataset {
  private String id;

  private String date;

  private List<File> files;
}
