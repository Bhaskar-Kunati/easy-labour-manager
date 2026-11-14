'use client';

import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { getCurrentUser, removeAuthToken } from '@/lib/auth';
import { useEffect, useState } from 'react';
import { User } from '@/types';
import { Wrench, User as UserIcon, LogOut, Calendar, Home, Search, MapPin, Menu, X } from 'lucide-react';

export default function Navbar() {
  const router = useRouter();
  const [user, setUser] = useState<User | null>(null);
  const [location, setLocation] = useState('Bengaluru');
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  useEffect(() => {
    setUser(getCurrentUser());
  }, []);

  const handleLogout = () => {
    removeAuthToken();
    setUser(null);
    router.push('/login');
  };

  return (
    <header className="bg-white border-b border-gray-100 sticky top-0 z-50 shadow-sm">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-20 flex items-center justify-between">
        {/* Logo */}
        <div className="flex items-center space-x-6">
          <Link href="/" className="flex items-center space-x-3">
            <div className="bg-primary text-white p-2.5 rounded-2xl shadow-subtle">
              <Wrench className="h-6 w-6" />
            </div>
            <div className="flex flex-col">
              <span className="text-xl font-extrabold text-navy tracking-tight leading-none">
                ETA
              </span>
              <span className="text-xs font-medium text-gray-500 tracking-wider">
                Easy Technician Access
              </span>
            </div>
          </Link>

          {/* Location Selector */}
          <div className="hidden lg:flex items-center space-x-1.5 bg-blue-50/80 text-primary px-3 py-1.5 rounded-full text-xs font-semibold">
            <MapPin className="h-3.5 w-3.5" />
            <span>{location}</span>
          </div>
        </div>

        {/* Desktop Navigation */}
        <nav className="hidden md:flex items-center space-x-8">
          <Link href="/" className="text-gray-700 hover:text-primary font-medium text-sm transition-colors">
            Home
          </Link>
          <Link href="/customer/catalog" className="text-gray-700 hover:text-primary font-medium text-sm transition-colors">
            Services
          </Link>
          <Link href="/technician/dashboard" className="text-gray-700 hover:text-primary font-medium text-sm transition-colors">
            For Technicians
          </Link>
          <Link href="#about" className="text-gray-700 hover:text-primary font-medium text-sm transition-colors">
            About Us
          </Link>
          <Link href="#help" className="text-gray-700 hover:text-primary font-medium text-sm transition-colors">
            Help
          </Link>
        </nav>

        {/* Right Actions */}
        <div className="hidden md:flex items-center space-x-4">
          {user ? (
            <div className="flex items-center space-x-4">
              {user.role === 'CUSTOMER' && (
                <Link
                  href="/customer/dashboard"
                  className="text-sm font-semibold text-gray-700 hover:text-primary"
                >
                  Dashboard
                </Link>
              )}
              {user.role === 'TECHNICIAN' && (
                <Link
                  href="/technician/dashboard"
                  className="text-sm font-semibold text-gray-700 hover:text-primary"
                >
                  Technician Portal
                </Link>
              )}
              <span className="text-xs font-medium text-gray-500 bg-gray-100 px-3 py-1.5 rounded-full">
                {user.email}
              </span>
              <button
                onClick={handleLogout}
                className="flex items-center space-x-1 text-sm text-red-600 hover:text-red-700 font-medium bg-red-50 hover:bg-red-100 px-3 py-2 rounded-xl transition-colors"
              >
                <LogOut className="h-4 w-4" />
                <span>Logout</span>
              </button>
            </div>
          ) : (
            <div className="flex items-center space-x-3">
              <Link
                href="/login"
                className="text-gray-700 hover:text-primary font-semibold text-sm px-4 py-2.5 transition-colors"
              >
                Login
              </Link>
              <Link
                href="/customer/catalog"
                className="bg-primary text-white hover:bg-primary-hover font-semibold px-5 py-2.5 rounded-xl text-sm transition-all shadow-subtle flex items-center space-x-2"
              >
                <span>Book a Service</span>
              </Link>
            </div>
          )}
        </div>

        {/* Mobile menu button */}
        <div className="md:hidden flex items-center">
          <button
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            className="text-gray-700 p-2 rounded-lg hover:bg-gray-100 focus:outline-none"
          >
            {mobileMenuOpen ? <X className="h-6 w-6" /> : <Menu className="h-6 w-6" />}
          </button>
        </div>
      </div>

      {/* Mobile Navigation Drawer */}
      {mobileMenuOpen && (
        <div className="md:hidden bg-white border-b border-gray-100 px-4 pt-2 pb-6 space-y-3 shadow-lg">
          <div className="flex items-center space-x-2 bg-blue-50 text-primary px-3 py-2 rounded-xl text-sm font-medium w-fit mb-2">
            <MapPin className="h-4 w-4" />
            <span>{location} (Bengaluru)</span>
          </div>
          <Link
            href="/"
            onClick={() => setMobileMenuOpen(false)}
            className="block text-gray-700 hover:text-primary font-medium py-2"
          >
            Home
          </Link>
          <Link
            href="/customer/catalog"
            onClick={() => setMobileMenuOpen(false)}
            className="block text-gray-700 hover:text-primary font-medium py-2"
          >
            Services
          </Link>
          <Link
            href="/technician/dashboard"
            onClick={() => setMobileMenuOpen(false)}
            className="block text-gray-700 hover:text-primary font-medium py-2"
          >
            For Technicians
          </Link>
          <Link
            href="#about"
            onClick={() => setMobileMenuOpen(false)}
            className="block text-gray-700 hover:text-primary font-medium py-2"
          >
            About Us
          </Link>
          <Link
            href="#help"
            onClick={() => setMobileMenuOpen(false)}
            className="block text-gray-700 hover:text-primary font-medium py-2"
          >
            Help
          </Link>
          <div className="pt-4 border-t border-gray-100 flex flex-col space-y-2">
            {user ? (
              <>
                <span className="text-sm font-medium text-gray-600">{user.email}</span>
                <button
                  onClick={() => { handleLogout(); setMobileMenuOpen(false); }}
                  className="bg-red-50 text-red-600 font-semibold px-4 py-2.5 rounded-xl text-center"
                >
                  Logout
                </button>
              </>
            ) : (
              <>
                <Link
                  href="/login"
                  onClick={() => setMobileMenuOpen(false)}
                  className="text-gray-700 font-semibold px-4 py-2.5 text-center border border-gray-200 rounded-xl"
                >
                  Login
                </Link>
                <Link
                  href="/customer/catalog"
                  onClick={() => setMobileMenuOpen(false)}
                  className="bg-primary text-white font-semibold px-4 py-2.5 rounded-xl text-center shadow-subtle"
                >
                  Book a Service
                </Link>
              </>
            )}
          </div>
        </div>
      )}
    </header>
  );
}
