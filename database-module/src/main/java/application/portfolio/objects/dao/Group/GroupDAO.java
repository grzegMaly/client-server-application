package application.portfolio.objects.dao.Group;

import java.util.UUID;

public class GroupDAO {

    private UUID groupId;
    private String groupName;
    private UUID ownerId;

    public GroupDAO() {
    }

    public GroupDAO(UUID groupId, String groupName, UUID ownerId) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.ownerId = ownerId;
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
}
