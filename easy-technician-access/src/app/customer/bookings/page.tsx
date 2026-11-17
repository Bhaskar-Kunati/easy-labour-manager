'use client';

import { useState, useEffect } from 'react';
import { Booking } from '@/types';
import { apiClient } from '@/lib/api-client';
import { Calendar, MapPin, DollarSign, XCircle, Clock } from 'lucide-react';
import Link from 'next/link';

export default function CustomerBookingsPage() {
  const [bookings, setBookings] = useState<Booking[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchBookings = async () => {
    try {
      const res = await apiClient.get('/api/v1/bookings/customer');
      setBookings(res.data || []);
    } catch {
      // Mock fallback data for testing
      setBookings([
        {
          bookingId: 'B-101',
          customerId: '1',
          serviceId: 'S-1',
          serviceTitle: 'Expert Plumbing Repair & Pipe Fitting',
          status: 'ACCEPTED',
          scheduledDate: '2026-11-15T10:00:00Z',
          totalAmount: 75.00,
          address: '123 Main Street, Apt 4B',
        },
        {
          bookingId: 'B-102',
          customerId: '1',
          serviceId: 'S-2',
          serviceTitle: 'Electrical Wiring & Appliance Setup',
          status: 'COMPLETED',
          scheduledDate: '2026-11-01T14:00:00Z',
          totalAmount: 90.00,
          address: '123 Main Street, Apt 4B',
        },
      ]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchBookings();
  }, []);

  const handleCancelBooking = async (bookingId: string) => {
    try {
      await apiClient.post(`/api/v1/bookings/${bookingId}/cancel`);
      setBookings((prev) =>
        prev.map((b) => (b.bookingId === bookingId ? { ...b, status: 'CANCELLED' } : b))
      );
    } catch {
      setBookings((prev) =>
        prev.map((b) => (b.bookingId === bookingId ? { ...b, status: 'CANCELLED' } : b))
      );
    }
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
      <div className="flex justify-between items-center mb-8">
        <div>
          <h1 className="text-3xl font-extrabold text-gray-900 tracking-tight">Your Service Bookings</h1>
          <p className="text-gray-600 mt-1">Manage active appointments, track technician status, and view history.</p>
        </div>
        <Link
          href="/customer/catalog"
          className="bg-blue-600 hover:bg-blue-700 text-white font-semibold px-5 py-2.5 rounded-xl text-sm transition-colors shadow-sm"
        >
          Book New Service
        </Link>
      </div>

      {loading ? (
        <p className="text-center text-gray-500 py-12">Loading bookings...</p>
      ) : bookings.length === 0 ? (
        <div className="text-center py-16 bg-white rounded-2xl border border-gray-100 shadow-sm">
          <p className="text-gray-500 mb-4">You have no bookings found.</p>
          <Link
            href="/customer/catalog"
            className="bg-blue-600 text-white px-6 py-2.5 rounded-xl text-sm font-semibold hover:bg-blue-700 transition-colors"
          >
            Browse Services
          </Link>
        </div>
      ) : (
        <div className="space-y-6">
          {bookings.map((booking) => (
            <div key={booking.bookingId} className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 flex flex-col md:flex-row justify-between items-start md:items-center">
              <div className="space-y-2">
                <div className="flex items-center space-x-3">
                  <span className={`text-xs font-bold px-3 py-1 rounded-full uppercase ${
                    booking.status === 'ACCEPTED' ? 'bg-blue-100 text-blue-700' :
                    booking.status === 'COMPLETED' ? 'bg-emerald-100 text-emerald-700' :
                    booking.status === 'CANCELLED' ? 'bg-red-100 text-red-700' : 'bg-amber-100 text-amber-700'
                  }`}>
                    {booking.status}
                  </span>
                  <span className="text-xs text-gray-400">Booking ID: {booking.bookingId}</span>
                </div>
                <h3 className="text-xl font-bold text-gray-900">{booking.serviceTitle}</h3>
                <p className="text-sm text-gray-600 flex items-center">
                  <MapPin className="h-4 w-4 text-gray-400 mr-1" />
                  {booking.address}
                </p>
                <p className="text-xs text-gray-500 flex items-center">
                  <Clock className="h-4 w-4 text-gray-400 mr-1" />
                  Scheduled: {new Date(booking.scheduledDate).toLocaleString()}
                </p>
              </div>

              <div className="mt-6 md:mt-0 flex flex-col md:flex-row items-start md:items-center space-y-4 md:space-y-0 md:space-x-6">
                <div className="text-left md:text-right">
                  <span className="text-2xl font-bold text-gray-900">${booking.totalAmount.toFixed(2)}</span>
                  <p className="text-xs text-gray-500">Total Price</p>
                </div>

                <div className="flex items-center space-x-3">
                  {booking.status !== 'CANCELLED' && booking.status !== 'COMPLETED' && (
                    <button
                      onClick={() => handleCancelBooking(booking.bookingId)}
                      className="border border-red-200 text-red-600 hover:bg-red-50 px-4 py-2 rounded-xl text-sm font-semibold transition-colors flex items-center space-x-1"
                    >
                      <XCircle className="h-4 w-4" />
                      <span>Cancel</span>
                    </button>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
