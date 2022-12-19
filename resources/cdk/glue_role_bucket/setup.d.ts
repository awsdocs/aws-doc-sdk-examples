#!/usr/bin/env node
import 'source-map-support/register';
import { Construct } from "constructs";
import { Stack, StackProps } from 'aws-cdk-lib';
export declare class SetupStack extends Stack {
    constructor(scope: Construct, id: string, props?: StackProps);
}
