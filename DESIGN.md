# EASY LABOUR – COMPLETE END-TO-END DEVELOPMENT MASTER PROMPT

You are a Senior Android Architect, Senior Java/Spring Boot Backend Engineer, PostgreSQL Database Architect, UI/UX Designer, Security Engineer, and DevOps Engineer.

Your task is to design and develop a **complete production-ready Easy Labour application**.

The application is similar to Urban Company, but focused on **booking labour and service workers**.

The system must support:

1. Customer Android App
2. Labour/Worker Android App or Worker Module
3. Admin Backend and Admin APIs
4. Spring Boot REST API Backend
5. PostgreSQL Database
6. Authentication and Authorization
7. Real-Time Labour Booking
8. Scheduled Booking
9. Payment Integration Architecture
10. Notifications
11. Location Tracking
12. Booking Management
13. Ratings and Reviews
14. Worker Management
15. Admin Management
16. Complete error handling
17. Production-ready API design

---

# IMPORTANT DEVELOPMENT RULES

Follow these rules strictly.

## Technology Stack

### Android

* Java
* Latest stable Android SDK
* Android Studio compatible project
* MVVM architecture
* Repository pattern
* Retrofit for REST APIs
* OkHttp
* Gson or Moshi
* Room Database for local caching where required
* LiveData
* ViewModel
* Material Design
* RecyclerView
* ConstraintLayout
* Navigation Component
* Google Maps SDK
* Firebase Cloud Messaging

Do NOT use Kotlin unless explicitly requested.

---

### Backend

Use:

* Java 21 or compatible stable LTS version
* Spring Boot
* Spring Web
* Spring Data JPA
* Spring Security
* Spring Validation
* PostgreSQL
* JWT Authentication
* Lombok
* Maven
* Hibernate
* Flyway or Liquibase for database migration
* OpenAPI/Swagger documentation

Architecture:

Controller
→ Service
→ Repository
→ Database

Use DTOs.

Never expose JPA entities directly from REST APIs.

---

# PROJECT NAME

Easy Labour

Package example:

com.easylabour.app

Backend:

easy-labour-backend

---

# BUSINESS IDEA

Easy Labour is an online platform where customers can find and book skilled and unskilled workers.

Examples:

* Construction Labour
* Mason
* Painter
* Plumber
* Electrician
* Carpenter
* Cleaner
* AC Technician
* Appliance Repair
* Home Cleaning
* Office Cleaning
* Driver
* Helper
* Gardener
* Security Staff
* Moving Labour
* Loading and Unloading Workers
* Other Local Services

The application supports two major service types.

## 1. REAL-TIME SERVICE

Customer needs a worker immediately.

Example:

"I need 2 workers now."

The system should:

1. Identify customer's current location.
2. Search nearby available workers.
3. Send booking requests.
4. Allow workers to accept or reject.
5. Assign the worker.
6. Show worker live status.
7. Track arrival.
8. Start service.
9. Complete service.
10. Collect payment.
11. Allow customer rating.

---

## 2. SCHEDULED SERVICE

Customer books a worker for a future date and time.

Example:

"I need 3 painters tomorrow at 10 AM."

Customer selects:

* Service category
* Number of workers
* Date
* Time
* Duration
* Location
* Special instructions

The system should:

1. Create booking.
2. Search eligible workers.
3. Assign workers.
4. Send confirmation.
5. Send reminders.
6. Track booking status.
7. Complete service.
8. Process payment.

---

# USER ROLES

The system must support the following roles.

## ROLE 1 – CUSTOMER

A customer can:

* Register
* Login
* Logout
* Manage profile
* Add addresses
* Detect current location
* Search services
* Browse categories
* Book labour
* Request real-time services
* Schedule future services
* Select number of workers
* View price estimate
* Apply coupons
* Make payment
* View booking history
* Cancel booking
* Track worker
* Chat with worker if enabled
* Call worker using masked communication architecture
* Rate worker
* Write review
* Contact support

---

## ROLE 2 – WORKER / LABOUR

A worker can:

* Register
* Login
* Submit profile
* Upload documents
* Select skills
* Set service categories
* Set working area
* Turn availability ON/OFF
* Receive booking requests
* Accept booking
* Reject booking
* Navigate to customer location
* Update arrival
* Start work
* Complete work
* View earnings
* View completed jobs
* View ratings
* Manage profile
* Manage bank/payment information

Worker verification must be controlled by Admin.

---

## ROLE 3 – ADMIN

Admin can:

* Login securely
* Manage customers
* Manage workers
* Approve worker registration
* Reject worker registration
* Suspend users
* Manage categories
* Manage services
* Configure pricing
* Manage bookings
* Assign workers manually
* Monitor active bookings
* Manage complaints
* Manage coupons
* Manage banners and advertisements
* View payments
* Manage refunds
* View reports
* View worker performance
* View customer statistics

