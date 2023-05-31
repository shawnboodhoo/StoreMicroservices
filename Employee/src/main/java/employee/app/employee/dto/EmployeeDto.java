package employee.app.employee.dto;

import employee.app.employee.model.enums.EmployeePosition;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@NoArgsConstructor
public class EmployeeDto {
    private Long id;
    private String first_name;
    private String last_name;
    private EmployeePosition position;
    private String unique_store_identifier;
}
