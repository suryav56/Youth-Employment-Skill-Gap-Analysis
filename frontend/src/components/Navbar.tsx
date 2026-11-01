'use client';

import React, { useState } from 'react';
import { useAuth } from '@/context/AuthContext';
import { Menu, User, LogOut, Bell, ChevronDown } from 'lucide-react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';

export default function Navbar({ onMenuClick }: { onMenuClick: () => void }) {
  const { user, logout } = useAuth();
  const [isUserDropdownOpen, setIsUserDropdownOpen] = useState(false);
  const pathname = usePathname();

  // Compute a user-friendly page header based on route
  const getHeaderTitle = () => {
    switch (pathname) {
      case '/dashboard': return 'Dashboard';
      case '/profile': return 'My Profile';
      case '/skills': return 'Skills Manager';
      case '/assessment': return 'Skill Assessment';
      case '/gap-analysis': return 'Skill Gap Analysis';
      case '/courses': return 'Recommended Courses';
      default: return 'Skill Gap Portal';
    }
  };

  return (
    <header className="flex h-16 items-center justify-between border-b border-border bg-card px-6 shadow-custom">
      <div className="flex items-center gap-4">
        {/* Mobile menu trigger */}
        <button
          onClick={onMenuClick}
          className="inline-flex h-10 w-10 items-center justify-center rounded-lg border border-border text-foreground hover:bg-accent md:hidden transition-colors"
        >
          <Menu className="h-5 w-5" />
        </button>
        <h1 className="text-xl font-bold text-foreground tracking-tight">{getHeaderTitle()}</h1>
      </div>

      <div className="flex items-center gap-4">
        {/* Notifications button */}
        <button className="relative flex h-10 w-10 items-center justify-center rounded-lg border border-border text-muted hover:bg-accent hover:text-foreground transition-colors">
          <Bell className="h-5 w-5" />
          <span className="absolute top-2 right-2 h-2.5 w-2.5 rounded-full bg-danger ring-2 ring-card"></span>
        </button>

        {/* User Account Menu */}
        <div className="relative">
          <button
            onClick={() => setIsUserDropdownOpen(!isUserDropdownOpen)}
            className="flex items-center gap-2 rounded-lg border border-border bg-card p-1.5 hover:bg-accent transition-colors"
          >
            <div className="flex h-8 w-8 items-center justify-center rounded-md bg-primary text-sm font-semibold text-white">
              {user?.username ? user.username.substring(0, 2).toUpperCase() : 'US'}
            </div>
            <div className="hidden text-left md:block">
              <p className="text-xs font-semibold leading-3 text-foreground">{user?.username}</p>
              <p className="text-[10px] text-muted">{user?.role?.replace('ROLE_', '')}</p>
            </div>
            <ChevronDown className="h-4 w-4 text-muted" />
          </button>

          {isUserDropdownOpen && (
            <>
              {/* Overlay blocker */}
              <div 
                className="fixed inset-0 z-10" 
                onClick={() => setIsUserDropdownOpen(false)}
              />
              <div className="absolute right-0 mt-2 w-48 origin-top-right rounded-lg border border-border bg-card p-1 shadow-custom-lg z-20 animate-fade-in">
                <Link
                  href="/profile"
                  onClick={() => setIsUserDropdownOpen(false)}
                  className="flex w-full items-center gap-2 rounded-md px-3 py-2 text-sm text-foreground hover:bg-accent transition-colors"
                >
                  <User className="h-4 w-4" />
                  My Profile
                </Link>
                <hr className="my-1 border-border" />
                <button
                  onClick={() => {
                    setIsUserDropdownOpen(false);
                    logout();
                  }}
                  className="flex w-full items-center gap-2 rounded-md px-3 py-2 text-sm text-danger hover:bg-red-500/10 transition-colors"
                >
                  <LogOut className="h-4 w-4" />
                  Sign Out
                </button>
              </div>
            </>
          )}
        </div>
      </div>
    </header>
  );
}