---

# APPLICATION MODULES

The application should be divided into the following modules.

1. Authentication
2. Customer Profile
3. Worker Profile
4. Location Management
5. Service Categories
6. Service Search
7. Real-Time Booking
8. Scheduled Booking
9. Worker Matching
10. Booking Management
11. Payment
12. Notifications
13. Ratings and Reviews
14. Coupons
15. Wallet Architecture
16. Support
17. Admin Management
18. Analytics and Reporting

---

# CUSTOMER APP FLOW

## SPLASH SCREEN

When the application starts:

1. Display Easy Labour logo.
2. Check internet connection.
3. Check authentication token.
4. Validate token.
5. Check user role.
6. Navigate accordingly.

Flow:

App Launch

↓

Splash Screen

↓

Check Login

↓

If Not Logged In

→ Login Screen

If Logged In

→ Home Screen

---

# ONBOARDING

Show onboarding screens.

Screen 1:

Find Trusted Workers

Screen 2:

Book Services Instantly

Screen 3:

Schedule Workers Easily

Screen 4:

Track Your Booking

Buttons:

* Skip
* Next
* Get Started

Store onboarding completion locally.

---

# AUTHENTICATION

## LOGIN SCREEN

Fields:

* Mobile Number
* Country Code

Button:

Continue

Login should support OTP architecture.

Flow:

Enter Mobile Number

↓

Send OTP

↓

OTP Verification Screen

↓

Verify OTP

↓

If Existing User

Go to Home

If New User

Go to Registration

---

# OTP SCREEN

Fields:

* 6 digit OTP

Features:

* Auto-read OTP architecture where supported
* Resend OTP
* Countdown timer
* Change mobile number

API:

POST /api/v1/auth/send-otp

POST /api/v1/auth/verify-otp

Do not hardcode OTP logic in production.

Create OTP provider abstraction.

Example:

OtpService

SmsProvider

MockSmsProvider

Future providers can be integrated without changing business logic.

---

# CUSTOMER REGISTRATION

Fields:

* Full Name
* Mobile Number
* Email optional
* Gender optional
* Profile Photo optional

After successful registration:

Create customer profile.

---

# CUSTOMER HOME SCREEN

Design a modern professional home screen.

## TOP SECTION

Display:

Location Selector

Example:

📍 Powai, Mumbai

Features:

* Current Location
* Saved Addresses
* Add New Address
* Change Location

---

## SEARCH BAR

Placeholder:

"What service do you need?"

Search:

* Plumber
* Painter
* Electrician
* Mason
* Cleaning
* Carpenter
* Labour

Search suggestions must come from backend.

---

## ADVERTISEMENT CAROUSEL

Display promotional banners.

Examples:

* 20% Off Home Services
* First Booking Discount
* Festival Offers

Admin manages banners.

API:

GET /api/v1/banners

---

## SERVICE CATEGORIES

Display grid icons.

Categories:

Home Services

Business Services

Construction Services

Cleaning Services

Beauty Services

Other Services

---

## POPULAR SERVICES

Examples:

* Plumber
* Electrician
* Painter
* Carpenter
* Home Cleaner
* Construction Labour

Display:

Icon

Service Name

Starting Price

Rating

---

## REAL-TIME SERVICE CARD

Display prominent card.

Title:

Need Labour Immediately?

Button:

Book Now

Click:

Open Real-Time Booking.

---

## SCHEDULE SERVICE CARD

Title:

Plan Your Service

Button:

Schedule Now

---

# BOTTOM NAVIGATION

Customer app bottom navigation:

1. Home
2. Bookings
3. Messages
4. Notifications
5. Profile

---

# SERVICE CATEGORY FLOW

Home

↓

Select Category

↓

Service List

↓

Select Service

↓

Service Details

---

# SERVICE DETAILS SCREEN

Display:

Service Name

Description

Images

Starting Price

Pricing Type

Estimated Duration

Available Workers

Ratings

Reviews

Service Includes

Service Excludes

Cancellation Policy

Buttons:

Book Now

Schedule Service

---

# PRICING TYPES

Support multiple pricing models.

## HOURLY

Example:

₹150 per hour

## DAILY

Example:

₹800 per day

## FIXED

Example:

₹500 per service

## PER WORKER

Example:

₹700 per worker

## CUSTOM QUOTATION

Worker or Admin provides quotation.

Pricing must be configurable from backend.

Do NOT hardcode pricing in Android.

---

# REAL-TIME BOOKING FLOW

Customer clicks:

Book Now

Screen should collect:

## STEP 1 – SERVICE

Select:

Service Category

Service

Number of Workers

