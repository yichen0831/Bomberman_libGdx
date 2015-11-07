package com.ychstudio.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.ychstudio.Bomberman;
import com.ychstudio.gamesys.GameManager;

public class MainMenuScreen extends ScreenAdapter {

    private final Bomberman game;
    private final SpriteBatch batch;
    private FitViewport viewport;
    private Stage stage;

    private BitmapFont font;

    private Image indicator0;
    private Image indicator1;
    private float indicatorX;
    private float indicatorY;
    private float currentSelection;
    private boolean selected;

    public MainMenuScreen(Bomberman game) {
        this.game = game;
        this.batch = game.getSpriteBatch();
    }

    @Override
    public void show() {
        viewport = new FitViewport(640, 480);
        stage = new Stage(viewport, batch);

        font = new BitmapFont(Gdx.files.internal("fonts/foo.fnt"));

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label easyLabel = new Label("Easy", labelStyle);
        easyLabel.setPosition((640 - easyLabel.getWidth()) / 2, 240);

        Label normalLabel = new Label("Normal", labelStyle);
        normalLabel.setPosition((640 - normalLabel.getWidth()) / 2, 180);

        Label hardLabel = new Label("Hard", labelStyle);
        hardLabel.setPosition((640 - hardLabel.getWidth()) / 2, 120);

        indicatorX = 160f;
        indicatorY = 240f;

        TextureAtlas textureAtlas = GameManager.getInstance().getAssetManager().get("img/actors.pack", TextureAtlas.class);
        indicator0 = new Image(new TextureRegion(textureAtlas.findRegion("MainMenuLogo"), 0, 0, 40, 26));
        indicator0.setSize(80f, 52f);
        indicator0.setPosition(indicatorX, indicatorY);

        indicator1 = new Image(new TextureRegion(textureAtlas.findRegion("MainMenuLogo"), 40, 0, 40, 26));
        indicator1.setSize(80f, 52f);
        indicator1.setPosition(indicatorX, indicatorY);
        indicator1.setVisible(false);

        stage.addActor(easyLabel);
        stage.addActor(normalLabel);
        stage.addActor(hardLabel);
        stage.addActor(indicator0);
        stage.addActor(indicator1);

        currentSelection = 0;
        selected = false;
    }

    private void inputHandler() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            currentSelection--;
            if (currentSelection < 0) {
                currentSelection += 3;
            }
            indicator0.setPosition(indicatorX, indicatorY - currentSelection * 60f);
            indicator1.setPosition(indicatorX, indicatorY - currentSelection * 60f);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            currentSelection++;
            if (currentSelection >= 3) {
                currentSelection -= 3;
            }
            indicator0.setPosition(indicatorX, indicatorY - currentSelection * 60f);
            indicator1.setPosition(indicatorX, indicatorY - currentSelection * 60f);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
            selected = true;

            indicator0.setVisible(false);
            indicator1.setVisible(true);
        }
    }

    @Override
    public void render(float delta) {
        inputHandler();

        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}
