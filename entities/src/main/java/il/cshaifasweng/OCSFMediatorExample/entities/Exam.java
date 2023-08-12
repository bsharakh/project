package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="Exams")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Exam implements Serializable
{
    @Id
    private int codeExam;
    private String title;
    private int time;
    private String descForStudent;
    private String descForTeacher;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "teacher_id2")
    private Teacher teacher;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,mappedBy ="exam")
    private List<ExecutedExam> executedExams;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,mappedBy ="exam" )
    private List<ExamQuestion> examQuestion;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "examsList", joinColumns = @JoinColumn(name = "exam_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id"))
    private List<Course> coursesList;

    public Exam() {}

    public Exam(int time, int codeExam, String descForStudent, String descForTeacher, Teacher teacher)
    {
        super();
        this.time = time;
        this.codeExam = codeExam;
        this.descForStudent = descForStudent;
        this.descForTeacher = descForTeacher;
        setTeacher(teacher);
    }

    public Exam(int codeExam, String title, int time, String descForStudent, String descForTeacher, Teacher teacher, Course course)
    {
        super();
        this.title = title;
        this.codeExam = generateExamCode(codeExam, course);
        this.time = time;
        this.descForStudent = descForStudent;
        this.descForTeacher = descForTeacher;

        this.coursesList = new ArrayList<>();
        this.coursesList.add(course);
        course.getExamsList().add(this);

        this.teacher = teacher;
        teacher.getExams().add(this);
    }

    public Exam(Exam exam)
    {
        super();
        this.title = exam.getTitle();
        this.codeExam = exam.getCodeExam();
        this.time = exam.getTime();
        this.descForStudent = exam.getDescForStudent();
        this.descForTeacher = exam.getDescForTeacher();

//        this.coursesList = new ArrayList<>();
//
//        this.teacher = exam.getTeacher();
    }

    private int generateExamCode (int code, Course course)
    {
        if(course == null) {return code;}
        String finalCode = Integer.toString(course.getSubject().getId());
        finalCode += Integer.toString(course.getId());
        finalCode += Integer.toString(code);
        return Integer.valueOf(finalCode);
    }

    public Exam(int codeExam, String title, List<ExamQuestion> list)
    { // TODO DELETE
        this.title = title;
        this.codeExam = codeExam;
        this.examQuestion = list;
    }

    public int getTime() {
        return time;
    }
    public void setTime(int time) {
        this.time = time;
    }

    public int getCodeExam() {
        return codeExam;
    }
    public void setCodeExam(int codeExam) {
        this.codeExam = codeExam;
    }

    public String getDescForStudent() {
        return descForStudent;
    }
    public void setDescForStudent(String descForStudent) {
        this.descForStudent = descForStudent;
    }

    public String getDescForTeacher() {
        return descForTeacher;
    }
    public void setDescForTeacher(String descForTeacher) {
        this.descForTeacher = descForTeacher;
    }

    public Teacher getTeacher() {
        return teacher;
    }
    public String getTitle() {
        return title;
    }

    public List<Course> getCoursesList() {return coursesList;}

    public void setCoursesList(List<Course> coursesList) {this.coursesList = coursesList;}

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTeacher(Teacher teacher) {
        if (this.teacher != null) {
            this.teacher.getExams().remove(this);
        }

        this.teacher = teacher;
        //teacher.getExams().add(this);
    }

    public List<ExamQuestion> getExamQuestion() {
        return examQuestion;
    }

    public void setExamQuestion(List<ExamQuestion> examQuestion) {
        this.examQuestion = examQuestion;
    }

    public List<ExecutedExam> getExecutedExams() {
        return executedExams;
    }

}


