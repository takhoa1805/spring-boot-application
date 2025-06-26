import React from "react";
import { Header } from "../components/Header";
import { 
    StudentImg, ManageContentImg,
    AIGenerateImg, ForumDiscussion
 } from "../images/Icon";
import './HomePage.css';

export default function HomePage() {
    const Card = ({ image, title, description }) => (
        <div className="card">
          <img src={image} alt={title} className="cardImage" />
          <h3 className="cardTitle">{title}</h3>
          <p className="cardDescription">{description}</p>
        </div>
    );

    return (
        <>
            <Header/>
            <div className="home-container">
                <div className="top-banner">
                    <div className="textContainer">
                        <h1 className="title">Generate content automatically using AI</h1>
                        <p className="subtitle">
                            A platform enhances your learning progress by providing learning resources and a space to manage contents
                        </p>
                        <button>See more →</button>
                    </div>
                    <div className="imageContainer">
                        <img src={StudentImg} alt="Illustration"/>
                    </div>
                </div>
                <div className="cardContainer">
                    <Card
                        image={ManageContentImg}
                        title="Manage content"
                        description="Manage your content easily right inside your workspace"
                    />
                    <Card
                        image={AIGenerateImg}
                        title="AI Generate"
                        description="Generate content automatically using AI"
                    />
                    <Card
                        image={ForumDiscussion}
                        title="Forum discussion"
                        description="Discuss your problem with others"
                    />
                </div>
            </div>
        </>
    )
}