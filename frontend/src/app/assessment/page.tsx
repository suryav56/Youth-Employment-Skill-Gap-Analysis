'use client';

import React, { useState, useEffect } from 'react';
import { getAssessmentQuestions, submitAssessment, getAssessmentHistory, Question, AssessmentAnswer, AssessmentResult } from '@/services/assessment';
import { ClipboardList, ChevronRight, ChevronLeft, Award, HelpCircle, CheckCircle2, RotateCcw, BookOpen, Clock, AlertCircle } from 'lucide-react';
import Link from 'next/link';

export default function AssessmentPage() {
  const [questions, setQuestions] = useState<Question[]>([]);
  const [history, setHistory] = useState<AssessmentResult[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Active quiz state
  const [activeQuiz, setActiveQuiz] = useState<boolean>(false);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [selectedAnswers, setSelectedAnswers] = useState<Record<number, string>>({});
  const [quizResult, setQuizResult] = useState<AssessmentResult | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    loadHistoryAndQuiz();
  }, []);

  async function loadHistoryAndQuiz() {
    try {
      const histData = await getAssessmentHistory();
      setHistory(histData);
    } catch {
      // Background history error, not fatal
    } finally {
      setIsLoading(false);
    }
  }

  const handleStartQuiz = async () => {
    setIsLoading(true);
    setError(null);
    setSelectedAnswers({});
    setQuizResult(null);
    setCurrentIndex(0);
    
    try {
      const questData = await getAssessmentQuestions();
      if (questData.length === 0) {
        setError('No assessment questions found for your profile. Add more skills to get custom questions.');
      } else {
        setQuestions(questData);
        setActiveQuiz(true);
      }
    } catch {
      setError('Failed to load assessment questions. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleSelectOption = (option: string) => {
    const activeQuestion = questions[currentIndex];
    setSelectedAnswers({
      ...selectedAnswers,
      [activeQuestion.id]: option
    });
  };

  const handleNext = () => {
    if (currentIndex < questions.length - 1) {
      setCurrentIndex(currentIndex + 1);
    }
  };

  const handlePrev = () => {
    if (currentIndex > 0) {
      setCurrentIndex(currentIndex - 1);
    }
  };

  const handleSubmitQuiz = async () => {
    // Verify all answered
    if (Object.keys(selectedAnswers).length < questions.length) {
      if (!confirm('You have unanswered questions. Are you sure you want to submit?')) return;
    }

    setIsSubmitting(true);
    setError(null);

    const submissionAnswers: AssessmentAnswer[] = questions.map((q) => ({
      questionId: q.id,
      selectedOption: selectedAnswers[q.id] || 'A' // default to A if not answered
    }));

    try {
      const result = await submitAssessment(submissionAnswers);
      setQuizResult(result);
      setActiveQuiz(false);
      
      // Reload history
      const histData = await getAssessmentHistory();
      setHistory(histData);
    } catch {
      setError('Failed to submit quiz. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  if (isLoading) {
    return (
      <div className="flex h-64 items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent"></div>
      </div>
    );
  }

  // Results Screen
  if (quizResult) {
    let breakdown: Record<string, string> = {};
    try {
      breakdown = JSON.parse(quizResult.details);
    } catch {
      // Fail fallback
    }

    return (
      <div className="max-w-2xl mx-auto space-y-6 animate-fade-in">
        <div className="rounded-2xl border border-border bg-card p-6 md:p-8 shadow-custom text-center space-y-6">
          <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-full bg-success/15 text-success">
            <CheckCircle2 className="h-10 w-10" />
          </div>
          <div>
            <h2 className="text-2xl font-black">Assessment Submitted Successfully!</h2>
            <p className="text-sm text-muted mt-1">Your performance score has been compiled and saved to your profile.</p>
          </div>

          {/* Large Score Indicator */}
          <div className="inline-flex flex-col items-center justify-center p-6 rounded-2xl bg-accent border border-border w-48 shadow-custom">
            <span className="text-4xl font-black text-primary">{quizResult.score}%</span>
            <span className="text-[10px] font-bold text-muted uppercase tracking-wider mt-1">Overall Score</span>
          </div>

          {/* Score breakdown per category */}
          {Object.keys(breakdown).length > 0 && (
            <div className="text-left space-y-3">
              <h3 className="text-xs font-bold uppercase tracking-wider text-muted border-b border-border pb-1.5">Category Breakdown</h3>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                {Object.entries(breakdown).map(([category, score]) => (
                  <div key={category} className="flex justify-between p-3 rounded-lg border border-border bg-background">
                    <span className="text-xs font-bold text-foreground">{category}</span>
                    <span className="text-xs font-bold text-primary">{score}</span>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Action buttons */}
          <div className="flex flex-col sm:flex-row gap-4 pt-4 border-t border-border">
            <button
              onClick={handleStartQuiz}
              className="flex-1 bg-primary hover:bg-primary-hover text-white text-sm font-semibold py-3 rounded-xl shadow-custom flex items-center justify-center gap-2 transition-all cursor-pointer"
            >
              <RotateCcw className="h-4 w-4" />
              Retake Assessment
            </button>
            <Link
              href="/courses"
              className="flex-grow flex items-center justify-center gap-2 border border-border bg-card hover:bg-accent text-foreground text-sm font-semibold py-3 rounded-xl transition-all"
            >
              <BookOpen className="h-4 w-4 text-muted" />
              View Recommended Courses
            </Link>
          </div>
        </div>
      </div>
    );
  }

  // Active Quiz taking Screen
  if (activeQuiz && questions.length > 0) {
    const q = questions[currentIndex];
    const total = questions.length;
    const progressPercent = ((currentIndex + 1) / total) * 100;
    const selectedOption = selectedAnswers[q.id];

    return (
      <div className="max-w-3xl mx-auto space-y-6 animate-fade-in">
        {/* Progress Bar & Header */}
        <div className="flex items-center justify-between px-2">
          <div className="flex items-center gap-2 text-sm font-bold text-foreground">
            <HelpCircle className="h-5 w-5 text-primary" />
            <span>Question {currentIndex + 1} of {total}</span>
          </div>
          <span className="text-xs text-muted font-semibold bg-accent px-2.5 py-1 rounded-full">
            {q.category}
          </span>
        </div>

        {/* Progress track */}
        <div className="h-1.5 w-full rounded-full bg-accent overflow-hidden">
          <div 
            className="h-full rounded-full bg-primary transition-all duration-300"
            style={{ width: `${progressPercent}%` }}
          />
        </div>

        {/* Question Card */}
        <div className="rounded-2xl border border-border bg-card p-6 md:p-8 shadow-custom space-y-6">
          <p className="text-base md:text-lg font-bold text-foreground leading-relaxed">
            {q.questionText}
          </p>

          {/* Choices Options list */}
          <div className="space-y-3">
            {[
              { key: 'A', text: q.optionA },
              { key: 'B', text: q.optionB },
              { key: 'C', text: q.optionC },
              { key: 'D', text: q.optionD }
            ].map((opt) => (
              <button
                key={opt.key}
                onClick={() => handleSelectOption(opt.key)}
                className={`flex w-full items-center gap-3.5 p-4 rounded-xl border text-left text-sm font-semibold transition-all ${
                  selectedOption === opt.key
                    ? 'border-primary bg-primary/5 text-primary shadow-custom'
                    : 'border-border bg-background hover:bg-accent text-foreground'
                }`}
              >
                <div className={`flex h-6 w-6 shrink-0 items-center justify-center rounded-full border text-xs font-bold ${
                  selectedOption === opt.key 
                    ? 'border-primary bg-primary text-white' 
                    : 'border-border text-muted bg-card'
                }`}>
                  {opt.key}
                </div>
                <span>{opt.text}</span>
              </button>
            ))}
          </div>

          {/* Navigation Controls */}
          <div className="flex justify-between items-center pt-6 border-t border-border">
            <button
              onClick={handlePrev}
              disabled={currentIndex === 0}
              className="flex items-center gap-1 border border-border bg-card hover:bg-accent text-foreground text-xs font-semibold px-4 py-2.5 rounded-lg disabled:opacity-40 transition-colors"
            >
              <ChevronLeft className="h-4 w-4" />
              Previous
            </button>

            {currentIndex === total - 1 ? (
              <button
                onClick={handleSubmitQuiz}
                disabled={isSubmitting}
                className="flex items-center gap-1.5 bg-success hover:bg-emerald-600 text-white text-xs font-bold px-5 py-2.5 rounded-lg shadow-custom disabled:opacity-50 transition-colors cursor-pointer"
              >
                {isSubmitting ? 'Submitting...' : 'Submit Answers'}
                <ChevronRight className="h-4 w-4" />
              </button>
            ) : (
              <button
                onClick={handleNext}
                className="flex items-center gap-1.5 bg-primary hover:bg-primary-hover text-white text-xs font-bold px-5 py-2.5 rounded-lg shadow-custom transition-colors"
              >
                Next
                <ChevronRight className="h-4 w-4" />
              </button>
            )}
          </div>
        </div>
      </div>
    );
  }

  // Initial Quiz Dashboard / History Screen
  return (
    <div className="grid grid-cols-1 lg:grid-cols-3 gap-8 max-w-7xl mx-auto">
      {/* Left 2 Cols: Start Card */}
      <div className="lg:col-span-2 space-y-6">
        {error && (
          <div className="flex items-center gap-2.5 rounded-xl border border-red-500/30 bg-red-500/10 p-4 text-sm text-red-500 animate-fade-in">
            <AlertCircle className="h-5 w-5 shrink-0" />
            <p className="font-semibold">{error}</p>
          </div>
        )}

        <div className="rounded-2xl border border-border bg-card p-6 md:p-8 shadow-custom text-center space-y-6 py-12">
          <div className="mx-auto flex h-14 w-14 items-center justify-center rounded-xl bg-primary/10 text-primary">
            <ClipboardList className="h-7 w-7" />
          </div>
          <div className="space-y-2">
            <h2 className="text-xl font-bold text-foreground">Technical Skill Assessment</h2>
            <p className="text-sm text-muted max-w-md mx-auto">
              Evaluate your knowledge! Based on your target career goal, we will curate 10 multiple-choice questions. 
              Answering correctly will automatically increment your profile skill levels.
            </p>
          </div>

          <div className="pt-2">
            <button
              onClick={handleStartQuiz}
              className="bg-primary hover:bg-primary-hover text-white text-sm font-semibold px-8 py-3 rounded-xl shadow-custom-lg transition-all inline-flex items-center gap-1.5 cursor-pointer"
            >
              Start 10-Question Quiz
              <ChevronRight className="h-4 w-4" />
            </button>
          </div>
        </div>
      </div>

      {/* Right 1 Col: Quiz History */}
      <div className="rounded-2xl border border-border bg-card p-6 shadow-custom space-y-4">
        <h3 className="text-lg font-bold text-foreground border-b border-border pb-3 flex items-center gap-1.5">
          <Clock className="h-5 w-5 text-muted" />
          Quiz History
        </h3>

        {history.length === 0 ? (
          <p className="text-xs text-muted text-center py-10">No assessment records found. Take your first quiz to record scores.</p>
        ) : (
          <div className="space-y-3 max-h-96 overflow-y-auto">
            {history.map((record) => (
              <div key={record.id} className="flex justify-between items-center p-3 rounded-lg border border-border bg-background">
                <div>
                  <p className="text-xs font-bold text-foreground">Score: {record.score}%</p>
                  <p className="text-[10px] text-muted">{new Date(record.takenAt).toLocaleDateString()}</p>
                </div>
                <span className={`text-[10px] font-bold px-2 py-0.5 rounded ${
                  record.score >= 70 ? 'bg-emerald-500/10 text-emerald-500' : 'bg-red-500/10 text-red-500'
                }`}>
                  {record.score >= 70 ? 'PASS' : 'STUDY'}
                </span>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
