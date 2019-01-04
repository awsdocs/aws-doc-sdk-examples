import os, fnmatch

def checkFile(directory, filePattern):
    filecount = 0;
    for path, dirs, files in os.walk(os.path.abspath(directory)):        
        for filename in fnmatch.filter(files, filePattern):
            filecount += 1
            filepath = os.path.join(path, filename)
            print("\nChecking File: " + filename + " in " + filepath)
            with open(filepath) as f:
                s = f.read()
                words = s.split()
            snippetStartCheck(words)
            snippets = s.split('snippet-')
            #print(snippets)
            snippetAuthorCheck(snippets)
            snippetServiceCheck(snippets)
            snippetDescriptionCheck(snippets)
            snippetTypeCheck(snippets)
            snippetDateCheck(snippets)
            snippetKeywordCheck(snippets)
    print(str(filecount) + " files scanned in " + directory)
    print("")

def checkFileStrings(directory, filePattern):
    filecount = 0;
    for path, dirs, files in os.walk(os.path.abspath(directory)):        
        for filename in fnmatch.filter(files, filePattern):
            wordcount = 0;
            filecount += 1
            filepath = os.path.join(path, filename)
            with open(filepath) as f:
                s = f.read()
                words = s.split()
            snippetStartCheck(words)
            for word in words:
                checkStringLength(word, filename)
                wordcount +=1;
            f.close();
            print("File: " + filename + " has been scanned. " + str(wordcount) + " words found.")
            print("")
    print(str(filecount) + " files scanned in " + directory)
    print("")
                
                
def checkStringLength (word, filename):
    length = len(word)
    if  length == 40 or length == 20:
        print ("String found in " + filename + " \n" + word + " is " + str(length) + " characters long")
        exit 1


def snippetStartCheck(words):
    #print (words)
    snippetStart = 'snippet-start:['
    snippetEnd = 'snippet-end:['
    if any(snippetStart in word for word in words) :
        matching = [s for s in words if snippetStart in s]
        Endmatching = [s for s in words if snippetEnd in s]
        #print(matching)
        snippettags = []
        for string in Endmatching: 
            snippettags += string.split(snippetEnd)
        if '//' in snippettags: snippettags.remove('//')
        if '#' in snippettags: snippettags.remove('#')
        #print(snippettags)
        #print(Endmatching)
        for string in matching:
            match = False
            for end in snippettags:
                if string.endswith(end):
                    match = True
            print(str(match) + ": " + string + " has matching end tag." )
    else: 
        print ("Snippet Start not detected")
        exit 1

def snippetAuthorCheck(words):
    author = 'sourceauthor:['
    matching = [s for s in words if author in s]
    if matching == []:
        print("Missing snippet-sourceauthor:[Your Name]")

def snippetServiceCheck(words):
    service = 'service:['
    matching = [s for s in words if service in s]
    if matching == []:
        print("Missing snippet-service:[AWS service name]")
        print("Find a list of AWS service names under AWS Service Namespaces in the General Reference Guide: https://docs.aws.amazon.com/general/latest/gr/aws-arns-and-namespaces.html")

def snippetDescriptionCheck(words):
    desc = 'sourcedescription:['
    matching = [s for s in words if desc in s]
    if matching == []:
        print("Missing snippet-sourcedescription:[Filename demonstrates how to ... ]")

def snippetTypeCheck(words):
    author = 'sourcetype:['
    matching = [s for s in words if author in s]
    containsType = False
    if matching == []:
        containsType = False
    for match in matching:
        if match.startswith('sourcetype:[full-example'):
            containsType = True
            break
        elif match.startswith('sourcetype:[snippet'):
            containsType = True
            break
    if not containsType:
        print("Missing snippet-sourcetype:[full-example] or snippet-sourcetype:[snippet]")
        

def snippetDateCheck(words):
    datetag = 'sourcedate:['
    matching = [s for s in words if datetag in s]
    if matching == []:
        print("Missing snippet-sourcedate:[YYYY-MM-DD]")

def snippetKeywordCheck(words):
    snippetkeyword = 'keyword:['
    matching = [s for s in words if snippetkeyword in s]
    # print(matching)
    codeSample = [s for s in words if 'keyword:[Code Sample]\n' in s]
    if not codeSample:
        print("Missing snippet-keyword:[Code Sample]")
    keywordServiceName(matching)
    keywordLanguageCheck(matching)
    keywordSDKCheck(matching)

def keywordServiceName(words):
    containsServiceTag = False;
    AWS = 'keyword:[AWS'
    matching = [s for s in words if AWS in s]
    if matching:
        containsServiceTag = True;
    Amazon = 'keyword:[Amazon'
    matching = [s for s in words if Amazon in s]
    if matching:
        containsServiceTag = True;
    if not containsServiceTag:
        print("Missing snippet-keyword:[FULL SERVICE NAME]")

def keywordLanguageCheck(words):
    languages = ['C++', '.NET', 'Go', 'Java', 'JavaScript', 'PHP', 'Python', 'Ruby','TypeScript' ]
    containsLanguageTag = False;
    for language in languages:
        languagekeyword = [s for s in words if 'keyword:[' + language + ']' in s]
        if languagekeyword:
            containsLanguageTag = True;
            break
    if containsLanguageTag == False:
        print("Missing snippet-keyword:[Language]")
        print("Options include:")
        print(languages)

def keywordSDKCheck(words):
    sdkVersions = ['AWS SDK for PHP v3', 'AWS SDK for Python (Boto3)', 'CDK V0.14.1' ]
    containsSDKTag = False;
    for sdk in sdkVersions:
        sdkkeyword = [s for s in words if 'keyword:[' + sdk + ']']
        if sdkkeyword:
            containsSDKTag = True;
            break
    if containsSDKTag == False:
        print("Missing snippet-keyword:[SDK Version used]")
        print("Options include:")
        print(sdkVersions)


print ('----------\n\nRun Tests\n')
print ('----------\n\nAWS SDK for C++\n')
checkFile( root, '*.cpp')
print ('----------\n\nAWS SDK for .NET\n')
checkFile( root, '*.cs')
print ('----------\n\nAWS SDK for Go\n')
checkFile( root, '*.go')
print ('----------\n\nAWS SDK for Java\n')
checkFile( root, '*.java')
print ('----------\n\nAWS SDK for JavaScript\n')
checkFile( root, '*.js')
print ('----------\n\nAWS SDK for PHP\n')
checkFile( root, '*.php')
print ('----------\n\nAWS SDK for Python\n')
checkFile( root, '*.py')
print ('----------\n\nAWS SDK for Ruby\n')
checkFile( root, '*.rb')
print ('----------\n\nAWS SDK for TypeScript\n')
checkFile( root, '*.ts')


