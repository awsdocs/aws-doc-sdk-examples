import {
  IAuthorizer,
  LambdaIntegration,
  Model,
  PassthroughBehavior,
  Resource,
  RestApi,
} from "aws-cdk-lib/aws-apigateway";
import { Function } from "aws-cdk-lib/aws-lambda";
import { Construct } from "constructs";
import { Empty } from "./app-api-models";

export interface AppRoutesProps {
  api: RestApi;
}

export interface RouteProps {
  path: string;
  fn: Function;
  method: string;
  authorizer?: IAuthorizer;
  model: {
    request?: Model;
    response?: Model;
    event?: boolean;
  };
}

export class AppRoutes extends Construct {
  apiRoot: Resource;
  constructor(
    scope: Construct,
    id: string,
    private readonly props: AppRoutesProps
  ) {
    super(scope, id);
    this.apiRoot = this.props.api.root.addResource("api");
  }

  addLambdaRoute({ path, fn, method, authorizer, model }: RouteProps) {
    const resource = this.apiRoot.addResource(path);
    const emptyResponse = new Empty(this, { restApi: this.props.api });
    resource.addMethod(
      method,
      new LambdaIntegration(fn, {
        ...(model.event && EVENT_INTEGRATION_OPTIONS),
      }),
      {
        requestModels: {
          "application/json": model.request ?? emptyResponse,
        },
        methodResponses: [statusOkResponse(model.response ?? emptyResponse)],
        ...(authorizer ? { authorizer } : {}),
      }
    );
  }
}

function statusOkResponse(response: Model) {
  return {
    statusCode: "200",
    responseParameters: {
      "method.response.header.Access-Control-Allow-Origin": true,
    },
    responseModels: { "application/json": response },
  };
}

const EVENT_INTEGRATION_OPTIONS = {
  proxy: false,
  passthroughBehavior: PassthroughBehavior.WHEN_NO_TEMPLATES,
  requestParameters: {
    "integration.request.header.X-Amz-Invocation-Type": "'Event'",
  },
  integrationResponses: [
    {
      statusCode: "200",
      responseParameters: {
        "method.response.header.Access-Control-Allow-Origin": "'*'",
      },
    },
  ],
};
