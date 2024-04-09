# Social Media Draft Generator
This script creates a social media draft that can be used as a starting point for a social media post.

# Why?
This script was created because Claude 3 Sonnet is a LLM capable of writing perfectly reasonable and find social media content, which an engineer can quickly discard or use as a foundation for whatever custom content they feel is missing from the raw output.

# Customization
You can easily modify the prompt or response length within the script by updating values in [config.yml](config.yml).

# Pre-requisites
This code requires you to enable Claude 3 Sonnet in your AWS Account.

## Execution
Tested on MacOS Sonoma with Python 3.11. From this location, you could run:
```bash
python3 write_post.py <README_LOCATION>
```
A working example:
```python
python3 write_post.py ../../python/cross_service/photo_analyzer/README.md
```
