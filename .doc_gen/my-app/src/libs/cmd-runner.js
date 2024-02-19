/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import {
  cond,
  invoker,
  startsWith,
  compose,
  split,
  trim,
  T,
  prop,
  apply,
} from "ramda";

import { flipApplyMap } from "./ext-ramda.js";
import { log } from "./utils/util-log.js";

const readerProp = prop("reader");
const handlersProp = prop("handlers");

const close = invoker(0, "close");
const on = invoker(2, "on");

const processCommands = (context) =>
  cond([
    ...handlersProp(context),
    [startsWith(["quit"]), () => close(readerProp(context))],
    [T, () => log("Command not recognized.")],
  ]);

const getCommands = compose(split(" "), trim);

const handleInput = (context) => compose(processCommands(context), getCommands);

/**
 *
 * @param {{
 *  reader: { on: (event: string) => void, close: () => void },
 *  handlers?: [(a: any) => boolean, (a: any) => any][]
 *  commands?: string[]
 * }} context
 * @returns
 */
const readCommands = compose(
  apply(on("line")),
  flipApplyMap([handleInput, readerProp])
);

export {
  readCommands,
  readerProp,
  handlersProp,
  close,
  on,
  processCommands,
  getCommands,
  handleInput,
};
