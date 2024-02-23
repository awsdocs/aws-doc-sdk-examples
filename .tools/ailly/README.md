# AWS Doc SDK Examples Ailly

1. Get a copy of the snippet database, either using `./extract_snippets.sh` or requesting one from DavidSouther.
1. Choose the source language and services to have ailly translate.
1. Update `ailly.py` and `plugin.mjs`

## Update `ailly.py`

1. Check the `--language` argument, and make sure the source language folder is there. Optionally, set the default for your run.
1. Check the `--service` argument, and the same. Optionally, set the default.
1. Update, as necessary, the `example` folder path on or around line 100.

## Update `plugin.mjs`

1. Around line 36, find the constant `Best`.
2. Check that both source and desired target languages have "best" entries.
3. If missing, add three (minus one/plus two) snippet entries. These should be the "best" examples that show client creation, logging, error handling, and other best practices you want the LLM to pick up on.

## Run Ailly

Ensure your account has [Bedrock model access](https://docs.aws.amazon.com/bedrock/latest/userguide/model-access.html) to `Titan Embeddings G1 - Text` and `Claude` (and maybe `Claude Instant`) in the region(s) you will be running.

```
python3 .tools/ailly/ailly.py --language [language] --service [service]
```

Optionally: `--verbose --npx-check --additional-prompt ...`

If successful, the generated code will be in a `.scouts` folder in the example.

### Under the hood

The ailly.py script coordinates aws-doc-sdk-examples project & folder structure into a command line invocation for [Ailly](https://github.com/davidsouther/ailly). This will use every file in the subfolder as an input to an LLM (default Claude on Bedrock), with a custom prompt and output in `.scouts/[target_language]` of the source service folder.

The prompt has three parts:

1. Several blocks of code found using RAG, as implemented in `plugin.mjs`. Ailly orchestrates the run overall, and `plugin.mjs` handles aws-sdk-code-examples specific RAG details.

2. The source input file

3. A final text prompt: `f"Translate the final block of code from {source} to {target} programming language. {instructions}"`. `{instructions}` is the value from `--additional-prompt` to `ailly.py`.
