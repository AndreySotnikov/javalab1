package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private ObservableList<Student> studentsData = FXCollections.observableArrayList();

    DBManager dbManager = new DBManager();


    @FXML
    private Button btnAdd;

    @FXML
    private TableView<Student> studentTable;

    @FXML
    private TableColumn<Student, String> fioColumn;

    @FXML
    private TableColumn<Student, Integer> courseColumn;

    @FXML
    private TableColumn<Student, Integer> groupColumn;

    @FXML
    private TableColumn<Student, List<Integer>> marksColumn;

    @FXML
    private Label trash;


    public void showStudent() throws SQLException, IOException {
        Student st = (Student) studentTable.getSelectionModel().getSelectedItem();
        if (st != null) {
            DTO.setInstance(st);
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(
                    Controller.class.getResource("add.fxml"));
            stage.setScene(new Scene(root));
            stage.setTitle("Show/Edit student");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.showAndWait();
            showStudents();
        }

    }

    @FXML
    public void onAddClick(ActionEvent event) throws IOException, SQLException {
        DTO.setName();
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(
                Controller.class.getResource("add.fxml"));
        stage.setScene(new Scene(root));
        stage.setTitle("Add a new student");
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(
                ((Node) event.getSource()).getScene().getWindow());
        stage.showAndWait();
        showStudents();
    }


    public void showStudents() throws SQLException {
        studentsData.clear();
        studentsData.addAll(dbManager.findAll());
        fioColumn.setCellValueFactory(new PropertyValueFactory<Student, String>("fio"));
        courseColumn.setCellValueFactory(new PropertyValueFactory<Student, Integer>("course"));
        groupColumn.setCellValueFactory(new PropertyValueFactory<Student, Integer>("group"));
        marksColumn.setCellValueFactory(new PropertyValueFactory<Student, List<Integer>>("marks"));
        studentTable.setItems(studentsData);
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            showStudents();
            studentTable.setRowFactory(tv -> {
                TableRow<Student> row = new TableRow<Student>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && (!row.isEmpty())) {
                        try {
                            showStudent();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                return row;
            });


            studentTable.setOnDragDetected(new EventHandler<MouseEvent>() {
                @Override
                public void handle(final MouseEvent me) {
                    log("setOnDragDetected(" + me + ")");
                    final Dragboard db = studentTable.startDragAndDrop(TransferMode.MOVE);
                    final ClipboardContent content = new ClipboardContent();
                    Student temp = studentTable.getSelectionModel().getSelectedItem();
                    content.putString(temp.getFio() + "/" + temp.getCourse() + "/" + temp.getGroup());
                    db.setContent(content);
                    me.consume();
                }
            });
            studentTable.setOnDragEntered(new EventHandler<DragEvent>() {
                @Override
                public void handle(final DragEvent de) {
                }
            });

            trash.setOnDragOver(new EventHandler<DragEvent>() {
                public void handle(DragEvent event) {
                    if (event.getGestureSource() != trash &&
                            event.getDragboard().hasString()) {
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    }

                    event.consume();
                }
            });

            trash.setOnDragEntered(new EventHandler<DragEvent>() {
                public void handle(DragEvent event) {
                    if (event.getGestureSource() != trash &&
                            event.getDragboard().hasString()) {
                        trash.setText("DROP");
                    }

                    event.consume();
                }
            });

            trash.setOnDragDropped(new EventHandler<DragEvent>() {
                public void handle(DragEvent event) {
                    Dragboard db = event.getDragboard();
                    boolean success = false;
                    if (db.hasString()) {
                        try {
                            String[] ar = db.getString().split("/");
                            dbManager.delete(ar[0], Integer.parseInt(ar[1]), Integer.parseInt(ar[2]));
                            showStudents();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        success = true;
                    }
                    event.setDropCompleted(success);
                    event.consume();
                }
            });

            studentTable.setOnDragDone(new EventHandler<DragEvent>() {
                public void handle(DragEvent event) {
                    event.consume();
                }
            });

            trash.setOnDragExited(new EventHandler<DragEvent>() {
                public void handle(DragEvent event) {
                    trash.setText("TRASH");
                    event.consume();
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void log(Object o) {
        System.out.println("" + o);
    }
}
