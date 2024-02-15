# SoS editor    
**This app is for internal AWS use only**.

SoS editor provide a GUI metadata to publish code examples to AWS Docs.
For details about SoS metadata, see [SoS Metadata](https://w.amazon.com/bin/view/AWSDocs/CodeExamples/Team/SOS/).

This metadata can also be edited manaully. The purpose of this tool is to make the process easier, and to ensure metadata is accurate.

## Usage
<div>
<h3>Prerequisites</h3>
  <p id ="intro"><label class="thissize">Before you begin:</label><br>
  <ol>
   <li><p>Ensure your <i>USERPROFILE</i> is set to 'C:\Users\[username]' </p>
   <p>To get a list of all environment variables enter the command <i><b>set</b></i>.</p>
  <p>You can send them to a text file enter the command <i><b>set > filename.txt</b></i>.</p></li>
  <li>You need a local web server environment that serves web pages on localhost, such as <a href="https://blog.containerize.com/how-to-install-and-configure-wamp-server-on-windows/">WAMP.</a></li>
  <li>You need an IDE with a CLI terminal, such as <a href-"https://w.amazon.com/bin/view/IntelliJ"IntelliJ</a>.</li>
</ol>
<h3>Edit metadata via GUI</h3>
  <ol>
  <li>In you IDE clone <a href="https://github.com/brmur/aws-doc-sdk-examples">aws-doc-sdk-examples GitHub repo</a>. See <a href="https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository">instructions here.</a></li>
  <li>Open your IDEs CLI terminal, and and navigate to "./doc_gen/metadata/sos_editor/js</li>
  <li>Run 'npm install'.</li>
  <li>Run 'node run.js' and follow the instructions.</li>
  </ol>
</div>




Copyright Amazon.com, Inc. or its affiliates. 
All Rights Reserved. SPDX-License-Identifier: Apache-2.0


    
