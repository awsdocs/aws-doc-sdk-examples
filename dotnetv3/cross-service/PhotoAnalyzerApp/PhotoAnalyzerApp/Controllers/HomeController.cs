/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using PhotoAnalyzerApp.Models;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Threading.Tasks;

namespace PhotoAnalyzerApp.Controllers
{
    public class HomeController : Controller
    {
        private readonly ILogger<HomeController> _logger;
        private String bucketName = "<Enter Bucket Name";

        [HttpGet]
        public async Task<ActionResult> GetObjects()
        {
           var awsService = new AWSService();
           var xml = await awsService.ListBucketObjects(bucketName);
           return Content(xml);
        }

        [HttpPost]
        public async Task<ActionResult> GetReport(string email)
        {
            var myemail = email;
            var awsService = new AWSService();
            List<List<WorkItem>> myList = new List<List<WorkItem>>();
            var myNameList = await awsService.ListBucketNames(bucketName);
            foreach (var obName in myNameList)
            {
                List<WorkItem> labelList = await awsService.DetectLabels(bucketName, obName);
                myList.Add(labelList);
            }
            // Now we have a list of WorkItems describing the photos in the S3 bucket.
            var xmlReport = awsService.GenerateXMLFromList(myList);
            awsService.SendMessage(xmlReport, myemail);
            return Content("Report was sent with "+ myList.Count() +" items ");
        }


        public HomeController(ILogger<HomeController> logger)
        {
            _logger = logger;
        }

        public IActionResult Index()
        {
            return View();
        }

        public IActionResult Process()
        {
            return View();
        }

        public IActionResult Privacy()
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
