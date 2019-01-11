import os, fnmatch, sys

def checkFile(directory, filePattern, noWarn, quiet):
    filecount = 0;
    for path, dirs, files in os.walk(os.path.abspath(directory)):        
        for filename in fnmatch.filter(files, filePattern):
            # Ignore this file
            if filename == sys.argv[0]:
                continue
            filecount += 1
            filepath = os.path.join(path, filename)
            if quiet == False:
                print("\nChecking File: " + filename + " in " + filepath)
            with open(filepath) as f:
                s = f.read()
                words = s.split()
            snippetStartCheck(words)
            snippets = s.split('snippet-')
            snippetAuthorCheck(snippets, noWarn, quiet)
            snippetServiceCheck(snippets, noWarn, quiet)
            snippetDescriptionCheck(snippets, noWarn, quiet)
            snippetTypeCheck(snippets, noWarn, quiet)
            snippetDateCheck(snippets, noWarn, quiet)
            snippetKeywordCheck(snippets, noWarn, quiet)
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
                checkStringLength(word, filename, noWarn, quiet)
                wordcount +=1;
            f.close();
            print("File: " + filename + " has been scanned. " + str(wordcount) + " words found.")
            print("")
    print(str(filecount) + " files scanned in " + directory)
    print("")
                
                
def checkStringLength (word, filename, noWarn, quiet):
    length = len(word)
    if  length == 40 or length == 20:
        if noWarn == False:
            sys.exit ("WARNING -- String found in " + filename + " \n" + word + " is " + str(length) + " characters long")


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
                    #print("True: "+ string + " has matching end tag." )                
            if match == False:
                sys.exit("ERROR -- " + string + "'s matching end tag not found.")                
    else:
        #print("WARNING -- Snippet Start not detected")
        return False

def snippetAuthorCheck(words, noWarn, quiet):
    author = 'sourceauthor:['
    matching = [s for s in words if author in s]
    if matching == []:
        if noWarn == False:
            print("WARNING -- Missing snippet-sourceauthor:[Your Name]")

def snippetServiceCheck(words, noWarn, quiet):
    service = 'service:['
    matching = [s for s in words if service in s]
    if matching == []:
        if noWarn == False:
            print("WARNING -- Missing snippet-service:[AWS service name]")
            print("Find a list of AWS service names under AWS Service Namespaces in the General Reference Guide: https://docs.aws.amazon.com/general/latest/gr/aws-arns-and-namespaces.html")

def snippetDescriptionCheck(words, noWarn, quiet):
    desc = 'sourcedescription:['
    matching = [s for s in words if desc in s]
    if matching == []:
        if noWarn == False:
            print("WARNING -- Missing snippet-sourcedescription:[Filename demonstrates how to ... ]")

def snippetTypeCheck(words, noWarn, quiet):
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
        if noWarn == False:
            print("WARNING -- Missing snippet-sourcetype:[full-example] or snippet-sourcetype:[snippet]")
        

def snippetDateCheck(words, noWarn, quiet):
    datetag = 'sourcedate:['
    matching = [s for s in words if datetag in s]
    if matching == []:
        if noWarn == False:
            print("WARNING -- Missing snippet-sourcedate:[YYYY-MM-DD]")

def snippetKeywordCheck(words, noWarn, quiet):
    snippetkeyword = 'keyword:['
    matching = [s for s in words if snippetkeyword in s]
    # print(matching)
    codeSample = [s for s in words if 'keyword:[Code Sample]\n' in s]
    if not codeSample:
        if noWarn == False:
            print("WARNING -- Missing snippet-keyword:[Code Sample]")
    keywordServiceName(matching, noWarn, quiet)
    keywordLanguageCheck(matching, noWarn, quiet)
    keywordSDKCheck(matching, noWarn, quiet)

def keywordServiceName(words, noWarn, quiet):
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
        if noWarn == False:
            print("WARNING -- Missing snippet-keyword:[FULL SERVICE NAME]")

def keywordLanguageCheck(words, noWarn, quiet):
    languages = ['C++', 'C', '.NET', 'Go', 'Java', 'JavaScript', 'PHP', 'Python', 'Ruby','TypeScript' ]
    containsLanguageTag = False;
    for language in languages:
        languagekeyword = [s for s in words if 'keyword:[' + language + ']' in s]
        if languagekeyword:
            containsLanguageTag = True;
            break
    if containsLanguageTag == False:
        if noWarn == False:
            print("WARNING -- Missing snippet-keyword:[Language]")
            print("Options include:")
            print(languages)

def keywordSDKCheck(words, noWarn, quiet):
    sdkVersions = ['AWS SDK for PHP v3', 'AWS SDK for Python (Boto3)', 'CDK V0.14.1' ]
    containsSDKTag = False;
    for sdk in sdkVersions:
        sdkkeyword = [s for s in words if 'keyword:[' + sdk + ']']
        if sdkkeyword:
            containsSDKTag = True;
            break
    if containsSDKTag == False:
        if noWarn == False:
            print("WARNING -- Missing snippet-keyword:[SDK Version used]")
            print("Options include:")
            print(sdkVersions)

# We allow two args:
#     -w to suppress warnings
#     -q to suppress name of file we are parsing (quiet mode)
noWarn = False;
quiet = False;

i = 0;

while i < len(sys.argv):
    if sys.argv[i] == "-w":
        noWarn = True
    elif sys.argv[i] == "-q":
        quiet = True
    i += 1

print ('----------\n\nRun Tests\n')
print ('----------\n\nC++ Code Examples(*.cpp)\n')
checkFile( './', '*.cpp', noWarn, quiet)
print ('----------\n\nC# Code Examples (*.cs)\n')
checkFile( './', '*.cs', noWarn, quiet)
print ('----------\n\nGo Code Examples (*.go)\n')
checkFile( './', '*.go', noWarn, quiet)
print ('----------\n\nJava Code Examples (*.java)\n')
checkFile( './', '*.java', noWarn, quiet)
print ('----------\n\nJavaScript Code Examples (*.js)\n')
checkFile( './', '*.js', noWarn, quiet)
print ('----------\n\nPHP Code Examples (*.php)\n')
checkFile( './', '*.php', noWarn, quiet)
print ('----------\n\nPython Code Examples (*.py)\n')
checkFile( './', '*.py', noWarn, quiet)
print ('----------\n\nRuby Code Examples (*.rb)\n')
checkFile( './', '*.rb', noWarn, quiet)
print ('----------\n\nTypeScript Code Examples (*.ts)\n')
checkFile( './', '*.ts', noWarn, quiet)
