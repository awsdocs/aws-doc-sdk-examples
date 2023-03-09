#!/usr/bin/env node
import { ok } from "assert";
import * as cdk from "aws-cdk-lib";
import { PamLambdasStrategy } from "../lib/pam-stack/lambdas";
import { PamStack } from "../lib/pam-stack/stack";
import {
  EMPTY_LAMBDAS_STRATEGY,
  JAVA_LAMBDAS_STRATEGY,
  PYTHON_LAMBDAS_STRATEGY,
} from "../lib/pam-stack/strategies";

const name = process.env["PAM_NAME"];
const email = process.env["PAM_EMAIL"];

ok(name, "Missing PAM_NAME");
ok(email, "Missing PAM_EMAIL");

function getStrategy(language: string = ""): PamLambdasStrategy {
  switch (language.toLowerCase()) {
    case "python":
      return PYTHON_LAMBDAS_STRATEGY;
    case "java":
      return JAVA_LAMBDAS_STRATEGY;
    default:
      return EMPTY_LAMBDAS_STRATEGY;
  }
}

const strategy = getStrategy(process.env["PAM_LANGUAGE"]);

const app = new cdk.App();
new PamStack(app, `${name}PamStack`, { email, strategy });
