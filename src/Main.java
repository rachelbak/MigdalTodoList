import model.Task;
import repository.ITaskRepository;
import repository.TaskRepository;
import service.TaskService;

import java.util.List;
import java.util.Scanner;

/**
 * The entry point of the application.
 * Provides a text-based user interface (CLI) for managing tasks.
 * This class handles all user input/output and delegates logic to the TaskService.
 */
public class Main {

    // Dependencies are injected manually (Simulating basic Dependency Injection)
    private static final ITaskRepository repository = new TaskRepository();
    private static final TaskService taskService = new TaskService(repository);
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * The main execution loop of the application.
     * Displays the menu and processes user choices until exit is requested.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        System.out.println("Welcome to Migdal Todo List App!");
        boolean running = true;

        while (running) {
            printMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> addTask();
                case "2" -> deleteTask();
                case "3" -> getTaskById();
                case "4" -> updateTask();
                case "5" -> markTaskAsDone();
                case "6" -> searchTasks();
                case "7" -> listAllTasks();
                case "8" -> listSortedTasks();
                case "0" -> {
                    System.out.println("Exiting... Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
            System.out.println(); // Empty line for better readability
        }
    }

    /**
     * Displays the available menu options to the console.
     */
    private static void printMenu() {
        System.out.println("--- Main Menu ---");
        System.out.println("1. Add New Task");
        System.out.println("2. Delete Task");
        System.out.println("3. Get Task by ID");
        System.out.println("4. Update Task Details");
        System.out.println("5. Mark Task as DONE");
        System.out.println("6. Search Tasks");
        System.out.println("7. List All Tasks");
        System.out.println("8. List Tasks Sorted by Status");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }

    /**
     * UI flow for adding a new task.
     * Captures title and description from the user and calls the service layer.
     */
    private static void addTask() {
        System.out.print("Enter Task Title: ");
        String title = scanner.nextLine();

        System.out.print("Enter Task Description: ");
        String description = scanner.nextLine();

        try {
            taskService.addTask(title, description);
            System.out.println("Task added successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * UI flow for deleting a task.
     * Requests an ID from the user and attempts to delete it via the service.
     */
    private static void deleteTask() {
        int id = readIntInput("Enter ID to delete: ");
        if (id != -1) {
            boolean isDeleted = taskService.deleteTask(id);
            if (isDeleted) {
                System.out.println("Success: Task deleted.");
            } else {
                System.out.println("Error: Task with ID " + id + " not found.");
            }
        }
    }

    /**
     * Retrieves and displays a specific task based on user-provided ID.
     */
    private static void getTaskById() {
        int id = readIntInput("Enter ID to view: ");
        if (id != -1) {
            Task task = taskService.getTaskById(id);
            if (task != null) {
                System.out.println("Task Found: " + task);
            } else {
                System.out.println("Error: Task with ID " + id + " not found.");
            }
        }
    }

    /**
     * UI flow for updating an existing task.
     * Allows the user to press Enter to skip fields they don't want to change.
     */
    private static void updateTask() {
        int id = readIntInput("Enter ID to update: ");
        if (id != -1) {
            if (taskService.getTaskById(id) == null) {
                System.out.println("Error: Task with ID " + id + " not found.");
                return;
            }

            System.out.println("Enter new details (press Enter to keep current value):");

            System.out.print("New Title: ");
            String title = scanner.nextLine();

            System.out.print("New Description: ");
            String description = scanner.nextLine();

            boolean updated = taskService.updateTaskDetails(id, title, description);
            if (updated) {
                System.out.println("Task updated successfully.");
            }
        }
    }

    /**
     * Updates the status of a specific task to DONE.
     */
    private static void markTaskAsDone() {
        int id = readIntInput("Enter ID to mark as DONE: ");
        if (id != -1) {
            boolean success = taskService.markTaskAsDone(id);
            if (success) {
                System.out.println("Status updated.");
            } else {
                System.out.println("Warning: Task with ID " + id + " not found.");
            }
        }
    }

    /**
     * Searches for tasks containing a specific string in their title or description.
     */
    private static void searchTasks() {
        System.out.print("Enter text to search: ");
        String text = scanner.nextLine();
        List<Task> results = taskService.searchTasks(text);

        if (results.isEmpty()) {
            System.out.println("No tasks found matching: " + text);
        } else {
            System.out.println("Found " + results.size() + " tasks:");
            results.forEach(System.out::println);
        }
    }

    /**
     * Lists all tasks currently in the repository without sorting.
     */
    private static void listAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        if (tasks.isEmpty()) {
            System.out.println("No tasks available.");
        } else {
            System.out.println("--- All Tasks ---");
            tasks.forEach(System.out::println);
        }
    }

    /**
     * Lists tasks sorted by their status (NEW -> IN_PROGRESS -> DONE).
     */
    private static void listSortedTasks() {
        List<Task> tasks = taskService.getTasksSortedByStatus();
        if (tasks.isEmpty()) {
            System.out.println("No tasks available.");
        } else {
            System.out.println("--- Tasks Sorted by Status (NEW -> DONE) ---");
            tasks.forEach(System.out::println);
        }
    }

    /**
     * Helper method to safely read integers from the user.
     * Prevents the application from crashing if the user enters non-numeric text.
     *
     * @param prompt The message to display to the user.
     * @return The parsed integer, or -1 if the input was invalid.
     */
    private static int readIntInput(String prompt) {
        System.out.print(prompt);
        try {
            String input = scanner.nextLine();
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a number.");
            return -1;
        }
    }
}