# JasperReports - Okapi Barcode Sample <img src="../../resources/jasperreports.svg" alt="JasperReports logo" align="right"/>

Shows how barcodes can be included in reports using the Okapi barcode components.

### Main Features in This Sample

[Barcodes (Okapi Barcode Components)](#okapi)

## <a name='okapi'>Barcodes (Okapi Barcode Components)</a>

**Description / Goal** \
How to draw Code 128, Code 39, Codabar, 2 of 5, ITF-14, EAN, UPC, GS1 DataBar, QR Code, Data Matrix, Aztec Code,
MaxiCode and other barcodes with the `okapi:*` components, which use the
[Okapi Barcode](https://github.com/woo-j/OkapiBarcode) library.

**Other Samples**\
[/demo/samples/barcode4j](../barcode4j/README.md)\
[/demo/samples/barbecue](../barbecue/README.md)

### The Okapi Barcode Components

Each barcode type has its own component, such as `okapi:Code128`, `okapi:EAN` or `okapi:QRCode`,
placed in a component element:

```xml
<element kind="component" x="0" y="40" width="160" height="50" style="Barcode">
  <component kind="okapi:Code128" moduleWidth="1.0" hImageAlign="Center">
    <codeExpression><![CDATA["JasperReports"]]></codeExpression>
  </component>
</element>
```

Every component has:
- `codeExpression`, the data to encode;
- `evaluationTime` and `evaluationGroup`, to evaluate the code later, for example at the end of the report;
- `rotation`, `hImageAlign` and `vImageAlign`, the rotation and alignment of the barcode image,
  which come from the element style when not set;
- `quietZoneHorizontal` and `quietZoneVertical`, the empty margins around the barcode, in report units
  (the standard margins of the symbology apply when not set).

Barcodes whose size depends on the data also have `moduleWidth`, the width of the narrowest bar
(the size of one square for QR Code), in report units. When it is set and the barcode fits the element,
the barcode is drawn at exactly that size and aligned in the element. When it is not set, or the barcode
doesn't fit, the barcode is sized to fill the element. Linear barcodes such as Code 128 and EAN take the
element height.

Barcodes that draw human-readable text have `humanReadableLocation` (`Bottom`, `Top` or `None`) and
`humanReadableAlignment`. The text uses the font of the element style, and the bars use its forecolor.

The other settings are specific to each type, for example `dataType` for GS1 and HIBC data,
`codeSet` for Code 128, `mode` for EAN-8 or UPC-E, or `preferredEccLevel` and `preferredVersion` for QR Code.

The linear types in this sample are:

| Component | Encodes | Own settings |
| --- | --- | --- |
| `okapi:Code128` | any ASCII text; GS1-128 and HIBC data | `codeSet`, `dataType`, `readerInit` |
| `okapi:Code39` | digits, upper-case letters, space and `- . $ / + %`; HIBC data | `moduleWidthRatio`, `checkDigit` (`None`, `Mod43`), `dataType` |
| `okapi:Code39Extended` | all 128 ASCII characters | `moduleWidthRatio`, `checkDigit` |
| `okapi:Codabar` | digits and `- $ : / . +`, between start/stop characters A, B, C or D | `moduleWidthRatio` |
| `okapi:Interleaved2Of5` | digits, in pairs | `moduleWidthRatio`, `checkDigit` (`None`, `Mod10`) |
| `okapi:ITF14` | 13 digits; the check digit is added | `moduleWidthRatio` |
| `okapi:Code2Of5` | digits | `mode` (`Matrix`, `Industrial`, `IATA`, `DataLogic`, `DPLeitcode`, `DPIdentcode`), `moduleWidthRatio` |
| `okapi:EAN` | 12 or 7 digits, and an add-on after `+`; the check digit is added | `mode` (`EAN13`, `EAN8`), `guardPatternExtraHeight` |
| `okapi:UPC` | 11 or 7 digits, and an add-on after `+`; the check digit is added | `mode` (`UPCA`, `UPCE`), `showCheckDigit`, `guardPatternExtraHeight` |
| `okapi:LOGMARS` | as Code 39; a mod 43 check character is added | `moduleWidthRatio` |
| `okapi:PZN` | up to 7 digits; the check digit is added | |
| `okapi:Code32` | up to 8 digits; the check digit is added | |
| `okapi:NVE18` | up to 17 digits; the (00) application identifier and the check digit are added | |
| `okapi:DPDCode` | 27 or 28 characters | |
| `okapi:USPSPackage` | GS1 data, digits with the application identifiers in square brackets, of even length | `humanReadableAlignment` only; the banner and the number are always drawn |
| `okapi:Code93` | all 128 ASCII characters; the two check characters are added | `showCheckDigits`, `startStopDelimiter` |
| `okapi:Code11` | digits and `-`; the check digits are added | `moduleWidthRatio`, `checkDigitCount` (1, 2), `startDelimiter`, `stopDelimiter` |
| `okapi:Telepen` | ASCII text, or digits | `mode` (`Normal`, `Numeric`) |
| `okapi:Plessey` | hexadecimal digits; the check digits are added | `checkDigitInHumanReadableText`, `moduleWidthRatio` |
| `okapi:MSIPlessey` | digits | `checkDigit` (`None`, `Mod10`, `Mod10Mod10`, `Mod11`, `Mod11Mod10`), `checkDigitInHumanReadableText`, `moduleWidthRatio` |
| `okapi:ChannelCode` | up to 7 digits | `preferredNumberOfChannels` (3 to 8) |
| `okapi:DataBar` | a GTIN of up to 13 digits; the check digit is added | `mode` (`Linear`, `Omni`, `Stacked`), `separatorHeight` |
| `okapi:DataBarLimited` | a GTIN of up to 13 digits, 13 digits starting with 0 or 1; the check digit is added | |
| `okapi:DataBarExpanded` | GS1 data, with the application identifiers in square brackets | `stacked`, `preferredColumns`, `barHeight` |
| `okapi:Pharmacode` | a number from 3 to 131070 | |
| `okapi:Pharmacode2Track` | a number from 4 to 64570080 | |

`moduleWidthRatio` is the width of the wide bars as a multiple of the narrow ones.
DataBar and DataBar Expanded can be stacked in rows. The stacked DataBar Expanded rows share the
element height, unless `barHeight`, the height of a row in report units, is set.
DataBar Stacked rows have a fixed height, so it is sized like QR Code. Pharmacode and
Pharmacode2Track draw no human-readable text, DataBar only draws it in the `Linear` mode, and
DataBar Expanded only when it is not stacked.

The 2D types are:

| Component | Encodes | Own settings |
| --- | --- | --- |
| `okapi:QRCode` | any text; GS1 and HIBC data | `preferredVersion`, `preferredEccLevel` (`L`, `M`, `Q`, `H`), `forceByteCompaction`, `dataType`, `eciMode`, structured append |
| `okapi:MicroQRCode` | short text, best digits | `preferredVersion` (1 to 4), `eccMode` (`L`, `M`, `Q`) |
| `okapi:DataMatrix` | any text; GS1 and HIBC data | `forceMode` (`Square`, `Rectangular`), `preferredSize` (1 to 30), `dataType`, `eciMode`, `readerInit`, `gs1SeparatorGs`, structured append |
| `okapi:AztecCode` | any text; GS1 and HIBC data | `mode` (`Normal`, `Compact`, `Any`), `preferredSize` (1 to 36), `preferredEccPercentage`, `dataType`, `eciMode`, `readerInit`, structured append |
| `okapi:AztecRune` | a number from 0 to 255 | |
| `okapi:CodeOne` | ISO 8859-1 text; GS1 data | `preferredVersion` (`A` to `H`, `S`, `T`), `dataType` |
| `okapi:GridMatrix` | ISO 8859-1 and GB 2312 (Chinese) text | `preferredVersion` (1 to 13), `preferredEccLevel` (1 to 5), `readerInit` |
| `okapi:MaxiCode` | text, and in modes 2 and 3 a postal code, country and service in `primaryExpression` | `mode` (`Mode2` to `Mode6`), `eciMode`, structured append |
| `okapi:SwissQRCode` | the data of a Swiss QR-bill | |
| `okapi:UPNQR` | the data of a Slovenian UPN payment order | |

A message split over several symbols (structured append) is described by expressions evaluated for each
symbol: `structuredAppendPositionExpression` and `structuredAppendTotalExpression`, and for some types an
identifier of the series, such as `structuredAppendFileIdExpression` for Data Matrix.
`preferredSize` is the exact Data Matrix size: data that doesn't fit fails.

MaxiCode has a fixed size, about 28 x 27 mm, so it has no `moduleWidth`: it fills the element, keeping its
shape, and the element sets its printed size. The Swiss QR-bill requires its QR code to print at 46 x 46 mm.

### Running the Sample

Run the commands below in the sample directory:

```
mvn clean compile exec:java
```

The report is compiled, filled and exported to `target/reports`, in PDF and the other supported formats.
Its first page shows Code 128, EAN and QR Code, its second page Code 39, Codabar, 2 of 5 and UPC,
its third page the other linear types, and its fourth page the 2D types.
