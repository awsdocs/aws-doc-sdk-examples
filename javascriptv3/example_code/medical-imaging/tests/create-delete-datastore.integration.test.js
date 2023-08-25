import {describe, it, expect} from "vitest";

import {createDatastore} from "../actions/create-datastore.js";
import {deleteDatastore} from "../actions/delete-datastore.js";

const sleep = (delay) => new Promise((resolve) => setTimeout(resolve, delay))
describe("createDatastore/deleteDatastore", () => {
        let datastoreID = "";
        const datastoreName = "createDeleteDatastoreTest";

        it("should create and delete a datastore", async () => {
            // Create topic.
            const createDatastoreCommandOutput = await createDatastore(datastoreName);
            datastoreID = createDatastoreCommandOutput.datastoreId;

            expect(datastoreID).toBeDefined();

            await sleep(10000); // Wait for the datastore to be created.

            const getDatastoreCommandOutput = await createDatastore(datastoreName);
            expect(getDatastoreCommandOutput.datastoreId).toEqual(datastoreID);

            const listDatastoresCommandOutput = await createDatastore(datastoreName);
            let found = false;
            for (const datastore of listDatastoresCommandOutput) {
                if (datastore.datastoreId === datastoreID) {
                    found = true;
                    break;
                }
            }

            expect(found).toBe(true);

            // Delete topic.
            await deleteDatastore(datastoreID);
        });
    },
    20000);
