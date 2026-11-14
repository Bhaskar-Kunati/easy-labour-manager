import HeroSection from '@/components/home/HeroSection';
import TrustStatistics from '@/components/home/TrustStatistics';
import ServiceCatalogGrid from '@/components/home/ServiceCatalogGrid';
import WhyChooseEta from '@/components/home/WhyChooseEta';
import HowItWorks from '@/components/home/HowItWorks';
import AppPromoBanner from '@/components/home/AppPromoBanner';

export default function Home() {
  return (
    <div className="flex flex-col min-h-screen bg-white">
      <HeroSection />
      <TrustStatistics />
      <ServiceCatalogGrid />
      <WhyChooseEta />
      <HowItWorks />
      <AppPromoBanner />
    </div>
  );
}
