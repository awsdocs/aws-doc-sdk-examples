When you're doing this for JS in the browser:

/**
 * Don't hard-code your credentials!
 * Export the following environment variables instead:
 *
 * export AWS_ACCESS_KEY_ID='AKID'
 * export AWS_SECRET_ACCESS_KEY='SECRET'
 */

Might make it easier for testing purposes, vs. doing a deep dive into Cognito foo.
