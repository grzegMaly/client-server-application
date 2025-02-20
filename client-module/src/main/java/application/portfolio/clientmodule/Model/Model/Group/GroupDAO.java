package application.portfolio.clientmodule.Model.Model.Group;


public class GroupDAO {

    private String groupId;
    private String groupName;
    private String ownerId;

    public GroupDAO() {
    }

    public GroupDAO(String groupName) {
        this(groupName, null);
    }

    public GroupDAO(String groupName, String ownerId) {
        this(null, groupName, ownerId);
    }

    public GroupDAO(String groupId, String groupName, String ownerId) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.ownerId = ownerId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public String toString() {
        return "GroupDAO{" +
                "groupId=" + groupId +
                ", groupName='" + groupName + '\'' +
                ", ownerId=" + ownerId +
                '}';
    }
}