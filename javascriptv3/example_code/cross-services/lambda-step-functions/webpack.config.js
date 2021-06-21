module.exports = {
    entry: ['./mylambdafunction.js'],
    target: 'node',
    output: {
        path: `${process.cwd()}/bin`,
        filename: 'index.js',
        libraryTarget: 'umd'
    }
};
