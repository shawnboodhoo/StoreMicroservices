package store.app.store.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class StoreDto {
    private Long id;
    private String storeName;
    private String location;
    private String sameStoreStoreIdentifier;
    private String uniqueStoreIdentifier;

}
