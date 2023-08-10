package il.cshaifasweng.OCSFMediatorExample.entities;

import il.cshaifasweng.OCSFMediatorExample.entities.Student;
import il.cshaifasweng.OCSFMediatorExample.entities.Teacher;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
public class Course implements Serializable
{


//    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "studentsList")
//    private List<Student> studentList;

//    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "teachersList")
//    private List<Teacher> teacherList;
//    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "examsList")
//    private List<Exam> examsList;

    //    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "coursesList")
//    private List<Question> questions;

    @Id
    private int id;

    private  String courseName;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;


    public Course() {}

    public Course(int id ,String courseName)
    {
        this.id=id;
        this.courseName = courseName;
    }



    public Course(int id ,String courseName,Subject subject)
    {
        this.id=id;
        this.courseName = courseName;
        setSubject(subject);
//        setSubject(subject);
   //   this.subject.addCourse(this);
    }



    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        if(subject != null){
            this.subject = subject;
            subject.getCourses().add(this);
        }
    }

//    public List<Question> getQuestions() {
//        return questions;
//    }

//    public void setQuestions(List<Question> questions) {
//        this.questions = questions;
//    }


//    public void addTeacher(Teacher... teachersList) {
//        for (Teacher  teacher: teachersList) {
//            this.teacherList.add(teacher);
//            teacher.getCoursesList().add(this);
//        }
//    }

//    public void addStudent(Student... students) {
//        for (Student  student: students) {
//            this.studentList.add(student);
//            student.getCoursesList().add(this);
//        }
//    }


//    public void addQuestion(Question... questions) {
//        for (Question question: questions) {
//            this.questions.add(question);
//            question.getCoursesList().add(this);
//        }
//    }

//    public void addExam(Exam... exams) {
//        for (Exam myExams: exams) {
//            this.examsList.add(myExams);
//            myExams.getCoursesList().add(this);
//        }
//    }

//    public List<Exam> getExamsList() {
//        return examsList;
//    }
//
//    public void setExamsList(List<Exam> examsList) {
//        this.examsList = examsList;
//    }


}
