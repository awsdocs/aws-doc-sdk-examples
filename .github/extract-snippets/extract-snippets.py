# extract-snippets.py v1.2.0 6/24/2021
# Jerry Kindall, Amazon Web Services

# extracts tagged regions from source files and writes them to a snippets directory.
# reads list of paths from stdin and extracts snippets from these files.  Takes the
# directory to which snippets should be extracted as a command line argument.  The
# second command line argument is optional and specifies the YAML file that contains
# a map of filename extensions to comment markers (default: snippet-extensions.yml
# in the same directory as this script)

# examples:
#
#       extract snippets from last commit on current git branch to /tmp
#           git diff @^ --name-only | python3 extract-snippets.py /tmp
#
#       extract snippets from all files to specified directory
#           find . -type f | python3 extract-snippets.py /path/to/snippets/dir
#
#       extract snippets from all files in current dir to current dir,
#       specifying a different filename map
#           ls | python3 extract-snippets.py . snippet-extensions-more.yml

# The same snippet can be extracted from more than one source file ONLY if all
# source files containing the snippet have the same filename and contents.  
# this is to support e.g. Lambda functions deployed by a CDK example, where the
# CDK app is provided in multiple languages but the same Lambda function source
# code (snippet tags included) is used in each version.  Ideally the snippet tags
# would be removed from all but one of the Lambda source files... ideally.

# This script also presents an index mapping snippet names back to the files
# they come from, and a list of source files having problems.

import sys, os, io, yaml, re, functools

# all open() calls have an implied encoding parameter, UTF-8 by default
open = functools.partial(__builtins__.open, 
    encoding=os.environ.get("SOURCE_ENCODING", "utf8"))

# some constants to make our lives easier
TAB = "\t"
EOL = "\n"

# regular expression for matching dedent specifier: 1 or 2 digits
DIGITS = re.compile("[0-9][0-9]?")

# returns cached contents of a file if it exists, or reads it into the cache and
# returns it if not.  cache is stored as a default parameter value.
#
# the cache is used only when there are duplicate snippets in two or more source files.
# only one copy of the file is ever cached (the first one that was found) so this shouldn't
# run up memory too much if you don't have many duplicate snippets.
def cached(path, cache={}):
    if path not in cache:
        with open(path) as infile:
            cache[path] = infile.read().rstrip()
    return cache[path]

# a file-like object used to avoid writing duplicate snippets we've already extracted
# in situations where this is not an error
class DummyFile:
    def __init__(self, *args, **kwargs):
        pass
    def write(self, text):
        pass
    def close(self):
        pass

# auto-vivifying dict (like DefaultDict but we don't need to import it)
class AutoDict(dict):
    def __init__(self, T):
        self.T = T
    def __missing__(self, key):
        self[key] = self.T()
        return self[key]

