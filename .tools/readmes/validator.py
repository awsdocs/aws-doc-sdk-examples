# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Validator module for WRITEME to check for issues in the codebase.
"""

import logging
from collections import defaultdict
from typing import Dict, List, Set, Tuple

from aws_doc_sdk_examples_tools.doc_gen import DocGen

logger = logging.getLogger(__name__)


class ValidationError(Exception):
    """Exception raised for validation errors."""
    pass


def check_duplicate_snippet_tags(doc_gen: DocGen) -> List[Tuple[str, List[str]]]:
    """
    Check for duplicate snippet tags in the codebase.
    
    Args:
        doc_gen: The DocGen instance containing snippets
        
    Returns:
        List of tuples containing (tag, [file_paths]) for duplicate tags
    """
    # Dictionary to store tag -> list of files
    tag_files: Dict[str, List[str]] = defaultdict(list)
    duplicates = []
    
    # Collect all tags and their file locations
    for tag, snippet in doc_gen.snippets.items():
        tag_files[tag].append(snippet.file)
    
    # Find tags that appear in multiple files
    for tag, files in tag_files.items():
        if len(files) > 1:
            duplicates.append((tag, files))
    
    return duplicates


def check_missing_snippet_tags(doc_gen: DocGen) -> List[Tuple[str, str]]:
    """
    Check for snippet tags referenced in metadata but not found in code.
    
    Args:
        doc_gen: The DocGen instance containing snippets and examples
        
    Returns:
        List of tuples containing (tag, example_id) for missing tags
    """
    missing = []
    
    # Get all available tags
    available_tags = set(doc_gen.snippets.keys())
    
    # Check all examples for referenced tags that don't exist
    for example_id, example in doc_gen.examples.items():
        for lang_name, language in example.languages.items():
            for version in language.versions:
                if version.excerpts:
                    for excerpt in version.excerpts:
                        if excerpt.snippet_tags:
                            for tag in excerpt.snippet_tags:
                                if tag not in available_tags:
                                    missing.append((tag, example_id))
    
    return missing


def validate_snippets(doc_gen: DocGen, strict: bool = False) -> bool:
    """
    Validate snippets in the codebase.
    
    Args:
        doc_gen: The DocGen instance containing snippets
        strict: If True, raise an exception for validation errors
        
    Returns:
        True if validation passed, False otherwise
    """
    validation_passed = True
    
    # Check for duplicate snippet tags
    duplicates = check_duplicate_snippet_tags(doc_gen)
    if duplicates:
        validation_passed = False
        logger.error("Found %d duplicate snippet tags:", len(duplicates))
        for tag, files in duplicates:
            file_list = ", ".join(files)
            logger.error("  Tag '%s' found in multiple files: %s", tag, file_list)
    
    # Check for missing snippet tags
    missing = check_missing_snippet_tags(doc_gen)
    if missing:
        validation_passed = False
        logger.error("Found %d missing snippet tags:", len(missing))
        for tag, example_id in missing:
            logger.error("  Tag '%s' referenced in example '%s' but not found in code", tag, example_id)
    
    if not validation_passed and strict:
        raise ValidationError("Snippet validation failed")
    
    return validation_passed