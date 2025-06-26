import React, { useEffect, useState } from 'react';
import '../styles/LeaderBoard.css'; // Create LeaderBoard.css for styling

const LeaderBoard = ({ currentQuestion, socket, sessionCode, leaderBoard }) => {
  const [sortedLeaderboard, setSortedLeaderboard] = useState([]);

  useEffect(() => {
    if (leaderBoard.length > 0) {
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
    </div>
  );
};

export default LeaderBoard;