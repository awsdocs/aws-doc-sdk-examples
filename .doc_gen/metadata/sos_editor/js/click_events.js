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
            var myObj = JSON.parse(this.responseText);
            console.log('myobject', myObj)
           if(myObj === null){
               const myBlocks =[];
               var newBlocks = myBlocks.push('Not listed')
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
            else{
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

        }
        const serviceStub = document.getElementById('selecttheservice').value
        const sourceJson = "./jsonholder/" + serviceStub + "_metadata.json"
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
        myfunction();
        myfunction2();
        myfunction3();
        myfunction35();
        myfunction35();
    }


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
        alert('You must have a title, abbreviated title, and either a synopsis or synopsis list.')
        document.getElementById("languages").selectedIndex = 0;

        return
    }
    document.getElementById('languages').disabled = true;
    console.log('button clicked');
    var selectedLanguage = document.getElementById('languages').value
    var searchKey = document.getElementById('selectBlock').value
    console.log('selectedLanguage', selectedLanguage)
    // The language isn't covered yet. This should cover adding a new language.
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
    // It's a new operation.
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
$("#newsnippet").on('click', function() {
    const noOfSnippets = document.getElementsByClassName('snippdisc').length
    if(noOfSnippets==0) {
        $(
            "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><textarea  class=\"snippdisc\" type=\"text\" name=\"text\" id =\"snippetdescription\" maxlength=\"200\"></textarea></p>\n" +
            "      <p id =\"snippet\" ><label class=\"thissize\">Snippet tag</label><textarea class=\"snipptag\" type=\"text\" name=\"text\" id =\"snippettag\" maxlength=\"200\"></textarea></p>\n" +
            "       ").insertBefore("#newsnippet");
    }
    if(noOfSnippets==1) {
        $(
            "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><textarea  class=\"snippdisc\" type=\"text\" name=\"text\" id =\"snippetdescription1\" maxlength=\"200\"></textarea></p>\n" +
            "      <p id =\"snippet\" ><label class=\"thissize\">Snippet tag</label><textarea class=\"snipptag\" type=\"text\" name=\"text\" id =\"snippettag1\" maxlength=\"200\"></textarea></p>\n" +
            "       ").insertBefore("#newsnippet");
    }
    if(noOfSnippets==2) {
        $(
            "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><textarea  class=\"snippdisc\" type=\"text\" name=\"text\" id =\"snippetdescription2\" maxlength=\"200\"></textarea></p>\n" +
            "      <p id =\"snippet\" ><label class=\"thissize\">Snippet tag</label><textarea class=\"snipptag\" type=\"text\" name=\"text\" id =\"snippettag2\" maxlength=\"200\"></textarea></p>\n" +
            "       ").insertBefore("#newsnippet");
    }
    if(noOfSnippets==3) {
        $(
            "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><textarea  class=\"snippdisc\" type=\"text\" name=\"text\" id =\"snippetdescription3\" maxlength=\"200\"></textarea></p>\n" +
            "      <p id =\"snippet\" ><label class=\"thissize\">Snippet tag</label><textarea class=\"snipptag\" type=\"text\" name=\"text\" id =\"snippettag3\" maxlength=\"200\"></textarea></p>\n" +
            "       ").insertBefore("#newsnippet");
    }
    if(noOfSnippets==4) {
        $(
            "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><textarea  class=\"snippdisc\" type=\"text\" name=\"text\" id =\"snippetdescription4\" maxlength=\"200\"></textarea></p>\n" +
            "      <p id =\"snippet\" ><label class=\"thissize\">Snippet tag</label><textarea class=\"snipptag\" type=\"text\" name=\"text\" id =\"snippettag4\" maxlength=\"200\"></textarea></p>\n" +
            "       ").insertBefore("#newsnippet");
    }
    if(noOfSnippets==5) {
        $(
            "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><textarea  class=\"snippdisc\" type=\"text\" name=\"text\" id =\"snippetdescription5\" maxlength=\"200\"></textarea></p>\n" +
            "      <p id =\"snippet\" ><label class=\"thissize\">Snippet tag</label><textarea class=\"snipptag\" type=\"text\" name=\"text\" id =\"snippettag5\" maxlength=\"200\"></textarea></p>\n" +
            "       ").insertBefore("#newsnippet");
    }
    if(noOfSnippets==6) {
        alert('Maximum of 6 snippets allowed.')
    }
});

$("#removesnip").on('click', function() {
    console.log('clicked')
    const noOfSnippets = document.getElementsByClassName('snippdisc').length;
    console.log('noOfSnippets', noOfSnippets)
    if(noOfSnippets==0) {
        alert('There are no snippets to remove')
    }
    else {
        $("p[id=snippDesc]:last").remove();
        $("p[id=snippet]:last").remove();
    }
});