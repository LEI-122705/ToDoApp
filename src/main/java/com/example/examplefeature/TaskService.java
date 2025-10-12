package com.example.examplefeature;

import com.example.email.EmailService;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final EmailService emailService;

    public TaskService(TaskRepository taskRepository, EmailService emailService) {
        this.taskRepository = taskRepository;
        this.emailService = emailService;
    }

    @Transactional
    public void create(String description, @Nullable LocalDate dueDate) {
        if ("fail".equals(description)) {
            throw new RuntimeException("This is for testing the error handler");
        }
        var task = new Task(description, Instant.now());
        task.setDueDate(dueDate);
        taskRepository.saveAndFlush(task);

        // Exemplo: depois de criar a task, dispara envio de email de confirmação (simulado, assíncrono)
        String to = "user@example.com";
        String subject = "Confirmação: tarefa criada";
        String body = "A sua tarefa \"" + description + "\" foi criada com sucesso às " + Instant.now() + ".";

        emailService.sendEmail(to, subject, body);
    }

    @Transactional(readOnly = true)
    public List<Task> list(Pageable pageable) {
        return taskRepository.findAllBy(pageable).toList();
    }

}
