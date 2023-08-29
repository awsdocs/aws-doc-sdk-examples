import {describe, it, expect} from "vitest";

import {createDatastore} from "../actions/create-datastore.js";
import {deleteDatastore} from "../actions/delete-datastore.js";
import {getDatastore} from "../actions/get-datastore.js";
import {listDatastores} from "../actions/list-datastores.js";
import { wait } from "../../libs/utils/util-timers.js";

const sleep = (delay) => new Promise((resolve) => setTimeout(resolve, delay))
describe("createDatastore/deleteDatastore", () => {
        let datastoreID = "";
        const datastoreName = "createDeleteDatastoreJSTest";

        it("should create and delete a datastore", async () => {
            // Create topic.
            const createDatastoreCommandOutput = await createDatastore(datastoreName);
            datastoreID = createDatastoreCommandOutput.datastoreId;

            expect(datastoreID).toBeDefined();

            await wait(15); // Wait for the datastore to be created.

            const getDatastoreCommandOutput = await getDatastore(datastoreID);
            expect(getDatastoreCommandOutput.datastoreId).toEqual(datastoreID);

            const listDatastoresCommandOutput  = await listDatastores();

            let found = false;
            for (const datastore of listDatastoresCommandOutput) {
                if (datastore.datastoreId === datastoreID) { // eslint-disable-line no-unsafe-member-access
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
