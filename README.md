# 🚀 Overview
The system manages a fleet of cars across three categories (Sedan, SUV, Van) with limited inventory. It provides a thread-safe mechanism to reserve vehicles while preventing double-bookings and overlapping schedules.

# 🛠 Tech Stack
- Language: Java 8+
- Testing Framework: JUnit 4.11
- Build Tool: Maven 3.9+
  
# 🏗 Architectural Design
- Models: Reservation and CarType act as immutable data carriers.
- InventoryManager: Acts as the "Source of Truth" for fleet capacity and existing bookings. It encapsulates the core availability logic.
- CarRentalService: The primary entry point (Service Layer) that orchestrates validations and reservation workflows.
- Custom Exceptions: Uses specific exceptions (e.g., NoAvailabilityException) to provide clear feedback on failure states.
