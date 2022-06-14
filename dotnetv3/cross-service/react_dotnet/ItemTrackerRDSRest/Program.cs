using ItemTrackerRDSRest;
var myAllowSpecificOrigins = "AllowCORS";
var builder = WebApplication.CreateBuilder(args);

builder.Services.AddCors(options =>
{
    options.AddPolicy(name: "AllowCORS", builder =>
    {
        builder.AllowAnyOrigin()
               .AllowAnyMethod()
               .AllowAnyHeader();
    });
});

var app = builder.Build();
app.UseCors(myAllowSpecificOrigins);

app.MapGet("/api/items/{state}", async (string state) =>
{
    var database = new RDSService();
    List<WorkItem> data = new List<WorkItem>();
    if (state == "active")
    {
        data = await database.GetItemsData(0);
    }
    else
    {
        data = await database.GetItemsData(1);
    }

    return data;
});



app.MapPut("/api/report/{email}", async (string email) =>
{
    var database = new RDSService();
    var myreport = await database.GetItemsReport(0);
    database.SendMessage(myreport, email);
    return "Report sent to " + email;
});

app.MapPut("/api/mod/{id}", async (string id) =>
{
    var database = new RDSService();
    var msg = await database.FlipItemArchive(id);
    return msg;
});

app.MapPost("/api/add", async (string guide, string description, string status) =>
{
    var database = new RDSService();
    var myWork = new WorkItem();
    myWork.Guide = guide;
    myWork.Description = description;
    myWork.Status = status;
    myWork.Name = "User";
    var msg = await database.injestNewSubmission(myWork);
    return msg;
});

app.Run();
