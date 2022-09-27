/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import * as R from "ramda";
import {
  __,
  compose,
  map,
  equals,
  length,
  includes,
  all,
  type,
  is,
  allPass,
  slice,
  curry,
  nth,
  gte,
} from "ramda";

const operators = ["add", "subtract", "multiply", "divide"];

const LogLevel = {
  DEBUG: 0,
  INFO: 1,
  WARN: 2,
};

const env = (varName) => process.env[varName];

const ifLogLevel = curry((level, outputFn) => {
  if (gte(level, env("LOG_LEVEL"))) {
    outputFn();
  }
});

const ifInfo = ifLogLevel(LogLevel.INFO);
const ifWarn = ifLogLevel(LogLevel.WARN);

const isLength3 = compose(equals(3), length);

const isValidOperator = compose(includes(__, operators), nth(0));

const hasValidOperands = compose(all(is(Number)), map(parseInt), slice(1, 3));

const isValid = allPass([isLength3, isValidOperator, hasValidOperands]);

const throwInputError = (input) => {
  throw new Error(
    `Invalid arguments. Expected [<operation>, <number>, <number>. Got: ${type(
      input
    )}(${JSON.stringify(input)})`
  );
};

const operate = ([operator, operand1, operand2]) =>
  R[operator](operand1, operand2);

const handler = async (input) => {
  if (!isValid(input)) {
    throwInputError(input);
  }

  ifInfo(() =>
    console.log(
      `Performing ${input[0]} operation on ${input[1]} and ${input[2]}`
    )
  );

  const result = operate(input);

  if (equals(result, Infinity)) {
    ifWarn(() =>
      console.log(
        `Function returned an 'Infinity' value. This can happen when the divisor is 0.`
      )
    );
  }

  return result;
};

export { handler };
