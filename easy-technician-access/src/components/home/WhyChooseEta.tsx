import { ShieldCheck, Tag, Clock, Headphones } from 'lucide-react';

const benefits = [
  {
    title: 'Verified & Background Checked',
    description: 'Every technician undergoes rigorous skill assessment and police background checks.',
    icon: ShieldCheck,
  },
  {
    title: 'Transparent Pricing',
    description: 'No hidden charges. Clear, upfront pricing on all home maintenance and repair services.',
    icon: Tag,
  },
  {
    title: 'On-Time Service',
    description: 'Punctual professionals who respect your time and arrive equipped for the job.',
    icon: Clock,
  },
  {
    title: '24/7 Support',
    description: 'Dedicated customer support and real-time live tracking throughout your booking.',
    icon: Headphones,
  },
];

export default function WhyChooseEta() {
  return (
    <section className="py-20 bg-white px-4 sm:px-6 lg:px-8">
      <div className="max-w-7xl mx-auto">
        <div className="text-center max-w-2xl mx-auto mb-16">
          <span className="text-primary font-semibold text-xs tracking-wider uppercase mb-2 block">
            Why ETA Is Different
          </span>
          <h2 className="text-3xl font-extrabold text-navy tracking-tight">
            Why Choose Easy Technician Access
          </h2>
          <p className="text-gray-600 mt-3">
            We connect you with certified professionals to ensure complete peace of mind.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
          {benefits.map((b, idx) => {
            const IconComponent = b.icon;
            return (
              <div
                key={idx}
                className="bg-blue-50/40 p-8 rounded-3xl border border-blue-100/60 hover:bg-white hover:shadow-subtle transition-all flex flex-col justify-between"
              >
                <div>
                  <div className="bg-primary text-white p-3.5 rounded-2xl w-fit mb-6 shadow-subtle">
                    <IconComponent className="h-6 w-6" />
                  </div>
                  <h3 className="text-lg font-bold text-navy mb-2">
                    {b.title}
                  </h3>
                  <p className="text-gray-600 text-sm leading-relaxed">
                    {b.description}
                  </p>
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </section>
  );
}
