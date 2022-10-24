import { SendRawEmailCommand } from "@aws-sdk/client-ses";
import { createMimeMessage, TextFormat } from "mimetext";
import { format } from "prettier";
import { Handler } from "src/types/handler.js";
import { command as getActiveItemsCommand } from "../statement-commands/get-active-items.js";

const makeCsv = (records: DBRecords) => {
  return records.reduce((prev, next) => {
    return `${prev}\n${next.map((prop) => `${Object.values(prop)[0]}`)}`;
  }, "id,description,guide,status,name,archived" as string);
};

const toHtmlTableCellData = (tag: "td" | "th") => (data: string) =>
  `<${tag}>${data}</${tag}>`;

const toHtmlTableRow = (isHeaderRow: boolean, columnsString: string) =>
  `<tr>${columnsString
    .split(",")
    .map(toHtmlTableCellData(isHeaderRow ? "th" : "td"))
    .join("")}</tr>`;

const csvToHtmlTable = (csv: string) => {
  const html = csv
    .split("\n")
    .map((line, index, all) => {
      let html = "";
      const isHeaderRow = index === 0;

      if (isHeaderRow) {
        html += "<table>";
      }

      html += toHtmlTableRow(isHeaderRow, line);

      if (index === all.length - 1) {
        html += "</table>";
      }

      return html;
    })
    .join("");

  return format(html, { parser: "html" });
};

const buildSendRawEmailCommand = (
  emailAddress: string,
  reportData: { csv: string; date: string; itemCount: number }
) => {
  const msg = createMimeMessage();
  msg.setSender({ name: emailAddress.split("@")[0], addr: emailAddress });
  msg.setTo({ name: emailAddress.split("@")[0], addr: emailAddress });
  msg.setSubject(`Work Item Report for ${reportData.date}`);
  msg.setMessage(
    "text/html",
    `<h1>Item Report</h1>
    ${csvToHtmlTable(reportData.csv)}`
  );
  msg.setMessage("text/plain", "Report");
  msg.setAttachment(
    "report.csv",
    "text/csv" as TextFormat,
    Buffer.from(reportData.csv).toString("base64")
  );

  return new SendRawEmailCommand({
    RawMessage: {
      Data: Buffer.from(msg.asRaw()),
    },
  });
};

const postItemsReportHandler: Handler = {
  withClient:
    ({ rdsDataClient, sesClient }) =>
    async (req, res) => {
      const { records } = await rdsDataClient.send<{ records: DBRecords }>(
        getActiveItemsCommand
      );
      const date = new Date().toLocaleString("en-US");
      const csv = makeCsv(records);
      const command = buildSendRawEmailCommand(req.body.email, {
        csv,
        date,
        itemCount: records.length,
      });
      await sesClient.send(command);

      res.send({});
    },
};

export { postItemsReportHandler, makeCsv, csvToHtmlTable };
