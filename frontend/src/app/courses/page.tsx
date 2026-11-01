'use client';

import React, { useState, useEffect } from 'react';
import { getUserCourses, enrollInCourse, updateCourseStatus, getRecommendedCourses, Course } from '@/services/courses';
import { BookOpen, CheckCircle, Clock, ExternalLink, GraduationCap, Plus, Loader2, AlertCircle, ArrowUpRight } from 'lucide-react';

export default function CoursesPage() {
  const [activeCourses, setActiveCourses] = useState<Course[]>([]);
  const [recommendations, setRecommendations] = useState<Course[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isUpdating, setIsUpdating] = useState<number | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadData();
  }, []);

  async function loadData() {
    try {
      const active = await getUserCourses();
      setActiveCourses(active);
      const rec = await getRecommendedCourses();
      setRecommendations(rec);
    } catch {
      setError('Failed to load courses data.');
    } finally {
      setIsLoading(false);
    }
  }

  const handleEnroll = async (courseId: number) => {
    setIsUpdating(courseId);
    setError(null);
    try {
      await enrollInCourse(courseId);
      // Refresh list
      const active = await getUserCourses();
      setActiveCourses(active);
      const rec = await getRecommendedCourses();
      setRecommendations(rec);
    } catch {
      setError('Enrollment failed. Try again.');
    } finally {
      setIsUpdating(null);
    }
  };

  const handleStatusChange = async (courseId: number, status: 'ENROLLED' | 'IN_PROGRESS' | 'COMPLETED') => {
    setIsUpdating(courseId);
    setError(null);
    try {
      await updateCourseStatus(courseId, status);
      const active = await getUserCourses();
      setActiveCourses(active);
    } catch {
      setError('Failed to update course status.');
    } finally {
      setIsUpdating(null);
    }
  };

  if (isLoading) {
    return (
      <div className="flex h-64 items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent"></div>
      </div>
    );
  }

  // Split active courses by status
  const enrolled = activeCourses.filter((c) => c.status === 'ENROLLED');
  const inProgress = activeCourses.filter((c) => c.status === 'IN_PROGRESS');
  const completed = activeCourses.filter((c) => c.status === 'COMPLETED');

  return (
    <div className="space-y-8 max-w-7xl mx-auto">
      {error && (
        <div className="flex items-center gap-2.5 rounded-xl border border-red-500/30 bg-red-500/10 p-4 text-sm text-red-500 animate-fade-in">
          <AlertCircle className="h-5 w-5 shrink-0" />
          <p className="font-semibold">{error}</p>
        </div>
      )}

      {/* Recommended Courses Section */}
      <div className="space-y-4">
        <h2 className="text-xl font-bold text-foreground flex items-center gap-2">
          <GraduationCap className="h-6 w-6 text-primary" />
          Recommended for Your Skill Gaps
        </h2>
        {recommendations.length === 0 ? (
          <p className="text-xs text-muted">All recommended courses completed or enrolled!</p>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {recommendations.map((course) => (
              <div 
                key={course.courseId} 
                className="flex flex-col justify-between p-5 rounded-2xl border border-border bg-card shadow-custom hover:shadow-custom-lg transition-all duration-300 group"
              >
                <div className="space-y-3">
                  <div className="flex justify-between items-start">
                    <span className="text-[10px] font-bold bg-primary/10 text-primary px-2.5 py-1 rounded-full uppercase tracking-wider">
                      {course.category}
                    </span>
                    <span className="text-[10px] font-bold bg-accent text-muted px-2 py-0.5 rounded">
                      {course.difficulty}
                    </span>
                  </div>
                  <h3 className="text-sm font-bold text-foreground group-hover:text-primary transition-colors line-clamp-2 leading-snug">
                    {course.title}
                  </h3>
                  <p className="text-xs text-muted">Provider: <span className="font-bold text-foreground">{course.provider}</span></p>
                </div>

                <div className="flex items-center gap-3 pt-5 border-t border-border mt-5">
                  <button
                    onClick={() => handleEnroll(course.courseId)}
                    disabled={isUpdating === course.courseId}
                    className="flex-1 bg-primary hover:bg-primary-hover disabled:opacity-50 text-white text-xs font-bold py-2 rounded-lg shadow-custom flex items-center justify-center gap-1 transition-colors cursor-pointer"
                  >
                    {isUpdating === course.courseId ? (
                      <Loader2 className="h-3.5 w-3.5 animate-spin" />
                    ) : (
                      <>
                        <Plus className="h-3.5 w-3.5" />
                        Track Progress
                      </>
                    )}
                  </button>
                  <a
                    href={course.url}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="flex-grow-0 p-2 rounded-lg border border-border bg-card hover:bg-accent text-muted hover:text-foreground transition-all flex items-center justify-center"
                    title="Go to course"
                  >
                    <ArrowUpRight className="h-4 w-4" />
                  </a>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Active Tracked Courses Section */}
      <div className="space-y-6 pt-4 border-t border-border">
        <h2 className="text-xl font-bold text-foreground flex items-center gap-2">
          <BookOpen className="h-6 w-6 text-primary" />
          My Tracked Learning Pathway
        </h2>

        {activeCourses.length === 0 ? (
          <div className="rounded-xl border border-dashed border-border p-12 text-center text-muted max-w-md mx-auto">
            <GraduationCap className="mx-auto h-12 w-12 text-muted/60 mb-2" />
            <h4 className="font-bold text-foreground">No active courses</h4>
            <p className="text-xs mt-1">Enroll in recommended courses above to start tracking your weekly learning metrics.</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            {/* 1. Enrolled (To Do) */}
            <div className="space-y-4">
              <h3 className="text-xs font-bold uppercase tracking-wider text-muted flex items-center justify-between border-b border-border pb-1.5">
                <span>To Do (Enrolled)</span>
                <span className="bg-accent text-muted text-[10px] px-2 py-0.5 rounded-full font-bold">{enrolled.length}</span>
              </h3>
              <div className="space-y-3">
                {enrolled.map((course) => (
                  <div key={course.courseId} className="p-4 rounded-xl border border-border bg-card space-y-4 shadow-custom">
                    <div className="space-y-1.5">
                      <h4 className="text-xs font-bold text-foreground line-clamp-1">{course.title}</h4>
                      <p className="text-[10px] text-muted">Platform: {course.provider}</p>
                    </div>
                    <div className="flex gap-2">
                      <button
                        onClick={() => handleStatusChange(course.courseId, 'IN_PROGRESS')}
                        className="flex-1 bg-accent hover:bg-accent-hover text-foreground text-[10px] font-bold py-1.5 rounded transition-colors cursor-pointer"
                      >
                        Start Studying
                      </button>
                      <a
                        href={course.url}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="p-1.5 rounded border border-border text-muted hover:text-foreground hover:bg-accent transition-all flex items-center justify-center"
                      >
                        <ExternalLink className="h-3.5 w-3.5" />
                      </a>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            {/* 2. In Progress */}
            <div className="space-y-4">
              <h3 className="text-xs font-bold uppercase tracking-wider text-amber-500 flex items-center justify-between border-b border-border pb-1.5">
                <span className="flex items-center gap-1.5">
                  <Clock className="h-4 w-4" />
                  In Progress
                </span>
                <span className="bg-amber-500/10 text-amber-500 text-[10px] px-2 py-0.5 rounded-full font-bold">{inProgress.length}</span>
              </h3>
              <div className="space-y-3">
                {inProgress.map((course) => (
                  <div key={course.courseId} className="p-4 rounded-xl border border-amber-500/20 bg-card space-y-4 shadow-custom">
                    <div className="space-y-1.5">
                      <h4 className="text-xs font-bold text-foreground line-clamp-1">{course.title}</h4>
                      <p className="text-[10px] text-muted">Platform: {course.provider}</p>
                    </div>
                    <div className="flex gap-2">
                      <button
                        onClick={() => handleStatusChange(course.courseId, 'COMPLETED')}
                        className="flex-1 bg-primary hover:bg-primary-hover text-white text-[10px] font-bold py-1.5 rounded transition-colors cursor-pointer"
                      >
                        Complete Course
                      </button>
                      <a
                        href={course.url}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="p-1.5 rounded border border-border text-muted hover:text-foreground hover:bg-accent transition-all flex items-center justify-center"
                      >
                        <ExternalLink className="h-3.5 w-3.5" />
                      </a>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            {/* 3. Completed */}
            <div className="space-y-4">
              <h3 className="text-xs font-bold uppercase tracking-wider text-emerald-500 flex items-center justify-between border-b border-border pb-1.5">
                <span className="flex items-center gap-1.5">
                  <CheckCircle className="h-4 w-4" />
                  Completed
                </span>
                <span className="bg-emerald-500/10 text-emerald-500 text-[10px] px-2 py-0.5 rounded-full font-bold">{completed.length}</span>
              </h3>
              <div className="space-y-3">
                {completed.map((course) => (
                  <div key={course.courseId} className="p-4 rounded-xl border border-emerald-500/20 bg-card space-y-3 shadow-custom opacity-80">
                    <div className="space-y-1.5">
                      <h4 className="text-xs font-bold text-foreground line-clamp-1 line-through">{course.title}</h4>
                      <p className="text-[10px] text-muted">Platform: {course.provider}</p>
                    </div>
                    <div className="flex justify-between items-center pt-2 border-t border-border">
                      <span className="text-[9px] font-bold text-emerald-500 uppercase">Passed</span>
                      <a
                        href={course.url}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="p-1 rounded hover:bg-accent text-muted hover:text-foreground transition-all flex items-center justify-center"
                      >
                        <ExternalLink className="h-3.5 w-3.5" />
                      </a>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
