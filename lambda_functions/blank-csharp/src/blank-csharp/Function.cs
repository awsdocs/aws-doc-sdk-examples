using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Amazon;
using Amazon.Util;
using Amazon.Lambda;
using Amazon.Lambda.Model;
using Amazon.Lambda.Core;
using Amazon.Lambda.SQSEvents;
using Amazon.XRay.Recorder.Core;
using Amazon.XRay.Recorder.Handlers.AwsSdk;
using Newtonsoft.Json;
using Newtonsoft.Json.Serialization;

[assembly: LambdaSerializer(typeof(Amazon.Lambda.Serialization.Json.JsonSerializer))]

namespace blankCsharp
{
  public class Function
  {
    private static AmazonLambdaClient lambdaClient;

    static Function() {
      initialize();
    }

    static async void initialize() {
      AWSSDKHandler.RegisterXRayForAllServices();
      lambdaClient = new AmazonLambdaClient();
      await callLambda();
    }

    public async Task<AccountUsage> FunctionHandler(SQSEvent invocationEvent, ILambdaContext context)
    {
      GetAccountSettingsResponse accountSettings;
      try
      {
        accountSettings = await callLambda();
      }
      catch (AmazonLambdaException ex)
      {
        throw ex;
      }
      AccountUsage accountUsage = accountSettings.AccountUsage;
      LambdaLogger.Log("ENVIRONMENT VARIABLES: " + JsonConvert.SerializeObject(System.Environment.GetEnvironmentVariables()));
      LambdaLogger.Log("CONTEXT: " + JsonConvert.SerializeObject(context));
      LambdaLogger.Log("EVENT: " + JsonConvert.SerializeObject(invocationEvent));
      return accountUsage;
    }

    public static async Task<GetAccountSettingsResponse> callLambda()
    {
      var request = new GetAccountSettingsRequest();
      var response = await lambdaClient.GetAccountSettingsAsync(request);
      return response;
    }
  }
}
