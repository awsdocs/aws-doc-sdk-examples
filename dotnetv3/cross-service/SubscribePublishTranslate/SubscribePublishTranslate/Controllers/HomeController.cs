// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier:  Apache-2.0

namespace SNSExample.Controllers
{
    using System.Diagnostics;
    using System.Threading.Tasks;
    using Microsoft.AspNetCore.Mvc;
    using Microsoft.Extensions.Logging;
    using SNSExample.Models;

    public class HomeController : Controller
    {
        private readonly ILogger<HomeController> _logger;

        public HomeController(ILogger<HomeController> logger)
        {
            _logger = logger;
        }

        [HttpPost]
        public async Task<ActionResult> PublishMessage(string body, string lang)
        {
            var snsService = new SnsService();
            var id = await snsService.PubTopic(body, lang);
            return Content($"Message {id} was successfully published");
        }

        [HttpPost]
        public async Task<ActionResult> RemoveEmailSub(string email)
        {
            var snsService = new SnsService();
            var msg = await snsService.UnSubEmail(email);
            return Content(msg);
        }

        [HttpPost]
        public async Task<ActionResult> AddEmailSub(string email)
        {
            var snsService = new SnsService();
            var arn = await snsService.SubEmail(email);
            return Content(arn);
        }

        [HttpGet]
        public async Task<ActionResult> GetAjaxValue()
        {
            var snsService = new SnsService();
            var xml = await snsService.GetSubs();
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

        [ResponseCache(Duration = 0, Location = ResponseCacheLocation.None, NoStore = true)]
        public IActionResult Error()
        {
            return View(new ErrorViewModel { RequestId = Activity.Current?.Id ?? HttpContext.TraceIdentifier });
        }
    }
}
