import { map, nth, pipe } from "ramda";
import { splitMapTrim } from "../ext-ramda.js";
import { getNewLineDelimitedEntries } from "./util-fs.js";

const getFirstEntry = pipe(
  getNewLineDelimitedEntries,
  map(pipe(splitMapTrim(","))),
  nth(0)
);

const getSecondValuesFromEntries = pipe(
  getNewLineDelimitedEntries,
  map(pipe(splitMapTrim(","), nth(1)))
);

const getFirstValuesFromEntries = pipe(
  getNewLineDelimitedEntries,
  map(pipe(splitMapTrim(","), nth(0)))
);

export { getFirstEntry, getFirstValuesFromEntries, getSecondValuesFromEntries };
