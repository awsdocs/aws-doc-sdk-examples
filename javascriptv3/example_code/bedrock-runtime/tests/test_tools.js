import { expect } from "vitest";

/**
 * Expects the provided string to be a non-empty string.
 *
 * @param {string} string - The string to be checked.
 */
export const expectToBeANonEmptyString = (string) => {
    expect(string.length).not.toBe(null);
    expect(typeof string).toBe("string");
    expect(string).not.toBe("");
}