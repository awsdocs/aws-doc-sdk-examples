/**
 * Cognito User Pool
 * API Gateway
 */

import {
  Cors,
  LambdaIntegration,
  Model,
  RestApi,
} from "aws-cdk-lib/aws-apigateway";
import { Function } from "aws-cdk-lib/aws-lambda";
import { Construct } from "constructs";
import { PamAuth } from "./auth";
import { PamLambda } from "./lambdas";
import * as models from "./models";

export interface PamApiProps {
  lambdas: PamLambda;
  email: string;
}

export class PamApi extends Construct {
  readonly auth: PamAuth;
  readonly restApi: RestApi;
  private readonly empty: Model;
  constructor(scope: Construct, id: string, props: PamApiProps) {
    super(scope, id);

    this.auth = new PamAuth(this, "PamAuth", { email: props.email });

    const restApi = (this.restApi = new RestApi(this, "PamRestApi", {
      defaultCorsPreflightOptions: {
        allowOrigins: Cors.ALL_ORIGINS,
        allowCredentials: true,
      },
    }));

    const lambdas = props.lambdas.fns;
    this.empty = new models.Empty(this, { restApi });
    this.route("labels", lambdas.labels, "GET", {
      response: new models.LabelsResponseModel(this, { restApi }),
    });

    this.route("upload", lambdas.upload, "PUT", {
      request: new models.UploadRequestModel(this, { restApi }),
      response: new models.UploadResponseModel(this, { restApi }),
    });

    this.route("s3_copy", lambdas.copy, "PUT", {
      request: new models.CopyRequestModel(this, { restApi }),
      response: new models.CopyResponseModel(this, { restApi }),
    });

    this.route("restore", lambdas.download, "PUT", {
      request: new models.DownloadRequestModel(this, { restApi }),
    });
  }

  private route(
    path: string,
    fn: Function,
    method: string,
    {
      request = this.empty,
      response = this.empty,
    }: {
      request?: Model;
      response?: Model;
    }
  ) {
    const resource = this.restApi.root.addResource(path);
    resource.addMethod(method, new LambdaIntegration(fn), {
      requestModels: { "application/json": request },
      methodResponses: [
        {
          statusCode: "200",
          responseModels: { "application/json": response },
        },
      ],
      // TODO: Uncomment after testing
      // authorizer=this.authorizer,
      // authorization_type=apigateway.AuthorizationType.COGNITO,
    });
  }
}
