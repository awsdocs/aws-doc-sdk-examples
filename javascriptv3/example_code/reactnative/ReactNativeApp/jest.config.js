module.exports = {
  preset: 'react-native',
  moduleNameMapper: {
    uuid: require.resolve('uuid'),
    '@aws-sdk/util-stream': require.resolve(
      '@aws-sdk/util-stream-node/dist-cjs',
    ),
  },
};
