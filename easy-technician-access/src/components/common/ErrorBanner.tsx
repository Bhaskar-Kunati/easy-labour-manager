'use client';

import { AlertTriangle, RefreshCw } from 'lucide-react';

interface ErrorBannerProps {
  message?: string;
  onRetry?: () => void;
}

export default function ErrorBanner({
  message = 'Unable to connect to the Easy Labour Manager API Gateway (http://localhost:8080). Please ensure backend services are running.',
  onRetry,
}: ErrorBannerProps) {
  return (
    <div className="bg-amber-50 border-b border-amber-200 px-4 py-3">
      <div className="max-w-7xl mx-auto flex flex-col sm:flex-row items-center justify-between space-y-2 sm:space-y-0">
        <div className="flex items-center space-x-3 text-amber-800 text-sm font-medium">
          <AlertTriangle className="h-5 w-5 text-amber-600 flex-shrink-0" />
          <span>{message}</span>
        </div>
        {onRetry && (
          <button
            onClick={onRetry}
            className="flex items-center space-x-1 text-xs font-semibold text-amber-900 bg-amber-200 hover:bg-amber-300 px-3 py-1.5 rounded-lg transition-colors"
          >
            <RefreshCw className="h-3.5 w-3.5" />
            <span>Retry Connection</span>
          </button>
        )}
      </div>
    </div>
  );
}
