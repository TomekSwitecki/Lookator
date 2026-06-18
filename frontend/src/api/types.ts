export type AuthResponse = {
  token: string;
  userId: string;
  email: string;
};

export type ScoreBreakdown = {
  transport: number;
  amenities: number;
  green: number;
  nuisances: number;
};

export type Metric = {
  type: string;
  value: number;
  unit: string;
  label: string;
  positiveWhenLower: boolean;
};

export type LocationAnalysis = {
  id: string;
  address?: string;
  latitude: number;
  longitude: number;
  score: number;
  breakdown: ScoreBreakdown;
  metrics: Metric[];
  strengths: string[];
  weaknesses: string[];
  createdAt: string;
};

export type Comparison = {
  id: string;
  locations: Array<{
    analysisId: string;
    latitude: number;
    longitude: number;
    score: number;
    strengths: string[];
    weaknesses: string[];
  }>;
  keyDifferences: string[];
  createdAt: string;
};
