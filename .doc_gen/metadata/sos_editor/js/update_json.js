// Download json file.
// noinspection JSAnnotator

function download1(filename, finalObject2) {
    console.log('download1' )
    var element = document.createElement('a');
    element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(finalObject2));
    element.setAttribute('download', filename);
    element.style.display = 'none';
    document.body.appendChild(element);
    element.click();
    document.body.removeChild(element);
};

function download(myObj2, serviceValue) {
    console.log('download')
    const finalObject = JSON.stringify(myObj2);
    const finalObject1 = finalObject.replaceAll("\\\"", "").replaceAll('\\n', '\",\"').replaceAll(':null', ':" "').replaceAll("[[","[").replaceAll("]]","]");
    const filename = serviceValue + "_metadata.json";
    var f = new File([myObj2], filename, {type: "text/plain"});
    var element = document.createElement('a');
    element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(finalObject1));
    element.setAttribute('download', filename);
    element.style.display = 'none';
    document.body.appendChild(element);
    element.click();
    document.body.removeChild(element);
};

// Update JSON based on user input.
function update_json() {
    console.log('update_json running')
    const noOfSnippets = document.getElementsByClassName('snippdisc').length;
    const noOfSnippetFiles = document.getElementsByClassName('snippfiledisc').length;
    if(noOfSnippets == 0 && noOfSnippetFiles == 0){
        alert('You must enter at least one snippet file or snippet tag.')
        return
    }
    // Create variables.
    const serviceValue = document.getElementById('selecttheservice').value
    console.log("serviceValue", serviceValue)
    const firstServiceValue = "\"" + serviceValue + "\"";
    let addServicesArray = [];
    addServicesArray.push(firstServiceValue);
    var apiCommands = document.getElementById('commands').value
    console.log("apiCommands", apiCommands);
    apiCommands = "{" + apiCommands + "}";
    console.log("apiCommands", apiCommands);
    var addServices = document.getElementById('selecttheservices').value;
        console.log("addServices", addServices);
        if(addServices !== ""){
            var addServices = document.getElementById('selecttheservices').value.replaceAll(" ", "").split(",");
        for (let i = 0; i < addServices.length; i++) {
            addServicesArray.push(addServices[i]);
        }
        const finalAddServices = JSON.stringify(addServices).replaceAll("[", "").replaceAll("]", "");
        console.log('finalAddServices', finalAddServices);
        let arrayAPIs = [];
        arrayAPIs.push(apiCommands)
        console.log('addServicesArray', addServicesArray);
        for (let i = 0; i < addServicesArray.length - 1; i++) {
            const myapi = "addServiceAPI" + i
            console.log('myapi', myapi)
            var api = document.getElementById(myapi).value
            api = "{" + api + "}";
            arrayAPIs.push(api)
            console.log("arrayAPIs", arrayAPIs);
        }
        var serviceEntry = {},
            j
        for (let j = 0; j < addServicesArray.length; j++) {
            serviceEntry[addServicesArray[j]] = arrayAPIs[j];
        }
    }
        else{
            console.log('addServicesArray', addServicesArray);
            console.log('apiCommands', apiCommands);
            var serviceEntry = {};
            var thearray = serviceValue.split(',');
            serviceEntry[thearray] =apiCommands;
            console.log('serviceEntry', serviceEntry);
        }
    console.log("serviceEntry", serviceEntry)
    const blockName = document.getElementById('blockname').value
    console.log('blockName', blockName)
    const blockValue = document.getElementById('selectBlock').value
    console.log("blockValue", blockValue);
    const titleValue = document.getElementById('thetitle').value
    console.log("titleValue", titleValue)
    const abbrevTitleValue = document.getElementById('addatitle').value
    console.log("abbrevTitleValue", abbrevTitleValue)
    const synopsisValue = document.getElementById('synopsis').value
    console.log("synopsisValue", synopsisValue)
    var synopsisListValue = document.getElementById('synopsislist').value
    console.log("synopsisListValue", synopsisListValue)

    const categoryValue = document.getElementById('category').value
    console.log("categoryValue", categoryValue)
    const languageValue = document.getElementById('languages').value
    console.log("languageValue", languageValue)
    const gitHubValue = document.getElementById('githublink').value
    console.log("gitHubValue", gitHubValue)
    const sdkVersion = document.getElementById('sdkVersion').value
    console.log("sdkVersion", sdkVersion)
    const snippetGuideValue = document.getElementById('sdkguidelink').value
    console.log("snippetGuideValue", snippetGuideValue)
    const addNewLanguage = document.getElementById('addlanguage').value
    console.log("addNewLanguage", addNewLanguage)
    const addedSDKVersion = document.getElementById('addskdversion').value
    console.log("addedSDKVersion", addedSDKVersion);
    var elements = document.getElementsByClassName('snipptag');
    for (var i = 1; i < elements.length; i++) {
        const snippettag = "snippettag" + i
        console.log('this snippet tag', snippettag)

        if (document.getElementById(snippettag).value == 0) {
            alert('You have empty snippet tags. This breaks the build.')
            return
        }
    }
    // Create a new block.
    if (blockValue == "Not Listed") {
        console.log("Creating an new block.")
        var xmlhttp = new XMLHttpRequest();
        xmlhttp.onload = function () {
            var myObj = JSON.parse(this.responseText);
            var myObj1 = JSON.parse(this.responseText);
            console.log('whats this', myObj);
            const addedSDKVersionFloat = parseFloat(addedSDKVersion);
            const noOfSnippets = document.getElementsByClassName('snippdisc').length;
            console.log('noOfSnippets', noOfSnippets)
            const noOfSnippetFiles = document.getElementsByClassName('snippfiledisc').length;
            console.log('noOfSnippetFiles', noOfSnippetFiles)
            const editedJson =
                {
                    [blockName]:{
                        title: titleValue,
                        title_abbrev: abbrevTitleValue,
                        synopsis: synopsisValue,
                        synopsis_list: [synopsisListValue],
                        category: categoryValue,
                        languages: {
                            [languageValue]: {
                                versions: [
                                    {
                                        sdk_version: addedSDKVersionFloat,
                                        github: gitHubValue,
                                        sdkguide: snippetGuideValue,
                                        excerpts: []
                                    }
                                ]
                            }
                        },
                        services: serviceEntry
                    }
                }
                console.log('editedJson', editedJson)
                if (noOfSnippetFiles === 1) {
                var snippetfiledescription1 = document.getElementById("snippetfiledescription1").value;
                var snippetfile1 = document.getElementById("snippetfile1").value;
                    console.log('1 snippet file');
                    const snippetFileInfo = {
                        description: snippetfiledescription1,
                        snippet_files: [
                            snippetfile1
                        ]
                    }
                    myObj = editedJson;

                    myObj[blockName]["languages"][languageValue]["versions"][0]["excerpts"].push(snippetFileInfo);

                    const myObj2 = Object.assign({}, myObj, myObj1);;
                    download(myObj2, serviceValue);
                     alert("Please return to the terminal to save your changes.")
            }
                if (noOfSnippetFiles === 2) {
                    console.log('2 snippet file');
                    var snippetfiledescription1 = document.getElementById("snippetfiledescription1").value;
                    var snippetfile1 = document.getElementById("snippetfile1").value;
                    var snippetfiledescription2 = document.getElementById("snippetfiledescription2").value;
                    var snipfile2 = document.getElementById("snippetfile2").value;
                    var snippetFileInfo = [{
                            description: snippetfiledescription1,
                            snippet_files: [
                                snippetfile1
                            ]
                        },
                        {
                            description: snippetfiledescription2,
                            snippet_files: [
                                snipfile2
                            ]
                        }]
                    myObj = editedJson;
                    myObj[blockName]["languages"][languageValue]["versions"][0]["excerpts"].push(snippetFileInfo);
                    download(myObj, serviceValue);
                    alert("Please return to the terminal to save your changes.")
                }
                if (noOfSnippetFiles === 3) {
                    var snippetfiledescription1 = document.getElementById("snippetfiledescription1").value;
                    var snippetfile1 = document.getElementById("snippetfile1").value;
                    var snippetfiledescription2 = document.getElementById("snippetfiledescription2").value;
                    var snipfile2 = document.getElementById("snippetfile2").value;
                    var snippetfiledescription3 = document.getElementById("snippetfiledescription3").value;
                    var snipfile3 = document.getElementById("snippetfile3").value;
                    const snippetFileInfo = [{
                        description: snippetfiledescription1,
                        snippet_files: [
                            snippetfile1
                        ]
                    },
                        {
                            description: snippetfiledescription2,
                            snippet_files: [
                                snipfile2
                            ]
                        },
                        {
                            description: snippetfiledescription3,
                            snippet_files: [
                                snipfile3
                            ]
                        }]
                    myObj = editedJson;
                    myObj[blockName]["languages"][languageValue]["versions"][0]["excerpts"].push(snippetFileInfo);
                    download(myObj, serviceValue);
                    alert("Please return to the terminal to save your changes.")
                }
                if (noOfSnippetFiles === 4) {
                    var snippetfiledescription1 = document.getElementById("snippetfiledescription1").value;
                    var snippetfile1 = document.getElementById("snippetfile1").value;
                    var snippetfiledescription2 = document.getElementById("snippetfiledescription2").value;
                    var snipfile2 = document.getElementById("snippetfile2").value;
                    var snippetfiledescription3 = document.getElementById("snippetfiledescription3").value;
                    var snipfile3 = document.getElementById("snippetfile3").value;
                    var snippetfiledescription4 = document.getElementById("snippetfiledescription3").value;
                    var snipfile4 = document.getElementById("snippetfile3").value;
                    const snippetFileInfo = [{
                        description: snippetfiledescription1,
                        snippet_files: [
                            snippetfile1
                        ]
                    },
                        {
                            description: snippetfiledescription2,
                            snippet_files: [
                                snipfile2
                            ]
                        },
                        {
                            description: snippetfiledescription3,
                            snippet_files: [
                                snipfile3
                            ]
                        },
                        {
                            description: snippetfiledescription4,
                            snippet_files: [
                                snipfile4
                            ]
                        }]
                    myObj = editedJson;
                    myObj[blockName]["languages"][languageValue]["versions"][0]["excerpts"].push(snippetFileInfo);
                    download(myObj, serviceValue);
                    alert("Please return to the terminal to save your changes.")
                }
                if (noOfSnippetFiles === 5) {
                    var snippetfiledescription1 = document.getElementById("snippetfiledescription1").value;
                    var snippetfile1 = document.getElementById("snippetfile1").value;
                    var snippetfiledescription2 = document.getElementById("snippetfiledescription2").value;
                    var snipfile2 = document.getElementById("snippetfile2").value;
                    var snippetfiledescription3 = document.getElementById("snippetfiledescription3").value;
                    var snipfile3 = document.getElementById("snippetfile3").value;
                    var snippetfiledescription4 = document.getElementById("snippetfiledescription3").value;
                    var snipfile4 = document.getElementById("snippetfile3").value;
                    var snippetfiledescription5 = document.getElementById("snippetfiledescription5").value;
                    var snipfile5 = document.getElementById("snippetfile5").value;
                    const snippetFileInfo = [{
                        description: snippetfiledescription1,
                        snippet_files: [
                            snippetfile1
                        ]
                    },
                        {
                            description: snippetfiledescription2,
                            snippet_files: [
                                snipfile2
                            ]
                        },
                        {
                            description: snippetfiledescription3,
                            snippet_files: [
                                snipfile3
                            ]
                        },
                        {
                            description: snippetfiledescription4,
                            snippet_files: [
                                snipfile4
                            ]
                        },
                        {
                            description: snippetfiledescription5,
                            snippet_files: [
                                snipfile5
                            ]
                        }]
                    myObj = editedJson;
                    myObj[blockName]["languages"][languageValue]["versions"][0]["excerpts"].push(snippetFileInfo);
                    download(myObj, serviceValue);
                  alert("Please return to the terminal to save your changes.")
                }
                if (noOfSnippetFiles === 6) {
                    var snippetfiledescription1 = document.getElementById("snippetfiledescription1").value;
                    var snipfile1 = document.getElementById("snippetfile1").value;
                    var snippetfiledescription2 = document.getElementById("snippetfiledescription2").value;
                    var snipfile2 = document.getElementById("snippetfile2").value;
                    var snippetfiledescription3 = document.getElementById("snippetfiledescription3").value;
                    var snipfile3 = document.getElementById("snippetfile3").value;
                    var snippetfiledescription4 = document.getElementById("snippetfiledescription3").value;
                    var snipfile4 = document.getElementById("snippetfile3").value;
                    var snippetfiledescription5 = document.getElementById("snippetfiledescription5").value;
                    var snipfile5 = document.getElementById("snippetfile5").value;
                    var snippetfiledescription5 = document.getElementById("snippetfiledescription6").value;
                    var snipfile5 = document.getElementById("snippetfile6").value;
                    const snippetFileInfo = [{
                        description: snippetfiledescription1,
                        snippet_files: [
                            snipfile1
                        ]
                    },
                        {
                            description: snippetfiledescription2,
                            snippet_files: [
                                snipfile2
                            ]
                        },
                        {
                            description: snippetfiledescription3,
                            snippet_files: [
                                snipfile3
                            ]
                        },
                        {
                            description: snippetfiledescription4,
                            snippet_files: [
                                snipfile4
                            ]
                        },
                        {
                            description: snippetfiledescription5,
                            snippet_files: [
                                snipfile5
                            ]
                        },
                        {
                            description: snippetfiledescription6,
                            snippet_files: [
                                snipfile6
                            ]
                        }]
                    myObj = editedJson;
                    myObj[blockName]["languages"][languageValue]["versions"][0]["excerpts"].push(snippetFileInfo);
                    download(myObj, serviceValue);
                   alert("Please return to the terminal to save your changes.")
                }
                else if (noOfSnippets === 1) {
                console.log('1 snippet');
                var snippetDescValue = document.getElementById('snippetdescription1').value;
                var snippetTagValue = document.getElementById('snippettag1').value;
                const snippetInfo = {
                    description: snippetDescValue,
                    snippet_tags: [
                        snippetTagValue
                    ]
                }
                myObj = editedJson;
                myObj[blockName]["languages"][languageValue]["versions"][0]["excerpts"].push(snippetInfo);
                const myObj2 = Object.assign({}, myObj, myObj1);;
                download(myObj2, serviceValue);
                alert("Please return to the terminal to save your changes.")
            }
                else if (noOfSnippets === 2) {
                var snippetDescValue = document.getElementById('snippetdescription1').value;
                var snippetTagValue = document.getElementById('snippettag1').value;
                var snipdisc2 = document.getElementById("snippetdescription2").value;
                var sniptag2 = document.getElementById("snippettag2").value;
                console.log('this one')
                let snippetInfo = [{
                    description: snippetDescValue,
                    snippet_tags: [
                        snippetTagValue
                        ]
                },
                    {
                        description: snipdisc2,
                        snippet_tags: [
                            sniptag2
                        ]
                    }]
                myObj = editedJson;
                myObj[blockName]["languages"][languageValue]["versions"][0]["excerpts"].push(snippetInfo);
                const myObj2 = Object.assign({}, myObj, myObj1);;
                download(myObj2, serviceValue);
                alert("Please return to the terminal to save your changes.")
            }
                else if (noOfSnippets === 3) {
                console.log('x3 snippet tags')
                var snippetDescValue = document.getElementById('snippetdescription1').value;
                var snippetTagValue = document.getElementById('snippettag1').value;
                var snipdisc2 = document.getElementById("snippetdescription2").value;
                var sniptag2 = document.getElementById("snippettag2").value;
                var snipdisc3 = document.getElementById("snippetdescription3").value;
                var sniptag3 = document.getElementById("snippettag3").value;
                let snippetInfo = [{
                        description: snippetDescValue,
                        snippet_tags: [
                            snippetTagValue
                        ]
                    },
                    {
                        description: snipdisc2,
                        snippet_tags: [
                            sniptag2
                        ]
                    },
                    {
                        description: snipdisc3,
                        snippet_tags: [
                            sniptag3
                        ]
                    }]
                myObj = editedJson;
                myObj[blockName]["languages"][languageValue]["versions"][0]["excerpts"].push(snippetInfo);
                const myObj2 = Object.assign({}, myObj, myObj1);;
                download(myObj2, serviceValue);
                alert("Please return to the terminal to save your changes.")
            }
                else if (noOfSnippets === 4) {
                var snippetDescValue = document.getElementById('snippetdescription1').value;
                var snippetTagValue = document.getElementById('snippettag1').value;
                var snipdisc2 = document.getElementById("snippetdescription2").value;
                var sniptag2 = document.getElementById("snippettag2").value;
                var snipdisc3 = document.getElementById("snippetdescription3").value;
                var sniptag3 = document.getElementById("snippettag3").value;
                var snipdisc4 = document.getElementById("snippetdescription4").value;
                var sniptag4 = document.getElementById("snippettag4").value;
                let snippetInfo = [{
                        description: snippetDescValue,
                        snippet_tags: [
                            snippetTagValue
                        ]
                    },
                    {
                        description: snipdisc2,
                        snippet_tags: [
                            sniptag2
                        ]
                    },
                    {
                        description: snipdisc3,
                        snippet_tags: [
                            sniptag3
                        ]
                    },
                    {
                        description: snipdisc4,
                        snippet_tags: [
                            sniptag4
                        ]
                    }]
                myObj = editedJson;
                myObj[blockName]["languages"][languageValue]["versions"][0]["excerpts"].push(snippetInfo);
                const myObj2 = Object.assign({}, myObj, myObj1);;
                download(myObj2, serviceValue);
                alert("Please return to the terminal to save your changes.")
            }
                else if (noOfSnippets === 5) {
                var snippetDescValue = document.getElementById('snippetdescription1').value;
                var snippetTagValue = document.getElementById('snippettag1').value;
                var snipdisc2 = document.getElementById("snippetdescription2").value;
                var sniptag2 = document.getElementById("snippettag2").value;
                var snipdisc3 = document.getElementById("snippetdescription3").value;
                var sniptag3 = document.getElementById("snippettag3").value;
                var snipdisc4 = document.getElementById("snippetdescription4").value;
                var sniptag4 = document.getElementById("snippettag4").value;
                var snipdisc5 = document.getElementById("snippetdescription5").value;
                var sniptag5 = document.getElementById("snippettag5").value;
                let snippetInfo = [{
                        description: snippetDescValue,
                        snippet_tags: [
                            snippetTagValue
                        ]
                    },
                    {
                        description: snipdisc2,
                        snippet_tags: [
                            sniptag2
                        ]
                    },
                    {
                        description: snipdisc3,
                        snippet_tags: [
                            sniptag3
                        ]
                    },
                    {
                        description: snipdisc4,
                        snippet_tags: [
                            sniptag4
                        ]
                    },
                    {
                        description: snipdisc5,
                        snippet_tags: [
                            sniptag5
                        ]
                    }]
                myObj = editedJson;
                myObj[blockName]["languages"][languageValue]["versions"][0]["excerpts"].push(snippetInfo);
                const myObj2 = Object.assign({}, myObj, myObj1);;
                download(myObj2, serviceValue);
                alert("Please return to the terminal to save your changes.")
            }
                else if (noOfSnippets === 6) {
                var snippetDescValue = document.getElementById('snippetdescription1').value;
                var snippetTagValue = document.getElementById('snippettag1').value;
                var snipdisc2 = document.getElementById("snippetdescription2").value;
                var sniptag2 = document.getElementById("snippettag2").value;
                var snipdisc3 = document.getElementById("snippetdescription3").value;
                var sniptag3 = document.getElementById("snippettag3").value;
                var snipdisc4 = document.getElementById("snippetdescription4").value;
                var sniptag4 = document.getElementById("snippettag4").value;
                var snipdisc5 = document.getElementById("snippetdescription5").value;
                var sniptag5 = document.getElementById("snippettag5").value;
                var snipdisc6 = document.getElementById("snippetdescription6").value;
                var sniptag6 = document.getElementById("snippettag6").value;
                let snippetInfo = [{
                        description: snippetDescValue,
                        snippet_tags: [
                            snippetTagValue
                        ]
                    },
                    {
                        description: snipdisc2,
                        snippet_tags: [
                            sniptag2
                        ]
                    },
                    {
                        description: snipdisc3,
                        snippet_tags: [
                            sniptag3
                        ]
                    },
                    {
                        description: snipdisc4,
                        snippet_tags: [
                            sniptag4
                        ]
                    },
                    {
                        description: snipdisc5,
                        snippet_tags: [
                            sniptag5
                        ]
                    },
                    {
                        description: snipdisc6,
                        snippet_tags: [
                            sniptag6
                        ]
                    }]
                myObj = editedJson;
                myObj[blockName]["languages"][languageValue]["versions"][0]["excerpts"].push(snippetInfo);
                const myObj2 = Object.assign({}, myObj, myObj1);;
                download(myObj2, serviceValue);
                alert("Please return to the terminal to save your changes.")
            }
        }
        const serviceStub = document.getElementById('selecttheservice').value
        const sourceJson = "../sos_editor/jsonholder/" + serviceStub + "_metadata.json"
        xmlhttp.open("GET", sourceJson, true);
        xmlhttp.send();
    }
    //Add a new language.
    else if (document.getElementById('languages').value == "Not Listed") {
        console.log('languages not listed');
        const addedSDKVersionFloat = parseFloat(addedSDKVersion)
        var xmlhttp = new XMLHttpRequest();
        xmlhttp.onload = function () {
            var myObj = JSON.parse(this.responseText);
            var myObj = myObj;
            myObj[blockValue].title = titleValue;
            myObj[blockValue].title_abbrev = abbrevTitleValue;
            myObj[blockValue].synopsis = synopsisValue;
            myObj[blockValue].synopsis_list = synopsisListValue;
            myObj[blockValue].category = categoryValue;
            myObj[blockValue].services = serviceEntry;
            const noOfSnippets = document.getElementsByClassName('snippdisc').length
            const noOfSnippetFiles = document.getElementsByClassName('snippfiledisc').length;
            if (noOfSnippets === 1) {
                const snippetDescValue = document.getElementById('snippetdescription1').value
                const snippetTagValue = document.getElementById('snippettag1').value
                const editedJson =
                    {
                        versions: [
                            {
                                sdk_version: addedSDKVersionFloat,
                                github: gitHubValue,
                                sdkguide: snippetGuideValue,
                                excerpts: [{
                                    description: snippetDescValue,
                                    snippet_tags: [
                                        snippetTagValue
                                    ]

                                }]
                            }]
                    }
                myObj[blockValue]["languages"][addNewLanguage] = editedJson;
                const finalObject = JSON.stringify(myObj);
                const finalObject1 = finalObject.replaceAll(':null', ':" "');
                const filename = serviceValue + "_metadata.json"
                download1(filename, finalObject1)
                alert("Please return to the terminal to save your changes.")
            }
            if (noOfSnippets === 2) {
                const snippetDescValue = document.getElementById('snippetdescription1').value
                const snippetTagValue = document.getElementById('snippettag1').value
                const snipdisc21 = document.getElementById("snippetdescription2").value
                const sniptag21 = document.getElementById("snippettag2").value
                const editedJson =
                    {
                        versions: [
                            {
                                sdk_version: addedSDKVersionFloat,
                                github: gitHubValue,
                                sdkguide: snippetGuideValue,
                                excerpts: [{
                                    description: snippetDescValue,
                                    snippet_tags: [
                                        snippetTagValue
                                    ]
                                },
                                    {
                                        description: snipdisc21,
                                        snippet_tags: [
                                            sniptag21
                                        ]

                                    }]
                            }
                        ]
                    }


                myObj[blockValue]["languages"][addNewLanguage] = editedJson;
                const finalObject = JSON.stringify(myObj);
                const finalObject1 = finalObject.replaceAll(':null', ':" "');
                const filename = serviceValue + "_metadata.json"
                download1(filename, finalObject1)
                alert("Please return to the terminal to save your changes.")
            }
            if (noOfSnippets === 3) {
                const snippetDescValue = document.getElementById('snippetdescription1').value
                const snippetTagValue = document.getElementById('snippettag1').value
                const snipdisc21 = document.getElementById("snippetdescription2").value
                const sniptag21 = document.getElementById("snippettag2").value
                const snipdisc31 = document.getElementById("snippetdescription3").value
                const sniptag31 = document.getElementById("snippettag3").value

                const editedJson =
                    {
                        versions: [
                            {
                                sdk_version: addedSDKVersionFloat,
                                github: gitHubValue,
                                sdkguide: snippetGuideValue,
                                excerpts: [{
                                    description: snippetDescValue,
                                    snippet_tags: [
                                        snippetTagValue
                                    ]
                                },
                                    {
                                        description: snipdisc21,
                                        snippet_tags: [
                                            sniptag21
                                        ]

                                    },
                                    {
                                        description: snipdisc31,
                                        snippet_tags: [
                                            sniptag31
                                        ]

                                    }]
                            }
                        ]
                    }

                myObj[blockValue]["languages"][addNewLanguage] = editedJson;
                const finalObject = JSON.stringify(myObj);
                const finalObject1 = finalObject.replaceAll(':null', ':" "');
                const filename = serviceValue + "_metadata.json"
                download1(filename, finalObject1)
                alert("Please return to the terminal to save your changes.")
            }
            if (noOfSnippets === 4) {
                const snippetDescValue = document.getElementById('snippetdescription1').value
                const snippetTagValue = document.getElementById('snippettag1').value
                const snipdisc21 = document.getElementById("snippetdescription2").value
                const sniptag21 = document.getElementById("snippettag2").value
                const snipdisc31 = document.getElementById("snippetdescription3").value
                const sniptag31 = document.getElementById("snippettag3").value
                const snipdisc41 = document.getElementById("snippetdescription4").value
                const sniptag41 = document.getElementById("snippettag4").value

                const editedJson =
                    {
                        versions: [
                            {
                                sdk_version: addedSDKVersionFloat,
                                github: gitHubValue,
                                sdkguide: snippetGuideValue,
                                excerpts: [{
                                    description: snippetDescValue,
                                    snippet_tags: [
                                        snippetTagValue
                                    ]
                                },
                                    {
                                        description: snipdisc21,
                                        snippet_tags: [
                                            sniptag21
                                        ]

                                    },
                                    {
                                        description: snipdisc31,
                                        snippet_tags: [
                                            sniptag31
                                        ]

                                    },
                                    {
                                        description: snipdisc41,
                                        snippet_tags: [
                                            sniptag41
                                        ]

                                    }]
                            }
                        ]
                    }

                myObj[blockValue]["languages"][addNewLanguage] = editedJson;
                const finalObject = JSON.stringify(myObj);
                const finalObject1 = finalObject.replaceAll(':null', ':" "');
                const filename = serviceValue + "_metadata.json"
                download1(filename, finalObject1)
                alert("Please return to the terminal to save your changes.")
            }
            if (noOfSnippets === 5) {
                const snippetDescValue = document.getElementById('snippetdescription1').value
                const snippetTagValue = document.getElementById('snippettag1').value
                const snipdisc21 = document.getElementById("snippetdescription2").value
                const sniptag21 = document.getElementById("snippettag2").value
                const snipdisc31 = document.getElementById("snippetdescription3").value
                const sniptag31 = document.getElementById("snippettag3").value
                const snipdisc41 = document.getElementById("snippetdescription4").value
                const sniptag41 = document.getElementById("snippettag4").value
                const snipdisc51 = document.getElementById("snippetdescription5").value
                const sniptag51 = document.getElementById("snippettag5").value

                const editedJson =
                    {
                        versions: [
                            {
                                sdk_version: addedSDKVersionFloat,
                                github: gitHubValue,
                                sdkguide: snippetGuideValue,
                                excerpts: [{
                                    description: snippetDescValue,
                                    snippet_tags: [
                                        snippetTagValue
                                    ]
                                },
                                    {
                                        description: snipdisc21,
                                        snippet_tags: [
                                            sniptag21
                                        ]

                                    },
                                    {
                                        description: snipdisc31,
                                        snippet_tags: [
                                            sniptag31
                                        ]

                                    },
                                    {
                                        description: snipdisc41,
                                        snippet_tags: [
                                            sniptag41
                                        ]

                                    },
                                    {
                                        description: snipdisc51,
                                        snippet_tags: [
                                            sniptag51
                                        ]

                                    }]
                            }
                        ]
                    }

                myObj[blockValue]["languages"][addNewLanguage] = editedJson;
                const finalObject = JSON.stringify(myObj);
                const finalObject1 = finalObject.replaceAll(':null', ':" "');
                const filename = serviceValue + "_metadata.json"
                download1(filename, finalObject1)
                alert("Please return to the terminal to save your changes.")
            }
            if (noOfSnippets === 6) {
                const snippetDescValue = document.getElementById('snippetdescription1').value
                const snippetTagValue = document.getElementById('snippettag1').value
                const snipdisc21 = document.getElementById("snippetdescription2").value
                const sniptag21 = document.getElementById("snippettag2").value
                const snipdisc31 = document.getElementById("snippetdescription3").value
                const sniptag31 = document.getElementById("snippettag3").value
                const snipdisc41 = document.getElementById("snippetdescription4").value
                const sniptag41 = document.getElementById("snippettag4").value
                const snipdisc51 = document.getElementById("snippetdescription5").value
                const sniptag51 = document.getElementById("snippettag5").value
                const snipdisc61 = document.getElementById("snippetdescription6").value
                const sniptag61 = document.getElementById("snippettag6").value
                const editedJson =
                    {
                        versions: [
                            {
                                sdk_version: addedSDKVersionFloat,
                                github: gitHubValue,
                                sdkguide: snippetGuideValue,
                                excerpts: [{
                                    description: snippetDescValue,
                                    snippet_tags: [
                                        snippetTagValue
                                    ]
                                },
                                    {
                                        description: snipdisc21,
                                        snippet_tags: [
                                            sniptag21
                                        ]

                                    },
                                    {
                                        description: snipdisc31,
                                        snippet_tags: [
                                            sniptag31
                                        ]

                                    },
                                    {
                                        description: snipdisc41,
                                        snippet_tags: [
                                            sniptag41
                                        ]

                                    },
                                    {
                                        description: snipdisc51,
                                        snippet_tags: [
                                            sniptag51
                                        ]

                                    },
                                    {
                                        description: snipdisc61,
                                        snippet_tags: [
                                            sniptag61
                                        ]

                                    }]
                            }
                        ]
                    }

                myObj[blockValue]["languages"][addNewLanguage] = editedJson;
                const finalObject = JSON.stringify(myObj);
                const finalObject1 = finalObject.replaceAll(':null', ':" "');
                const filename = serviceValue + "_metadata.json"
                download1(filename, finalObject1)
                alert("Please return to the terminal to save your changes.")
            }
            if (noOfSnippetFiles === 1) {
                const snippetFileDescValue = document.getElementById('snippetfiledescription1').value
                const snippetFileValue = document.getElementById('snippetfile1').value
                const editedJson =
                    {
                        versions: [
                            {
                                sdk_version: addedSDKVersionFloat,
                                github: gitHubValue,
                                sdkguide: snippetGuideValue,
                                excerpts: [{
                                    description: snippetFileDescValue,
                                    snippet_tags: [
                                        snippetFileValue
                                    ]

                                }]
                            }]
                    }
                myObj[blockValue]["languages"][addNewLanguage] = editedJson;
                const finalObject = JSON.stringify(myObj);
                const finalObject1 = finalObject.replaceAll(':null', ':" "');
                const filename = serviceValue + "_metadata.json"
                var f = new File([myObj], filename, {type: "text/plain"})
                download1(filename, finalObject1)
                alert("Please return to the terminal to save your changes.")
            }
            if (noOfSnippetFiles === 2) {
                const snippetFileDescValue = document.getElementById('snippetfiledescription1').value
                const snippetFileValue = document.getElementById('snippetfile1').value
                const snipfiledisc21 = document.getElementById("snippetfiledescription2").value
                const snipfile21 = document.getElementById("snippetfile2").value
                const editedJson =
                    {
                        versions: [
                            {
                                sdk_version: addedSDKVersionFloat,
                                github: gitHubValue,
                                sdkguide: snippetGuideValue,
                                excerpts: [{
                                    description: snippetFileDescValue,
                                    snippet_tags: [
                                        snippetFileValue
                                    ]
                                },
                                    {
                                        description: snipfiledisc21,
                                        snippet_tags: [
                                            snipfile21
                                        ]

                                    }]
                            }
                        ]
                    }


                myObj[blockValue]["languages"][addNewLanguage] = editedJson;
                const finalObject = JSON.stringify(myObj);
                const finalObject1 = finalObject.replaceAll(':null', ':" "');
                const filename = serviceValue + "_metadata.json"
                download1(filename, finalObject1)
                alert("Please return to the terminal to save your changes.")
            }
            if (noOfSnippetFiles === 3) {
                const snippetFileDescValue = document.getElementById('snippetfiledescription1').value
                const snippetFileValue = document.getElementById('snippetfile1').value
                const snipfiledisc21 = document.getElementById("snippetfiledescription2").value
                const snipfile21 = document.getElementById("snippetfile2").value
                const snipfiledisc31 = document.getElementById("snippetfiledescription3").value
                const snipfile31 = document.getElementById("snippetfile3").value
                const editedJson =
                    {
                        versions: [
                            {
                                sdk_version: addedSDKVersionFloat,
                                github: gitHubValue,
                                sdkguide: snippetGuideValue,
                                excerpts: [{
                                    description: snippetFileDescValue,
                                    snippet_tags: [
                                        snippetFileValue
                                    ]
                                },
                                    {
                                        description: snipfiledisc21,
                                        snippet_tags: [
                                            snipfile21
                                        ]

                                    },
                                    {
                                        description: snipfiledisc31,
                                        snippet_tags: [
                                            snipfile31
                                        ]

                                    }]
                            }
                        ]
                    }

                myObj[blockValue]["languages"][addNewLanguage] = editedJson;
                const finalObject = JSON.stringify(myObj);
                const finalObject1 = finalObject.replaceAll(':null', ':" "');
                const filename = serviceValue + "_metadata.json"
                download1(filename, finalObject1)
                alert("Please return to the terminal to save your changes.")
            }
            if (noOfSnippetFiles === 4) {
                const snippetFileDescValue = document.getElementById('snippetfiledescription1').value
                const snippetFileValue = document.getElementById('snippetfile1').value
                const snipfiledisc21 = document.getElementById("snippetfiledescription2").value
                const snipfile21 = document.getElementById("snippetfile2").value
                const snipfiledisc31 = document.getElementById("snippetfiledescription3").value
                const snipfile31 = document.getElementById("snippetfile3").value
                const snipfiledisc41 = document.getElementById("snippetfiledescription4").value
                const snipfile41 = document.getElementById("snippetfile4").value
                const editedJson =
                    {
                        versions: [
                            {
                                sdk_version: addedSDKVersionFloat,
                                github: gitHubValue,
                                sdkguide: snippetGuideValue,
                                excerpts: [{
                                    description: snippetFileDescValue,
                                    snippet_tags: [
                                        snippetFileValue
                                    ]
                                },
                                    {
                                        description: snipfiledisc21,
                                        snippet_tags: [
                                            snipfile21
                                        ]

                                    },
                                    {
                                        description: snipfiledisc31,
                                        snippet_tags: [
                                            snipfile31
                                        ]

                                    },
                                    {
                                        description: snipfiledisc41,
                                        snippet_tags: [
                                            snipfile41
                                        ]

                                    }]
                            }
                        ]
                    }

                myObj[blockValue]["languages"][addNewLanguage] = editedJson;
                const finalObject = JSON.stringify(myObj);
                const finalObject1 = finalObject.replaceAll(':null', ':" "');
                const filename = serviceValue + "_metadata.json"
                download1(filename, finalObject1)
                alert("Please return to the terminal to save your changes.")
            }
            if (noOfSnippetFiles === 5) {
                const snippetFileDescValue = document.getElementById('snippetfiledescription1').value
                const snippetFileValue = document.getElementById('snippetfile1').value
                const snipfiledisc21 = document.getElementById("snippetfiledescription2").value
                const snipfile21 = document.getElementById("snippetfile2").value
                const snipfiledisc31 = document.getElementById("snippetfiledescription3").value
                const snipfile31 = document.getElementById("snippetfile3").value
                const snipfiledisc41 = document.getElementById("snippetfiledescription4").value
                const snipfile41 = document.getElementById("snippetfile4").value
                const snipfiledisc51 = document.getElementById("snippetfiledescription5").value
                const snipfile51 = document.getElementById("snippetfile5").value
                const editedJson =
                    {
                        versions: [
                            {
                                sdk_version: addedSDKVersionFloat,
                                github: gitHubValue,
                                sdkguide: snippetGuideValue,
                                excerpts: [{
                                    description: snippetFileDescValue,
                                    snippet_tags: [
                                        snippetFileValue
                                    ]
                                },
                                    {
                                        description: snipfiledisc21,
                                        snippet_tags: [
                                            snipfile21
                                        ]

                                    },
                                    {
                                        description: snipfiledisc31,
                                        snippet_tags: [
                                            snipfile31
                                        ]

                                    },
                                    {
                                        description: snipfiledisc41,
                                        snippet_tags: [
                                            snipfile41
                                        ]

                                    },
                                    {
                                        description: snipfiledisc51,
                                        snippet_tags: [
                                            snipfile51
                                        ]

                                    }]
                            }
                        ]
                    }
                myObj[blockValue]["languages"][addNewLanguage] = editedJson;
                const finalObject = JSON.stringify(myObj);
                const finalObject1 = finalObject.replaceAll(':null', ':" "');
                const filename = serviceValue + "_metadata.json"
                download1(filename, finalObject1)
                alert("Please return to the terminal to save your changes.")
            }
            if (noOfSnippetFiles === 6) {
                const snippetFileDescValue = document.getElementById('snippetfiledescription1').value
                const snippetFileValue = document.getElementById('snippetfile1').value
                const snipfiledisc21 = document.getElementById("snippetfiledescription2").value
                const snipfile21 = document.getElementById("snippetfile2").value
                const snipfiledisc31 = document.getElementById("snippetfiledescription3").value
                const snipfile31 = document.getElementById("snippetfile3").value
                const snipfiledisc41 = document.getElementById("snippetfiledescription4").value
                const snipfile41 = document.getElementById("snippetfile4").value
                const snipfiledisc51 = document.getElementById("snippetfiledescription5").value
                const snipfile51 = document.getElementById("snippetfile5").value
                const snipfiledisc61 = document.getElementById("snippetfiledescription6").value
                const snipfile61 = document.getElementById("snippetfile6").value
                const editedJson =
                    {
                        versions: [
                            {
                                sdk_version: addedSDKVersionFloat,
                                github: gitHubValue,
                                sdkguide: snippetGuideValue,
                                excerpts: [{
                                    description: snippetFileDescValue,
                                    snippet_tags: [
                                        snippetFileValue
                                    ]
                                },
                                    {
                                        description: snipfiledisc21,
                                        snippet_tags: [
                                            snipfile21
                                        ]

                                    },
                                    {
                                        description: snipfiledisc31,
                                        snippet_tags: [
                                            snipfile31
                                        ]

                                    },
                                    {
                                        description: snipfiledisc41,
                                        snippet_tags: [
                                            snipfile41
                                        ]

                                    },
                                    {
                                        description: snipfiledisc51,
                                        snippet_tags: [
                                            snipfile51
                                        ]

                                    },
                                    {
                                        description: snipfiledisc61,
                                        snippet_tags: [
                                            snipfile61
                                        ]

                                    }]
                            }
                        ]
                    }

                myObj[blockValue]["languages"][addNewLanguage] = editedJson;
                const finalObject = JSON.stringify(myObj);
                const finalObject1 = finalObject.replaceAll(':null', ':" "');
                const filename = serviceValue + "_metadata.json"
                download1(filename, finalObject1)
                alert("Please return to the terminal to save your changes.")
            }
        }
        const serviceStub = document.getElementById('selecttheservice').value
        const sourceJson = "../sos_editor/jsonholder/" + serviceStub + "_metadata.json"
        xmlhttp.open("GET", sourceJson, true);
        xmlhttp.send();
    }
    // Add new SDK version.
    else if (document.getElementById('sdkVersion').value === "Not Listed") {
        console.log('sdk version not listed')
        var xmlhttp = new XMLHttpRequest();
        xmlhttp.onload = function () {
            const addedSDKVersionFloat = parseFloat(addedSDKVersion)
            var myObj = JSON.parse(this.responseText);
            var myObj = myObj;
            myObj[blockValue].title = titleValue;
            myObj[blockValue].title_abbrev = abbrevTitleValue;
            myObj[blockValue].synopsis = synopsisValue;
            myObj[blockValue].synopsis_list = synopsisListValue;
            myObj[blockValue].category = categoryValue;
            myObj[blockValue].services = serviceEntry;
            const noOfSnippets = document.getElementsByClassName('snippdisc').length
            if (noOfSnippets === 0) {
                const editedJson =
                    {
                        sdk_version: addedSDKVersionFloat,
                        github: gitHubValue,
                        sdkguide: snippetGuideValue,
                    }
                myObj[blockValue]["languages"][languageValue]["versions"].push(editedJson);
                const finalObject = JSON.stringify(myObj);
                const finalObject1 = finalObject.replaceAll(':null', ':" "');
                const filename = serviceValue + "_metadata.json"
                download1(filename, finalObject1)
                alert("Please return to the terminal to save your changes.")
            }
            if (noOfSnippets === 1) {
                const snippetDescValue = document.getElementById('snippetdescription1').value
                const snippetTagValue = document.getElementById('snippettag1').value
                const editedJson =
                    {
                        sdk_version: addedSDKVersionFloat,
                        github: gitHubValue,
                        sdkguide: snippetGuideValue,
                        excerpts: [
                            {
                                description: snippetDescValue,
                                snippet_tags: [
                                    snippetTagValue
                                ]
                            }
                        ]
                    }

                myObj[blockValue]["languages"][languageValue]["versions"].push(editedJson);
                const finalObject = JSON.stringify(myObj);
                const finalObject1 = finalObject.replaceAll(':null', ':" "');
                const filename = serviceValue + "_metadata.json"
                download1(filename, finalObject1)
                alert("Please return to the terminal to save your changes.")
            }
            if (noOfSnippets === 2) {
                const snippetDescValue = document.getElementById('snippetdescription1').value
                const snippetTagValue = document.getElementById('snippettag1').value
                const snipdisc2 = document.getElementById("snippetdescription2").value
                const sniptag2 = document.getElementById("snippettag2").value
                const editedJson =
                    {
                        sdk_version: addedSDKVersionFloat,
                        github: gitHubValue,
                        sdkguide: snippetGuideValue,
                        excerpts: [
                            {
                                description: snippetDescValue,
                                snippet_tags: [
                                    snippetTagValue
                                ]
                            },
                            {
                                description: snipdisc2,
                                snippet_tags: [
                                    sniptag2
                                ]
                            }
                        ]
                    }

                myObj[blockValue]["languages"][languageValue]["versions"].push(editedJson);

                const finalObject = JSON.stringify(myObj);
                const finalObject1 = finalObject.replaceAll(':null', ':" "');
                const filename = serviceValue + "_metadata.json"
                download1(filename, finalObject1)
                alert("Please return to the terminal to save your changes.")
            }
            if (noOfSnippets === 3) {
                const snippetDescValue = document.getElementById('snippetdescription1').value
                const snippetTagValue = document.getElementById('snippettag1').value
                const snipdisc2 = document.getElementById("snippetdescription2").value
                const sniptag2 = document.getElementById("snippettag2").value
                const snipdisc3 = document.getElementById("snippetdescription3").value
                const sniptag3 = document.getElementById("snippettag3").value
                const editedJson =
                    {
                        sdk_version: addedSDKVersionFloat,
                        github: gitHubValue,
                        sdkguide: snippetGuideValue,
                        excerpts: [
                            {
                                description: snippetDescValue,
                                snippet_tags: [
                                    snippetTagValue
                                ]
                            },
                            {
                                description: snipdisc2,
                                snippet_tags: [
                                    sniptag2
                                ]
                            },
                            {
                                description: snipdisc3,
                                snippet_tags: [
                                    sniptag3
                                ]
                            }
                        ]
                    }

                myObj[blockValue]["languages"][languageValue]["versions"].push(editedJson);

                const finalObject = JSON.stringify(myObj);
                const finalObject1 = finalObject.replaceAll(':null', ':" "');
                const filename = serviceValue + "_metadata.json"
                download1(filename, finalObject1)
                alert("Please return to the terminal to save your changes.")
            }
            if (noOfSnippets === 4) {
                const snippetDescValue = document.getElementById('snippetdescription1').value
                const snippetTagValue = document.getElementById('snippettag1').value
                const snipdisc2 = document.getElementById("snippetdescription2").value
                const sniptag2 = document.getElementById("snippettag2").value
                const snipdisc3 = document.getElementById("snippetdescription3").value
                const sniptag3 = document.getElementById("snippettag3").value
                const snipdisc4 = document.getElementById("snippetdescription4").value
                const sniptag4 = document.getElementById("snippettag4").value
                const editedJson =
                    {
                        sdk_version: addedSDKVersionFloat,
                        github: gitHubValue,
                        sdkguide: snippetGuideValue,
                        excerpts: [
                            {
                                description: snippetDescValue,
                                snippet_tags: [
                                    snippetTagValue
                                ]
                            },
                            {
                                description: snipdisc2,
                                snippet_tags: [
                                    sniptag2
                                ]
                            },
                            {
                                description: snipdisc3,
                                snippet_tags: [
                                    sniptag3
                                ]
                            },
                            {
                                description: snipdisc4,
                                snippet_tags: [
                                    sniptag4
                                ]
                            }
                        ]
                    }

                myObj[blockValue]["languages"][languageValue]["versions"].push(editedJson);

                const finalObject = JSON.stringify(myObj);
                const finalObject1 = finalObject.replaceAll(':null', ':" "');
                const filename = serviceValue + "_metadata.json"
                download1(filename, finalObject1)
                alert("Please return to the terminal to save your changes.")
            }
            if (noOfSnippets === 5) {
                const snippetDescValue = document.getElementById('snippetdescription1').value
                const snippetTagValue = document.getElementById('snippettag1').value
                const snipdisc2 = document.getElementById("snippetdescription2").value
                const sniptag2 = document.getElementById("snippettag2").value
                const snipdisc3 = document.getElementById("snippetdescription3").value
                const sniptag3 = document.getElementById("snippettag3").value
                const snipdisc4 = document.getElementById("snippetdescription4").value
                const sniptag4 = document.getElementById("snippettag4").value
                const snipdisc5 = document.getElementById("snippetdescription5").value
                const sniptag5 = document.getElementById("snippettag5").value
                const editedJson =
                    {
                        sdk_version: addedSDKVersionFloat,
                        github: gitHubValue,
                        sdkguide: snippetGuideValue,
                        excerpts: [
                            {
                                description: snippetDescValue,
                                snippet_tags: [
                                    snippetTagValue
                                ]
                            },
                            {
                                description: snipdisc2,
                                snippet_tags: [
                                    sniptag2
                                ]
                            },
                            {
                                description: snipdisc3,
                                snippet_tags: [
                                    sniptag3
                                ]
                            },
                            {
                                description: snipdisc4,
                                snippet_tags: [
                                    sniptag4
                                ]
                            },
                            {
                                description: snipdisc5,
                                snippet_tags: [
                                    sniptag5
                                ]
                            }
                        ]
                    }

                myObj[blockValue]["languages"][languageValue]["versions"].push(editedJson);

                const finalObject = JSON.stringify(myObj);
                const finalObject1 = finalObject.replaceAll(':null', ':" "');
                const filename = serviceValue + "_metadata.json"
                download1(filename, finalObject1)
                alert("Please return to the terminal to save your changes.")
            }
            if (noOfSnippets === 6) {
                const snippetDescValue = document.getElementById('snippetdescription1').value
                const snippetTagValue = document.getElementById('snippettag1').value
                const snipdisc2 = document.getElementById("snippetdescription2").value
                const sniptag2 = document.getElementById("snippettag2").value
                const snipdisc3 = document.getElementById("snippetdescription3").value
                const sniptag3 = document.getElementById("snippettag3").value
                const snipdisc4 = document.getElementById("snippetdescription4").value
                const sniptag4 = document.getElementById("snippettag4").value
                const snipdisc5 = document.getElementById("snippetdescription5").value
                const sniptag5 = document.getElementById("snippettag5").value
                const snipdisc6 = document.getElementById("snippetdescription6").value
                const sniptag6 = document.getElementById("snippettag6").value
                const editedJson =
                    {
                        sdk_version: addedSDKVersionFloat,
                        github: gitHubValue,
                        sdkguide: snippetGuideValue,
                        excerpts: [
                            {
                                description: snippetDescValue,
                                snippet_tags: [
                                    snippetTagValue
                                ]
                            },
                            {
                                description: snipdisc2,
                                snippet_tags: [
                                    sniptag2
                                ]
                            },
                            {
                                description: snipdisc3,
                                snippet_tags: [
                                    sniptag3
                                ]
                            },
                            {
                                description: snipdisc4,
                                snippet_tags: [
                                    sniptag4
                                ]
                            },
                            {
                                description: snipdisc5,
                                snippet_tags: [
                                    sniptag5
                                ]
                            },
                            {
                                description: snipdisc6,
                                snippet_tags: [
                                    sniptag6
                                ]
                            }
                        ]
                    }

                myObj[blockValue]["languages"][languageValue]["versions"].push(editedJson);

                const finalObject = JSON.stringify(myObj);
                const finalObject1 = finalObject.replaceAll(':null', ':" "');
                const filename = serviceValue + "_metadata.json"
                download1(filename, finalObject1)
                alert("Please return to the terminal to save your changes.")
            }
        }
        const serviceStub = document.getElementById('selecttheservice').value
        const sourceJson = "../sos_editor/jsonholder/" + serviceStub + "_metadata.json"
        xmlhttp.open("GET", sourceJson, true);
        xmlhttp.send();
        var xmlhttp = new XMLHttpRequest();
    }
    // Edit an existing block.
    else {
        console.log('Editing an existing block.')
        var xmlhttp = new XMLHttpRequest();
        xmlhttp.onload = function () {
            var myObj = JSON.parse(this.responseText);
            console.log('myObj', myObj)
            myObj[blockValue].title = titleValue;
            myObj[blockValue].title_abbrev = abbrevTitleValue;
            console.log('abbrevTitleValue ',myObj[blockValue].title_abbrev)
            if (myObj[blockValue].synopsis_list = "undefined") {
                console.log('there is no synopsis_list');
            }
            myObj[blockValue].synopsis_list = [synopsisListValue];
            myObj[blockValue].category = categoryValue;
            const noOfVersions = myObj[blockValue]["languages"][languageValue]["versions"].length;
            let versions = [];
            for (let i = 0; i < noOfVersions; i++) {
                versions.push(myObj[blockValue]["languages"][languageValue]["versions"][i]["sdk_version"]);
            }
            console.log('sdkversion', '"' + sdkVersion + '"')
            console.log("versions", versions)
            const newSDKV = parseInt(sdkVersion);
            const myLatestNumber = versions.indexOf(newSDKV);
            console.log('MyLatestNumber', myLatestNumber);
            myObj[blockValue]["languages"][languageValue]["versions"][myLatestNumber].sdk_version = parseFloat(sdkVersion);
            myObj[blockValue]["languages"][languageValue]["versions"][myLatestNumber]["github"] = gitHubValue;
            myObj[blockValue]["languages"][languageValue]["versions"][myLatestNumber]["sdkguide"] = snippetGuideValue;
            myObj[blockValue].services = serviceEntry;
            const noOfSnippets = document.getElementsByClassName('snippdisc').length
            const noOfSnippetFiles = document.getElementsByClassName('snippfiledisc').length
            const lengthofExcerpts = myObj[blockValue]["languages"][languageValue]["versions"][myLatestNumber]["excerpts"].length
            console.log('lengthofExcerpts', lengthofExcerpts);
                if (noOfSnippetFiles === 1) {
                    var snippetfiledescription1 = document.getElementById("snippetfiledescription1").value;
                    var snippetfile1 = document.getElementById("snippetfile1").value;
                    console.log('1 snippet file');
                    const snippetFileInfo = {
                        description: snippetfiledescription1,
                        snippet_files: [
                            snippetfile1
                        ]
                    }
                    myObj[blockValue]["languages"][languageValue]["versions"][0]["excerpts"]= [];
                    myObj[blockValue]["languages"][languageValue]["versions"][0]["excerpts"].push(snippetFileInfo);
                    download(myObj, serviceValue);
                    alert("Please return to the terminal to save your changes.")
                }
                if (noOfSnippetFiles === 2) {
                    console.log('2 snippet files');
                    var snippetfiledescription1 = document.getElementById("snippetfiledescription1").value;
                    var snippetfile1 = document.getElementById("snippetfile1").value;
                    var snippetfiledescription2 = document.getElementById("snippetfiledescription2").value;
                    var snipfile2 = document.getElementById("snippetfile2").value;
                    var snippetFileInfo = [{
                        description: snippetfiledescription1,
                        snippet_files: [
                            snippetfile1
                        ]
                    },
                        {
                            description: snippetfiledescription2,
                            snippet_files: [
                                snipfile2
                            ]
                        }]
                    myObj[blockValue]["languages"][languageValue]["versions"][0]["excerpts"]= [];
                    myObj[blockValue]["languages"][languageValue]["versions"][0]["excerpts"].push(snippetFileInfo);
                    download(myObj, serviceValue);
                    alert("Please return to the terminal to save your changes.")
                }
                if (noOfSnippetFiles === 3) {
                    var snippetfiledescription1 = document.getElementById("snippetfiledescription1").value;
                    var snippetfile1 = document.getElementById("snippetfile1").value;
                    var snippetfiledescription2 = document.getElementById("snippetfiledescription2").value;
                    var snipfile2 = document.getElementById("snippetfile2").value;
                    var snippetfiledescription3 = document.getElementById("snippetfiledescription3").value;
                    var snipfile3 = document.getElementById("snippetfile3").value;
                    const snippetFileInfo = [{
                        description: snippetfiledescription1,
                        snippet_files: [
                            snippetfile1
                        ]
                    },
                        {
                            description: snippetfiledescription2,
                            snippet_files: [
                                snipfile2
                            ]
                        },
                        {
                            description: snippetfiledescription3,
                            snippet_files: [
                                snipfile3
                            ]
                        }]
                    myObj[blockValue]["languages"][languageValue]["versions"][0]["excerpts"]= [];
                    myObj[blockName]["languages"][languageValue]["versions"][0]["excerpts"].push(snippetFileInfo);
                    download(myObj, serviceValue);
                    alert("Please return to the terminal to save your changes.")
                }
                if (noOfSnippetFiles === 4) {
                    var snippetfiledescription1 = document.getElementById("snippetfiledescription1").value;
                    var snippetfile1 = document.getElementById("snippetfile1").value;
                    var snippetfiledescription2 = document.getElementById("snippetfiledescription2").value;
                    var snipfile2 = document.getElementById("snippetfile2").value;
                    var snippetfiledescription3 = document.getElementById("snippetfiledescription3").value;
                    var snipfile3 = document.getElementById("snippetfile3").value;
                    var snippetfiledescription4 = document.getElementById("snippetfiledescription3").value;
                    var snipfile4 = document.getElementById("snippetfile3").value;
                    const snippetFileInfo = [{
                        description: snippetfiledescription1,
                        snippet_files: [
                            snippetfile1
                        ]
                    },
                        {
                            description: snippetfiledescription2,
                            snippet_files: [
                                snipfile2
                            ]
                        },
                        {
                            description: snippetfiledescription3,
                            snippet_files: [
                                snipfile3
                            ]
                        },
                        {
                            description: snippetfiledescription4,
                            snippet_files: [
                                snipfile4
                            ]
                        }]
                    myObj[blockValue]["languages"][languageValue]["versions"][0]["excerpts"]= [];
                    myObj[blockName]["languages"][languageValue]["versions"][0]["excerpts"].push(snippetFileInfo);
                    download(myObj, serviceValue);
                    alert("Please return to the terminal to save your changes.")
                }
                if (noOfSnippetFiles === 5) {
                    var snippetfiledescription1 = document.getElementById("snippetfiledescription1").value;
                    var snippetfile1 = document.getElementById("snippetfile1").value;
                    var snippetfiledescription2 = document.getElementById("snippetfiledescription2").value;
                    var snipfile2 = document.getElementById("snippetfile2").value;
                    var snippetfiledescription3 = document.getElementById("snippetfiledescription3").value;
                    var snipfile3 = document.getElementById("snippetfile3").value;
                    var snippetfiledescription4 = document.getElementById("snippetfiledescription3").value;
                    var snipfile4 = document.getElementById("snippetfile3").value;
                    var snippetfiledescription5 = document.getElementById("snippetfiledescription5").value;
                    var snipfile5 = document.getElementById("snippetfile5").value;
                    const snippetFileInfo = [{
                        description: snippetfiledescription1,
                        snippet_files: [
                            snippetfile1
                        ]
                    },
                        {
                            description: snippetfiledescription2,
                            snippet_files: [
                                snipfile2
                            ]
                        },
                        {
                            description: snippetfiledescription3,
                            snippet_files: [
                                snipfile3
                            ]
                        },
                        {
                            description: snippetfiledescription4,
                            snippet_files: [
                                snipfile4
                            ]
                        },
                        {
                            description: snippetfiledescription5,
                            snippet_files: [
                                snipfile5
                            ]
                        }]
                    myObj[blockValue]["languages"][languageValue]["versions"][0]["excerpts"]= [];
                    myObj[blockName]["languages"][languageValue]["versions"][0]["excerpts"].push(snippetFileInfo);
                    download(myObj, serviceValue);
                    alert("Please return to the terminal to save your changes.")
                }
                if (noOfSnippetFiles === 6) {
                    var snippetfiledescription1 = document.getElementById("snippetfiledescription1").value;
                    var snippetfile1 = document.getElementById("snippetfile1").value;
                    var snippetfiledescription2 = document.getElementById("snippetfiledescription2").value;
                    var snipfile2 = document.getElementById("snippetfile2").value;
                    var snippetfiledescription3 = document.getElementById("snippetfiledescription3").value;
                    var snipfile3 = document.getElementById("snippetfile3").value;
                    var snippetfiledescription4 = document.getElementById("snippetfiledescription4").value;
                    var snipfile4 = document.getElementById("snippetfile4").value;
                    var snippetfiledescription5 = document.getElementById("snippetfiledescription5").value;
                    var snipfile5 = document.getElementById("snippetfile5").value;
                    var snippetfiledescription5 = document.getElementById("snippetfiledescription6").value;
                    var snipfile5 = document.getElementById("snippetfile6").value;
                    const snippetFileInfo = [{
                        description: snippetfiledescription1,
                        snippet_files: [
                            snippetfile1
                        ]
                    },
                        {
                            description: snippetfiledescription2,
                            snippet_files: [
                                snipfile2
                            ]
                        },
                        {
                            description: snippetfiledescription3,
                            snippet_files: [
                                snipfile3
                            ]
                        },
                        {
                            description: snippetfiledescription4,
                            snippet_files: [
                                snipfile4
                            ]
                        },
                        {
                            description: snippetfiledescription5,
                            snippet_files: [
                                snipfile5
                            ]
                        },
                        {
                            description: snippetfiledescription6,
                            snippet_files: [
                                snipfile6
                            ]
                        }]
                    myObj[blockValue]["languages"][languageValue]["versions"][0]["excerpts"]= [];
                    myObj[blockName]["languages"][languageValue]["versions"][0]["excerpts"].push(snippetFileInfo);
                    download(myObj, serviceValue);
                    alert("Please return to the terminal to save your changes.")
                }
                if (noOfSnippets === 1) {
                console.log('1 snippet, edit existing block')
                const snippetDescValue = document.getElementById('snippetdescription1').value
                const snippetTagValue = document.getElementById('snippettag1').value
                const editedJson =
                    {
                        description: snippetDescValue,
                        snippet_tags: [
                            snippetTagValue
                        ]

                    }

                myObj[blockValue]["languages"][languageValue]["versions"][myLatestNumber]["excerpts"] = []
                myObj[blockValue]["languages"][languageValue]["versions"][myLatestNumber]["excerpts"].push(editedJson)
                console.log('final object', myObj)
                console.log('editedJson', editedJson)
                const finalObject = JSON.stringify(myObj);
                const finalObject1 = finalObject.replaceAll(':null', ':" "').replaceAll("\\\"", "").replaceAll('\\n', '\",\"').replaceAll(':null', ':" "').replaceAll("[[","[").replaceAll("]]","]");
                const filename = serviceValue + "_metadata.json"
                download1(filename, finalObject1)
                alert("Please return to the terminal to save your changes.")
            }
                if (noOfSnippets === 2) {
                console.log('two snippets')
                const snippetDescValue = document.getElementById('snippetdescription1').value
                const snippetTagValue = document.getElementById('snippettag1').value
                const snipdisc21 = document.getElementById("snippetdescription2").value
                const sniptag21 = document.getElementById("snippettag2").value
                const editedJson =
                    [{
                        description: snippetDescValue,
                        snippet_tags: [
                            snippetTagValue
                        ]
                    },
                        {
                            description: snipdisc21,
                            snippet_tags: [
                                sniptag21
                            ]

                        }]


                myObj[blockValue]["languages"][languageValue]["versions"][myLatestNumber]["excerpts"] = []
                myObj[blockValue]["languages"][languageValue]["versions"][myLatestNumber]["excerpts"].push(editedJson)
                const finalObject = JSON.stringify(myObj);
                console.log('finalObject', finalObject)
                var finalObject1 = finalObject.replaceAll(':null', ':" "').replaceAll("\\\"", "").replaceAll('\\n', '\",\"').replaceAll(':null', ':" "').replaceAll("[[","[").replaceAll("]]","]").replaceAll('category:','synopsis_list:\n  category:');;
                /*if(!finalObject1.includes("synopsis_list")) {
                    console.log('it does not');
                    finalObject1 = finalObject1.replaceAll(':null', ':" "').replaceAll("\\\"", "").replaceAll('\\n', '\",\"').replaceAll(':null', ':" "').replaceAll("[[", "[").replaceAll("]]", "]").replaceAll('category:','synopsis_list:\n  category:');
                }*/
                console.log('finalObject1', finalObject1)
                const filename = serviceValue + "_metadata.json"
                download1(filename, finalObject1)
                alert("Please return to the terminal to save your changes.")
            }
                if (noOfSnippets === 3) {
                const snippetDescValue = document.getElementById('snippetdescription1').value
                const snippetTagValue = document.getElementById('snippettag1').value
                const snipdisc21 = document.getElementById("snippetdescription2").value
                const sniptag21 = document.getElementById("snippettag2").value
                const snipdisc31 = document.getElementById("snippetdescription3").value
                const sniptag31 = document.getElementById("snippettag3").value
                const editedJson =
                    [{
                        description: snippetDescValue,
                        snippet_tags: [
                            snippetTagValue
                        ]
                    },
                        {
                            description: snipdisc21,
                            snippet_tags: [
                                sniptag21
                            ]

                        },
                        {
                            description: snipdisc31,
                            snippet_tags: [
                                sniptag31
                            ]

                        }]


                myObj[blockValue]["languages"][languageValue]["versions"][myLatestNumber]["excerpts"] = []
                myObj[blockValue]["languages"][languageValue]["versions"][myLatestNumber]["excerpts"].push(editedJson)
                const finalObject = JSON.stringify(myObj);
                console.log('finalObject', finalObject)
                const finalObject1 = finalObject.replaceAll(':null', ':" "').replaceAll("\\\"", "").replaceAll('\\n', '\",\"').replaceAll(':null', ':" "').replaceAll("[[","[").replaceAll("]]","]");
                console.log('finalObject1', finalObject1)
                const filename = serviceValue + "_metadata.json"
                download1(filename, finalObject1)
                alert("Please return to the terminal to save your changes.")
            }
                if (noOfSnippets === 4) {
                const snippetDescValue = document.getElementById('snippetdescription1').value
                const snippetTagValue = document.getElementById('snippettag1').value
                const snipdisc21 = document.getElementById("snippetdescription2").value
                const sniptag21 = document.getElementById("snippettag2").value
                const snipdisc31 = document.getElementById("snippetdescription3").value
                const sniptag31 = document.getElementById("snippettag3").value
                const snipdisc41 = document.getElementById("snippetdescription4").value
                const sniptag41 = document.getElementById("snippettag4").value
                const editedJson =
                    [{
                        description: snippetDescValue,
                        snippet_tags: [
                            snippetTagValue
                        ]
                    },
                        {
                            description: snipdisc21,
                            snippet_tags: [
                                sniptag21
                            ]

                        },
                        {
                            description: snipdisc31,
                            snippet_tags: [
                                sniptag31
                            ]

                        },
                        {
                            description: snipdisc41,
                            snippet_tags: [
                                sniptag41
                            ]

                        }]


                myObj[blockValue]["languages"][languageValue]["versions"][myLatestNumber]["excerpts"] = []
                myObj[blockValue]["languages"][languageValue]["versions"][myLatestNumber]["excerpts"].push(editedJson)
                const finalObject = JSON.stringify(myObj);
                console.log('finalObject', finalObject)
                const finalObject1 = finalObject.replaceAll(':null', ':" "').replaceAll("\\\"", "").replaceAll('\\n', '\",\"').replaceAll(':null', ':" "').replaceAll("[[","[").replaceAll("]]","]");
                console.log('finalObject1', finalObject1)
                const filename = serviceValue + "_metadata.json"
                download1(filename, finalObject1)
                alert("Please return to the terminal to save your changes.")
            }
                if (noOfSnippets === 5) {
                const snippetDescValue = document.getElementById('snippetdescription1').value
                const snippetTagValue = document.getElementById('snippettag1').value
                const snipdisc21 = document.getElementById("snippetdescription2").value
                const sniptag21 = document.getElementById("snippettag2").value
                const snipdisc31 = document.getElementById("snippetdescription3").value
                const sniptag31 = document.getElementById("snippettag3").value
                const snipdisc41 = document.getElementById("snippetdescription4").value
                const sniptag41 = document.getElementById("snippettag4").value
                const snipdisc51 = document.getElementById("snippetdescription5").value
                const sniptag51 = document.getElementById("snippettag5").value

                const editedJson =
                    [{
                        description: snippetDescValue,
                        snippet_tags: [
                            snippetTagValue
                        ]
                    },
                        {
                            description: snipdisc21,
                            snippet_tags: [
                                sniptag21
                            ]

                        },
                        {
                            description: snipdisc31,
                            snippet_tags: [
                                sniptag31
                            ]

                        },
                        {
                            description: snipdisc41,
                            snippet_tags: [
                                sniptag41
                            ]

                        },
                        {
                            description: snipdisc51,
                            snippet_tags: [
                                sniptag51
                            ]

                        }]

                myObj[blockValue]["languages"][languageValue]["versions"][myLatestNumber]["excerpts"] = []
                myObj[blockValue]["languages"][languageValue]["versions"][myLatestNumber]["excerpts"].push(editedJson)
                const finalObject = JSON.stringify(myObj);
                const finalObject1 = finalObject.replaceAll(':null', ':" "').replaceAll("\\\"", "").replaceAll('\\n', '\",\"').replaceAll(':null', ':" "').replaceAll("[[","[").replaceAll("]]","]");
                const filename = serviceValue + "_metadata.json"
                download1(filename, finalObject1)
                alert("Please return to the terminal to save your changes.")
            }
                if (noOfSnippets === 6) {
                const snippetDescValue = document.getElementById('snippetdescription1').value
                const snippetTagValue = document.getElementById('snippettag1').value
                const snipdisc21 = document.getElementById("snippetdescription2").value
                const sniptag21 = document.getElementById("snippettag2").value
                const snipdisc31 = document.getElementById("snippetdescription3").value
                const sniptag31 = document.getElementById("snippettag3").value
                const snipdisc41 = document.getElementById("snippetdescription4").value
                const sniptag41 = document.getElementById("snippettag4").value
                const snipdisc51 = document.getElementById("snippetdescription5").value
                const sniptag51 = document.getElementById("snippettag5").value
                const snipdisc61 = document.getElementById("snippetdescription6").value
                const sniptag61 = document.getElementById("snippettag6").value
                const editedJson =
                    [{
                        description: snippetDescValue,
                        snippet_tags: [
                            snippetTagValue
                        ]
                    },
                        {
                            description: snipdisc21,
                            snippet_tags: [
                                sniptag21
                            ]

                        },
                        {
                            description: snipdisc31,
                            snippet_tags: [
                                sniptag31
                            ]

                        },
                        {
                            description: snipdisc41,
                            snippet_tags: [
                                sniptag41
                            ]

                        },
                        {
                            description: snipdisc51,
                            snippet_tags: [
                                sniptag51
                            ]

                        },
                        {
                            description: snipdisc61,
                            snippet_tags: [
                                sniptag61
                            ]

                        }]


                myObj[blockValue]["languages"][languageValue]["versions"][myLatestNumber]["excerpts"] = []
                myObj[blockValue]["languages"][languageValue]["versions"][myLatestNumber]["excerpts"].push(editedJson)
                const finalObject = JSON.stringify(myObj);
                console.log('finalObject', finalObject)
                const finalObject1 = finalObject.replaceAll(':null', ':" "').replaceAll("\\\"", "").replaceAll('\\n', '\",\"').replaceAll(':null', ':" "').replaceAll("[[","[").replaceAll("]]","]");
                console.log('finalObject1', finalObject1)
                const filename = serviceValue + "_metadata.json"
                download1(filename, finalObject1)
                alert("Please return to the terminal to save your changes.")
            }
        }
        const serviceStub = document.getElementById('selecttheservice').value
        const sourceJson = "../sos_editor/jsonholder/" + serviceStub + "_metadata.json"
        console.log('sourceJson', sourceJson)
        xmlhttp.open("GET", sourceJson, true);
        xmlhttp.send();
    }
};

