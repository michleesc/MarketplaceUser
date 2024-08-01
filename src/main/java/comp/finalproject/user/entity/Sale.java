package comp.finalproject.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

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
    private long id;
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "id_item", referencedColumnName = "id")
    private Item item;
    @ManyToOne
    @JoinColumn(name = "id_user")
    @JsonIgnoreProperties("sales")
    private User user;
    private int quantity;
    private float subtotal;
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    private String status;
    @Column(name = "metode_pembayaran")
    private String metodePembayaran;
    @Column(name = "proof_of_payment")
    private String proofOfPayment;
    @Column(name = "cash_input")
    private Float cashInput;
    @Column(name = "change_payment")
    private Float changePayment;
    private boolean deleted = false;

    public Sale(Item item, int quantity, Float cashInput, Float changePayment, float subtotal, Long userId) {
        this.item = item;
        this.quantity = quantity;
        this.cashInput = cashInput;
        this.changePayment = changePayment;
        this.subtotal = subtotal;
        this.user = new User();
        this.user.setId(userId);
    }
}
