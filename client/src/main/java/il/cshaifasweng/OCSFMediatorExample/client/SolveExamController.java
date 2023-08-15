package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.*;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


public class SolveExamController
{
    //////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////// Class Fields ///////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////
    public SimpleClient client;
    private List<Integer> examAnswers;
    private int examLength = 0;
    private ExamQuestion currentQuestion;
    private static int currentQuestionNumber;
    private Exam exam;
    private ExecutedVirtual vexam;
    @FXML
    private Button answer1_button, answer2_button, answer3_button, answer4_button, finish_exam_button;
    private Button [] answersButtons;
    @FXML
    private Text exam_name_text, date_text, question_number_text, question_text, student_note_text, clock_text, extra_time_text;
    @FXML
    ImageView note_ImageView;
    @FXML
    ImageView clock_0,clock_1,clock_2,clock_3,clock_4,clock_5,clock_6,clock_7,clock_8;
    ImageView[] clks;
    private int clks_counter, extraTime;
    private LocalTime startTime;

    private boolean timeUpFlag;

    //////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////// Initialize ///////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////
    @FXML
    private void initialize () throws IOException {
        EventBus.getDefault().register(this);
        client = SimpleClient.getClient();
        client.openConnection();

        exam = App.getExam();
        exam_name_text.setText(exam.getTitle());
        date_text.setText(App.getDate());

        timeUpFlag = true;
        extraTime = 0;
        extra_time_text.setText("");
        startTime = LocalTime.now();
        initClock(exam.getTime());

        answersButtons = new Button[4];
        examAnswers = new ArrayList<Integer>();
        examLength = getQuestions().size();
        for(int i=0; i<examLength; i++)
        {
            examAnswers.add(0);
        }

        currentQuestionNumber = 0;
        loadNewQuestion(currentQuestionNumber);
    }

    private void initClock(int duration)
    {
        initClockImages();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

        LocalTime endTime = startTime.plusMinutes(duration);
        LocalTime endWithExtra = endTime.plusMinutes(extraTime);

        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            loadNextClk();

            LocalTime currentTime = LocalTime.now();
            long diff = currentTime.until(endWithExtra, ChronoUnit.SECONDS);

            if(extraTime > 0) {extra_time_text.setText("Extra Time: " + extraTime + "min");}

            LocalTime defaultTime = LocalTime.parse("00:00:00");
            if(diff<=0 && timeUpFlag){
                timeUpFlag = false;
                exam_name_text.setText("Time Is Up !");
                clock_text.setText(defaultTime.format(dtf));
                try {
                    timeIsUp();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }else{
                LocalTime remaining = defaultTime.plusSeconds(diff);
                clock_text.setText(remaining.format(dtf));
            }
        }),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    private void initClockImages()
    {
        clks_counter = 0;
        clks = new ImageView[9];

        clks[0] = clock_0;
        clks[1] = clock_1;
        clks[2] = clock_2;
        clks[3] = clock_3;
        clks[4] = clock_4;
        clks[5] = clock_5;
        clks[6] = clock_6;
        clks[7] = clock_7;
        clks[8] = clock_8;

        for (int i=0; i< clks.length; i++){
            clks[i].setVisible(false);
        }
    }

    private String getStartTime ()
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        return  startTime.format(dtf);
    }

    private void loadNextClk()
    {
        clks[clks_counter].setVisible(false);
        clks_counter = (clks_counter+1)%9;
        clks[clks_counter].setVisible(true);
    }

    private void loadNewQuestion (int questionNumber)
    {
        currentQuestion = getQuestions().get(questionNumber);
        initializeButtons();

        if (examAnswers.get(questionNumber) != 0){
            setCorrectAnswer(examAnswers.get(questionNumber));
        }

        question_text.setText(currentQuestion.getQuestion().getQuestion());
        for(int i=0; i<answersButtons.length; i++)
        {
            String tempAnswer = currentQuestion.getQuestion().getAnswers()[i];
            answersButtons[i].setText(tempAnswer);
        }

        String myQuestionNumber = "Question ";
        myQuestionNumber += Integer.toString(questionNumber+1);
        myQuestionNumber += "/";
        myQuestionNumber += Integer.toString(examLength);
        question_number_text.setText(myQuestionNumber);

        student_note_text.setText(currentQuestion.getStudent_note());
        if(currentQuestion.getStudent_note().equals("")){
            note_ImageView.setVisible(false);
        }else{
            note_ImageView.setVisible(true);
        }
    }

    private ArrayList<ExamQuestion> getQuestions ()
    {
        ArrayList<ExamQuestion> allQuestions = new ArrayList<ExamQuestion>(exam.getExamQuestion());
        return allQuestions;
    }


    private void initializeButtons ()
    {
        answer1_button.setDisable(false);
        setButtonColor(answer1_button, "gray");
        answersButtons[0] = answer1_button;

        answer2_button.setDisable(false);
        setButtonColor(answer2_button, "gray");
        answersButtons[1] = answer2_button;

        answer3_button.setDisable(false);
        setButtonColor(answer3_button, "gray");
        answersButtons[2] = answer3_button;

        answer4_button.setDisable(false);
        setButtonColor(answer4_button, "gray");
        answersButtons[3] = answer4_button;
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
            colorID =  "#a5a9ab";
        }
        B.setStyle("-fx-background-color: " + colorID);
    }

    private void setCorrectAnswer (int n)
    {
        int correctAnswerNumber = n-1;
        examAnswers.set(currentQuestionNumber, n);

        for (int i=0; i<answersButtons.length; i++)
        {
            if(i == correctAnswerNumber){
                setButtonColor(answersButtons[i], "orange");
            }
            else {
                setButtonColor(answersButtons[i], "gray");
            }
        }
    }

    private Object [] endExam (boolean inTime)
    {
        vexam = new ExecutedVirtual((Student)App.getUser(), date_text.getText(), getStartTime(), inTime, examAnswers);
        Object [] objects = {exam.getCodeExam(), vexam};
        return objects;
    }

    private void timeIsUp () throws IOException {
//        try {
//            SimpleClient.getClient().sendToServer(new Message("#newExecutedVirtualExam", endExam(false)));
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        EventBus.getDefault().unregister(this);
        App.setRoot("studentMain");
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////// Server Replay //////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////

    @Subscribe
    public void overtimeAddedMessage(EventOvertimeAdded event)
    {
        // TODO need to set the infoID to the vxam according to the ExecutedExamInfo
        if(event.getInfoID() == vexam.getInfoID()){

        }
//        error_bar_text.setText("Please Choose a Subject");
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

    public void finish_exam_click(ActionEvent actionEvent) throws IOException {
//        try {
//            SimpleClient.getClient().sendToServer(new Message("#newExecutedVirtualExam", endExam(true)));
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        EventBus.getDefault().unregister(this);
        App.setRoot("studentMain");
    }

    public void prev_button_click(ActionEvent actionEvent)
    {
        currentQuestionNumber = (currentQuestionNumber + examLength - 1 ) % examLength;
        loadNewQuestion(currentQuestionNumber);
    }
    public void next_button_click(ActionEvent actionEvent)
    {
        currentQuestionNumber = (currentQuestionNumber+1)%examLength;
        loadNewQuestion(currentQuestionNumber);
    }

    public void msin() {
        setButtonColor(finish_exam_button,"green");
    }
    public void msout() {
        setButtonColor(finish_exam_button,"orange");
    }

}