Example:

Construction Labour

Workers:

[-] 2 [+]

---

## STEP 2 – LOCATION

Options:

Use Current Location

Select Saved Address

Add New Address

Capture:

Latitude

Longitude

Address

Landmark

---

## STEP 3 – REQUIREMENT

Fields:

Description

Example:

"Need two workers for loading construction material."

Optional:

Upload Images

---

## STEP 4 – DURATION

Options:

* 1 Hour
* 2 Hours
* Half Day
* Full Day
* Custom

---

## STEP 5 – PRICE ESTIMATE

Display:

Base Price

Worker Cost

Platform Fee

Tax

Discount

Total

Example:

Worker Cost: ₹1000

Platform Fee: ₹50

Tax: ₹18

Discount: ₹100

Total: ₹968

Pricing calculation must happen on backend.

Android must only display backend response.

---

## STEP 6 – CONFIRM BOOKING

Customer clicks:

Find Worker

Backend:

1. Create booking.
2. Booking status = SEARCHING.
3. Search nearby available workers.
4. Filter by:

   * Service skill
   * Availability
   * Distance
   * Worker rating
   * Worker verification
5. Send request to workers.
6. Wait for acceptance.

---

# REAL-TIME BOOKING STATUS FLOW

SEARCHING

↓

WORKER_ASSIGNED

↓

WORKER_ACCEPTED

↓

WORKER_ON_THE_WAY

↓

WORKER_ARRIVED

↓

SERVICE_STARTED

↓

SERVICE_COMPLETED

↓

PAYMENT_PENDING

↓

PAID

↓

CLOSED

Alternative statuses:

CANCELLED

FAILED

NO_WORKER_AVAILABLE

EXPIRED

---

# WORKER MATCHING ALGORITHM

Create a WorkerMatchingService.

Input:

Booking

↓

Find workers.

Criteria:

1. Worker status = ACTIVE
2. Worker verification = APPROVED
3. Availability = AVAILABLE
4. Required skill exists
5. Worker within configured radius
6. Worker not already assigned to conflicting booking

Sort priority:

1. Distance
2. Rating
3. Acceptance rate
4. Completion rate

Create configurable matching radius.

Example:

5 KM

10 KM

20 KM

Do not hardcode.

Store configuration in database or application configuration.

---

# WORKER REQUEST FLOW

Backend sends booking request.

Worker receives notification.

Worker screen displays:

New Booking Request

Service:

Construction Labour

Distance:

2.5 KM

Customer Location:

Map preview

Estimated Duration:

4 Hours

Estimated Earnings:

₹900

Buttons:

ACCEPT

REJECT

---

# WORKER ACCEPTANCE

When worker clicks ACCEPT:

Backend must use transaction and locking.

Important:

Only one worker should successfully accept a booking slot.

Prevent race conditions.

Use:

Optimistic locking or database transaction locking.

Flow:

Worker Accepts

↓

Check Booking Status

↓

Check Available Slots

↓

Reserve Worker

↓

Update Booking

↓

Notify Customer

---

# MULTIPLE WORKER BOOKING

Customer may request multiple workers.

Example:

Requested Workers:

5

Backend must:

1. Match multiple workers.
2. Assign workers independently.
3. Track each worker assignment.

Create:

Booking

BookingWorker

Worker

Relationship.

Booking status should support:

PARTIALLY_ASSIGNED

FULLY_ASSIGNED

NO_WORKER_AVAILABLE

---

# SCHEDULED BOOKING FLOW

Customer selects:

Service

↓

Workers Count

↓

Address

↓

Date

↓

Time

↓

Duration

↓

Special Instructions

↓

Price Estimate

↓

Payment Option

↓

Confirm Booking

---

# SCHEDULE VALIDATION

Backend must validate:

1. Date is not past.
2. Time is valid.
3. Service availability.
4. Worker availability.
5. Customer address exists.
6. Worker count is greater than zero.
7. Duration is valid.

---

# BOOKING SCREEN

Bottom navigation:

Bookings

Tabs:

Upcoming

Active

Completed

Cancelled

---

# BOOKING DETAILS SCREEN

Display:

Booking ID

Service

Booking Date

Scheduled Date

Worker Details

Worker Photo

Worker Name

Worker Rating

Booking Address

Map

Status Timeline

Price Breakdown

Payment Status

Customer Instructions

Buttons based on status:

Cancel Booking

Contact Support

Track Worker

Pay Now

Rate Worker

Download Invoice

---

# BOOKING CANCELLATION

Customer can cancel depending on cancellation policy.

Cancellation reasons:

* Changed my mind
* Worker taking too long
* Booked by mistake
* Service no longer required
* Other

Backend should:

