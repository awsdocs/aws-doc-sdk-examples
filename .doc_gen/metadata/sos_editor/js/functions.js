// Display control for selecting block to add/edit.
function myfunction() {
    var searchKey = document.getElementById('selectBlock').value
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onload = function () {
        var myObj = JSON.parse(this.responseText);
        const selectedItem = myObj[searchKey]["title"];
        document.getElementById("thetitle").value = selectedItem
    }
    const serviceStub = document.getElementById('selecttheservice').value
    const sourceJson = "./jsonholder/" + serviceStub + "_metadata.json"
    xmlhttp.open("GET", sourceJson, true);
    xmlhttp.send();
};

// Display control for entering new block name.
function myfunction2() {
    var searchKey = document.getElementById('selectBlock').value
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onload = function () {
        var myObj = JSON.parse(this.responseText);
        const selectedItem = myObj[searchKey]["title_abbrev"];
        document.getElementById("addatitle").value = selectedItem
    }
    const serviceStub = document.getElementById('selecttheservice').value
    const sourceJson = "./jsonholder/" + serviceStub + "_metadata.json"
    xmlhttp.open("GET", sourceJson, true);
    xmlhttp.send();
};

// Populate API commands.
function myfunction36() {
    var searchKey = document.getElementById('selectBlock').value;
    console.log('searchKey ', searchKey)
    var searchKey2 = document.getElementById('selecttheservice').value
    //var searchKey2 = "\""+searchKey2+"\""
    console.log('searchKey2 ', searchKey2)
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onload = function () {
        var myObj = JSON.parse(this.responseText);
        console.log('myObj', myObj);
        const principalCommands = myObj[searchKey]["services"][searchKey2];
        console.log('principalCommands', principalCommands);

        document.getElementById("commands").innerText = principalCommands.replace('{', '').replace('}', '')
    }
    const serviceStub = document.getElementById('selecttheservice').value
    const sourceJson = "./jsonholder/" + serviceStub + "_metadata.json"
    xmlhttp.open("GET", sourceJson, true);
    xmlhttp.send();
};


// Populate synopsis and languages control.
function myfunction3() {
    var searchKey = document.getElementById('selectBlock').value
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onload = function () {
        var myObj = JSON.parse(this.responseText);
        const selectedItem = myObj[searchKey]["synopsis"];
        console.log('this selected synopsis', selectedItem);
        if (selectedItem !=undefined){
            console.log('synopsis undefined')
            document.getElementById("synopsis").value = selectedItem
        }
        else{
            console.log('synopsis nothing')
            document.getElementById("synopsis").value = ""
        }
        const synopsislist = myObj[searchKey]["synopsis_list"]
        if (synopsislist !=undefined){
            const finalList = JSON.stringify(synopsislist).replaceAll(",","\n").replaceAll("[", "").replaceAll("[", "").replaceAll("]", "").replaceAll("\"", "");
            console.log('this selected synopsislist', finalList);
            document.getElementById("synopsislist").value = finalList;
        }
        else{
            console.log('synopsis nothing')
            document.getElementById("synopsislist").value = ""
        }
        const languageList = Object.keys(myObj[searchKey]["languages"])
        var select = document.getElementById("languages");
        languageList.push('Not Listed')
        var options = languageList;
        for (var i = 0; i < options.length; i++) {
            var opt = options[i];
            var el = document.createElement("option");
            el.textContent = opt;
            el.value = opt;
            select.appendChild(el);
        }
    }

    const serviceStub = document.getElementById('selecttheservice').value
    const sourceJson = "./jsonholder/" + serviceStub + "_metadata.json"
    xmlhttp.open("GET", sourceJson, true);
    xmlhttp.send();
};

// Populate synopsis list
function myfunction34() {
    var searchKey = document.getElementById('selectBlock').value
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onload = function () {
        var myObj = JSON.parse(this.responseText);
        const selectedItem = myObj[searchKey]["synopsis_list"];
        document.getElementById("synopsislist").value = selectedItem
    }
    const serviceStub = document.getElementById('selecttheservice').value
    const sourceJson = "./jsonholder/" + serviceStub + "_metadata.json"
    xmlhttp.open("GET", sourceJson, true);
    xmlhttp.send();
};

// Populate category
function myfunction35() {
    var searchKey = document.getElementById('selectBlock').value
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onload = function () {
        var myObj = JSON.parse(this.responseText);
        const selectedItem = myObj[searchKey]["category"];
        document.getElementById("category").value = selectedItem
    }
    const serviceStub = document.getElementById('selecttheservice').value
    const sourceJson = "./jsonholder/" + serviceStub + "_metadata.json"
    xmlhttp.open("GET", sourceJson, true);
    xmlhttp.send();
};

