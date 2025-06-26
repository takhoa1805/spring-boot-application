import * as React from 'react';
import Box from '@mui/material/Box';
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';
import Switch from '@mui/material/Switch';
import FormControlLabel from '@mui/material/FormControlLabel';

export default function TimePicker({second, minute, setSecond, setMinute, isAutoChangeQuestion, setIsAutoChangeQuestion}) {

  const handleChangeMinute = (event) => {
    setMinute(event.target.value);
  };

  const handleChangeSecond = (event) => {
    setSecond(event.target.value);
  };

  const handleChangeisAutoChangeQuestion = (event) => {
    setIsAutoChangeQuestion(event.target.checked);
  };

  return (
    <>
      <Box sx={{ minWidth: 120, p: 2, border: '1px groove black',
          borderRadius: 1,
          bgcolor: 'primary.white',
          '&:hover': {
            bgcolor: 'primary.light',
          },}}>
        <FormControlLabel
          control={
            <Switch
              onChange={handleChangeisAutoChangeQuestion}
              checked={isAutoChangeQuestion}
              color="primary"
            />
          }
          label="Change question automatically"
        />
      </Box>
      {isAutoChangeQuestion && (
        <>
          <Box sx={{ display: 'flex', gap: 2, marginBottom: 2 }}>
            <FormControl fullWidth sx={{ '.MuiInputBase-root': { fontSize: '1.25rem', height: '56px' } }}>
              <InputLabel id="minute-select-label">Minute</InputLabel>
              <Select
                labelId="minute-select-label"
                id="minute-select"
                value={minute}
                label="Minute"
                onChange={handleChangeMinute}
                required
              >
                {[...Array(60).keys()].map((value) => (
                  <MenuItem key={value} value={value}>
                    {value}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
            <FormControl fullWidth sx={{ '.MuiInputBase-root': { fontSize: '1.25rem', height: '56px' } }}>
              <InputLabel id="second-select-label">Second</InputLabel>
              <Select
                labelId="second-select-label"
                id="second-select"
                value={second}
                label="Second"
                onChange={handleChangeSecond}
                required
              >
                {[...Array(60).keys()].map((value) => (
                  <MenuItem key={value} value={value}>
                    {value}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Box>
        </>
      )}
    </>
  );
}