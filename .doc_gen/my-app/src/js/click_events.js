// Submit service details.
function selectService() {
    if (document.getElementById('selecttheservice').value != "") {
        document.getElementById('selectService').disabled = true;
        var element = document.getElementById('selectBlockView');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('selectBlock');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('selectService');
        element.setAttribute("style", "visibility: collapse");
        var xmlhttp = new XMLHttpRequest();
        xmlhttp.onload = function () {
            var myObj = JSON.parse(this.responseText);
            console.log('myobject', myObj)
            if (myObj === null) {
                const myBlocks = [];
                var newBlocks = myBlocks.push('Not Listed')
                var select = document.getElementById("selectBlock");
                var options = myBlocks;
                for (var i = 0; i < options.length; i++) {
                    var opt = options[i];
                    var el = document.createElement("option");
                    el.textContent = opt;
                    el.value = opt;
                    select.appendChild(el);
                }
            } else {
                const myBlocks = Object.keys(myObj);
                console.log('myBlocks', myBlocks);
                var newBlocks = myBlocks.push('Not Listed')
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

        }
        const serviceStub = document.getElementById('selecttheservice').value
        const sourceJson = "./jsonholder/" + serviceStub + "_metadata.json"
        xmlhttp.open("GET", sourceJson, true);
        xmlhttp.send();
    }
};

// Confirm user has entered a service before proceeding.
$("#selectService").on('click', function () {
    console.log('selectService clicked')
    if (document.getElementById('selecttheservice').value == "") {
        myAlert('Alert', 'You must enter the service stub'); // call it here
        return
    } else {
        selectService();
    }
});

// Display controls to receive api commands for each additional service.
$("#selectServices").on('click', function () {
    console.log('selectServices clicked')
    if (document.getElementById('selecttheservices').value != "") {
        const services = document.getElementById('selecttheservices').value.split(",");
        console.log('services', services);
        let myServices = [];
        myServices.push(services);
        console.log("myServices", myServices);
        const noOfServices = myServices.length;
        console.log('noOfServices', noOfServices);
        let i
        for (i = 0; i < services.length; i++) {
            const additionalserviceAPIsName = "API commands for " + services[i] + " (optional)";
            const additionalserviceAPIs = "addServiceAPI" + i;
            $(
                "<p id =apiCommands ><label class=thissize>" + additionalserviceAPIsName + "</label><br><textarea class=additionalserviceapi type=text name=text id =" + additionalserviceAPIs + " maxlength=200></textarea></p>" +
                "").insertBefore("#selectService");
        }
    }
});

// Submit selected block name to create, or edit if it already exists.
$("#selectBlock").on('change', function () {
    const services = document.getElementById('selecttheservices').value.split(",");
    console.log('services', services);
    document.getElementById('selectBlock').disabled = true;
    var element = document.getElementById('apiCommands');
    element.setAttribute("style", "visibility: visible");
    var element = document.getElementById('commands');
    element.setAttribute("style", "visibility: visible");
    if (document.getElementById('selecttheservices').value != "") {
        let i
        for (i = 0; i < services.length; i++) {
            const additionalserviceAPIsName = "API commands for " + services[i] +" (optional)";
            const additionalserviceAPIs = "addServiceAPI" + i;
            $(
                "<p id =addAPICommands ><label class=thissize>" + additionalserviceAPIsName + "</label><br><textarea class=additionalserviceapi type=text name=text id =" + additionalserviceAPIs + " maxlength=200></textarea></p>" +
                "").insertAfter("#apiCommands");
        }
        for (i = 0; i < services.length; i++){


        }
    }
    if (document.getElementById("selectBlock").value === "Not Listed") {
        var element = document.getElementById('addBlockName');
        element.setAttribute("style", "visibility: visible");
        const myService = document.getElementById('selecttheservice').value;
        var theService = myService + "_"
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
        myfunction(); // Populate and display control for selecting block to add/edit.
        myfunction2();
        myfunction3();
        myfunction35();
        myfunction36();
    }
});

$("#addServiceBtn").on('click', function () {
    document.getElementById('addServiceBtn').disabled = true;
    console.log('button clicked')
    var element = document.getElementById('addBlockName');
    element.setAttribute("style", "visibility: visible");
    if (document.getElementById('selecttheservice').value === "Not Listed") {
        var myService = document.getElementById('service').value;
        var theService = myService.slice(0, myService.lastIndexOf('_')) + "_";
        document.getElementById('blockname').value = theService;
    }
});

$("#compBlockBtn").on('click', function () {
    document.getElementById('compBlockBtn').disabled = true;
    var compBlock = document.getElementById('compBlockBtn').value;
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
    if(compBlock = "Not Listed"){
        var options = ["SAP ABAP", "Bash", "C++", "DotNet", "Go", "Kotlin", "Java", "JavaScript", "PHP", "Python", "Ruby", "Rust", "Swift"];
        for (var i = 0; i < options.length; i++) {
            var opt = options[i];
            var el = document.createElement("option");
            el.textContent = opt;
            el.value = opt;
            select.appendChild(el);
        }
    }
    else if(compBlock != "Not Listed"){
        var options = ["SAP ABAP", "Bash", "C++", "DotNet", "Go", "Kotlin", "Java", "JavaScript", "PHP", "Python", "Ruby", "Rust", "Swift", "Not Listed"];
        for (var i = 0; i < options.length; i++) {
            var opt = options[i];
            var el = document.createElement("option");
            el.textContent = opt;
            el.value = opt;
            select.appendChild(el);
        }
    }
});

$("#languages").on('change', function () {
    console.log('languages clicked')
    const titleValue = document.getElementById('thetitle').value
    const abbrevTitleValue = document.getElementById('addatitle').value
    const synopsisValue = document.getElementById('synopsis').value
    const synopsisListValue = document.getElementById('synopsislist').value
    if (titleValue == "" || abbrevTitleValue == "" || synopsisValue == "" && synopsisListValue == "") {
        myAlert('alert','You must have a title, abbreviated title, and either a synopsis or synopsis list.')

        document.getElementById("languages").selectedIndex = 0;
        return
    }
    document.getElementById('languages').disabled = true;
    console.log('button clicked');
    var selectedLanguage = document.getElementById('languages').value
    var searchKey = document.getElementById('selectBlock').value
    console.log('selectedLanguage', selectedLanguage)
    if (selectedLanguage != "Not Listed"&& searchKey != "Not Listed") {
        console.log('Editing existing block and language');
        var element = document.getElementById('sdkVersion');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('chooseSDKView');
        element.setAttribute("style", "visibility: visible");
        myfunction4();                                                                                                                                     
}
    // The language isn't covered yet. This should cover adding a new language.
    if (selectedLanguage == "Not Listed"&& searchKey == "Not Listed") {
        console.log('Language and block are not Listed...');
        var element = document.getElementById('addlanguage');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('addLanguageView');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('sdkVersion');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('chooseSDKView');
        element.setAttribute("style", "visibility: visible");
        var allOptions = ["SAP ABAP", "Bash", "C++", "DotNet", "Go", "Kotlin", "Java", "JavaScript", "PHP", "Python", "Ruby", "Rust", "Swift"];
        var select = document.getElementById("addlanguage");
        for (var i = 0; i < allOptions.length; i++) {
            var opt = allOptions[i];
            var el = document.createElement("option");
            el.textContent = opt;
            el.value = opt;
            select.appendChild(el);
        }
        myfunction4();
    }

    else if (selectedLanguage == "Not Listed") {
        console.log('Language is not Listed...');
            var element = document.getElementById('addlanguage');
            element.setAttribute("style", "visibility: visible");
            var element = document.getElementById('addLanguageView');
            element.setAttribute("style", "visibility: visible");
            let currentLangs = [];
            var values = $("#languages>option").map(function () {
                return $(this).val();
            }).get();
            currentLangs.push(values);
            var mycurrentLangs = currentLangs[0].splice(0, 1);
            console.log('currentLangs', currentLangs)
            var allOptions = ["SAP ABAP", "Bash", "C++", "DotNet", "Go", "Kotlin", "Java", "JavaScript", "PHP", "Python", "Ruby", "Rust", "Swift", "Not Listed"];
            var options = $.grep(allOptions, function (value) {
                return $.inArray(value, currentLangs[0]) < 0;
            });
            console.log('options', options)
            var select = document.getElementById("addlanguage");
            for (var i = 0; i < allOptions.length; i++) {
                var opt = options[i];
                var el = document.createElement("option");
                el.textContent = opt;
                el.value = opt;
                select.appendChild(el);
            }
        }
    // It's a new operation.
    else if (searchKey == "Not Listed") {
        console.log('Block is not Listed...');
        var element = document.getElementById('addSdkVersion');
        /*element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('code');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('codeExample');*/
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('chooseSDKView');
        element.setAttribute("style", "visibility: visible");
         /*var element = document.getElementById('code');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('codeExample');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('code');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('codeExample');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('code');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('codeExample');*/
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('gitHub');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('sdkGuide');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('selectTagOrFile');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('selecttagorfile');
        element.setAttribute("style", "visibility: visible");
        var select = document.getElementById("selecttagorfile");
        var options = ["Snippet tags", "Snippet files"];
        for (var i = 0; i < options.length; i++) {
            var opt = options[i];
            var el = document.createElement("option");
            el.textContent = opt;
            el.value = opt;
            select.appendChild(el);
        }
        myfunction4();
    }
});


$("#selecttagorfile").on('change', function (){
    var selectedOption = document.getElementById('selecttagorfile').value
    document.getElementById('selecttagorfile').disabled = true;
    if (selectedOption === "Snippet files") {
        console.log('Snippet files selected')
        $(
            "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description<i> (optional)</i></label><textarea  class=\"snippfiledisc\" type=\"text\" name=\"text\" id =\"snippetfiledescription1\" maxlength=\"200\"></textarea></p>\n" +
            "      <p id =\"snippet\" ><label class=\"thissize\">Snippet file<i>(mandatory)</i></label><textarea class=\"snippfile\" type=\"text\" name=\"text\" id =\"snippetfile1\" maxlength=\"200\"></textarea></p>\n" +
            "    <p id=\"addSnippetFile\" ><button id=\"newsnippetfile\" onclick=\"newsnippetfile()\">Add a snippet file</button></p>" +
            "       ").insertBefore("#createYAML");
        /*var element = document.getElementById('addSnippetFile');
        element.setAttribute("style", "visibility: visible");*/
       /* var element = document.getElementById('removeSnippetFile');
        element.setAttribute("style", "visibility: visible"); */
        var element = document.getElementById('createYAML');
        element.setAttribute("style", "visibility: visible");
       /*$('#newsnippetfile').click();*/
    }
   if (selectedOption === "Snippet tags"){
       console.log('Snippet tags selected')
       $(
           "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description<i>(optional)</i></label><textarea  class=\"snippdisc\" type=\"text\" name=\"text\" id =\"snippetdescription1\" maxlength=\"200\"></textarea></p>\n" +
           "      <p id =\"snippet\" ><label class=\"thissize\">Snippet tag<i>(mandatory)</i></label><textarea class=\"snipptag\" type=\"text\" name=\"text\" id =\"snippettag1\" maxlength=\"200\"></textarea></p>\n" +
           "    <p id=\"anotherSnippet\" ><button id=\"newsnippet\">Add a snippet tag</button></p>" +
           "       ").insertBefore("#createYAML");
      /*  var element = document.getElementById('anotherSnippet');
        element.setAttribute("style", "visibility: visible");*/
        /*var element = document.getElementById('removeSnippet');
        element.setAttribute("style", "visibility: visible");*/
        var element = document.getElementById('createYAML');
        element.setAttribute("style", "visibility: visible");
      /*$('#newsnippet').click();*/
    }
});


$("#addlanguage").on('change', function () {
    document.getElementById('addlanguage').disabled = true;
    var element = document.getElementById('addSdkVersion');
    element.setAttribute("style", "visibility: visible");
});

$("#subVersion").on('click', function () {
    console.log('why')
    if (document.getElementById('languages').value !== "Not Listed") {
        myAlert('alert','Check this version in defined in the ../../sdk.yaml.')
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
            myAlert('alert', 'This SDK version already exists. Please select it.')
            document.getElementById('sdkVersion').disabled = false;
        } else {
            document.getElementById('sdkVersion').disabled = true;
            var element = document.getElementById('gitHub');
            element.setAttribute("style", "visibility: visible");
            var element = document.getElementById('sdkGuide');
            element.setAttribute("style", "visibility: visible");
            var element = document.getElementById('apiCommands');
            element.setAttribute("style", "visibility: visible");
            var element = document.getElementById('commands');
            element.setAttribute("style", "visibility: collapse");
            var element = document.getElementById('selectTagOrFile');
            element.setAttribute("style", "visibility: visible");
            var element = document.getElementById('selecttagorfile');
            element.setAttribute("style", "visibility: visible");
            var select = document.getElementById("selecttagorfile");
            var options = ["Snippet tags", "Snippet files"];
            for (var i = 0; i < options.length; i++) {
                var opt = options[i];
                var el = document.createElement("option");
                el.textContent = opt;
                el.value = opt;
                select.appendChild(el);
            }
        }
    } else {
        document.getElementById('subVersion').disabled = true;
        myAlert('alert','Check this version in defined in the ../../sdk.yaml.')
        var element = document.getElementById('gitHub');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('sdkGuide');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('apiCommands');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('commands');
        element.setAttribute("style", "visibility: collapse");
        var element = document.getElementById('selectTagOrFile');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('selecttagorfile');
        element.setAttribute("style", "visibility: visible");
        var select = document.getElementById("selecttagorfile");
        var options = ["Snippet tags", "Snippet files"];
        for (var i = 0; i < options.length; i++) {
            var opt = options[i];
            var el = document.createElement("option");
            el.textContent = opt;
            el.value = opt;
            select.appendChild(el);
        }
    }
});

