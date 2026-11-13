
# JasperReports - Barcode4J Sample <img src="../../resources/jasperreports.svg" alt="JasperReports logo" align="right"/>

Shows how barcodes could be included in reports using the Barcode4J component.

### Main Features in This Sample

[Barcodes](#barcodes)\
[Barcodes (Barcode4J Custom Component)](#barcode4j)
				

## <a name='barcodes'>Barcodes</a>
<div align="right">Documented by <a href='mailto:shertage@users.sourceforge.net'>Sanda Zaharia</a></div>

**Description / Goal** \
Shows how to render barcodes in a report, using the barcode components that are shipped with the JasperReports library.

**Since:** 3.5.2

**Other Samples**\
[/demo/samples/barbecue](../barbecue/README.md)

### Barcode Overview

A barcode denotes an encoded graphical representation of data which can be read/decoded using dedicated scanning devices such as barcode scanners. Due to its exceptional ability to completely characterize a given product, the barcode technology became one of the most common product registering technologies. It consists in computing a graphical "fingerprint" for a given object based on a unique digit/character sequence mapped to the object. Once generated, the barcode uniquely identifies the product. When scanned, the barcode allows retrieving the original data it was built on, in order to be visualized or printed out.

### Barcode Symbologies

The whole "fingerprint" graphics is computed from elementary fingerprints of single digits/characters in the sequence. There are several ways to map individual characters or digits to a graphical representation. These mappings are specified in the so-called barcode symbologies. Some very common symbology categories are:

- linear vs. 2D
    - linear - represent data by varying the widths and spacings of parallel lines. Are optimized for laser scanners.
    - 2D - use rectangles, dots, hexagons and other 2D geometric patterns to represent data. Are optimized for image-based scanners.
- continuous vs. discrete
    - continuous - one character ends with a space and the next begins with the next bar, etc.
    - discrete - any character begins and ends with bars; the space between consecutive characters is ignored
- two-width vs. many-width
    - two-width - bars and spaces are either wide or narrow. The exact width of a bar/space has no meaning by itself.
    - many-width - bars and spaces may have only predefined width values, multiple of a basic width called "module". Usually a bar/space width can count up to 4 modules.

Some of linear symbologies are highly standardized, some others are niche-oriented, most of them presenting mixed features. Below are few examples:

- `Universal Product Code (UPC)` - International Standard ISO/IEC 15420. There are 5 versions of the `UPC` symbology designed for future industry requirements: `UPC A, B, C, D, E`.
- `Codabar` - Outdated format used in libraries, blood banks and on airway bills. Not standardized.
- `Code 25` – Interleaved 2 of 5 - Used in wholesales and libraries. International standard ISO/IEC 16390
- `Code 39` - International standard ISO/IEC 16388
- `Code 128` – International Standard ISO/IEC 15417
- `EAN-8` - International Standard ISO/IEC 15420
- `EAN-13` - International Standard ISO/IEC 15420
- `JAN` - Available for Japan, compatible with EAN-13 (ISO/IEC 15420)
- etc.

Some of the 2D symbologies are enumerated below:

- `Aztec Code` - International Standard ISO/IEC 24778
- `Codablock` - Not standardized.
- `Code 16K` - Based on linear Code 128.
- `Data Matrix` - Used throughout the United States. Standard ISO/IEC 16022:2000(E)
- `EZcode` - Designed for decoding by cameraphones.
- `PDF417` - Standard ISO/IEC 15438:2001(E)
- `QRCode` - Standard ISO/IEC 18004:2006
- etc.

<div align="right"><a href='#top'>top</a></div>

---

## <a name='barcode4j'>Barcode4J</a> Custom Component
<div align="right">Documented by <a href='mailto:shertage@users.sourceforge.net'>Sanda Zaharia</a></div>

**Description / Goal** \
Shows how to render barcodes in a report, using the barcode component based on the Barcode4J library.

**Since:** 3.5.2

### The Barcode4J Library

Today barcodes are present almost everywhere: in wholesales as well as in retails, on the bills, in airports, libraries, banks, hospitals, hotels, theaters, amusement parks, on the internet, and the list could continue... Due to this ubiquity, there is an increasing need of barcode components in the reporting area. Powerful reporting engines should now provide support for as many as possible barcode symbologies. To cover a large category of customer needs, JR comes with 2 built-in barcode components, and in addition, it also makes room for other complementary implementations. This sample illustrates how to use the built-in component mostly based on the open source Barcode4J library.
The Barcode4J library supports the following symbologies:

- linear:
    - `Code 25` – Interleaved 2 of 5
    - `Codabar`
    - `ITF-14`
    - `Code 39` - International standard ISO/IEC 16388
    - `Code 128` – International Standard ISO/IEC 15417
    - `EAN-128, GS1-128` (based on Code 128)
    - `EAN-13` and `EAN-8` (with supplementals)
    - `UPC-A` and `UPC-E` (with supplementals)
    - `POSTNET`
    - `Royal Mail Customer Barcode` (Four State)
    - `USPS Intelligent Mail` (4-State Customer Barcode)
- 2D:
    - `DataMatrix`
    - `PDF 417`

Note: The `QRCode` 2D-symbology being not yet supported in Barcode4J, its related component implementation in JasperReports is based on the ZXing library.

Multiple output formats are also supported:

- SVG
- EPS
- Bitmap images (such as PNG or JPEG)
- Java2D
- Text - for testing and debugging only

### The Barcode4J Component

Each of [Barcode4jComponent](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/barcode4j/Barcode4jComponent.html) supported symbologies enumerated above is characterized by a common structure of elements and attributes described in the Barcode4j parent element, and their specific difference given as additional extracontent:

- [Codabar](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/barcode4j/CodabarComponent.html)
- [Code39](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/barcode4j/Code39Component.html)
- [Code128](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/barcode4j/Code128Component.html)
- [EAN8](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/barcode4j/EAN8Component.html)
- [EAN13](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/barcode4j/EAN13Component.html)
- [EAN128](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/barcode4j/EAN128Component.html)
- [DataMatrix](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/barcode4j/DataMatrixComponent.html)
- [Interleaved2Of5](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/barcode4j/Interleaved2Of5Component.html)
- [PDF417](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/barcode4j/PDF417Component.html)
- [POSTNET](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/barcode4j/POSTNETComponent.html)
- [QRCode](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/barcode4j/QRCodeComponent.html)
- [RoyalMailCustomer](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/barcode4j/RoyalMailCustomerComponent.html)
- [UPCA](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/barcode4j/UPCAComponent.html)
- [UPCE](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/barcode4j/UPCEComponent.html)
- [USPSIntelligentMail](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/barcode4j/USPSIntelligentMailComponent.html)

The basic [Barcode4jComponent](https://jasperreports.sourceforge.net/api/net/sf/jasperreports/barcode4j/Barcode4jComponent.html) structure is characterized by the following series of elements:

- `codeExpression`
- `patternExpression`

and contains the following attributes:

- `evaluationTime` - the evaluationTime attribute of any JR report element
- `evaluationGroup` - the evaluationGroup attribute of any JR report element
- `orientation` - specifies the rotation of the generated barcode image in degrees (allowed values are: `0, 90, 180, 270`), or their text equivalents: `up, left, right, down`. The default value is 0.
- `moduleWidth` - specifies the narrow module width (in points). This will be converted in mm at runtime.
- `textPosition` - specifies the text presence and its position relative to the bar. Allowed values are: `none, bottom, top`.
- `quietZone` - specifies the width of the quiet zone left and right of the barcode in points. This will be converted in mm at runtime.
- `verticalQuietZone` - specifies the height of the vertical quiet zone above and below the barcode in points. This will be converted in mm at runtime.

Barcode4J 2.1 limitations of `verticalQuietZone`:

- For `Codabar`, `Code128`, `Code39`, `EAN128` and `Interleaved2Of5`, the bars are moved down by the vertical quiet zone, but the human-readable text is not. With `textPosition="bottom"` (the default), the text overlaps the bars by the height of the vertical quiet zone, and with `textPosition="top"` it is separated from the bars by that height. The sample therefore uses `textPosition="none"` for these components when it sets `verticalQuietZone`.
- For `EAN13`, `EAN8`, `UPCA` and `UPCE`, the attribute is ignored.

Another attribute available for almost all Barcode4j elements is the `checksumMode`. Allowed values are `auto, ignore, add` and `check`.

Starting with JasperReports 5.1.2 a `templateExpression` is available for the `EAN128` component, in order to allow custom configuration for the human readable part of the barcode. The format of the `templateExpression` must conform to rules specified in the official documentation:

'The format of "template" is a repeating set of AI number (in brackets) followed by a field description. The allowed data types are "n" (numeric), "an" (alpha-numeric), "d" (date) and "cd" (check digit). "cd" will calculate the check digit starting with the first character after the `AI`. "cd0" will include the `AI` in the check digit calculation (used for `USPS`). Multiple field parts can be separated by "+" characters.'

### The QRCode component

This barcode component depends on [ZXing](https://github.com/zxing/zxing) library. It shares some common features with the other barcode components:

- Common expressions:
    - `codeExpression` - provides the text content used to generate the barcode object.
- Common attributes:
    - `evaluationTime`
    - `evaluationGroup`

Specific attributes for this component are enumerated below:

- `margin` - number of pixels reserved as margin around the barcode graphic.
- `errorCorrectionLevel` - enabled; single uppercase character representing the error correction level. Possible values are: `L, M, Q, H`. Default is `L`.

### Barcode4J Samples

Some examples of howto use the available Barcode4j components are given in the JRXML sample:

```
<element kind="component" x="130" y="100" width="200" height="30" style="Barcode">
  <component kind="barcode4j:Code128" moduleWidth="1.0">
    <codeExpression><![CDATA["0123456789"]] ></codeExpression>
  </component>
</element>
...
<element kind="component" x="130" y="135" width="200" height="30" style="Barcode">
  <component kind="barcode4j:Codabar" moduleWidth="1.2" textPosition="none" wideFactor="4.0">
    <codeExpression><![CDATA["01234567890"]] ></codeExpression>
  </component>
</element>
...
<element kind="component" x="130" y="170" width="70" height="40" style="Barcode">
  <component kind="barcode4j:DataMatrix" moduleWidth="4.0">
    <codeExpression><![CDATA["JasperReports"]] ></codeExpression>
  </component>
</element>
...
<element kind="component" x="130" y="215" width="250" height="30" style="Barcode">
  <component kind="barcode4j:EAN128" moduleWidth="1.4" checksumMode="check">
    <codeExpression><![CDATA["0101234567890128"]] ></codeExpression>
  </component>
</element>
...
<element kind="component" x="130" y="250" width="400" height="30" style="Barcode">
  <component kind="barcode4j:Code39">
    <codeExpression><![CDATA["0123456789"]] ></codeExpression>
  </component>
</element>
...
<element kind="component" x="130" y="285" width="400" height="30" style="Barcode">
  <component kind="barcode4j:USPSIntelligentMail" ascenderHeight="8.0" trackHeight="10.0">
    <codeExpression><![CDATA["00040123456200800001987654321"]] ></codeExpression>
  </component>
</element>
...
<element kind="component" x="130" y="320" width="400" height="30" style="Barcode">
  <component kind="barcode4j:RoyalMailCustomer" ascenderHeight="8.0" intercharGapWidth="2.5" trackHeight="10.0">
    <codeExpression><![CDATA["0123456789"]] ></codeExpression>
  </component>
</element>
...
<element kind="component" x="130" y="355" width="400" height="30" style="Barcode">
  <component kind="barcode4j:Interleaved2Of5">
    <codeExpression><![CDATA["0123456789"]] ></codeExpression>
  </component>
</element>
...
<element kind="component" x="130" y="390" width="400" height="30" style="Barcode">
  <component kind="barcode4j:UPCA">
    <codeExpression><![CDATA["01234567890"]] ></codeExpression>
  </component>
</element>
...
<element kind="component" x="130" y="425" width="400" height="30" style="Barcode">
  <component kind="barcode4j:UPCE">
    <codeExpression><![CDATA["01234133"]] ></codeExpression>
  </component>
</element>
...
<element kind="component" x="130" y="460" width="400" height="30" style="Barcode">
  <component kind="barcode4j:EAN13">
    <codeExpression><![CDATA["012345678901"]] ></codeExpression>
  </component>
</element>
...
<element kind="component" x="130" y="495" width="400" height="30" style="Barcode">
  <component kind="barcode4j:EAN8">
    <codeExpression><![CDATA["01234565"]] ></codeExpression>
  </component>
</element>
...
<element kind="component" x="130" y="530" width="400" height="20" style="Barcode">
  <component kind="barcode4j:POSTNET" shortBarHeight="10.0" checksumMode="add">
    <codeExpression><![CDATA["01234"]] ></codeExpression>
  </component>
</element>
...
<element kind="component" x="130" y="555" width="400" height="30" style="Barcode">
  <component kind="barcode4j:PDF417" moduleWidth="4.0">
    <codeExpression><![CDATA["JasperReports"]] ></codeExpression>
  </component>
</element>
...
<element kind="component" mode="Opaque"... forecolor="#0000FF" backcolor="#FFFF00" style="Barcode">
  <component kind="barcode4j:QRCode" margin="2" errorCorrectionLevel="M">
    <codeExpression><![CDATA["http://barcode4j.sourceforge.net/"]] ></codeExpression>
  </component>
</element>
```

### Running the Sample

Running the sample requires the [Apache Maven](https://maven.apache.org) library. Make sure that `maven` is already installed on your system (version 3.6 or later).\
In a command prompt/terminal window set the current folder to `demo/samples/barcode4j` within the JasperReports source project and run the following command:

```
> mvn clean compile exec:exec@all
```

It will generate all supported document types containing the sample report in the `demo/samples/barcode4j/target/reports` directory.
