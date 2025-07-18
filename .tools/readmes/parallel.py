# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Parallel processing module for WRITEME to speed up README generation.
"""

import concurrent.futures
import logging
import os
from typing import Any, Callable, Dict, List, Tuple

logger = logging.getLogger(__name__)

# Default to number of CPUs minus 1 (leave one for system)
DEFAULT_WORKERS = max(1, os.cpu_count() - 1) if os.cpu_count() else 2


def process_in_parallel(
    func: Callable,
    items: List[Tuple],
    max_workers: int = DEFAULT_WORKERS,
    progress_callback: Callable = None
) -> Dict[str, List[Any]]:
    """
    Process items in parallel using a thread pool.
    
    Args:
        func: Function to call for each item
        items: List of argument tuples to pass to the function
        max_workers: Maximum number of worker threads
        progress_callback: Optional callback function to report progress
        
    Returns:
        Dict with categorized results
    """
    results = {
        "written": [],
        "unchanged": [],
        "skipped": [],
        "failed": [],
        "non_writeme": [],
        "no_folder": []
    }
    
    # Use fewer workers if we have fewer items
    actual_workers = min(max_workers, len(items))
    
    if actual_workers <= 1 or len(items) <= 1:
        # For small jobs, just process sequentially
        for i, args in enumerate(items):
            try:
                result = func(*args)
                _categorize_result(result, results)
                
                if progress_callback:
                    progress_callback(1, f"Processed {i+1}/{len(items)}")
            except Exception as e:
                logger.error(f"Error processing item {args}: {e}")
                results["failed"].append(args)
                
                if progress_callback:
                    progress_callback(1, f"Error: {e}")
    else:
        # Process in parallel for larger jobs
        logger.info(f"Processing {len(items)} items with {actual_workers} workers")
        
        with concurrent.futures.ThreadPoolExecutor(max_workers=actual_workers) as executor:
            future_to_args = {executor.submit(func, *args): args for args in items}
            
            for i, future in enumerate(concurrent.futures.as_completed(future_to_args)):
                args = future_to_args[future]
                try:
                    result = future.result()
                    _categorize_result(result, results)
                    
                    if progress_callback:
                        progress_callback(1, f"Processed {i+1}/{len(items)}")
                except Exception as e:
                    logger.error(f"Error processing item {args}: {e}")
                    results["failed"].append(args)
                    
                    if progress_callback:
                        progress_callback(1, f"Error: {e}")
    
    return results


def _categorize_result(result, results):
    """Categorize a result into the appropriate result list."""
    if result is None:
        return
        
    if isinstance(result, tuple) and len(result) == 2:
        category, item = result
        if category in results:
            results[category].append(item)
    elif isinstance(result, str):
        # Default to "written" category for string results
        results["written"].append(result)