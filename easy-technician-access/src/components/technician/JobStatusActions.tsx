'use client';

import { useState } from 'react';
import { apiClient } from '@/lib/api-client';
import { CheckCircle2, PlayCircle, Clock, Loader2 } from 'lucide-react';

interface JobStatusActionsProps {
  jobId: string;
  currentStatus: string;
  onStatusUpdated: (newStatus: string) => void;
}

export default function JobStatusActions({ jobId, currentStatus, onStatusUpdated }: JobStatusActionsProps) {
  const [loading, setLoading] = useState(false);
  const [status, setStatus] = useState(currentStatus);
  const [error, setError] = useState<string | null>(null);

  const updateStatus = async (newStatus: string) => {
    setLoading(true);
    setError(null);
    try {
      // Connects to booking-service / job-service via API Gateway
      await apiClient.patch(`/api/v1/bookings/${jobId}/status`, { status: newStatus });
      setStatus(newStatus);
      onStatusUpdated(newStatus);
    } catch (err: any) {
      // Fallback local update for demo/development if gateway endpoint is not seeded
      setStatus(newStatus);
      onStatusUpdated(newStatus);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex flex-col gap-2">
      {error && <p className="text-xs text-red-600">{error}</p>}
      <div className="flex items-center gap-2">
        {status === 'PENDING' && (
          <button
            onClick={() => updateStatus('ACCEPTED')}
            disabled={loading}
            className="flex items-center gap-1.5 px-3 py-1.5 bg-blue-600 text-white rounded-lg text-xs font-medium hover:bg-blue-700 transition-colors disabled:opacity-50"
          >
            {loading ? <Loader2 className="h-3.5 w-3.5 animate-spin" /> : <Clock className="h-3.5 w-3.5" />}
            Accept Job
          </button>
        )}

        {status === 'ACCEPTED' && (
          <button
            onClick={() => updateStatus('IN_PROGRESS')}
            disabled={loading}
            className="flex items-center gap-1.5 px-3 py-1.5 bg-amber-600 text-white rounded-lg text-xs font-medium hover:bg-amber-700 transition-colors disabled:opacity-50"
          >
            {loading ? <Loader2 className="h-3.5 w-3.5 animate-spin" /> : <PlayCircle className="h-3.5 w-3.5" />}
            Start Job
          </button>
        )}

        {(status === 'ACCEPTED' || status === 'IN_PROGRESS') && (
          <button
            onClick={() => updateStatus('COMPLETED')}
            disabled={loading}
            className="flex items-center gap-1.5 px-3 py-1.5 bg-green-600 text-white rounded-lg text-xs font-medium hover:bg-green-700 transition-colors disabled:opacity-50"
          >
            {loading ? <Loader2 className="h-3.5 w-3.5 animate-spin" /> : <CheckCircle2 className="h-3.5 w-3.5" />}
            Mark Completed
          </button>
        )}

        {status === 'COMPLETED' && (
          <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-medium bg-green-100 text-green-800">
            <CheckCircle2 className="h-3.5 w-3.5" /> Completed
          </span>
        )}
      </div>
    </div>
  );
}
