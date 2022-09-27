/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { createInterface } from "readline";
import { stdin as input, stdout as output } from "process";
import { startsWith } from "ramda";

import { readCommands } from "../../../libs/cmd-runner.js";
import { log } from "../../../libs/utils/util-log.js";

import { initializeHandler } from "./command-handlers/initialize-handler.js";
import { createFunctionHandler } from "./command-handlers/create-function-handler.js";
import { updateFunctionHandler } from "./command-handlers/update-function-code-handler.js";
import { listFunctionsHandler } from "./command-handlers/list-functions-handler.js";
import { invokeHandler } from "./command-handlers/invoke-handler.js";
import { updateFunctionConfigurationHandler } from "./command-handlers/update-function-configuration-handler.js";
import { cleanUpHandler } from "./command-handlers/clean-up-handler.js";

const cmdInterface = createInterface({ input, output });

const help = `
usage: <command> [<args>]

  Commands:

  initialize                             Creates an AWS Identity and Access Management
                                         (IAM) role with the necessary permissions.

  create-function <function_name>        If a project under '../../functions/' exists with the
                                         provided name, this zips the project and uploads it to
                                         AWS Lambda.

  update-function-code <function_name>   Updates 'function-name' with the code from 'new-function'.
    <new_function>                       'new-function' is zipped similar to create-function.

  update-function-configuration          Takes the name of a function and updates the function's
    <function_name>                      configuration with the provided config.json.
    

  list-functions                         Lists all Lambda functions for the configured account.

  invoke <function_name> [<args>]        Invokes the provided function with arguments.

  clean-up                               Removes all created functions and roles. (Note: Tampering with
                                         .zip files, .tmp files, or directory structure might cause this
                                         to fail. Always double-check your environment in the console to
                                         verify that AWS services have been cleaned up correctly.)

  quit                                   Quits the current interactive session.
`;

const handlers = [
  [startsWith(["help"]), () => log(help)],
  [startsWith(["initialize"]), initializeHandler],
  [startsWith(["create-function"]), createFunctionHandler],
  [startsWith(["update-function-code"]), updateFunctionHandler],
  [
    startsWith(["update-function-configuration"]),
    updateFunctionConfigurationHandler,
  ],
  [startsWith(["list-functions"]), listFunctionsHandler],
  [startsWith(["invoke"]), invokeHandler],
  [startsWith(["clean-up"]), cleanUpHandler],
];

cmdInterface.write('Welcome to Lambda. Type "help" for more info.\n');
readCommands({ reader: cmdInterface, handlers });
