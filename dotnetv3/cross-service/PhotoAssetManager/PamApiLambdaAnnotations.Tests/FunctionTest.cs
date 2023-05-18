using Xunit;
using Amazon.Lambda.TestUtilities;

namespace PamApiLambdaAnnotations.Tests
{
    public class FunctionTest
    {
        public FunctionTest()
        {
        }

        [Fact]
        public void TestAdd()
        {
            TestLambdaContext context = new TestLambdaContext();
            
            var functions = new Functions();
            Assert.Equal(12, functions.Add(3, 9, context));
        }
    }
}
