import Link from 'next/link';
import { Wrench, Shield, Clock, Phone } from 'lucide-react';

export default function Footer() {
  return (
    <footer className="bg-navy text-white border-t border-gray-800">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8 mb-8">
          <div>
            <div className="flex items-center space-x-2 mb-4">
              <div className="bg-primary text-white p-2 rounded-xl">
                <Wrench className="h-5 w-5" />
              </div>
              <span className="text-xl font-bold tracking-tight">
                ETA <span className="text-primary-light text-sm font-normal">Easy Technician Access</span>
              </span>
            </div>
            <p className="text-gray-400 text-sm">
              Book verified home service professionals quickly, safely, and affordably across Bengaluru.
            </p>
          </div>
          <div>
            <h4 className="font-semibold text-white mb-4">Quick Links</h4>
            <ul className="space-y-2 text-sm text-gray-400">
              <li><Link href="/" className="hover:text-white transition-colors">Home</Link></li>
              <li><Link href="/customer/catalog" className="hover:text-white transition-colors">Services</Link></li>
              <li><Link href="/customer/dashboard" className="hover:text-white transition-colors">Customer Dashboard</Link></li>
              <li><Link href="/technician/dashboard" className="hover:text-white transition-colors">For Technicians</Link></li>
            </ul>
          </div>
          <div>
            <h4 className="font-semibold text-white mb-4">Support & Help</h4>
            <ul className="space-y-2 text-sm text-gray-400">
              <li><span className="hover:text-white transition-colors cursor-pointer">Help Center</span></li>
              <li><span className="hover:text-white transition-colors cursor-pointer">Safety & Trust</span></li>
              <li><span className="hover:text-white transition-colors cursor-pointer">Privacy Policy</span></li>
              <li><span className="hover:text-white transition-colors cursor-pointer">Terms of Service</span></li>
            </ul>
          </div>
          <div>
            <h4 className="font-semibold text-white mb-4">Contact</h4>
            <p className="text-sm text-gray-400 mb-2">Bengaluru, Karnataka, India</p>
            <p className="text-sm text-gray-400 flex items-center space-x-2">
              <Phone className="h-4 w-4 text-primary" />
              <span>support@easytechnician.com</span>
            </p>
          </div>
        </div>
        <div className="border-t border-gray-800 pt-8 flex flex-col sm:flex-row items-center justify-between text-sm text-gray-400">
          <p>© 2026 ETA (Easy Technician Access). All rights reserved.</p>
          <p className="mt-4 sm:mt-0">Powered by Easy Labour Manager Microservices & API Gateway</p>
        </div>
      </div>
    </footer>
  );
}
