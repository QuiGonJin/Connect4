package com.pennypop.project.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.openal.Wav.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.pennypop.project.Assets;
import com.pennypop.project.ProjectApplication;
import com.pennypop.project.WeatherAPI;

/**
 * This is where you screen code will go, any UI should be in here
 * 
 * @author Richard Taylor, Kevin Chen
 */
public class MainScreen implements Screen {
	public static Skin skin;
	public static Screen screen;

	private final Stage stage;
	private final SpriteBatch spriteBatch;

	public MainScreen() {
		MainScreen.screen = this;
		spriteBatch = new SpriteBatch();
		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, spriteBatch);

		// initialize skin with styles
		initSkin();

		// Add 'pennyPop' label
		Label pennyLabel = new Label("PennyPop", skin);
		pennyLabel.setPosition(Gdx.graphics.getWidth() / 4 - pennyLabel.getWidth() / 2,
				(float) (Gdx.graphics.getHeight() * 0.6));
		stage.addActor(pennyLabel);

		// Add 'SFX' button and listener
		ImageButton sfxButton = new ImageButton(skin, "sfxButton");
		sfxButton.setPosition(Gdx.graphics.getWidth() / 4 - sfxButton.getWidth(),
				Gdx.graphics.getHeight() / 2 - sfxButton.getWidth());
		sfxButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				Sound sound = Assets.manager.get(Assets.CLICK_SOUND);
				sound.play();
				return true;
			}
		});
		stage.addActor(sfxButton);

		// Add 'API' button and define listener
		ImageButton apiButton = new ImageButton(skin, "apiButton");
		apiButton.setPosition(sfxButton.getX() + sfxButton.getWidth(), sfxButton.getY());
		apiButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				displayWeather();
				return true;
			}
		});
		stage.addActor(apiButton);

		// Add Player vs Player 'Game' button and listener
		ImageButton gameButton = new ImageButton(skin, "gameButton");
		gameButton.setPosition(Gdx.graphics.getWidth() / 4 - sfxButton.getWidth(),
				Gdx.graphics.getHeight() / 2 - sfxButton.getWidth() * 2);
		gameButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				Screen gameScreen = new GameScreen(false);
				ProjectApplication.app.setScreen(gameScreen);
				return true;
			}
		});
		stage.addActor(gameButton);

		// Add Player vs AI 'Game' button and listener
		ImageButton AIgameButton = new ImageButton(skin, "AIButton");
		AIgameButton.setPosition(Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() / 2 - sfxButton.getWidth() * 2);
		AIgameButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				Screen gameScreen = new GameScreen(true);
				ProjectApplication.app.setScreen(gameScreen);
				return true;
			}
		});
		stage.addActor(AIgameButton);
	}

	/**
	 * Creates WeatherAPI object Pulls weather data from OpenWeatherMap, then
	 * displays labels for weather on MainScreen
	 */
	private void displayWeather() {
		WeatherAPI weather = new WeatherAPI(
				"http://api.openweathermap.org/data/2.5/weather?q=San%20Francisco,US&appid=2e32d2b4b825464ec8c677a49531e9ae");

		// ------------ Create Labels for weather conditions -----------//
		// Add label for "Current Weather"
		Label currentWeather = new Label("Current Weather", skin, "colored");
		currentWeather.setPosition((float) (Gdx.graphics.getWidth() * 0.7 - currentWeather.getWidth() / 2),
				(float) (Gdx.graphics.getHeight() * 0.6));
		currentWeather.setColor(Color.BLACK);
		stage.addActor(currentWeather);

		// Add label for city
		Label city = new Label(weather.getName(), skin, "colored");
		city.setPosition((float) (Gdx.graphics.getWidth() * 0.7 - city.getWidth() / 2),
				currentWeather.getY() - currentWeather.getHeight());
		city.setColor(Color.BLUE);
		stage.addActor(city);

		// Add label for sky conditions
		Label description = new Label("Sky: " + weather.getDescription(), skin);
		description.setPosition((float) (Gdx.graphics.getWidth() * 0.7 - description.getWidth() / 2),
				city.getY() - currentWeather.getHeight() * 2);
		stage.addActor(description);

		// Add label for temperature and wind
		Label tempAndWind = new Label(
				Math.round(weather.getTemp()) + " degrees, " + weather.getWindspeed() + "mph wind", skin);
		tempAndWind.setPosition((float) (Gdx.graphics.getWidth() * 0.7 - tempAndWind.getWidth() / 2),
				description.getY() - currentWeather.getHeight());
		stage.addActor(tempAndWind);
	}

	/**
	 * Defines the styles for various UI Widgets and stores them in the Skin
	 * variable
	 */
	private void initSkin() {
		// Create a font
		BitmapFont font = Assets.manager.get(Assets.FONT);
		skin = new Skin();
		skin.add("default", font);

		// Create label style with default red color
		Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.RED);
		skin.add("default", labelStyle);

		// Create label with color to be chosen later
		Label.LabelStyle coloredLabel = new Label.LabelStyle(font, Color.WHITE);
		skin.add("colored", coloredLabel);

		// Create sfx button style
		ImageButton.ImageButtonStyle sfxButtonStyle = new ImageButton.ImageButtonStyle();
		sfxButtonStyle.imageUp = new Image(Assets.manager.get(Assets.SFX_TXT, Texture.class)).getDrawable();
		sfxButtonStyle.imageDown = new Image(Assets.manager.get(Assets.BTN_DOWN_TXT, Texture.class)).getDrawable();
		skin.add("sfxButton", sfxButtonStyle);

		// Create api button style
		ImageButton.ImageButtonStyle apiButtonStyle = new ImageButton.ImageButtonStyle();
		apiButtonStyle.imageUp = new Image(Assets.manager.get(Assets.API_TXT, Texture.class)).getDrawable();
		apiButtonStyle.imageDown = new Image(Assets.manager.get(Assets.BTN_DOWN_TXT, Texture.class)).getDrawable();
		skin.add("apiButton", apiButtonStyle);

		// Create game button style
		ImageButton.ImageButtonStyle gameButtonStyle = new ImageButton.ImageButtonStyle();
		gameButtonStyle.imageUp = new Image(Assets.manager.get(Assets.GAME_TXT, Texture.class)).getDrawable();
		gameButtonStyle.imageDown = new Image(Assets.manager.get(Assets.BTN_DOWN_TXT, Texture.class)).getDrawable();
		skin.add("gameButton", gameButtonStyle);

		// Create AI button style
		ImageButton.ImageButtonStyle AIButtonStyle = new ImageButton.ImageButtonStyle();
		AIButtonStyle.imageUp = new Image(Assets.manager.get(Assets.AI_TXT, Texture.class)).getDrawable();
		AIButtonStyle.imageDown = new Image(Assets.manager.get(Assets.BTN_DOWN_TXT, Texture.class)).getDrawable();
		skin.add("AIButton", AIButtonStyle);

		// Create Menu button style
		ImageButton.ImageButtonStyle menuButtonStyle = new ImageButton.ImageButtonStyle();
		menuButtonStyle.imageUp = new Image(Assets.manager.get(Assets.MENU_TXT, Texture.class)).getDrawable();
		menuButtonStyle.imageDown = new Image(Assets.manager.get(Assets.BTN_DOWN_TXT, Texture.class)).getDrawable();
		skin.add("menuButton", menuButtonStyle);

		// Create Reset button style
		ImageButton.ImageButtonStyle resetButtonStyle = new ImageButton.ImageButtonStyle();
		resetButtonStyle.imageUp = new Image(Assets.manager.get(Assets.RESET_TXT, Texture.class)).getDrawable();
		resetButtonStyle.imageDown = new Image(Assets.manager.get(Assets.BTN_DOWN_TXT, Texture.class)).getDrawable();
		skin.add("resetButton", resetButtonStyle);
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		stage.dispose();
	}

	@Override
	public void render(float delta) {
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport(width, height, false);
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void pause() {
		// Irrelevant on desktop, ignore this
	}

	@Override
	public void resume() {
		// Irrelevant on desktop, ignore this
	}

}
