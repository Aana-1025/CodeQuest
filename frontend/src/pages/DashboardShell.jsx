export default function DashboardShell({ profile, onBackHome }) {
  return (
    <div className="min-h-screen bg-slate-50">
      {/* Header */}
      <div className="border-b border-slate-200 bg-white px-4 py-6 sm:px-8">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-semibold text-slate-900">Dashboard</h1>
            <p className="mt-1 text-sm text-slate-600">Your Java learning command center.</p>
          </div>
          {typeof onBackHome === "function" && (
            <button
              onClick={onBackHome}
              className="rounded-xl border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-900 transition hover:bg-slate-50"
            >
              Back to Home
            </button>
          )}
        </div>
      </div>

      {/* Main content */}
      <div className="px-4 py-8 sm:px-8">
        <div className="max-w-4xl mx-auto space-y-6">
          {/* Profile summary card */}
          <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
            <h2 className="text-xl font-semibold text-slate-900 mb-4">Profile Summary</h2>
            {profile ? (
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <p className="text-sm text-slate-600">Name</p>
                  <p className="text-lg font-semibold text-slate-900">{profile.name}</p>
                </div>
                <div>
                  <p className="text-sm text-slate-600">Email</p>
                  <p className="text-lg font-semibold text-slate-900">{profile.email}</p>
                </div>
                <div>
                  <p className="text-sm text-slate-600">Rank</p>
                  <p className="text-lg font-semibold text-slate-900">{profile.rank}</p>
                </div>
                <div>
                  <p className="text-sm text-slate-600">XP</p>
                  <p className="text-lg font-semibold text-slate-900">{profile.xp}</p>
                </div>
                <div>
                  <p className="text-sm text-slate-600">Streak</p>
                  <p className="text-lg font-semibold text-slate-900">{profile.streak}</p>
                </div>
              </div>
            ) : (
              <p className="text-slate-600">Profile not loaded yet.</p>
            )}
          </div>

          {/* Course progress placeholder */}
          <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
            <h2 className="text-xl font-semibold text-slate-900 mb-4">Course Progress</h2>
            <div className="bg-slate-50 rounded-lg px-4 py-6 text-center">
              <p className="text-slate-600">Course progress will appear here later.</p>
            </div>
          </div>

          {/* Next actions placeholder */}
          <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
            <h2 className="text-xl font-semibold text-slate-900 mb-4">Next Actions</h2>
            <div className="bg-slate-50 rounded-lg px-4 py-6 text-center">
              <p className="text-slate-600">Generate course, lessons, quizzes, and code practice will be added later.</p>
            </div>
          </div>

          {/* Status note */}
          <div className="rounded-2xl border border-amber-200 bg-amber-50 p-6 shadow-sm">
            <p className="text-sm text-amber-800">
              <strong>Dashboard shell:</strong> This is a dashboard shell only. No course, AI, XP, streak, leaderboard, or code execution logic is implemented yet.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
