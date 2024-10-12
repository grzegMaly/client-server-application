package application.portfolio.clientmodule.OtherElements;

import java.util.UUID;

public class GroupDAO {

    private final UUID groupId = UUID.randomUUID();
    private String groupName;
    private PersonDAO owner;

    public GroupDAO(String groupName) {
        this(groupName, null);
    }

    public GroupDAO(String groupName, PersonDAO owner) {
        this.groupName = groupName;
        this.owner = owner;
    }

    public UUID getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public PersonDAO getOwner() {
        return owner;
    }

    public void setOwner(PersonDAO owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "GroupDAO{" +
                "groupId=" + groupId +
                ", groupName='" + groupName + '\'' +
                ", owner=" + owner +
                '}';
    }
}