import type { AuthResponse, Comparison, LocationAnalysis } from './types';

const API_BASE = (import.meta.env.VITE_API_BASE ?? '').replace(/\/$/, '');

async function request<T>(path: string, token: string | null, options: RequestInit = {}): Promise<T> {
  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...options.headers
    }
  });

  if (!response.ok) {
    const problem = await response.json().catch(() => ({ detail: response.statusText }));
    throw new Error(problem.detail ?? 'Request failed');
  }

  return response.json();
}

export const api = {
  register: (email: string, password: string) =>
    request<AuthResponse>('/api/v1/auth/register', null, {
      method: 'POST',
      body: JSON.stringify({ email, password })
    }),
  login: (email: string, password: string) =>
    request<AuthResponse>('/api/v1/auth/login', null, {
      method: 'POST',
      body: JSON.stringify({ email, password })
    }),
  analyze: (token: string, payload: { address: string; latitude: number; longitude: number }) =>
    request<LocationAnalysis>('/api/v1/analyses', token, {
      method: 'POST',
      body: JSON.stringify(payload)
    }),
  analyses: (token: string) => request<LocationAnalysis[]>('/api/v1/analyses', token),
  createComparison: (token: string, analysisIds: string[]) =>
    request<Comparison>('/api/v1/comparisons', token, {
      method: 'POST',
      body: JSON.stringify({ analysisIds })
    }),
  comparisons: (token: string) => request<Comparison[]>('/api/v1/comparisons', token)
};
