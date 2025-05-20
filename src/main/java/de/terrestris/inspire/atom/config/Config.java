package de.terrestris.inspire.atom.config;

import lombok.Data;

import java.util.List;

@Data
public class Config {

  private Service service;

  private List<Dataset> datasets;

}
