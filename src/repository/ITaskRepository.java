package repository;

import model.Task;
import java.util.List;

/**
 * Defines the contract for task storage operations.
 * This allows swapping the storage mechanism (JSON, DB, Memory) without changing logic.
 */
public interface ITaskRepository {
    void add(Task task);
    void update(Task task);
    void delete(int id);
    Task getById(int id);
    List<Task> listAll();
}