# the class that does the snippet extraction. instantiate it passing the directory to
# which snippets should be extracted.  call the instance with each source file.
class Snipper:

    # initialize Snipper
    def __init__(self, snippetdir):
        self.dir        = snippetdir        # directory where snippets will be extracted
        self.source     = {}                # source file of each snippet
        self.count      = 0                 # number of snippets extracted
        self.errors     = 0                 # processing errors
        self.issues     = AutoDict(set)     # files with issues
        self.index      = AutoDict(list)    # index of snippets to files (this should probably be merged with self.source)
        self.log        = io.StringIO()

    # if used as context manager, we capture the log instead of printing it as we go
    # by switching print() to print to a StringIO object
    def __enter__(self):
        global print
        print = functools.partial(__builtins__.print, file=self.log)
        return self

    def __exit__(self, *args):
        global print 
        print = __builtins__.print

    # extract snippets from a single file
    def __call__(self, path, markers):
        print(path)
        self.started    = set()         # snippets we've started in this source file
        self.duplicates = set()         # snippets we've determined are duplicates so we won't append/echo
        tag = re.compile(f" *({'|'.join(markers)}) ?snippet-") # e.g. if ext is "// #" end up with regex: " *(#|//) ?snippet-"
        self.files  = {}                # files currently open to write snippets
        self.dedent = {}                # amount of whitespace to strip from each line of snippet
        self.path = path                # source file we are working with (store it on instance so we can use it in error messages)
        self.markers = markers
        try:
            with open(path) as infile:      # read source file entirely into memory
                self.text = infile.read().rstrip()
        except IOError as ex:
            print("ERROR reading file", ex)
            self.errors += 1
            return
        if TAB in self.text and "snippet-start" in self.text:
            print("    WARNING tab(s) found in %s may cause formatting problems in docs" % path)
        # process each line in source file. self.i is the line we're on (for error messages)
        for self.i, self.line in enumerate(self.text.splitlines(keepends=False), start=1):
            line = self.line            # use a local variable for a bit more performance
            if tag.match(line):         # line is a snippet directive, parse and process it
                self.directive = line.split("snippet-")[1].split(":")[0].rstrip()   # get e.g. append fron snippet-append
                self.arg = line.split("[")[1].split("]")[0].rstrip()                # get e.g. snippet-name from [snippet-name]
                func = getattr(self, self.directive.lstrip("_"), None)
                if func and callable(func):
                    func(self.arg)      # call our method named same as directive (e.g. start(..) for snippet-start)
                else:
                    print("    ERROR invalid directive snippet-%s at %s in %s" % (self.directive, self.i, self.path))
                    self.errors += 1
                    self.issues[path].add("invalid directive snippet-%s" % self.directive)
            else:                       # line is NOT a snippet directive. write it to any open snippet files
                for snip, file in self.files.items():           # for each snippet file we're writing, write the line
                    dedent = self.dedent[snip]
                    if dedent and line[:dedent].strip():        # is the text we want to strip to dedent all whitespace? error if not 
                        print(("    ERROR unable to dedent %s space(s) " % dedent) + 
                            ("in snippet %s at line %s in %s " % self._where) + 
                            f"(only indented {len(line) - len(line.lstrip())} spaces)")
                        self.errors += 1
                    file.write(line[dedent:].rstrip() + EOL)    # write it (strip whitespace at end just to be neat)
        # done processing this file. make sure all snippets had snippet-end tags
        for snip, file in self.files.items():
            print("    ERROR snippet-end tag for %s missing in %s, extracted to end of file" % (snip, path))
            file.close()
            self.issues[path].add("snippet-end tag for %s missing" % snip)
            self.errors += 1

    # directive: beginning of snippet
    def start(self, arg):
        path = os.path.join(self.dir, f"{arg}.txt")
        indicator = "EXTRACT"
        opener = open
        printer = print
        if arg in self.files:
            printer = lambda *a: print("    ERROR snippet %s already open at line %s in %s" % self._where)
            self.issues[self.path].add("snippet %s opened multiple times")
            self.errors += 1
        elif os.path.isfile(path):
            # if snippet output already exists, this is OK only if it source file has the same name and identical content
            if self.path != self.source[arg] and self.path.rpartition("/")[2] == self.source[arg].rpartition("/")[2] and self.text == cached(self.source[arg]):
                printer = lambda *a: print("WARNING redundant snippet %s at line %s in %s" % self._where)
                self.duplicates.add(arg)
            else:
                printer = lambda *a: print("    ERROR duplicate snippet %s at line %s in %s" % self._where,
                    "(also in %s)" % self.source[arg])
                pfxlen = len(os.path.commonprefix([self.path, self.source[arg]]))
                path1 = self.source[arg][pfxlen:]
                if "/" not in path1: path1 = self.source[arg]
                path2 = self.path[pfxlen:]
                if "/" not in path2: path2 = self.path
                self.issues[self.path].add("%s also declared in %s" % (arg, path1))
                self.issues[self.source[arg]].add("%s also declared in %s" % (arg, path2))
                self.errors += 1
            opener = DummyFile      # don't write to the file, but still track it so we can detect missing snippet-end
        else:
            self.count += 1
        # parse number at end of line as dedent value
        self.dedent[arg] = int(DIGITS.search(self.line.rpartition("]")[2] + " 0").group(0))
        self.files[arg] = opener(path, "w")     # open real file or dummy
        self.index[arg].append(self.path)
        self.started.add(arg)       # record that we started this snippet in this source file
        if arg not in self.source:  # record that we *first* saw this snippet in this source file
            self.source[arg] = self.path
        printer("   ", indicator, arg)

    # directive: append to given file (for extracting multiple chunks of code to a single snippet)
    def append(self, arg):
        if arg in self.files:           # is the file already open?
            print("    ERROR snippet %s already open at line %s in %s" % self._where)
            self.issues[self,path].add("snippet %s opened multiple times" % arg)
            self.errors += 1
            return
        if arg not in self.started:     # did we start this snippet in current source file?
            print("    ERROR snippet file %s not found at line %s in %s" % self._where)
            self.issues[self.path].add("snippet %s doesn't exist" % arg)
            self.errors += 1
            return
        self.files[arg] = DummyFile() if arg in self.duplicates else open(os.path.join(self.dir, arg) + ".txt", "a")
        print("    APPEND", arg)

    # directive: end of snippet
    def end(self, arg):
        if arg in self.files:
            self.files[arg].close()
            del self.files[arg]
        else:
            print("    ERROR snippet file %s not open at %s in %s" % self._where)
            self.issues[self.path].add("snippet-end tag for %s which is not open" % arg)
            self.errors += 1

    # directive: insert arg verbatim as a line into all currently open snippets
    # useful for e.g. adding closing brackets to partial code block (could also use append for that)
    def echo(self, arg):
        arg = arg.rstrip() + EOL
        if self.files:
            for file in self.files.values():
                file.write(arg)
        else:
            print("    ERROR echo '%s' outside snippet at %s in %s" % self._where)
            self.issues[self.path].add("echo outside snippet")
            self.errors += 1

    # do-nothing handler used for directives that we ignore
    def _nop(self, arg): return

    # the aforementioned ignored directives
    service = comment = keyword = sourceauthor = sourcedate = sourcedescription = sourcetype = sourcesyntax = _nop

    # convenience property for returning error location tuple (used in error messages)
    @property
    def _where(self):
        return self.arg, self.i, self.path

