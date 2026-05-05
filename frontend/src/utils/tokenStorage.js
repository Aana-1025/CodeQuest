const ACCESS_TOKEN_KEY = "codequest_access_token";
const REFRESH_TOKEN_KEY = "codequest_refresh_token";

function isLocalStorageAvailable() {
  try {
    const testKey = "codequest_localstorage_test";
    window.localStorage.setItem(testKey, "test");
    window.localStorage.removeItem(testKey);
    return true;
  } catch {
    return false;
  }
}

function saveTokens({ accessToken, refreshToken }) {
  if (!isLocalStorageAvailable()) {
    return;
  }

  if (typeof accessToken === "string") {
    window.localStorage.setItem(ACCESS_TOKEN_KEY, accessToken);
  }

  if (typeof refreshToken === "string") {
    window.localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
  }
}

function getAccessToken() {
  if (!isLocalStorageAvailable()) {
    return null;
  }

  const token = window.localStorage.getItem(ACCESS_TOKEN_KEY);
  return token || null;
}

function getRefreshToken() {
  if (!isLocalStorageAvailable()) {
    return null;
  }

  const token = window.localStorage.getItem(REFRESH_TOKEN_KEY);
  return token || null;
}

function clearTokens() {
  if (!isLocalStorageAvailable()) {
    return;
  }

  window.localStorage.removeItem(ACCESS_TOKEN_KEY);
  window.localStorage.removeItem(REFRESH_TOKEN_KEY);
}

export { ACCESS_TOKEN_KEY, REFRESH_TOKEN_KEY, saveTokens, getAccessToken, getRefreshToken, clearTokens };
