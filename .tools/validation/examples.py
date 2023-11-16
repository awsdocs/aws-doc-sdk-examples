#!/usr/bin/env python3

from dataclasses import dataclass
from enum import Enum
from typing import Optional, Self
from os.path import splitext
import example_errors
from example_errors import ExampleErrors, ExampleParseError

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

    @staticmethod
    def from_yaml(yaml: any) -> None | Self | ExampleParseError:
        if yaml is None:
            return None
        title = yaml.get("title")
        url = yaml.get("url")

        if title is None:
            return example_errors.URLMissingTitle(url=url)

        return Url(title, url)


@dataclass
class Excerpt:
    description: Optional[str]
    # A path within the repo to extract the entire file as a snippet.
    snippet_files: list[str]
    # Tags embedded in source files to extract as snippets.
    snippet_tags: list[str]

    @staticmethod
    def from_yaml(yaml: any) -> Self:
        description = yaml.get("description")
        snippet_files = [str(file) for file in yaml.get("snippet_files", [])]
        snippet_tags = [str(tag) for tag in yaml.get("snippet_tags", [])]
        return Excerpt(description, snippet_files, snippet_tags)


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

    @staticmethod
    def from_yaml(yaml: dict[str, any]) -> Self | ExampleParseError:
        """
        if _, ok := mapping.Sdks[language].Sdk[version.SdkVersion]; !ok {errs = append(errs, &metaSdkVersionNotValidError{metaName: name, language: language, sdkVersion: version.SdkVersion}) }

        if version.BlockContent != "" {
                if len(crossContents) > 0 {
                        if !sliceContains(crossContents, version.BlockContent) {
                                errs = append(errs, &metaLanguageBlockContentNotFoundError{metaName: name, blockContent: version.BlockContent})
                        }
                }
                if version.Sdkguide != "" || len(version.Excerpts) > 0 {
                        errs = append(errs, &metaLanguageBlockContentVersionConflictError{metaName: name, language: language})
                }
                for addService := range version.AddServices {
                        if _, ok := mapping.Services[addService]; !ok {
                                errs = append(errs, &metaServiceNotValidError{
                                        metaName: fmt.Sprintf("%v.%v.add_service", name, language), service: addService})
                        }
                }
        } else {
                if path.Ext(version.Github) != "" {
                        errs = append(errs, &metaVersionGithubLinkToFileError{metaName: name, link: version.Github})
                }
                if strings.HasPrefix(version.Sdkguide, DOCS_DOMAIN) {
                        errs = append(errs, &metaVersionSdkGuideStartsWithDocsDomain{
                                metaName: name, language: language, sdkguide: version.Sdkguide})
                }
                if len(version.AddServices) > 0 {
                        errs = append(errs, &metaVersionAddServicesNotEmpty{metaName: name, language: language})
                }
                for _, excerpt := range version.Excerpts {
                        if err := validateEntities(excerpt.Description, name, "Excerpt.Description"); err != nil {
                                errs = append(errs, err)
                        }

                        if len(snippets) > 0 {
                                for _, tag := range excerpt.SnippetTags {
                                        if !sliceContains(snippets, tag) {
                                                errs = append(errs, &metaExcerptTagNotValidError{metaName: name, excerpt: tag})
                                        }
                                }
                        }
                }
        }
        """
        errors = ExampleErrors()

        sdk_version = int(yaml.get("sdk_version", 0))
        if sdk_version == 0:
            errors.append(example_errors.MissingField(field="sdk_version"))

        block_content = yaml.get("block_content")
        github = yaml.get("github")
        sdkguide = yaml.get("sdkguide")

        if sdkguide is not None:
            if sdkguide.startswith("https://docs.aws.amazon.com"):
                errors.append(example_errors.InvalidSdkGuideStart(guide=sdkguide))

        if github is not None:
            _, ext = splitext(github)
            if ext != "":
                errors.append(example_errors.InvalidGithubLink())

        excerpts = yaml.get("excerpts", [])
        if len(excerpts) == 0:
            excerpts = None
        else:
            excerpts = [Excerpt.from_yaml(excerpt) for excerpt in excerpts]

        if excerpts is None and block_content is None:
            errors.append(example_errors.MissingBlockContentAndExcerpt())
        if excerpts is not None and block_content is not None:
            errors.append(example_errors.BlockContentAndExcerptConflict())

        more_info = []
        for url in yaml.get("more_info", []):
            url = Url.from_yaml(url)
            if isinstance(url, Url):
                more_info.append(url)
            else:
                errors.append(url)

        add_services = parse_services(yaml.get("add_services", {}), errors)
        if add_services and block_content is not None:
            errors.append(example_errors.APIExampleCannotAddService())

        if len(errors) > 0:
            return errors

        return Version(
            sdk_version,
            block_content,
            excerpts,
            github,
            add_services,
            sdkguide,
            more_info,
        )


