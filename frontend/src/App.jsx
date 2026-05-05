import { useState, useEffect } from "react";
import Login from "./pages/Login";
import Register from "./pages/Register";
import { getCurrentUserProfile } from "./services/authApi";
import { getAccessToken } from "./utils/tokenStorage";
import { getStoredAuthSnapshot } from "./utils/authState";

function App() {
  const [activePage, setActivePage] = useState("home");
  const [loggedIn, setLoggedIn] = useState(false);
  const [homeMessage, setHomeMessage] = useState("");
  const [profileLoading, setProfileLoading] = useState(false);
  const [profileError, setProfileError] = useState("");
  const [profile, setProfile] = useState(null);

  useEffect(() => {
    const snapshot = getStoredAuthSnapshot();
    setLoggedIn(snapshot.isAuthenticated);
  }, []);

  const handleLoginSuccess = () => {
    setLoggedIn(true);
    setActivePage("protected");
  };

  const handleLoadProfile = async () => {
    const token = getAccessToken();
    if (!token) {
      setProfileError("Access token is missing.");
      return;
    }

    setProfileLoading(true);
    setProfileError("");
    setProfile(null);

    try {
      const data = await getCurrentUserProfile(token);
      setProfile(data);
    } catch (error) {
      setProfileError(error.message || "Failed to load profile.");
    } finally {
      setProfileLoading(false);
    }
  };

  const renderHome = () => (
    <div className="min-h-screen flex items-center justify-center px-4 py-8 bg-slate-50">
      <div className="w-full max-w-md bg-white rounded-2xl shadow-md p-6 sm:p-8 text-center">
        <h1 className="text-3xl font-semibold text-slate-900 mb-2">CodeQuest</h1>
        <p className="text-slate-600 mb-8">Learn Java through quests, levels, and practice.</p>

        {loggedIn && (
          <div
            className="mb-6 rounded-lg border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-800"
            role="status"
          >
            You are logged in for this MVP session.
          </div>
        )}

        {homeMessage && (
          <div
            className="mb-6 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800"
            role="alert"
          >
            {homeMessage}
          </div>
        )}

        <div className="space-y-3 flex flex-col">
          <button
            onClick={() => {
              setHomeMessage("");
              setActivePage("login");
            }}
            className="rounded-xl bg-slate-900 px-4 py-3 text-sm font-semibold text-white transition hover:bg-slate-700"
          >
            Log in
          </button>
          <button
            onClick={() => {
              setHomeMessage("");
              setActivePage("register");
            }}
            className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm font-semibold text-slate-900 transition hover:bg-slate-50"
          >
            Register
          </button>
          <button
            onClick={() => {
              if (loggedIn) {
                setHomeMessage("");
                setActivePage("protected");
              } else {
                setHomeMessage("Please log in to view the protected area.");
              }
            }}
            className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm font-semibold text-slate-900 transition hover:bg-slate-50"
          >
            Protected Area
          </button>
        </div>
      </div>
    </div>
  );

  const renderProtected = () => {
    const token = getAccessToken();

    return (
      <div className="min-h-screen flex items-center justify-center px-4 py-8 bg-slate-50">
        <div className="w-full max-w-md bg-white rounded-2xl shadow-md p-6 sm:p-8">
          <h1 className="text-2xl font-semibold text-slate-900 mb-4">Protected Area</h1>
          <p className="text-sm text-slate-600 mb-6">Dashboard will be added later.</p>

          {!token ? (
            <div className="text-center">
              <p className="text-slate-700 mb-4">You need to log in first.</p>
              <button
                onClick={() => setActivePage("login")}
                className="rounded-xl bg-slate-900 px-4 py-3 text-sm font-semibold text-white transition hover:bg-slate-700"
              >
                Go to Login
              </button>
            </div>
          ) : (
            <div className="space-y-4">
              <button
                onClick={handleLoadProfile}
                disabled={profileLoading}
                className="w-full rounded-xl bg-slate-900 px-4 py-3 text-sm font-semibold text-white transition hover:bg-slate-700 disabled:cursor-not-allowed disabled:bg-slate-400"
              >
                {profileLoading ? "Loading..." : "Load my profile"}
              </button>

              {profileError && (
                <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800" role="alert">
                  {profileError}
                </div>
              )}

              {profile && (
                <div className="rounded-lg border border-slate-200 bg-slate-50 px-4 py-3">
                  <h2 className="text-lg font-semibold text-slate-900 mb-2">Profile</h2>
                  <p><strong>Name:</strong> {profile.name}</p>
                  <p><strong>Email:</strong> {profile.email}</p>
                  <p><strong>Rank:</strong> {profile.rank}</p>
                  <p><strong>XP:</strong> {profile.xp}</p>
                  <p><strong>Streak:</strong> {profile.streak}</p>
                </div>
              )}

              <button
                onClick={() => setActivePage("home")}
                className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm font-semibold text-slate-900 transition hover:bg-slate-50"
              >
                Back to Home
              </button>
            </div>
          )}
        </div>
      </div>
    );
  };

  if (activePage === "login") {
    return <Login onNavigate={setActivePage} onLoginSuccess={handleLoginSuccess} />;
  }

  if (activePage === "register") {
    return <Register onNavigate={setActivePage} />;
  }

  if (activePage === "protected") {
    return renderProtected();
  }

  return renderHome();
}

export default App;

