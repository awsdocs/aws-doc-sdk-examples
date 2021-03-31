module.exports = {
    entry: ['./mylambdafunction.ts'],
    target: 'node',
    output: {
        path: `${process.cwd()}/bin`,
        filename: 'index.js',
        libraryTarget: 'umd'
    }
};
