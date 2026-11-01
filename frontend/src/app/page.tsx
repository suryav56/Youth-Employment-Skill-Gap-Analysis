import React from 'react';
import Link from 'next/link';
import { Compass, ArrowRight, Award, LineChart, BookOpen, BrainCircuit } from 'lucide-react';

export default function LandingPage() {
  const features = [
    {
      title: 'MCQ Skills Assessment',
      description: 'Test your capabilities against interactive questions mapping to Frontend, Backend, Data Science, and Agile Management categories.',
      icon: Award,
    },
    {
      title: 'AI Gap Analysis & Insights',
      description: 'Synthesize your skill profile and receive detailed analyses showing where you stand relative to industry role benchmarks.',
      icon: LineChart,
    },
    {
      title: 'Groq API Powered Roadmaps',
      description: 'Get custom weekly learning roadmaps from Llama-3 indicating exactly what topics to study and projects to build.',
      icon: BrainCircuit,
    },
    {
      title: 'Curated Course Pathways',
      description: 'Discover and track real course suggestions from Udemy, Coursera, and freeCodeCamp. Track progress directly on your portal.',
      icon: BookOpen,
    },
  ];

  const steps = [
    {
      num: '01',
      title: 'Create Your Account & Set Goals',
      description: 'Register in seconds, fill out your profile details, and state your target career goal (e.g. Frontend Developer).',
    },
    {
      num: '02',
      title: 'Evaluate Your Core Knowledge',
      description: 'Take technical MCQ quizes to calculate your starting strengths. Doing well automatically updates your skill levels.',
    },
    {
      num: '03',
      title: 'Run AI Skill Gap Analysis',
      description: 'Compare your profile against requirements. Groq generates a personalized step-by-step weekly learning path.',
    },
    {
      num: '04',
      title: 'Acquire Skills & Track Success',
      description: 'Enroll in recommended courses, update progress from learning to acquired, and watch your dashboard stats climb.',
    },
  ];

  return (
    <div className="flex flex-col bg-background min-h-screen">
      {/* Header */}
      <header className="flex h-20 items-center justify-between px-6 md:px-12 border-b border-border bg-card/50 backdrop-blur-md sticky top-0 z-50">
        <div className="flex items-center gap-2">
          <Compass className="h-6 w-6 text-primary animate-float" />
          <span className="text-xl font-bold tracking-wider bg-gradient-to-r from-primary to-indigo-400 bg-clip-text text-transparent">
            SKILL GAP
          </span>
        </div>
        <div className="flex items-center gap-4">
          <Link href="/login" className="text-sm font-semibold hover:text-primary transition-colors px-4 py-2">
            Sign In
          </Link>
          <Link href="/register" className="text-sm font-semibold bg-primary hover:bg-primary-hover text-white px-5 py-2.5 rounded-lg shadow-custom transition-all">
            Get Started
          </Link>
        </div>
      </header>

      {/* Hero Section */}
      <section className="relative px-6 py-20 md:py-32 md:px-12 text-center max-w-5xl mx-auto flex flex-col items-center">
        <div className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-primary/10 text-primary text-xs font-semibold uppercase tracking-wider mb-6">
          <BrainCircuit className="h-4 w-4" /> AI-Powered Career Platform
        </div>
        <h1 className="text-4xl md:text-6xl font-extrabold tracking-tight mb-6 leading-tight">
          Close the Skill Gap. <br/>
          <span className="bg-gradient-to-r from-primary to-indigo-500 bg-clip-text text-transparent">
            Unlock Your Dream Career.
          </span>
        </h1>
        <p className="text-lg text-muted max-w-2xl mb-10 leading-relaxed">
          Empowering youth to audit technical capabilities, perform gap analyses, generate customized AI learning paths, and land employment.
        </p>
        <div className="flex flex-col sm:flex-row gap-4 justify-center">
          <Link href="/register" className="flex items-center gap-2 bg-primary hover:bg-primary-hover text-white text-base font-semibold px-8 py-4 rounded-xl shadow-custom-lg transition-all group">
            Analyze Your Skills Now
            <ArrowRight className="h-5 w-5 group-hover:translate-x-1 transition-transform" />
          </Link>
        </div>
      </section>

      {/* Features Grid */}
      <section className="px-6 py-20 bg-card border-y border-border md:px-12">
        <div className="max-w-6xl mx-auto">
          <div className="text-center mb-16">
            <h2 className="text-3xl font-bold tracking-tight mb-4">Core Platform Features</h2>
            <p className="text-muted max-w-xl mx-auto">Everything you need to evaluate, study, and master skills required for modern employment.</p>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
            {features.map((feat) => {
              const Icon = feat.icon;
              return (
                <div key={feat.title} className="flex flex-col p-6 rounded-2xl border border-border bg-background hover:shadow-custom-lg hover:border-primary/30 transition-all duration-300 group">
                  <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-primary/10 text-primary mb-5 group-hover:scale-110 transition-transform">
                    <Icon className="h-6 w-6" />
                  </div>
                  <h3 className="text-lg font-bold mb-3">{feat.title}</h3>
                  <p className="text-sm text-muted leading-relaxed">{feat.description}</p>
                </div>
              );
            })}
          </div>
        </div>
      </section>

      {/* How It Works */}
      <section className="px-6 py-20 md:px-12">
        <div className="max-w-5xl mx-auto">
          <div className="text-center mb-16">
            <h2 className="text-3xl font-bold tracking-tight mb-4">How It Works</h2>
            <p className="text-muted">Four simple steps to verify your path and start learning.</p>
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-8 relative">
            {steps.map((step) => (
              <div key={step.num} className="flex flex-col relative">
                <span className="text-5xl font-black text-primary/15 mb-3">{step.num}</span>
                <h3 className="text-base font-bold mb-2">{step.title}</h3>
                <p className="text-xs text-muted leading-relaxed">{step.description}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="mt-auto border-t border-border bg-card py-12 px-6 text-center text-sm text-muted">
        <div className="max-w-6xl mx-auto flex flex-col sm:flex-row justify-between items-center gap-4">
          <div className="flex items-center gap-2">
            <Compass className="h-5 w-5 text-primary" />
            <span className="font-bold text-foreground">SKILL GAP PORTAL</span>
          </div>
          <p>© 2026 Skill Gap Portal. Prepared for Production Deployment.</p>
        </div>
      </footer>
    </div>
  );
}
