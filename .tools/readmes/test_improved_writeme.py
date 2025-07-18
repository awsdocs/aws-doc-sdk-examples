#!/usr/bin/env python3
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Test script for the improved writeme.py
"""

import os
import sys
import subprocess
from pathlib import Path

def run_test(args, expected_success=True):
    """Run a test with the given arguments and check if it succeeds."""
    script_path = Path(__file__).parent / "improved_writeme.py"
    
    # Add --no-update to speed up tests
    if "--no-update" not in args:
        args.append("--no-update")
    
    # Add --dry-run to avoid making changes
    if "--dry-run" not in args:
        args.append("--dry-run")
    
    cmd = [sys.executable, str(script_path)] + args
    print(f"Running: {' '.join(cmd)}")
    
    try:
        result = subprocess.run(cmd, capture_output=True, text=True)
        success = result.returncode == 0
        
        if success == expected_success:
            print(f"✅ Test passed: {' '.join(args)}")
            return True
        else:
            print(f"❌ Test failed: {' '.join(args)}")
            print(f"Exit code: {result.returncode}")
            print(f"Output: {result.stdout}")
            print(f"Error: {result.stderr}")
            return False
    except Exception as e:
        print(f"❌ Test error: {e}")
        return False

def main():
    """Run tests for the improved writeme.py."""
    tests = [
        # Basic tests
        (["--languages", "all", "--services", "all"], True),
        (["--languages", "Python:3", "--services", "s3"], True),
        (["--languages", "Python:3", "--services", "s3", "--diff"], True),
        
        # Performance options
        (["--use-cache"], True),
        
        # Output options
        (["--verbose"], True),
        (["--summary"], True),
    ]
    
    failures = 0
    for args, expected_success in tests:
        if not run_test(args, expected_success):
            failures += 1
    
    print(f"\nTests completed: {len(tests) - failures} passed, {failures} failed")
    return failures

if __name__ == "__main__":
    sys.exit(main())