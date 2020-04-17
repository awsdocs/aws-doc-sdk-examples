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
        public void CheckRDSInstancesTest()
        {
            Task.Run(Program.CheckRDSInstances).Wait();
        }

        [TestMethod()]
        public void CheckS3BucketsTest()
        {
            Task.Run(Program.CheckS3Buckets).Wait();
        }
    }
}