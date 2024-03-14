import {expect} from "vitest";

export const expectToBeANonEmptyString = (string) => {
    expect(string.length).not.toBe(null);
    expect(typeof string).toBe("string");
    expect(string).not.toBe("");
}