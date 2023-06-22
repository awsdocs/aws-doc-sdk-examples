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

export class EnvModel extends Model {
  constructor(scope: Construct, { restApi }: PartialModelProps) {
    super(scope, "EnvModel", {
      restApi,
      schema: {
        schema: JsonSchemaVersion.DRAFT4,
        type: JsonSchemaType.OBJECT,
        properties: {
          COGNITO_SIGN_IN_URL: {
            schema: JsonSchemaVersion.DRAFT4,
            type: JsonSchemaType.STRING,
          },
          COGNITO_SIGN_OUT_URL: {
            schema: JsonSchemaVersion.DRAFT4,
            type: JsonSchemaType.STRING,
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
