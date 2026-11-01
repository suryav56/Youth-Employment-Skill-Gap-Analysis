'use client';

import React, { useState } from 'react';
import { useAuth } from '@/context/AuthContext';
import Navbar from './Navbar';
import Sidebar from './Sidebar';
import { usePathname, useRouter } from 'next/navigation';

export default function ClientLayout({ children }: { children: React.ReactNode }) {
  const { isAuthenticated, isLoading } = useAuth();
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const pathname = usePathname();
  const router = useRouter();

  // Pages that do NOT show the navbar/sidebar (unauthenticated routes)
  const isAuthPage = pathname === '/login' || pathname === '/register';
  const isLandingPage = pathname === '/';
  
  const showDashboardLayout = isAuthenticated && !isAuthPage && !isLandingPage;

  React.useEffect(() => {
    if (!isLoading && !isAuthenticated && !isAuthPage && !isLandingPage) {
      router.push('/login');
    }
  }, [isLoading, isAuthenticated, isAuthPage, isLandingPage, router]);

  if (isLoading) {
    return (
      <div className="flex h-screen w-screen items-center justify-center bg-background text-foreground">
        <div className="flex flex-col items-center gap-4">
          <div className="h-10 w-10 animate-spin rounded-full border-4 border-primary border-t-transparent"></div>
          <p className="text-sm font-medium text-muted">Loading Skill Gap Portal...</p>
        </div>
      </div>
    );
  }

  if (showDashboardLayout) {
    return (
      <div className="flex h-screen w-screen overflow-hidden bg-background text-foreground">
        {/* Sidebar for Desktop */}
        <Sidebar isOpen={isSidebarOpen} onClose={() => setIsSidebarOpen(false)} />
        
        {/* Main Content Area */}
        <div className="flex flex-1 flex-col overflow-hidden">
          <Navbar onMenuClick={() => setIsSidebarOpen(true)} />
          <main className="flex-1 overflow-y-auto p-4 md:p-8 animate-fade-in">
            {children}
          </main>
        </div>
      </div>
    );
  }

  // Raw layout for landing/login/register pages
  return (
    <div className="min-h-screen bg-background text-foreground">
      {children}
    </div>
  );
}
