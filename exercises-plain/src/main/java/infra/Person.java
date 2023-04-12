package infra;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

// tag::code[]
public class Person {
    @NotBlank
    @Size(max = 80)
    private String firstName;
    @NotNull
    @Size(min = 2, max = 80)
    private String lastName;
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    @Past
    private LocalDate birthday;

    // getters, setters, toString...
    // end::code[]

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "Person [firstName=" + firstName + ", lastName=" + lastName + ", birthday=" + birthday + "]";
    }
    // tag::code[]
}
//end::code[]
