package com.ychstudio.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
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

    private Texture backgroundTexture;
    
    private Texture indicationsTexture;
    private Image indications;

    private Image indicator0;
    private Image indicator1;
    private float indicatorX;
    private float indicatorY;
    private int currentSelection;
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

        Label titleLabel = new Label("Bomberman", labelStyle);
        titleLabel.setFontScale(1.6f);
        titleLabel.setPosition(140, 360);

        Label easyLabel = new Label("Easy", labelStyle);
        easyLabel.setPosition((640 - easyLabel.getWidth()) / 2, 240);

        Label normalLabel = new Label("Normal", labelStyle);
        normalLabel.setPosition((640 - normalLabel.getWidth()) / 2, 180);

        Label hardLabel = new Label("Hard", labelStyle);
        hardLabel.setPosition((640 - hardLabel.getWidth()) / 2, 120);

        Pixmap pixmap = new Pixmap(640, 480, Pixmap.Format.RGB888);
        pixmap.setColor(240.0f / 255.0f, 128 / 255.0f, 0, 1.0f);
        pixmap.fill();
        backgroundTexture = new Texture(pixmap);
        pixmap.dispose();
        Image background = new Image(backgroundTexture);

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
        
        indicationsTexture = new Texture("img/indications.png");
        indications = new Image(indicationsTexture);
        indications.setPosition(640f - indications.getWidth() - 12f, 12f);

        stage.addActor(background);
        stage.addActor(indications);
        stage.addActor(titleLabel);
        stage.addActor(easyLabel);
        stage.addActor(normalLabel);
        stage.addActor(hardLabel);
        stage.addActor(indicator0);
        stage.addActor(indicator1);

        currentSelection = 0;
        selected = false;
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && !selected) {
            GameManager.getInstance().playSound("Pickup.ogg");
            currentSelection--;
            if (currentSelection < 0) {
                currentSelection += 3;
            }

            float newIndicatorY = indicatorY - currentSelection * 60f;

            MoveToAction moveToAction = new MoveToAction();
            moveToAction.setPosition(indicatorX, newIndicatorY);
            moveToAction.setDuration(0.2f);
            indicator0.clearActions();
            indicator0.addAction(moveToAction);
            indicator1.setPosition(indicatorX, newIndicatorY);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) && !selected) {
            GameManager.getInstance().playSound("Pickup.ogg");
            currentSelection++;
            if (currentSelection >= 3) {
                currentSelection -= 3;
            }

            float newIndicatorY = indicatorY - currentSelection * 60f;

            MoveToAction moveToAction = new MoveToAction();
            moveToAction.setPosition(indicatorX, newIndicatorY);
            moveToAction.setDuration(0.2f);
            indicator0.clearActions();
            indicator0.addAction(moveToAction);
            indicator1.setPosition(indicatorX, newIndicatorY);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.X) && !selected) {
            GameManager.getInstance().playSound("Teleport.ogg");
            
            selected = true;

            indicator0.setVisible(false);
            indicator1.setVisible(true);

            RunnableAction runnableAction = new RunnableAction();
            runnableAction.setRunnable(new Runnable() {
                @Override
                public void run() {
                    switch (currentSelection) {
                        case 2: // hard mode
                            GameManager.infiniteLives = false;
                            GameManager.resetPlayerAbilities = true;
                            break;
                        case 1: // normal mode
                            GameManager.infiniteLives = true;
                            GameManager.resetPlayerAbilities = true;
                            break;
                        case 0: // easy mode
                        default:
                            GameManager.infiniteLives = true;
                            GameManager.resetPlayerAbilities = false;
                            break;
                    }
                    GameManager.playerLives = 3;
                    game.setScreen(new PlayScreen(game, 1));
                }
            });

            stage.addAction(new SequenceAction(Actions.delay(0.2f), Actions.fadeOut(1f), runnableAction));
        }
    }

    @Override
    public void render(float delta) {
        handleInput();

        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
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
        backgroundTexture.dispose();
        indicationsTexture.dispose();
        stage.dispose();
        font.dispose();
    }

}
