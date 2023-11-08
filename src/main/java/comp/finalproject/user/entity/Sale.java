package comp.finalproject.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sales")
public class Sale implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "iditem", unique = false, referencedColumnName = "id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "idUser")
    @JsonIgnoreProperties("sales")
    private User user;

    private int quantity;
    private float subtotal;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    private String status;

    public Sale(Item item, int quantity, float subtotal, Long userId) {
        this.item = item;
        this.quantity = quantity;
        this.subtotal = subtotal;
        this.user = new User();
        this.user.setId(userId);
    }
}
