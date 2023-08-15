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

public class CreateExamController
{
    //////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////// Class Fields ///////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////
    public SimpleClient client;
    private boolean selectQuestionFlag, removeColumnFlag, inThisPage;
    String courseName;
    @FXML
    private TextField Teacher_Note_TextField, Student_Note_TextField, Points_TextField;
    @FXML
    private TextField Title_TextField, Code_TextField, Time_TextField, Teacher_Desc_TextField, Student_Desc_TextField;
    @FXML
    private Text error_bar_text, question_text;
    @FXML
    private VBox Vbox;
    private Question currentQuestion;
    private ArrayList<Question> allQuestions;
    private List<ExamQuestion> allExamQuestions;
    @FXML
    private Button Answer1, Answer2, Answer3, Answer4;
    private Button [] answersButtons;
    @FXML
    ComboBox SelectQuestionComboBox, Subject_ComboBox, Course_ComboBox;
    @FXML
    TableView Table;
    @FXML
    private TableColumn<ExamQuestion, String> Question_Column, Points_Column;

    //////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////// Initialize ///////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////
    @FXML
    private void initialize () throws IOException {
        EventBus.getDefault().register(this);
        client = SimpleClient.getClient();
        client.openConnection();

        inThisPage = true;

        answersButtons = new Button[4];
        selectQuestionFlag = false;
        removeColumnFlag = true;
        error_bar_text.setText("");
        initializeButtons();
        allExamQuestions = new ArrayList<ExamQuestion>();

        Course_ComboBox.setDisable(true);
        try {
            SimpleClient.getClient().sendToServer(new Message("#GetAllSubjectsNames"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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

    private void initSelectQuestionComboBox ()
    {
        ArrayList<String> allQuestionsNames = new ArrayList<String>();
        for (Question question : allQuestions)
        {
            allQuestionsNames.add(question.getQuestion());
        }
        ObservableList<String> basesList = FXCollections.observableArrayList(allQuestionsNames);
        SelectQuestionComboBox.setItems(basesList);
    }

    private void loadNewQuestion (Question question)
    {
        selectQuestionFlag = true;
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
        Points_TextField.setText("");
        Teacher_Note_TextField.setText("");
        Student_Desc_TextField.setText("");
    }

    private Question findQuestion (String selectedQuestionName)
    {
        for (Question question : allQuestions)
        {
            if(question.getQuestion().equals(selectedQuestionName))
            {
                return question;
            }
        }
        return null;
    }

    private void refreshTable ()
    {
        ObservableList<ExamQuestion> allExamQuestions_OL = FXCollections.observableArrayList(allExamQuestions);
        Table.setItems(allExamQuestions_OL);

        Question_Column.setCellValueFactory(new PropertyValueFactory<>("questionMll"));
        Points_Column.setCellValueFactory(new PropertyValueFactory<>("points"));
        if(removeColumnFlag){
            addColumnToTable();
            removeColumnFlag = false;
        }
    }

    private void addColumnToTable() {
        TableColumn<ExamQuestion, Void> colBtn = new TableColumn("");

        Callback<TableColumn<ExamQuestion, Void>, TableCell<ExamQuestion, Void>> cellFactory = new Callback<TableColumn<ExamQuestion, Void>, TableCell<ExamQuestion, Void>>() {
            @Override
            public TableCell<ExamQuestion, Void> call(final TableColumn<ExamQuestion, Void> param) {
                final TableCell<ExamQuestion, Void> cell = new TableCell<ExamQuestion, Void>() {

                    private final Button btn = new Button("remove");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            ExamQuestion remove_question = getTableView().getItems().get(getIndex());
                            allExamQuestions.remove(remove_question);
                            refreshTable();
                            error_bar_text.setText("Question Removed");
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

    private boolean validatePoints (String points)
    {
        if (points.equals("")){
            error_bar_text.setText("Please Fill Question Points");
            return false;
        }
        if (!isNumber(points)){
            error_bar_text.setText("Question Points Must Be a Number");
            return false;
        }
        int Npoints = Integer.valueOf(points);
        if(Npoints < 0){
            error_bar_text.setText("Question Points Must Be Higher Than 0");
            return false;
        }
        if(Npoints > 100){
            error_bar_text.setText("Question Points Must Be Less Than 100");
            return false;
        }
        return true;
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
        }else if(inThisPage){
            initSelectQuestionComboBox();
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////// On Action Functions ///////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////

    public void finish_exam_click(ActionEvent actionEvent) throws IOException {
        String title = Title_TextField.getText().toString();
        String code = Code_TextField.getText().toString();
        String time = Time_TextField.getText().toString();
        String studentDesc = Student_Desc_TextField.getText().toString();
        String teacherDesc = Teacher_Desc_TextField.getText().toString();

        if (title.equals("")) {error_bar_text.setText("Please Enter The Exam Name");}
        else if (code.equals("")) {error_bar_text.setText("Please Enter The Exam Cade");}
        else if (!isNumber(code)){
            error_bar_text.setText("Exam Code Must Be a Number");
        }
        else if (code.length() != 2){
            error_bar_text.setText("Exam Code Must Be 2 Digits");
        }
        else if (time.equals("")) {error_bar_text.setText("Please Enter The Exam Time");}
        else if (!isNumber(time)){
            error_bar_text.setText("Exam Time Must Be a Number");
        }
        else if(allExamQuestions.size() == 0) {
            error_bar_text.setText("Please Choose At Lease One Question");
        }else {
            error_bar_text.setText("Creating Exam ...");

            Exam exam = new Exam(Integer.valueOf(code), title, Integer.valueOf(time), studentDesc, teacherDesc);
            Object [] objects = {exam, courseName, App.getUser().getUserName()};
            try {
                SimpleClient.getClient().sendToServer(new Message("#CreateNewExam", objects));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
//            client.closeConnection();
            EventBus.getDefault().unregister(this);
            inThisPage = false;
            App.setRoot("teacherMain");
        }
    }

    public void QuestionSelected(ActionEvent actionEvent)
    {
        String selectedQuestionName = SelectQuestionComboBox.getValue().toString();
        Question selectedQuestion = findQuestion(selectedQuestionName);
        if(selectedQuestion != null){
            loadNewQuestion(selectedQuestion);
        }
    }

    public void Add_Question_Click(ActionEvent actionEvent)
    {
        String points = Points_TextField.getText().toString();
        if(selectQuestionFlag == false){
            error_bar_text.setText("Please Select a Question to Add ");
        }
        else if(validatePoints(points)){
            int Npoints = Integer.valueOf(points);
            String teacher_note = Teacher_Note_TextField.getText().toString();
            String student_note = Student_Note_TextField.getText().toString();
            allExamQuestions.add(new ExamQuestion(currentQuestion, Npoints, teacher_note, student_note));
            refreshTable();
            Subject_ComboBox.setDisable(true);
            Course_ComboBox.setDisable(true);
            error_bar_text.setText("The Question Has Been Added To The Exam");
        }
    }

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
        courseName = Course_ComboBox.getValue().toString();

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
        inThisPage = false;
        App.setRoot("teacherMain");
    }
}