import { ExecuteStatementCommand } from "@aws-sdk/client-rds-data";
import env from "../../env.json" assert { type: "json" };

const buildStatementCommand = (sql: string) => {
  return new ExecuteStatementCommand({
    resourceArn: env.CLUSTER_ARN,
    secretArn: env.SECRET_ARN,
    database: env.DB_NAME,
    sql,
  });
};

export { buildStatementCommand };
