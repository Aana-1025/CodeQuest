import { useState } from "react";
import Login from "./pages/Login";
import Register from "./pages/Register";

function App() {
  const [activePage, setActivePage] = useState("home");
  const [loggedIn, setLoggedIn] = useState(false);

  const handleLoginSuccess = () => {
    setLoggedIn(true);
    setActivePage("home");
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

        <div className="space-y-3 flex flex-col">
          <button
            onClick={() => setActivePage("login")}
            className="rounded-xl bg-slate-900 px-4 py-3 text-sm font-semibold text-white transition hover:bg-slate-700"
          >
            Log in
          </button>
          <button
            onClick={() => setActivePage("register")}
            className="rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm font-semibold text-slate-900 transition hover:bg-slate-50"
          >
            Register
          </button>
        </div>
      </div>
    </div>
  );

  if (activePage === "login") {
    return <Login onNavigate={setActivePage} onLoginSuccess={handleLoginSuccess} />;
  }

  if (activePage === "register") {
    return <Register onNavigate={setActivePage} />;
  }

  return renderHome();
}

export default App;

