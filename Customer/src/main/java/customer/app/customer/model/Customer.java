package customer.app.customer.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Customer {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id", nullable = false)
private Long id;
    @Column(nullable = false)
    private String first_name;
    @Column(nullable = false)
    private String last_name;
    @Column(nullable = false)
    private String unique_store_identifier;

}
