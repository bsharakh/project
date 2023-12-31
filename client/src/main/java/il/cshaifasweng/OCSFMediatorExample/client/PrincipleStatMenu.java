package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

import static il.cshaifasweng.OCSFMediatorExample.client.App.setRoot;

public class PrincipleStatMenu {

    @FXML
    private Button p_back_btn;
    @FXML
    private Button p_students;
    @FXML
    private Button p_teachers;
    @FXML
    private Button p_courses;

    @FXML
    public void go_back(ActionEvent actionEvent) throws IOException {
        setRoot("principle_homepage");
    }

    @FXML
    public void view_students(ActionEvent actionEvent) throws IOException {
        try {
            SimpleClient.getClient().sendToServer(new Message("#GetListOfStudents"));
        } catch (IOException e){
            e.printStackTrace();
        }
        App.setRoot("principle_student_menu");
    }
    @FXML
    public void view_teachers(ActionEvent actionEvent) throws IOException {
        try {
            SimpleClient.getClient().sendToServer(new Message("#GetListOfTeachers"));
        } catch (IOException e){
            e.printStackTrace();
        }
        App.setRoot("principle_teachers_menu");
    }

    @FXML
    public void view_courses(ActionEvent actionEvent) throws IOException {
        try {
            SimpleClient.getClient().sendToServer(new Message("#GetListOfCourses"));
        } catch (IOException e){
            e.printStackTrace();
        }
        App.setRoot("principle_courses_menu");
    }
}
