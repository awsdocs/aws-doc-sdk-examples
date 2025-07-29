# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import typer
from typing_extensions import Annotated
import logging
import os

from difflib import unified_diff
from enum import Enum
from pathlib import Path
from typing import Optional, Generator, Callable

from render import Renderer, RenderStatus, MissingMetadataError
from scanner import Scanner

from aws_doc_sdk_examples_tools.doc_gen import DocGen
from aws_doc_sdk_examples_tools.metadata_errors import MetadataError
from collections import defaultdict
import re

# Folders to exclude from processing
EXCLUDED_FOLDERS = {'.kiro', '.git', 'node_modules', '__pycache__'}


def _configure_folder_exclusion():
    """Configure file processing to exclude specified folders."""
    from aws_doc_sdk_examples_tools import file_utils, validator_config
    from aws_doc_sdk_examples_tools.fs import Fs, PathFs

    def enhanced_skip(path: Path) -> bool:
        """Skip function that ignores excluded folders and standard ignored files."""
        # Check if path contains any excluded folders
        if any(excluded_folder in path.parts for excluded_folder in EXCLUDED_FOLDERS):
            return True
        
        # Apply standard skip logic
        return path.suffix.lower() not in validator_config.EXT_LOOKUP or path.name in validator_config.IGNORE_FILES

    def enhanced_get_files(
        root: Path, skip: Callable[[Path], bool] = lambda _: False, fs: Fs = PathFs()
    ) -> Generator[Path, None, None]:
        """Get files using enhanced skip function."""
        for path in file_utils.walk_with_gitignore(root, fs=fs):
            if not enhanced_skip(path):
                yield path

    # Configure the file processing functions
    validator_config.skip = enhanced_skip
    file_utils.get_files = enhanced_get_files
    
    excluded_list = ', '.join(sorted(EXCLUDED_FOLDERS))
    print(f"Folder exclusion configured: {excluded_list} folders excluded")


# Configure folder exclusion when module is imported
_configure_folder_exclusion()


# Default to not using Rich
if "USE_RICH" not in os.environ:
    import typer.core

    typer.core.rich = None  # type: ignore

logging.basicConfig(level=os.environ.get("LOGLEVEL", "INFO").upper(), force=True)


class UnmatchedSnippetTagError(MetadataError):
    def __init__(self, file, id, tag=None, line=None, tag_type=None):
        super().__init__(file=file, id=id)
        self.tag = tag
        self.line = line
        self.tag_type = tag_type  # 'start' or 'end'
    
    def message(self):
        return f"Unmatched snippet-{self.tag_type} tag '{self.tag}' at line {self.line}"


class DuplicateSnippetTagError(MetadataError):
    def __init__(self, file, id, tag=None, line=None):
        super().__init__(file=file, id=id)
        self.tag = tag
        self.line = line
    
    def message(self):
        return f"Duplicate snippet tag '{self.tag}' found at line {self.line}"


