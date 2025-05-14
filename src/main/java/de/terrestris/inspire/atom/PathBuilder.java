package de.terrestris.inspire.atom;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

public class PathBuilder {
  public static String build(String... parts) {
    return Arrays.stream(parts)
      .map(part -> part.replaceAll("^/+", "").replaceAll("/+$", ""))
      .collect(Collectors.joining("/"));
  }

  public static Path buildPath(String... parts) {
    return Path.of(build(parts));
  }
}