function create_code_example_tag(tagnumber, codeExampleTitle) {
    const serviceValue = document.getElementById('selecttheservice').value
    console.log("serviceValue", serviceValue);
    const languageValue = document.getElementById('languages').value
    console.log("languageValue", languageValue);
    const blockName = document.getElementById('blockname').value
    console.log('blockName', blockName);
    const apiCall = blockName.replace(serviceValue +'_', "")
    console.log("apiCall", apiCall);
    const codeExample = document.getElementById('codeExample').value
    console.log('codeExample', codeExample);
    const snippetStart = "//snippet-start:[" + serviceValue + ".example_code." + languageValue + "." + codeExampleTitle + "]";
    console.log("snippetStart", snippetStart);
    const snippetEnd = "//snippet-end:[" + serviceValue + ".example_code." + languageValue + "." + codeExampleTitle + "]";
    console.log("snippetEnd", snippetEnd);
    const finalExample = snippetStart + "\n" + codeExample + "\n" + snippetEnd
    if (languageValue == "SAP ABAP") {
        var fileExtension = ".abap"
    } else if (languageValue == "Bash") {
        var fileExtension = ".sh"
    } else if (languageValue == "C++") {
        var fileExtension = ".cpp"
    } else if (languageValue == "DotNet") {
        var fileExtension = "."
    } else if (languageValue == "Go") {
        var fileExtension = ".cs"
    } else if (languageValue == "Kotlin") {
        var fileExtension = ".kt"
    } else if (languageValue == "Java") {
        var fileExtension = ".java"
    } else if (languageValue == "JavaScript") {
        var fileExtension = ".js"
    } else if (languageValue == "PHP") {
        var fileExtension = ".php"
    } else if (languageValue == "Python") {
        var fileExtension = ".py"
    } else if (languageValue == "Ruby") {
        var fileExtension = ".rb"
    } else if (languageValue == "Rust") {
        var fileExtension = ".rs"
    } else if (languageValue == "Swift") {
        var fileExtension = ".swift"
    }
    console.log('fileextenstion', fileExtension)
    const myFilename = codeExampleTitle;
    download1(myFilename, finalExample);
    alert(myFilename + " is in your Downloads folder. Please copy it to the appropriate folder.")
    const snippettag = serviceValue + ".example_code." + languageValue + "." + codeExampleTitle;
    console.log('snippettag', snippettag)
    document.getElementById("snippettag" +tagnumber).value = snippettag ;
}

