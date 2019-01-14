import os, fnmatch, sys

def checkFile(directory, filePattern, warn, quiet):
    filecount = 0;
    for path, dirs, files in os.walk(os.path.abspath(directory)):        
        for filename in fnmatch.filter(files, filePattern):
            # Ignore this file
            if filename == sys.argv[0]:
                continue
            wordcount = 0;
            filecount += 1
            errors = []
            filepath = os.path.join(path, filename)
            if quiet == False:
                print("\nChecking File: " + filepath)
            with open(filepath) as f:
                s = f.read()
                words = s.split()
            for word in words:
                checkStringLength(word, warn)
                wordcount +=1;

            # Check for mismatched Snippet start and end.    
            snippets = s.split('snippet-')

            # Check Metadata for optional metadata
            errors.append(snippetStartCheck(words, filepath))
            errors.append(snippetAuthorCheck(snippets, warn))
            errors.append(snippetServiceCheck(snippets, warn))
            errors.append(snippetDescriptionCheck(snippets, warn))
            errors.append(snippetTypeCheck(snippets, warn))
            errors.append(snippetDateCheck(snippets, warn))
            errors.append(snippetKeywordCheck(snippets, warn))
            f.close()
            if quiet == False:
                print(str(wordcount) + " words found.")
            if warn == True:
                # Filter to only warning messages
                errors = list(filter(None, errors))
                # print out file name, if warnings found
                if len(errors) > 0 and quiet == True:
                    print("\nChecking File: " + filepath)
                for error in errors:
                    if error:
                        print(error)
    print(str(filecount) + " files scanned in " + directory)
    print("")


def checkStringLength (word, warn):
    length = len(word)
    if  length == 40 or length == 20:
        if warn == True:            
            return "WARNING -- " + word + " is " + str(length) + " characters long"


def snippetStartCheck(words, filelocation):
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
                    #return "True: "+ string + " has matching end tag." )                
            if match == False:
                print("ERROR -- Found in " + filelocation)
                sys.exit("ERROR -- " + string + "'s matching end tag not found.")                
    else:
        #return "WARNING -- Snippet Start not detected"
        return False

def snippetAuthorCheck(words, warn):
    author = 'sourceauthor:['
    matching = [s for s in words if author in s]
    if matching == []:
        if warn == True:
            return "WARNING -- Missing snippet-sourceauthor:[Your Name]"

def snippetServiceCheck(words, warn):
    service = 'service:['
    matching = [s for s in words if service in s]
    if matching == []:
        if warn == True:
            return "WARNING -- Missing snippet-service:[AWS service name] \nFind a list of AWS service names under AWS Service Namespaces in the General Reference Guide: https://docs.aws.amazon.com/general/latest/gr/aws-arns-and-namespaces.html"

def snippetDescriptionCheck(words, warn):
    desc = 'sourcedescription:['
    matching = [s for s in words if desc in s]
    if matching == []:
        if warn == True:
            return "WARNING -- Missing snippet-sourcedescription:[Filename demonstrates how to ... ]"

def snippetTypeCheck(words, warn):
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
            return "WARNING -- Missing snippet-sourcetype:[full-example] or snippet-sourcetype:[snippet]"
        

def snippetDateCheck(words, warn):
    datetag = 'sourcedate:['
    matching = [s for s in words if datetag in s]
    if matching == []:
        if warn == True:
            return "WARNING -- Missing snippet-sourcedate:[YYYY-MM-DD]"

def snippetKeywordCheck(words, warn):
    snippetkeyword = 'keyword:['
    matching = [s for s in words if snippetkeyword in s]
    # print(matching)
    codeSample = [s for s in words if 'keyword:[Code Sample]\n' in s]
    if not codeSample:
        if warn == True:
            return "WARNING -- Missing snippet-keyword:[Code Sample]"
    keywordServiceName(matching, warn)
    keywordLanguageCheck(matching, warn)
    keywordSDKCheck(matching, warn)

def keywordServiceName(words, warn):
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
            return "WARNING -- Missing snippet-keyword:[FULL SERVICE NAME]"

def keywordLanguageCheck(words, warn):
    languages = ['C++', 'C', '.NET', 'Go', 'Java', 'JavaScript', 'PHP', 'Python', 'Ruby','TypeScript' ]
    containsLanguageTag = False;
    for language in languages:
        languagekeyword = [s for s in words if 'keyword:[' + language + ']' in s]
        if languagekeyword:
            containsLanguageTag = True;
            break
    if containsLanguageTag == False:
        if warn == True:
            return "WARNING -- Missing snippet-keyword:[Language] \nOptions include:" + ', '.join(languages)
            

def keywordSDKCheck(words, warn):
    sdkVersions = ['AWS SDK for PHP v3', 'AWS SDK for Python (Boto3)', 'CDK V0.14.1' ]
    containsSDKTag = False;
    for sdk in sdkVersions:
        sdkkeyword = [s for s in words if 'keyword:[' + sdk + ']']
        if sdkkeyword:
            containsSDKTag = True;
            break
    if containsSDKTag == False:
        if warn == True:
            return "WARNING -- Missing snippet-keyword:[SDK Version used] \nOptions include:" + ', '.join(sdkVersions)

# We allow two args:
#     -w to suppress warnings
#     -q to suppress name of file we are parsing (quiet mode)
warn = True;
quiet = False;

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
