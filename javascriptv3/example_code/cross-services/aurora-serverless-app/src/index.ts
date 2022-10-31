import express from "express";
import cors from "cors";
import { rdsDataClient, sesClient } from "./client.js";
import { getItemsHandler } from "./handlers/get-items-handler.js";
import { postItemsHandler } from "./handlers/post-items-handler.js";
import { postItemsReportHandler } from "./handlers/post-items-report-handler.js";
import { putItemsArchiveHandler } from "./handlers/put-items-archive-handler.js";
import { validateDb } from "./middleware/validate-db.js";

const exp = express();
const port = 8080;

exp.use(cors());
exp.use(validateDb.withClient({ rdsDataClient }));
exp.use(express.json());

exp.get("/api", (_req, res) => res.send({ version: "1.0" }));

exp.get("/api/items", getItemsHandler.withClient({ rdsDataClient }));

exp.post(
  "/api/items\\:report",
  postItemsReportHandler.withClient({ rdsDataClient, sesClient })
);

exp.post("/api/items", postItemsHandler.withClient({ rdsDataClient }));

exp.put(
  "/api/items/:itemId\\:archive",
  putItemsArchiveHandler.withClient({ rdsDataClient })
);

exp.listen(port, () => {
  console.log(`App is listening on port: ${port}`);
});
