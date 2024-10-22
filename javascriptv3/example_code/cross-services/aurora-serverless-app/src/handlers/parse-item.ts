// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import type { Item } from "../types/item.js";
import type { DBRecord } from "../types/db-record.js";

const parseItem = (record: DBRecord): Item => {
  return {
    id: `${record[0].stringValue}`,
    description: `${record[1].stringValue}`,
    guide: `${record[2].stringValue}`,
    status: `${record[3].stringValue}`,
    name: `${record[4].stringValue}`,
    archived: Boolean(record[5].longValue),
  };
};

export { parseItem };