// Populate language control
function myfunction4() {
    var searchKey = document.getElementById('selectBlock').value
    var searchLang = document.getElementById('languages').value
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            var myObj = JSON.parse(this.responseText);
            console.log('myObj search key', myObj[searchKey])
            if (searchLang != "Not Listed") {
                const noOfVersions = myObj[searchKey]["languages"][searchLang]["versions"].length
                let versions = [];
                for (let i = 0; i < noOfVersions; i++) {
                    versions.push(myObj[searchKey]["languages"][searchLang]["versions"][i]["sdk_version"]);
                }

                var newBlocks = versions.push('Not Listed')
                var select = document.getElementById("sdkVersion");
                var options = versions;
                console.log('noOfOptions', options.length)
                for (var i = 0; i < options.length; i++) {
                    var opt = options[i];
                    var el = document.createElement("option");
                    el.textContent = opt;
                    el.value = opt;
                    select.appendChild(el);
                }
            } else {
                var select = document.getElementById("sdkVersion");
                var el = document.createElement("option");
                el.textContent = "Not Listed";
                el.value = "not listed";
                select.appendChild(el);
            }
        }
    };
    const serviceStub = document.getElementById('selecttheservice').value
    const sourceJson = "./jsonholder/" + serviceStub + "_metadata.json"
    xmlhttp.open("GET", sourceJson, true);
    xmlhttp.send();
}


// Populate SDK language version control.
function myfunction5() {
    var searchKey = document.getElementById('selectBlock').value
    var searchLang = document.getElementById('languages').value
    var ThisSDKversion = document.getElementById('sdkVersion').value
    console.log('ThisSDKversion', ThisSDKversion);

    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onload = function () {
        var myObj = JSON.parse(this.responseText);
        console.log('myfirstversion', myObj[searchKey]["languages"][searchLang]["versions"][0]["sdk_version"])
        if (myObj[searchKey]["languages"][searchLang]["versions"][0]["sdk_version"] == ThisSDKversion) {
            console.log("display something about 2")
            const selectedItem = myObj[searchKey]["languages"][searchLang]["versions"][0]["github"]
            console.log("selectedItem", selectedItem)
            document.getElementById("githublink").value = selectedItem
        }
        if (myObj[searchKey]["languages"][searchLang]["versions"][1]["sdk_version"] == ThisSDKversion) {
            console.log("display something about 1")
            const selectedItem1 = myObj[searchKey]["languages"][searchLang]["versions"][1]["github"]
            console.log("selectedItem1", selectedItem1)
            document.getElementById("githublink").value = selectedItem1
        }

    }
    const serviceStub = document.getElementById('selecttheservice').value
    const sourceJson = "./jsonholder/" + serviceStub + "_metadata.json"
    xmlhttp.open("GET", sourceJson, true);
    xmlhttp.send();

}

// Populate
function myfunction6() {
    var searchKey = document.getElementById('selectBlock').value
    var searchLang = document.getElementById('languages').value
    var ThisSDKversion = document.getElementById('sdkVersion').value
    console.log('ThisSDKversion', ThisSDKversion);
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onload = function () {
        var myObj = JSON.parse(this.responseText);
        console.log('myfirstversion', myObj[searchKey]["languages"][searchLang]["versions"][0]["sdk_version"])
        if (myObj[searchKey]["languages"][searchLang]["versions"][0]["sdk_version"] == ThisSDKversion) {
            console.log("display something about 2")
            const selectedItem = myObj[searchKey]["languages"][searchLang]["versions"][0]["sdkguide"]
            console.log("selectedItem", selectedItem)
            if (selectedItem != undefined) {
                document.getElementById("sdkguidelink").value = selectedItem
            }
        }
        if (myObj[searchKey]["languages"][searchLang]["versions"][1]["sdk_version"] == ThisSDKversion) {
            console.log("display something about 1")
            const selectedItem1 = myObj[searchKey]["languages"][searchLang]["versions"][1]["sdkguide"]
            console.log("selectedItem1", selectedItem1)
            document.getElementById("sdkguidelink").value = selectedItem1
        }

    }
    const serviceStub = document.getElementById('selecttheservice').value
    const sourceJson = "./jsonholder/" + serviceStub + "_metadata.json"
    xmlhttp.open("GET", sourceJson, true);
    xmlhttp.send();

}

