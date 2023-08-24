package com.neptuneli.pdfa_conversion;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.graphics.color.PDOutputIntent;

public class PdfaConverter {

  private static final float PDF_VERSION = 1.4f;
  private static final String PDF_PART = "1";
  private static final String PDF_CONFORMANCE = "A";

  public static void convert(final String inputFilePath, final String outputFilePath)
      throws Exception {
    convert(inputFilePath, outputFilePath, PDF_VERSION, PDF_PART, PDF_CONFORMANCE);
  }

  public static void convert(
      final String inputFilePath, final String outputFilePath, final float pdfVersion,
      final String pdfPart, final String pdfConformance) throws Exception {

    final File inputFile = new File(inputFilePath);
    final File outputFile = new File(outputFilePath);

    if (!inputFile.exists()) {
      throw new PdfaException("Input file does not exist");
    }

    final byte[] inputContent = Files.readAllBytes(inputFile.getAbsoluteFile().toPath());

    final byte[] outputContent = convert(inputContent, pdfVersion, pdfPart, pdfConformance);

    try (final FileOutputStream outputStream = new FileOutputStream(outputFile)) {
      outputStream.write(outputContent);
    }

    if (!outputFile.exists()) {
      throw new PdfaException("Fail to create output file");
    }
  }

  public static byte[] convert(final byte[] inputContent) throws Exception {
    return convert(inputContent, PDF_VERSION, PDF_PART, PDF_CONFORMANCE);
  }

  public static byte[] convert(final byte[] inputContent, final float pdfVersion,
      final String pdfPart, final String pdfConformance) throws Exception {

    final File colorPFile = new File("src/main/resources/sRGB Color Space Profile.icm");

    if (inputContent == null || inputContent.length == 0) {
      throw new PdfaException("Input file/content does not exist");
    }

    final PDDocument doc = PDDocument.load(inputContent);
    final PDDocumentCatalog catalog = setCompliant(doc, pdfPart, pdfConformance);

    try (final InputStream colorProfile = Files.newInputStream(colorPFile.toPath())) {
      addOutputIntent(doc, catalog, colorProfile);
    }

    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    doc.setVersion(pdfVersion);
    doc.save(outputStream);
    doc.close();

    if (outputStream == null || outputStream.size() == 0) {
      throw new PdfaException("Fail to create output file");
    }

    return outputStream.toByteArray();
  }

  private static void addOutputIntent(final PDDocument doc, final PDDocumentCatalog catalog,
      final InputStream colorProfile) throws IOException {

    final String profile = "sRGB IEC61966-2.1";

    if (catalog.getOutputIntents().isEmpty()) {

      final PDOutputIntent oi = new PDOutputIntent(doc, colorProfile);
      oi.setInfo(profile);
      oi.setOutputCondition(profile);
      oi.setOutputConditionIdentifier(profile);
      oi.setRegistryName("http://www.color.org");

      catalog.addOutputIntent(oi);
    }

  }

  private static PDDocumentCatalog setCompliant(final PDDocument doc, final String pdfPart,
      final String pdfConformance) throws IOException {

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

    final byte[] fileBytes
        = Files.readAllBytes(new File("src/main/resources/xmpTemplate.xml").toPath());

    String content = new String(fileBytes, charset);
    content = content.replaceAll("@#pdfaid:part#@", pdfPart);
    content = content.replaceAll("@#pdfaid:conformance#@", pdfConformance);

    final byte[] editedBytes = content.getBytes(charset);
    metadata.importXMPMetadata(editedBytes);

    return catalog;
  }
}
