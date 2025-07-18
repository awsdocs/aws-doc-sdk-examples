# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Progress tracking module for WRITEME to provide better feedback during execution.
"""

import sys
import time
from typing import Dict, List, Optional, Any

class ProgressTracker:
    """Track and display progress for WRITEME operations."""
    
    def __init__(self, total: int = 0, show_spinner: bool = True):
        self.total = total
        self.current = 0
        self.start_time = time.time()
        self.show_spinner = show_spinner
        self.spinner_chars = ['⠋', '⠙', '⠹', '⠸', '⠼', '⠴', '⠦', '⠧', '⠇', '⠏']
        self.spinner_index = 0
        self.last_update = 0
        self.results: Dict[str, List[Any]] = {
            "written": [],
            "unchanged": [],
            "skipped": [],
            "failed": [],
            "non_writeme": [],
            "no_folder": []
        }
        
    def start(self, message: str = "Processing") -> None:
        """Start the progress tracking with an initial message."""
        self.start_time = time.time()
        self.current = 0
        print(f"{message}...", end="", flush=True)
        
    def update(self, increment: int = 1, message: Optional[str] = None) -> None:
        """Update the progress counter and display."""
        self.current += increment
        
        # Only update display every 100ms to avoid excessive terminal output
        current_time = time.time()
        if current_time - self.last_update < 0.1 and self.current < self.total:
            return
            
        self.last_update = current_time
        
        if self.total > 0:
            percentage = min(100, int(100 * self.current / self.total))
            
            if self.show_spinner:
                spinner = self.spinner_chars[self.spinner_index % len(self.spinner_chars)]
                self.spinner_index += 1
                
                # Calculate elapsed time and ETA
                elapsed = current_time - self.start_time
                if self.current > 0:
                    eta = elapsed * (self.total - self.current) / self.current
                    eta_str = f"ETA: {int(eta)}s" if eta > 0 else "Done"
                else:
                    eta_str = "Calculating..."
                
                status = f"\r{spinner} {percentage}% ({self.current}/{self.total}) {eta_str}"
                if message:
                    status += f" - {message}"
                
                # Clear the line and print the status
                print(f"\r{' ' * 80}", end="", flush=True)
                print(f"\r{status}", end="", flush=True)
        elif message:
            # Just show spinner and message if no total is known
            if self.show_spinner:
                spinner = self.spinner_chars[self.spinner_index % len(self.spinner_chars)]
                self.spinner_index += 1
                print(f"\r{' ' * 80}", end="", flush=True)
                print(f"\r{spinner} {message}", end="", flush=True)
    
    def add_result(self, category: str, item: Any) -> None:
        """Add an item to a result category."""
        if category in self.results:
            self.results[category].append(item)
    
    def finish(self) -> None:
        """Complete the progress tracking and show final status."""
        elapsed = time.time() - self.start_time
        print(f"\r{' ' * 80}", end="", flush=True)
        print(f"\rCompleted in {elapsed:.2f}s", flush=True)
    
    def summary(self) -> None:
        """Print a summary of the results."""
        print("\n=== WRITEME Summary ===")
        print(f"Total time: {time.time() - self.start_time:.2f}s")
        
        for category, items in self.results.items():
            if items:
                print(f"{category.capitalize()}: {len(items)}")
                
        # Print details for important categories
        if self.results["written"]:
            print("\nWritten READMEs:")
            for item in sorted(self.results["written"]):
                print(f"  ✓ {item}")
                
        if self.results["failed"]:
            print("\nFailed READMEs:")
            for item in sorted(self.results["failed"]):
                if isinstance(item, tuple):
                    print(f"  ✗ {item[0]}")
                else:
                    print(f"  ✗ {item}")