# snippet-sourcedescription:[push-in-parts.py demonstrates how to migrate a Git repository in increments and repushes only those increments that did not succeed until the migration is complete.]
# snippet-service:[codecommit]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[AWS CodeCommit]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Git]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2015-07-09]
# snippet-sourceauthor:[AWS]
# snippet-start:[codecommit.python.push-in-parts.complete]
# Copyright 2015 Amazon.com, Inc. or its affiliates. All Rights Reserved. Licensed under the Amazon Software License (the "License"). 
# You may not use this file except in compliance with the License. A copy of the License is located at 
#    http://aws.amazon.com/asl/ 
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, express or implied. See the License for
# the specific language governing permissions and limitations under the License.

#!/usr/bin/env python

import os
import sys
from optparse import OptionParser
from git import Repo, TagReference, RemoteProgress, GitCommandError

class PushProgressPrinter(RemoteProgress):
    def update(self, op_code, cur_count, max_count=None, message=''):
        op_id = op_code & self.OP_MASK
        stage_id = op_code & self.STAGE_MASK
        if op_id == self.WRITING and stage_id == self.BEGIN:
            print("\tObjects: %d" % max_count)

class RepositoryMigration:

    MAX_COMMITS_TOLERANCE_PERCENT = 0.05
    PUSH_RETRY_LIMIT = 3
    MIGRATION_TAG_PREFIX = "codecommit_migration_"

    def migrate_repository_in_parts(self, repo_dir, remote_name, commit_batch_size, clean):
        """
        Migrates the files from the local to remote repository.
        :param repo_dir (string): The repository directory we are working with.
        :param remote_name (string): Name of the remote repository which we are migrating to.
        :param commit_batch_size (int): max size of batch for each commit.
        :param clean (Boolean): boolean to determine whether to delete tag .
        """

        self.next_tag_number = 0
        self.migration_tags = []
        self.walked_commits = set()
        self.local_repo = Repo(repo_dir)
        self.remote_name = remote_name
        self.max_commits_per_push = commit_batch_size
        self.max_commits_tolerance = self.max_commits_per_push * self.MAX_COMMITS_TOLERANCE_PERCENT

        try:
            self.remote_repo = self.local_repo.remote(remote_name)
            self.get_remote_migration_tags()
        except (ValueError, GitCommandError):
            print("Could not contact the remote repository. The most common reasons for this error are that the name of the remote repository is incorrect, or that you do not have permissions to interact with that remote repository.")
            sys.exit(1)

        if clean:
            self.clean_up(clean_up_remote=True)
            return

        self.clean_up()

        print("Analyzing repository")
        head_commit = self.local_repo.head.commit
        sys.setrecursionlimit(max(sys.getrecursionlimit(), head_commit.count()))

        # tag commits on default branch
        leftover_commits = self.migrate_commit(head_commit)
        self.tag_commits([commit for (commit, commit_count) in leftover_commits])

        # tag commits on each branch
        for branch in self.local_repo.heads:
            leftover_commits = self.migrate_commit(branch.commit)
            self.tag_commits([commit for (commit, commit_count) in leftover_commits])

        # push the tags
        self.push_migration_tags()

        # push all branch references
        for branch in self.local_repo.heads:
            print("Pushing branch %s" % branch.name)
            self.do_push_with_retries(ref=branch.name)

        # push all tags
        print("Pushing tags")
        self.do_push_with_retries(push_tags=True)

        self.get_remote_migration_tags()
        self.clean_up(clean_up_remote=True)

        print("Migration to CodeCommit was successful")

    def migrate_commit(self, commit):

        """
        Executes a single commit.
        :param commit (string): The name of the commit we are dealing with.
        """
        
        if commit in self.walked_commits:
            return []

        pending_ancestor_pushes = []
        commit_count = 1

        if len(commit.parents) > 1:
            # This is a merge commit
            # Ensure that all parents are pushed first
            for parent_commit in commit.parents:
                pending_ancestor_pushes.extend(self.migrate_commit(parent_commit))
        elif len(commit.parents) == 1:
            # Split linear history into individual pushes
            next_ancestor, commits_to_next_ancestor = self.find_next_ancestor_for_push(commit.parents[0])
            commit_count += commits_to_next_ancestor
            pending_ancestor_pushes.extend(self.migrate_commit(next_ancestor))

        self.walked_commits.add(commit)

        return self.stage_push(commit, commit_count, pending_ancestor_pushes)

    def find_next_ancestor_for_push(self, commit):
        """
        Traverse the commit history until we find the initial commit or merge commit.
        Return the merge commit and the number of other commits in between.
        :param commit (string): The name of the commit we are dealing with.
        """

        commit_count = 0

        # Traverse linear history until we reach our commit limit, a merge commit, or an initial commit
        while len(commit.parents) == 1 and commit_count < self.max_commits_per_push and commit not in self.walked_commits:
            commit_count += 1
            self.walked_commits.add(commit)
            commit = commit.parents[0]

        return commit, commit_count

    def stage_push(self, commit, commit_count, pending_ancestor_pushes):
        # Determine whether we can roll up pending ancestor pushes into this push
        combined_commit_count = commit_count + sum(ancestor_commit_count for (ancestor, ancestor_commit_count) in pending_ancestor_pushes)

        if combined_commit_count < self.max_commits_per_push:
            # don't push anything, roll up all pending ancestor pushes into this pending push
            return [(commit, combined_commit_count)]

        if combined_commit_count <= (self.max_commits_per_push + self.max_commits_tolerance):
            # roll up everything into this commit and push
            self.tag_commits([commit])
            return []

        if commit_count >= self.max_commits_per_push:
            # need to push each pending ancestor and this commit
            self.tag_commits([ancestor for (ancestor, ancestor_commit_count) in pending_ancestor_pushes])
            self.tag_commits([commit])
            return []

        # push each pending ancestor, but roll up this commit
        self.tag_commits([ancestor for (ancestor, ancestor_commit_count) in pending_ancestor_pushes])
        return [(commit, commit_count)]

    def tag_commits(self, commits):
        for commit in commits:
            self.next_tag_number += 1
            tag_name = self.MIGRATION_TAG_PREFIX + str(self.next_tag_number)

            if tag_name not in self.remote_migration_tags:
                tag = self.local_repo.create_tag(tag_name, ref=commit)
                self.migration_tags.append(tag)
            elif self.remote_migration_tags[tag_name] != str(commit):
                print("Migration tags on the remote do not match the local tags. Most likely your batch size has changed since the last time you ran this script. Please run this script with the --clean option, and try again.")
                sys.exit(1)

    def push_migration_tags(self):
        print("Will attempt to push %d tags" % len(self.migration_tags))
        self.migration_tags.sort(key=lambda tag: int(tag.name.replace(self.MIGRATION_TAG_PREFIX, "")))
        for tag in self.migration_tags:
            print("Pushing tag %s (out of %d tags), commit %s" % (tag.name, self.next_tag_number, str(tag.commit)))
            self.do_push_with_retries(ref=tag.name)

    def do_push_with_retries(self, ref=None, push_tags=False):
        for i in range(0, self.PUSH_RETRY_LIMIT):
            if i == 0:
                progress_printer = PushProgressPrinter()
            else:
                progress_printer = None

            try:
                if push_tags:
                    infos = self.remote_repo.push(tags=True, progress=progress_printer)
                elif ref is not None:
                    infos = self.remote_repo.push(refspec=ref, progress=progress_printer)
                else:
                    infos = self.remote_repo.push(progress=progress_printer)

                success = True
                if len(infos) == 0:
                    success = False
                else:
                    for info in infos:
                        if info.flags & info.UP_TO_DATE or info.flags & info.NEW_TAG or info.flags & info.NEW_HEAD:
                            continue
                        success = False
                        print(info.summary)

                if success:
                    return
            except GitCommandError as err:
                print(err)

        if push_tags:
            print("Pushing all tags failed after %d attempts" % (self.PUSH_RETRY_LIMIT))
        elif ref is not None:
            print("Pushing %s failed after %d attempts" % (ref, self.PUSH_RETRY_LIMIT))
            print("For more information about the cause of this error, run the following command from the local repo: 'git push %s %s'" % (self.remote_name, ref))
        else:
            print("Pushing all branches failed after %d attempts" % (self.PUSH_RETRY_LIMIT))
        sys.exit(1)

    def get_remote_migration_tags(self):
        remote_tags_output = self.local_repo.git.ls_remote(self.remote_name, tags=True).split('\n')
        self.remote_migration_tags = dict((tag.split()[1].replace("refs/tags/",""), tag.split()[0]) for tag in remote_tags_output if self.MIGRATION_TAG_PREFIX in tag)

    def clean_up(self, clean_up_remote=False):
        tags = [tag for tag in self.local_repo.tags if tag.name.startswith(self.MIGRATION_TAG_PREFIX)]

        # delete the local tags
        TagReference.delete(self.local_repo, *tags)

        # delete the remote tags
        if clean_up_remote:
            tags_to_delete = [":" + tag_name for tag_name in self.remote_migration_tags]
            self.remote_repo.push(refspec=tags_to_delete)

