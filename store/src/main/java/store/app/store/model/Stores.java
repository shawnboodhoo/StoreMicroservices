package store.app.store.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Stores {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(nullable = false)
    private String storeName;
    @Column(nullable = false)
    private String location;
    @Column(nullable = false)
    private String sameStoreStoreIdentifier;
    @Column(nullable = false)
    private String uniqueStoreIdentifier;
}
