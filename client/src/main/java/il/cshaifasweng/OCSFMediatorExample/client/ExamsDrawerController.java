package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExamsDrawerController
{
    public SimpleClient client;
    @FXML
    private TextField Title_TextField, Code_TextField, Time_TextField, Teacher_Desc_TextField, Student_Desc_TextField;
    @FXML
    private Text error_bar_text, Exam_text, question_number_text;
//    private Question currentQuestion;
//    private ArrayList<Question> allQuestions;
    private List<Exam> allExams;
    private Exam selectedExam;
    private List<Subject> allSubjects;
    private Subject selectedSubject;
    private List<Course> allCourses;
    private Course selectedCourse;
    @FXML
    private Button Answer1, Answer2, Answer3, Answer4;
    private Button [] answersButtons;
    @FXML
    ComboBox Subject_ComboBox, Course_ComboBox;
    @FXML
    TableView Table;
    @FXML
    private TableColumn<Exam, String> Code_Column, Exam_Column;
    @FXML
    Button Home_Button;
    @FXML
    TextArea question_text_area;

    private boolean tableInitFlag;
    private int examLength;

    private ExamQuestion currentQuestion;
    private List<ExamQuestion> allExamQuestions;
    private static int currentQuestionNumber;

    //////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////// Initialize ///////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////

    @FXML
    private void initialize() throws IOException {
        EventBus.getDefault().register(this);
        client = SimpleClient.getClient();
        client.openConnection();

        tableInitFlag = true;
        error_bar_text.setText("");
        answersButtons = new Button[4];
        initializeButtons();

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
        App.setButtonColor(Answer1, "orange");
        answersButtons[0] = Answer1;

        Answer2.setDisable(false);
        App.setButtonColor(Answer2, "orange");
        answersButtons[1] = Answer2;

        Answer3.setDisable(false);
        App.setButtonColor(Answer3, "orange");
        answersButtons[2] = Answer3;

        Answer4.setDisable(false);
        App.setButtonColor(Answer4, "orange");
        answersButtons[3] = Answer4;

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////// Common /////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////

    private void loadExam (Exam exam)
    {
        Exam_text.setText(exam.getTitle());
        currentQuestionNumber = 0;
        allExamQuestions = exam.getExamQuestion();
        examLength = allExamQuestions.size();
        loadNewQuestion(currentQuestionNumber);
        error_bar_text.setText("Exam Loaded");
    }

    private void loadNewQuestion (int questionNumber)
    {
        currentQuestion = allExamQuestions.get(questionNumber);
        initializeButtons();

        int correctAnswerNumber = currentQuestion.getCorrect_answer() - 1;
        App.setButtonColor(answersButtons[correctAnswerNumber], "green");

        question_text_area.setText(currentQuestion.getQuestion());
        for(int i=0; i<answersButtons.length; i++)
        {
            String tempAnswer = currentQuestion.getAnswers()[i];
            answersButtons[i].setText(tempAnswer);
        }

        String myQuestionNumber = "Question ";
        myQuestionNumber += Integer.toString(questionNumber+1);
        myQuestionNumber += "/";
        myQuestionNumber += Integer.toString(examLength);
        question_number_text.setText(myQuestionNumber);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////// Server Replay //////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////

    @Subscribe
    public void getAllSubjectsResponse(EventGetAllSubjects event)
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
    public void GetAllExamsByCourse_Replay(EventGetAllExamsByCourse event)
    {
        allExams = event.getAllExams();
        initTable();
//        ArrayList<String> allNames = new ArrayList<>(event.getAllExamsNames());
//        ObservableList<String> basesList = FXCollections.observableArrayList(allNames);
//        Course_ComboBox.setItems(basesList);
//        error_bar_text.setText("All Courses Loaded");
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////// Table //////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////

    private void initTable ()
    {
        ObservableList<Exam> allExamQuestions_OL = FXCollections.observableArrayList(allExams);
        Table.setItems(allExamQuestions_OL);

        Exam_Column.setCellValueFactory(new PropertyValueFactory<>("title"));
        if(tableInitFlag){
            initViewTableColumn();
            initEditTableColumn();
            initExecuteTableColumn();
            tableInitFlag = false;
        }
    }

    private void initEditTableColumn() {
        TableColumn<Exam, Void> colBtn = new TableColumn("");

        Callback<TableColumn<Exam, Void>, TableCell<Exam, Void>> cellFactory = new Callback<TableColumn<Exam, Void>, TableCell<Exam, Void>>() {
            @Override
            public TableCell<Exam, Void> call(final TableColumn<Exam, Void> param) {
                final TableCell<Exam, Void> cell = new TableCell<Exam, Void>() {

                    private final Button btn = new Button("Edit");
                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Exam view_exam = getTableView().getItems().get(getIndex());
                            loadExam(view_exam);
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

    private void initExecuteTableColumn() {
        TableColumn<Exam, Void> colBtn = new TableColumn("");

        Callback<TableColumn<Exam, Void>, TableCell<Exam, Void>> cellFactory = new Callback<TableColumn<Exam, Void>, TableCell<Exam, Void>>() {
            @Override
            public TableCell<Exam, Void> call(final TableColumn<Exam, Void> param) {
                final TableCell<Exam, Void> cell = new TableCell<Exam, Void>() {

                    private final Button btn = new Button("Execute");
                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Exam view_exam = getTableView().getItems().get(getIndex());
                            loadExam(view_exam);
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

    private void initViewTableColumn() {
        TableColumn<Exam, Void> colBtn = new TableColumn("");

        Callback<TableColumn<Exam, Void>, TableCell<Exam, Void>> cellFactory = new Callback<TableColumn<Exam, Void>, TableCell<Exam, Void>>() {
            @Override
            public TableCell<Exam, Void> call(final TableColumn<Exam, Void> param) {
                final TableCell<Exam, Void> cell = new TableCell<Exam, Void>() {

                    private final Button btn = new Button("view");
                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Exam view_exam = getTableView().getItems().get(getIndex());
                            loadExam(view_exam);
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

    private void initData ()
    {
        String [] s1 = {"1","2","100","pi"};
        ExamQuestion e1 = new ExamQuestion(1, 11, "1+1=?", 2, s1, 10, "", "");

        String [] s2 = {"blue","green","black","red"};
        ExamQuestion e2 = new ExamQuestion(2, 22, "Apples are ____ ?", 4, s2, 10, "", "");

        String [] s3 = {"0","10","100","1000"};
        ExamQuestion e3 = new ExamQuestion(3, 33, "100*0=?", 1, s3, 10, "", "");

        ArrayList<ExamQuestion> qqq = new ArrayList<ExamQuestion>();
        qqq.add(e1);
        qqq.add(e3);

        ArrayList<ExamQuestion> rrr = new ArrayList<ExamQuestion>();
        rrr.add(e2);

        Exam exam1 = new Exam(123, "Math Exam", qqq);
        Exam exam2 = new Exam(456, "Colors Exam", rrr);
        ArrayList<Exam> eee = new ArrayList<Exam>();
        eee.add(exam1);
        eee.add(exam2);

        allExams = eee;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////// On Action //////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////

    public void prev_button_click(ActionEvent actionEvent)
    {
        currentQuestionNumber = (currentQuestionNumber + examLength - 1) % examLength;
        loadNewQuestion(currentQuestionNumber);
    }
    public void next_button_click(ActionEvent actionEvent)
    {
        currentQuestionNumber = (currentQuestionNumber + 1 ) % examLength;
        loadNewQuestion(currentQuestionNumber);
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
        String courseName = Course_ComboBox.getValue().toString();

        try {
            SimpleClient.getClient().sendToServer(new Message("#GetAllExamsByCourse", courseName));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void Home_Click(ActionEvent actionEvent) throws IOException {
        App.setRoot("teacherMain");
    }
}