@dataclass
class Language:
    name: str
    versions: list[Version]

    @staticmethod
    def from_yaml(name: str, yaml: any) -> Self | ExampleErrors:
        errors = ExampleErrors()
        if name not in Languages.__members__:
            errors.append(example_errors.UnknownLanguage(language=name))

        yaml_versions = yaml.get("versions")
        if yaml_versions is None or len(yaml_versions) == 0:
            errors.append(example_errors.MissingField(field="versions"))
            yaml_versions = []

        versions: list[Version] = []
        for version in yaml_versions:
            version = Version.from_yaml(version)
            if isinstance(version, Version):
                versions.append(version)
            else:
                for error in version:
                    error.language = name
                    errors.append(error)

        if len(errors) > 0:
            return errors

        return Language(name, versions)


@dataclass
class Example:
    # Human readable title. TODO: Defaults to slug-to-title of the ID if not provided.
    title: str
    # Used in the TOC. TODO: Defaults to slug-to-title of the ID if not provided.
    title_abbrev: str
    # String label categories. Categories inferred by cross-service with multiple services, and can be whatever else it wants. Controls where in the TOC it appears.
    category: Optional[str]
    # Link to additional topic places.
    guide_topic: Url  # TODO: Url|list[Url]
    # TODO how to add a language here and require it in sdks_schema.
    languages: dict[Languages, Language]
    # TODO document service_main and services. Not to be used by tributaries. Part of Cross Service.
    # List of services used by the examples. Lines up with those in services.yaml.
    service_main: Optional[ServiceNames]
    services: dict[ServiceNames, dict[str, str]]
    synopsis: str
    synopsis_list: list[str]
    file: str
    id: str

    @staticmethod
    def from_yaml(yaml: any) -> Self | ExampleErrors:
        errors = ExampleErrors()

        title = get_with_valid_entities("title", yaml, errors)
        title_abbrev = get_with_valid_entities("title_abbrev", yaml, errors)
        synopsis = get_with_valid_entities("synopsis", yaml, errors, opt=True)

        category = yaml.get("category")
        services = parse_services(yaml.get("services", {}), errors)
        synopsis_list = [str(syn) for syn in yaml.get("synopsis_list", [])]

        guide_topic = Url.from_yaml(yaml.get("guide_topic"))
        if isinstance(guide_topic, ExampleParseError):
            errors.append(guide_topic)
            guide_topic = None

        yaml_languages = yaml.get("languages")
        languages = []
        if yaml_languages is None:
            errors.append(example_errors.MissingField(field="languages"))
        else:
            for name in yaml_languages:
                language = Language.from_yaml(name, yaml_languages[name])
                if isinstance(language, Language):
                    languages.append(language)
                else:
                    errors.extend(language)

        service_main = yaml.get("service_main", None)
        if not (service_main is None or service_main in ServiceNames.__members__):
            errors.append(example_errors.UnknownService(service=service_main))

        if len(errors) > 0:
            return errors

        return Example(
            title,
            title_abbrev,
            category,
            guide_topic,
            languages,
            service_main,
            services,
            synopsis,
            synopsis_list,
            file="",
            id="",
        )


def parse_services(
    yaml: any, errors: ExampleErrors
) -> dict[ServiceNames, dict[str, str]]:
    if yaml is None:
        return {}
    services = {}
    for name in yaml:
        if name not in ServiceNames.__members__:
            errors.append(example_errors.UnknownService(service=name))
        else:
            service = yaml.get(name, {})
            if service is None:
                service = {}
            services[name] = {key: str(service[key]) for key in service}
    return services


ALLOWED = ["&AWS;", "&AWS-Region;", "&AWS-Regions;" "AWSJavaScriptSDK"]


def get_with_valid_entities(
    name: str, d: dict[str, str], errors: ExampleErrors, opt: bool = False
) -> Optional[str]:
    field = d.get(name)
    if field is None:
        if not opt:
            errors.append(example_errors.MissingField(field=name))
        return None

    disallowed = field.count("AWS")
    allowed = sum([field.count(token) for token in ALLOWED])

    if disallowed != allowed:
        errors.append(example_errors.AwsNotEntity(field_name=name, field_value=field))
        return None

    return field


def idFormat(id: str) -> bool:
    return len(id.split("_")) >= 2


def parse(file: str, yaml: dict[str, any]) -> list[Example] | ExampleErrors:
    examples: list[Example] = []
    errors = ExampleErrors()
    for id in yaml:
        if not idFormat(id):
            errors.append(example_errors.NameFormat(file=file, id=id))
        example = Example.from_yaml(yaml[id])
        if isinstance(example, Example):
            example.file = file
            example.id = id
            examples.append(example)
        else:
            for error in example:
                error.file = file
                error.id = id
                errors.append(error)
    if len(errors) > 0:
        return errors

    return examples


if __name__ == "__main__":
    import yaml
    from pathlib import Path

    with open(
        Path(__file__).parent.parent.parent
        / ".doc_gen"
        / "metadata"
        / "s3_metadata.yaml"
    ) as file:
        meta = yaml.safe_load(file)
    examples = parse(file, meta)
    print(f"{examples!r}")
