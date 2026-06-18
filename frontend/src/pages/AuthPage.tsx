import { FormEvent, useState } from 'react';
import { Building2, LogIn } from 'lucide-react';
import { api } from '../api/client';
import type { AuthResponse } from '../api/types';
import styles from './AuthPage.module.scss';

type Props = {
  onAuthenticated: (auth: AuthResponse) => void;
};

export function AuthPage({ onAuthenticated }: Props) {
  const [mode, setMode] = useState<'login' | 'register'>('login');
  const [email, setEmail] = useState('demo@lookator.local');
  const [password, setPassword] = useState('password123');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  async function submit(event: FormEvent) {
    event.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const response = mode === 'login' ? await api.login(email, password) : await api.register(email, password);
      onAuthenticated(response);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Authentication failed');
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className={styles.page}>
      <section className={styles.visual}>
        <div className={styles.map}>
          <span className={styles.pinA} />
          <span className={styles.pinB} />
          <span className={styles.pinC} />
        </div>
      </section>
      <section className={styles.panel}>
        <div className={styles.brand}>
          <Building2 size={26} />
          <span>Lookator</span>
        </div>
        <h1>Location reports for property decisions</h1>
        <form onSubmit={submit} className={styles.form}>
          <label>
            Email
            <input value={email} onChange={(event) => setEmail(event.target.value)} type="email" required />
          </label>
          <label>
            Password
            <input value={password} onChange={(event) => setPassword(event.target.value)} type="password" minLength={8} required />
          </label>
          {error && <p className={styles.error}>{error}</p>}
          <button type="submit" disabled={loading}>
            <LogIn size={18} />
            {loading ? 'Working...' : mode === 'login' ? 'Log in' : 'Create account'}
          </button>
        </form>
        <button className={styles.switcher} type="button" onClick={() => setMode(mode === 'login' ? 'register' : 'login')}>
          {mode === 'login' ? 'Create a new account' : 'Use an existing account'}
        </button>
      </section>
    </main>
  );
}
