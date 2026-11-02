# XXE / TransformerFactory findings (Talos, CWE-611) — investigation report

READ-ONLY investigation. No code was changed.

## Summary

- All 8 flagged files exist locally and are byte-identical to the flagged commit `679128b` (`git diff --stat 679128b HEAD -- <8 files>` is empty). The flagged line numbers match exactly.
- Every finding points at the same line in the same pattern: `Transformer transformer = transformerFactory.newTransformer();` inside a private `convertToString(Document)` method.
- In every file the factory comes from a local `getSecureTransformerFactory()` helper that sets only `XMLConstants.FEATURE_SECURE_PROCESSING`. None set `XMLConstants.ACCESS_EXTERNAL_DTD` or `XMLConstants.ACCESS_EXTERNAL_STYLESHEET`. A repo-wide grep for `ACCESS_EXTERNAL` in `*.java` returns zero matches. That missing pair of attributes is what the scanner wants, and it's the exact fix the ticket recommends.
- Exploitability: low, close to a false positive. Each `Transformer` is an identity transform (no XSLT stylesheet). It serializes an in-memory DOM that the app built with `DocumentBuilder.newDocument()` + `createElement`/`createTextNode` from AWS SDK results (SNS, S3, DynamoDB, an Excel file in S3). No external XML or XSLT is parsed anywhere on these paths, and `createTextNode` escapes the values. So there's no DTD or external entity for the parser to resolve.
- Recommendation: fix all 8 (plus nothing else needed, see below) rather than ask for a severity downgrade. The change is two `setAttribute` lines in each helper, it doesn't change behavior, and this is public sample code that customers copy into apps where the input may not be trusted.

## Common pattern (applies to all 8)

```java
private String convertToString(Document xml) {
    try {
        TransformerFactory transformerFactory = getSecureTransformerFactory();
        Transformer transformer = transformerFactory.newTransformer();   // <-- flagged line
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(xml);
        transformer.transform(source, result);
        return result.getWriter().toString();
    } catch (TransformerException ex) {
        ex.printStackTrace();
    }
    return null;
}

private static TransformerFactory getSecureTransformerFactory() {
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    try {
        transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    } catch (TransformerConfigurationException e) {
        e.printStackTrace();
    }
    return transformerFactory;
}
```

The companion `toXml(...)` in each file uses `DocumentBuilderFactory.newInstance()` with `setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)` and then `builder.newDocument()`. That's already the strongest DocumentBuilderFactory hardening, and it never parses input anyway, so it needs no change.

`javax.xml.XMLConstants` is already imported in all 8 files, so no new import is needed.

### Minimal secure fix (same for every file, inside `getSecureTransformerFactory()`)

```java
private static TransformerFactory getSecureTransformerFactory() {
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    try {
        transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    } catch (TransformerConfigurationException e) {
        e.printStackTrace();
    }
    // Disallow DTDs and external stylesheets (prevents XXE, CWE-611).
    transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
    transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
    return transformerFactory;
}
```

Notes:
- `setAttribute` throws only the unchecked `IllegalArgumentException`. That happens only when the implementation doesn't support JAXP 1.5. None of the 8 `pom.xml` files pull in Xalan or Saxon (checked by grep), so the JDK's built-in XSLTC is used, and it supports both attributes (JDK 7u40+). The projects target Java 17, except `create_spring_stream_app`, which targets 11.
- Because the fix sits in the shared helper, the flagged `newTransformer()` line itself doesn't change. Scanners like Talos usually trace the factory back to its configuration. If the scanner still flags it after the change, a fallback is to inline the two `setAttribute` calls in `convertToString` right before `newTransformer()`, as in the ticket's example.

## Per-finding evidence

| # | File (under `javav2/usecases/`) | Flagged line | Helper (`TransformerFactory.newInstance`) | DOM data source | Hardening present |
|---|---|---|---|---|---|
| 1 | `creating_sns_sample_app/src/main/java/com/spring/sns/SnsService.java` | 207 `newTransformer()` in `convertToString` (204) | `getSecureTransformerFactory()` L219–227, newInstance L220 | `getAllSubscriptions()` → SNS `listSubscriptionsByTopic` endpoints → `toXml(List<String>)` (L177) | FSP only; DBF has disallow-doctype-decl (L179) |
| 2 | `creating_sns_async/src/main/java/com/spring/sns/SnsService.java` | 241 (`convertToString` 238) | L253–261, newInstance L254 | SNS async subscriptions list → `toXml(List<String>)` (L210) | FSP only; DBF disallow-doctype-decl (L213) |
| 3 | `create_spring_stream_app/src/main/java/com/example/VideoStreamService.java` | 218 (`convertToString` 214) | L230–238, newInstance L231 | S3 object tags → `List<Tags>` → `toXml` (L180, called L109) | FSP set twice (helper L233 and redundant again at L217); DBF disallow-doctype-decl (L183) |
| 4 | `workflow_multiple_channels/src/main/java/com/example/GetStudents.java` | 126 (`convertToString` 123) | `getSecureTransformerFactory()` (instance method) L138, newInstance L139 | DynamoDB scan → `List<Student>` → `toXml` (L84, called L80) | FSP only; DBF disallow-doctype-decl (L87) |
| 5 | `Creating_etl_workflow/src/main/java/com/etl/example/ExcelService.java` | 213 (`convertToString` 210) | L225, newInstance L226 | `getData()`: Excel workbook bytes from S3 read with JExcel (`Workbook.getWorkbook`), cell strings → `List<PopData>` → `toXml` (L124, called L115) | FSP only; DBF disallow-doctype-decl (L127) |
| 6 | `creating_photo_analyzer_app/src/main/java/com/example/photo/S3Service.java` | 192 (`convertToString` 189) | L204, newInstance L205 | S3 `listObjects` → `List<BucketItem>` → `toXml` (L144, called L92) | FSP only; DBF disallow-doctype-decl (L147) |
| 7 | `creating_photo_analyzer_async/src/main/java/com/example/photo/S3Service.java` | 267 (`convertToString` 264) | L279, newInstance L280 | S3 async `listObjects` → `List<BucketItem>` → `toXml` (L220, called L210) | FSP only; DBF disallow-doctype-decl (L223) |
| 8 | `video_analyzer_application/src/main/java/com/example/video/S3Service.java` | 146 (`convertToString` 143, static) | L158, newInstance L159 | S3 `listObjects` → `List<BucketItem>` → `toXml` (L98, called L66) | FSP only; DBF disallow-doctype-decl (L101) |

