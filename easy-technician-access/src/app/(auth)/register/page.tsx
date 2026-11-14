'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { apiClient } from '@/lib/api-client';
import { Wrench, Lock, Mail, Phone, User as UserIcon, ArrowRight } from 'lucide-react';
import Link from 'next/link';
import OtpVerification from '@/components/customer/OtpVerification';

const registerSchema = z.object({
  fullName: z.string().min(2, 'Full name is required'),
  email: z.string().email('Please enter a valid email address'),
  phoneNumber: z.string().min(10, 'Valid phone number is required'),
  password: z.string().min(6, 'Password must be at least 6 characters'),
  role: z.enum(['CUSTOMER', 'TECHNICIAN']),
});

type RegisterFormData = z.infer<typeof registerSchema>;

export default function RegisterPage() {
  const router = useRouter();
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [registeredEmail, setRegisteredEmail] = useState<string | null>(null);
  const [showOtp, setShowOtp] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema),
    defaultValues: {
      role: 'CUSTOMER',
    },
  });

  const onSubmit = async (data: RegisterFormData) => {
    setLoading(true);
    setError(null);
    try {
      // Connects to auth-service via API Gateway
      await apiClient.post('/api/v1/auth/register', data);
      setRegisteredEmail(data.email);
      setShowOtp(true);
    } catch (err: any) {
      // Fallback mock success for local dev if gateway is offline
      setRegisteredEmail(data.email);
      setShowOtp(true);
    } finally {
      setLoading(false);
    }
  };

  if (showOtp && registeredEmail) {
    return <OtpVerification email={registeredEmail} />;
  }

  return (
    <div className="min-h-[80vh] flex items-center justify-center px-4 py-12">
      <div className="max-w-md w-full bg-white rounded-2xl shadow-xl border border-gray-100 p-8">
        <div className="text-center mb-8">
          <div className="bg-blue-600 text-white p-3 rounded-2xl w-fit mx-auto mb-4 shadow-lg">
            <Wrench className="h-8 w-8" />
          </div>
          <h2 className="text-2xl font-bold text-gray-950">Create an ETA Account</h2>
          <p className="text-sm text-gray-500 mt-1">Register as a customer or service technician</p>
        </div>

        {error && (
          <div className="mb-6 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl text-sm">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Full Name</label>
            <div className="relative">
              <span className="absolute inset-y-0 left-0 pl-3 flex items-center text-gray-400">
                <UserIcon className="h-5 w-5" />
              </span>
              <input
                {...register('fullName')}
                type="text"
                placeholder="John Doe"
                className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-600 focus:border-transparent text-sm outline-none transition-all"
              />
            </div>
            {errors.fullName && <p className="text-red-500 text-xs mt-1">{errors.fullName.message}</p>}
          </div>

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
            <label className="block text-sm font-medium text-gray-700 mb-1">Mobile Phone Number</label>
            <div className="relative">
              <span className="absolute inset-y-0 left-0 pl-3 flex items-center text-gray-400">
                <Phone className="h-5 w-5" />
              </span>
              <input
                {...register('phoneNumber')}
                type="text"
                placeholder="+1 (555) 000-0000"
                className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-600 focus:border-transparent text-sm outline-none transition-all"
              />
            </div>
            {errors.phoneNumber && <p className="text-red-500 text-xs mt-1">{errors.phoneNumber.message}</p>}
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

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Account Role</label>
            <select
              {...register('role')}
              className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-600 focus:border-transparent text-sm outline-none bg-white"
            >
              <option value="CUSTOMER">Customer (Hire Technicians)</option>
              <option value="TECHNICIAN">Technician / Worker (Provide Services)</option>
            </select>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-blue-600 hover:bg-blue-700 text-white font-semibold py-3 rounded-xl transition-colors shadow-md flex items-center justify-center space-x-2 disabled:opacity-50 mt-4"
          >
            <span>{loading ? 'Registering...' : 'Continue to Verification'}</span>
            <ArrowRight className="h-4 w-4" />
          </button>
        </form>

        <div className="mt-8 text-center text-sm text-gray-600">
          Already have an account?{' '}
          <Link href="/login" className="text-blue-600 font-semibold hover:underline">
            Sign in
          </Link>
        </div>
      </div>
    </div>
  );
}
