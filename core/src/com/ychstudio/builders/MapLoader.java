package com.ychstudio.builders;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.ychstudio.gamesys.GameManager;

public class MapLoader {

    public static enum BLOCK {

        EMPTY(255, 255, 255), // white
        WALL(0, 0, 0), // black
        BREAKABLE(0, 255, 255), // cyan
        INDESTRUCTIBLE(0, 0, 255), // blue
        PLAYER(255, 0, 0), // red
        ENEMY1(0, 255, 0), // green
        ENEMY2(0, 128, 0); // dark green

        int color;

        BLOCK(int r, int g, int b) {
            color = r << 24 | g << 16 | b << 8 | 0xff;
        }

        boolean sameColor(int color) {
            return this.color == color;
        }
    }

    protected final World b2dWorld;
    protected final com.artemis.World world;
    protected final AssetManager assetManager;

    protected TextureAtlas tileTextureAtlas;
    protected Pixmap pixmap;

    protected int mapWidth;
    protected int mapHeight;

    protected int level;

    protected final float radius = 0.46f;

    public MapLoader(World b2dWorld, com.artemis.World world, int level) {
        this.b2dWorld = b2dWorld;
        this.world = world;
        this.level = level;
        assetManager = GameManager.getInstance().getAssetManager();

        pixmap = assetManager.get("maps/level_" + level + ".png", Pixmap.class);
        switch (level) {
            case 3:
                tileTextureAtlas = assetManager.get("maps/area_2_tiles.pack", TextureAtlas.class);
                break;
            case 2:
            case 1:
            default:
                tileTextureAtlas = assetManager.get("maps/area_1_tiles.pack", TextureAtlas.class);
                break;
        }

        mapWidth = pixmap.getWidth();
        mapHeight = pixmap.getHeight();
    }

    public void loadMap() {
        ActorBuilder actorBuilder = new ActorBuilder(b2dWorld, world);

        int color;
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                color = pixmap.getPixel(x, mapHeight - y - 1);
                if (BLOCK.WALL.sameColor(color)) {
                    actorBuilder.createWall(x + 0.5f, y + 0.5f, mapWidth, mapHeight, tileTextureAtlas);
                } else if (BLOCK.BREAKABLE.sameColor(color)) {
                    actorBuilder.createBreakable(x + 0.5f, y + 0.5f, tileTextureAtlas);
                } else if (BLOCK.INDESTRUCTIBLE.sameColor(color)) {
                    actorBuilder.createIndestructible(x + 0.5f, y + 0.5f, tileTextureAtlas);
                } else if (BLOCK.PLAYER.sameColor(color)) {
                    actorBuilder.createPlayer(x + 0.5f, y + 0.5f, false);
                    GameManager.getInstance().setPlayerRespawnPosition(new Vector2(x + 0.5f, y + 0.5f));
                } else if (BLOCK.ENEMY1.sameColor(color)) {
                    switch (level) {
                        case 3:
                            actorBuilder.createHare(x + 0.5f, y + 0.5f);
                            break;
                        case 2:
                            actorBuilder.createOctopus(x + 0.5f, y + 0.5f);
                            break;
                        case 1:
                        default:
                            actorBuilder.createOctopus(x + 0.5f, y + 0.5f);
                            break;
                    }
                } else if (BLOCK.ENEMY2.sameColor(color)) {
                    switch (level) {
                        case 3:
                            actorBuilder.createHare(x + 0.5f, y + 0.5f);
                            break;
                        case 2:
                            actorBuilder.createSlime(x + 0.5f, y + 0.5f);
                            break;
                        case 1:
                        default:
                            actorBuilder.createOctopus(x + 0.5f, y + 0.5f);
                            break;
                    }
                }
            }
        }
        GameManager.getInstance().setPortalPosition(new Vector2(mapWidth / 2, mapHeight / 2));
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    protected Sprite createGroundSprite() {
        TextureRegion textureRegion = tileTextureAtlas.findRegion("ground");

        Sprite sprite = new Sprite();
        sprite.setRegion(textureRegion);
        sprite.setBounds(0, 0, 1, 1);

        return sprite;
    }
}
