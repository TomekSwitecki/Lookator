import { FormEvent, useEffect, useMemo, useState } from 'react';
import { GitCompare, Loader2, LogOut, MapPin, Search } from 'lucide-react';
import { api } from '../api/client';
import type { AuthResponse, Comparison, LocationAnalysis } from '../api/types';
import { ReportCard } from '../components/ReportCard';
import styles from './Dashboard.module.scss';

type Props = {
  auth: AuthResponse;
  onLogout: () => void;
};

export function Dashboard({ auth, onLogout }: Props) {
  const [address, setAddress] = useState('Plac Defilad 1, Warsaw');
  const [latitude, setLatitude] = useState(52.2319);
  const [longitude, setLongitude] = useState(21.0067);
  const [analyses, setAnalyses] = useState<LocationAnalysis[]>([]);
  const [selected, setSelected] = useState<string[]>([]);
  const [comparison, setComparison] = useState<Comparison | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    api.analyses(auth.token).then(setAnalyses).catch(() => setAnalyses([]));
  }, [auth.token]);

  const latest = analyses[0];
  const canCompare = selected.length >= 2;

  const selectedAnalyses = useMemo(
    () => analyses.filter((analysis) => selected.includes(analysis.id)),
    [analyses, selected]
  );

  async function analyze(event: FormEvent) {
    event.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const result = await api.analyze(auth.token, { address, latitude, longitude });
      setAnalyses((items) => [result, ...items]);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Analysis failed');
    } finally {
      setLoading(false);
    }
  }

  async function compare() {
    setError(null);
    try {
      setComparison(await api.createComparison(auth.token, selected));
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Comparison failed');
    }
  }

  return (
    <main className={styles.page}>
      <header className={styles.header}>
        <div>
          <span className={styles.kicker}>Lookator MVP</span>
          <h1>Location workspace</h1>
        </div>
        <button className={styles.iconButton} onClick={onLogout} title="Log out">
          <LogOut size={18} />
        </button>
      </header>

      <section className={styles.shell}>
        <aside className={styles.sidebar}>
          <form onSubmit={analyze} className={styles.searchForm}>
            <label>
              Address label
              <input value={address} onChange={(event) => setAddress(event.target.value)} />
            </label>
            <div className={styles.coordGrid}>
              <label>
                Latitude
                <input value={latitude} onChange={(event) => setLatitude(Number(event.target.value))} type="number" step="0.0001" />
              </label>
              <label>
                Longitude
                <input value={longitude} onChange={(event) => setLongitude(Number(event.target.value))} type="number" step="0.0001" />
              </label>
            </div>
            <button type="submit" disabled={loading}>
              {loading ? <Loader2 className={styles.spin} size={18} /> : <Search size={18} />}
              Analyze
            </button>
            {error && <p className={styles.error}>{error}</p>}
          </form>

          <div className={styles.savedHeader}>
            <h2>Saved analyses</h2>
            <button disabled={!canCompare} onClick={compare} title="Compare selected">
              <GitCompare size={17} />
              Compare
            </button>
          </div>
          <div className={styles.savedList}>
            {analyses.map((analysis) => (
              <label className={styles.savedItem} key={analysis.id}>
                <input
                  type="checkbox"
                  checked={selected.includes(analysis.id)}
                  onChange={(event) =>
                    setSelected((ids) =>
                      event.target.checked ? [...ids, analysis.id] : ids.filter((id) => id !== analysis.id)
                    )
                  }
                />
                <MapPin size={16} />
                <span>{analysis.address ?? `${analysis.latitude.toFixed(3)}, ${analysis.longitude.toFixed(3)}`}</span>
                <strong>{analysis.score}</strong>
              </label>
            ))}
          </div>
        </aside>

        <section className={styles.content}>
          {latest ? <ReportCard analysis={latest} /> : <div className={styles.empty}>Run an analysis to create the first report.</div>}

          <section className={styles.compareBand}>
            <h2>Comparison</h2>
            {comparison ? (
              <div className={styles.comparison}>
                {comparison.locations.map((location, index) => (
                  <article key={location.analysisId}>
                    <span>Property {String.fromCharCode(65 + index)}</span>
                    <strong>{location.score}/100</strong>
                    <p>{location.strengths[0]}</p>
                  </article>
                ))}
                <ul>
                  {comparison.keyDifferences.map((difference) => (
                    <li key={difference}>{difference}</li>
                  ))}
                </ul>
              </div>
            ) : (
              <div className={styles.selectedPreview}>
                {selectedAnalyses.length ? selectedAnalyses.map((item) => <span key={item.id}>{item.score}/100</span>) : 'Select saved analyses to compare.'}
              </div>
            )}
          </section>
        </section>
      </section>
    </main>
  );
}
