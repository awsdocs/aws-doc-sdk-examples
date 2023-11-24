function download(filename, text) {
    var element = document.createElement('a');
    element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
    element.setAttribute('download', filename);
    element.style.display = 'none';
    document.body.appendChild(element);

    element.click();

    document.body.removeChild(element);
}

function renameKey ( obj, oldKey, newKey ) {
    obj[newKey] = obj[oldKey];
    delete obj[oldKey];
}

function createYAML() {
    const serviceValue = document.getElementById('selecttheservice').value
    console.log("serviceValue", serviceValue)
    const blockValue = document.getElementById('selectBlock').value
    console.log("blockValue", blockValue)
    const blockName = document.getElementById('blockname').value
    console.log('blockName', blockName)
    if(blockValue=="Not listed") {
var apiCall = blockName.replace(serviceValue + '_', "")
        console.log("apiCall", apiCall)
    }
    else{
        var apiCall = blockValue.replace(serviceValue + '_', "")
        console.log("apiCall", apiCall)
    }
    const serviceEntry =  serviceValue + ": {"+apiCall+"}";
    const titleValue = document.getElementById('thetitle').value
    console.log("titleValue", titleValue)
    const abbrevTitleValue = document.getElementById('addatitle').value
    console.log("abbrevTitleValue", abbrevTitleValue)
    const synopsisValue = document.getElementById('synopsis').value
    console.log("synopsisValue", synopsisValue)
    const synopsisListValue = document.getElementById('synopsislist').value
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
    for (var i = 1 ; i < elements.length; i++) {
        const snippettag = "snippettag" + i
        console.log('this snippet tag', snippettag)

        if(document.getElementById(snippettag).value == 0){
            alert('You have empty snippet tags. This breaks the build.')
            return
        }

    }
    if (blockValue == "Not listed") {
        var xmlhttp = new XMLHttpRequest();
        xmlhttp.onload = function () {
            var myObj = JSON.parse(this.responseText);
            console.log('whats this', myObj);
            const addedSDKVersionFloat = parseFloat(addedSDKVersion)
            const noOfSnippets = document.getElementsByClassName('snippdisc').length
            if (noOfSnippets === 0) {
                const editedJson =
                    {
                        title: titleValue,
                        title_abbrev: abbrevTitleValue,
                        synopsis: synopsisValue,
                        synopsis_list: synopsisListValue,
                        category: categoryValue,
                        languages: {
                            [languageValue]: {
                                versions: [
                                    {
                                        sdk_version: addedSDKVersionFloat,
                                        github: gitHubValue,
                                        sdkguide: snippetGuideValue
                                    }
                                ]
                            }
                        },
                        services: {serviceEntry}
                    }
                myObj[blockName] = editedJson
                const finalObject = JSON.stringify(myObj);
                const finalObject1 = finalObject.replaceAll(':null', ':" "' );
                const filename = serviceValue + "_metadata.json"
                var f = new File([myObj], filename, {type: "text/plain"})
                download(filename, finalObject1)
            }
            if (noOfSnippets === 1) {
                const snippetDescValue = document.getElementById('snippetdescription').value
                const snippetTagValue = document.getElementById('snippettag').value
                const editedJson =
                    {
                        title: titleValue,
                        title_abbrev: abbrevTitleValue,
                        synopsis: synopsisValue,
                        synopsis_list: synopsisListValue,
                        category: categoryValue,
                        languages: {
                            [languageValue]: {
                                versions: [
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
                                ]
                            }
                        },
                        services:[serviceEntry]
                    }
                myObj[blockName] = editedJson
                console.log('final object2', myObj)
                console.log('edited Json', editedJson)
                const finalObject = JSON.stringify(myObj);
                const finalObject1 = finalObject.replaceAll(':null', ':" "' );
                const filename = serviceValue + "_metadata.json"
                var f = new File([myObj], filename, {type: "text/plain"})
                download(filename, finalObject1)
            }
            if (noOfSnippets === 2) {
                const snippetDescValue = document.getElementById('snippetdescription').value
                const snippetTagValue = document.getElementById('snippettag').value
                const snipdisc2 = document.getElementById("snippetdescription1").value
                const sniptag2 = document.getElementById("snippettag1").value
                const editedJson =
                    {
                        title: titleValue,
                        title_abbrev: abbrevTitleValue,
                        synopsis: synopsisValue,
                        synopsis_list: synopsisListValue,
                        category: categoryValue,
                        languages: {
                            [languageValue]: {
                                versions: [
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
                                ]
                            }
                        },
                        services: [serviceEntry]
                    }
                myObj[blockName] = editedJson
                console.log('final object3', myObj)
                const finalObject = JSON.stringify(myObj);
                const finalObject1 = finalObject.replaceAll(':null', ':" "' );
                const filename = serviceValue + "_metadata.json"
                var f = new File([myObj], filename, {type: "text/plain"})
                download(filename, finalObject1)
            }
            if (noOfSnippets === 3) {
                const snippetDescValue = document.getElementById('snippetdescription').value
                const snippetTagValue = document.getElementById('snippettag').value
                const snipdisc2 = document.getElementById("snippetdescription1").value
                const sniptag2 = document.getElementById("snippettag1").value
                const snipdisc3 = document.getElementById("snippetdescription2").value
                const sniptag3 = document.getElementById("snippettag2").value
                const editedJson =
                    {
                        title: titleValue,
                        title_abbrev: abbrevTitleValue,
                        synopsis: synopsisValue,
                        synopsis_list: synopsisListValue,
                        category: categoryValue,
                        languages: {
                            [languageValue]: {
                                versions: [
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
                                ]
                            }
                        },
                        services: {serviceEntry}
                    }
                myObj[blockName] = editedJson
                console.log('final object4', myObj)
                const finalObject = JSON.stringify(myObj);
                const finalObject1 = finalObject.replaceAll(':null', ':" "' );
                const filename = serviceValue + "_metadata.json"
                var f = new File([myObj], filename, {type: "text/plain"})
                download(filename, finalObject1)
            }
            if (noOfSnippets === 4) {
                const snippetDescValue = document.getElementById('snippetdescription').value
                const snippetTagValue = document.getElementById('snippettag').value
                const snipdisc2 = document.getElementById("snippetdescription1").value
                const sniptag2 = document.getElementById("snippettag1").value
                const snipdisc3 = document.getElementById("snippetdescription2").value
                const sniptag3 = document.getElementById("snippettag2").value
                const snipdisc4 = document.getElementById("snippetdescription3").value
                const sniptag4 = document.getElementById("snippettag3").value
                const editedJson =
                    {
                        title: titleValue,
                        title_abbrev: abbrevTitleValue,
                        synopsis: synopsisValue,
                        synopsis_list: synopsisListValue,
                        category: categoryValue,
                        languages: {
                            [languageValue]: {
                                versions: [
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
                                ]
                            }
                        },
                        services: {serviceEntry}
                    }

                myObj[blockName] = editedJson
                console.log('final object5', myObj)
                const finalObject = JSON.stringify(myObj);
                const finalObject1 = finalObject.replaceAll(':null', ':" "' );
                const filename = serviceValue + "_metadata.json"
                var f = new File([myObj], filename, {type: "text/plain"})
                download(filename, finalObject1)
            }
            if (noOfSnippets === 5) {
                const snippetDescValue = document.getElementById('snippetdescription').value
                const snippetTagValue = document.getElementById('snippettag').value
                const snipdisc2 = document.getElementById("snippetdescription1").value
                const sniptag2 = document.getElementById("snippettag1").value
                const snipdisc3 = document.getElementById("snippetdescription2").value
                const sniptag3 = document.getElementById("snippettag2").value
                const snipdisc4 = document.getElementById("snippetdescription3").value
                const sniptag4 = document.getElementById("snippettag3").value
                const snipdisc5 = document.getElementById("snippetdescription4").value
                const sniptag5 = document.getElementById("snippettag4").value
                const editedJson =
                    {
                        title: titleValue,
                        title_abbrev: abbrevTitleValue,
                        synopsis: synopsisValue,
                        synopsis_list: synopsisListValue,
                        category: categoryValue,
                        languages: {
                            [languageValue]: {
                                versions: [
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
                                ]
                            }
                        },
                        services: {serviceEntry}
                    }

                myObj[blockName] = editedJson
                console.log('final object6', myObj)
                const finalObject = JSON.stringify(myObj);
                const finalObject1 = finalObject.replaceAll(':null', ':" "' );
                const filename = serviceValue + "_metadata.json"
                var f = new File([myObj], filename, {type: "text/plain"})
                download(filename, finalObject1)
            }
            if (noOfSnippets === 6) {
                const snippetDescValue = document.getElementById('snippetdescription').value
                const snippetTagValue = document.getElementById('snippettag').value
                const snipdisc2 = document.getElementById("snippetdescription1").value
                const sniptag2 = document.getElementById("snippettag1").value
                const snipdisc3 = document.getElementById("snippetdescription2").value
                const sniptag3 = document.getElementById("snippettag2").value
                const snipdisc4 = document.getElementById("snippetdescription3").value
                const sniptag4 = document.getElementById("snippettag3").value
                const snipdisc5 = document.getElementById("snippetdescription4").value
                const sniptag5 = document.getElementById("snippettag4").value
                const snipdisc6 = document.getElementById("snippetdescription5").value
                const sniptag6 = document.getElementById("snippettag5").value
                const editedJson =
                    {
                        title: titleValue,
                        title_abbrev: abbrevTitleValue,
                        synopsis: synopsisValue,
                        synopsis_list: synopsisListValue,
                        category: categoryValue,
                        languages: {
                            [languageValue]: {
                                versions: [
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
                                ]
                            }
                        },
                        services: {serviceEntry}
                    }

                myObj[blockName] = editedJson
                console.log('final object7', myObj)
                const finalObject = JSON.stringify(myObj);
                const finalObject1 = finalObject.replaceAll(':null', ':" "' );
                const filename = serviceValue + "_metadata.json"
                var f = new File([myObj], filename, {type: "text/plain"})
                download(filename, finalObject1)
            }
        }
        const serviceStub = document.getElementById('selecttheservice').value
        const sourceJson = "../sos_editor/jsonholder/" + serviceStub + "_metadata.json"
        xmlhttp.open("GET", sourceJson, true);
        xmlhttp.send();
    }
    else if(document.getElementById('languages').value=="Not listed") {
        const addedSDKVersionFloat = parseFloat(addedSDKVersion)
        var xmlhttp = new XMLHttpRequest();
        xmlhttp.onload = function () {
            var myObj = JSON.parse(this.responseText);
            var myObj = myObj;
            myObj[blockValue].title = titleValue;
            myObj[blockValue].title_abbrev = abbrevTitleValue;
            myObj[blockValue].synopsis = synopsisValue;
            myObj[blockValue].synopsis_list= synopsisListValue;
            myObj[blockValue].category = categoryValue;
            const noOfSnippets = document.getElementsByClassName('snippdisc').length
            if (noOfSnippets === 0) {
                const editedJson =
                    {
                        versions: [
                            {
                                sdk_version: addedSDKVersionFloat,
                                github: gitHubValue,
                                sdkguide: snippetGuideValue
                            }]
                    }


                myObj[blockValue]["languages"][addNewLanguage] =editedJson;
                const finalObject = JSON.stringify(myObj);
                const finalObject1 = finalObject.replaceAll(':null', ':" "' );
                const filename = serviceValue + "_metadata.json"
                var f = new File([myObj], filename, {type: "text/plain"})
                download(filename, finalObject1)
            }

            if (noOfSnippets === 1) {
                const snippetDescValue = document.getElementById('snippetdescription').value
                const snippetTagValue = document.getElementById('snippettag').value
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


                myObj[blockValue]["languages"][addNewLanguage] =editedJson;
                const finalObject = JSON.stringify(myObj);
                const finalObject1 = finalObject.replaceAll(':null', ':" "' );
                const filename = serviceValue + "_metadata.json"
                var f = new File([myObj], filename, {type: "text/plain"})
                download(filename, finalObject1)
            }
            if (noOfSnippets === 2) {
                const snippetDescValue = document.getElementById('snippetdescription').value
                const snippetTagValue = document.getElementById('snippettag').value
                const snipdisc21 = document.getElementById("snippetdescription1").value
                const sniptag21 = document.getElementById("snippettag1").value
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


                myObj[blockValue]["languages"][addNewLanguage] =editedJson;
                const finalObject = JSON.stringify(myObj);
                const finalObject1 = finalObject.replaceAll(':null', ':" "' );
                const filename = serviceValue + "_metadata.json"
                var f = new File([myObj], filename, {type: "text/plain"})
                download(filename, finalObject1)
            }
            if (noOfSnippets === 3) {
                const snippetDescValue = document.getElementById('snippetdescription').value
                const snippetTagValue = document.getElementById('snippettag').value
                const snipdisc21 = document.getElementById("snippetdescription1").value
                const sniptag21 = document.getElementById("snippettag1").value
                const snipdisc31 = document.getElementById("snippetdescription2").value
                const sniptag31 = document.getElementById("snippettag2").value

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

                myObj[blockValue]["languages"][addNewLanguage] =editedJson;
                const finalObject = JSON.stringify(myObj);
                const finalObject1 = finalObject.replaceAll(':null', ':" "' );
                const filename = serviceValue + "_metadata.json"
                var f = new File([myObj], filename, {type: "text/plain"})
                download(filename, finalObject1)
            }
            if (noOfSnippets === 4) {
                const snippetDescValue = document.getElementById('snippetdescription').value
                const snippetTagValue = document.getElementById('snippettag').value
                const snipdisc21 = document.getElementById("snippetdescription1").value
                const sniptag21 = document.getElementById("snippettag1").value
                const snipdisc31 = document.getElementById("snippetdescription2").value
                const sniptag31 = document.getElementById("snippettag2").value
                const snipdisc41 = document.getElementById("snippetdescription3").value
                const sniptag41 = document.getElementById("snippettag3").value

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

                myObj[blockValue]["languages"][addNewLanguage] =editedJson;
                const finalObject = JSON.stringify(myObj);
                const finalObject1 = finalObject.replaceAll(':null', ':" "' );
                const filename = serviceValue + "_metadata.json"
                var f = new File([myObj], filename, {type: "text/plain"})
                download(filename, finalObject1)
            }
            if (noOfSnippets === 5) {
                const snippetDescValue = document.getElementById('snippetdescription').value
                const snippetTagValue = document.getElementById('snippettag').value
                const snipdisc21 = document.getElementById("snippetdescription1").value
                const sniptag21 = document.getElementById("snippettag1").value
                const snipdisc31 = document.getElementById("snippetdescription2").value
                const sniptag31 = document.getElementById("snippettag2").value
                const snipdisc41 = document.getElementById("snippetdescription3").value
                const sniptag41 = document.getElementById("snippettag3").value
                const snipdisc51 = document.getElementById("snippetdescription4").value
                const sniptag51 = document.getElementById("snippettag4").value

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

                myObj[blockValue]["languages"][addNewLanguage] =editedJson;
                const finalObject = JSON.stringify(myObj);
                const finalObject1 = finalObject.replaceAll(':null', ':" "' );
                const filename = serviceValue + "_metadata.json"
                var f = new File([myObj], filename, {type: "text/plain"})
                download(filename, finalObject1)
            }
            if (noOfSnippets === 6) {
                const snipdisc21 = document.getElementById("snippetdescription1").value
                const sniptag21 = document.getElementById("snippettag1").value
                const snipdisc31 = document.getElementById("snippetdescription2").value
                const sniptag31 = document.getElementById("snippettag2").value
                const snipdisc41 = document.getElementById("snippetdescription3").value
                const sniptag41 = document.getElementById("snippettag3").value
                const snipdisc51 = document.getElementById("snippetdescription4").value
                const sniptag51 = document.getElementById("snippettag4").value
                const snipdisc61 = document.getElementById("snippetdescription5").value
                const sniptag61 = document.getElementById("snippettag5").value
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

                myObj[blockValue]["languages"][addNewLanguage] =editedJson;
                const finalObject = JSON.stringify(myObj);
                const finalObject1 = finalObject.replaceAll(':null', ':" "' );
                const filename = serviceValue + "_metadata.json"
                var f = new File([myObj], filename, {type: "text/plain"})
                download(filename, finalObject1)
            }
        }
        const serviceStub = document.getElementById('selecttheservice').value
        const sourceJson = "../sos_editor/jsonholder/" + serviceStub + "_metadata.json"
        xmlhttp.open("GET", sourceJson, true);
        xmlhttp.send();
    }
    else if(document.getElementById('sdkVersion').value === "Not listed") {
        var xmlhttp = new XMLHttpRequest();
        xmlhttp.onload = function () {
            const addedSDKVersionFloat = parseFloat(addedSDKVersion)
            var myObj = JSON.parse(this.responseText);
            var myObj = myObj;
            myObj[blockValue].title = titleValue;
            myObj[blockValue].title_abbrev = abbrevTitleValue;
            myObj[blockValue].synopsis = synopsisValue;
            myObj[blockValue].synopsis_list= synopsisListValue;
            myObj[blockValue].category = categoryValue;
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
                const finalObject1 = finalObject.replaceAll(':null', ':" "' );
                const filename = serviceValue + "_metadata.json"
                var f = new File([myObj], filename, {type: "text/plain"})
                download(filename, finalObject1)
            }
            if (noOfSnippets === 1) {
                const snippetDescValue = document.getElementById('snippetdescription').value
                const snippetTagValue = document.getElementById('snippettag').value
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
                const finalObject1 = finalObject.replaceAll(':null', ':" "' );
                const filename = serviceValue + "_metadata.json"
                var f = new File([myObj], filename, {type: "text/plain"})
                download(filename, finalObject1)
            }
            if (noOfSnippets === 2) {
                const snippetDescValue = document.getElementById('snippetdescription').value
                const snippetTagValue = document.getElementById('snippettag').value
                const snipdisc2 = document.getElementById("snippetdescription1").value
                const sniptag2 = document.getElementById("snippettag1").value
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
                const finalObject1 = finalObject.replaceAll(':null', ':" "' );
                const filename = serviceValue + "_metadata.json"
                var f = new File([myObj], filename, {type: "text/plain"})
                download(filename, finalObject1)
            }
            if (noOfSnippets === 3) {
                const snippetDescValue = document.getElementById('snippetdescription').value
                const snippetTagValue = document.getElementById('snippettag').value
                const snipdisc2 = document.getElementById("snippetdescription1").value
                const sniptag2 = document.getElementById("snippettag1").value
                const snipdisc3 = document.getElementById("snippetdescription2").value
                const sniptag3 = document.getElementById("snippettag2").value
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
                const finalObject1 = finalObject.replaceAll(':null', ':" "' );
                const filename = serviceValue + "_metadata.json"
                var f = new File([myObj], filename, {type: "text/plain"})
                download(filename, finalObject1)
            }
            if (noOfSnippets === 4) {
                const snippetDescValue = document.getElementById('snippetdescription').value
                const snippetTagValue = document.getElementById('snippettag').value
                const snipdisc2 = document.getElementById("snippetdescription1").value
                const sniptag2 = document.getElementById("snippettag1").value
                const snipdisc3 = document.getElementById("snippetdescription2").value
                const sniptag3 = document.getElementById("snippettag2").value
                const snipdisc4 = document.getElementById("snippetdescription3").value
                const sniptag4 = document.getElementById("snippettag3").value
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
                const finalObject1 = finalObject.replaceAll(':null', ':" "' );
                const filename = serviceValue + "_metadata.json"
                var f = new File([myObj], filename, {type: "text/plain"})
                download(filename, finalObject1)
            }
            if (noOfSnippets === 5) {
                const snippetDescValue = document.getElementById('snippetdescription').value
                const snippetTagValue = document.getElementById('snippettag').value
                const snipdisc2 = document.getElementById("snippetdescription1").value
                const sniptag2 = document.getElementById("snippettag1").value
                const snipdisc3 = document.getElementById("snippetdescription2").value
                const sniptag3 = document.getElementById("snippettag2").value
                const snipdisc4 = document.getElementById("snippetdescription3").value
                const sniptag4 = document.getElementById("snippettag3").value
                const snipdisc5 = document.getElementById("snippetdescription4").value
                const sniptag5 = document.getElementById("snippettag4").value
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
                const finalObject1 = finalObject.replaceAll(':null', ':" "' );
                const filename = serviceValue + "_metadata.json"
                var f = new File([myObj], filename, {type: "text/plain"})
                download(filename, finalObject1)
            }
            if (noOfSnippets === 6) {
                const snippetDescValue = document.getElementById('snippetdescription').value
                const snippetTagValue = document.getElementById('snippettag').value
                const snipdisc2 = document.getElementById("snippetdescription1").value
                const sniptag2 = document.getElementById("snippettag1").value
                const snipdisc3 = document.getElementById("snippetdescription2").value
                const sniptag3 = document.getElementById("snippettag2").value
                const snipdisc4 = document.getElementById("snippetdescription3").value
                const sniptag4 = document.getElementById("snippettag3").value
                const snipdisc5 = document.getElementById("snippetdescription4").value
                const sniptag5 = document.getElementById("snippettag4").value
                const snipdisc6 = document.getElementById("snippetdescription5").value
                const sniptag6 = document.getElementById("snippettag5").value
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
                const finalObject1 = finalObject.replaceAll(':null', ':" "' );
                const filename = serviceValue + "_metadata.json"
                var f = new File([myObj], filename, {type: "text/plain"})
                download(filename, finalObject1)
            }
        }
        const serviceStub = document.getElementById('selecttheservice').value
        const sourceJson = "../sos_editor/jsonholder/" + serviceStub + "_metadata.json"
        xmlhttp.open("GET", sourceJson, true);
        xmlhttp.send();
        var xmlhttp = new XMLHttpRequest();

    }
    else{
        console.log('this is else')
        var xmlhttp = new XMLHttpRequest();
        xmlhttp.onload = function () {
            var myObj = JSON.parse(this.responseText);
            console.log('myObj', myObj)
            myObj[blockValue].title = titleValue;
            myObj[blockValue].title_abbrev= abbrevTitleValue;
            console.log(myObj[blockValue].title_abbrev)
            if(myObj[blockValue].title_abbrev = "undefined"){
                console.log('there is no abbrev');
            }
            if(myObj[blockValue].synopsis_list = "undefined"){
               console.log('there is no synopsis_list');
            }
            myObj[blockValue].synopsis_list = synopsisListValue;
            myObj[blockValue].category = categoryValue;
            const noOfVersions = myObj[blockValue]["languages"][languageValue]["versions"].length;
            let versions = [];
            for (let i = 0; i < noOfVersions; i++) {
                versions.push(myObj[blockValue]["languages"][languageValue]["versions"][i]["sdk_version"]);
            }
            console.log('sdkversion', '"'+ sdkVersion +'"')
            console.log ("versions", versions)
            const newSDKV = parseInt(sdkVersion);
            const myLatestNumber = versions.indexOf(newSDKV);
            console.log('MyLatestNumber', myLatestNumber);

            myObj[blockValue]["languages"][languageValue]["versions"][myLatestNumber].sdk_version = parseFloat(sdkVersion);
            myObj[blockValue]["languages"][languageValue]["versions"][myLatestNumber]["github"] = gitHubValue;
            myObj[blockValue]["languages"][languageValue]["versions"][myLatestNumber]["sdkguide"] = snippetGuideValue;

            const noOfSnippets = document.getElementsByClassName('snippdisc').length

            if (noOfSnippets === 0) {
                const finalObject = JSON.stringify(myObj);
                const finalObject1 = finalObject.replaceAll(':null', ':" "' );
                const filename = serviceValue + "_metadata.json"
                var f = new File([myObj], filename, {type: "text/plain"})
                download(filename, finalObject1)
            }
            if (noOfSnippets === 1) {
                const snippetDescValue = document.getElementById('snippetdescription').value
                const snippetTagValue = document.getElementById('snippettag').value
                const editedJson =
                    [{
                        description: snippetDescValue,
                        snippet_tags: [
                            snippetTagValue
                        ]

                    }]

                myObj[blockValue]["languages"][languageValue]["versions"][myLatestNumber]["excerpts"] = editedJson ;
                console.log('final object', myObj)
                console.log('editedJson', editedJson)
                const finalObject = JSON.stringify(myObj);
                const finalObject1 = finalObject.replaceAll(':null', ':" "' );
                const filename = serviceValue + "_metadata.json"
                var f = new File([myObj], filename, {type: "text/plain"})
                download(filename, finalObject1)
            }
            if (noOfSnippets === 2) {
                console.log('two snippets')
                const snippetDescValue = document.getElementById('snippetdescription').value
                const snippetTagValue = document.getElementById('snippettag').value
                const snipdisc21 = document.getElementById("snippetdescription1").value
                const sniptag21 = document.getElementById("snippettag1").value
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


                myObj[blockValue]["languages"][languageValue]["versions"][myLatestNumber]["excerpts"] = editedJson ;
                const finalObject = JSON.stringify(myObj);
                console.log('finalObject', finalObject)
                const finalObject1 = finalObject.replaceAll(':null', ':" "' );
                console.log('finalObject1', finalObject1)
                const filename = serviceValue + "_metadata.json"
                var f = new File([myObj], filename, {type: "text/plain"})
                download(filename, finalObject1)
            }
            if (noOfSnippets === 3) {
                const snippetDescValue = document.getElementById('snippetdescription').value
                const snippetTagValue = document.getElementById('snippettag').value
                const snipdisc21 = document.getElementById("snippetdescription1").value
                const sniptag21 = document.getElementById("snippettag1").value
                const snipdisc31 = document.getElementById("snippetdescription2").value
                const sniptag31 = document.getElementById("snippettag2").value

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


                myObj[blockValue]["languages"][languageValue]["versions"][myLatestNumber]["excerpts"] = editedJson ;
                const finalObject = JSON.stringify(myObj)
                const filename = serviceValue + "_metadata.json"
                var f = new File([myObj], filename, {type: "text/plain"})
                download(filename, finalObject)
            }
            if (noOfSnippets === 4) {
                const snippetDescValue = document.getElementById('snippetdescription').value
                const snippetTagValue = document.getElementById('snippettag').value
                const snipdisc21 = document.getElementById("snippetdescription1").value
                const sniptag21 = document.getElementById("snippettag1").value
                const snipdisc31 = document.getElementById("snippetdescription2").value
                const sniptag31 = document.getElementById("snippettag2").value
                const snipdisc41 = document.getElementById("snippetdescription3").value
                const sniptag41 = document.getElementById("snippettag3").value

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



                myObj[blockValue]["languages"][languageValue]["versions"][myLatestNumber]["excerpts"] = editedJson ;
                const finalObject = JSON.stringify(myObj)
                const filename = serviceValue + "_metadata.json"
                var f = new File([myObj], filename, {type: "text/plain"})
                download(filename, finalObject)
            }
            if (noOfSnippets === 5) {
                const snippetDescValue = document.getElementById('snippetdescription').value
                const snippetTagValue = document.getElementById('snippettag').value
                const snipdisc21 = document.getElementById("snippetdescription1").value
                const sniptag21 = document.getElementById("snippettag1").value
                const snipdisc31 = document.getElementById("snippetdescription2").value
                const sniptag31 = document.getElementById("snippettag2").value
                const snipdisc41 = document.getElementById("snippetdescription3").value
                const sniptag41 = document.getElementById("snippettag3").value
                const snipdisc51 = document.getElementById("snippetdescription4").value
                const sniptag51 = document.getElementById("snippettag4").value

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

                myObj[blockValue]["languages"][languageValue]["versions"][myLatestNumber]["excerpts"] = editedJson ;
                const finalObject = JSON.stringify(myObj)
                const filename = serviceValue + "_metadata.json"
                var f = new File([myObj], filename, {type: "text/plain"})
                download(filename, finalObject)
            }
            if (noOfSnippets === 6) {
                const snippetDescValue = document.getElementById('snippetdescription').value
                const snippetTagValue = document.getElementById('snippettag').value
                const snipdisc21 = document.getElementById("snippetdescription1").value
                const sniptag21 = document.getElementById("snippettag1").value
                const snipdisc31 = document.getElementById("snippetdescription2").value
                const sniptag31 = document.getElementById("snippettag2").value
                const snipdisc41 = document.getElementById("snippetdescription3").value
                const sniptag41 = document.getElementById("snippettag3").value
                const snipdisc51 = document.getElementById("snippetdescription4").value
                const sniptag51 = document.getElementById("snippettag4").value
                const snipdisc61 = document.getElementById("snippetdescription5").value
                const sniptag61 = document.getElementById("snippettag5").value
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


                myObj[blockValue]["languages"][languageValue]["versions"][myLatestNumber]["excerpts"] = editedJson ;
                const finalObject = JSON.stringify(myObj)
                const filename = serviceValue + "_metadata.json"
                var f = new File([myObj], filename, {type: "text/plain"})
                download(filename, finalObject)
            }
        }
        const serviceStub = document.getElementById('selecttheservice').value
        const sourceJson = "../sos_editor/jsonholder/" + serviceStub + "_metadata.json"
        console.log('sourceJson', sourceJson)
        xmlhttp.open("GET", sourceJson, true);
        xmlhttp.send();
    }
    const serviceStub = document.getElementById('selecttheservice').value
