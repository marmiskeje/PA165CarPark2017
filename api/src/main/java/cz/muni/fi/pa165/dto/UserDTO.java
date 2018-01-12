package cz.muni.fi.pa165.dto;

import cz.muni.fi.pa165.dto.enums.UserType;

import java.time.LocalDateTime;

public class UserDTO {

    private long id;
    private String userName;
    private UserType type;
    private LocalDateTime creationDate;
    private LocalDateTime activationDate;
    private LocalDateTime modificationDate;
    private RegionalBranchDTO regionalBranch;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDTO)) return false;

        UserDTO userDTO = (UserDTO) o;

        if (getId() != userDTO.getId()) return false;
        if (getUserName() != null ? !getUserName().equals(userDTO.getUserName()) : userDTO.getUserName() != null)
            return false;
        return getType() == userDTO.getType();
    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + (getUserName() != null ? getUserName().hashCode() : 0);
        result = 31 * result + (getType() != null ? getType().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", type=" + type +
                ", creationDate=" + creationDate +
                ", activationDate=" + activationDate +
                ", modificationDate=" + modificationDate +
                ", regionalBranchId=" + ((regionalBranch == null) ? null :regionalBranch.getId()) +
                ", regionalBranchName=" + ((regionalBranch == null) ? null :regionalBranch.getName()) +
                '}';
    }
}
