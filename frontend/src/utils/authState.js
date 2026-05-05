import { getAccessToken, getRefreshToken } from "./tokenStorage";

function isAuthenticated() {
  const token = getAccessToken();
  return !!token;
}

function getStoredAuthSnapshot() {
  const accessToken = getAccessToken();
  const refreshToken = getRefreshToken();

  return {
    isAuthenticated: !!accessToken,
    hasRefreshToken: !!refreshToken,
  };
}

export { isAuthenticated, getStoredAuthSnapshot };