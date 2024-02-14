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
</ol>
<h3>Edit metadata via GUI</h3>
  <ol>
  <li>Install WAMP and setup your localhost. <a href="https://blog.containerize.com/how-to-install-and-configure-wamp-server-on-windows/">Instructions here.</a></li>
  <li>Create your authoring environment per <a href="https://w.amazon.com/bin/view/AWSDocs/CodeExamples/Team/SOS/#HAuthoringenvironment">Authoring environment</a>.</li>
  <li>Clone <a href="https://github.com/brmur/aws-doc-sdk-examples">aws-doc-sdk-examples GitHub repo</a> locally. <a href="https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository">Instructions here.</a></li>
  <li>Open your CLI tool, and navigate to "./doc_gen/metadata/sos_editor/</li>
  <li>Run 'npm install'</li>
  <li>Run 'node run.js' and follow the instructions.</li>
  <li>Open this file in your localhost.</li>
  </ol>
</div>




Copyright Amazon.com, Inc. or its affiliates. 
All Rights Reserved. SPDX-License-Identifier: Apache-2.0


    