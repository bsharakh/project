package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.animation.FadeTransition;
        import javafx.collections.FXCollections;
        import javafx.collections.ObservableList;
        import javafx.event.ActionEvent;
        import javafx.fxml.FXML;
        import javafx.fxml.FXMLLoader;
        import javafx.scene.Node;
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

public class QuestionsDrawerController
{
    //////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////// Class Fields ///////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////
    public SimpleClient client;
    private boolean tableFlag;
    @FXML
    private Text error_bar_text, question_text, code_text;
    @FXML
    private VBox Vbox;
    private Question currentQuestion;
    private ArrayList<Question> allQuestions;
    @FXML
    private Button Answer1, Answer2, Answer3, Answer4;
    private Button [] answersButtons;
    @FXML
    ComboBox Subject_ComboBox, Course_ComboBox;
    @FXML
    TableView Table;
    @FXML
    private TableColumn<Question, String> Code_Column, Question_Column;

    //////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////// Initialize ///////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////
    @FXML
    private void initialize () throws IOException {
        System.out.println("initialize QD");
        EventBus.getDefault().register(this);
        client = SimpleClient.getClient();
        client.openConnection();

        Course_ComboBox.setDisable(true);
        answersButtons = new Button[4];
        tableFlag = true;
        error_bar_text.setText("");

        try {
            SimpleClient.getClient().sendToServer(new Message("#GetAllSubjectsNames"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        initializeButtons();
    }

    private void initializeButtons ()
    {
        Answer1.setDisable(false);
        setButtonColor(Answer1, "orange");
        answersButtons[0] = Answer1;

        Answer2.setDisable(false);
        setButtonColor(Answer2, "orange");
        answersButtons[1] = Answer2;

        Answer3.setDisable(false);
        setButtonColor(Answer3, "orange");
        answersButtons[2] = Answer3;

        Answer4.setDisable(false);
        setButtonColor(Answer4, "orange");
        answersButtons[3] = Answer4;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////// Common /////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////

    private void setButtonColor (Button B, String color)
    {
        String colorID = "#ffffff"; // default white
        if(color.equals("black")){
            colorID = "#000000";
        } else if (color.equals("orange")) {
            colorID = "#e28743";
        } else if (color.equals("dblue")) {
            colorID = "#063970";
        } else if (color.equals("lblue")) {
            colorID = "#abdbe3";
        } else if (color.equals("green")) {
            colorID = "#34b048";
        } else if (color.equals("red")) {
            colorID = "#ff0404";
        } else if (color.equals("gray")) {
            colorID = "#c5c5c5";
        }
        B.setStyle("-fx-background-color: " + colorID);
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

    private void loadNewQuestion (Question question)
    {
        currentQuestion = question;
        initializeButtons();
        question_text.setText(currentQuestion.getQuestion());

        for(int i=0; i<answersButtons.length; i++)
        {
            String tempAnswer = currentQuestion.getAnswers()[i];
            answersButtons[i].setText(tempAnswer);
            if (currentQuestion.getCorrect_answer() == i+1){
                setButtonColor(answersButtons[i], "green");
            }
        }
        code_text.setText("Code: " + question.getCode());
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////// Server Replay //////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////

    @Subscribe
    public void getAllSubjectsNames_Replay(EventGetAllSubjectsNames event)
    {
        ArrayList<String> allSubjectsNames = new ArrayList<>(event.getAllSubjectsNames());
        ObservableList<String> basesList = FXCollections.observableArrayList(allSubjectsNames);
        Subject_ComboBox.setItems(basesList);
        error_bar_text.setText("Please Choose a Subject");
    }

    @Subscribe
    public void GetAllCoursesBySubject_Replay(EventGetAllCoursesBySubject event)
    {
        ArrayList<String> allNames = new ArrayList<>(event.getAllCoursesNames());
        ObservableList<String> basesList = FXCollections.observableArrayList(allNames);
        Course_ComboBox.setDisable(false);
        Course_ComboBox.setItems(basesList);
        error_bar_text.setText("All Courses Loaded");
    }

    @Subscribe
    public void GetAllQuestionsByCourse_Replay(EventGetAllQuestionsByCourse event)
    {
        allQuestions = new ArrayList<>(event.getAllQuestions());
        if(allQuestions == null) {
            error_bar_text.setText("No Questions Found");
        }else if(allQuestions.size() == 0){
            error_bar_text.setText("No Questions Found");
        }
//        else {
            initTable();
//        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////// Table //////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////

    private void initTable ()
    {
        ObservableList<Question> allExamQuestions_OL = FXCollections.observableArrayList(allQuestions);
        Table.setItems(allExamQuestions_OL);

        Code_Column.setCellValueFactory(new PropertyValueFactory<>("code"));
        Question_Column.setCellValueFactory(new PropertyValueFactory<>("question"));

        if(tableFlag){
            initViewTableColumn();
            tableFlag = false;
        }
    }

    private void initViewTableColumn() {
        TableColumn<Question, Void> colBtn = new TableColumn("");

        Callback<TableColumn<Question, Void>, TableCell<Question, Void>> cellFactory = new Callback<TableColumn<Question, Void>, TableCell<Question, Void>>() {
            @Override
            public TableCell<Question, Void> call(final TableColumn<Question, Void> param) {
                final TableCell<Question, Void> cell = new TableCell<Question, Void>() {

                    private final Button btn = new Button("view");
                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Question question = getTableView().getItems().get(getIndex());
                            loadNewQuestion(question);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };
        colBtn.setCellFactory(cellFactory);
        Table.getColumns().add(colBtn);
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////// On Action Functions ///////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////

    public void SubjectSelected(ActionEvent actionEvent)
    {
        if(Subject_ComboBox.getValue() == null) {return;}
        String subjectName = Subject_ComboBox.getValue().toString();

        try {
            SimpleClient.getClient().sendToServer(new Message("#GetAllCoursesBySubject", subjectName));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void CourseSelected(ActionEvent actionEvent)
    {
        if(Course_ComboBox.getValue() == null) {return;}
        String courseName = Course_ComboBox.getValue().toString();

        try {
            SimpleClient.getClient().sendToServer(new Message("#GetAllQuestionsByCourse", courseName));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void Home_Click(ActionEvent actionEvent) throws IOException {
//        client.closeConnection();
        EventBus.getDefault().unregister(this);
        App.setRoot("teacherMain");
    }

}
