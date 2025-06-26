// index.js
require('dotenv').config();
const express = require('express');
const nodemailer = require('nodemailer');
const bodyParser = require('body-parser');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(bodyParser.json());

app.post('/invite', async (req, res) => {
  const { email, invitationLink } = req.body;

  if (!email || !invitationLink) {
    return res.status(400).json({ error: 'Missing email or invitationLink' });
  }

  try {
    const transporter = nodemailer.createTransport({
      service: process.env.EMAIL_SERVICE,
      auth: {
        user: process.env.EMAIL_USER,
        pass: process.env.EMAIL_PASS,
      },
    });

    const mailOptions = {
      from: `"LREAS System" <${process.env.EMAIL_USER}>`,
      to: email,
      subject: 'Welcome to LREAS!',
      html: `
        <h2>Welcome to the LREAS System!</h2>
        <p>We're excited to have you on board.</p>
        <p>Please click the link below to activate your account:</p>
        <a href="${invitationLink}">${invitationLink}</a>
        <p>If you didn't expect this email, you can ignore it.</p>
      `,
    };

    // await transporter.sendMail(mailOptions);

    const info = await transporter.sendMail(mailOptions);
    console.log('Email sent: ', info);


    res.status(200).json({ message: 'Invitation email sent successfully.' });
  } catch (error) {
    console.error('Email sending failed:', error);
    res.status(500).json({ error: 'Failed to send email.' });
  }
});

app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});
