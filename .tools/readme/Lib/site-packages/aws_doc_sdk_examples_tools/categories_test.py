# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from pathlib import Path
from typing import Dict, List, Tuple
import pytest
import yaml

from aws_doc_sdk_examples_tools import metadata_errors
from .categories import (
    parse,
    Category,
    TitleInfo,
    Prefix,
)


def load(
    path: str,
) -> Tuple[List[str], Dict[str, Category], metadata_errors.MetadataErrors]:
    root = Path(__file__).parent
    filename = root / "test_resources" / path
    with open(filename) as file:
        meta = yaml.safe_load(file)
    return parse(filename, meta)


def test_empty_categories():
    _, _, errs = load("empty_categories.yaml")
    assert [*errs] == [
        metadata_errors.MissingCategoryBody(
            file=Path(__file__).parent / "test_resources/empty_categories.yaml",
            id="EmptyCat",
        )
    ]


def test_categories():
    _, categories, _ = load("categories.yaml")
    assert categories == {
        "Actions": Category(
            key="Actions",
            display="Actions test",
            overrides=TitleInfo(
                title="Title override",
                title_suffixes={
                    "cli": " with a CLI",
                    "sdk": " with an &AWS; SDK",
                    "sdk_cli": " with an &AWS; SDK or CLI",
                },
                title_abbrev="Title abbrev override",
                synopsis="synopsis test.",
            ),
            description="test description.",
        ),
        "Basics": Category(
            key="Basics",
            display="Basics",
            defaults=TitleInfo(
                title="Title default",
                title_abbrev="Title abbrev default",
            ),
            description="default description.",
        ),
        "TributaryLite": Category(
            key="TributaryLite",
            display="Tea light",
            description="light your way.",
            synopsis_prefix=Prefix(one="Tea light is", many="Tea lights are"),
            more_info="This is more tea light info.",
        ),
    }


if __name__ == "__main__":
    pytest.main([__file__, "-vv"])
