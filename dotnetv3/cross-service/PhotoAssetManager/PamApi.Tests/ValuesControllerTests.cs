using System.Text.Json;
using Xunit;
using Amazon.Lambda.Core;
using Amazon.Lambda.TestUtilities;
using Amazon.Lambda.APIGatewayEvents;



namespace PamApi.Tests;

public class ValuesControllerTests
{


    [Fact]
    public async Task TestGet()
    {
        var lambdaFunction = new LambdaEntryPoint();

        var requestStr = File.ReadAllText("./SampleRequests/ValuesController-Get.json");
        var request = JsonSerializer.Deserialize<APIGatewayProxyRequest>(requestStr, new JsonSerializerOptions
        {
            PropertyNameCaseInsensitive = true
        });
        var context = new TestLambdaContext();
        var response = await lambdaFunction.FunctionHandlerAsync(request, context);

        Assert.Equal(200, response.StatusCode);
        Assert.Equal("[\"value1\",\"value2\"]", response.Body);
        Assert.True(response.MultiValueHeaders.ContainsKey("Content-Type"));
        Assert.Equal("application/json; charset=utf-8", response.MultiValueHeaders["Content-Type"][0]);
    }


}