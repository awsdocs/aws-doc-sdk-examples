# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import argparse
import config
import logging
import os
import json

from pathlib import Path
from typing import Optional, Dict
from copy import deepcopy

from render import Renderer, MissingMetadataError, RenderStatus
from scanner import Scanner

from aws_doc_sdk_examples_tools.entities import expand_all_entities
from aws_doc_sdk_examples_tools.doc_gen import DocGen, DocGenEncoder

logging.basicConfig(level=os.environ.get("LOGLEVEL", "INFO").upper(), force=True)


def prepare_scanner(doc_gen: DocGen) -> Optional[Scanner]:
    for path in (doc_gen.root / ".doc_gen/metadata").glob("*_metadata.yaml"):
        doc_gen.process_metadata(path)
    doc_gen.collect_snippets()
    doc_gen.validate()
    if doc_gen.errors:
        error_strings = [str(error) for error in doc_gen.errors]
        failed_list = "\n".join(f"DocGen Error: {e}" for e in error_strings)
        print(f"Metadata errors encountered:\n\t{failed_list}")
        return None

    scanner = Scanner(doc_gen)

    # Preload cross-content examples
    scanner.load_crosses()

    return scanner


def main():
    # Load all examples immediately for cross references. Trades correctness for speed.
    doc_gen = DocGen.from_root(Path(__file__).parent.parent.parent, incremental=True)
    # To get the complete list, fill the missing fields.
    doc_gen.fill_missing_fields()

    languages = ['Python:3']  # Currently enabled only for Python version 3.
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--languages",
        choices=[*languages] + ["all"],
        nargs="+",
        help="The languages of the SDK. Choose from: %(choices)s.",
        default=["all"],
    )

    parser.add_argument(
        "--services",
        choices=[*doc_gen.services.keys()] + ["all"],
        nargs="+",
        help="The targeted service. Choose from: %(choices)s.",
        default=["all"],
    )

    parser.add_argument(
        "--verbose",
        action="store_true",
        help="When set, output verbose debugging info.",
    )
    parser.add_argument(
        "--dry-run",
        action="store_true",
        dest="dry_run",
        help="In dry run, compare current vs generated and exit with failure if they do not match.",
        default=False,
    )
    parser.add_argument("--no-dry-run", dest="dry_run", action="store_false")
    parser.add_argument("--check", dest="dry_run", action="store_true")
    parser.add_argument("--diff", action="store_true", default=False)
    args = parser.parse_args()

    if "all" in args.languages:
        args.languages = [*languages]

    if "all" in args.services:
        args.services = [*doc_gen.services.keys()]

    if args.verbose:
        logging.basicConfig(level=logging.DEBUG)

    logging.debug(f"Args configuration: {args}")

    if args.dry_run:
        print("Dry run, no changes will be made.")

    skipped = []
    failed = []
    written = []
    unchanged = []

    scanner = prepare_scanner(doc_gen)
    if scanner is None:
        return -1

    renderer = Renderer(scanner)

    for service in args.services:
        for language_and_version in args.languages:
            (language, version) = language_and_version.split(":")
            id = f"{language}:{version}:{service}"
            try:
                renderer.set_example(service, language, int(version), False)
                service_folder_path = renderer.lang_config["service_folder"]
                logging.debug("Cataloging %s", id)
                catalog_status = write_catalog_json(doc_gen, service, language, service_folder_path, args.dry_run)
                logging.debug("Status %s", catalog_status)

                if catalog_status == RenderStatus.UPDATED:
                    if args.dry_run:
                        diff = None
                        failed.append((id, diff))
                    else:
                        written.append(id)
                elif catalog_status == RenderStatus.UNCHANGED:
                    unchanged.append(id)
                    pass
            except FileNotFoundError as fnfe:
                logging.debug(fnfe, exc_info=True)
                skipped.append(id)
            except MissingMetadataError as mme:
                logging.debug(mme, exc_info=True)
                failed.append(id)
            except Exception as e:
                logging.error(e, exc_info=True)
                failed.append(id)

    skip_list = "\n".join(f"Skipped {f}" for f in sorted(skipped))
    logging.debug(skip_list or "(None Skipped)")
    if unchanged:
        unchanged_list = "\n".join(f"Unchanged {f}" for f in sorted(unchanged))
        print(unchanged_list)
    if not args.dry_run:
        done_list = "\n".join(f"Wrote {f}" for f in sorted(written))
        print(done_list or "(None Written)")
    if failed:
        if args.diff:
            failed_list = "\n".join(
                f"Diff: {f[1]}" for f in sorted(failed, key=lambda f: f[0])
            )
        else:
            failed_list = "\n".join(f"Incorrect: {f[0]}" for f in sorted(failed))
        print(failed_list)
        print("Rerun catalog.py to update the example catalog list.")
    print("Catalog Run completed.")
    return len(failed)


def write_catalog_json(doc_gen, service_name, language_name, folder_path, is_dry_run):
    filepath = (
            Path(__file__).parent.parent.parent
            / folder_path
            / 'examples_catalog.json'
    )

    language_examples = []
    for example in doc_gen.examples.values():
        for lang_name, language in example.languages.items():
            if lang_name == language_name and service_name in example.services:
                example.title = sanitize_example_title(example, service_name)
                # Add to the catalog.
                language_examples.append(deepcopy(example))

    for example in language_examples:
        # Remove the lists that aren't needed.
        example.languages = []
        example.doc_filenames.sdk_pages = []
        example.services = []

    new_catalog = json.dumps(
        {"examples": language_examples},
        cls=DocGenEncoder, indent="\t"
    )

    # Expand all of the entity text.
    [text, errors] = expand_all_entities(new_catalog, doc_gen.entities)
    if errors:
        print(errors)
        return RenderStatus.UNCHANGED
    new_catalog = text

    # If the file already exists, read it to compare contents.
    try:
        with open(filepath, "r", encoding="utf-8") as example_meta:
            old_catalog = example_meta.read()
    except FileNotFoundError:
        old_catalog = ""

    if old_catalog == new_catalog:
        return RenderStatus.UNCHANGED
    else:
        if not is_dry_run:
            print(f"Writing serialized versions of DocGen to {filepath}")
            with open(filepath, "w", encoding="utf-8") as example_meta:
                example_meta.write(new_catalog)
        return RenderStatus.UPDATED


def sanitize_example_title(example, service) -> [str, None]:
    """Clean up the text in an example."""
    # API examples use the API name.
    if example.category == 'Api':
        return sorted(example.services[service])[0]
    # Basics use a standard title.
    if example.category == 'Basics':
        return 'Learn the basics'
    # Otherwise use the title with the code tags removed.
    s = example.title
    return s.replace("<code>", "")\
        .replace("</code>", "")\

