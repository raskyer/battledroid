package fr.slick.scene;

import fr.battledroid.core.Colors;
import fr.battledroid.core.Direction;
import fr.battledroid.core.Settings;
import fr.battledroid.core.adaptee.AssetFactory;
import fr.battledroid.core.adaptee.SpriteFactory;
import fr.battledroid.core.artifact.ArtifactFactory;
import fr.battledroid.core.engine.Engine;
import fr.battledroid.core.engine.EngineFactory;
import fr.battledroid.core.engine.ViewContext;
import fr.battledroid.core.map.Map;
import fr.battledroid.core.map.MapFactory;
import fr.battledroid.core.map.tile.math.IsometricDaniloff;
import fr.battledroid.core.player.*;
import fr.slick.adapter.CanvasAdapter;
import fr.slick.adapter.ColorAdapter;
import fr.slick.adapter.SlickSpriteFactory;
import fr.slick.bridge.AssetFacade;
import fr.slick.bridge.SysListener;
import fr.slick.bridge.SysObserver;
import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.util.Objects;

public final class Play extends BasicGameState {
    private final SpriteFactory spriteFactory;
    private final AssetFactory assetFactory;
    private ViewContext context;
    private CanvasAdapter adapter;

    private long lastMove;

    private Play(SpriteFactory spriteFactory, AssetFactory assetFactory) {
        this.spriteFactory = Objects.requireNonNull(spriteFactory);
        this.assetFactory = Objects.requireNonNull(assetFactory);
    }

    public static Play create() {
        SpriteFactory spriteFactory = new SlickSpriteFactory(2);
        AssetFactory assetFactory = new AssetFactory(spriteFactory);

        return new Play(spriteFactory, assetFactory);
    }

    @Override
    public int getID() {
        return 1;
    }

    @Override
    public void init(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
        Settings settings = new Settings.Builder()
                .setTileWidth(56)
                .setTileHeight(74)
                .setTileAlphaWidth(0)
                .setTileAlphaHeight(0)
                .setMapSize(50)
                .setScreenWidth(container.getWidth())
                .setScreenHeight(container.getHeight())
                .build();

        Colors colors = Colors.instance();
        colors.setRed(new ColorAdapter(Color.red));
        colors.setGreen(new ColorAdapter(Color.green));
        colors.setBlue(new ColorAdapter(Color.blue));
        colors.setBlack(new ColorAdapter(Color.black));

        AssetFacade.initAsset(assetFactory);
        Map map = MapFactory.createRandom(assetFactory, new IsometricDaniloff());

        Player human = PlayerFactory.createDroid(assetFactory);
        Player monster = PlayerFactory.createMonster(assetFactory, true);
        human.attach(new SysObserver(human));
        monster.attach(new SysObserver(monster));

        ArtifactFactory artifactFactory = ArtifactFactory.create(assetFactory);

        Engine engine = EngineFactory.create(map);
        engine.addHuman(human);
        engine.addMonster(monster);
        engine.generateArtifact(artifactFactory);
        engine.setListener(new SysListener());

        context = new ViewContext(engine, human);
        adapter = new CanvasAdapter(container.getWidth(), container.getHeight());
        context.center();
        container.getInput().enableKeyRepeat();
    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics g) throws SlickException {
        context.draw(adapter.wrap(g));
    }

    @Override
    public void update(GameContainer container, StateBasedGame stateBasedGame, int i) throws SlickException {
        Input in = container.getInput();
        long t = System.currentTimeMillis();
        if (t - lastMove > 300) {
            if (in.isControllerLeft(0) || in.isKeyDown(Input.KEY_Q)) {
                lastMove = t;
                context.move(Direction.LEFT);
            }
            if (in.isControllerRight(0) || in.isKeyDown(Input.KEY_D)) {
                lastMove = t;
                context.move(Direction.RIGHT);
            }
            if (in.isControllerUp(0) || in.isKeyDown(Input.KEY_Z)) {
                lastMove = t;
                context.move(Direction.UP);
            }
            if (in.isControllerDown(0) || in.isKeyDown(Input.KEY_S)) {
                lastMove = t;
                context.move(Direction.DOWN);
            }
        }
        context.tick();
    }

    @Override
    public void keyPressed(int key, char c) {
        switch (key) {
            case Input.KEY_LEFT:
                context.shoot(Direction.LEFT);
                break;
            case Input.KEY_RIGHT:
                context.shoot(Direction.RIGHT);
                break;
            case Input.KEY_UP:
                context.shoot(Direction.UP);
                break;
            case Input.KEY_DOWN:
                context.shoot(Direction.DOWN);
                break;
        }
    }

    @Override
    public void controllerButtonPressed(int controller, int button) {
        switch (button) {
            case 0:
                context.shoot(Direction.DOWN);
                break;
            case 1:
                break;
            case 2:
                context.shoot(Direction.LEFT);
                break;
        }
    }
}