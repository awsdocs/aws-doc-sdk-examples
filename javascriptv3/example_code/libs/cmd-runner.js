// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { startsWith } from "./utils/util-array.js";
import { log } from "./utils/util-log.js";

/**
 * @typedef {(...args: unknown[]) => void} HandlerFn
 *
 * @typedef {{ on: (event: string, fn: HandlerFn ) => void}} Reader
 *
 * @typedef {{
 *  reader: Reader,
 *  handlers?: []
 *  commands?: string[]
 * }} Context
 *
 * @typedef {[(a: unknown) => boolean, (a: unknown) => unknown]} Handler
 */

/**
 * @param {string} input
 */
function getCommands(input) {
  return input.trim().split(" ");
}

/**
 * @param {{ reader: Reader, handlers: Handler[] }} handlers
 */
function readCommands({ reader, handlers }) {
  reader.on("line", (input) => {
    const commands = getCommands(input);

    for (let handler of handlers) {
      if (handler[0](commands)) {
        return handler[1](commands);
      }
    }

    const isQuitCommand = startsWith(["quit"]);
    if (isQuitCommand(commands)) {
      return reader.close();
    }

    return log("Command not recognized.");
  });
}

export { readCommands, getCommands };
