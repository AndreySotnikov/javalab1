package sample;

/**
 * Created by andrey on 24.04.15.
 */
public class DTO {
    private static final Student student = new Student();
    public static void setInstance(Student std){
        student.setId(std.getId());
        student.setFio(std.getFio());
        student.setCourse(std.getCourse());
        student.setGroup(std.getGroup());
        student.setMarks(std.getMarks());
    }
    public static Student getInstance(){
        return student;
    }
    public static void setName(){
        student.setFio("");
    }
}
