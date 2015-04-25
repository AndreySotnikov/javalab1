package sample;

import com.sun.javafx.scene.control.skin.IntegerFieldSkin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;


import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by andrey on 24.04.15.
 */
public class AddController implements Initializable {

    private ObservableList<Mark> marksData = FXCollections.observableArrayList();

    private DBManager dbManager = new DBManager();

    @FXML
    Pane root;

    @FXML
    private TextField tfName;

    @FXML
    private TextField tfCourse;

    @FXML
    private TextField tfGroup;

    @FXML
    private TableView<Mark> tableMark;

    @FXML
    private TableColumn<Mark, String> subjectColumn;

    @FXML
    private TableColumn<Mark, String> markColumn;

    @FXML
    private Button btnAdd;

    @FXML
    public void onAddClick() throws SQLException, IOException {
        Stage stage = (Stage) btnAdd.getScene().getWindow();
        try {
            Map<String, Integer> map = new HashMap<>();
            for (Mark mark : marksData) {
                map.put(mark.getName(), Integer.parseInt(mark.getValue()));
            }
            Student student = new Student(tfName.getText(),
                    Integer.parseInt(tfCourse.getText()),
                    Integer.parseInt(tfGroup.getText()), map);

            if (stage.getTitle().equals("Add a new student"))
                dbManager.save(student);
            else {
                Student oldStudent = DTO.getInstance();
                dbManager.update(oldStudent.getId(), student);
            }
            stage.close();
        }catch (NumberFormatException e){
            Dialogs.create()
                    .owner(stage)
                    .title("Error Dialog")
                    .message("Incorrect input")
                    .showError();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Student student = DTO.getInstance();
        if (student.getFio() != null && !student.getFio().equals("")) {
            tfName.setText(student.getFio());
            tfCourse.setText("" + student.getCourse());
            tfGroup.setText("" + student.getGroup());
            for (Map.Entry<String, Integer> elem : student.getMarks().entrySet()) {
                marksData.add(new Mark(elem.getKey(), elem.getValue().toString()));
            }
        }else{
            for(int i = 0; i < 5; i++){
                marksData.add(new Mark("",""));
            }
        }
        subjectColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        markColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        markColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        subjectColumn.setOnEditCommit(
                (TableColumn.CellEditEvent<Mark, String> t) -> {
                    ((Mark) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                    ).setName(t.getNewValue());
                });
        markColumn.setOnEditCommit(
                (TableColumn.CellEditEvent<Mark, String> t) -> {
                    ((Mark) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                    ).setValue(t.getNewValue());
                });
        tableMark.setItems(marksData);
    }
}