def err_exit(msg):
    print("ERROR", msg)
    sys.exit(1)

# ----------------------------------------------------------------------------

if __name__ == "__main__":
    
    # read list of filenames from stdin first, so we don't get broken pipe if we error out
    stdin_lines = []
    if not sys.stdin.isatty():
        stdin_lines = sys.stdin.readlines()

    # get output directory from command line, or error
    if len(sys.argv) > 1 and os.path.isdir(sys.argv[1]):
        snippetdir = sys.argv[1]
    else:
        err_exit("snippet output directory not passed or does not exist")
    
    # get filename of extersions list from command line, or use default, then load it
    if len(sys.argv) > 2:
        commentfile = sys.argv[2]
    else:
        commentfile = "snippet-extensions.yml"

    # reports to be printed can be passed in via environment variable REPORTS
    # if this value is not set, print all reports
    reports = os.environ.get("REPORTS", "log issues index").lower().split()

    # if no directory specified, file is in same directory as script
    if "/" not in commentfile and "\\" not in commentfile:
        commentfile = os.path.join(os.path.dirname(__file__), commentfile)
    if not os.path.isfile(commentfile):
        err_exit("source file extension map %s not found" % commentfile)
    with open(commentfile) as comments:
        MAP_EXT_MARKER = yaml.safe_load(comments)
        if not isinstance(MAP_EXT_MARKER, dict):
            err_exit("source map is not a key-value store (dictionary)")
        for k, v in MAP_EXT_MARKER.items():
            if isinstance(k, str) and isinstance(v, str):
                MAP_EXT_MARKER[k] = v.split()
            else:
                err_exit("key, value must both be strings; got %s, %s (%s, %s)" % 
                    (k, v, type(k).__name__, type(v).__name__))

    print("==== extracting snippets in source files", 
        " ".join(ex for ex in MAP_EXT_MARKER if ex and MAP_EXT_MARKER[ex]), "\n")
    print("reports:", " ".join(reports).upper(), end="\n\n")

    # initialize snipper instance and our counters
    with Snipper(snippetdir) as snipper:
        seen = processed = 0

        # main loop: for each file named on stdin, check to see if we should process it, and if so, do so
        for path in sorted(stdin_lines):
            path = path.strip()
            if not path:                                    # skip blank lines in input
                continue
            # make sure relative path starts with ./ so that e.g. /Makefile in the extensions map
            # can be used to match an entire filename. 
            if not (path.startswith(("./", "/", "\\")) or   # already relative or Linux/Mac absolute path or UNC path
                (path[0].isalpha() and path[1] == ":")):    # already Windows absolute path
                    path = "./" + path
            if "/." in path or "\\." in path:               # skip hidden file or directory
                continue
            seen += 1                                       # count files seen (not hidden)
            # find first extension from extension map that matches current file
            # replace backslashes with forward slashes for purposes of matching so it works with Windows or UNC paths
            ext = next((ext for ext in MAP_EXT_MARKER if path.replace("\\", "/").endswith(ext)), None)
            markers = MAP_EXT_MARKER.get(ext, ())
            if markers:                                     # process it if we know its comment markers
                snipper(path, markers)
                processed += 1
    
    # files with issues report (files with most issues first)
    if "issues" in reports:
        if snipper.issues:
            print("====", len(snipper.issues), "file(s) with issues:", end="\n\n")
            for issue, details in sorted(snipper.issues.items(), key=lambda item: -len(item[1])):
                print(issue, end="\n     ")
                print(*sorted(details), sep="\n     ", end="\n\n")
        else:
            print("---- no issues found\n")

    # snippet index report (snippets that appear in the most files first)
    if "index" in reports:
        if snipper.index:
            print("====", len(snipper.index), "snippet(s) extracted from", processed, "files:", end="\n\n")
            for snippet, files in sorted(snipper.index.items(), key=lambda item: -len(item[1])):
                print(snippet, "declared in:", end="\n     ")
                print(*sorted(files), sep="\n     ", end="\n\n")
        else:
            print("--- no snippets were extracted\n")

    # print log
    if "log" in reports:
        print("==== Complete processing log\n")
        if processed:
            print(snipper.log.getvalue(), end="\n\n")
        else:
            print("No files were processed\n")

    # print summary
    print("====", snipper.count, "snippet(s) extracted from", processed, 
        "source file(s) processed of", seen, "candidate(s) with", snipper.errors, 
        "error(s) in", len(snipper.issues), "file(s)\n")

    # exit with nonzero status if we found any errors, so caller won't commit the snippets
    sys.exit(snipper.errors > 0)
