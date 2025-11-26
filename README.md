# Migdal Home Assignment - Todo List

This project contains my solution for the developer position assignment.
The application is written in standard Java (no external libraries) and manages data using a local JSON file.

## Project Structure

I have organized the solution into the 3 requested parts:

1. **Part 1: Server Side** - The main logic is located under `src/` (split into Service, Repository, and Model layers).
2. **Part 2: Algorithms** - [Link to the solution code](src/algorithms/IncreasingSubsequences.java).
3. **Part 3: System Design** - [Link to the design document](DESIGN.md).

---

## Part 1: Features (Todo App)

The application implements a full CLI menu with the following capabilities:

- **Add** new tasks.
- **Delete** tasks by ID.
- **Get Task by ID** (View specific details).
- **Update Task** (Edit Title & Description).
- **Mark task as DONE**.
- **Search** tasks by text (in Title or Description).
- **List All** tasks.
- **List Sorted** tasks (Order: NEW → IN_PROGRESS → DONE).
- **Persistence:** Data is saved automatically to `tasks.json`.

### Technical Highlights
- **Custom JSON Parser:** Since external libraries were not allowed, I implemented a robust regex-based parser to handle data integrity safely.
- **Architecture:** I used Dependency Injection between the `Main`, `Service`, and `Repository` layers to ensure the code is modular and testable.

## Installation & Execution

### 1. Clone the Repository
```bash
git clone https://github.com/rachelbak/MigdalTodoList.git
cd MigdalTodoList
```

### 2. How to Run
1. Make sure you have JDK 21 (or higher) installed.
2. Open the project in IntelliJ IDEA.
3. Run the Main class (`src/Main.java`).
4. Follow the instructions in the terminal.

---