# WRITEME: auto-generated READMEs

## Overview

Describes WRITEME, a tool to automatically generate service-level READMEs from
metadata and Jinja templates.

This is an internal tool intended for use only by the AWS code examples team.

## Prerequisites

We recommend a virtual environment to run this tool.

```
cd .tools/readmes
python -m venv .venv

# Windows
.venv\Scripts\activate
python -m pip install -r requirements_freeze.txt

# Linux or MacOS
source .venv/bin/activate
```

Depending on how you have Python installed and on your operating system,
the commands might vary slightly. For example, on Windows, use `py` in place of
`python` and uses backslashes in the `venv` path.

## Generate a README

> These instructions assume you're running the commands from the `.tools/readmes`
> directory, using the venv installed there.

WRITEME creates content primarily from metadata you have already
authored for the SOS project. After you have authored metadata and snippet tags
for your examples, run the following command in the `.tools/readmes` folder:

```
python -m writeme --languages <language>:<version> --services <service>
```

WRITEME reads metadata and config data and generates READMEs in the service
folder for the specified languages, versions, and services.

For example, to generate an S3 README for Python:

```
python -m writeme --languages Python:3 --services s3
```

This creates a README.md file in the `python/example_code/s3` folder.

### Parameters

- `--languages` must match a top-level language:version in sdks.yaml.
- `--services` must match a top-level service in services.yaml.
- `--safe` (optional) when specified, the existing README.md is renamed to the
  `saved_readme` value in config.py (such as README.old.md).
- `--verbose` When set, output verbose debugging info.
- `--dry-run`, `--no-dry-run` In dry run, compare current vs generated and exit with failure if they do not match.
- `--check` Verifies whether the existing README.md matches the proposed new README.md
  (but does not write a new README.md). This is the same check that is run by the GitHub action.

You can get inline usage info by using the `-h` flag:

```
python -m writeme -h
```

### Configuration

Additional configuration is kept in `config.py`.

- `entities` is a dictionary of entities that are not otherwise defined in
  services.yaml.
- `language` is a dictionary of language and version for each SDK. Fields are:
  - `base_folder` the root folder for the SDK version.
  - `service_folder` a Jinja template of the service folder for the SDK version.
    This might not work in all cases. If not, use the `--svc_folder` override.
  - `sdk_api_ref` a Jinja template of the SDK API Reference topic for the SDK version.
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

## Generate multiple READMEs

[`writeme.py`](writeme.py) executes the WRITEME logic across a set of languages,
versions, and services.

```
python .tools/readmes/writeme.py --languages <language1>:<version> <language2>:<version> --service <service1> <service2>
```

For example, to generate S3 and STS READMEs for Python sdk version 3 and Go sdk version 2:

```
python .tools/readmes/writeme.py --languages Python:3 Go:2 --services s3 sts
```

This creates the README.md files in `python/example_code/s3` and other folders.

To build all READMEs for Rust:

```
$ python -m writeme --languages Rust:1
INFO:root:Dry run, no changes will be made.
DEBUG:root:Rendering Rust:1:acm
DEBUG:root:Rendering Rust:1:api-gateway
DEBUG:root:Rendering Rust:1:apigatewaymanagementapi
# ...
DEBUG:root:Rendering Rust:1:transcribe-medical
DEBUG:root:Rendering Rust:1:translate
```

To specify `svc_folder` overrides, add a dict to the language in `config.py` with
the name `service_folder_overrides` and entries with the service name as the key
and complete folder override as the value. See dotnetv3 for an example.

And yes, building all readmes for all languages after changing metadata or templates is now as easy as

```
python -m writeme
```
