import sys
from scanner import Scanner

class LinkGenerator:
    def __init__(self):
        self.language = input("language, e.g. ruby: ").lower()
        self.version = input("version, e.g. v3: ").lower()

        services_list = ["cloudtrail", "codebuild", "ec2", "elastictranscoder", "iam", "lambda", "rds", "secretsmanager", "sns", "workdocs", "cloudwatch", "dynamodb", "elasticbeanstalk", "eventbridge", "kms", "polly", "s3", "ses", "sqs"]

        scanner = Scanner('../../.doc_gen/metadata')
        services = scanner.services()
        for key, value in services.items():
            if key in services_list:
                self.service_name = key
                self.short_name = value['expanded']['short']
                camel_case = self.short_name.replace(" ", "")
                camel_case = camel_case.replace("AWS", "")
                self.camel_case = camel_case.replace("Amazon", "")
                self.long_name = value['expanded']['long']
                self.service_dev_guide = value['guide']['url']
                self.service_api_ref = value['api_ref']
                self.generate_text()
            # for sub_key, sub_value in value.items():
            #     breakpoint()
            #     print(f"  {sub_key}: {sub_value}")
        # self.language = input("language, e.g. ruby: ").lower()
        # self.version = input("version, e.g. v3: ").lower()
        # self.service_camel = input("service name in camelcase: ")
        # self.service_marketing = input("marketing name: ").lower()

    def more_examples(self):
        link = f"https://docs.aws.amazon.com/sdk-for-ruby/3/developer-guide/ruby_{self.service_name}_code_examples.html"
        # print(link)
        return link

    def dev_guide(self):
        link = f'https://aws.amazon.com/developer/language/ruby/'
        # print(link)
        return link

    def language_service_module_ref(self):
        link = f'https://docs.aws.amazon.com/sdk-for-ruby/v3/api/Aws/{self.camel_case}.html'
        # print(link)
        return link

    # def service_dev_guide(self):
    #     link = f"https://docs.aws.amazon.com/{self.service_marketing}/latest/developerguide/Introduction.html"
    #     print(link)
    #     return link
    #
    # def service_api_ref(self):
    #     link = f"https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/Welcome.html"
    #     print(link)
    #     return link

    def generate_text(self):
        text = f"""
        ################### {self.service_name} ###########################
        * [More Ruby {self.short_name} code examples]({self.more_examples()})
        * [SDK for Ruby Developer Guide]({self.dev_guide()})
        * [SDK for Ruby {self.short_name} Module]({self.language_service_module_ref()})
        * [{self.short_name} Developer Guide](https://docs.aws.amazon.com/{self.service_dev_guide})
        * [{self.short_name} API Reference](https://docs.aws.amazon.com/{self.service_api_ref})
        """
        print(text)
        print('')

