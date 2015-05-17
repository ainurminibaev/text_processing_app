package pack.model;

import javax.persistence.*;

/**
 * Created by ainurminibaev on 12.05.15.
 */
@MappedSuperclass
public class BaseObject {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
