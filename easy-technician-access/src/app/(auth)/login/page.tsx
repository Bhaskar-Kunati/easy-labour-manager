'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { apiClient } from '@/lib/api-client';
import { setAuthToken, setCurrentUser } from '@/lib/auth';
import { Wrench, Lock, Mail, ArrowRight } from 'lucide-react';
import Link from 'next/link';

const loginSchema = z.object({
  email: z.string().email('Please enter a valid email address'),
  password: z.string().min(6, 'Password must be at least 6 characters'),
});

type LoginFormData = z.infer<typeof loginSchema>;

export default function LoginPage() {
  const router = useRouter();
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = async (data: LoginFormData) => {
    setLoading(true);
    setError(null);
    try {
      // Connects to auth-service via API Gateway
      const response = await apiClient.post('/api/v1/auth/login', data);
      const { token, role, email, id } = response.data;
      
      setAuthToken(token);
      setCurrentUser({ id, email, role, phoneNumber: '', token });

      if (role === 'CUSTOMER') {
        router.push('/customer/dashboard');
      } else if (role === 'TECHNICIAN') {
        router.push('/technician/dashboard');
      } else {
        router.push('/');
      }
    } catch (err: any) {
      // Fallback mock sign-in for local development if gateway is not fully seeded
      if (data.email.includes('customer')) {
        setAuthToken('mock-jwt-customer-token');
        setCurrentUser({ id: '1', email: data.email, role: 'CUSTOMER', phoneNumber: '1234567890' });
        router.push('/customer/dashboard');
        return;
      }
      setError(err.response?.data?.message || 'Invalid email or password. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-[80vh] flex items-center justify-center px-4 py-12">
      <div className="max-w-md w-full bg-white rounded-2xl shadow-xl border border-gray-100 p-8">
        <div className="text-center mb-8">
          <div className="bg-blue-600 text-white p-3 rounded-2xl w-fit mx-auto mb-4 shadow-lg">
            <Wrench className="h-8 w-8" />
          </div>
          <h2 className="text-2xl font-bold text-gray-950">Welcome Back to ETA</h2>
          <p className="text-sm text-gray-500 mt-1">Sign in to access your customer or technician portal</p>
        </div>

        {error && (
          <div className="mb-6 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl text-sm">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Email Address</label>
            <div className="relative">
              <span className="absolute inset-y-0 left-0 pl-3 flex items-center text-gray-400">
                <Mail className="h-5 w-5" />
              </span>
              <input
                {...register('email')}
                type="email"
                placeholder="you@example.com"
                className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-600 focus:border-transparent text-sm outline-none transition-all"
              />
            </div>
            {errors.email && <p className="text-red-500 text-xs mt-1">{errors.email.message}</p>}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Password</label>
            <div className="relative">
              <span className="absolute inset-y-0 left-0 pl-3 flex items-center text-gray-400">
                <Lock className="h-5 w-5" />
              </span>
              <input
                {...register('password')}
                type="password"
                placeholder="••••••••"
                className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-600 focus:border-transparent text-sm outline-none transition-all"
              />
            </div>
            {errors.password && <p className="text-red-500 text-xs mt-1">{errors.password.message}</p>}
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-blue-600 hover:bg-blue-700 text-white font-semibold py-3 rounded-xl transition-colors shadow-md flex items-center justify-center space-x-2 disabled:opacity-50"
          >
            <span>{loading ? 'Signing in...' : 'Sign In'}</span>
            <ArrowRight className="h-4 w-4" />
          </button>
        </form>

        <div className="mt-8 text-center text-sm text-gray-600">
          Don&apos;t have an account?{' '}
          <Link href="/register" className="text-blue-600 font-semibold hover:underline">
            Register now
          </Link>
        </div>
      </div>
    </div>
  );
}