def validate_snippet_tags(doc_gen: DocGen):
    """Validate snippet-start/snippet-end pairs across all files."""
    errors = []
    
    # We need to scan files directly since DocGen.snippets only contains valid pairs
    from aws_doc_sdk_examples_tools.file_utils import get_files
    from aws_doc_sdk_examples_tools.validator_config import skip
    
    for file_path in get_files(doc_gen.root, skip, fs=doc_gen.fs):
        try:
            content = doc_gen.fs.read(file_path)
            lines = content.splitlines()
            
            snippet_starts = {}  # Track all snippet-start tags and their line numbers
            snippet_ends = {}    # Track all snippet-end tags and their line numbers
            snippet_tags_seen = set()  # Track all tags in this file to detect duplicates
            
            for line_num, line in enumerate(lines, 1):
                # Look for snippet-start patterns (# or // comment styles)
                start_match = re.search(r'(#|//)\s*snippet-start:\[([^\]]+)\]', line)
                if start_match:
                    tag = start_match.group(2)
                    
                    # Check for duplicate start tags in the same file
                    if tag in snippet_starts:
                        errors.append(DuplicateSnippetTagError(
                            file=file_path,
                            id=f"Duplicate snippet-start tag in {file_path}",
                            tag=tag,
                            line=line_num
                        ))
                    else:
                        snippet_starts[tag] = line_num
                        snippet_tags_seen.add(tag)
                
                # Look for snippet-end patterns
                end_match = re.search(r'(#|//)\s*snippet-end:\[([^\]]+)\]', line)
                if end_match:
                    tag = end_match.group(2)
                    
                    # Check for duplicate end tags in the same file
                    if tag in snippet_ends:
                        errors.append(DuplicateSnippetTagError(
                            file=file_path,
                            id=f"Duplicate snippet-end tag in {file_path}",
                            tag=tag,
                            line=line_num
                        ))
                    else:
                        snippet_ends[tag] = line_num
            
            # Check that every snippet-start has a corresponding snippet-end
            for tag, start_line in snippet_starts.items():
                if tag not in snippet_ends:
                    errors.append(UnmatchedSnippetTagError(
                        file=file_path,
                        id=f"Unclosed snippet-start in {file_path}",
                        tag=tag,
                        line=start_line,
                        tag_type='start'
                    ))
            
            # Check that every snippet-end has a corresponding snippet-start
            for tag, end_line in snippet_ends.items():
                if tag not in snippet_starts:
                    errors.append(UnmatchedSnippetTagError(
                        file=file_path,
                        id=f"Unmatched snippet-end in {file_path}",
                        tag=tag,
                        line=end_line,
                        tag_type='end'
                    ))
                
        except Exception as e:
            # Skip files that can't be read (binary files, etc.)
            continue
    
    return errors


def prepare_scanner(doc_gen: DocGen) -> Optional[Scanner]:
    for path in (doc_gen.root / ".doc_gen/metadata").glob("*_metadata.yaml"):
        doc_gen.process_metadata(path)
    doc_gen.collect_snippets()
    doc_gen.validate()
    
    # Validate snippet tag pairs
    snippet_errors = validate_snippet_tags(doc_gen)
    if snippet_errors:
        doc_gen.errors.extend(snippet_errors)
    
    if doc_gen.errors:
        error_strings = [str(error) for error in doc_gen.errors]
        failed_list = "\n".join(f"DocGen Error: {e}" for e in error_strings)
        print(f"Metadata errors encountered:\n\t{failed_list}")
        return None
    scanner = Scanner(doc_gen)

    # Preload cross-content examples
    scanner.load_crosses()

    return scanner


# Load all examples immediately for cross references. Trades correctness for speed.
doc_gen = DocGen.from_root(Path(__file__).parent.parent.parent, incremental=True)


Language = Enum(
    "Language", {lang: lang for lang in ([*doc_gen.languages()] + ["all"])}
)  # type: ignore
Service = Enum(
    "Service", {serv: serv for serv in ([*doc_gen.services.keys()] + ["all"])}
)  # type: ignore


