# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from __future__ import annotations

import re
from dataclasses import dataclass, field
from pathlib import Path
from typing import Optional, Iterator, Iterable, List, TypeVar, Generic, Dict, Set


ErrorT = TypeVar("ErrorT")


class InvalidItemException(Exception, Generic[ErrorT]):
    def __init__(self, item: ErrorT):
        super().__init__(self, f"Cannot append {item!r} to ExampleErrors")


class ErrorsList(Generic[ErrorT]):
    """MyPy isn't catching List[Foo].append(List[Foo])"""

    def __init__(self, no_duplicates: bool = False):
        self.no_duplicates = no_duplicates
        self._errors: List[ErrorT] = []

    def append(self, item: ErrorT):
        # Look up the generic type. This is reliant on the internal implementation
        # of __orig_bases__, but it will definitely fail tests if a python minor
        # version breaks it.
        generic = self.__orig_bases__[0]  # type: ignore
        T = generic.__args__[0]
        if not isinstance(item, T):
            raise InvalidItemException(item)

        """
        It is dangerous to go alone: 🗡️
        If you're seeing duplicated Errors, and aren't sure why, uncommenting these lines may help you debug it.
        """
        # if item in self._errors:
        #     raise DuplicateItemException(item)
        self._errors.append(item)

    def extend(self, errors: Iterable[ErrorT]):
        self._errors.extend(errors)

    def maybe_extend(self, maybe_errors: K | ErrorsList[ErrorT]) -> K | None:
        if isinstance(maybe_errors, ErrorsList):
            self.extend(maybe_errors._errors)
            return None
        return maybe_errors

    def __getitem__(self, key: int) -> ErrorT:
        return self._errors[key]

    def __setitem__(self, key: int, value: ErrorT):
        self._errors[key] = value

    def __len__(self) -> int:
        return len(self._errors)

    def __iter__(self) -> Iterator[ErrorT]:
        return self._errors.__iter__()

    def __repr__(self) -> str:
        return repr(self._errors)

    def __str__(self) -> str:
        errs = "\n".join([f"\t{err}" for err in self._errors])
        return f"ExampleErrors with {len(self)} errors:\n{errs}"

    def __eq__(self, __value: object) -> bool:
        return isinstance(__value, ErrorsList) and self._errors == __value._errors


@dataclass
class MetadataError:
    file: Optional[Path] = None
    id: Optional[str] = None

    def prefix(self):
        prefix = f"In {self.file or 'several'} at {self.id},"
        return prefix

    def message(self) -> str:
        return ""

    def __str__(self):
        return f"{self.prefix()} {self.message()}"


class MetadataErrors(ErrorsList[MetadataError]):
    pass


@dataclass
class MetadataParseError(MetadataError):
    id: Optional[str] = None
    language: Optional[str] = None
    sdk_version: Optional[int] = None

    def prefix(self):
        prefix = super().prefix() + f" example {self.id}"
        if self.language:
            prefix += f" {self.language}"
        if self.sdk_version:
            prefix += f":{self.sdk_version}"
        return prefix

    def __str__(self):
        return f"{self.prefix()} {self.message()}"


K = TypeVar("K")


class DuplicateItemException(Exception):
    def __init__(self, item: MetadataError):
        super().__init__(self, f"Already have item {item!r} in ExampleErrors")


@dataclass
class MissingServiceBody(MetadataParseError):
    def message(self):
        return "service definition missing body"


@dataclass
class NamePrefixMismatch(MetadataParseError):
    def message(self):
        return "name prefix does not match any known service."


@dataclass
class NameFormat(MetadataParseError):
    def message(self):
        return "name does not match the required format of 'svc_Operation', 'svc_Operation_Specialization', or 'cross_Title'"


@dataclass
class ServiceNameFormat(NameFormat):
    svc: str = ""
    svcs: list[str] = field(default_factory=list)

    def message(self):
        return (
            NameFormat.message(self)
            + f" (service {self.svc}, services {', '.join(self.svcs)})"
        )


@dataclass
class ActionNameFormat(MetadataParseError):
    def message(self):
        return "name of API example does not match the required format of 'svc_Action'"


@dataclass
class MissingCrossContent(MetadataParseError):
    block: str = ""

    def message(self):
        return f"missing cross content block {self.block}"


@dataclass
class FieldError(MetadataParseError):
    field: str = ""
    value: str = ""


@dataclass
class MissingField(FieldError):
    def message(self):
        return f"missing field {self.field}"


@dataclass
class InvalidFieldType(FieldError):
    reason: str = "unknown"

    def message(self):
        return f"invalid field type {self.field} ({self.reason})"


@dataclass
class AwsNotEntity(FieldError):
    check_err: str = ""

    def message(self):
        return f"field {self.field} is '{self.value}', which has a validation issue: {self.check_err}."


@dataclass
class MappingMustBeEntity(FieldError):
    def message(self):
        return f"Mapping field {self.field} with value {self.value} must be an entity."


@dataclass
class LanguagesEmptyError(MetadataParseError):
    def message(self):
        return "does not contain any languages."


@dataclass
class LanguageError(MetadataParseError):
    def message(self) -> str:
        return "LanguageError?"