/*    setTimeout(function(){
        alert('Success. To save these changes your local metadata, navigate in your CLI to your metadata/sos_editor folder and run \'node update_yaml.js ' +serviceStub + '\'.' + '\n' +
            '\n' +
            'Note: You can ignore these changes and restart by deleting the ' +serviceStub + '_metadata.json file from your Downloads folder.')
        window.location.reload();
    }, 3000);*///wait 2 second
};
$("#selectService").on('click', function() {
    console.log('selectService clicked')
    if (document.getElementById('selecttheservice').value != "") {
        document.getElementById('selectService').disabled = true;
        var element = document.getElementById('selectBlockView');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('selectBlock');
        element.setAttribute("style", "visibility: visible");
        var xmlhttp = new XMLHttpRequest();
        xmlhttp.onload = function () {
            console.log('tried to')
            var myObj = JSON.parse(this.responseText);
            const selectedItem = Object.keys(myObj)[1];
            console.log(selectedItem);
            const myBlocks = Object.keys(myObj);
            console.log('myBlocks', myBlocks);
            var newBlocks = myBlocks.push('Not listed')
            console.log(newBlocks)
            console.log(myBlocks)
            var select = document.getElementById("selectBlock");
            var options = myBlocks;
            for (var i = 0; i < options.length; i++) {
                var opt = options[i];
                var el = document.createElement("option");
                el.textContent = opt;
                el.value = opt;
                select.appendChild(el);
            }

        }
        const serviceStub = document.getElementById('selecttheservice').value
        const sourceJson = "../sos_editor/jsonholder/" + serviceStub + "_metadata.json"
        xmlhttp.open("GET", sourceJson, true);
        xmlhttp.send();
    } if(document.getElementById('selecttheservice').value == "") {
        alert('You must enter the service stub');
    }
});

