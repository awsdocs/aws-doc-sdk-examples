# Developer guide

Elwing is a bootstrapped companion app to the Cloudscape design system. Its opinionated
layout accelerates development time by exposing a simple plugin API. Developers can use
the plugin API to focus on defining pages and content instead of app structure.

## Prerequisites

1. Install NodeJS 18

## Run

1. Run `npm i` from the same directory as this readme.
1. Run `npm start`.

### What is a plugin?

A plugin is a JavaScript Object that contains the following properties:

- `navigationItem` - A Cloudscape `SideNavigationProps` object.
- `component` - The component that displays when choosing the link in the sidebar.

## Add a plugin

### ⚠️ Important

The automated steps overwrite the `manifest.ts` file with the latest
plugins from the `plugins` directory.

### Add a plugin automatically

A convenience script is included to support quick plugin creation.

1. Run `npm run create-plugin <plugin-name>`.
1. Find your plugin in the `plugins` directory.
1. Modify the generated code to match your use case.

### Add a plugin manually

You can choose to create a plugin manually instead of using the script.

1. Create a new folder in the plugins directory. For example, `my-plugin`.
2. Add an `index.ts` file to the folder. Refer to the following example to populate the file.

```javascript
const MyPlugin = {
  navigationItem: {
    text: "My Plugin",
    href: "/my_plugin",
    type: "expandable-link-group",
    items: [{
      type: "link",
      text: "Cloudscape Design System",
      href: "https://cloudscape.design/",
      external: true
    }],
  } as SideNavigationProps.ExpandableLinkGroup,
    component: lazy(() => import("./src/MyPluginComponent")),
  };

export { MyPlugin };
```

3. Add a `MyPlugin.tsx` file to the folder and populate it with component code.

```javascript
const Component = () => <h1>Hello, my-component!</h1>;
export default Component;
```

4. Open `plugins/manifest.ts` and add your plugin to the `plugins` array.

```javascript
import { AppPlugin } from "./AppPlugin";
import { MyPlugin } from "./my-plugin";
const plugins: AppPlugin[] = [MyPlugin];
export default plugins;
```

## Troubleshoot

- Static files like the README are not updating in the client.
  - This is likely a caching problem. Run `rm -rf node_modules/.cache`.
