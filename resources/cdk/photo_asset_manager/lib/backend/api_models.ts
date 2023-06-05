import {
  JsonSchemaType,
  JsonSchemaVersion,
  Model,
  ModelProps,
} from "aws-cdk-lib/aws-apigateway";
import { Construct } from "constructs";

interface PartialModelProps {
  restApi: ModelProps["restApi"];
}

export class LabelsResponseModel extends Model {
  constructor(scope: Construct, props: PartialModelProps) {
    super(scope, "LabelsResponseModel", {
      ...props,
      schema: {
        schema: JsonSchemaVersion.DRAFT4,
        type: JsonSchemaType.OBJECT,
        properties: {
          labels: {
            type: JsonSchemaType.OBJECT,
            additionalProperties: {
              type: JsonSchemaType.OBJECT,
              properties: {
                count: {
                  type: JsonSchemaType.INTEGER,
                },
              },
            },
          },
        },
      },
    });
  }
}

export class UploadRequestModel extends Model {
  constructor(scope: Construct, props: PartialModelProps) {
    super(scope, "UploadRequestModel", {
      ...props,
      schema: {
        schema: JsonSchemaVersion.DRAFT4,
        type: JsonSchemaType.OBJECT,
        properties: {
          ["file_name"]: {
            schema: JsonSchemaVersion.DRAFT4,
            type: JsonSchemaType.STRING,
          },
        },
      },
    });
  }
}

export class UploadResponseModel extends Model {
  constructor(scope: Construct, props: PartialModelProps) {
    super(scope, "UploadResponseModel", {
      ...props,
      schema: {
        schema: JsonSchemaVersion.DRAFT4,
        type: JsonSchemaType.OBJECT,
        properties: {
          ["url"]: {
            schema: JsonSchemaVersion.DRAFT4,
            type: JsonSchemaType.STRING,
          },
        },
      },
    });
  }
}

export class DownloadRequestModel extends Model {
  constructor(scope: Construct, props: PartialModelProps) {
    super(scope, "DownloadRequestModel", {
      ...props,
      schema: {
        schema: JsonSchemaVersion.DRAFT4,
        type: JsonSchemaType.OBJECT,
        properties: {
          labels: {
            type: JsonSchemaType.ARRAY,
            items: { type: JsonSchemaType.STRING },
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
