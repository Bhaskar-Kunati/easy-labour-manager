'use client';

import Link from 'next/link';
import { Wind, Wrench, Zap, Hammer, Paintbrush, Sparkles, Tv, FolderPlus, ArrowRight } from 'lucide-react';

const categories = [
  { id: 'ac', name: 'AC Repair & Service', description: 'Installation, servicing, gas refill & repair', icon: Wind, color: 'bg-blue-50 text-primary' },
  { id: 'plumbing', name: 'Plumbing', description: 'Taps, pipes, leakage, bathroom fittings', icon: Wrench, color: 'bg-indigo-50 text-indigo-600' },
  { id: 'electrical', name: 'Electrical', description: 'Wiring, switches, fans, light fixtures', icon: Zap, color: 'bg-amber-50 text-amber-600' },
  { id: 'carpentry', name: 'Carpentry & Furniture', description: 'Door locks, bed repair, custom woodwork', icon: Hammer, color: 'bg-emerald-50 text-emerald-600' },
  { id: 'painting', name: 'Painting', description: 'Interior, exterior, waterproofing, walls', icon: Paintbrush, color: 'bg-purple-50 text-purple-600' },
  { id: 'cleaning', name: 'Cleaning Services', description: 'Deep home cleaning, kitchen, sofa & bathroom', icon: Sparkles, color: 'bg-cyan-50 text-cyan-600' },
  { id: 'appliance', name: 'Home Appliance Repair', description: 'Washing machine, refrigerator, microwave', icon: Tv, color: 'bg-rose-50 text-rose-600' },
  { id: 'other', name: 'Other Services', description: 'General handyman work and custom tasks', icon: FolderPlus, color: 'bg-gray-100 text-gray-700' },
];

export default function ServiceCatalogGrid() {
  return (
    <section className="py-20 bg-gray-50/60 px-4 sm:px-6 lg:px-8">
      <div className="max-w-7xl mx-auto">
        <div className="flex flex-col md:flex-row md:items-end justify-between mb-12">
          <div>
            <span className="text-primary font-semibold text-xs tracking-wider uppercase mb-2 block">
              Our Professional Offerings
            </span>
            <h2 className="text-3xl font-extrabold text-navy tracking-tight">
              Explore Our Services
            </h2>
            <p className="text-gray-600 mt-2 max-w-xl">
              Book expert technicians across Bengaluru with upfront pricing and verified background checks.
            </p>
          </div>
          <div className="mt-6 md:mt-0">
            <Link
              href="/customer/catalog"
              className="inline-flex items-center space-x-2 text-primary font-semibold hover:text-primary-hover transition-colors bg-white px-5 py-2.5 rounded-xl border border-gray-200 shadow-sm hover:shadow-md"
            >
              <span>View All Services</span>
              <ArrowRight className="h-4 w-4" />
            </Link>
          </div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          {categories.map((cat) => {
            const IconComponent = cat.icon;
            return (
              <Link
                key={cat.id}
                href={`/customer/catalog?category=${cat.id}`}
                className="group bg-white p-6 rounded-2xl shadow-card border border-gray-100 hover:border-primary/40 hover:shadow-subtle transition-all flex flex-col justify-between"
              >
                <div>
                  <div className={`${cat.color} p-3.5 rounded-2xl w-fit mb-4 group-hover:scale-110 transition-transform`}>
                    <IconComponent className="h-6 w-6" />
                  </div>
                  <h3 className="text-lg font-bold text-navy group-hover:text-primary transition-colors mb-1">
                    {cat.name}
                  </h3>
                  <p className="text-gray-500 text-sm">
                    {cat.description}
                  </p>
                </div>
                <div className="mt-6 pt-4 border-t border-gray-50 flex items-center justify-between text-xs font-semibold text-primary">
                  <span>Book Now</span>
                  <ArrowRight className="h-3.5 w-3.5 group-hover:translate-x-1 transition-transform" />
                </div>
              </Link>
            );
          })}
        </div>
      </div>
    </section>
  );
}
