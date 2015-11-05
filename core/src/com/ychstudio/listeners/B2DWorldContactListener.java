package com.ychstudio.listeners;

import com.artemis.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.ychstudio.components.Bomb;
import com.ychstudio.components.Enemy;
import com.ychstudio.components.Player;
import com.ychstudio.components.PowerUp;
import com.ychstudio.gamesys.GameManager;

public class B2DWorldContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        // explosion
        if (fixtureA.getFilterData().categoryBits == GameManager.EXPLOSION_BIT || fixtureB.getFilterData().categoryBits == GameManager.EXPLOSION_BIT) {
            // explode player
            if (fixtureA.getFilterData().categoryBits == GameManager.PLAYER_BIT) {
                Entity e = (Entity) fixtureA.getBody().getUserData();
                Player player = e.getComponent(Player.class);
                player.damage(1);
            } else if (fixtureB.getFilterData().categoryBits == GameManager.PLAYER_BIT) {
                Entity e = (Entity) fixtureB.getBody().getUserData();
                Player player = e.getComponent(Player.class);
                player.damage(1);
            } // explode enemy
            else if (fixtureA.getFilterData().categoryBits == GameManager.ENEMY_BIT) {
                Entity e = (Entity) fixtureA.getBody().getUserData();
                Enemy enemy = e.getComponent(Enemy.class);
                enemy.damage(1);
            } else if (fixtureB.getFilterData().categoryBits == GameManager.ENEMY_BIT) {
                Entity e = (Entity) fixtureB.getBody().getUserData();
                Enemy enemy = e.getComponent(Enemy.class);
                enemy.damage(1);
            } // explode bomb
            else if (fixtureA.getFilterData().categoryBits == GameManager.BOMB_BIT) {
                Entity e = (Entity) fixtureA.getBody().getUserData();
                Bomb bomb = e.getComponent(Bomb.class);
                bomb.countDown = 0;
            } else if (fixtureB.getFilterData().categoryBits == GameManager.BOMB_BIT) {
                Entity e = (Entity) fixtureB.getBody().getUserData();
                Bomb bomb = e.getComponent(Bomb.class);
                bomb.countDown = 0;
            }
        } // enemy
        else if (fixtureA.getFilterData().categoryBits == GameManager.ENEMY_BIT || fixtureB.getFilterData().categoryBits == GameManager.ENEMY_BIT) {
            if (fixtureA.getFilterData().categoryBits == GameManager.PLAYER_BIT) {
                Entity e = (Entity) fixtureA.getBody().getUserData();
                Player player = e.getComponent(Player.class);
                player.damage(1);
            } else if (fixtureB.getFilterData().categoryBits == GameManager.PLAYER_BIT) {
                Entity e = (Entity) fixtureB.getBody().getUserData();
                Player player = e.getComponent(Player.class);
                player.damage(1);
            }
        } // player
        else if (fixtureA.getFilterData().categoryBits == GameManager.PLAYER_BIT || fixtureB.getFilterData().categoryBits == GameManager.PLAYER_BIT) {
            // power up item
            if (fixtureA.getFilterData().categoryBits == GameManager.POWERUP_BIT) {
                Entity powerUpEntity = (Entity) fixtureA.getBody().getUserData();
                PowerUp powerUp = powerUpEntity.getComponent(PowerUp.class);
                powerUp.life = 0; // consume power-up
                Entity playerEntity = (Entity) fixtureB.getBody().getUserData();
                Player player = playerEntity.getComponent(Player.class);
                switch (powerUp.type) {
                    case REMOTE:
                        player.powerUpRemote();
                        break;
                    case KICK:
                        player.powerUpKick();
                        break;
                    case SPEED:
                        player.powerUpSpeed();
                        break;
                    case POWER:
                        player.powerUpPower();
                        break;
                    case AMMO:
                    default:
                        player.powerUpAmmo();
                        break;
                }
            } else if (fixtureB.getFilterData().categoryBits == GameManager.POWERUP_BIT) {
                Entity powerUpEntity = (Entity) fixtureB.getBody().getUserData();
                PowerUp powerUp = powerUpEntity.getComponent(PowerUp.class);
                powerUp.life = 0; // consume power-up
                Entity playerEntity = (Entity) fixtureA.getBody().getUserData();
                Player player = playerEntity.getComponent(Player.class);
                switch (powerUp.type) {
                    case REMOTE:
                        player.powerUpRemote();
                        break;
                    case KICK:
                        player.powerUpKick();
                        break;
                    case SPEED:
                        player.powerUpSpeed();
                        break;
                    case POWER:
                        player.powerUpPower();
                        break;
                    case AMMO:
                    default:
                        player.powerUpAmmo();
                        break;
                }
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

}
