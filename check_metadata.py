import os, fnmatch, sys

def checkFile(directory, filePattern, warn, filelocation):
    filecount = 0;
    for path, dirs, files in os.walk(os.path.abspath(directory)):        
        for filename in fnmatch.filter(files, filePattern):
            # Ignore this file
            if filename == sys.argv[0]:
                continue
            wordcount = 0;
            filecount += 1
            filepath = os.path.join(path, filename)
            if warn == True:
                print ("")
            if quiet == False:
                print("\nChecking File: " + filepath)
            with open(filepath) as f:
                s = f.read()
                words = s.split()
            for word in words:
                checkStringLength(word, warn, filepath)
                wordcount +=1;
            snippetStartCheck(words)
            snippets = s.split('snippet-')
            snippetAuthorCheck(snippets, warn, filepath)
            snippetServiceCheck(snippets, warn, filepath)
            snippetDescriptionCheck(snippets, warn, filepath)
            snippetTypeCheck(snippets, warn, filepath)
            snippetDateCheck(snippets, warn, filepath)
            snippetKeywordCheck(snippets, warn, filepath)
            f.close()
        
    print(str(filecount) + " files scanned in " + directory)
    print("")


def checkStringLength (word, warn, filelocation):
    length = len(word)
    if  length == 40 or length == 20:
        if warn == True:            
            print("WARNING -- " + word + " is " + str(length) + " characters long.")
            if quiet == True:
                print("  Found in " + filelocation)
             


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
                if quiet == True:
                    print("  Found in " + filelocation)
                sys.exit("ERROR -- " + string + "'s matching end tag not found.")                
    else:
        #print("WARNING -- Snippet Start not detected")
        return False

def snippetAuthorCheck(words, warn, filelocation):
    author = 'sourceauthor:['
    matching = [s for s in words if author in s]
    if matching == []:
        if warn == True:
            print("WARNING -- Missing snippet-sourceauthor:[Your Name]")
            if quiet == True:
                print("  Found in " + filelocation)

def snippetServiceCheck(words, warn, filelocation):
    service = 'service:['
    matching = [s for s in words if service in s]
    if matching == []:
        if warn == True:
            print("WARNING -- Missing snippet-service:[AWS service name]")
            if quiet == True:
                print("  Found in " + filelocation)
            print("Find a list of AWS service names under AWS Service Namespaces in the General Reference Guide: https://docs.aws.amazon.com/general/latest/gr/aws-arns-and-namespaces.html")

def snippetDescriptionCheck(words, warn, filelocation):
    desc = 'sourcedescription:['
    matching = [s for s in words if desc in s]
    if matching == []:
        if warn == True:
            print("WARNING -- Missing snippet-sourcedescription:[Filename demonstrates how to ... ]")
            if quiet == True:
                print("  Found in " + filelocation)

def snippetTypeCheck(words, warn, filelocation):
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
        if warn == True:
            print("WARNING -- Missing snippet-sourcetype:[full-example] or snippet-sourcetype:[snippet]")
            if quiet == True:
                print("  Found in " + filelocation)
        

def snippetDateCheck(words, warn, filelocation):
    datetag = 'sourcedate:['
    matching = [s for s in words if datetag in s]
    if matching == []:
        if warn == True:
            print("WARNING -- Missing snippet-sourcedate:[YYYY-MM-DD]")
            if quiet == True:
                print("  Found in " + filelocation)

def snippetKeywordCheck(words, warn, filelocation):
    snippetkeyword = 'keyword:['
    matching = [s for s in words if snippetkeyword in s]
    # print(matching)
    codeSample = [s for s in words if 'keyword:[Code Sample]\n' in s]
    if not codeSample:
        if warn == True:
            print("WARNING -- Missing snippet-keyword:[Code Sample]")
            if quiet == True:
                print("  Found in " + filelocation)
    keywordServiceName(matching, warn, filelocation)
    keywordLanguageCheck(matching, warn, filelocation)
    keywordSDKCheck(matching, warn, filelocation)

def keywordServiceName(words, warn, filelocation):
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
        if warn == True:
            print("WARNING -- Missing snippet-keyword:[FULL SERVICE NAME]")
            if quiet == True:
                print("  Found in " + filelocation)

def keywordLanguageCheck(words, warn, filelocation):
    languages = ['C++', 'C', '.NET', 'Go', 'Java', 'JavaScript', 'PHP', 'Python', 'Ruby','TypeScript' ]
    containsLanguageTag = False;
    for language in languages:
        languagekeyword = [s for s in words if 'keyword:[' + language + ']' in s]
        if languagekeyword:
            containsLanguageTag = True;
            break
    if containsLanguageTag == False:
        if warn == True:
            print("WARNING -- Missing snippet-keyword:[Language]")
            if quiet == True:
                print("  Found in " + filelocation)
            print("Options include:")
            print(languages)
            

def keywordSDKCheck(words, warn, filelocation):
    sdkVersions = ['AWS SDK for PHP v3', 'AWS SDK for Python (Boto3)', 'CDK V0.14.1' ]
    containsSDKTag = False;
    for sdk in sdkVersions:
        sdkkeyword = [s for s in words if 'keyword:[' + sdk + ']']
        if sdkkeyword:
            containsSDKTag = True;
            break
    if containsSDKTag == False:
        if warn == True:
            print("WARNING -- Missing snippet-keyword:[SDK Version used]")
            if quiet == True:
                print("  Found in " + filelocation)
            print("Options include:")
            print(sdkVersions)

# We allow two args:
#     -w to suppress warnings
#     -q to suppress name of file we are parsing (quiet mode)
warn = True;
quiet = True;

i = 0;

while i < len(sys.argv):
    if sys.argv[i] == "-w":
        warn = False
    elif sys.argv[i] == "-q":
        quiet = True
    i += 1

print ('----------\n\nRun Tests\n')
print ('----------\n\nC++ Code Examples(*.cpp)\n')
checkFile( './', '*.cpp', warn, quiet)
print ('----------\n\nC# Code Examples (*.cs)\n')
checkFile( './', '*.cs', warn, quiet)
print ('----------\n\nGo Code Examples (*.go)\n')
checkFile( './', '*.go', warn, quiet)
print ('----------\n\nJava Code Examples (*.java)\n')
checkFile( './', '*.java', warn, quiet)
print ('----------\n\nJavaScript Code Examples (*.js)\n')
checkFile( './', '*.js', warn, quiet)
print ('----------\n\nPHP Code Examples (*.php)\n')
checkFile( './', '*.php', warn, quiet)
print ('----------\n\nPython Code Examples (*.py)\n')
checkFile( './', '*.py', warn, quiet)
print ('----------\n\nRuby Code Examples (*.rb)\n')
checkFile( './', '*.rb', warn, quiet)
print ('----------\n\nTypeScript Code Examples (*.ts)\n')
checkFile( './', '*.ts', warn, quiet)
