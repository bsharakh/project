package il.cshaifasweng.OCSFMediatorExample.client;

import com.sun.source.doctree.SystemPropertyTree;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import il.cshaifasweng.OCSFMediatorExample.entities.Student;
import il.cshaifasweng.OCSFMediatorExample.entities.Teacher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.lang.model.type.NullType;
import java.io.IOException;
import java.util.List;

public class PrincipleTeachersMenu {

    public static Teacher teacher1;

    public static List<Teacher> getTeachers1() {
        return teachersList;
    }

    public static void setTeachers1(List<Teacher> teachers) {
        PrincipleTeachersMenu.teachersList = teachers;
    }

    private static List<Teacher> teachersList;

    private static Teacher selectedTeacher;

    @FXML
    private Button p_back_btn;
    @FXML
    private Button p_view_teacher;

    @FXML
    private Label teacher_lbl;

    @FXML
    private TableView<Teacher> p_teacher_list;

    @FXML
    private TableView<Teacher> p_teacher_list1;

    @FXML
    private TableColumn<Teacher, String> idCloumn;

    @FXML
    private TableColumn<Teacher, String> usernameCloumn;

    @FXML
    private TableColumn<Teacher, String> idCloumn1;

    @FXML
    private TableColumn<Teacher, String> usernameCloumn1;

    private boolean isInitialized = false;

    @FXML
    private void initialize (){

        System.out.println("QQQQQQQQQQQQQQQQ");

        assert p_back_btn != null : "fx:id=\"p_back_btn\" was not injected: check your FXML file 'principle_teachers_menu.fxml'.";
        assert p_view_teacher != null : "fx:id=\"p_view_teacher\" was not injected: check your FXML file 'principle_teachers_menu.fxml'.";
        assert teacher_lbl != null : "fx:id=\"teacher_lbl\" was not injected: check your FXML file 'principle_teachers_menu.fxml'.";
        assert p_teacher_list != null : "fx:id=\"p_teacher_list\" was not injected: check your FXML file 'principle_teachers_menu.fxml'.";
        assert p_teacher_list1 != null : "fx:id=\"p_teacher_list1\" was not injected: check your FXML file 'principle_teachers_menu.fxml'.";
        assert idCloumn != null : "fx:id=\"idCloumn\" was not injected: check your FXML file 'principle_teachers_menu.fxml'.";
        assert usernameCloumn != null : "fx:id=\"usernameCloumn\" was not injected: check your FXML file 'principle_teachers_menu.fxml'.";
        assert idCloumn1 != null : "fx:id=\"idCloumn1\" was not injected: check your FXML file 'principle_teachers_menu.fxml'.";
        assert usernameCloumn1 != null : "fx:id=\"usernameCloumn1\" was not injected: check your FXML file 'principle_teachers_menu.fxml'.";

        if (teachersList == null) {
            teachersList = App.getTeacherList();
            if (teachersList == null) {
                // Abort initialization if the list is not available
                return;
            }
        }

        ObservableList<Teacher> names = FXCollections.observableArrayList(teachersList);
        p_teacher_list.setItems(names); // Set the items (rows) for the TableView
        p_teacher_list1.setItems(names);

        usernameCloumn.setCellValueFactory(new PropertyValueFactory<Teacher, String>("userName"));
        idCloumn.setCellValueFactory(new PropertyValueFactory<Teacher, String>("id"));
        usernameCloumn1.setCellValueFactory(new PropertyValueFactory<Teacher, String>("userName"));
        idCloumn1.setCellValueFactory(new PropertyValueFactory<Teacher, String>("id"));
    }

    @FXML
    void go_back(ActionEvent event) throws IOException {
        App.setRoot("principle_stat_menu");
    }

    @FXML
    void view_teacher_stats(ActionEvent event) throws IOException {
        ////////////////////////////////
        App.setRoot("principle_teacher_exam_menu");
    }

    @FXML
    void save_item(){
        selectedTeacher = p_teacher_list.getSelectionModel().getSelectedItem();
        System.out.println(selectedTeacher.getUserName());
    }

}