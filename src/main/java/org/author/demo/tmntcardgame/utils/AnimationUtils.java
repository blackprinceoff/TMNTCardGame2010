package org.author.demo.tmntcardgame.utils;


import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class AnimationUtils {

    public static void flipCard(Node card, Runnable midFlipAction) {
        RotateTransition rotateOut = new RotateTransition(Duration.millis(300), card);
        rotateOut.setAxis(Rotate.Y_AXIS);
        rotateOut.setFromAngle(0);
        rotateOut.setToAngle(90);

        RotateTransition rotateIn = new RotateTransition(Duration.millis(300), card);
        rotateIn.setAxis(Rotate.Y_AXIS);
        rotateIn.setFromAngle(90);
        rotateIn.setToAngle(0);

        rotateOut.setOnFinished(e -> {
            if (midFlipAction != null) {
                midFlipAction.run();
            }
            rotateIn.play();
        });

        rotateOut.play();
    }

    public static void shakeNode(Node node) {
        TranslateTransition shake = new TranslateTransition(Duration.millis(100), node);
        shake.setFromX(0);
        shake.setToX(10);
        shake.setCycleCount(4);
        shake.setAutoReverse(true);
        shake.play();
    }

    public static void fadeIn(Node node) {
        FadeTransition fade = new FadeTransition(Duration.millis(500), node);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
    }

    public static void fadeOut(Node node) {
        FadeTransition fade = new FadeTransition(Duration.millis(500), node);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.play();
    }

    public static void scaleAnimation(Node node, double scale) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(300), node);
        scaleTransition.setToX(scale);
        scaleTransition.setToY(scale);
        scaleTransition.play();
    }

    public static void moveCard(Node card, double toX, double toY) {
        TranslateTransition move = new TranslateTransition(Duration.millis(500), card);
        move.setToX(toX);
        move.setToY(toY);
        move.play();
    }

    public static ParallelTransition createDealAnimation(Node card, double toX, double toY) {
        TranslateTransition move = new TranslateTransition(Duration.millis(600), card);
        move.setToX(toX);
        move.setToY(toY);

        RotateTransition rotate = new RotateTransition(Duration.millis(600), card);
        rotate.setByAngle(360);

        ParallelTransition parallel = new ParallelTransition(move, rotate);
        return parallel;
    }
}
