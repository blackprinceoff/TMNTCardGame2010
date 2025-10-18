module org.author.demo.tmntcardgame {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires com.google.gson;

    exports org.author.demo.tmntcardgame;
    exports org.author.demo.tmntcardgame.controller;
    exports org.author.demo.tmntcardgame.model.game;
    exports org.author.demo.tmntcardgame.model.cards;
    exports org.author.demo.tmntcardgame.view;
    exports org.author.demo.tmntcardgame.utils;

    opens org.author.demo.tmntcardgame.controller to javafx.fxml;
    opens org.author.demo.tmntcardgame.model.cards to com.google.gson;
    opens org.author.demo.tmntcardgame.utils to com.google.gson;
}