import { toggleMark, setBlockType, wrapIn } from "prosemirror-commands";
import { schema } from "prosemirror-schema-basic";

// Bold text
export function bold(view) {
  toggleMark(schema.marks.strong)(view.state, view.dispatch);
}

// Italic text
export function italic(view) {
  toggleMark(schema.marks.em)(view.state, view.dispatch);
}

// Underline (not in basic schema)
export function underline(view) {
  alert("ProseMirror does not support underline by default. Consider extending the schema.");
}

// Toggle headings
export function toggleHeading(view, level) {
  setBlockType(schema.nodes.heading, { level })(view.state, view.dispatch);
}

// Bullet list
export function toggleBulletList(view) {
  wrapIn(schema.nodes.bullet_list)(view.state, view.dispatch);
}

// Ordered list
export function toggleOrderedList(view) {
  wrapIn(schema.nodes.ordered_list)(view.state, view.dispatch);
}
