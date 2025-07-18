#!/usr/bin/env python3
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
WRITEME - AWS SDK Examples README Generator

This tool generates README.md files for AWS SDK code examples across different
programming languages and services.
"""

import argparse
import logging
import os
import sys
import time
import re
from pathlib import Path

# Command line flags
NO_UPDATE_FLAG = "--no-update"
CACHE_FLAG = "--use-cache"


def setup_logging(verbose: bool = False) -> None:
    """Configure logging based on verbosity level."""
    level = logging.DEBUG if verbose else logging.INFO
    logging.basicConfig(
        level=level,
        format="%(asctime)s - %(levelname)s - %(message)s",
        datefmt="%H:%M:%S",
        force=True
    )


def parse_arguments() -> argparse.Namespace:
    """Parse command line arguments with better help messages."""
    parser = argparse.ArgumentParser(
        description="Generate README.md files for AWS SDK code examples",
        formatter_class=argparse.ArgumentDefaultsHelpFormatter
    )
    
    # Add language and service options
    parser.add_argument(
        "--languages", 
        nargs="+", 
        default=["all"],
        help="Languages to process (e.g. 'Python:3 JavaScript:3' or 'all')"
    )
    
    parser.add_argument(
        "--services", 
        nargs="+", 
        default=["all"],
        help="Services to process (e.g. 's3 dynamodb' or 'all')"
    )
    
    # Add operation mode options
    parser.add_argument(
        "--safe", 
        action="store_true",
        help="Save a backup of the original README files"
    )
    
    parser.add_argument(
        "--dry-run", 
        action="store_true",
        help="Compare current vs generated READMEs without making changes"
    )
    
    parser.add_argument(
        "--check", 
        action="store_true",
        help="Alias for --dry-run"
    )
    
    parser.add_argument(
        "--diff", 
        action="store_true",
        help="Show a diff of READMEs that have changed"
    )
    
    # Add validation options
    parser.add_argument(
        "--validate", 
        action="store_true",
        help="Validate snippet tags and other metadata"
    )
    
    parser.add_argument(
        "--strict-validation", 
        action="store_true",
        help="Fail if validation errors are found"
    )
    
    parser.add_argument(
        "--validate-only", 
        action="store_true",
        help="Only run validation, don't generate READMEs"
    )
    
    parser.add_argument(
        "--skip-duplicate-check", 
        action="store_true",
        help="Skip checking for duplicate snippet tags (not recommended)"
    )
    
    # Add performance options
    parser.add_argument(
        "--no-update",
        action="store_true",
        help="Skip updating the environment (for faster development)"
    )
    
    parser.add_argument(
        "--use-cache",
        action="store_true",
        help="Use cached metadata when available (speeds up repeated runs)"
    )
    
    # Add output options
    parser.add_argument(
        "--verbose", 
        action="store_true",
        help="Enable verbose debugging output"
    )
    
    parser.add_argument(
        "--summary", 
        action="store_true",
        help="Show a summary of changes at the end"
    )
    
    return parser.parse_args()


def update_environment() -> None:
    """Update the WRITEME environment by installing the package in development mode."""
    from update import update
    # The update function already logs a message, so we don't need to log here
    update()


def show_summary(start_time) -> None:
    """Show a summary of the changes made."""
    elapsed = time.time() - start_time
    
    print("\n=== WRITEME Summary ===")
    print(f"Execution time: {elapsed:.2f} seconds")


def count_readme_errors_from_output(output: str, args) -> int:
    """
    Count the number of README errors from the output.
    
    Args:
        output: The output from the writeme function
        args: Command line arguments
        
    Returns:
        The number of README errors
    """
    # First check for "Incorrect:" lines (standard format)
    if "Incorrect:" in output:
        incorrect_lines = [line for line in output.split('\n') if line.strip().startswith("Incorrect:")]
        return len(incorrect_lines)
    
    # If using --diff flag, we need to parse the diff output
    if args.diff and "Diff:" in output:
        # Use regex to find all unique README identifiers in the diff output
        # The pattern looks for lines like "Diff: --- .NET:4:Service.dynamodb/current"
        pattern = r"Diff:\s+---\s+([^/]+)/current"
        matches = re.findall(pattern, output)
        
        # If we found matches, return the count of unique identifiers
        if matches:
            return len(set(matches))
        
        # Fallback: count the number of diff sections
        # Each diff section starts with "Diff:" and represents one incorrect README
        sections = output.split("Diff:")
        # First section is before any "Diff:" so skip it
        return len(sections) - 1
    
    # If we can't find any errors, return 0
    return 0


def main() -> int:
    """Main entry point for the WRITEME tool."""
    start_time = time.time()
    
    # Parse command line arguments
    args = parse_arguments()
    
    # Setup logging
    setup_logging(args.verbose)
    
    # Configure caching if requested
    if args.use_cache:
        os.environ["USE_METADATA_CACHE"] = "1"
    
    # Update environment if needed
    if not args.no_update:
        try:
            update_environment()
        except Exception as e:
            logging.error(f"Failed to update environment: {e}")
            return 1
    
    # Import DocGen and validator here to avoid circular imports
    from aws_doc_sdk_examples_tools.doc_gen import DocGen
    
    # Try to use the enhanced validator if available
    try:
        from enhanced_validator import (
            validate_snippets_enhanced as validate_snippets,
            check_duplicate_snippet_tags_enhanced as check_duplicate_snippet_tags,
            validate_snippet_tags,
            format_snippet_tag_issues_report,
            ValidationError
        )
        logging.info("Using enhanced validator for comprehensive snippet tag checks")
        use_enhanced_validator = True
    except ImportError:
        try:
            from validator import validate_snippets, check_duplicate_snippet_tags, ValidationError
            logging.info("Using standard validator for snippet tag checks")
            use_enhanced_validator = False
        except ImportError:
            logging.error("Validator module not found. Cannot check for duplicate snippet tags.")
            return 1
    
    # Load DocGen data
    try:
        # Try to import cache module
        try:
            from cache import load_from_cache, save_to_cache
            CACHE_AVAILABLE = True
        except ImportError:
            CACHE_AVAILABLE = False
            
            # Dummy cache functions if cache module not available
            def load_from_cache(key):
                return None
                
            def save_to_cache(key, data):
                return False
        
        # Try to load from cache first
        doc_gen = None
        if CACHE_AVAILABLE and args.use_cache:
            doc_gen = load_from_cache("doc_gen_cache")
            if doc_gen:
                logging.info("Using cached DocGen data")
        
        if doc_gen is None:
            logging.info("Building DocGen data from scratch")
            doc_gen = DocGen.from_root(Path(__file__).parent.parent.parent, incremental=True)
            
            # Process metadata
            for path in (doc_gen.root / ".doc_gen/metadata").glob("*_metadata.yaml"):
                doc_gen.process_metadata(path)
            
            # Collect snippets
            doc_gen.collect_snippets()
            
            # Save to cache if available
            if CACHE_AVAILABLE and args.use_cache:
                save_to_cache("doc_gen_cache", doc_gen)
    except Exception as e:
        logging.error(f"Failed to load DocGen data: {e}")
        return 1
    
    # Store validation results to avoid running validation twice
    validation_issues = None
    validation_passed = True
    error_count = 0
    
    # Always check for snippet tag issues unless explicitly skipped
    if not args.skip_duplicate_check:
        logging.info("Checking for snippet tag issues...")
        
        if use_enhanced_validator:
            # Use the enhanced validator to check for all types of snippet tag issues
            validation_issues = validate_snippet_tags(doc_gen)
            if validation_issues:
                print("\n=== SNIPPET TAG VALIDATION ISSUES ===")
                print(format_snippet_tag_issues_report(validation_issues))
                validation_passed = False
                error_count = len(validation_issues)
                
                # Exit with error if strict validation is enabled
                if args.strict_validation:
                    logging.error(f"Validation failed: {error_count} snippet tag issues found")
                    return error_count
            else:
                print("No snippet tag issues found.")
        else:
            # Fall back to the standard validator for duplicate tags only
            duplicates = check_duplicate_snippet_tags(doc_gen)
            if duplicates:
                print("\n=== DUPLICATE SNIPPET TAGS ===")
                print(f"Found {len(duplicates)} duplicate snippet tags:")
                for tag, files in duplicates:
                    file_list = ", ".join(files)
                    print(f"  Tag '{tag}' found in multiple files: {file_list}")
                validation_passed = False
                error_count = len(duplicates)
                
                # Exit with error if strict validation is enabled
                if args.strict_validation:
                    logging.error(f"Validation failed: {error_count} duplicate snippet tags found")
                    return error_count
            else:
                print("No snippet tag issues found.")
    
    # Run additional validation if requested (but don't repeat snippet tag validation)
    if args.validate or args.validate_only:
        logging.info("Running additional validation checks...")
        
        # Only run full validation if we haven't already done so
        if args.skip_duplicate_check:
            try:
                # Run validation and get issues
                if use_enhanced_validator:
                    validation_issues = validate_snippet_tags(doc_gen)
                    validation_passed = len(validation_issues) == 0
                    error_count = len(validation_issues) if validation_issues else 0
                    
                    if not validation_passed:
                        print("\n=== SNIPPET TAG VALIDATION ISSUES ===")
                        print(format_snippet_tag_issues_report(validation_issues))
                        
                        if args.strict_validation:
                            logging.error(f"Validation failed: {error_count} snippet tag issues found")
                            return error_count
                else:
                    # Standard validator doesn't return issues directly
                    validation_passed = validate_snippets(doc_gen, False)  # Don't raise exception
                    if not validation_passed and args.strict_validation:
                        logging.error("Validation failed: snippet tag issues found")
                        return 1  # Can't get exact count with standard validator
            except ValidationError as e:
                logging.error(f"Validation error: {e}")
                if args.strict_validation:
                    return 1
            except Exception as e:
                logging.error(f"Unexpected error during validation: {e}")
                if args.strict_validation:
                    return 1
        else:
            # We've already run validation, just report the status
            if not validation_passed:
                logging.warning(f"Validation found {error_count} issues (see above)")
                if args.strict_validation:
                    return error_count
            else:
                print("All validations passed successfully.")
        
        # Exit if only validation was requested
        if args.validate_only:
            # Return error count if validation failed, otherwise 0
            return error_count if not validation_passed else 0
    
    # Use the original writeme.py approach but with our improved arguments
    # This avoids the enum conversion issues
    modified_argv = [sys.argv[0]]
    
    # Add our arguments to the modified argv
    if args.languages:
        for lang in args.languages:
            modified_argv.extend(["--languages", lang])
    
    if args.services:
        for svc in args.services:
            modified_argv.extend(["--services", svc])
    
    if args.safe:
        modified_argv.append("--safe")
    
    if args.verbose:
        modified_argv.append("--verbose")
    
    if args.dry_run or args.check:
        modified_argv.append("--dry-run")
    
    if args.diff:
        modified_argv.append("--diff")
    
    # Save original argv
    original_argv = sys.argv.copy()
    
    readme_error_count = 0
    try:
        # Replace sys.argv with our modified version
        sys.argv = modified_argv
        
        # Import and run the original writeme function through typer
        from typer import run
        from runner import writeme
        
        # Capture stdout to parse for incorrect READMEs
        import io
        from contextlib import redirect_stdout
        
        f = io.StringIO()
        with redirect_stdout(f):
            try:
                run(writeme)
                result = 0
            except SystemExit as e:
                # Capture the exit code from typer
                result = e.code
        
        # Get the output and print it
        output = f.getvalue()
        print(output)
        
        # Count README errors from the output
        readme_error_count = count_readme_errors_from_output(output, args)
        if readme_error_count > 0:
            logging.info(f"Found {readme_error_count} incorrect READMEs")
    except Exception as e:
        logging.error(f"Error running writeme: {e}", exc_info=True)
        result = 1
    finally:
        # Restore original argv
        sys.argv = original_argv
    
    # Show summary if requested
    if args.summary:
        show_summary(start_time)
    
    # Calculate total error count (snippet issues + README errors)
    total_error_count = error_count + readme_error_count
    
    # If we have any errors and we're not in validate-only mode, return the total error count
    if total_error_count > 0 and not args.validate_only:
        print(f"Found {error_count} snippet issues and {readme_error_count} incorrect READMEs.")
        print(f"Returning total error count ({total_error_count}) as exit code.")
        return total_error_count
    
    # Otherwise return the result from the writeme function
    return result


if __name__ == "__main__":
    sys.exit(main())
else:
    from .runner import writeme
    main = writeme