import React,{useState,useEffect} from "react";
import  {useNavigate} from "react-router-dom";
import { TextField, Button, Card, CardContent,CardHeader, CardActions, Typography, Box,Container } from "@mui/material";
import { useForm } from "react-hook-form";
import Alert from '@mui/material/Alert';
import GoogleIcon from "@mui/icons-material/Google";
import FacebookIcon from "@mui/icons-material/Facebook";
import brandLogo from '../../images/brand_icon.png';
import "./styles/SignUpPage.css"; // Import the CSS file

import Form from "./components/Form";



export default function SignUpPage() {

  const [signUpStatus, setSignUpStatus] = useState({
    status: false,
    message: "Unexpected error during signing up process"
  });

  const navigate = useNavigate();
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm();

  
  return (
    <Box className="login-container" sx={{ display: "flex", flexDirection: "column", padding:"2em"}}>
      <Box className="header-container" sx={{ display: "flex", flexDirection: "row", alignItems: "left", justifyContent: "flex-start",mr:"2em"}}>
        <img src={brandLogo} alt="Brand Logo" className="brand-logo" />
        <Typography variant="h4" className="brand-text" gutterBottom>
          LREAS
        </Typography>
      </Box>
      <Card className="card-container" sx={{mt:'1em'}}>
        <CardContent className="card-content">
        <Typography variant="h5" align="center"  className="sign-in-text" sx={{mb:'1em'}}>
          Sign Up
        </Typography>
        {signUpStatus.status == "error" && 
          <Alert severity="error">{signUpStatus.message}</Alert>
        }
         <Form setSignUpStatus={setSignUpStatus}/>
        </CardContent>
   
      </Card>
    </Box>
  );
}