1. Validate cancellation eligibility.
2. Calculate cancellation fee.
3. Release assigned workers.
4. Process refund if applicable.
5. Update booking status.
6. Notify worker.

---

# WORKER APP FLOW

## WORKER REGISTRATION

Fields:

Full Name

Mobile Number

Email

Profile Photo

Date of Birth

Gender

Address

Current Location

---

# WORKER PROFESSIONAL DETAILS

Fields:

Worker Type

Skills

Years of Experience

Languages

Daily Rate

Hourly Rate

Working Radius

---

# DOCUMENT VERIFICATION

Support document upload.

Examples:

Government ID

Address Proof

Skill Certificate

Profile Photo

Bank Details

Document status:

PENDING

APPROVED

REJECTED

Admin reviews documents.

Never expose document files publicly without authorization.

---

# WORKER HOME SCREEN

Display:

Availability Toggle

OFFLINE / ONLINE

Today's Jobs

Upcoming Jobs

Today's Earnings

Rating

New Booking Requests

---

# WORKER AVAILABILITY

Worker can select:

AVAILABLE

BUSY

OFFLINE

When worker turns AVAILABLE:

Store:

Current Latitude

Current Longitude

Last Location Update

Location should be updated periodically only with proper user permission.

---

# WORKER ACTIVE JOB FLOW

ACCEPTED

↓

ON_THE_WAY

↓

ARRIVED

↓

START_WORK

↓

COMPLETE_WORK

---

# WORKER EARNINGS

Display:

Today

This Week

This Month

Total Earnings

Pending Payout

Paid Amount

Transaction History

---

# ADMIN MODULE

Create backend APIs designed for an Admin Web Dashboard.

The Android app does not need to contain full Admin functionality unless requested later.

---

# ADMIN FEATURES

## DASHBOARD

Display:

Total Customers

Total Workers

Available Workers

Active Bookings

Completed Bookings

Cancelled Bookings

Today's Revenue

Monthly Revenue

---

# CUSTOMER MANAGEMENT

Admin can:

View Customer

Search Customer

Deactivate Customer

Activate Customer

View Customer Bookings

---

# WORKER MANAGEMENT

Admin can:

View Worker

Approve Worker

Reject Worker

Suspend Worker

Activate Worker

View Documents

View Ratings

View Booking History

---

# SERVICE MANAGEMENT

Admin can:

Create Category

Update Category

Deactivate Category

Create Service

Update Service

Configure Price

Configure Duration

Configure Worker Requirements

---

# BOOKING MANAGEMENT

Admin can:

View All Bookings

Filter Bookings

Search Booking

Assign Worker

Cancel Booking

Resolve Disputes

Process Refund

---

# DATABASE DESIGN

Use PostgreSQL.

Create database schema with proper:

Primary Keys

Foreign Keys

Indexes

Unique Constraints

Audit Fields

---

# REQUIRED TABLES

## USERS

Fields:

id

mobile_number

email

password_hash if password login is enabled

role

status

is_verified

created_at

updated_at

---

# CUSTOMER_PROFILES

id

user_id

full_name

profile_image

gender

created_at

updated_at

---

# WORKER_PROFILES

id

user_id

full_name

profile_image

experience_years

rating

total_jobs

availability_status

verification_status

working_radius

created_at

updated_at

---

# SERVICE_CATEGORIES

id

name

description

icon

image

status

display_order

created_at

updated_at

---

# SERVICES

id

category_id

name

description

pricing_type

base_price

status

estimated_duration

created_at

updated_at

---

# WORKER_SKILLS

id

worker_id

service_id

experience_years

price_override

status

---

# ADDRESSES

id

user_id

address_type

full_address

landmark

city

state

postal_code

latitude

longitude

is_default

created_at

updated_at

---

# BOOKINGS

id

booking_number

customer_id

service_id

booking_type

requested_worker_count

assigned_worker_count

status

scheduled_start_time

estimated_duration

latitude

longitude

address

special_instructions

subtotal

platform_fee

tax

discount

total_amount

payment_status

created_at

updated_at

version

Use version for optimistic locking if required.

---

# BOOKING_WORKERS

id

booking_id

worker_id

status

assigned_at

accepted_at

arrival_at

service_started_at

service_completed_at

earnings

created_at

updated_at

---

# PAYMENTS

id

booking_id

customer_id

payment_gateway

gateway_payment_id

gateway_order_id

amount

currency

payment_status

payment_method

created_at

updated_at

---

# PAYMENT TRANSACTIONS

id

payment_id

transaction_type

amount

status

gateway_response

created_at

---

# REVIEWS

id

booking_id

customer_id

worker_id

rating

comment

created_at

---

# COUPONS

id

code

description

discount_type

discount_value

minimum_order_amount

maximum_discount

start_date

end_date

usage_limit

