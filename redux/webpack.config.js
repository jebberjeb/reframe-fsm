const path = require('path');
const webpack = require('webpack');
module.exports = {
  context: path.resolve(__dirname, './src'),
  entry: {
    app: './app.js',
  },
  output: {
    path: path.resolve(__dirname, './dist'),
    filename: '[name].bundle.js',
    publicPath: '/assets',
  },
  devServer: {
    contentBase: path.resolve(__dirname, './src'),
  },
  module: {
      loaders: [
      {
          test : /\.jsx?/,
          //include : APP_DIR,
          loader : 'babel-loader'
      }
      ]
  }
};
