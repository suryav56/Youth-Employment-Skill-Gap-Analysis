import { apiRequest } from './api';

export async function login(username: string, password: String) {
  const data = await apiRequest('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username, password }),
  });
  
  if (data.token && !data.token.startsWith('User') && !data.token.startsWith('Invalid')) {
    localStorage.setItem('token', data.token);
    localStorage.setItem('user', JSON.stringify({
      username: data.username,
      email: data.email,
      role: data.role
    }));
    window.dispatchEvent(new Event('auth-change'));
    return data;
  } else {
    throw new Error(data.token || 'Login failed');
  }
}

export async function register(username: string, email: string, password: String) {
  const data = await apiRequest('/api/auth/register', {
    method: 'POST',
    body: JSON.stringify({ username, email, password }),
  });

  if (data.token && !data.token.startsWith('Username') && !data.token.startsWith('Email')) {
    localStorage.setItem('token', data.token);
    localStorage.setItem('user', JSON.stringify({
      username: data.username,
      email: data.email,
      role: data.role
    }));
    window.dispatchEvent(new Event('auth-change'));
    return data;
  } else {
    throw new Error(data.token || 'Registration failed');
  }
}

export function logout() {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
  window.dispatchEvent(new Event('auth-change'));
}

export function getCurrentUser() {
  if (typeof window === 'undefined') return null;
  const user = localStorage.getItem('user');
  return user ? JSON.parse(user) : null;
}
