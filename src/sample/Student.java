package sample;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by andrey on 23.04.15.
 */
public class Student {
    private int id;
    private String fio;
    private int course;
    private int group;
    private Map<String,Integer> marks;

    public Student(String fio, int course, int group, Map<String, Integer> marks) {
        this.fio = fio;
        this.course = course;
        this.group = group;
        this.marks = marks;
    }

    public Student(int id,String fio, int course, int group, Map<String, Integer> marks) {
        this.id=id;
        this.fio = fio;
        this.course = course;
        this.group = group;
        this.marks = marks;
    }

    public Student() {
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public int getCourse() {
        return course;
    }

    public void setCourse(int course) {
        this.course = course;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public Map<String, Integer> getMarks() {
        return marks;
    }

    public void setMarks(Map<String, Integer> marks) {
        this.marks = marks;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Student student = (Student) o;

        if (course != student.course) return false;
        if (group != student.group) return false;
        if (fio != null ? !fio.equals(student.fio) : student.fio != null) return false;
        if (marks != null ? !marks.equals(student.marks) : student.marks != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = fio != null ? fio.hashCode() : 0;
        result = 31 * result + course;
        result = 31 * result + group;
        result = 31 * result + (marks != null ? marks.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Student{" +
                "fio='" + fio + '\'' +
                ", course=" + course +
                ", group=" + group +
                ", marks=" + marks +
                '}';
    }
}
