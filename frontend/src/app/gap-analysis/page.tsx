'use client';

import React, { useState, useEffect } from 'react';
import { getLatestRecommendation, generateNewRecommendation, getGapAnalysis, Recommendation, GapItem } from '@/services/recommendations';
import { 
  LineChart, 
  RefreshCw, 
  CheckCircle2, 
  BookOpen, 
  HelpCircle, 
  BrainCircuit, 
  AlertCircle,
  TrendingUp,
  Compass
} from 'lucide-react';

export default function GapAnalysisPage() {
  const [recommendation, setRecommendation] = useState<Recommendation | null>(null);
  const [gapItems, setGapItems] = useState<GapItem[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isRegenerating, setIsRegenerating] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadData();
  }, []);

  async function loadData() {
    try {
      const rec = await getLatestRecommendation();
      setRecommendation(rec);
      const gap = await getGapAnalysis();
      setGapItems(gap);
    } catch {
      setError('Could not retrieve gap analysis records. Complete your profile details first.');
    } finally {
      setIsLoading(false);
    }
  }

  const handleRegenerate = async () => {
    setIsRegenerating(true);
    setError(null);
    try {
      const rec = await generateNewRecommendation();
      setRecommendation(rec);
      const gap = await getGapAnalysis();
      setGapItems(gap);
    } catch {
      setError('Failed to regenerate AI roadmap. Verify your Groq connection.');
    } finally {
      setIsRegenerating(false);
    }
  };

  // Simple, elegant parser to render markdown details cleanly
  const renderMarkdown = (text: string) => {
    if (!text) return null;
    const lines = text.split('\n');
    return lines.map((line, idx) => {
      const trimmed = line.trim();
      
      // Headers
      if (trimmed.startsWith('###')) {
        return <h4 key={idx} className="text-base font-bold text-foreground mt-4 mb-2">{trimmed.replace('###', '').trim()}</h4>;
      }
      if (trimmed.startsWith('####')) {
        return <h5 key={idx} className="text-sm font-bold text-primary mt-3 mb-1.5">{trimmed.replace('####', '').trim()}</h5>;
      }
      if (trimmed.startsWith('##')) {
        return <h3 key={idx} className="text-lg font-black text-foreground mt-6 mb-3 border-b border-border pb-1">{trimmed.replace('##', '').trim()}</h3>;
      }
      if (trimmed.startsWith('#')) {
        return <h2 key={idx} className="text-xl font-black text-foreground mt-6 mb-4">{trimmed.replace('#', '').trim()}</h2>;
      }

      // Horizontal line
      if (trimmed === '---') {
        return <hr key={idx} className="my-6 border-border" />;
      }

      // List items
      if (trimmed.startsWith('*') || trimmed.startsWith('-')) {
        const content = trimmed.substring(1).trim();
        return (
          <ul key={idx} className="list-disc pl-6 space-y-1 my-1">
            <li className="text-sm text-foreground/90">{parseInlineFormatting(content)}</li>
          </ul>
        );
      }

      // Regular paragraph
      if (trimmed.length > 0) {
        return <p key={idx} className="text-sm text-foreground/80 leading-relaxed my-2">{parseInlineFormatting(trimmed)}</p>;
      }

      return <div key={idx} className="h-2" />;
    });
  };

  // Helper to parse bold (**text**)
  const parseInlineFormatting = (text: string) => {
    const parts = text.split('**');
    return parts.map((part, index) => {
      if (index % 2 === 1) {
        return <strong key={index} className="font-bold text-foreground">{part}</strong>;
      }
      return part;
    });
  };

  if (isLoading) {
    return (
      <div className="flex h-64 items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent"></div>
      </div>
    );
  }

  // Calculate Match Score
  const requiredItems = gapItems.filter(i => i.required);
  const acquiredRequired = requiredItems.filter(i => i.status === 'acquired').length;
  const matchPercentage = requiredItems.length > 0 ? Math.round((acquiredRequired * 100) / requiredItems.length) : 0;

  return (
    <div className="space-y-8 max-w-7xl mx-auto">
      {error && (
        <div className="flex items-center gap-2.5 rounded-xl border border-red-500/30 bg-red-500/10 p-4 text-sm text-red-500 animate-fade-in">
          <AlertCircle className="h-5 w-5 shrink-0" />
          <p className="font-semibold">{error}</p>
        </div>
      )}

      {/* Top Banner */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 bg-card border border-border p-6 rounded-2xl shadow-custom">
        <div className="space-y-1">
          <h2 className="text-xl font-bold flex items-center gap-2">
            <LineChart className="h-6 w-6 text-primary" />
            Skill Gap Audit & AI Advice
          </h2>
          <p className="text-xs text-muted">Audits your current skills list against the target role requirements.</p>
        </div>
        <button
          onClick={handleRegenerate}
          disabled={isRegenerating}
          className="bg-primary hover:bg-primary-hover text-white text-xs font-bold px-4 py-2.5 rounded-lg shadow-custom flex items-center justify-center gap-2 transition-all disabled:opacity-50 cursor-pointer"
        >
          <RefreshCw className={`h-4 w-4 ${isRegenerating ? 'animate-spin' : ''}`} />
          {isRegenerating ? 'Analyzing...' : 'Regenerate AI Analysis'}
        </button>
      </div>

      {/* Audit Matrix & Visual Match */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Visual matching gauge (1 Col) */}
        <div className="rounded-2xl border border-border bg-card p-6 shadow-custom flex flex-col items-center justify-center text-center space-y-6">
          <h3 className="text-sm font-bold text-foreground self-start">Overall Match Index</h3>
          
          <div className="relative inline-flex items-center justify-center">
            {/* Custom SVG Circular gauge */}
            <svg className="h-36 w-36 transform -rotate-90">
              <circle
                cx="72"
                cy="72"
                r="64"
                stroke="var(--border)"
                strokeWidth="10"
                fill="transparent"
                className="text-border"
              />
              <circle
                cx="72"
                cy="72"
                r="64"
                stroke="var(--primary)"
                strokeWidth="10"
                fill="transparent"
                strokeDasharray={402}
                strokeDashoffset={402 - (402 * matchPercentage) / 100}
                className="text-primary transition-all duration-1000 ease-out"
              />
            </svg>
            <div className="absolute flex flex-col items-center">
              <span className="text-3xl font-black text-foreground">{matchPercentage}%</span>
              <span className="text-[9px] font-bold text-muted uppercase tracking-wider mt-0.5">Role Fit</span>
            </div>
          </div>

          <div className="space-y-1">
            <p className="text-sm font-bold text-foreground">Skills Alignment Level</p>
            <p className="text-xs text-muted max-w-[200px]">
              {matchPercentage >= 80 
                ? 'Excellent alignment! You possess most of the required skills.' 
                : matchPercentage >= 50 
                ? 'Moderate alignment. Complete courses in missing skills to upgrade.' 
                : 'Initial phase. Study the AI learning path details below.'}
            </p>
          </div>
        </div>

        {/* Skill comparison matrix list (2 Cols) */}
        <div className="lg:col-span-2 rounded-2xl border border-border bg-card p-6 shadow-custom space-y-4">
          <h3 className="text-sm font-bold text-foreground border-b border-border pb-3">Requirements Audit</h3>
          
          <div className="space-y-2.5 max-h-[300px] overflow-y-auto">
            {gapItems.map((item) => (
              <div 
                key={item.skill} 
                className="flex items-center justify-between p-3 rounded-xl border border-border bg-background hover:shadow-custom transition-all"
              >
                <div className="flex items-center gap-3">
                  <div className={`h-2 w-2 rounded-full ${
                    item.status === 'acquired' 
                      ? 'bg-emerald-500' 
                      : item.status === 'learning' 
                      ? 'bg-amber-500' 
                      : 'bg-red-500'
                  }`} />
                  <div>
                    <p className="text-sm font-bold text-foreground">{item.skill}</p>
                    <p className="text-[10px] text-muted">{item.required ? 'Role Requirement' : 'Extra Skill'}</p>
                  </div>
                </div>

                <div className="flex items-center gap-3">
                  {item.level !== 'NONE' && (
                    <span className="text-[9px] font-bold bg-accent text-muted px-2 py-0.5 rounded">
                      {item.level}
                    </span>
                  )}
                  <span className={`text-[10px] font-bold px-3 py-1 rounded flex items-center gap-1 uppercase ${
                    item.status === 'acquired' 
                      ? 'bg-emerald-500/10 text-emerald-500' 
                      : item.status === 'learning' 
                      ? 'bg-amber-500/10 text-amber-500' 
                      : 'bg-red-500/10 text-red-500'
                  }`}>
                    {item.status === 'acquired' ? <CheckCircle2 className="h-3.5 w-3.5" /> : item.status === 'learning' ? <BookOpen className="h-3.5 w-3.5" /> : <HelpCircle className="h-3.5 w-3.5" />}
                    {item.status}
                  </span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* AI Roadmap learning path details */}
      <div className="rounded-2xl border border-border bg-card p-6 md:p-8 shadow-custom space-y-6">
        <div className="flex items-center gap-2.5 border-b border-border pb-4">
          <BrainCircuit className="h-6 w-6 text-primary animate-float" />
          <div>
            <h3 className="text-lg font-bold text-foreground">AI Study Roadmap</h3>
            <p className="text-xs text-muted">Weekly study instructions and milestones generated by the Groq Llama-3 AI model.</p>
          </div>
        </div>

        {recommendation ? (
          <div className="prose prose-sm dark:prose-invert max-w-none bg-background/50 border border-border p-6 rounded-xl space-y-3">
            {renderMarkdown(recommendation.learningPath)}
          </div>
        ) : (
          <div className="text-center py-10">
            <p className="text-sm text-muted">No roadmap found. Click the button above to generate a new AI study roadmap.</p>
          </div>
        )}
      </div>
    </div>
  );
}
