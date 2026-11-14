'use client';

import { useState, useEffect } from 'react';
import { PaymentRecord } from '@/types';
import { apiClient } from '@/lib/api-client';
import { CreditCard, CheckCircle2, Clock } from 'lucide-react';

export default function CustomerPaymentsPage() {
  const [payments, setPayments] = useState<PaymentRecord[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchPayments = async () => {
      try {
        const res = await apiClient.get('/api/v1/payments/customer');
        setPayments(res.data || []);
      } catch {
        // Mock fallback data
        setPayments([
          {
            paymentId: 'P-101',
            bookingId: 'B-102',
            amount: 90.00,
            status: 'SUCCESS',
            paymentDate: '2026-11-01T15:30:00Z',
          },
        ]);
      } finally {
        setLoading(false);
      }
    };

    fetchPayments();
  }, []);

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
      <div className="mb-8">
        <h1 className="text-3xl font-extrabold text-gray-900 tracking-tight">Payment & Billing History</h1>
        <p className="text-gray-600 mt-1">Review past transactions and payment receipts securely.</p>
      </div>

      {loading ? (
        <p className="text-center text-gray-500 py-12">Loading payment history...</p>
      ) : payments.length === 0 ? (
        <div className="text-center py-16 bg-white rounded-2xl border border-gray-100 shadow-sm">
          <p className="text-gray-500">No payment records found.</p>
        </div>
      ) : (
        <div className="space-y-4">
          {payments.map((p) => (
            <div key={p.paymentId} className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 flex items-center justify-between">
              <div className="flex items-center space-x-4">
                <div className="bg-emerald-100 text-emerald-600 p-3 rounded-xl">
                  <CreditCard className="h-6 w-6" />
                </div>
                <div>
                  <div className="flex items-center space-x-2">
                    <span className="bg-emerald-50 text-emerald-700 text-xs font-bold px-2.5 py-0.5 rounded-full uppercase">
                      {p.status}
                    </span>
                    <span className="text-xs text-gray-400">Payment ID: {p.paymentId}</span>
                  </div>
                  <h3 className="text-lg font-bold text-gray-900 mt-1">Booking Reference: {p.bookingId}</h3>
                  <p className="text-xs text-gray-500 flex items-center mt-0.5">
                    <Clock className="h-3.5 w-3.5 mr-1" />
                    {new Date(p.paymentDate).toLocaleString()}
                  </p>
                </div>
              </div>

              <div className="text-right">
                <span className="text-2xl font-bold text-gray-900">${p.amount.toFixed(2)}</span>
                <p className="text-xs text-emerald-600 font-semibold">Paid Securely</p>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
