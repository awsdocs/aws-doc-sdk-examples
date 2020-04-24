using Microsoft.VisualStudio.TestTools.UnitTesting;
using Getting_Started_VS;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;


namespace Getting_Started_VS.Tests
{
    [TestClass()]
    public class ProgramTests
    {
        [TestMethod()]
        public async Task CheckRDSInstancesTest()
        {
            await Program.CheckRDSInstances();

            //Task.Run(Program.CheckRDSInstances).Wait();
        }

        [TestMethod()]
        public async Task CheckS3BucketsTest()
        {
            await Program.CheckS3Buckets();
        }
    }
}