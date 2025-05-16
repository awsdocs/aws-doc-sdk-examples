# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from typing import Dict, Set, Tuple, Any, List, Optional, Union
from .metadata import (
    Example,
    Language,
    Url,
    Version,
    Excerpt,
    Person,
)
from .sdks import Sdk
from .services import Service
from . import metadata_errors
from .metadata_errors import MetadataErrors, DuplicateItemException, MetadataParseError
from .project_validator import ValidationConfig
from .metadata_validator import StringExtension


def example_from_yaml(
    yaml: Any,
    sdks: Dict[str, Sdk],
    services: Dict[str, Service],
    blocks: Set[str],
    validation: ValidationConfig,
) -> Tuple[Example, MetadataErrors]:
    errors = MetadataErrors()

    title = get_with_valid_entities("title", yaml, errors, True)
    title_abbrev = get_with_valid_entities("title_abbrev", yaml, errors, True)
    synopsis = get_with_valid_entities("synopsis", yaml, errors, opt=True)
    synopsis_list = [str(syn) for syn in yaml.get("synopsis_list", [])]

    source_key = yaml.get("source_key")
    guide_topic = url_from_yaml(yaml.get("guide_topic"))
    if isinstance(guide_topic, MetadataParseError):
        errors.append(guide_topic)
        guide_topic = None

    parsed_services = parse_services(yaml.get("services", {}), errors)
    category = yaml.get("category", "")
    if category == "":
        category = "Api" if len(parsed_services) == 1 else "Scenarios"
    is_action = category == "Api"
    is_basics = category == "Basics"

    if is_action:
        svc_actions = []
        for svc, actions in parsed_services.items():
            for action in actions:
                svc_actions.append(f"{svc}:{action}")
        if len(svc_actions) != 1:
            errors.append(
                metadata_errors.APIMustHaveOneServiceOneAction(
                    svc_actions=", ".join(svc_actions)
                )
            )

    if validation.strict_titles:
        if is_action:
            if title or title_abbrev or synopsis or synopsis_list:
                errors.append(metadata_errors.APICannotHaveTitleFields())
        elif is_basics:
            # Basics examples can have custom titles or no titles (in this case they're generated).
            if not (synopsis or synopsis_list):
                errors.append(metadata_errors.BasicsMustHaveSynopsisField())
        else:
            if not (title and title_abbrev and (synopsis or synopsis_list)):
                errors.append(metadata_errors.NonAPIMustHaveTitleFields())

    service_main = yaml.get("service_main", None)
    if service_main is not None and service_main not in services:
        try:
            errors.append(metadata_errors.UnknownService(service=service_main))
        except DuplicateItemException:
            pass

    yaml_languages = yaml.get("languages")
    languages: Dict[str, Language] = {}
    if yaml_languages is None:
        errors.append(metadata_errors.MissingField(field="languages"))
    else:
        for name in yaml_languages:
            language, errs = language_from_yaml(
                name, yaml_languages[name], sdks, blocks, is_action
            )
            languages[language.name] = language
            errors.extend(errs)

    return (
        Example(
            id="",
            file=None,
            title=title,
            title_abbrev=title_abbrev,
            category=category,
            guide_topic=guide_topic,
            languages=languages,
            service_main=service_main,
            services=parsed_services,
            synopsis=synopsis,
            synopsis_list=synopsis_list,
            source_key=source_key,
        ),
        errors,
    )


def excerpt_from_yaml(yaml: Any) -> Tuple["Excerpt", MetadataErrors]:
    description = yaml.get("description")
    snippet_files = [str(file) for file in yaml.get("snippet_files", [])]
    snippet_tags = [str(tag) for tag in yaml.get("snippet_tags", [])]
    genai = yaml.get("genai", "none")

    errors = MetadataErrors()
    if genai not in {"none", "some", "most", "all"}:
        errors.append(metadata_errors.FieldError(field="genai", value=genai))

    return (Excerpt(description, snippet_tags, snippet_files, genai), errors)


def get_with_valid_entities(
    name: str, d: Dict[str, str], errors: MetadataErrors, opt: bool = False
) -> str:
    field = d.get(name)
    if field is None:
        if not opt:
            errors.append(metadata_errors.MissingField(field=name))
        return ""

    checker = StringExtension()
    if not checker.is_valid(field):
        errors.append(
            metadata_errors.AwsNotEntity(
                field=name, value=field, check_err=checker.get_name()
            )
        )
        return ""
    return field


