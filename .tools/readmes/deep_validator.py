# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Deep validator module for WRITEME to check for issues in the codebase.
This version performs a more thorough check for duplicate snippet tags by
directly scanning the files in the repository.
"""

import logging
import os
import re
import concurrent.futures
from collections import defaultdict
from pathlib import Path
from typing import Dict, List, Set, Tuple, Optional, Any

from aws_doc_sdk_examples_tools.doc_gen import DocGen

logger = logging.getLogger(__name__)


class ValidationError(Exception):
    """Exception raised for validation errors."""
    pass


def find_snippet_tags_in_file(file_path: Path) -> List[Tuple[str, int]]:
    """
    Find all snippet tags in a file by directly parsing the file content.
    
    Args:
        file_path: Path to the file to check
        
    Returns:
        List of tuples containing (tag, line_number)
    """
    if not file_path.exists():
        return []
    
    try:
        with open(file_path, 'r', encoding='utf-8', errors='replace') as f:
            lines = f.readlines()
    except Exception as e:
        logger.warning(f"Error reading file {file_path}: {e}")
        return []
    
    # Common snippet tag patterns
    patterns = [
        # Standard snippet tag format
        r'snippet-start:\s*\[([^\]]+)\]',
        r'snippet-end:\s*\[([^\]]+)\]',
        # Alternative formats
        r'SNIPPET\s+START\s+\[([^\]]+)\]',
        r'SNIPPET\s+END\s+\[([^\]]+)\]',
        r'//\s*SNIPPET:\s*([^\s]+)',
        r'#\s*SNIPPET:\s*([^\s]+)',
        r'<!--\s*SNIPPET:\s*([^\s]+)\s*-->',
        # Look for any other potential tag formats
        r'snippet[:\-_]([a-zA-Z0-9_\-]+)',
        # Common AWS SDK snippet formats
        r'//\s*snippet-start:\s*([^\s]+)',
        r'#\s*snippet-start:\s*([^\s]+)',
        r'<!--\s*snippet-start:\s*([^\s]+)\s*-->',
        r'//\s*snippet-end:\s*([^\s]+)',
        r'#\s*snippet-end:\s*([^\s]+)',
        r'<!--\s*snippet-end:\s*([^\s]+)\s*-->',
    ]
    
    results = []
    for i, line in enumerate(lines, 1):
        for pattern in patterns:
            matches = re.findall(pattern, line, re.IGNORECASE)
            for match in matches:
                results.append((match, i))
    
    return results


def scan_directory_for_snippet_tags(
    root_dir: Path, 
    extensions: Optional[List[str]] = None,
    max_workers: int = 10
) -> Dict[str, List[Tuple[str, int, str]]]:
    """
    Scan a directory recursively for files containing snippet tags.
    Uses parallel processing for faster scanning.
    
    Args:
        root_dir: Root directory to scan
        extensions: Optional list of file extensions to check
        max_workers: Maximum number of parallel workers
        
    Returns:
        Dictionary mapping snippet tags to lists of (file_path, line_number, context)
    """
    if extensions is None:
        # Default extensions to check
        extensions = [
            '.py', '.java', '.js', '.ts', '.cs', '.cpp', '.c', '.go', '.rb', 
            '.php', '.swift', '.kt', '.rs', '.abap', '.md', '.html', '.xml'
        ]
    
    # Find all files with the specified extensions
    files_to_scan = []
    for root, _, files in os.walk(root_dir):
        for file in files:
            if any(file.endswith(ext) for ext in extensions):
                files_to_scan.append(Path(root) / file)
    
    # Process files in parallel
    tag_to_locations = defaultdict(list)
    
    def process_file(file_path):
        try:
            relative_path = file_path.relative_to(root_dir)
            tags = find_snippet_tags_in_file(file_path)
            
            results = []
            for tag, line_number in tags:
                # Get some context from the file
                try:
                    with open(file_path, 'r', encoding='utf-8', errors='replace') as f:
                        lines = f.readlines()
                        start_line = max(0, line_number - 2)
                        end_line = min(len(lines), line_number + 1)
                        context = ''.join(lines[start_line:end_line]).strip()
                except Exception:
                    context = "<context unavailable>"
                
                results.append((str(relative_path), line_number, context))
            
            return {tag: [loc] for tag, line_number in tags for loc in [(str(relative_path), line_number, "")]}
        except Exception as e:
            logger.warning(f"Error processing file {file_path}: {e}")
            return {}
    
    # Use ThreadPoolExecutor for parallel processing
    with concurrent.futures.ThreadPoolExecutor(max_workers=max_workers) as executor:
        future_to_file = {executor.submit(process_file, file): file for file in files_to_scan}
        
        for future in concurrent.futures.as_completed(future_to_file):
            file_results = future.result()
            for tag, locations in file_results.items():
                tag_to_locations[tag].extend(locations)
    
    return tag_to_locations


def check_duplicate_snippet_tags_deep(doc_gen: DocGen) -> List[Tuple[str, List[Dict[str, Any]]]]:
    """
    Deep check for duplicate snippet tags in the codebase.
    This function scans all files directly to find snippet tags.
    
    Args:
        doc_gen: The DocGen instance containing snippets
        
    Returns:
        List of tuples containing (tag, [location_details]) for duplicate tags
    """
    logger.info("Starting deep scan for duplicate snippet tags...")
    
    # Scan the repository directly for snippet tags
    root_dir = doc_gen.root
    tag_locations = scan_directory_for_snippet_tags(root_dir)
    
    # Find tags that appear in multiple files
    duplicates = []
    for tag, locations in tag_locations.items():
        # Group locations by file path
        files = {}
        for file_path, line_number, context in locations:
            if file_path not in files:
                files[file_path] = []
            files[file_path].append({"line": line_number, "context": context})
        
        # If the tag appears in multiple files, it's a duplicate
        if len(files) > 1:
            duplicate_info = []
            for file_path, occurrences in files.items():
                duplicate_info.append({
                    "file": file_path,
                    "occurrences": occurrences
                })
            duplicates.append((tag, duplicate_info))
    
    logger.info(f"Deep scan complete. Found {len(duplicates)} duplicate tags.")
    return duplicates


def format_duplicate_report(duplicates: List[Tuple[str, List[Dict[str, Any]]]]) -> str:
    """
    Format a detailed report of duplicate snippet tags.
    
    Args:
        duplicates: List of duplicate tag information
        
    Returns:
        Formatted report as a string
    """
    if not duplicates:
        return "No duplicate snippet tags found."
    
    report = [f"Found {len(duplicates)} duplicate snippet tags:"]
    
    for tag, locations in duplicates:
        report.append(f"\nTag: '{tag}' found in {len(locations)} files:")
        
        for location in locations:
            file_path = location["file"]
            occurrences = location["occurrences"]
            
            report.append(f"  File: {file_path}")
            for occurrence in occurrences:
                line = occurrence.get("line", "unknown")
                context = occurrence.get("context", "").replace("\n", " ").strip()
                if context:
                    context = f" - Context: {context[:60]}..."
                report.append(f"    Line {line}{context}")
    
    return "\n".join(report)


def validate_snippets_deep(doc_gen: DocGen, strict: bool = False) -> bool:
    """
    Deep validation of snippets in the codebase.
    
    Args:
        doc_gen: The DocGen instance containing snippets
        strict: If True, raise an exception for validation errors
        
    Returns:
        True if validation passed, False otherwise
    """
    validation_passed = True
    
    # Check for duplicate snippet tags using the deep method
    duplicates = check_duplicate_snippet_tags_deep(doc_gen)
    if duplicates:
        validation_passed = False
        report = format_duplicate_report(duplicates)
        print("\n=== DUPLICATE SNIPPET TAGS (DEEP SCAN) ===")
        print(report)
        
        # Exit with error if strict validation is enabled
        if strict:
            raise ValidationError("Validation failed: duplicate snippet tags found")
    else:
        print("No duplicate snippet tags found in deep scan.")
    
    return validation_passed