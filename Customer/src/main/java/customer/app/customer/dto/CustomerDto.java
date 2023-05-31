package customer.app.customer.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@NoArgsConstructor
public class CustomerDto {
    private Long id;
    private String first_name;
    private String last_name;
    private String unique_store_identifier;

}