def language_from_yaml(
    name: str,
    yaml: Any,
    sdks: Dict[str, Sdk],
    blocks: Set[str],
    is_action: bool,
) -> Tuple[Language, MetadataErrors]:
    errors = MetadataErrors()
    if name not in sdks:
        errors.append(metadata_errors.UnknownLanguage(language=name))

    sdk = sdks.get(name)
    property = sdk.property if sdk else ""

    yaml_versions: Union[List[Dict[str, Any]], None] = yaml.get("versions")
    if yaml_versions is None or len(yaml_versions) == 0:
        errors.append(metadata_errors.MissingField(field="versions"))
        yaml_versions = []

    versions: List[Version] = []
    for version in yaml_versions:
        vers, version_errors = version_from_yaml(version, blocks, is_action)
        errors.extend(version_errors)
        versions.append(vers)

    for error in errors:
        if isinstance(error, MetadataParseError):
            error.language = name

    return Language(name, property, versions), errors


def parse_services(yaml: Any, errors: MetadataErrors) -> Dict[str, Set[str]]:
    if yaml is None:
        return {}
    services: Dict[str, Set[str]] = {}
    for name in yaml:
        service: Union[Dict[str, None], Set[str], None] = yaml.get(name)
        # While .get replaces missing with {}, `sqs: ` in yaml parses a literal `None`
        if service is None:
            service = set()
        if isinstance(service, dict):
            service = set(service.keys())
        if isinstance(service, set):
            # Make a copy of the set for ourselves
            service = set(service)
        services[name] = set(service)
    return services


def url_from_yaml(
    yaml: Union[None, Dict[str, Optional[str]]]
) -> Optional[Union[Url, MetadataParseError]]:
    if yaml is None:
        return None
    title = yaml.get("title", "")
    url = yaml.get("url", "")

    if title is None:
        return metadata_errors.URLMissingTitle(url=str(url))

    return Url(title, url)


def person_from_yaml(
    yaml: Union[None, Dict[str, Optional[str]]]
) -> Optional[Union[Person, MetadataParseError]]:
    if yaml is None:
        return None
    name = yaml.get("name")
    alias = yaml.get("alias")

    if name is None or alias is None:
        return metadata_errors.PersonMissingField(name=str(name), alias=str(alias))

    return Person(name, alias)


def version_from_yaml(
    yaml: Dict[str, Any],
    cross_content_blocks: Set[str],
    is_action: bool,
) -> Tuple["Version", MetadataErrors]:
    errors = MetadataErrors()

    sdk_version = int(yaml.get("sdk_version", 0))
    if sdk_version == 0:
        errors.append(metadata_errors.MissingField(field="sdk_version"))

    block_content = yaml.get("block_content")
    github = yaml.get("github")
    sdkguide = yaml.get("sdkguide")

    if sdkguide is not None:
        if sdkguide.startswith("https://docs.aws.amazon.com"):
            errors.append(metadata_errors.InvalidSdkGuideStart(guide=sdkguide))

    excerpts = []
    for excerpt in yaml.get("excerpts", []):
        parsed, parse_errors = excerpt_from_yaml(excerpt)
        excerpts.append(parsed)
        errors.extend(parse_errors)

    if len(excerpts) == 0 and block_content is None:
        errors.append(metadata_errors.MissingBlockContentAndExcerpt())
        excerpts = []
    if len(excerpts) > 0 and block_content is not None:
        errors.append(metadata_errors.BlockContentAndExcerptConflict())

    more_info: List[Url] = []
    for url in yaml.get("more_info", []):
        url = url_from_yaml(url)
        if isinstance(url, Url):
            more_info.append(url)
        elif url is not None:
            errors.append(url)

    authors: List[Person] = []
    for author in yaml.get("authors", []):
        author = person_from_yaml(author)
        if isinstance(author, Person):
            authors.append(author)
        elif author is not None:
            errors.append(author)

    owner = yaml.get("owner")
    if owner and not isinstance(owner, str):
        errors.append(metadata_errors.InvalidFieldType(reason="must be string"))
        owner = None

    add_services = parse_services(yaml.get("add_services", {}), errors)
    if add_services:
        errors.append(
            metadata_errors.AddServicesHasBeenDeprecated(add_services=add_services)
        )

    source = None
    if source_url := yaml.get("source", None):
        source_url = url_from_yaml(source_url)
        if isinstance(source_url, Url):
            source = source_url
        elif source_url is not None:
            errors.append(source_url)

    if block_content is not None and block_content not in cross_content_blocks:
        errors.append(metadata_errors.MissingCrossContent(block=block_content))

    for error in errors:
        if hasattr(error, "sdk_version"):
            error.sdk_version = sdk_version

    return (
        Version(
            sdk_version,
            block_content,
            excerpts,
            github,
            sdkguide,
            more_info,
            authors,
            owner,
            source,
        ),
        errors,
    )
