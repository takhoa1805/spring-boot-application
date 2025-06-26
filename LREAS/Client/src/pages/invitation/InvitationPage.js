import React, { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { TextField, Button, Card, CardContent, Typography, Box, CircularProgress, Alert } from "@mui/material";
import { useForm } from "react-hook-form";
import brandLogo from '../../images/brand_icon.png';
import { createPasswordFromInvitation, getInvitationInformation } from "../../api";
import "./styles/InvitationPage.css"; // Import the CSS file

export default function InvitationPage() {
  const [isRegisterFailed, setIsRegisterFailed] = useState(false);
  const [isLoadingInvitationInformation, setIsLoadingInvitationInformation] = useState(true);
  const [isValidInvitation, setIsValidInvitation] = useState(false);
  const [invitationInformation, setInvitationInformation] = useState({});
  const [confirmPassword, setConfirmPassword] = useState("");
  const [registerStatus, setRegisterStatus] = useState({
    status: false,
    message: "Unexpected error during register process"
  });
  const [password, setPassword] = useState("");
  const { invitationId } = useParams();
  const navigate = useNavigate();

  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm();

  useEffect(() => {
    const fetchInvitationInfo = async () => {
      const invitationInformation = await getInvitationInformation(invitationId);
      setIsValidInvitation(!!invitationInformation);

      if (invitationInformation) {
        setInvitationInformation(invitationInformation);
      }

      setIsLoadingInvitationInformation(false);
    };

    fetchInvitationInfo();
  }, [invitationId]);

  const onSubmit = async (data) => {
    if (password !== confirmPassword) {
      return;
    }

    try {
      const response = await createPasswordFromInvitation(data.password, invitationId);
      if (response?.status === 200) {
        navigate('/login');
      } else {
        const errorMessage = response.data.error.message.match(/"(.*?)"/)?.[1] || "Unexpected error during signing up process";
        setRegisterStatus(errorMessage);
      }
    } catch (e) {
      setRegisterStatus({ status: "error", message: "Unexpected error in verifying process" });
    }
  };

  return (
    <Box className="login-container">
      <Card className="card-container">
        {isLoadingInvitationInformation ? (
          <Box display="flex" justifyContent="center" alignItems="center" height="300px">
            <CircularProgress />
          </Box>
        ) : !isValidInvitation ? (
          <Box display="flex" flexDirection="column" alignItems="center" justifyContent="center" height="300px" p={3}>
            <Alert severity="error">Invalid or expired invitation link.</Alert>
          </Box>
        ) : (
          <>
            <Box className="header-container">
              <img src={brandLogo} alt="Brand Logo" className="brand-logo" />
              <Typography variant="h4" align="center" className="brand-text" gutterBottom>
                LREAS
              </Typography>
            </Box>

            <CardContent className="card-content">
              <Typography variant="h6" align="left" className="sign-in-text">
                Welcome to LREAS, {invitationInformation?.username}
              </Typography>

              {isRegisterFailed && <Alert severity="error" onClose={() => setIsRegisterFailed(false)}>Register failed</Alert>}
              {registerStatus.status === "error" && <Alert severity="error">{registerStatus.message}</Alert>}

              <form onSubmit={handleSubmit(onSubmit)}>
                <TextField fullWidth margin="normal" value={invitationInformation?.email || 'Invalid email'} disabled />
                <TextField fullWidth label="Password" type="password" margin="normal"
                  {...register("password", { required: "Password is required" })}
                  error={!!errors.password} helperText={errors.password?.message}
                  onChange={(e) => setPassword(e.target.value)} />
                
                <TextField fullWidth label="Confirm Password" type="password" margin="normal"
                  {...register("confirmPassword", { required: "Password confirmation is required" })}
                  error={!!errors.confirmPassword} helperText={errors.confirmPassword?.message}
                  onChange={(e) => setConfirmPassword(e.target.value)} />

                {password !== confirmPassword && confirmPassword !== "" && <Alert severity="error">Passwords do not match</Alert>}

                <Button type="submit" fullWidth variant="contained" color="primary"
                  disabled={isSubmitting} className="sign-in-button" 
                  sx={{ mt: '1em', borderRadius: 1.9, bgcolor: '#3F67FB', textTransform: 'none' }}>
                  {isSubmitting ? "Registering..." : "Register"}
                </Button>
              </form>
            </CardContent>
          </>
        )}
      </Card>
    </Box>
  );
}
