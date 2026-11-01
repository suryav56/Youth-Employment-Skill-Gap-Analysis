'use client';

import React, { createContext, useContext, useState, useEffect } from 'react';
import { getCurrentUser, login as authLogin, register as authRegister, logout as authLogout } from '@/services/auth';

interface AuthContextType {
  user: { username: string; email: string; role: string } | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (username: string, password: string) => Promise<void>;
  register: (username: string, email: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<{ username: string; email: string; role: string } | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  const refreshUser = () => {
    setUser(getCurrentUser());
    setIsLoading(false);
  };

  useEffect(() => {
    refreshUser();

    // Listen to custom auth-change events
    window.addEventListener('auth-change', refreshUser);
    return () => {
      window.removeEventListener('auth-change', refreshUser);
    };
  }, []);

  const login = async (username: string, password: string) => {
    setIsLoading(true);
    try {
      await authLogin(username, password);
    } finally {
      setIsLoading(false);
    }
  };

  const register = async (username: string, email: string, password: string) => {
    setIsLoading(true);
    try {
      await authRegister(username, email, password);
    } finally {
      setIsLoading(false);
    }
  };

  const logout = () => {
    authLogout();
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated: !!user,
        isLoading,
        login,
        register,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
