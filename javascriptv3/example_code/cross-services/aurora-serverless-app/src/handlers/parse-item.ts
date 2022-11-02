import { pipe, zipWith, fromPairs } from "ramda";
import { Item } from "src/types/item.js";

const makePairs = (key: string, value: DBRecordValue) => {
  if (key === "archived") {
    return [key, Boolean(value.longValue)];
  } else {
    return [key, value.stringValue];
  }
};

const parseItem: (record: DBRecord) => Item = pipe(
  zipWith(makePairs, [
    "id",
    "description",
    "guide",
    "status",
    "name",
    "archived",
  ]),
  fromPairs
);

export { parseItem };
