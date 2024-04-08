---
skip: True
prompt: Write instructions on how to do Workflow Scouting with Ailly.
---

# How to Scout with Ailly

## Prereq: Install and run Ailly

1. On the command line, install globally as `ailly` with `npm install -g @ailly/cli`
   - and can then be updated with `npm install -g @ailly/cli@{version}`
   - As of this writing (2024-04-08), version is `1.2.5` or `1.2.6-rc1`
1. Choose your engine, probably bedrock.
   - Export an environment variable: `export AILLY_ENGINE=bedrock`.
   - Ensure you're using your AWS account (`ada` or copying the access keys from isengard)

## Readme

The first step is getting a feel for Ailly and working with it. We'll have it make a README for the workflow, edit the README ourselves for fine detailing, and use it in "assist" or "tmp" or "macro" mode (I need a good name for this) to do bulk editing tasks.

1. WITHOUT using an LLM, do the Workflow process. Meet a SME, develop a high level plan for what the workflow should do.
2. Create a new workspace for your scout in the `workflow` folder.
3. Create a new folder for doing Ailly work, `workflow/scout_name/content`, and cd into it.
4. Add a `.aillyrc` file with:
   - A greymatter head:
     ```
     ---
     combined: true
     ---
     ```
   - A level-setting prompt (Coming soon!) to guide output format.
   - The summary of the Code Examples team.
   - A summary of the service you're writing the workflow for.
   - The summary of the workflow.
   - A list of the API calls you expect to make.
5. Add a file, `01_README.md`, with a greymatter head:
   ```
   ---
   prompt: Create a README.md for this project
   ---
   ```
   - at any (and every?) point in this process, experiment with your own prompting!
6. Run ailly: `ailly 10_README.md`
7. Review the output. Correct it as desired.

   - If there's a thing you want to change, you can use Ailly like a macro
   - Let's say Ailly creates a list of the API calls to use, but doesn't include links
   - Create a folder, `tmp`
   - Create an aillyrc, `tmp/.aillyrc`, with
     ```
     ---
     isolated: true
     ---
     ```
   - Create a file, `tmp/links.md`
   - Add this head:

     ```
     ---
     prompt: |
       Reformat this list into links to the documentation. For instance, the SendEmail item should link to https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_SendEmail.html.

       SendEmail
       CreateEmailIdentity
       [paste the rest of your items]
      ---
     ```

   - Run Ailly: `ailly tmp/links.md`
   - Review the output, which should now have
     ```
     * [SendEmail](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_SendEmail.html)
     * [CreateEmailIdentity](https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_CreateEmailIdentity.html)
     * [The rest of your links]
     ```
   - If this doesn't work the first time, rerun and see if it helps.
     - If it doesn't a second time, play with the prompt until it does what you want.
   - Paste the output back into the `01_README.md` document.

## First Spec

At this point, you should have a good start on the README, which we can use to have Ailly write the spec. From current experiments, LLM generation works best with ~500 word output "chunks", so we start planning our Ailly calls around that. It also works best having additional context, so we'll get that first.

