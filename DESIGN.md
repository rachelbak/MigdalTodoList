# Part 3: System Design & Architecture

## 1. Library System - REST API Design

**Goal:** Design a robust REST API for a Library system.

### A. Endpoints & Operations

I designed the API following standard RESTful conventions (Resources & Verbs), ensuring clarity and scalability.

| Method | Endpoint | Description | Status Code |
|--------|----------|-------------|-------------|
| **GET** | `/api/books` | Retrieve all books (supports pagination). | 200 OK |
| **GET** | `/api/books/{id}` | Get specific book details. | 200 OK |
| **POST** | `/api/books` | Create a new book entry. | 201 Created |
| **PUT** | `/api/books/{id}` | Update book details. | 200 OK |
| **DELETE** | `/api/books/{id}` | Remove a book. | 204 No Content |
| **POST** | `/api/books/{id}/borrow` | Action: Change status to LOANED. | 200 OK |
| **POST** | `/api/books/{id}/return` | Action: Change status to AVAILABLE. | 200 OK |

**Design Note:** I initially considered using `PATCH /api/books/{id}` for borrow/return operations, but decided on separate endpoints (`/borrow`, `/return`) because they represent distinct business actions rather than simple field updates. This makes the API more explicit and easier to understand.

### B. Data Transfer Objects (DTOs)

I chose to use DTOs to decouple the internal database entities from the external API layer. This provides better security and flexibility.

**BookDTO Structure:**
```json
{
  "id": 1,
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "isbn": "978-0132350884",
  "isAvailable": true,
  "copiesAvailable": 5
}
```

**BorrowRequest:**
```json
{
  "userId": 101,
  "dueDate": "2025-12-01"
}
```

### C. Validation Logic

For the `POST /api/books` endpoint, I enforce the following mandatory fields to ensure data integrity:

- **title & author**: Basic identification.
- **isbn**: Unique global identifier (must be valid format).
- **quantity**: Must be at least 1.

---

## 2. Orders & Payments System Design

**Goal:** Design a secure "Orders + Payments" architecture.

### A. Class Design & Responsibilities

1. **Order**: The root entity. Manages the lifecycle (PENDING → PAID → SHIPPED), holds the total amount, and the list of items.
2. **OrderItem**: Represents a line item (Product + Quantity + Snapshot of price at purchase time).
3. **Payment**: Encapsulates the financial transaction. Stores the external gateway reference ID and timestamp.
4. **Customer**: Stores user profile and shipping details.

### B. Ensuring Payment-Order Integrity

To ensure that a payment is strictly and correctly linked to an order, I implemented a 3-layer safety mechanism:

1. **Foreign Key Constraint**: The Payment class must hold a mandatory `orderId` reference.
2. **Pre-Charge Validation**: Before calling the payment gateway, the system verifies:
    - The order exists and is in PENDING status
    - `paymentAmount == orderTotal` (prevents amount manipulation)
3. **Transactional Atomicity**: The creation of the Payment record and the update of the Order status to "PAID" must occur within a single database transaction. If one fails, both roll back.

**Implementation Example (Java Logic):**
```java
@Transactional  // Ensures database rollback if any step fails
public void processPayment(Order order, PaymentDetails details) {
    // 1. Validate Order State
    if (order.getStatus() != OrderStatus.PENDING) {
        throw new IllegalStateException("Order is already processed");
    }

    // 2. Validate Amount
    if (!order.getTotalAmount().equals(details.getAmount())) {
        throw new IllegalArgumentException("Payment amount mismatch");
    }

    // 3. Execute Payment (via external gateway)
    var result = paymentGateway.charge(details);

    if (result.isSuccess()) {
        // 4. ATOMIC OPERATION: Both must succeed or both rollback
        // A. Create payment record with gateway transaction ID
        Payment payment = new Payment(order.getId(), result.getTransactionId());
        paymentRepo.save(payment);

        // B. Update order status
        order.setStatus(OrderStatus.PAID);
        orderRepo.save(order);
        
        System.out.println("Order " + order.getId() + " paid successfully.");
    } else {
        throw new PaymentFailedException("Transaction declined: " + result.getError());
    }
}
```

**Why @Transactional is Critical:**
Without this annotation, there's a risk that the payment record gets saved but the order status update fails (or vice versa), leading to data inconsistency. The transaction ensures both operations succeed together or fail together.