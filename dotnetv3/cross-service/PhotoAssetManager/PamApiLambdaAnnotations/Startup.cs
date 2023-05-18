using Microsoft.Extensions.DependencyInjection;

namespace PamApiLambdaAnnotations
{
    [Amazon.Lambda.Annotations.LambdaStartup]
    public class Startup
    {
        /// <summary>
        /// Services for Lambda functions can be registered in the services dependency injection container in this method. 
        ///
        /// The services can be injected into the Lambda function through the containing type's constructor or as a
        /// parameter in the Lambda function using the FromService attribute. Services injected for the constructor have
        /// the lifetime of the Lambda compute container. Services injected as parameters are created within the scope
        /// of the function invocation.
        /// </summary>
        public void ConfigureServices(IServiceCollection services)
        {
            //// Example of creating the IConfiguration object and
            //// adding it to the dependency injection container.
            //var builder = new ConfigurationBuilder()
            //                    .AddJsonFile("appsettings.json", true);

            //// Add AWS Systems Manager as a potential provider for the configuration. This is 
            //// available with the Amazon.Extensions.Configuration.SystemsManager NuGet package.
            //builder.AddSystemsManager("/app/settings");

            //var configuration = builder.Build();
            //services.AddSingleton<IConfiguration>(configuration);

            //// Example of using the AWSSDK.Extensions.NETCore.Setup NuGet package to add
            //// the Amazon S3 service client to the dependency injection container.
            //services.AddAWSService<Amazon.S3.IAmazonS3>();
        }
    }
}
