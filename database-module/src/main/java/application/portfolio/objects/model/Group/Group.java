package application.portfolio.objects.model.Group;

import application.portfolio.objects.dao.Group.GroupDAO;
import application.portfolio.objects.dao.DAOConverter;

import java.util.UUID;

public class Group implements DAOConverter<Group, GroupDAO> {

    private UUID groupId;
    private String groupName;
    private UUID ownerId;

    public Group() {
    }
    public Group(String groupName, UUID uoId) {
        this(null, groupName, uoId);
    }

    public Group(UUID gId, String groupName, UUID oId) {
        this.groupId = gId;
        this.groupName = groupName;
        this.ownerId = oId;
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

    @Override
    public GroupDAO toDAO() {
        return new GroupDAO(getGroupId(), getGroupName(), getOwnerId());
    }
}