1. Help Ailly get API docs for the service.

   - Create a new file, `tmp/get_api.md`
   - Add a prompt:

     ```
     ---
     prompt: |
       Write a (bash or powershell or batch (or python?)) script to download the API docs for each API used in this workflow.

       An API doc link looks like https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_SendEmail.html

       After downloading, pass the HTML files first through `pup` to select '#main-content', then use `html2text -nobs -utf8` to get just the main text of the document.

       Put the downloaded text in a file, 10_{ApiName}.md, with a greymatter header that has a property `skip: true`.

       For example, `echo "---\nskip: true\n---" > 10_SendEmail.md\ncurl https://docs.aws.amazon.com/ses/latest/APIReference-V2/API_SendEmail.html | pup "#main-content" | html2text -nobs -utf8 >> 10_SendEmail.md`
     ---
     ```

   - This kind of prompt can take a bit of work to get right. In my experience, I spend a couple minutes digging into structure of the document, then use the command line tools like `pup`, `jq`, `yq`, `html2text`, `pandoc`
   - It might be faster to just copy/paste it a handful of times, but that's not as fun
   - Why spend 2 minutes doing repetitive tasks when we could spend 15 minutes learning how to make the LLM do the repetitive task for us?
   - To run the commands it generates, I just copy/paste them into my terminal. #yolo
     - Real #yolo will come maybe next quarter when I add a tools module to ailly to give it direct shell access
   - However you do it, make sure you end up with a few `10_API.md` files in your `content/` folder. These will be used by the remaining prompts.

2. Create the first part of the spec, `30_SPECIFICATION.md` (I left the 20s available for future use...)

   - Contents:

     ```
     ---
     prompt: |
       Write a specification for this workflow.

       [Lots of instructions for how you want it to handle the workflow!]

       [How should it handle input? Prompt, or variables? Team says prompt...]

       [Share what works and what doesn't! We can build a library of these!]

       [Example:]

       The specification is independent of any programming language, and should enable any programmer competent with programming using any published AWS SDK to follow along. It must specify the API calls to make, and it must include the parameters to send. It should describe the parameters in a list format.  Implementations will use the specific SDKs, so it does not need to specify URL calls to make, only the API calls and the request parameters to include. It must specify the exact environment variable names and files to use when referring to runtime data.
     ---
     ```

   - Run Ailly! `ailly 30_specification.md`
     - As we keep specifying the file name, Ailly will load the entire directory before this file as context, but only generate this file as output.
   - This output will, keeping with the "500 words", probably be pretty close to the README but a bit more formal. That's OK, we're going to build from here.
   - Iterate the prompt until you get a result you like. Keep copies or git commits, as you prefer, if you want.
   - When you have one you like, edit it a bit.

3. Create specific parts of the spec. If the workflow has five "parts", create `31_PART_1.md`, `32_PART_2.md`, etc. (Recommend replacing `PART_1` with the short name of the part or step.)

   - For each, this is the prompt I started with:
     ```
     ---
     prompt: |
       Describe the exact SESv2 API calls and parameters for this step.
       ## Prepare the Application
     ---
     ```
   - This prompt kinda sucked, TBH, but it got the job done. Lots of room for improvement here.
   - Run Ailly: `ailly 31_PART_1.md` (or `ailly 3{1,2,3}*.md`).
   - Iterate!

4. If the spec needs sample files, have Ailly & Claude make them!

   - `50_SAMPLE_FILES.md`
     ```
     ---
     prompt: |
       List and describe the sample files that this workflow will need at runtime.
     ---
     ```
   - `51_SAMPLE_FILE_A.md`
     ```
     ---
     prompt: |
       Create [Sample File A]
     ---
     ```
   - etc

5. Consolidate these files
   - Maybe in the future Ailly can have file system access and know how to issue instructions to combine the various in-progess files, but for now I just open the handful of files and copy/paste from `content/01_README.md` to `README.md`.
   - Open a PR and review the workflow spec

# First Language

## Structure

1. Create a folder for the language, say, `python`, and add an `.aillyrc`:

   ```
   You are a Python programmer, using Python 3.9.

   <examples>
   <example type="main with argument handling">
   ...python...
   </example>

   <example type="input request">
   ...python...
   </example>

   <example type="error handling">
   ...python...
   </example>

   <example type="sdk call">
   ...python...
   </example>

   <example type="pagination">
   ...python...
   </example>
   </examples>


   Imports should be sorted. [Additional notes as necessary]
   ```

   You may or may not include snippets for these, but they should help. If there are other examples you find help a lot, let the team know!

   Anthropic claims [claude does best with XML tags](https://docs.anthropic.com/claude/docs/long-context-window-tips)? But I've seen it do fine with markdown? So I'm playing with both, preferring Markdown for quick self contained things, and XML for longer/larger/more detailed examples.

   - When running ailly, run it from the folder with the original `.aillyrc` - `ailly python/20_PLAN.md`.

1. Copy the current files. Ailly uses `.aillyrc` files going up, but only includes files in the current folder for the current context.
   - Copy the README.md and SPECIFICATION.md consolidated files to `01_README.md` and `02_SPECIFICATION.md`.
1. Find the language-specific API documentation, and put it into `10_[API_CALL].md`.
   - Add greymatter to each with `skip: true` (don't want to be regenerating these.)
1. Create a `20_PLAN.md`. Your plan should have a prompt asking for the implementation files and class & method stubs, but not full implementations
   - Generate the plan. This might take a few passes. Maybe add some examples for how you want it structured. Eventually you should come out with an outline for the project you're satisfied with.
   - You can ask it to make the plan in the form of a shell script that would create the files it wants - this can be helpful if you know you'll need a few files for different parts.
   - Maybe create `21b_PLAN_SUPPLEMENTAL.md` with a `skip: true` and prompt: "When implementing methods, only implement the method you're currently instructed to implement." There may be other supplemental details as well.
     - The important part here is to remind you you can slip in additional details for all downstream instructions in a few different ways.
   - When running ailly, run it from the folder with the original `.aillyrc` - `ailly python/20_PLAN.md`.
1. Create `21{a,b,c}_PLAN_DETAILS.md`

   - Write a prompt that instructs the model to write itself prompts:

     ```
     Based on the plan, create a human prompt for each method in the SES2Mailer class, as well as the main method.

     Format your output as a shell heredoc cat that writes the prompt into a markdown file.

     The markdown should have yaml greymatter with two properties - `skip: true` and `prompt: ` with the content you generate.

     The prompt will be used as the `human` side of an LLM conversation.

     The file should get written to `51_{function_name}.md`.

     Start with these methods:
     - def create_email_identity(self):
     - def create_contact_list(self, contact_list_name):
     [... etc, copied from the plan]
     ```

   - This is the first "model writing for the model" step.
   - After running, you should get a shell script that will make these files.
   - You can also make the files yourselves

1. Create and generate the `51_{function name}.md` files from the PLAN_DETAILS step.
1. Repeat for testing (or did you proactively add testing in the original plan? Nice!)
1. Consolidate everything you have at this point to a new project. Maybe have Ailly write a script that does it for you?

## Run and Test

1. Create a new file, and paste the generated code in.
1. Use IDE tooling to fix type issues; run it, test it, etc.
1. Copy your edits back into the output, and add `skip: true` to lock them in.
1. Iterate. If your plan had 4 functions, but you wanted 5, edit the plan and rerun the steps after the plan.
1. If your main function did some special set up, edit it as well.
1. Edit the "first" function with your patterns and best practices. This will 'level set' future answers for running after this step.
1. Continue iteratively prompting for the next step of the plan, copying it to the working directory, running, testing, editing, and copying back.

> This process sucks, but [I don't know how to fix it yet](https://github.com/DavidSouther/ailly/issues/18). Ideas and PRs welcome.

# Second Language

1. Copy all your work from the first language to a second language.
1. Keep `skip: true` on README, Spec, and other language independent files.
1. Redo the API lookup and extraction.
1. Replace all first language- prompt words with the second - eg "write python to" with "write java to"
1. Rerun for the second language.
1. Re-iterate for the Run and Test steps.

# Metadata

1. Provide examples of metadata?
1. Provide metadata docs and instruction?
1. Ask for a patch to add snippet tags?
   - This didn't actually work - the patch rarely applies cleanly, and the locations aren't what I'd want.

# PLAN

- **Prepare** a precise prompt (by writing an aillyrc system prompt, providing supporting documents, and giving individual prompt steps).
- **Leverage** LLM models (by running Ailly on some or all parts of the context chain).
- **Assess** the generated content (as Ailly and the LLM writes output, make sure it's on the right track).
- **Narrow** your context (by editing Ailly's generated content to keep the conversation going where you want it to).
