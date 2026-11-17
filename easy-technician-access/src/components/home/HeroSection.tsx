'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { Search, ShieldCheck, DollarSign, Lock, ArrowRight } from 'lucide-react';

export default function HeroSection() {
  const router = useRouter();
  const [searchTerm, setSearchTerm] = useState('');

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    router.push(`/customer/catalog?search=${encodeURIComponent(searchTerm)}`);
  };

  return (
    <section className="relative bg-gradient-to-b from-blue-50/40 via-white to-white py-6 lg:py-10 px-4 sm:px-6 lg:px-8 overflow-hidden">
      <div className="max-w-7xl mx-auto grid grid-cols-1 lg:grid-cols-12 gap-6 lg:gap-10 items-center">
        {/* Left Column: Headline, Search, Trust Badges */}
        <div className="lg:col-span-7 space-y-4">
          <div className="inline-flex items-center space-x-2 bg-blue-100/70 text-primary px-3 py-1 rounded-full text-xs font-bold tracking-wide uppercase">
            <span>Bengaluru’s Trusted Home Services</span>
          </div>

          <h1 className="text-3xl sm:text-4xl lg:text-[2.75rem] font-extrabold text-navy tracking-tight leading-[1.12]">
            Find Trusted <br />
            <span className="text-primary">Technicians Near You</span>
          </h1>

          <p className="text-sm sm:text-base text-gray-600 max-w-xl font-normal leading-relaxed">
            Book home services quickly, safely and affordably. Expert plumbers, electricians, AC technicians, and repair pros at your doorstep.
          </p>

          {/* Search Bar */}
          <form onSubmit={handleSearch} className="bg-white p-2 rounded-2xl shadow-card border border-gray-100 flex flex-col sm:flex-row items-center gap-2 max-w-xl">
            <div className="flex items-center space-x-3 px-3 py-2 w-full sm:w-auto flex-1 border-b sm:border-b-0 sm:border-r border-gray-100">
              <Search className="h-5 w-5 text-gray-400 flex-shrink-0" />
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
              className="w-full sm:w-auto bg-primary hover:bg-primary-hover text-white font-semibold px-6 py-2.5 rounded-xl transition-all shadow-subtle flex items-center justify-center space-x-2 flex-shrink-0 text-sm"
            >
              <span>Search</span>
              <ArrowRight className="h-4 w-4" />
            </button>
          </form>

          {/* Trust Indicators */}
          <div className="pt-2 grid grid-cols-3 gap-3 border-t border-gray-100 max-w-xl">
            <div className="flex items-center space-x-2">
              <div className="bg-blue-50 text-primary p-1.5 rounded-xl">
                <ShieldCheck className="h-4 w-4" />
              </div>
              <div>
                <p className="text-xs font-bold text-navy">Verified</p>
                <p className="text-[10px] text-gray-500">Professionals</p>
              </div>
            </div>
            <div className="flex items-center space-x-2">
              <div className="bg-blue-50 text-primary p-1.5 rounded-xl">
                <DollarSign className="h-4 w-4" />
              </div>
              <div>
                <p className="text-xs font-bold text-navy">Transparent</p>
                <p className="text-[10px] text-gray-500">Pricing</p>
              </div>
            </div>
            <div className="flex items-center space-x-2">
              <div className="bg-blue-50 text-primary p-1.5 rounded-xl">
                <Lock className="h-4 w-4" />
              </div>
              <div>
                <p className="text-xs font-bold text-navy">Secure</p>
                <p className="text-[10px] text-gray-500">Payments</p>
              </div>
            </div>
          </div>
        </div>

        {/* Right Column: Professional Home-Service Technician & Customer Photograph */}
        <div className="lg:col-span-5 relative">
          <div className="relative mx-auto max-w-md lg:max-w-none">
            <div className="absolute -inset-2 bg-gradient-to-r from-primary to-blue-600 rounded-3xl blur-md opacity-15"></div>
            <div className="relative rounded-2xl overflow-hidden shadow-xl border border-gray-100 bg-white aspect-[16/9] lg:aspect-[4/3] h-[320px] sm:h-[360px] lg:h-[380px]">
              <img
                src="https://images.unsplash.com/photo-1581092160607-ee22621dd758?auto=format&fit=crop&w=1200&q=80"
                alt="Professional Home Service Technician and Customer in Modern Interior"
                className="w-full h-full object-cover"
              />
              <div className="absolute inset-0 bg-gradient-to-t from-black/60 via-transparent to-transparent flex flex-col justify-end p-5 text-white">
                <div className="flex items-center space-x-3 bg-white/20 backdrop-blur-md px-3.5 py-2 rounded-xl border border-white/25">
                  <div className="bg-primary text-white px-2.5 py-1 rounded-lg font-bold text-xs">
                    4.9★
                  </div>
                  <div>
                    <p className="text-xs font-bold">50,000+ Happy Customers</p>
                    <p className="text-[10px] text-gray-200">Verified Expert Technicians</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
