package com.example.examplefeature.ui;

import com.example.base.ui.component.ViewToolbar;
import com.example.examplefeature.Task;
import com.example.examplefeature.TaskService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSelectionColumn;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;

import static com.vaadin.flow.spring.data.VaadinSpringDataHelpers.toSpringPageRequest;

import com.itextpdf.text.pdf.PdfWriter ; //NOTE! A minha versao e o do 5, se tiver o statement do kernel e para a versao 7!!
import com.itextpdf.text.Document; //mesma coisa para a palavra layout como o kernel
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import java.io.*;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.server.StreamResource;

@Route("")
@PageTitle("Task List")
@Menu(order = 0, icon = "vaadin:clipboard-check", title = "Task List")
class TaskListView extends Main {

    private final TaskService taskService;

    final TextField description;
    final DatePicker dueDate;
    final Button createBtn;
    final Grid<Task> taskGrid;
    final Anchor downloadLink;

    TaskListView(TaskService taskService) {
        this.taskService = taskService;

        description = new TextField();
        description.setPlaceholder("What do you want to do?");
        description.setAriaLabel("Task description");
        description.setMaxLength(Task.DESCRIPTION_MAX_LENGTH);
        description.setMinWidth("20em");

        dueDate = new DatePicker();
        dueDate.setPlaceholder("Due date");
        dueDate.setAriaLabel("Due date");

        createBtn = new Button("Create", event -> createTask());
        createBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(getLocale())
                .withZone(ZoneId.systemDefault());
        var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(getLocale());

        taskGrid = new Grid<>();
        taskGrid.setItems(query -> taskService.list(toSpringPageRequest(query)).stream());
        taskGrid.addColumn(Task::getDescription).setHeader("Description");
        taskGrid.addColumn(task -> Optional.ofNullable(task.getDueDate()).map(dateFormatter::format).orElse("Never"))
                .setHeader("Due Date");
        taskGrid.addColumn(task -> dateTimeFormatter.format(task.getCreationDate())).setHeader("Creation Date");
        taskGrid.setSizeFull();

        setSizeFull();
        addClassNames(LumoUtility.BoxSizing.BORDER, LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Padding.MEDIUM, LumoUtility.Gap.SMALL);

        downloadLink= new Anchor(printPdf(), "Pdf");
        downloadLink.getElement().setAttribute("download", "lista.pdf");

        add(new ViewToolbar("Task List", ViewToolbar.group(description, dueDate, createBtn)));
        add(taskGrid);
        add(downloadLink);
    }

    private void createTask() {
        taskService.createTask(description.getValue(), dueDate.getValue());
        taskGrid.getDataProvider().refreshAll();
        description.clear();
        dueDate.clear();
        Notification.show("Task added", 3000, Notification.Position.BOTTOM_END)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private StreamResource printPdf(){
        return new StreamResource("lista.pdf", () -> {
            ByteArrayOutputStream byteA= new ByteArrayOutputStream();

            try{
                //criar documento q vai ter o pdf
                Document doc= new Document();
                PdfWriter.getInstance(doc, byteA);
                doc.open();

                //criar lista para guardar elementos
                List l= new List(List.UNORDERED); //ORDERED da numbered lista

                //guardar todos os elementos
                DateTimeFormatter dateForm = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(getLocale());//para formatar datas corretamente

                var tasks= taskService.listAll(); //assim nao tenho de converter a tabela, faz automaticamente :/
                for(Task t: tasks){
                    String rowText = t.getDescription() + " : " +
                            (t.getDueDate() != null ? dateForm.format(t.getDueDate()) : "No due date");

                    System.out.println(rowText);
                    l.add(new ListItem(rowText));
                }

                //add content to document
                Paragraph title= new Paragraph("To Do:");
                doc.add(title);
                doc.add(l);
                doc.close();
            }
            catch(Exception e){}

            return new ByteArrayInputStream(byteA.toByteArray());
        });
    }
}