$("#sdkVersion").on('change', function () {
    document.getElementById('sdkVersion').disabled = true;

    if (document.getElementById('sdkVersion').value == "Not Listed") {
        console.log('its happening')
        var element = document.getElementById('addSdkVersion');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('addSdkVersion');
        element.setAttribute("style", "visibility: visible");
    } else {
        var element = document.getElementById('gitHub');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('sdkGuide');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('apiCommands');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('commands');
        element.setAttribute("style", "visibility: visible");
/*        var element = document.getElementById('anotherSnippet');
        element.setAttribute("style", "visibility: visible");*/
       /* var element = document.getElementById('removeSnippet');*/
       /* element.setAttribute("style", "visibility: visible");*/
        var element = document.getElementById('createYAML');
        element.setAttribute("style", "visibility: visible");
        myfunction4();
        myfunction5();
        myfunction6();
        myfunction7();
    }

});
/*$("#newsnippet").on('click', function () {*/
function newsnippet(){
    const noOfSnippets = document.getElementsByClassName('snippdisc').length
    if (noOfSnippets === 0) {
        console.log('0 snippetTags')
        $(
          "<p id =\"code\"><label class=\"thissize\">Create and download snippet file (optional)</label><br>" +
            "<label class=\"thissize\">Step 1. Enter unique snippet file name</label><br><i>This name is used to format the snippet tag \"//snippet-start:[service].example_code.[language].[snippetName]\", so must be unique in any other snippet tag for the primary service.</i><br><textarea type=\"text\" name=\"text\" id =\"codeExampleTitle\"></textarea><br>"+
            "<label class=\"thissize\">Step 2. Paste snippet below</label><br><textarea type=\"text\" name=\"text\" id =\"codeExample\"></textarea><br>" +
            "<button id = \"createCode\" onclick=\"create_code_example_tag(1, document.getElementById('codeExampleTitle').value)\">Create snippet</button></p>\n" +
            "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Tag Description <i>(optional)</i></label><br><textarea  class=\"snippdisc\" type=\"text\" name=\"text\" id =\"snippetdescription1\" maxlength=\"200\"></textarea></p>\n" +
            "      <p id =\"snippet\" ><label class=\"thissize\">Snippet tag <i>(mandatory)</i></label></label><br><textarea class=\"snipptag\" type=\"text\" name=\"text\" id =\"snippettag1\" maxlength=\"200\"></textarea></p>\n" +
       "").insertBefore("#newsnippet");
    }
    if (noOfSnippets === 1) {
               console.log('1 snippetTags')
        $(
          "<p id =\"code\"><label class=\"thissize\">Create and download snippet file (optional)</label><br>" +
            "<label class=\"thissize\">Step 1. Enter unique snippet file name</label><br><i>This name is used to format the snippet tag \"//snippet-start:[service].example_code.[language].[snippetName]\", and must be unique in any other snippet tag for the primary service.</i><br><textarea type=\"text\" name=\"text\" id =\"codeExampleTitle\"></textarea><br>"+
            "<label class=\"thissize\">Step 2. Paste snippet below</label><br><textarea type=\"text\" name=\"text\" id =\"codeExample\"></textarea><br>" +
            "<button id = \"createCode\" onclick=\"create_code_example_tag(2, document.getElementById('codeExampleTitle').value)\">Create snippet</button></p>\n" +
             "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Tag Description <i>(optional)</i></label><br><textarea  class=\"snippdisc\" type=\"text\" name=\"text\" id =\"snippetdescription2\" maxlength=\"200\"></textarea></p>\n" +
              "      <p id =\"snippet\" ><label class=\"thissize\">Snippet tag <i>(mandatory)</i></label><br><textarea class=\"snipptag\" type=\"text\" name=\"text\" id =\"snippettag2\" maxlength=\"200\"></textarea></p>\n" +
             "").insertBefore("#newsnippet");
    }
    if (noOfSnippets === 2) {
          console.log('2 snippetTags')
        $(
            "<label class=\"thissize\">Step 1. Enter unique snippet file name</label><br><i>This name is used to format the snippet tag \"//snippet-start:[service].example_code.[language].[snippetName]\", and must be unique in any other snippet tag for the primary service.</i><br><textarea type=\"text\" name=\"text\" id =\"codeExampleTitle\"></textarea><br>"+
            "<label class=\"thissize\">Step 2. Paste snippet below</label><br><textarea type=\"text\" name=\"text\" id =\"codeExample\"></textarea><br>" +
            "<button id = \"createCode\" onclick=\"create_code_example_tag(3, document.getElementById('codeExampleTitle').value)\">Create snippet</button></p>\n" +
             "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Tag Description <i>(optional)</i></label><br><textarea  class=\"snippdisc\" type=\"text\" name=\"text\" id =\"snippetdescription3\" maxlength=\"200\"></textarea></p>\n" +
              "      <p id =\"snippet\" ><label class=\"thissize\">Snippet tag <i>(mandatory)</i></label><br><textarea class=\"snipptag\" type=\"text\" name=\"text\" id =\"snippettag3\" maxlength=\"200\"></textarea></p>\n" +
                "").insertBefore("#newsnippet");
    }
    if (noOfSnippets === 3) {
                    $(
            "<label class=\"thissize\">Step 1. Enter unique snippet file name</label><br><i>This name is used to format the snippet tag \"//snippet-start:[service].example_code.[language].[snippetName]\", and must be unique in any other snippet tag for the primary service.</i><br><textarea type=\"text\" name=\"text\" id =\"codeExampleTitle\"></textarea><br>"+
            "<label class=\"thissize\">Step 2. Paste snippet below</label><br><textarea type=\"text\" name=\"text\" id =\"codeExample\"></textarea><br>" +
            "<button id = \"createCode\" onclick=\"create_code_example_tag(4, document.getElementById('codeExampleTitle').value)\">Create snippet</button></p>\n" +
             "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Tag Description <i>(optional)</i></label><br><textarea  class=\"snippdisc\" type=\"text\" name=\"text\" id =\"snippetdescription4\" maxlength=\"200\"></textarea></p>\n" +
              "      <p id =\"snippet\" ><label class=\"thissize\">Snippet tag <i>(mandatory)</i></label><br><textarea class=\"snipptag\" type=\"text\" name=\"text\" id =\"snippettag4\" maxlength=\"200\"></textarea></p>\n" +
                     "").insertBefore("#newsnippet");
    }
    if (noOfSnippets === 4) {
        $(
            "<label class=\"thissize\">Step 1. Enter unique snippet file name</label><br><i>This name is used to format the snippet tag \"//snippet-start:[service].example_code.[language].[snippetName]\", and must be unique from snippet tag for the primary service.</i><br><textarea type=\"text\" name=\"text\" id =\"codeExampleTitle\"></textarea><br>"+
            "<label class=\"thissize\">Step 2. Paste snippet below</label><br><textarea type=\"text\" name=\"text\" id =\"codeExample\"></textarea><br>" +
            "<button id = \"createCode\" onclick=\"create_code_example_tag(5, document.getElementById('codeExampleTitle').value)\">Create snippet</button></p>\n" +
             "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Tag Description <i>(optional)</i></label><br><textarea  class=\"snippdisc\" type=\"text\" name=\"text\" id =\"snippetdescription5\" maxlength=\"200\"></textarea></p>\n" +
              "      <p id =\"snippet\" ><label class=\"thissize\">Snippet tag <i>(mandatory)</i></label><br><textarea class=\"snipptag\" type=\"text\" name=\"text\" id =\"snippettag5\" maxlength=\"200\"></textarea></p>\n" +
                      "").insertBefore("#newsnippet");
    }
    if (noOfSnippets === 5) {
        $(
            "<label class=\"thissize\">Step 1. Enter unique snippet file name</label><br><i>This name is used to format the snippet tag \"//snippet-start:[service].example_code.[language].[snippetName]\", and must be unique from snippet tag for the primary service.</i><br><textarea type=\"text\" name=\"text\" id =\"codeExampleTitle\"></textarea><br>"+
            "<label class=\"thissize\">Step 2. Paste snippet below</label><br><textarea type=\"text\" name=\"text\" id =\"codeExample\"></textarea><br>" +
            "<button id = \"createCode\" onclick=\"create_code_example_tag(5, document.getElementById('codeExampleTitle').value)\">Create snippet</button></p>\n" +
            "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Tag Description <i>(optional)</i></label><br><textarea  class=\"snippdisc\" type=\"text\" name=\"text\" id =\"snippetdescription6\" maxlength=\"200\"></textarea></p>\n" +
            "      <p id =\"snippet\" ><label class=\"thissize\">Snippet tag <i>(mandatory)</i></label><br><textarea class=\"snipptag\" type=\"text\" name=\"text\" id =\"snippettag6\" maxlength=\"200\"></textarea></p>\n" +
            "").insertBefore("#newsnippet");
    }
    if (noOfSnippets === 6) {

        myAlert('alert', 'Maximum of 6 snippets allowed.');
    }
};
function countValues(dropdown){
    var dropDown=dropdown;
    var count=dropDown.options.length
    return count
}