status

---

# COUPON_USAGE

id

coupon_id

customer_id

booking_id

discount_amount

used_at

---

# NOTIFICATIONS

id

user_id

title

message

notification_type

reference_id

is_read

created_at

---

# BANNERS

id

title

image_url

action_type

action_value

display_order

start_date

end_date

status

---

# WORKER_LOCATIONS

id

worker_id

latitude

longitude

updated_at

Important:

Add indexes for:

worker_id

updated_at

Latitude/Longitude strategy should support efficient nearby search.

Initially use a simple distance calculation.

Future architecture should allow:

PostGIS integration.

---

# SUPPORT_TICKETS

id

user_id

booking_id

subject

description

status

priority

created_at

updated_at

---

# AUDIT_LOGS

id

user_id

action

entity_type

entity_id

old_value

new_value

created_at

---

# ENUMS

Create proper enums.

## UserRole

CUSTOMER

WORKER

ADMIN

---

## UserStatus

ACTIVE

INACTIVE

SUSPENDED

BLOCKED

---

## WorkerAvailability

AVAILABLE

BUSY

OFFLINE

---

## WorkerVerificationStatus

PENDING

APPROVED

REJECTED

---

## BookingType

REAL_TIME

SCHEDULED

---

## BookingStatus

DRAFT

SEARCHING

PARTIALLY_ASSIGNED

WORKER_ASSIGNED

WORKER_ACCEPTED

WORKER_ON_THE_WAY

WORKER_ARRIVED

SERVICE_STARTED

SERVICE_COMPLETED

PAYMENT_PENDING

PAID

CLOSED

CANCELLED

FAILED

NO_WORKER_AVAILABLE

EXPIRED

---

## PaymentStatus

PENDING

PROCESSING

SUCCESS

FAILED

REFUNDED

PARTIALLY_REFUNDED

---

# REST API DESIGN

Use versioned APIs.

Base URL:

/api/v1

---

# AUTH APIs

POST

/auth/send-otp

/auth/verify-otp

/auth/register

/auth/refresh-token

/auth/logout

---

# CUSTOMER APIs

GET

/customers/me

PUT

/customers/me

DELETE

/customers/me

---

# ADDRESS APIs

GET

/addresses

POST

/addresses

PUT

/addresses/{id}

DELETE

/addresses/{id}

PUT

/addresses/{id}/default

---

# CATEGORY APIs

GET

/categories

GET

/categories/{id}

---

# SERVICE APIs

GET

/services

GET

/services/{id}

GET

/services/search

---

# REAL-TIME BOOKING APIs

POST

/bookings/estimate

POST

/bookings/real-time

GET

/bookings/{id}

POST

/bookings/{id}/cancel

POST

/bookings/{id}/workers/accept

POST

/bookings/{id}/start

POST

/bookings/{id}/complete

---

# SCHEDULED BOOKING APIs

POST

/bookings/scheduled

GET

/bookings/upcoming

GET

/bookings/history

---

# WORKER APIs

GET

/workers/me

PUT

/workers/me

POST

/workers/availability

POST

/workers/location

GET

/workers/jobs

GET

/workers/booking-requests

POST

/workers/bookings/{bookingId}/accept

POST

/workers/bookings/{bookingId}/reject

POST

/workers/bookings/{bookingId}/on-the-way

POST

/workers/bookings/{bookingId}/arrived

POST

/workers/bookings/{bookingId}/start

POST

/workers/bookings/{bookingId}/complete

---

# PAYMENT APIs

POST

/payments/create-order

POST

/payments/verify

GET

/payments/{bookingId}

---

# REVIEW APIs

POST

/reviews

GET

/workers/{id}/reviews

---

# NOTIFICATION APIs

GET

/notifications

PUT

/notifications/{id}/read

PUT

/notifications/read-all

---

# ERROR RESPONSE FORMAT

All APIs must return consistent error responses.

Example:

{
"timestamp": "2026-08-29T10:30:00",
"status": 400,
"error": "VALIDATION_ERROR",
"message": "Invalid request",
"path": "/api/v1/bookings",
"errors": [
{
"field": "workerCount",
"message": "Worker count must be greater than zero"
}
]
}

Create:

GlobalExceptionHandler

Use:

@ControllerAdvice

Handle:

ValidationException

ResourceNotFoundException

UnauthorizedException

AccessDeniedException

BookingException

PaymentException

Generic Exception

---

# SECURITY REQUIREMENTS

Implement:

JWT Authentication

Access Token

Refresh Token

Role Based Authorization

Password encryption if passwords are used

OTP expiration

OTP retry limits

Rate limiting architecture

Input validation

SQL injection protection through JPA

Authorization checks

Secure file upload

File type validation

File size validation