function myfunction7() {
    var searchKey = document.getElementById('selectBlock').value
    var searchLang = document.getElementById('languages').value
    var ThisSDKversion = document.getElementById('sdkVersion').value
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onload = function () {
        var myObj = JSON.parse(this.responseText);
        if (myObj[searchKey]["languages"][searchLang]["versions"][0]["sdk_version"] == ThisSDKversion) {
            const noOfExcerpts = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"].length;
            if (myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][0].hasOwnProperty('snippet_tags')) {
                if (noOfExcerpts == 1) {
                    console.log('one excerpt')
                    $(
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippdisc\" type=\"text\" name=\"text\" id =\"snippetdescription1\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet tag</label><i>(mandatory)</i><textarea class=\"snipptag\" type=\"text\" name=\"text\" id =\"snippettag1\" maxlength=\"200\"></textarea></p>\n" +
                        "    <p id=\"anotherSnippet\" ><button id=\"newsnippet\" onclick=\"newsnippet()\" class=\"thissize\">Add a snippet file</button></p>" +
                        "       ").insertBefore("#createYAML");
                    const selectedItem = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][0]["description"]
                    console.log('selectedItem ', selectedItem);
                    document.getElementById("snippetdescription1").value = selectedItem
                    const selectedItem1 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][0]["snippet_tags"]
                    const snippetString1= selectedItem1.toString().replaceAll(',','\n')
                    document.getElementById("snippettag1").value = snippetString1;
                    console.log('snippetString1 ', snippetString1);
                }
                if (noOfExcerpts == 2) {
                    $(
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippdisc\" type=\"text\" name=\"text\" id =\"snippetdescription1\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet tag</label><i>(mandatory)</i><textarea class=\"snipptag\" type=\"text\" name=\"text\" id =\"snippettag1\" maxlength=\"200\"></textarea></p>\n" +
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippdisc\" type=\"text\" name=\"text\" id =\"snippetdescription2\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet tag</label><i>(mandatory)</i><textarea class=\"snipptag\" type=\"text\" name=\"text\" id =\"snippettag2\" maxlength=\"200\"></textarea></p>\n" +
                        "    <p id=\"anotherSnippet\" ><button id=\"newsnippet\" onclick=\"newsnippet()\" class=\"thissize\">Add a snippet file</button></p>" +
                        "       ").insertBefore("#createYAML");
                    const selectedItem = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][0]["description"]
                    document.getElementById("snippetdescription1").value = selectedItem
                    const selectedItem1 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][0]["snippet_tags"]
                    const snippetString1= selectedItem1.toString().replaceAll(',','\n')
                    document.getElementById("snippettag1").value = snippetString1;
                    const selectedItem2 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][1]["description"]
                    document.getElementById("snippetdescription2").value = selectedItem2
                    const selectedItem3 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][0]["snippet_tags"]
                    const snippetString2= selectedItem3.toString().replaceAll(',','\n')
                    document.getElementById("snippettag2").value = snippetString2
                }
                if (noOfExcerpts == 3) {
                    console.log('noOfExcerpts ', noOfExcerpts);
                    $(
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippdisc\" type=\"text\" name=\"text\" id =\"snippetdescription1\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet tag</label><i>(mandatory)</i><textarea class=\"snipptag\" type=\"text\" name=\"text\" id =\"snippettag1\" maxlength=\"200\"></textarea></p>\n" +
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippdisc\" type=\"text\" name=\"text\" id =\"snippetdescription2\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet tag</label><i>(mandatory)</i><textarea class=\"snipptag\" type=\"text\" name=\"text\" id =\"snippettag2\" maxlength=\"200\"></textarea></p>\n" +
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippdisc\" type=\"text\" name=\"text\" id =\"snippetdescription3\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet tag</label><i>(mandatory)</i><textarea class=\"snipptag\" type=\"text\" name=\"text\" id =\"snippettag3\" maxlength=\"200\"></textarea></p>\n" +
                        "    <p id=\"anotherSnippet\" ><button id=\"newsnippet\" onclick=\"newsnippet()\" class=\"thissize\">Add a snippet file</button></p>" +
                        "       ").insertBefore("#createYAML");
                    const selectedItem = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][0]["description"]
                    document.getElementById("snippetdescription1").value = selectedItem
                    const selectedItem1 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][0]["snippet_tags"]
                    const snippetString1= selectedItem1.toString().replaceAll(',','\n')
                    document.getElementById("snippettag1").value = snippetString1;
                    const selectedItem2 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][1]["description"]
                    document.getElementById("snippetdescription2").value = selectedItem2
                    const selectedItem3 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][0]["snippet_tags"]
                    const snippetString2= selectedItem3.toString().replaceAll(',','\n')
                    document.getElementById("snippettag2").value = snippetString2
                    const selectedItem4 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][2]["description"]
                    document.getElementById("snippetdescription3").value = selectedItem4
                    const selectedItem5 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][2]["snippet_tags"]
                    const snippetString3= selectedItem5.toString().replaceAll(',','\n')
                    document.getElementById("snippettag3").value = snippetString3
                }
                if (noOfExcerpts == 4) {
                    $(
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippdisc\" type=\"text\" name=\"text\" id =\"snippetdescription1\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet tag</label><i>(mandatory)</i><textarea class=\"snipptag\" type=\"text\" name=\"text\" id =\"snippettag1\" maxlength=\"200\"></textarea></p>\n" +
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippdisc\" type=\"text\" name=\"text\" id =\"snippetdescription2\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet tag</label><i>(mandatory)</i><textarea class=\"snipptag\" type=\"text\" name=\"text\" id =\"snippettag2\" maxlength=\"200\"></textarea></p>\n" +
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippdisc\" type=\"text\" name=\"text\" id =\"snippetdescription3\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet tag</label><i>(mandatory)</i><textarea class=\"snipptag\" type=\"text\" name=\"text\" id =\"snippettag3\" maxlength=\"200\"></textarea></p>\n" +
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippdisc\" type=\"text\" name=\"text\" id =\"snippetdescription4\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet tag</label><i>(mandatory)</i><textarea class=\"snipptag\" type=\"text\" name=\"text\" id =\"snippettag4\" maxlength=\"200\"></textarea></p>\n" +
                        "    <p id=\"anotherSnippet\" ><button id=\"newsnippet\" onclick=\"newsnippet()\" class=\"thissize\">Add a snippet file</button></p>" +
                        "       ").insertBefore("#createYAML");
                    const selectedItem1 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][0]["snippet_tags"]
                    const snippetString1= selectedItem1.toString().replaceAll(',','\n')
                    document.getElementById("snippettag1").value = snippetString1;
                    const selectedItem2 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][1]["description"]
                    document.getElementById("snippetdescription2").value = selectedItem2
                    const selectedItem3 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][0]["snippet_tags"]
                    const snippetString2= selectedItem3.toString().replaceAll(',','\n')
                    document.getElementById("snippettag2").value = snippetString2
                    const selectedItem4 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][2]["description"]
                    document.getElementById("snippetdescription3").value = selectedItem4
                    const selectedItem5 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][2]["snippet_tags"]
                    const snippetString3= selectedItem5.toString().replaceAll(',','\n')
                    document.getElementById("snippettag3").value = snippetString3
                    const selectedItem6 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][3]["description"]
                    document.getElementById("snippetdescription4").value = selectedItem6
                    const selectedItem7 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][3]["snippet_tags"]
                    const snippetString4= selectedItem7.toString().replaceAll(',','\n')
                    document.getElementById("snippettag4").value = snippetString4
                }
                if (noOfExcerpts == 5) {
                    $(
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippdisc\" type=\"text\" name=\"text\" id =\"snippetdescription1\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet tag</label><i>(mandatory)</i><textarea class=\"snipptag\" type=\"text\" name=\"text\" id =\"snippettag1\" maxlength=\"200\"></textarea></p>\n" +
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippdisc\" type=\"text\" name=\"text\" id =\"snippetdescription2\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet tag</label><i>(mandatory)</i><textarea class=\"snipptag\" type=\"text\" name=\"text\" id =\"snippettag2\" maxlength=\"200\"></textarea></p>\n" +
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippdisc\" type=\"text\" name=\"text\" id =\"snippetdescription3\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet tag</label><i>(mandatory)</i><textarea class=\"snipptag\" type=\"text\" name=\"text\" id =\"snippettag3\" maxlength=\"200\"></textarea></p>\n" +
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippdisc\" type=\"text\" name=\"text\" id =\"snippetdescription4\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet tag</label><i>(mandatory)</i><textarea class=\"snipptag\" type=\"text\" name=\"text\" id =\"snippettag4\" maxlength=\"200\"></textarea></p>\n" +
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippdisc\" type=\"text\" name=\"text\" id =\"snippetdescription5\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet tag</label><i>(mandatory)</i><textarea class=\"snipptag\" type=\"text\" name=\"text\" id =\"snippettag5\" maxlength=\"200\"></textarea></p>\n" +
                        "    <p id=\"anotherSnippet\" ><button id=\"newsnippet\" onclick=\"newsnippet()\" class=\"thissize\">Add a snippet file</button></p>" +
                        "       ").insertBefore("#createYAML");
                    const selectedItem1 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][0]["snippet_tags"]
                    const snippetString1= selectedItem1.toString().replaceAll(',','\n')
                    document.getElementById("snippettag1").value = snippetString1;
                    const selectedItem2 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][1]["description"]
                    document.getElementById("snippetdescription2").value = selectedItem2
                    const selectedItem3 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][0]["snippet_tags"]
                    const snippetString2= selectedItem3.toString().replaceAll(',','\n')
                    document.getElementById("snippettag2").value = snippetString2
                    const selectedItem4 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][2]["description"]
                    document.getElementById("snippetdescription3").value = selectedItem4
                    const selectedItem5 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][2]["snippet_tags"]
                    const snippetString3= selectedItem5.toString().replaceAll(',','\n')
                    document.getElementById("snippettag3").value = snippetString3
                    const selectedItem6 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][3]["description"]
                    document.getElementById("snippetdescription4").value = selectedItem6
                    const selectedItem7 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][3]["snippet_tags"]
                        const snippetString4= selectedItem7.toString().replaceAll(',','\n')
                    document.getElementById("snippettag4").value = snippetString4
                    const selectedItem8 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][4]["description"]
                    document.getElementById("snippetdescription5").value = selectedItem8
                    const selectedItem9 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][4]["snippet_tags"]
                    const snippetString5= selectedItem9.toString().replaceAll(',','\n')
                    document.getElementById("snippettag5").value = snippetString5
                }
                if (noOfExcerpts == 6) {
                    $(
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippdisc\" type=\"text\" name=\"text\" id =\"snippetdescription1\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet tag</label><i>(mandatory)</i><textarea class=\"snipptag\" type=\"text\" name=\"text\" id =\"snippettag1\" maxlength=\"200\"></textarea></p>\n" +
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippdisc\" type=\"text\" name=\"text\" id =\"snippetdescription2\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet tag</label><i>(mandatory)</i><textarea class=\"snipptag\" type=\"text\" name=\"text\" id =\"snippettag2\" maxlength=\"200\"></textarea></p>\n" +
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippdisc\" type=\"text\" name=\"text\" id =\"snippetdescription3\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet tag</label><i>(mandatory)</i><textarea class=\"snipptag\" type=\"text\" name=\"text\" id =\"snippettag3\" maxlength=\"200\"></textarea></p>\n" +
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippdisc\" type=\"text\" name=\"text\" id =\"snippetdescription4\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet tag</label><i>(mandatory)</i><textarea class=\"snipptag\" type=\"text\" name=\"text\" id =\"snippettag4\" maxlength=\"200\"></textarea></p>\n" +
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippdisc\" type=\"text\" name=\"text\" id =\"snippetdescription5\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet tag</label><i>(mandatory)</i><textarea class=\"snipptag\" type=\"text\" name=\"text\" id =\"snippettag5\" maxlength=\"200\"></textarea></p>\n" +
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippdisc\" type=\"text\" name=\"text\" id =\"snippetdescription6\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet tag</label><i>(mandatory)</i><textarea class=\"snipptag\" type=\"text\" name=\"text\" id =\"snippettag6\" maxlength=\"200\"></textarea></p>\n" +
                        "    <p id=\"anotherSnippet\" ><button id=\"newsnippet\" onclick=\"newsnippet()\" class=\"thissize\">Add a snippet file</button></p>" +
                        "       ").insertBefore("#createYAML");
                    const selectedItem1 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][0]["snippet_tags"]
                    const snippetString1= selectedItem1.toString().replaceAll(',','\n')
                    document.getElementById("snippettag1").value = snippetString1;
                    const selectedItem2 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][1]["description"]
                    document.getElementById("snippetdescription2").value = selectedItem2
                    const selectedItem3 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][0]["snippet_tags"]
                    const snippetString2= selectedItem3.toString().replaceAll(',','\n')
                    document.getElementById("snippettag2").value = snippetString2
                    const selectedItem4 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][2]["description"]
                    document.getElementById("snippetdescription3").value = selectedItem4
                    const selectedItem5 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][2]["snippet_tags"]
                    const snippetString3= selectedItem5.toString().replaceAll(',','\n')
                    document.getElementById("snippettag3").value = snippetString3
                    const selectedItem6 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][3]["description"]
                    document.getElementById("snippetdescription4").value = selectedItem6
                    const selectedItem7 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][3]["snippet_tags"]
                    const snippetString4= selectedItem7.toString().replaceAll(',','\n')
                    document.getElementById("snippettag4").value = snippetString4
                    const selectedItem8 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][4]["description"]
                    document.getElementById("snippetdescription5").value = selectedItem8
                    const selectedItem9 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][4]["snippet_tags"]
                    const snippetString5= selectedItem9.toString().replaceAll(',','\n')
                    document.getElementById("snippettag5").value = snippetString5
                    const selectedItem10 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][5]["description"]
                    document.getElementById("snippetdescription6").value = selectedItem10
                    const selectedItem11 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][5]["snippet_tags"]
                    const snippetString6= selectedItem11.toString().replaceAll(',','\n')
                    document.getElementById("snippettag6").value = snippetString6
                }
            }
            if (myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][0].hasOwnProperty('snippet_files')) {
                if (noOfExcerpts == 1) {
                    console.log('one excerpt')
                    $(
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippfiledisc\" type=\"text\" name=\"text\" id =\"snippetfiledescription1\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet file</label><i>(mandatory)</i><textarea class=\"snippfile\" type=\"text\" name=\"text\" id =\"snippetfile1\" maxlength=\"200\"></textarea></p>\n" +
                        "    <p id=\"addSnippetFile\" ><button id=\"newsnippetfile\" onclick=\"newsnippetfile()\" class=\"thissize\">Add a snippet file</button></p>" +
                        "       ").insertBefore("#createYAML");
                    const selectedItem = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][0]["description"]
                    document.getElementById("snippetfiledescription1").value = selectedItem
                    const selectedItem1 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][0]["snippet_files"]
                    const snippetFile1= selectedItem1.toString().replaceAll(',','\n')
                    document.getElementById("snippetfile1").value = snippetFile1
                }
                if (noOfExcerpts == 2) {
                    $(
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippfiledisc\" type=\"text\" name=\"text\" id =\"snippetfiledescription1\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet file</label><i>(mandatory)</i><textarea class=\"snippfile\" type=\"text\" name=\"text\" id =\"snippetfile1\" maxlength=\"200\"></textarea></p>\n" +
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippfiledisc\" type=\"text\" name=\"text\" id =\"snippetfiledescription2\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet file</label><i>(mandatory)</i><textarea class=\"snippfile\" type=\"text\" name=\"text\" id =\"snippetfile2\" maxlength=\"200\"></textarea></p>\n" +
                        "    <p id=\"addSnippetFile\" ><button id=\"newsnippetfile\" onclick=\"newsnippetfile()\" class=\"thissize\">Add a snippet file</button></p>" +
                        "       ").insertBefore("#createYAML");
                    const selectedItem = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][0]["description"]
                    document.getElementById("snippetfiledescription1").value = selectedItem
                    const selectedItem1 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][0]["snippet_files"]
                    const snippetFile1= selectedItem1.toString().replaceAll(',','\n')
                    document.getElementById("snippetfile1").value = snippetFile1
                    const selectedItem2 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][1]["description"]
                    document.getElementById("snippetfiledescription2").value = selectedItem2
                    const selectedItem3 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][1]["snippet_files"]
                    const snippetFile2= selectedItem3.toString().replaceAll(',','\n')
                    document.getElementById("snippetfile2").value = snippetFile2
                }
                if (noOfExcerpts == 3) {
                    $(
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippfiledisc\" type=\"text\" name=\"text\" id =\"snippetfiledescription1\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet file</label><i>(mandatory)</i><textarea class=\"snippfile\" type=\"text\" name=\"text\" id =\"snippetfile1\" maxlength=\"200\"></textarea></p>\n" +
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippfiledisc\" type=\"text\" name=\"text\" id =\"snippetfiledescription2\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet file</label><i>(mandatory)</i><textarea class=\"snippfile\" type=\"text\" name=\"text\" id =\"snippetfile2\" maxlength=\"200\"></textarea></p>\n" +
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippfiledisc\" type=\"text\" name=\"text\" id =\"snippetfiledescription3\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet file</label><i>(mandatory)</i><textarea class=\"snippfile\" type=\"text\" name=\"text\" id =\"snippetfile3\" maxlength=\"200\"></textarea></p>\n" +
                        "    <p id=\"addSnippetFile\" ><button id=\"newsnippetfile\" onclick=\"newsnippetfile()\" class=\"thissize\">Add a snippet file</button></p>" +
                        "       ").insertBefore("#createYAML");
                    const selectedItem = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][0]["description"]
                    document.getElementById("snippetfiledescription1").value = selectedItem
                    const selectedItem1 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][0]["snippet_files"]
                    const snippetFile1= selectedItem1.toString().replaceAll(',','\n')
                    document.getElementById("snippetfile1").value = snippetFile1
                    const selectedItem2 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][1]["description"]
                    document.getElementById("snippetfiledescription2").value = selectedItem2
                    const selectedItem3 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][1]["snippet_files"]
                    const snippetFile2= selectedItem3.toString().replaceAll(',','\n')
                    document.getElementById("snippetfile2").value = snippetFile2
                    const selectedItem4 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][2]["description"]
                    document.getElementById("snippetfiledescription3").value = selectedItem4
                    const selectedItem5 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][2]["snippet_files"]
                    const snippetFile3= selectedItem5.toString().replaceAll(',','\n')
                    document.getElementById("snippetfile3").value = snippetFile3
                }
                if (noOfExcerpts == 4) {
                    $(
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippfiledisc\" type=\"text\" name=\"text\" id =\"snippetfiledescription1\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet file</label><i>(mandatory)</i><textarea class=\"snippfile\" type=\"text\" name=\"text\" id =\"snippetfile1\" maxlength=\"200\"></textarea></p>\n" +
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippfiledisc\" type=\"text\" name=\"text\" id =\"snippetfiledescription2\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet file</label><i>(mandatory)</i><textarea class=\"snippfile\" type=\"text\" name=\"text\" id =\"snippetfile2\" maxlength=\"200\"></textarea></p>\n" +
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippfiledisc\" type=\"text\" name=\"text\" id =\"snippetfiledescription3\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet file</label><i>(mandatory)</i><textarea class=\"snippfile\" type=\"text\" name=\"text\" id =\"snippetfile3\" maxlength=\"200\"></textarea></p>\n" +
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippfiledisc\" type=\"text\" name=\"text\" id =\"snippetfiledescription4\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet file</label><i>(mandatory)</i><textarea class=\"snippfile\" type=\"text\" name=\"text\" id =\"snippetfile4\" maxlength=\"200\"></textarea></p>\n" +
                        "    <p id=\"addSnippetFile\" ><button id=\"newsnippetfile\" onclick=\"newsnippetfile()\" class=\"thissize\">Add a snippet file</button></p>" +
                        "       ").insertBefore("#createYAML");
                    const selectedItem = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][0]["description"]
                    document.getElementById("snippetfiledescription1").value = selectedItem
                    const selectedItem1 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][0]["snippet_files"]
                    const snippetFile1= selectedItem1.toString().replaceAll(',','\n')
                    document.getElementById("snippetfile1").value = snippetFile1
                    const selectedItem2 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][1]["description"]
                    document.getElementById("snippetfiledescription2").value = selectedItem2
                    const selectedItem3 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][1]["snippet_files"]
                    const snippetFile2= selectedItem3.toString().replaceAll(',','\n')
                    document.getElementById("snippetfile2").value = snippetFile2
                    const selectedItem4 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][2]["description"]
                    document.getElementById("snippetfiledescription3").value = selectedItem4
                    const selectedItem5 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][2]["snippet_files"]
                    const snippetFile3= selectedItem5.toString().replaceAll(',','\n')
                    document.getElementById("snippetfile3").value = snippetFile3
                    const selectedItem6 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][3]["description"]
                    document.getElementById("snippetfiledescription4").value = selectedItem6
                    const selectedItem7 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][3]["snippet_files"]
                    const snippetFile4= selectedItem7.toString().replaceAll(',','\n')
                    document.getElementById("snippetfile4").value = snippetFile4
                }
                if (noOfExcerpts == 5) {
                    $(
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippfiledisc\" type=\"text\" name=\"text\" id =\"snippetfiledescription1\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet file</label><i>(mandatory)</i><textarea class=\"snippfile\" type=\"text\" name=\"text\" id =\"snippetfile1\" maxlength=\"200\"></textarea></p>\n" +
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippfiledisc\" type=\"text\" name=\"text\" id =\"snippetfiledescription2\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet file</label><i>(mandatory)</i><textarea class=\"snippfile\" type=\"text\" name=\"text\" id =\"snippetfile2\" maxlength=\"200\"></textarea></p>\n" +
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippfiledisc\" type=\"text\" name=\"text\" id =\"snippetfiledescription3\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet file</label><i>(mandatory)</i><textarea class=\"snippfile\" type=\"text\" name=\"text\" id =\"snippetfile3\" maxlength=\"200\"></textarea></p>\n" +
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippfiledisc\" type=\"text\" name=\"text\" id =\"snippetfiledescription4\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet file</label><i>(mandatory)</i><textarea class=\"snippfile\" type=\"text\" name=\"text\" id =\"snippetfile4\" maxlength=\"200\"></textarea></p>\n" +
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippfiledisc\" type=\"text\" name=\"text\" id =\"snippetfiledescription5\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet file</label><i>(mandatory)</i><textarea class=\"snippfile\" type=\"text\" name=\"text\" id =\"snippetfile5\" maxlength=\"200\"></textarea></p>\n" +
                        "    <p id=\"addSnippetFile\" ><button id=\"newsnippetfile\" onclick=\"newsnippetfile()\" class=\"thissize\">Add a snippet file</button></p>" +
                        "       ").insertBefore("#createYAML");
                    const selectedItem = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][0]["description"]
                    document.getElementById("snippetfiledescription1").value = selectedItem
                    const selectedItem1 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][0]["snippet_files"]
                    const snippetFile1= selectedItem1.toString().replaceAll(',','\n')
                    document.getElementById("snippetfile1").value = snippetFile1
                    const selectedItem2 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][1]["description"]
                    document.getElementById("snippetfiledescription2").value = selectedItem2
                    const selectedItem3 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][1]["snippet_files"]
                    const snippetFile2= selectedItem3.toString().replaceAll(',','\n')
                    document.getElementById("snippetfile2").value = snippetFile2
                    const selectedItem4 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][2]["description"]
                    document.getElementById("snippetfiledescription3").value = selectedItem4
                    const selectedItem5 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][2]["snippet_files"]
                    const snippetFile3= selectedItem5.toString().replaceAll(',','\n')
                    document.getElementById("snippetfile3").value = snippetFile3
                    const selectedItem6 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][3]["description"]
                    document.getElementById("snippetfiledescription4").value = selectedItem6
                    const selectedItem7 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][3]["snippet_files"]
                    const snippetFile4= selectedItem7.toString().replaceAll(',','\n')
                    document.getElementById("snippetfile4").value = snippetFile4
                    const selectedItem8 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][4]["description"]
                    document.getElementById("snippetfiledescription5").value = selectedItem8
                    const selectedItem9 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][4]["snippet_files"]
                    const snippetFile5= selectedItem9.toString().replaceAll(',','\n')
                    document.getElementById("snippetfile5").value = snippetFile5
                }
                if (noOfExcerpts == 6) {
                    $(
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippfiledisc\" type=\"text\" name=\"text\" id =\"snippetfiledescription1\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet file</label><i>(mandatory)</i><textarea class=\"snippfile\" type=\"text\" name=\"text\" id =\"snippetfile1\" maxlength=\"200\"></textarea></p>\n" +
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippfiledisc\" type=\"text\" name=\"text\" id =\"snippetfiledescription2\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet file</label><i>(mandatory)</i><textarea class=\"snippfile\" type=\"text\" name=\"text\" id =\"snippetfile2\" maxlength=\"200\"></textarea></p>\n" +
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippfiledisc\" type=\"text\" name=\"text\" id =\"snippetfiledescription3\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet file</label><i>(mandatory)</i><textarea class=\"snippfile\" type=\"text\" name=\"text\" id =\"snippetfile3\" maxlength=\"200\"></textarea></p>\n" +
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippfiledisc\" type=\"text\" name=\"text\" id =\"snippetfiledescription4\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet file</label><i>(mandatory)</i><textarea class=\"snippfile\" type=\"text\" name=\"text\" id =\"snippetfile4\" maxlength=\"200\"></textarea></p>\n" +
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippfiledisc\" type=\"text\" name=\"text\" id =\"snippetfiledescription5\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet file</label><i>(mandatory)</i><textarea class=\"snippfile\" type=\"text\" name=\"text\" id =\"snippetfile5\" maxlength=\"200\"></textarea></p>\n" +
                        "<p id =\"snippDesc\" ><label class=\"thissize\">Snippet Description</label><br><textarea  class=\"snippfiledisc\" type=\"text\" name=\"text\" id =\"snippetfiledescription6\" maxlength=\"200\"></textarea></p>\n" +
                        "      <p id =\"snippet\" ><label class=\"thissize\">Snippet file</label><i>(mandatory)</i><textarea class=\"snippfile\" type=\"text\" name=\"text\" id =\"snippetfile6\" maxlength=\"200\"></textarea></p>\n" +
                        "    <p id=\"addSnippetFile\" ><button id=\"newsnippetfile\" onclick=\"newsnippetfile()\" class=\"thissize\">Add a snippet file</button></p>" +
                        "       ").insertBefore("#createYAML");
                    const selectedItem = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][0]["description"]
                    document.getElementById("snippetfiledescription1").value = selectedItem
                    const selectedItem1 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][0]["snippet_files"]
                    const snippetFile1= selectedItem1.toString().replaceAll(',','\n')
                    document.getElementById("snippetfile1").value = snippetFile1
                    const selectedItem2 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][1]["description"]
                    document.getElementById("snippetfiledescription2").value = selectedItem2
                    const selectedItem3 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][1]["snippet_files"]
                    const snippetFile2= selectedItem3.toString().replaceAll(',','\n')
                    document.getElementById("snippetfile2").value = snippetFile2
                    const selectedItem4 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][2]["description"]
                    document.getElementById("snippetfiledescription3").value = selectedItem4
                    const selectedItem5 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][2]["snippet_files"]
                    const snippetFile3= selectedItem5.toString().replaceAll(',','\n')
                    document.getElementById("snippetfile3").value = snippetFile3
                    const selectedItem6 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][3]["description"]
                    document.getElementById("snippetfiledescription4").value = selectedItem6
                    const selectedItem7 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][3]["snippet_files"]
                    const snippetFile4= selectedItem7.toString().replaceAll(',','\n')
                    document.getElementById("snippetfile4").value = snippetFile4
                    const selectedItem8 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][4]["description"]
                    document.getElementById("snippetfiledescription5").value = selectedItem8
                    const selectedItem9 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][4]["snippet_files"]
                    const snippetFile5= selectedItem9.toString().replaceAll(',','\n')
                    document.getElementById("snippetfile5").value = snippetFile5
                    const selectedItem10 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][5]["description"]
                    document.getElementById("snippetfiledescription6").value = selectedItem10
                    const selectedItem11 = myObj[searchKey]["languages"][searchLang]["versions"][0]["excerpts"][5]["snippet_files"]
                    const snippetFile6= selectedItem11.toString().replaceAll(',','\n')
                    document.getElementById("snippettag6").value = snippetFile6
                }
            }
        }
    }
    const serviceStub = document.getElementById('selecttheservice').value
    const sourceJson = "./jsonholder/" + serviceStub + "_metadata.json"
    xmlhttp.open("GET", sourceJson, true);
    xmlhttp.send();
};

