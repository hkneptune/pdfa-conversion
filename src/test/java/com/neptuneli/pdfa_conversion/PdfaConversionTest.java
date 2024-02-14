package com.neptuneli.pdfa_conversion;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfaConversionTest {

  @TempDir Path tempDir;

  @Test void testing() throws Exception {

    System.out.println("[START]");
    boolean result = false;

    try {

      ClassLoader classLoader = getClass().getClassLoader();
      PdfaConverter.convert(classLoader.getResource("test.pdf").getPath(),
          tempDir.resolve("testResult.pdf").toFile().getPath(),
          1.7f, "3", "U");

      System.out.println("PDF conversion success");
      result = true;

    } catch (final Exception e) {

      e.printStackTrace(System.out);
    }

    System.out.println("[END]");

    assertTrue(result);
  }
}
