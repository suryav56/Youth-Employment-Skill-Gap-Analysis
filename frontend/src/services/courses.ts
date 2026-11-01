import { apiRequest } from './api';

export interface Course {
  id?: number; // UserCourse enrollment ID
  courseId: number; // Core Course ID
  title: string;
  provider: string;
  url: string;
  category: string;
  difficulty: string;
  status?: string; // ENROLLED, IN_PROGRESS, COMPLETED
}

export async function getCourseCatalog(): Promise<Course[]> {
  return apiRequest('/api/courses/catalog');
}

export async function getUserCourses(): Promise<Course[]> {
  return apiRequest('/api/courses');
}

export async function enrollInCourse(courseId: number): Promise<Course> {
  return apiRequest(`/api/courses/enroll/${courseId}`, {
    method: 'POST',
  });
}

export async function updateCourseStatus(courseId: number, status: 'ENROLLED' | 'IN_PROGRESS' | 'COMPLETED'): Promise<Course> {
  return apiRequest(`/api/courses/progress/${courseId}?status=${status}`, {
    method: 'PUT',
  });
}

export async function getRecommendedCourses(): Promise<Course[]> {
  return apiRequest('/api/courses/recommendations');
}
