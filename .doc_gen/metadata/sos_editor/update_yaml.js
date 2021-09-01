const fs = require('fs');
const YAML = require('json-to-pretty-yaml');
var downloadFolder = process.env.USERPROFILE + "\\Downloads"
var destFolder = "..\\"
const json = require(downloadFolder +"\\" + process.argv[2] +"_metadata.json");
const fileName =  process.argv[2] +"_metadata.yaml"
const data = YAML.stringify(json).replace(/['"]+/g, '');


fs.writeFile(destFolder + fileName, data, function(err) {
  if(err) {
    return console.log("error here", err);
  }
  console.log("Your metadata has been successfully updated.");
  try {
    fs.unlinkSync(downloadFolder + "\\" + process.argv[2] +"_metadata.json")
    //file removed

  } catch(err) {
    console.error('Cant remove file',err)
  }
});




//Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
