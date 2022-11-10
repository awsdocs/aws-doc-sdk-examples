/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { log } from "../log.js";
/** snippet-start:[javascript.v3.glue.scenarios.basic.CreateCrawlerStep] */
const crawlerExists = async ({ getCrawler }, crawlerName) => {
  try {
    await getCrawler(crawlerName);
    return true;
  } catch {
    return false;
  }
};

const makeCreateCrawlerStep = (actions) => async (context) => {
  if (await crawlerExists(actions, context.envVars.CRAWLER_NAME)) {
    log("Crawler already exists. Skipping creation.");
  } else {
    await actions.createCrawler(
      context.envVars.CRAWLER_NAME,
      context.envVars.ROLE_NAME,
      context.envVars.DATABASE_NAME,
      context.envVars.TABLE_PREFIX,
      context.envVars.S3_TARGET_PATH
    );

    log("Crawler created successfully.", { type: "success" });
  }

  return { ...context };
};
/** snippet-end:[javascript.v3.glue.scenarios.basic.CreateCrawlerStep] */
export { makeCreateCrawlerStep };
