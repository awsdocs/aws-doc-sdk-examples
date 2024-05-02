using AWS.Messaging;
using Microsoft.AspNetCore.Mvc;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// Configure the AWS Message Processing Framework for .NET
builder.Services.AddAWSMessageBus(builder =>
{
    // Check for input SQS URL.
    if ((args.Length == 1) && (args[0].Contains("https://sqs.")))
    {
        // Register that you'll publish messages of type GreetingMessage 
        // 1. To a specified queue,
        // 2. using the message identifier "greetingMessage", which will be used
        //    by handlers to route the message to the appropriate handler.
        builder.AddSQSPublisher<GreetingMessage>(args[0], "greetingMessage");
    }
    // You can map additional message types to queues or topics here as well
});
var app = builder.Build();


// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();

// Create an API Endpoint that receives GreetingMessage objects 
// from the caller and then sends them as an SQS message.
app.MapPost("/greeting", async ([FromServices]IMessagePublisher publisher, GreetingMessage message) =>
{
    if (message.SenderName == null || message.Greeting == null)
    {
        return Results.BadRequest();
    }

    // Publish the message the queue configured above
    await publisher.PublishAsync(message);

    return Results.Ok();
})
.WithName("SendGreeting")
.WithOpenApi();

app.Run();

/// <summary>
/// This class represents the message contents
/// </summary>
public class GreetingMessage
{
    public string? SenderName { get; set; }
    public string? Greeting { get; set; }
}