// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { confirmSignUp } from './authService';

const ConfirmUserPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  // eslint-disable-next-line
  const [email, setEmail] = useState(location.state?.email || '');
  const [confirmationCode, setConfirmationCode] = useState('');

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    try {
      await confirmSignUp(email, confirmationCode);
      alert("Account confirmed successfully!\nSign in on next page.");
      navigate('/login');
    } catch (error) {
      alert(`Failed to confirm account: ${error}`);
    }
  };

return (
  <div className="loginForm">
    <h2>Confirm Account</h2>
    <form onSubmit={handleSubmit}>
      <div>
        <input
          className="inputText"
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          placeholder="Email"
          required
        />
      </div>
      <div>
        <input
          className="inputText"
          type="text"
          value={confirmationCode}
          onChange={(e) => setConfirmationCode(e.target.value)}
          placeholder="Confirmation Code"
          required />
      </div>
      <button type="submit">Confirm Account</button>
    </form>
  </div>
);

};

export default ConfirmUserPage;