Sensitive data masking in logs

Never log:

OTP

JWT token

Passwords

Bank information

Payment secrets

---

# JWT FLOW

Login

↓

OTP Verification

↓

Generate Access Token

↓

Generate Refresh Token

↓

Return Tokens

Android stores tokens securely.

Use:

EncryptedSharedPreferences or secure storage strategy.

When access token expires:

Call refresh API.

If refresh fails:

Logout user.

---

# ANDROID ARCHITECTURE

Use MVVM.

Package structure:

com.easylabour.app

├── data
│   ├── remote
│   ├── local
│   ├── repository
│   └── model
│
├── domain
│   ├── model
│   ├── repository
│   └── usecase
│
├── ui
│   ├── splash
│   ├── onboarding
│   ├── auth
│   ├── home
│   ├── services
│   ├── booking
│   ├── worker
│   ├── profile
│   └── common
│
├── utils
│
├── di
│
└── EasyLabourApplication

---

# ANDROID NETWORK LAYER

Use Retrofit.

Create:

ApiService

ApiClient

AuthInterceptor

TokenAuthenticator

NetworkResult

ApiErrorHandler

Repository classes.

All API calls must handle:

Loading

Success

Error

No Internet

Timeout

Unauthorized

Server Error

---

# ANDROID API RESPONSE WRAPPER

Use consistent handling.

Example:

NetworkResult<T>

States:

Loading

Success

Error

---

# UI REQUIREMENTS

Design should be:

Modern

Clean

Professional

Simple

Mobile-friendly

Similar quality to leading service booking applications.

Use:

Material Components

Rounded Cards

Proper spacing

Consistent typography

Loading indicators

Skeleton loading where appropriate

Empty states

Error states

Retry buttons

---

# HOME SCREEN COMPONENTS

1. Location Header
2. Search Bar
3. Banner Carousel
4. Service Categories
5. Popular Services
6. Real-Time Service Card
7. Scheduled Service Card
8. Recommended Services
9. Bottom Navigation

Do NOT include wallet card on home screen.

Wallet/earnings related information should be available under profile or dedicated screens.

---

# PROFILE SCREEN

Customer profile should contain:

Profile Photo

Name

Mobile Number

Edit Profile

My Addresses

Payment Methods

Wallet

Coupons

Help & Support

Terms and Conditions

Privacy Policy

Logout

Delete Account

---

# NOTIFICATION SYSTEM

Use Firebase Cloud Messaging.

Notification types:

NEW_BOOKING_REQUEST

WORKER_ASSIGNED

WORKER_ACCEPTED

WORKER_ON_THE_WAY

WORKER_ARRIVED

SERVICE_STARTED

SERVICE_COMPLETED

PAYMENT_SUCCESS

PAYMENT_FAILED

BOOKING_CANCELLED

PROMOTIONAL

---

# LOCATION FEATURES

Customer:

Request current location permission only when required.

Worker:

Request location permission for worker availability and active jobs.

Store:

Latitude

Longitude

Timestamp

Do not continuously track location unnecessarily.

Implement battery-conscious location updates.

---

# GOOGLE MAPS FLOW

Booking details:

Display customer location.

Worker app:

Open navigation intent.

Customer app:

Display worker ETA architecture.

For future real-time tracking:

Support WebSocket or polling architecture.

Initially implement API polling cleanly.

---

# REAL-TIME UPDATES

Design architecture to support:

Option 1:

WebSocket

Option 2:

Server-Sent Events

Option 3:

Polling

For initial version:

Use efficient polling.

Example:

Every 10–15 seconds during active booking.

Stop polling when:

Activity closes

Booking completes

Booking is cancelled

---

# PAYMENT ARCHITECTURE

Create payment abstraction.

PaymentService

PaymentGateway

Implement gateway adapters.

Example:

RazorpayPaymentGateway

CashfreePaymentGateway

MockPaymentGateway

Do not directly couple business logic to a specific payment gateway.

Flow:

Create Booking

↓

Calculate Final Amount

↓

Create Payment Order

↓

Return Gateway Order

↓

Android Opens Payment SDK

↓

Payment Callback

↓

Backend Verification

↓

Update Payment Status

↓

Update Booking

Important:

Backend must verify payment.

Never trust only Android payment success callback.

---

# COUPON FLOW

Customer:

Enter Coupon

↓

Backend validates:

Coupon active

Date valid

Minimum amount

Usage limit

User eligibility

↓

Calculate discount

↓

Return updated price

---

# PRICE ESTIMATION SERVICE

Create:

PricingService

Input:

Service

Worker Count

Duration

Location

Coupon

Output:

Base Price

Worker Price

Platform Fee

Tax

Discount

Final Total

All calculations must happen on backend.

Use BigDecimal for money.

