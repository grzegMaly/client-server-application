package application.portfolio.objects.model.Group;

import application.portfolio.utils.DataParser;
import com.fasterxml.jackson.databind.JsonNode;

import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class GroupUtils {

    private static final List<String> existingGroupKeys = List.of("id", "groupName", "ownerId");
    private static final List<String> newGroupKeys = List.of("groupName", "ownerId");
    public static Group createGroup(ResultSet rs) {
        try {
            String id = rs.getString(1);
            String groupName = rs.getString(2).trim();
            String groupOwnerId = rs.getString(3);
            UUID gId = DataParser.parseId(id);
            UUID oId = DataParser.parseId(groupOwnerId);

            if (gId == null || oId == null || groupName.isBlank()) {
                throw new Exception();
            }
            return new Group(gId, groupName, oId);
        } catch (Exception e) {
            return null;
        }
    }

    public static List<Group> createGroup(JsonNode node) {

        List<Group> validatedGroups = Collections.synchronizedList(new ArrayList<>());
        BlockingQueue<Group> queue = new LinkedBlockingQueue<>();
        Object lock = new Object();

        Consumer<JsonNode> jNodeLambda = n -> {
            String[] keysArr = DataParser.getNodeKeys(n);
            Group group = createGroup(n, keysArr);
            if (group != null) {
                queue.add(group);
            }
        };

        final boolean[] isProducerRunning = {true};
        Thread producer = new Thread(() -> {
            try {
                if (node.isArray()) {
                    for (JsonNode groupNode : node) {
                        jNodeLambda.accept(groupNode);
                    }
                } else {
                    jNodeLambda.accept(node);
                }
            } catch (Exception e) {
                throw new RuntimeException();
            } finally {
                synchronized (lock) {
                    isProducerRunning[0] = false;
                    lock.notifyAll();
                }
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                while (true) {
                    synchronized (lock) {
                        if (queue.isEmpty() && !isProducerRunning[0]) {
                            break;
                        }
                    }

                    Group group = queue.poll();
                    if (group != null && validateGroup(group)) {
                        validatedGroups.add(group);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException();
            }
        });

        producer.start();
        consumer.start();

        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return validatedGroups;
    }

    private static Group createGroup(JsonNode node, String[] keysArr) {
        Group group = null;
        try {
            Set<String> keys = new HashSet<>(Arrays.asList(keysArr));
            String[] data = DataParser.parseElements(node, keysArr);
            if (keys.size() == existingGroupKeys.size()) {
                if (keys.containsAll(existingGroupKeys)) {
                    group = createExistingGroup(data);
                }
            } else if (keys.size() == newGroupKeys.size()) {
                if (keys.containsAll(newGroupKeys)) {
                    group = createNewGroup(data);
                }
            }
        } catch (IllegalArgumentException ignored) {
        }
        return group;
    }

    private static Group createExistingGroup(String[] data) {

        Group group = createNewGroup(new String[]{data[1], data[2]});
        String id = data[0].trim();
        UUID ugId = DataParser.parseId(id);

        group.setGroupId(ugId);
        return group;
    }

    private static Group createNewGroup(String[] data) {
        String groupName = data[0].trim();
        String ownerId = data[1].trim();

        UUID uoId = DataParser.parseId(ownerId);
        return new Group(groupName, uoId);
    }

    private static boolean validateGroup(Group group) {
        return true;
    }
}
