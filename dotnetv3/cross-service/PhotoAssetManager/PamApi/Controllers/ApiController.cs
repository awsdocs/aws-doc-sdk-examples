using Microsoft.AspNetCore.Cors;
using Microsoft.AspNetCore.Mvc;

namespace PamApi.Controllers;

[Route("")]
public class ApiController : ControllerBase
{
    // PUT prod/upload
    [HttpPut("upload")]
    public IActionResult Upload([FromBody] string file_name)
    {
        var response = new { url = "testurl" };
        return Ok(response);
    }

    // GET prod/labels
    [HttpGet("labels")]
    public IActionResult Get(int id)
    {
        var response = new Dictionary<string, Dictionary<string, Dictionary<string, int>>> ();
        var labels = new Dictionary<string, Dictionary<string, int>>();
        labels.Add("sunrise", new Dictionary<string, int> { { "count", 3 } });
        labels.Add("beach", new Dictionary<string, int> { { "count", 1 } });
        response.Add("labels", labels);
        return Ok(response);
    }

    // POST prod/download
    [HttpPost("download")]
    public IActionResult Post([FromBody] string[] labels)
    {
        return Ok();
    }
}