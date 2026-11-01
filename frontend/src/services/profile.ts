import { apiRequest } from './api';

export interface Profile {
  fullName: string;
  email: string;
  education: string;
  experience: string;
  careerGoal: string;
  bio: string;
  profileCompleted?: boolean;
}

export interface DashboardStats {
  profileCompletion: number;
  skillsCount: number;
  latestAssessmentScore: number;
  skillMatchPercentage: number;
  careerGoal: string;
}

export async function getProfile(): Promise<Profile> {
  return apiRequest('/api/users/profile');
}

export async function updateProfile(profile: Profile): Promise<Profile> {
  return apiRequest('/api/users/profile', {
    method: 'PUT',
    body: JSON.stringify(profile),
  });
}

export async function getDashboardStats(): Promise<DashboardStats> {
  return apiRequest('/api/users/dashboard');
}
