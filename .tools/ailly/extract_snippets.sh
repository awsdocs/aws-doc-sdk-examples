python3 .tools/validation/snippets.py
rm -rf .tools/ailly/.vectors
mv .snippets .tools/ailly/.vectors
cd .tools/ailly
npx @ailly/cli@1.1.1 --update-db --root .vectors --engine bedrock --plugin file://${PWD}/plugin.mjs
