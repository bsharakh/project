package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.application.Platform;
import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

import java.util.ArrayList;
import java.util.List;

public class SimpleClient extends AbstractClient {
	
	private static SimpleClient client = null;

	private SimpleClient(String host, int port) {
		super(host, port);
	}

	@Override
	protected void handleMessageFromServer(Object msg)
	{
		Message message = (Message) msg;
		String messageSt = message.getMessage();
		System.out.println("message: "+messageSt);

		if (messageSt.equals("#LogInSuccessfully"))
		{
			User user = (User) message.getObject1();
			loginEvent newEvent= new loginEvent(user);
			Platform.runLater(()->{
			EventBus.getDefault().post(newEvent);
					}
			);
		}
		else if (messageSt.equals("#loginWarning"))
		{
			Platform.runLater(()->{
			EventBus.getDefault().post(new WarningEvent((Warning) message.getObject1()));
					}
			);
		}
		else if (messageSt.equals("#GetUserResponce"))
		{
			User user = (User) message.getObject1();
			getUserEvent newEvent = new getUserEvent(user);
			Platform.runLater(()->{
						EventBus.getDefault().post(newEvent);
					}
			);
		}
		else if(messageSt.equals("#SolveExamResponse"))
		{

			Exam exam=(Exam) message.getObject1();
			SolveExamEvent newEvent=new SolveExamEvent(exam);
			Platform.runLater(()->{
						EventBus.getDefault().post(newEvent);
					}
			);

		}

		else if(messageSt.equals("#StartSolveSuccessfully"))
		{

			Object[] obj = (Object[]) message.getObject1();

			Exam exam=(Exam)obj[0];
			ExecutedExamInfo.ExamType examType=(ExecutedExamInfo.ExamType)obj[1];

			StartSolveExamEvent newEvent=new StartSolveExamEvent(exam,examType);
			Platform.runLater(()->{
						EventBus.getDefault().post(newEvent);
					}
			);

		}
	else if (messageSt.equals("#StartSolveWarning"))
	{
		Platform.runLater(()->{
					EventBus.getDefault().post(new WarningEvent((Warning) message.getObject1()));
				}
		);
	}

		else if (messageSt.equals("#SolveExamWarning"))
		{
			Platform.runLater(()->{
						EventBus.getDefault().post(new WarningEvent((Warning) message.getObject1()));
					}
			);
		}

		else if (messageSt.equals("#ShowAllStudents"))
		{
			List<Student> students=(List<Student>) message.getObject1();
			for(int i=0;i<students.size();i++)
			{
				System.out.println(students.get(i).getUserName());
			}
			ShowStudentsEvent newEvent= new ShowStudentsEvent(students);
			Platform.runLater(()->{
						EventBus.getDefault().post(newEvent);
					}
			);
		}

		else if (messageSt.equals("#ShowAllTeachers"))
		{
			System.out.println("SHSHSHSHSHSHSHSH");
			List<Teacher> teachers=(List<Teacher>) message.getObject1();
			for(int i=0;i<teachers.size();i++)
			{
				System.out.println(teachers.get(i).getUserName());
			}
			ShowTeachersEvent newEvent= new ShowTeachersEvent(teachers);
			Platform.runLater(()->{
						EventBus.getDefault().post(newEvent);
					}
			);
		}
		else if (messageSt.equals("#ShowStudentExecutedExams"))
		{
			ArrayList<ExecutedExam> exe_ex=(ArrayList<ExecutedExam>) message.getObject1();
			for(int i=0;i<exe_ex.size();i++)
			{
				System.out.println(exe_ex.get(i).getExamNum());
			}
			ShowStudentsExecutedExamsEvent newEvent= new ShowStudentsExecutedExamsEvent(exe_ex);
			Platform.runLater(()->{
						EventBus.getDefault().post(newEvent);
					}
			);
		}
		else if (messageSt.equals("#ShowAllTeachersExamInfo"))
		{
			ArrayList<ExecutedExamInfo> exe_ex_info=(ArrayList<ExecutedExamInfo>) message.getObject1();
			for(int i=0;i<exe_ex_info.size();i++)
			{
				System.out.println(exe_ex_info.get(i).getCode());
			}
			ShowTeachersExamInfoEvent newEvent= new ShowTeachersExamInfoEvent(exe_ex_info);
			Platform.runLater(()->{
						EventBus.getDefault().post(newEvent);
					}
			);
		}
		else if (messageSt.equals("#ShowTeachersExamInfoDetails"))
		{
			ExecutedExamInfo[] arrex_info=(ExecutedExamInfo[]) message.getObject1();
//
//			ExecutedExamInfo ex_info1 = arrex_info[0];
//			ExecutedExamInfo ex_info2 = arrex_info[1];
//
//			for(int i=0;i<exe_ex_info.size();i++)
//			{
//				System.out.println(exe_ex_info.get(i).getCode());
//			}
			ShowTeachersExamInfoDetailsEvent newEvent= new ShowTeachersExamInfoDetailsEvent(arrex_info);
			Platform.runLater(()->{
						EventBus.getDefault().post(newEvent);
					}
			);
		}
		else if (messageSt.equals("#CodeForETExists"))
		{
			Exam exam = (Exam) message.getObject1();
			System.out.println(exam.getCodeExam());
			ShowExamEvent newEvent= new ShowExamEvent(exam);
			Platform.runLater(()->{
						EventBus.getDefault().post(newEvent);
					}
			);
		}
		else if (messageSt.equals("#GetAllSubjectsResponce"))
		{
			List<Subject> allSubjects = (List<Subject>) message.getObject1();
			EventGetAllSubjects newEvent = new EventGetAllSubjects(allSubjects);
			Platform.runLater(()->{
						EventBus.getDefault().post(newEvent);
					}
			);
		}
		else if (messageSt.equals("#getAllSubjectsNames_Replay"))
		{
			List<String> list = (List<String>) message.getObject1();
			EventGetAllSubjectsNames newEvent = new EventGetAllSubjectsNames(list);
			Platform.runLater(()->{EventBus.getDefault().post(newEvent);});
		}
		else if (messageSt.equals("#GetAllCoursesBySubject_Replay"))
		{
			List<String> list = (List<String>) message.getObject1();
			EventGetAllCoursesBySubject newEvent = new EventGetAllCoursesBySubject(list);
			Platform.runLater(()->{EventBus.getDefault().post(newEvent);});
		}
		else if (messageSt.equals("#GetAllExamsByCourse_Replay"))
		{
			List<Exam> list = (List<Exam>) message.getObject1();
			EventGetAllExamsByCourse newEvent = new EventGetAllExamsByCourse(list);
			Platform.runLater(()->{EventBus.getDefault().post(newEvent);});
		}
    else if (messageSt.equals("#GetAllQuestionsByCourse_Replay"))
		{
			List<Question> list = (List<Question>) message.getObject1();
			EventGetAllQuestionsByCourse newEvent = new EventGetAllQuestionsByCourse(list);
			Platform.runLater(()->{EventBus.getDefault().post(newEvent);});
		}
		else if (messageSt.equals("#GetAllWrittenExamRes"))
		{
			List<ExecutedExamInfo> allwrittenInfo = (List<ExecutedExamInfo>) message.getObject1();
			WrittenExamViewEvent newevent= new WrittenExamViewEvent(allwrittenInfo);
			Platform.runLater(()->{
						EventBus.getDefault().post(newevent);
					}
			);
		}
		else if (messageSt.equals("#GetAllExcutedExamRes"))
	{
		List<ExecutedExamInfo> allexcutedInfo = (List<ExecutedExamInfo>) message.getObject1();
		ExcutedExamViewEvent newevent= new ExcutedExamViewEvent(allexcutedInfo);
		Platform.runLater(()->{
					EventBus.getDefault().post(newevent);
				}
		);
	}
		else if(messageSt.equals("#GetExcutedExamRes"))
		{
			Object[] obj = (Object[]) message.getObject1();

			List<ExecutedExam> executedExamList=(List<ExecutedExam>)obj[0];
			ExecutedExamInfo examInfo=(ExecutedExamInfo) obj[1];

			ExcutedExamEvent newEvent=new ExcutedExamEvent(executedExamList,examInfo);
			Platform.runLater(()->{
						EventBus.getDefault().post(newEvent);
					}
			);

		}
		else if (messageSt.equals("#UpdateGradeSuccessfully"))
		{

		}
		else if (messageSt.equals("#UpdateGradeWarning"))
		{
			Platform.runLater(()->{
						EventBus.getDefault().post(new WarningEvent((Warning) message.getObject1()));
					}
			);
		}
		else if (messageSt.equals("#GetTeacherAllExams_Replay"))
		{
			Object [] data = (Object[]) message.getObject1();
			ArrayList<ExecutedExamInfo> writtenExamsInfoList = (ArrayList<ExecutedExamInfo>) data[0];
			ArrayList<ExecutedExamInfo> executedExamInfoList = (ArrayList<ExecutedExamInfo>) data[1];

			EventGetTeacherAllExams newEvent= new EventGetTeacherAllExams(writtenExamsInfoList,executedExamInfoList);
			Platform.runLater(()->{EventBus.getDefault().post(newEvent);});
		}
		else if (messageSt.equals("#returunGrade"))
		{
			Object [] data = (Object[]) message.getObject1();
			ArrayList<ExecutedExamInfo> writtenExamsInfoList = (ArrayList<ExecutedExamInfo>) data[0];
			ArrayList<ExecutedExamInfo> executedExamInfoList = (ArrayList<ExecutedExamInfo>) data[1];

			EventGetTeacherAllExams newEvent= new EventGetTeacherAllExams(writtenExamsInfoList,executedExamInfoList);
			Platform.runLater(()->{EventBus.getDefault().post(newEvent);});
		}

	}
	
	public static SimpleClient getClient() {
		if (client == null) {
			client = new SimpleClient("localhost", 3000);
		}
		return client;
	}

}
