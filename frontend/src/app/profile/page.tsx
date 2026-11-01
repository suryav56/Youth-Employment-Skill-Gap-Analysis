'use client';

import React, { useState, useEffect } from 'react';
import { getProfile, updateProfile, Profile } from '@/services/profile';
import { useAuth } from '@/context/AuthContext';
import { User, Mail, GraduationCap, Briefcase, Target, FileText, CheckCircle2, Loader2, AlertCircle } from 'lucide-react';

export default function ProfilePage() {
  const { user } = useAuth();
  const [profile, setProfile] = useState<Profile | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    async function loadProfile() {
      try {
        const data = await getProfile();
        setProfile(data);
      } catch (err: any) {
        setError('Failed to fetch profile details.');
      } finally {
        setIsLoading(false);
      }
    }
    loadProfile();
  }, []);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    if (!profile) return;
    setProfile({
      ...profile,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!profile) return;
    setError(null);
    setSuccess(false);
    setIsSaving(true);

    try {
      const updated = await updateProfile(profile);
      setProfile(updated);
      setSuccess(true);
      window.scrollTo({ top: 0, behavior: 'smooth' });
    } catch (err: any) {
      setError(err.message || 'Failed to update profile.');
    } finally {
      setIsSaving(false);
    }
  };

  if (isLoading) {
    return (
      <div className="flex h-64 items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent"></div>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      {/* Alert Notices */}
      {success && (
        <div className="flex items-center gap-2.5 rounded-xl border border-success/30 bg-success/10 p-4 text-sm text-success-foreground animate-fade-in">
          <CheckCircle2 className="h-5 w-5 text-success shrink-0" />
          <p className="font-semibold">Profile updated successfully! Dashboard stats recalculated.</p>
        </div>
      )}

      {error && (
        <div className="flex items-center gap-2.5 rounded-xl border border-danger/30 bg-danger/10 p-4 text-sm text-danger-foreground animate-fade-in">
          <AlertCircle className="h-5 w-5 text-danger shrink-0" />
          <p className="font-semibold">{error}</p>
        </div>
      )}

      <div className="rounded-2xl border border-border bg-card shadow-custom overflow-hidden">
        {/* Header Block */}
        <div className="bg-gradient-to-r from-primary to-indigo-600 px-6 py-8 text-white flex flex-col sm:flex-row items-center gap-4">
          <div className="flex h-16 w-16 items-center justify-center rounded-2xl bg-white/10 text-2xl font-black border border-white/20">
            {user?.username ? user.username.substring(0, 2).toUpperCase() : 'US'}
          </div>
          <div className="text-center sm:text-left space-y-1">
            <h2 className="text-xl font-bold">{profile?.fullName || user?.username}</h2>
            <p className="text-sm text-white/80 flex items-center justify-center sm:justify-start gap-1">
              <Mail className="h-4 w-4" /> {profile?.email}
            </p>
          </div>
        </div>

        {/* Edit Form */}
        <form onSubmit={handleSubmit} className="p-6 md:p-8 space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Full Name */}
            <div>
              <label htmlFor="fullName" className="flex items-center gap-2 text-sm font-semibold text-foreground mb-1.5">
                <User className="h-4 w-4 text-muted" />
                Full Name
              </label>
              <input
                id="fullName"
                name="fullName"
                type="text"
                value={profile?.fullName || ''}
                onChange={handleChange}
                className="block w-full rounded-lg border border-border bg-background px-4 py-2.5 text-foreground placeholder-muted shadow-sm focus:border-primary focus:outline-none focus:ring-1 focus:ring-primary transition-colors text-sm"
                placeholder="Enter your full name"
              />
            </div>

            {/* Target Career Goal */}
            <div>
              <label htmlFor="careerGoal" className="flex items-center gap-2 text-sm font-semibold text-foreground mb-1.5">
                <Target className="h-4 w-4 text-muted" />
                Target Career Goal
              </label>
              <select
                id="careerGoal"
                name="careerGoal"
                value={profile?.careerGoal || ''}
                onChange={handleChange}
                className="block w-full rounded-lg border border-border bg-background px-4 py-2.5 text-foreground shadow-sm focus:border-primary focus:outline-none focus:ring-1 focus:ring-primary transition-colors text-sm"
              >
                <option value="">Select Target Career Goal</option>
                <option value="Frontend Developer">Frontend Developer</option>
                <option value="Backend Developer">Backend Developer</option>
                <option value="Full Stack Developer">Full Stack Developer</option>
                <option value="Data Scientist">Data Scientist</option>
                <option value="Project Manager">Project Manager</option>
              </select>
            </div>

            {/* Education */}
            <div className="md:col-span-2">
              <label htmlFor="education" className="flex items-center gap-2 text-sm font-semibold text-foreground mb-1.5">
                <GraduationCap className="h-4 w-4 text-muted" />
                Education / Degree / University
              </label>
              <input
                id="education"
                name="education"
                type="text"
                value={profile?.education || ''}
                onChange={handleChange}
                className="block w-full rounded-lg border border-border bg-background px-4 py-2.5 text-foreground placeholder-muted shadow-sm focus:border-primary focus:outline-none focus:ring-1 focus:ring-primary transition-colors text-sm"
                placeholder="e.g. Bachelor of Science in Computer Science - ABC University"
              />
            </div>

            {/* Experience */}
            <div className="md:col-span-2">
              <label htmlFor="experience" className="flex items-center gap-2 text-sm font-semibold text-foreground mb-1.5">
                <Briefcase className="h-4 w-4 text-muted" />
                Work Experience
              </label>
              <textarea
                id="experience"
                name="experience"
                rows={4}
                value={profile?.experience || ''}
                onChange={handleChange}
                className="block w-full rounded-lg border border-border bg-background px-4 py-2.5 text-foreground placeholder-muted shadow-sm focus:border-primary focus:outline-none focus:ring-1 focus:ring-primary transition-colors text-sm"
                placeholder="Detail your previous internships, projects, or professional positions..."
              />
            </div>

            {/* Bio */}
            <div className="md:col-span-2">
              <label htmlFor="bio" className="flex items-center gap-2 text-sm font-semibold text-foreground mb-1.5">
                <FileText className="h-4 w-4 text-muted" />
                Short Bio
              </label>
              <textarea
                id="bio"
                name="bio"
                rows={3}
                value={profile?.bio || ''}
                onChange={handleChange}
                className="block w-full rounded-lg border border-border bg-background px-4 py-2.5 text-foreground placeholder-muted shadow-sm focus:border-primary focus:outline-none focus:ring-1 focus:ring-primary transition-colors text-sm"
                placeholder="Write a brief professional summary about yourself..."
              />
            </div>
          </div>

          {/* Form Action */}
          <div className="flex justify-end pt-4 border-t border-border">
            <button
              type="submit"
              disabled={isSaving}
              className="flex items-center gap-2 rounded-lg bg-primary px-6 py-2.5 text-sm font-semibold text-white shadow-custom hover:bg-primary-hover focus:outline-none focus:ring-2 focus:ring-primary disabled:opacity-50 transition-all cursor-pointer"
            >
              {isSaving ? (
                <>
                  <Loader2 className="h-4 w-4 animate-spin" />
                  Saving Profile...
                </>
              ) : (
                'Save Changes'
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
