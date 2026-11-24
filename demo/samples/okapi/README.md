# JasperReports - Okapi Barcode Sample <img src="../../resources/jasperreports.svg" alt="JasperReports logo" align="right"/>

Shows how barcodes can be included in reports using the Okapi barcode components.

### Main Features in This Sample

[Barcodes (Okapi Barcode Components)](#okapi)

## <a name='okapi'>Barcodes (Okapi Barcode Components)</a>

**Description / Goal** \
How to draw Code 128, Code 39, Codabar, 2 of 5, ITF-14, EAN, UPC and QR Code barcodes with the
`okapi:*` components, which use the
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

`moduleWidthRatio` is the width of the wide bars as a multiple of the narrow ones.

### Running the Sample

Run the commands below in the sample directory:

```
mvn clean compile exec:java
```

The report is compiled, filled and exported to `target/reports`, in PDF and the other supported formats.
Its first page shows Code 128, EAN and QR Code, and its second page the other linear types.
