
# JasperReports - Barbecue Sample <img src="../../resources/jasperreports.svg" alt="JasperReports logo" align="right"/>

Shows how barcodes could be included in reports using the Barbecue component.

### Main Features in This Sample

[Barcodes (Barbecue Custom Component)](#barbecue)

### Secondary Features

[Barcodes](../barcode4j/README.md#barcodes)

## <a name='top'>Barcodes</a> (Barbecue Custom Component)
<div align="right">Documented by <a href='mailto:shertage@users.sourceforge.net'>Sanda Zaharia</a></div>

**Description / Goal** \
Shows how to render barcodes in a report, using the barcode component based on the Barbecue library.

**Since:** 3.5.2

### The Barbecue Library

As shown in the [Barcodes](../barcode4j/README.md#barcodes) section, the use of barcodes became universal. Depending on particular needs, there are several barcode specifications, called symbologies. This sample illustrates how to use the built-in component based on the open source [Barbecue](http://barbecue.sourceforge.net/) library.

The [Barbecue](http://barbecue.sourceforge.net/) library supports the following barcode formats:

- 2 of 7 (Codabar)
- 3 of 9 (Code 39)
- Bookland
- Codabar
- Code 128
- Code 128A
- Code 128B
- Code 128C
- Code 39
- Code 39 (Extended)
- EAN 128
- EAN 13
- Global Trade Item Number
- Interleave 2 of 5
- Monarch
- NW7
- PDF417 (see the note below)
- PostNet
- Random Weight UPCA
- SCC-14 Shipping Code
- Shipment Identification Number
- SSCC-18
- Standard 2 of 5
- UCC 128
- UPCA
- USD-3
- USD-4
- USPS

> **Note:** Barbecue 1.5beta1 draws PDF417 symbols that do not conform to the
> PDF417 specification (the start pattern and the row indicators are wrong), so
> barcode readers are not expected to decode them. This is a long-standing Barbecue
> issue, see [Barbecue bug #19](https://sourceforge.net/p/barbecue/bugs/19/). The sample
> shows them only to illustrate the component settings. For PDF417 barcodes that
> need to be read, use the PDF417 component of the [Barcode4J sample](../barcode4j/README.md).

It also provides support for multiple output formats:

- PNG
- GIF
- JPEG

### The Barbecue Component

To completely determine a barcode, the following pieces are needed:

- Elements:
    - `<codeExpression />` - representing the alphanumeric character sequence for the barcode
    - `<applicationIdentifierExpression />` - useful when the UCCEAN128Barcode class is involved, to distinguis between various barcode providers.
- Attributes:
    - `type` - determines the symbology used to generate the barcode. Allowed values are:
        - `2of7`
        - `3of9`
        - `Bookland`
        - `Codabar`
        - `Code128`
        - `Code128A`
        - `Code128B`
        - `Code128C`
        - `Code39`
        - `Code39 (Extended)`
        - `EAN128`
        - `EAN13`
        - `GlobalTradeItemNumber`
        - `Int2of5`
        - `Monarch`
        - `NW7`
        - `PDF417` - the symbols drawn are not expected to scan, see the note above
        - `PostNet`
        - `RandomWeightUPCA`
        - `SCC14ShippingCode`
        - `ShipmentIdentificationNumber`
        - `SSCC18`
        - `Std2of5`
        - `UCC128`
        - `UPCA`
        - `USD3`
        - `USD4`
        - `USPS`
    - `drawText` - specifies whether the text information should be visible in the generated barcode
    - `checksumRequired` - specifies whether the checksum is required for the barcode
    - `barWidth` - represents the width (in pixels) of the thinnest bar in the barcode
    - `barHeight` - represents the height (in pixels) for the bars in the barcode
    - `evaluationTime` - represents the report element evaluation time
    - `evaluationGroup` - represents the report element evaluation group name
    - `rotation` - represents the type of orientation for the barcode object.Allowed values are:
        - `None`
        - `Left`
        - `Right`
        - `UpsideDown`

### The Barbecue Sample

The sample shows how can be used the barbecue component to compute various barcodes:

```
<parameter name="Code" class="java.lang.String">
  <defaultValueExpression><![CDATA["01234567890"]] ></defaultValueExpression>
</parameter>
...
<element kind="component y="100" width="400" height="50">
  <component kind="barbecue" drawText="true" type="Code128">
    <codeExpression><![CDATA["JasperReports"]] ></codeExpression>
  </component>
</element>
<element kind="component" y="160" width="400" height="50">
  <component kind="barbecue" drawText="true" type="Codabar">
    <codeExpression><![CDATA[$P{Code}]] ></codeExpression>
  </component>
</element>
<element kind="component" y="220" width="400" height="50">
  <component kind="barbecue" drawText="true" checksumRequired="true" barWidth="3" barHeight="20" type="Int2of5">
    <codeExpression><![CDATA[$P{Code}]] ></codeExpression>
  </component>
</element>
<element kind="component" y="280" width="400" height="50">
  <component kind="barbecue" type="Std2of5">
    <codeExpression><![CDATA[$P{Code}]] ></codeExpression>
  </component>
</element>
...
```

### Running the Sample

Running the sample requires the [Apache Maven](https://maven.apache.org) library. Make sure that `maven` is already installed on your system (version 3.6 or later).\
In a command prompt/terminal window set the current folder to `demo/samples/barbecue` within the JasperReports source project and run the following command:

```
> mvn clean compile exec:exec@all
```

It will generate all supported document types containing the sample report in the `demo/samples/barbecue/target/reports` directory.

