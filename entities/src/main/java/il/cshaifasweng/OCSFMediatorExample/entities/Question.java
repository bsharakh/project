package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="questions")
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Question implements Serializable {
    @Id
    private int code;
    private String question;
    private String[] answers;
    private int correct_answer;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "questions_courses",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id"))
    private List<Course> coursesList;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "question")
    private List<ExamQuestion> examQuestions;

    public Question(int code, String question, int correct_answer) {
        super();
        this.question = question;
        this.correct_answer = correct_answer;
        this.answers = new String[4];
        this.coursesList = new ArrayList<>();
        this.code = generateExamQuestion(code);
    }

    public Question(int code, String question, String[] answers, int correct_answer) {
        super();
        this.question = question;
        this.correct_answer = correct_answer;
        this.answers = answers;
        this.coursesList = new ArrayList<>();
        this.code = generateExamQuestion(code);
    }

    public Question(int code, String question, String[] answers, int correct_answer, List<Course> coursesList) {
        super();
        this.question = question;
        this.correct_answer = correct_answer;
        this.answers = answers;
        this.coursesList = new ArrayList<>();
        for(Course c : coursesList){
            this.coursesList.add(c);
            c.addQuestion(this);
        }
        this.code = generateExamQuestion(code);
    }

    public Question(int code, String question, String[] answers, int correct_answer, Course course) {
        super();
        this.question = question;
        this.correct_answer = correct_answer;
        this.answers = answers;
        this.coursesList = new ArrayList<>();
        this.coursesList.add(course);
        course.addQuestion(this);
        this.code = generateExamQuestion(code);
    }

    public Question(Question question) {
        super();
        this.code = question.getCode();
        this.question = question.getQuestion();
        this.correct_answer = question.getCorrect_answer();
        this.answers = question.getAnswers();
        this.coursesList = new ArrayList<>();
    }

    private int generateExamQuestion (int code){
        int scode = 11;
        if(this.coursesList != null){
            scode = this.coursesList.get(0).getSubject().getId();
        }
        int finalCode = scode*1000 + code;
        return finalCode;
    }

    public Question() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }


    public String[] getAnswers() {
        return answers;
    }

    public void setAnswers(String[] answers) {
        this.answers = answers;
    }


    public int getCorrect_answer() {
        return correct_answer;
    }

    public void setCorrect_answer(int correct_answer) {
        this.correct_answer = correct_answer;
    }

    public void addExamQuestion (ExamQuestion e){
        if(this.examQuestions == null){
            this.examQuestions = new ArrayList<>();
        }
        this.examQuestions.add(e);
    }

    public void addCourse (Course c){
        if(this.coursesList == null){
            this.coursesList = new ArrayList<>();
        }
        this.coursesList.add(c);
        c.addQuestion(this);
    }


//

    public List<Course> getCoursesList() {
        return coursesList;
    }

    public void resetCoursesList(){
        this.coursesList = new ArrayList<>();
    }
//
//    public void setCoursesList(List<Course> coursesList) {
//        this.coursesList = coursesList;
//    }

//    public void addCourse(Course...coursesList ) {
//        for (Course  course: coursesList) {
//            this.coursesList.add(course);
//            course.getQuestions().add(this);
//        }
//    }


}
