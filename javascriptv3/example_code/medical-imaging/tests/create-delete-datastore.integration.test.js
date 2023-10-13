import { describe, it, expect } from "vitest";

import { wait } from "@aws-sdk-examples/libs/utils/util-timers.js";

import { createDatastore } from "../actions/create-datastore.js";
import { deleteDatastore } from "../actions/delete-datastore.js";
import { getDatastore } from "../actions/get-datastore.js";
import { listDatastores } from "../actions/list-datastores.js";

describe("createDatastore/deleteDatastore", () => {
  let datastoreID = "";
  const datastoreName = "jstest-" + Math.floor(Math.random() * 200000000);

  it("should create and delete a data store", async () => {
    // Create topic.
    const createDatastoreCommandOutput = await createDatastore(datastoreName);
    datastoreID = createDatastoreCommandOutput.datastoreId;

    expect(datastoreID).toBeDefined();

    let status = "NONE";
    let counter = 1;
    while (counter < 20 && status !== "ACTIVE") {
      // Redundant check with test timeout.
      await wait(1);
      const getDatastoreCommandOutput = await getDatastore(datastoreID);
      status = getDatastoreCommandOutput["datastoreStatus"];
      counter++;
    }

    const listDatastoresCommandOutput = await listDatastores();

    let found = false;
    for (const datastore of listDatastoresCommandOutput) {
      if (datastore.datastoreId === datastoreID) {
        // eslint-disable-line
        found = true;
        break;
      }
    }

    expect(found).toBe(true);

    // Delete topic.
    await deleteDatastore(datastoreID);
  });
}, 20000); // 20 seconds test timeout.