parser = OptionParser()
parser.add_option("-l", "--local",
                  action="store", dest="localrepo", default=os.getcwd(),
                  help="The path to the local repo. If this option is not specified, the script will attempt to use current directory by default. If it is not a local git repo, the script will fail.")
parser.add_option("-r", "--remote",
                  action="store", dest="remoterepo", default="codecommit",
                  help="The name of the remote repository to be used as the push or migration destination. The remote must already be set in the local repo ('git remote add ...'). If this option is not specified, the script will use 'codecommit' by default.")
parser.add_option("-b", "--batch",
                  action="store", dest="batchsize", default="1000",
                  help="Specifies the commit batch size for pushes. If not explicitly set, the default is 1,000 commits.")
parser.add_option("-c", "--clean",
                  action="store_true", dest="clean", default=False,
                  help="Remove the temporary tags created by migration from both the local repo and the remote repository. This option will not do any migration work, just cleanup. Cleanup is done automatically at the end of a successful migration, but not after a failure so that when you re-run the script, the tags from the prior run can be used to identify commit batches that were not pushed successfully.")

(options, args) = parser.parse_args()

migration = RepositoryMigration()
migration.migrate_repository_in_parts(options.localrepo, options.remoterepo, int(options.batchsize), options.clean)

# snippet-end:[codecommit.python.push-in-parts.complete]
