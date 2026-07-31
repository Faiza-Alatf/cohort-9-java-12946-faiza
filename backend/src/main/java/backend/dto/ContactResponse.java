package backend.dto;

public class ContactResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String title;
    private String workEmail;
    private String personalEmail;
    private String workPhone;
    private String homePhone;
    private String personalPhone;

    public ContactResponse() {
    }

    public ContactResponse(
            Long id,
            String firstName,
            String lastName,
            String title,
            String workEmail,
            String personalEmail,
            String workPhone,
            String homePhone,
            String personalPhone) {

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.title = title;
        this.workEmail = workEmail;
        this.personalEmail = personalEmail;
        this.workPhone = workPhone;
        this.homePhone = homePhone;
        this.personalPhone = personalPhone;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getTitle() {
        return title;
    }

    public String getWorkEmail() {
        return workEmail;
    }

    public String getPersonalEmail() {
        return personalEmail;
    }

    public String getWorkPhone() {
        return workPhone;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public String getPersonalPhone() {
        return personalPhone;
    }
}