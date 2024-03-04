import {fileURLToPath} from "url";
import * as yaml from "js-yaml";
import * as fs from "fs";
import * as YAML from "json-to-pretty-yaml";
import {promptForText, promptToContinue} from "../libs/utils/util-io.js";
import {wrapText} from "../libs/utils/util-string.js";

// Standard waiting function.
function wait(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
};

// Recursive function to get files
function getFiles(dir, files = []) {
    // Get an array of all files and directories in the passed directory using fs.readdirSync
    const fileList = fs.readdirSync(dir)
    // Create the full path of the file/directory by concatenating the passed directory and file/directory name
    for (const file of fileList) {
        const name = `${dir}/${file}`
        // Check if the current file/directory is a directory using fs.statSync
        if (fs.statSync(name).isDirectory()) {
            // If it is a directory, recursively call the getFiles function with the directory path and the files array
            getFiles(name, files)
        } else {
            // If it is a file, push the full path to the files array
            files.push(name)
        }
    }
    return files
}

// Convert yaml to Json
const create_json = async () => {
    // Check if service metadata file exists
    const serviceStub = await promptForText(
        "Please enter the service stub for Principle Service used in the example you're adding/updating metadata for:"
    );
    // Get document, or throw exception on error
    try {
        var doc = yaml.load(
            fs.readFileSync(
                "../../" + serviceStub + "_metadata.yaml",
                "utf8",
            ).replaceAll(/{+/g, "'{").replace(/}+/g, "}'")
        );
        wait(3000)
        /*console.log("doc", doc)*/
        var mydoc = JSON.stringify(doc, null, 2);
        if(mydoc.includes("synopsis_list")) {
            /*console.log("mydoc", mydoc)*/
            fs.writeFileSync(
                "../jsonholder/" + serviceStub + "_metadata.json",
                mydoc,
                function (err) {
                    if (err) throw err;
                }
            );
            console.log("Open the SOS GUI editor (./doc_gen/metadata/sos_editor/index.html) in your local host and enter \'" + serviceStub + "\' in the Principle Service field. Please leave this terminal running.");
            return serviceStub
        }
        else{
            var doc = yaml.load(
                fs.readFileSync(
                    "../../" + serviceStub + "_metadata.yaml",
                    "utf8",
                ).replaceAll(/{+/g, "'{").replace(/}+/g, "}'").replaceAll('category:','synopsis_list:\n  category:')
            );
            wait(3000)
            /*console.log("doc", doc)*/
            const mydoc = JSON.stringify(doc, null, 2);
            /*console.log("mydoc", mydoc)*/
            fs.writeFileSync(
                "../jsonholder/" + serviceStub + "_metadata.json",
                mydoc,
                function (err) {
                    if (err) throw err;
                }
            );
            console.log("Open the SOS GUI editor (./doc_gen/metadata/sos_editor/index.html) in your local host and enter \'" + serviceStub + "\' in the Principle Service field. Please leave this terminal running.");
            return serviceStub
        }
    } catch (e) {
        console.log(e + "\n" + serviceStub + "_metadata.yaml does not exist in the \/metadata folder.")

        const filesInTheFolder = getFiles('../../../metadata/');;
        console.log('Here\'s a list of the existing metadata files\n');
        console.log(filesInTheFolder);
        const answer = await promptForText(
            "Do you want to create " + serviceStub + "_metadata.yaml? (yes/no)"
        );
        if (answer === "yes") {
            const my_json = ("# zexi 0.4.0\n").toString();
            fs.writeFile("../../../metadata/" + serviceStub + "_metadata.yaml", my_json, function (err) {
                if (err) {
                    return console.log("error here", err);
                }
                console.log("File created. Enter "/" + serviceStub  + "/" to continue.");
            });

        } else {
            return
        }
    }
};

//Convert edited Json back to YAML
const updateYAML = async (serviceName) => {
    const answer = await promptForText(
        "When finished editing the metadata return to this terminal, and enter 'yes' below."
    );
    if (answer === "yes") {
        const downloadFolder = "/Users/tkhill/Downloads"
        const destFolder = "..\/..\/..\/metadata\/"
        const origJson = "..\/jsonholder\/";
        const my_json = fs.readFileSync(
            downloadFolder + "\/" + serviceName + "_metadata.json",
            "utf8"
        );
        const json = JSON.parse(my_json);
        const Json = JSON.stringify(json, null, 2);
        const fileName = serviceName + "_metadata.yaml"
        const data = YAML.stringify(json).replace(/['"]+/g, '').replace(/': ""+/g, ': ').replace(/': "undefined"+/g, ': ');
        fs.writeFile(destFolder + fileName, data, function (err) {
            if (err) {
                return console.log("error here", err);
            }
            console.log("Your metadata has been successfully updated.");
            fs.writeFile(origJson + serviceName + "_metadata.json", Json, function (err) {
                if (err) {
                    return console.log("error here", err);
                }
                console.log("You can now make more changes using the SoS editor.");
            });
            try {
                fs.unlinkSync(downloadFolder + "\/" + serviceName + "_metadata.json")

                //file removed

            } catch (err) {
                console.error('Cant remove file', err)
            }
        })
    } else {
        console.log('Enter some metadata!')
    }
};

// Control interactivity in CLI.
const main = async () => {
    try {
        console.log(wrapText("Welcome to the Code Example Metadata GUI editor. Let's write some metadata."));
        const serviceStub = await create_json();
        if (serviceStub != undefined) {
            //await promptToContinue();
            await updateYAML(
                serviceStub
            );
            await promptToContinue();
            console.log(
                "Go to UI.")
        } else {
            await main();
        }
    } catch (err) {
        console.error(err);
    }
};
// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
    main();
}

//Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
