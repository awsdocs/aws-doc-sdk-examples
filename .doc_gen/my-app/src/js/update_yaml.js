const fs = require('fs');
const insertLine = require('insert-line')
const YAML = require('json-to-pretty-yaml');

var downloadFolder = process.env.USERPROFILE + "\\Downloads"
var destFolder = "..\\metadata\\"
const origJson = "..\\jsonholder\\";
const json = require(downloadFolder +"\\" + process.argv[2] +"_metadata.json");
const yaml = require('js-yaml');
const Json = JSON.stringify(json, null, 2);
const fileName =  process.argv[2] +"_metadata.yaml"
const data = YAML.stringify(json).replace(/['"]+/g, '').replace(/': ""+/g, ': ').replace(/': "undefined"+/g, ': ' );
console.log('data', data)
fs.writeFile(destFolder + fileName, data, function(err) {
  if(err) {
    return console.log("error here", err);
  }
  console.log("Your metadata has been successfully updated.");
  fs.writeFile(origJson + process.argv[2] +"_metadata.json", Json, function(err){
    insertLine('../../origJson + process.argv[2] +"_metadata.json').prepend("# zexi 0.4.0");
    if(err) {
      return console.log("error here", err);
    }
    console.log("You can now make more changes using the SoS editor.");
  });
  try {
    fs.unlinkSync(downloadFolder + "\\" + process.argv[2] +"_metadata.json")

    //file removed

  } catch(err) {
    console.error('Cant remove file',err)
  }
});
//Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
