'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import { getCurrentUser } from '@/lib/auth';
import { User, Booking, ServiceItem } from '@/types';
import { apiClient } from '@/lib/api-client';
import { Calendar, Search, CreditCard, Star, ArrowRight, UserCheck, MapPin } from 'lucide-react';

export default function CustomerDashboard() {
  const [user, setUser] = useState<User | null>(null);
  const [activeBookings, setActiveBookings] = useState<Booking[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const currentUser = getCurrentUser();
    setUser(currentUser);

    // Fetch customer bookings via API Gateway
    const fetchBookings = async () => {
      try {
        const res = await apiClient.get('/api/v1/bookings/customer');
        setActiveBookings(res.data || []);
      } catch {
        // Mock fallback data for demo/testing
        setActiveBookings([
          {
            bookingId: 'B-101',
            customerId: currentUser?.id || '1',
            serviceId: 'S-1',
            serviceTitle: 'Expert Plumbing Repair & Pipe Fitting',
            status: 'ACCEPTED',
            scheduledDate: '2026-11-15T10:00:00Z',
            totalAmount: 75.00,
            address: '123 Main Street, Apt 4B',
          },
        ]);
      } finally {
        setLoading(false);
      }
    };

    fetchBookings();
  }, []);

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
      {/* Welcome Banner */}
      <div className="bg-gradient-to-r from-blue-600 to-indigo-700 rounded-3xl p-8 text-white shadow-xl mb-10 flex flex-col md:flex-row justify-between items-start md:items-center">
        <div>
          <span className="bg-blue-500/30 text-blue-100 text-xs font-semibold px-3 py-1 rounded-full uppercase tracking-wider mb-2 inline-block">
            Customer Portal
          </span>
          <h1 className="text-3xl font-bold">Welcome back, {user?.email || 'Customer'}!</h1>
          <p className="text-blue-100 mt-1">Manage your service bookings, explore labor catalog, and track technicians live.</p>
        </div>
        <Link
          href="/customer/catalog"
          className="mt-6 md:mt-0 bg-white text-blue-600 hover:bg-blue-50 font-semibold px-6 py-3 rounded-xl shadow transition-all flex items-center space-x-2"
        >
          <Search className="h-5 w-5" />
          <span>Book a Service Now</span>
        </Link>
      </div>

      {/* Quick Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-10">
        <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 flex items-center space-x-4">
          <div className="bg-blue-100 text-blue-600 p-4 rounded-xl">
            <Calendar className="h-6 w-6" />
          </div>
          <div>
            <p className="text-sm text-gray-500 font-medium">Active Bookings</p>
            <h3 className="text-2xl font-bold text-gray-900">{activeBookings.length}</h3>
          </div>
        </div>
        <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 flex items-center space-x-4">
          <div className="bg-emerald-100 text-emerald-600 p-4 rounded-xl">
            <UserCheck className="h-6 w-6" />
          </div>
          <div>
            <p className="text-sm text-gray-500 font-medium">Verified Status</p>
            <h3 className="text-2xl font-bold text-emerald-600">Active Account</h3>
          </div>
        </div>
        <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 flex items-center space-x-4">
          <div className="bg-indigo-100 text-indigo-600 p-4 rounded-xl">
            <CreditCard className="h-6 w-6" />
          </div>
          <div>
            <p className="text-sm text-gray-500 font-medium">Payment History</p>
            <h3 className="text-2xl font-bold text-gray-900">Secure</h3>
          </div>
        </div>
      </div>

      {/* Active Bookings Section */}
      <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-8">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-xl font-bold text-gray-900">Your Recent Bookings</h2>
          <Link href="/customer/bookings" className="text-blue-600 hover:underline text-sm font-semibold flex items-center space-x-1">
            <span>View All</span>
            <ArrowRight className="h-4 w-4" />
          </Link>
        </div>

        {loading ? (
          <p className="text-gray-500 py-6 text-center">Loading bookings...</p>
        ) : activeBookings.length === 0 ? (
          <div className="text-center py-12">
            <p className="text-gray-500 mb-4">You have no active bookings at the moment.</p>
            <Link
              href="/customer/catalog"
              className="bg-blue-600 text-white px-6 py-2.5 rounded-xl text-sm font-semibold hover:bg-blue-700 transition-colors"
            >
              Browse Services
            </Link>
          </div>
        ) : (
          <div className="space-y-4">
            {activeBookings.map((b) => (
              <div key={b.bookingId} className="border border-gray-100 rounded-xl p-6 flex flex-col sm:flex-row justify-between items-start sm:items-center bg-gray-50/50">
                <div>
                  <div className="flex items-center space-x-2">
                    <span className="bg-blue-100 text-blue-700 text-xs font-bold px-2.5 py-0.5 rounded-full uppercase">
                      {b.status}
                    </span>
                    <span className="text-xs text-gray-500">ID: {b.bookingId}</span>
                  </div>
                  <h3 className="text-lg font-bold text-gray-900 mt-2">{b.serviceTitle || 'General Labour Service'}</h3>
                  <p className="text-sm text-gray-600 flex items-center mt-1">
                    <MapPin className="h-4 w-4 text-gray-400 mr-1" />
                    {b.address}
                  </p>
                </div>
                <div className="mt-4 sm:mt-0 flex items-center space-x-4">
                  <div className="text-right">
                    <span className="text-lg font-bold text-gray-900">${b.totalAmount.toFixed(2)}</span>
                    <p className="text-xs text-gray-500">{new Date(b.scheduledDate).toLocaleDateString()}</p>
                  </div>
                  <Link
                    href={`/customer/bookings`}
                    className="bg-white border border-gray-300 text-gray-700 hover:bg-gray-100 px-4 py-2 rounded-xl text-sm font-semibold transition-colors"
                  >
                    Manage
                  </Link>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
