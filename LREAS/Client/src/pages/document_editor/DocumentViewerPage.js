import React, { useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import { URL } from "../../utils/websocket";
import { getDocumentViewer } from "../../api/documentApi";
import "./styles/DocumentViewerPage.css";
import NotFoundPage from "../NotFoundPage";

export default function DocumentViewerPage() {
  const viewerRef = useRef(null);
  const socketRef = useRef(null); // useRef for socket
  const { docId } = useParams();
  const [loading, setLoading] = useState(true);
  const [isNotFound, setIsNotFound] = useState(false);
  const [fileContent, setFileContent] = useState("");

  useEffect(() => {
    async function fetchDocument() {
      try {
        const data = await getDocumentViewer(docId);
        if (!data?.content) {
          setIsNotFound(true);
          return;
        }
        setFileContent(data.content); // Correct: let React update first
      } catch (error) {
        console.error("❌ Error fetching document:", error);
        setIsNotFound(true);
      } finally {
        setLoading(false);
      }
    }

    if (docId) {
      fetchDocument();
    } else {
      setIsNotFound(true);
    }
  }, [docId]);

  useEffect(() => {
    if (viewerRef.current && fileContent) {
      updateViewer(fileContent);
    }
  }, [fileContent]); // Correct: react to fileContent changes

  useEffect(() => {
    const socket = new WebSocket(URL);
    socketRef.current = socket;

    socket.onopen = () => {
      console.log("✅ Connected to WebSocket server", socket);
    };

    socket.onmessage = (event) => {
      try {
        const { docId: updatedDocId, content } = JSON.parse(event.data);
        if (updatedDocId === docId) {
          console.log("🔄 Updating document in viewer...");
          updateViewer(content);
        }
      } catch (error) {
        console.error("❌ Error parsing WebSocket message:", error);
      }
    };

    socket.onerror = (error) => {
      console.error("❌ WebSocket Error:", error);
    };

    socket.onclose = () => {
      console.log("ℹ️ WebSocket connection closed");
    };

    return () => {
      if (socketRef.current) {
        socketRef.current.close();
      }
    };
  }, [docId]); // recreate socket if docId changes

  function updateViewer(content) {
    if (viewerRef.current) {
      viewerRef.current.innerHTML = ""; // Clear old content
      viewerRef.current.insertAdjacentHTML("beforeend", content); // Insert new content
    }
  }

  if (loading) return <p>Loading document...</p>;
  if (isNotFound) return <NotFoundPage />;

  return (
    <div className="viewer-container">
      <h2>Document Viewer</h2>
      <div ref={viewerRef} className="viewer"></div>
    </div>
  );
}
