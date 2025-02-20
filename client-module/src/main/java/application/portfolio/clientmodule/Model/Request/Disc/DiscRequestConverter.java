package application.portfolio.clientmodule.Model.Request.Disc;

import application.portfolio.clientmodule.Connection.UserSession;
import application.portfolio.clientmodule.Model.Request.Disc.DiscRequest.DiscRequest;
import application.portfolio.clientmodule.utils.DataParser;

import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

public class DiscRequestConverter {

    public DiscRequest convertToRequest(Path path) {
        return convertToRequest(path, false);
    }

    public DiscRequest convertToRequest(Path path, boolean isFile) {
        UUID userId = UserSession.getInstance().getLoggedInUser().getUserId();;
        return new DiscRequest(userId, path, isFile);
    }

    public static String toQueryParams(DiscRequest discRequest) {
        String uId = discRequest.getUserId().toString();
        String pathString = discRequest.getPath().toString().replace("\\", "/");
        pathString = pathString.replace(" ", "+");
        return DataParser.paramsString(Map.of("userId", uId, "path", pathString));
    }
}
