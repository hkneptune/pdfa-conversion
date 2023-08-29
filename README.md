# pdfa-conversion
Simple PDF/A Conversion with Apache PDFBox

## Interfaces

```
PdfaConverter.convert(String inputFilePath, String outputFilePath);
PdfaConverter.convert(String inputFilePath, String outputFilePath, float pdfVersion, String pdfPart, String pdfConformance);
byte[] result = PdfaConverter.convert(byte[] inputContent);
byte[] result = PdfaConverter.convert(byte[] inputContent, float pdfVersion, String pdfPart, String pdfConformance);
```

## Usage

1. Add below contents to `pom.xml`.

```

<dependency>
	<groupId>org.apache.pdfbox</groupId>
	<artifactId>pdfbox</artifactId>
	<version>2.0.29</version>
</dependency>
<dependency>
	<groupId>com.neptuneli</groupId>
	<artifactId>pdfa-conversion</artifactId>
	<version>1.0.1</version>
</dependency>
```

2. Convert the PDF.

```
PdfaConverter.convert("D:\\input.pdf", "D:\\output.pdf");
```
