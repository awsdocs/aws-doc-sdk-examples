from collections import defaultdict
from dataclasses import asdict
from pathlib import Path
from typing import Any, DefaultDict, Dict, List
import yaml

from aws_doc_sdk_examples_tools.doc_gen import DocGen
from aws_doc_sdk_examples_tools.metadata import Example


def write_many(root: Path, to_write: Dict[str, str]):
    for path, examples in to_write.items():
        with open(root / path, "w") as file:
            file.write(examples)


def dump_yaml(value: Any) -> str:
    repr: str = yaml.dump(value, sort_keys=False, width=float("inf"))
    repr = repr.replace(r"!!set {}", r"{}")
    return repr


def prepare_write(examples: Dict[str, Example]) -> Dict[str, str]:
    reindexed: DefaultDict[Path, Dict[str, Any]] = defaultdict(dict)

    for id, example in examples.items():
        if example.file:
            reindexed[example.file][id] = example_dict(asdict(example))

    to_write = {str(path): dump_yaml(examples) for path, examples in reindexed.items()}

    return to_write


EXAMPLE_FIELD_ORDER = [
    # "id", # do not include ID, it goes in the key
    # "file", # similarly, do not include the file, it's the path to write to later
    "title",
    "title_abbrev",
    "synopsis",
    "synopsis_list",
    "guide_topic",
    # "doc_filenames", # These are currently generated, and don't need to be stored.
    "source_key",
    "category",
    "languages",
    "service_main",
    "services",
]

VERSION_FIELD_ORDER = [
    "sdk_version",
    "block_content",
    "github",
    "sdkguide",
    "more_info",
    "owner",
    "authors",
    "source",
    "excerpts",
]

EXCERPT_FIELD_ORDER = [
    "description",
    "genai",
    "snippet_tags",
    "snippet_files",
]


def reorder_dict(order: List[str], dict: Dict) -> Dict:
    replaced = {}

    for field in order:
        if value := dict[field]:
            replaced[field] = value

    return replaced


def example_dict(example: Dict) -> Dict:
    replaced = reorder_dict(EXAMPLE_FIELD_ORDER, example)

    replaced["languages"] = {
        k: dict(versions=[version_dict(version) for version in v["versions"]])
        for k, v in replaced["languages"].items()
    }

    return replaced


def version_dict(version: Dict) -> Dict:
    replaced = reorder_dict(VERSION_FIELD_ORDER, version)

    replaced["excerpts"] = [excerpt_dict(excerpt) for excerpt in replaced["excerpts"]]

    return replaced


def excerpt_dict(excerpt: Dict) -> Dict:
    reordered = reorder_dict(EXCERPT_FIELD_ORDER, excerpt)
    if reordered.get("genai") == "none":
        del reordered["genai"]
    return reordered


# For testing
if __name__ == "__main__":
    doc_gen = DocGen.from_root(
        Path(__file__).parent / "test_resources" / "doc_gen_test"
    )
    writes = prepare_write(doc_gen.examples)
    write_many(Path("/"), writes)
    # print(writes)
