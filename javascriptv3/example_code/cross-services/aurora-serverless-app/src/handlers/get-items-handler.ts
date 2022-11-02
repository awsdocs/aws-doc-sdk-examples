import { Handler } from "src/types/handler.js";
import { command as getAllItemsCommand } from "../statement-commands/get-all-items.js";
import { command as getArchivedItemsCommand } from "../statement-commands/get-archived-items.js";
import { command as getActiveItemsCommand } from "../statement-commands/get-active-items.js";
import { parseItem } from "./parse-item.js";

const getItemsHandler: Handler = {
  withClient:
    ({ rdsDataClient }) =>
    async (req, res) => {
      const archived = req.query.archived as string;

      const commands = {
        true: getArchivedItemsCommand,
        false: getActiveItemsCommand,
      };

      const response = await rdsDataClient.send<{ records: DBRecords }>(
        commands[archived] || getAllItemsCommand
      );

      res.send(response.records.map(parseItem));
    },
};

export { getItemsHandler };
