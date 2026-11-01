import { apiRequest } from './api';

export interface Question {
  id: number;
  questionText: string;
  optionA: string;
  optionB: string;
  optionC: string;
  optionD: string;
  category: string;
  difficulty: string;
}

export interface AssessmentAnswer {
  questionId: number;
  selectedOption: string; // A, B, C, D
}

export interface AssessmentResult {
  id: number;
  score: number;
  details: string; // JSON String representing category breakdown
  takenAt: string;
}

export async function getAssessmentQuestions(): Promise<Question[]> {
  return apiRequest('/api/assessments/questions');
}

export async function submitAssessment(answers: AssessmentAnswer[]): Promise<AssessmentResult> {
  return apiRequest('/api/assessments/submit', {
    method: 'POST',
    body: JSON.stringify({ answers }),
  });
}

export async function getAssessmentHistory(): Promise<AssessmentResult[]> {
  return apiRequest('/api/assessments/history');
}