@dataclass
class UnknownLanguage(LanguageError):
    def message(self):
        return (
            f"contains {self.language} as a language, which is not listed in sdks.yaml."
        )


@dataclass
class MissingBlockContent(LanguageError):
    block_content: str = ""

    def message(self):
        return f"block_content {self.block_content} was not found in the cross-contents folder."


@dataclass
class BlockContentAndExcerptConflict(LanguageError):
    def message(self):
        return "contains both block_content and excerpt data. You cannot use both."


@dataclass
class MissingBlockContentAndExcerpt(LanguageError):
    def message(self):
        return "must contain either block_content or excerpt data."


@dataclass
class SdkVersionError(LanguageError):
    def message(self) -> str:
        return "SdkVersionError"


@dataclass
class InvalidSdkVersion(SdkVersionError):
    def message(self):
        return "lists version which is not listed in sdks.yaml."


@dataclass
class InvalidGithubLink(SdkVersionError):
    link: str = ""

    def message(self):
        return f"has link {self.link}, which looks like a file. Links to Github should be to the folder that contains the README that describes the example."


@dataclass
class MissingGithubLink(SdkVersionError):
    link: str = ""
    root: Optional[Path] = None

    def message(self):
        return f"has link {self.link}, which is not a folder in this project (root: {self.root})."


@dataclass
class InvalidSdkGuideStart(SdkVersionError):
    guide: str = ""

    def message(self):
        return f"contains an sdkguide link of '{self.guide}'. Use a relative link instead and let the tool insert a 'type=documentation' attribute in the link on your behalf."


@dataclass
class AddServicesHasBeenDeprecated(SdkVersionError):
    add_services: Dict[str, Set[str]] = field(default_factory=dict)

    def message(self):
        return "lists additional services in add_services, which has been deprecated."


@dataclass
class APIMustHaveOneServiceOneAction(MetadataParseError):
    svc_actions: str = ""

    def message(self):
        return (
            f"is an API example but lists svc:actions as '{self.svc_actions}'. "
            f"API examples must contain exactly one service and one action."
        )


@dataclass
class APICannotHaveTitleFields(MetadataParseError):
    def message(self):
        return (
            "is an API example and defines title, title_abbrev, or synopsis. "
            "API examples cannot define these fields because they are generated."
        )


@dataclass
class NonAPIMustHaveTitleFields(MetadataParseError):
    def message(self):
        return (
            "is not an API example and does not define title, title_abbrev, or synopsis. "
            "Non-API examples must define these fields."
        )


@dataclass
class BasicsMustHaveSynopsisField(MetadataParseError):
    def message(self):
        return (
            "is a Basics example and does not define synopsis or synopsis_list. "
            "Basics examples must define one of these fields."
        )


@dataclass
class MissingSnippetTag(SdkVersionError):
    tag: str = ""

    def message(self):
        return f" excerpt {self.tag} not found in the snippets folder."


@dataclass
class UnknownService(MetadataParseError):
    service: str = ""

    def message(self):
        return f"has unknown service {self.service}"


@dataclass
class DuplicateService(MetadataParseError):
    services: str = ""

    def message(self):
        return f"service {self.services} listed more than once"


@dataclass
class DuplicateExample(MetadataParseError):
    other_file: str = ""

    def message(self):
        return f"also found in {self.other_file}"


@dataclass
class DuplicateAPIExample(MetadataError):
    svc_action: str = ""
    duplicates: List[str] = field(default_factory=list)

    def message(self):
        return f"multiple API examples found for service:action {self.svc_action} also in {', '.join(self.duplicates)}"


@dataclass
class DuplicateTitleAbbrev(MetadataError):
    title_abbrev: str = ""
    language: str = ""

    def message(self):
        return f"multiple examples found with conflicting title_abbrev: {self.title_abbrev} in {self.language}"


@dataclass
class URLMissingTitle(SdkVersionError):
    url: str = ""

    def message(self):
        return f"URL {self.url} is missing a title"


@dataclass
class PersonMissingField(SdkVersionError):
    name: str = ""
    alias: str = ""

    def message(self):
        return f"person is missing a field: name: {self.name}, alias: {self.alias}"


@dataclass
class MissingCategoryBody(MetadataParseError):
    def message(self):
        return "category definition missing body"


@dataclass
class ExampleMergeMismatchedId(MetadataError):
    other_id: str = ""
    other_file: Optional[Path] = None

    def message(self) -> str:
        return f"mismatched other ID: {self.other_id} in {self.other_file}"


@dataclass
class ExampleMergeMismatchedLanguage(LanguageError):
    other_lang: str = ""

    def message(self) -> str:
        return f"mismatched other language: {self.other_lang}"


@dataclass
class ExampleMergeConflict(LanguageError):
    other_file: Optional[Path] = None

    def message(self) -> str:
        return f"conflict from {self.other_file}: example already exists for this language and SDK version"


def check_mapping(
    mapping: str | None, field: str, strict: bool = True
) -> str | MetadataParseError:
    if not mapping:
        return MissingField(field=field)
    if strict and not re.match("&[-_a-zA-Z0-9]+;", mapping):
        return MappingMustBeEntity(field=field, value=mapping)

    return mapping
