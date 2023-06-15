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

export class Empty extends Model {
  constructor(scope: Construct, props: PartialModelProps) {
    super(scope, "Empty", {
      ...props,
      schema: { schema: JsonSchemaVersion.DRAFT4, type: JsonSchemaType.OBJECT },
    });
  }
}
