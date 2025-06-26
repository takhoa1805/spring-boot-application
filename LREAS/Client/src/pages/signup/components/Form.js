import React, {
    useState,
    useEffect
} from 'react';
import {
    useForm
} from "react-hook-form";
import {
    TextField,
    Button,
    Card,
    CardContent,
    CardHeader,
    CardActions,
    Typography,
    Box,
    Checkbox
} from "@mui/material";
import Alert from '@mui/material/Alert';

import "../styles/SignUpPage.css"; // Import the CSS file
import {
    setToken
} from "../../../utils";
import {
    createAccount
} from "../../../api";
import {
    useNavigate
} from "react-router-dom";


export default function Form({setSignUpStatus}) {
    const {
        register,
        handleSubmit,
        formState: {
            errors,
            isSubmitting
        },
    } = useForm();

    const navigate = useNavigate();

    const [isChecked, setIsChecked] = useState(false);

    const [confirmPassword, setConfirmPassword] = useState("");
    const [password, setPassword] = useState("");



    const onSubmit = async (data) => {


        if (password !== confirmPassword) {
            return;
        }


        if (!isChecked) {
            setSignUpStatus({
                status:"error",
                message:"You must agree to our terms and conditions to continue "
            });
            return;
        }



        try {
            // Mockup calling login api:
            const response = await createAccount(data);



            if (response?.status===200) {
                setSignUpStatus({
                    status:response.status,
                    message: response.data.message
                });
                navigate('/login');

            }  else {
                const errorMessage = response.data.error.message.match(/"(.*?)"/)?.[1] || "Unexpected error during signing up process";
                setSignUpStatus({
                    status: "error",
                    message: errorMessage
                });
            }


        } catch (e) {
            if (e.status >= 400 && e.status < 500) {
                setSignUpStatus({
                    status: "error",
                    message: e.response.data.error.message || e.message
                });
            }   else {
                setSignUpStatus({
                    status: "error",
                    message: "Unexpected error during signing up process"
                });
            }
        }
    };




    return (

        <form onSubmit={handleSubmit(onSubmit)}>
            
        <TextField
          fullWidth
          label="Full name"
          margin="normal"
          {...register("username", { required: "Name is required" })}
          error={!!errors.username}
          helperText={errors.username?.message}
        />



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
          label="Institution name"
          margin="normal"
          {...register("institutionName", { required: "Organization name is required" })}
          error={!!errors.organizationName}
          helperText={errors.organizationName?.message}
        />

        <TextField
          fullWidth
          label="Prefered subdomain (example.lreas.com)"
          margin="normal"
          {...register("subdomain", { required: "Prefered domain is required" })}
          error={!!errors.preferedDomain}
          helperText={errors.preferedDomain?.message}
        />

        <TextField
          fullWidth
          label="Password"
          type="password"
          margin="normal"
          {...register("password", { required: "Password is required" })}
          error={!!errors.password}
          helperText={errors.password?.message}
          onChange={(event) => {
            setPassword(event.target.value);
          }}
        />

        <TextField
          fullWidth
          label="Confirm password"
          type="password"
          margin="normal"
          {...register("confirmPassword", { required: "Password confirmation is required" })}
          error={!!errors.confirmPassword}
          helperText={errors.confirmPassword?.message}
          onChange={(event) => {
            setConfirmPassword(event.target.value);
          }}
        />

        {password !== confirmPassword && confirmPassword !== "" &&
            <Alert severity="error" >Your password does not match</Alert>
        
        }

        

        <Box sx={{ display: "flex", flexDirection: "row"}}>

            <Checkbox
                onChange={()=>{setIsChecked(!isChecked)}}
                inputProps={{ 'aria-label': 'controlled' }}
            />
            <Typography variant="body2" align="left"  sx={{ mt: '1em' }}>
            I have read and agree to our <span className="terms-conditions"> terms and conditions </span>
            </Typography>
        </Box>

        <Button type="submit" fullWidth variant="contained" color="primary" disabled={isSubmitting} className="sign-in-button" sx={{mt:'1em',borderRadius: 1.9,bgcolor:'#3F67FB',textTransform: 'none'}}> 
          {isSubmitting ? "Signing Up..." : "Sign Up"}
        </Button>
      </form>
    )
}