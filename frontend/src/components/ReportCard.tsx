import type { LocationAnalysis } from '../api/types';
import styles from './ReportCard.module.scss';

type Props = {
  analysis: LocationAnalysis;
};

export function ReportCard({ analysis }: Props) {
  return (
    <article className={styles.report}>
      <div className={styles.summary}>
        <div>
          <span>Location score</span>
          <strong>{analysis.score}</strong>
        </div>
        <div className={styles.breakdown}>
          {Object.entries(analysis.breakdown).map(([key, value]) => (
            <label key={key}>
              <span>{key}</span>
              <meter min="0" max="100" value={value} />
              <b>{value}</b>
            </label>
          ))}
        </div>
      </div>

      <div className={styles.metrics}>
        {analysis.metrics.map((metric) => (
          <div key={metric.type}>
            <span>{metric.label}</span>
            <strong>{formatValue(metric.value, metric.unit)}</strong>
          </div>
        ))}
      </div>

      <div className={styles.notes}>
        <section>
          <h3>Strengths</h3>
          {analysis.strengths.map((item) => <p key={item}>{item}</p>)}
        </section>
        <section>
          <h3>Weaknesses</h3>
          {analysis.weaknesses.length ? analysis.weaknesses.map((item) => <p key={item}>{item}</p>) : <p>No major weakness detected.</p>}
        </section>
      </div>
    </article>
  );
}

function formatValue(value: number, unit: string) {
  if (unit === 'm2') {
    return `${Math.round(value / 10000)} ha`;
  }
  return value >= 1000 ? `${(value / 1000).toFixed(1)} km` : `${Math.round(value)} m`;
}
