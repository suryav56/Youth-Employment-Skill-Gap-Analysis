import { apiRequest } from './api';

export interface Recommendation {
  id: number;
  analysis: string;
  recommendedCareers: string;
  recommendedSkills: string;
  learningPath: string; // Markdown content
  createdAt: string;
}

export interface GapItem {
  skill: string;
  required: boolean;
  status: 'acquired' | 'learning' | 'missing';
  level: string;
  matchScore: number; // 0, 50, or 100
}

export async function getLatestRecommendation(): Promise<Recommendation> {
  return apiRequest('/api/recommendations');
}

export async function generateNewRecommendation(): Promise<Recommendation> {
  return apiRequest('/api/recommendations/generate', {
    method: 'POST',
  });
}

export async function getGapAnalysis(): Promise<GapItem[]> {
  return apiRequest('/api/recommendations/gap');
}