/* $("#removesnip").on('click', function () {
    console.log('clicked')
    const noOfSnippets = document.getElementsByClassName('snippdisc').length;
    console.log('noOfSnippets', noOfSnippets)
    if (noOfSnippets == 0) {
            myAlert('alert', 'There are no snippets to remove.');
    } else {
        $("p[id=snippDesc]:last").remove();
        $("p[id=snippet]:last").remove();
        var element = document.getElementById('anotherSnippet');
        element.setAttribute("style", "visibility: collapse");
        var element = document.getElementById('removeSnippet');
        element.setAttribute("style", "visibility: collapse");
        var element = document.getElementById('createYAML');
        element.setAttribute("style", "visibility: collapse");
        var element = document.getElementById('selectTagOrFile');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('selecttagorfile');
        element.setAttribute("style", "visibility: visible");
        var select = document.getElementById("selecttagorfile");
        if(countValues(select)>=2){
        }
        else{
            var options = ["Snippet tags", "Snippet files"];
            for (var i = 0; i < options.length; i++) {
                var opt = options[i];
                var el = document.createElement("option");
                el.textContent = opt;
                el.value = opt;
                select.appendChild(el);
            }
        }
        document.getElementById('selecttagorfile').disabled = false;
    }
});*/

//after remove, if there are no snippets left, offer to enable them to add snippet files or tags.
/*$("#newsnippetfile").on('click', function () {*/
function newsnippetfile(){
    const noOfSnippetFiles = document.getElementsByClassName('snippfiledisc').length

    if (noOfSnippetFiles == 1) {
        $(
            "<p id =\"snippFileDesc\" ><label class=\"thissize\">  Snippet File Description (optional)</label><br><textarea  class=\"snippfiledisc\" type=\"text\" name=\"text\" id =\"snippetfiledescription2\" maxlength=\"200\"></textarea></p>\n" +
            "      <p id =\"snippetFile\" ><label class=\"thissize\">Snippet File <i>(mandatory)</i><br><i style=\"font-weight:lighter\">Recommended format is \"[service].example_code.[language].[uniqueSnippetName]\" - unique from the final part of any other snippet tag for the primary service.</i><br></label><br><textarea class=\"snippfile\" type=\"text\" name=\"text\" id =\"snippetfile2\" maxlength=\"200\"></textarea></p>\n" +
            "").insertBefore("#addSnippetFile");
    }
    if (noOfSnippetFiles == 2) {
        $(
            "<p id =\"snippFileDesc\" ><label class=\"thissize\">  Snippet File Description (optional)</label><br><textarea  class=\"snippfiledisc\" type=\"text\" name=\"text\" id =\"snippetfiledescription3\" maxlength=\"200\"></textarea></p>\n" +
            "      <p id =\"snippetFile\" ><label class=\"thissize\">Snippet File <i>(mandatory)</i><br><i style=\"font-weight:lighter\">Recommended format is \"[service].example_code.[language].[uniqueSnippetName]\" - unique from the final part of any other snippet tag for the primary service.</i><br></label><br><textarea class=\"snippfile\" type=\"text\" name=\"text\" id =\"snippetfile3\" maxlength=\"200\"></textarea></p>\n" +
            "").insertBefore("#addSnippetFile");
    }
    if (noOfSnippetFiles == 3) {
        $(
            "<p id =\"snippFileDesc\" ><label class=\"thissize\">  Snippet File Description (optional)</label><br><textarea  class=\"snippfiledisc\" type=\"text\" name=\"text\" id =\"snippetfiledescription4\" maxlength=\"200\"></textarea></p>\n" +
            "      <p id =\"snippetFile\" ><label class=\"thissize\">Snippet File <i>(mandatory)</i><br><i style=\"font-weight:lighter\">Recommended format is \"[service].example_code.[language].[uniqueSnippetName]\" - unique from the final part of any other snippet tag for the primary service.</i><br></label><br><textarea class=\"snippfile\" type=\"text\" name=\"text\" id =\"snippetfile4\" maxlength=\"200\"></textarea></p>\n" +
            "").insertBefore("#addSnippetFile");
    }
    if (noOfSnippetFiles == 4) {
        $(
            "<p id =\"snippFileDesc\" ><label class=\"thissize\">  Snippet File Description (optional)</label><br><textarea  class=\"snippfiledisc\" type=\"text\" name=\"text\" id =\"snippetfiledescription5\" maxlength=\"200\"></textarea></p>\n" +
            "      <p id =\"snippetFile\" ><label class=\"thissize\">Snippet File <i>(mandatory)</i><br><i style=\"font-weight:lighter\">Recommended format is \"[service].example_code.[language].[uniqueSnippetName]\" - unique from the final part of any other snippet tag for the primary service.</i><br></label><br><textarea class=\"snippfile\" type=\"text\" name=\"text\" id =\"snippetfile5\" maxlength=\"200\"></textarea></p>\n" +
           "").insertBefore("#addSnippetFile");
    }
    if (noOfSnippetFiles == 5) {
        $(
            "<p id =\"snippFileDesc\" ><label class=\"thissize\">  Snippet File Description (optional)</label><br><textarea  class=\"snippfiledisc\" type=\"text\" name=\"text\" id =\"snippetfiledescription6\" maxlength=\"200\"></textarea></p>\n" +
            "      <p id =\"snippetFile\" ><label class=\"thissize\">Snippet File <i>(mandatory)</i><br><i style=\"font-weight:lighter\">Recommended format is \"[service].example_code.[language].[uniqueSnippetName]\" - unique from the final part of any other snippet tag for the primary service.</i><br></label><br><textarea class=\"snippfile\" type=\"text\" name=\"text\" id =\"snippetfile6\" maxlength=\"200\"></textarea></p>\n" +
              "").insertBefore("#addSnippetFile");
    }
    if (noOfSnippetFiles == 6) {
        myAlert('alert', 'Maximum of 6 snippets allowed.')
    }
};
/*$("#removesnipfile").on('click', function () {
    console.log('clicked')
    const noOfSnippetFiles = document.getElementsByClassName('snippfiledisc').length;
    console.log('noOfSnippets', noOfSnippetFiles)
    if (noOfSnippetFiles == 0) {
        alert('There are no snippets to remove')
    } else {
        $("p[id=snippFileDesc]:last").remove();
        $("p[id=snippetFile]:last").remove();
        var element = document.getElementById('addSnippetFile');
        element.setAttribute("style", "visibility: collapse");
        var element = document.getElementById('removeSnippetFile');
        element.setAttribute("style", "visibility: collapse");
        var element = document.getElementById('createYAML');
        element.setAttribute("style", "visibility: collapse");
        var element = document.getElementById('selectTagOrFile');
        element.setAttribute("style", "visibility: visible");
        var element = document.getElementById('selecttagorfile');
        element.setAttribute("style", "visibility: visible");
        var select = document.getElementById("selecttagorfile");
        if(countValues(select)>=2){


        }
        else{
            var options = ["Snippet tags", "Snippet files"];
            for (var i = 0; i < options.length; i++) {
                var opt = options[i];
                var el = document.createElement("option");
                el.textContent = opt;
                el.value = opt;
                select.appendChild(el);
            }
        }
        document.getElementById('selecttagorfile').disabled = false;
    }
}); */