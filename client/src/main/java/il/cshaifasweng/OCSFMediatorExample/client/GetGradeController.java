package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.ExamQuestion;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import il.cshaifasweng.OCSFMediatorExample.entities.Question;
import il.cshaifasweng.OCSFMediatorExample.entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetGradeController
{
    //////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////// Class Fields ///////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////
    @FXML
    private TextField Code_TextField;
    @FXML
    private Text error_bar_text;

    @FXML
    private TableColumn<ExamQuestion, String> Code_Column, Question_Column, Points_Column;

    public static User user;
    public static User getUser() {
        return user;
    }
    public static void setUser(User user) {
        GetGradeController.user = user;
    }

    public SimpleClient client;

    //////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////// Initialize ///////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////
    @FXML
    private void initialize () throws IOException {
        EventBus.getDefault().register(this);
        client = SimpleClient.getClient();
        client.openConnection();

        error_bar_text.setText("");
    }


    public boolean isNumber (String s)
    {
        char [] arr = s.toCharArray();
        for(char c : arr){
            if((c < '0') || (c > '9')){
                return false;
            }
        }
        return true;
    }

    private boolean validateExamCode (String code)
    {
        if (!isNumber(code)){
            error_bar_text.setText("Exam Code Must Be a Number");
            return false;
        }
        if (code.length() != 6){
            error_bar_text.setText("Exam Code Must Be 5 Digits");
            return false;
        }
        return true;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////// Server Replay //////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////

    @Subscribe
    public void eventReturnGrade(EventReturnGrade event)
    {
        error_bar_text.setText("You're Grade Is: " + event.getGrade());
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////// On Action Functions ///////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////

    public void Click_Back(ActionEvent actionEvent) throws IOException
    {
        EventBus.getDefault().unregister(this);
        App.setRoot("studentMain");
    }

    public void Click_GetGrade(ActionEvent actionEvent)
    {
        String code = Code_TextField.getText().toString();
        if(validateExamCode(code)){
            User myUser = App.getUser();
            Object[] user_data = {myUser.getUserName(), code};
            try {
                SimpleClient.getClient().sendToServer(new Message("#GetGrade", user_data));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}