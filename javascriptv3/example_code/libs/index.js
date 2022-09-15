import { v4 as uuidv4 } from "uuid";

export const getUniqueName = (name) => `${uuidv4()}-${name.toLowerCase()}`;

/**
 *
 * @param { string } source the string to modify
 * @param { string } str the string to affix to the source
 * @returns { string } the postfixed source
 */
export const postfix = (source, str) => {
  if (typeof str !== "string") {
    throw new Error("Cannot postfix a non-string value.");
  }

  return `${source}${str}`;
};
