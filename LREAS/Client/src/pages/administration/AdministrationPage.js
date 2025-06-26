import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  Box,
  Button,
  Container,
  Grid,
  IconButton,
  InputBase,
  List,
  ListItem,
  ListItemText,
  Menu,
  MenuItem,
  Paper,
  Typography,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Checkbox,
  FormControlLabel,
  TextField,
  Select,
  FormControl,
  InputLabel,
  RadioGroup,
  Radio,
  LinearProgress,
} from "@mui/material";
import MoreVertIcon from "@mui/icons-material/MoreVert";
import SearchIcon from "@mui/icons-material/Search";
import CloseIcon from "@mui/icons-material/Close";

import { Header } from "../../components/Header";
import { NavigationDrawer } from "../../components/NavigationDrawer";
import {
  inviteUser,
  getAllUser,
  deleteUser,
  banUser,
  unBanUser,
  promoteUserToStudent,
  promoteUserToTeacher,
  promoteUserToAdmin,
  sendInvitationEmail
} from "../../api";

import InputFileUpload from "./component/InputFileUpload";
import Papa from 'papaparse'; // For parsing CSV



export default function AdministrationPage() {
  const navigate = useNavigate();
  const [anchorEl, setAnchorEl] = useState(null);
  const [selectedUserIndex, setSelectedUserIndex] = useState(null);
  const [addUserOpen, setAddUserOpen] = useState(false);
  const [emailInput, setEmailInput] = useState("");
  const [usernameInput, setUsernameInput] = useState("");
  const [roles, setRoles] = useState({
    Student: false,
    Teacher: false,
    Administrator: false,
  });
  const [searchQuery, setSearchQuery] = useState("");
  const [users, setUsers] = useState([]);
  const [filteredUsers, setFilteredUsers] = useState([]);
  const [roleFilter, setRoleFilter] = useState("All");
  const [stateFilter, setStateFilter] = useState("All");

  const [deleteConfirmOpen, setDeleteConfirmOpen] = useState(false);
  const [banConfirmOpen, setBanConfirmOpen] = useState(false);
  const [promoteDialogOpen, setPromoteDialogOpen] = useState(false);
  const [selectedPromoteRole, setSelectedPromoteRole] = useState("Student");

  const [csvUsers, setCsvUsers] = useState([]);
  const [processing, setProcessing] = useState(false);
  const [progress, setProgress] = useState(0);
  const [currentUser, setCurrentUser] = useState('');
  const [userStatuses, setUserStatuses] = useState([]);


  const resetState = () => {
    setEmailInput('');
    setUsernameInput('');
    setRoles('');
    setCsvUsers([]);
    setProcessing(false);
    setProgress(0);
    setCurrentUser('');
    setUserStatuses([]);
  };
  const formatRole = (role) => {
    switch (role.toUpperCase()) {
      case "ADMIN":
        return "Administrator";
      case "STUDENT":
        return "Student";
      case "TEACHER":
        return "Teacher";
      default:
        return role;
    }
  };

  const formatState = (state) => {
    switch (state.toUpperCase()) {
      case "ACTIVE":
        return "Active";
      case "INACTIVE":
        return "Inactive";
      case "DELETED":
        return "Deleted";
      case "PENDING":
        return "Pending";
      default:
        return state;
    }
  };

  const fetchUsers = async () => {
    try {
      const response = (await getAllUser()).data;
      if (response.success) {
        const formattedUsers = response.users.map((user) => ({
          userId: user.userId,
          name: user.name,
          email: user.email,
          role: formatRole(user.role),
          workflowState: user.workflowState.toLowerCase(),
        }));
        setUsers(formattedUsers);
        setFilteredUsers(formattedUsers);
      } else {
        console.error("Failed to fetch users:", response.message);
      }
    } catch (error) {
      console.error("Error fetching users:", error);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  useEffect(() => {
    const query = searchQuery.toLowerCase();
    const filtered = users.filter((user) => {
      const matchesSearch =
        user.name.toLowerCase().includes(query) ||
        user.email.toLowerCase().includes(query);
      const matchesRole =
        roleFilter === "All" || user.role === roleFilter;
      const matchesState =
        stateFilter === "All" ||
        user.workflowState === stateFilter.toLowerCase();
      return matchesSearch && matchesRole && matchesState;
    });
    setFilteredUsers(filtered);
  }, [searchQuery, roleFilter, stateFilter, users]);

  const handleOpenMenu = (event, index) => {
    event.stopPropagation(); // Prevent navigating when clicking on the menu
    setAnchorEl(event.currentTarget);
    setSelectedUserIndex(index);
  };

  const handleCloseMenu = () => {
    setAnchorEl(null);
    setSelectedUserIndex(null);
  };

  const handleAddUser = async () => {
    const selectedRole = Object.keys(roles).find((key) => roles[key]);
    if (!emailInput || !usernameInput || !selectedRole) {
      alert("Please fill in email, username, and select a role.");
      return;
    }

    try {
      const body = {
        email: emailInput,
        username: usernameInput,
        role: selectedRole.toUpperCase() == "ADMINISTRATOR" ? "ADMIN" : selectedRole.toUpperCase(),
      };

      const data = await inviteUser(body);
      console.log("data.status",data.status);
      if (data.status !== 200) {
        alert ("failed to invite user");
        return;
      } else {
        alert("User invited successfully.");
        const sendEmail = await sendInvitationEmail({email:emailInput,invitationLink:data.data.invitationUrl.replace("lvh.me","http://lreas.takhoa.site")});
      }

      // console.log('data',data);
      // alert(data.data.invitationUrl);

      setEmailInput("");
      setUsernameInput("");
      setRoles({ Student: false, Teacher: false, Administrator: false });
      setAddUserOpen(false);
      fetchUsers();
    } catch (error) {
      console.error("Failed to invite user:", error);
      alert("Failed to invite user.");
    }
  };

  const handleDeleteUser = async () => {
    const user = filteredUsers[selectedUserIndex];
    await deleteUser(user.userId);
    setDeleteConfirmOpen(false);
    fetchUsers();
    handleCloseMenu();
  };

  const handleBanToggleUser = async () => {
    const user = filteredUsers[selectedUserIndex];
    if (user.workflowState === "inactive") {
      await unBanUser(user.userId);
    } else {
      await banUser(user.userId);
    }
    setBanConfirmOpen(false);
    fetchUsers();
    handleCloseMenu();
  };

  const handlePromoteUser = async () => {
    const user = filteredUsers[selectedUserIndex];
    switch (selectedPromoteRole) {
      case "Student":
        await promoteUserToStudent(user.userId);
        break;
      case "Teacher":
        await promoteUserToTeacher(user.userId);
        break;
      case "Administrator":
        await promoteUserToAdmin(user.userId);
        break;
      default:
        break;
    }
    setPromoteDialogOpen(false);
    fetchUsers();
    handleCloseMenu();
  };

  const selectedUser = selectedUserIndex !== null ? filteredUsers[selectedUserIndex] : null;


  const handleCSVUpload = (e) => {
    const file = e.target.files[0];
    if (!file || file.type !== 'text/csv') {
      alert('Please upload a valid CSV file.');
      return;
    }

    Papa.parse(file, {
      header: true,
      skipEmptyLines: true,
      complete: (results) => {
        const valid = results.meta.fields.length === 3 &&
          results.meta.fields.includes('email') &&
          results.meta.fields.includes('username') &&
          results.meta.fields.includes('role');

        if (!valid) {
          alert('CSV format invalid. Must contain columns: email, username, role');
          return;
        }

        setCsvUsers(results.data);
        setUserStatuses(results.data.map(user => ({
          email: user.email,
          status: 'pending'
        })));
      },
    });
  };

  const handleBulkInvite = async () => {
    setProcessing(true);
    let completed = 0;

    for (const [index, user] of csvUsers.entries()) {
      const { email, username, role } = user;
      // console.log("user",user);
      setCurrentUser(email);

      try {

        const data = await inviteUser({ email:email, username:username, role:role.toUpperCase() });
        // console.log("data.status",data.status);
        if (data.status !== 200) {
          console.log("failed to invite user",{email,username,role});
          continue;
        } else {
          console.log("User invited successfully.",{email,username,role});
          const sendEmail = await sendInvitationEmail({email:email,invitationLink:data.data.invitationUrl.replace("lvh.me","http://lreas.takhoa.site")});
        }


        // await inviteUser({ email, username, role });
        // const invitationLink = 'https://example.com/invite'; // Generate dynamically as needed
        // await sendInvitationEmail({ email, invitationLink });

        setUserStatuses(prev =>
          prev.map(u =>
            u.email === email ? { ...u, status: 'success' } : u
          )
        );
      } catch (err) {
        setUserStatuses(prev =>
          prev.map(u =>
            u.email === email ? { ...u, status: 'fail' } : u
          )
        );
      }

      completed += 1;
      setProgress((completed / csvUsers.length) * 100);
    }

    setCurrentUser('');
  };



  return (
    <div className="content-page">
      <Header />
      <div className="content-page-container">
        <div className="container">
          <NavigationDrawer />
          <div className="main-container">
            <Container maxWidth="md" sx={{ mt: 4 }}>
              <Typography variant="h4" gutterBottom>
                Administration
              </Typography>

              <Grid container spacing={2} alignItems="center">
                <Grid item xs={12} sm={6}>
                  <Paper
                    component="form"
                    sx={{ p: "4px 8px", display: "flex", alignItems: "center" }}
                  >
                    <SearchIcon />
                    <InputBase
                      sx={{ ml: 1, flex: 1 }}
                      placeholder="Search by name or email"
                      value={searchQuery}
                      onChange={(e) => setSearchQuery(e.target.value)}
                    />
                  </Paper>
                </Grid>
                <Grid item xs={6} sm={3}>
                  <FormControl fullWidth>
                    <InputLabel>Role</InputLabel>
                    <Select
                      value={roleFilter}
                      label="Role"
                      onChange={(e) => setRoleFilter(e.target.value)}
                    >
                      <MenuItem value="All">All</MenuItem>
                      <MenuItem value="Administrator">Administrator</MenuItem>
                      <MenuItem value="Teacher">Teacher</MenuItem>
                      <MenuItem value="Student">Student</MenuItem>
                    </Select>
                  </FormControl>


                </Grid>
                <Grid item xs={6} sm={3}>
                  <FormControl fullWidth>
                    <InputLabel>State</InputLabel>
                    <Select
                      value={stateFilter}
                      label="State"
                      onChange={(e) => setStateFilter(e.target.value)}
                    >
                      <MenuItem value="All">All</MenuItem>
                      <MenuItem value="ACTIVE">Active</MenuItem>
                      <MenuItem value="INACTIVE">Inactive</MenuItem>
                      <MenuItem value="DELETED">Deleted</MenuItem>
                      <MenuItem value="PENDING">Pending</MenuItem>
                    </Select>
                  </FormControl>
                </Grid>
                <Grid item xs={12} sm="auto">
                  <Button variant="contained" onClick={() => setAddUserOpen(true)}>
                    Add single user
                  </Button>
                    {/* <Typography variant="body1" fontWeight="bold" gutterBottom>
                    Or upload a CSV file
                  </Typography> */}
                  <InputFileUpload handleCSVUpload={handleCSVUpload} />


                    {csvUsers.length > 0 && !processing && (
                      <>
                      <Button variant="contained" color="primary" onClick={handleBulkInvite} 
                        sx={{ marginLeft: '10px' }}  

                      >
                        Invite {csvUsers.length} Users
                      </Button>
                        <Button variant="contained" color="dangerous" onClick={resetState} 
                        sx={{ marginLeft: '10px' }}  

                      >
                        Cancel
                      </Button>
                      </>
                    )}

                    {processing && (
                      <Box sx={{ mt: 3 }}>
                        <Typography variant="body1">Inviting: {currentUser}</Typography>
                        <LinearProgress variant="determinate" value={progress} sx={{ mt: 1, mb: 2 }} />
                        <List dense>
                          {userStatuses.map(({ email, status }) => (
                            <ListItem key={email}>
                              <ListItemText
                                primary={email}
                                secondary={status}
                                secondaryTypographyProps={{
                                  color:
                                    status === 'success'
                                      ? 'green'
                                      : status === 'fail'
                                      ? 'red'
                                      : 'textSecondary',
                                }}
                              />
                            </ListItem>
                          ))}
                        </List>
                      </Box>
                    )}
                </Grid>
              </Grid>

              <List sx={{ mt: 2 }}>
                {filteredUsers.map((user, index) => (
                  <ListItem
                    key={user.userId}
                    divider
                    button
                    onClick={() => navigate(`/profile/${user.userId}`)}
                    secondaryAction={
                      <IconButton edge="end" onClick={(e) => handleOpenMenu(e, index)}>
                        <MoreVertIcon />
                      </IconButton>
                    }
                  >
                    <ListItemText
                      primary={`${user.name} (${formatState(user.workflowState)})`}
                      secondary={`${user.role} - ${user.email}`}
                    />
                  </ListItem>
                ))}
              </List>

              <Menu anchorEl={anchorEl} open={Boolean(anchorEl)} onClose={handleCloseMenu}>
                {(selectedUser?.workflowState.toLowerCase() != 'pending' && !selectedUser?.workflowState.toLowerCase().includes('delete')) && (
                  <MenuItem onClick={() => setBanConfirmOpen(true)}>
                    {selectedUser?.workflowState === "inactive" ? "Unban user" : "Ban user"}
                  </MenuItem>
                ) }
          
                <MenuItem onClick={() => setDeleteConfirmOpen(true)}>
                  Remove user
                </MenuItem>
                <MenuItem onClick={() => setPromoteDialogOpen(true)}>
                  Promote user
                </MenuItem>
              </Menu>

              {/* Add User Dialog */}
              <Dialog open={addUserOpen} onClose={() => setAddUserOpen(false)}>
                <DialogTitle>
                  Add new user
                  <IconButton
                    onClick={() => setAddUserOpen(false)}
                    sx={{ position: "absolute", right: 8, top: 8 }}
                  >
                    <CloseIcon />
                  </IconButton>
                </DialogTitle>
                <DialogContent>
                  <Typography variant="body2" mb={1}>Enter email</Typography>
                  <TextField
                    fullWidth
                    variant="standard"
                    placeholder="user@email.com"
                    value={emailInput}
                    onChange={(e) => setEmailInput(e.target.value)}
                    sx={{ mb: 2 }}
                  />
                  <Typography variant="body2" mb={1}>Enter username</Typography>
                  <TextField
                    fullWidth
                    variant="standard"
                    placeholder="username"
                    value={usernameInput}
                    onChange={(e) => setUsernameInput(e.target.value)}
                    sx={{ mb: 2 }}
                  />
                  <Typography variant="body1" fontWeight="bold" gutterBottom>
                    Select a role
                  </Typography>
                  {["Teacher", "Student", "Administrator"].map((roleOption) => (
                    <FormControlLabel
                      key={roleOption}
                      control={
                        <Checkbox
                          checked={roles[roleOption]}
                          onChange={() =>
                            setRoles({
                              Student: false,
                              Teacher: false,
                              Administrator: false,
                              [roleOption]: true,
                            })
                          }
                        />
                      }
                      label={roleOption}
                    />
                  ))}
                </DialogContent>
                <DialogActions>
                  <Button variant="contained" onClick={handleAddUser}>ADD USER</Button>
                  <Button onClick={() => setAddUserOpen(false)}>CANCEL</Button>
                </DialogActions>
              </Dialog>

              {/* Delete confirmation */}
              <Dialog open={deleteConfirmOpen} onClose={() => setDeleteConfirmOpen(false)}>
                <DialogTitle>Confirm Delete</DialogTitle>
                <DialogContent>
                  Are you sure you want to delete this user?
                </DialogContent>
                <DialogActions>
                  <Button color="error" onClick={handleDeleteUser}>Delete</Button>
                  <Button onClick={() => setDeleteConfirmOpen(false)}>Cancel</Button>
                </DialogActions>
              </Dialog>

              {/* Ban/Unban confirmation */}
              <Dialog open={banConfirmOpen} onClose={() => setBanConfirmOpen(false)}>
                <DialogTitle>Confirm Action</DialogTitle>
                <DialogContent>
                  Are you sure you want to {selectedUser?.workflowState === "inactive" ? "unban" : "ban"} this user?
                </DialogContent>
                <DialogActions>
                  <Button onClick={handleBanToggleUser}>
                    {selectedUser?.workflowState === "inactive" ? "Unban" : "Ban"}
                  </Button>
                  <Button onClick={() => setBanConfirmOpen(false)}>Cancel</Button>
                </DialogActions>
              </Dialog>

              {/* Promote user dialog */}
              <Dialog open={promoteDialogOpen} onClose={() => setPromoteDialogOpen(false)}>
                <DialogTitle>Promote User</DialogTitle>
                <DialogContent>
                  <FormControl component="fieldset">
                    <RadioGroup
                      value={selectedPromoteRole}
                      onChange={(e) => setSelectedPromoteRole(e.target.value)}
                    >
                      <FormControlLabel value="Student" control={<Radio />} label="Student" />
                      <FormControlLabel value="Teacher" control={<Radio />} label="Teacher" />
                      <FormControlLabel value="Administrator" control={<Radio />} label="Administrator" />
                    </RadioGroup>
                  </FormControl>
                </DialogContent>
                <DialogActions>
                  <Button onClick={handlePromoteUser}>Promote</Button>
                  <Button onClick={() => setPromoteDialogOpen(false)}>Cancel</Button>
                </DialogActions>
              </Dialog>
            </Container>
          </div>
        </div>
      </div>
    </div>
  );
}
