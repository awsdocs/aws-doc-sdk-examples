// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import {
  JsonSchemaType,
  JsonSchemaVersion,
  Model,
  ModelProps,
} from "aws-cdk-lib/aws-apigateway";
import { Construct } from "constructs";

export interface PartialModelProps {
  restApi: ModelProps["restApi"];
}
const JSON_SCHEMA_STRING = {
  schema: JsonSchemaVersion.DRAFT4,
  type: JsonSchemaType.STRING,
};

export class EnvModel extends Model {
  constructor(scope: Construct, { restApi }: PartialModelProps) {
    super(scope, "EnvModel", {
      restApi,
      schema: {
        schema: JsonSchemaVersion.DRAFT4,
        type: JsonSchemaType.OBJECT,
        properties: {
          COGNITO_SIGN_IN_URL: JSON_SCHEMA_STRING,
          COGNITO_SIGN_OUT_URL: JSON_SCHEMA_STRING,
        },
      },
    });
  }
}

export class GetFeedbackModel extends Model {
  constructor(scope: Construct, { restApi }: PartialModelProps) {
    super(scope, "GetFeedbackModel", {
      restApi,
      schema: {
        schema: JsonSchemaVersion.DRAFT4,
        type: JsonSchemaType.OBJECT,
        properties: {
          feedback: {
            schema: JsonSchemaVersion.DRAFT4,
            type: JsonSchemaType.ARRAY,
            items: {
              schema: JsonSchemaVersion.DRAFT4,
              type: JsonSchemaType.OBJECT,
              properties: {
                key: JSON_SCHEMA_STRING,
                text: JSON_SCHEMA_STRING,
                audioUrl: JSON_SCHEMA_STRING,
              },
            },
          },
        },
      },
    });
  }
}

export class Empty extends Model {
  constructor(scope: Construct, props: PartialModelProps) {
    super(scope, "Empty", {
      ...props,
      schema: { schema: JsonSchemaVersion.DRAFT4, type: JsonSchemaType.OBJECT },
    });
  }
}

export class UploadModel extends Model {
  constructor(scope: Construct, props: PartialModelProps) {
    super(scope, "UploadModel", {
      ...props,
      schema: {},
    });
  }
}

export class DownloadModel extends Model {
  constructor(scope: Construct, props: PartialModelProps) {
    super(scope, "DownloadModel", {
      ...props,
      schema: JSON_SCHEMA_STRING,
    });
  }
}
