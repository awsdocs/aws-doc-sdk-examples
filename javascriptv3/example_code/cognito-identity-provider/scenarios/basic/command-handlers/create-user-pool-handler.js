// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { logger } from "@aws-doc-sdk-examples/lib/utils/util-log.js";
import { getTmp, setTmp } from "@aws-doc-sdk-examples/lib/utils/util-fs.js";
import { createUserPool } from "../../../actions/create-user-pool.js";
import { FILE_USER_POOLS, NAME_CLIENT } from "./constants.js";
import { setUserPoolMfaConfig } from "../../../actions/set-user-pool-mfa-config.js";
import { createUserPoolClient } from "../../../actions/create-user-pool-client.js";

const storeUserPoolMeta = (...args) => {
  const tmp = getTmp(FILE_USER_POOLS);
  const entry = args.join(",");
  setTmp(FILE_USER_POOLS, tmp ? `${tmp}\n${entry}` : entry);
};

const validateUserPool = (poolName) => {
  if (!poolName) {
    throw new Error(
      `User pool name is missing. It must be provided as an argument to the 'initialize' command.`,
    );
  }

  const tmp = getTmp(FILE_USER_POOLS);

  if (tmp) {
    throw new Error(
      `A user pool already exists. Run 'clean-up' to delete any existing user pools created with this tool.`,
    );
  }
};

const createUserPoolHandler = async (commands) => {
  const [_, poolName] = commands;

  try {
    validateUserPool(poolName);

    logger.log(`Creating user pool: ${poolName}`);

    const {
      UserPool: { Id },
    } = await createUserPool(poolName);
    logger.log("User pool created.");

    logger.log(
      "Configuring user pool to only allow MFA via an authenticator app.",
    );
    await setUserPoolMfaConfig(Id);
    logger.log("MFA configured.");

    logger.log(`Creating user pool client: ${NAME_CLIENT}`);
    const {
      UserPoolClient: { ClientId },
    } = await createUserPoolClient(NAME_CLIENT, Id);
    logger.log("Client created.");

    storeUserPoolMeta(Id, ClientId, poolName);
  } catch (err) {
    logger.error(err);
  }
};

export { createUserPoolHandler };
