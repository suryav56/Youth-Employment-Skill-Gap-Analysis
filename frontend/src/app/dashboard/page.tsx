'use client';

import React, { useState, useEffect } from 'react';
import { useAuth } from '@/context/AuthContext';
import { getDashboardStats, DashboardStats } from '@/services/profile';
import { getLatestRecommendation, Recommendation } from '@/services/recommendations';
import { 
  UserCheck, 
  Award, 
  TrendingUp, 
  Zap, 
  ArrowRight,
  ClipboardList,
  AlertCircle,
  BookOpen
} from 'lucide-react';
import Link from 'next/link';

export default function DashboardPage() {
  const { user } = useAuth();
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [recommendation, setRecommendation] = useState<Recommendation | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function loadData() {
      try {
        const statsData = await getDashboardStats();
        setStats(statsData);
        
        try {
          const recData = await getLatestRecommendation();
          setRecommendation(recData);
        } catch {
          // No recommendations generated yet
        }
      } catch (err: any) {
        setError('Failed to fetch dashboard metrics. Is the Spring Boot backend running?');
      } finally {
        setIsLoading(false);
      }
    }
    loadData();
  }, []);

  if (isLoading) {
    return (
      <div className="flex h-64 items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent"></div>
      </div>
    );
  }

  if (error || !stats) {
    return (
      <div className="rounded-xl border border-red-500/30 bg-red-500/10 p-6 text-center text-red-500 max-w-xl mx-auto mt-10">
        <AlertCircle className="mx-auto h-12 w-12 mb-3" />
        <h3 className="text-lg font-bold">Connection Refused</h3>
        <p className="text-sm mt-1 mb-4">{error || 'Could not retrieve data from the backend API.'}</p>
        <button 
          onClick={() => window.location.reload()}
          className="bg-red-500 hover:bg-red-600 text-white font-semibold px-4 py-2 rounded-lg text-sm transition-colors"
        >
          Try Again
        </button>
      </div>
    );
  }

  const kpis = [
    {
      name: 'Profile Completion',
      value: `${stats.profileCompletion}%`,
      description: 'Fill details to unlock paths',
      icon: UserCheck,
      color: 'from-blue-500 to-indigo-500',
      progress: stats.profileCompletion,
    },
    {
      name: 'Tracked Skills',
      value: stats.skillsCount,
      description: 'Technical or soft skills',
      icon: Award,
      color: 'from-emerald-500 to-teal-500',
      progress: Math.min(stats.skillsCount * 10, 100), // visual gauge
    },
    {
      name: 'Last Quiz Score',
      value: stats.latestAssessmentScore > 0 ? `${stats.latestAssessmentScore}%` : 'N/A',
      description: 'MCQ assessment metrics',
      icon: ClipboardList,
      color: 'from-amber-500 to-orange-500',
      progress: stats.latestAssessmentScore,
    },
    {
      name: 'Career Skill Match',
      value: `${stats.skillMatchPercentage}%`,
      description: stats.careerGoal ? `Aligned with ${stats.careerGoal}` : 'Set target goal in profile',
      icon: TrendingUp,
      color: 'from-violet-500 to-purple-500',
      progress: stats.skillMatchPercentage,
    },
  ];

  return (
    <div className="space-y-8 max-w-7xl mx-auto">
      {/* Welcome Banner */}
      <div className="relative rounded-2xl bg-gradient-to-r from-primary to-indigo-600 p-6 md:p-8 text-white shadow-custom-lg overflow-hidden">
        <div className="relative z-10 space-y-2 max-w-2xl">
          <h2 className="text-2xl md:text-3xl font-black">Welcome Back, {user?.username}!</h2>
          <p className="text-white/80 text-sm md:text-base">
            {stats.careerGoal 
              ? `You are currently matching ${stats.skillMatchPercentage}% of the requirements for your goal: ${stats.careerGoal}.`
              : 'Add your target career goal in your profile to initialize customized AI assessments and learning pathways.'}
          </p>
          {!stats.careerGoal && (
            <div className="pt-2">
              <Link href="/profile" className="inline-flex items-center gap-1.5 bg-white text-primary font-bold px-4 py-2 rounded-lg text-sm hover:bg-slate-100 transition-colors shadow-custom">
                Set Career Goal
                <ArrowRight className="h-4 w-4" />
              </Link>
            </div>
          )}
        </div>
        <div className="absolute right-0 bottom-0 opacity-10 translate-y-1/4 translate-x-1/4">
          <Zap className="h-96 w-96 text-white" />
        </div>
      </div>

      {/* Profile completion callout */}
      {stats.profileCompletion < 100 && (
        <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 p-4 rounded-xl border border-warning/30 bg-warning/10 text-warning-foreground">
          <div className="flex items-center gap-3">
            <AlertCircle className="h-5 w-5 text-warning shrink-0" />
            <div>
              <p className="font-semibold text-sm">Your profile is only {stats.profileCompletion}% complete</p>
              <p className="text-xs text-muted">Complete details (bio, education, experience, career goal) to receive more customized recommendations.</p>
            </div>
          </div>
          <Link href="/profile" className="text-xs font-bold bg-amber-500 hover:bg-amber-600 text-white px-3.5 py-2 rounded-lg shadow-custom transition-all shrink-0">
            Complete Profile
          </Link>
        </div>
      )}

      {/* KPI Cards Grid */}
      <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
        {kpis.map((kpi) => {
          const Icon = kpi.icon;
          return (
            <div key={kpi.name} className="relative rounded-2xl border border-border bg-card p-6 shadow-custom hover:shadow-custom-lg transition-all duration-300">
              <div className="flex justify-between items-start mb-4">
                <div>
                  <p className="text-xs font-semibold text-muted tracking-wider uppercase">{kpi.name}</p>
                  <p className="text-2xl font-black text-foreground mt-1">{kpi.value}</p>
                </div>
                <div className={`flex h-12 w-12 items-center justify-center rounded-xl bg-gradient-to-br ${kpi.color} text-white shadow-custom`}>
                  <Icon className="h-6 w-6" />
                </div>
              </div>
              <p className="text-xs text-muted mb-4">{kpi.description}</p>
              {/* Progress bar */}
              <div className="h-1.5 w-full rounded-full bg-accent overflow-hidden">
                <div 
                  className={`h-full rounded-full bg-gradient-to-r ${kpi.color} transition-all duration-500`}
                  style={{ width: `${kpi.progress}%` }}
                />
              </div>
            </div>
          );
        })}
      </div>

      {/* Action shortcuts & AI Summary block */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Left 2 cols: AI Insights */}
        <div className="lg:col-span-2 rounded-2xl border border-border bg-card p-6 shadow-custom space-y-6">
          <div className="flex justify-between items-center border-b border-border pb-4">
            <h3 className="text-lg font-bold text-foreground">AI Career Advice Insights</h3>
            {recommendation && (
              <span className="text-[10px] bg-accent px-2 py-1 rounded text-muted font-medium">
                Generated {new Date(recommendation.createdAt).toLocaleDateString()}
              </span>
            )}
          </div>

          {recommendation ? (
            <div className="space-y-4">
              <div className="p-4 rounded-xl bg-accent border border-border">
                <p className="text-xs font-bold text-primary mb-1 uppercase tracking-wider">Overall Analysis</p>
                <p className="text-sm text-foreground leading-relaxed font-medium">{recommendation.analysis}</p>
              </div>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div className="p-4 rounded-xl border border-border">
                  <p className="text-xs font-bold text-muted mb-1 uppercase tracking-wider">Recommended Career Roles</p>
                  <p className="text-sm font-semibold text-foreground">{recommendation.recommendedCareers}</p>
                </div>
                <div className="p-4 rounded-xl border border-border">
                  <p className="text-xs font-bold text-muted mb-1 uppercase tracking-wider">Identified Missing Skills</p>
                  <p className="text-sm font-semibold text-danger">{recommendation.recommendedSkills}</p>
                </div>
              </div>
              <div className="pt-2">
                <Link href="/gap-analysis" className="inline-flex items-center gap-1 text-sm font-bold text-primary hover:text-primary-hover group">
                  View Full Weekly AI Study Roadmap
                  <ArrowRight className="h-4 w-4 group-hover:translate-x-0.5 transition-transform" />
                </Link>
              </div>
            </div>
          ) : (
            <div className="flex flex-col items-center justify-center text-center py-10 space-y-4">
              <div className="flex h-12 w-12 items-center justify-center rounded-full bg-primary/10 text-primary">
                <Zap className="h-6 w-6" />
              </div>
              <div>
                <h4 className="font-bold text-foreground">No AI Roadmap Generated</h4>
                <p className="text-xs text-muted max-w-sm mt-1">Run your first skill gap analysis to generate a personalized timeline roadmap powered by the Groq Llama-3 API.</p>
              </div>
              <Link href="/gap-analysis" className="bg-primary hover:bg-primary-hover text-white text-xs font-bold px-4 py-2.5 rounded-lg shadow-custom transition-all">
                Run Gap Analysis
              </Link>
            </div>
          )}
        </div>

        {/* Right 1 col: Quick actions panel */}
        <div className="rounded-2xl border border-border bg-card p-6 shadow-custom flex flex-col justify-between">
          <div className="space-y-6">
            <h3 className="text-lg font-bold text-foreground border-b border-border pb-4">Portal Quick Actions</h3>
            
            <div className="space-y-3">
              <Link href="/skills" className="flex items-center justify-between p-3 rounded-xl border border-border hover:border-primary/40 hover:bg-accent transition-all group">
                <div className="flex items-center gap-3">
                  <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-emerald-500/10 text-emerald-500">
                    <Award className="h-5 w-5" />
                  </div>
                  <div>
                    <p className="text-sm font-bold">Manage Skills</p>
                    <p className="text-[10px] text-muted">Add, remove, search skills</p>
                  </div>
                </div>
                <ArrowRight className="h-4 w-4 text-muted group-hover:translate-x-0.5 transition-transform" />
              </Link>

              <Link href="/assessment" className="flex items-center justify-between p-3 rounded-xl border border-border hover:border-primary/40 hover:bg-accent transition-all group">
                <div className="flex items-center gap-3">
                  <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-amber-500/10 text-amber-500">
                    <ClipboardList className="h-5 w-5" />
                  </div>
                  <div>
                    <p className="text-sm font-bold">Take Skills Assessment</p>
                    <p className="text-[10px] text-muted">Grade your technical knowledge</p>
                  </div>
                </div>
                <ArrowRight className="h-4 w-4 text-muted group-hover:translate-x-0.5 transition-transform" />
              </Link>

              <Link href="/courses" className="flex items-center justify-between p-3 rounded-xl border border-border hover:border-primary/40 hover:bg-accent transition-all group">
                <div className="flex items-center gap-3">
                  <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-violet-500/10 text-violet-500">
                    <BookOpen className="h-5 w-5" />
                  </div>
                  <div>
                    <p className="text-sm font-bold">Study Courses</p>
                    <p className="text-[10px] text-muted">Acquire missing skills via courses</p>
                  </div>
                </div>
                <ArrowRight className="h-4 w-4 text-muted group-hover:translate-x-0.5 transition-transform" />
              </Link>
            </div>
          </div>

          <div className="pt-6 border-t border-border mt-6">
            <p className="text-[10px] text-muted text-center">Version 1.0.0 (Production Build ready for Vercel)</p>
          </div>
        </div>
      </div>
    </div>
  );
}
