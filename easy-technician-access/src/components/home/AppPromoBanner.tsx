import { Smartphone, Download, CheckCircle2 } from 'lucide-react';

export default function AppPromoBanner() {
  return (
    <section className="py-20 bg-white px-4 sm:px-6 lg:px-8">
      <div className="max-w-7xl mx-auto">
        <div className="bg-gradient-to-r from-primary via-blue-600 to-indigo-700 rounded-3xl p-8 sm:p-12 lg:p-16 text-white shadow-2xl relative overflow-hidden">
          <div className="absolute right-0 bottom-0 translate-x-12 translate-y-12 opacity-10 pointer-events-none">
            <Smartphone className="w-96 h-96 text-white" />
          </div>

          <div className="max-w-2xl space-y-6 relative z-10">
            <span className="bg-white/20 text-white text-xs font-semibold px-3.5 py-1.5 rounded-full uppercase tracking-wider inline-block">
              Mobile App Experience
            </span>
            <h2 className="text-3xl sm:text-4xl lg:text-5xl font-extrabold tracking-tight leading-tight">
              Professional Home Services at Your Fingertips
            </h2>
            <p className="text-blue-100 text-base sm:text-lg">
              Download the ETA Android or iOS app for instant booking, live GPS technician tracking, secure in-app payments, and exclusive discounts.
            </p>

            <div className="space-y-3 pt-2">
              <div className="flex items-center space-x-3 text-blue-100 text-sm">
                <CheckCircle2 className="h-5 w-5 text-blue-200 flex-shrink-0" />
                <span>Instant push notifications for technician arrival & booking updates</span>
              </div>
              <div className="flex items-center space-x-3 text-blue-100 text-sm">
                <CheckCircle2 className="h-5 w-5 text-blue-200 flex-shrink-0" />
                <span>Dedicated Android app powered by Jetpack Compose & Clean Architecture</span>
              </div>
            </div>

            <div className="pt-6 flex flex-wrap gap-4">
              <button
                onClick={() => alert('Android APK available in repository release / android-app')}
                className="bg-navy hover:bg-navy-dark text-white font-semibold px-6 py-3.5 rounded-xl transition-all shadow-lg flex items-center space-x-3"
              >
                <Download className="h-5 w-5" />
                <div className="text-left">
                  <p className="text-[10px] text-gray-300 uppercase">Download for</p>
                  <p className="text-sm font-bold">Android App</p>
                </div>
              </button>
              <button
                onClick={() => alert('iOS app link coming soon')}
                className="bg-white/10 hover:bg-white/20 border border-white/20 text-white font-semibold px-6 py-3.5 rounded-xl transition-all flex items-center space-x-3"
              >
                <Download className="h-5 w-5" />
                <div className="text-left">
                  <p className="text-[10px] text-blue-200 uppercase">Download for</p>
                  <p className="text-sm font-bold">iOS App</p>
                </div>
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
