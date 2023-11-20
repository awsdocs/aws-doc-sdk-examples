#!/usr/bin/env bash

SRC=rust_dev_preview
DEST=rustv1

git restore --staged .
git restore .
rm -rf $DEST

# List git text files, so we don't grep through 
git-ls-text-files() {
  git ls-files "${@:1}" | while read -r file; do
    if file --brief "$file" | grep -q text; then
      echo "$file"
    fi
  done
}

FOLDERS=(
  .doc_gen
  .github
  .tools
  applications
  resources
  "$SRC"
)

# Replace path across repo: rust_dev_preview with rust
RE="/snippet-(start|end)/!s/$SRC/$DEST/g"
git-ls-text-files "${FOLDERS[@]}" | xargs sed -i '' -e "$RE" 

# Not sure why these are getting skipped?
sed -i '' -e "$RE" README.md rust_dev_preview/**/{Dockerfile,*.py}

# Remove SDKs caveat
sed -i '' -e '/      caveat: "This documentation is for an SDK in preview release. The SDK is subject to change and should not be used in production."/d' .doc_gen/metadata/sdks.yaml

# Move files mv rust_dev_preview rust
mv $SRC $DEST
# Create symlink for forwarding
ln -s $DEST $SRC

.tools/readmes/multi.py --languages Rust:1 --no-dry-run

git add .
# git restore --staged migrate_rust_dev_preview.sh

echo "Remaining usages of $SRC":

git ls-files . | xargs grep $SRC 2>/dev/null