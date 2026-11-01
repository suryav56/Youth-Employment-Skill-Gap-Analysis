import { apiRequest } from './api';

export interface Skill {
  id?: number;
  skillId?: number;
  name: string;
  category: string;
  level?: string; // BEGINNER, INTERMEDIATE, ADVANCED
  status?: string; // LEARNING, ACQUIRED
}

export async function getUserSkills(): Promise<Skill[]> {
  return apiRequest('/api/skills');
}

export async function getCoreSkills(): Promise<Skill[]> {
  return apiRequest('/api/skills/catalog');
}

export async function searchCoreSkills(query: string): Promise<Skill[]> {
  return apiRequest(`/api/skills/search?query=${encodeURIComponent(query)}`);
}

export async function addUserSkill(skill: Skill): Promise<Skill> {
  return apiRequest('/api/skills', {
    method: 'POST',
    body: JSON.stringify(skill),
  });
}

export async function removeUserSkill(userSkillId: number): Promise<void> {
  return apiRequest(`/api/skills/${userSkillId}`, {
    method: 'DELETE',
  });
}
