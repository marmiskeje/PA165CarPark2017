package cz.muni.fi.pa165.dto;

import java.time.LocalDateTime;

/*
@author Matej Kralik, modified by Martin Miskeje
*/
public class CarDTO {

    private Long id;
    private String name;
    private LocalDateTime creationDate;
    private LocalDateTime activationDate;
    private LocalDateTime modificationDate;
    private RegionalBranchDTO regionalBranch = null;
    private boolean deactivated;

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

    public RegionalBranchDTO getRegionalBranch() {
        return regionalBranch;
    }

    public void setRegionalBranch(RegionalBranchDTO regionalBranch) {
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
        if (!(o instanceof CarDTO)) return false;

        CarDTO car = (CarDTO) o;

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
                ", regionalBranchId=" + ((regionalBranch == null) ? null :regionalBranch.getId()) +
                ", regionalBranchName=" + ((regionalBranch == null) ? null :regionalBranch.getName()) +
                '}';
    }
}
