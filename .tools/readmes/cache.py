# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Cache implementation for WRITEME to speed up repeated runs.
"""

import json
import logging
import os
import pickle
from pathlib import Path
from typing import Any, Dict, Optional

logger = logging.getLogger(__name__)

# Cache directory relative to the readmes directory
CACHE_DIR = Path(__file__).parent / ".cache"


def get_cache_enabled() -> bool:
    """Check if caching is enabled via environment variable."""
    return os.environ.get("USE_METADATA_CACHE", "0") == "1"


def ensure_cache_dir() -> None:
    """Ensure the cache directory exists."""
    if not CACHE_DIR.exists():
        CACHE_DIR.mkdir(exist_ok=True)
        logger.debug(f"Created cache directory: {CACHE_DIR}")


def get_cache_path(key: str) -> Path:
    """Get the cache file path for a given key."""
    # Create a filename-safe version of the key
    safe_key = key.replace("/", "_").replace(":", "_")
    return CACHE_DIR / f"{safe_key}.pickle"


def save_to_cache(key: str, data: Any) -> bool:
    """
    Save data to cache.
    
    Args:
        key: Cache key
        data: Data to cache (must be pickle-able)
        
    Returns:
        bool: True if successfully cached, False otherwise
    """
    if not get_cache_enabled():
        return False
    
    try:
        ensure_cache_dir()
        cache_path = get_cache_path(key)
        
        with open(cache_path, "wb") as f:
            pickle.dump(data, f)
        
        logger.debug(f"Cached data for key: {key}")
        return True
    except Exception as e:
        logger.warning(f"Failed to cache data for key {key}: {e}")
        return False


def load_from_cache(key: str) -> Optional[Any]:
    """
    Load data from cache.
    
    Args:
        key: Cache key
        
    Returns:
        The cached data or None if not found or caching disabled
    """
    if not get_cache_enabled():
        return None
    
    cache_path = get_cache_path(key)
    
    if not cache_path.exists():
        return None
    
    try:
        with open(cache_path, "rb") as f:
            data = pickle.load(f)
        
        logger.debug(f"Loaded data from cache for key: {key}")
        return data
    except Exception as e:
        logger.warning(f"Failed to load cache for key {key}: {e}")
        return None


def clear_cache() -> None:
    """Clear all cached data."""
    if CACHE_DIR.exists():
        for cache_file in CACHE_DIR.glob("*.pickle"):
            try:
                cache_file.unlink()
            except Exception as e:
                logger.warning(f"Failed to delete cache file {cache_file}: {e}")
        
        logger.info("Cache cleared")