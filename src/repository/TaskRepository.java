package repository;

import model.Status;
import model.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages task persistence using a local JSON file.
 * Handles manual parsing and writing of JSON data without external libraries.
 */
public class TaskRepository implements ITaskRepository {

    private static final String FILE_PATH = "tasks.json";
    private final List<Task> tasks;
    private int nextId = 1;

    public TaskRepository() {
        this.tasks = new ArrayList<>();
        loadDataFromFile();
    }

    //  Public Operations
    @Override
    public void add(Task task) {
        task.setId(nextId++);
        tasks.add(task);
        saveDataToFile();
    }

    @Override
    public void update(Task updatedTask) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == updatedTask.getId()) {
                tasks.set(i, updatedTask);
                saveDataToFile();
                return;
            }
        }
    }

    @Override
    public void delete(int id) {
        tasks.removeIf(t -> t.getId() == id);
        saveDataToFile();
    }

    @Override
    public Task getById(int id) {
        return tasks.stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Task> listAll() {
        return new ArrayList<>(tasks);
    }

    //  File Handling & Manual JSON Parsing

    private void loadDataFromFile() {
        Path path = Paths.get(FILE_PATH);
        if (!Files.exists(path)) {
            return;
        }

        try {
            String content = Files.readString(path).trim();
            if (content.isEmpty() || content.equals("[]")) {
                return;
            }

            // Remove outer brackets [ ]
            if (content.startsWith("[") && content.endsWith("]")) {
                content = content.substring(1, content.length() - 1).trim();
            }

            if (content.isEmpty()) {
                return;
            }

            String[] taskObjects = content.split("(?<=\\}),\\s*(?=\\{)");

            for (String taskJson : taskObjects) {
                parseTask(taskJson);
            }


            this.nextId = tasks.stream()
                    .mapToInt(Task::getId)
                    .max()
                    .orElse(0) + 1;

        } catch (IOException e) {
            System.err.println("Error loading tasks: " + e.getMessage());
        }
    }

    private void parseTask(String json) {
        json = json.trim();
        if (json.isEmpty()) return;

        // Remove surrounding curly braces
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}")) json = json.substring(0, json.length() - 1);

        json = json.trim();
        if (json.isEmpty()) return;

        int id = 0;
        String title = "";
        String description = "";
        Status status = Status.NEW;

        String[] fields = json.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        for (String field : fields) {
            String[] keyValue = field.split(":", 2);
            if (keyValue.length < 2) continue;

            String key = keyValue[0].trim().replace("\"", "");
            String value = keyValue[1].trim();

            if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
                value = value.substring(1, value.length() - 1);
            }

            value = unescape(value);

            try {
                switch (key) {
                    case "id" -> id = Integer.parseInt(value);
                    case "title" -> title = value;
                    case "description" -> description = value;
                    case "status" -> status = Status.valueOf(value);
                }
            } catch (Exception e) {
                // Ignore invalid fields
            }
        }

        if (id > 0 || !title.isEmpty()) {
            tasks.add(new Task(id, title, description, status));
        }
    }

    private void saveDataToFile() {
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);


            json.append(String.format(
                    "  { \"id\": %d, \"title\": \"%s\", \"description\": \"%s\", \"status\": \"%s\" }",
                    t.getId(),
                    escape(t.getTitle()),
                    escape(t.getDescription()),
                    t.getStatus()
            ));

            if (i < tasks.size() - 1) {
                json.append(",\n");
            }
        }
        json.append("\n]");

        try {
            Files.writeString(Paths.get(FILE_PATH), json.toString(),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Error saving tasks: " + e.getMessage());
        }
    }

    //  Helper Methods for Data Integrity

    private String escape(String raw) {
        if (raw == null) return "";
        return raw.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }

    private String unescape(String escaped) {
        if (escaped == null) return "";
        return escaped.replace("\\\"", "\"")
                .replace("\\n", "\n")
                .replace("\\\\", "\\");
    }
}