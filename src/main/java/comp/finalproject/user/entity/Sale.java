package comp.finalproject.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
    @ManyToOne
    @JoinColumn(name = "id_user")
    @JsonIgnoreProperties("sales")
    private User user;
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
    private Float total;

    public Sale(Float cashInput, Float changePayment, float subtotal, Long userId) {
        this.cashInput = cashInput;
        this.changePayment = changePayment;
        this.user = new User();
        this.user.setId(userId);
    }

    public Sale(Float cashInput, Float changePayment, Float total, Long userId) {
        this.cashInput = cashInput;
        this.changePayment = changePayment;
        this.total = total;
        this.user = new User();
        this.user.setId(userId);
    }
}
