import { useEffect, useMemo, useState } from 'react';
import { AuthPage } from './pages/AuthPage';
import { Dashboard } from './pages/Dashboard';
import type { AuthResponse } from './api/types';

const STORAGE_KEY = 'lookator.auth';

export function App() {
  const [auth, setAuth] = useState<AuthResponse | null>(() => {
    const saved = localStorage.getItem(STORAGE_KEY);
    return saved ? JSON.parse(saved) : null;
  });

  useEffect(() => {
    if (auth) {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(auth));
    } else {
      localStorage.removeItem(STORAGE_KEY);
    }
  }, [auth]);

  const session = useMemo(() => auth, [auth]);

  return session ? (
    <Dashboard auth={session} onLogout={() => setAuth(null)} />
  ) : (
    <AuthPage onAuthenticated={setAuth} />
  );
}
