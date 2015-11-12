package com.ychstudio.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.ychstudio.Bomberman;
import com.ychstudio.gamesys.GameManager;
import com.ychstudio.scenes.actors.AnimImage;

public class EndingScreen extends ScreenAdapter {

    private final float WIDTH = 640;
    private final float HEIGHT = 480;

    private final Bomberman game;
    private final SpriteBatch batch;
    private final AssetManager assetManager;

    private FitViewport viewport;
    private Stage stage;

    private BitmapFont font;

    private Label lastSentenceLabel;

    public EndingScreen(Bomberman game) {
        this.game = game;
        batch = game.getSpriteBatch();
        assetManager = GameManager.getInstance().getAssetManager();
    }

    @Override
    public void show() {
        viewport = new FitViewport(WIDTH, HEIGHT);
        stage = new Stage(viewport, batch);

        TextureRegion bomberman = assetManager.get("img/actors.pack", TextureAtlas.class).findRegion("Bomberman1");
        Array<TextureRegion> keyFrames = new Array<>();

        // Bomberman walking animation
        for (int i = 9; i < 12; i++) {
            keyFrames.add(new TextureRegion(bomberman, 16 * i, 0, 16, 24));
        }
        Animation anim = new Animation(0.1f, keyFrames, Animation.PlayMode.LOOP);
        AnimImage bombermanAnimImage = new AnimImage(new TextureRegion(bomberman, 0, 0, 16, 24));
        bombermanAnimImage.put("walking", anim);

        keyFrames.clear();
        // Bomberman idling animation
        keyFrames.add(new TextureRegion(bomberman, 16 * 7, 0, 16, 24));
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.NORMAL);
        bombermanAnimImage.put("idling", anim);

        keyFrames.clear();
        // Bomberman shocked animation
        for (int i = 12; i < 13; i++) {
            keyFrames.add(new TextureRegion(bomberman, 16 * i, 0, 16, 24));
        }
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.NORMAL);
        bombermanAnimImage.put("shocked", anim);

        bombermanAnimImage.setScale(2.5f);
        bombermanAnimImage.setPosition(180, 160);

        keyFrames.clear();
        // Princess walking animation
        TextureRegion princess = assetManager.get("img/actors.pack", TextureAtlas.class).findRegion("Princess");
        AnimImage princessAnimImage = new AnimImage(new TextureRegion(princess, 0, 0, 16, 24));
        for (int i = 0; i < 3; i++) {
            keyFrames.add(new TextureRegion(princess, 16 * i, 0, 16, 24));
        }
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.LOOP);
        princessAnimImage.put("walking", anim);

        keyFrames.clear();
        // Princess idling animation
        for (int i = 3; i < 4; i++) {
            keyFrames.add(new TextureRegion(princess, 16 * i, 0, 16, 24));
        }
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.NORMAL);
        princessAnimImage.put("idling", anim);

        keyFrames.clear();
        // Princess dying animation
        for (int i = 5; i < 6; i++) {
            keyFrames.add(new TextureRegion(princess, 16 * i, 0, 16, 24));
        }
        anim = new Animation(0.1f, keyFrames, Animation.PlayMode.NORMAL);
        princessAnimImage.put("dying", anim);

        princessAnimImage.setCurrentAnim("walking");
        princessAnimImage.setScale(2.5f);
        princessAnimImage.setPosition(480, 160);

        // actions
        bombermanAnimImage.addAction(
                Actions.sequence(
                        Actions.run(() -> bombermanAnimImage.setCurrentAnim("walking")),
                        Actions.moveBy(130, 0, 5f),
                        Actions.run(() -> {
                            if (GameManager.playerLives > 0) {
                                bombermanAnimImage.setCurrentAnim("idling");
                            } else {
                                bombermanAnimImage.setCurrentAnim("shocked");
                            }
                        })
                )
        );

        princessAnimImage.addAction(
                Actions.sequence(
                        Actions.run(() -> princessAnimImage.setCurrentAnim("walking")),
                        Actions.moveBy(-140, 0, 5f),
                        Actions.run(
                                () -> {
                                    if (GameManager.playerLives > 0) {
                                        princessAnimImage.setCurrentAnim("idling");
                                    } else {
                                        princessAnimImage.setCurrentAnim("dying");
                                        GameManager.getInstance().playMusic("Oops.ogg", false);
                                        princessAnimImage.addAction(
                                                Actions.sequence(
                                                        Actions.moveTo(360f, 300f, 1.0f),
                                                        Actions.moveTo(400f, -100f, 0.8f)
                                                )
                                        );
                                    }
                                })
                )
        );

        font = new BitmapFont(Gdx.files.internal("fonts/foo.fnt"));
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

        Label congratulationsLabel = new Label("Congratulations!", labelStyle);
        congratulationsLabel.setPosition((WIDTH - congratulationsLabel.getWidth()) / 2, HEIGHT - congratulationsLabel.getHeight() - 52f);

        Label wordsLabel = new Label("You took the princess\n   from her father", labelStyle);
        wordsLabel.setPosition(WIDTH - wordsLabel.getWidth() - 52f, HEIGHT - 200f);
        wordsLabel.setFontScale(0.9f);

        lastSentenceLabel = new Label("", labelStyle);
        lastSentenceLabel.setText(
                GameManager.playerLives <= 0
                        ? "And she hates you!" : "And you live happily ever after..."
        );
        lastSentenceLabel.setFontScale(0.8f);
        lastSentenceLabel.setPosition(20f, 120f);
        lastSentenceLabel.setVisible(false);

        stage.addActor(congratulationsLabel);
        stage.addActor(wordsLabel);
        stage.addActor(lastSentenceLabel);
        stage.addActor(bombermanAnimImage);
        stage.addActor(princessAnimImage);

        stage.addAction(
                Actions.sequence(
                        Actions.delay(5f),
                        Actions.run(() -> lastSentenceLabel.setVisible(true)),
                        Actions.delay(5f),
                        Actions.fadeOut(1f),
                        Actions.run(() -> {
                            if (GameManager.playerLives > 0) {
                                game.setScreen(new MainMenuScreen(game));
                            } else {
                                game.setScreen(new GameOverScreen(game));
                            }
                        })
                )
        );

        GameManager.getInstance().playMusic("StageCleared.ogg", false);
    }

    @Override
    public void render(float delta) {
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
        font.dispose();
        stage.dispose();
    }

}
