import aws_cdk as core
import aws_cdk.assertions as assertions

from public_ecr_repositories.public_ecr_repositories_stack import PublicEcrRepositoriesStack

# example tests. To run these tests, uncomment this file along with the example
# resource in public_ecr_repositories/public_ecr_repositories_stack.py
def test_sqs_queue_created():
    app = core.App()
    stack = PublicEcrRepositoriesStack(app, "public-ecr-repositories")
    template = assertions.Template.from_stack(stack)

#     template.has_resource_properties("AWS::SQS::Queue", {
#         "VisibilityTimeout": 300
#     })
