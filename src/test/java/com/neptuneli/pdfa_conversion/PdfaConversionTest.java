package com.neptuneli.pdfa_conversion;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfaConversionTest {

  @Test
  void testing() throws Exception {

    System.out.println("[START]");
    boolean result = false;

    try {

      PdfaConverter.convert(
          "D:\\temp\\TestingDocument.pdf",
          "D:\\temp\\NewTestingDocument1.pdf", 1.7f, "3", "U");

      System.out.println("PDF conversion success");
      result = true;

    } catch (final Exception e) {

      e.printStackTrace(System.out);
    }

    System.out.println("[END]");

    assertTrue(result);
  }
}
