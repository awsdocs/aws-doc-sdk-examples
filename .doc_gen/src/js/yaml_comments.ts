const YAML_COMMENT_PREFIX = '___YAML_COMMENT_';
const YamlCommentRegex = /( *)___YAML_COMMENT_\d+: *([^ |].*)/g;
const MultilineYamlCommentRegex = /( *)___YAML_COMMENT_\d+: *\|-\n((\1 +)[^ ].*(?:\n\3[^ ].+)*)/g;

let yamlCommentIndex = 0;

/**
 * Creates a key to be used as a YAML comment
 */
export function yamlComment() {
    return YAML_COMMENT_PREFIX + yamlCommentIndex++;
}

/**
 * Replaces YAML comment keys created with {@link yamlComment} with proper YAML comment syntax
 */
export function processYamlComments(baseYaml: string) {
    return baseYaml
        .replace(YamlCommentRegex, (_match, leadingSpace: string, comment: string) => leadingSpace + '# ' + comment.trim())
        .replace(
            MultilineYamlCommentRegex,
            (_match, leadingSpace: string, comment: string) => comment
                .split('\n')
                .map(x => `${leadingSpace}# ${x.trim()}`)
                .join('\n')
        );
}