def writeme(
    languages: Annotated[
        list[Language],  # type: ignore
        typer.Option(
            help="The languages of the SDK.",
        ),
    ] = [
        Language.all.value  # type: ignore
    ],  # type: ignore
    services: Annotated[
        list[Service],  # type: ignore
        typer.Option(
            help="The targeted service.",
        ),
    ] = [
        Service.all.value  # type: ignore
    ],  # type: ignore
    safe: Annotated[
        bool,
        typer.Option(
            help="Save a copy of the original README as the 'saved_readme' value specified in config.py ({config.saved_readme})."
        ),
    ] = False,
    verbose: Annotated[
        bool, typer.Option(help="When set, output verbose debugging info.")
    ] = False,
    dry_run: Annotated[
        bool,
        typer.Option(
            help="In dry run, compare current vs generated and exit with failure if they do not match."
        ),
    ] = False,
    check: Annotated[bool, typer.Option(help="Alias for --dry-run.")] = False,
    diff: Annotated[
        bool, typer.Option(help="Show a diff of READMEs that have changed.")
    ] = False,
):
    if Language.all in languages:  # type: ignore
        languages = list(Language)  # type: ignore
        languages.remove(Language.all)  # type: ignore

    if Service.all in services:  # type: ignore
        services = list(Service)  # type: ignore
        services.remove(Service.all)  # type: ignore

    if verbose:
        logging.basicConfig(level=logging.DEBUG)

    if check:
        dry_run = check

    if dry_run:
        print("Dry run, no changes will be made.")

    skipped = []
    failed = []
    written = []
    non_writeme = []
    unchanged = []
    no_folder = []

    scanner = prepare_scanner(doc_gen)
    if scanner is None:
        return -1

    renderer = Renderer(scanner)
    for service in services:
        if service == Service.all:  # type: ignore
            continue
        for language_and_version in languages:
            if language_and_version == Language.all:  # type: ignore
                continue
            (language, version) = language_and_version.value.split(":")
            id = f"{language}:{version}:{service}"
            try:
                renderer.set_example(service.value, language, int(version), safe)

                logging.debug("Rendering %s", id)
                render_status = renderer.render()
                logging.debug("Status %s", render_status)

                if render_status == RenderStatus.UPDATED:
                    if dry_run:
                        diff_text = None
                        if diff:
                            diff_text = make_diff(renderer, id)
                        failed.append((id, diff_text))
                    else:
                        renderer.write()
                        written.append(id)
                elif render_status == RenderStatus.UNCHANGED:
                    unchanged.append(id)
                elif render_status == RenderStatus.UNMANAGED:
                    non_writeme.append(id)
                elif render_status == RenderStatus.NO_EXAMPLES:
                    skipped.append(id)
                elif render_status == RenderStatus.NO_FOLDER:
                    no_folder.append(id)
                elif render_status == RenderStatus.UNIMPLEMENTED:
                    pass
            except FileNotFoundError as fnfe:
                logging.debug(fnfe, exc_info=True)
                skipped.append(id)
            except MissingMetadataError as mme:
                logging.debug(mme, exc_info=True)
                failed.append((id, None))
            except Exception as e:
                logging.error(e, exc_info=True)
                failed.append((id, None))

    skip_list = "\n".join(f"Skipped {f}" for f in sorted(skipped))
    logging.debug(skip_list or "(None Skipped)")
    if unchanged:
        unchanged_list = "\n".join(f"Unchanged {f}" for f in sorted(unchanged))
        print(unchanged_list)
    if non_writeme:
        non_writeme_list = "\n".join(f"Non-WRITEME: {f}" for f in sorted(non_writeme))
        print(non_writeme_list)
    if no_folder:
        no_folder_list = "\n".join(f"No folder: {f}" for f in sorted(no_folder))
        print(no_folder_list)
    if not dry_run:
        done_list = "\n".join(f"Wrote {f}" for f in sorted(written))
        print(done_list or "(None Written)")
    if failed:
        if diff:
            failed_list = "\n".join(
                f"Diff: {f[1]}" for f in sorted(failed, key=lambda f: f[0])
            )
        else:
            failed_list = "\n".join(f"Incorrect: {f[0]}" for f in sorted(failed))
        print(failed_list)
        print("Rerun writeme.py to update README links and sections.")
    print("WRITEME Run completed.")
    fail_count = len(failed)
    if fail_count > 0:
        raise typer.Exit(code=fail_count) # Return a non-zero code through typer so Github check will fail. 
    return fail_count


def make_diff(renderer, id):
    current = renderer.read_current().split("\n")
    expected = renderer.readme_text.split("\n")
    diff = unified_diff(current, expected, f"{id}/current", f"{id}/expected")
    return "\n".join(diff)