import React, { useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import { EditorState } from "prosemirror-state";
import { EditorView } from "prosemirror-view";
import { Schema, DOMParser } from "prosemirror-model";
import { schema } from "prosemirror-schema-basic";
import { addListNodes } from "prosemirror-schema-list";
import { exampleSetup } from "prosemirror-example-setup";
import {URL} from "../../utils/websocket";
import { getDocumentEditor, getDocumentViewer, saveDocument } from "../../api/documentApi";
import brandLogo from '../../images/brand_icon.png';
import { TextField, Button, Card, CardContent,CardHeader, CardActions, Typography, Box } from "@mui/material";
import "prosemirror-view/style/prosemirror.css";
import "./styles/DocumentEditorPage.css";
import NotFoundPage from "../NotFoundPage";
import { getToken,parseJwt } from "../../utils";
import PersonIcon from '@mui/icons-material/Person';
import GroupIcon from '@mui/icons-material/Group';
import GroupAddIcon from '@mui/icons-material/GroupAdd';
import Tooltip from "@mui/material/Tooltip";
import { styled } from "@mui/material/styles";
import Badge from "@mui/material/Badge";

import { history, undo, redo } from "prosemirror-history";
import { keymap } from "prosemirror-keymap";
import { schema as basicSchema } from "prosemirror-schema-basic";
import { inputRules, wrappingInputRule, textblockTypeInputRule, smartQuotes, emDash, ellipsis } from "prosemirror-inputrules";

import { baseKeymap, toggleMark, setBlockType, wrapIn, chainCommands, exitCode } from "prosemirror-commands";
import { menuBar, MenuItem, Dropdown, MenuElement } from "prosemirror-menu";
import { wrapInList } from "prosemirror-schema-list";


const OnlineBadge = styled(Badge)(({ theme }) => ({
  "& .MuiBadge-badge": {
    backgroundColor: "#4CAF50",
    color: "#4CAF50",
    boxShadow: `0 0 0 2px ${theme.palette.background.paper}`,
    // adjust these to move the dot
    right: 4,
    top: 4,
    height: "8px",
    minWidth: "8px",
    borderRadius: "4px",
  },
}));

// Extend schema with list support
const mySchema = new Schema({
  nodes: addListNodes(schema.spec.nodes, "paragraph block*", "block"),
  marks: schema.spec.marks,
});

// A small set of input rules (auto-convert “- ” into a bullet list, quotes, etc.)
function buildInputRules(schema) {
  let rules = smartQuotes.concat(ellipsis, emDash);
  let { paragraph, blockquote, ordered_list, bullet_list } = schema.nodes;
  rules.push(wrappingInputRule(/^\s*([-+*])\s$/, bullet_list));
  rules.push(wrappingInputRule(/^(\d+)\.\s$/, ordered_list, match => ({ order: +match[1] })));
  rules.push(textblockTypeInputRule(/^>\s$/, blockquote));
  return inputRules({ rules });
}

// Extend marks
const myMarks = basicSchema.spec.marks.append({
  underline: {
    parseDOM: [{ tag: "u" }, { style: "text-decoration=underline" }],
    toDOM() { return ["u", 0]; }
  },
  link: {
    attrs: { href: {} },
    inclusive: false,
    parseDOM: [
      {
        tag: "a[href]",
        getAttrs(dom) {
          return { href: dom.getAttribute("href") };
        },
      },
    ],
    toDOM(node) {
      return ["a", { ...node.attrs, rel: "noopener noreferrer", target: "_blank" }, 0];
    },
  },
});

// Extend nodes
const myNodes = addListNodes(basicSchema.spec.nodes, "paragraph block*", "block").update("paragraph", {
  ...basicSchema.spec.nodes.get("paragraph"),
  attrs: { align: { default: "left" } },
  parseDOM: [{
    tag: "p",
    getAttrs(dom) {
      return { align: dom.style.textAlign || "left" };
    },
  }],
  toDOM(node) {
    return ["p", { style: `text-align: ${node.attrs.align}` }, 0];
  },
});



// Create command to set alignment
const setTextAlign = (align) => (state, dispatch) => {
  let { $from, $to } = state.selection;
  let tr = state.tr;
  state.doc.nodesBetween($from.pos, $to.pos, (node, pos) => {
    if (node.type.name === "paragraph") {
      tr.setNodeMarkup(pos, undefined, { ...node.attrs, align });
    }
  });
  if (tr.docChanged && dispatch) {
    dispatch(tr);
    return true;
  }
  return false;
};

// Command to create link
const insertLink = () => {
  return function(state, dispatch) {
    const href = window.prompt("Enter the URL");
    if (!href) return false;
    const { from, to } = state.selection;
    if (dispatch) {
      dispatch(state.tr.addMark(from, to, state.schema.marks.link.create({ href })));
    }
    return true;
  };
};

// Text size options
const headingOptions = [
  new MenuItem({
    title: "Normal",
    label: "P",
    run: setBlockType(mySchema.nodes.paragraph),
  }),
  new MenuItem({
    title: "Heading 1",
    label: "H1",
    run: setBlockType(mySchema.nodes.heading, { level: 1 }),
  }),
  new MenuItem({
    title: "Heading 2",
    label: "H2",
    run: setBlockType(mySchema.nodes.heading, { level: 2 }),
  }),
];

// Align menu
const alignMenu = [
  new MenuItem({ label: "Left", run: setTextAlign("left") }),
  new MenuItem({ label: "Center", run: setTextAlign("center") }),
  new MenuItem({ label: "Right", run: setTextAlign("right") }),
  new MenuItem({ label: "Justify", run: setTextAlign("justify") }),
];

// Build menu
const buildMenu = (schema) => [
  [
    new MenuItem({ title: "Bold", label: "Bold", run: toggleMark(schema.marks.strong) }),
    new MenuItem({ title: "Italic", label: "Italic", run: toggleMark(schema.marks.em) }),
    new MenuItem({ title: "Underline", label: "Underline", run: toggleMark(schema.marks.underline) }),
    new MenuItem({ title: "Link", label: "Link", run: insertLink() }),
  ],
  headingOptions,
  alignMenu,
  [
    new MenuItem({
      title: "Bullet List",
      label: "• List",
      run: wrapInList(schema.nodes.bullet_list),
    }),
    new MenuItem({
      title: "Undo",
      label: "Undo",
      run: undo,
    }),
    new MenuItem({
      title: "Redo",
      label: "Redo",
      run: redo,
    }),
  ],
];

export const basicPlugins = [
  history(),
  keymap({
    "Mod-z": undo,
    "Mod-y": redo,
    "Mod-b": toggleMark(mySchema.marks.strong),
    "Mod-i": toggleMark(mySchema.marks.em),
    "Mod-u": toggleMark(mySchema.marks.underline),
  }),
  keymap(baseKeymap),
  menuBar({ floating: true, content: buildMenu(mySchema) }),
];


export default function DocumentEditorPage() {
  const socketRef = useRef(null); // store socket safely
  const editorRef = useRef(null);
  const viewRef = useRef(null);
  const { docId } = useParams();
  const [isNotFound, setIsNotFound] = useState(false);
  const JWT_loginToken = getToken();
  const userInfo = parseJwt(JWT_loginToken);
  const [currentEditors, setCurrentEditors] = useState([]);


  useEffect(() => {
    // Create socket and connect
    const socket = new WebSocket(URL);
    socketRef.current = socket;

    socket.onopen = () => {
      console.log("✅ Connected to WebSocket server", socket);
    };

    socket.onmessage = (event) => {
      console.log("🔄 Received update:", event.data);
      setCurrentEditors(JSON.parse(event.data).currentEditors);
      const { content } = JSON.parse(event.data);


      if (!JSON.parse(event.data).updateEditors){
        if (viewRef.current) {
          const docElement = document.createElement("div");
          docElement.innerHTML = content;
          const newState = EditorState.create({
            doc: DOMParser.fromSchema(mySchema).parse(docElement),
            // plugins: exampleSetup({ schema: mySchema }),
            plugins: basicPlugins,

          });
          viewRef.current.updateState(newState);
        }
      }
  
    };

    socket.onerror = (error) => {
      // socketRef.current.send(JSON.stringify({ docId, content: updatedContent,username: userInfo.username }));
      console.error("❌ WebSocket Error:", error);
    };

    return () => {
      socket.close(1000,userInfo.username);
      // socketRef.current.send(JSON.stringify({ docId, content: updatedContent,username: userInfo.username }));

      if (viewRef.current) viewRef.current.destroy();
    };
  }, []);

  useEffect(() => {
    async function initializeEditor() {
      if (!editorRef.current) return;

      try {
        const data = await getDocumentEditor(docId);
        const content = data?.content?.trim() || "<h2>Hello ProseMirror</h2><p>Start editing...</p>";
        setCurrentEditors(data?.currentEditors || []);
        console.log("Current Editors: ", data?.currentEditors || []);

        if (!data?.content) {
          setIsNotFound(true);
          return;
        }

        const docElement = document.createElement("div");
        docElement.innerHTML = content;
        const doc = DOMParser.fromSchema(mySchema).parse(docElement);

        const state = EditorState.create({
          doc,
          // plugins: exampleSetup({ schema: mySchema }),
          plugins: basicPlugins,

        });

        viewRef.current = new EditorView(editorRef.current, {
          state,
          dispatchTransaction(transaction) {
            const newState = viewRef.current.state.apply(transaction);
            viewRef.current.updateState(newState);

            const updatedContent = viewRef.current.dom.innerHTML;

            // Safe send to WebSocket
            if (socketRef.current && socketRef.current.readyState === WebSocket.OPEN) {
              socketRef.current.send(JSON.stringify({ docId, content: updatedContent,username: userInfo.username }));
            } else {
              console.warn("WebSocket is not open. Skipped sending.");
            }

            saveDocument(docId, updatedContent).then((data) => {
              setCurrentEditors(data?.currentEditors || []);
            });
          },
        });

      } catch (error) {
        console.error(error);
        setIsNotFound(true);
      }
    }

    initializeEditor();
  }, [docId]);

  return (
    isNotFound ? (
      <NotFoundPage />
    ) : (
      <div className="editor-container">
        <Box
          className="header-container"
          sx={{
            position: "fixed",
            top: 0,
            left: 0,
            right: 0,
            backgroundColor: "#EAF1FB",
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            zIndex: 1000,
            p: 2,
            mb: 10,
          }}
        >
          <img src={brandLogo} alt="Brand Logo" className="brand-logo" />
          <Typography variant="h4" align="center" className="brand-text" gutterBottom>
            LREAS
          </Typography>

          {currentEditors.length > 0 && (
            <Tooltip title={currentEditors.join(", ")} arrow>
              <OnlineBadge
                overlap="circular"
                variant="dot"
                anchorOrigin={{ vertical: "top", horizontal: "right" }}
              >
                {currentEditors.length === 1 ? (
                  <PersonIcon sx={{ fontSize: 30, color: "#3F51B5" , cursor: "pointer"}} />
                ) : currentEditors.length === 2 ? (
                  <GroupIcon sx={{ fontSize: 30, color: "#3F51B5" , cursor: "pointer"}} />
                ) : (
                  <GroupAddIcon sx={{ fontSize: 30, color: "#3F51B5" , cursor: "pointer"}} />
                )}
              </OnlineBadge>
            </Tooltip>
          )}
        </Box>

        <div ref={editorRef} className="editor"  style={{ marginBottom: "10px" }}></div>
      </div>
    )
  );
}
