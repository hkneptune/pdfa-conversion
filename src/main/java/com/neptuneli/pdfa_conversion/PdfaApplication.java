package com.neptuneli.pdfa_conversion;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

/**
 * The PDF Conversion Application
 */
public final class PdfaApplication {

  /**
   * Hide implicit public constructor suggested by SonarLint
   */
  private PdfaApplication() {

  }

  /**
   * The main method
   *
   * @param args The parameters passing in
   */
  public static void main(final String[] args) {

    final Path inputPath = Paths.get("D:\\temp\\TestingDocument.pdf");
    final Path outputPath = Paths.get("D:\\temp\\NewTestingDocument.pdf");

    if (Files.exists(inputPath)) {
      try {

        PdfaConverter.convert(inputPath.toFile().getPath(), outputPath.toFile().getPath());

      } catch (final PdfaException e) {

        final Logger logger = Logger.getLogger(PdfaApplication.class.getName());
        logger.info("Failed to convert the PDF file");
      }
    }
  }
}
