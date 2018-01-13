package cz.muni.fi.pa165.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author Matej Kralik, updated by Martin Mi�keje
 */
@Entity
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable=false)
    private String name;

    @NotNull
    @Column(nullable=false)
    private LocalDateTime creationDate;

    private LocalDateTime activationDate;
    
    @NotNull
    @Column(nullable=false)
    private LocalDateTime modificationDate;
    
    @ManyToOne
    @JoinColumn(name = "regionalBranchId", nullable = true)
    private RegionalBranch regionalBranch = null;

    private boolean deactivated = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(LocalDateTime activationDate) {
        this.activationDate = activationDate;
    }

    public LocalDateTime getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(LocalDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }
    
    public RegionalBranch getRegionalBranch() {
        return regionalBranch;
    }

    public void setRegionalBranch(RegionalBranch regionalBranch) {
        this.regionalBranch = regionalBranch;
    }

    public boolean isDeactivated() {
        return deactivated;
    }

    public void setDeactivated(boolean deactivated) {
        this.deactivated = deactivated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof Car)) return false;

        Car car = (Car) o;

        if (getId() != car.getId()) return false;
        if (getName() != null ? !getName().equals(car.getName()) : car.getName() != null) return false;
        return getCreationDate() != null ? getCreationDate().equals(car.getCreationDate()) : car.getCreationDate() == null;
    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getCreationDate() != null ? getCreationDate().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", creationDate=" + creationDate +
                ", activationDate=" + activationDate +
                ", modificationDate=" + modificationDate +
                ", regionalBranchId=" + ((regionalBranch == null) ? null :regionalBranch.getId()) + //due to StackOverflowError because reginalBranch has car class
                ", regionalBranchName=" + ((regionalBranch == null) ? null :regionalBranch.getName()) + //due to StackOverflowError because reginalBranch has car class
                '}';
    }
}