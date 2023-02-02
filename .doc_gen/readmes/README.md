# WRITEME: auto-generated READMEs

## Overview

Describes WRITEME, a tool to automatically generate service-level READMEs from
metadata and Jinja templates.

This is an internal tool intended for use only by the AWS code examples team.

## Prerequisites

You must have a recent version of Python installed to run this tool, 
and a recent version of pip (Python's package manager) to install the
required packages.

### Install packages

We recommend a virtual environment. Create a virtual environment 
and install packages by running the following commands in the
`.doc_gen/readmes` folder:

```
python -m venv .venv
.venv\Scripts\activate # Windows
source .venv/bin/activate # *nix
python -m pip install -r requirements.txt
```

Depending on how you have Python installed and on your operating system,
the commands might vary slightly. For example, on Windows, use `py` in place of
`python` and uses backslashes in the `venv` path.

## Generate a README

WRITEME creates content primarily from metadata you have already
authored for the SOS project. After you have authored metadata and snippet tags
for your examples, run the following command in the root folder of the repo:

```
python .doc_gen/readmes/writeme.py <language> <version> <service>
```

WRITME reads metadata and config data and generates a README in the service
folder for the specified language, version, and service.

For example, to generate an S3 README for Python:

```
python .doc_gen/readmes/writeme.py Python 3 s3
```

This creates a README.md file in the `python/example_code/s3` folder.

### Parameters

* `language` must match a top-level language in sdks.yaml.
* `version` must be defined for the language in sdks.yaml.
* `service` must match a top-level service in services.yaml.
* `--safe` (optional) when specified, the existing README.md is renamed to the 
`saved_readme` value in config.py (such as README.old.md).
* `--svc_folder` (optional) overrides the output folder for the README.

You can get inline usage info by using the `-h` flag:

```
python .doc_gen/readmes/writeme.py -h
``` 

### Configuration

Additional configuration is kept in `config.py`.

* `entities` is a dictionary of entities that are not otherwise defined in
services.yaml.
* `language` is a dictionary of language and version for each SDK. Fields are:
    * `base_folder` the root folder for the SDK version.
    * `service_folder` a Jinja template of the service folder for the SDK version.
    This might not work in all cases. If not, use the `--svc_folder` override.
    * `sdk_api_ref` a Jinja template of the SDK API Reference topic for the SDK version.
    This is used to create the link to the reference page in the Additional Resources
    section, such as to the Boto3 S3 reference page for Python. This is a best effort,
    and if the generated link is wrong, you can update it manually. On subsequent runs
    of WRITEME, the existing link is kept. 
    
### Custom content

Custom content can be per-SDK or per-README.

#### SDK custom content

Currently, you can define per-SDK content in `includes/prerequisites.jinja2` and
`includes/run_instructions.jinja2`. In each of these files, you can find (or add) `if`
blocks that include custom content for specific SDK versions.

#### README custom content

Each README can have custom content in specific places. The first time you
generate a README, it contains empty blocks designated by special comments, such as
the following:

```
<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->
```

Any content you add within these comments is preserved in subsequent generations
of the README. Do not change the names of these comments or remove them. Keep them
empty if you don't need custom content.
