import { styled } from '@mui/material/styles';
import Button from '@mui/material/Button';
import { ExcelIcon } from '../../../images/Icon';
import "./InputFileUpload.css";

const VisuallyHiddenInput = styled('input')({
  clip: 'rect(0 0 0 0)',
  clipPath: 'inset(50%)',
  height: 1,
  overflow: 'hidden',
  position: 'absolute',
  bottom: 0,
  left: 0,
  whiteSpace: 'nowrap',
  width: 1,
});

export default function InputFileUpload({handleCSVUpload}) {
  return (
    <Button
      component="label"
      role={undefined}
      variant="contained"
      tabIndex={-1}
      // startIcon={<img src={ExcelIcon}/>}
      className="upload-csv-button"
    >
      Upload csv
      <VisuallyHiddenInput
        type="file"
        accept=".csv"
        onChange={handleCSVUpload}
      />
    </Button>
  );
}