$("#selectBlock").on('change', function() {
    document.getElementById('selectBlock').disabled = true;
    if (document.getElementById("selectBlock").value === "Not listed") {
        var element = document.getElementById('addBlockName');
        element.setAttribute("style", "visibility: visible");
        const myService = document.getElementById('selecttheservice').value;
        theService = myService + "_"
        document.getElementById('blockname').value = theService;
    } else {
        var element = document.getElementById('addTitle');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('addAbbTitle');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('addSynopsis');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('addSynopsisList');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('addCategory');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('languages');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('chooseLangView');
        element.setAttribute("style", "visibility: visible");
        var select = document.getElementById("languages");
    }
    myfunction();
    myfunction2();
    myfunction3();
    myfunction34();
    myfunction35();

});
$("#addServiceBtn").on('click', function() {
    document.getElementById('addServiceBtn').disabled = true;
    console.log('button clicked')
    var element = document.getElementById('addBlockName');
    element.setAttribute("style", "visibility: visible");
    if(document.getElementById('selecttheservice').value === "Not listed") {
        myService = document.getElementById('service').value;
        theService = myService.slice(0, myService.lastIndexOf('_')) + "_";
        document.getElementById('blockname').value = theService;
    }
});
$("#compBlockBtn").on('click', function() {
    document.getElementById('compBlockBtn').disabled = true;
    console.log('button clicked')
    var element = document.getElementById('addBlockName');
    element.setAttribute("style", "visibility: visible");
    var element = document.getElementById('addTitle');
    element.setAttribute("style", "visibility: visible");
    var element = document.getElementById('addAbbTitle');
    element.setAttribute("style", "visibility: visible");
    var element = document.getElementById('addSynopsis');
    element.setAttribute("style", "visibility: visible");
    var element = document.getElementById('addSynopsisList');
    element.setAttribute("style", "visibility: visible");
    var element = document.getElementById('addCategory');
    element.setAttribute("style", "visibility: visible");
    var element = document.getElementById('chooseLangView');
    element.setAttribute("style", "visibility: visible");
    var element = document.getElementById('languages');
    element.setAttribute("style", "visibility: visible");
    var select = document.getElementById("languages");
    var options = ["SAP ABAP", "Bash", "C++","DotNet", "Go", "Kotlin", "Java", "JavaScript", "PHP", "Python", "Ruby", "Rust", "Swift", "Not Listed"];
    for (var i = 0; i < options.length; i++) {
        var opt = options[i];
        var el = document.createElement("option");
        el.textContent = opt;
        el.value = opt;
        select.appendChild(el);
    }
});
$("#languages").on('change', function() {
    const titleValue = document.getElementById('thetitle').value
    const abbrevTitleValue = document.getElementById('addatitle').value
    const synopsisValue = document.getElementById('synopsis').value
    const synopsisListValue = document.getElementById('synopsislist').value
    if(titleValue==""||abbrevTitleValue==""||synopsisValue==""&&synopsisListValue==""){
        alert('You must have a title, abbreviated title, and a synopsis or synopsis list.')
        document.getElementById("languages").selectedIndex = 0;

        return
    }
    document.getElementById('languages').disabled = true;

    console.log('button clicked');
    var selectedLanguage = document.getElementById('languages').value
    var searchKey = document.getElementById('selectBlock').value
    console.log('selectedLanguage', selectedLanguage)
    if(selectedLanguage == "Not listed"){
        var element = document.getElementById('addlanguage');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('addLanguageView');
        element.setAttribute("style", "visibility: visible");
        let currentLangs = [];
        var values = $("#languages>option").map(function() { return $(this).val(); }).get();
        currentLangs.push(values);
        mycurrentLangs = currentLangs[0].splice(0,1);
        const mynewcurrentlangs = currentLangs[0].splice(-1,1);
        console.log('currentLangs', currentLangs)
        var allOptions = ["SAP ABAP", "Bash", "C++","DotNet", "Go", "Kotlin", "Java", "JavaScript", "PHP", "Python", "Ruby", "Rust", "Swift", "Not Listed"];
        var options = $.grep(allOptions, function(value) {
            return $.inArray(value, currentLangs[0]) < 0;
        });
        console.log('options',options)
        var select = document.getElementById("addlanguage");
        for (var i = 0; i < options.length; i++) {
            var opt = options[i];
            var el = document.createElement("option");
            el.textContent = opt;
            el.value = opt;
            select.appendChild(el);
        }
    }
    if(searchKey == "Not listed"){
        var element = document.getElementById('addSdkVersion');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('chooseSDKView');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('gitHub');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('sdkGuide');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('anotherSnippet');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('removeSnippet');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('createYAML');
        element.setAttribute("style", "visibility: visible");
        myfunction4();
    }
    if(selectedLanguage != "Not listed" && searchKey != "Not listed"){
        var element = document.getElementById('sdkVersion');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('chooseSDKView');
        element.setAttribute("style", "visibility: visible");
        myfunction4();
    }
});
$("#addlanguage").on('change', function() {

    document.getElementById('addlanguage').disabled = true;

    var element = document.getElementById('addSdkVersion');
    element.setAttribute("style", "visibility: visible");
});

