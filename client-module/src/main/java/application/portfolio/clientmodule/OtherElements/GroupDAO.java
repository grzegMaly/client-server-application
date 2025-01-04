package application.portfolio.clientmodule.OtherElements;


import application.portfolio.clientmodule.utils.DataParser;

import java.util.UUID;

public class GroupDAO {

    private UUID groupId;
    private String groupName;
    private UUID ownerId;

    public GroupDAO(String groupName) {
        this(null, groupName);
    }

    public GroupDAO(String groupName, String ownerId) {
        this(null, groupName, ownerId);
    }

    public GroupDAO(String groupId, String groupName, String ownerId) {
        this.groupId = DataParser.parseId(groupId);
        this.groupName = groupName;
        this.ownerId = DataParser.parseId(ownerId);
    }

    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
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

    public UUID getOwner() {
        return ownerId;
    }

    public void setOwner(UUID owner) {
        this.ownerId = owner;
    }

    @Override
    public String toString() {
        return "GroupDAO{" +
                "groupId=" + groupId +
                ", groupName='" + groupName + '\'' +
                ", owner=" + ownerId +
                '}';
    }
}