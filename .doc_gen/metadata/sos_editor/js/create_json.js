const yaml = require("js-yaml");
const YAML = require('yaml')
const fs = require("fs");
function wait(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}
const run = async () => {
    // Check if service metadata file exists
    const serviceStub = process.argv[2];
    // Get document, or throw exception on error
    let myArray = [];
    try {
        const doc = yaml.load(
            fs.readFileSync(
                "../../" + serviceStub + "_metadata.yaml",
                "utf8"
            ).replace(/{/,"'{" ).replace(/}/,"}'" )
        );
        console.log("doc, ", doc)
        wait(3000)
        const mydoc = JSON.stringify(doc, null, 2);
        wait(3000)
        fs.writeFileSync(
            "../jsonholder/" + serviceStub + "_metadata.json",
            mydoc,
            function (err) {
                if (err) throw err;
            }
        );
        console.log("Success. " + serviceStub + "_metadata.json created successfully. You can now edit 'sns' metadata in the SoS editor.");

    } catch (e) {
        console.log(e + "Unsuccessful. " + serviceStub + "_metadata.yaml does not exist in the \/metadata folder. Create it.")
    }
};
run();
