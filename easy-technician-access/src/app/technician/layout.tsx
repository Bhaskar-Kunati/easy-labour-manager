'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { getCurrentUser } from '@/lib/auth';
import { User } from '@/types';
import { ShieldAlert } from 'lucide-react';

export default function TechnicianLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const router = useRouter();
  const [authorized, setAuthorized] = useState(false);

  useEffect(() => {
    const user = getCurrentUser() as User | null;
    if (!user) {
      router.push('/login');
    } else if (user.role !== 'TECHNICIAN' && user.role !== 'ADMIN') {
      setAuthorized(false);
    } else {
      setAuthorized(true);
    }
  }, [router]);

  if (!authorized) {
    return (
      <div className="min-h-[70vh] flex items-center justify-center px-4">
        <div className="max-w-md w-full bg-white rounded-2xl shadow-xl border border-gray-100 p-8 text-center">
          <div className="bg-red-100 text-red-600 p-3 rounded-2xl w-fit mx-auto mb-4">
            <ShieldAlert className="h-8 w-8" />
          </div>
          <h2 className="text-xl font-bold text-gray-900 mb-2">Access Restricted</h2>
          <p className="text-gray-600 text-sm mb-6">This portal requires a verified Technician/Worker account role.</p>
          <button
            onClick={() => router.push('/login')}
            className="bg-blue-600 text-white px-6 py-2.5 rounded-xl text-sm font-semibold hover:bg-blue-700 transition-colors"
          >
            Sign In with Technician Account
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      <div className="flex-1">{children}</div>
    </div>
  );
}
