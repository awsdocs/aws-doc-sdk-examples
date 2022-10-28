import { describe, it, expect, afterAll } from "@jest/globals";
import { testEqual } from "../utils/util-test.js";
import { getUniqueName } from "../utils/util-string.js";
import { getFirstEntry, getFirstValuesFromEntries, getSecondValuesFromEntries } from "../utils/util-csv.js";
import { deleteFiles, setTmp } from "../utils/util-fs.js";

describe("util-csv", () => {
  const files = [];

  afterAll(() => {
    deleteFiles(files);
  });

  describe("getFirstEntry", () => {
    it("should return the first line from the CSV as an array", () => {
      // Jest tests run in parallel. If a test's file does not have a unique name
      // it could interfere with other tests.
      const filename = getUniqueName("getFirstEntry-test");
      files.push(`./${filename}.tmp`);
      setTmp(filename, `a,b,c\nd,e,f\ng,h,i\n`);

      expect(getFirstEntry(filename)).toEqual(
        expect.arrayContaining(["a", "b", "c"])
      );
    });

    it(
      "should return an array with a single empty string if the file is missing",
      testEqual(expect.arrayContaining([""]), getFirstEntry("fake-filename"))
    );
  });

  describe("getFirstValuesFromEntries", () => {
    it("should return an array of the first elements of each entry", () => {
      const filename = getUniqueName("getFirstValuesFromEntries-test");
      files.push(`./${filename}.tmp`);
      setTmp(filename, `a,b,c\nd,e,f\ng,h,i`);

      expect(getFirstValuesFromEntries(filename)).toEqual(
        expect.arrayContaining(["a", "d", "g"])
      );
    });
  });

  describe("getSecondValuesFromEntries", () => {
    it("should return an array of the second elements of each entry", () => {
      const filename = getUniqueName("getSecondValuesFromEntries-test");
      files.push(`./${filename}.tmp`);
      setTmp(filename, `a,b,c\nd,e,f\ng,h,i`);

      expect(getSecondValuesFromEntries(filename)).toEqual(
        expect.arrayContaining(["b", "e", "h"])
      );
    });
  });
});
