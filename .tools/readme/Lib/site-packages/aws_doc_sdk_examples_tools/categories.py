# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from __future__ import annotations

import re

from pathlib import Path
from typing import Any, Callable, Dict, List, Optional
from dataclasses import dataclass, field

from aws_doc_sdk_examples_tools import metadata_errors
from .metadata_errors import (
    MetadataErrors,
)


def fake_gotmpl(tmpl: Optional[str], service: str, action: str):
    if not tmpl:
        return
    values = {
        ".ServiceEntity.Short": service,
        ".Action": action,
    }
    return re.sub(
        r"{{(?P<name>[.\w]+)}}",
        lambda x: values[
            x.groupdict()["name"]
        ],  # This will be a KeyError if the replacement isn't in the values dict
        tmpl,
    )


@dataclass
class TitleInfo:
    title: Optional[str] = field(default=None)
    title_abbrev: Optional[str] = field(default=None)
    synopsis: Optional[str] = field(default=None)
    title_suffixes: str | Dict[str, str] = field(default_factory=dict)

    @classmethod
    def from_yaml(cls, yaml: Dict[str, str] | None) -> Optional[TitleInfo]:
        if yaml is None:
            return None

        title = yaml.get("title")
        title_suffixes: str | Dict[str, str] = yaml.get("title_suffixes", {})
        title_abbrev = yaml.get("title_abbrev")
        synopsis = yaml.get("synopsis")

        return cls(
            title=title,
            title_suffixes=title_suffixes,
            title_abbrev=title_abbrev,
            synopsis=synopsis,
        )


@dataclass
class Prefix:
    one: Optional[str]
    many: Optional[str]

    @classmethod
    def from_yaml(cls, yaml: Optional[Dict[str, str]]) -> Optional[Prefix]:
        if yaml is None:
            return None

        one = yaml.get("one")
        many = yaml.get("many")

        return cls(
            one=one,
            many=many,
        )


@dataclass
class CategoryWithNoDisplayError(metadata_errors.MetadataError):
    def message(self):
        return "Category has no display value"


empty_title_info = TitleInfo()


@dataclass
class Category:
    key: str
    display: str
    defaults: Optional[TitleInfo] = field(default=None)
    overrides: Optional[TitleInfo] = field(default=None)
    description: Optional[str] = field(default=None)
    synopsis_prefix: Optional[Prefix] = field(default=None)
    more_info: Optional[str] = field(default=None)

    def evaluate(
        self,
        value: Optional[str],
        field: Callable[[TitleInfo], Optional[str]],
        service: str,
        action: str,
    ):
        overrides = field(self.overrides or empty_title_info)
        if overrides:
            return fake_gotmpl(overrides, service, action)
        if value:
            return value
        defaults = field(self.defaults or empty_title_info)
        if defaults:
            return fake_gotmpl(defaults, service, action)
        return f"{service} {action}"

    def validate(self, errors: MetadataErrors):
        if not self.display:
            errors.append(CategoryWithNoDisplayError(id=self.key))

    @classmethod
    def from_yaml(
        cls, key: str, yaml: Dict[str, Any]
    ) -> tuple[Category, MetadataErrors]:
        errors = MetadataErrors()
        display = str(yaml.get("display"))
        defaults = TitleInfo.from_yaml(yaml.get("defaults"))
        overrides = TitleInfo.from_yaml(yaml.get("overrides"))
        description = yaml.get("description")
        synopsis_prefix = Prefix.from_yaml(yaml.get("synopsis_prefix"))
        more_info = yaml.get("more_info")

        return (
            cls(
                key=key,
                display=display,
                defaults=defaults,
                overrides=overrides,
                description=description,
                synopsis_prefix=synopsis_prefix,
                more_info=more_info,
            ),
            errors,
        )


def parse(
    file: Path, yaml: Dict[str, Any]
) -> tuple[List[str], Dict[str, Category], MetadataErrors]:
    categories: Dict[str, Category] = {}
    errors = MetadataErrors()

    standard_cats = yaml.get("standard_categories", [])
    # Work around inconsistency where some tools use 'Actions' and DocGen uses 'Api' to refer to single-action examples.
    for i in range(len(standard_cats)):
        if standard_cats[i] == "Actions":
            standard_cats[i] = "Api"
    for key, yaml_cat in yaml.get("categories", {}).items():
        if yaml_cat is None:
            errors.append(metadata_errors.MissingCategoryBody(id=key, file=file))
        else:
            category, cat_errs = Category.from_yaml(key, yaml_cat)
            categories[key] = category
            for error in cat_errs:
                error.file = file
                error.id = key
            errors.extend(cat_errs)

    return standard_cats, categories, errors


if __name__ == "__main__":
    from pprint import pp
    import yaml

    path = Path(__file__).parent / "config" / "categories.yaml"
    with open(path) as file:
        meta = yaml.safe_load(file)
    standard_cats, cats, errs = parse(path, meta)
    pp(standard_cats)
    pp(cats)
