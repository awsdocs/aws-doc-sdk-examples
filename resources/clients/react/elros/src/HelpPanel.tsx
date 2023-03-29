import HelpPanel from "@cloudscape-design/components/help-panel";
import Icon from "@cloudscape-design/components/icon";

export default () => {
  return (
    <HelpPanel
      footer={
        <div>
          <h3>
            Learn more <Icon name="external" />
          </h3>
          <ul>
            <li>
              <a href="">Link to documentation</a>
            </li>
            <li>
              <a href="">Link to documentation</a>
            </li>
          </ul>
        </div>
      }
      header={<h2>Help panel title (h2)</h2>}
    >
      <div>
        <p>
          This is a paragraph with some <b>bold text</b> and also some{" "}
          <i>italic text</i>.
        </p>

        <h3>h3 section header</h3>
        <ul>
          <li>Unordered list item.</li>
          <li>Unordered list item.</li>
        </ul>

        <h4>h4 section header</h4>
        <p>
          Code can be formatted as lines of code or blocks of code. Add inline
          code <code>like this</code> using a <code>{"<code>"}</code> tag.
          <pre>
            Or format blocks of code (like this) using a <code>{"<pre>"}</code>{" "}
            tag.
          </pre>
        </p>

        <h5>h5 section header</h5>
        <dl>
          <dt>This is a term</dt>
          <dd>This is its description.</dd>
          <dt>This is a term</dt>
          <dd>This is its description</dd>
        </dl>
      </div>
    </HelpPanel>
  );
};
