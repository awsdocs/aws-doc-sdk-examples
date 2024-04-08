// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// INSTALLATION:
// In this directory:
//
// npm install

// USAGE:
// In this directory:
// (nb these are for bash, please adjust ${PWD} as necessary for bash/powershell)
//
// npx ailly --plugin file://${PWD}/plugin.mjs --engine bedrock \
//     --root ../../[language]/example/[service] \
//     --out ../../[language]/example/[service]/scouts/[target language] \
//     --prompt "Translate from [source language] to [target language]. [Additional instructions.]"
//
// TODO:
// python3 .tools/ailly/ailly.py --target Rust:1 --source Python:3 --service cloudwatch-logs

// DATA:
// Get the vector file from davidsouther via S3 presigned URL
// MacOS:
// cd .tools/ailly
// rm -rf .vectors
// curl [presigned_url] > vectors-12-15.zip
// unzip vectors-12-15.zip

import { readFile } from "node:fs/promises";
import { join } from "node:path";
import { fileURLToPath } from "node:url";
import { Ailly } from "@ailly/core";

const SIMILAR = 0.8;
const SAME = 0.99609375;
const TOP_N = 5;
const PER_LANG = 3;

const Best = {
  javascriptv3: [
    {
      score: 0.9,
      language: "javascriptv3",
      name: "javascript.v3.dynamodb.hello.txt",
    },
    {
      score: 0.9,
      language: "javascriptv3",
      name: "javascript.v3.glue.hello.txt",
    },
    {
      score: 0.9,
      language: "javascriptv3",
      name: "javascript.v3.support.scenarios.Hello.txt",
    },
  ],
  python: [
    {
      score: 0.9,
      language: "python",
      name: "python.example_code.python.LambdaWrapper.full.txt",
    },
    {
      score: 0.9,
      language: "python",
      name: "python.example_code.rds.helper.InstanceWrapper_full.txt",
    },
    {
      score: 0.9,
      language: "python",
      name: "python.example_code.sfn.Scenario_GetStartedStateMachines.txt",
    },
  ],
  rust: [
    {
      score: 0.9,
      language: "rust",
      name: "ec2.rust.create-instance.txt",
    },
    {
      score: 0.9,
      language: "rust",
      name: "logging.rust.main.txt",
    },
    {
      score: 0.9,
      language: "rust",
      name: "s3.rust.list-buckets.txt",
    },
  ],
  kotlin: [
    {
      score: 0.9,
      language: "kotlin",
      name: "comprehend.kotlin.detect_language.main.txt",
    },
    {
      score: 0.9,
      language: "kotlin",
      name: "comprehend.kotlin.detect_sentiment.main.txt",
    },
    {
      score: 0.9,
      language: "kotlin",
      name: "comprehend.kotlin.detect_syntax.main.txt",
    },
  ],
};

const LANGUAGES = Object.keys(Best);

class MyRAG extends Ailly.RAG {
  constructor(engine, _path) {
    super(engine, join(fileURLToPath(import.meta.url), "../.vectors"));
  }

  async clean(c) {
    // if (c.results?.indexOf('```') > -1) {
    //   c.results = c.results.substring(c.results.indexOf('```'), c.results.lastIndexOf('```') + 3)
    // }
  }

  async init() {
    if (!(await this.index.isIndexCreated())) {
      await this.index.createIndex();
    }
    await this.index.loadIndexData();
    this.index._data.items.forEach((item) => {
      if (Array.isArray(item.vector)) return;
      const vector = Buffer.from(item.vector, "base64");
      // 1536 is a common vector length for encodings. This is a heuristic to load it into a Float32Array,
      // which is faster for per-element dot product computations than native arrays.
      if (vector.length === 1536 * 4)
        item.vector = new Float32Array(vector.buffer);
    });
    return this;
  }

  /*
    1. From every language, get best three (PER_LANG).
        1. Filter on similarity (cosine >= SIMILAR), to guard on bad results
        2. and sameness (cosine <= SAME), to not include the same snippet as the source
    2. From best three per language, take five (TOP_N) total (with at least three languages included)
    */
  async augment(content) {
    let vector = [];
    try {
      vector = await this.engine.vector(content.prompt, {});
    } catch (e) { }
    const langs = LANGUAGES.filter(lang => content.system[0].includes(lang));
    const map = (
      await Promise.all(
        langs.map(async (language) => [
          ...(Best[language] ?? []),
          ...(
            await this.index.queryItems(vector, PER_LANG, { language })
          )
            .filter(({ score }) => score >= SIMILAR && score < SAME)
            .map(({ score, item }) => ({
              score,
              path: item.metadata.path,
              name: item.metadata.name,
              language,
            })),
        ])
      )
    ).flat();

    map.sort((a, b) => b.score - a.score);
    const tmpResult = map.slice(0, TOP_N);

    console.log("Found augmentations:");
    console.log(tmpResult.map(({ score, name }) => `\t${name} (${score})`).join("\n"));

    const results = tmpResult.map(async ({ score, name, language }) => ({
      score,
      name,
      language,
      content: await readFile(join(".vectors", name), { encoding: "utf8" }),
    }));

    content.augment = await Promise.all(results);
  }
}

export default async function makeSDKExamplesPlugin(engine, { root: path }) {
  return new MyRAG(engine).init();
}

async function cli() {
  const engine = await Ailly.getEngine("openai");
  const plugin = await makeSDKExamplesPlugin(engine, { root: process.cwd() });
  const content = {
    path: "./test.txt",
    name: "test.txt",
    prompt: "Create an example using cloudwatch logs that includes pagination.",
  };
  await plugin.augment(content);
  console.log(content.prompt);
  console.table(
    content.augment.map(({ score, language, name }) => ({
      score,
      language,
      name,
    }))
  );
}

if (process.argv[1] === fileURLToPath(import.meta.url)) {
  await cli();
}
