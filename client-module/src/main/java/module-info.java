module application.portfolio.clientmodule {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires java.desktop;
    requires com.fasterxml.jackson.databind;
    requires java.compiler;
    requires org.apache.pdfbox;
    exports application.portfolio.clientmodule.Model.Request.Login.LoginRequest;


    exports application.portfolio.clientmodule;
    exports application.portfolio.clientmodule.Model.View;
    exports application.portfolio.clientmodule.Model.View.Bars;
    exports application.portfolio.clientmodule.Model.View.Scenes;
    exports application.portfolio.clientmodule.Model.View.LeftBarCards.Chat;
    exports application.portfolio.clientmodule.Model.Request.Chat.Chat.ChatRequest;
    exports application.portfolio.clientmodule.Model.View.LeftBarCards.Chat.Bars;
    exports application.portfolio.clientmodule.Model.Request.Chat.Chat;
    exports application.portfolio.clientmodule.Connection.WebSocket.Listeners;
    exports application.portfolio.clientmodule.Model.Model.Chat;
    exports application.portfolio.clientmodule.Model.Model.Notes;
    exports application.portfolio.clientmodule.Model.Model.Task;
    exports application.portfolio.clientmodule.Model.Model.Person;
    exports application.portfolio.clientmodule.Model.Model.Group;
    exports application.portfolio.clientmodule.utils.session;
}