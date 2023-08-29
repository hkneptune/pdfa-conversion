package com.neptuneli.pdfa_conversion;

/**
 * Exception of the PDFA Conversion library
 */
public class PdfaException extends Exception {

  private static final long serialVersionUID = 7481170113676804944L;

  /**
   * Default constructor
   *
   * @param string The log message
   */
  public PdfaException(final String string) {
    super(string);
  }

  /**
   * Constructs a new exception with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause   the cause
   */
  public PdfaException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
