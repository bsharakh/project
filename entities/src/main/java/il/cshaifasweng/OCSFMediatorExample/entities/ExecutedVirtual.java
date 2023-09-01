package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

@Entity
@Table(name = "executedVirtual")
//@DiscriminatorValue("executedvirtual")
public class ExecutedVirtual extends ExecutedExam implements Serializable {

    private String note;

    @Column
    @ElementCollection(targetClass=Integer.class)
    private List<Integer> solutions;

    public ExecutedVirtual (ExecutedExam ex, ArrayList<Integer> solutions)
    {
        super(ex.getTitle(), ex.getInfoID(), ex.getStudentName(), ex.getExamDate(), ex.getStartime(), ex.getEndtime(), ex.isSubmitInTime(), ex.isMarked());
        this.solutions=solutions;
    }

    public ExecutedVirtual (ExecutedExam ex, ArrayList<Integer> solutions, String note)
    {
        super(ex.getTitle(), ex.getInfoID(), ex.getStudentName(), ex.getExamDate(), ex.getStartime(), ex.getEndtime(), ex.isSubmitInTime(), ex.isMarked());
        this.solutions=solutions;
        this.note=note;
    }

    public ExecutedVirtual(int examNum, Student student,String examDate , String startime ,double greade, boolean submitInTime ,Exam exam,String note, ExecutedExamInfo examInfo) {
        super(examNum, student, examDate,startime,greade,submitInTime,exam,examInfo);
        this.note = note;
        this.solutions=new ArrayList<Integer>();
    }

    public ExecutedVirtual(int examNum, Student student,String examDate , String startime ,double greade, boolean submitInTime ,Exam exam,String note, ExecutedExamInfo examInfo, List<Integer> solutions) {
        super(examNum, student, examDate,startime,greade,submitInTime,exam,examInfo);
        this.note = note;
        this.solutions=solutions;
    }

    public ExecutedVirtual() {}


    public String getNote() {return note;}
    public void setNote(String note) {this.note = note;}

    public List<Integer> getSolutions() {return solutions;}
    public void setSolutions(List<Integer> solutions) {this.solutions = solutions;}

}
