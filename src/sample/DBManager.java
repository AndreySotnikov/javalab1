package sample;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by andrey on 23.04.15.
 */
public class DBManager {
    private Connection connection;
    private Statement statement;
    private ResultSet wrk;
    private ResultSet skl;
    private ResultSet dept;

    private String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private String DB_URL = "jdbc:mysql://localhost/mydb";
    private String DB_PASS = "123456789";
    private String DB_USER = "root";


    public boolean connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            statement = connection.createStatement();

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public List<Student> findAll() throws SQLException {
        connect();
        List<Student> result = new ArrayList<Student>();
        try {
            String query = "select * from Student";
            Statement sst = connection.createStatement();
            Statement mst = connection.createStatement();
            Statement subbst = connection.createStatement();
            ResultSet allStudent = sst.executeQuery(query);
            ResultSet marks;
            ResultSet subject;
            while (allStudent.next()) {
                int id = allStudent.getInt("idStudent");
                String fio = allStudent.getString("fio");
                int course = allStudent.getInt("course");
                int group = allStudent.getInt("cgroup");
                HashMap<String, Integer> marksMap = new HashMap<String, Integer>();
                query = "select * from Mark where Student_idStudent=" + id;
                marks = mst.executeQuery(query);
                int value;
                String subj;
                while (marks.next()) {
                    int subId = marks.getInt("Subject_idSubject");
                    value = marks.getInt("value");
                    query = "select name from Subject where idSubject=" + subId;
                    subject = subbst.executeQuery(query);
                    subject.next();
                    subj = subject.getString("name");
                    subject.close();
                    marksMap.put(subj, value);
                }
                marks.close();
                result.add(new Student(id,fio, course, group, marksMap));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            connection.close();
            statement.close();
            return result;
        }
    }

    public Student findOne(int idStudent) throws SQLException {
        connect();
        Student result=null;
        try {
            String query = "select * from Student where idStudent=" + idStudent;
            Statement sst = connection.createStatement();
            Statement mst = connection.createStatement();
            Statement subbst = connection.createStatement();
            ResultSet allStudent = sst.executeQuery(query);
            ResultSet marks;
            ResultSet subject;
            allStudent.next();
            int id = allStudent.getInt("idStudent");
            String fio = allStudent.getString("fio");
            int course = allStudent.getInt("course");
            int group = allStudent.getInt("cgroup");
            HashMap<String, Integer> marksMap = new HashMap<String, Integer>();
            query = "select * from Mark where Student_idStudent=" + id;
            marks = mst.executeQuery(query);
            int value;
            String subj;
            while (marks.next()) {
                int subId = marks.getInt("Subject_idSubject");
                value = marks.getInt("value");
                query = "select name from Subject where idSubject=" + subId;
                subject = subbst.executeQuery(query);
                subject.next();
                subj = subject.getString("name");
                subject.close();
                marksMap.put(subj, value);
            }
            marks.close();
            result = new Student(fio, course, group, marksMap);
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            connection.close();
            statement.close();
            return result;
        }
    }

    public boolean closeConnection() {
        try {
            connection.close();
            statement.close();

        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public Student save(Student student) throws SQLException {
        connect();
        int idStudent=0;
        try {
            Statement sst = connection.createStatement();
            Statement mst;
            Statement subst;
            String query = String.format("INSERT INTO Student (idStudent, fio, course, cgroup) VALUES (NULL,\'%s\',%d,%d)", student.getFio(), student.getCourse(), student.getGroup());
            sst.execute(query);
            sst.close();
            ResultSet maxid = statement.executeQuery("select max(idStudent) as cnt from Student");
            maxid.next();
            idStudent = maxid.getInt("cnt");
            statement.close();
            student.setId(idStudent);
            for (Map.Entry<String, Integer> elem : student.getMarks().entrySet()) {
                subst = connection.createStatement();
                mst = connection.createStatement();
                int value = elem.getValue();
                String strSub = elem.getKey();
                ResultSet idSubSet = subst.executeQuery(String.format("select idSubject from Subject where name=\'%s\'", strSub));
                int id;
                if (idSubSet.next()) {
                    id = idSubSet.getInt("idSubject");
                    subst.close();
                }else {
                    subst.close();
                    subst = connection.createStatement();
                    subst.execute(String.format("insert into Subject (idSubject, name)values (NULL,\'%s\')",strSub));
                    subst.close();
                    subst = connection.createStatement();
                    idSubSet = subst.executeQuery("select max(idSubject) as maxsub from Subject");
                    idSubSet.next();
                    id = idSubSet.getInt("maxsub");
                    subst.close();
                }
                mst.execute(String.format("insert into Mark (Student_idStudent,Subject_idSubject,value) values (%d,%d,%d)",idStudent,id,value));
                mst.close();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            connection.close();
            statement.close();
            return student;
        }
    }

    public void delete(int id) throws SQLException {
        connect();
        try {
            statement = connection.createStatement();
            statement.execute(String.format("delete from Mark where Student_idStudent=%d",id));
            statement.close();
            statement = connection.createStatement();
            statement.execute(String.format("delete from Student where idStudent=%d",id));
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            connection.close();
            statement.close();
        }
    }

    public void delete(String fio, int course, int group){
        connect();
        int id = 0;
        try {
            statement = connection.createStatement();
            ResultSet idSet = statement.executeQuery(String.format("select idStudent as id from Student where fio=\'%s\' and course=%d and cgroup=%d", fio, course, group));
            if (idSet.next())
                id=idSet.getInt("id");
            statement.close();
            delete(id);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void update(int id, Student student) throws SQLException{
        connect();
        try{
            statement = connection.createStatement();
            statement.execute(String.format("update Student set fio=\'%s\', course=%d, cgroup=%d where idStudent=%d",student.getFio(),student.getCourse(),student.getGroup(),id));
            statement.close();
            statement = connection.createStatement();
            statement.execute(String.format("delete from Mark where Student_idStudent=%d",id));
            statement.close();
            for (Map.Entry<String, Integer> elem : student.getMarks().entrySet()) {
                statement = connection.createStatement();
                Statement subst = connection.createStatement();
                ResultSet idSubSet = statement.executeQuery(String.format("select idSubject from Subject where name=\'%s\'", elem.getKey()));
                int idSub = 0;
                if (idSubSet.next()){
                    idSub = idSubSet.getInt("idSubject");
                    statement.close();
                }else{
                    subst = connection.createStatement();
                    subst.execute(String.format("insert into Subject (idSubject, name)values (NULL,\'%s\')",elem.getKey()));
                    subst.close();
                    subst = connection.createStatement();
                    ResultSet maxId = subst.executeQuery("select max(idSubject) as maxid from Subject");
                    maxId.next();
                    idSub = maxId.getInt("maxid");
                    subst.close();
                }
                statement = connection.createStatement();
                statement.execute(String.format("insert into Mark (Student_idStudent,Subject_idSubject,value) values (%d,%d,%d)",id,idSub,elem.getValue()));
                statement.close();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            connection.close();
            statement.close();
        }
    }
}
