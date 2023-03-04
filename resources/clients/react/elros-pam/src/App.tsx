import AppLayout from "@cloudscape-design/components/app-layout";

import "./App.css";
import TagsLayout from "./TagsLayout";

function App() {
  return (
    <AppLayout
      toolsHide={true}
      navigationHide={true}
      contentType="cards"
      content={<TagsLayout />}
    ></AppLayout>
  );
}

export default App;
