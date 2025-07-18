# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Enhanced validator module for WRITEME to check for issues in the codebase.
This version performs comprehensive checks for snippet tag issues:
1. Duplicate snippet tags across files
2. Unpaired snippet-start and snippet-end tags within files
3. Multiple snippet-start or snippet-end tags with the same name within a file
"""

import logging
import os
import re
from collections import defaultdict
from pathlib import Path
from typing import Dict, List, Set, Tuple, Optional, Any

from aws_doc_sdk_examples_tools.doc_gen import DocGen

logger = logging.getLogger(__name__)


class ValidationError(Exception):
    """Exception raised for validation errors."""
    pass


class SnippetTagIssue:
    """Class to represent a snippet tag issue."""
    
    DUPLICATE_ACROSS_FILES = "duplicate_across_files"
    UNPAIRED_TAG = "unpaired_tag"
    DUPLICATE_IN_FILE = "duplicate_in_file"
    
    def __init__(self, issue_type: str, tag: str, locations: List[Dict[str, Any]]):
        self.issue_type = issue_type
        self.tag = tag
        self.locations = locations
    
    def __str__(self) -> str:
        if self.issue_type == self.DUPLICATE_ACROSS_FILES:
            files = [loc["file"] for loc in self.locations]
            return f"Tag '{self.tag}' found in multiple files: {', '.join(files)}"
        elif self.issue_type == self.UNPAIRED_TAG:
            details = []
            for loc in self.locations:
                file = loc["file"]
                tag_type = loc["tag_type"]
                line = loc["line"]
                details.append(f"{tag_type} at line {line} in {file}")
            return f"Unpaired tag '{self.tag}': {', '.join(details)}"
        elif self.issue_type == self.DUPLICATE_IN_FILE:
            file = self.locations[0]["file"]
            lines = [str(loc["line"]) for loc in self.locations]
            return f"Multiple instances of tag '{self.tag}' in {file} at lines: {', '.join(lines)}"
        else:
            return f"Unknown issue with tag '{self.tag}'"


def find_snippet_tags_in_file(file_path: Path) -> List[Dict[str, Any]]:
    """
    Find all snippet tags in a file by directly parsing the file content.
    
    Args:
        file_path: Path to the file to check
        
    Returns:
        List of dictionaries containing tag information
    """
    if not file_path.exists():
        return []
    
    try:
        with open(file_path, 'r', encoding='utf-8', errors='replace') as f:
            content = f.read()
            lines = content.splitlines()
    except Exception as e:
        logger.warning(f"Error reading file {file_path}: {e}")
        return []
    
    # Patterns for snippet start and end tags
    start_patterns = [
        r'snippet-start:\s*\[([^\]]+)\]',
        r'SNIPPET\s+START\s+\[([^\]]+)\]',
        r'//\s*snippet-start:\s*\[([^\]]+)\]',
        r'#\s*snippet-start:\s*\[([^\]]+)\]',
        r'<!--\s*snippet-start:\s*\[([^\]]+)\]\s*-->',
    ]
    
    end_patterns = [
        r'snippet-end:\s*\[([^\]]+)\]',
        r'SNIPPET\s+END\s+\[([^\]]+)\]',
        r'//\s*snippet-end:\s*\[([^\]]+)\]',
        r'#\s*snippet-end:\s*\[([^\]]+)\]',
        r'<!--\s*snippet-end:\s*\[([^\]]+)\]\s*-->',
    ]
    
    results = []
    
    # Process each line individually to avoid duplicate matches
    for i, line in enumerate(lines, 1):
        # Check for start tags
        for pattern in start_patterns:
            matches = re.findall(pattern, line, re.IGNORECASE)
            # Only take the first match per line for each pattern to avoid duplicates
            if matches:
                results.append({
                    "tag": matches[0],
                    "tag_type": "snippet-start",
                    "line": i,
                    "content": line.strip()
                })
                break  # Only process the first matching pattern
        
        # Check for end tags
        for pattern in end_patterns:
            matches = re.findall(pattern, line, re.IGNORECASE)
            # Only take the first match per line for each pattern to avoid duplicates
            if matches:
                results.append({
                    "tag": matches[0],
                    "tag_type": "snippet-end",
                    "line": i,
                    "content": line.strip()
                })
                break  # Only process the first matching pattern
    
    return results


def scan_directory_for_snippet_tags(
    root_dir: Path, 
    extensions: Optional[List[str]] = None,
    exclude_dirs: Optional[List[str]] = None
) -> Dict[str, List[Dict[str, Any]]]:
    """
    Scan a directory recursively for files containing snippet tags.
    
    Args:
        root_dir: Root directory to scan
        extensions: Optional list of file extensions to check
        exclude_dirs: Optional list of directories to exclude from scanning
        
    Returns:
        Dictionary mapping file paths to lists of tag information
    """
    if extensions is None:
        # Default extensions to check
        extensions = [
            '.py', '.java', '.js', '.ts', '.cs', '.cpp', '.c', '.go', '.rb', 
            '.php', '.swift', '.kt', '.rs', '.abap', '.md', '.html', '.xml'
        ]
    
    if exclude_dirs is None:
        # Default directories to exclude
        exclude_dirs = ['.tools', '.git', 'node_modules', 'venv', '.venv']
    
    file_tags = {}
    
    # Walk through the directory
    for root, dirs, files in os.walk(root_dir):
        # Skip excluded directories
        dirs[:] = [d for d in dirs if d not in exclude_dirs]
        
        for file in files:
            # Check if the file has one of the extensions we're interested in
            if any(file.endswith(ext) for ext in extensions):
                file_path = Path(root) / file
                try:
                    relative_path = str(file_path.relative_to(root_dir))
                    
                    # Skip files in excluded directories
                    if any(f"/{exclude_dir}/" in f"/{relative_path}/" for exclude_dir in exclude_dirs):
                        continue
                    
                    # Find tags in the file
                    tags = find_snippet_tags_in_file(file_path)
                    
                    if tags:
                        file_tags[relative_path] = tags
                except Exception as e:
                    logger.warning(f"Error processing file {file_path}: {e}")
    
    return file_tags


def check_for_snippet_tag_issues(file_tags: Dict[str, List[Dict[str, Any]]]) -> List[SnippetTagIssue]:
    """
    Check for various snippet tag issues.
    
    Args:
        file_tags: Dictionary mapping file paths to lists of tag information
        
    Returns:
        List of SnippetTagIssue objects
    """
    issues = []
    
    # Track all unique tags across all files
    tag_to_files = defaultdict(list)
    
    # First pass: collect all tags and check for issues within each file
    for file_path, tags in file_tags.items():
        # Group tags by name and type within this file
        tags_by_name_and_type = defaultdict(list)
        for tag_info in tags:
            tag_name = tag_info["tag"]
            tag_type = tag_info["tag_type"]
            key = f"{tag_name}:{tag_type}"
            tags_by_name_and_type[key].append(tag_info)
            
            # Track which files contain each tag
            tag_to_files[tag_name].append({
                "file": file_path,
                "line": tag_info["line"],
                "tag_type": tag_info["tag_type"],
                "content": tag_info["content"]
            })
        
        # Check for multiple instances of the same tag type within the file
        for key, tag_infos in tags_by_name_and_type.items():
            tag_name, tag_type = key.split(":", 1)
            
            # If there are multiple instances of the same tag type, report it
            if len(tag_infos) > 1:
                locations = []
                for t in tag_infos:
                    locations.append({
                        "file": file_path,
                        "line": t["line"],
                        "tag_type": t["tag_type"],
                        "content": t["content"]
                    })
                
                issues.append(SnippetTagIssue(
                    SnippetTagIssue.DUPLICATE_IN_FILE,
                    f"{tag_name} ({tag_type})",
                    locations
                ))
        
        # Check for unpaired tags within the file
        tags_by_name = defaultdict(list)
        for tag_info in tags:
            tags_by_name[tag_info["tag"]].append(tag_info)
        
        for tag_name, tag_infos in tags_by_name.items():
            # Count start and end tags
            start_tags = [t for t in tag_infos if t["tag_type"] == "snippet-start"]
            end_tags = [t for t in tag_infos if t["tag_type"] == "snippet-end"]
            
            # Check for unpaired tags (missing start or end)
            if len(start_tags) != len(end_tags):
                # Create location information
                locations = []
                for t in tag_infos:
                    locations.append({
                        "file": file_path,
                        "line": t["line"],
                        "tag_type": t["tag_type"],
                        "content": t["content"]
                    })
                
                issues.append(SnippetTagIssue(
                    SnippetTagIssue.UNPAIRED_TAG,
                    tag_name,
                    locations
                ))
    
    # Second pass: check for tags that appear in multiple files
    for tag_name, locations in tag_to_files.items():
        # Group locations by file
        files = defaultdict(list)
        for loc in locations:
            files[loc["file"]].append(loc)
        
        # If the tag appears in multiple files, it's a duplicate across files
        if len(files) > 1:
            # Create a simplified location list with just one entry per file
            simplified_locations = []
            for file_path, file_locs in files.items():
                # Include the first location in each file
                simplified_locations.append({
                    "file": file_path,
                    "line": file_locs[0]["line"],
                    "tag_type": file_locs[0]["tag_type"],
                    "content": file_locs[0]["content"]
                })
            
            issues.append(SnippetTagIssue(
                SnippetTagIssue.DUPLICATE_ACROSS_FILES,
                tag_name,
                simplified_locations
            ))
    
    return issues


def validate_snippet_tags(doc_gen: DocGen) -> List[SnippetTagIssue]:
    """
    Validate snippet tags in the codebase.
    
    Args:
        doc_gen: The DocGen instance
        
    Returns:
        List of SnippetTagIssue objects
    """
    # Scan the repository for snippet tags
    root_dir = doc_gen.root
    file_tags = scan_directory_for_snippet_tags(root_dir)
    
    # Check for issues
    issues = check_for_snippet_tag_issues(file_tags)
    
    return issues


def format_snippet_tag_issues_report(issues: List[SnippetTagIssue]) -> str:
    """
    Format a report of snippet tag issues.
    
    Args:
        issues: List of SnippetTagIssue objects
        
    Returns:
        Formatted report as a string
    """
    if not issues:
        return "No snippet tag issues found."
    
    # Group issues by type
    issues_by_type = defaultdict(list)
    for issue in issues:
        issues_by_type[issue.issue_type].append(issue)
    
    report_lines = [f"Found {len(issues)} snippet tag issues:"]
    
    # Report duplicate tags across files
    if SnippetTagIssue.DUPLICATE_ACROSS_FILES in issues_by_type:
        duplicates = issues_by_type[SnippetTagIssue.DUPLICATE_ACROSS_FILES]
        report_lines.append(f"\n=== DUPLICATE TAGS ACROSS FILES ({len(duplicates)}) ===")
        for issue in duplicates:
            report_lines.append(f"  {issue}")
    
    # Report unpaired tags
    if SnippetTagIssue.UNPAIRED_TAG in issues_by_type:
        unpaired = issues_by_type[SnippetTagIssue.UNPAIRED_TAG]
        report_lines.append(f"\n=== UNPAIRED TAGS ({len(unpaired)}) ===")
        for issue in unpaired:
            report_lines.append(f"  {issue}")
    
    # Report duplicate tags within files
    if SnippetTagIssue.DUPLICATE_IN_FILE in issues_by_type:
        duplicates_in_file = issues_by_type[SnippetTagIssue.DUPLICATE_IN_FILE]
        report_lines.append(f"\n=== DUPLICATE TAGS WITHIN FILES ({len(duplicates_in_file)}) ===")
        for issue in duplicates_in_file:
            report_lines.append(f"  {issue}")
    
    return "\n".join(report_lines)


def check_duplicate_snippet_tags_enhanced(doc_gen: DocGen) -> List[Tuple[str, List[str]]]:
    """
    Check for duplicate snippet tags across files.
    This is a simplified version that returns data in the format expected by the main script.
    
    Args:
        doc_gen: The DocGen instance
        
    Returns:
        List of tuples containing (tag, [file_paths]) for duplicate tags
    """
    issues = validate_snippet_tags(doc_gen)
    
    # Extract duplicate across files issues
    duplicates = []
    for issue in issues:
        if issue.issue_type == SnippetTagIssue.DUPLICATE_ACROSS_FILES:
            files = [loc["file"] for loc in issue.locations]
            duplicates.append((issue.tag, files))
        elif issue.issue_type == SnippetTagIssue.UNPAIRED_TAG:
            # Also report unpaired tags as duplicates for the main script
            files = [f"{loc['file']} (unpaired {loc['tag_type']} at line {loc['line']})" for loc in issue.locations]
            duplicates.append((f"{issue.tag} (unpaired)", files))
        elif issue.issue_type == SnippetTagIssue.DUPLICATE_IN_FILE:
            # Also report duplicate tags within files for the main script
            file = issue.locations[0]["file"]
            lines = [str(loc["line"]) for loc in issue.locations]
            duplicates.append((issue.tag, [f"{file} (multiple instances at lines: {', '.join(lines)})"]))
    
    return duplicates


def validate_snippets_enhanced(doc_gen: DocGen, strict: bool = False) -> bool:
    """
    Validate snippets in the codebase.
    
    Args:
        doc_gen: The DocGen instance
        strict: If True, raise an exception for validation errors
        
    Returns:
        True if validation passed, False otherwise
    """
    issues = validate_snippet_tags(doc_gen)
    
    if issues:
        report = format_snippet_tag_issues_report(issues)
        print(report)
        
        if strict:
            raise ValidationError("Snippet tag validation failed")
        
        return False
    
    return True