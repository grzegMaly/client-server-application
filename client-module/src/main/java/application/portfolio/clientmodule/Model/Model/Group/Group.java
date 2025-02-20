package application.portfolio.clientmodule.Model.Model.Group;

import application.portfolio.clientmodule.Model.Model.Person.Person;

import java.util.UUID;

public class Group {

    private UUID groupId;
    private String groupName;
    private UUID ownerId;
    private Person owner;

    public Group() {
    }

    public Group(UUID groupId, String groupName) {
        this.groupId = groupId;
        this.groupName = groupName;
    }

    public Group(String groupName, Person owner) {
        this.groupName = groupName;
        this.owner = owner;
    }

    public Group(UUID groupId, String groupName, Person owner) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.owner = owner;
    }

    public UUID getGroupId() {
        return groupId;
    }

    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public static GroupDAO toDAO(Group group) {

        GroupDAO groupDAO = new GroupDAO();

        UUID groupId = group.getGroupId();

        groupDAO.setGroupId(groupId == null ? null : groupId.toString());
        groupDAO.setGroupName(group.getGroupName());
        groupDAO.setOwnerId(group.getOwner().getUserId().toString());
        return groupDAO;
    }

    @Override
    public String toString() {
        return "Group{" +
                "groupId=" + groupId +
                ", groupName='" + groupName + '\'' +
                ", ownerId=" + ownerId +
                ", owner=" + owner +
                '}';
    }
}
