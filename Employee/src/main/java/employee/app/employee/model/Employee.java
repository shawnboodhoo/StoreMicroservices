package employee.app.employee.model;

import employee.app.employee.model.enums.EmployeePosition;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(nullable = false)
    private String first_name;
    @Column(nullable = false)
    private String last_name;
    @Column(nullable = false)
    private EmployeePosition position;
    @Column(nullable = false)
    private String unique_store_identifier;
}
