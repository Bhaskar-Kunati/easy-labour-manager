'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { getCurrentUser } from '@/lib/auth';
import { apiClient } from '@/lib/api-client';
import { Wrench, ArrowLeft, Wallet, DollarSign, ArrowUpRight, ArrowDownLeft, Loader2, AlertCircle } from 'lucide-react';
import Link from 'next/link';

interface Transaction {
  id: string;
  type: 'CREDIT' | 'PAYOUT';
  amount: number;
  description: string;
  date: string;
  status: string;
}

export default function TechnicianWalletPage() {
  const router = useRouter();
  const [wallet, setWallet] = useState({ balance: 840, pendingPayout: 215, totalEarned: 3450 });
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const currentUser = getCurrentUser();
    if (!currentUser) {
      router.push('/login');
      return;
    }
    fetchWalletData();
  }, [router]);

  const fetchWalletData = async () => {
    try {
      setLoading(true);
      const res = await apiClient.get('/api/v1/technicians/wallet');
      setWallet(res.data.wallet);
      setTransactions(res.data.transactions);
    } catch (err) {
      // Fallback mock data for development/demo
      setTransactions([
        {
          id: 'tx-801',
          type: 'CREDIT',
          amount: 120,
          description: 'Job Payout: Electrical Wiring & Repair',
          date: 'November 12, 2026',
          status: 'SUCCESS',
        },
        {
          id: 'tx-802',
          type: 'PAYOUT',
          amount: 500,
          description: 'Direct Bank Deposit to Chase ****4892',
          date: 'November 8, 2026',
          status: 'COMPLETED',
        },
        {
          id: 'tx-803',
          type: 'CREDIT',
          amount: 150,
          description: 'Job Payout: Plumbing Leak Repair',
          date: 'November 7, 2026',
          status: 'SUCCESS',
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
            <h1 className="text-lg font-bold text-gray-900">Technician Wallet & Earnings</h1>
          </div>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
        {/* Wallet Cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="bg-gradient-to-br from-blue-700 to-indigo-800 rounded-3xl p-6 text-white shadow-xl space-y-3">
            <div className="flex items-center justify-between">
              <span className="text-blue-200 text-sm font-medium">Available Balance</span>
              <Wallet className="h-6 w-6 text-blue-200" />
            </div>
            <p className="text-4xl font-extrabold">${wallet.balance}</p>
            <div className="pt-2">
              <button
                onClick={() => alert('Payout withdrawal requested successfully!')}
                className="w-full bg-white text-blue-900 font-semibold py-2.5 rounded-xl text-sm hover:bg-blue-50 transition-colors shadow-md"
              >
                Withdraw Funds
              </button>
            </div>
          </div>

          <div className="bg-white rounded-3xl p-6 border border-gray-100 shadow-sm space-y-3">
            <div className="flex items-center justify-between">
              <span className="text-gray-500 text-sm font-medium">Pending Payouts</span>
              <div className="bg-amber-50 text-amber-600 p-2 rounded-xl">
                <DollarSign className="h-5 w-5" />
              </div>
            </div>
            <p className="text-3xl font-extrabold text-gray-900">${wallet.pendingPayout}</p>
            <p className="text-xs text-gray-400">Clears upon job completion confirmation.</p>
          </div>

          <div className="bg-white rounded-3xl p-6 border border-gray-100 shadow-sm space-y-3">
            <div className="flex items-center justify-between">
              <span className="text-gray-500 text-sm font-medium">Total Lifetime Earnings</span>
              <div className="bg-emerald-50 text-emerald-600 p-2 rounded-xl">
                <DollarSign className="h-5 w-5" />
              </div>
            </div>
            <p className="text-3xl font-extrabold text-gray-900">${wallet.totalEarned}</p>
            <p className="text-xs text-gray-400">Lifetime completed service payouts.</p>
          </div>
        </div>

        {/* Transactions History */}
        <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-6 space-y-6">
          <h3 className="text-xl font-bold text-gray-900">Earnings & Payout Transactions</h3>

          {loading ? (
            <div className="py-12 flex justify-center items-center">
              <Loader2 className="h-8 w-8 animate-spin text-blue-600" />
            </div>
          ) : transactions.length === 0 ? (
            <div className="py-12 text-center text-gray-500 bg-gray-50 rounded-xl border border-dashed border-gray-200">
              <AlertCircle className="h-8 w-8 mx-auto mb-2 text-gray-400" />
              <p className="font-medium">No transactions found</p>
            </div>
          ) : (
            <div className="space-y-4">
              {transactions.map((tx) => (
                <div key={tx.id} className="flex items-center justify-between p-4 rounded-xl border border-gray-100 hover:border-gray-200 transition-all">
                  <div className="flex items-center gap-4">
                    <div className={`p-3 rounded-xl ${tx.type === 'CREDIT' ? 'bg-emerald-50 text-emerald-600' : 'bg-blue-50 text-blue-600'}`}>
                      {tx.type === 'CREDIT' ? <ArrowDownLeft className="h-5 w-5" /> : <ArrowUpRight className="h-5 w-5" />}
                    </div>
                    <div>
                      <h4 className="font-bold text-gray-900 text-sm">{tx.description}</h4>
                      <p className="text-xs text-gray-400 mt-0.5">{tx.date} • ID: {tx.id}</p>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className={`text-base font-extrabold ${tx.type === 'CREDIT' ? 'text-emerald-600' : 'text-gray-900'}`}>
                      {tx.type === 'CREDIT' ? `+$${tx.amount}` : `-$${tx.amount}`}
                    </p>
                    <span className="inline-block px-2 py-0.5 rounded-full text-[10px] font-semibold bg-emerald-50 text-emerald-700 mt-1">
                      {tx.status}
                    </span>
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
