import { buildStatementCommand } from "./command-helper.js";

const command = buildStatementCommand(
  'select * from items where archived = 0'
);

export { command };
