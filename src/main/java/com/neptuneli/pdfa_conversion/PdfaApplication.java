package com.neptuneli.pdfa_conversion;

public class PdfaApplication {

  public static void main(String[] args) {

    try {

      PdfaConverter.convert(
          "D:\\temp\\TestingDocument.pdf",
          "D:\\temp\\NewTestingDocument1.pdf");

    } catch (final Exception e) {

      e.printStackTrace(System.out);
    }
  }
}