Never use float or double for monetary values.

---

# BOOKING NUMBER GENERATION

Generate unique human-readable booking number.

Example:

EL202608290001

Must be unique.

Do not use predictable IDs as security identifiers.

Use database ID internally and booking number externally where appropriate.

---

# FILE UPLOAD

Support:

Profile images

Worker documents

Service images

Booking issue images

Create APIs:

POST

/files/upload

Requirements:

Validate MIME type.

Validate file extension.

Validate maximum size.

Generate unique filename.

Store metadata.

Use local storage abstraction initially.

Architecture should allow future migration to:

AWS S3

Google Cloud Storage

Azure Blob Storage

---

# DATABASE MIGRATION

Use Flyway.

Create migration files.

Example:

V1__create_users.sql

V2__create_customer_profiles.sql

V3__create_worker_profiles.sql

V4__create_categories.sql

Continue migrations properly.

Never modify an already deployed migration.

Create new migration.

---

# LOGGING

Use structured logging.

Log:

Request ID

User ID where safe

Booking ID

Service execution errors

Payment errors without sensitive data

Do NOT log secrets.

---

# TESTING REQUIREMENTS

Create tests.

## Backend

Unit Tests:

Service Layer

Pricing Service

Worker Matching Service

Booking Service

Payment Service

Integration Tests:

Authentication

Booking Creation

Worker Assignment

Payment Verification

---

## Android

Test:

ViewModels

Repositories

Critical business flows

---

# IMPORTANT BUSINESS RULES

## BOOKING CREATION

Customer cannot create booking if:

Service inactive

Worker count invalid

Address invalid

Date invalid

Customer inactive

---

## WORKER ACCEPTANCE

Worker cannot accept booking if:

Worker offline

Worker suspended

Worker not verified

Worker already busy

Worker skill does not match

Booking already full

---

# BOOKING COMPLETION

Worker cannot complete booking before:

Worker assignment exists

Booking is active

Service started

---

# CONCURRENCY REQUIREMENTS

Booking assignment is critical.

Implement transaction-safe worker assignment.

Prevent:

Two workers taking same single-worker slot.

Prevent:

Worker accepting multiple conflicting bookings.

Use:

@Transactional

Optimistic locking or pessimistic locking where justified.

Document the approach used.

---

# ADMIN API SECURITY

Admin APIs must require:

ROLE_ADMIN

Example:

