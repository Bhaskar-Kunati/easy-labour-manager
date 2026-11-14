import { setAuthToken, getAuthToken, removeAuthToken, setCurrentUser, getCurrentUser } from '@/lib/auth';

describe('Auth Utility Functions', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('should store and retrieve auth token correctly', () => {
    setAuthToken('test-jwt-token-123');
    expect(getAuthToken()).toBe('test-jwt-token-123');
  });

  it('should remove auth token and user correctly', () => {
    setAuthToken('test-jwt-token-123');
    setCurrentUser({ id: '1', email: 'test@example.com', role: 'CUSTOMER', phoneNumber: '1234567890' });
    
    removeAuthToken();
    expect(getAuthToken()).toBeNull();
    expect(getCurrentUser()).toBeNull();
  });

  it('should store and retrieve current user correctly', () => {
    const user = { id: '2', email: 'customer@example.com', role: 'CUSTOMER' as const, phoneNumber: '9876543210' };
    setCurrentUser(user);
    expect(getCurrentUser()).toEqual(user);
  });
});
