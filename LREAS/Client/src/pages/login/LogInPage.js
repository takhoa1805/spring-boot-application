import React,{useState,useEffect} from "react";
import  {useNavigate} from "react-router-dom";
import { TextField, Button, Card, CardContent,CardHeader, CardActions, Typography, Box } from "@mui/material";
import { useForm } from "react-hook-form";
import Alert from '@mui/material/Alert';
import GoogleIcon from "@mui/icons-material/Google";
import FacebookIcon from "@mui/icons-material/Facebook";
import brandLogo from '../../images/brand_icon.png';
import { setToken } from "../../utils";
import "./styles/LoginPage.css"; // Import the CSS file
import {sendInfoLogin} from "../../api";


export default function LoginPage() {

  const [isLoginFailed, setIsLoginFailed] = useState(false);

  const navigate = useNavigate();
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm();

  const onSubmit = async (data) => {
    // mockupLoginInfo:
    //  {
    //   email:"takhoa",
    //   password:"123"
    // }


    try{
      // Mockup calling login api:
      const response = await sendInfoLogin(data);


      if (response.status === 200) {
        navigate('/content');
        setToken(response?.data?.token);
        window.location.reload();
      } else {
        // Popup alert alternatives
        setIsLoginFailed(true);
      }
  
  
      }catch(e){
        setIsLoginFailed(true);
      }
  };

  const createAccount = () => {
    navigate("/signup");
  }

  
  return (
    <Box className="login-container">
      <Card className="card-container">
        <Box className="header-container">
          <img src={brandLogo} alt="Brand Logo" className="brand-logo" />
          <Typography variant="h4" align="center" className="brand-text"gutterBottom>
            LREAS
          </Typography>
        </Box> {/* Fixed incorrect closing tag */}

        <CardContent className="card-content">
        <Typography variant="h5" align="left"  className="sign-in-text">
          Sign In
        </Typography>
        {isLoginFailed && 
          <Alert severity="error" onClose={()=>{setIsLoginFailed(false)}}>Login failed</Alert>
        }
          <form onSubmit={handleSubmit(onSubmit)}>
            <TextField
              fullWidth
              label="Email"
              margin="normal"
              {...register("email", { required: "Email is required" })}
              error={!!errors.email}
              helperText={errors.email?.message}
            />
            <TextField
              fullWidth
              label="Password"
              type="password"
              margin="normal"
              {...register("password", { required: "Password is required" })}
              error={!!errors.password}
              helperText={errors.password?.message}
            />
            <Typography variant="body2" align="right" className="forgot-password"  sx={{ mt: '1em' }}>
              Forgot password?
            </Typography>
            <Button type="submit" fullWidth variant="contained" color="primary" disabled={isSubmitting} className="sign-in-button" sx={{mt:'1em',borderRadius: 1.9,bgcolor:'#3F67FB',textTransform: 'none'}}> 
              {isSubmitting ? "Signing In..." : "Sign In"}
            </Button>
          </form>

          <Typography variant="body1" className="login-alternatives" align="left" sx={{mt:'1em'}}>
            Login using:
          </Typography>

          <Box sx={{ display: "flex", flexDirection: "row", gap: 1, mt: "0.5em"}}>
            <Button fullWidth variant="outlined" startIcon={<GoogleIcon />} sx={{textTransform: 'none'}}> 
              Google
            </Button>
            <Button fullWidth variant="outlined" startIcon={<FacebookIcon />}sx={{textTransform: 'none'}}>
              Facebook
            </Button>
          </Box>
          
          <Typography variant="body1" align="left" sx={{mt:'1em'}}>
            Don’t have an account? 
          </Typography>
          <Box sx={{ display: "flex", flexDirection: "row", gap: '5em', mt: "0.5em"}}>
            <Typography variant="body2" className="create-account" onClick={createAccount} sx={{mt:'0.5em'}}>
              Create new account →
            </Typography>
          </Box>


        </CardContent>
   
      </Card>
    </Box>
  );
}
