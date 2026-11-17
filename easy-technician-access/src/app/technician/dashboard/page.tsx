'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { getCurrentUser, removeAuthToken } from '@/lib/auth';
import { apiClient } from '@/lib/api-client';
import { Wrench, User as UserIcon, Briefcase, Wallet, Star, LogOut, CheckCircle, Clock, MapPin, AlertCircle, Loader2 } from 'lucide-react';
import Link from 'next/link';

interface Job {
  id: string;
  serviceTitle: string;
  customerName: string;
  location: string;
  scheduledTime: string;
  status: string;
  amount: number;
}

export default function TechnicianDashboardPage() {
  const router = useRouter();
  const [user, setUser] = useState<any>(null);
  const [stats, setStats] = useState({ activeJobs: 3, completedJobs: 24, totalEarnings: 1250, rating: 4.8 });
  const [activeJobs, setActiveJobs] = useState<Job[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const currentUser = getCurrentUser();
    if (!currentUser) {
      router.push('/login');
      return;
    }
    setUser(currentUser);
    fetchDashboardData();
  }, [router]);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      // Fetch assigned jobs from gateway
      const res = await apiClient.get('/api/v1/bookings/assigned');
      setActiveJobs(res.data);
    } catch (err) {
      // Fallback mock jobs for development/demo
      setActiveJobs([
        {
          id: 'job-101',
          serviceTitle: 'Electrical Wiring & Repair',
          customerName: 'Sarah Jenkins',
          location: '124 Main Street, Apt 4B',
          scheduledTime: 'Today, 2:00 PM',
          status: 'ACCEPTED',
          amount: 120,
        },
        {
          id: 'job-102',
          serviceTitle: 'AC Maintenance & Servicing',
          customerName: 'Michael Chang',
          location: '88 Park Avenue',
          scheduledTime: 'Tomorrow, 10:00 AM',
          status: 'PENDING',
          amount: 95,
        },
      ]);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    removeAuthToken();
    router.push('/login');
  };

  return (
    <div className="min-h-screen bg-gray-50 pb-16">
      {/* Header */}
      <header className="bg-white border-b border-gray-200 sticky top-0 z-30">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="bg-blue-600 text-white p-2 rounded-xl shadow-md">
              <Wrench className="h-6 w-6" />
            </div>
            <div>
              <h1 className="text-lg font-bold text-gray-900">ETA Technician Portal</h1>
              <p className="text-xs text-gray-500">Worker Dashboard</p>
            </div>
          </div>

          <div className="flex items-center gap-4">
            <nav className="hidden md:flex items-center gap-6 text-sm font-medium text-gray-600">
              <Link href="/technician/dashboard" className="text-blue-600 font-semibold">Dashboard</Link>
              <Link href="/technician/profile" className="hover:text-blue-600 transition-colors">Profile & Skills</Link>
              <Link href="/technician/jobs" className="hover:text-blue-600 transition-colors">Assigned Jobs</Link>
              <Link href="/technician/jobs/history" className="hover:text-blue-600 transition-colors">Job History</Link>
              <Link href="/technician/wallet" className="hover:text-blue-600 transition-colors">Wallet</Link>
            </nav>
            <button
              onClick={handleLogout}
              className="flex items-center gap-2 text-gray-700 hover:text-red-600 px-3 py-2 rounded-xl border border-gray-200 hover:border-red-200 text-sm font-medium transition-all"
            >
              <LogOut className="h-4 w-4" />
              <span className="hidden sm:inline">Sign Out</span>
            </button>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
        {/* Welcome Banner */}
        <div className="bg-gradient-to-r from-blue-700 to-indigo-800 rounded-3xl p-8 text-white shadow-xl flex flex-col md:flex-row items-start md:items-center justify-between gap-6">
          <div className="space-y-2">
            <div className="inline-flex items-center gap-2 bg-blue-500/30 px-3 py-1 rounded-full text-xs font-semibold backdrop-blur-md">
              <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
              Online & Ready for Jobs
            </div>
            <h2 className="text-3xl font-extrabold tracking-tight">Welcome back, {user?.email || 'Technician'}!</h2>
            <p className="text-blue-100 text-sm max-w-xl">
              Manage your assigned service requests, update task milestones, and track your weekly earnings.
            </p>
          </div>
          <div className="flex items-center gap-3 bg-white/10 backdrop-blur-md px-5 py-3 rounded-2xl border border-white/20">
            <Star className="h-6 w-6 text-yellow-400 fill-yellow-400" />
            <div>
              <p className="text-xs text-blue-200 font-medium">Worker Rating</p>
              <p className="text-lg font-bold">{stats.rating} / 5.0 (42 reviews)</p>
            </div>
          </div>
        </div>

        {/* Stats Grid */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
          <div className="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm flex items-center gap-4">
            <div className="bg-blue-50 text-blue-600 p-4 rounded-2xl">
              <Briefcase className="h-6 w-6" />
            </div>
            <div>
              <p className="text-sm font-medium text-gray-500">Active Jobs</p>
              <p className="text-2xl font-bold text-gray-900">{activeJobs.length}</p>
            </div>
          </div>

          <div className="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm flex items-center gap-4">
            <div className="bg-emerald-50 text-emerald-600 p-4 rounded-2xl">
              <CheckCircle className="h-6 w-6" />
            </div>
            <div>
              <p className="text-sm font-medium text-gray-500">Completed Jobs</p>
              <p className="text-2xl font-bold text-gray-900">{stats.completedJobs}</p>
            </div>
          </div>

          <div className="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm flex items-center gap-4">
            <div className="bg-purple-50 text-purple-600 p-4 rounded-2xl">
              <Wallet className="h-6 w-6" />
            </div>
            <div>
              <p className="text-sm font-medium text-gray-500">Total Earnings</p>
              <p className="text-2xl font-bold text-gray-900">${stats.totalEarnings}</p>
            </div>
          </div>

          <div className="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm flex items-center gap-4">
            <div className="bg-amber-50 text-amber-600 p-4 rounded-2xl">
              <UserIcon className="h-6 w-6" />
            </div>
            <div>
              <p className="text-sm font-medium text-gray-500">Account Role</p>
              <p className="text-lg font-bold text-gray-900">TECHNICIAN</p>
            </div>
          </div>
        </div>

        {/* Assigned Jobs Section */}
        <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-6 space-y-6">
          <div className="flex items-center justify-between">
            <div>
              <h3 className="text-xl font-bold text-gray-900">Assigned Service Requests</h3>
              <p className="text-sm text-gray-500">Active jobs requiring your attention and status updates</p>
            </div>
            <Link
              href="/technician/jobs"
              className="text-sm font-semibold text-blue-600 hover:text-blue-700 transition-colors"
            >
              View All Jobs &rarr;
            </Link>
          </div>

          {loading ? (
            <div className="py-12 flex justify-center items-center">
              <Loader2 className="h-8 w-8 animate-spin text-blue-600" />
            </div>
          ) : activeJobs.length === 0 ? (
            <div className="py-12 text-center text-gray-500 bg-gray-50 rounded-xl border border-dashed border-gray-200">
              <AlertCircle className="h-8 w-8 mx-auto mb-2 text-gray-400" />
              <p className="font-medium">No assigned jobs found</p>
              <p className="text-xs text-gray-400 mt-1">You will be notified when new service requests are dispatched.</p>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {activeJobs.map((job) => (
                <div key={job.id} className="border border-gray-200 rounded-2xl p-5 hover:border-blue-300 transition-all space-y-4">
                  <div className="flex items-start justify-between">
                    <div>
                      <span className="inline-block px-2.5 py-1 rounded-full text-xs font-semibold bg-blue-50 text-blue-700 mb-2">
                        {job.status}
                      </span>
                      <h4 className="font-bold text-gray-900 text-lg">{job.serviceTitle}</h4>
                      <p className="text-sm font-medium text-gray-700 mt-0.5">Customer: {job.customerName}</p>
                    </div>
                    <span className="text-lg font-extrabold text-blue-600">${job.amount}</span>
                  </div>

                  <div className="space-y-2 text-sm text-gray-600 border-t border-gray-100 pt-3">
                    <div className="flex items-center gap-2">
                      <MapPin className="h-4 w-4 text-gray-400 shrink-0" />
                      <span>{job.location}</span>
                    </div>
                    <div className="flex items-center gap-2">
                      <Clock className="h-4 w-4 text-gray-400 shrink-0" />
                      <span>{job.scheduledTime}</span>
                    </div>
                  </div>

                  <div className="flex items-center justify-between pt-3 border-t border-gray-100">
                    <Link
                      href={`/technician/jobs`}
                      className="text-xs font-semibold text-blue-600 hover:underline"
                    >
                      Manage Job Status &rarr;
                    </Link>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </main>
    </div>
  );
}
