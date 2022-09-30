# Developer guide

Elwing is a bootstrapped companion app to the Cloudscape design system. Its opinionated
layout accelerates development time by exposing a simple plugin API allowing a developer
to focus on defining pages and content rather than app structure.

## Prerequisites

1. Install NodeJS 18

## Run

1. Run `npm i` from the same directory as this readme.
1. Run `npm start`.

### What is a plugin?

A plugin is just a JavaScript Object containing the following properties:

- `navigationItem` - A Cloudscape `SideNavigationProps` object.
- `component` - The component that will display when the link in the sidebar is clicked.

## Add a plugin

### ⚠️ Important

The automated steps will overwrite the `manifest.ts` file with the latest
plugins from the `plugins` directory.

### Add a plugin automatically

A convenience script has been added to support the quick creation of a plugin.

1. Run `npm run create-plugin <plugin-name>`.
1. Find your plugin in the `plugins` directory.
1. Modify the generated code to match your use case.

### Add a plugin manually

If you want to create a plugin manually instead of using the script, you can do so.

1. Create a new folder in the plugins directory. For example, `my-plugin`.
2. Add an `index.ts` file to the folder and populate it using the below as an example.

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
