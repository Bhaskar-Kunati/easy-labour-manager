'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { getCurrentUser } from '@/lib/auth';
import { apiClient } from '@/lib/api-client';
import JobStatusActions from '@/components/technician/JobStatusActions';
import { Wrench, ArrowLeft, MapPin, Clock, Calendar, AlertCircle, Loader2, DollarSign } from 'lucide-react';
import Link from 'next/link';

interface Job {
  id: string;
  serviceTitle: string;
  customerName: string;
  customerPhone: string;
  location: string;
  scheduledTime: string;
  status: string;
  amount: number;
  description: string;
}

export default function TechnicianJobsPage() {
  const router = useRouter();
  const [jobs, setJobs] = useState<Job[]>([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('ALL');

  useEffect(() => {
    const currentUser = getCurrentUser();
    if (!currentUser) {
      router.push('/login');
      return;
    }
    fetchJobs();
  }, [router]);

  const fetchJobs = async () => {
    try {
      setLoading(true);
      const res = await apiClient.get('/api/v1/bookings/assigned');
      setJobs(res.data);
    } catch (err) {
      // Fallback mock jobs for development/demo
      setJobs([
        {
          id: 'job-101',
          serviceTitle: 'Electrical Wiring & Repair',
          customerName: 'Sarah Jenkins',
          customerPhone: '+1 (555) 234-5678',
          location: '124 Main Street, Apt 4B',
          scheduledTime: 'Today, 2:00 PM',
          status: 'ACCEPTED',
          amount: 120,
          description: 'Living room main outlet sparking and tripping breaker.',
        },
        {
          id: 'job-102',
          serviceTitle: 'AC Maintenance & Servicing',
          customerName: 'Michael Chang',
          customerPhone: '+1 (555) 876-5432',
          location: '88 Park Avenue, Suite 12',
          scheduledTime: 'Tomorrow, 10:00 AM',
          status: 'PENDING',
          amount: 95,
          description: 'Annual AC filter cleaning and coolant check.',
        },
        {
          id: 'job-103',
          serviceTitle: 'Plumbing Leak Repair',
          customerName: 'Emily Watson',
          customerPhone: '+1 (555) 345-6789',
          location: '452 Elm Street',
          scheduledTime: 'Yesterday, 3:30 PM',
          status: 'IN_PROGRESS',
          amount: 150,
          description: 'Kitchen sink pipe leaking underneath cabinet.',
        },
      ]);
    } finally {
      setLoading(false);
    }
  };

  const filteredJobs = jobs.filter((job) => {
    if (filter === 'ALL') return true;
    return job.status === filter;
  });

  const handleStatusUpdated = (jobId: string, newStatus: string) => {
    setJobs(jobs.map((j) => (j.id === jobId ? { ...j, status: newStatus } : j)));
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
            <h1 className="text-lg font-bold text-gray-900">Assigned Jobs & Management</h1>
          </div>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
        {/* Filters */}
        <div className="flex items-center gap-2 overflow-x-auto pb-2">
          {['ALL', 'PENDING', 'ACCEPTED', 'IN_PROGRESS', 'COMPLETED'].map((statusOption) => (
            <button
              key={statusOption}
              onClick={() => setFilter(statusOption)}
              className={`px-4 py-2 rounded-xl text-xs font-semibold transition-all whitespace-nowrap ${
                filter === statusOption
                  ? 'bg-blue-600 text-white shadow-md'
                  : 'bg-white text-gray-700 border border-gray-200 hover:bg-gray-100'
              }`}
            >
              {statusOption.replace('_', ' ')}
            </button>
          ))}
        </div>

        {/* Jobs List */}
        {loading ? (
          <div className="py-20 flex justify-center items-center">
            <Loader2 className="h-8 w-8 animate-spin text-blue-600" />
          </div>
        ) : filteredJobs.length === 0 ? (
          <div className="bg-white rounded-2xl border border-gray-100 p-12 text-center text-gray-500">
            <AlertCircle className="h-10 w-10 mx-auto mb-3 text-gray-400" />
            <h3 className="text-lg font-bold text-gray-900">No jobs found</h3>
            <p className="text-sm text-gray-500 mt-1">There are no service requests matching the selected filter.</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 gap-5">
            {filteredJobs.map((job) => (
              <div key={job.id} className="bg-white rounded-2xl border border-gray-200 p-6 shadow-sm hover:border-blue-300 transition-all space-y-4">
                <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                  <div>
                    <div className="flex items-center gap-2 mb-1">
                      <span className="px-2.5 py-1 rounded-full text-xs font-semibold bg-blue-50 text-blue-700">
                        {job.status}
                      </span>
                      <span className="text-xs text-gray-400 font-mono">ID: {job.id}</span>
                    </div>
                    <h3 className="text-xl font-bold text-gray-900">{job.serviceTitle}</h3>
                  </div>
                  <div className="text-left sm:text-right">
                    <p className="text-xs text-gray-500">Payout Amount</p>
                    <p className="text-2xl font-extrabold text-blue-600">${job.amount}</p>
                  </div>
                </div>

                <p className="text-sm text-gray-600 bg-gray-50 p-3 rounded-xl border border-gray-100">
                  <span className="font-semibold text-gray-700">Note: </span>
                  {job.description}
                </p>

                <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-sm text-gray-600 border-t border-gray-100 pt-4">
                  <div className="flex items-center gap-2">
                    <MapPin className="h-4 w-4 text-gray-400 shrink-0" />
                    <span>{job.location}</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <Calendar className="h-4 w-4 text-gray-400 shrink-0" />
                    <span>{job.scheduledTime}</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <span className="font-medium text-gray-900">Customer:</span>
                    <span>{job.customerName} ({job.customerPhone})</span>
                  </div>
                </div>

                <div className="flex items-center justify-between pt-4 border-t border-gray-100">
                  <span className="text-xs text-gray-500">Update job milestone:</span>
                  <JobStatusActions
                    jobId={job.id}
                    currentStatus={job.status}
                    onStatusUpdated={(newStatus) => handleStatusUpdated(job.id, newStatus)}
                  />
                </div>
              </div>
            ))}
          </div>
        )}
      </main>
    </div>
  );
}
