import { Users, Star, ShieldCheck, Headphones } from 'lucide-react';

const stats = [
  {
    label: 'Happy Customers',
    value: '50,000+',
    description: 'Trusted homes across Bengaluru',
    icon: Users,
  },
  {
    label: 'Customer Rating',
    value: '4.9 / 5.0',
    description: 'Verified service reviews',
    icon: Star,
  },
  {
    label: 'Secure Payments',
    value: '100% Secure',
    description: 'Encrypted gateway transactions',
    icon: ShieldCheck,
  },
  {
    label: '24/7 Support',
    value: 'Always On',
    description: 'Dedicated customer assistance',
    icon: Headphones,
  },
];

export default function TrustStatistics() {
  return (
    <section className="py-16 bg-gray-50 border-t border-b border-gray-100 px-4 sm:px-6 lg:px-8">
      <div className="max-w-7xl mx-auto">
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-8">
          {stats.map((s, idx) => {
            const IconComponent = s.icon;
            return (
              <div key={idx} className="bg-white p-6 rounded-2xl shadow-card border border-gray-100 flex items-center space-x-4">
                <div className="bg-blue-50 text-primary p-4 rounded-2xl flex-shrink-0">
                  <IconComponent className="h-6 w-6" />
                </div>
                <div>
                  <h4 className="text-2xl font-extrabold text-navy">{s.value}</h4>
                  <p className="text-sm font-bold text-gray-700">{s.label}</p>
                  <p className="text-xs text-gray-500">{s.description}</p>
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </section>
  );
}
