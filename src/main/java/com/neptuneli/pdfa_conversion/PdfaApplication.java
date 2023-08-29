package com.neptuneli.pdfa_conversion;

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

    try {

      PdfaConverter.convert(
          "D:\\temp\\TestingDocument.pdf",
          "D:\\temp\\NewTestingDocument1.pdf");

    } catch (final PdfaException e) {

      System.out.println();
    }
  }
}
