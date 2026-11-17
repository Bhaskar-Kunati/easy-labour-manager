'use client';

import { useState, useEffect } from 'react';
import { ServiceItem } from '@/types';
import { apiClient } from '@/lib/api-client';
import { Search, Wrench, Clock, DollarSign, ArrowRight } from 'lucide-react';
import Link from 'next/link';

export default function ServiceCatalogPage() {
  const [services, setServices] = useState<ServiceItem[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('ALL');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchServices = async () => {
      try {
        const res = await apiClient.get('/api/v1/catalog/services');
        setServices(res.data || []);
      } catch {
        // Fallback mock services for development/testing
        setServices([
          {
            serviceId: 'S-1',
            title: 'Expert Plumbing Repair & Pipe Fitting',
            description: 'Professional plumbing services for leaks, fixture installations, and pipe repairs.',
            category: 'Plumbing',
            price: 75.00,
            estimatedDuration: '1-2 hours',
          },
          {
            serviceId: 'S-2',
            title: 'Electrical Wiring & Appliance Setup',
            description: 'Certified electrical troubleshooting, switchboard repairs, and appliance installation.',
            category: 'Electrical',
            price: 90.00,
            estimatedDuration: '2 hours',
          },
          {
            serviceId: 'S-3',
            title: 'General Carpentry & Furniture Assembly',
            description: 'Custom woodworking, door repairs, and flat-pack furniture assembly.',
            category: 'Carpentry',
            price: 65.00,
            estimatedDuration: '1.5 hours',
          },
          {
            serviceId: 'S-4',
            title: 'Deep House Cleaning & Sanitization',
            description: 'Comprehensive residential cleaning, deep sanitization, and floor care.',
            category: 'Cleaning',
            price: 50.00,
            estimatedDuration: '3 hours',
          },
        ]);
      } finally {
        setLoading(false);
      }
    };

    fetchServices();
  }, []);

  const filteredServices = services.filter((s) => {
    const matchesSearch = s.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
                          s.description.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesCategory = selectedCategory === 'ALL' || s.category === selectedCategory;
    return matchesSearch && matchesCategory;
  });

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
      <div className="text-center max-w-3xl mx-auto mb-12">
        <h1 className="text-3xl font-extrabold text-gray-900 tracking-tight">Service Catalog & Pricing</h1>
        <p className="text-gray-600 mt-2">Browse trusted labor services and book certified technicians instantly.</p>

        {/* Search Bar */}
        <div className="mt-6 flex flex-col sm:flex-row gap-4 justify-center">
          <div className="relative flex-1 max-w-md">
            <span className="absolute inset-y-0 left-0 pl-3 flex items-center text-gray-400">
              <Search className="h-5 w-5" />
            </span>
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Search services (e.g. plumbing, electrical)..."
              className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-600 focus:border-transparent text-sm outline-none bg-white shadow-sm"
            />
          </div>
          <select
            value={selectedCategory}
            onChange={(e) => setSelectedCategory(e.target.value)}
            className="px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-600 focus:border-transparent text-sm outline-none bg-white shadow-sm"
          >
            <option value="ALL">All Categories</option>
            <option value="Plumbing">Plumbing</option>
            <option value="Electrical">Electrical</option>
            <option value="Carpentry">Carpentry</option>
            <option value="Cleaning">Cleaning</option>
          </select>
        </div>
      </div>

      {loading ? (
        <p className="text-center text-gray-500 py-12">Loading service catalog...</p>
      ) : filteredServices.length === 0 ? (
        <div className="text-center py-12 bg-white rounded-2xl border border-gray-100 shadow-sm">
          <p className="text-gray-500 mb-2">No services found matching your criteria.</p>
          <button
            onClick={() => { setSearchQuery(''); setSelectedCategory('ALL'); }}
            className="text-blue-600 font-semibold hover:underline text-sm"
          >
            Reset filters
          </button>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          {filteredServices.map((service) => (
            <div key={service.serviceId} className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 flex flex-col justify-between hover:shadow-md transition-shadow">
              <div>
                <div className="flex justify-between items-start mb-4">
                  <span className="bg-blue-50 text-blue-700 text-xs font-bold px-3 py-1 rounded-full">
                    {service.category}
                  </span>
                  <div className="flex items-center text-gray-900 font-bold text-lg">
                    <DollarSign className="h-5 w-5 text-emerald-600" />
                    <span>{service.price.toFixed(2)}</span>
                  </div>
                </div>
                <h3 className="text-xl font-bold text-gray-900 mb-2">{service.title}</h3>
                <p className="text-gray-600 text-sm mb-6">{service.description}</p>
              </div>

              <div className="pt-4 border-t border-gray-100 flex items-center justify-between">
                <span className="text-xs text-gray-500 flex items-center">
                  <Clock className="h-4 w-4 mr-1 text-gray-400" />
                  {service.estimatedDuration}
                </span>
                <Link
                  href={`/customer/bookings/new?serviceId=${service.serviceId}&title=${encodeURIComponent(service.title)}&price=${service.price}`}
                  className="bg-blue-600 hover:bg-blue-700 text-white font-semibold px-4 py-2 rounded-xl text-sm transition-colors flex items-center space-x-1"
                >
                  <span>Book Now</span>
                  <ArrowRight className="h-4 w-4" />
                </Link>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
