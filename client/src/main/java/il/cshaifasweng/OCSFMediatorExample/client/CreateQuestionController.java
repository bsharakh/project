package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateQuestionController
{
    //////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////// Class Fields ///////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////
    public SimpleClient client;
    static int correctAnswerG=0;
    private List<Subject> allSubjects;
    private Subject selectedSubject;
    private List<Course> allCourses;

//    private ArrayList<String> allCoursesNames;
    private List<Course> chosenCourses;
    @FXML
    private Button Answer1_Button, Answer2_Button, Answer3_Button, Answer4_Button, Save_Button;
    private Button [] answers;
    @FXML
    private TextField QuestionCode_TextField, Answer1_TextField, Answer2_TextField, Answer3_TextField, Answer4_TextField;
    @FXML
    private TextArea Question_TextArea;
    @FXML
    private Text error_bar_text;
    @FXML
    ComboBox Subject_ComboBox;
    @FXML
    TableView Table;
    private boolean tableInitFlag;
    @FXML
    private TableColumn<Course, String> Course_Column;

    //////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////// Initialize ///////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////
    @FXML
    private void initialize () throws IOException {
        EventBus.getDefault().register(this);
        client = SimpleClient.getClient();
        client.openConnection();

        tableInitFlag = true;
        answers = new Button[4];
        chosenCourses = new ArrayList<>();
        initializeButtons();

        try {
            SimpleClient.getClient().sendToServer(new Message("#GetAllSubjects"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void initializeButtons ()
    {
        Answer1_Button.setDisable(false);
        Answer1_Button.setText("X");
        App.setButtonColor(Answer1_Button, "red");
        answers[0] = Answer1_Button;

        Answer2_Button.setDisable(false);
        Answer2_Button.setText("X");
        App.setButtonColor(Answer2_Button, "red");
        answers[1] = Answer2_Button;

        Answer3_Button.setDisable(false);
        Answer3_Button.setText("X");
        App.setButtonColor(Answer3_Button, "red");
        answers[2] = Answer3_Button;

        Answer4_Button.setDisable(false);
        Answer4_Button.setText("X");
        App.setButtonColor(Answer4_Button, "red");
        answers[3] = Answer4_Button;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////// Common /////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////

    private void setCorrectAnswer (int n)
    {
        int correctAnswerNumber = n-1;
        for (int i=0; i<4; i++)
        {
            if(i == correctAnswerNumber){
                answers[i].setText("V");
                App.setButtonColor(answers[i], "green");
                correctAnswerG = n;
            }
            else {
                answers[i].setText("X");
                App.setButtonColor(answers[i], "red");
            }
        }
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

    //////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////// Server Replay //////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////

    @Subscribe
    public void getAllSubjectsNames_Replay(EventGetAllSubjects event)
    {
        allSubjects = event.getAllSubjects();
        ArrayList<String> allSubjectsNames = new ArrayList<String>();
        for (Subject subject : allSubjects)
        {
            allSubjectsNames.add(subject.getSubName());
        }
        ObservableList<String> basesList = FXCollections.observableArrayList(allSubjectsNames);
        Subject_ComboBox.setItems(basesList);
        error_bar_text.setText("Please Choose a Subject");
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////// Table //////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////

    private void initTable()
    {
        ObservableList<Course> allExamQuestions_OL = FXCollections.observableArrayList(allCourses);
        Table.setItems(allExamQuestions_OL);

        Course_Column.setCellValueFactory(new PropertyValueFactory<>("courseName"));

        if(tableInitFlag){
            initSelectTableColumn();
            tableInitFlag = false;
        }
    }

    private void initSelectTableColumn() {
        TableColumn<Course, Void> colBtn = new TableColumn("");

        Callback<TableColumn<Course, Void>, TableCell<Course, Void>> cellFactory = new Callback<TableColumn<Course, Void>, TableCell<Course, Void>>() {
            @Override
            public TableCell<Course, Void> call(final TableColumn<Course, Void> param) {
                final TableCell<Course, Void> cell = new TableCell<Course, Void>() {

                    private final Button btn = new Button("Select");
                    {
                        App.setButtonColor(btn, "orange");

                        btn.setOnAction((ActionEvent event) ->
                        {
                            Course select_course = getTableView().getItems().get(getIndex());
                            if(btn.getText().toString().equals("Select")){
                                btn.setText("Selected");
                                App.setButtonColor(btn, "green");
                                chosenCourses.add(select_course);
                            }
                            else {
                                btn.setText("Select");
                                App.setButtonColor(btn, "orange");
                                chosenCourses.remove(select_course);
                            }
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

    public void answer1_click(ActionEvent actionEvent) {
        setCorrectAnswer(1);
    }

    public void answer2_click(ActionEvent actionEvent) {
        setCorrectAnswer(2);
    }

    public void answer3_click(ActionEvent actionEvent) {
        setCorrectAnswer(3);
    }

    public void answer4_click(ActionEvent actionEvent) {
        setCorrectAnswer(4);
    }

    public void save_click(ActionEvent actionEvent) throws IOException {
        System.out.println("Chosen Courses:");
        for (int i=0; i<chosenCourses.size(); i++){
            System.out.println(chosenCourses.get(i).getCourseName());
        }
        String s_question_code = QuestionCode_TextField.getText().toString();
        String s_question = Question_TextArea.getText().toString();
        String s_answer1 = Answer1_TextField.getText().toString();
        String s_answer2 = Answer2_TextField.getText().toString();
        String s_answer3 = Answer3_TextField.getText().toString();
        String s_answer4 = Answer4_TextField.getText().toString();
        List<String> l_answers = new ArrayList<String>();
        l_answers.add(s_answer1);
        l_answers.add(s_answer2);
        l_answers.add(s_answer3);
        l_answers.add(s_answer4);

        if(s_question_code.equals(""))
        {
            error_bar_text.setText("Please Fill Question Code");
        }
        else if(s_question_code.length() != 2)
        {
            error_bar_text.setText("Question Code Must Be 2 Digits");
        }
        else if(!isNumber(s_question_code))
        {
            error_bar_text.setText("Question Code Must Be a Number");
        }
        else if (s_question.equals(""))
        {
            error_bar_text.setText("Please Fill a Question");
        }
        else if (s_answer1.equals("") || s_answer2.equals("") ||
                s_answer3.equals("") || s_answer4.equals(""))
        {
            error_bar_text.setText("Please Fill All Answers");
        }
        else if ((correctAnswerG<1) || (correctAnswerG>4))
        {
            error_bar_text.setText("Please Choose a Correct Answers");
        }
        else if (selectedSubject == null){
            error_bar_text.setText("Please Choose a Subject");
        }
        else if (chosenCourses.size() == 0){
            error_bar_text.setText("Please Choose at least one Course");
        }
        else
        {
            error_bar_text.setText("Saving Question...");
            Question question = new Question (Integer.valueOf(s_question_code), s_question, l_answers.toArray(new String[0]), correctAnswerG, chosenCourses);
            try {
                SimpleClient.getClient().sendToServer(new Message("#CreateNewQusetion", question));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            App.setRoot("teacherMain");
        }
    }

    public void SubjectSelected(ActionEvent actionEvent)
    {
        if(Subject_ComboBox.getValue() == null) {return;}
        String subjectName = Subject_ComboBox.getValue().toString();
        selectedSubject = null;

        for(Subject s : allSubjects){
            if(s.getSubName().equals(subjectName)){
                selectedSubject = s;
                break;
            }
        }

        if(selectedSubject != null){
            allCourses = selectedSubject.getCourses();
        }
        initTable();
    }

//    public void SubjectSelected(ActionEvent actionEvent)
//    {
//        String selectedSubjectName = Subject_ComboBox.getValue().toString();
//        for (Subject subject : allSubjects)
//        {
//            if(subject.getSubName().equals(selectedSubjectName)){
//                selectedSubject = subject;
//                break;
//            }
//        }
////        allCourses = selectedSubject.getCourses();
////        ArrayList<String> allCoursesNames = new ArrayList<String>();
////        for (Course course : allCourses)
////        {
////            allCoursesNames.add(course.getCourseName());
////        }
//        initData ();
//        error_bar_text.setText("Please Select Courses");
//        initTable();
//    }

    public void Home_Click(ActionEvent actionEvent) throws IOException {
//        client.closeConnection();
        EventBus.getDefault().unregister(this);
        App.setRoot("teacherMain");
    }

    public void save_in() {
        App.setButtonColor(Save_Button,"green");
    }
    public void save_out() {
        App.setButtonColor(Save_Button,"orange");
    }
}
