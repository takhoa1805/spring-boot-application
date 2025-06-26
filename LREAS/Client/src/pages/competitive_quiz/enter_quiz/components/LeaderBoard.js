import React, { useEffect, useState } from 'react';
import '../styles/LeaderBoard.css'; // Create LeaderBoard.css for styling
import { Button } from '@mui/material';
import { sendMessage } from "../../../../utils/socket";


const LeaderBoard = ({ currentQuestion, socket, sessionCode, leaderBoard }) => {
  const [sortedLeaderboard, setSortedLeaderboard] = useState([]);


  const handleNextQuestionClick = () => {
      // Check if sessionCode and socket are available
      if (!sessionCode) {
          console.error("Session code is missing, cannot emit 'next' event.");
          return;
      }
      if (!socket) {
          console.error("Socket instance is missing, cannot emit 'next' event.");
          return;
      }

      console.log(`Emitting 'next' event for session: ${sessionCode}`);

      // Use the sendMessage utility (consistent with QuizHall)
      sendMessage({
          message: { sessionCode: sessionCode }, // Send sessionCode in the message payload
          event: "next",                       // The event name to emit
          socket: socket                       // The socket instance to use
      });

      // Optional: Add feedback like disabling the button after click
      // e.g., set a state variable like 'isLoadingNext' to true
  };
  


  useEffect(() => {
    if (leaderBoard.length>0) {
      const sorted = [...leaderBoard].sort((a, b) => b.score - a.score);
      setSortedLeaderboard(sorted);
    }
  }, [leaderBoard]);





  

  return (
    <div className="leaderboard-container">
      <div className="leaderboard-header">
        <h2>Leaderboard</h2>
        {currentQuestion && <p>Question: {currentQuestion}</p>}
        {sessionCode && <p>Session: {sessionCode}</p>}
      </div>
      <table className="leaderboard-table">
        <thead>
          <tr>
            <th>Rank</th>
            <th>Player</th> {/* Changed to display Alias */}
            <th>Score</th>
          </tr>
        </thead>
        <tbody>
          {sortedLeaderboard.map((player, index) => (
            <tr key={player.player._id}> {/* Use player._id as key */}
              <td>{index + 1}</td>
              <td>{player.player.alias}</td> {/* Display player.alias */}
              <td>{player.score}</td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* Next Question Button */}
      <div className="next-question-action"> {/* Wrapper div for positioning/styling */}
          <button
              className="next-question-button"   // Class for styling
              onClick={handleNextQuestionClick} // Attach the handler
          >
              Next 
          </button>
      </div>



    </div>
    
  );
};

export default LeaderBoard;