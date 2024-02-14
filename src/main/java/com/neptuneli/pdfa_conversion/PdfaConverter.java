package com.neptuneli.pdfa_conversion;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.graphics.color.PDOutputIntent;

/**
 * PDF/A Converter
 */
public final class PdfaConverter {

  private static final float PDF_VERSION = 1.4f;
  private static final String PDF_PART = "1";
  private static final String PDF_CONFORMANCE = "A";

  /**
   * Hide implicit public constructor suggested by SonarLint
   */
  private PdfaConverter() {
  }

  /**
   * Convert PDF to PDF/A
   *
   * @param inputFilePath  the input file path
   * @param outputFilePath the output file path
   * @throws PdfaException exception
   */
  public static void convert(final String inputFilePath, final String outputFilePath)
      throws PdfaException {
    convert(inputFilePath, outputFilePath, PDF_VERSION, PDF_PART, PDF_CONFORMANCE);
  }

  /**
   * Convert PDF to PDF/A
   *
   * @param inputFilePath  the input file path
   * @param outputFilePath the output file path
   * @param pdfVersion     the PDF version
   * @param pdfPart        the part
   * @param pdfConformance the conformance
   * @throws PdfaException exception
   */
  public static void convert(
      final String inputFilePath, final String outputFilePath, final float pdfVersion,
      final String pdfPart, final String pdfConformance) throws PdfaException {

    final File inputFile = new File(inputFilePath);
    final File outputFile = new File(outputFilePath);

    if (!inputFile.exists()) {
      throw new PdfaException("Input file does not exist");
    }

    byte[] inputContent;

    try {
      inputContent = Files.readAllBytes(inputFile.getAbsoluteFile().toPath());
    } catch (final IOException ioe) {
      throw new PdfaException("Cannot read the input file content", ioe);
    }

    final byte[] outputContent = convert(inputContent, pdfVersion, pdfPart, pdfConformance);

    try (final OutputStream outputStream = Files.newOutputStream(outputFile.toPath())) {
      outputStream.write(outputContent);
    } catch (final FileNotFoundException e) {
      throw new PdfaException("Cannot find the output file", e);
    } catch (final IOException ioe) {
      throw new PdfaException("Cannot write to the output file", ioe);
    }

    if (!outputFile.exists()) {
      throw new PdfaException("Fail to create output file");
    }
  }

  /**
   * Convert PDF to PDF/A
   *
   * @param inputContent the input file content
   * @return the output file content
   * @throws PdfaException exception
   */
  public static byte[] convert(final byte[] inputContent) throws PdfaException {
    return convert(inputContent, PDF_VERSION, PDF_PART, PDF_CONFORMANCE);
  }

  /**
   * Convert PDF to PDF/A
   *
   * @param inputContent   the input file content
   * @param pdfVersion     the PDF version
   * @param pdfPart        the part
   * @param pdfConformance the conformance
   * @return the output file content
   * @throws PdfaException exception
   */
  public static byte[] convert(final byte[] inputContent, final float pdfVersion,
      final String pdfPart, final String pdfConformance) throws PdfaException {

    final InputStream colorSpaceProfileInputStream = PdfaConverter.class.getClassLoader()
        .getResourceAsStream("sRGB Color Space Profile.icm");

    if (inputContent == null || inputContent.length == 0) {
      throw new PdfaException("Input file/content does not exist");
    }

    PDDocument doc;
    try {
      doc = Loader.loadPDF(inputContent);
    } catch (final IOException ioe) {
      throw new PdfaException("Cannot load the input file content", ioe);
    }

    PDDocumentCatalog catalog;
    try {
      catalog = setCompliant(doc, pdfPart, pdfConformance);
    } catch (final IOException ioe) {
      throw new PdfaException("Cannot set compliant for the PDF", ioe);
    }

    try {
      addOutputIntent(doc, catalog, colorSpaceProfileInputStream);
    } catch (IOException e) {
      throw new PdfaException("Cannot add output intent", e);
    }

    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    doc.setVersion(pdfVersion);

    try {
      doc.save(outputStream);
      doc.close();
    } catch (final IOException ioe) {
      throw new PdfaException("Cannot save to the output file", ioe);
    }

    if (outputStream.size() == 0) {
      throw new PdfaException("Fail to create output file");
    }

    return outputStream.toByteArray();
  }

  private static void addOutputIntent(final PDDocument doc, final PDDocumentCatalog catalog,
      final InputStream colorProfile) throws IOException {

    final String profile = "sRGB IEC61966-2.1";

    if (catalog.getOutputIntents().isEmpty()) {

      final PDOutputIntent outputIntent;

      outputIntent = new PDOutputIntent(doc, colorProfile);
      outputIntent.setInfo(profile);
      outputIntent.setOutputCondition(profile);
      outputIntent.setOutputConditionIdentifier(profile);
      outputIntent.setRegistryName("http://www.color.org");

      catalog.addOutputIntent(outputIntent);
    }

  }

  private static PDDocumentCatalog setCompliant(final PDDocument doc, final String pdfPart,
      final String pdfConformance) throws IOException, PdfaException {

    final PDDocumentCatalog catalog = doc.getDocumentCatalog();
    final PDDocumentInformation info = doc.getDocumentInformation();
    final PDMetadata metadata = new PDMetadata(doc);
    catalog.setMetadata(metadata);

    final PDDocumentInformation newInfo = new PDDocumentInformation();
    newInfo.setProducer(info.getProducer());
    newInfo.setAuthor(info.getAuthor());
    newInfo.setTitle(info.getTitle());
    newInfo.setSubject(info.getSubject());
    newInfo.setKeywords(info.getKeywords());
    // newInfo.setCustomMetadataValue("Credit", "https://github.com/hkneptune");
    doc.setDocumentInformation(newInfo);

    final Charset charset = StandardCharsets.UTF_8;

    final InputStream is =
        PdfaConverter.class.getClassLoader().getResourceAsStream("xmpTemplate.xml");
    if (is == null) {
      throw new PdfaException("Cannot load the xmp template");
    }

    final byte[] fileBytes = IOUtils.toByteArray(is);

    String content = new String(fileBytes, charset);
    content = content.replace("@#pdfaid:part#@", pdfPart);
    content = content.replace("@#pdfaid:conformance#@", pdfConformance);

    is.close();

    final byte[] editedBytes = content.getBytes(charset);
    metadata.importXMPMetadata(editedBytes);

    return catalog;
  }
}
