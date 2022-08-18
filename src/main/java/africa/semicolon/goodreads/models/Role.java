package africa.semicolon.goodreads.models;

import africa.semicolon.goodreads.models.enums.RoleType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor

public class Role {
    @Id
//    @Column(name="id", nullable = false)
//    @SequenceGenerator(name = "role_id_sequence", sequenceName = "role_id_sequence")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(value = EnumType.STRING)
    private RoleType roleType;

    public Role(RoleType roleType){
        this.roleType = roleType;
    }
}
