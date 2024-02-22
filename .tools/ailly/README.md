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
