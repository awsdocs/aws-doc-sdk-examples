const yaml = require("js-yaml");
const fs = require("fs");

const run = async () => {
  // Check if service metadata file exists
  const serviceStub = process.argv[2];
  // Get document, or throw exception on error
    let myArray = [];
    try {
      const doc = yaml.load(
          fs.readFileSync(
              "../" + serviceStub + "_metadata.yaml",
              "utf8"
          )
      );
      try {
        const mydoc = JSON.stringify(doc, null, 2);
        fs.writeFileSync(
            "./jsonholder/" + serviceStub + "_metadata.json",
            mydoc,
            function (err) {
              if (err) throw err;
            }
        );
        console.log("Success. " + serviceStub + "_metadata.json created successfully. You can now edit 'sns' metadata in the SoS editor.");

      } catch (e) {
        console.log("Success. " + serviceStub + "_metadata.yaml does not exist in the \/metadata folder. Create it.")
      }
    }
     catch (err) {
      console.log("Unsuccessful. " + serviceStub + "_metadata.yaml does not exist in the \/metadata folder. Create it and try again.")
    }
};
run();

/*
const secondrun = async () => {
  try {
    let myArray1 = [];
    const doc1 = yaml.load(
      fs.readFileSync("../../metadata/mapping.yaml", "utf8")
    );
    const mydoc1 = JSON.stringify(doc1);
    fs.writeFile("./jsonholder/mapping.json", mydoc1, function (err) {
      if (err) throw err;
      console.log("mapping.json file created successfully.");
    });
  } catch (e) {
    console.log(e);
  }
};



const yaml = require("js-yaml");
const fs = require("fs");
const { parseAllDocuments, parseDocument } = require("yaml");

const run = async () => {
  try {
    const file   = fs.readFileSync("../../metadata/sns_metadata.yaml", "utf8");
    const doc = parseDocument(file, {lineCounter: true});
    const result = doc.toJSON();
    console.log(result)
  } catch (err) {
    console.log("something happened");
  }
};
run();
*/


//Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
