import { Item } from "../types/item.js";

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