$("#subVersion").on('click', function() {
    console.log('why')
    if(document.getElementById('languages').value !== "Not listed") {
        alert('You may need to add an entry for this version in the \'sdk \' section of the mapping.yaml.')
        let currentVersions = [];
        var values = $("#sdkVersion>option").map(function () {
            return $(this).val();
        }).get();
        currentVersions.push(values);
        const mynewcurrentVersions = currentVersions[0].splice(-1, 1);
        console.log('currentVersions1', currentVersions)
        const valueToCheck = document.getElementById('addskdversion').value
        if (currentVersions[0].includes(valueToCheck) === true) {
            console.log('says it does')
            var element = document.getElementById('addSdkVersion');
            element.setAttribute("style", "class:show-when-target");
            alert('This SDK version already exists. Please select it.')
            document.getElementById('sdkVersion').disabled = false;
        }
        else{
            document.getElementById('sdkVersion').disabled = true;
            var element = document.getElementById('gitHub');
            element.setAttribute("style", "visibility: visible");
            var element = document.getElementById('sdkGuide');
            element.setAttribute("style", "visibility: visible");
            var element = document.getElementById('anotherSnippet');
            element.setAttribute("style", "visibility: visible");
            var element = document.getElementById('removeSnippet');
            element.setAttribute("style", "visibility: visible");
            var element = document.getElementById('createYAML');
            element.setAttribute("style", "visibility: visible");
        }
    }
    else{
        document.getElementById('subVersion').disabled = true;
        alert('You may need to add an entry for this version in the \'sdk \' section of the mapping.yaml.')
        var element = document.getElementById('gitHub');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('sdkGuide');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('anotherSnippet');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('removeSnippet');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('createYAML');
        element.setAttribute("style", "visibility: visible");
    }
});

$("#sdkVersion").on('change', function() {
    document.getElementById('sdkVersion').disabled = true;

    if(document.getElementById('sdkVersion').value == "Not listed"){
        console.log('its happening')
        var element = document.getElementById('addSdkVersion');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('addSdkVersion');
        element.setAttribute("style", "visibility: visible");
    }
    else {
        var element = document.getElementById('gitHub');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('sdkGuide');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('anotherSnippet');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('removeSnippet');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('createYAML');
        element.setAttribute("style", "visibility: visible");
        myfunction5();
        myfunction6();
        myfunction7();
    }
});