/api/v1/admin/**

Use method security where appropriate.

---

# API DOCUMENTATION

Integrate Swagger/OpenAPI.

Document:

Request

Response

Authentication

Error responses

Required roles

Example:

Customer APIs

Worker APIs

Admin APIs

---

# ENVIRONMENT CONFIGURATION

Use environment variables.

Examples:

DB_HOST

DB_PORT

DB_NAME

DB_USERNAME

DB_PASSWORD

JWT_SECRET

JWT_EXPIRATION

REFRESH_TOKEN_EXPIRATION

GOOGLE_MAPS_API_KEY

FCM_CONFIGURATION

PAYMENT_GATEWAY_KEY

Never commit secrets to Git.

Provide:

application.yml

application-dev.yml

application-prod.yml

.example.env

---

# DOCKER

Create Docker support.

Backend:

Dockerfile

docker-compose.yml

Include:

Spring Boot

PostgreSQL

Optional pgAdmin for development

---

# PROJECT IMPLEMENTATION PHASES

Do NOT attempt to generate the entire application in one response.

Implement in phases.

---

# PHASE 1 – PROJECT INITIALIZATION

Create:

Backend project.

Android project.

Database configuration.

Flyway.

Basic package structure.

Git ignore.

Environment configuration.

README.

---

# PHASE 2 – AUTHENTICATION

Implement:

OTP architecture.

Mock OTP provider for development.

JWT.

Refresh token.

User roles.

Registration.

Login.

Android login screens.

---

# PHASE 3 – PROFILE AND ADDRESS

Implement:

Customer profile.

Worker profile.

Address management.

Location support.

---

# PHASE 4 – SERVICE MANAGEMENT

Implement:

Categories.

Services.

Search.

Admin APIs.

Android service browsing.

---

# PHASE 5 – REAL-TIME BOOKING

Implement:

Price estimation.

Booking creation.

Worker matching.

Booking assignment.

Booking status updates.

Android booking flow.

---

# PHASE 6 – SCHEDULED BOOKINGS

Implement:

Date/time selection.

Worker scheduling.

Conflict validation.

Booking reminders architecture.

---

# PHASE 7 – WORKER APPLICATION

Implement:

Worker dashboard.

Availability.

Booking requests.

Job management.

Earnings.

Location updates.

---

# PHASE 8 – PAYMENTS

Implement:

Payment abstraction.

Mock gateway first.

Real gateway integration architecture.

Payment verification.

Refund architecture.

---

# PHASE 9 – NOTIFICATIONS

Implement:

FCM integration.

Notification storage.

Notification screens.

---

# PHASE 10 – REVIEWS AND SUPPORT

Implement:

Ratings.

Reviews.

Support tickets.

Complaint flow.

---

# PHASE 11 – ADMIN

Implement:

Admin APIs.

Dashboard statistics.

Worker approval.

Booking management.

Service management.

Coupon management.

Banner management.

---

# PHASE 12 – TESTING AND HARDENING

Perform:

Security review.

Error handling review.

Concurrency review.

Performance review.

Database indexing review.

API validation review.

Testing.

---

# GEMINI CLI WORKING INSTRUCTIONS

IMPORTANT:

Before writing code:

1. Analyze the existing project structure.
2. Do not overwrite working files unnecessarily.
3. Identify existing architecture.
4. Follow existing naming conventions.
5. Check existing dependencies.
6. Ask for clarification only if absolutely necessary.

---

# CODE GENERATION RULES

When implementing a feature:

1. First explain which files will be created or modified.
2. Then implement the feature.
3. Keep changes minimal and focused.
4. Do not rewrite unrelated files.
5. Do not introduce duplicate classes.
6. Reuse existing utilities.
7. Follow SOLID principles.
8. Use clean code.
9. Add validation.
10. Add error handling.
11. Add logging where appropriate.
12. Add tests for critical logic.

---

# RESPONSE FORMAT FOR GEMINI CLI

For every implementation task, respond in this format.

## 1. ANALYSIS

Briefly explain:

* Existing architecture
* Required changes
* Risks
* Dependencies

Maximum concise explanation.

---

## 2. IMPLEMENTATION PLAN

List:

Files to create.

Files to modify.

Database changes.

API changes.

---

## 3. CODE

Provide or apply complete working code.

Do not provide pseudo code unless explicitly requested.

---

## 4. VALIDATION

Explain:

How to run.

How to test.

Example API request.

Expected response.

---

# IMPORTANT DEVELOPMENT BEHAVIOR

Never:

* Generate fake production functionality.
* Hardcode API secrets.
* Hardcode prices.
* Trust client-side payment confirmation.
* Expose database entities directly.
* Store plain passwords.
* Ignore authorization.
* Ignore validation.
* Use float/double for money.
* Create massive God classes.
* Put business logic in controllers.
* Duplicate business logic.
* Modify unrelated files.
* Break existing functionality.

---

# BACKEND LAYER RESPONSIBILITIES

## CONTROLLER

Responsible for:

Request mapping.

Request validation.

Calling services.

Returning DTOs.

No business logic.

---

## SERVICE

Responsible for:

Business logic.

Transactions.

Validation beyond request validation.

Orchestration.

---

## REPOSITORY

Responsible for:

Database access.

Queries.

No business logic.

---

## DTO

Separate:

Request DTO.

Response DTO.

Do not expose entities.

---

# REQUIRED BACKEND PACKAGE STRUCTURE

com.easylabour

├── config
├── controller
├── dto
│   ├── request
│   └── response
├── entity
├── enums
├── exception
├── repository
├── security
├── service
│   └── impl
├── mapper
├── util
└── EasyLabourApplication

---

# REQUIRED ANDROID PACKAGE STRUCTURE

Use feature-based organization where appropriate.

com.easylabour.app

├── core
│   ├── network
│   ├── security
│   ├── utils
│   └── common
│
├── data
│   ├── remote
│   ├── local
│   └── repository
│
├── domain
│   ├── model
│   └── usecase
│
├── feature
│   ├── splash
│   ├── onboarding
│   ├── auth
│   ├── home
│   ├── services
│   ├── booking
│   ├── worker
│   ├── notifications
│   ├── messages
│   └── profile
│
└── EasyLabourApplication

---

# MVP PRIORITY

First create a fully working MVP.

MVP must include:

1. Customer registration/login
2. Customer profile
3. Address management
4. Categories
5. Services
6. Real-time booking
7. Scheduled booking
8. Worker registration
9. Worker approval architecture
10. Worker availability
11. Worker booking acceptance
12. Booking status flow
13. Price estimation
14. Booking history
15. Basic notifications
16. Ratings

Do not start advanced features before MVP works.

---

# FIRST TASK

Start by inspecting the existing project.

Then provide:

1. Current project structure analysis.
2. Missing components.
3. Recommended architecture.
4. Complete implementation roadmap.
5. Phase 1 implementation.

Do not implement every phase at once.

Implement Phase 1 first.

After Phase 1 is complete and builds successfully, proceed to the next phase.

Always ensure the project compiles before moving forward.
