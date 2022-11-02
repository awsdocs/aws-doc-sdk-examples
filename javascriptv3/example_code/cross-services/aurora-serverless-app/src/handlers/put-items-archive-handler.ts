import type { Handler } from "src/types/handler.js";
import { buildStatementCommand } from "../statement-commands/command-helper.js";

const putItemsArchiveHandler: Handler = {
  withClient:
    ({ rdsDataClient }) =>
    async (req, res) => {
      const { itemId } = req.params;

      const command = buildStatementCommand(
        "update items\n" + "set archived = 1\n" + `where iditem = "${itemId}"`
      );

      await rdsDataClient.send(command);
      res.send({});
    },
};

export { putItemsArchiveHandler };
