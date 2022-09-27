
/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { it, describe, expect, jest } from "@jest/globals";
import { startsWith } from "ramda";
import {
  close,
  getCommands,
  handleInput,
  handlersProp,
  on,
  processCommands,
  readCommands,
  readerProp,
} from "../cmd-runner";
import { testEqual } from "../utils/util-test";

describe("cmd-runner", () => {
  const getContext = (
    handlers = [],
    reader = { on: jest.fn(), close: jest.fn() }
  ) => ({
    reader,
    handlers,
  });

  describe("readerProp", () => {
    it(
      "should return the 'reader' property of an object",
      testEqual("MyReader", readerProp({ reader: "MyReader" }))
    );
  });

  describe("handlersProp", () => {
    it(
      "should return the 'handlers' property of an object",
      testEqual("MyHandlers", handlersProp({ handlers: "MyHandlers" }))
    );
  });

  describe("close", () => {
    it("should call the close function on an object", () => {
      const fn = jest.fn();
      const closeable = { close: fn };
      close(closeable);
      expect(fn).toHaveBeenCalled();
    });
  });

  describe("on", () => {
    it("should call the 'on' function on an object with two arguments", () => {
      const fn = jest.fn();
      const onable = { on: fn };
      on("a", "b", onable);
      expect(fn).toHaveBeenCalledWith("a", "b");
    });
  });

  describe("processCommands", () => {
    it(
      "should return an error message if the command is not recognized",
      testEqual(
        "Command not recognized.",
        processCommands(getContext())(["hello"])
      )
    );

    it(
      "should return the handler result for a given command",
      testEqual(
        "success",
        processCommands(getContext([[(x) => x === 1, () => "success"]]))(1)
      )
    );
  });

  describe("getCommands", () => {
    it(
      "should split commands by spaces",
      testEqual(["do", "thing"], getCommands("do thing"))
    );

    it(
      "should downcase all commands",
      testEqual(["do", "thing"], getCommands("DO THING"))
    );

    it(
      "should trim spaces off the ends of commands",
      testEqual(["do", "thing"], getCommands("   do thing   "))
    );
  });

  describe("handleInput", () => {
    it(
      "should convert commands to actions",
      testEqual(
        "Woof!",
        handleInput(getContext([[startsWith(["speak"]), () => "Woof!"]]))(
          "speak"
        )
      )
    );
  });

  describe("readCommands", () => {
    it("should take a context object and set up a listener for 'line' events", () => {
      const handlers = [[startsWith(["ping"]), () => "pong"]];
      const reader = {
        on: (_, fn) => fn("ping "),
      };
      const context = getContext(handlers, reader);
      const result = readCommands(context);
      expect(result).toBe("pong")
    });
  });
});
