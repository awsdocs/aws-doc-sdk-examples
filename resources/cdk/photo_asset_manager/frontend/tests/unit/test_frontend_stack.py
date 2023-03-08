import aws_cdk as core
import aws_cdk.assertions as assertions

from frontend.frontend_stack import FrontendStack

# example tests. To run these tests, uncomment this file along with the example
# resource in frontend/frontend_stack.py
def test_sqs_queue_created():
    app = core.App()
    stack = FrontendStack(app, "frontend")
    template = assertions.Template.from_stack(stack)

#     template.has_resource_properties("AWS::SQS::Queue", {
#         "VisibilityTimeout": 300
#     })
