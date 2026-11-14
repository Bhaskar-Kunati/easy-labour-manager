import { Calendar, UserCheck, Smile } from 'lucide-react';

const steps = [
  {
    step: '01',
    title: 'Book a Service',
    description: 'Select your required home service category, pick a convenient time slot in Bengaluru, and confirm.',
    icon: Calendar,
  },
  {
    step: '02',
    title: 'Get a Professional',
    description: 'A verified, skilled technician is assigned and arrives at your doorstep on time, ready to work.',
    icon: UserCheck,
  },
  {
    step: '03',
    title: 'Relax & Pay',
    description: 'Enjoy flawless service execution, track live progress, and pay securely via multiple payment options.',
    icon: Smile,
  },
];

export default function HowItWorks() {
  return (
    <section className="py-20 bg-blue-50/40 px-4 sm:px-6 lg:px-8">
      <div className="max-w-7xl mx-auto">
        <div className="text-center max-w-2xl mx-auto mb-16">
          <span className="text-primary font-semibold text-xs tracking-wider uppercase mb-2 block">
            Simple 3-Step Process
          </span>
          <h2 className="text-3xl font-extrabold text-navy tracking-tight">
            How It Works
          </h2>
          <p className="text-gray-600 mt-3">
            Getting your home repaired and maintained has never been this effortless.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {steps.map((s, idx) => {
            const IconComponent = s.icon;
            return (
              <div
                key={idx}
                className="relative bg-white p-8 rounded-3xl shadow-card border border-gray-100 flex flex-col justify-between"
              >
                <div className="absolute top-6 right-8 text-4xl font-extrabold text-blue-100 select-none">
                  {s.step}
                </div>
                <div>
                  <div className="bg-primary/10 text-primary p-4 rounded-2xl w-fit mb-6">
                    <IconComponent className="h-7 w-7" />
                  </div>
                  <h3 className="text-xl font-bold text-navy mb-3">
                    {s.title}
                  </h3>
                  <p className="text-gray-600 text-sm leading-relaxed">
                    {s.description}
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
