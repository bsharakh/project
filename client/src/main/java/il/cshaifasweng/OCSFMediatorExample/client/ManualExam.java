package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Array;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.greenrobot.eventbus.EventBus;

public class ManualExam {
    @FXML
    Text error_bar_text, exam_name_text;
    StringBuilder documentContent = new StringBuilder();
    String downloaded = "no", uploaded = "no", submitted = "no";

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
    private Text date_text, question_number_text, question_text, student_note_text, clock_text, extra_time_text;
    @FXML
    ImageView note_ImageView;
    @FXML
    ImageView clock_0,clock_1,clock_2,clock_3,clock_4,clock_5,clock_6,clock_7,clock_8;
    ImageView[] clks;
    private int clks_counter, extraTime;
    private LocalTime startTime;

    private boolean timeUpFlag;

    @FXML
    public  void initialize() {
        Exam mExam = App.getExam();
        exam_name_text.setText(mExam.getTitle());
        // Create title
        documentContent.append(mExam.getTitle()).append("\n\n");

        exam = App.getExam();
        exam_name_text.setText(exam.getTitle());
        date_text.setText(App.getDate());

        timeUpFlag = true;
        extraTime = 0;
        startTime = LocalTime.now();
        initClock(exam.getTime());


        // Placeholder for questions and answers
        List<ExamQuestion> questions = mExam.getExamQuestion();


        String[] answers = new String[4];

        int questionNumber = 0;
        char ch = 'a';
        for (ExamQuestion q : questions) {
            // Create question
            questionNumber++;
            documentContent.append("Question " + questionNumber + ": " + q.getQuestion().getQuestion()).append("\n");
            answers = q.getQuestion().getAnswers();
            ch = 'a';
            // Create answers
            for (int i=0; i<4; i++) {
                documentContent.append(ch + ". " + answers[i]).append("\n");
                ch++;
            }

            // Add spacing between questions
            documentContent.append("\n");
        }

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
                    App.setRoot("studentMain");
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


    public void download_Action(ActionEvent actionEvent) {
        downloaded = "yes";
        // Save the document to a file
        try (FileOutputStream out = new FileOutputStream("target/" + "exam_document.docx")) {
            byte[] contentBytes = documentContent.toString().getBytes();
            out.write(contentBytes);
            error_bar_text.setText("Exam document downloaded successfully.");
        } catch (IOException e) {
            System.err.println("Error creating the document: " + e.getMessage());
        }
    }

    public void upload_Action(ActionEvent actionEvent) {
        if (Objects.equals(downloaded, "no"))
        {
            error_bar_text.setText("Invalid Upload! Please download first");
        }
        else {
            uploaded = "yes";
        }

    }

    public void submit_click(ActionEvent actionEvent) throws IOException {
        App.setRoot("studentMain");
    }
}
