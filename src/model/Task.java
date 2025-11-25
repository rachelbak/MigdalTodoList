package model;

/**
 * Represents a Todo Task.
 * This is a POJO (Plain Old Java Object) containing task details.
 */
public class Task {
    private int id;
    private String title;
    private String description;
    private Status status;

    // Constructor for new tasks (ID will be assigned by Repository)
    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = Status.NEW; // Default status
    }

    // Constructor for loading from file (with ID and specific Status)
    public Task(int id, String title, String description, Status status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    // --- Getters and Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task [ID=" + id + ", Title=" + title + ", Description=" + description + ", Status=" + status + "]";
    }
}