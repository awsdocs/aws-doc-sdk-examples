import path from "node:path";
import { fileURLToPath } from "node:url";
import HtmlWebpackPlugin from "html-webpack-plugin";

const dirname = path.dirname(fileURLToPath(import.meta.url));

const ASSET_PATH = process.env.ASSET_PATH || "/";

export default {
  mode: "development",
  entry: ["./src/index.js"],
  context: dirname,
  plugins: [
    new HtmlWebpackPlugin({
      template: path.resolve(dirname, "static", "index.html"),
    }),
  ],
  output: {
    filename: "main.js",
    path: path.resolve(dirname, "dist"),
    publicPath: ASSET_PATH,
  },
  devtool: "source-map",
  devServer: {
    port: 3000,
    compress: false,
    static: false,
  },
};
