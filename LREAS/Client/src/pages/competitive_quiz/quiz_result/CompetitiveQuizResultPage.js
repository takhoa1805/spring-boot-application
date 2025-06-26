import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './styles/CompetitiveQuizResultPage.css'; // Add CSS styles similar to the Kahoot podium


const CompetitiveQuizResultPage = () => {
  const { sessionMongoId } = useParams();
  const navigate = useNavigate();
  const [topPlayers, setTopPlayers] = useState([]);

  useEffect(() => {
    if (!sessionMongoId) {
      navigate('/notfound');
      return;
    }

    axios.get(`/api/competitive-quiz/sessions/result/${sessionMongoId}`)
      .then((response) => {
        const { leaderBoard } = response.data;

        // Sort by score descending and get top 3
        const sorted = leaderBoard
          .sort((a, b) => b.score - a.score)
          .slice(0, 3);

        setTopPlayers(sorted);
      })
      .catch((error) => {
        console.error(error);
        navigate('/notfound');
      });
  }, [sessionMongoId, navigate]);

  const podiumOrder = ['second', 'first', 'third']; // for display order

  return (
    <div className="quiz-result-container">
      <h1 className="title">Top scorers!</h1>
      <div className="podium">
        {topPlayers.map((entry, index) => (
          <div key={index} className={`podium-position ${podiumOrder[index]}`}>
            <div className="podium-name">{entry.player?.alias || 'Anonymous'}</div>
            <div className="podium-score">{entry.score} points</div>
          </div>
        ))}
      </div>
      <div className="actions">
        <button onClick={() => navigate('/feedback')} className="btn">
          Feedback & results
        </button>
        <button onClick={() => navigate('/content')} className="btn next-btn">
          Back to home
        </button>
      </div>
    </div>
  );
};

export default CompetitiveQuizResultPage;
