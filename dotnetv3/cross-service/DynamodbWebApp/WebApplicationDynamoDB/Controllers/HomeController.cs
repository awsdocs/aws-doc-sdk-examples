/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

using System.Diagnostics;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using WebApplicationDynamoDB.Models;

namespace WebApplicationDynamoDB.Controllers
{
    public class HomeController : Controller
    {
        private readonly ILogger<HomeController> _logger;

        public HomeController(ILogger<HomeController> logger)
        {
            _logger = logger;
        }

        [HttpPost]
        public async Task<ActionResult> GetRecord(string id)
        {
            var dbService = new DynamoDBService();
            var xml = await dbService.GetSingleItem(id);
            return Content(xml);
        }

        [HttpPost]
        public async Task<ActionResult> ModRecord(string id, string status)
        {
            var dbService = new DynamoDBService();
            var val = await dbService.ModStatus(id, status);
            return Content(val);
        }

        [HttpPost]
        public async Task<ActionResult> Archive(string id)
        {
            var dbService = new DynamoDBService();
            dbService.ArchiveItemEC(id);
            return Content(id);
        }

        [HttpPost]
        public async Task<ActionResult> AddRecord(string guide, string description, string status)
        {
            var dbService = new DynamoDBService();
            var value = await dbService.AddNewRecord(description, guide, status);
            return Content(value);
        }

        [HttpGet]
        public async Task<ActionResult> PutReport()
        {
            var dbService = new DynamoDBService();
            dbService.S3Report();
            return Content("Report was successfully sent to Amazon S3");
        }

        [HttpGet]
        public async Task<ActionResult> GetActiveItems()
        {
           var dbService = new DynamoDBService();
           var xml = await dbService.GetItems("Open");
           return Content(xml);
        }

        [HttpGet]
        public async Task<ActionResult> GetClosedItems()
        {
            var dbService = new DynamoDBService();
            var xml = await dbService.GetItems("Closed");
            return Content(xml);
        }

        public IActionResult Index()
        {
            return View();
        }

        public IActionResult Privacy()
        {
            return View();
        }

        public IActionResult Items()
        {
            return View();
        }

        public IActionResult Moditems()
        {
            return View();
        }

        [ResponseCache(Duration = 0, Location = ResponseCacheLocation.None, NoStore = true)]
        public IActionResult Error()
        {
            return View(new ErrorViewModel { RequestId = Activity.Current?.Id ?? HttpContext.TraceIdentifier });
        }
    }
}
