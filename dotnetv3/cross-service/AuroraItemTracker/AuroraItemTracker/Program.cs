// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

using Amazon.RDSDataService;
using Amazon.SimpleEmailV2;
using AuroraItemTracker;

// Top level statements to set up the API.
var builder = WebApplication.CreateBuilder(args);
builder.Host.ConfigureLogging(logging =>
{
    logging.AddConsole();
});

// Add services to the container.
builder.Services.AddAWSService<IAmazonRDSDataService>();
builder.Services.AddAWSService<IAmazonSimpleEmailServiceV2>();
builder.Services.AddScoped<WorkItemService>();
builder.Services.AddScoped<ReportService>();

builder.Services.AddAuthorization();

// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// Set up CORS.
var myAllowSpecificOrigins = "AllowCORS";
builder.Services.AddCors(p => p.AddPolicy(myAllowSpecificOrigins, bldr =>
{
    bldr.AllowAnyOrigin().AllowAnyMethod().AllowAnyHeader();
}));

var app = builder.Build();
app.UsePathBase("/api");
app.UseCors(myAllowSpecificOrigins);

// Set up the Swagger UI if running in a development environment.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

// Configure the HTTP request pipeline.
app.UseRouting();
app.UseHttpsRedirection();
app.UseAuthorization();

// GET endpoint for getting a collection of work items, either with or without an archive state.
app.MapGet("/items", async (WorkItemService workItemService, bool? archived) =>
{
    var result = await workItemService.GetItems(archived);

    return result;
});

// GET endpoint for getting a single item by its ID.
app.MapGet("/items/{itemId}", async (WorkItemService workItemService, string itemId) =>
{
    var result = await workItemService.GetItem(itemId);

    return result;
});

// POST to add a new work item.
app.MapPost("/items", async (WorkItemService workItemService, WorkItem workItem) =>
{
    var result = await workItemService.CreateItem(workItem);

    return result;
});

// PUT to set the archive state of a work item by its ID.
app.MapPut("/items/{itemId}:archive", async (WorkItemService workItemService, string itemId) =>
{
    var result = await workItemService.ArchiveItem(itemId);

    return result;
});

// POST to send a CSV report to a specific email address.
app.MapPost("/items:report", async (WorkItemService workItemService, ReportService reportService, ReportRequest reportRequest) =>
{
    var activeItems = await workItemService.GetItemsByArchiveState(false);

    await reportService.SendReport(activeItems, reportRequest.Email);

    return Results.Ok();
});

app.Run();