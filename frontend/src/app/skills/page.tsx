'use client';

import React, { useState, useEffect } from 'react';
import { getUserSkills, searchCoreSkills, addUserSkill, removeUserSkill, Skill } from '@/services/skills';
import { Search, Plus, Trash2, Award, BookOpen, Check, Loader2, AlertCircle } from 'lucide-react';

export default function SkillsPage() {
  const [userSkills, setUserSkills] = useState<Skill[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState<Skill[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isSearching, setIsSearching] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Skill form state
  const [selectedSkill, setSelectedSkill] = useState<Skill | null>(null);
  const [skillLevel, setSkillLevel] = useState('BEGINNER');
  const [skillStatus, setSkillStatus] = useState('LEARNING');
  const [isAdding, setIsAdding] = useState(false);

  useEffect(() => {
    loadUserSkills();
  }, []);

  async function loadUserSkills() {
    try {
      const data = await getUserSkills();
      setUserSkills(data);
    } catch {
      setError('Failed to fetch user skills.');
    } finally {
      setIsLoading(false);
    }
  }

  // Trigger search on query change
  useEffect(() => {
    if (searchQuery.trim().length < 2) {
      setSearchResults([]);
      return;
    }
    const delayDebounce = setTimeout(async () => {
      setIsSearching(true);
      try {
        const results = await searchCoreSkills(searchQuery);
        setSearchResults(results);
      } catch {
        // Silent catch
      } finally {
        setIsSearching(false);
      }
    }, 300);

    return () => clearTimeout(delayDebounce);
  }, [searchQuery]);

  const handleSelectSkill = (skill: Skill) => {
    setSelectedSkill(skill);
    setSkillLevel('BEGINNER');
    setSkillStatus('LEARNING');
  };

  const handleSaveSkill = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedSkill) return;
    setIsAdding(true);
    setError(null);

    try {
      await addUserSkill({
        name: selectedSkill.name,
        category: selectedSkill.category || 'Other',
        level: skillLevel,
        status: skillStatus
      });
      setSelectedSkill(null);
      setSearchQuery('');
      setSearchResults([]);
      await loadUserSkills();
    } catch (err: any) {
      setError(err.message || 'Failed to add skill.');
    } finally {
      setIsAdding(false);
    }
  };

  const handleAddCustomSkill = () => {
    setSelectedSkill({
      name: searchQuery.trim(),
      category: 'Other'
    });
    setSkillLevel('BEGINNER');
    setSkillStatus('LEARNING');
  };

  const handleDeleteUserSkill = async (id: number) => {
    if (!confirm('Are you sure you want to remove this skill from your profile?')) return;
    try {
      await removeUserSkill(id);
      await loadUserSkills();
    } catch {
      setError('Failed to delete skill.');
    }
  };

  // Group user skills by category
  const skillsByCategory: Record<string, Skill[]> = {};
  userSkills.forEach((skill) => {
    const cat = skill.category || 'Other';
    if (!skillsByCategory[cat]) {
      skillsByCategory[cat] = [];
    }
    skillsByCategory[cat].push(skill);
  });

  if (isLoading) {
    return (
      <div className="flex h-64 items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent"></div>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 lg:grid-cols-3 gap-8 max-w-7xl mx-auto">
      {/* Left 2 Cols: User Skills List */}
      <div className="lg:col-span-2 space-y-6">
        <div className="rounded-2xl border border-border bg-card p-6 shadow-custom">
          <h2 className="text-xl font-bold text-foreground mb-4">My Tracked Skills</h2>
          
          {userSkills.length === 0 ? (
            <div className="text-center py-12 space-y-3">
              <Award className="mx-auto h-12 w-12 text-muted" />
              <h3 className="font-bold text-foreground">No Skills Added Yet</h3>
              <p className="text-xs text-muted max-w-xs mx-auto">Use the search panel on the right to start adding technical or soft skills to your profile.</p>
            </div>
          ) : (
            <div className="space-y-8">
              {Object.entries(skillsByCategory).map(([category, list]) => (
                <div key={category} className="space-y-3">
                  <h3 className="text-xs font-bold uppercase tracking-wider text-muted border-b border-border pb-1.5">{category}</h3>
                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                    {list.map((us) => (
                      <div 
                        key={us.id} 
                        className="flex items-center justify-between p-3.5 rounded-xl border border-border bg-background hover:shadow-custom transition-all group"
                      >
                        <div className="space-y-1">
                          <p className="font-bold text-sm text-foreground">{us.name}</p>
                          <div className="flex items-center gap-2">
                            <span className="text-[10px] font-medium bg-primary/10 text-primary px-2 py-0.5 rounded">
                              {us.level}
                            </span>
                            <span className={`text-[10px] font-medium px-2 py-0.5 rounded flex items-center gap-0.5 ${
                              us.status === 'ACQUIRED' 
                                ? 'bg-emerald-500/10 text-emerald-500' 
                                : 'bg-amber-500/10 text-amber-500'
                            }`}>
                              {us.status === 'ACQUIRED' ? <Check className="h-3 w-3" /> : <BookOpen className="h-3 w-3" />}
                              {us.status}
                            </span>
                          </div>
                        </div>
                        <button
                          onClick={() => us.id && handleDeleteUserSkill(us.id)}
                          className="h-8 w-8 inline-flex items-center justify-center rounded-lg text-muted hover:text-danger hover:bg-red-500/10 opacity-0 group-hover:opacity-100 transition-all"
                        >
                          <Trash2 className="h-4 w-4" />
                        </button>
                      </div>
                    ))}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* Right 1 Col: Search & Add Panel */}
      <div className="space-y-6">
        <div className="rounded-2xl border border-border bg-card p-6 shadow-custom space-y-4">
          <h3 className="text-lg font-bold text-foreground">Add New Skills</h3>
          
          {/* Search bar */}
          <div className="relative">
            <Search className="absolute left-3 top-3.5 h-4 w-4 text-muted" />
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Search e.g. React, Java..."
              className="block w-full rounded-lg border border-border bg-background pl-10 pr-4 py-3 text-foreground placeholder-muted shadow-sm focus:border-primary focus:outline-none focus:ring-1 focus:ring-primary transition-colors text-sm"
            />
          </div>

          {/* Search results list */}
          <div className="space-y-2 max-h-60 overflow-y-auto">
            {isSearching && (
              <div className="flex justify-center py-4">
                <Loader2 className="h-5 w-5 animate-spin text-primary" />
              </div>
            )}
            
            {searchResults.map((result) => (
              <button
                key={result.id}
                onClick={() => handleSelectSkill(result)}
                className="flex w-full items-center justify-between p-3 rounded-lg border border-border bg-background hover:border-primary/40 hover:bg-accent transition-colors text-left"
              >
                <div>
                  <p className="text-sm font-semibold">{result.name}</p>
                  <p className="text-[10px] text-muted">{result.category}</p>
                </div>
                <Plus className="h-4 w-4 text-muted" />
              </button>
            ))}

            {searchQuery.trim().length >= 2 && searchResults.length === 0 && !isSearching && (
              <button
                onClick={handleAddCustomSkill}
                className="flex w-full items-center justify-between p-3 rounded-lg border border-dashed border-primary/40 bg-primary/5 hover:bg-primary/10 transition-colors text-left"
              >
                <div>
                  <p className="text-sm font-semibold text-primary">Add Custom Skill</p>
                  <p className="text-[10px] text-muted">"{searchQuery.trim()}" is not in catalog</p>
                </div>
                <Plus className="h-4 w-4 text-primary" />
              </button>
            )}
          </div>
        </div>

        {/* Skill customizer modal details */}
        {selectedSkill && (
          <div className="rounded-2xl border border-primary/30 bg-primary/5 p-6 shadow-custom-lg space-y-4 animate-fade-in">
            <h3 className="text-base font-bold text-foreground flex items-center gap-1.5">
              Configure: <span className="text-primary">{selectedSkill.name}</span>
            </h3>
            
            <form onSubmit={handleSaveSkill} className="space-y-4">
              {/* Skill level */}
              <div>
                <label className="block text-xs font-semibold text-foreground mb-1.5">Skill Level</label>
                <div className="grid grid-cols-3 gap-2">
                  {['BEGINNER', 'INTERMEDIATE', 'ADVANCED'].map((level) => (
                    <button
                      key={level}
                      type="button"
                      onClick={() => setSkillLevel(level)}
                      className={`py-1.5 text-[10px] font-bold rounded-lg border transition-all ${
                        skillLevel === level 
                          ? 'border-primary bg-primary text-white' 
                          : 'border-border bg-card hover:bg-accent text-muted'
                      }`}
                    >
                      {level}
                    </button>
                  ))}
                </div>
              </div>

              {/* Learning status */}
              <div>
                <label className="block text-xs font-semibold text-foreground mb-1.5">Learning Status</label>
                <div className="grid grid-cols-2 gap-2">
                  {[
                    { name: 'LEARNING', icon: BookOpen },
                    { name: 'ACQUIRED', icon: Check }
                  ].map((status) => {
                    const Icon = status.icon;
                    return (
                      <button
                        key={status.name}
                        type="button"
                        onClick={() => setSkillStatus(status.name)}
                        className={`py-2 text-[10px] font-bold rounded-lg border flex items-center justify-center gap-1.5 transition-all ${
                          skillStatus === status.name 
                            ? 'border-primary bg-primary text-white' 
                            : 'border-border bg-card hover:bg-accent text-muted'
                        }`}
                      >
                        <Icon className="h-3.5 w-3.5" />
                        {status.name}
                      </button>
                    );
                  })}
                </div>
              </div>

              {/* Modal controls */}
              <div className="flex gap-2.5 pt-2">
                <button
                  type="submit"
                  disabled={isAdding}
                  className="flex-1 bg-primary hover:bg-primary-hover text-white text-xs font-bold py-2.5 rounded-lg shadow-custom flex items-center justify-center gap-1.5 transition-colors cursor-pointer"
                >
                  {isAdding && <Loader2 className="h-3.5 w-3.5 animate-spin" />}
                  Add Skill
                </button>
                <button
                  type="button"
                  onClick={() => setSelectedSkill(null)}
                  className="flex-1 bg-card border border-border hover:bg-accent text-foreground text-xs font-bold py-2.5 rounded-lg transition-colors"
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        )}
      </div>
    </div>
  );
}