function create_code_example_file(filenumber) {
    const serviceValue = document.getElementById('selecttheservice').value
    console.log("serviceValue", serviceValue);
    const languageValue = document.getElementById('languages').value
    console.log("languageValue", languageValue);
    const blockName = document.getElementById('blockname').value
    console.log('blockName', blockName);
    const apiCall = blockName.replace(serviceValue +'_', "")
    console.log("apiCall", apiCall);
    const codeExample = document.getElementById('codeExample').value
    console.log('codeExample', codeExample);
    const snippetStart = "//snippet-start:[" + serviceValue + ".example_code." + languageValue + "." + apiCall + "]";
    console.log("snippetStart", snippetStart);
    const snippetEnd = "//snippet-end:[" + serviceValue + ".example_code." + languageValue + "." + apiCall + "]";
    console.log("snippetEnd", snippetEnd);
    const finalExample = snippetStart + "\n" + codeExample + "\n" + snippetEnd
    if (languageValue == "SAP ABAP") {
        var fileExtension = ".abap"
    } else if (languageValue == "Bash") {
        var fileExtension = ".sh"
    } else if (languageValue == "C++") {
        var fileExtension = ".cpp"
    } else if (languageValue == "DotNet") {
        var fileExtension = "."
    } else if (languageValue == "Go") {
        var fileExtension = ".cs"
    } else if (languageValue == "Kotlin") {
        var fileExtension = ".kt"
    } else if (languageValue == "Java") {
        var fileExtension = ".java"
    } else if (languageValue == "JavaScript") {
        var fileExtension = ".js"
    } else if (languageValue == "PHP") {
        var fileExtension = ".php"
    } else if (languageValue == "Python") {
        var fileExtension = ".py"
    } else if (languageValue == "Ruby") {
        var fileExtension = ".rb"
    } else if (languageValue == "Rust") {
        var fileExtension = ".rs"
    } else if (languageValue == "Swift") {
        var fileExtension = ".swift"
    }
    console.log('fileextenstion', fileExtension)
    const myFilename = apiCall + fileExtension;
    download1(myFilename, finalExample);
    alert(myFilename + " is in your Downloads folder. Please copy it to the appropriate folder.")
    const snippetfile = serviceValue + ".example_code." + languageValue + "." + apiCall
    console.log('snippetfile1', snippetfile)
    document.getElementById('snippetfile'+ filenumber).value = snippetfile ;
}