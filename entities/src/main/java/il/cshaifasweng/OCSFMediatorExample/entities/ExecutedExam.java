package il.cshaifasweng.OCSFMediatorExample.entities;


import javax.persistence.*;
import java.util.List;
import java.io.Serializable;

@Entity
@Table(name="executedExams")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ExecutedExam implements Serializable {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int examNum;

    private String title;

    private int infoID;

    private String examDate;

    private  String startime;

    private String studentName;

    private String endtime;

    private  double grade;

    public Exam getExam() {
        return exam;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    private boolean marked;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "student_id")
    private Student student;

    public boolean isSubmitInTime() {
        return submitInTime;
    }

    public void setSubmitInTime(boolean submitInTime) {
        this.submitInTime = submitInTime;
    }

    private boolean submitInTime;

    public void setExecutedExamInfo(ExecutedExamInfo testDate) {
        this.testDate = testDate;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "testDate_id")
    private ExecutedExamInfo testDate;

    public ExecutedExam() {
    }

    public ExecutedExam(Student student, String examDate, String startime, double grade, boolean submitInTime, Exam exam,ExecutedExamInfo examInfo) {
        super();
        setStudent(student);
        setExam(exam);
        setTestDate(examInfo);
        if(this.student != null){
            this.studentName = this.student.getUserName();
        }else {
            this.studentName = "";
        }
        this.examDate=examDate;
        this.startime=startime;
        this.endtime= this.startime+exam.getTime();
        this.grade=grade;
        this.submitInTime=submitInTime;
        this.title = exam.getTitle();
    }

    public ExecutedExam(Student student, String examDate, String startime, double grade, boolean submitInTime, Exam exam) {
        super();
        setStudent(student);
        setExam(exam);
        if(this.student != null){
            this.studentName = this.student.getUserName();
        }else {
            this.studentName = "";
        }
        this.examDate=examDate;
        this.startime=startime;
        this.endtime= this.startime+exam.getTime();
        this.grade=grade;
        this.submitInTime=submitInTime;
        this.title = exam.getTitle();
    }

    public ExecutedExam(Student student, String examDate, String startime, boolean submitInTime) {
        super();
        setStudent(student);
        if(this.student != null){
            this.studentName = this.student.getUserName();
        }else {
            this.studentName = "";
        }
        this.examDate=examDate;
        this.startime=startime;
//        this.endtime= this.startime+exam.getTime();
        this.submitInTime=submitInTime;
//        this.title = exam.getTitle();
    }

    public ExecutedExam(ExecutedExam exam) {
        super();
        this.examNum = exam.getExamNum();
        this.title = exam.exam.getTitle();
//        setStudent(exam.getStudent());
//        setExam(exam.getExam());
        this.examDate=exam.getExamDate();
        this.startime=exam.getStartime();
        this.endtime= this.startime+exam.getExam().getTime();
        this.grade=exam.getGrade();
        this.submitInTime=exam.isSubmitInTime();
        this.marked = false;
    }

    public int getInfoID() { return infoID; }

    public void setInfoID(int infoID) { this.infoID = infoID; }

    public int getExamNum() {return examNum;}

    public void setExamNum(int examNum) {this.examNum = examNum;}

    public String getExamDate() {
        return examDate;
    }

    public void setExamDate(String examDate) {
        this.examDate = examDate;
    }

    public String getStartime() {
        return startime;
    }

    public void setStartime(String startime) {
        this.startime = startime;
    }

    public void setEndtime(String endtime) {
        this.startime = endtime;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    public boolean isInDuration() {
        return submitInTime;
    }

    public void setInDuration(boolean inDuration) {
        this.submitInTime = inDuration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ExecutedExamInfo getTestDate() {
        return testDate;
    }

    public Student getStudent() {
        return student;
    }

    public void setTestDate(ExecutedExamInfo testDate) {
        if (this.testDate != null) {
            this.testDate.getExecutedExamList().remove(this);
        }
        this.testDate = testDate;
        if (testDate != null) {
            testDate.getExecutedExamList().add(this);
        }
    }

    //TODO:check if need
    public void setStudent(Student student)
    {
        if(this.student!=null)
        {
            this.student.getMyExams().remove(this);
        }
        this.student=student;
        student.addExam1(this);
    }
//
//
    public void setExam(Exam exam)
    {
        if(this.exam==null && exam!=null)
        {
            this.exam=exam;
            exam.addExecutedExam(this);
            this.title = exam.getTitle();
        }
    }
//    public void setStudent(Student student) {
//        if (this.student != null) {
//            if(this.student.getMyExams()!=null){this.student.getMyExams().remove(this);}
//        }
//        this.student = student;
//        if (student != null) {
//            if(student.getMyExams()!=null){student.getMyExams().add(this);}
//        }
//    }


}
