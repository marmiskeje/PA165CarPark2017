package cz.muni.fi.pa165.entity;

import cz.muni.fi.pa165.enums.CarReservationRequestState;
import org.springframework.format.datetime.joda.LocalDateTimeParser;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * @author Matej Kralik
 */
@Entity
public class CarReservationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(optional = false)
    @Column (nullable=false)
    private Car car;

    @NotNull
    @ManyToOne(optional = false)
    @Column(nullable=false)
    private User user;

    @NotNull
    @Column(nullable=false)
    private ZonedDateTime reservationStartDate;

    private ZonedDateTime reservationEndDate;

    @NotNull
    @Column(nullable=false)
    @Enumerated(EnumType.STRING)
    private CarReservationRequestState state;

    @NotNull
    @Column(nullable=false)
    private LocalDateTime creationDate;

    private LocalDateTime modificationDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ZonedDateTime getReservationStartDate() {
        return reservationStartDate;
    }

    public void setReservationStartDate(ZonedDateTime reservationStartDate) {
        this.reservationStartDate = reservationStartDate;
    }

    public ZonedDateTime getReservationEndDate() {
        return reservationEndDate;
    }

    public void setReservationEndDate(ZonedDateTime reservationEndDate) {
        this.reservationEndDate = reservationEndDate;
    }

    public CarReservationRequestState getState() {
        return state;
    }

    public void setState(CarReservationRequestState state) {
        this.state = state;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(LocalDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }
}
