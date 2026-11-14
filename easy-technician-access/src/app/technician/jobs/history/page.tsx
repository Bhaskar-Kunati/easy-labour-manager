'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { getCurrentUser } from '@/lib/auth';
import { apiClient } from '@/lib/api-client';
import { Wrench, ArrowLeft, CheckCircle2, Star, Calendar, MapPin, Loader2, AlertCircle } from 'lucide-react';
import Link from 'next/link';

interface CompletedJob {
  id: string;
  serviceTitle: string;
  customerName: string;
  completedDate: string;
  amount: number;
  rating: number;
  feedback: string;
}

export default function TechnicianJobHistoryPage() {
  const router = useRouter();
  const [history, setHistory] = useState<CompletedJob[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const currentUser = getCurrentUser();
    if (!currentUser) {
      router.push('/login');
      return;
    }
    fetchHistory();
  }, [router]);

  const fetchHistory = async () => {
    try {
      setLoading(true);
      const res = await apiClient.get('/api/v1/technicians/jobs/history');
      setHistory(res.data);
    } catch (err) {
      // Fallback mock history for development/demo
      setHistory([
        {
          id: 'job-901',
          serviceTitle: 'Plumbing Pipe Repair',
          customerName: 'Robert Fox',
          completedDate: 'November 10, 2026',
          amount: 140,
          rating: 5,
          feedback: 'Extremely professional and fixed the leak quickly!',
        },
        {
          id: 'job-902',
          serviceTitle: 'Electrical Panel Inspection',
          customerName: 'Jessica Miller',
          completedDate: 'November 8, 2026',
          amount: 110,
          rating: 4.8,
          feedback: 'Great service, explained everything clearly.',
        },
        {
          id: 'job-903',
          serviceTitle: 'Smart Thermostat Installation',
          customerName: 'David Smith',
          completedDate: 'November 5, 2026',
          amount: 85,
          rating: 5,
          feedback: 'On time and neat work.',
        },
      ]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 pb-16">
      <header className="bg-white border-b border-gray-200 sticky top-0 z-30">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <Link href="/technician/dashboard" className="text-gray-500 hover:text-gray-900 transition-colors">
              <ArrowLeft className="h-5 w-5" />
            </Link>
            <div className="bg-blue-600 text-white p-2 rounded-xl shadow-md">
              <Wrench className="h-5 w-5" />
            </div>
            <h1 className="text-lg font-bold text-gray-900">Completed Jobs History</h1>
          </div>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
        <div>
          <h2 className="text-2xl font-bold text-gray-900">Past Completed Jobs</h2>
          <p className="text-sm text-gray-500 mt-1">Review your completed service history, customer ratings, and earned payouts.</p>
        </div>

        {loading ? (
          <div className="py-20 flex justify-center items-center">
            <Loader2 className="h-8 w-8 animate-spin text-blue-600" />
          </div>
        ) : history.length === 0 ? (
          <div className="bg-white rounded-2xl border border-gray-100 p-12 text-center text-gray-500">
            <AlertCircle className="h-10 w-10 mx-auto mb-3 text-gray-400" />
            <h3 className="text-lg font-bold text-gray-900">No completed jobs yet</h3>
            <p className="text-sm text-gray-500 mt-1">Jobs marked as completed will appear here.</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 gap-4">
            {history.map((item) => (
              <div key={item.id} className="bg-white rounded-2xl border border-gray-200 p-6 shadow-sm space-y-4">
                <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                  <div>
                    <div className="flex items-center gap-2 mb-1">
                      <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-700">
                        <CheckCircle2 className="h-3.5 w-3.5" /> Completed
                      </span>
                      <span className="text-xs text-gray-400 font-mono">ID: {item.id}</span>
                    </div>
                    <h3 className="text-xl font-bold text-gray-900">{item.serviceTitle}</h3>
                    <p className="text-sm text-gray-600 mt-0.5">Customer: {item.customerName}</p>
                  </div>
                  <div className="text-left sm:text-right">
                    <p className="text-xs text-gray-500">Earned Payout</p>
                    <p className="text-2xl font-extrabold text-emerald-600">${item.amount}</p>
                  </div>
                </div>

                <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-gray-50 p-4 rounded-xl border border-gray-100">
                  <div className="flex items-center gap-1.5 text-yellow-500 font-bold">
                    <Star className="h-4 w-4 fill-yellow-400" />
                    <span>{item.rating} / 5.0</span>
                    <span className="text-xs text-gray-500 font-normal ml-2">"{item.feedback}"</span>
                  </div>
                  <div className="flex items-center gap-2 text-xs text-gray-500">
                    <Calendar className="h-4 w-4 text-gray-400" />
                    <span>{item.completedDate}</span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </main>
    </div>
  );
}
