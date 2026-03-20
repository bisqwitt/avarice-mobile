package com.avaricious.components.popups;

public interface IPopup {

    void update(float delta);

    void draw();

    boolean isFinished();
}
