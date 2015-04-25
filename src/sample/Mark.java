package sample;

/**
 * Created by andrey on 24.04.15.
 */
public class Mark {
    private String name;
    private String value;

    public Mark(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Mark() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mark mark = (Mark) o;

        if (value != mark.value) return false;
        if (name != null ? !name.equals(mark.name) : mark.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        return result;
    }

    @Override
    public String toString() {
        return "Mark{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
