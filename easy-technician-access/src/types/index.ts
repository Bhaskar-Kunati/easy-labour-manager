export interface ServiceCategory {
  id: string;
  name: string;
  iconName: string;
  description: string;
  basePrice?: number;
}

export interface PlatformStatistics {
  happyCustomers: string;
  customerRating: string;
  securePayments: string;
  supportAvailability: string;
}

export type UserRole = 'CUSTOMER' | 'TECHNICIAN' | 'ADMIN';

export interface User {
  id: string;
  email: string;
  phoneNumber: string;
  role: UserRole;
  token?: string;
}

export interface CustomerProfile {
  customerId: string;
  fullName: string;
  email: string;
  phoneNumber: string;
  addresses: Address[];
}

export interface Address {
  id: string;
  street: string;
  city: string;
  state: string;
  postalCode: string;
}

export interface ServiceItem {
  serviceId: string;
  title: string;
  description: string;
  category: string;
  price: number;
  estimatedDuration: string;
}

export interface Booking {
  bookingId: string;
  customerId: string;
  technicianId?: string;
  serviceId: string;
  serviceTitle?: string;
  status: 'PENDING' | 'ACCEPTED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
  scheduledDate: string;
  totalAmount: number;
  address: string;
}

export interface PaymentRecord {
  paymentId: string;
  bookingId: string;
  amount: number;
  status: 'SUCCESS' | 'PENDING' | 'FAILED';
  paymentDate: string;
}

export interface Review {
  reviewId: string;
  bookingId: string;
  customerId: string;
  technicianId: string;
  rating: number;
  comment: string;
  createdAt: string;
}
