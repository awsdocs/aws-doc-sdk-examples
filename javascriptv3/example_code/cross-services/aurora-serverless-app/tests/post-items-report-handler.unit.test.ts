import { describe, expect, it } from "@jest/globals";
import {
  csvToHtmlTable,
  makeCsv,
} from "../src/handlers/post-items-report-handler.js";

const records = [
  [
    { stringValue: "abcd-1234-asfd-sdff" },
    { stringValue: "Test csv maker." },
    { stringValue: "js" },
    { stringValue: "in-progress" },
    { stringValue: "corepyle" },
    { longValue: 0 },
  ],

  [
    { stringValue: "dlj2-lkjf-asfd-sdff" },
    { stringValue: "Test csv maker2." },
    { stringValue: "js" },
    { stringValue: "in-progress" },
    { stringValue: "corepyle" },
    { longValue: 1 },
  ],
];

describe("postItemsReportHandler", () => {
  describe("makeCsv", () => {
    it("should produce a valid csv with static headers from DBRecord content", () => {
      const result = makeCsv(records);
      expect(result).toBe(
        "id,description,guide,status,name,archived" +
          `\n${records[0][0].stringValue},${records[0][1].stringValue},${records[0][2].stringValue},${records[0][3].stringValue},${records[0][4].stringValue},${records[0][5].longValue}` +
          `\n${records[1][0].stringValue},${records[1][1].stringValue},${records[1][2].stringValue},${records[1][3].stringValue},${records[1][4].stringValue},${records[1][5].longValue}`
      );
    });
  });

  describe("csvToHtmlTable", () => {
    it("should produce an HTML table matching the CSV content", () => {
      const csv = makeCsv(records);
      const html = csvToHtmlTable(csv);
      expect(html).toBe(
`<table>
  <tr>
    <th>id</th>
    <th>description</th>
    <th>guide</th>
    <th>status</th>
    <th>name</th>
    <th>archived</th>
  </tr>
  <tr>
    <td>abcd-1234-asfd-sdff</td>
    <td>Test csv maker.</td>
    <td>js</td>
    <td>in-progress</td>
    <td>corepyle</td>
    <td>0</td>
  </tr>
  <tr>
    <td>dlj2-lkjf-asfd-sdff</td>
    <td>Test csv maker2.</td>
    <td>js</td>
    <td>in-progress</td>
    <td>corepyle</td>
    <td>1</td>
  </tr>
</table>
`
      );
    });
  });
});