What the code does at each site: `TransformerFactory.newTransformer()` with no arguments makes an identity transformer. It takes a `DOMSource` holding the app-built `Document` and writes it to a `StringWriter`. The XML string goes back to the web view or caller.

Data flow, all sites: the `Document` comes from `DocumentBuilder.newDocument()` and is filled with `createElement`/`createTextNode`. No `parse(...)`, no `StreamSource` and no stylesheet appear in any of the 8 files (grep for `parse(`/`StreamSource` came back empty). Values from AWS (subscription endpoints, S3 keys/tags, DynamoDB items, Excel cells) go into text nodes, which get escaped on output. An attacker who controls those values (an S3 key or an Excel cell, say) can't inject a DOCTYPE or entity reference that gets resolved. So no site has a real XXE path today. The scanner flags the pattern: a TransformerFactory without the ACCESS_EXTERNAL_* attributes.

On FEATURE_SECURE_PROCESSING: in the JDK's JAXP implementation, setting FSP explicitly through the API should also restrict the `accessExternal*` properties (JAXP 1.5 behavior). That means the current code is probably already protected at runtime. The scanner doesn't credit FSP alone, though, and the behavior depends on the implementation. Setting the attributes explicitly is the portable, scanner-recognized fix. I didn't verify this at runtime.

Extra note for #3 (`VideoStreamService.java`): line 217 repeats `setFeature(FEATURE_SECURE_PROCESSING, true)` after the helper already set it. It's harmless and redundant. You can remove it during the fix, but it's optional.

## Same pattern elsewhere in javav2 (not flagged)

A full grep of `javav2/**/*.java` for `TransformerFactory.newInstance`, `DocumentBuilderFactory.newInstance`, `SAXParserFactory.newInstance`, `XMLInputFactory.new*`, `SchemaFactory.newInstance`, `XPathFactory.newInstance` and `new SAXBuilder` found:

- TransformerFactory / DocumentBuilderFactory: only in the 8 flagged files. There are no other unflagged TransformerFactory sites in javav2.
- JDOM2 `SAXBuilder`. This is real parsing of a string, but it's already hardened and wasn't flagged:
  - `usecases/Creating_etl_workflow/src/main/java/com/etl/example/DynamoDBService.java` L30–33: `new SAXBuilder()` + `setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)`, then `builder.build(new InputSource(new StringReader(myDom)))`.
  - `usecases/workflow_multiple_channels/src/main/java/com/example/SendNotifications.java` L33–36 and L58–61: same pattern and same hardening.
  - These parse a `myDom` string coming back from the client. That makes them the only paths where outside XML is actually parsed, and the disallow-doctype-decl feature already blocks XXE there. No change needed.

Shared helper: none. Each of the 8 files has its own copy of `getSecureTransformerFactory()` (copy-paste pattern, 6 static and GetStudents' instance method). The use-case projects are separate Maven projects, so a cross-project shared utility isn't practical. Fix each copy the same way.

## Conclusions and recommendation

1. Root cause: `getSecureTransformerFactory()` sets FSP but not `ACCESS_EXTERNAL_DTD`/`ACCESS_EXTERNAL_STYLESHEET`, which is what Talos (Aristotle rec 174) requires.
2. Real risk: low. Input is an app-built DOM from trusted AWS SDK responses, nothing external gets parsed, and this is an identity transform. That would qualify for the Sev2→Sev3 downgrade path.
3. Recommended action: fix all 8 anyway.
   - These are public samples customers copy. If someone reuses the helper with a `StreamSource` built from untrusted XML or XSLT, it would be vulnerable on non-JDK or older implementations.
   - The fix is 2 lines per file, the import is already present, and there's no behavior change.
   - It closes all 8 findings at once with no AppSec exemption needed.
4. Per file: add the two `setAttribute` lines to `getSecureTransformerFactory()` (snippet above). Optionally drop the redundant FSP line at `VideoStreamService.java` L217. Leave the `DocumentBuilderFactory` and `SAXBuilder` code as is.
5. Verify each touched project with `mvn -q compile` in its `javav2/usecases/<project>` directory. After merge, confirm the Talos findings auto-resolve on the next scan. If any are still open, inline the attributes at the flagged `newTransformer()` call sites.
