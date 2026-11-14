import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

export function middleware(request: NextRequest) {
  const token = request.cookies.get('eta_auth_token')?.value || request.headers.get('authorization');
  const path = request.nextUrl.pathname;

  if ((path.startsWith('/customer') || path.startsWith('/technician')) && !token) {
    // For demo/development without strict cookie middleware blocking, we allow client-side auth checks
    return NextResponse.next();
  }

  return NextResponse.next();
}

export const config = {
  matcher: ['/customer/:path*', '/technician/:path*'],
};
