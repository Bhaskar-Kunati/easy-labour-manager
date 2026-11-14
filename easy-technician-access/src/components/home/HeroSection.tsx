'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { Search, ShieldCheck, DollarSign, Lock, ArrowRight, Wrench } from 'lucide-react';

export default function HeroSection() {
  const router = useRouter();
  const [searchTerm, setSearchTerm] = useState('');

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    router.push(`/customer/catalog?search=${encodeURIComponent(searchTerm)}`);
  };

  return (
    <section className="relative bg-gradient-to-b from-blue-50/60 via-white to-white py-16 lg:py-24 px-4 sm:px-6 lg:px-8 overflow-hidden">
      <div className="max-w-7xl mx-auto grid grid-cols-1 lg:grid-cols-12 gap-12 items-center">
        {/* Left Column: Headline, Search, Trust Badges */}
        <div className="lg:col-span-7 space-y-8">
          <div className="inline-flex items-center space-x-2 bg-blue-100/70 text-primary px-3.5 py-1.5 rounded-full text-xs font-bold tracking-wide uppercase">
            <span>Bengaluru’s Trusted Home Services</span>
          </div>

          <h1 className="text-4xl sm:text-5xl lg:text-6xl font-extrabold text-navy tracking-tight leading-[1.15]">
            Find Trusted Technicians <br />
            <span className="text-primary">Near You</span>
          </h1>

          <p className="text-lg sm:text-xl text-gray-600 max-w-2xl font-normal leading-relaxed">
            Book home services quickly, safely and affordably. Expert plumbers, electricians, AC technicians, and repair pros at your doorstep.
          </p>

          {/* Search Bar */}
          <form onSubmit={handleSearch} className="bg-white p-3 rounded-2xl shadow-card border border-gray-100 flex flex-col sm:flex-row items-center gap-3 max-w-2xl">
            <div className="flex items-center space-x-3 px-3 py-2 w-full sm:w-auto flex-1 border-b sm:border-b-0 sm:border-r border-gray-100">
              <Search className="h-5 w-5 text-gray-400" />
              <input
                type="text"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                placeholder="Search for AC repair, plumbing, electrical..."
                className="w-full focus:outline-none text-gray-800 text-sm placeholder-gray-400 bg-transparent"
              />
            </div>
            <button
              type="submit"
              className="w-full sm:w-auto bg-primary hover:bg-primary-hover text-white font-semibold px-8 py-3.5 rounded-xl transition-all shadow-subtle flex items-center justify-center space-x-2 flex-shrink-0"
            >
              <span>Search</span>
              <ArrowRight className="h-4 w-4" />
            </button>
          </form>

          {/* Trust Indicators */}
          <div className="pt-4 grid grid-cols-3 gap-4 border-t border-gray-100 max-w-2xl">
            <div className="flex items-center space-x-2.5">
              <div className="bg-blue-50 text-primary p-2 rounded-xl">
                <ShieldCheck className="h-5 w-5" />
              </div>
              <div>
                <p className="text-xs font-bold text-navy">Verified</p>
                <p className="text-[11px] text-gray-500">Professionals</p>
              </div>
            </div>
            <div className="flex items-center space-x-2.5">
              <div className="bg-blue-50 text-primary p-2 rounded-xl">
                <DollarSign className="h-5 w-5" />
              </div>
              <div>
                <p className="text-xs font-bold text-navy">Transparent</p>
                <p className="text-[11px] text-gray-500">Pricing</p>
              </div>
            </div>
            <div className="flex items-center space-x-2.5">
              <div className="bg-blue-50 text-primary p-2 rounded-xl">
                <Lock className="h-5 w-5" />
              </div>
              <div>
                <p className="text-xs font-bold text-navy">Secure</p>
                <p className="text-[11px] text-gray-500">Payments</p>
              </div>
            </div>
          </div>
        </div>

        {/* Right Column: Professional Technician & Customer Image Representation */}
        <div className="lg:col-span-5 relative">
          <div className="relative mx-auto max-w-md lg:max-w-none">
            <div className="absolute -inset-1.5 bg-gradient-to-r from-primary to-blue-600 rounded-3xl blur-lg opacity-25 animate-pulse"></div>
            <div className="relative bg-gradient-to-br from-blue-900 to-navy rounded-3xl p-8 text-white shadow-2xl overflow-hidden min-h-[420px] flex flex-col justify-between">
              <div className="absolute top-0 right-0 p-8 opacity-10">
                <Wrench className="w-64 h-64 text-white" />
              </div>
              <div>
                <span className="bg-white/10 text-blue-200 text-xs font-semibold px-3 py-1 rounded-full uppercase tracking-wider">
                  Verified Expert
                </span>
                <h3 className="text-2xl font-bold mt-4">Professional Service at Your Doorstep</h3>
                <p className="text-blue-200 text-sm mt-2">
                  Background-checked technicians equipped with professional tools and guaranteed punctuality.
                </p>
              </div>
              <div className="bg-white/10 backdrop-blur-md rounded-2xl p-4 border border-white/10 mt-8 flex items-center space-x-4">
                <div className="bg-primary text-white p-3 rounded-xl font-bold text-lg">
                  4.9★
                </div>
                <div>
                  <p className="text-sm font-bold">Rated by 50,000+ Customers</p>
                  <p className="text-xs text-blue-200">Top-rated home service platform in Bengaluru</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
