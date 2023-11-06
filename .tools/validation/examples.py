from dataclasses import dataclass
from enum import Enum
from typing import Optional

Languages = Enum(
    "Language",
    [
        "Bash",
        "C++",
        "CLI",
        "Go",
        "Java",
        "JavaScript",
        "Kotlin",
        ".NET",
        "PHP",
        "Python",
        "Ruby",
        "Rust",
        "SAP ABAP",
        "Swift",
    ],
)

ServiceNames = Enum(
    "ServiceNames",
    [
        "acm",
        "api-gateway",
        "apigatewaymanagementapi",
        "application-autoscaling",
        "auditmanager",
        "aurora",
        "auto-scaling",
        "batch",
        "bedrock",
        "bedrock-runtime",
        "cloudformation",
        "cloudfront",
        "cloudwatch",
        "cloudwatch-events",
        "cloudwatch-logs",
        "codebuild",
        "cognito",
        "cognito-identity",
        "cognito-identity-provider",
        "cognito-sync",
        "comprehend",
        "config-service",
        "device-farm",
        "dynamodb",
        "ebs",
        "ec2",
        "ecr",
        "ecs",
        "eks",
        "elastic-load-balancing-v2",
        "emr",
        "eventbridge",
        "firehose",
        "forecast",
        "glacier",
        "glue",
        "iam",
        "iot",
        "keyspaces",
        "kinesis",
        "kinesis-analytics-v2",
        "kms",
        "lambda",
        "lex",
        "lookoutvision",
        "mediaconvert",
        "medialive",
        "mediapackage",
        "medical-imaging",
        "migration-hub",
        "opensearch",
        "organizations",
        "personalize",
        "personalize-runtime",
        "personalize-events",
        "pinpoint",
        "pinpoint-email",
        "pinpoint-sms-voice",
        "polly",
        "qldb",
        "rds",
        "rds-data",
        "redshift",
        "rekognition",
        "route-53",
        "route53-domains",
        "route53-recovery-cluster",
        "s3",
        "sagemaker",
        "secrets-manager",
        "ses",
        "sesv2",
        "sfn",
        "snowball",
        "sns",
        "sqs",
        "ssm",
        "sts",
        "support",
        "textract",
        "transcribe",
        "transcribe-medical",
        "translate",
    ],
)


@dataclass
class Snippet:
    id: str
    file: str
    line_start: int
    line_end: int


@dataclass
class Url:
    title: str
    url: Optional[str]


@dataclass
class Excerpt:
    description: Optional[str]
    # A path within the repo to extract the entire file as a snippet.
    snippet_files: list[str]
    # Tags embedded in source files to extract as snippets.
    snippet_tags: list[str]


@dataclass
class Version:
    sdk_version: int
    # Additional ZonBook XML to include in the tab for this sample.
    block_content: Optional[str]
    # The specific code samples to include in the example.
    excerpts: Optional[list[Excerpt]]
    # Link to the source code for this example. TODO rename.
    github: Optional[str]
    add_services: dict[ServiceNames, str]
    # Deprecated. Replace with guide_topic list.
    sdkguide: Optional[str]
    # Link to additional topic places. TODO: Overwritten by aws-doc-sdk-example when merging.
    more_info: list[Url]


@dataclass
class Language:
    versions: list[Version]


@dataclass
class Example:
    # Human readable title. TODO: Defaults to slug-to-title of the ID if not provided. Overwritten by aws-doc-sdk-example when merging.
    title: str
    # Used in the TOC. TODO: Defaults to slug-to-title of the ID if not provided. Overwritten by aws-doc-sdk-example when merging.
    title_abbrev: Optional[str]
    # String label categories. Categories inferred by cross-service with multiple services, and can be whatever else it wants. Controls where in the TOC it appears. Overwritten by aws-doc-sdk-example when merging.
    category: Optional[str]
    # Link to additional topic places. Overwritten by aws-doc-sdk-example when merging.
    guide_topic: list[Url]
    # TODO how to add a language here and require it in sdks_schema. TODO: Keys merged by aws-doc-sdk-example when merging.
    languages: dict[Languages, Language]
    # TODO document service_main and services. Not to be used by tributaries. Part of Cross Service.
    # List of services used by the examples. Lines up with those in services.yaml. Overwritten by aws-doc-sdk-example when merging.
    service_main: Optional[ServiceNames]
    services: dict[ServiceNames, dict[str, str]]
    synopsis: Optional[str]
    synopsis_list: list[str]


def parse(yaml: any) -> list[Example]:
    pass


if __name__ == "__main__":
    import yaml
    from pathlib import Path

    with open(
        Path(__file__).parent.parent / ".doc_gen" / "metadata" / "s3_metadata.yaml"
    ) as file:
        meta = yaml.safe_load(file)
    examples = parse(meta)
    print(f"{examples!r}")
