import {fileURLToPath} from "url";
import * as yaml from "js-yaml";
import * as fs from "fs";
import * as YAML from "json-to-pretty-yaml";
import { promptForText, promptToContinue } from "../libs/utils/util-io.js";
import { wrapText } from "../libs/utils/util-string.js";
import {JSON_SCHEMA} from "js-yaml";

function wait(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

const createJson = async () => {
    // Check if service metadata file exists
    const serviceStub = await promptForText(
        "Enter the service stub."
    );
    // Get document, or throw exception on error
    try {
        const doc = yaml.load(
            fs.readFileSync(
                "../../" + serviceStub + "_metadata.yaml",
                "utf8",
            ).replaceAll(/{+/g,"'{" ).replace(/}+/g,"}'" )
        );
        console.log("doc, ", doc)
        wait(3000)
        const mydoc = JSON.stringify(doc, null, 2);
        fs.writeFileSync(
            "../jsonholder/" + serviceStub + "_metadata.json",
            mydoc,
            function (err) {
                if (err) throw err;
            }
        );
        console.log("Success. " + serviceStub + "_metadata.json created successfully. You can now edit 'sns' metadata in the SoS editor.");
        return serviceStub
    } catch (e) {
        console.log(e + "Unsuccessful. " + serviceStub + "_metadata.yaml may not exist in the \/metadata folder, or if it does, it does not have a blank line at the start of it.")
    }
};

const updateYAML = async (serviceName) => {
    const answer = await promptForText(
        "Have you finished entering metadata? (yes)"
    );
    if (answer === "yes") {
    const downloadFolder = process.env.USERPROFILE + "\\Downloads"
    const destFolder = "..\\..\\"
    const origJson = "..\\jsonholder\\";
    const my_json = fs.readFileSync(
        downloadFolder + "\\" + serviceName + "_metadata.json",
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
            fs.unlinkSync(downloadFolder + "\\" + serviceName + "_metadata.json")

            //file removed

        } catch (err) {
            console.error('Cant remove file', err)
        }
    })
}
    else{
        console.log('Enter some metadata!')
    }
};

const main = async () => {
    try {
        console.log(wrapText("Welcome."));
        console.log("Let's write some metadata.");
        const serviceStub = await createJson();
        await promptToContinue();

        console.log(
            "Go to UI."
        );
        await updateYAML(
            serviceStub
        );
        await promptToContinue();
        console.log(
            "Go to UI.")
    } catch (err) {
        console.error(err);
    }
};


// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
    main();
}

//Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. SPDX-License-Identifier: Apache-2.0
