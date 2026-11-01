'use client';

import React from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { 
  LayoutDashboard, 
  User, 
  Award, 
  ClipboardList, 
  LineChart, 
  BookOpen, 
  X,
  Compass
} from 'lucide-react';

interface SidebarProps {
  isOpen: boolean;
  onClose: () => void;
}

export default function Sidebar({ isOpen, onClose }: SidebarProps) {
  const pathname = usePathname();

  const navigation = [
    { name: 'Dashboard', href: '/dashboard', icon: LayoutDashboard },
    { name: 'Profile', href: '/profile', icon: User },
    { name: 'Skills Manager', href: '/skills', icon: Award },
    { name: 'Skills Quiz', href: '/assessment', icon: ClipboardList },
    { name: 'Gap Analysis', href: '/gap-analysis', icon: LineChart },
    { name: 'Recommended Courses', href: '/courses', icon: BookOpen },
  ];

  const sidebarContent = (
    <div className="flex h-full flex-col border-r border-border bg-card shadow-custom z-20">
      {/* Brand Header */}
      <div className="flex h-16 items-center justify-between px-6 border-b border-border bg-card">
        <Link href="/dashboard" className="flex items-center gap-2" onClick={onClose}>
          <Compass className="h-6 w-6 text-primary animate-float" />
          <span className="text-lg font-bold text-foreground tracking-wider bg-gradient-to-r from-primary to-indigo-400 bg-clip-text text-transparent">
            SKILL GAP
          </span>
        </Link>
        {/* Mobile close button */}
        <button
          onClick={onClose}
          className="rounded-lg p-1.5 text-muted hover:bg-accent hover:text-foreground md:hidden transition-colors"
        >
          <X className="h-5 w-5" />
        </button>
      </div>

      {/* Navigation Links */}
      <nav className="flex-1 space-y-1.5 px-4 py-6 overflow-y-auto">
        {navigation.map((item) => {
          const isActive = pathname === item.href;
          const Icon = item.icon;
          return (
            <Link
              key={item.name}
              href={item.href}
              onClick={onClose}
              className={`flex items-center gap-3 rounded-lg px-4 py-3 text-sm font-medium transition-all duration-200 ${
                isActive
                  ? 'bg-primary text-white shadow-custom'
                  : 'text-muted hover:bg-accent hover:text-foreground'
              }`}
            >
              <Icon className={`h-5 w-5 ${isActive ? 'text-white' : 'text-muted'}`} />
              {item.name}
            </Link>
          );
        })}
      </nav>
    </div>
  );

  return (
    <>
      {/* Sidebar for Desktop */}
      <aside className="hidden md:block md:w-64 h-full">
        {sidebarContent}
      </aside>

      {/* Sidebar Drawer for Mobile */}
      {isOpen && (
        <div className="fixed inset-0 z-40 flex md:hidden">
          {/* Backdrop overlay */}
          <div 
            className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm transition-opacity" 
            onClick={onClose}
          />
          <div className="relative flex w-64 max-w-xs flex-1 flex-col animate-fade-in">
            {sidebarContent}
          </div>
        </div>
      )}
    </>
  );
}
