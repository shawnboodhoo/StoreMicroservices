package employee.app.employee.model.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum EmployeePosition {

    @JsonProperty("manager")
    MANAGER("Manager"),
    @JsonProperty("boss")
    BOSS("Boss"),
    @JsonProperty("employee")
    EMPLOYEE("Employee");

    private String position;

    EmployeePosition(String position) {
        this.position = position;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
