package service;

import model.Status;
import model.Task;
import repository.ITaskRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer responsible for business logic and data manipulation.
 */
public class TaskService {

    private final ITaskRepository repository;

    public TaskService(ITaskRepository repository) {
        this.repository = repository;
    }

    /**
     * Creates a new task and saves it to the repository.
     * @param title The title of the task.
     * @param description The description of the task.
     */

    public void addTask(String title, String description) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty.");
        }

        if (description == null) {
            description = "";
        }

        Task newTask = new Task(title, description);
        repository.add(newTask);
    }

    /**
     * Deletes a task by its ID.
     * @param id The unique identifier of the task.
     */
    public boolean deleteTask(int id) {
        Task task = repository.getById(id);
        if (task != null) {
            repository.delete(id);
            return true;
        }
        return false;
    }

    /**
     * Retrieves all existing tasks.
     * @return A list of all tasks.
     */
    public List<Task> getAllTasks() {
        return repository.listAll();
    }

    /**
     * Retrieves a single task by ID.
     */
    public Task getTaskById(int id) {
        return repository.getById(id);
    }

    /**
     * Updates title and description of an existing task.
     * Only updates fields that are not null/empty.
     */
    public boolean updateTaskDetails(int id, String newTitle, String newDescription) {
        Task task = repository.getById(id);
        if (task != null) {
            if (newTitle != null && !newTitle.trim().isEmpty()) {
                task.setTitle(newTitle);
            }
            if (newDescription != null && !newDescription.trim().isEmpty()) {
                task.setDescription(newDescription);
            }
            repository.update(task);
            return true;
        }
        return false;
    }

    /**
     * Updates the status of a specific task to DONE.
     * @param id The ID of the task to update.
     */
    public boolean markTaskAsDone(int id) {
        Task task = repository.getById(id);
        if (task != null) {
            task.setStatus(Status.DONE);
            repository.update(task);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Searches for tasks containing the given text in their title or description.
     * The search is case-insensitive.
     * @param text The text to search for.
     * @return A list of matching tasks.
     */
    public List<Task> searchTasks(String text) {
        if (text == null || text.trim().isEmpty()) {
            return repository.listAll();
        }
        String lowerText = text.toLowerCase();

        return repository.listAll().stream()
                .filter(t -> t.getTitle().toLowerCase().contains(lowerText) ||
                        t.getDescription().toLowerCase().contains(lowerText))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all tasks sorted by their status logic.
     * Order: NEW -> IN_PROGRESS -> DONE.
     * @return A sorted list of tasks.
     */
    public List<Task> getTasksSortedByStatus() {
        return repository.listAll().stream()
                .sorted(Comparator.comparing(Task::getStatus))
                .collect(Collectors.toList());
    }
}